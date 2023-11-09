package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;

public class EdgeQuery extends Query {

    /**
     * 关系名称
     */
    @QueryLike
    @ApiModelProperty("关系名称")
    private String name;

    /**
     * 关系描述
     */
    @QueryLike
    @ApiModelProperty("关系描述")
    private String description;

    /**
     * 索引名称
     */
    @QueryLike
    @ApiModelProperty("索引名称")
    private String indexName;

    /**
     * 时间字段
     */
    @ApiModelProperty("时间字段")
    private String timeField;

    /**
     * 搜索字段
     */
    @QueryLike
    @ApiModelProperty("搜索字段")
    private String searchField;

    /**
     * 搜索实体编号
     */
    @ApiModelProperty("搜索实体编号")
    private Integer searchEntityId;

    /**
     * 目标字段
     */
    @QueryLike
    @ApiModelProperty("目标字段")
    private String goalField;

    /**
     * 目标实体编号
     */
    @ApiModelProperty("目标实体编号")
    private Integer goalEntityId;

    /**
     * 关系类型：0-正向，1-反向
     */
    @ApiModelProperty("关系类型")
    private String type;

    /**
     * 图标路径
     */
    @ApiModelProperty("图标路径")
    private String icoId;

    /**
     * 最后修改时间
     */
    @ApiModelProperty("最后修改时间")
    private Date lastUpdateTime;

    /**
     * 获取关系名称
     *
     * @return name - 关系名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置关系名称
     *
     * @param name 关系名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取关系描述
     *
     * @return description - 关系描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置关系描述
     *
     * @param description 关系描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取索引名称
     *
     * @return index_name - 索引名称
     */
    public String getIndexName() {
        return indexName;
    }

    /**
     * 设置索引名称
     *
     * @param indexName 索引名称
     */
    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    /**
     * 获取搜索字段
     *
     * @return search_field - 搜索字段
     */
    public String getSearchField() {
        return searchField;
    }

    /**
     * 设置搜索字段
     *
     * @param searchField 搜索字段
     */
    public void setSearchField(String searchField) {
        this.searchField = searchField;
    }

    /**
     * 获取搜索实体编号
     *
     * @return search_entity_id - 搜索实体编号
     */
    public Integer getSearchEntityId() {
        return searchEntityId;
    }

    /**
     * 设置搜索实体编号
     *
     * @param searchEntityId 搜索实体编号
     */
    public void setSearchEntityId(Integer searchEntityId) {
        this.searchEntityId = searchEntityId;
    }

    /**
     * 获取目标字段
     *
     * @return goal_field - 目标字段
     */
    public String getGoalField() {
        return goalField;
    }

    /**
     * 设置目标字段
     *
     * @param goalField 目标字段
     */
    public void setGoalField(String goalField) {
        this.goalField = goalField;
    }

    /**
     * 获取目标实体编号
     *
     * @return goal_entity_id - 目标实体编号
     */
    public Integer getGoalEntityId() {
        return goalEntityId;
    }

    /**
     * 设置目标实体编号
     *
     * @param goalEntityId 目标实体编号
     */
    public void setGoalEntityId(Integer goalEntityId) {
        this.goalEntityId = goalEntityId;
    }

    /**
     * 获取关系类型：0-正向，1-反向
     *
     * @return type - 关系类型：0-正向，1-反向
     */
    public String getType() {
        return type;
    }

    /**
     * 设置关系类型：0-正向，1-反向
     *
     * @param type 关系类型：0-正向，1-反向
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取图标路径
     *
     * @return ico_id - 图标路径
     */
    public String getIcoId() {
        return icoId;
    }

    /**
     * 设置图标路径
     *
     * @param icoId 图标路径
     */
    public void setIcoId(String icoId) {
        this.icoId = icoId;
    }

    /**
     * 获取最后修改时间
     *
     * @return last_update_time - 最后修改时间
     */
    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    /**
     * 设置最后修改时间
     *
     * @param lastUpdateTime 最后修改时间
     */
    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getTimeField() {
        return timeField;
    }

    public void setTimeField(String timeField) {
        this.timeField = timeField;
    }
}