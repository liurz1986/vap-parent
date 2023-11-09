package com.vrv.vap.data.model;

import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;

@Table(name = "data_dashboard")
public class Dashboard {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 仪表盘标题
     */
    @ApiModelProperty("仪表盘标题")
    private String title;

    /**
     * 全局时间范围
     */
    @Column(name = "time_restore")
    @ApiModelProperty("全局时间范围")
    private String timeRestore;

    /**
     * 缩略图
     */
    @ApiModelProperty("缩略图")
    private String thumbnail;

    /**
     * 排序
     */
    @ApiModelProperty("排序")
    private Integer sort;

    /**
     * 置顶
     */
    @ApiModelProperty("置顶")
    @Column(name = "top")
    private Integer top;

    /**
     * 仪表盘配置
     */
    @ApiModelProperty("仪表盘配置")
    @Column(name = "ui")
    private String ui;

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
     * 获取仪表盘标题
     *
     * @return title - 仪表盘标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置仪表盘标题
     *
     * @param title 仪表盘标题
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
     * 获取排序
     *
     * @return sort - 排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }

    /**
     * 获取仪表盘配置
     *
     * @return ui - 仪表盘配置
     */
    public String getUi() {
        return ui;
    }

    /**
     * 设置仪表盘配置
     *
     * @param ui 仪表盘配置
     */
    public void setUi(String ui) {
        this.ui = ui;
    }

    public Integer getTop() {
        return top;
    }

    public void setTop(Integer top) {
        this.top = top;
    }
}