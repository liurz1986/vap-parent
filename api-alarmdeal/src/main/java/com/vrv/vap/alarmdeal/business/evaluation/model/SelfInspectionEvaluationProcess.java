package com.vrv.vap.alarmdeal.business.evaluation.model;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import javax.persistence.*;

/**
 * 自查自评中间表
 *
 * @Date 2023-09
 * @author liurz
 */
@Data
@Table(name="self_inspection_evaluation_process")
@Entity
@ApiModel(value = "自查自评中间表")
public class SelfInspectionEvaluationProcess {
    /**
     * 主键ID
     */
    @Id
    private String id;
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
     * 部门名称
     */
    @Column(name = "org_name")
    private String orgName;

    /**
     * 待查部门(策略中)
     */
    @Column(name = "check_dep")
    private String checkDep;

    /**
     * 事件处理人名称
     */
    @Column(name = "user_name")
    private String userName;
    /**
     * 关联事件ID(多个逗号分割)
     */
    @Column(name = "event_ids")
    private String eventIds;

    /**
     *  检查大类、成因类型、部门名称、用户名称相同记录数(同一个部门同一个用户的事件数量)
     */
    @Column(name = "event_count")
    private int eventCount;
    /**
     * 关联策略id
     */
    @Column(name = "ref_id")
    private int refId;


}
