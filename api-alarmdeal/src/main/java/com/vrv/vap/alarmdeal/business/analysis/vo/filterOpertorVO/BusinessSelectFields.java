package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import lombok.Data;

/**
 * 业务查询字段条件
 *
 */
@Data
public class BusinessSelectFields {
    private String selectField;//选择的字段,  //
    private String relationship;//"=", //通过字段类型获得能够选择的关系
            //根据type字段的类型匹配出threshHold字段值类型；
    private String  threshhold;  //1（字符串或者字符串数组）（手动填写，后端）
    private String selectFieldType;
}
