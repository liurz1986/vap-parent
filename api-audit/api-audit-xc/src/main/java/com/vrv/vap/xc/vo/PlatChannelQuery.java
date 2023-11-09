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
 * 边界平台管理通道
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-20
 */
@ApiModel(value="PlatChannel对象", description="边界平台管理通道")
public class PlatChannelQuery extends Query {

    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "通道名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "部署位置（地区）")
    private String areaCode;

    @ApiModelProperty(value = "部署位置（详细地址）")
    private String areaDetail;

    @ApiModelProperty(value = "公安网接入点IP（多个用逗号分割）")
    private String apIp;

    @ApiModelProperty(value = "互联网对外服务URL")
    private String externalUrl;

    @ApiModelProperty(value = "接入网络类型")
    private String netType;

    @ApiModelProperty(value = "责任民警所属单位")
    private String policeDept;

    @ApiModelProperty(value = "责任民警所属单位名称")
    private String policeDeptName;

    @ApiModelProperty(value = "责任民警警号")
    private String policeNo;

    @ApiModelProperty(value = "责任民警姓名")
    private String policeName;

    @ApiModelProperty(value = "责任民警手机")
    private String policePhone;

    @ApiModelProperty(value = "责任民警办公电话")
    private String policeTel;

    @ApiModelProperty(value = "承建厂商名称")
    private String companyName;

    @ApiModelProperty(value = "承建厂商责任人")
    private String companyCharge;

    @ApiModelProperty(value = "承建厂商责任人手机")
    private String companyChargePhone;

    @ApiModelProperty(value = "承建厂商责任人办公电话")
    private String companyChargeTel;

    @ApiModelProperty(value = "建设方案文件")
    private String programFile;

    @ApiModelProperty(value = "建设方案文件路径")
    private String programFilePath;

    @ApiModelProperty(value = "建设方案批复文件")
    private String programApproveFile;

    @ApiModelProperty(value = "建设方案批复文件路径")
    private String programApproveFilePath;

    @ApiModelProperty(value = "安全测评报告")
    private String reportFile;

    @ApiModelProperty(value = "安全测评报告路径")
    private String reportFilePath;

    @ApiModelProperty(value = "通道部署拓扑图")
    private String gplotFile;

    @ApiModelProperty(value = "通道部署拓扑图文件路径")
    private String gplotFilePath;

    @ApiModelProperty(value = "填报时间")
    private Date fillingTime;

    @ApiModelProperty(value = "审批时间")
    private Date approveTime;

    @ApiModelProperty(value = "创建用户")
    private String creator;

    @ApiModelProperty(value = "审批意见")
    private String opinion;

    @ApiModelProperty(value = "状态(0=填报中，1=审批通过，2=审批不通过)")
    private Integer status;

    @ApiModelProperty(value = "运营商信息")
    private String operatorInformation;

    @ApiModelProperty(value = "通道状态（0-在建、1-使用中、2-暂停）")
    private Integer channelStatus;

    @ApiModelProperty(value = "填报人所在机构编码")
    private String orgCode;

    @ApiModelProperty(value = "填报人所在机构系统编码（用于级联查询）")
    private String subCode;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getAreaDetail() {
        return areaDetail;
    }

    public void setAreaDetail(String areaDetail) {
        this.areaDetail = areaDetail;
    }
    public String getApIp() {
        return apIp;
    }

    public void setApIp(String apIp) {
        this.apIp = apIp;
    }
    public String getExternalUrl() {
        return externalUrl;
    }

