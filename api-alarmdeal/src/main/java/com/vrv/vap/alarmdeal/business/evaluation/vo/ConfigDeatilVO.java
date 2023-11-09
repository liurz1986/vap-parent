package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;

@Data
public class ConfigDeatilVO {

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
    private String checkdepartment;
    /**
     * 推荐条件
     */
    private String sellConditions;
    /**
     * 推荐原因
     */
    private String sellReason;

}
