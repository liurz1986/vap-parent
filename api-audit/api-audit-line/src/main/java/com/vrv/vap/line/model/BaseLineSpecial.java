package com.vrv.vap.line.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;

import java.util.Date;

@ApiModel(value="BaseLineSpecial对象", description="基线特殊模型表")
public class BaseLineSpecial {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    private String name;
    private String mainClass;
    private String actualClass;
    private String mainCron;
    private String actualCron;
    private String monitorId;
    private String config;
    private String type;
    private String jarName;
    private String params;
    private String frame;
    private Date time;

    public String getFrame() {
        return frame;
    }

    public void setFrame(String frame) {
        this.frame = frame;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getActualCron() {
        return actualCron;
    }

    public void setActualCron(String actualCron) {
        this.actualCron = actualCron;
    }

    public String getJarName() {
        return jarName;
    }

    public void setJarName(String jarName) {
        this.jarName = jarName;
    }

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

    public String getMainClass() {
        return mainClass;
    }

    public void setMainClass(String mainClass) {
        this.mainClass = mainClass;
    }

    public String getActualClass() {
        return actualClass;
    }

    public void setActualClass(String actualClass) {
        this.actualClass = actualClass;
    }

    public String getMainCron() {
        return mainCron;
    }

    public void setMainCron(String mainCron) {
        this.mainCron = mainCron;
    }

    public String getMonitorId() {
        return monitorId;
    }

    public void setMonitorId(String monitorId) {
        this.monitorId = monitorId;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
