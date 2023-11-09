package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lizj on 2019/6/28.
 */

@ApiModel("用户访问model")
public class UserVisitModel extends PageModel {

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
