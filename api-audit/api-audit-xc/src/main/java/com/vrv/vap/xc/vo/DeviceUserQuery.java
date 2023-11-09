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
 * @since 2021-05-27
 */
@ApiModel(value="DeviceUser对象", description="")
public class DeviceUserQuery extends Query {

    @ApiModelProperty(value = "设备IP地址")
    private String deviceIp;

    @ApiModelProperty(value = "设备使用日期")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.IGNORE)
    private String yyyymmdd;

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

    public String getDeviceIp() {
        return deviceIp;
    }

    public void setDeviceIp(String deviceIp) {
        this.deviceIp = deviceIp;
    }
    public String getYyyymmdd() {
        return yyyymmdd;
    }

    public void setYyyymmdd(String yyyymmdd) {
        this.yyyymmdd = yyyymmdd;
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

    @Override
    public String toString() {
        return "DeviceUserQuery{" +
            "deviceIp=" + deviceIp +
            ", yyyymmdd=" + yyyymmdd +
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
