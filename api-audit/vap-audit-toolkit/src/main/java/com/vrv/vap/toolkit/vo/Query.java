package com.vrv.vap.toolkit.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vrv.vap.toolkit.annotations.Effective;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

@ApiModel(value = "Query", description = "通用列表查询模型")
public class Query {

    /**
     * 查询开始条数，默认为 0
     * */
    @ApiModelProperty(value="查询开始条数，默认为 0")
    @JsonProperty(value = "start_", access = JsonProperty.Access.WRITE_ONLY)
    private int myStart = 0;
    /**
     * 查询数量（每页数量），默认为10
     * */

    @ApiModelProperty(value="查询数量（每页数量），默认为10")
    @JsonProperty(value = "count_", access = JsonProperty.Access.WRITE_ONLY)
    private int myCount = 10;
    /**
     * 查询排序的字段，默认为 空 (不排序)
     * */
    @ApiModelProperty(value="查询排序的字段，默认为 空 (不排序)")
    @JsonProperty(value = "order_", access = JsonProperty.Access.WRITE_ONLY)
    private String order = null;
    /**
     * 查询排序的值：支持 "asc" "desc" 默认为空 （不排序）
     * */
    @ApiModelProperty(value="查询排序的值：支持 \"asc\" \"desc\" 默认为空 （不排序）")
    @Effective({"desc", "asc"})
    @JsonProperty(value = "by_", access = JsonProperty.Access.WRITE_ONLY)
    private String by = null;

    @ApiModelProperty("开始时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "start_time", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date myStartTime;

    @ApiModelProperty("结束时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "end_time", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date myEndTime;

    public int getMyStart() {
        return myStart;
    }

    public void setMyStart(int myStart) {
        this.myStart = myStart;
    }

    public int getMyCount() {
        return myCount;
    }

    public void setMyCount(int myCount) {
        this.myCount = myCount;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public String getBy() {
        return by;
    }

    public void setBy(String by) {
        this.by = by;
    }

    public Date getMyStartTime() {
        return myStartTime;
    }

    public void setMyStartTime(Date myStartTime) {
        this.myStartTime = myStartTime;
    }

    public Date getMyEndTime() {
        return myEndTime;
    }

    public void setMyEndTime(Date myEndTime) {
        this.myEndTime = myEndTime;
    }

    public int getCurrentPage() {
        if (myCount == 0) {
            myCount = 10;
        }
        return myStart / myCount + 1;
    }
}
