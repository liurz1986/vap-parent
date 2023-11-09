package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "visual_database_connection")
public class VisualDatabaseConnection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("连接名称")
    @Column(name = "connection_name")
    private String connectionName;

    @ApiModelProperty("表名")
    @Column(name = "table_name")
    private String tableName;

    @ApiModelProperty("表描述")
    @Column(name = "table_description")
    private String tableDescription;

    @ApiModelProperty("时间字段")
    @Column(name = "time_field_name")
    private String timeFieldName;

    @ApiModelProperty("表字段")
    @Column(name = "table_field_json")
    private String tableFieldJson;

    @ApiModelProperty("链接类型 0：内部数据库，1：外部数据库")
    private Integer type;

    @ApiModelProperty("数据库IP地址")
    private String address;

    @ApiModelProperty("数据库端口号")
    private String port;

    @ApiModelProperty("数据库名称")
    @Column(name = "database_name")
    private String databaseName;

    @ApiModelProperty("数据库用户")
    private String user;

    @ApiModelProperty("数据库密码")
    private String password;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTableDescription() {
        return tableDescription;
    }

    public void setTableDescription(String tableDescription) {
        this.tableDescription = tableDescription;
    }

    public String getTableFieldJson() {
        return tableFieldJson;
    }

    public void setTableFieldJson(String tableFieldJson) {
        this.tableFieldJson = tableFieldJson;
    }

    public String getTimeFieldName() {
        return timeFieldName;
    }

    public void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }

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
}
