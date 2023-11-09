package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;

public class EsSearchQuery {

    @ApiModelProperty("索引")
    private String index;

    @ApiModelProperty("时间字段名称")
    private String timeFieldName;

    @ApiModelProperty("开始时间")
    private String startTime;

    @ApiModelProperty("结束时间")
    private String endTime;

    @ApiModelProperty("搜索字段")
    private String selectFields;

    @ApiModelProperty("搜索字段")
    private String  fieldName;


    @ApiModelProperty("搜索语句")
    private String queryJsonStr;

    @ApiModelProperty("列表长度")
    private  Integer count;

    @ApiModelProperty("模式")
    private String mode;

    @ApiModelProperty("主题ID")
    private Integer topicId;

    public String getIndex() {
        return index;
    }

    public void setIndex(String index) {
        this.index = index;
    }

    public String getTimeFieldName() {
        return timeFieldName;
    }

    public void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getSelectFields() {
        return selectFields;
    }

    public void setSelectFields(String selectFields) {
        this.selectFields = selectFields;
    }

    public String getQueryJsonStr() {
        return queryJsonStr;
    }

    public void setQueryJsonStr(String queryJsonStr) {
        this.queryJsonStr = queryJsonStr;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }
}
