package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年4月22日 上午10:44:30 
* 类说明 
*/
@Data
@ApiModel("威胁库VO")
public class ThreatLibraryVO {
	@ApiModelProperty(value="威胁库主键")
	private String id;
	@ApiModelProperty(value="威胁名称")
	private String threat_name; //威胁名称
	@ApiModelProperty(value="威胁来源")
	private String threat_source; //威胁来源
	@ApiModelProperty(value="威胁分类")
	private String threat_classification; //威胁分类
	@ApiModelProperty(value="威胁描述")
	private String threat_desc; //威胁描述
	@ApiModelProperty(value="动机描述")
	private String motivate_desc; //动机描述
	@ApiModelProperty(value="动机赋值")
	private Integer motivate_assignment; //动机赋值
	@ApiModelProperty(value="能力描述")
	private String ability_desc; //能力描述
	@ApiModelProperty(value="能力赋值")
	private Integer ability_assignment; //能力赋值
	@ApiModelProperty(value="作用目标")
	private String effect_target; //作用目标
	@ApiModelProperty(value="关联漏洞")
	private String relate_vulnerability; //关联漏洞
	@ApiModelProperty(value="排序字段")
	private String order_;    // 排序字段
    @ApiModelProperty(value="排序顺序")
	private String by_;   // 排序顺序
    @ApiModelProperty(value="开始行")
	private Integer start_;//
    @ApiModelProperty(value="每页个数")
	private Integer count_;
    @ApiModelProperty(value="威胁管理标识")
    private Boolean flag;
}
