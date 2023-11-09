package com.vrv.rule.ruleInfo.cal;

/**
 * 数量计算接口
 * @author wudi
 * @date 2023/6/27 15:22
 */
public interface ICountCal {

 /**
  * 计算总数(返回int类型)
  * @param countObject
  * @return
  */
 public int calCountByInt(Object countObject);

 /**
  * 计算总数（返回double类型）
  * @param countObject
  * @return
  */
 public double calCountByDouble(Object countObject);

 /**
  * 初始化count赋值
  * @param countObject
  * @param fieldType
  * @return
  */
 public Object calCountByInit(Object countObject,String fieldType);


}
