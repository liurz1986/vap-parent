package com.vrv.vap.xc.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.toolkit.annotations.NotNull;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

/**
 * This class was generated by MyBatis Generator.
 * This class corresponds to the database table base_sysinfo_database
 *
 * @mbg.generated do_not_delete_during_merge 2018-04-26 15:55:25
 */
@ApiModel
@SuppressWarnings("unused")
public class BaseSysinfoDatabaseQuery extends Query {
    /**
     *
     */
    @ApiModelProperty("")
    private Integer id;

    /**
     * 应用系统ID
     */
    @ApiModelProperty("应用系统ID")
    @NotNull
    private Integer sysId;

    /**
     * 数据库别名
     */
    @ApiModelProperty("数据库别名")
    private String aliasName;

    /**
     * 所属地市编码
     */
    @ApiModelProperty("所属地市编码")
    private String areaCode;

    /**
     * 所属网络:互联网;公安;专网
     */
    @ApiModelProperty("所属网络:互联网;公安;专网")
    private String network;

    /**
     * 数据库类别:oracle;mysql;sqlserver;gbase;mongodb;hbase
     */
    @ApiModelProperty("数据库类别:oracle;mysql;sqlserver;gbase;mongodb;hbase")
    @NotNull
    private String type;

    /**
     * 数据源IP
     */
    @ApiModelProperty("数据源IP")
    private String soucreIp;

    /**
     * 备用IP
     */
    @ApiModelProperty("备用IP")
    private String backupIp;

    /**
     * 数据库端口
     */
    @ApiModelProperty("数据库端口")
    private String port;

    /**
     * 数据库实例名
     */
    @ApiModelProperty("数据库实例名")
    private String databaseName;

    /**
     * 数据库字符集
     */
    @ApiModelProperty("数据库字符集")
    private String characters;

    /**
     * 用户名
     */
    @ApiModelProperty("用户名")
    private String userName;

    /**
     * 密码
     */
    @ApiModelProperty("密码")
    private String password;

    /**
     * 责任人
     */
    @ApiModelProperty("责任人")
    private String personLiable;

    /**
     * 联系电话
     */
    @ApiModelProperty("联系电话")
    private String phone;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String mark;

    /**
     *
     */
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("")
    private Date lastUpdateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSysId() {
        return sysId;
    }

    public void setSysId(Integer sysId) {
        this.sysId = sysId;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSoucreIp() {
        return soucreIp;
    }

    public void setSoucreIp(String soucreIp) {
        this.soucreIp = soucreIp;
    }

    public String getBackupIp() {
        return backupIp;
    }

    public void setBackupIp(String backupIp) {
        this.backupIp = backupIp;
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

    public String getCharacters() {
        return characters;
    }

    public void setCharacters(String characters) {
        this.characters = characters;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonLiable() {
        return personLiable;
    }

    public void setPersonLiable(String personLiable) {
        this.personLiable = personLiable;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMark() {
        return mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }
}