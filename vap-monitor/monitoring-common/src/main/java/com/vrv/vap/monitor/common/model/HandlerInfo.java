package com.vrv.vap.monitor.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class HandlerInfo {
    /**
     * 告警机器IP
     */
    private String ip;
    /**
     * 告警组件名称
     */
    private String monitorName;
    /**
     * 告警时间
     */
    private Date time;
    /**
     * 类型
     */
    private String type;
    /**
     * 级别 1：低 2：中 3：高
     */
    private Integer level;
    /**
     * 状态
     */
    private Integer status;
    /**
     * 描述
     */
    private String desc;
    /**
     * 其他传递内容
     */
    private String extendContent;
}
