package com.vrv.vap.admin.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.*;
import java.util.Date;

/**
 * @author lilang
 * @date 2019/11/21
 * @description 仪表盘分享实体类
 */
@Table(name = "visual_dashboard_share")
@ApiModel("仪表盘分享")
public class DashboardShare {
    /**
     * 主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @ApiModelProperty("主键")
    private Integer id;

    @ApiModelProperty("仪表盘id")
    @Column(name = "dashboard_id")
    private Integer dashboardId;

    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("创建人")
    private String creator;

    @ApiModelProperty("创建时间")
    @Column(name = "create_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @ApiModelProperty("截止时间")
    @Column(name = "expire_time")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date expireTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDashboardId() {
        return dashboardId;
    }

    public void setDashboardId(Integer dashboardId) {
        this.dashboardId = dashboardId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(Date expireTime) {
        this.expireTime = expireTime;
    }
}
