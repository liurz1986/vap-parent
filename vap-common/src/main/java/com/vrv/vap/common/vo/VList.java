package com.vrv.vap.common.vo;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;

@ApiModel(value = "VList", description = "Api 响应体：响应分页列表，用于分页")
public class VList<T> extends Result{

    @ApiModelProperty(value="当前页的数据列表")
    private List<T> list;

    @ApiModelProperty(value="条件可查询到的数据总条数")
    private int total;


    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

}
