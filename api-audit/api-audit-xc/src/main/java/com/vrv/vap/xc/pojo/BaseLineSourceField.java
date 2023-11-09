package com.vrv.vap.xc.pojo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.UUID;

/**
 * <p>
 * 基线数据源字段表
 * </p>
 *
 * @author CodeGenerator
 * @since 2022-02-21
 */
@ApiModel(value="BaseLineSourceField对象", description="基线数据源字段表")
public class BaseLineSourceField implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "主键")
    @TableId(type = IdType.INPUT)
    private String id;

    @ApiModelProperty(value = "数据源id")
    private String sourceId;

    @ApiModelProperty(value = "字段名")
    private String field;

    @ApiModelProperty(value = "字段标题")
    @TableField("`name`")
    private String name;

    @ApiModelProperty(value = "类型，支持：keyword text long double date object json")
    private String type;

    @ApiModelProperty(value = "字段别名")
    private String alias;

    @ApiModelProperty(value = "格式")
    private String format;

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }
    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public BaseLineSourceField(String sourceId, String field, String type) {
        this.sourceId = sourceId;
        this.field = field;
        this.type = type;
    }

    public BaseLineSourceField(String sourceId, String field, String type,String format) {
        this.id = UUID.randomUUID().toString().replaceAll("-","");
        this.sourceId = sourceId;
        this.field = field;
        this.type = type;
        this.format = format;
    }

    public BaseLineSourceField(String sourceId, String field,String alias, String type,String format) {
        this.sourceId = sourceId;
        this.field = field;
        this.type = type;
        this.format = format;
        this.alias = alias;
    }

    public BaseLineSourceField() {
    }

    @Override
    public String toString() {
        return "BaseLineSourceField{" +
            "id=" + id +
            ", sourceId=" + sourceId +
            ", field=" + field +
            ", name=" + name +
            ", type=" + type +
            ", alias=" + alias +
        "}";
    }
}
