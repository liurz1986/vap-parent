package com.vrv.rule.vo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class DimensionKeyVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String dimensionTableName; //维表名称
    private String dimensionFieldName; //维表字段名称
    private String filterCondition; //逻辑树过滤条件
    private String highFilterCondition; //高级过滤条件
    private String filterCode; //过滤器编码
    private String ruleCode; //规则编码


}
