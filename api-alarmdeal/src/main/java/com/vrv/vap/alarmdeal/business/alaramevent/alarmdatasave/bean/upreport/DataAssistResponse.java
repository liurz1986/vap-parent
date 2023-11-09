package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.google.gson.annotations.SerializedName;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfoDispose;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 协办反馈工单里面的内容
 */
@Data
public class DataAssistResponse extends AbstractUpData {
    /**
     * 协查编码
     */
    @SerializedName(value = "assis_id", alternate = "assistId")
    private String assis_id;
    /**
     * 协办申请单位
     */
    @SerializedName(value = "apply_unit", alternate = "applyUnit")
    private String apply_unit;
    /**
     * 请求协助目标单位名称
     */
    @SerializedName(value = "assist_unit", alternate = "assistUnit")
    private String assist_unit;
    /**
     * 协查事件简要描述
     */
    @SerializedName(value = "event_describe", alternate = "taskDesc")
    private String event_describe;
    /**
     * 反馈的结果
     */
    @SerializedName(value = "conlusion", alternate = "responseNote")
    private String conlusion;


    ////////////////////新标准中的新增加的字段20230831//////////////////////
    /**
     * 事件id
     */
    @SerializedName(value = "event_id", alternate = {"eventId"})
    private String event_id;
    /**
     * 事件名称 es
     */
    @SerializedName(value = "event_name", alternate = {"eventName"})
    private String event_name;
    /**
     * 事件类型 es
     */
    @SerializedName(value = "event_type", alternate = "eventType")
    private String event_type;
    /**
     * 告警详情 es
     */
    @SerializedName(value = "alert_detail", alternate = {"eventDetails"})
    private String alert_detail;
    /**
     * 核实过程
     */
    private String verify_process;
    /**
     * 初步成因分析
     */
    private String cause;
    /**
     * 初步成因类型 字符型，需要进行翻译
     */
    private String cause_type;

    /**
     * 处置过程
     */
    private String disposal_process;
    /**
     * 处置措施
     */
    private String disposal_measure;
    /**
     * 未知外部ip地址
     */
    private String extern_ip;
    /**
     * 未知外部地址来源
     */
    private String ip_source;
    /**
     * 未知外部地址联通范围  es todo 如何进行补全？
     */
    @SerializedName(value = "connect_range",alternate = "connectRange")
    private List<ConnectDevice> connect_range=new ArrayList<>();

    /**
     * 未知外部地址访问应用名称  es
     */
    private List<String> app_name=new ArrayList<>();
    /**
     * 未知外部地址使用的账号  es
     */
    private List<String> app_account=new ArrayList<>();
    /**
     * 协查原因
     */
    private String assis_cause;
    /**
     * 待核实
     */
    private String verify_content;
    /**
     * 核实处置方
     */
    private String recommend_disposal_measure;
    /**
     * 核实结果
     */
    private String verify_result;
    /**
     * 涉事人员信息  es
     */
    @SerializedName(value = "person_list", alternate = {"staffInfos"})
    private List<StaffInfoDispose> person_list=new ArrayList<>();

    /**
     * 事件成因
     */
    private String confirmed_cause;
    /**
     * 成因类型
     */
    private String confirmed_cause_type;
    /**
     *
     * 事件处置结果
     */
    private String disposal_result;

    /**
     * 申请单位联系人姓名
     */
    private String apply_contact_name;
    /**
     * 申请单位联系人所属部门
     */
    private String apply_contact_dept;
    /**
     * 申请单位联系人联系方式
     */
    private String apply_telephone;
    /**
     * 协查单位联系人姓名
     */
    private String assis_contact_name;
    /**
     * 协查单位联系人所属部门
     */
    private String assis_contact_dept;
    /**
     * 协查单位联系人联系方式
     */
    private String assis_telephone;
}
