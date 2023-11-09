package com.vrv.vap.alarmdeal.business.flow.core.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.flow.core.model.BusinessInstanceStatEnum;
import lombok.Data;

import java.util.Date;
import java.util.Set;

@Data
public class BusinessIntanceVO {
    private String guid;
    private String code;   // 工单编号
    private String name;   // 工单名称
    private String processInstanceId;  // 流程实例id
    private String processDefGuid; //流程定义guid
    private String processDefName; //流程定义名称
    private String createUserName;//流程实例创建人名称
    private String createUserId;//流程实例创建人guid
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    private BusinessInstanceStatEnum statEnum;
    private String busiArgs;    // 表单的json字符串
    private String dealPeoples;   // 经手人，每次经手都直接附加，逗号分隔
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date finishDate; //归档时间
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private  Date deadlineDate; //逾期时间
    private  String candidatePerson;  //处理人
    private String contextId;
    private String contextKey;
    private Set<String> contextIdArr; //contextId的数据类型
}
