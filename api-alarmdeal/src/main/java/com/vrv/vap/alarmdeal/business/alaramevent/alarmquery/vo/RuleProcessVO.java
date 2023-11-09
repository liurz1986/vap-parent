package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年3月12日 上午9:58:08 
* 类说明   规则进程VO 
*/
@Data
@ApiModel(value="规则进程VO")
public class RuleProcessVO {
    
	@ApiModelProperty(value="规则编码")
	private String ruleCode;
	@ApiModelProperty(value="启动主函数路径")
	private String main_class;
	@ApiModelProperty(value="自定义规则表单信息")
	private String rule_field_json;
	@ApiModelProperty(value="原始日志路径")
	private String orignalLogPath;
	@ApiModelProperty(value="表名")
	private String tableName;
	@ApiModelProperty(value="生成sql")
	private String sql;
	@ApiModelProperty(value="执行任务名称")
	private String jobName;
	@ApiModelProperty(value="规则类型")
	private String ruleType;
	@ApiModelProperty(value="规则ID")
	private String ruleId;
	@ApiModelProperty(value="分析器ID")
	private String analysisId;
	@ApiModelProperty(value="规则等级")
	private String ruleLevel;
	@ApiModelProperty(value="规则名称")
	private String ruleName;
}
