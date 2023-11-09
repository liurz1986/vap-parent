package com.vrv.vap.data.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;


@ApiModel(value = "通用（搜索）请求参数")
public class ElasticParam {

    @ApiModelProperty("索引")
    private String[] index;

    @ApiModelProperty("搜索语句")
    private String query;

    @ApiModelProperty("模块")
    @Ignore
    private String model;

    public String[] getIndex() {
        return index;
    }

    public void setIndex(String[] index) {
        this.index = index;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }
}
