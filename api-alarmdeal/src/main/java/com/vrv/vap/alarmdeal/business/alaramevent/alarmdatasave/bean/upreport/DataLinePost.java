package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;

import com.google.gson.annotations.SerializedName;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.DeviceInoSupervise;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfoDispose;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 线所部分data
 */
@Data
public class DataLinePost extends AbstractUpData {
    /**
     * 事件id
     */
    @SerializedName(value = "event_id", alternate = {"eventId"})
    private String event_id;
    /**
     * 事件名称
     */
    @SerializedName(value = "event_name", alternate = {"eventName"})
    private String event_name;
    /**
     * 事件总类
     */
    @SerializedName(value = "event_kind", alternate = "eventKind")
    private String event_kind;
    /**
     * 事件类型
     */
    @SerializedName(value = "event_type", alternate = "eventType")
    private String event_type;
    /**
     * 事件创建时间
     */
    @SerializedName(value = "event_occurtime", alternate = {"eventCreattime"})
    private String event_occurtime;
    /**
     * 责任人员数量
     */
    @SerializedName(value = "person_count", alternate = {"staffNum"})
    private Integer person_count;
    /**
     * 责任人员列表
     */
    @SerializedName(value = "person_list", alternate = {"staffInfos"})
    private List<StaffInfoDispose> person_list = new ArrayList<>();
    /**
     * 处置人员姓名  处置人姓名--》通过session来获取
     */
    private String disposal_person_name;
    /**
     * 处置人员所属部门
     */
    private String disposal_department_name;
    /**
     * 处置人角色   字符类型
     */
    private String disposal_person_role;
    /**
     * 处置时间  当前下发时间
     */
    private String disposal_time;
    /**
     * 处置过程 需要详细描述处置过程信息，包括核实情况，整改措施实施，该字段为必填，*年*月*日 保密部分向信息部门发起了事件合适任务。）
     */
    private String disposal_process;
    /**
     * 事件过程
     */
    private String event_inquriy;
    /**
     * 成因分析 来自表单
     */
    private String cause;
    /**
     * 事件详情 字段名称改了一下
     */
    @SerializedName(value = "alert_detail", alternate = {"eventDetails"})
    private String alert_detail;
    /**
     * 技术整改措施  来自表单
     */
    private String rectification;
    /**
     * 残留风险情况 新的
     */
    private String risidual_risk;
    /**
     * 涉事部门
     */
    private String event_dept;
    /**
     * 涉事单位
     */
    private String event_unit;
    /**
     * 涉事设备
     */
    @SerializedName(value = "device_list", alternate = {"deviceInfos"})
    private List<DeviceInoSupervise> device_list = new ArrayList<>();
    /**
     * 失泄密评估 来自表单
     */
    private Integer result_details;
    /**
     * 成因类型  todo   来自表单，是通过枚举进行的下拉选择。暂时先补充进去，等后续表单补充进来后，再进行完善。
     */
    private String cause_type;
    /**
     * 案件依据 todo 来自表单，手动填写，后续表单补充进来后再进行完善
     */
    private String case_basis;
}
