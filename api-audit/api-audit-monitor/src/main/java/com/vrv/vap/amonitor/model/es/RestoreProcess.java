package com.vrv.vap.amonitor.model.es;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * es快照还原的运行状态
 */
public class RestoreProcess implements Serializable {

    private String restoreId;
    /**
     * 运行结果:1成功,0还原中,-1失败
     */
    private int status;

    /**
     * 还原开始时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date start;

    /**
     * 还原结束时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date end;

    public RestoreProcess(String restoreId, int status, Date start, Date end) {
        this.restoreId = restoreId;
        this.status = status;
        this.start = start;
        this.end = end;
    }

    public RestoreProcess(String restoreId, int status, Date start) {
        this.restoreId = restoreId;
        this.status = status;
        this.start = start;
    }

    public RestoreProcess(String restoreId, int status) {
        this.restoreId = restoreId;
        this.status = status;
    }

    public RestoreProcess() {
    }

    public String getRestoreId() {
        return restoreId;
    }

    public void setRestoreId(String restoreId) {
        this.restoreId = restoreId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }
}
