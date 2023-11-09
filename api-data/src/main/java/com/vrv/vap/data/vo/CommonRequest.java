package com.vrv.vap.data.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.data.constant.SYSTEM;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import java.util.Date;
import java.util.List;

@ApiModel(value = "通用查询生成参数")
public class CommonRequest {

    @ApiModelProperty("索引")
    private List<Integer> source;

    @ApiModelProperty("开始时间")
    @JsonFormat(pattern = SYSTEM.TIME_PATTERN, timezone = SYSTEM.TIME_ZONE)
    private Date startTime;

    @ApiModelProperty("结束时间")
    @JsonFormat(pattern = SYSTEM.TIME_PATTERN, timezone = SYSTEM.TIME_ZONE)
    private Date endTime;

    @ApiModelProperty("安全域")
    @Ignore
    private String domain;

    @ApiModelProperty("组件权限")
    @Ignore
    private String moduleAuth;

    @ApiModelProperty("分组间隔")
    @Ignore
    private String interval;

    @ApiModelProperty("是否聚合结果")
    @Ignore
    private boolean agg = false;

    @ApiModelProperty("是否专家模式")
    @Ignore
    private boolean professor = false;

    @ApiModelProperty("通用请求参数")
    private RequestParam param;


    public List<Integer> getSource() {
        return source;
    }

    public void setSource(List<Integer> source) {
        this.source = source;
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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getModuleAuth() {
        return moduleAuth;
    }

    public void setModuleAuth(String moduleAuth) {
        this.moduleAuth = moduleAuth;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public boolean isAgg() {
        return agg;
    }

    public void setAgg(boolean agg) {
        this.agg = agg;
    }

    public boolean isProfessor() {
        return professor;
    }

    public void setProfessor(boolean professor) {
        this.professor = professor;
    }

    public RequestParam getParam() {
        return param;
    }

    public void setParam(RequestParam param) {
        this.param = param;
    }
}
