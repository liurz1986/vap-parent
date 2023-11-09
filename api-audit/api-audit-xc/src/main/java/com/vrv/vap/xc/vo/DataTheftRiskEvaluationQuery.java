package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 数据盗取行为分析
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="DataTheftRiskEvaluation对象", description="数据盗取行为分析")
public class DataTheftRiskEvaluationQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "设备IP地址")
    private String deviceIp;

    @ApiModelProperty(value = "每日下载数据量风险（0-1之间）")
    private Double downloadsRisk;

    @ApiModelProperty(value = "数据来源的分散性风险（0-1之间）")
    private Double downloadSrcDispersionRisk;

    @ApiModelProperty(value = "每日在非工作时间访问业务系统的访问量风险（0-1之间）")
    private Double nonworkingTimeAccessCountRisk;

    @ApiModelProperty(value = "热点应用系统的访问次数风险（0-1之间）")
    private Double hotspotAccessCountRisk;

    @ApiModelProperty(value = "每日下载数据量")
    private Long downloads;

    @ApiModelProperty(value = "数据来源的分散性")
    private Double downloadSrcDispersion;

    @ApiModelProperty(value = "每日在非工作时间访问业务系统的访问量")
    private Long nonworkingTimeAccessCount;

    @ApiModelProperty(value = "热点应用系统的访问次数")
    private Long hotspotAccessCount;

    @ApiModelProperty(value = "数据盗取风险评估值（0-1之间）")
    private Double riskEvaluation;

    @ApiModelProperty(value = "评估日期")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
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

    public Long getId() {
        return id;
    }

    @ApiModelProperty("")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private boolean focus;

    public void setId(Long id) {
        this.id = id;
    }
    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
    public Double getDownloadsRisk() {
        return downloadsRisk;
    }

    public void setDownloadsRisk(Double downloadsRisk) {
        this.downloadsRisk = downloadsRisk;
    }
    public Double getDownloadSrcDispersionRisk() {
        return downloadSrcDispersionRisk;
    }

    public void setDownloadSrcDispersionRisk(Double downloadSrcDispersionRisk) {
        this.downloadSrcDispersionRisk = downloadSrcDispersionRisk;
    }
    public Double getNonworkingTimeAccessCountRisk() {
        return nonworkingTimeAccessCountRisk;
    }

    public void setNonworkingTimeAccessCountRisk(Double nonworkingTimeAccessCountRisk) {
        this.nonworkingTimeAccessCountRisk = nonworkingTimeAccessCountRisk;
    }
    public Double getHotspotAccessCountRisk() {
        return hotspotAccessCountRisk;
    }

    public void setHotspotAccessCountRisk(Double hotspotAccessCountRisk) {
        this.hotspotAccessCountRisk = hotspotAccessCountRisk;
    }
    public Long getDownloads() {
        return downloads;
    }

    public void setDownloads(Long downloads) {
        this.downloads = downloads;
    }
    public Double getDownloadSrcDispersion() {
        return downloadSrcDispersion;
    }

    public void setDownloadSrcDispersion(Double downloadSrcDispersion) {
        this.downloadSrcDispersion = downloadSrcDispersion;
    }
    public Long getNonworkingTimeAccessCount() {
        return nonworkingTimeAccessCount;
    }

    public void setNonworkingTimeAccessCount(Long nonworkingTimeAccessCount) {
        this.nonworkingTimeAccessCount = nonworkingTimeAccessCount;
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
        return "DataTheftRiskEvaluationQuery{" +
            "id=" + id +
            ", deviceIp=" + deviceIp +
            ", downloadsRisk=" + downloadsRisk +
            ", downloadSrcDispersionRisk=" + downloadSrcDispersionRisk +
            ", nonworkingTimeAccessCountRisk=" + nonworkingTimeAccessCountRisk +
            ", hotspotAccessCountRisk=" + hotspotAccessCountRisk +
            ", downloads=" + downloads +
            ", downloadSrcDispersion=" + downloadSrcDispersion +
            ", nonworkingTimeAccessCount=" + nonworkingTimeAccessCount +
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
