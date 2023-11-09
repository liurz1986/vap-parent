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
 * 企业项目
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyProject对象", description="企业项目")
public class CompanyProjectQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "关联公司id")
    private Integer companyId;

    @ApiModelProperty(value = "合同名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String contractName;

    @ApiModelProperty(value = "资金来源")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String capitalSource;

    @ApiModelProperty(value = "签订日期")
    private String signDate;

    @ApiModelProperty(value = "合同资金")
    private Double capital;

    @ApiModelProperty(value = "竣工日期")
    private String completeDate;

    @ApiModelProperty(value = "验收情况")
    private String acceptance;

    @ApiModelProperty(value = "已支付资金")
    private Double capitalPayed;

    @ApiModelProperty(value = "支付比例")
    private Double percentPayed;

    @ApiModelProperty(value = "维保期限")
    private String maintenanceTerm;

    @ApiModelProperty(value = "驻场人数")
    private Integer residentNum;

    @ApiModelProperty(value = "公安责任单位")
    private String responsibleOrg;

    @ApiModelProperty(value = "项目责任民警")
    private String responsiblePoliceName;

    @ApiModelProperty(value = "责任民警电话")
    private String responsiblePoliceTel;

    @ApiModelProperty(value = "责任民警警号")
    private String responsiblePoliceCode;

    @ApiModelProperty(value = "责任民警身份证号")
    private String responsiblePoliceIdcard;

    @ApiModelProperty(value = "项目类型")
    private String projectType;

    @ApiModelProperty(value = "项目合同扫描件")
    private String projectFile;

    @ApiModelProperty(value = "培训要求")
    private String trainingRequirement;

    @ApiModelProperty(value = "任务内容")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String taskContent;

    @ApiModelProperty(value = "项目进展")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String projectProcess;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否有第三方产品")
    private String existThirdPro;

    @ApiModelProperty(value = "是否存在项目外包")
    private String existSubCom;

    @ApiModelProperty(value = "转包类型")
    private String existSubType;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "操作人所在机构")
    private String operatorOrg;

    @ApiModelProperty(value = "录入人员所属角色")
    private String operatorRole;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    @ApiModelProperty(value = "提交状态 0=未提交 1=已提交")
    private Integer status;

    @ApiModelProperty(value = "信息系统名称")
    private String sysName1;

    @ApiModelProperty(value = "系统URL")
    private String sysUrl1;

    @ApiModelProperty(value = "部署网络")
    private String sysNetType1;

    @ApiModelProperty(value = "信息系统名称2")
    private String sysName2;

    @ApiModelProperty(value = "系统URL2")
    private String sysUrl2;

    @ApiModelProperty(value = "部署网络2")
    private String sysNetType2;

    @ApiModelProperty(value = "信息系统名称3")
    private String sysName3;

    @ApiModelProperty(value = "系统URL3")
    private String sysUrl3;

    @ApiModelProperty(value = "部署网络3")
    private String sysNetType3;

    @ApiModelProperty(value = "信息系统名称4")
    private String sysName4;

    @ApiModelProperty(value = "系统URL4")
    private String sysUrl4;

    @ApiModelProperty(value = "部署网络4")
    private String sysNetType4;

    @ApiModelProperty(value = "信息系统名称5")
    private String sysName5;

    @ApiModelProperty(value = "系统URL5")
    private String sysUrl5;

    @ApiModelProperty(value = "部署网络5")
    private String sysNetType5;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }
    public String getContractName() {
        return contractName;
    }

    public void setContractName(String contractName) {
        this.contractName = contractName;
    }
    public String getCapitalSource() {
        return capitalSource;
    }

    public void setCapitalSource(String capitalSource) {
        this.capitalSource = capitalSource;
    }
    public String getSignDate() {
        return signDate;
    }

    public void setSignDate(String signDate) {
        this.signDate = signDate;
    }
    public Double getCapital() {
        return capital;
    }

    public void setCapital(Double capital) {
        this.capital = capital;
    }
    public String getCompleteDate() {
        return completeDate;
    }

    public void setCompleteDate(String completeDate) {
        this.completeDate = completeDate;
    }
    public String getAcceptance() {
        return acceptance;
    }

    public void setAcceptance(String acceptance) {
        this.acceptance = acceptance;
    }
    public Double getCapitalPayed() {
        return capitalPayed;
    }

    public void setCapitalPayed(Double capitalPayed) {
        this.capitalPayed = capitalPayed;
    }
    public Double getPercentPayed() {
        return percentPayed;
    }

    public void setPercentPayed(Double percentPayed) {
        this.percentPayed = percentPayed;
    }
    public String getMaintenanceTerm() {
        return maintenanceTerm;
    }

    public void setMaintenanceTerm(String maintenanceTerm) {
        this.maintenanceTerm = maintenanceTerm;
    }
    public Integer getResidentNum() {
        return residentNum;
    }

    public void setResidentNum(Integer residentNum) {
        this.residentNum = residentNum;
    }
    public String getResponsibleOrg() {
        return responsibleOrg;
    }

    public void setResponsibleOrg(String responsibleOrg) {
        this.responsibleOrg = responsibleOrg;
    }
    public String getResponsiblePoliceName() {
        return responsiblePoliceName;
    }

    public void setResponsiblePoliceName(String responsiblePoliceName) {
        this.responsiblePoliceName = responsiblePoliceName;
    }
    public String getResponsiblePoliceTel() {
        return responsiblePoliceTel;
    }

    public void setResponsiblePoliceTel(String responsiblePoliceTel) {
        this.responsiblePoliceTel = responsiblePoliceTel;
    }
    public String getResponsiblePoliceCode() {
        return responsiblePoliceCode;
    }

    public void setResponsiblePoliceCode(String responsiblePoliceCode) {
        this.responsiblePoliceCode = responsiblePoliceCode;
    }
    public String getResponsiblePoliceIdcard() {
        return responsiblePoliceIdcard;
    }

    public void setResponsiblePoliceIdcard(String responsiblePoliceIdcard) {
        this.responsiblePoliceIdcard = responsiblePoliceIdcard;
    }
    public String getProjectType() {
        return projectType;
    }

    public void setProjectType(String projectType) {
        this.projectType = projectType;
    }
    public String getProjectFile() {
        return projectFile;
    }

    public void setProjectFile(String projectFile) {
        this.projectFile = projectFile;
    }
    public String getTrainingRequirement() {
        return trainingRequirement;
    }

    public void setTrainingRequirement(String trainingRequirement) {
        this.trainingRequirement = trainingRequirement;
    }
    public String getTaskContent() {
        return taskContent;
    }

    public void setTaskContent(String taskContent) {
        this.taskContent = taskContent;
    }
    public String getProjectProcess() {
        return projectProcess;
    }

    public void setProjectProcess(String projectProcess) {
        this.projectProcess = projectProcess;
    }
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
    public String getExistThirdPro() {
        return existThirdPro;
    }

    public void setExistThirdPro(String existThirdPro) {
        this.existThirdPro = existThirdPro;
    }
    public String getExistSubCom() {
        return existSubCom;
    }

    public void setExistSubCom(String existSubCom) {
        this.existSubCom = existSubCom;
    }
    public String getExistSubType() {
        return existSubType;
    }

    public void setExistSubType(String existSubType) {
        this.existSubType = existSubType;
    }
    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }
    public String getOperatorOrg() {
        return operatorOrg;
    }

    public void setOperatorOrg(String operatorOrg) {
        this.operatorOrg = operatorOrg;
    }
    public String getOperatorRole() {
        return operatorRole;
    }

    public void setOperatorRole(String operatorRole) {
        this.operatorRole = operatorRole;
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
    public String getSysName1() {
        return sysName1;
    }

    public void setSysName1(String sysName1) {
        this.sysName1 = sysName1;
    }
    public String getSysUrl1() {
        return sysUrl1;
    }

    public void setSysUrl1(String sysUrl1) {
        this.sysUrl1 = sysUrl1;
    }
    public String getSysNetType1() {
        return sysNetType1;
    }

    public void setSysNetType1(String sysNetType1) {
        this.sysNetType1 = sysNetType1;
    }
    public String getSysName2() {
        return sysName2;
    }

    public void setSysName2(String sysName2) {
        this.sysName2 = sysName2;
    }
    public String getSysUrl2() {
        return sysUrl2;
    }

    public void setSysUrl2(String sysUrl2) {
        this.sysUrl2 = sysUrl2;
    }
    public String getSysNetType2() {
        return sysNetType2;
    }

    public void setSysNetType2(String sysNetType2) {
        this.sysNetType2 = sysNetType2;
    }
    public String getSysName3() {
        return sysName3;
    }

    public void setSysName3(String sysName3) {
        this.sysName3 = sysName3;
    }
    public String getSysUrl3() {
        return sysUrl3;
    }

    public void setSysUrl3(String sysUrl3) {
        this.sysUrl3 = sysUrl3;
    }
    public String getSysNetType3() {
        return sysNetType3;
    }

    public void setSysNetType3(String sysNetType3) {
        this.sysNetType3 = sysNetType3;
    }
    public String getSysName4() {
        return sysName4;
    }

    public void setSysName4(String sysName4) {
        this.sysName4 = sysName4;
    }
    public String getSysUrl4() {
        return sysUrl4;
    }

    public void setSysUrl4(String sysUrl4) {
        this.sysUrl4 = sysUrl4;
    }
    public String getSysNetType4() {
        return sysNetType4;
    }

    public void setSysNetType4(String sysNetType4) {
        this.sysNetType4 = sysNetType4;
    }
    public String getSysName5() {
        return sysName5;
    }

    public void setSysName5(String sysName5) {
        this.sysName5 = sysName5;
    }
    public String getSysUrl5() {
        return sysUrl5;
    }

    public void setSysUrl5(String sysUrl5) {
        this.sysUrl5 = sysUrl5;
    }
    public String getSysNetType5() {
        return sysNetType5;
    }

    public void setSysNetType5(String sysNetType5) {
        this.sysNetType5 = sysNetType5;
    }

    @Override
    public String toString() {
        return "CompanyProject{" +
            "id=" + id +
            ", companyId=" + companyId +
            ", contractName=" + contractName +
            ", capitalSource=" + capitalSource +
            ", signDate=" + signDate +
            ", capital=" + capital +
            ", completeDate=" + completeDate +
            ", acceptance=" + acceptance +
            ", capitalPayed=" + capitalPayed +
            ", percentPayed=" + percentPayed +
            ", maintenanceTerm=" + maintenanceTerm +
            ", residentNum=" + residentNum +
            ", responsibleOrg=" + responsibleOrg +
            ", responsiblePoliceName=" + responsiblePoliceName +
            ", responsiblePoliceTel=" + responsiblePoliceTel +
            ", responsiblePoliceCode=" + responsiblePoliceCode +
            ", responsiblePoliceIdcard=" + responsiblePoliceIdcard +
            ", projectType=" + projectType +
            ", projectFile=" + projectFile +
            ", trainingRequirement=" + trainingRequirement +
            ", taskContent=" + taskContent +
            ", projectProcess=" + projectProcess +
            ", remark=" + remark +
            ", existThirdPro=" + existThirdPro +
            ", existSubCom=" + existSubCom +
            ", existSubType=" + existSubType +
            ", operator=" + operator +
            ", operatorOrg=" + operatorOrg +
            ", operatorRole=" + operatorRole +
            ", operateTime=" + operateTime +
            ", status=" + status +
            ", sysName1=" + sysName1 +
            ", sysUrl1=" + sysUrl1 +
            ", sysNetType1=" + sysNetType1 +
            ", sysName2=" + sysName2 +
            ", sysUrl2=" + sysUrl2 +
            ", sysNetType2=" + sysNetType2 +
            ", sysName3=" + sysName3 +
            ", sysUrl3=" + sysUrl3 +
            ", sysNetType3=" + sysNetType3 +
            ", sysName4=" + sysName4 +
            ", sysUrl4=" + sysUrl4 +
            ", sysNetType4=" + sysNetType4 +
            ", sysName5=" + sysName5 +
            ", sysUrl5=" + sysUrl5 +
            ", sysNetType5=" + sysNetType5 +
        "}";
    }
}
