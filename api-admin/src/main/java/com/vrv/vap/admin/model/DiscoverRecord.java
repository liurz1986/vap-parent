package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

@Table(name = "discover_record")
public class DiscoverRecord {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    /**
     * 索引
     */
    @Column(name = "index_id")
    @ApiModelProperty("索引")
    private String indexId;

    /**
     * 用户账号
     */
    @Column(name = "account")
    @ApiModelProperty("用户账号")
    private String account;

    /**
     * 权限ID
     */
    @Column(name = "role_id")
    @ApiModelProperty("权限ID")
    private String roleId;

    /**
     * 查询关键字
     */
    @Column(name = "search_key")
    @ApiModelProperty("查询关键字")
    private String searchKey;

    /**
     * 查询源queryStr
     */
    @Column(name = "search_source")
    @ApiModelProperty("查询源queryStr")
    private String searchSource;

    /**
     * 搜索时间
     */
    @Column(name = "search_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty("搜索时间")
    private Date searchTime;

    /**
     * 开始时间
     */
    @Column(name = "start_time")
    @ApiModelProperty("开始时间")
    private String startTime;

    /**
     * 结束时间
     */
    @Column(name = "end_time")
    @ApiModelProperty("结束时间")
    private String endTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public String getSearchKey() {
        return searchKey;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

    public String getSearchSource() {
        return searchSource;
    }

    public void setSearchSource(String searchSource) {
        this.searchSource = searchSource;
    }

    public Date getSearchTime() {
        return searchTime;
    }

    public void setSearchTime(Date searchTime) {
        this.searchTime = searchTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }
}