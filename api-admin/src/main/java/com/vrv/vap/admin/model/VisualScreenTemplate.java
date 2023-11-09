package com.vrv.vap.admin.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2020/1/13
 * @description
 */
@Table(name = "visual_screen_template")
@ApiModel("标准化大屏模板")
public class VisualScreenTemplate {

    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("标题")
    private String title;

    @Column(name = "ui_state_json")
    @ApiModelProperty("组件配置")
    private String uiStateJson;

    @Column(name = "color_scheme")
    @ApiModelProperty("配色方案")
    private String colorScheme;

    @Column(name = "background_image")
    @ApiModelProperty("背景图片")
    private String backGroundImage;

    @ApiModelProperty("页面特效配置")
    private String effect;

    @ApiModelProperty("缩略图")
    private String thumbnail;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUiStateJson() {
        return uiStateJson;
    }

    public void setUiStateJson(String uiStateJson) {
        this.uiStateJson = uiStateJson;
    }

    public String getBackGroundImage() {
        return backGroundImage;
    }

    public void setBackGroundImage(String backGroundImage) {
        this.backGroundImage = backGroundImage;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public String getColorScheme() {
        return colorScheme;
    }

    public void setColorScheme(String colorScheme) {
        this.colorScheme = colorScheme;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }
}
