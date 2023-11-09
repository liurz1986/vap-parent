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

public class AssetGuidRiskByJdbc extends RichAsyncFunction<Row,Row> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//等待可用连接的最大时间，单位毫秒，默认值为-1，表示永不超时。如果超过等待时间，则直接抛出JedisConnectionException；
    private static int TIMEOUT = 10000;
    private static final Logger logger = LoggerFactory.getLogger(AssetGuidRiskByJdbc.class);

    private String sql;
    private List<FieldInfoVO> inputFields;
    private List<FieldInfoVO> outputFileds;
    
    
    
    public AssetGuidRiskByJdbc(String sql,List<FieldInfoVO> inputFields,List<FieldInfoVO> outputFileds) {
    	this.sql = sql;
    	this.inputFields = inputFields;
    	this.outputFileds = outputFileds;
    }
    
    public AssetGuidRiskByJdbc() {
    	
    }
    
    
    
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        
    }
	
	@Override
	public void asyncInvoke(Row input, ResultFuture<Row> resultFuture) throws Exception {
		logger.info("进入异步I/O {}", "0000");
		CompletableFuture.supplyAsync(new Supplier<List<Row>>() {
			@Override
			public List<Row> get() {
				List<Row> results = new ArrayList<>();
				String guid = input.getField(0).toString();
				sql = sql +" and guid = '"+guid+"'";
				logger.info("打印输出sql：{}", sql);
				List<Map<String, Object>> list = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql);
				Map<String, Object> map = list.get(0);
				Row newRow = new Row(outputFileds.size());
				for (int i = 0;i < input.getArity(); i++) {
					newRow.setField(i, input.getField(i));
				}
				Set<String> keySet = map.keySet();
				int size = keySet.size();
				int begin = inputFields.size();
				int j = 0;
				for (int i = begin; i < begin+size; i++,j++) {
					List<String> result = new ArrayList<>(keySet);
					String key = result.get(j);
					newRow.setField(i, map.get(key));
				}
				results.add(Row.of(1,2,3,4));
				results.add(Row.of(1,2,3,4,5));
				return results;
			}
		}).thenAccept((List<Row> dbResult) -> {
			// 设置请求完成时的回调: 将结果传递给 collector
			resultFuture.complete(dbResult);
		});					
	
		
		
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
