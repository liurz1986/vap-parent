package com.flink.demo.analysis;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.vrv.rule.vo.ExpVO;
import com.vrv.rule.vo.LogicOperator;

/**
 * 过滤组件解析器
 * @author wd-pc
 *
 */
public class FilterOperatorModel {

	
	public static void main(String[] args) {
		Gson  gson = new Gson();
		LogicOperator logicFilter = getLogicFilter();
		String filterCondition = logicFilter.getFilterCondition();
		System.out.println(filterCondition);
//		String json = gson.toJson(logicFilter);
//		System.out.println(json);
//		LogicOperator logicOperator = gson.fromJson(json, LogicOperator.class);
//		System.out.println(logicOperator);
		
		
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
		expVO.setField("switchjoinroute.device_ip");
		expVO.setName("设备IP");
		expVO.setOperator("=");
		expVO.setValue("192.168.118.91");
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
		expVO3.setField("switchjoinroute.device_name");
		expVO3.setName("设备名称");
		expVO3.setOperator("=");
		expVO3.setValue("ASUS");
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
