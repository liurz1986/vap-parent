package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.annotations.LogDict;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.xc.model.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 动态基线表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-10
 */
@ApiModel(value="BaseLine对象", description="动态基线表")
public class BaseLine extends BaseModel {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "名称")
    @TableField("`name`")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    //@ApiModelProperty(value = "算法配置	[{		"indexId": "源索引id",		"column": "name",		"filter": "name",		"type": "1",		"value": "",		"calculation": [{			"column": "ip",			"algorithm": "1",			"agg": "true",			"aggLevel": "true"		}]	}]")
    private String config;

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
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String description;

    @ApiModelProperty(value = "入库索引")
    private String saveIndex;

    @ApiModelProperty(value = "入库字段")
    private String saveColumns;

    @LogDict("4ee0f79d-c32f-03dc-6aa1-2f3960a9b20f")
    @ApiModelProperty(value = "入库类型（1：es；2：kafka；3：es+kafka）")
    private String saveType;

    @ApiModelProperty(value = "入库字段")
    private String fields;

    @ApiModelProperty(value = "数据源类型1:es 2:mysql")
    private String sourceType;

    @ApiModelProperty(value = "时段")
    private String timeSlot;

    @ApiModelProperty(value = "cron表达式")
    private String cron;

    @LogDict("c4cd270a-a31a-a1f9-c46a-ae0942e4a05e")
    @ApiModelProperty(value = "状态 0初始化 1启用 2停用")
    private String status;

    @ApiModelProperty(value = "特殊模型参数")
    private String specialParam;

    @ApiModelProperty(value = "运行状态")
    private String workStatus;

    @ApiModelProperty(value = "运行状态说明")
    private String workMsg;

    @ApiModelProperty(value = "数据保存天数")
    private Integer saveDays;

    @ApiModelProperty(value = "统计次数")
    private Integer summaryNum;

    @ApiModelProperty(value = "数据周期")
    private Integer dataCycle;

    @ApiModelProperty(value = "运行次数")
    private Integer runNum;

    public Integer getRunNum() {
        return runNum;
    }

    public void setRunNum(Integer runNum) {
        this.runNum = runNum;
    }

    public Integer getDataCycle() {
        return dataCycle;
    }

    public void setDataCycle(Integer dataCycle) {
        this.dataCycle = dataCycle;
    }

    public String getWorkMsg() {
        return workMsg;
    }

    public void setWorkMsg(String workMsg) {
        this.workMsg = workMsg;
    }

    public String getWorkStatus() {
        return workStatus;
    }

    public void setWorkStatus(String workStatus) {
        this.workStatus = workStatus;
    }

    public Integer getSummaryNum() {
        return summaryNum;
    }

    public void setSummaryNum(Integer summaryNum) {
        this.summaryNum = summaryNum;
    }

    public String getSpecialParam() {
        return specialParam;
    }

    public void setSpecialParam(String specialParam) {
        this.specialParam = specialParam;
    }

    private Integer specialId;

    public String getFields() {
        return fields;
    }

    public void setFields(String fields) {
        this.fields = fields;
    }

    public Integer getSpecialId() {
        return specialId;
    }

    public void setSpecialId(Integer specialId) {
        this.specialId = specialId;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSaveType() {
        return saveType;
    }

    public void setSaveType(String saveType) {
        this.saveType = saveType;
    }

    public String getSourceType() {
        return sourceType;
    }

    public void setSourceType(String sourceType) {
        this.sourceType = sourceType;
    }


    public Integer getSaveDays() {
        return saveDays;
    }

    public void setSaveDays(Integer saveDays) {
        this.saveDays = saveDays;
    }

    @Override
    public String toString() {
        return "BaseLine{" +
            "id=" + id +
            ", name=" + name +
            ", config=" + config +
            ", type=" + type +
            ", openGroup=" + openGroup +
            ", days=" + days +
            ", alias=" + alias +
            ", label=" + label +
            ", multiple=" + multiple +
            ", description=" + description +
        "}";
    }


}
