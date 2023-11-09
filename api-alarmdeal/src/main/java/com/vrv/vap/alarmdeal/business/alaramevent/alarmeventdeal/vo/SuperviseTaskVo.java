package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import lombok.Data;

/**
 * @author lps 2021/8/4
 */

@Data
public class SuperviseTaskVo {


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
     * 任务下发/上报时间
     */
    private String sendTime;

    /**
     * 描述
     */
    private String noticeDesc;

    /**
     * 响应附件
     */
    private String responseAttachment;
    /**
     * 申请附件
     */
    private String applyAttachment;

    /**
     * 处理状态
     */
    private String dealStatus;


    /**
     * 响应信息
     */
    private String responseNote;


    private String taskCreate;
    /**
     * 申请单位
     */
    private String applyUnit;
    /**
     * 协办单位 新增
     */
    private String assistUnit;

    /**
     * 协办说明  预警中的任务描述都用这个字段(新增)
     */
    private String taskDesc;
    /**
     * 事件id
     */
    private String eventId;


}
