package com.vrv.vap.admin.model;

import java.util.Date;
import javax.persistence.*;

@Table(name = "supervise_status_submit")
public class SuperviseStatusSubmit {
    @Id
    private String guid;

    @Column(name = "update_time")
    private Date updateTime;

    @Column(name = "run_state")
    private Integer runState;

    @Column(name = "submit_time")
    private Date submitTime;

    @Column(name = "submit_status")
    private Integer submitStatus;

    /**
     * @return guid
     */
    public String getGuid() {
        return guid;
    }

    /**
     * @param guid
     */
    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * @return update_time
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * @param updateTime
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    /**
     * @return run_state
     */
    public Integer getRunState() {
        return runState;
    }

    /**
     * @param runState
     */
    public void setRunState(Integer runState) {
        this.runState = runState;
    }

    /**
     * @return submit_time
     */
    public Date getSubmitTime() {
        return submitTime;
    }

    /**
     * @param submitTime
     */
    public void setSubmitTime(Date submitTime) {
        this.submitTime = submitTime;
    }

    /**
     * @return submit_status
     */
    public Integer getSubmitStatus() {
        return submitStatus;
    }

    /**
     * @param submitStatus
     */
    public void setSubmitStatus(Integer submitStatus) {
        this.submitStatus = submitStatus;
    }
}