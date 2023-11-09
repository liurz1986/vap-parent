package com.vrv.vap.admin.vo;

import com.vrv.vap.admin.model.BaseSecurityDomain;

/**
 * @author lilang
 * @date 2019/9/20
 * @description
 */
public class BaseSecurityDomainVO extends BaseSecurityDomain {

    private Boolean hasChildren;

    public Boolean getHasChildren() {
        return hasChildren;
    }

    private String ipRange;

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public String getIpRange() {
        return ipRange;
    }

    public void setIpRange(String ipRange) {
        this.ipRange = ipRange;
    }
}
