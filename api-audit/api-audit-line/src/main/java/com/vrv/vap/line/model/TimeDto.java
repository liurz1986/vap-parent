package com.vrv.vap.line.model;

public class TimeDto {
    private String userKey;
    private long size;
    private long queryTime;
    private long splitTime;
    private long analysisTime;
    private long filterTime;
    private long saveTime;
    private String type;

    public long getSplitTime() {
        return splitTime;
    }

    public void setSplitTime(long splitTime) {
        this.splitTime = splitTime;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public long getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(long queryTime) {
        this.queryTime = queryTime;
    }

    public long getAnalysisTime() {
        return analysisTime;
    }

    public void setAnalysisTime(long analysisTime) {
        this.analysisTime = analysisTime;
    }

    public long getFilterTime() {
        return filterTime;
    }

    public void setFilterTime(long filterTime) {
        this.filterTime = filterTime;
    }

    public long getSaveTime() {
        return saveTime;
    }

    public void setSaveTime(long saveTime) {
        this.saveTime = saveTime;
    }
}
