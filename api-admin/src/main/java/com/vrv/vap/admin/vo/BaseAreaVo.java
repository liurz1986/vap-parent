package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryIn;
import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Column;

@ApiModel("地区查询条件")
public class BaseAreaVo extends Query{
    @QueryIn
    @ApiModelProperty("id")
    private String id;

    @QueryLike
    @ApiModelProperty("区域编码")
    private String areaCode;

    @QueryLike
    @ApiModelProperty("区域名称")
    @Column(name="area_name")
    private String areaName;

    @ApiModelProperty("区域编码")
    @Column(name="parent_code")
    private String parentCode;

    @ApiModelProperty("描述")
    private String description;

    @ApiModelProperty("上级区域")
    @Column(name="area_code_sub")
    private String areaCodeSub;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String code) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }


    public String getAreaCodeSub() {
        return areaCodeSub;
    }

    public void setAreaCodeSub(String areaCodeSub) {
        this.areaCodeSub = areaCodeSub;
    }
}
