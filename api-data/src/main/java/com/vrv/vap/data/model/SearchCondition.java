package com.vrv.vap.data.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

import javax.persistence.*;

@Table(name = "data_search_condition")
@ApiModel(value = "保存的查询条件")
public class SearchCondition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ApiModelProperty("条件命名")
    private String name;


    @Column(name = "user_id")
    @ApiModelProperty("用户")
    private Integer userId;

    @ApiModelProperty("输入的关键字")
    private String q;


    @Column(name = "start_time")
    @ApiModelProperty("时间范围-开始时间")
    private String startTime;


    @Column(name = "end_time")
    @ApiModelProperty("时间范围-结束时间")
    private String endTime;

    @Column(name = "topic_id")
    @ApiModelProperty("选择的主题")
    @Ignore
    private Integer topicId;

    @Column(name = "source_ids")
    @ApiModelProperty("选择的索引")
    @Ignore
    private String sourceIds;

    @ApiModelProperty("过滤条件")
    private String filter;

    @Transient
    @ApiModelProperty("主题名称")
    private String topicName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQ() {
        return q;
    }

    public void setQ(String q) {
        this.q = q;
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

    public Integer getTopicId() {
        return topicId;
    }

    public void setTopicId(Integer topicId) {
        this.topicId = topicId;
    }

    public String getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(String sourceIds) {
        this.sourceIds = sourceIds;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getTopicName() {
        return topicName;
    }

    public void setTopicName(String topicName) {
        this.topicName = topicName;
    }
}