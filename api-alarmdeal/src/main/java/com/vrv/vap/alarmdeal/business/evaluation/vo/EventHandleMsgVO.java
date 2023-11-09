package com.vrv.vap.alarmdeal.business.evaluation.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 事件处置信息VO
 *
 * 数据主要来自business_intance表
 */
@Data
public class EventHandleMsgVO {
    /**
     * 事件名称
     */
    private String eventName;
    /**
     * 事件处置时间
     * finish_date
     */
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date eventHandleDate;
    /**
     * 事件处理人姓名
     * deal_peoples子段中逗号分割最后一个人
     */
    private String eventHandleUserName;
    /**
     * 是否误报
     * busi_args中is_misreport
     */
    private String isMisreport;
    /**
     * 是否造成失密
     * busi_args中result_evaluation
     */
    private String resultEvaluation;
    /**
     * 事件成因说明
     * busi_args中cause
     */
    private String cause;
    /**
     * 事件详情过程
     * busi_args中event_inquriy
     */
    private String eventInquriy;
    /**
     * 失泄密情况
     * busi_args中result_details
     */
    private String resultDetails;
    /**
     * 案件依据
     * busi_args中case_base
     * （如果造成了失泄密则需要展示此字段，否则不需要展示）
     */
    private String caseBase;

    /**
     * 整改措施
     * busi_args中zjgRevise
     */
    private String zjgRevise;
}
