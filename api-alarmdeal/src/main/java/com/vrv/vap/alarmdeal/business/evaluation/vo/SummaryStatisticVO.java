package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;

@Data
public class SummaryStatisticVO {
    /**
     * 检查大类
     */
    private String checkType;
    /**
     * 成因类型
     */
    private String geneticType;
    /**
     * 待查部门名称
     */
    private String orgName;

    /**
     * 发生次数
     */
    private int occurCount;
    /**
     * 自查自评状态:自查自评状态(0:未开始,1:已自查自评)
     */

    private String curStatus;

    /**
     * 自查自评结果
     */
    private String evResult;
    /**
     * 整改情况
     */
    private String rectification;
}
