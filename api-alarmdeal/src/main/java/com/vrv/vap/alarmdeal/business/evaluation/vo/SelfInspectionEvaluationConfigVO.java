package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;


/**
 * 自查自评策略配置VO
 */
@Data

public class SelfInspectionEvaluationConfigVO {
    /**
     * 主键ID
     */
     private int id;
    /**
     * 检查大类
     */
     private String checkType;
    /**
     * 成因类型
     */
    private String geneticType;
    /**
     * 部门数量
     */
    private int departmentnum;
    /**
     * 部门数量是否支持修改
     */
    private String departmentModify;
    /**
     * 事件频率阀值
     */
    private int thresholdCount;
    /**
     * 待查部门
     */
    private String checkdepartment;
    /**
     * 推荐条件
     */
    private String sellConditions;
    /**
     * 推荐原因
     */
    private String sellReason;
    /**
     * 涉事人角色
     */
    private String roleName;
}
