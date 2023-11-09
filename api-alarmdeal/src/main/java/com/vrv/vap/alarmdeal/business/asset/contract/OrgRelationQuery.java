package com.vrv.vap.alarmdeal.business.asset.contract;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

public class OrgRelationQuery implements Serializable {

    private String code;

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
