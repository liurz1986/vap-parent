package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class ListQuery extends Query {

    @ApiModelProperty("实体值")
    private String value;

    @ApiModelProperty("目标字段值")
    private String goalFieldValue;

    @ApiModelProperty("探索关系编号")
    private Integer edgeId;

    @ApiModelProperty("开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty("使用目标字段聚合")
    private boolean useFieldAggr;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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

    public Integer getEdgeId() {
        return edgeId;
    }

    public void setEdgeId(Integer edgeId) {
        this.edgeId = edgeId;
    }

    public String getGoalFieldValue() {
        return goalFieldValue;
    }

    public void setGoalFieldValue(String goalFieldValue) {
        this.goalFieldValue = goalFieldValue;
    }

    public boolean isUseFieldAggr() {
        return useFieldAggr;
    }

    public void setUseFieldAggr(boolean useFieldAggr) {
        this.useFieldAggr = useFieldAggr;
    }
}