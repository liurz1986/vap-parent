package com.vrv.vap.data.model;

import javax.persistence.*;

@Table(name = "data_screen")
public class Screen {
    /**
     * KEY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 大屏标题
     */
    private String title;

    /**
     * 全局时间范围
     */
    @Column(name = "time_restore")
    private String timeRestore;

    /**
     * 配色方案
     */
    @Column(name = "color_scheme")
    private String colorScheme;

    /**
     * 背景图片
     */
    @Column(name = "background_image")
    private String backgroundImage;

    /**
     * 页面特效
     */
    private String effect;

    /**
     * 缩略图
     */
    private String thumbnail;

    /**
     * 大屏UI配置
     */
    private String ui;

    /**
     * 获取KEY
     *
     * @return id - KEY
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置KEY
     *
     * @param id KEY
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取大屏标题
     *
     * @return title - 大屏标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置大屏标题
     *
     * @param title 大屏标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取全局时间范围
     *
     * @return time_restore - 全局时间范围
     */
    public String getTimeRestore() {
        return timeRestore;
    }

    /**
     * 设置全局时间范围
     *
     * @param timeRestore 全局时间范围
     */
    public void setTimeRestore(String timeRestore) {
        this.timeRestore = timeRestore;
    }

    /**
     * 获取配色方案
     *
     * @return color_scheme - 配色方案
     */
    public String getColorScheme() {
        return colorScheme;
    }

    /**
     * 设置配色方案
     *
     * @param colorScheme 配色方案
     */
    public void setColorScheme(String colorScheme) {
        this.colorScheme = colorScheme;
    }

    /**
     * 获取背景图片
     *
     * @return background_image - 背景图片
     */
    public String getBackgroundImage() {
        return backgroundImage;
    }

    /**
     * 设置背景图片
     *
     * @param backgroundImage 背景图片
     */
    public void setBackgroundImage(String backgroundImage) {
        this.backgroundImage = backgroundImage;
    }

    /**
     * 获取页面特效
     *
     * @return effect - 页面特效
     */
    public String getEffect() {
        return effect;
    }

    /**
     * 设置页面特效
     *
     * @param effect 页面特效
     */
    public void setEffect(String effect) {
        this.effect = effect;
    }

    /**
     * 获取缩略图
     *
     * @return thumbnail - 缩略图
     */
    public String getThumbnail() {
        return thumbnail;
    }

    /**
     * 设置缩略图
     *
     * @param thumbnail 缩略图
     */
    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    /**
     * 获取大屏UI配置
     *
     * @return ui - 大屏UI配置
     */
    public String getUi() {
        return ui;
    }

    /**
     * 设置大屏UI配置
     *
     * @param ui 大屏UI配置
     */
    public void setUi(String ui) {
        this.ui = ui;
    }
}