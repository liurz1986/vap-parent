package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.upreport;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.gson.annotations.SerializedName;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.Attachment;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.DeviceDispose;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.FileInfoDispose;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean.StaffInfoDispose;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件日志data
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataDispose extends AbstractUpData {
    /**
     * 处置部门名称
     */
    private String disposal_department_name;

    @SerializedName(value = "event_name", alternate = "eventName")
    private String event_name;
    /**
     * 事件id
     */
    @SerializedName(value = "event_id", alternate = {"eventId"})
    private String event_id;
    /**
     * 事件创建时间
     */
    @SerializedName(value = "event_creattime", alternate = {"eventCreattime"})
    private String event_creattime;
    /**
     * 处置人员姓名  处置人姓名--》通过session来获取
     */
    private String disposal_person_name;
    /**
     * 处置人角色   ---->通过session来获取 字符串类型
     */
    private String disposal_person_role;
    /**
     * 处置时间  当前下发时间
     */
    private String disposal_time;
    /**
     * 是否误报   来自表单内容
     */
    private Integer is_misreport;
    /**
     * 处置状态  根据5种发送状态来 字符类型
     */
    private String disposal_status;
    /**
     * 单位保密办督办
     */
    @SerializedName(value = "is_supervise", alternate = {"isUrge"})
    private Integer is_supervise;
    /**
     * 单位保密办督办原因描述 这个就是督促
     */
    private String supervise_descripiton;
    /**
     * 上级单位督办
     */

    @SerializedName(value = "issued_supervise", alternate = {"isSupervise"})
    private Integer issued_supervise;
    /**
     * 上级单位督办原因描述
     */

    @SerializedName(value = "issued_supervise_description", alternate = {"noticeDesc"})
    private String issued_supervise_description;
    /**
     * 处置版本 待定
     */
    private String disposal_version = "1.0";
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
     * 设备数量
     */
    @SerializedName(value = "associated_device_count", alternate = {"deviceCount"})
    private Integer associated_device_count;
    /**
     * 关联设备列表
     */
    @SerializedName(value = "associated_device", alternate = {"deviceInfos"})
    private List<DeviceDispose> associated_device = new ArrayList<>();
    /**
     * 涉密文件数量
     */
    @SerializedName(value = "file_mm_count", alternate = {"fileCount"})
    private String file_mm_count;
    /**
     * 涉密文件列表
     */
    private List<FileInfoDispose> file_mm_list;
    /**
     * 成因分析 来自表单
     */
    private String cause = "";
    /**
     * 失泄密评估 来自表单
     */
    private Integer result_evaluation;
    /**
     * 事件详情
     */
    @SerializedName(value = "result_details", alternate = {"eventDetails"})
    private String result_details;
    /**
     * 技术整改措施  来自表单 ,表单给个默认值
     */
    private String rectification = "";
    /**
     * 附件  来自表单
     */
    private List<Attachment> attachment = new ArrayList<>();
    /**
     * 事件过程
     */
    private String event_inquriy = "";
}
