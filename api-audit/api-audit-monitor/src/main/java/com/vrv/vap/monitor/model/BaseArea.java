package com.vrv.vap.monitor.model;

import io.swagger.annotations.ApiModelProperty;

public class BaseArea {
    /**
     */
    private Integer id;

    /**
     *   地区编码
     */
    @ApiModelProperty("地区编码")
    private String areaCode;

    /**
     *   地区名称
     */
    @ApiModelProperty("地区名称")
    private String areaName;

    /**
     *   上级编号
     */
    @ApiModelProperty("上级编号")
    private String parentCode;

    /**
     *   描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     *   截取编码（确认地区）
     */
    @ApiModelProperty("截取编码（确认地区）")
    private String areaCodeSub;

    /**
     *   排序
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     *   ip范围
     */
    @ApiModelProperty("ip范围")
    private String ipRange;

    /**
     *   可分析
     */
    @ApiModelProperty("可分析")
    private Integer available;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAreaCodeSub() {
        return areaCodeSub;
    }

    public void setAreaCodeSub(String areaCodeSub) {
        this.areaCodeSub = areaCodeSub;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getIpRange() {
        return ipRange;
    }

    public void setIpRange(String ipRange) {
        this.ipRange = ipRange;
    }

    public Integer getAvailable() {
        return available;
    }

    public void setAvailable(Integer available) {
        this.available = available;
    }
}