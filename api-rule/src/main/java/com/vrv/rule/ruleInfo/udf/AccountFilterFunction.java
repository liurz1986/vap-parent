package com.vrv.rule.ruleInfo.udf;

import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.types.Row;

/**
 * Account过滤UDF
 * @author wd-pc
 *
 */
public class AccountFilterFunction extends ScalarFunction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    
	private static volatile int count = 0;
	
	/**
	 * 进行对应的操作
	 * @param objArray
	 * @return
	 */
	public int  eval(Object obj,String sign,Long threadHold){    //数组类型，把这个数组抽象化（对数组进行遍历操作）
		try {
			Long fieldValue=(Long)obj;
    		if(UdfFunctionUtil.compareFieldBySign(fieldValue,sign,threadHold)){
    			count++;
    		}
    	}catch(Exception e){
    		throw new RuntimeException("自定义报错",e);
    	}
	   return count;
   }
	
}
