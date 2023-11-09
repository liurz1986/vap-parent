package com.vrv.vap.data.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "data_discover_edge")
@ApiModel(value = "探索关系定义")
public class DiscoverEdge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;


    @ApiModelProperty("关系名称")
    private String name;

    @ApiModelProperty("关系描述")
    private String description;

    @ApiModelProperty("数据源")
    @Column(name = "source_id")
    private Integer sourceId;

    @ApiModelProperty("搜索字段")
    @Column(name = "search_field")
    private String searchField;

    @ApiModelProperty("搜索实体编号")
    @Column(name = "search_entity_id")
    private Integer searchEntityId;


    @ApiModelProperty("目标字段")
    @Column(name = "goal_field")
    private String goalField;

    @ApiModelProperty("目标实体编号")
    @Column(name = "goal_entity_id")
    private Integer goalEntityId;

    @ApiModelProperty("数据方向：0-正向，1-反向")
    private Boolean reverse;

    @ApiModelProperty("对象图标")
    private String icon;

    @ApiModelProperty("目标字段聚合 0：否，1：是")
    private Boolean agg;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSourceId() {
        return sourceId;
    }

    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    public String getSearchField() {
        return searchField;
    }

    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    public Integer getSearchEntityId() {
        return searchEntityId;
    }

    public void setSearchEntityId(Integer searchEntityId) {
        this.searchEntityId = searchEntityId;
    }

    public String getGoalField() {
        return goalField;
    }

    public void setGoalField(String goalField) {
        this.goalField = goalField;
    }

    public Integer getGoalEntityId() {
        return goalEntityId;
    }

    public void setGoalEntityId(Integer goalEntityId) {
        this.goalEntityId = goalEntityId;
    }

    public Boolean getReverse() {
        return reverse;
    }

    public void setReverse(Boolean reverse) {
        this.reverse = reverse;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getAgg() {
        return agg;
    }

    public void setAgg(Boolean agg) {
        this.agg = agg;
    }
}