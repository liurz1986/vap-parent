package com.vrv.vap.admin.vo;

import com.vrv.vap.common.plugin.annotaction.QueryLike;
import com.vrv.vap.common.vo.Query;
import io.swagger.annotations.ApiModelProperty;

/**
 * @author lilang
 * @date 2021/9/29
 * @description
 */
public class DbBackupStrategyVO extends Query {

    @ApiModelProperty("备份内容")
    @QueryLike
    private String dataTypes;
    @ApiModelProperty("状态")
    private Integer strategyStatus;

    public String getDataTypes() {
        return dataTypes;
    }

    public void setDataTypes(String dataTypes) {
        this.dataTypes = dataTypes;
    }

    public Integer getStrategyStatus() {
        return strategyStatus;
    }

    public void setStrategyStatus(Integer strategyStatus) {
        this.strategyStatus = strategyStatus;
    }
}
