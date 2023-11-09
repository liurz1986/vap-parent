package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lilang
 * @date 2022/4/21
 * @description 基础数据同步
 */
@Table(name = "sync_base_data")
public class SyncBaseData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("协议类型")
    @Column(name = "protocol_type")
    private String protocolType;

    @ApiModelProperty("ip地址")
    private String ip;

    @ApiModelProperty("端口")
    private String port;

    @ApiModelProperty("执行周期")
    private String cron;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("备注")
    private String description;

    /**
     * asset资产，person人员，app应用
     */
    @ApiModelProperty("任务类型")
    private String type;

    /**
     * ：公司-产品名称
     */
    @ApiModelProperty("数据来源")
    private String source;

    /**
     * ：0启用，1停止
     */
    @ApiModelProperty("状态")
    private Integer status;

    @ApiModelProperty("开始时间")
    @Column(name = "start_time")
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date startTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
}
