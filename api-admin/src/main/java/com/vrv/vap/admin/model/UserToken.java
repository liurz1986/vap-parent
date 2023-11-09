package com.vrv.vap.admin.model;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2021/6/21
 * @description
 */
@Table(name = "user_token")
public class UserToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guid;

    @Column(name = "user_card")
    private String userCard;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "bios_userName")
    private String biosUserName;

    @Column(name = "admin_name")
    private String adminName;

    @Column(name = "admin_code")
    private String adminCode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getUserCard() {
        return userCard;
    }

    public void setUserCard(String userCard) {
        this.userCard = userCard;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBiosUserName() {
        return biosUserName;
    }

    public void setBiosUserName(String biosUserName) {
        this.biosUserName = biosUserName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getAdminCode() {
        return adminCode;
    }

    public void setAdminCode(String adminCode) {
        this.adminCode = adminCode;
    }
}
