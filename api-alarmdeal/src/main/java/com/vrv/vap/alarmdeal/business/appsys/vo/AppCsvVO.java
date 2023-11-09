package com.vrv.vap.alarmdeal.business.appsys.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Cvs文件实体
 * 2023-07-14
 * dev_ip,std_sys_id,std_sys_name,std_sys_secret_level
 * dev_ip：服务器的ip
 * std_sys_id：应用系统编号
 * std_sys_name：应用系统名称，
 * std_sys_secret_level ：应用系统涉密等级 ：采用数字
 */
@Data
public class AppCsvVO {
    @ApiModelProperty("服务器的ip")
    private String devIp;
    @ApiModelProperty("应用系统编号")
    private String stdSysId;
    @ApiModelProperty("应用系统名称")
    private String stdSysName;
    @ApiModelProperty("应用系统涉密等级")
    private String stdSysSecretLevel;

}
