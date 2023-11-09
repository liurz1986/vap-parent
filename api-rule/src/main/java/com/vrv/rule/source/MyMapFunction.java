package com.vrv.rule.source;

import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.typeutils.ResultTypeQueryable;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月17日 下午4:42:45 
* 类说明 
*/
public interface MyMapFunction<T, O> extends MapFunction<T, O>, ResultTypeQueryable<O>  {

}
