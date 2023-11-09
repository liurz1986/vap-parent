package com.vrv.vap.alarmdeal.business.evaluation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;


/**
 * 自查自评中间VO
 */
@Data
public class SelfInspectionEvaluationVO {
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
     * 待查部门名称
     */
    private String orgName;

    /**
     * 发生次数
     */
    private String occurCount;
    /**
     * 自查自评状态
     * (0:未开始,1:已自查自评)
     */
    private  String status;
    /**
     * 自查自评项产生时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    /**
     * 自查自评开展时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date evTime;
    /**
     * 自查自评人员
     */
    private String evUserName;
    /**
     * 自查自评人员code
     */
    private String evUserCode;
    /**
     * 自查自评结果
     */
    private String evResult;
    /**
     * 整改情况说明
     */
    private String rectification;
    /**
     * 关联事件ID(多个逗号分割)
     */
    private String eventIds;
    /**
     * 关联自查自评策略中间表id
     */
    private String refProcessId;

    /**
     * 关联策略id
     */
    private int refId;

}
