package com.vrv.vap.xc.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Created by lizj on 2019/6/28.
 */

@ApiModel("应用model")
public class ApplicationModel extends PageModel {

    /**
     * 应用名称
     */
    @ApiModelProperty("应用编号")
    private String appNo;

    @ApiModelProperty("ip")
    private String ip;
    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String username;

    @ApiModelProperty("部门编号")
    private String departCode;

    @ApiModelProperty("源应用系统编号")
    private String srcAppNo;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getSrcAppNo() {
        return srcAppNo;
    }

    public void setSrcAppNo(String srcAppNo) {
        this.srcAppNo = srcAppNo;
    }

    public String getDepartCode() {
        return departCode;
    }

    public void setDepartCode(String departCode) {
        this.departCode = departCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAppNo() {
        return appNo;
    }

    public void setAppNo(String appNo) {
        this.appNo = appNo;
    }
}
