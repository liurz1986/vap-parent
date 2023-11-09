package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2022/1/5
 * @description
 */
public class CollectorRuleQuery extends Query {

    @ApiModelProperty("规则名称")
    @QueryLike
    private String name;

    @ApiModelProperty("规则集ID")
    private Integer collectionId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }
}
