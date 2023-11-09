package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
@ApiModel(value="告警处置入参对象")
public class AlarmDealVO {
    @ApiModelProperty(value="告警入参对象主键ID")
	private String guid;
    @ApiModelProperty(value="告警产生时间")
	private String createTime;
    @ApiModelProperty(value="告警处置开始时间")
    private String startTime;
    @ApiModelProperty(value="告警处置结束时间")
	private String endTime;
    @ApiModelProperty(value="告警创建人")
	private String createPeople;
    @ApiModelProperty(value="告警处置状态")
    private String dealStatus;
    @ApiModelProperty(value="告警guid")
    private String alarmGuid;
    @ApiModelProperty(value="告警日志详情")
    private String dealDetail;
    @ApiModelProperty(value="风险事件名称")
    private String riskEventName;
    @ApiModelProperty(value="排序字段")
	private String order_;    // 排序字段
    @ApiModelProperty(value="排序顺序")
	private String by_;   // 排序顺序
    @ApiModelProperty(value="开始行")
	private Integer start_;//
    @ApiModelProperty(value="每页个数")
	private Integer count_;
    @ApiModelProperty(value="角色Ids")
    private String[] roleIds;
    @ApiModelProperty(value="登陆用户Id")
    private String userId;

    @ApiModelProperty(value="告警处置人")
    private String dealPeople;

    
}
