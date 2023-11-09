package com.vrv.vap.admin.vo;


import com.vrv.vap.admin.util.ModelUtil;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Map;

@ApiModel("报表模块查询")
public class BaseReportModelVo extends Query {

  @ApiModelProperty("主键")
  private Integer id;

  @QueryLike
  @ApiModelProperty("查询语句")
  private String sql;

  @ApiModelProperty("类型")
  private String type;

  @QueryLike
  @ApiModelProperty("标题")
  private String title;

  @QueryLike
  @ApiModelProperty("参数")
  private String params;

  @QueryLike
  @ApiModelProperty("描述")
  private String description;

  @QueryLike
  @ApiModelProperty("模型内容")
  private String content;

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
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

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
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

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
