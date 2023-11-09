package com.flink.demo.jdbc;

import java.util.List;
import java.util.Map;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;

import com.vrv.rule.util.JdbcConnectionUtil;

public class MysqlSource extends RichSourceFunction<Map<String,Object>> {

	

    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        JdbcConnectionUtil.initJdbcTemplate();
    }

	
	@Override
	public void run(SourceContext<Map<String, Object>> ctx) throws Exception {
		List<Map<String,Object>> list = JdbcConnectionUtil.querySqlForList("select * from test");
		for (Map<String, Object> map : list) {
			ctx.collect(map);
		}
		
	}

	@Override
	public void cancel() {
		
	}

}
