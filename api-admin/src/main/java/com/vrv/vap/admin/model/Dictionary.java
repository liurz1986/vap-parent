package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "discover_dictionary")
@ApiModel("数据字典对象")
public class Dictionary {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 字典名称
     */
    @Column(name = "index_name")
    @ApiModelProperty("字典名称")
    private String indexname;

    /**
     * 字典类型
     */
    @Column(name = "type")
    @ApiModelProperty("字典类型")
    private String type;

    /**
     * 字典id
     */
    @Column(name = "index_id")
    @ApiModelProperty("字典id")
    private String indexid;

    /**
     * 字典描述
     */
    @Column(name = "description")
    @ApiModelProperty("字典描述")
    private String description;

    /**
     * 详情
     */
    @Column(name = "details")
    @ApiModelProperty("详情")
    private String details;

    /**
     * 状态
     */
    @Column(name = "state")
    @ApiModelProperty("状态")
    private Integer state;

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
     * 获取字典名称
     *
     * @return indexName - 字典名称
     */
    public String getIndexname() {
        return indexname;
    }

    /**
     * 设置字典名称
     *
     * @param indexname 字典名称
     */
    public void setIndexname(String indexname) {
        this.indexname = indexname;
    }

    /**
     * 获取字典类型
     *
     * @return type - 字典类型
     */
    public String getType() {
        return type;
    }

    /**
     * 设置字典类型
     *
     * @param type 字典类型
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * 获取字典id
     *
     * @return indexId - 字典id
     */
    public String getIndexid() {
        return indexid;
    }

    /**
     * 设置字典id
     *
     * @param indexid 字典id
     */
    public void setIndexid(String indexid) {
        this.indexid = indexid;
    }

    /**
     * 获取字典描述
     *
     * @return description - 字典描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置字典描述
     *
     * @param description 字典描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取详情
     *
     * @return details - 详情
     */
    public String getDetails() {
        return details;
    }

    /**
     * 设置详情
     *
     * @param details 详情
     */
    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getState() { return state; }

    public void setState(Integer state) { this.state = state; }
}