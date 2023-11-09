package com.vrv.vap.admin.vo;


import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

@ApiModel("报表查询")
public class BaseReportVo extends Query {

  @ApiModelProperty("主键")
  private Integer id;

  @QueryLike
  @ApiModelProperty("名称")
  private String name;

  @QueryLike
  @ApiModelProperty("标题")
  private String title;

  @QueryLike
  @ApiModelProperty("副标题")
  private String subTitle;


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
}
