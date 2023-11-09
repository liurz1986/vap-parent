package com.vrv.vap.alarmdeal.business.evaluation.vo;
import lombok.Data;

import javax.persistence.Column;


/**
 * 自查自评中间VO
 */
@Data
public class SelfInspectionEvaluationProcessVO {
    /**
     * 主键ID
     */
     private String id;
    /**
     * 检查大类
     */
     private String checkType;
    /**
     * 成因类型
     */
    private String geneticType;
    /**
     * 部门名称
     */
    private String orgName;
    /**
     * 待查部门(策略中)
     */
    @Column(name = "check_dep")
    private String checkDep;

    /**
     * 事件数量
     */
    private int eventCount;
    /**
     * 关联事件ID(多个逗号分割)
     */
    private String eventIds;
    /**
     * 是否已入自查自评结果表(0:未入结果表 1：已入结果表)
     */
    private String resultStatus;

    /**
     * 关联策略id
     */
    private int refId;

}
