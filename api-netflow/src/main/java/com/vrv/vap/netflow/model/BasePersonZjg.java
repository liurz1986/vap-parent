package com.vrv.vap.netflow.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

@Table(name = "base_person_zjg")
public class BasePersonZjg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 员工编号
     */
    @Column(name = "user_no")
    @ApiModelProperty("员工编号")
    private String userNo;

    /**
     * 姓名
     */
    @Column(name = "user_name")
    @ApiModelProperty("姓名")
    private String userName;

    /**
     * 性别
     */
    @ApiModelProperty("性别")
    private String sex;

    /**
     * 身份证号
     */
    @ApiModelProperty("身份证号")
    @Column(name = "user_idn_ex")
    private String userIdnEx;

    /**
     * 用户类型:1用户 2管理员
     */
    @ApiModelProperty("用户类型:1用户 2管理员")
    @Column(name = "person_type")
    private String personType;

    /**
     * 职务
     */
    @ApiModelProperty("职务")
    @Column(name = "person_rank")
    private String personRank;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级 4核心、3重要、2一般、1非密")
    @Column(name = "secret_level")
    private Integer secretLevel;

    /**
     * 单位名称
     */
    @ApiModelProperty("单位编码")
    @Column(name = "org_code")
    private String orgCode;

    /**
     * 单位名称
     */
    @ApiModelProperty("单位名称")
    @Column(name = "org_name")
    private String orgName;


    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;


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
    public Integer getSecretLevel() {
        return secretLevel;
    }

    /**
     * 设置保密等级
     *
     * @param secretLevel 保密等级
     */
    public void setSecretLevel(Integer secretLevel) {
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


    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}