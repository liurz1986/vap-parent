package com.vrv.vap.alarmdeal.business.analysis.vo;

import lombok.Data;

import java.util.Date;

@Data
public class BusinessTaskLogVO {

    private String id;
    private String peopleName; //操作人名称
    private String peopleId; //操作人Guid
    private String action; //操作动作
    private String taskDefineKey;  // 任务节点定义的id
    private String taskDefindName; // 任务节点定义的名称
    private String advice; //说明备注
    private String processKey; //流程标识
    private String processInstanceId; //流程实例Id
    private String time;
    private String operation; //操作动作
    private Date deadlineDate; //逾期时间
    private Date finishDate; //归档时间
    private String params; //归档时间

    private String contextKey;

    private String contextId;

    private String contextLabel;
}
