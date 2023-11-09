package com.vrv.vap.line.model;

import com.alibaba.fastjson.annotation.JSONField;

import java.util.Date;
import java.util.UUID;

public class BaseLinePage {
    private int id;
    private int size;
    @JSONField(name = "time_total")
    private int timeTotal;
    @JSONField(name = "invalid_num")
    private int invalidNum;
    @JSONField(name = "resource_num")
    private int resourceNum;
    @JSONField(name = "user_ip")
    private String userIp;
    private String type;
    @JSONField(name = "sys_id")
    private String sysId;
    private float frequency;
    private float inefficiency;
    private float purity;
    @JSONField(name = "date_time")
    private String dateTime;
    @JSONField(name = "insert_time")
    private Date insertTime;
    private String guid;

    public BaseLinePage() {
        this.guid = UUID.randomUUID().toString().replaceAll("-", "");
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getTimeTotal() {
        return timeTotal;
    }

    public void setTimeTotal(int timeTotal) {
        this.timeTotal = timeTotal;
    }

    public int getInvalidNum() {
        return invalidNum;
    }

    public void setInvalidNum(int invalidNum) {
        this.invalidNum = invalidNum;
    }

    public int getResourceNum() {
        return resourceNum;
    }

    public void setResourceNum(int resourceNum) {
        this.resourceNum = resourceNum;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency(float frequency) {
        this.frequency = frequency;
    }

    public float getInefficiency() {
        return inefficiency;
    }

    public void setInefficiency(float inefficiency) {
        this.inefficiency = inefficiency;
    }

    public float getPurity() {
        return purity;
    }

    public void setPurity(float purity) {
        this.purity = purity;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
