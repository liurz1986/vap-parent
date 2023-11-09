package com.vrv.vap.line.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lil on 2018/4/9.
 */
@ApiModel("定时任务信息")
public class TaskModel {
    @ApiModelProperty("主键id")
    private int id;

    @ApiModelProperty("任务名称")
    private String taskName;

    @ApiModelProperty("周期cron表达式")
    private String cronTime;

    @ApiModelProperty("执行任务类")
    private String classpath;

    @ApiModelProperty("是否开启:0关闭1开启")
    private String shouldRun;

    @ApiModelProperty("描述")
    private String description;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getCronTime() {
        return cronTime;
    }

    public void setCronTime(String cronTime) {
        this.cronTime = cronTime;
    }

    public String getClasspath() {
        return classpath;
    }

    public void setClasspath(String classpath) {
        this.classpath = classpath;
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

    @Override
    public String toString() {
        return "TaskModel [id=" + id + ", taskName=" + taskName + ", cronTime=" + cronTime + ", classpath=" + classpath
                + ", shouldRun=" + shouldRun + ", description=" + description + "]";
    }

}
