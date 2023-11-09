package com.vrv.vap.monitor.model;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Monitor2SnmpInfo {

    @ApiModelProperty(value = "snmp版本:1/2/3")
    private Integer snmpVersion;

    @ApiModelProperty(value = "snmp连接端口")
    private String snmpPort;

    @ApiModelProperty(value = "团体名")
    private String communityName;

    @ApiModelProperty(value = "v3安全级别:1=无认证无加密, 2=有认证无加密, 3=有认证有加密")
    private Integer securityLevel;

    @ApiModelProperty(value = "用户")
    private String user;

    @ApiModelProperty(value = "认证加密方式")
    private String passEncWay;

    @ApiModelProperty(value = "认证密码")
    private String authPassphrase;

    @ApiModelProperty(value = "私钥加密方式")
    private String privacyEncWay;

    @ApiModelProperty(value = "私钥密码")
    private String privacyPassphrase;
}
