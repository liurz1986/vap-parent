package com.vrv.vap.line.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
@Data
public class BasePersonZjg  {

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
    private String personRank;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级")
    private Integer secretLevel;

    /**
     * 单位编码
     */
    @ApiModelProperty("单位编码")
    private String orgCode;

    /**
     * 单位名称
     */
    @ApiModelProperty("单位名称")
    private String orgName;


    /**
     * 创建时间
     */
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
    @Ignore
    private Integer dataSourceType;

    /**
     * 外部账号
     */
    @ApiModelProperty("外部账号")
    @Ignore
    private String originAccount;

    private Integer backgroundAudit;

    private String backgroundAuditComment;

    private String backgroundAuditAttachment;

    private Integer skillCheck;


    private String skillCheckComment;


    private String skillCheckAttachment;


    private Integer secretProtocol;


    private String secretProtocolComment;


    private String secretProtocolAttachment;


    private String auditAttachmentName;


    private String checkAttachmentName;


    private String protocolAttachmentName;

}