package com.vrv.vap.line.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * spark离线定时任务
 *
 * @author xw
 * @date 2018年11月9日
 */
@ApiModel("spark离线定时任务")
public class TaskOfflineModel {
    @ApiModelProperty("主键id")
    private int id;

    @ApiModelProperty("任务名称")
    private String name;

    @ApiModelProperty("周期cron表达式")
    private String cronTime;

    @ApiModelProperty("执行任务类")
    private String app;

    @ApiModelProperty("是否开启:0关闭1开启")
    private String shouldRun;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("是否写入taskId")
    private int needTaskId;

    @ApiModelProperty("任务详细参数")
    private String taskDetail;

    @ApiModelProperty("任务周期:month,day或者year")
    private String cronType;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCronTime() {
        return cronTime;
    }

    public void setCronTime(String cronTime) {
        this.cronTime = cronTime;
    }

    public String getApp() {
        return app;
    }

    public void setApp(String app) {
        this.app = app;
    }

    public String getShouldRun() {
        return shouldRun;
    }

    public void setShouldRun(String shouldRun) {
        this.shouldRun = shouldRun;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getNeedTaskId() {
        return needTaskId;
    }

    public void setNeedTaskId(int needTaskId) {
        this.needTaskId = needTaskId;
    }

    public String getTaskDetail() {
        return taskDetail;
    }

    public void setTaskDetail(String taskDetail) {
        this.taskDetail = taskDetail;
    }

    public String getCronType() {
        return cronType;
    }

    public void setCronType(String cronType) {
        this.cronType = cronType;
    }
}
