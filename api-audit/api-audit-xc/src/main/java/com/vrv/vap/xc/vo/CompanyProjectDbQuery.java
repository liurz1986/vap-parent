package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;

/**
 * <p>
 * 企业数据库建设情况
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyProjectDb对象", description="企业数据库建设情况")
public class CompanyProjectDbQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "项目id")
    private Integer projectId;

    @ApiModelProperty(value = "关联公司id")
    private Integer companyId;

    @ApiModelProperty(value = "数据库名称")
    private String dbName;

    @ApiModelProperty(value = "数据库类型")
    private String dbType;

    @ApiModelProperty(value = "数据库存储类型")
    private String dbStorageType;

    @ApiModelProperty(value = "数据种类")
    private String dbContent;

    @ApiModelProperty(value = "数据库对接方式")
    private String dbAccessType;

    @ApiModelProperty(value = "数据库备份方式")
    private String dbBackupType;

    @ApiModelProperty(value = "运维民警")
    private String maintainPolice;

    @ApiModelProperty(value = "运维技术人员")
    private String maintainTechnicist;

    @ApiModelProperty(value = "责任单位")
    private String responsibleOrg;

    @ApiModelProperty(value = "主要负责人")
    private String responsibleCharge;

    @ApiModelProperty(value = "使用单位")
    private String useOrg;

    @ApiModelProperty(value = "使用单位负责人")
    private String useOrgCharge;

    @ApiModelProperty(value = "承建单位")
    private String buildOrg;

    @ApiModelProperty(value = "承建单位负责人")
    private String buildOrgCharge;

    @ApiModelProperty(value = "数据操作日志是否留存及期限")
    private String dbOperateRecord;

    @ApiModelProperty(value = "是否介入数据库审计设备")
    private String inAudit;

    @ApiModelProperty(value = "操作人id")
    private String operator;

    @ApiModelProperty(value = "操作时间")
    private Date operateTime;

    @ApiModelProperty(value = "数据库用户密码是否交由民警保管")
    private String policeChargePass;

    @ApiModelProperty(value = "密码更改周期")
    private String passChangePeriod;

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
    public String getDbName() {
        return dbName;
    }

    public void setDbName(String dbName) {
        this.dbName = dbName;
    }
    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }
    public String getDbStorageType() {
        return dbStorageType;
    }

    public void setDbStorageType(String dbStorageType) {
        this.dbStorageType = dbStorageType;
    }
    public String getDbContent() {
        return dbContent;
    }

    public void setDbContent(String dbContent) {
        this.dbContent = dbContent;
    }
    public String getDbAccessType() {
        return dbAccessType;
    }

    public void setDbAccessType(String dbAccessType) {
        this.dbAccessType = dbAccessType;
    }
    public String getDbBackupType() {
        return dbBackupType;
    }

    public void setDbBackupType(String dbBackupType) {
        this.dbBackupType = dbBackupType;
    }
    public String getMaintainPolice() {
        return maintainPolice;
    }

    public void setMaintainPolice(String maintainPolice) {
        this.maintainPolice = maintainPolice;
    }
    public String getMaintainTechnicist() {
        return maintainTechnicist;
    }

    public void setMaintainTechnicist(String maintainTechnicist) {
        this.maintainTechnicist = maintainTechnicist;
    }
    public String getResponsibleOrg() {
        return responsibleOrg;
    }

    public void setResponsibleOrg(String responsibleOrg) {
        this.responsibleOrg = responsibleOrg;
    }
    public String getResponsibleCharge() {
        return responsibleCharge;
    }

    public void setResponsibleCharge(String responsibleCharge) {
        this.responsibleCharge = responsibleCharge;
    }
    public String getUseOrg() {
        return useOrg;
    }

    public void setUseOrg(String useOrg) {
        this.useOrg = useOrg;
    }
    public String getUseOrgCharge() {
        return useOrgCharge;
    }

    public void setUseOrgCharge(String useOrgCharge) {
        this.useOrgCharge = useOrgCharge;
    }
    public String getBuildOrg() {
        return buildOrg;
    }

    public void setBuildOrg(String buildOrg) {
        this.buildOrg = buildOrg;
    }
    public String getBuildOrgCharge() {
        return buildOrgCharge;
    }

    public void setBuildOrgCharge(String buildOrgCharge) {
        this.buildOrgCharge = buildOrgCharge;
    }
    public String getDbOperateRecord() {
        return dbOperateRecord;
    }

    public void setDbOperateRecord(String dbOperateRecord) {
        this.dbOperateRecord = dbOperateRecord;
    }
    public String getInAudit() {
        return inAudit;
    }

    public void setInAudit(String inAudit) {
        this.inAudit = inAudit;
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
    public String getPoliceChargePass() {
        return policeChargePass;
    }

    public void setPoliceChargePass(String policeChargePass) {
        this.policeChargePass = policeChargePass;
    }
    public String getPassChangePeriod() {
        return passChangePeriod;
    }

    public void setPassChangePeriod(String passChangePeriod) {
        this.passChangePeriod = passChangePeriod;
    }

    @Override
    public String toString() {
        return "CompanyProjectDb{" +
            "id=" + id +
            ", projectId=" + projectId +
            ", companyId=" + companyId +
            ", dbName=" + dbName +
            ", dbType=" + dbType +
            ", dbStorageType=" + dbStorageType +
            ", dbContent=" + dbContent +
            ", dbAccessType=" + dbAccessType +
            ", dbBackupType=" + dbBackupType +
            ", maintainPolice=" + maintainPolice +
            ", maintainTechnicist=" + maintainTechnicist +
            ", responsibleOrg=" + responsibleOrg +
            ", responsibleCharge=" + responsibleCharge +
            ", useOrg=" + useOrg +
            ", useOrgCharge=" + useOrgCharge +
            ", buildOrg=" + buildOrg +
            ", buildOrgCharge=" + buildOrgCharge +
            ", dbOperateRecord=" + dbOperateRecord +
            ", inAudit=" + inAudit +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
            ", policeChargePass=" + policeChargePass +
            ", passChangePeriod=" + passChangePeriod +
        "}";
    }
}
