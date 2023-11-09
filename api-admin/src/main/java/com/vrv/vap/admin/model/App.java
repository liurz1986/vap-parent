package com.vrv.vap.admin.model;

import java.util.Date;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import jdk.nashorn.internal.ir.annotations.Ignore;

public class App {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "client_id")
    @ApiModelProperty(value = "服务名称")
    @Ignore
    private String clientid = "";

    @Column(name = "client_secret")
    @ApiModelProperty(value = "服务秘钥")
    private String clientsecret = "";

    private String scope = "";

    @Column(name = "authorized_grant_types")
    private String authorizedgranttypes = "";

    @ApiModelProperty(value = "应用名称")
    private String name = "";

    private String icon = "";

    private Byte type;

    @ApiModelProperty(value = "应用路径")
    private String url = "";

    private Byte third;

    private Byte folder;

    private Byte status; 

    private String searchInfo;

    @Column(name = "createTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createtime;

    @Column(name = "updateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date updatetime;

    @Column(name = "parent_id")
    private int parentId = 0;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    /**
     * @return type
     */
    public Byte getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * @return url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * @return status
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * @param status
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * @return createTime
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * @return updateTime
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * @param updatetime
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * @return parent_id
     */
    public int getParentId() {
        return parentId;
    }

    /**
     * @param parentId
     */
    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public String getClientid() {
        return clientid;
    }

    public void setClientid(String clientid) {
        this.clientid = clientid;
    }

    public String getClientsecret() {
        return clientsecret;
    }

    public void setClientsecret(String clientsecret) {
        this.clientsecret = clientsecret;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getAuthorizedgranttypes() {
        return authorizedgranttypes;
    }


    public void setAuthorizedgranttypes(String authorizedgranttypes) {
        this.authorizedgranttypes = authorizedgranttypes;
    }

    public Byte getThird() {
        return third;
    }

    public void setThird(Byte third) {
        this.third = third;
    }

    public Byte getFolder() {
        return folder;
    }

    public void setFolder(Byte folder) {
        this.folder = folder;
    }
  
    public String getSearchInfo() {
        return searchInfo;
    }

    public void setSearchInfo(String searchInfo) {
        this.searchInfo = searchInfo;
    }

}