package com.flink.demo.analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vrv.rule.util.JdbcSingeConnectionUtil;
import com.vrv.rule.vo.FieldInfoVO;

public class AssetGuidRiskByFilterJdbc extends RichAsyncFunction<Row,Row> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(AssetGuidRiskByFilterJdbc.class);

    private String sql;
    private List<FieldInfoVO> inputFields;
    private List<FieldInfoVO> outputFileds;
    private String inputFieldName;
    private String dimensionFieldName;
    
    
    
    public AssetGuidRiskByFilterJdbc(String sql,List<FieldInfoVO> inputFields,List<FieldInfoVO> outputFileds) {
    	this.sql = sql;
    	this.inputFields = inputFields;
    	this.outputFileds = outputFileds;
    }
    
    public AssetGuidRiskByFilterJdbc(String sql,List<FieldInfoVO> inputFields,List<FieldInfoVO> outputFileds,String inputFieldName,String dimensionFieldName) {
    	this.sql = sql;
    	this.inputFields = inputFields;
    	this.outputFileds = outputFileds;
    	this.inputFieldName = inputFieldName;
    	this.dimensionFieldName = dimensionFieldName;
    }
    
    
    public AssetGuidRiskByFilterJdbc() {
    	
    }
    
    
    
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        
    }
	
	@Override
	public void asyncInvoke(Row input, ResultFuture<Row> resultFuture) throws Exception {
		logger.info("进入异步I/O {}", "dddddd");
		List<Map<String, Object>> list = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql);
		if(list.size()==1){
			CompletableFuture.supplyAsync(new Supplier<Row>() {
				@Override
				public Row get() {
					Map<String, Object> map = list.get(0);
					String value = map.get(dimensionFieldName).toString(); //sql查出来的值
					int order = 0;
					for (FieldInfoVO fieldInfoVO : inputFields) {
						String fieldName2 = fieldInfoVO.getFieldName();
						if(fieldName2.equals(inputFieldName)) {
							order = fieldInfoVO.getOrder();
							break;
						}
					}
					String inputValue = input.getField(order).toString();
					if(inputValue.equals(value)){
						return input;
					}else {
//						Row row = new Row(outputFileds.size());
//						return row;
						return null;
					}
				}
			}).thenAccept((Row dbResult) -> {
				// 设置请求完成时的回调: 将结果传递给 collector
				if(dbResult!=null) {
					resultFuture.complete(Collections.singleton(dbResult));					
				}else {
					resultFuture.complete(null);
				}
			});					
		}else {
			 logger.info("维表查询多余一个请查询 "); 
		}
	}
	
	
	
    @Override
    public void close() throws Exception {
        super.close();
    }

	public static void main(String[] args) {
		Row row = new Row(4);
		Row newRow = new Row(6);
		
		for (int i = 0; i < 4; i++) {
			newRow.setField(i, i);
		}
		Map<String, Object> map = new HashMap<>();
		map.put("guid", "123455667");
		map.put("name", "wudi12345667");
		Set<String> keySet = map.keySet();
		int size = keySet.size();
		int begin = row.getArity();
		int j = 0;
		for (int i = begin; i < begin+size; i++,j++) {
			List<String> result = new ArrayList<>(keySet);
			String key = result.get(j);
			newRow.setField(i, map.get(key));
		}
		
		System.out.println(newRow);
		
		
	}

	

}
