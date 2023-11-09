package com.vrv.rule.ruleInfo.cal;

import com.vrv.rule.ruleInfo.cal.params.DistinctCountCalParams;

import java.util.Map;

/**
 * dinctCount接口
 * @author wudi
 * @date 2023/6/27 16:22
 */
public interface IdistinctCountCal {

 /**
  * 转换成为double数值类型
  * @param distinctCountCalParams
  * @return
  */
 public Map<String,Object> calDistinctCountByDouble(DistinctCountCalParams distinctCountCalParams);


 /**
  * 转换成为long数值类型
  * @param distinctCountCalParams
  * @return
  */
 public Map<String,Object> calDistinctCountByLong(DistinctCountCalParams distinctCountCalParams);

}
