package com.flink.demo.operatorTest;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.vrv.rule.ruleInfo.exchangeType.impl.MapExchangeTypeImpl;
import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;
import com.vrv.rule.model.filter.Attach;
import com.vrv.rule.model.filter.Column;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.ruleInfo.exchangeType.ExchangeUtil;
import com.vrv.rule.util.RoomInfoConstant;
import com.vrv.rule.vo.ExpVO;
import com.vrv.rule.vo.LogicOperator;


public class RuleOperator {

    
	FilterConfigObject filterConfigObject = new FilterConfigObject();
	Gson gson = new Gson();
	/**
	 * q(source)---->w(out)
	 */
	@Before
	public void initRuleOperator(){
		Exchanges[][] exchanges = new Exchanges[1][];  //定义一个二维数据，由一行
		exchanges[0] = new Exchanges[1]; //这个数组当中有三个exchange成员变量
		for (int i = 0; i < 1; i++){
			Exchanges exchange = new Exchanges();
			exchange.setType("map");
			List<String> sourceList = new ArrayList<>();
			sourceList.add("59755a18-90df-4411-8da8-c69403e3130f");
			exchange.setSources(sourceList);
			exchange.setTarget("8dc0e49b-2f4e-4316-bca5-6aa1afb1e2eb");
			LogicOperator logicFilter = getLogicFilter();
			String json = gson.toJson(logicFilter);
			exchange.setOptions(json);
			exchanges[0][i] = exchange;			
		}
	   Tables[][] tables = new Tables[1][];
	   tables[0] = new Tables[2];
       Tables table1 = new Tables();
	   table1.setName("switch");
	   table1.setLabel("交换机");
	   table1.setType("filter");
	   List<Column> columns = getColumn();
	   table1.setColumn(columns);
	   List<Attach> attachs = getAttachs();
	   table1.setAttachs(attachs);
	   table1.setId("8dc0e49b-2f4e-4316-bca5-6aa1afb1e2eb");
	   tables[0][0]=table1;
	   
	   
	   Tables table2 = new Tables();
	   table2.setId("59755a18-90df-4411-8da8-c69403e3130f");
	   table2.setName("switchEvent");
	   table2.setLabel("交换机原始日志");
	   table2.setType("filter");
	   List<Column> allcolumns = getAllColumn();
	   table2.setColumn(allcolumns);
	   
	  
	   tables[0][1]=table2;
	   
	   filterConfigObject.setExchanges(exchanges);
	   filterConfigObject.setTables(tables);
		
	   
	}
	
	
	private List<Attach> getAttachs() {
		List<Attach> attachs = new ArrayList<>();
		  Attach attach1 = new Attach();
		  attach1.setId(UUID.randomUUID().toString());
		  attach1.setType("key");
		  attach1.setOptions("assetGuid");
		  attachs.add(attach1);
		  return attachs;
	}

	
	
	

	
	private List<Column> getColumn() {
		  List<Column> list = new ArrayList<>();
		  Column column1 = new Column();
		  column1.setName("device_ip");
		  column1.setExp(null);
		  column1.setOrder(0);
		  column1.setDataType("varchar");
		  column1.setDataHint(null);
		  column1.setLabel("设备IP");
		  list.add(column1);
		  
		  Column column2 = new Column();
		  column2.setName("device_name");
		  column2.setExp(null);
		  column2.setOrder(1);
		  column2.setDataType("varchar");
		  column2.setDataHint(null);
		  column2.setLabel("设备名称");
		  list.add(column2);
		  return list;
	}
	
	
	
