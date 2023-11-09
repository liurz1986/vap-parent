package com.vrv.vap.xc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * Created by lil on 2018/5/4.
 */
public class BasePkiQuery extends Query {

    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     *
     */
    @ApiModelProperty("")
    @NotNull
    private String pkiId;

    /**
     *
     */
    @ApiModelProperty("")
    private String username;

    /**
     *
     */
    @ApiModelProperty("")
    private String userIdNum;

    /**
     *
     */
    @ApiModelProperty("")
    private String userIdNumex;

    /**
     *
     */
    @ApiModelProperty("")
    private String policeType;

    /**
     *
     */
    @ApiModelProperty("")
    private String station;

    /**
     *
     */
    @ApiModelProperty("")
    private String certSn;

    /**
     *
     */
    @ApiModelProperty("")
    private String certStatus;

    /**
     *
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("")
    private Date certNotBefore;

    /**
     *
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("")
    private Date certNotAfter;

    /**
     *
     */
    @ApiModelProperty("")
    private String certRole;

    /**
     *
     */
    @ApiModelProperty("")
    private String orgCode;

    /**
     *
     */
    @ApiModelProperty("")
    private String orgName;

    /**
     *
     */
    @ApiModelProperty("")
    private String dutyLevel;

    /**
     *
     */
    @ApiModelProperty("")
    private String charge;

    /**
     *
     */
    @ApiModelProperty("")
    private String provinceAreaCode;
    /**
     *
     */
    @ApiModelProperty("")
    private String userIdnEx;

    private String userName;

    public String getProvinceAreaCode() {
        return provinceAreaCode;
    }

    public void setProvinceAreaCode(String provinceAreaCode) {
        this.provinceAreaCode = provinceAreaCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIdnEx() {
        return userIdnEx;
    }

    public void setUserIdnEx(String userIdnEx) {
        this.userIdnEx = userIdnEx;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPkiId() {
        return pkiId;
    }

    public void setPkiId(String pkiId) {
        this.pkiId = pkiId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserIdNum() {
        return userIdNum;
    }

    public void setUserIdNum(String userIdNum) {
        this.userIdNum = userIdNum;
    }

    public String getUserIdNumex() {
        return userIdNumex;
    }

    public void setUserIdNumex(String userIdNumex) {
        this.userIdNumex = userIdNumex;
    }

    public String getPoliceType() {
        return policeType;
    }

    public void setPoliceType(String policeType) {
        this.policeType = policeType;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public String getCertSn() {
        return certSn;
    }

    public void setCertSn(String certSn) {
        this.certSn = certSn;
    }

    public String getCertStatus() {
        return certStatus;
    }

    public void setCertStatus(String certStatus) {
        this.certStatus = certStatus;
    }

    public Date getCertNotBefore() {
        return certNotBefore;
    }

    public void setCertNotBefore(Date certNotBefore) {
        this.certNotBefore = certNotBefore;
    }

    public Date getCertNotAfter() {
        return certNotAfter;
    }

    public void setCertNotAfter(Date certNotAfter) {
        this.certNotAfter = certNotAfter;
    }

    public String getCertRole() {
        return certRole;
    }

    public void setCertRole(String certRole) {
        this.certRole = certRole;
    }

    public String getOrgCode() {
        return orgCode;
    }

    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    public String getOrgName() {
        return orgName;
    }

    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getDutyLevel() {
        return dutyLevel;
    }

    public void setDutyLevel(String dutyLevel) {
        this.dutyLevel = dutyLevel;
    }

    public String getCharge() {
        return charge;
    }

    public void setCharge(String charge) {
        this.charge = charge;
    }
}
