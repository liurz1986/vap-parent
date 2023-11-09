package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2020/7/21
 * @description 概要实体类
 */
@Table(name = "discover_summarise")
@ApiModel("概要实体类")
public class DiscoverSummarise {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 索引名称
     */
    @Column(name = "index_id")
    @ApiModelProperty("索引名称")
    private String indexId;

    /**
     * 索引描述
     */
    @Column(name = "title_desc")
    @ApiModelProperty("概要描述")
    private String titleDesc;

    /**
     * 时间字段
     */
    @Column(name = "time_field_name")
    @ApiModelProperty("时间字段")
    private String timeFieldName;

    /**
     * 索引字段
     */
    @Column(name = "index_fields")
    @ApiModelProperty("索引字段")
    private String indexFields;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getTitleDesc() {
        return titleDesc;
    }

    public void setTitleDesc(String titleDesc) {
        this.titleDesc = titleDesc;
    }

    public String getTimeFieldName() {
        return timeFieldName;
    }

    public void setTimeFieldName(String timeFieldName) {
        this.timeFieldName = timeFieldName;
    }

    public String getIndexFields() {
        return indexFields;
    }

    public void setIndexFields(String indexFields) {
        this.indexFields = indexFields;
    }
}
