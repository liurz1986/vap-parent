package com.vrv.vap.netflow.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;
import java.util.Date;

/**
 * @author lilang
 * @date 2022/3/28
 * @description
 */
public class CollectorOfflineRecordQuery extends Query {

    @QueryLike
    private String name;

    private Integer type;

    @ApiModelProperty("开始时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "startTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @QueryMoreThan
    @Column(name="createTime")
    private Date startTime;

    @ApiModelProperty("结束时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "endTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="createTime")
    @QueryLessThan
    private Date endTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
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
