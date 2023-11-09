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
@ApiModel(value="OltBevisitedPeopleImportDetail对象", description="")
public class OltBevisitedPeopleImportDetailQuery extends Query {

@TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "任务id")
    private String taskId;

    @ApiModelProperty(value = "区域名称")
    private String areaName;

    @ApiModelProperty(value = "身份证")
    private String idCard;

    @ApiModelProperty(value = "姓名")
    private String userName;

    @ApiModelProperty(value = "ip")
    private String ip;

    @ApiModelProperty(value = "省级地区编码")
    private String provinceCode;

    @ApiModelProperty(value = "市级地区编码")
    private String cityCode;

    @ApiModelProperty(value = "县级地区编码")
    private String countyCode;

    @ApiModelProperty(value = "统计的地区编码")
    private String countArea;

    @ApiModelProperty(value = "巨龙系统编号")
    private String sysId;

    @ApiModelProperty(value = "操作时间")
    private Date operTime;

    @ApiModelProperty(value = "组织机构")
    private String organ;

    @ApiModelProperty(value = "操作条件")
    private String operation;

    @ApiModelProperty(value = "警种")
    private String policeType;

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
    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }
    public String getCountyCode() {
        return countyCode;
    }

    public void setCountyCode(String countyCode) {
        this.countyCode = countyCode;
    }
    public String getCountArea() {
        return countArea;
    }

    public void setCountArea(String countArea) {
        this.countArea = countArea;
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
    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    @Override
    public String toString() {
        return "OltBevisitedPeopleImportDetail{" +
            "id=" + id +
            ", taskId=" + taskId +
            ", areaName=" + areaName +
            ", idCard=" + idCard +
            ", userName=" + userName +
            ", ip=" + ip +
            ", provinceCode=" + provinceCode +
            ", cityCode=" + cityCode +
            ", countyCode=" + countyCode +
            ", countArea=" + countArea +
            ", sysId=" + sysId +
            ", operTime=" + operTime +
            ", organ=" + organ +
            ", operation=" + operation +
            ", policeType=" + policeType +
            ", areaCode=" + areaCode +
        "}";
    }
}
