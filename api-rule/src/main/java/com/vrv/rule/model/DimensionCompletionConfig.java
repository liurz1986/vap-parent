package com.vrv.rule.model;

import com.vrv.rule.vo.LogicOperator;
import lombok.Data;

import java.io.Serializable;

/**
 * 集合算子数据结构
 */
@Data
public class DimensionCompletionConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dimensionTableName; //关联维表表名
    private String dimensionFieldName; //关联选择字段，逗号分割
    private String dimensionAliasName;//关联选择字段别名，逗号分割
    private String filterCon;//过滤条件
    private String highLevelSearchCon;//高级搜索条件
    private LogicOperator loginExp;  //逻辑表达式
    private Boolean collectionOutput; //是否集合输出
    private String collectionOutputType; //集合输出类型
    private String collectionName; //集合字段输出名称
    private String collectionLabel; //集合字段输出中文名称
    private String tableType;   //维表类型   baseline 、 base（不可填参数） 、 other






}
