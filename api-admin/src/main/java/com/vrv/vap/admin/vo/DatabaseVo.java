package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;

public class DatabaseVo {

    @ApiModelProperty("搜索语句")
    private String queryJsonStr;

    @ApiModelProperty("链接ID")
    private Integer id;

    @ApiModelProperty("查询排序的字段，默认为 空 (不排序)")
    private String order_ = null;
    @ApiModelProperty("查询排序的值：支持 \"asc\" \"desc\" 默认为空 （不排序）")
    private String by_ = null;

    public String getQueryJsonStr() {
        return queryJsonStr;
    }

    public void setQueryJsonStr(String queryJsonStr) {
        this.queryJsonStr = queryJsonStr;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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
