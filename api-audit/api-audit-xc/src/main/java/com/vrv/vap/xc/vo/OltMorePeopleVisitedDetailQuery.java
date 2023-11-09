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
@ApiModel(value="OltMorePeopleVisitedDetail对象", description="")
public class OltMorePeopleVisitedDetailQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    private String userName;

    private String areaName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "巨龙系统编号")
    private String sysId;

    @ApiModelProperty(value = "操作时间")
    private Date operTime;

    @ApiModelProperty(value = "组织机构")
    private String organ;

    @ApiModelProperty(value = "操作条件")
    private String operation;

    @ApiModelProperty(value = "操作对象")
    private String objectPeople;

    private String areaCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
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
    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
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
    public String getOrgan() {
        return organ;
    }

    public void setOrgan(String organ) {
        this.organ = organ;
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getObjectPeople() {
        return objectPeople;
    }

    public void setObjectPeople(String objectPeople) {
        this.objectPeople = objectPeople;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "OltMorePeopleVisitedDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", areaName=" + areaName +
            ", ip=" + ip +
            ", sysId=" + sysId +
            ", operTime=" + operTime +
            ", organ=" + organ +
            ", operation=" + operation +
            ", objectPeople=" + objectPeople +
            ", areaCode=" + areaCode +
        "}";
    }
}
