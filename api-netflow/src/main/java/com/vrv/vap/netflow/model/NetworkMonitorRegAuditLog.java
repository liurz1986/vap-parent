package com.vrv.vap.netflow.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

//注册审计日志
@Table(name = "network_monitor_reg_audit_log")
@Data
public class NetworkMonitorRegAuditLog {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 设备ID
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     * 所审计的上报记录的id
     * 只审计最后一次的记录
     */
    @Column(name = "reg_id")
    private Integer regId;


    /**
     * 操作时间
     */
    @Column(name = "audit_time")
    private Date auditTime;

    /**
     * 审计结果 1 通过 0 不通过
     */
    @Column(name = "audit_result")
    private Integer auditResult;

    /**
     * 审计结果说明
     */
    @Column(name = "memo")
    private String memo;

    /**
     * 审计账户id
     */
    @Column(name = "audit_account")
    private String auditAccount;

    /**
     * 审计账户名称
     */
    @Column(name = "audit_account_name")
    private String auditAccountName;


}