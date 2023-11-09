package com.vrv.vap.server.push.vo;

import com.vrv.vap.server.push.model.Message;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(value="用户组消息")
public class MessageVO extends Message {

    @ApiModelProperty(value="用户组ID", required = true)
    private int roleId = 0;

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }
}
