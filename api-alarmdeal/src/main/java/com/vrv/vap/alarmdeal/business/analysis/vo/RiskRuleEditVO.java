package com.vrv.vap.alarmdeal.business.analysis.vo;

import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmNotice;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.RelateSqlVO;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilterVo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.persistence.Column;

@Data
@ApiModel(value="告警规则编辑对象")
public class RiskRuleEditVO {
	@ApiModelProperty(value="告警规则对象主键ID")
	private String id;
	@ApiModelProperty(value="事件ID")
    private String eventId;
	@ApiModelProperty(value="风险事件ID")
    private String riskEventId;
	@ApiModelProperty(value="告警规则名称")
    private String name;
	@ApiModelProperty(value="告警规则类型")
    private Integer type;
	@ApiModelProperty(value="告警规则描述")
    private String desc;
	@ApiModelProperty(value="告警规则备注")
    private String note;
	@ApiModelProperty(value="告警规则是否启动")
    private String started;
	@ApiModelProperty(value="告警规则级联状态")
    private String cascadeState;
	@ApiModelProperty(value="告警规则等级状态")
    private String levelstatus;
	@ApiModelProperty(value="告警规则校验")
	private String validations;
	@ApiModelProperty(value="告警规则额外扩展字段2")
	private String extend2;
	@ApiModelProperty(value="告警规则额外扩展字段1")
	private String extend1;
	@ApiModelProperty(value="告警规则编码")
	private String ruleCode;
	@ApiModelProperty(value="告警事件类型")
	private String warmType;
	@ApiModelProperty(value="告警规则处置方式")
	private AlarmNotice alarmNotice;
	@ApiModelProperty(value="数据源")
	private String data_source;
	@ApiModelProperty(value="告警规则")
	private FieldInfoVO fieldInfoVO;
	@ApiModelProperty(value="主类名称")
	private String main_class; //flink启动主类
	@ApiModelProperty(value="任务名称")
	private String job_name; //任务名称
	@ApiModelProperty(value="规则描述")
	private String rule_desc;
	@ApiModelProperty(value="自定义表单信息")
	private String rule_field_json;
	@ApiModelProperty(value="单位分数")
	private Float unitScore; //单位分数
	@ApiModelProperty(value="最高分数")
	private Float maxScore; //最高分数
    @ApiModelProperty(value="对应的事件表名")
    private String tableName; //表名
    @ApiModelProperty(value="关联sql的属性")
    private List<RelateSqlVO> relateList;
    @ApiModelProperty(value="sql自定义模式")
    private String flag; //complex or filter or editor
    @ApiModelProperty(value="sql")
    private String riskSql; //complex or filter
    @ApiModelProperty(value="表名")
    private String tableLabel; //complex or filter
    @ApiModelProperty(value="知识库标签")
    private String knowledgeTag; //complex or filter
    @ApiModelProperty(value="表类型")
    private String tableType;
    @ApiModelProperty(value="初始化状态")
    private String initStatus; // 0为非内置 1 为内置

	@ApiModelProperty(value="事件名称") //事件名称
	private String eventName;
	@ApiModelProperty(value="攻击链阶段")//攻击链阶段
	private String attackLine;
	@ApiModelProperty(value="威胁可信度") //威胁可信度
	private String threatCredibility;
	@ApiModelProperty(value="处置建议") //处置建议
	private String dealAdvcie;
	@ApiModelProperty(value="产生威胁") //产生威胁
	private Boolean produceThreat;
	@ApiModelProperty(value="失陷状态") //失陷状态
	private Integer failedStatus;
	@ApiModelProperty(value="标签") //标签
	private String ruleType;
	@ApiModelProperty(value="危害")
	private String harm;	//危害
	@ApiModelProperty(value="原理")
	private  String principle;	//原理
	@ApiModelProperty(value="违规场景")
	private String  violationScenario;// 违规场景

	/***************策略优化*********************/
	@ApiModelProperty(value="关联sql的属性")
	private List<RuleFilterVo> filters;

	@ApiModelProperty(value = "关联的类型：category（分类）、rule（规则）")
	private String linkType;

	@ApiModelProperty(value = "规则path：category填写code_level，rule填写warmType/id")
	private String rulePath;

	@ApiModelProperty(value = "是否督促")
	private Boolean isUrge;

	@ApiModelProperty(value = "限期时间")
	private Integer timeLimitNum;

	@ApiModelProperty(value = "督促理由")
	private String urgeReason;

	@ApiModelProperty(value = "通知哪个角色")
	private String toRole;

	@ApiModelProperty(value = "通知谁")
	private String toUser;

	@ApiModelProperty(value = "是否关联资产责任人")
	private Boolean toAssetUser;
}
