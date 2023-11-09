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
@ApiModel(value="HbswOneAccountManyDeviceCount对象", description="")
public class HbswOneAccountManyDeviceCountQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String taskId;

    private String account;

    private String sysId;

    private String org;

    private String userName;

    private String ip;

    private String searchCount;

    private String areaCode;

    private String areaName;

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
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(String searchCount) {
        this.searchCount = searchCount;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    @Override
    public String toString() {
        return "HbswOneAccountManyDeviceCount{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", account=" + account +
            ", sysId=" + sysId +
            ", org=" + org +
            ", userName=" + userName +
            ", ip=" + ip +
            ", searchCount=" + searchCount +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
        "}";
    }
}
