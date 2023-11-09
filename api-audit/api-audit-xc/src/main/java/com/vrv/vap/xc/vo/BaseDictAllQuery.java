package com.vrv.vap.xc.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 * 字典表
 * </p>
 *
 * @author CodeGenerator
 * @since 2021-05-27
 */
@ApiModel(value="BaseDictAll对象", description="字典表")
public class BaseDictAllQuery extends Query {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @ApiModelProperty(value = "字典表编号")
    private String code;

    @ApiModelProperty(value = "字典表名称")
    private String codeValue;

    @ApiModelProperty(value = "字典表类型")
    private String type;

    @ApiModelProperty(value = "父类型")
    private String parentType;

    @ApiModelProperty(value = "是否是叶子节点")
    private String leaf;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "创建人ID")
    private String createId;

    @ApiModelProperty(value = "创建时间")
    private String createTime;

    @ApiModelProperty(value = "更新人ID")
    private String updateId;

    @ApiModelProperty(value = "更新时间")
    private String updateTime;

    @ApiModelProperty(value = "排序")
    private Integer sort;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
    public String getCodeValue() {
        return codeValue;
    }

    public void setCodeValue(String codeValue) {
        this.codeValue = codeValue;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
    public String getParentType() {
        return parentType;
    }

    public void setParentType(String parentType) {
        this.parentType = parentType;
    }
    public String getLeaf() {
        return leaf;
    }

    public void setLeaf(String leaf) {
        this.leaf = leaf;
    }
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getCreateId() {
        return createId;
    }

    public void setCreateId(String createId) {
        this.createId = createId;
    }
    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }
    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }
    public Integer getSort() {
        return sort;
    }

    public void setSort(Integer sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "BaseDictAllQuery{" +
            "id=" + id +
            ", code=" + code +
            ", codeValue=" + codeValue +
            ", type=" + type +
            ", parentType=" + parentType +
            ", leaf=" + leaf +
            ", description=" + description +
            ", createId=" + createId +
            ", createTime=" + createTime +
            ", updateId=" + updateId +
            ", updateTime=" + updateTime +
            ", sort=" + sort +
        "}";
    }
}
