package com.vrv.rule.ruleInfo.udf;

import com.vrv.rule.util.DateUtil;
import org.apache.flink.table.functions.ScalarFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 时间戳自定义函数
 * 功能：将yyyy-mm-dd hh:mm:ss转换成为时间戳数值类型
 */
public class TimeStampFunction extends ScalarFunction {

    private static Logger logger = LoggerFactory.getLogger(TimeStampFunction.class);

    private static final long serialVersionUID = 1L;

    public TimeStampFunction(){}

    public Long  eval(Object inputObj){
        if(inputObj instanceof String){
            String eventTime = (String)inputObj;
            Long timestamp = DateUtil.getTimestamp(eventTime, DateUtil.DEFAULT_DATE_PATTERN);
            return timestamp;
        }else {
            throw new RuntimeException(inputObj+"不是字符串类型，请检查！");
        }
    }

}
