package com.vrv.vap.admin.vo;

import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2019/12/30
 * @description
 */
public class ExploreRecordQuery extends Query {

    @ApiModelProperty("账户")
    private String account;

    @ApiModelProperty("关键词")
    private String keyword;

    @ApiModelProperty("实体ID")
    private Integer entityId;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Integer getEntityId() {
        return entityId;
    }

    public void setEntityId(Integer entityId) {
        this.entityId = entityId;
    }
}
