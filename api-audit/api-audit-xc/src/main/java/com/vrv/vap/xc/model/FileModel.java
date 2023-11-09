package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lizj on 2019/6/28.
 */

@ApiModel("时间model")
public class FileModel {

    /**
     * 时间
     */
    @ApiModelProperty("时间 1：近七天，2：近一个月")
    private String time;

    @ApiModelProperty("类型 1：上传   2：下载")
    private String type;

    @ApiModelProperty("用户名")
    private String username;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
