package com.vrv.vap.alarmdeal.frameworks.contract.user;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.io.Serializable;


public class BaseSecurityDomain implements Serializable {
 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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



    private Integer sort;

    private String subCode;


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

    public String getSubCode() {
        return subCode;
    }

    public void setSubCode(String subCode) {
        this.subCode = subCode;
    }
}
