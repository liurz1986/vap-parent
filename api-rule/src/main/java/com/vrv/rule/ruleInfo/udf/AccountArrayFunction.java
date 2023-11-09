package com.vrv.rule.ruleInfo.udf;

import java.lang.reflect.Field;

import org.apache.flink.table.functions.ScalarFunction;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年10月14日 下午5:40:33 
* 类说明    求个数的UDF   单条日志当中存在数组成员，每个数组成员的某一个属性比较阈值阈值，含有多少个这样的数组成员
*/
public class AccountArrayFunction extends ScalarFunction {
    
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public AccountArrayFunction(){
		
	}
			
	/**
	 * 进行对应的操作
	 * @param objArray
	 * @return
	 */
	public int  eval(Object[] objArray,String fieldName,String sign,Integer threadHold){    //数组类型，把这个数组抽象化（对数组进行遍历操作）
		int count = 0; //初始值
		long longValue = threadHold.longValue();
		for (Object obj : objArray) {
	    	try {
	    		Field field = obj.getClass().getDeclaredField(fieldName);
	    		field.setAccessible(true);
	    		Class<?> type = field.getType();
	    		Long fieldValue=UdfFunctionUtil.compareRelateType(type,fieldName,field, obj);
	    		if(UdfFunctionUtil.compareFieldBySign(fieldValue,sign,longValue)){
	    			count++;
	    		}
	    	}catch(Exception e){
	    		throw new RuntimeException("反射获取数据出现异常",e);
	    	}
		}
	   return count;
   }
	
	
	
	

	
	
}
