package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2019/12/11
 * @description 标准化大屏查询对象
 */
public class VisualScreenQuery extends Query {

    @ApiModelProperty("主键")
    private Integer id;

    @QueryLike
    @ApiModelProperty("标题")
    private String title;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
