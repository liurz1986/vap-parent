package com.vrv.vap.data.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;


@ApiModel(value = "数据源查询参数")
public class SourceQuery extends Query {

    @QueryLike
    @ApiModelProperty("数据源名称")
    private String name;

    @QueryLike
    @ApiModelProperty("数据源标题")
    private String title;

    @ApiModelProperty("数据源类型")
    private Byte type;

    @ApiModelProperty("数据类型")
    private Integer dataType;

    @ApiModelProperty("主题别名")
    private String topicAlias;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public Integer getDataType() {
        return dataType;
    }

    public void setDataType(Integer dataType) {
        this.dataType = dataType;
    }

    public String getTopicAlias() {
        return topicAlias;
    }

    public void setTopicAlias(String topicAlias) {
        this.topicAlias = topicAlias;
    }
}
