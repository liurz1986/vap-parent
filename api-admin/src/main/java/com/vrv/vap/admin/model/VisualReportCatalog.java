package com.vrv.vap.admin.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "visual_report_catalog")
public class VisualReportCatalog {
    /**
     * 报表分类ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 报表分类名称
     */
    private String name;

    /**
     * 报表分类排序
     */
    private Integer sort;

    /**
     * 获取报表分类ID
     *
     * @return id - 报表分类ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置报表分类ID
     *
     * @param id 报表分类ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取报表分类名称
     *
     * @return name - 报表分类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置报表分类名称
     *
     * @param name 报表分类名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取报表分类排序
     *
     * @return sort - 报表分类排序
     */
    public Integer getSort() {
        return sort;
    }

    /**
     * 设置报表分类排序
     *
     * @param sort 报表分类排序
     */
    public void setSort(Integer sort) {
        this.sort = sort;
    }
}