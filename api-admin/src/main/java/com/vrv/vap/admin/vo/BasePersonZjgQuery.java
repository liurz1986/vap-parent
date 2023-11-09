package com.vrv.vap.admin.vo;

import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.vrv.vap.common.plugin.annotaction.QueryLessThan;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.plugin.annotaction.QueryMoreThan;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import java.util.List;

@ApiModel("人员信息查询")
public class BasePersonZjgQuery extends Query{

    private Integer id;

    /**
     * 员工编号
     */
    @ApiModelProperty("员工编号")
    private String userNo;

    /**
     * 姓名
     */
    @ApiModelProperty("姓名")
    @QueryLike
    private String userName;

    /**
     * 性别
     */
    private String sex;

    /**
     * 身份证号
     */
    private String userIdnEx;

    /**
     * 用户类型:1用户 2管理员
     */
    @ApiModelProperty("用户类型")
    private String personType;

    /**
     * 职务
     */
    @ApiModelProperty("职务")
    @QueryLike
    private String personRank;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级 ")
    private String secretLevel;

    /**
     * 单位名称
     */
    @ApiModelProperty("单位名称 ")
    private String orgCode;

    /**
     * 单位名称
     */
    @ApiModelProperty("单位编码 ")
    private String orgName;




    @ApiModelProperty("开始时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "startTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @QueryMoreThan
    @Column(name="createTime")
    private Date startTime;

    @ApiModelProperty("结束时间,格式yyyy-MM-dd HH:mm:ss")
    @JsonProperty(value = "endTime", access = JsonProperty.Access.WRITE_ONLY)
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name="createTime")
    @QueryLessThan
    private Date endTime;

    private String formatType;

    private List<String> userType;

    private String month;

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取员工编号
     *
     * @return user_no - 员工编号
     */
    public String getUserNo() {
        return userNo;
    }

    /**
     * 设置员工编号
     *
     * @param userNo 员工编号
     */
    public void setUserNo(String userNo) {
        this.userNo = userNo;
    }

    /**
     * 获取姓名
     *
     * @return user_name - 姓名
     */
    public String getUserName() {
        return userName;
    }

    /**
     * 设置姓名
     *
     * @param userName 姓名
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * 获取性别
     *
     * @return sex - 性别
     */
    public String getSex() {
        return sex;
    }

    /**
     * 设置性别
     *
     * @param sex 性别
     */
    public void setSex(String sex) {
        this.sex = sex;
    }

    /**
     * 获取身份证号
     *
     * @return user_idn_ex - 身份证号
     */
    public String getUserIdnEx() {
        return userIdnEx;
    }

    /**
     * 设置身份证号
     *
     * @param userIdnEx 身份证号
     */
    public void setUserIdnEx(String userIdnEx) {
        this.userIdnEx = userIdnEx;
    }

    /**
     * 获取用户类型:1用户 2管理员
     *
     * @return person_type - 用户类型:1用户 2管理员
     */
    public String getPersonType() {
        return personType;
    }

    /**
     * 设置用户类型:1用户 2管理员
     *
     * @param personType 用户类型:1用户 2管理员
     */
    public void setPersonType(String personType) {
        this.personType = personType;
    }

    /**
     * 获取职务
     *
     * @return person_rank - 职务
     */
    public String getPersonRank() {
        return personRank;
    }

    /**
     * 设置职务
     *
     * @param personRank 职务
     */
    public void setPersonRank(String personRank) {
        this.personRank = personRank;
    }

    /**
     * 获取保密等级
     *
     * @return secret_level - 保密等级
     */
    public String getSecretLevel() {
        return secretLevel;
    }

    /**
     * 设置保密等级
     *
     * @param secretLevel 保密等级
     */
    public void setSecretLevel(String secretLevel) {
        this.secretLevel = secretLevel;
    }

    /**
     * 获取单位名称
     *
     * @return org_code - 单位名称
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * 设置单位名称
     *
     * @param orgCode 单位名称
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

    /**
     * 获取单位名称
     *
     * @return org_name - 单位名称
     */
    public String getOrgName() {
        return orgName;
    }

    /**
     * 设置单位名称
     *
     * @param orgName 单位名称
     */
    public void setOrgName(String orgName) {
        this.orgName = orgName;
    }

    public String getFormatType() {
        return formatType;
    }

    public void setFormatType(String formatType) {
        this.formatType = formatType;
    }

    public List<String> getUserType() {
        return userType;
    }

    public void setUserType(List<String> userType) {
        this.userType = userType;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}