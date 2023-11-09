package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo;

import com.vrv.vap.es.util.page.QueryCondition_ES;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel(value="告警入参对象")
public class AnalysisVO  {
	@ApiModelProperty(value="ID")
	private String id;
	@ApiModelProperty(value="用户名称")
	private String userName;
    @ApiModelProperty(value="组织结构")
    private String orgCode;
	@ApiModelProperty(value="事件类型")
	private String eventType; //事件类型
	@ApiModelProperty(value="状态枚举")
 	private String statusEnum; //状态枚举
	@ApiModelProperty(value="开始时间")
 	private String stime; //开始时间
	@ApiModelProperty(value="结束时间")
 	private String etime; //结束时间
	@ApiModelProperty(value="规则ID")
 	private String ruleId;//规则ID
	@ApiModelProperty(value="权重")
 	private String weight; //权重
	@ApiModelProperty(value="告警类型")
 	private String analysisType; //告警类型
	@ApiModelProperty(value="规则名称")
 	private String ruleName; //规则名称
	@ApiModelProperty(value="政策名称")
 	private String policyName; //政策名称
	@ApiModelProperty(value="事件类型等级")
 	private String eventtypelevel; //事件类型等级
	@ApiModelProperty(value="风险事件名称")
 	private String riskEventName; //风险事件名称
	@ApiModelProperty(value="开始时间类型")
 	private String stimebar;
	@ApiModelProperty(value="源IP")
 	private String relatedIps; //源IP
	@ApiModelProperty(value="目的IP")
 	private String dstIps; //目的IP
	@ApiModelProperty(value="类型")
 	private String type;
	@ApiModelProperty(value="day:按天统计，week：按周统计，month:按月统计")
 	private String timeType; //day:按天统计，week：按周统计，month:按月统计
	@ApiModelProperty(value="排序字段")
	private String order_;    // 排序字段
	@ApiModelProperty(value="排序顺序")
	private String by_;   // 排序顺序
	@ApiModelProperty(value="第几页")
	private Integer start_;//
	@ApiModelProperty(value="每页个数")
	private Integer count_;
	@ApiModelProperty(value="告警Id的集合")
	private String analysisIds; 
	@ApiModelProperty(value="本身guid")
	private String self_guid;
	@ApiModelProperty(value="区域编码")
	private String srcAreaCode;
	
	@ApiModelProperty(value="源IP")
 	private String srcIp_eq; //目的IP 相等查询
	
	
	@ApiModelProperty(value="告警规则集合")
	private String[] riskEventCodeArr;
	
	
	@ApiModelProperty(value="告警规则编码集合")
	private String[] ruleCodeArr;
	
	@ApiModelProperty(value="与资产关联（assetInfo.count>0）")
	private Boolean linkAsset;
	
	
	@ApiModelProperty(value="目的安全域")
	private String dstAreaCode;
	
	//暂未使用 
	//@ApiModelProperty(value="srcAreaName")
	//private String srcAreaName;

	@ApiModelProperty(value="dstAreaName")
	private String dstAreaName;
	
	@ApiModelProperty(value="assetguids")
	private String assetguids;

	@ApiModelProperty(value="区域名称")
	private String srcAreaName;
	@ApiModelProperty(value="额外字段")
	private String extraField;
	@ApiModelProperty(value="标识区分字段")
	private String flag;
	@ApiModelProperty(value="复合查询")
	private String complexSearch;
	@ApiModelProperty(value="是否是攻击类型，attackFlag==1：攻击类型；attackFlag==0：非攻击类型")
	private String attackFlag;
	@ApiModelProperty(value="地图类型")
	private String mapType;
	@ApiModelProperty(value="地图级别")
	private String  mapLevel;
	@ApiModelProperty(value="应用系统Id")
	private String systemId;
	@ApiModelProperty(value="是否为应用")
	private  Boolean linkApp;
	@ApiModelProperty(value="ES扩展查询条件")
	private QueryCondition_ES esQuery;
	@ApiModelProperty(value="世界域源地址")
	private String fromAreaName;
	@ApiModelProperty(value="世界域目的地址")
	private String toAreaName;
	@ApiModelProperty(value="规则编码")
	private String ruleCode;
	@ApiModelProperty(value="区域编码")
	private String areaCode;
	
	@ApiModelProperty(value="规则编码分组")
	private String ruleCodeGroup;

	@ApiModelProperty(value="失陷状态")
	private Integer failedStatus;

	@ApiModelProperty("查询全部")
	private Boolean isAll;

	@ApiModelProperty("威胁数据来源")
	private  String dataSource;


	@ApiModelProperty(value="开始时间")
	private String start_time; //开始时间

	@ApiModelProperty(value="结束时间")
	private String end_time; //结束时间
	
	@ApiModelProperty(value="源IP")
	private String src_ips;

	@ApiModelProperty(value="统计top前多少")
	private Integer size;

	
}
