package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("supervise_task")
public class SuperviseTask implements Serializable {

    /**
     * 主键id
     */
    private String guid;


    /**
     * 名称
     */
    private String noticeName;

    /**
     * 类型
     */
    private String noticeType;


    /**
     * 任务创建时间
     */
    private String createTime;
    /**
     * 发送时间
     */
    private String sendTime;
    /**
     * 协查编码
     */
    private String assistId;


    /**
     * 描述(预警描述、协办说明)
     */
    private String noticeDesc;


    /**
     * 处理状态
     */
    private String dealStatus;

    /**
     * 事件id
     * 字段命名由上级确定
     */
    private String eventId;

    /**
     * 响应信息、反馈结果
     */
    private String responseNote;


    /**
     * 任务产生途径
     * up--上报，down下发
     */
    private String taskCreate;

    /**
     * 响应时间
     */
    private String responseTime;

    /**
     * 申请单位 新增
     */
    private String applyUnit;
    /**
     * 协办单位 新增
     */
    private String assistUnit;

    /**
     * 预警任务描述、事件简要描述
     */
    @SerializedName(value = "taskDesc", alternate = "eventDescription")
    private String taskDesc;
    /**
     * 申请附件
     */
    private String applyAttachment;
    /**
     * 反馈附件
     */
    private String responseAttachment;

    private String noticeId;
    /**
     * 协查单位目前对事件采取的措施  工单数据
     */
    @SerializedName(value = "disposalDescribe", alternate = "disposal_describe")
    private String disposalDescribe;
    /**
     * 预警id
     */
    private String warnningId;
    /**
     * 表单中的业务数据
     */
    private String busiArgs;

}
