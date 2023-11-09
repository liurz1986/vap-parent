package com.flink.demo.operatorTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.model.EventTable;
import com.vrv.rule.model.FilterOperator;
import com.vrv.rule.util.JdbcSingeConnectionUtil;
import com.vrv.rule.util.JdbcSqlConstant;

/**
 * jdbc工具类测试
 * @author wd-pc
 *
 */
public class JdbcUtilTest {

	
	private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
	
	/**
	 * 测试对应的Jdbc
	 */
	@Test
	public void testJdbcTest() {
		
		//String sql = JdbcSqlConstant.FILTER_TABLE_SQL;
		String sql = "select * from vul_attack_view limit 0,100";
		//List<String> list = new ArrayList<>();
		//list.add("6f339035746a46f698f71dbf519a67fc");
	 	//List<Map<String, Object>> list2 = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql, jsons);
		List<Map<String,Object>> querySqlForList = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql);
		System.out.println(querySqlForList);
	}
}
