package com.vrv.vap.xc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table virus_info
 *
 * @mbg.generated do_not_delete_during_merge 2018-11-01 16:34:29
 */
@ApiModel
@SuppressWarnings("unused")
public class VirusInfoQuery extends Query {
    /**
     * 厂商名称
     */
    @ApiModelProperty("厂商名称")
    private String manufactName;

    /**
     * 设备id
     */
    @ApiModelProperty("设备id")
    private String deviceId;

    /**
     * 产品软件版本信息
     */
    @ApiModelProperty("产品软件版本信息")
    private String productVersion;

    /**
     * 病毒库版本信息
     */
    @ApiModelProperty("病毒库版本信息")
    private String virusLibVersion;

    /**
     * 设备唯一ID
     */
    @ApiModelProperty("设备唯一ID")
    private String deviceOnlyId;

    /**
     * 威胁等级
     */
    @ApiModelProperty("威胁等级")
    private String threatLevel;

    /**
     * 本条事件检测的描述
     */
    @ApiModelProperty("本条事件检测的描述")
    private String description;

    /**
     * 事件唯一ID（日志ID）
     */
    @ApiModelProperty("事件唯一ID（日志ID）")
    @NotNull
    private String id;

    /**
     * 病毒检测到时间
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("病毒检测到时间")
    @NotNull
    private Date virusDetectTime;

    /**
     * 威胁文件大小
     */
    @ApiModelProperty("威胁文件大小")
    private String threatFileSize;

    /**
     * 应用层协议（HTTP）
     */
    @ApiModelProperty("应用层协议（HTTP）")
    private String protocalType;

    /**
     * 威胁存储的文件名称
     */
    @ApiModelProperty("威胁存储的文件名称")
    private String threatStorefileName;

    /**
     * 威胁文件类型
     */
    @ApiModelProperty("威胁文件类型")
    private String threatFileType;

    /**
     * 源IPv4
     */
    @ApiModelProperty("源IPv4")
    private String srcIpV4;

    /**
     * 源mac地址
     */
    @ApiModelProperty("源mac地址")
    private String srcMac;

    /**
     * 源端口号
     */
    @ApiModelProperty("源端口号")
    private Long srcPort;

    /**
     * 目标IPv4
     */
    @ApiModelProperty("目标IPv4")
    private String dstIpV4;

    /**
     * 目标mac地址
     */
    @ApiModelProperty("目标mac地址")
    private String dstMac;

    /**
     * 目标端口号
     */
    @ApiModelProperty("目标端口号")
    private Long dstPort;

    /**
     * 源地区
     */
    @ApiModelProperty("源地区")
    private String srcArea;

    /**
     * 目标地区
     */
    @ApiModelProperty("目标地区")
    private String dstArea;

    /**
     * 检出类型
     */
    @ApiModelProperty("检出类型")
    private String detectType;

    /**
     * 病毒名
     */
    @ApiModelProperty("病毒名")
    private String virusName;

    /**
     * 病毒行为
     */
    @ApiModelProperty("病毒行为")
    private String virusAction;

    /**
     * 病毒类别
     */
    @ApiModelProperty("病毒类别")
    private String virusType;

    /**
     * 检出对象
     */
    @ApiModelProperty("检出对象")
    private String detectionObject;

    /**
     * 厂商
     */
    @ApiModelProperty("厂商")
    private String ajbProducer;

    /**
     * 整表数据来源IP
     */
    @ApiModelProperty("整表数据来源IP")
    private String ajbHost;

    /**
     * 接收时间
     */
    @ApiModelProperty("接收时间")
    private String vrvReceiveTime;

