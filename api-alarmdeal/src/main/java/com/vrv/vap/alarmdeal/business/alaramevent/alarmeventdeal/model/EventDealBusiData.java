package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.model;

import com.google.gson.annotations.SerializedName;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 事件处置业务数据表，记录事件处置过程中的业务数据，可以用到自查自身
 */
@Table(name = "event_deal_busi_data")
@Entity
@Data
public class EventDealBusiData {
    @Id
    private String guid;
    /**
     * 是否正常业务需要  1：是 0：否
     */
    @Column(name = "business_need")
    @SerializedName(value = "businessNeed", alternate = "is_business_need")
    private Integer businessNeed;
    /**
     * 是否误报 1：是 0：否
     */
    @Column(name = "false_positive")
    @SerializedName(value = "falsePositive", alternate = "is_false_positive")
    private Integer falsePositive;
    /**
     * 审批登记情况
     */
    @Column(name = "approve_info")
    @SerializedName(value = "approveInfo", alternate = "approve_info")
    private String approveInfo;
    /**
     * 设备基本情况
     */
    @Column(name = "device_info")
    @SerializedName(value = "deviceInfo", alternate = "device_info")
    private String deviceInfo;
    /**
     * 设备责任人情况
     */
    @Column(name = "device_res_person_list")
    @SerializedName(value = "deviceResPersonList", alternate = "device_res_person_list")
    private String deviceResPersonList;
    /**
     * 涉事人员情况
     */
    @Column(name = "event_res_person_list")
    @SerializedName(value = "eventResPersonList", alternate = "event_res_person_list")
    private String eventResPersonList;
    /**
     * 防护策略情况
     */
    @Column(name = "protection_strategy")
    @SerializedName(value = "protectionStrategy", alternate = "protection_strategy")
    private String protectionStrategy;
    /**
     * 恶意程序情况
     */
    @Column(name = "malware_info")
    @SerializedName(value = "malwareInfo", alternate = "malware_info")
    private String malwareInfo;
    /**
     * 文件下载或刻录记录
     */
    @Column(name = "download_files_list")
    @SerializedName(value = "downloadFilesList", alternate = "download_files_list")
    private String downloadFilesList;
    /**
     * 失泄密评估
     */
    @Column(name = "result_evaluation")
    @SerializedName(value = "resultEvaluation", alternate = "result_evaluation")
    private Integer resultEvaluation;
    /**
     * 详细描述失泄密情况
     */
    @Column(name = "result_details")
    @SerializedName(value = "resultDetails", alternate = "result_details")
    private String resultDetails;
    /**
     * 保密宣传教育情况
     */
    @Column(name = "confidentiality_publicity")
    @SerializedName(value = "confidentialityPublicity", alternate = "confidentiality_publicity")
    private String confidentialityPublicity;
    /**
     * 相关保密制度情况
     */
    @Column(name = "confidentiality_rules")
    @SerializedName(value = "confidentialityRules", alternate = "confidentiality_rules")
    private String confidentialityRules;
    /**
     * 事件成因类型
     */
    @Column(name = "cause_type")
    @SerializedName(value = "causeType", alternate = "cause_type")
    private String causeType;
    /**
     * 事件成因类型
     */
    @Column(name = "cause")
    private String cause;
    /**
     * 事件详细过程
     */
    @Column(name = "event_inquriy")
    @SerializedName(value = "eventInquriy", alternate = "event_inquriy")
    private String eventInquriy;
    /**
     * 整改措施
     */
    @Column(name = "rectification")
    private String rectification;
    /**
     * 事件类型
     */
    @Column(name = "rule_id")
    @SerializedName(value = "ruleId", alternate = "rule_id")
    private String ruleId;
    /**
     * 告警事件id
     */
    @Column(name = "event_id")
    @SerializedName(value = "eventId", alternate = "businessId")
    private String eventId;
    /**
     * 处置状态
     * 1 审核中
     * 2 审核完成
     * 3 被驳回
     */
    @Column(name = "deal_status")
    private Integer dealStatus;
    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;
    /**
     * 完成时间
     */
    @Column(name = "finish_time")
    private Date finishTime;


}
