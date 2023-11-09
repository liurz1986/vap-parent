package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;

@Data
public class EvResultHandleVO {

    /**
     * 检查大类
     */
    private String checkType;
    /**
     * 成因类型
     */
    private String geneticType;
    /**
     * 待查部门
     */
    private String depName;

    /**
     * 事件Id
     */
    private String eventId;
}
