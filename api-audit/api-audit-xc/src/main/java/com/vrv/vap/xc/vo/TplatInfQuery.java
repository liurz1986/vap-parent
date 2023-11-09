package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 边界平台平台注册信息
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-20
 */
@ApiModel(value="TplatInf对象", description="边界平台平台注册信息")
public class TplatInfQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "接入平台标识")
    private String platId;

    @ApiModelProperty(value = "接入平台名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String platName;

    @ApiModelProperty(value = "物理位置信息")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String address;

    @ApiModelProperty(value = "负责人姓名")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String manager;

    @ApiModelProperty(value = "负责人联系电话")
    private String managerPhone;

    @ApiModelProperty(value = "负责人公安网邮箱地址")
    private String managerMail;

    @ApiModelProperty(value = "负责人其他联系方式")
    private String managerOtherLink;

    @ApiModelProperty(value = "监控系统公安网IP地址")
    private String remoteAccessIp;

    @ApiModelProperty(value = "建设单位代码")
    private String constructUnit;

    @ApiModelProperty(value = "主要承建单位代码")
    private String buildingUnitCode;

    @ApiModelProperty(value = "建设时间")
    private Date buildingTime;

    @ApiModelProperty(value = "是否签署保密协议")
    private Integer signSecrecyProtocol;

    @ApiModelProperty(value = "审批单位")
    private String approveUnit;

    @ApiModelProperty(value = "审批时间")
    private Date approveTime;

    @ApiModelProperty(value = "审批批号")
    private String approveNo;

    @ApiModelProperty(value = "审批材料")
    private String approveMaterial;

    @ApiModelProperty(value = "安全技术方案")
    private String securityProject;

    @ApiModelProperty(value = "保密协议")
    private String secrecyProtocol;

    @ApiModelProperty(value = "运维单位名称")
    private String maintainUnit;

    @ApiModelProperty(value = "管理负责人姓名")
    private String maintainManager;

    @ApiModelProperty(value = "管理负责人联系电话")
    private String maintainManagerPhone;

    @ApiModelProperty(value = "管理负责人公安网邮箱地址")
    private String maintainManagerMail;

    @ApiModelProperty(value = "管理负责人其他联系方式")
    private String maintainManagerLink;

    @ApiModelProperty(value = "平台拓扑图")
    private String platTopo;

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
    public String getPlatName() {
        return platName;
    }

    public void setPlatName(String platName) {
        this.platName = platName;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
    public String getManagerPhone() {
        return managerPhone;
    }

    public void setManagerPhone(String managerPhone) {
        this.managerPhone = managerPhone;
    }
    public String getManagerMail() {
        return managerMail;
    }

    public void setManagerMail(String managerMail) {
        this.managerMail = managerMail;
    }
    public String getManagerOtherLink() {
        return managerOtherLink;
    }

    public void setManagerOtherLink(String managerOtherLink) {
        this.managerOtherLink = managerOtherLink;
    }
    public String getRemoteAccessIp() {
        return remoteAccessIp;
    }

    public void setRemoteAccessIp(String remoteAccessIp) {
        this.remoteAccessIp = remoteAccessIp;
    }
    public String getConstructUnit() {
        return constructUnit;
    }

    public void setConstructUnit(String constructUnit) {
        this.constructUnit = constructUnit;
    }
    public String getBuildingUnitCode() {
        return buildingUnitCode;
    }

    public void setBuildingUnitCode(String buildingUnitCode) {
        this.buildingUnitCode = buildingUnitCode;
    }
    public Date getBuildingTime() {
        return buildingTime;
    }

    public void setBuildingTime(Date buildingTime) {
        this.buildingTime = buildingTime;
    }
    public Integer getSignSecrecyProtocol() {
        return signSecrecyProtocol;
    }

    public void setSignSecrecyProtocol(Integer signSecrecyProtocol) {
        this.signSecrecyProtocol = signSecrecyProtocol;
    }
    public String getApproveUnit() {
        return approveUnit;
    }

    public void setApproveUnit(String approveUnit) {
        this.approveUnit = approveUnit;
    }
    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }
    public String getApproveNo() {
        return approveNo;
    }

    public void setApproveNo(String approveNo) {
        this.approveNo = approveNo;
    }
    public String getApproveMaterial() {
        return approveMaterial;
    }

    public void setApproveMaterial(String approveMaterial) {
        this.approveMaterial = approveMaterial;
    }
    public String getSecurityProject() {
        return securityProject;
    }

    public void setSecurityProject(String securityProject) {
        this.securityProject = securityProject;
    }
    public String getSecrecyProtocol() {
        return secrecyProtocol;
    }

    public void setSecrecyProtocol(String secrecyProtocol) {
        this.secrecyProtocol = secrecyProtocol;
    }
    public String getMaintainUnit() {
        return maintainUnit;
    }

    public void setMaintainUnit(String maintainUnit) {
        this.maintainUnit = maintainUnit;
    }
    public String getMaintainManager() {
        return maintainManager;
    }

    public void setMaintainManager(String maintainManager) {
        this.maintainManager = maintainManager;
    }
    public String getMaintainManagerPhone() {
        return maintainManagerPhone;
    }

    public void setMaintainManagerPhone(String maintainManagerPhone) {
        this.maintainManagerPhone = maintainManagerPhone;
    }
    public String getMaintainManagerMail() {
        return maintainManagerMail;
    }

    public void setMaintainManagerMail(String maintainManagerMail) {
        this.maintainManagerMail = maintainManagerMail;
    }
    public String getMaintainManagerLink() {
        return maintainManagerLink;
    }

    public void setMaintainManagerLink(String maintainManagerLink) {
        this.maintainManagerLink = maintainManagerLink;
    }
    public String getPlatTopo() {
        return platTopo;
    }

    public void setPlatTopo(String platTopo) {
        this.platTopo = platTopo;
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
        return "TplatInfQuery{" +
            "id=" + id +
            ", platId=" + platId +
            ", platName=" + platName +
            ", address=" + address +
            ", manager=" + manager +
            ", managerPhone=" + managerPhone +
            ", managerMail=" + managerMail +
            ", managerOtherLink=" + managerOtherLink +
            ", remoteAccessIp=" + remoteAccessIp +
            ", constructUnit=" + constructUnit +
            ", buildingUnitCode=" + buildingUnitCode +
            ", buildingTime=" + buildingTime +
            ", signSecrecyProtocol=" + signSecrecyProtocol +
            ", approveUnit=" + approveUnit +
            ", approveTime=" + approveTime +
            ", approveNo=" + approveNo +
            ", approveMaterial=" + approveMaterial +
            ", securityProject=" + securityProject +
            ", secrecyProtocol=" + secrecyProtocol +
            ", maintainUnit=" + maintainUnit +
            ", maintainManager=" + maintainManager +
            ", maintainManagerPhone=" + maintainManagerPhone +
            ", maintainManagerMail=" + maintainManagerMail +
            ", maintainManagerLink=" + maintainManagerLink +
            ", platTopo=" + platTopo +
            ", collectTime=" + collectTime +
            ", dt=" + dt +
            ", province=" + province +
            ", policeType=" + policeType +
        "}";
    }
}
