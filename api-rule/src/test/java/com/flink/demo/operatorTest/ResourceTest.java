package com.flink.demo.operatorTest;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.vrv.rule.vo.ExpVO;
import com.vrv.rule.vo.LogicOperator;



public class ResourceTest {
    
	private LogicOperator logicOperator = new LogicOperator();
	
	@Before
	public void initResource() {
		logicOperator.setKey("6679202");
		logicOperator.setType("AND");
		logicOperator.setParent("");
		List<LogicOperator> list = new ArrayList<>();
		LogicOperator childLogicOperator1 = new LogicOperator();
		childLogicOperator1.setKey("3749645");
		childLogicOperator1.setType("filter");
		childLogicOperator1.setParent("6679202");
		ExpVO exp1 = new ExpVO();
		exp1.setResourceType("port");
		exp1.setName("异常端口");
		exp1.setOperator("contain");
		exp1.setValue("80,83~90");
		exp1.setField("exceptionCount");
		exp1.setValueType("resource");
		childLogicOperator1.setExp(exp1);
		list.add(childLogicOperator1);
		LogicOperator childLogicOperator2 = new LogicOperator();
		childLogicOperator2.setKey("9157424");
		childLogicOperator2.setType("filter");
		childLogicOperator2.setParent("6679202");
		ExpVO exp2 = new ExpVO();
		exp2.setResourceType("ip");
		exp2.setName("源ip");
		exp2.setOperator("contain");
		exp2.setValue("127.0.0.1,192.168.102.24/10");
		exp2.setField("srcIp");
		exp2.setValueType("resource");
		childLogicOperator2.setExp(exp2);
		list.add(childLogicOperator2);
		logicOperator.setFilters(list);
		
	}
	
	@Test
	public void testLogicOperator(){
		String filterCondition = logicOperator.getFilterCondition();
		System.out.println(filterCondition);
	}
	
	
	
	@Test
	public void testAggLogicOperator(){
		List<LogicOperator> filters = logicOperator.getFilters();
		for (LogicOperator logicOperator : filters) {
			String resourceType = logicOperator.getExp().getResourceType();
			switch (resourceType) {
			case "date":
				
				break;
			case "ip":
				boolean resourceResult1 = logicOperator.getExp().getResourceResult("127.0.0.1");
				System.out.println(resourceResult1);			
			break;
			case "port":
				boolean resourceResult2 = logicOperator.getExp().getResourceResult("80");
				System.out.println(resourceResult2);		
		   break;
			default:
				break;
			}
		}
	}
	
	
}
