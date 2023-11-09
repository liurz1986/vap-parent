package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.vo.Query;

import java.util.List;

public class SystemTopAreaQuery extends Query {
    public int id; // required
    public String name;
    public String recordTime;
    public long total; // required
    public String sysId; // required
    public String areaCode; // required
    public List<String> tableList;
    public List<String> rpts; // required

    public List<String> getRpts() {
        return rpts;
    }

    public void setRpts(List<String> rpts) {
        this.rpts = rpts;
    }

    public List<String> getTableList() {
        return tableList;
    }

    public void setTableList(List<String> tableList) {
        this.tableList = tableList;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }
}
