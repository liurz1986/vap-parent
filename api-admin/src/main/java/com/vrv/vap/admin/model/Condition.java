package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "discover_condition")
@ApiModel("查询条件对象")
public class Condition {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 查询标题
     */
    @ApiModelProperty("查询标题")
    private String title;

    /**
     * 查询串
     */
    @Column(name = "query_jsonstr")
    @ApiModelProperty("查询串")
    private String queryStr;

    /**
     * 索引id
     */
    @Column(name = "index_id")
    @ApiModelProperty("索引id")
    private String indexId;

    /**
     * 搜索关键词
     */
    @Column(name = "search_key")
    @ApiModelProperty("搜索关键词")
    private String searchKey;

    /**
     * 搜索时间
     */
    @Column(name = "search_time")
    @ApiModelProperty("搜索时间")
    private String searchTime;

    /**
     * 搜索次数
     */
    @Column(name = "search_count")
    @ApiModelProperty("搜索次数")
    private Integer searchCount;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    @ApiModelProperty("开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @ApiModelProperty("结束时间")
    private String endTime;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取查询条件
     *
     * @return title - 查询条件
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置查询条件
     *
     * @param title 查询条件
     */
    public void setTitle(String title) {
        this.title = title;
    }


    public String getQueryStr() {
        return queryStr;
    }

    public void setQueryStr(String queryStr) {
        this.queryStr = queryStr;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(String searchTime) {
        this.searchTime = searchTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getSearchCount() {
        return searchCount;
    }

    public void setSearchCount(Integer searchCount) {
        this.searchCount = searchCount;
    }
}