package com.vrv.vap.alarmdeal.frameworks.contract.user;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

 @Data
public class BasePersonZjg {
  
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
    @ApiModelProperty("用户类型:1用户 2管理员")
    private String personType;

    /**
     * 职务
     */
    @ApiModelProperty("职务")
    private String personRank;

    /**
     * 保密等级
     */
    @ApiModelProperty("保密等级 0绝密，1机密，2秘密，3内部")
    private Integer secretLevel;

    /**
     * 单位名称
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
    @JsonFormat(timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createTime;

}