package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2020/7/23
 * @description
 */
public class ReportQuery extends Query {

    @ApiModelProperty("ID")
    private String id;

    @ApiModelProperty("标题")
    @QueryLike
    private String title;

    @ApiModelProperty("副标题")
    @QueryLike
    private String subTitle;

    @ApiModelProperty("目录ID")
    private String catalogId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getCatalogId() {
        return catalogId;
    }

    public void setCatalogId(String catalogId) {
        this.catalogId = catalogId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
