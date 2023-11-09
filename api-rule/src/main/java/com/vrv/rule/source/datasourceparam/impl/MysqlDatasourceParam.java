package com.vrv.rule.source.datasourceparam.impl;

import com.vrv.rule.source.datasourceparam.DataSourceParamsAbs;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Builder
@Data
public class MysqlDatasourceParam extends DataSourceParamsAbs implements Serializable {

    private static final long serialVersionUID = 1L;

    private String driverClassName;  //数据库驱动

    private String jdbcUrl; //jdbcurl

//    private String mysqlHost;  //主机IP
//
//    private String port; //数据库端口地址

    private String userName; //用户名

    private String password; //密码

    private Integer minimumIdel; //最小空闲连接数

    private Integer maximumPoolSize; //最大连接数

    private Long idleTimeout; //连接最大空闲时间

    private String sql; //筛选sql




}
