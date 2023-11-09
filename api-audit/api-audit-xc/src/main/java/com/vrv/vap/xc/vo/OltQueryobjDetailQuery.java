package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-28
 */
@ApiModel(value="OltQueryobjDetail对象", description="")
public class OltQueryobjDetailQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "任务ID")
    private String taskId;

    @ApiModelProperty(value = "区域")
    private String areaName;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "IP")
    private String ip;

    @ApiModelProperty(value = "系统")
    private String sysId;

    @ApiModelProperty(value = "操作时间")
    private Date operTime;

    @ApiModelProperty(value = "单位")
    private String org;

    @ApiModelProperty(value = "操作对象")
    private String operateName;

    @ApiModelProperty(value = "操作内容")
    private String operation;

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
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }
    public String getIdCard() {
        return idCard;
    }

    public void setIdCard(String idCard) {
        this.idCard = idCard;
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
    public String getSysId() {
        return sysId;
    }

    public void setSysId(String sysId) {
        this.sysId = sysId;
    }
    public Date getOperTime() {
        return operTime;
    }

    public void setOperTime(Date operTime) {
        this.operTime = operTime;
    }
    public String getOrg() {
        return org;
    }

    public void setOrg(String org) {
        this.org = org;
    }
    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return "OltQueryobjDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaName=" + areaName +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", ip=" + ip +
            ", sysId=" + sysId +
            ", operTime=" + operTime +
            ", org=" + org +
            ", operateName=" + operateName +
            ", operation=" + operation +
        "}";
    }
}
