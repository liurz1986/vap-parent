package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2020/7/21
 * @description
 */
public class SummariseQuery extends Query {

    @ApiModelProperty("索引名称")
    @QueryLike
    private String indexId;

    @ApiModelProperty("索引描述")
    @QueryLike
    private String titleDesc;

    public String getIndexId() {
        return indexId;
    }

    public void setIndexId(String indexId) {
        this.indexId = indexId;
    }

    public String getTitleDesc() {
        return titleDesc;
    }

    public void setTitleDesc(String titleDesc) {
        this.titleDesc = titleDesc;
    }
}
