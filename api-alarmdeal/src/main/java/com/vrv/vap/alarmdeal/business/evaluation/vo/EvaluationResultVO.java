package com.vrv.vap.alarmdeal.business.evaluation.vo;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 成因类型下的自查自评结果数据
 */
@Data
public class EvaluationResultVO {
    /**
     * 检查大类
     */
    private String checkType;

    /**
     * 待查部门名称
     */
    private String orgName;

    /**
     * 自查自评结果
     */
    private String evResult;
    /**
     * 整改情况
     */
    private String rectification;

    /**
     * 自查自评开展时间
     */
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date evTime;
}
