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
 * 企业服务器建设情况
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-25
 */
@ApiModel(value="CompanyProjectServer对象", description="企业服务器建设情况")
public class CompanyProjectServerQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "项目id")
    private Integer projectId;

    @ApiModelProperty(value = "关联公司id")
    private Integer companyId;

    @ApiModelProperty(value = "服务器类型")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String serverType;

    @ApiModelProperty(value = "型号")
    private String category;

    @ApiModelProperty(value = "网络环境")
    private String networkEnv;

    @ApiModelProperty(value = "ip(网段)")
    private String ip;

    @ApiModelProperty(value = "mac")
    private String mac;

    @ApiModelProperty(value = "序列号")
    private String sn;

    @ApiModelProperty(value = "服务器系统")
    private String system;

    @ApiModelProperty(value = "服务器具体位置")
    private String position;

    @ApiModelProperty(value = "运维民警")
    private String maintainPolice;

    @ApiModelProperty(value = "运维技术人员")
    private String maintainTechnicist;

    @ApiModelProperty(value = "是否接入堡垒机")
    private String hasBastion;

    @ApiModelProperty(value = "主要功能")
    private String mainFeature;

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
    public String getServerType() {
        return serverType;
    }

    public void setServerType(String serverType) {
        this.serverType = serverType;
    }
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
    public String getNetworkEnv() {
        return networkEnv;
    }

    public void setNetworkEnv(String networkEnv) {
        this.networkEnv = networkEnv;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }
    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }
    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
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
    public String getHasBastion() {
        return hasBastion;
    }

    public void setHasBastion(String hasBastion) {
        this.hasBastion = hasBastion;
    }
    public String getMainFeature() {
        return mainFeature;
    }

    public void setMainFeature(String mainFeature) {
        this.mainFeature = mainFeature;
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
        return "CompanyProjectServer{" +
            "id=" + id +
            ", projectId=" + projectId +
            ", companyId=" + companyId +
            ", serverType=" + serverType +
            ", category=" + category +
            ", networkEnv=" + networkEnv +
            ", ip=" + ip +
            ", mac=" + mac +
            ", sn=" + sn +
            ", system=" + system +
            ", position=" + position +
            ", maintainPolice=" + maintainPolice +
            ", maintainTechnicist=" + maintainTechnicist +
            ", hasBastion=" + hasBastion +
            ", mainFeature=" + mainFeature +
            ", operator=" + operator +
            ", operateTime=" + operateTime +
        "}";
    }
}
