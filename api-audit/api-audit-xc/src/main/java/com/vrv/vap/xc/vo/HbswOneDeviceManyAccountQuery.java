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
@ApiModel(value="HbswOneDeviceManyAccount对象", description="")
public class HbswOneDeviceManyAccountQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String taskId;

    private String areaCode;

    private String areaName;

    private String userName;

    private String organ;

    private String ip;

    private String sysId;

    private Integer personCount;

    private Integer searchCount;

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
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public Integer getPersonCount() {
        return personCount;
    }

    public void setPersonCount(Integer personCount) {
        this.personCount = personCount;
    }
    public Integer getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }

    @Override
    public String toString() {
        return "HbswOneDeviceManyAccount{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", userName=" + userName +
            ", organ=" + organ +
            ", ip=" + ip +
            ", sysId=" + sysId +
            ", personCount=" + personCount +
            ", searchCount=" + searchCount +
        "}";
    }
}
