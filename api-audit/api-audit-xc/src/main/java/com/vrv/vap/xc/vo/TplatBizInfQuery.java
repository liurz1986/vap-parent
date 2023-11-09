package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 边界平台应用信息
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-20
 */
@ApiModel(value="TplatBizInf对象", description="边界平台应用信息")
public class TplatBizInfQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "平台标识")
    private String platId;

    @ApiModelProperty(value = "内部链路标识")
    private String innerLinkId;

    @ApiModelProperty(value = "应用标识")
    private String bizId;

    @ApiModelProperty(value = "业务主管部门名称")
    private String bizManageDepart;

    @ApiModelProperty(value = "业务应用系统名称")
    private String bizName;

    @ApiModelProperty(value = "业务类型代码")
    private String bizTypeCode;

    @ApiModelProperty(value = "业务操作方式代码")
    private String bizOperateStylecode;

    @ApiModelProperty(value = "业务主管部门主管人姓名")
    private String manageDepartManager;

    @ApiModelProperty(value = "业务主管部门主管人联系电话")
    private String managerPhone;

    @ApiModelProperty(value = "业务主管部门主管人公安网E-mail")
    private String managerMail;

    @ApiModelProperty(value = "业务主管部门主管人其他联系方式")
    private String managerOtherLink;

    @ApiModelProperty(value = "审批部门名称")
    private String approveUnit;

    @ApiModelProperty(value = "审批时间")
    private Date approveTime;

    @ApiModelProperty(value = "审批批号")
    private String approveNo;

    @ApiModelProperty(value = "审批材料")
    private String approveMaterial;

    @ApiModelProperty(value = "注册时间")
    private Date registerTime;

    @ApiModelProperty(value = "数据交换方向:1代表“单入”;2代表“单出”;3代表“双向”")
    private Integer dataExchangDirect;

    @ApiModelProperty(value = "基本协议代码")
    private String baseProcode;

    @ApiModelProperty(value = "是否有实时性要求:是1否0")
    private Integer realTime;

    @ApiModelProperty(value = "数据估算日交换量")
    private String dataExchangDataflux;

    @ApiModelProperty(value = "是否已备案:1是0否")
    private Integer isBackup;

    @ApiModelProperty(value = "备案单位名称")
    private String backupUnit;

    @ApiModelProperty(value = "实际运行拓扑图")
    private String topo;

    @ApiModelProperty(value = "统计时间")
    private String collectTime;

    @ApiModelProperty(value = "时间")
    private String dt;

    @ApiModelProperty(value = "区域代码")
    private String province;

    @ApiModelProperty(value = "警种")
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
    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }
    public String getBizManageDepart() {
        return bizManageDepart;
    }

    public void setBizManageDepart(String bizManageDepart) {
        this.bizManageDepart = bizManageDepart;
    }
    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }
    public String getBizTypeCode() {
        return bizTypeCode;
    }

    public void setBizTypeCode(String bizTypeCode) {
        this.bizTypeCode = bizTypeCode;
    }
    public String getBizOperateStylecode() {
        return bizOperateStylecode;
    }

    public void setBizOperateStylecode(String bizOperateStylecode) {
        this.bizOperateStylecode = bizOperateStylecode;
    }
    public String getManageDepartManager() {
        return manageDepartManager;
    }

    public void setManageDepartManager(String manageDepartManager) {
        this.manageDepartManager = manageDepartManager;
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
    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }
    public Integer getDataExchangDirect() {
        return dataExchangDirect;
    }

    public void setDataExchangDirect(Integer dataExchangDirect) {
        this.dataExchangDirect = dataExchangDirect;
    }
    public String getBaseProcode() {
        return baseProcode;
    }

    public void setBaseProcode(String baseProcode) {
        this.baseProcode = baseProcode;
    }
    public Integer getRealTime() {
        return realTime;
    }

    public void setRealTime(Integer realTime) {
        this.realTime = realTime;
    }
    public String getDataExchangDataflux() {
        return dataExchangDataflux;
    }

    public void setDataExchangDataflux(String dataExchangDataflux) {
        this.dataExchangDataflux = dataExchangDataflux;
    }
    public Integer getIsBackup() {
        return isBackup;
    }

    public void setIsBackup(Integer isBackup) {
        this.isBackup = isBackup;
    }
    public String getBackupUnit() {
        return backupUnit;
    }

    public void setBackupUnit(String backupUnit) {
        this.backupUnit = backupUnit;
    }
    public String getTopo() {
        return topo;
    }

    public void setTopo(String topo) {
        this.topo = topo;
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
        return "TplatBizInfQuery{" +
            "id=" + id +
            ", platId=" + platId +
            ", innerLinkId=" + innerLinkId +
            ", bizId=" + bizId +
            ", bizManageDepart=" + bizManageDepart +
            ", bizName=" + bizName +
            ", bizTypeCode=" + bizTypeCode +
            ", bizOperateStylecode=" + bizOperateStylecode +
            ", manageDepartManager=" + manageDepartManager +
            ", managerPhone=" + managerPhone +
            ", managerMail=" + managerMail +
            ", managerOtherLink=" + managerOtherLink +
            ", approveUnit=" + approveUnit +
            ", approveTime=" + approveTime +
            ", approveNo=" + approveNo +
            ", approveMaterial=" + approveMaterial +
            ", registerTime=" + registerTime +
            ", dataExchangDirect=" + dataExchangDirect +
            ", baseProcode=" + baseProcode +
            ", realTime=" + realTime +
            ", dataExchangDataflux=" + dataExchangDataflux +
            ", isBackup=" + isBackup +
            ", backupUnit=" + backupUnit +
            ", topo=" + topo +
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
