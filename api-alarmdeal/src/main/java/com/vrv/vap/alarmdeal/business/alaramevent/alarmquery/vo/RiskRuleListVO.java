package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import javax.persistence.Column;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmNotice;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilterVo;
import com.vrv.vap.alarmdeal.business.analysis.vo.FieldInfoVO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * 告警规则RuleVO
 * @author wd-pc
 *
 */
@Data
public class RiskRuleListVO {

	private String id;
    private ValueText event;
    private ValueText eventCategory;
    private String name;
    private String desc;
    private String note;
    private String started;
    private String createdTime;
    private String cascadeState;
    private String extend2; 
    private String ruleCode;
    private String levelstatus;
    private String riskEventId;
    private String field_info;
    private String data_source;
    private AlarmNotice alarmNotice;
    private FieldInfoVO fieldInfoVO;
	private String rule_desc; //规则描述
	private String rule_field_json; //规则表单描述
	private Float unitScore; //单位分数
	private Float maxScore; //最高分数
	private String riskSql;  //对应的sql规则
	private String tableName; //表名
	private String logPath; //日志路径
	private String flag; //日志路径
	private String tableLabel; //表名
	private String knowledgeTag; //知识库标签; //表名
	private String initStatus; //告警初始化状态
	private boolean deleteFlag; //删除标识
	private String analysisId;
    private String eventName;
    private String attackLine;
    private String threatCredibility;
    private String dealAdvcie;
    private Boolean produceThreat;
    private Integer failedStatus;
    private  String type;
    private String tag;
    private List<RiskRuleListVO> children;
    private String ruleType;  //规则类型
    private String modelId;
    
    
	private String harm;	//危害

	private  String principle;	//原理

	@Column(name = "violation_scenario")
	private String  violationScenario;// 违规场景
	
	private Boolean recommend ;//推荐
	
	@Column(name = "is_built_in_data") 
	private Boolean isBuiltInData;//是否内置
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

    private String msg;

}
