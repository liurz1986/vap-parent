package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lilang
 * @date 2021/3/26
 * @description
 */
@Table(name = "cascade_strategy_receive")
public class CascadeStrategyReceive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("父唯一ID")
    private String puid;

    @ApiModelProperty("日志大类别")
    private String type;

    @ApiModelProperty("产品编号")
    private Integer number;

    @ApiModelProperty("日志小类别")
    @Column(name = "op_code")
    private Integer opCode;

    @ApiModelProperty("行为类别")
    private Integer kind;

    @ApiModelProperty("风险级别")
    private Integer level;

    @ApiModelProperty("数据起始时间")
    @Column(name = "start_time")
    private String startTime;

    @ApiModelProperty("数据结束时间")
    @Column(name = "end_time")
    private String endTime;

    @ApiModelProperty("执行状态")
    @Column(name = "execute_status")
    private Integer executeStatus;

    @ApiModelProperty("上级kafka连接信息")
    private String kafka;

    @ApiModelProperty("时间标记")
    private String timeFlag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPuid() {
        return puid;
    }

    public void setPuid(String puid) {
        this.puid = puid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Integer getOpCode() {
        return opCode;
    }

    public void setOpCode(Integer opCode) {
        this.opCode = opCode;
    }

    public Integer getKind() {
        return kind;
    }

    public void setKind(Integer kind) {
        this.kind = kind;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getExecuteStatus() {
        return executeStatus;
    }

    public void setExecuteStatus(Integer executeStatus) {
        this.executeStatus = executeStatus;
    }

    public String getKafka() {
        return kafka;
    }

    public void setKafka(String kafka) {
        this.kafka = kafka;
    }

    public String getTimeFlag() {
        return timeFlag;
    }

    public void setTimeFlag(String timeFlag) {
        this.timeFlag = timeFlag;
    }
}
