package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModelProperty;

public class BaseLineSourceQuery extends Query {
    @ApiModelProperty(value = "标题")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String title;

    @ApiModelProperty(value = "索引")
    @TableField("`name`")
    @QueryWapper(queryWapperEnum = QueryWapperEnum.LIKE)
    private String name;

    @ApiModelProperty(value = "时间字段")
    private String timeField;

    @ApiModelProperty(value = "时间字段格式")
    private String timeFormat;

    @ApiModelProperty(value = "数据源类型")
    private String type;

    @ApiModelProperty(value = "描述")
    private String description;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
