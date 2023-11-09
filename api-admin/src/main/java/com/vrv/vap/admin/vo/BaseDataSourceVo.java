package com.vrv.vap.admin.vo;


import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

@ApiModel("数据源查询")
public class BaseDataSourceVo extends Query {
  @ApiModelProperty("主键")
  private Integer id;

  @QueryLike
  @ApiModelProperty("名称")
  private String name;

  @QueryLike
  @ApiModelProperty("链接")
  private String url;

  @ApiModelProperty("类型")
  private String type;


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

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }
}
