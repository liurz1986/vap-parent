package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.*;
import java.util.Date;

@Table(name = "discover_sql_info")
public class DiscoverSqlInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 名称
     */
    private String name;

    /**
     * 保存时间
     */
    @Column(name = "create_time")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss",timezone="Asia/Shanghai")
    private Date createTime;

    /**
     * sql内容
     */
    @Column(name = "sql_info")
    private String sqlInfo;

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
     * 获取名称
     *
     * @return name - 名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 名称
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取保存时间
     *
     * @return create_time - 保存时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置保存时间
     *
     * @param createTime 保存时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取sql内容
     *
     * @return sql_info - sql内容
     */
    public String getSqlInfo() {
        return sqlInfo;
    }

    /**
     * 设置sql内容
     *
     * @param sqlInfo sql内容
     */
    public void setSqlInfo(String sqlInfo) {
        this.sqlInfo = sqlInfo;
    }
}