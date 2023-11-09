package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.RuleProcessVO;
import lombok.Data;

/**
 * 告警规则
 * @author wd-pc
 *
 */
@Entity
@Data
@Table(name = "risk_event_rule")
public class RiskEventRule implements Serializable {

     public static final  String  START="start";  //启动
     public static final  String   STOP="stop";  //停止
     public static final  String  RESTART="reStart";  //重启

	public static final String PERMIT = "permit";
	public static final String FORBID = "forbid";

	 public static final String INSTANCE = "instance";
	 public static final String RULE = "rule";
	 public static final String MODEL = "model";

    public static  final String EDITOR="editor";  //2.0规则
	public static  final String FILTER="filter";  //1.0规则



	//private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id")
	private String id;
	@Column(name="riskEventId")
	private String riskEventId;   //事件分类ID
	@Column(name="name_")
	private String name;  //规则名称
	@Column(name="type_")
	private Integer type;  //类型
	@Column(name="desc_")
	private String desc;  //规则描述
	@Column(name="note")
	private String note; //规则备注
	@Column(name="extend1")
	private String extend1;
	@Column(name="extend2")
	private String extend2;
	@Column(name="isStarted")
	private String started;  //是否启动  0：已停用   1：已启用   2：启用中  3：停用中
	@Column(name="createdTime")
	private String createdTime;  //创建时间
	@Column(name="modifiedTime")
	private String modifiedTime; //修改时间
	@Column(name="assetip")
	private String assetip;   //资产ip
	@Column(name="assetguid")
	private String assetguid; //资产guid
	@Column(name="warmType")
	private String warmType;  //告警类型
	@Column(name="cascadeState")
	private String cascadeState;  //级联状态
	@Column(name="levelstatus")
	private String levelstatus;  //等级状态
	@Column(name="validations")
	private String validations;  //告警有效性
	@Column(name="rule_code")
	private String ruleCode;    //规则编码
	@Column(name="field_info")
	private String field_info;  //处置字段
	@Column(name="data_source")
	private String data_source;  //数据源
	@Column(name="rule_complex")
	private String rule_complex; //规则类型
	@Column(name="attack_event")
    private Boolean attack_event; //攻击事件
	@Column(name="main_class")
	private String main_class; //flink启动主类
	@Column(name="job_name")
	private String job_name; //任务名称
	@Column(name="rule_desc")
	private String rule_desc; //规则描述
	@Column(name="rule_field_json")
	private String rule_field_json; //规则表单描述
	@Column(name="unitScore")
	private Float unitScore; //单位分数
	@Column(name="maxScore")
	private Float maxScore; //最高分数
	@Column(name="risk_sql")
	private String riskSql;  //对应的sql规则
	@Column(name="tableName")
	private String tableName; //表名
	@Column(name="logPath")
	private String logPath; //日志路径
	@Column(name="flag")
	private String flag; //日志类型标识
	@Column(name="tableLabel")
	private String tableLabel; //日志类型标识
	@Column(name="knowledgeTag")
	private String knowledgeTag; //知识库标签
	@Column(name = "init_status")
	private String initStatus; //告警初始化  // 0为非内置 1 为内置
	@Column(name = "delete_flag")
	private boolean deleteFlag; //删除标识
	@Column(name = "analysis_id") //分析器Id
	private String analysisId;
	@Column(name = "event_name") //事件名称
	private String eventName;
	@Column(name = "attack_line") //攻击链阶段
	private String attackLine;
	@Column(name = "threat_credibility") //威胁可信度
	private String threatCredibility;
	@Column(name = "deal_advcie") //处置建议
	private String dealAdvcie;
	

	private String harm;	//危害

	private  String principle;	//原理
	
	@Column(name = "violation_scenario") 
	private String  violationScenario;// 违规场景

	@Column(name = "recommend")
	private Boolean recommend;//推荐
	
	@Column(name = "is_built_in_data") 
	private Boolean isBuiltInData;//是否内置
	
	@Column(name = "produce_threat") //产生威胁
	private Boolean produceThreat;
	@Column(name = "failed_status") //失陷状态
	private Integer failedStatus;
	@Column(name="model_id")
	private String modelId;
	@Column(name="rule_type")
	private String ruleType;  //规则类型
	@Column(name="tag")
	private  String tag;   // 实例是否允许启动
    @Column(name = "allow_start")
	private Boolean allowStart;  //规则是否有误

	@Column(name = "create_username")
	private String createUsername;  //创建人姓名

	@Column(name = "create_userno")
	private String createUserno;  //创建人编号

	@Column(name = "update_username")
	private String updateUsername;  //修改人姓名

	@Column(name = "update_userno")
	private String updateUserno;  //修改人编号

	public RuleProcessVO structureVO(){
		RuleProcessVO ruleProcessVO=new RuleProcessVO();
		ruleProcessVO.setMain_class(this.main_class);
		ruleProcessVO.setRuleType(this.flag);
		ruleProcessVO.setAnalysisId(this.analysisId);
		ruleProcessVO.setRuleId(this.getId());
		ruleProcessVO.setRuleCode(this.ruleCode);
		ruleProcessVO.setOrignalLogPath(logPath);
		ruleProcessVO.setSql(this.riskSql);
		ruleProcessVO.setJobName(this.job_name);
		ruleProcessVO.setTableName(this.tableName);
		return  ruleProcessVO;
	}


}
