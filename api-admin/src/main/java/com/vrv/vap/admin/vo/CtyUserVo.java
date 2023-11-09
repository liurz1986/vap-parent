package com.vrv.vap.admin.vo;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CtyUserVo {

     /**
      * 楚天雲用戶id
      * */
     @ApiModelProperty("楚天雲用戶id")
     private Integer ctyId;


     /**
      * 用户名称
      */
     @ApiModelProperty("用户名称")
     private String name;


     /**
      * 用户用到登录的帐号名
      */
     @ApiModelProperty("用户用到登录的帐号名")
     private String account;


     @ApiModelProperty("机构编码")
     private String orgCode;

     @ApiModelProperty("机构名称")
     private String orgName;

     @ApiModelProperty("平台类别")
     private  String role_type;



}
