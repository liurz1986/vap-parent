package com.vrv.rule.ruleInfo.udf;

import java.util.List;
import java.util.regex.Pattern;

import com.google.gson.reflect.TypeToken;
import org.apache.flink.table.functions.ScalarFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.vrv.rule.resource.impl.RegexResourceRefImpl;
import com.vrv.rule.util.VapUtil;

/**
 * 正则表达式UDF
 * @author Administrator
 *
 */
public class RegularExpressionFunction extends ScalarFunction {

	private static Gson gson = new Gson();
	
	private static Logger logger = LoggerFactory.getLogger(RegularExpressionFunction.class);
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 正则表达式UDF正则表达式
	 */
	public RegularExpressionFunction() {}
	
	private String element;
	private String parttern;
	
	
	public RegularExpressionFunction(String element,String parttern) {
		this.element  = element;
		this.parttern = parttern;
	} 
	
	
	/**
	 * 进行对应的操作
	 * @return
	 */
	public int  eval(Object obj,String pattern){    //正则表达式匹配
		String element = null;
		if(obj instanceof String) {  //表明是正则表达式类型
		    element = (String)obj;
		    logger.info("正则表达式：{}"+pattern);
			boolean matches = Pattern.matches(pattern, element);
			if(matches) { //匹配成功
				return 1;
			}else {
				return 0;
			}
		}else {
			return 0;
		}
   }
	
   
	/**
	 * 进行对应的操作
	 * @return
	 */
	public int  eval(Object obj,String sign,String content){    //某一个属性 ，符号（包含或者是不包含），内容
		List<String> contents = gson.fromJson(content, new TypeToken<List<String>>() {}.getType());
		List<String> contentList = VapUtil.getContentList(contents);
		RegexResourceRefImpl regexResourceImpl = new RegexResourceRefImpl(contentList);
		boolean signResult = VapUtil.getResourceResultBySign(sign);
		boolean result = regexResourceImpl.computer(obj, signResult);
		if(result) {
			return 1;
		}else {
			return 0;
		}
   }
	
	
	
	
	
}
