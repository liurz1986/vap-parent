package com.vrv.vap.netflow.model;

import io.swagger.annotations.ApiModelProperty;

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
    private String code;

    /**
     * 区域名称
     */
    @Column(name = "domain_name")
    private String domainName;

    /**
     * 上级编号
     */
    @Column(name = "parent_code")
    private String parentCode;


    /**
     * 上级编号
     */
    @Column(name = "sub_code")
    private String subCode;


    private Integer sort;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级  0绝密，1机密，2秘密，3内部")
    @Column(name = "secret_level")
    private Integer secretLevel;

    /**
     * 0为一级单位，1为二级单位，2为三级单位
     */
    @ApiModelProperty("0为一级单位，1为二级单位，2为三级单位")
    @Column(name = "org_hierarchy")
    private Byte orghierarchy;



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
