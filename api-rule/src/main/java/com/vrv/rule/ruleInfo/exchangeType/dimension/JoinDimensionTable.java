package com.vrv.rule.ruleInfo.exchangeType.dimension;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.model.CacheVo;
import com.vrv.rule.ruleInfo.FlinkRuleOperatorFunction;
import com.vrv.rule.util.*;
import com.vrv.rule.util.gson.GsonUtil;
import com.vrv.rule.vo.DimensionKeyVO;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
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
 * 维表连接异步操作
 *
 * @author wd-pc
 */
public class JoinDimensionTable extends RichAsyncFunction<Row, Row> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private static final Logger logger = LoggerFactory.getLogger(JoinDimensionTable.class);

    private static final Gson gson = GsonUtil.getGson();


    private List<FieldInfoVO> inputFields;
    private List<FieldInfoVO> outputFileds;
    private DimensionConfig dimensionConfig;
    private String filterCode;
    private String ruleCode;

//    private RedissonClient redissonClient;
//
//    public JdbcTemplate jdbcTemplate;


    public JoinDimensionTable(List<FieldInfoVO> inputFields, List<FieldInfoVO> outputFileds, DimensionConfig dimensionConfig, String filterCode, String ruleCode) {
        this.dimensionConfig = dimensionConfig;
        this.inputFields = inputFields;
        this.outputFileds = outputFileds;
        this.filterCode = filterCode;
        this.ruleCode = ruleCode;
    }

    public JoinDimensionTable() {

    }


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
//        initRedission();
//        initJdbc(); //初始化数据库

    }


    @Override
    public void asyncInvoke(Row input, ResultFuture<Row> resultFuture) throws Exception {
        dealResult(input, resultFuture);


    }


    private void dealResult1(Row input, ResultFuture<Row> resultFuture) {
        String dimensionTableName = dimensionConfig.getDimensionTableName();
        String dimensionFieldName = dimensionConfig.getDimensionFieldName();
        String filterCon = dimensionConfig.getFilterCon();
        String dimensionAliasName = dimensionConfig.getDimensionAliasName();
        String highLevelSearchCon = dimensionConfig.getHighLevelSearchCon();
        String filterConditions = getDimensionFilterConditions(dimensionConfig, input, inputFields);
        StringBuffer sb = new StringBuffer();
        List<Row> dbResult = getRowList(input, dimensionTableName, dimensionFieldName,filterCon,dimensionAliasName, filterConditions, highLevelSearchCon,sb);
        resultFuture.complete(dbResult);
    }

    private List<Row> getRowList(Row input, String dimensionTableName, String dimensionFieldName,String filterCon,String dimensionAliasName,
                                 String filterConditions,String highLevelSearchCon, StringBuffer sb) {
        List<Map<String, Object>> list = new ArrayList<>();
        String highLevelCondtion = FieldInfoUtil.setLogFieldValueByHighLevelSearchCon(highLevelSearchCon,input,inputFields);
        DimensionKeyVO dimensionKeyVO = DimensionKeyVO.builder().dimensionTableName(dimensionTableName)
                .dimensionFieldName(dimensionFieldName)
                .filterCondition(filterConditions)
                .highFilterCondition(highLevelCondtion)
                .filterCode(filterCode)
                .ruleCode(ruleCode)
                .build();
        String dataKey = FilterOperatorUtil.getDimensionDataKey(dimensionKeyVO);
        if (RedissonSingleUtil.getInstance().exists(dataKey)) {
            String json = null;
            try{
                json = RedissonSingleUtil.getInstance().get(dataKey);
                setDimentionList(list, json);
            }catch (Exception e){
                // logger.error("json解析出现错误：{}",json);
                 json = DimensionUtil.subCacheVO(json);
                 setDimentionList(list, json);
            }
        } else {
            String sql = getFilterDimensionSql(dimensionTableName, dimensionFieldName, filterConditions, sb,filterCon,dimensionAliasName,highLevelSearchCon,input);
            logger.info("维表对应得值sql:{}", sql);
            try {
                 list = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql);
            } catch (Exception e) {
                logger.error("sql访问异常，请检查！ sql：{},异常报错:{}", sql, e);
            }
            RedissonSingleUtil.getInstance().set(dataKey, gson.toJson(list));
        }
        List<Row> results = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Object idRoomField = input.getField(input.getArity() - 1);
            Row newRow = new Row(outputFileds.size());
            for (int i = 0; i < input.getArity() - 1; i++) {
                newRow.setField(i, input.getField(i));
            }
            Set<String> keySet = map.keySet();
            List<String> result = new ArrayList<>(keySet);
            int size = keySet.size();
            int begin = inputFields.size() - 1;
            int j = 0;
            for (int i = begin; i < begin + size; i++, j++) {
                String key = result.get(j);
                newRow.setField(i, map.get(key));
            }
            newRow.setField(outputFileds.size() - 1, idRoomField);
            results.add(newRow);
        }

        return results;
    }

    /**
     * 设置dimensionList
     * @param list
     * @param json
     */
    private void setDimentionList(List<Map<String, Object>> list, String json) {
        Type listType = new TypeToken<List<Map<String,Object>>>(){}.getType();
        List<Map<String, Object>> mapList = gson.fromJson(json,listType);
        list.addAll(mapList);
    }

    private void dealResult(Row input, ResultFuture<Row> resultFuture) {
        String dimensionTableName = dimensionConfig.getDimensionTableName();
        String dimensionFieldName = dimensionConfig.getDimensionFieldName();
        String filterCon = dimensionConfig.getFilterCon();
        String dimensionAliasName = dimensionConfig.getDimensionAliasName();
        String highLevelSearchCon = dimensionConfig.getHighLevelSearchCon();
        String filterConditions = getDimensionFilterConditions(dimensionConfig, input, inputFields);
        StringBuffer sb = new StringBuffer();
        CompletableFuture.supplyAsync(new Supplier<List<Row>>() {
            @Override
            public List<Row> get() {
                List<Map<String, Object>> list = new ArrayList<>();
                String highLevelCondtion = FieldInfoUtil.setLogFieldValueByHighLevelSearchCon(highLevelSearchCon,input,inputFields);
                DimensionKeyVO dimensionKeyVO = DimensionKeyVO.builder().dimensionTableName(dimensionTableName)
                        .dimensionFieldName(dimensionFieldName)
                        .filterCondition(filterConditions)
                        .highFilterCondition(highLevelCondtion)
                        .filterCode(filterCode)
                        .ruleCode(ruleCode)
                        .build();
                String dataKey = FilterOperatorUtil.getDimensionDataKey(dimensionKeyVO);
                if (RedissonSingleUtil.getInstance().exists(dataKey)) {
                    String json = null;
                    try{
                        json = RedissonSingleUtil.getInstance().get(dataKey);
                        setDimentionList(list, json);
                    }catch (Exception e){
                      //  logger.error("json解析出现错误：{}",json);
                        json = DimensionUtil.subCacheVO(json);
                        setDimentionList(list, json);
                    }
                } else {
                    String sql = getFilterDimensionSql(dimensionTableName, dimensionFieldName, filterConditions, sb,filterCon,dimensionAliasName,highLevelSearchCon,input);
                    logger.info("维表对应得值sql:{}", sql);
                    try {
                        list = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql);
                        //list = jdbcTemplate.queryForList(sql);
                    } catch (Exception e) {
                        logger.error("sql访问异常，请检查！ sql：{},异常报错:{}", sql, e);
                    }
                    RedissonSingleUtil.getInstance().set(dataKey, gson.toJson(list));
                }
                List<Row> results = new ArrayList<>();
                for (Map<String, Object> map : list) {
                    Object idRoomField = input.getField(input.getArity() - 1);
                    Row newRow = new Row(outputFileds.size());
                    for (int i = 0; i < input.getArity() - 1; i++) {
                        newRow.setField(i, input.getField(i));
                    }
                    Set<String> keySet = map.keySet();
                    List<String> result = new ArrayList<>(keySet);
                    int size = keySet.size();
                    int begin = inputFields.size() - 1;
                    int j = 0;
                    for (int i = begin; i < begin + size; i++, j++) {
                        String key = result.get(j);
                        newRow.setField(i, map.get(key));
                    }
                    newRow.setField(outputFileds.size() - 1, idRoomField);
                    results.add(newRow);
                }
                return results;
            }
        }).thenAccept((List<Row> dbResult) -> {
            // 设置请求完成时的回调: 将结果传递给 collector
            resultFuture.complete(dbResult);
        });
    }

    /**
     * 获得过滤维表
     *
     * @param dimensionTableName
     * @param dimensionFieldName
     * @param filterConditions
     * @param sb
     * @param filterCon
     * @param dimensionAliasName
     */
    private String getFilterDimensionSql(String dimensionTableName, String dimensionFieldName, String filterConditions,
                                         StringBuffer sb,String filterCon,String dimensionAliasName,String highFilterCon,Row input) {
        if (StringUtils.isBlank(dimensionAliasName)){
            dimensionAliasName=dimensionFieldName;
        }
        String fieldNameAsAliasName=getFieldNameAsAliasName(dimensionFieldName,dimensionAliasName);

        sb.append("select").append(" ").append(fieldNameAsAliasName).append(" ").append("from").append(" ").append(dimensionTableName).append(" ").append("where").append(" ").append("1=1");

        if(StringUtils.isNotEmpty(filterConditions)) {
            sb.append(" ").append("and").append(" ").append(filterConditions);
        }

        if(StringUtils.isNotEmpty(highFilterCon)){
            highFilterCon = FieldInfoUtil.setLogFieldValueByHighLevelSearchCon(highFilterCon, input,inputFields);
            sb.append(" ").append(highFilterCon);
        }

        if (StringUtils.isNotBlank(filterCon)){
            sb.append(" ").append("and").append(" ").append(filterCon);
        }
//        if(dimensionTableName.startsWith("baseline")){
//            String ruleId = ruleCode.split("-")[0];
//            String newRuleCode = ruleId.split("_")[1];
//            sb.append(" and filter_code='"+filterCode).append("' ").append("and rule_code='"+newRuleCode+"'");
//        }
        if (sb.indexOf("limit")<0){
            sb.append(" limit 0,1000");
        }
        String sql = sb.toString();
        return sql;
    }
    private String getFieldNameAsAliasName(String dimensionFieldName, String dimensionAliasName) {
        List<String> strings=new ArrayList<>();
        String[] dimensionFieldNames = dimensionFieldName.split(",");
        String[] dimensionAliasNames = dimensionAliasName.split(",");
        for (int i = 0; i <dimensionFieldNames.length ; i++) {
            strings.add(dimensionFieldNames[i]+" "+"as"+" "+dimensionAliasNames[i]);
        }
        return StringUtils.join(strings,",");
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    /**
     * 获得维表过滤条件
     *
     * @param dimensionConfig
     * @param row
     * @param inputFields
     * @return
     */
    private String getDimensionFilterConditions(DimensionConfig dimensionConfig, Row row, List<FieldInfoVO> inputFields) {
        LogicOperator loginExp = dimensionConfig.getLoginExp();
        if (loginExp != null) {
            String dimensionFilterCondition = loginExp.getDimensionFilterCondition(row, inputFields);
            return dimensionFilterCondition;
        } else {
            return null;
        }
    }

    @Override
    public void timeout(Row input, ResultFuture<Row> resultFuture) throws Exception {
        dealResult1(input, resultFuture);
        //super.timeout(input,resultFuture);
        //resultFuture.complete(Collections.singleton(input));
    }


}
