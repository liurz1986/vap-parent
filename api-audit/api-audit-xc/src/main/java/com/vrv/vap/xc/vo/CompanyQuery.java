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
 * 企业
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="Company对象", description="企业")
public class CompanyQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "企业名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "联系电话")
    private String tel;

    @ApiModelProperty(value = "注册号")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String orgCode;

    @ApiModelProperty(value = "资质列表")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String securityQualification;

    @ApiModelProperty(value = "通信地址")
    private String address;

    @ApiModelProperty(value = "简介")
    private String description;

    @ApiModelProperty(value = "注册资金")
    private String registeredFund;

    @ApiModelProperty(value = "成立时间")
    private String foundTime;

    @ApiModelProperty(value = "是否签订保密协议")
    private String signedSecurity;

    @ApiModelProperty(value = "保密协议扫描件")
    private String securityFile;

    @ApiModelProperty(value = "备注")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String remark;

    @ApiModelProperty(value = "联系人")
    private String contacts;

    @ApiModelProperty(value = "与公安机关累计合作年限")
    private String cooperationDuration;

    @ApiModelProperty(value = "是否参与涉密项目")
    private String inSecurityProject;

    @ApiModelProperty(value = "目前是否具有涉密资质")
    private String hasSecurityQualification;

    @ApiModelProperty(value = "三年内存在不良记录情况")
    private String badnessRecord;

    @ApiModelProperty(value = "不良记录描述")
    private String badnessRecordDesc;

    @ApiModelProperty(value = "合作事项")
    private String coCase;

    @ApiModelProperty(value = "合作警种")
    private String coPolice;

    @ApiModelProperty(value = "合作类型")
    private String coType;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "录入人员所属角色")
    private String operatorRole;

    @ApiModelProperty(value = "操作人所在机构")
    private String operatorOrg;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    @ApiModelProperty(value = "提交状态 0=未提交 1=已提交")
    private Integer status;

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
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public String getSecurityQualification() {
        return securityQualification;
    }

    public void setSecurityQualification(String securityQualification) {
        this.securityQualification = securityQualification;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getRegisteredFund() {
        return registeredFund;
    }

    public void setRegisteredFund(String registeredFund) {
        this.registeredFund = registeredFund;
    }
    public String getFoundTime() {
        return foundTime;
    }

    public void setFoundTime(String foundTime) {
        this.foundTime = foundTime;
    }
    public String getSignedSecurity() {
        return signedSecurity;
    }

    public void setSignedSecurity(String signedSecurity) {
        this.signedSecurity = signedSecurity;
    }
    public String getSecurityFile() {
        return securityFile;
    }

    public void setSecurityFile(String securityFile) {
        this.securityFile = securityFile;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getContacts() {
        return contacts;
    }

    public void setContacts(String contacts) {
        this.contacts = contacts;
    }
    public String getCooperationDuration() {
        return cooperationDuration;
    }

    public void setCooperationDuration(String cooperationDuration) {
        this.cooperationDuration = cooperationDuration;
    }
    public String getInSecurityProject() {
        return inSecurityProject;
    }

    public void setInSecurityProject(String inSecurityProject) {
        this.inSecurityProject = inSecurityProject;
    }
    public String getHasSecurityQualification() {
        return hasSecurityQualification;
    }

    public void setHasSecurityQualification(String hasSecurityQualification) {
        this.hasSecurityQualification = hasSecurityQualification;
    }
    public String getBadnessRecord() {
        return badnessRecord;
    }

    public void setBadnessRecord(String badnessRecord) {
        this.badnessRecord = badnessRecord;
    }
    public String getBadnessRecordDesc() {
        return badnessRecordDesc;
    }

    public void setBadnessRecordDesc(String badnessRecordDesc) {
        this.badnessRecordDesc = badnessRecordDesc;
    }
    public String getCoCase() {
        return coCase;
    }

    public void setCoCase(String coCase) {
        this.coCase = coCase;
    }
    public String getCoPolice() {
        return coPolice;
    }

    public void setCoPolice(String coPolice) {
        this.coPolice = coPolice;
    }
    public String getCoType() {
        return coType;
    }

    public void setCoType(String coType) {
        this.coType = coType;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public String getOperatorRole() {
        return operatorRole;
    }

    public void setOperatorRole(String operatorRole) {
        this.operatorRole = operatorRole;
    }
    public String getOperatorOrg() {
        return operatorOrg;
    }

    public void setOperatorOrg(String operatorOrg) {
        this.operatorOrg = operatorOrg;
    }
    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }
    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "CompanyQuery{" +
            "id=" + id +
            ", name=" + name +
            ", tel=" + tel +
            ", orgCode=" + orgCode +
            ", securityQualification=" + securityQualification +
            ", address=" + address +
            ", description=" + description +
            ", registeredFund=" + registeredFund +
            ", foundTime=" + foundTime +
            ", signedSecurity=" + signedSecurity +
            ", securityFile=" + securityFile +
            ", remark=" + remark +
            ", contacts=" + contacts +
            ", cooperationDuration=" + cooperationDuration +
            ", inSecurityProject=" + inSecurityProject +
            ", hasSecurityQualification=" + hasSecurityQualification +
            ", badnessRecord=" + badnessRecord +
            ", badnessRecordDesc=" + badnessRecordDesc +
            ", coCase=" + coCase +
            ", coPolice=" + coPolice +
            ", coType=" + coType +
            ", operator=" + operator +
            ", operatorRole=" + operatorRole +
            ", operatorOrg=" + operatorOrg +
            ", operateTime=" + operateTime +
            ", status=" + status +
        "}";
    }
}
