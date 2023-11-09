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
 * 边界平台管理业务
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-20
 */
@ApiModel(value="PlatBusiness对象", description="边界平台管理业务")
public class PlatBusinessQuery extends Query {

    @ApiModelProperty(value = "自增主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "业务名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "主要功能描述")
    private String businessDesc;

    @ApiModelProperty(value = "关联通道")
    private Integer channelId;

    @ApiModelProperty(value = "地区")
    private String areaCode;

    @ApiModelProperty(value = "接入业务主管单位")
    private String company;

    @ApiModelProperty(value = "责任民警姓名")
    private String policeName;

    @ApiModelProperty(value = "责任民警警号")
    private String policeNo;

    @ApiModelProperty(value = "责任民警手机号")
    private String policePhone;

    @ApiModelProperty(value = "责任民警办公电话")
    private String policeTel;

    @ApiModelProperty(value = "接入业务承载网络")
    private String netType;

    @ApiModelProperty(value = "数据交互方向")
    private String direction;

    @ApiModelProperty(value = "业务操作方式")
    private String operation;

    @ApiModelProperty(value = "是否实时交互")
    private Integer isRealTime;

    @ApiModelProperty(value = "是否接入业务审批")
    private Integer businessApprove;

    @ApiModelProperty(value = "业务审批文件")
    private String businessApproveFile;

    @ApiModelProperty(value = "业务审批文件路径")
    private String businessApproveFilePath;

    @ApiModelProperty(value = "填报时间")
    private Date fillingTime;

    @ApiModelProperty(value = "审批时间")
    private Date approveTime;

    @ApiModelProperty(value = "填报用户")
    private String creator;

    @ApiModelProperty(value = "审批意见")
    private String opinion;

    @ApiModelProperty(value = "状态(0=填报中，1=审批通过，2=审批不通过)")
    private Integer status;

    @ApiModelProperty(value = "开发单位")
    private String developmentUnit;

    @ApiModelProperty(value = "最高流量申请")
    private String maxFlow;

    @ApiModelProperty(value = "业务使用源ip，多个逗号分隔")
    private String srcIp;

    @ApiModelProperty(value = "业务使用源端口，多个逗号分隔")
    private String srcPort;

    @ApiModelProperty(value = "业务使用协议，多个逗号分隔")
    private String protocol;

    @ApiModelProperty(value = "业务使用目标ip，多个逗号分隔")
    private String dstIp;

    @ApiModelProperty(value = "业务使用目标端口，多个逗号分隔")
    private String dstPort;

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
    public String getBusinessDesc() {
        return businessDesc;
    }

    public void setBusinessDesc(String businessDesc) {
        this.businessDesc = businessDesc;
    }
    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }
    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }
    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
    public String getPoliceName() {
        return policeName;
    }

    public void setPoliceName(String policeName) {
        this.policeName = policeName;
    }
    public String getPoliceNo() {
        return policeNo;
    }

    public void setPoliceNo(String policeNo) {
        this.policeNo = policeNo;
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
    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }
    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
    public Integer getIsRealTime() {
        return isRealTime;
    }

    public void setIsRealTime(Integer isRealTime) {
        this.isRealTime = isRealTime;
    }
    public Integer getBusinessApprove() {
        return businessApprove;
    }

    public void setBusinessApprove(Integer businessApprove) {
        this.businessApprove = businessApprove;
    }
    public String getBusinessApproveFile() {
        return businessApproveFile;
    }

    public void setBusinessApproveFile(String businessApproveFile) {
        this.businessApproveFile = businessApproveFile;
    }
    public String getBusinessApproveFilePath() {
        return businessApproveFilePath;
    }

    public void setBusinessApproveFilePath(String businessApproveFilePath) {
        this.businessApproveFilePath = businessApproveFilePath;
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
    public String getDevelopmentUnit() {
        return developmentUnit;
    }

    public void setDevelopmentUnit(String developmentUnit) {
        this.developmentUnit = developmentUnit;
    }
    public String getMaxFlow() {
        return maxFlow;
    }

    public void setMaxFlow(String maxFlow) {
        this.maxFlow = maxFlow;
    }
    public String getSrcIp() {
        return srcIp;
    }

    public void setSrcIp(String srcIp) {
        this.srcIp = srcIp;
    }
    public String getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(String srcPort) {
        this.srcPort = srcPort;
    }
    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }
    public String getDstIp() {
        return dstIp;
    }

    public void setDstIp(String dstIp) {
        this.dstIp = dstIp;
    }
    public String getDstPort() {
        return dstPort;
    }

    public void setDstPort(String dstPort) {
        this.dstPort = dstPort;
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
        return "PlatBusinessQuery{" +
            "id=" + id +
            ", name=" + name +
            ", businessDesc=" + businessDesc +
            ", channelId=" + channelId +
            ", areaCode=" + areaCode +
            ", company=" + company +
            ", policeName=" + policeName +
            ", policeNo=" + policeNo +
            ", policePhone=" + policePhone +
            ", policeTel=" + policeTel +
            ", netType=" + netType +
            ", direction=" + direction +
            ", operation=" + operation +
            ", isRealTime=" + isRealTime +
            ", businessApprove=" + businessApprove +
            ", businessApproveFile=" + businessApproveFile +
            ", businessApproveFilePath=" + businessApproveFilePath +
            ", fillingTime=" + fillingTime +
            ", approveTime=" + approveTime +
            ", creator=" + creator +
            ", opinion=" + opinion +
            ", status=" + status +
            ", developmentUnit=" + developmentUnit +
            ", maxFlow=" + maxFlow +
            ", srcIp=" + srcIp +
            ", srcPort=" + srcPort +
            ", protocol=" + protocol +
            ", dstIp=" + dstIp +
            ", dstPort=" + dstPort +
            ", orgCode=" + orgCode +
            ", subCode=" + subCode +
        "}";
    }
}
