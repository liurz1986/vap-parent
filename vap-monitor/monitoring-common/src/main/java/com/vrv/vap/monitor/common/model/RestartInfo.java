package com.vrv.vap.monitor.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class RestartInfo {
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
     * 类型 0 ,内部错误，未执行重启 1、正在重启 2、执行重启命令失败  3、重启成功
     */
    private Integer status;

    /**
     * 消息
     */
    private String msg;


}
