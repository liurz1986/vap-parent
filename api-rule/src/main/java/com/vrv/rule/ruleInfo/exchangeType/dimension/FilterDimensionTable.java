package com.vrv.rule.ruleInfo.exchangeType.dimension;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.model.CacheVo;
import com.vrv.rule.ruleInfo.FlinkRuleOperatorFunction;
import com.vrv.rule.util.*;
import com.vrv.rule.util.gson.GsonUtil;
import com.vrv.rule.vo.DimensionKeyVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.runtime.executiongraph.Execution;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.types.Row;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vrv.rule.model.DimensionConfig;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.LogicOperator;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 维表过滤异步操作
 * @author wd-pc
 *
 */
public class FilterDimensionTable extends RichAsyncFunction<Row,Row> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
    private static final Logger logger = LoggerFactory.getLogger(FilterDimensionTable.class);

	private static final Gson gson = GsonUtil.getGson();
    private List<FieldInfoVO> inputFields;
    private DimensionConfig dimensionConfig;
	private String filterCode;
	private String ruleCode;


	private JdbcTemplate jdbcTemplate;


    
    
    
    public FilterDimensionTable(List<FieldInfoVO> inputFields,DimensionConfig dimensionConfig,String filterCode,String ruleCode) {
    	this.dimensionConfig = dimensionConfig;
    	this.inputFields = inputFields;
		this.filterCode = filterCode;
		this.ruleCode = ruleCode;
    }
    
    public FilterDimensionTable() {
    	
    }
    
    
    
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
    }



	@Override
	public void asyncInvoke(Row input, ResultFuture<Row> resultFuture) throws Exception {
		String dimensionTableName = dimensionConfig.getDimensionTableName();
		String filterCon = dimensionConfig.getFilterCon();
		String highLevelSearchCon = dimensionConfig.getHighLevelSearchCon();
		String filterConditions = getDimensionFilterConditions(dimensionConfig, input, inputFields);
		StringBuffer sb = new StringBuffer();
		CompletableFuture.supplyAsync(new Supplier<Row>() {
			@Override
			public Row get() {
				String highLevelCondtion = FieldInfoUtil.setLogFieldValueByHighLevelSearchCon(highLevelSearchCon,input,inputFields);
				DimensionKeyVO dimensionKeyVO = DimensionKeyVO.builder().dimensionTableName(dimensionTableName)
						.filterCondition(filterConditions)
						.highFilterCondition(highLevelCondtion)
						.filterCode(filterCode)
						.ruleCode(ruleCode)
						.build();
				String dataKey = FilterOperatorUtil.getDimensionDataKey(dimensionKeyVO);
				int count=0;
				if(RedissonSingleUtil.getInstance().exists(dataKey)){
					String json = null;
					try{
						json = RedissonSingleUtil.getInstance().get(dataKey);
						count = getCount(json);
					}catch (Exception e){
						//logger.error("json解析报错的内容：{}，原因：{}",json,e);
						json = DimensionUtil.subCacheVO(json);
						//logger.error("处理后的数据：{}",json);
						count = getCount(json);
					}
				}else{
					String sql = getFilterDimensionSql(dimensionTableName, filterConditions, sb,filterCon,highLevelSearchCon,input);
					logger.info("asyncInvoke维表对应得值sql:{}", sql);
					try {
						//count =jdbcTemplate.queryForObject(sql, Integer.class);
						count = JdbcSingeConnectionUtil.getInstance().querySqlForCount(sql);
					}catch (Exception e){
						logger.error("sql查询异常错误，请检查！sql:{},异常报错:{}",sql,e);
					}
					CacheVo cacheVo=new CacheVo();
					cacheVo.setTimestamp(System.currentTimeMillis());
					cacheVo.setData(count);
//					countCache.put(dataKey,cacheVo);
					RedissonSingleUtil.getInstance().set(dataKey,gson.toJson(cacheVo));
					logger.info("asyncInvoke redis cache input success!");
				}
				Boolean matchOutput = dimensionConfig.isMatchOutput();
				if(matchOutput) {  //是匹配中输出
					if(count>0){
						return input;
					}else {
						return null;
					}
				}else { //没有匹配中输出
					if(count>0){
						return null;
					}else {
						return input;
					}
				}
			}
		}).thenAccept((Row dbResult) -> {
			// 设置请求完成时的回调: 将结果传递给 collector
			if(dbResult!=null) {
				resultFuture.complete(Collections.singleton(dbResult));
			}else {
				resultFuture.complete(Collections.emptySet());
			}
		});
	}

	
	  /**
     * 获得维表过滤条件
     * @return
     */
    private String getDimensionFilterConditions(DimensionConfig dimensionConfig,Row row,List<FieldInfoVO> inputFields) {
    	LogicOperator loginExp = dimensionConfig.getLoginExp();
    	if(loginExp!=null) {
    		String dimensionFilterCondition = loginExp.getDimensionFilterCondition(row, inputFields);
    		return dimensionFilterCondition;    		
    	}else {
    		return null;
    	}
	}




	private String getFilterDimensionSql(String dimensionTableName, String filterConditions,
			StringBuffer sb,String filterCon,String highLevelSearchCon,Row inutRow) {
		//TODO 高级搜索如果有高级搜索条件

		sb.append("select").append(" ").append("count(*)").append(" ").append("from").append(" ").append(dimensionTableName).append(" ").append("where").append(" ").append("1=1");

		if(StringUtils.isNotEmpty(filterConditions)){
			sb.append(" ").append("and").append(" ").append(filterConditions);
		}


		if(StringUtils.isNotEmpty(highLevelSearchCon)) {
			highLevelSearchCon = FieldInfoUtil.setLogFieldValueByHighLevelSearchCon(highLevelSearchCon,inutRow,inputFields);
			sb.append(" ").append(highLevelSearchCon);
		}



		if (StringUtils.isNotBlank(filterCon)){
			sb.append(" ").append("and").append(" ").append(filterCon);
		}

//		if(dimensionTableName.startsWith("baseline")){
//			String ruleId = ruleCode.split("-")[0];
//			String newRuleCode = ruleId.split("_")[1];
//			sb.append(" and filter_code='"+filterCode).append("' ").append("and rule_code='"+newRuleCode+"'");
//		}
		String sql = sb.toString();
		return sql;
	}
	
	
	
	
	
    @Override
    public void close() throws Exception {
        super.close();
    }



    @Override
    public void timeout(Row input, ResultFuture<Row> resultFuture) throws Exception {
		//super.timeout(input,resultFuture);
		//asyncInvoke(input,resultFuture);
		//throw  new RuntimeException("time out");
		//resultFuture.complete(Collections.singleton(input));
		dealResult(input, resultFuture);
	}

	private void dealResult(Row input, ResultFuture<Row> resultFuture){
		String dimensionTableName = dimensionConfig.getDimensionTableName();
		String filterCon = dimensionConfig.getFilterCon();
		String highLevelSearchCon = dimensionConfig.getHighLevelSearchCon();
		String filterConditions = getDimensionFilterConditions(dimensionConfig, input, inputFields);
		StringBuffer sb = new StringBuffer();
		Row dbResult = getRow(input, dimensionTableName, filterConditions,sb,filterCon,highLevelSearchCon);
		if(dbResult!=null) {
			resultFuture.complete(Collections.singleton(dbResult));
		}else {
			resultFuture.complete(Collections.emptySet());
		}
	}



	private Row getRow(Row input,String dimensionTableName,String filterConditions,StringBuffer sb,String filterCon,String highLevelSearchCon){
		String highLevelCondtion = FieldInfoUtil.setLogFieldValueByHighLevelSearchCon(highLevelSearchCon,input,inputFields);
		DimensionKeyVO dimensionKeyVO = DimensionKeyVO.builder().dimensionTableName(dimensionTableName)
				.filterCondition(filterConditions)
				.highFilterCondition(highLevelCondtion)
				.filterCode(filterCode)
				.ruleCode(ruleCode)
				.build();
		String dataKey = FilterOperatorUtil.getDimensionDataKey(dimensionKeyVO);

		int count=0;
		if(RedissonSingleUtil.getInstance().exists(dataKey)){
			String json = null;
			try{
				json = RedissonSingleUtil.getInstance().get(dataKey);
				count = getCount(json);
			}catch (Exception e){
				//logger.error("json解析报错的内容：{}，原因：{}",json,e);
				json = DimensionUtil.subCacheVO(json);
				//logger.error("处理后的数据：{}",json);
				count = getCount(json);
			}
			//logger.info("dataKey的值：{},value的值：{}",dataKey,count);
		}else{
			String sql = getFilterDimensionSql(dimensionTableName, filterConditions, sb,filterCon,highLevelSearchCon,input);
			logger.info("dealResult 维表对应得值sql:{}", sql);
			try {
				count = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql).size();
			}catch (Exception e){
				logger.error("dealResult sql查询异常错误，请检查！sql:{},异常报错:{}",sql,e);
			}
			CacheVo cacheVo=new CacheVo();
			cacheVo.setTimestamp(System.currentTimeMillis());
			cacheVo.setData(count);
			RedissonSingleUtil.getInstance().set(dataKey,gson.toJson(cacheVo));
			logger.info("dealResult redis cache input success!");
		}
		Boolean matchOutput = dimensionConfig.isMatchOutput();
		if(matchOutput) {  //是匹配中输出
			if(count>0){
				return input;
			}else {
				return null;
			}
		}else { //没有匹配中输出
			if(count>0){
				return null;
			}else {
				return input;
			}
		}
	}

	/**
	 * 获得对应的个数
	 * @param json
	 * @return
	 */
	private int getCount(String json) {
		int count;
		Type listType = new TypeToken<CacheVo>(){}.getType();
		CacheVo cacheVo = gson.fromJson(json,listType);
		Object countObj = cacheVo.getData();
		count = Integer.valueOf(String.valueOf(countObj));
		return count;
	}

}
