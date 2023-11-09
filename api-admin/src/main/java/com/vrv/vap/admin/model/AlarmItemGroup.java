package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

public class AlarmItemGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "alarm_type")
    private String alarmType;

    // 告警级别 1：低 2：中 3：高
    @Column(name = "alarm_level")
    private Integer alarmLevel;

    @Column(name = "alarm_source")
    private String alarmSource;

    @Column(name = "alarm_desc")
    private String alarmDesc;

    // 告警状态 0：未处理 1：已处理 2：已忽略
    @Transient
    private Integer alarmStatus;

    @Transient
    private Integer alarmCount;

    @Transient
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date firstTime;

    @Transient
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date lastTime;

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

    public Integer getAlarmCount() {
        return alarmCount;
    }

    public void setAlarmCount(Integer alarmCount) {
        this.alarmCount = alarmCount;
    }

    public Date getFirstTime() {
        return firstTime;
    }

    public void setFirstTime(Date firstTime) {
        this.firstTime = firstTime;
    }

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}