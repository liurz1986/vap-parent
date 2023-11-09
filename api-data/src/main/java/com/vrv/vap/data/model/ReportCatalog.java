package com.vrv.vap.data.model;

import javax.persistence.*;

@Table(name = "data_report_catalog")
public class ReportCatalog {
    /**
     * KEY
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 分类名称
     */
    private String name;

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
     * 获取分类名称
     *
     * @return name - 分类名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置分类名称
     *
     * @param name 分类名称
     */
    public void setName(String name) {
        this.name = name;
    }
}