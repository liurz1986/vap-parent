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
 * 边界平台链路设备
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-20
 */
@ApiModel(value="TplatDeviceInf对象", description="边界平台链路设备")
public class TplatDeviceInfQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "平台标识")
    private String platId;

    @ApiModelProperty(value = "内部链路标识")
    private String innerLinkId;

    @ApiModelProperty(value = "设备标识")
    private String deviceId;

    @ApiModelProperty(value = "设备所属区域代码")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String areaCode;

    @ApiModelProperty(value = "设备描述")
    private String deviceDesc;

    @ApiModelProperty(value = "设备类型")
    private String deviceTypeCode;

    @ApiModelProperty(value = "设备IP地址")
    private String deviceIp;

    @ApiModelProperty(value = "设备配置文件名")
    private String deviceConf;

    @ApiModelProperty(value = "生产厂家名称/型号")
    private String brandType;

    @ApiModelProperty(value = "技术支持联系电话")
    private String linkPhone;

    @ApiModelProperty(value = "技术支持其他联系方式")
    private String otherLink;

    @ApiModelProperty(value = "统计时间")
    private String collectTime;

    @ApiModelProperty(value = "时间")
    private String dt;

    @ApiModelProperty(value = "区域代码")
    private String province;

    @ApiModelProperty(value = "警种")
    private String policeType;

    @ApiModelProperty(value = "状态")
    private String currentStatus;

    @ApiModelProperty(value = "是否包含下级平台信息")
    private String childInclude;

    @ApiModelProperty(value = "位置,1:外网，0:内网")
    private String inorout;

    @ApiModelProperty(value = "设备类型")
    private String deviceType;

    @ApiModelProperty(value = "备注")
    private String descr;

    @ApiModelProperty(value = "webservice服务端代码版本号")
    private String version;

    @ApiModelProperty(value = "设备级别")
    private String level;

    @ApiModelProperty(value = "下级平台个数")
    private String childNum;

    @ApiModelProperty(value = "是否在用,0：在用；1：不用")
    private String isLive;

    @ApiModelProperty(value = "负责人")
    private String dutyName;

    @ApiModelProperty(value = "公共设备,1:否，0:是")
    private String isPublic;

    @ApiModelProperty(value = "设备接入区域码")
    private String tbeqpubCodeId;

    @ApiModelProperty(value = "设备子网掩码")
    private String netMask;

    @ApiModelProperty(value = "设备名称")
    private String deviceName;

    @ApiModelProperty(value = "图标设置")
    private String iconName;

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
    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getDeviceDesc() {
        return deviceDesc;
    }

    public void setDeviceDesc(String deviceDesc) {
        this.deviceDesc = deviceDesc;
    }
    public String getDeviceTypeCode() {
        return deviceTypeCode;
    }

    public void setDeviceTypeCode(String deviceTypeCode) {
        this.deviceTypeCode = deviceTypeCode;
    }
    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
    public String getDeviceConf() {
        return deviceConf;
    }

    public void setDeviceConf(String deviceConf) {
        this.deviceConf = deviceConf;
    }
    public String getBrandType() {
        return brandType;
    }

    public void setBrandType(String brandType) {
        this.brandType = brandType;
    }
    public String getLinkPhone() {
        return linkPhone;
    }

    public void setLinkPhone(String linkPhone) {
        this.linkPhone = linkPhone;
    }
    public String getOtherLink() {
        return otherLink;
    }

    public void setOtherLink(String otherLink) {
        this.otherLink = otherLink;
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
    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }
    public String getChildInclude() {
        return childInclude;
    }

    public void setChildInclude(String childInclude) {
        this.childInclude = childInclude;
    }
    public String getInorout() {
        return inorout;
    }

    public void setInorout(String inorout) {
        this.inorout = inorout;
    }
    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }
    public String getDescr() {
        return descr;
    }

    public void setDescr(String descr) {
        this.descr = descr;
    }
    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }
    public String getChildNum() {
        return childNum;
    }

    public void setChildNum(String childNum) {
        this.childNum = childNum;
    }
    public String getIsLive() {
        return isLive;
    }

    public void setIsLive(String isLive) {
        this.isLive = isLive;
    }
    public String getDutyName() {
        return dutyName;
    }

    public void setDutyName(String dutyName) {
        this.dutyName = dutyName;
    }
    public String getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(String isPublic) {
        this.isPublic = isPublic;
    }
    public String getTbeqpubCodeId() {
        return tbeqpubCodeId;
    }

    public void setTbeqpubCodeId(String tbeqpubCodeId) {
        this.tbeqpubCodeId = tbeqpubCodeId;
    }
    public String getNetMask() {
        return netMask;
    }

    public void setNetMask(String netMask) {
        this.netMask = netMask;
    }
    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
    public String getIconName() {
        return iconName;
    }

    public void setIconName(String iconName) {
        this.iconName = iconName;
    }

    @Override
    public String toString() {
        return "TplatDeviceInfQuery{" +
            "id=" + id +
            ", platId=" + platId +
            ", innerLinkId=" + innerLinkId +
            ", deviceId=" + deviceId +
            ", areaCode=" + areaCode +
            ", deviceDesc=" + deviceDesc +
            ", deviceTypeCode=" + deviceTypeCode +
            ", deviceIp=" + deviceIp +
            ", deviceConf=" + deviceConf +
            ", brandType=" + brandType +
            ", linkPhone=" + linkPhone +
            ", otherLink=" + otherLink +
            ", collectTime=" + collectTime +
            ", dt=" + dt +
            ", province=" + province +
            ", policeType=" + policeType +
            ", currentStatus=" + currentStatus +
            ", childInclude=" + childInclude +
            ", inorout=" + inorout +
            ", deviceType=" + deviceType +
            ", descr=" + descr +
            ", version=" + version +
            ", level=" + level +
            ", childNum=" + childNum +
            ", isLive=" + isLive +
            ", dutyName=" + dutyName +
            ", isPublic=" + isPublic +
            ", tbeqpubCodeId=" + tbeqpubCodeId +
            ", netMask=" + netMask +
            ", deviceName=" + deviceName +
            ", iconName=" + iconName +
        "}";
    }
}
