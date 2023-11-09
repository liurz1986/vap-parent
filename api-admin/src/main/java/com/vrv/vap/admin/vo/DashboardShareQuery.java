package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;


/**
 * @author lilang
 * @date 2019/11/21
 * @description
 */
public class DashboardShareQuery extends Query {

    private Integer dashboardId;

    @QueryLike
    @ApiModelProperty("标题")
    private String title;

    @ApiModelProperty("token")
    private String token;

    @ApiModelProperty("创建人")
    private String creator;

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
}
