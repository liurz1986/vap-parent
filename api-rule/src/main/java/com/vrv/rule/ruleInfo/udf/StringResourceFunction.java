package com.vrv.rule.ruleInfo.udf;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.resource.impl.RegexResourceRefImpl;
import com.vrv.rule.resource.impl.StringResourceRefImpl;
import com.vrv.rule.util.VapUtil;
import org.apache.flink.table.functions.ScalarFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

/**
 * 字符串比较UDF
 * @author Administrator
 *
 */
public class StringResourceFunction extends ScalarFunction {


	private static Logger logger = LoggerFactory.getLogger(StringResourceFunction.class);

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 正则表达式UDF正则表达式
	 */
	public StringResourceFunction() {}


	/**
	 * udf 进行对应的操作
	 * @param inputObj
	 * @param sign
	 * @param compareObj
	 * @return
	 */
	public int  eval(Object inputObj,String sign,Object compareObj){    //某一个属性 ，符号（包含或者是不包含），内容
		StringResourceRefImpl stringResourceRef = new StringResourceRefImpl(compareObj);
		boolean signResult = VapUtil.getResourceResultBySign(sign);
		boolean result = stringResourceRef.computer(inputObj, signResult);
		if(result) {
			return 1;
		}else {
			return 0;
		}
   }
	
	
	
	
	
}
