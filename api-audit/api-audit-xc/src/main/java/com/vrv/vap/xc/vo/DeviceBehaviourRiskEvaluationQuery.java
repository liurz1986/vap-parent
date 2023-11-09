package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 设备行为风险分析
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="DeviceBehaviourRiskEvaluation对象", description="设备行为风险分析")
public class DeviceBehaviourRiskEvaluationQuery extends Query {

    private Long id;

    @ApiModelProperty(value = "设备IP地址")
    private String deviceIp;

    @ApiModelProperty(value = "IDS告警的数量风险（0-1之间）")
    private Double warningMsgCountRisk;

    @ApiModelProperty(value = "IDS告警的分散性风险（0-1之间）")
    private Double warningMsgDispersionRisk;

    @ApiModelProperty(value = "PKI证书登录次数风险（0-1之间）")
    private Double loginCountRisk;

    @ApiModelProperty(value = "PKI证书登录分散性风险（0-1之间）")
    private Double loginDispersionRisk;

    @ApiModelProperty(value = "设备访问应用系统的次数风险（0-1之间）")
    private Double appAccessCountRisk;

    @ApiModelProperty(value = "设备访问应用系统的分散性风险（0-1之间）")
    private Double appAccessDispersionRisk;

    @ApiModelProperty(value = "热点应用系统的访问次数风险（0-1之间）")
    private Double hotspotAccessCountRisk;

    @ApiModelProperty(value = "IDS告警的数量")
    private Long warningMsgCount;

    @ApiModelProperty(value = "IDS告警的分散性")
    private Double warningMsgDispersion;

    @ApiModelProperty(value = "PKI证书登录次数")
    private Long loginCount;

    @ApiModelProperty(value = "PKI证书登录的分散性")
    private Double loginDispersion;

    @ApiModelProperty(value = "设备访问应用系统的次数")
    private Long appAccessCount;

    @ApiModelProperty(value = "设备访问应用系统的分散性")
    private Double appAccessDispersion;

    @ApiModelProperty(value = "热点应用系统的访问次数")
    private Long hotspotAccessCount;

    @ApiModelProperty(value = "设备行为风险评估值")
    private Double riskEvaluation;

    @ApiModelProperty(value = "日期")
    private String yyyymmdd;

    @ApiModelProperty(value = "设备IP区域码")
    private String areaCode;

    @ApiModelProperty(value = "设备IP区域名称")
    private String areaName;

    @ApiModelProperty(value = "设备注册用户ID")
    private String userId;

    @ApiModelProperty(value = "设备注册用户名")
    private String userName;

    @ApiModelProperty(value = "设备注册用户警种ID")
    private String policeTypeId;

    @ApiModelProperty(value = "设备注册用户警种名称")
    private String policeTypeName;

    @ApiModelProperty(value = "单位编码（组织机构编码）")
    private String orgCode;

    @ApiModelProperty(value = "单位名称（组织机构名称）")
    private String orgName;

    @ApiModelProperty(value = "岗位编码")
    private String station;

    @ApiModelProperty(value = "岗位名称")
    private String stationName;

    @ApiModelProperty("")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private boolean focus;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
    public Double getWarningMsgCountRisk() {
        return warningMsgCountRisk;
    }

    public void setWarningMsgCountRisk(Double warningMsgCountRisk) {
        this.warningMsgCountRisk = warningMsgCountRisk;
    }
    public Double getWarningMsgDispersionRisk() {
        return warningMsgDispersionRisk;
    }

    public void setWarningMsgDispersionRisk(Double warningMsgDispersionRisk) {
        this.warningMsgDispersionRisk = warningMsgDispersionRisk;
    }
    public Double getLoginCountRisk() {
        return loginCountRisk;
    }

    public void setLoginCountRisk(Double loginCountRisk) {
        this.loginCountRisk = loginCountRisk;
    }
    public Double getLoginDispersionRisk() {
        return loginDispersionRisk;
    }

    public void setLoginDispersionRisk(Double loginDispersionRisk) {
        this.loginDispersionRisk = loginDispersionRisk;
    }
    public Double getAppAccessCountRisk() {
        return appAccessCountRisk;
    }

