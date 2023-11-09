package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="HbswOneAccountManyArea对象", description="")
public class HbswOneAccountManyAreaQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String taskId;

    private String sysId;

    private String account;

    private String deviceCount;

    private String areaCount;

    private String searchCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getDeviceCount() {
        return deviceCount;
    }

    public void setDeviceCount(String deviceCount) {
        this.deviceCount = deviceCount;
    }
    public String getAreaCount() {
        return areaCount;
    }

    public void setAreaCount(String areaCount) {
        this.areaCount = areaCount;
    }
    public String getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(String searchCount) {
        this.searchCount = searchCount;
    }

    @Override
    public String toString() {
        return "HbswOneAccountManyArea{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", sysId=" + sysId +
            ", account=" + account +
            ", deviceCount=" + deviceCount +
            ", areaCount=" + areaCount +
            ", searchCount=" + searchCount +
        "}";
    }
}
