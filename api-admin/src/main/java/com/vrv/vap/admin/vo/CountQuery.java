package com.vrv.vap.admin.vo;

/**
 * @author lilang
 * @date 2020/9/22
 * @description 查询总条数、总数量
 */
public class CountQuery {

    private String fieldName;

    private String startTime;

    private String endTime;

    private String indexName;

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
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

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }
}
