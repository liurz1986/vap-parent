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
 * 项目产品或转包公司信息
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyProjectProdCom对象", description="项目产品或转包公司信息")
public class CompanyProjectProdComQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "项目id")
    private Integer projectId;

    @ApiModelProperty(value = "关联公司id")
    private Integer companyId;

    @ApiModelProperty(value = "0=产品公司,1=转包公司")
    private Integer companyType;

    @ApiModelProperty(value = "企业名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "注册号")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String orgCode;

    @ApiModelProperty(value = "通信地址")
    private String address;

    @ApiModelProperty(value = "电子邮箱")
    private String email;

    @ApiModelProperty(value = "项目参与人数")
    private Integer projectPersonNum;

    @ApiModelProperty(value = "联系人及电话")
    private String tel;

    @ApiModelProperty(value = "是否签订保密协议")
    private String signedSecurity;

    @ApiModelProperty(value = "保密协议扫描件")
    private String securityFile;

    @ApiModelProperty(value = "维保期限")
    private String maintenanceTerm;

    @ApiModelProperty(value = "涉密资质")
    private String securityQualification;

    @ApiModelProperty(value = "简介")
    private String description;

    @ApiModelProperty(value = "备注")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String remark;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getProjectId() {
        return projectId;
    }

    public void setProjectId(Integer projectId) {
        this.projectId = projectId;
    }
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public Integer getCompanyType() {
        return companyType;
    }

    public void setCompanyType(Integer companyType) {
        this.companyType = companyType;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public Integer getProjectPersonNum() {
        return projectPersonNum;
    }

    public void setProjectPersonNum(Integer projectPersonNum) {
        this.projectPersonNum = projectPersonNum;
    }
    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
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
    public String getMaintenanceTerm() {
        return maintenanceTerm;
    }

    public void setMaintenanceTerm(String maintenanceTerm) {
        this.maintenanceTerm = maintenanceTerm;
    }
    public String getSecurityQualification() {
        return securityQualification;
    }

    public void setSecurityQualification(String securityQualification) {
        this.securityQualification = securityQualification;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    @Override
    public String toString() {
        return "CompanyProjectProdCom{" +
            "id=" + id +
            ", projectId=" + projectId +
            ", companyId=" + companyId +
            ", companyType=" + companyType +
            ", name=" + name +
            ", orgCode=" + orgCode +
            ", address=" + address +
            ", email=" + email +
            ", projectPersonNum=" + projectPersonNum +
            ", tel=" + tel +
            ", signedSecurity=" + signedSecurity +
            ", securityFile=" + securityFile +
            ", maintenanceTerm=" + maintenanceTerm +
            ", securityQualification=" + securityQualification +
            ", description=" + description +
            ", remark=" + remark +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
