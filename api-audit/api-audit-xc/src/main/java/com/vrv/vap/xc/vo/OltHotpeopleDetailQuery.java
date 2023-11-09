package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
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
@ApiModel(value="OltHotpeopleDetail对象", description="")
public class OltHotpeopleDetailQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "地区名称")
    private String areaName;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String userName;

    @ApiModelProperty(value = "IP地址")
    private String ip;

    @ApiModelProperty(value = "应用系统id")
    private String sysId;

    @ApiModelProperty(value = "操作时间")
    private Date operTime;

    @ApiModelProperty(value = "机构名称")
    private String org;

    @ApiModelProperty(value = "人名")
    private String people;

    @ApiModelProperty(value = "操作条件")
    private String operation;

    private String areaCode;

    @ApiModelProperty(value = "警种")
    private String policeType;

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
    public String getPeople() {
        return people;
    }

    public void setPeople(String people) {
        this.people = people;
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }

    @Override
    public String toString() {
        return "OltHotpeopleDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaName=" + areaName +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", ip=" + ip +
            ", sysId=" + sysId +
            ", operTime=" + operTime +
            ", org=" + org +
            ", people=" + people +
            ", operation=" + operation +
            ", areaCode=" + areaCode +
            ", policeType=" + policeType +
        "}";
    }
}
