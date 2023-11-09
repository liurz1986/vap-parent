package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 设备风险得分
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-26
 */
@ApiModel(value="DeviceRiskScore对象", description="设备风险得分")
public class DeviceRiskScoreQuery extends Query {

    @ApiModelProperty(value = "主键ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "设备IP")
    private String deviceIp;

    @ApiModelProperty(value = "流量告警得分")
    private Double netflowRiskScore;

    @ApiModelProperty(value = "访问次数")
    private Integer netflowVisitCount;

    @ApiModelProperty(value = "访问设备数量")
    private Integer netflowVisitDeviceCount;

    @ApiModelProperty(value = "被访问次数")
    private Integer netflowVisitedCount;

    @ApiModelProperty(value = "被访问设备数")
    private Integer netflowVisitedDeviceCount;

    @ApiModelProperty(value = "上班访问次数")
    private Integer netOnCount;

    @ApiModelProperty(value = "下班访问次数")
    private Integer netOffCount;

    @ApiModelProperty(value = "应用访问告警得分")
    private Double netRiskScore;

    @ApiModelProperty(value = "攻击风险得分")
    private Integer threatRiskScore;

    @ApiModelProperty(value = "攻击次数")
    private Integer threatCount;

    @ApiModelProperty(value = "被攻击次数")
    private Integer threatByCount;

    @ApiModelProperty(value = "风险得分")
    private Double riskScore;

    @ApiModelProperty(value = "数据时间")
    private String dataTime;

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
    public Double getNetflowRiskScore() {
        return netflowRiskScore;
    }

    public void setNetflowRiskScore(Double netflowRiskScore) {
        this.netflowRiskScore = netflowRiskScore;
    }
    public Integer getNetflowVisitCount() {
        return netflowVisitCount;
    }

    public void setNetflowVisitCount(Integer netflowVisitCount) {
        this.netflowVisitCount = netflowVisitCount;
    }
    public Integer getNetflowVisitDeviceCount() {
        return netflowVisitDeviceCount;
    }

    public void setNetflowVisitDeviceCount(Integer netflowVisitDeviceCount) {
        this.netflowVisitDeviceCount = netflowVisitDeviceCount;
    }
    public Integer getNetflowVisitedCount() {
        return netflowVisitedCount;
    }

    public void setNetflowVisitedCount(Integer netflowVisitedCount) {
        this.netflowVisitedCount = netflowVisitedCount;
    }
    public Integer getNetflowVisitedDeviceCount() {
        return netflowVisitedDeviceCount;
    }

    public void setNetflowVisitedDeviceCount(Integer netflowVisitedDeviceCount) {
        this.netflowVisitedDeviceCount = netflowVisitedDeviceCount;
    }
    public Integer getNetOnCount() {
        return netOnCount;
    }

    public void setNetOnCount(Integer netOnCount) {
        this.netOnCount = netOnCount;
    }
    public Integer getNetOffCount() {
        return netOffCount;
    }

    public void setNetOffCount(Integer netOffCount) {
        this.netOffCount = netOffCount;
    }
    public Double getNetRiskScore() {
        return netRiskScore;
    }

    public void setNetRiskScore(Double netRiskScore) {
        this.netRiskScore = netRiskScore;
    }
    public Integer getThreatRiskScore() {
        return threatRiskScore;
    }

    public void setThreatRiskScore(Integer threatRiskScore) {
        this.threatRiskScore = threatRiskScore;
    }
    public Integer getThreatCount() {
        return threatCount;
    }

    public void setThreatCount(Integer threatCount) {
        this.threatCount = threatCount;
    }
    public Integer getThreatByCount() {
        return threatByCount;
    }

    public void setThreatByCount(Integer threatByCount) {
        this.threatByCount = threatByCount;
    }
    public Double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(Double riskScore) {
        this.riskScore = riskScore;
    }
    public String getDataTime() {
        return dataTime;
    }

    public void setDataTime(String dataTime) {
        this.dataTime = dataTime;
    }

    @Override
    public String toString() {
        return "DeviceRiskScore{" +
            "id=" + id +
            ", deviceIp=" + deviceIp +
            ", netflowRiskScore=" + netflowRiskScore +
            ", netflowVisitCount=" + netflowVisitCount +
            ", netflowVisitDeviceCount=" + netflowVisitDeviceCount +
            ", netflowVisitedCount=" + netflowVisitedCount +
            ", netflowVisitedDeviceCount=" + netflowVisitedDeviceCount +
            ", netOnCount=" + netOnCount +
            ", netOffCount=" + netOffCount +
            ", netRiskScore=" + netRiskScore +
            ", threatRiskScore=" + threatRiskScore +
            ", threatCount=" + threatCount +
            ", threatByCount=" + threatByCount +
            ", riskScore=" + riskScore +
            ", dataTime=" + dataTime +
        "}";
    }
}
