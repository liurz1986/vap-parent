package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 告警规则VO
 *
 * @author wd-pc
 */
@Data
@ApiModel(value = "告警规则查询对象")
public class RiskEventRuleQueryVO {

    @ApiModelProperty(value = "规则id")
    private String id;  //规则id
    @ApiModelProperty(value = "风险事件规则名称")
    private String riskEventRuleName; //告警规则名称
    @ApiModelProperty(value = "事件类型guid")
    private String riskEventId;
    @ApiModelProperty(value = "告警事件类型")
    private String warmType;
    @ApiModelProperty(value = "排序字段")
    private String order_;    // 排序字段
    @ApiModelProperty(value = "排序顺序")
    private String by_;   // 排序顺序
    @ApiModelProperty(value = "起始行")
    private Integer start_;//
    @ApiModelProperty(value = "每页个数")
    private Integer count_;
    @ApiModelProperty(value = "告警id集合")
    private List<String> ids;
    @ApiModelProperty(value = "攻击类型")
    private Boolean attack_type;
    @ApiModelProperty(value = "对应的sql规则")
    private String riskSql;  //对应的sql规则
    @ApiModelProperty(value = "对应的事件表名")
    private String tableName; //表名
    @ApiModelProperty(value = "日志表路径")
    private String logPath; //日志路径
    @ApiModelProperty(value = "定义类型")
    private Integer type;  //预定义|自定义   0--自定义，1--预定义
    @ApiModelProperty(value = "启动状态")
    private String started; //是否启动

    @ApiModelProperty(value = "事件等级")
    private String levelstatus;
}
