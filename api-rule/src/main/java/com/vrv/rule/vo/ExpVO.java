package com.vrv.rule.vo;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.vrv.rule.util.EventTableHelper;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.resource.impl.IpResourceRefImpl;
import com.vrv.rule.resource.impl.PortResourceRefImpl;
import com.vrv.rule.resource.impl.RegexResourceRefImpl;
import com.vrv.rule.resource.impl.TimeResourceRefImpl;
import com.vrv.rule.ruleInfo.udf.UdfFunctionUtil;
import com.vrv.rule.util.DateUtil;
import com.vrv.rule.util.VapUtil;

import lombok.Data;

@Data
public class ExpVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String name; //中文名
	private String fieldType; //字段类型
	private String field; //英文名a.x
	private String operator; //操作符
	private String value; //阈值b.y
	private String valueType; //constant or attribute or resource
	private String resourceType; //资源类型
	private String resguid; //资源标识
	private boolean nonAttr; //非属性
	private String expVlueType; //表达式类型（常量还是属性）
	
	public boolean getResult(Map<String ,Row> map,List<FieldInfoVO> inputFieldInfoVOs){
		String[] fieldArray = field.split("\\.");
		boolean result = false;
		if(fieldArray.length==2) {
			String tableName = fieldArray[0];
			String fieldName = fieldArray[1];
			Row row = map.get(tableName);
			EventColumn eventColumn = EventTableHelper.getFieldsInfo(tableName, fieldName,inputFieldInfoVOs);
			Integer order = eventColumn.getOrder();
			String fieldType = eventColumn.getFieldType();
			switch (fieldType) {
			case "varchar":
				result = getVarcharCompareResult(map, inputFieldInfoVOs, fieldArray, row, order);
				break;
	        case "bigint":
	        case "double":
	        case "float":
	        case "int":   
	        	result=getValueCompareResult(map, inputFieldInfoVOs, fieldArray, row, order,fieldType);
				break;
	        case "boolean":   
	        	result=getBooleanCompareResult(map, inputFieldInfoVOs, fieldArray, row, order);
				break;
			default:
				break;
			}
			
		}else {
			throw new RuntimeException("fieldName不符合规则,规范为a.x,请检查！");
		}
		
		return result;
	}

    /**
     * 获得数值类型的比较
     * @param map
     * @param inputFieldInfoVOs
     * @param fieldArray
     * @param row
     * @param order
     * @return
     */
	private boolean getValueCompareResult(Map<String, Row> map, List<FieldInfoVO> inputFieldInfoVOs, String[] fieldArray,
			Row row, Integer order,String fieldType) {
		   operator = UdfFunctionUtil.getOperatoror(operator, nonAttr);
		   Object fieldValue = row.getField(order);
		   boolean result = false;
		   switch (valueType) {
			case "constant":
				if(operator.equals("between")) {
					result = UdfFunctionUtil.compareFieldByBetween(fieldValue, value);
				}else if(operator.equals("not between")){
					result = UdfFunctionUtil.compareFieldByNotBetween(fieldValue, value);
				}else {
					result = UdfFunctionUtil.compareFieldBySign(fieldValue, operator, Long.valueOf(value)); //equal or like					
				}
				 break;
	        case "attribute":
	        	result = getAttributeValue(map, inputFieldInfoVOs, fieldArray, fieldValue);
				break;
	        case "resource":
	        	result = getResourceResultValueType(fieldValue,fieldType);
				break;
			default:
				break;
		}
		return result;
	}

	private boolean getAttributeValue(Map<String, Row> map, List<FieldInfoVO> inputFieldInfoVOs, String[] fieldArray,
			Object fieldValue) {
		boolean result= false;
		String[] valueTppeArr = value.split("\\.");
		if(valueTppeArr.length==2){
			String valueTableName = fieldArray[0];
		    String valueFieldName = fieldArray[1];
			Row row2 = map.get(valueTableName);
			EventColumn eventColumn2 = EventTableHelper.getFieldsInfo(valueTableName, valueFieldName,inputFieldInfoVOs);
			Integer order2 = eventColumn2.getOrder();
			Long fieldValue2 = (Long)row2.getField(order2);
			result = UdfFunctionUtil.compareFieldBySign(fieldValue, operator, fieldValue2);
		}else {
			throw new RuntimeException("valueTppeArr个数不等于2，请检查！");
		}
		return result;
	}

	
	private boolean getBooleanCompareResult(Map<String, Row> map, List<FieldInfoVO> inputFieldInfoVOs, String[] fieldArray,
			Row row, Integer order) {
		 boolean result = false;
		 Boolean fieldValueStr = (Boolean)row.getField(order);
		 switch (valueType) {
			case "constant":
				 Boolean booleanValue = Boolean.valueOf(value);
				 result = UdfFunctionUtil.compareFieldBySignBoolean(fieldValueStr,booleanValue);
				 break;
	        case "attribute":
	        	result = getAttributeResult(map, inputFieldInfoVOs, fieldArray, fieldValueStr);
				break;
			default:
				break;
		}
		return result;
	}
	
	/**
	 * 获得字符串类型的比较
	 * @param map
	 * @param inputFieldInfoVOs
	 * @param fieldArray
	 * @param row
	 * @param order
	 * @return
	 */
	private boolean getVarcharCompareResult(Map<String, Row> map, List<FieldInfoVO> inputFieldInfoVOs, String[] fieldArray,
			Row row, Integer order) {
		 boolean result = false;
		 String fieldValueStr = (String)row.getField(order);
		 if(StringUtils.isNotEmpty(fieldValueStr)) {
			 operator = UdfFunctionUtil.getOperatoror(operator, nonAttr);
			 switch (valueType) {
			 case "constant":
				 result = UdfFunctionUtil.compareFieldBySignStr(fieldValueStr, operator, value); //equal or like
				 break;
			 case "attribute":
				 result = getAttributeResult(map, inputFieldInfoVOs, fieldArray, fieldValueStr,operator);
				 break;
			 case "resource":
				 result = getResourceResult(fieldValueStr);
				 break;
			 default:
				 break;
			 }
		 }
		return result;
	}

	
	private boolean getResourceResultValueType(Object field,String fieldType) {
		if(fieldType.equals("int")){
			boolean result = false;
			String[] content = value.split(",");
			boolean sign = VapUtil.getResourceResultBySign(operator);
			switch (resourceType) {
			case "date":
				throw new RuntimeException(field+"为int类型字段，不能够引用时间类型资源，请检查！");
	        case "ip":
	        	throw new RuntimeException(field+"为int类型字段，不能够引用IP类型资源，请检查！");
	        case "port":
	        	 PortResourceRefImpl portResourceRef=new PortResourceRefImpl();
	        	 portResourceRef.setContent(content);
	        	 Integer port = (Integer)field;
	        	 result = portResourceRef.computer(port, sign);
				break;
			default:
				break;
			}
			return result;
		}else{
			throw new RuntimeException(field+"不是int类型属性，请检查！");
		}
	}
	
	
	
	public boolean getResourceResult(String fieldValueStr) {
		boolean result = false;
		boolean sign = VapUtil.getResourceResultBySign(operator);
		String[] content = value.split(",");
		switch (resourceType) {
		case "date":
			     TimeResourceRefImpl timeResourceRef=new TimeResourceRefImpl();
		    try {
			     Date date = DateUtil.parseDate(fieldValueStr, DateUtil.DEFAULT_DATE_PATTERN);
			     timeResourceRef.setContent(content);
			     result=timeResourceRef.computer(date, sign);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			break;
        case "ip":
        	IpResourceRefImpl ipResourceRef=new IpResourceRefImpl();
        	ipResourceRef.setContent(content);
        	result = ipResourceRef.computer(fieldValueStr, sign);
			break;
        case "port":
        	 PortResourceRefImpl portResourceRef=new PortResourceRefImpl();
        	 portResourceRef.setContent(content);
        	 Integer port = Integer.valueOf(fieldValueStr);
        	 result = portResourceRef.computer(port, sign);
			break;
        case "regex":
        	Gson gson = new Gson();
       	    List<String> lists =gson.fromJson(value, new TypeToken<List<String>>() {}.getType());
       	    List<String> contentList = VapUtil.getContentList(lists);
       	    RegexResourceRefImpl regexResourceRef = new RegexResourceRefImpl(contentList);
	       	result = regexResourceRef.computer(fieldValueStr, sign);
			break;
		default:
			break;
		}
		return result;
	}
	
	
	/**
	 * 获得属性值类型返回结果
	 * @param map
	 * @param inputFieldInfoVOs
	 * @param fieldArray
	 * @param fieldValueStr
	 * @return
	 */
	private boolean getAttributeResult(Map<String, Row> map, List<FieldInfoVO> inputFieldInfoVOs, String[] fieldArray,
			String fieldValueStr,String operator) {
		boolean result = false;
		String[] valueTppeArr = value.split("\\.");
		if(valueTppeArr.length==2){
			String valueTableName = fieldArray[0];
			String valueFieldName = fieldArray[1];
			Row row2 = map.get(valueTableName);
			EventColumn eventColumn2 = EventTableHelper.getFieldsInfo(valueTableName, valueFieldName,inputFieldInfoVOs);
			Integer order2 = eventColumn2.getOrder();
			String fieldValue2 = (String)row2.getField(order2);
			result = UdfFunctionUtil.compareFieldBySignStr(fieldValueStr, operator, fieldValue2);
		}else {
			throw new RuntimeException("valueTppeArr个数不等于2，请检查！");
		}
		return result;
	}
	
	
	
	private boolean getAttributeResult(Map<String, Row> map, List<FieldInfoVO> inputFieldInfoVOs, String[] fieldArray,
			Boolean fieldValue) {
		boolean result = false;
		String[] valueTppeArr = value.split("\\.");
		if(valueTppeArr.length==2){
			String valueTableName = fieldArray[0];
			String valueFieldName = fieldArray[1];
			Row row2 = map.get(valueTableName);
			EventColumn eventColumn2 = EventTableHelper.getFieldsInfo(valueTableName, valueFieldName,inputFieldInfoVOs);
			Integer order2 = eventColumn2.getOrder();
			Object fieldValue2 = row2.getField(order2);
			if(fieldValue2 instanceof Boolean) {
				Boolean fieldResult = (Boolean)fieldValue2;
				result = UdfFunctionUtil.compareFieldBySignBoolean(fieldValue, fieldResult);
			}else {
				throw new RuntimeException(fieldValue2+"不是boolean类型，请检查！");
			}
		}else {
			throw new RuntimeException("valueTppeArr个数不等于2，请检查！");
		}
		return result;
	}
	
	
	
	
}
