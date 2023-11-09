package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 * 基线源索引表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-21
 */
@ApiModel(value="BaseLineSource对象", description="基线源索引表")
public class BaseLineSource implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "标题")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String title;

    @ApiModelProperty(value = "索引")
    @TableField("`name`")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "时间字段")
    private String timeField;

    @ApiModelProperty(value = "类型：1 es 2 mysql")
    private String type;

    @ApiModelProperty(value = "时间字段格式")
    private String timeFormat;

    @ApiModelProperty(value = "描述")
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }
    public String getTimeFormat() {
        return timeFormat;
    }

    public void setTimeFormat(String timeFormat) {
        this.timeFormat = timeFormat;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "BaseLineSource{" +
            "id=" + id +
            ", title=" + title +
            ", name=" + name +
            ", timeField=" + timeField +
            ", timeFormat=" + timeFormat +
            ", description=" + description +
        "}";
    }
}
