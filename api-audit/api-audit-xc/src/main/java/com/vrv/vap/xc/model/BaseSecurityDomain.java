package com.vrv.vap.xc.model;

/**
 * Created by lil on 2019/9/26.
 */
public class BaseSecurityDomain {

    private Integer id;
    /**
     * 关联使用guid
     */
    private String code;

    /**
     * 区域名称
     */
    private String domainName;

    /**
     * 上级编号
     */
    private String parentCode;

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
}
