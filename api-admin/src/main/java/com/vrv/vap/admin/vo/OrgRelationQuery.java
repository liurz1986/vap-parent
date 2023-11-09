package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class OrgRelationQuery {

    @ApiModelProperty(value="组织机构code(优先)")
    private String code;

    @ApiModelProperty(value="ip")
    private String ip;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }



}
