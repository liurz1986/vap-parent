package com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月07日 11:26
 */
@Data
public class RuleOperation {
    @ApiModelProperty("规则总数")
    private long filterTotal;

    @ApiModelProperty("运行规则数据")
    private long filterRunning;

    @ApiModelProperty("停用规则数")
    private long filterFail;
}
