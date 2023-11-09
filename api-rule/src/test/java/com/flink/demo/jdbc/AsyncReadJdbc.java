package com.flink.demo.jdbc;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vrv.rule.util.JdbcConnectionUtil;

public class AsyncReadJdbc extends RichAsyncFunction<String, List<Map<String,Object>>> {

	
	private static Logger logger = LoggerFactory.getLogger(AsyncReadJdbc.class);
	
	

	@Override
	public void open(Configuration parameters) throws Exception {
		super.open(parameters);
		JdbcConnectionUtil.initJdbcTemplate();
		

	}

	@Override
	public void asyncInvoke(String input, ResultFuture<List<Map<String,Object>>> resultFuture) throws Exception {
		
		CompletableFuture.supplyAsync(new Supplier<List<Map<String,Object>>>() {
            @Override
            public List<Map<String,Object>> get() {
            	List<Map<String,Object>> list = JdbcConnectionUtil.querySqlForList("select * from test");
            	return list;
            }
        }).thenAccept((List<Map<String,Object>> dbResult) -> {
            resultFuture.complete(Collections.singleton(dbResult));
        });	
	}

	

}
