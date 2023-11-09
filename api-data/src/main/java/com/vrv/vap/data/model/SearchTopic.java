package com.vrv.vap.data.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "data_search_topic")
@ApiModel(value = "搜索主题配置")
public class SearchTopic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主题ID")
    private Integer id;

    @ApiModelProperty("类型 1: 分组 2: 数据源")
    private Byte type;

    @ApiModelProperty("主题名称")
    private String name;

    @Column(name = "parent_id")
    @ApiModelProperty("父主题ID 0=首层")
    private Integer parentId;

    @ApiModelProperty("主题内的数据源ID，以逗号区分")
    @Column(name = "source_ids")
    private String sourceIds;

    @ApiModelProperty("排序")
    private Short sort;

    @ApiModelProperty("状态 0 可用，1禁用")
    private Byte status;


    @Column(name = "role_ids")
    @ApiModelProperty("分配角色权限，以逗号区分")
    private String roleIds;

    private String filter;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Byte getType() {
        return type;
    }

    public void setType(Byte type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }

    public String getSourceIds() {
        return sourceIds;
    }

    public void setSourceIds(String sourceIds) {
        this.sourceIds = sourceIds;
    }

    public Short getSort() {
        return sort;
    }

    public void setSort(Short sort) {
        this.sort = sort;
    }

    public Byte getStatus() {
        return status;
    }

    public void setStatus(Byte status) {
        this.status = status;
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}