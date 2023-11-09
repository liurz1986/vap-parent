package com.vrv.vap.common.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value = "Query", description = "通用列表查询模型")
public class Query {

    /**
     * 查询开始条数，默认为 0
     * */
    @ApiModelProperty(value="查询开始条数，默认为 0")
    private int start_ = 0;
    /**
     * 查询数量（每页数量），默认为10
     * */

    @ApiModelProperty(value="查询数量（每页数量），默认为10")
    private int count_ = 10;
    /**
     * 查询排序的字段，默认为 空 (不排序)
     * */
    @ApiModelProperty(value="查询排序的字段，默认为 空 (不排序)")
    private String order_ = null;

    /**
     * 查询排序的值：支持 "asc" "desc" 默认为空 （不排序）
     * */
    @ApiModelProperty(value="查询排序的值：支持 \"asc\" \"desc\" 默认为空 （不排序）")
    private String by_ = null;

    @ApiModelProperty(value="查询时根据该字段进行排序，合并order和by的值")
    private String orderByColumn;

    public String getOrderByColumn() {
        return orderByColumn;
    }

    public void setOrderByColumn(String orderByColumn) {
        this.orderByColumn = orderByColumn;
    }

    public int getStart_() {
        return start_;
    }

    public void setStart_(int start_) {
        this.start_ = start_;
    }

    public int getCount_() {
        return count_;
    }

    public void setCount_(int count_) {
        this.count_ = count_;
    }

    public String getOrder_() {
        return order_;
    }

    public void setOrder_(String order_) {
        this.order_ = order_;
    }

    public String getBy_() {
        return by_;
    }

    public void setBy_(String by_) {
        this.by_ = by_;
    }
}
