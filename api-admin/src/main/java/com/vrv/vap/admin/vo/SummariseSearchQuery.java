package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2020/7/21
 * @description
 */
public class SummariseSearchQuery {

    @ApiModelProperty("索引Id")
    private String indexId;

    @ApiModelProperty("时间字段")
    private String timeFieldName;

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getTimeFieldName() {
        return timeFieldName;
    }

    public void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }
}
