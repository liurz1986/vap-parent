package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

@Table(name = "visual_widget")
@ApiModel("图形配置对象")
public class VisualWidgetModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private String id;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String title;


    /**
     * 索引名称
     */
    @Column(name = "index_name")
    @ApiModelProperty("索引名称")
    private String indexName;

    /**
     * 索引类型
     */
    @Column(name = "index_type")
    @ApiModelProperty("索引类型")
    private String indexType;


    /**
     * 索引id
     */
    @Column(name = "index_id")
    @ApiModelProperty("索引ID")
    private String indexId;

    /**
     * 图形类型
     */
    @Column(name = "visual_type")
    @ApiModelProperty("图形类型")
    private String visualType;

    /**
     * 查询ID
     */
    @Column(name = "saved_search_id")
    @ApiModelProperty("查询ID")
    private String savedSearchId;

    /**
     * UI状态
     */
    @Column(name = "ui_statejson")
    @ApiModelProperty("UI状态")
    private String uiStateJSON;

    /**
     * 图形配置
     */
    @Column(name = "vis_state")
    @ApiModelProperty("图形配置")
    private String visState;

    /**
     * 查询配置
     */
    @Column(name = "search_sourcejson")
    @ApiModelProperty("查询配置")
    private String searchSourceJSON;


    /**
     * 最后修改时间
     */
    @Column(name = "last_update_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    @ApiModelProperty("最后修改时间")
    private Date lastUpdateTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public String getSavedSearchId() {
        return savedSearchId;
    }

    public void setSavedSearchId(String savedSearchId) {
        this.savedSearchId = savedSearchId;
    }

    public String getUiStateJSON() {
        return uiStateJSON;
    }

    public void setUiStateJSON(String uiStateJSON) {
        this.uiStateJSON = uiStateJSON;
    }

    public String getVisState() {
        return visState;
    }

    public void setVisState(String visState) {
        this.visState = visState;
    }


    public String getSearchSourceJSON() {
        return searchSourceJSON;
    }

    public void setSearchSourceJSON(String searchSourceJSON) {
        this.searchSourceJSON = searchSourceJSON;
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String getVisualType() {
        return visualType;
    }

    public void setVisualType(String visualType) {
        this.visualType = visualType;
    }

    public String getIndexType() {
        return indexType;
    }

    public void setIndexType(String indexType) {
        this.indexType = indexType;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public Date getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(Date lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

}
