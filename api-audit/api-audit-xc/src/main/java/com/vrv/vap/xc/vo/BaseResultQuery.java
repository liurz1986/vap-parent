package com.vrv.vap.xc.vo;

import com.vrv.vap.toolkit.plugin.QueryWapper;
import com.vrv.vap.toolkit.plugin.QueryWapperEnum;
import com.vrv.vap.toolkit.vo.Query;
import io.swagger.annotations.ApiModelProperty;

public class BaseResultQuery extends Query {
    @ApiModelProperty("基线标识")
    private int baseLineId;

    public int getBaseLineId() {
        return baseLineId;
    }

    public void setBaseLineId(int baseLineId) {
        this.baseLineId = baseLineId;
    }
}
