package com.vrv.vap.line.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 策略配置表
 * </p>
 *
 * @author CodeGenerator
 * @since 2023-05-11
 */
@ApiModel(value="StrategyConfig对象", description="策略配置表")
public class StrategyConfig implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "")
    private String config;

    @ApiModelProperty(value = "入库索引")
    private String saveIndex;

    @ApiModelProperty(value = "入库字段配置")
    private String saveColumns;

    @ApiModelProperty(value = "字段信息")
    private String fields;

    @ApiModelProperty(value = "执行cron表达式")
    private String cron;

    @ApiModelProperty(value = "状态 0初始化 1启用 2停用")
    private String status;

    @ApiModelProperty(value = "对比信息")
    private String contrast;

    @ApiModelProperty(value = "描述")
    private String description;

    private String custom;

    private String customClass;

    @ApiModelProperty(value = "入库时间")
    private LocalDateTime insertTime;

    private String alias;

    private String ruleCode;

    private int day;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

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
    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
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
    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }
    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public String getContrast() {
        return contrast;
    }

    public void setContrast(String contrast) {
        this.contrast = contrast;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public LocalDateTime getInsertTime() {
        return insertTime;
    }

    public void setInsertTime(LocalDateTime insertTime) {
        this.insertTime = insertTime;
    }

    public String getCustom() {
        return custom;
    }

    public void setCustom(String custom) {
        this.custom = custom;
    }

    public String getCustomClass() {
        return customClass;
    }

    public void setCustomClass(String customClass) {
        this.customClass = customClass;
    }

    @Override
    public String toString() {
        return "StrategyConfig{" +
            "id=" + id +
            ", name=" + name +
            ", config=" + config +
            ", saveIndex=" + saveIndex +
            ", saveColumns=" + saveColumns +
            ", fields=" + fields +
            ", cron=" + cron +
            ", status=" + status +
            ", contrast=" + contrast +
            ", description=" + description +
            ", insertTime=" + insertTime +
        "}";
    }
}
