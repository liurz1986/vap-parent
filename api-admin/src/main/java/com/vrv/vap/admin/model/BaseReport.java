package com.vrv.vap.admin.model;


import com.vrv.vap.report.beetl.model.ItemModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;
import java.util.*;

@Table(name = "base_report")
public class BaseReport {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "`name`")
  @ApiModelProperty("报表名称")
  private String name;

  @Column(name = "title")
  @ApiModelProperty("报表标题")
  private String title;

  @Column(name = "sub_title")
  @ApiModelProperty("报表副标题")
  private String subTitle;

  @Column(name = "models")
  @ApiModelProperty(value = "报表模型组",required = true)
  private String models;

  @Column(name = "create_time")
  @ApiModelProperty(hidden = true)
  @Ignore
  private Date createTime;

  @Column(name = "`status`")
  @Ignore
  @ApiModelProperty(hidden = true)
  private String status;

  @Column(name = "menu_enable")
  @ApiModelProperty("是否启用目录")
  private Boolean menuEnable;

  /**
   * ：[{"name":"区域","field":"area"}]
   */
  @Column(name = "params")
  @ApiModelProperty("配置参数")
  private String params;

  @ApiModelProperty(hidden = true)
  @Ignore
  private List<ItemModel> body;

  @ApiModelProperty(hidden = true)
  private Map<String,Object> bindParam = new HashMap<>();

  public String getParams() {
    return params;
  }

  public void setParams(String params) {
    this.params = params;
  }

  public Map<String, Object> getBindParam() {
    return bindParam;
  }

  public void setBindParam(Map<String, Object> bindParam) {
    this.bindParam = bindParam;
  }

  public Boolean getMenuEnable() {
    return menuEnable;
  }

  public void setMenuEnable(Boolean menuEnable) {
    this.menuEnable = menuEnable;
  }

  public Integer getId() {
    return id;
  }

  public void setId(int id) {
        this.id = id;
    }

  public void setId(Integer id) {
    this.id = id;
  }

  public List<ItemModel> getBody() {
    return body;
  }

  public void setBody(List<ItemModel> body) {
    this.body = body;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }


  

  public String getSubTitle() {
    return subTitle;
  }

  public void setSubTitle(String subTitle) {
    this.subTitle = subTitle;
  }


  

  public String getModels() {
    return models;
  }

  public void setModels(String models) {
    this.models = models;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseReport that = (BaseReport) o;
        return id == that.id && Objects.equals(name, that.name) && Objects.equals(title, that.title) && Objects.equals(subTitle, that.subTitle) && Objects.equals(models, that.models) && Objects.equals(createTime, that.createTime) && Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, title, subTitle, models, createTime, status);
    }
}
