package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class TrendQuery {

    @ApiModelProperty("搜索字段的值")
    private String searchValue;

    @ApiModelProperty("目标字段的值")
    private String goalValue;

    @ApiModelProperty("探索关系编号")
    private Integer edgeId;

    @ApiModelProperty("开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    public String getSearchValue() {
        return searchValue;
    }

    public void setSearchValue(String searchValue) {
        this.searchValue = searchValue;
    }

    public String getGoalValue() {
        return goalValue;
    }

    public void setGoalValue(String goalValue) {
        this.goalValue = goalValue;
    }

    public Integer getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Integer edgeId) {
        this.edgeId = edgeId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}