package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.annotations.LogDict;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModelProperty;

public class BaseLineQuery extends Query {
    @ApiModelProperty(value = "基线名称")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "算法配置")
    private String config;

    @LogDict("ccda3321-2ae4-7834-5cb4-70abeff68428")
    @ApiModelProperty(value = "基线类型")
    private String type;

    @ApiModelProperty(value = "是否计算群体基线")
    private String openGroup;

    @ApiModelProperty(value = "计算天数")
    private Integer days;

    @ApiModelProperty(value = "标识字段别名")
    private String alias;

    @ApiModelProperty(value = "标识字段含义")
    private String label;

    @ApiModelProperty(value = "正负范围倍数")
    private Integer multiple;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "描述")
    private String saveIndex;

    @ApiModelProperty(value = "描述")
    private String saveColumns;

    @ApiModelProperty(value = "描述")
    private String timeSlot;

    @ApiModelProperty(value = "描述")
    private String cron;

    @LogDict("c4cd270a-a31a-a1f9-c46a-ae0942e4a05e")
    @ApiModelProperty(value = "状态")
    private String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOpenGroup() {
        return openGroup;
    }

    public void setOpenGroup(String openGroup) {
        this.openGroup = openGroup;
    }

    public Integer getDays() {
        return days;
    }

    public void setDays(Integer days) {
        this.days = days;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getMultiple() {
        return multiple;
    }

    public void setMultiple(Integer multiple) {
        this.multiple = multiple;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSaveIndex() {
        return saveIndex;
    }

    public void setSaveIndex(String saveIndex) {
        this.saveIndex = saveIndex;
    }

    public String getSaveColumns() {
        return saveColumns;
    }

    public void setSaveColumns(String saveColumns) {
        this.saveColumns = saveColumns;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
}
