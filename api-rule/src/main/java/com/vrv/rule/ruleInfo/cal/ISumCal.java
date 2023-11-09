package com.vrv.rule.ruleInfo.cal;

import com.vrv.rule.ruleInfo.cal.params.SumCalParams;
import org.apache.flink.types.Row;

/**
 * @author wudi
 * @date 2023/6/28 11:12
 */
public interface ISumCal {

 /**
  * 返回数据为long
  * @param sumCalParams
  * @return
  */
 public Long calSumByLong(SumCalParams sumCalParams);

 /**
  * 返回数据为double
  * @param sumCalParams
  * @return
  */
 public Double calSumByDouble(SumCalParams sumCalParams);


}
