package com.vrv.vap.admin.model;

import javax.persistence.*;

/**
 * 用户喜好
 * 
 * @author 涂美政
 */
@Table(name = "user_preference")
public class UserPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userid;

    private String pages;

    @Column(name = "skin_class")
    private String skinClass;

    private String resolution;

    private String folders;

    private String modules;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getPages() {
        return pages;
    }

    public void setPages(String pages) {
        this.pages = pages;
    }

    public String getSkinClass() {
        return skinClass;
    }

    public void setSkinClass(String skinClass) {
        this.skinClass = skinClass;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }

    public String getFolders() {
        return folders;
    }

    public void setFolders(String folders) {
        this.folders = folders;
    }

    public String getModules() {
        return modules;
    }

    public void setModules(String modules) {
        this.modules = modules;
    }

}