package com.vrv.vap.data.model;

import javax.persistence.*;

@Table(name = "data_report")
public class Report {
    /**
     * 报告ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 标题
     */
    private String title;

    /**
     * 副标题
     */
    @Column(name = "sub_title")
    private String subTitle;

    /**
     * 主题ID
     */
    @Column(name = "theme_id")
    private Integer themeId;

    /**
     * 所属分类
     */
    @Column(name = "catalog_id")
    private Integer catalogId;

    /**
     * 默认时间
     */
    @Column(name = "time_restore")
    private String timeRestore;

    /**
     * 数据模板
     */
    private String ui;

    /**
     * 参数
     */
    private String param;

    /**
     * 全局数据集
     */
    private String dataset;

    /**
     * 获取报告ID
     *
     * @return id - 报告ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置报告ID
     *
     * @param id 报告ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取标题
     *
     * @return title - 标题
     */
    public String getTitle() {
        return title;
    }

    /**
     * 设置标题
     *
     * @param title 标题
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * 获取副标题
     *
     * @return sub_title - 副标题
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * 设置副标题
     *
     * @param subTitle 副标题
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    /**
     * 获取主题ID
     *
     * @return theme_id - 主题ID
     */
    public Integer getThemeId() {
        return themeId;
    }

    /**
     * 设置主题ID
     *
     * @param themeId 主题ID
     */
    public void setThemeId(Integer themeId) {
        this.themeId = themeId;
    }

    /**
     * 获取所属分类
     *
     * @return catalog_id - 所属分类
     */
    public Integer getCatalogId() {
        return catalogId;
    }

    /**
     * 设置所属分类
     *
     * @param catalogId 所属分类
     */
    public void setCatalogId(Integer catalogId) {
        this.catalogId = catalogId;
    }

    /**
     * 获取默认时间
     *
     * @return time_restore - 默认时间
     */
    public String getTimeRestore() {
        return timeRestore;
    }

    /**
     * 设置默认时间
     *
     * @param timeRestore 默认时间
     */
    public void setTimeRestore(String timeRestore) {
        this.timeRestore = timeRestore;
    }

    /**
     * 获取数据模板
     *
     * @return ui - 数据模板
     */
    public String getUi() {
        return ui;
    }

    /**
     * 设置数据模板
     *
     * @param ui 数据模板
     */
    public void setUi(String ui) {
        this.ui = ui;
    }

    /**
     * 获取参数
     *
     * @return param - 参数
     */
    public String getParam() {
        return param;
    }

    /**
     * 设置参数
     *
     * @param param 参数
     */
    public void setParam(String param) {
        this.param = param;
    }

    /**
     * 获取全局数据集
     *
     * @return dataset - 全局数据集
     */
    public String getDataset() {
        return dataset;
    }

    /**
     * 设置全局数据集
     *
     * @param dataset 全局数据集
     */
    public void setDataset(String dataset) {
        this.dataset = dataset;
    }
}