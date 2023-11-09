package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModelProperty;

public class DatabaseConnectionVo {

    @ApiModelProperty("链接类型 0：内部数据库，1：外部数据库")
    private Integer type;

    @ApiModelProperty("数据库IP地址")
    private String address;

    @ApiModelProperty("数据库端口号")
    private String port;

    @ApiModelProperty("数据库名称")
    private String databaseName;

    @ApiModelProperty("数据库用户")
    private String user;

    @ApiModelProperty("数据库密码")
    private String password;

    @ApiModelProperty("表名")
    private String tableName;

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }
}
