package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.admin.common.ConvertField;

import java.util.Date;

/**
 * @author lilang
 * @date 2020/11/23
 * @description
 */
public class HeatModel {

    /**
     * 索引
     */
    @ConvertField
    private String indexName;

    /**
     * es时间字段
     *
     * @return
     */
    @ConvertField
    private String timeField = "@timestamp";

    /**
     * 开始时间
     */
    @ConvertField
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;
    /**
     * 结束时间
     */
    @ConvertField
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    private String dateField;

    private String termField;

    public String getDateField() {
        return dateField;
    }

    public void setDateField(String dateField) {
        this.dateField = dateField;
    }

    public String getTermField() {
        return termField;
    }

    public void setTermField(String termField) {
        this.termField = termField;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }
}
