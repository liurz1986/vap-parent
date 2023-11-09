package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class UserPersonInfo extends User {
    @ApiModelProperty("人员姓名")
    private String personName;

    @ApiModelProperty("角色名称")
    private String roleName;
}