package com.vrv.vap.alarmdeal.business.evaluation.vo;

import lombok.Data;

/**
 * 自查自评结果页面详情
 */
@Data
public class EvaluationResultDeatilVO {
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
     * 自查自评结果
     */
    private String evResult;
    /**
     * 整改情况说明
     */
    private String rectification;
    /**
     * 提示
     */
    private String prompt;


}
