package com.vrv.vap.admin.vo;


import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("报表查询")
public class InterfaceVo extends Query {

  @QueryLike
  @ApiModelProperty("名称")
  private String name;



  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

}
