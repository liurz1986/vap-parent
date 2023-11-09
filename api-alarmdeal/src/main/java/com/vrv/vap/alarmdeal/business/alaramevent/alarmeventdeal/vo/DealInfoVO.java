package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("告警处置实体")
public class DealInfoVO {
	@ApiModelProperty(value="告警Id")
	private String id; //告警Id
	@ApiModelProperty(value="当前登陆用户Id")
    private String userId; //当前登陆用户Id
	@ApiModelProperty(value="当前登陆用户名称")
	private String userName;
	@ApiModelProperty(value="状态改变")
    private String statusChange; //状态改变
	@ApiModelProperty(value="处置类型")
    private String type;       // TypeClass
	@ApiModelProperty(value="告警内容")
    private String content;    //内容
	@ApiModelProperty(value="截止时间")
    private String deal_line;    
	@ApiModelProperty(value="处理人")
    private String dealPerson;    
}
