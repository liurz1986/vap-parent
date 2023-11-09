package com.vrv.vap.admin.vo;

import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/5/16
 * @description
 */
public class SyncOrgVO {

    private String syncUid;

    private String syncSource;

    private Integer dataSourceType;

    private String code;

    private String name;

    private String type;

    private String parentCode;

    private String secretLevel;

    private String protectionLevel;

    private String secretQualifications;

    private String orgType;

    private List<Map> ipSegment;

    private Integer sort;

    public String getSyncUid() {
        return syncUid;
    }

    public void setSyncUid(String syncUid) {
        this.syncUid = syncUid;
    }

    public String getSyncSource() {
        return syncSource;
    }

    public void setSyncSource(String syncSource) {
        this.syncSource = syncSource;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getSecretLevel() {
        return secretLevel;
    }

    public void setSecretLevel(String secretLevel) {
        this.secretLevel = secretLevel;
    }

    public String getProtectionLevel() {
        return protectionLevel;
    }

    public void setProtectionLevel(String protectionLevel) {
        this.protectionLevel = protectionLevel;
    }

    public String getSecretQualifications() {
        return secretQualifications;
    }

    public void setSecretQualifications(String secretQualifications) {
        this.secretQualifications = secretQualifications;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
    }

    public List<Map> getIpSegment() {
        return ipSegment;
    }

    public void setIpSegment(List<Map> ipSegment) {
        this.ipSegment = ipSegment;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }
}