	private List<Column> getAllColumn() {
		  List<Column> list = new ArrayList<>();
		  Column column0 = new Column();
		  column0.setName("event_time");
		  column0.setExp(null);
		  column0.setOrder(0);
		  column0.setDataType("varchar");
		  column0.setDataHint(null);
		  column0.setLabel("处理时间");
		  list.add(column0);
		  
		  Column column1 = new Column();
		  column1.setName("event_level");
		  column1.setExp(null);
		  column1.setOrder(1);
		  column1.setDataType("varchar");
		  column1.setDataHint(null);
		  column1.setLabel("事件等级");
		  list.add(column1);
		  
		  Column column2 = new Column();
		  column2.setName("device_ip");
		  column2.setExp(null);
		  column2.setOrder(2);
		  column2.setDataType("varchar");
		  column2.setDataHint(null);
		  column2.setLabel("设备IP");
		  list.add(column2);
		  
		  Column column3 = new Column();
		  column3.setName("event_name");
		  column3.setExp(null);
		  column3.setOrder(3);
		  column3.setDataType("varchar");
		  column3.setDataHint(null);
		  column3.setLabel("事件名称");
		  list.add(column3);
		  
		  Column column4 = new Column();
		  column4.setName("report_msg");
		  column4.setExp(null);
		  column4.setOrder(4);
		  column4.setDataType("varchar");
		  column4.setDataHint(null);
		  column4.setLabel("原始字段");
		  list.add(column4);
		  
		  Column column5 = new Column();
		  column5.setName("msg_src");
		  column5.setExp(null);
		  column5.setOrder(5);
		  column5.setDataType("varchar");
		  column5.setDataHint(null);
		  column5.setLabel("来源");
		  list.add(column5);
		  
		  Column column6 = new Column();
		  column6.setName("indate");
		  column6.setExp(null);
		  column6.setOrder(6);
		  column6.setDataType("varchar");
		  column6.setDataHint(null);
		  column6.setLabel("入库时间");
		  list.add(column6);
		  
		  Column column7 = new Column();
		  column7.setName("security_level");
		  column7.setExp(null);
		  column7.setOrder(7);
		  column7.setDataType("varchar");
		  column7.setDataHint(null);
		  column7.setLabel("安全级别");
		  list.add(column7);
		  
		  Column column8 = new Column();
		  column8.setName("safety_margin_ip");
		  column8.setExp(null);
		  column8.setOrder(8);
		  column8.setDataType("varchar");
		  column8.setDataHint(null);
		  column8.setLabel("安全域关联IP");
		  list.add(column8);
		  
		  Column column9 = new Column();
		  column9.setName("log_type");
		  column9.setExp(null);
		  column9.setOrder(9);
		  column9.setDataType("varchar");
		  column9.setDataHint(null);
		  column9.setLabel("日志类别");
		  list.add(column9);
		  
		  Column column10 = new Column();
		  column10.setName("report_ip_num");
		  column10.setExp(null);
		  column10.setOrder(10);
		  column10.setDataType("bigint");
		  column10.setDataHint(null);
		  column10.setLabel("上报设备IP转换");
		  list.add(column10);
		  
		  Column column11 = new Column();
		  column11.setName("safety_margin");
		  column11.setExp(null);
		  column11.setOrder(11);
		  column11.setDataType("varchar");
		  column11.setDataHint(null);
		  column11.setLabel("安全域");
		  list.add(column11);
		  
		  Column column12 = new Column();
		  column12.setName("triggerTime");
		  column12.setExp(null);
		  column12.setOrder(12);
		  column12.setDataType("datetime");
		  column12.setDataHint(null);
		  column12.setLabel("发生时间");
		  list.add(column12);
		  
		  Column column13 = new Column();
		  column13.setName("report_ip");
		  column13.setExp(null);
		  column13.setOrder(13);
		  column13.setDataType("varchar");
		  column13.setDataHint(null);
		  column13.setLabel("上报设备IP");
		  list.add(column13);
		  
		  Column column14 = new Column();
		  column14.setName("device_name");
		  column14.setExp(null);
		  column14.setOrder(14);
		  column14.setDataType("varchar");
		  column14.setDataHint(null);
		  column14.setLabel("设备名称");
		  list.add(column14);
		  
		  Column column15 = new Column();
		  column15.setName("event_type");
		  column15.setExp(null);
		  column15.setOrder(15);
		  column15.setDataType("varchar");
		  column15.setDataHint(null);
		  column15.setLabel("事件类型");
		  list.add(column15);
		  
		  Column column16 = new Column();
		  column16.setName("event_detail");
		  column16.setExp(null);
		  column16.setOrder(16);
		  column16.setDataType("varchar");
		  column16.setDataHint(null);
		  column16.setLabel("事件详情");
		  list.add(column16);
		  
		  return list;
	}
	
	
	
	@Test
	public void testFilterConfigObject() {
		Exchanges exchange = new Exchanges();
		exchange.setType("filter");
		List<String> sourceList = new ArrayList<>();
		sourceList.add("59755a18-90df-4411-8da8-c69403e3130f");
		exchange.setSources(sourceList);
		exchange.setTarget("8dc0e49b-2f4e-4316-bca5-6aa1afb1e2eb");
		LogicOperator logicFilter = getLogicFilter();
		String json = gson.toJson(logicFilter);
		exchange.setOptions(json);	
		MapExchangeTypeImpl mapExchangeType = new MapExchangeTypeImpl();
		String dataStreamAnalysisSql = ExchangeUtil.getDataStreamAnalysisSql(filterConfigObject, exchange,RoomInfoConstant.ID_ROOM_TYPE);
		System.out.println(dataStreamAnalysisSql);
	}
	
	
	public static LogicOperator getLogicFilter() {
		LogicOperator logicOperator = new LogicOperator();
		List<LogicOperator> logicList = getChildrenLogicFilter();
		ExpVO expVO = new ExpVO();
		logicOperator.setExp(expVO);
		logicOperator.setFilters(logicList);
		logicOperator.setType("and");
		return logicOperator;
	}

	private static List<LogicOperator> getChildrenLogicFilter() {
		List<LogicOperator> logicList = new ArrayList<>();
		LogicOperator logicOperator1 = new LogicOperator();
		ExpVO expVO = new ExpVO();
		expVO.setField("a.speedRate");
		expVO.setName("速率");
		expVO.setOperator(">");
		expVO.setValue("1500");
		expVO.setValueType("constant");
		logicOperator1.setExp(expVO);
		logicOperator1.setType("filter");
		logicList.add(logicOperator1);
		
		
//		LogicOperator logicOperator2 = new LogicOperator();
//		logicOperator2.setExp(new ExpVO());
//		logicOperator2.setType("and");
//		List<LogicOperator> logicList2 = new ArrayList<>();
		
		LogicOperator logicOperator3 = new LogicOperator();
		ExpVO expVO3 = new ExpVO();
		expVO3.setField("a.speedRate");
		expVO3.setName("速率");
		expVO3.setOperator(">");
		expVO3.setValue("1400");
		expVO3.setValueType("constant");
		logicOperator3.setExp(expVO3);
		logicOperator3.setType("filter");
		//logicList2.add(logicOperator3);
		
		
//		LogicOperator logicOperator4 = new LogicOperator();
//		ExpVO expVO4 = new ExpVO();
//		expVO4.setField("a.speedRate");
//		expVO4.setName("速率");
//		expVO4.setOperator(">");
//		expVO4.setValue("1300");
//		expVO4.setValueType("constant");
//		logicOperator4.setExp(expVO4);
//		logicOperator4.setType("filter");
//		logicList2.add(logicOperator4);
//		logicOperator2.setFilters(logicList2);
		logicList.add(logicOperator3);
		
		return logicList;
	}
	
	
	
}
