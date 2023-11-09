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
@ApiModel(value="HbswOneDeviceManyAccountCount对象", description="")
public class HbswOneDeviceManyAccountCountQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String taskId;

    private String ip;

    private String sysId;

    private Integer nums;

    private String account;

    private String areaCode;

    private String areaName;

    private String organ;

    private String userName;

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
    public Integer getNums() {
        return nums;
    }

    public void setNums(Integer nums) {
        this.nums = nums;
    }
    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
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
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "HbswOneDeviceManyAccountCount{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", ip=" + ip +
            ", sysId=" + sysId +
            ", nums=" + nums +
            ", account=" + account +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", organ=" + organ +
            ", userName=" + userName +
        "}";
    }
}