    public void setExternalUrl(String externalUrl) {
        this.externalUrl = externalUrl;
    }
    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }
    public String getPoliceDept() {
        return policeDept;
    }

    public void setPoliceDept(String policeDept) {
        this.policeDept = policeDept;
    }
    public String getPoliceDeptName() {
        return policeDeptName;
    }

    public void setPoliceDeptName(String policeDeptName) {
        this.policeDeptName = policeDeptName;
    }
    public String getPoliceNo() {
        return policeNo;
    }

    public void setPoliceNo(String policeNo) {
        this.policeNo = policeNo;
    }
    public String getPoliceName() {
        return policeName;
    }

    public void setPoliceName(String policeName) {
        this.policeName = policeName;
    }
    public String getPolicePhone() {
        return policePhone;
    }

    public void setPolicePhone(String policePhone) {
        this.policePhone = policePhone;
    }
    public String getPoliceTel() {
        return policeTel;
    }

    public void setPoliceTel(String policeTel) {
        this.policeTel = policeTel;
    }
    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public String getCompanyCharge() {
        return companyCharge;
    }

    public void setCompanyCharge(String companyCharge) {
        this.companyCharge = companyCharge;
    }
    public String getCompanyChargePhone() {
        return companyChargePhone;
    }

    public void setCompanyChargePhone(String companyChargePhone) {
        this.companyChargePhone = companyChargePhone;
    }
    public String getCompanyChargeTel() {
        return companyChargeTel;
    }

    public void setCompanyChargeTel(String companyChargeTel) {
        this.companyChargeTel = companyChargeTel;
    }
    public String getProgramFile() {
        return programFile;
    }

    public void setProgramFile(String programFile) {
        this.programFile = programFile;
    }
    public String getProgramFilePath() {
        return programFilePath;
    }

    public void setProgramFilePath(String programFilePath) {
        this.programFilePath = programFilePath;
    }
    public String getProgramApproveFile() {
        return programApproveFile;
    }

    public void setProgramApproveFile(String programApproveFile) {
        this.programApproveFile = programApproveFile;
    }
    public String getProgramApproveFilePath() {
        return programApproveFilePath;
    }

    public void setProgramApproveFilePath(String programApproveFilePath) {
        this.programApproveFilePath = programApproveFilePath;
    }
    public String getReportFile() {
        return reportFile;
    }

    public void setReportFile(String reportFile) {
        this.reportFile = reportFile;
    }
    public String getReportFilePath() {
        return reportFilePath;
    }

    public void setReportFilePath(String reportFilePath) {
        this.reportFilePath = reportFilePath;
    }
    public String getGplotFile() {
        return gplotFile;
    }

    public void setGplotFile(String gplotFile) {
        this.gplotFile = gplotFile;
    }
    public String getGplotFilePath() {
        return gplotFilePath;
    }

    public void setGplotFilePath(String gplotFilePath) {
        this.gplotFilePath = gplotFilePath;
    }
    public Date getFillingTime() {
        return fillingTime;
    }

    public void setFillingTime(Date fillingTime) {
        this.fillingTime = fillingTime;
    }
    public Date getApproveTime() {
        return approveTime;
    }

    public void setApproveTime(Date approveTime) {
        this.approveTime = approveTime;
    }
    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }
    public String getOpinion() {
        return opinion;
    }

    public void setOpinion(String opinion) {
        this.opinion = opinion;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
    public String getOperatorInformation() {
        return operatorInformation;
    }

    public void setOperatorInformation(String operatorInformation) {
        this.operatorInformation = operatorInformation;
    }
    public Integer getChannelStatus() {
        return channelStatus;
    }

    public void setChannelStatus(Integer channelStatus) {
        this.channelStatus = channelStatus;
    }
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    @Override
    public String toString() {
        return "PlatChannelQuery{" +
            "id=" + id +
            ", name=" + name +
            ", areaCode=" + areaCode +
            ", areaDetail=" + areaDetail +
            ", apIp=" + apIp +
            ", externalUrl=" + externalUrl +
            ", netType=" + netType +
            ", policeDept=" + policeDept +
            ", policeDeptName=" + policeDeptName +
            ", policeNo=" + policeNo +
            ", policeName=" + policeName +
            ", policePhone=" + policePhone +
            ", policeTel=" + policeTel +
            ", companyName=" + companyName +
            ", companyCharge=" + companyCharge +
            ", companyChargePhone=" + companyChargePhone +
            ", companyChargeTel=" + companyChargeTel +
            ", programFile=" + programFile +
            ", programFilePath=" + programFilePath +
            ", programApproveFile=" + programApproveFile +
            ", programApproveFilePath=" + programApproveFilePath +
            ", reportFile=" + reportFile +
            ", reportFilePath=" + reportFilePath +
            ", gplotFile=" + gplotFile +
            ", gplotFilePath=" + gplotFilePath +
            ", fillingTime=" + fillingTime +
            ", approveTime=" + approveTime +
            ", creator=" + creator +
            ", opinion=" + opinion +
            ", status=" + status +
            ", operatorInformation=" + operatorInformation +
            ", channelStatus=" + channelStatus +
            ", orgCode=" + orgCode +
            ", subCode=" + subCode +
        "}";
    }
}
