package com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月06日 18:02
 */
@Entity
@Table(name = "rule_filter")
@Data
public class RuleFilter {
    @Id
    @Column(name = "guid")
    @ApiModelProperty("主键guid")
    private String guid;

    @Column(name = "rule_id")
    @ApiModelProperty("规则ID")
    private String ruleId;

    @Column(name = "filter_code")
    @ApiModelProperty("规则ID")
    private String filterCode;

    @Column(name = "isStarted")
    @ApiModelProperty("是否启用")
    private String isStarted;
}
