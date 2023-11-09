package com.vrv.rule.ruleInfo.udf;

import java.text.ParseException;
import java.util.Date;

import org.apache.flink.table.functions.ScalarFunction;

import com.vrv.rule.resource.impl.TimeResourceRefImpl;
import com.vrv.rule.util.DateUtil;
import com.vrv.rule.util.VapUtil;

/**
 * 时间资源引用的function
 * @author wd-pc
 *
 */
public class TimeResourceFunction extends ScalarFunction {

	private static final long serialVersionUID = 1L;
    
	public TimeResourceFunction() {
		
	}
	
	/**
	 * 进行对应的操作
	 * @param objArray
	 * @return
	 */
	public int  eval(Object obj,String sign,String content){    //某一个属性 ，符号（包含或者是不包含），内容
		TimeResourceRefImpl timeResourceRef=new TimeResourceRefImpl();
		String[] contentArr = content.split(",");
		timeResourceRef.setContent(contentArr);
		boolean signResult = VapUtil.getResourceResultBySign(sign);
		String timeStr = obj.toString();
		Date date = null;
		try {

			boolean result = DateUtil.isDateFormatValid(timeStr, DateUtil.DEFAULT_DATE_PATTERN);
            if(result){ //说明是标准时间格式
				date = DateUtil.parseDate(timeStr, DateUtil.DEFAULT_DATE_PATTERN);
			}else{
				//采用UTC时间格式,先转换成为标准时间格式
				timeStr= DateUtil.utcToDefaultFormat(timeStr);
				date = DateUtil.parseDate(timeStr, DateUtil.DEFAULT_DATE_PATTERN);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		boolean result = timeResourceRef.computer(date, signResult);
		if(result) {
			return 1;
		}else {
			return 0;
		}
   }
	
	
}
