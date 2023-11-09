package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 边界平台应用协议
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-20
 */
@ApiModel(value="TplatBizProInf对象", description="边界平台应用协议")
public class TplatBizProInfQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "平台标识")
    private String platId;

    @ApiModelProperty(value = "应用标识")
    private String bizId;

    @ApiModelProperty(value = "业务协议标识")
    private String protocolId;

    @ApiModelProperty(value = "业务协议名称")
    private String protocolName;

    @ApiModelProperty(value = "协议代码")
    private String protocolCode;

    @ApiModelProperty(value = "源IP地址范围")
    private String srcIp;

    @ApiModelProperty(value = "目的IP地址范围")
    private String destIp;

    @ApiModelProperty(value = "源端口范围")
    private String srcPort;

    @ApiModelProperty(value = "目的端口范围")
    private String destPort;

    @ApiModelProperty(value = "统计时间")
    private String collectTime;

    @ApiModelProperty(value = "时间")
    private String dt;

    @ApiModelProperty(value = "区域代码")
    private String province;

    @ApiModelProperty(value = "警种")
    private String policeType;

    @ApiModelProperty(value = "描述")
    private String tplatDesc;

    @ApiModelProperty(value = "是否包含下级平台信息")
    private String childInclude;

    @ApiModelProperty(value = "webservice服务端代码版本号")
    private String version;

    @ApiModelProperty(value = "下级平台个数")
    private String childNum;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getPlatId() {
        return platId;
    }

    public void setPlatId(String platId) {
        this.platId = platId;
    }
    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
    public String getProtocolId() {
        return protocolId;
    }

    public void setProtocolId(String protocolId) {
        this.protocolId = protocolId;
    }
    public String getProtocolName() {
        return protocolName;
    }

    public void setProtocolName(String protocolName) {
        this.protocolName = protocolName;
    }
    public String getProtocolCode() {
        return protocolCode;
    }

    public void setProtocolCode(String protocolCode) {
        this.protocolCode = protocolCode;
    }
    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }
    public String getDestIp() {
        return destIp;
    }

    public void setDestIp(String destIp) {
        this.destIp = destIp;
    }
    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }
    public String getDestPort() {
        return destPort;
    }

    public void setDestPort(String destPort) {
        this.destPort = destPort;
    }
    public String getCollectTime() {
        return collectTime;
    }

    public void setCollectTime(String collectTime) {
        this.collectTime = collectTime;
    }
    public String getDt() {
        return dt;
    }

    public void setDt(String dt) {
        this.dt = dt;
    }
    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }
    public String getTplatDesc() {
        return tplatDesc;
    }

    public void setTplatDesc(String tplatDesc) {
        this.tplatDesc = tplatDesc;
    }
    public String getChildInclude() {
        return childInclude;
    }

    public void setChildInclude(String childInclude) {
        this.childInclude = childInclude;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getChildNum() {
        return childNum;
    }

    public void setChildNum(String childNum) {
        this.childNum = childNum;
    }

    @Override
    public String toString() {
        return "TplatBizProInfQuery{" +
            "id=" + id +
            ", platId=" + platId +
            ", bizId=" + bizId +
            ", protocolId=" + protocolId +
            ", protocolName=" + protocolName +
            ", protocolCode=" + protocolCode +
            ", srcIp=" + srcIp +
            ", destIp=" + destIp +
            ", srcPort=" + srcPort +
            ", destPort=" + destPort +
            ", collectTime=" + collectTime +
            ", dt=" + dt +
            ", province=" + province +
            ", policeType=" + policeType +
            ", tplatDesc=" + tplatDesc +
            ", childInclude=" + childInclude +
            ", version=" + version +
            ", childNum=" + childNum +
        "}";
    }
}
