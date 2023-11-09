package com.vrv.vap.monitor.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class LogSendInfo {
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
     * 类型 1、上传 2、正在打包  3、完成
     */
    private String type;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 日志路径
     */
    private String filePath;

    /**
     * 最终日志名称
     */
    private String logFileName;

    /**
     * 日志下载目录
     */
    private String tempDir;
}
