package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Map;

@ApiModel("查询构造体")
public class QueryModel {

    @ApiModelProperty("排序字段")
    private String order;

    @ApiModelProperty("desc->降序排列,asc->升序排列")
    private String by;

    @ApiModelProperty(value = "开始位置", required = true)
    private int start;

    @ApiModelProperty(value = "返回多少条", required = true)
    private int count;

    @ApiModelProperty(value = "表名", required = true)
    private String table;

    private Map<String, Object> where;

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

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public Map<String, Object> getWhere() {
        return where;
    }

    public void setWhere(Map<String, Object> where) {
        this.where = where;
    }

}
