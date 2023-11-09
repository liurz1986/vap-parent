package com.vrv.vap.admin.model;

import javax.persistence.*;

/**
 * @author lilang
 * @date 2020/11/12
 * @description 用户证书关系表
 */
@Table(name = "user_ukey")
public class UserUkey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    private String serial;

    @Column(name = "public_key")
    private String publicKey;

    @Column(name = "certificate")
    private String ukeyCertificate;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getUkeyCertificate() {
        return ukeyCertificate;
    }

    public void setUkeyCertificate(String ukeyCertificate) {
        this.ukeyCertificate = ukeyCertificate;
    }
}
