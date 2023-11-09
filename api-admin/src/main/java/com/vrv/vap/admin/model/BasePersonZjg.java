package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.common.annotation.LogColumn;
import com.vrv.vap.syslog.common.annotation.LogField;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Table(name = "base_person_zjg")
public class BasePersonZjg implements Serializable {
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
    @LogField(name = "userIdnEx", description = "身份证号", desensitization = true)
    @Ignore
    private String userIdnEx;

    /**
     * 用户类型:1用户 2管理员
     */
    @ApiModelProperty("用户类型")
    @Column(name = "person_type")
    @LogColumn(mapping = "{\"1\":\"用户\",\"2\":\"管理员\"}")
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
    @ApiModelProperty("保密等级")
    @Column(name = "secret_level")
    @LogColumn(mapping = "{\"4\":\"核心\",\"3\":\"重要\",\"2\":\"一般\",\"1\":\"非密\"}")
    private Integer secretLevel;

    /**
     * 单位编码
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
    @ApiModelProperty("创建时间")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 外部系统人员表主键
     */
    @ApiModelProperty("外部系统人员表主键")
    @Ignore
    private String uid;

    /**
     * 数据来源
     */
    @ApiModelProperty("数据来源")
    @Ignore
    private String source;

    /**
     * 数据来源类型
     */
    @ApiModelProperty("数据来源类型")
    @Column(name = "data_source_type")
    @Ignore
    private Integer dataSourceType;

    /**
     * 外部账号
     */
    @ApiModelProperty("外部账号")
    @Column(name = "origin_account")
    @Ignore
    private String originAccount;

    @Column(name = "background_audit")
    private Integer backgroundAudit;

    @Column(name = "background_audit_comment")
    private String backgroundAuditComment;

    @Column(name = "background_audit_attachment")
    private String backgroundAuditAttachment;

    @Column(name = "skill_check")
    private Integer skillCheck;

    @Column(name = "skill_check_comment")
    private String skillCheckComment;

    @Column(name = "skill_check_attachment")
    private String skillCheckAttachment;

    @Column(name = "secret_protocol")
    private Integer secretProtocol;

    @Column(name = "secret_protocol_comment")
    private String secretProtocolComment;

    @Column(name = "secret_protocol_attachment")
    private String secretProtocolAttachment;

    @Column(name = "audit_attachment_name")
    private String auditAttachmentName;

    @Column(name = "check_attachment_name")
    private String checkAttachmentName;

    @Column(name = "protocol_attachment_name")
    private String protocolAttachmentName;

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

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getDataSourceType() {
        return dataSourceType;
    }

    public void setDataSourceType(Integer dataSourceType) {
        this.dataSourceType = dataSourceType;
    }

    public String getOriginAccount() {
        return originAccount;
    }

    public void setOriginAccount(String originAccount) {
        this.originAccount = originAccount;
    }

    public Integer getBackgroundAudit() {
        return backgroundAudit;
    }

    public void setBackgroundAudit(Integer backgroundAudit) {
        this.backgroundAudit = backgroundAudit;
    }

    public String getBackgroundAuditComment() {
        return backgroundAuditComment;
    }

    public void setBackgroundAuditComment(String backgroundAuditComment) {
        this.backgroundAuditComment = backgroundAuditComment;
    }

    public String getBackgroundAuditAttachment() {
        return backgroundAuditAttachment;
    }

    public void setBackgroundAuditAttachment(String backgroundAuditAttachment) {
        this.backgroundAuditAttachment = backgroundAuditAttachment;
    }

    public Integer getSkillCheck() {
        return skillCheck;
    }

    public void setSkillCheck(Integer skillCheck) {
        this.skillCheck = skillCheck;
    }

    public String getSkillCheckComment() {
        return skillCheckComment;
    }

    public void setSkillCheckComment(String skillCheckComment) {
        this.skillCheckComment = skillCheckComment;
    }

    public String getSkillCheckAttachment() {
        return skillCheckAttachment;
    }

    public void setSkillCheckAttachment(String skillCheckAttachment) {
        this.skillCheckAttachment = skillCheckAttachment;
    }

    public Integer getSecretProtocol() {
        return secretProtocol;
    }

    public void setSecretProtocol(Integer secretProtocol) {
        this.secretProtocol = secretProtocol;
    }

    public String getSecretProtocolComment() {
        return secretProtocolComment;
    }

    public void setSecretProtocolComment(String secretProtocolComment) {
        this.secretProtocolComment = secretProtocolComment;
    }

    public String getSecretProtocolAttachment() {
        return secretProtocolAttachment;
    }

    public void setSecretProtocolAttachment(String secretProtocolAttachment) {
        this.secretProtocolAttachment = secretProtocolAttachment;
    }

    public String getAuditAttachmentName() {
        return auditAttachmentName;
    }

    public void setAuditAttachmentName(String auditAttachmentName) {
        this.auditAttachmentName = auditAttachmentName;
    }

    public String getCheckAttachmentName() {
        return checkAttachmentName;
    }

    public void setCheckAttachmentName(String checkAttachmentName) {
        this.checkAttachmentName = checkAttachmentName;
    }

    public String getProtocolAttachmentName() {
        return protocolAttachmentName;
    }

    public void setProtocolAttachmentName(String protocolAttachmentName) {
        this.protocolAttachmentName = protocolAttachmentName;
    }
}