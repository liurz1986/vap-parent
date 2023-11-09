package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;


import javax.persistence.*;
import java.util.Date;

@Table(name = "short_message_log")
public class ShortMessageLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String uuid;

    /**
     * 用户ID
     */
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 用户名
     */
    @Column(name = "user_name")
    private String userName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 发送状态 0:发送成功,1:未配置手机号,2:登记成功,500:系统错误
     */
    private Integer status;

    /**
     * 发送内容
     */
    private String content;

    /**
     * 发送时间
     */
    @JsonFormat(pattern= "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "create_time")
    private Date createTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
