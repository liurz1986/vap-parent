package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "visual_storyboard")
@ApiModel("故事板对象")
public class Storyboard {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 标题
     */
    @Column(name = "title")
    @ApiModelProperty("标题")
    private String title;

    /**
     * 描述
     */
    @Column(name = "description")
    @ApiModelProperty("描述")
    private String description;

    /**
     * 图片id
     */
    @Column(name = "image_id")
    @ApiModelProperty("图片ID")
    private Integer imageId;

    /**
     * 是否首页
     */
    @Column(name = "first_flag")
    @ApiModelProperty("首页标记")
    private Integer firstFlag;

    /**
     * uiJSON对象
     */
    @Column(name = "ui_json")
    @ApiModelProperty("UI状态")
    private String uiJson;

    /**
     * 类型
     */
    @Column(name = "type")
    @ApiModelProperty("故事板类型")
    private Integer type;

    /**
     * 所属分组
     */
    @Column(name = "parent_id")
    @ApiModelProperty("所属分组")
    private Integer parentId;

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
     * 获取名称
     *
     * @return title - 名称
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置名称
     *
     * @param title 名称
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取描述
     *
     * @return description - 描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 设置描述
     *
     * @param description 描述
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 获取图片id
     *
     * @return image_id - 图片id
     */
    public Integer getImageId() {
        return imageId;
    }

    /**
     * 设置图片id
     *
     * @param imageId 图片id
     */
    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }

    /**
     * 获取是否首页
     *
     * @return first_flag - 是否首页
     */
    public Integer getFirstFlag() {
        return firstFlag;
    }

    /**
     * 设置是否首页
     *
     * @param firstFlag 是否首页
     */
    public void setFirstFlag(Integer firstFlag) {
        this.firstFlag = firstFlag;
    }

    /**
     * 获取uiJSON对象
     *
     * @return ui_json - uiJSON对象
     */
    public String getUiJson() {
        return uiJson;
    }

    /**
     * 设置uiJSON对象
     *
     * @param uiJson uiJSON对象
     */
    public void setUiJson(String uiJson) {
        this.uiJson = uiJson;
    }


    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getParentId() {
        return parentId;
    }

    public void setParentId(Integer parentId) {
        this.parentId = parentId;
    }
}