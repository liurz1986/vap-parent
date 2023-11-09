package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.xc.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value="QueryDuration", description="客户端轮询时长表")
public class QueryDuration extends BaseModel {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "设备id")
    private String devId;
    @ApiModelProperty(value = "设备ip")
    private String devIp;

    public String getDevIp() {
        return devIp;
    }

    public void setDevIp(String devIp) {
        this.devIp = devIp;
    }

    @ApiModelProperty(value = "时长（分钟）")
    private Long duration;

    @ApiModelProperty(value = "查询时间")
    private Date queryTime;

    @ApiModelProperty(value = "入库时间")
    private Date insertTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDevId() {
        return devId;
    }

    public void setDevId(String devId) {
        this.devId = devId;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Date getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Date queryTime) {
        this.queryTime = queryTime;
    }

    public Date getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(Date insertTime) {
        this.insertTime = insertTime;
    }
}
