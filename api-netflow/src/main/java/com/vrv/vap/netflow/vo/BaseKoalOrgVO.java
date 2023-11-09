package com.vrv.vap.netflow.vo;

import com.vrv.vap.netflow.model.BaseKoalOrg;

import java.util.List;

/**
 * @author lilang
 * @date 2019/8/1
 * @description
 */
public class BaseKoalOrgVO extends BaseKoalOrg {

    private Long startIpNum;

    private Long endIpNum;

    private Boolean hasChildren;

    private List<BaseKoalOrgVO> children;

    private Integer isAuthorized;

    public Long getStartIpNum() {
        return startIpNum;
    }

    public void setStartIpNum(Long startIpNum) {
        this.startIpNum = startIpNum;
    }

    public Long getEndIpNum() {
        return endIpNum;
    }

    public void setEndIpNum(Long endIpNum) {
        this.endIpNum = endIpNum;
    }

    public Boolean getHasChildren() {
        return hasChildren;
    }

    public void setHasChildren(Boolean hasChildren) {
        this.hasChildren = hasChildren;
    }

    public Integer getIsAuthorized() {
        return isAuthorized;
    }

    public void setIsAuthorized(Integer isAuthorized) {
        this.isAuthorized = isAuthorized;
    }

    public List<BaseKoalOrgVO> getChildren() {
        return children;
    }

    public void setChildren(List<BaseKoalOrgVO> children) {
        this.children = children;
    }
}
