package com.vrv.vap.alarmdeal.business.evaluation.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import javax.persistence.*;

/**
 * 自查自评策略配置表
 * @Date 2023-09
 * @author liurz
 */
@Data
@Table(name="self_inspection_evaluation_config")
@Entity
@ApiModel(value = "自查自评策略配置表")
public class SelfInspectionEvaluationConfig {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 设置主键生成策略为：主键自增长，适用于MySQL等部分数据库
     private int id;
    /**
     * 检查大类
     */
    @Column(name = "check_type")
     private String checkType;
    /**
     * 成因类型
     */
    @Column(name = "genetic_type")
    private String geneticType;
    /**
     * 事件部门默认数量
     * 内置数据中：有全部
     */
    @Column(name = "department_num")
    private int departmentnum;
    /**
     * 部门数量是否支持修改(是、否)
     */
    @Column(name = "department_modify")
    private String departmentModify;
    /**
     * 事件频率阀值
     */
    @Column(name = "threshold_count")
    private int thresholdCount;
    /**
     * 待查部门
     */
    @Column(name = "check_department")
    private String checkdepartment;
    /**
     * 推荐条件
     */
    @Column(name = "sell_conditions")
    private String sellConditions;
    /**
     * 推荐原因
     */
    @Column(name = "sell_reason")
    private String sellReason;
    /**
     * 涉事人角色：无要求--表示对角色没有要求
     */
    @Column(name = "role_name")
    private String roleName;

    /**
     * 维度维度：部门、用户
     * dep 、user
     * user：人员(同一个部门同一个人)、人员(同一个部门不同的人)
     *
     */
    @Column(name = "dimension")
    private String dimension;
}
