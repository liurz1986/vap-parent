package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

/**
 *  探索结果对象
 */
public class ResultModel {

    @ApiModelProperty("关键内容")
    private String content;

    @ApiModelProperty("数据json")
    private String dataJson;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDataJson() {
        return dataJson;
    }

    public void setDataJson(String dataJson) {
        this.dataJson = dataJson;
    }
}