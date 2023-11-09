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
public class RiskRuleInfo {
    @ApiModelProperty("策略总数")
    private long ruleTotal;

    @ApiModelProperty("运行策略个数")
    private long ruleRunning;

    @ApiModelProperty("停用策略个数")
    private long ruleFail;
}
