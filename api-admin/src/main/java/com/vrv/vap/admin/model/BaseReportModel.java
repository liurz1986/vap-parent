package com.vrv.vap.admin.model;


import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

import com.vrv.vap.admin.util.ModelUtil;
import com.vrv.vap.common.annotation.LogColumn;
import io.swagger.annotations.ApiModelProperty;

@Table(name = "base_report_model")
public class BaseReportModel {

  @Id
  @ApiModelProperty("主键")
  private String id;

  @Column(name = "`sql`")
  @ApiModelProperty(value = "查询语句",required = true)
  private String sql;

  @Column(name = "`type`")
  @ApiModelProperty(value = "类型,1：饼图，2：折线图，3：柱状图，4：表格, 5:段落 ,6:引用，7:列表",required = true)
  @LogColumn(mapping = "{\"1\":\"饼图\",\"2\":\"折线图\",\"3\":\"柱状图\",\"4\":\"表格\",\"5\":\"段落\",\"6\":\"引用\",\"7\":\"列表\"}")
  private String type;

  @Column(name = "params")
  @ApiModelProperty("参数")
  private String params;

  @Column(name = "title")
  @ApiModelProperty("标题")
  private String title;

  @Column(name = "description")
  @ApiModelProperty("描述")
  private String description;

  @Column(name = "content")
  @ApiModelProperty(value = "模型内容",required = true)
  private String content;

  @Column(name = "data_source_id")
  @ApiModelProperty(value = "数据源id：-1 mysql, -2 es")
  private Integer dataSourceId;

  @Column(name = "is_interface")
  @ApiModelProperty("数据类型是否是指标")
  private String isInterface;

  @Column(name = "interface_id")
  @ApiModelProperty("指标编号")
  private String interfaceId;

  @Column(name = "config")
  @ApiModelProperty("配置")
  private String config;

  @ApiModelProperty(hidden = true)
  private Map<String,Object> bindParam;

  public String getIsInterface() {
    return isInterface;
  }

  public void setIsInterface(String isInterface) {
    this.isInterface = isInterface;
  }

  public BaseReportModel() {
    this.bindParam = new HashMap<>();
  }

  public String getInterfaceId() {
    return interfaceId;
  }

  public void setInterfaceId(String interfaceId) {
    this.interfaceId = interfaceId;
  }

  public Map<String, Object> getBindParam() {
    return bindParam;
  }

  public void setBindParam(Map<String, Object> bindParam) {
    this.bindParam = bindParam;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSql() {
    return sql;
  }

  public void setSql(String sql) {
    this.sql = sql;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public Integer getDataSourceId() {
    return dataSourceId;
  }

  public void setDataSourceId(Integer dataSourceId) {
    this.dataSourceId = dataSourceId;
  }

  public String getConfig() {
    return config;
  }

  public void setConfig(String config) {
    this.config = config;
  }
}
