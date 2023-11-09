package com.vrv.vap.monitor.common.model;

import lombok.Data;

import java.util.Date;

@Data
public class ServerStateInfo {
    /**
     * IP
     */
    private String serverIp;

    /**
     * 状态是否良好
     */
    private Boolean status;

    /**
     * 状态更新时间
     */
    private Date time;
}
