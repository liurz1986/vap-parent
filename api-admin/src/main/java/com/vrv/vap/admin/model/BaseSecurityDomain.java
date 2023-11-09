package com.vrv.vap.admin.model;

import com.vrv.vap.common.annotation.LogColumn;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.io.Serializable;

@Table(name = "base_security_domain")
public class BaseSecurityDomain implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    /**
     * 关联使用guid
     */
    @Column(name = "code")
    @ApiModelProperty(value = "安全域编码")
    @Ignore
    private String code;

    /**
     * 区域名称
     */
    @Column(name = "domain_name")
    @ApiModelProperty(value = "安全域名称")
    private String domainName;

    /**
     * 上级编号
     */
    @Column(name = "parent_code")
    @ApiModelProperty(value = "上级安全域")
    private String parentCode;


    /**
     * 上级编号
     */
    @Column(name = "sub_code")
    @ApiModelProperty(value = "层级维护编码")
    private String subCode;


    private Integer sort;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级")
    @Column(name = "secret_level")
    @LogColumn(mapping = "{\"0\":\"绝密\",\"1\":\"机密\",\"2\":\"秘密\",\"3\":\"内部\",\"4\":\"非密\"}")
    private Integer secretLevel;

    /**
     * 0为一级单位，1为二级单位，2为三级单位
     */
    @ApiModelProperty("单位级别")
    @Column(name = "org_hierarchy")
    private Byte orghierarchy;

    @ApiModelProperty("责任人名称")
    @Column(name = "responsible_name")
    private String responsibleName;

    @ApiModelProperty("责任人code(用户编号）")
    @Column(name = "responsible_code")
    private String responsibleCode;
    @ApiModelProperty("组织结构名称")
    @Column(name = "org_name")
    private String orgName;
    @ApiModelProperty("组织结构code")
    @Column(name = "org_code")
    private String orgCode;

    public String getResponsibleName() {
        return responsibleName;
    }

    public void setResponsibleName(String responsibleName) {
        this.responsibleName = responsibleName;
    }

    public String getResponsibleCode() {
        return responsibleCode;
    }

    public void setResponsibleCode(String responsibleCode) {
        this.responsibleCode = responsibleCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public Byte getOrghierarchy() {
        return orghierarchy;
    }

    public void setOrghierarchy(Byte orghierarchy) {
        this.orghierarchy = orghierarchy;
    }

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }

    public Integer getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(Integer secretLevel) {
        this.secretLevel = secretLevel;
    }
}
