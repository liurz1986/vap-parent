package com.vrv.vap.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.LinkedHashSet;

@ApiModel(value = "通用查询生成响应")
public class CommonResponse {

    @ApiModelProperty("索引片段")
    private LinkedHashSet<String> segment;

    @ApiModelProperty("查询字符串")
    private String query;

    @ApiModelProperty("聚合字符串（仅在 agg:true 时返回）")
    private String aggs;

    private int total = 0;

    private int totalAcc = 0;

    public LinkedHashSet<String> getSegment() {
        return segment;
    }

    public void setSegment(LinkedHashSet<String> segment) {
        this.segment = segment;
    }


    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }


    public String getAggs() {
        return aggs;
    }

    public void setAggs(String aggs) {
        this.aggs = aggs;
    }


    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalAcc() {
        return totalAcc;
    }

    public void setTotalAcc(int totalAcc) {
        this.totalAcc = totalAcc;
    }
}
