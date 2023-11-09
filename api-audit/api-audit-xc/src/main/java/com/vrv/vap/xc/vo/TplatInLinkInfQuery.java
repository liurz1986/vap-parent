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
 * 边界平台内部链路
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-20
 */
@ApiModel(value="TplatInLinkInf对象", description="边界平台内部链路")
public class TplatInLinkInfQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "链路标识")
    private String linkId;

    @ApiModelProperty(value = "链路名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String linkName;

    private String platId;

    @ApiModelProperty(value = "接入对象代码")
    private String connectObjectCode;

    @ApiModelProperty(value = "业务操作方式代码")
    private String bizOperatestyleCode;

    @ApiModelProperty(value = "链路带宽,单位M")
    private Integer bandWidth;

    @ApiModelProperty(value = "防火墙品牌型号, 不能为空")
    private String fireWall;

    @ApiModelProperty(value = "可信边界安全网关品牌型号, 不能为空")
    private String tbsg;

    @ApiModelProperty(value = "安全隔离设备品牌型号, 不能为空")
    private String gap;

    @ApiModelProperty(value = "VPN网关品牌型号, 不能为空")
    private String vpn;

    @ApiModelProperty(value = "其它安全措施")
    private String otherSecurity;

    @ApiModelProperty(value = "统计时间")
    private String collectTime;

    private String dt;

    private String province;

    private String policeType;

    @ApiModelProperty(value = "标识当前数据处理动作状态")
    private String status;

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
    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }
    public String getLinkName() {
        return linkName;
    }

    public void setLinkName(String linkName) {
        this.linkName = linkName;
    }
    public String getPlatId() {
        return platId;
    }

    public void setPlatId(String platId) {
        this.platId = platId;
    }
    public String getConnectObjectCode() {
        return connectObjectCode;
    }

    public void setConnectObjectCode(String connectObjectCode) {
        this.connectObjectCode = connectObjectCode;
    }
    public String getBizOperatestyleCode() {
        return bizOperatestyleCode;
    }

    public void setBizOperatestyleCode(String bizOperatestyleCode) {
        this.bizOperatestyleCode = bizOperatestyleCode;
    }
    public Integer getBandWidth() {
        return bandWidth;
    }

    public void setBandWidth(Integer bandWidth) {
        this.bandWidth = bandWidth;
    }
    public String getFireWall() {
        return fireWall;
    }

    public void setFireWall(String fireWall) {
        this.fireWall = fireWall;
    }
    public String getTbsg() {
        return tbsg;
    }

    public void setTbsg(String tbsg) {
        this.tbsg = tbsg;
    }
    public String getGap() {
        return gap;
    }

    public void setGap(String gap) {
        this.gap = gap;
    }
    public String getVpn() {
        return vpn;
    }

    public void setVpn(String vpn) {
        this.vpn = vpn;
    }
    public String getOtherSecurity() {
        return otherSecurity;
    }

    public void setOtherSecurity(String otherSecurity) {
        this.otherSecurity = otherSecurity;
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
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        return "TplatInLinkInfQuery{" +
            "id=" + id +
            ", linkId=" + linkId +
            ", linkName=" + linkName +
            ", platId=" + platId +
            ", connectObjectCode=" + connectObjectCode +
            ", bizOperatestyleCode=" + bizOperatestyleCode +
            ", bandWidth=" + bandWidth +
            ", fireWall=" + fireWall +
            ", tbsg=" + tbsg +
            ", gap=" + gap +
            ", vpn=" + vpn +
            ", otherSecurity=" + otherSecurity +
            ", collectTime=" + collectTime +
            ", dt=" + dt +
            ", province=" + province +
            ", policeType=" + policeType +
            ", status=" + status +
            ", childInclude=" + childInclude +
            ", version=" + version +
            ", childNum=" + childNum +
        "}";
    }
}
