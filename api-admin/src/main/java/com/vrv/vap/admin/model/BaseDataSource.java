package com.vrv.vap.admin.model;


import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;

@Table(name = "base_data_source")
public class BaseDataSource {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @ApiModelProperty("主键")
  private Integer id;

  @Column(name = "`name`")
  @ApiModelProperty("数据源名称")
  private String name;

  @Column(name = "url")
  @ApiModelProperty("mysql链接")
  private String url;

  @Column(name = "username")
  @ApiModelProperty("用户名")
  private String username;

  @Column(name = "password")
  @ApiModelProperty("密码")
  private String password;

  @Column(name = "cluster_ip")
  @ApiModelProperty("es集群地址")
  private String clusterIp;

  @Column(name = "port")
  @ApiModelProperty("es端口")
  private String port;

  @Column(name = "`type`")
  @ApiModelProperty("类型：1 mysql，2 es")
  private String type;

  @Column(name = "driver")
  @ApiModelProperty("mysql驱动类")
  private String driver;

  @Column(name = "es_index")
  @ApiModelProperty("es数据库index")
  private String esIndex;

  @ApiModelProperty(hidden = true)
  @Column(name = "create_time")
  private Date createTime;

  @ApiModelProperty(hidden = true)
  @Column(name = "`status`")
  private String status;

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getClusterIp() {
    return clusterIp;
  }

  public void setClusterIp(String clusterIp) {
    this.clusterIp = clusterIp;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getDriver() {
    return driver;
  }

  public void setDriver(String driver) {
    this.driver = driver;
  }

  public String getEsIndex() {
    return esIndex;
  }

  public void setEsIndex(String esIndex) {
    this.esIndex = esIndex;
  }
}
