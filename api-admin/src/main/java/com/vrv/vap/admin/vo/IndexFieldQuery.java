package com.vrv.vap.admin.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("索引字段查询对象")
public class IndexFieldQuery {
/**
 *@author lilang
 *@date 2018/9/10
 *@description
 */
    //主键
    @ApiModelProperty("主键")
    public Integer id;
    //索引字段名称
    @ApiModelProperty("索引字段名称")
    public String name;
    //索引字段描述
    @ApiModelProperty("索引字段描述")
    public String nameDesc;
    //数据字典key
    @ApiModelProperty("是否格式化")
    public String format;
    //是否过滤
    @ApiModelProperty("是否过滤")
    public String filtered;
    //是否显示
    @ApiModelProperty("是否显示")
    public String displayed;
    //开始时间
    @ApiModelProperty("开始时间")
    public String startTime;
    //结束时间
    @ApiModelProperty("结束时间")
    public String endTime;
    //档案类型
    @ApiModelProperty("档案类型")
    public String linkType;
    //是否标签
    @ApiModelProperty("是否标签")
    public String tag;
    //序号
    @ApiModelProperty("序号")
    public Integer sort;
    //单位转换
    @ApiModelProperty("单位转换")
    public String unit;
    //是否显示详情
    @ApiModelProperty("是否显示详情")
    public String detailed;

    @ApiModelProperty("字段大小")
    public Integer size;

    @ApiModelProperty("字段类型")
    public String type;

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

    public String getNameDesc() {
        return nameDesc;
    }

    public void setNameDesc(String nameDesc) {
        this.nameDesc = nameDesc;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getFiltered() {
        return filtered;
    }

    public void setFiltered(String filtered) {
        this.filtered = filtered;
    }

    public String getDisplayed() {
        return displayed;
    }

    public void setDisplayed(String displayed) {
        this.displayed = displayed;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getDetailed() {
        return detailed;
    }

    public void setDetailed(String detailed) {
        this.detailed = detailed;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