    /**
     * 时间
     */
    @ApiModelProperty("时间")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.TIME_RANGE)
    private String dt;

    /**
     * 开始时间  时间分区
     */
    private String startDt;

    /**
     * 结束时间  时间分区
     */
    private String endDt;

    /**
     * 区域代码
     */
    @ApiModelProperty("区域代码")
    private String province;

    public String getManufactName() {
        return manufactName;
    }

    public void setManufactName(String manufactName) {
        this.manufactName = manufactName;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getProductVersion() {
        return productVersion;
    }

    public void setProductVersion(String productVersion) {
        this.productVersion = productVersion;
    }

    public String getVirusLibVersion() {
        return virusLibVersion;
    }

    public void setVirusLibVersion(String virusLibVersion) {
        this.virusLibVersion = virusLibVersion;
    }

    public String getDeviceOnlyId() {
        return deviceOnlyId;
    }

    public void setDeviceOnlyId(String deviceOnlyId) {
        this.deviceOnlyId = deviceOnlyId;
    }

    public String getThreatLevel() {
        return threatLevel;
    }

    public void setThreatLevel(String threatLevel) {
        this.threatLevel = threatLevel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getVirusDetectTime() {
        return virusDetectTime;
    }

    public void setVirusDetectTime(Date virusDetectTime) {
        this.virusDetectTime = virusDetectTime;
    }

    public String getThreatFileSize() {
        return threatFileSize;
    }

    public void setThreatFileSize(String threatFileSize) {
        this.threatFileSize = threatFileSize;
    }

    public String getProtocalType() {
        return protocalType;
    }

    public void setProtocalType(String protocalType) {
        this.protocalType = protocalType;
    }

    public String getThreatStorefileName() {
        return threatStorefileName;
    }

    public void setThreatStorefileName(String threatStorefileName) {
        this.threatStorefileName = threatStorefileName;
    }

    public String getThreatFileType() {
        return threatFileType;
    }

    public void setThreatFileType(String threatFileType) {
        this.threatFileType = threatFileType;
    }

    public String getSrcIpV4() {
        return srcIpV4;
    }

    public void setSrcIpV4(String srcIpV4) {
        this.srcIpV4 = srcIpV4;
    }

    public String getSrcMac() {
        return srcMac;
    }

    public void setSrcMac(String srcMac) {
        this.srcMac = srcMac;
    }

    public Long getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(Long srcPort) {
        this.srcPort = srcPort;
    }

    public String getDstIpV4() {
        return dstIpV4;
    }

    public void setDstIpV4(String dstIpV4) {
        this.dstIpV4 = dstIpV4;
    }

    public String getDstMac() {
        return dstMac;
    }

    public void setDstMac(String dstMac) {
        this.dstMac = dstMac;
    }

    public Long getDstPort() {
        return dstPort;
    }

    public void setDstPort(Long dstPort) {
        this.dstPort = dstPort;
    }

    public String getSrcArea() {
        return srcArea;
    }

    public void setSrcArea(String srcArea) {
        this.srcArea = srcArea;
    }

    public String getDstArea() {
        return dstArea;
    }

    public void setDstArea(String dstArea) {
        this.dstArea = dstArea;
    }

    public String getDetectType() {
        return detectType;
    }

    public void setDetectType(String detectType) {
        this.detectType = detectType;
    }

    public String getVirusName() {
        return virusName;
    }

    public void setVirusName(String virusName) {
        this.virusName = virusName;
    }

    public String getVirusAction() {
        return virusAction;
    }

    public void setVirusAction(String virusAction) {
        this.virusAction = virusAction;
    }

    public String getVirusType() {
        return virusType;
    }

    public void setVirusType(String virusType) {
        this.virusType = virusType;
    }

    public String getDetectionObject() {
        return detectionObject;
    }

    public void setDetectionObject(String detectionObject) {
        this.detectionObject = detectionObject;
    }

    public String getAjbProducer() {
        return ajbProducer;
    }

    public void setAjbProducer(String ajbProducer) {
        this.ajbProducer = ajbProducer;
    }

    public String getAjbHost() {
        return ajbHost;
    }

    public void setAjbHost(String ajbHost) {
        this.ajbHost = ajbHost;
    }

    public String getVrvReceiveTime() {
        return vrvReceiveTime;
    }

    public void setVrvReceiveTime(String vrvReceiveTime) {
        this.vrvReceiveTime = vrvReceiveTime;
    }

    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }

    public String getStartDt() {
        return startDt;
    }

    public void setStartDt(String startDt) {
        this.startDt = startDt;
    }

    public String getEndDt() {
        return endDt;
    }

    public void setEndDt(String endDt) {
        this.endDt = endDt;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}