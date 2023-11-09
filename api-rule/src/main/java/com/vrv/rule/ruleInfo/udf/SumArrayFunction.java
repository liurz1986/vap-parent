package com.vrv.rule.ruleInfo.udf;

import java.lang.reflect.Field;

import org.apache.flink.table.functions.ScalarFunction;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年10月14日 下午3:07:32 
* 类说明     求总数的udf
*/
public class SumArrayFunction extends  ScalarFunction {

	
	 
	private static final long serialVersionUID = 1L;
   
	public SumArrayFunction(){
		
	}
	
	
	
   
   public long  eval(Object[] objArray,String fieldName) {
	   long sum = 0;
	   for (Object obj : objArray) {
	    	try {
	    		Field field = obj.getClass().getDeclaredField(fieldName);
	    		field.setAccessible(true);
	    		Class<?> type = field.getType();
	    		Long fieldValue=UdfFunctionUtil.compareRelateType(type,fieldName,field, obj);
	    		sum+=fieldValue;
	    	}catch(Exception e){
	    		throw new RuntimeException("反射获取数据出现异常",e);
	    	}
		}
	   return sum;
   }
   
   
   
  
   
	
	
}
