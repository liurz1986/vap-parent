package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lizj on 2019/6/28.
 */

@ApiModel("时间model")
public class TimeModel extends PageModel {

    /**
     * 时间
     */
    @ApiModelProperty("时间 1：近七天，2：近一个月")
    private String time;

    /**
     * 应用名称
     */
    @ApiModelProperty("应用编号")
    private String appNo;

    @ApiModelProperty("部门编号")
    private String departCode;

    /**
     * 应用名称
     */
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("源应用系统编号")
    private String srcAppNo;

    public String getSrcAppNo() {
        return srcAppNo;
    }

    public void setSrcAppNo(String srcAppNo) {
        this.srcAppNo = srcAppNo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartCode() {
        return departCode;
    }

    public void setDepartCode(String departCode) {
        this.departCode = departCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }
}
