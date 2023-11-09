package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;

/**
 * 事件结果信息结果VO
 *
 *  其中必填字段：事件Id、部门名称、成因类型、事件责任人角色名称、事件责任人名称
 * 2023-09-06
 * @author vrv
 */
@Data
public class EventResultVO {
    /**
     * 事件Id:必填字段
     */
    private String eventGuid;
    /**
     * 事件责任人部门名称:必填字段
     */
    private String orgName;
    /**
     * 事件责任人部门Code
     */
    private String orgCode;
    /**
     * 成因类型:必填字段
     */
    private String geneticType;
    /**
     * 事件责任人角色名称:必填字段
     */
    private String roleName;
    /**
     * 事件责任人角色code
     */
    private String roleCode;

    /**
     * 事件责任人名称:必填字段
     */
    private String userName;

}
