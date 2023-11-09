package com.vrv.vap.xc.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class ReportParam {

    @ApiModelProperty("开始时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "startTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date startTime;

    @ApiModelProperty("结束时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "endTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(exist = false)
    private Date endTime;

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
