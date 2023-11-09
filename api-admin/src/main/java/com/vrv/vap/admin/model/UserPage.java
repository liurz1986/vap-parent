package com.vrv.vap.admin.model;

import javax.persistence.*;

@Table(name = "user_page")
public class UserPage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userid;

    private String pages;

    private String reside;


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


    public String getReside() {
        return reside;
    }

    public void setReside(String reside) {
        this.reside = reside;
    }
}