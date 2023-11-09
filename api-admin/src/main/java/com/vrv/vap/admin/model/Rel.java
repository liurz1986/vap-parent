package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

@Table(name = "discover_entity_index_rel")
public class Rel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 索引编号
     */
    @ApiModelProperty("索引编号")
    @Column(name = "index_id")
    private Integer indexId;

    /**
     * 实体编号
     */
    @ApiModelProperty("实体编号")
    @Column(name = "entity_id")
    private Integer entityId;

    /**
     * 字段
     */
    @ApiModelProperty("字段")
    private String field;

    /**
     * 字段描述
     */
    @Column(name = "field_description")
    @ApiModelProperty("字段描述")
    private String fieldDescription;

    /**
     * 备注
     */
    @ApiModelProperty("备注")
    private String remark;

    /**
     * 最后修改时间
     */
    @ApiModelProperty("最后修改时间")
    @Column(name = "last_update_time")
    private Date lastUpdateTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取索引编号
     *
     * @return index_id - 索引编号
     */
    public Integer getIndexId() {
        return indexId;
    }

    /**
     * 设置索引编号
     *
     * @param indexId 索引编号
     */
    public void setIndexId(Integer indexId) {
        this.indexId = indexId;
    }

    /**
     * 获取实体编号
     *
     * @return entity_id - 实体编号
     */
    public Integer getEntityId() {
        return entityId;
    }

    /**
     * 设置实体编号
     *
     * @param entityId 实体编号
     */
    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }

    /**
     * 获取字段
     *
     * @return field - 字段
     */
    public String getField() {
        return field;
    }

    /**
     * 设置字段
     *
     * @param field 字段
     */
    public void setField(String field) {
        this.field = field;
    }

    /**
     * 获取字段描述
     *
     * @return field_description - 字段描述
     */
    public String getFieldDescription() {
        return fieldDescription;
    }

    /**
     * 设置字段描述
     *
     * @param fieldDescription 字段描述
     */
    public void setFieldDescription(String fieldDescription) {
        this.fieldDescription = fieldDescription;
    }

    /**
     * 获取备注
     *
     * @return remark - 备注
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 备注
     */
    public void setRemark(String remark) {
        this.remark = remark;
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
}