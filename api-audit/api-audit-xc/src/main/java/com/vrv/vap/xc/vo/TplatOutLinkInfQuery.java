package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 边界平台外部链路
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-20
 */
@ApiModel(value="TplatOutLinkInf对象", description="边界平台外部链路")
public class TplatOutLinkInfQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "平台标识")
    private String platId;

    @ApiModelProperty(value = "内部链路标识")
    private String innerLinkId;

    @ApiModelProperty(value = "外部链路标识")
    private String outLinkId;

    @ApiModelProperty(value = "外部链路提供商")
    private String outLinkVender;

    @ApiModelProperty(value = "外部链路接入对象说明")
    private String connectObject;

    @ApiModelProperty(value = "外部链路带宽,单位M")
    private Integer outLinkBandwidth;

    @ApiModelProperty(value = "外部链路说明")
    private String outLinkDesc;

    @ApiModelProperty(value = "统计时间")
    private String collectTime;

    @ApiModelProperty(value = "时间")
    private String dt;

    @ApiModelProperty(value = "区域代码")
    private String province;

    @ApiModelProperty(value = "警种")
    private String policeType;

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
    public String getInnerLinkId() {
        return innerLinkId;
    }

    public void setInnerLinkId(String innerLinkId) {
        this.innerLinkId = innerLinkId;
    }
    public String getOutLinkId() {
        return outLinkId;
    }

    public void setOutLinkId(String outLinkId) {
        this.outLinkId = outLinkId;
    }
    public String getOutLinkVender() {
        return outLinkVender;
    }

    public void setOutLinkVender(String outLinkVender) {
        this.outLinkVender = outLinkVender;
    }
    public String getConnectObject() {
        return connectObject;
    }

    public void setConnectObject(String connectObject) {
        this.connectObject = connectObject;
    }
    public Integer getOutLinkBandwidth() {
        return outLinkBandwidth;
    }

    public void setOutLinkBandwidth(Integer outLinkBandwidth) {
        this.outLinkBandwidth = outLinkBandwidth;
    }
    public String getOutLinkDesc() {
        return outLinkDesc;
    }

    public void setOutLinkDesc(String outLinkDesc) {
        this.outLinkDesc = outLinkDesc;
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

    @Override
    public String toString() {
        return "TplatOutLinkInfQuery{" +
            "id=" + id +
            ", platId=" + platId +
            ", innerLinkId=" + innerLinkId +
            ", outLinkId=" + outLinkId +
            ", outLinkVender=" + outLinkVender +
            ", connectObject=" + connectObject +
            ", outLinkBandwidth=" + outLinkBandwidth +
            ", outLinkDesc=" + outLinkDesc +
            ", collectTime=" + collectTime +
            ", dt=" + dt +
            ", province=" + province +
            ", policeType=" + policeType +
        "}";
    }
}