    public void setAppAccessCountRisk(Double appAccessCountRisk) {
        this.appAccessCountRisk = appAccessCountRisk;
    }
    public Double getAppAccessDispersionRisk() {
        return appAccessDispersionRisk;
    }

    public void setAppAccessDispersionRisk(Double appAccessDispersionRisk) {
        this.appAccessDispersionRisk = appAccessDispersionRisk;
    }
    public Double getHotspotAccessCountRisk() {
        return hotspotAccessCountRisk;
    }

    public void setHotspotAccessCountRisk(Double hotspotAccessCountRisk) {
        this.hotspotAccessCountRisk = hotspotAccessCountRisk;
    }
    public Long getWarningMsgCount() {
        return warningMsgCount;
    }

    public void setWarningMsgCount(Long warningMsgCount) {
        this.warningMsgCount = warningMsgCount;
    }
    public Double getWarningMsgDispersion() {
        return warningMsgDispersion;
    }

    public void setWarningMsgDispersion(Double warningMsgDispersion) {
        this.warningMsgDispersion = warningMsgDispersion;
    }
    public Long getLoginCount() {
        return loginCount;
    }

    public void setLoginCount(Long loginCount) {
        this.loginCount = loginCount;
    }
    public Double getLoginDispersion() {
        return loginDispersion;
    }

    public void setLoginDispersion(Double loginDispersion) {
        this.loginDispersion = loginDispersion;
    }
    public Long getAppAccessCount() {
        return appAccessCount;
    }

    public void setAppAccessCount(Long appAccessCount) {
        this.appAccessCount = appAccessCount;
    }
    public Double getAppAccessDispersion() {
        return appAccessDispersion;
    }

    public void setAppAccessDispersion(Double appAccessDispersion) {
        this.appAccessDispersion = appAccessDispersion;
    }
    public Long getHotspotAccessCount() {
        return hotspotAccessCount;
    }

    public void setHotspotAccessCount(Long hotspotAccessCount) {
        this.hotspotAccessCount = hotspotAccessCount;
    }
    public Double getRiskEvaluation() {
        return riskEvaluation;
    }

    public void setRiskEvaluation(Double riskEvaluation) {
        this.riskEvaluation = riskEvaluation;
    }
    public String getYyyymmdd() {
        return yyyymmdd;
    }

    public void setYyyymmdd(String yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
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
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPoliceTypeId() {
        return policeTypeId;
    }

    public void setPoliceTypeId(String policeTypeId) {
        this.policeTypeId = policeTypeId;
    }
    public String getPoliceTypeName() {
        return policeTypeName;
    }

    public void setPoliceTypeName(String policeTypeName) {
        this.policeTypeName = policeTypeName;
    }
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }
    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }
    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public boolean isFocus() {
        return focus;
    }

    public void setFocus(boolean focus) {
        this.focus = focus;
    }

    @Override
    public String toString() {
        return "DeviceBehaviourRiskEvaluationQuery{" +
            "id=" + id +
            ", deviceIp=" + deviceIp +
            ", warningMsgCountRisk=" + warningMsgCountRisk +
            ", warningMsgDispersionRisk=" + warningMsgDispersionRisk +
            ", loginCountRisk=" + loginCountRisk +
            ", loginDispersionRisk=" + loginDispersionRisk +
            ", appAccessCountRisk=" + appAccessCountRisk +
            ", appAccessDispersionRisk=" + appAccessDispersionRisk +
            ", hotspotAccessCountRisk=" + hotspotAccessCountRisk +
            ", warningMsgCount=" + warningMsgCount +
            ", warningMsgDispersion=" + warningMsgDispersion +
            ", loginCount=" + loginCount +
            ", loginDispersion=" + loginDispersion +
            ", appAccessCount=" + appAccessCount +
            ", appAccessDispersion=" + appAccessDispersion +
            ", hotspotAccessCount=" + hotspotAccessCount +
            ", riskEvaluation=" + riskEvaluation +
            ", yyyymmdd=" + yyyymmdd +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
            ", userId=" + userId +
            ", userName=" + userName +
            ", policeTypeId=" + policeTypeId +
            ", policeTypeName=" + policeTypeName +
            ", orgCode=" + orgCode +
            ", orgName=" + orgName +
            ", station=" + station +
            ", stationName=" + stationName +
        "}";
    }
}
