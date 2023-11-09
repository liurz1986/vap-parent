package com.vrv.vap.admin.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class StatisticsQuery {

    @ApiModelProperty("实体编号")
    private Integer entityId;

    @ApiModelProperty("实体值")
    private String value;

    @ApiModelProperty("开始时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    @ApiModelProperty("使用目标字段聚合")
    private boolean useFieldAggr;

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

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

    public boolean isUseFieldAggr() {
        return useFieldAggr;
    }

    public void setUseFieldAggr(boolean useFieldAggr) {
        this.useFieldAggr = useFieldAggr;
    }
}