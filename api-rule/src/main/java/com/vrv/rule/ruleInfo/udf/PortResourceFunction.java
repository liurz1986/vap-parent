package com.vrv.rule.ruleInfo.udf;

import org.apache.flink.table.functions.ScalarFunction;

import com.vrv.rule.resource.impl.PortResourceRefImpl;
import com.vrv.rule.util.VapUtil;

/**
 * 端口资源引用的function
 * @author wd-pc
 *
 */
public class PortResourceFunction extends ScalarFunction {

	private static final long serialVersionUID = 1L;
    
	public PortResourceFunction() {
		
	}
	
	/**
	 * 进行对应的操作
	 * @param objArray
	 * @return
	 */
	public int  eval(Object obj,String sign,String content){    //某一个属性 ，符号（包含或者是不包含），内容
		PortResourceRefImpl portResourceRef=new PortResourceRefImpl();
		String[] contentArr = content.split(",");
		portResourceRef.setContent(contentArr);
		boolean signResult = VapUtil.getResourceResultBySign(sign);
		boolean result = portResourceRef.computer(obj, signResult);
		if(result) {
			return 1;
		}else {
			return 0;
		}
   }
	
	
}
