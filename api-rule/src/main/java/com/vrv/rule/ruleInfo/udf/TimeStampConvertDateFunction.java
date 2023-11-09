package com.vrv.rule.ruleInfo.udf;

import com.vrv.rule.util.DateUtil;
import org.apache.flink.table.functions.ScalarFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 将时间戳转换成为日期格式（yyyy-mm-dd hh:mm:ss）
 */
public class TimeStampConvertDateFunction extends ScalarFunction {

    private static Logger logger = LoggerFactory.getLogger(TimeStampConvertDateFunction.class);


    public TimeStampConvertDateFunction(){}

    public String  eval(Object inputObj){
        if(inputObj instanceof Long){
            Long timeStamp = (Long)inputObj;
            String date = DateUtil.timeStampToDateString(timeStamp);
            return date;
        }else {
            throw new RuntimeException(inputObj+"不是长整型，请检查！");
        }
    }


}
