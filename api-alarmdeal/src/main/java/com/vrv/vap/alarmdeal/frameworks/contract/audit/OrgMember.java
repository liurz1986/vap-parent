package com.vrv.vap.alarmdeal.frameworks.contract.audit;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@ApiModel("组织机构成员")
@Data
public class OrgMember {

    @ApiModelProperty("关系ID，主键")
    private Integer id;

    @ApiModelProperty("用户ID")
    private Integer userId;

    @ApiModelProperty("是否是领导")
    private Byte isLeader;

    @ApiModelProperty("用户姓名")
    private String name;

    @ApiModelProperty("用户角色ID")
    private String roleId;

    @ApiModelProperty("用户登录账号")
    private  String account;

    @ApiModelProperty("用户身份证号")
    private  String idcard;

    @ApiModelProperty("用户电话")
    private  String phone;

    @ApiModelProperty("用户邮箱")
    private  String email;

}
