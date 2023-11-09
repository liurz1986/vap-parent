package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-31
 */
@ApiModel(value="NatProxyRiskEvaluation对象", description="")
public class NatProxyRiskEvaluationQuery extends Query {

    private Long id;

    @ApiModelProperty(value = "设备IP地址")
    private String deviceIp;

    @ApiModelProperty(value = "上行数据包数")
    private Long uploadPkg;

    @ApiModelProperty(value = "上行数据包数风险（0-1之间）")
    private Double uploadPkgRisk;

    @ApiModelProperty(value = "上行数据字节数")
    private Long uploadBytes;

    @ApiModelProperty(value = "上行数据字节数风险（0-1之间）")
    private Double uploadBytesRisk;

    @ApiModelProperty(value = "下行数据包数")
    private Long downloadPkg;

    @ApiModelProperty(value = "下行数据包数风险（0-1之间）")
    private Double downloadPkgRisk;

    @ApiModelProperty(value = "下行数据字节数")
    private Long downloadBytes;

    @ApiModelProperty(value = "下行数据字节数风险（0-1之间")
    private Double downloadBytesRisk;

    @ApiModelProperty(value = "流量五元组数")
    private Long flowCnt;

    @ApiModelProperty(value = "流量五元组数风险（0-1之间）")
    private Double flowCntRisk;

    @ApiModelProperty(value = "源端口数")
    private Long sportCnt;

    @ApiModelProperty(value = "源端口数风险（0-1之间）")
    private Double sportCntRisk;

    @ApiModelProperty(value = "目标端口数")
    private Long dportCnt;

    @ApiModelProperty(value = "目标端口数风险（0-1之间）")
    private Double dportCntRisk;

    @ApiModelProperty(value = "TCP连接数")
    private Long tcpCnt;

    @ApiModelProperty(value = "TCP连接数风险（0-1之间）")
    private Double tcpCntRisk;

    @ApiModelProperty(value = "DNS报文数")
    private Long dnsCnt;

    @ApiModelProperty(value = "DNS报文数风险（0-1之间）")
    private Double dnsCntRisk;

    @ApiModelProperty(value = "目标IP地址数")
    private Long dipCnt;

    @ApiModelProperty(value = "目标IP地址数风险（0-1之间）")
    private Double dipCntRisk;

    @ApiModelProperty(value = "综合风险评估值（0-1之间）")
    private Double riskEvaluation;

    @ApiModelProperty(value = "日期")
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
    public Long getUploadPkg() {
        return uploadPkg;
    }

    public void setUploadPkg(Long uploadPkg) {
        this.uploadPkg = uploadPkg;
    }
    public Double getUploadPkgRisk() {
        return uploadPkgRisk;
    }

    public void setUploadPkgRisk(Double uploadPkgRisk) {
        this.uploadPkgRisk = uploadPkgRisk;
    }
    public Long getUploadBytes() {
        return uploadBytes;
    }

    public void setUploadBytes(Long uploadBytes) {
        this.uploadBytes = uploadBytes;
    }
    public Double getUploadBytesRisk() {
        return uploadBytesRisk;
    }

    public void setUploadBytesRisk(Double uploadBytesRisk) {
        this.uploadBytesRisk = uploadBytesRisk;
    }
    public Long getDownloadPkg() {
        return downloadPkg;
    }

    public void setDownloadPkg(Long downloadPkg) {
        this.downloadPkg = downloadPkg;
    }
    public Double getDownloadPkgRisk() {
        return downloadPkgRisk;
    }

    public void setDownloadPkgRisk(Double downloadPkgRisk) {
        this.downloadPkgRisk = downloadPkgRisk;
    }
    public Long getDownloadBytes() {
        return downloadBytes;
    }

    public void setDownloadBytes(Long downloadBytes) {
        this.downloadBytes = downloadBytes;
    }
    public Double getDownloadBytesRisk() {
        return downloadBytesRisk;
    }

    public void setDownloadBytesRisk(Double downloadBytesRisk) {
        this.downloadBytesRisk = downloadBytesRisk;
    }
    public Long getFlowCnt() {
        return flowCnt;
    }

    public void setFlowCnt(Long flowCnt) {
        this.flowCnt = flowCnt;
    }
    public Double getFlowCntRisk() {
        return flowCntRisk;
    }

    public void setFlowCntRisk(Double flowCntRisk) {
        this.flowCntRisk = flowCntRisk;
    }
    public Long getSportCnt() {
        return sportCnt;
    }

    public void setSportCnt(Long sportCnt) {
        this.sportCnt = sportCnt;
    }
    public Double getSportCntRisk() {
        return sportCntRisk;
    }

    public void setSportCntRisk(Double sportCntRisk) {
        this.sportCntRisk = sportCntRisk;
    }
    public Long getDportCnt() {
        return dportCnt;
    }

    public void setDportCnt(Long dportCnt) {
        this.dportCnt = dportCnt;
    }
    public Double getDportCntRisk() {
        return dportCntRisk;
    }

    public void setDportCntRisk(Double dportCntRisk) {
        this.dportCntRisk = dportCntRisk;
    }
    public Long getTcpCnt() {
        return tcpCnt;
    }

    public void setTcpCnt(Long tcpCnt) {
        this.tcpCnt = tcpCnt;
    }
    public Double getTcpCntRisk() {
        return tcpCntRisk;
    }

    public void setTcpCntRisk(Double tcpCntRisk) {
        this.tcpCntRisk = tcpCntRisk;
    }
    public Long getDnsCnt() {
        return dnsCnt;
    }

    public void setDnsCnt(Long dnsCnt) {
        this.dnsCnt = dnsCnt;
    }
    public Double getDnsCntRisk() {
        return dnsCntRisk;
    }

    public void setDnsCntRisk(Double dnsCntRisk) {
        this.dnsCntRisk = dnsCntRisk;
    }
    public Long getDipCnt() {
        return dipCnt;
    }

    public void setDipCnt(Long dipCnt) {
        this.dipCnt = dipCnt;
    }
    public Double getDipCntRisk() {
        return dipCntRisk;
    }

    public void setDipCntRisk(Double dipCntRisk) {
        this.dipCntRisk = dipCntRisk;
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
        return "NatProxyRiskEvaluation{" +
            "id=" + id +
            ", deviceIp=" + deviceIp +
            ", uploadPkg=" + uploadPkg +
            ", uploadPkgRisk=" + uploadPkgRisk +
            ", uploadBytes=" + uploadBytes +
            ", uploadBytesRisk=" + uploadBytesRisk +
            ", downloadPkg=" + downloadPkg +
            ", downloadPkgRisk=" + downloadPkgRisk +
            ", downloadBytes=" + downloadBytes +
            ", downloadBytesRisk=" + downloadBytesRisk +
            ", flowCnt=" + flowCnt +
            ", flowCntRisk=" + flowCntRisk +
            ", sportCnt=" + sportCnt +
            ", sportCntRisk=" + sportCntRisk +
            ", dportCnt=" + dportCnt +
            ", dportCntRisk=" + dportCntRisk +
            ", tcpCnt=" + tcpCnt +
            ", tcpCntRisk=" + tcpCntRisk +
            ", dnsCnt=" + dnsCnt +
            ", dnsCntRisk=" + dnsCntRisk +
            ", dipCnt=" + dipCnt +
            ", dipCntRisk=" + dipCntRisk +
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
