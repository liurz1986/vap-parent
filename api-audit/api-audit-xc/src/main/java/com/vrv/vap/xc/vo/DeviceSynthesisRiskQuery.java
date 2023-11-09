package com.vrv.vap.xc.vo;

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
@ApiModel(value="DeviceSynthesisRisk对象", description="")
public class DeviceSynthesisRiskQuery extends Query {

    private Long id;

    @ApiModelProperty(value = "设备IP地址")
    private String deviceIp;

    @ApiModelProperty(value = "网络流量风险值")
    private Double netflowRisk;

    @ApiModelProperty(value = "攻击风险值")
    private Double attackRisk;

    @ApiModelProperty(value = "热点应用系统访问风险值")
    private Double hotspotAccessRisk;

    @ApiModelProperty(value = "病毒文件风险值")
    private Double virusRisk;

    @ApiModelProperty(value = "脆弱性风险值")
    private Double vulnerabilityRisk;

    @ApiModelProperty(value = "威胁情报风险值")
    private Double threatRisk;

    @ApiModelProperty(value = "设备综合风险值")
    private Double deviceSynthesisRisk;

    @ApiModelProperty(value = "评估日期")
    private String yyyymmdd;

    @ApiModelProperty(value = "设备IP区域码")
    private String areaCode;

    @ApiModelProperty(value = "设备IP区域名称")
    private String areaName;

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
    public Double getNetflowRisk() {
        return netflowRisk;
    }

    public void setNetflowRisk(Double netflowRisk) {
        this.netflowRisk = netflowRisk;
    }
    public Double getAttackRisk() {
        return attackRisk;
    }

    public void setAttackRisk(Double attackRisk) {
        this.attackRisk = attackRisk;
    }
    public Double getHotspotAccessRisk() {
        return hotspotAccessRisk;
    }

    public void setHotspotAccessRisk(Double hotspotAccessRisk) {
        this.hotspotAccessRisk = hotspotAccessRisk;
    }
    public Double getVirusRisk() {
        return virusRisk;
    }

    public void setVirusRisk(Double virusRisk) {
        this.virusRisk = virusRisk;
    }
    public Double getVulnerabilityRisk() {
        return vulnerabilityRisk;
    }

    public void setVulnerabilityRisk(Double vulnerabilityRisk) {
        this.vulnerabilityRisk = vulnerabilityRisk;
    }
    public Double getThreatRisk() {
        return threatRisk;
    }

    public void setThreatRisk(Double threatRisk) {
        this.threatRisk = threatRisk;
    }
    public Double getDeviceSynthesisRisk() {
        return deviceSynthesisRisk;
    }

    public void setDeviceSynthesisRisk(Double deviceSynthesisRisk) {
        this.deviceSynthesisRisk = deviceSynthesisRisk;
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

    @Override
    public String toString() {
        return "DeviceSynthesisRisk{" +
            "id=" + id +
            ", deviceIp=" + deviceIp +
            ", netflowRisk=" + netflowRisk +
            ", attackRisk=" + attackRisk +
            ", hotspotAccessRisk=" + hotspotAccessRisk +
            ", virusRisk=" + virusRisk +
            ", vulnerabilityRisk=" + vulnerabilityRisk +
            ", threatRisk=" + threatRisk +
            ", deviceSynthesisRisk=" + deviceSynthesisRisk +
            ", yyyymmdd=" + yyyymmdd +
            ", areaCode=" + areaCode +
            ", areaName=" + areaName +
        "}";
    }
}
