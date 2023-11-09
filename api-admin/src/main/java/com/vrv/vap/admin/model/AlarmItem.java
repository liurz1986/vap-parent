package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

@Table(name = "alarm_item_collection")
public class AlarmItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ApiModelProperty("事件类别")
    @Column(name = "alarm_type")
    private String alarmType;
    @ApiModelProperty("告警级别")
    // 告警级别 1：低 2：中 3：高
    @Column(name = "alarm_level")
    private Integer alarmLevel;
    @ApiModelProperty("告警来源")
    @Column(name = "alarm_source")
    private String alarmSource;
    @ApiModelProperty("告警描述")
    @Column(name = "alarm_desc")
    private String alarmDesc;

    @Column(name = "origin_data")
    private String originData;
    @ApiModelProperty("告警状态")
    // 告警状态 0：未处理 1：已处理 2：已忽略
    @Column(name = "alarm_status")
    private Integer alarmStatus;
    @ApiModelProperty("告警时间")
    @Column(name = "alarm_time")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date alarmTime;
    @ApiModelProperty("更新时间")
    @Column(name = "update_time")
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAlarmType() {
        return alarmType;
    }

    public void setAlarmType(String alarmType) {
        this.alarmType = alarmType;
    }

    public Integer getAlarmLevel() {
        return alarmLevel;
    }

    public void setAlarmLevel(Integer alarmLevel) {
        this.alarmLevel = alarmLevel;
    }

    public String getAlarmSource() {
        return alarmSource;
    }

    public void setAlarmSource(String alarmSource) {
        this.alarmSource = alarmSource;
    }

    public String getAlarmDesc() {
        return alarmDesc;
    }

    public void setAlarmDesc(String alarmDesc) {
        this.alarmDesc = alarmDesc;
    }

    public Integer getAlarmStatus() {
        return alarmStatus;
    }

    public void setAlarmStatus(Integer alarmStatus) {
        this.alarmStatus = alarmStatus;
    }

    public String getOriginData() {
        return originData;
    }

    public void setOriginData(String originData) {
        this.originData = originData;
    }

    public Date getAlarmTime() {
        return alarmTime;
    }

    public void setAlarmTime(Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "AlarmItem{" +
                "id=" + id +
                ", alarmType='" + alarmType + '\'' +
                ", alarmLevel=" + alarmLevel +
                ", alarmSource='" + alarmSource + '\'' +
                ", alarmDesc='" + alarmDesc + '\'' +
                ", alarmStatus=" + alarmStatus +
                ", alarmTime=" + alarmTime +
                '}';
    }
}