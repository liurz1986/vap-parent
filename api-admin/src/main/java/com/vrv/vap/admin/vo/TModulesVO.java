package com.vrv.vap.admin.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class TModulesVO {
    private Integer id;

    private String moduleName;

    private String moduleType;

    private String moduleVersion;

    private String moduleDesc;

    private Integer moduleInstancesNumber;

    private Date createTime;

    private String moduleOriginal;

    private String moduleInstancesIp;

    private Integer moduleInstancesPort;

    private Integer moduleInstancesStatus;

}
