package com.vrv.vap.alarmdeal.business.analysis.vo;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.jpa.web.page.PageReq;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 事件详情查询实体
 * 
 * @author sj100d
 *
 */
@Data
public class EventDetailQueryVO extends PageReq {
	@ApiModelProperty(value = "设备ip")
	String deviceIp;

	@ApiModelProperty(value = "主体资产ip")
	String principalIp;
	@ApiModelProperty(value = "应用系统id")
	Integer appId;
	@ApiModelProperty(value = "机构编码")
	String orgCode;

	@ApiModelProperty(value = "机构树结构编码")
	String orgTreeCode;

	@ApiModelProperty(value = "部门编码")
	String deptCode;
	
	@ApiModelProperty(value = "部门名称")
	String deptName;

	@ApiModelProperty(value = "事件级别")
	Integer eventLevel;

	@ApiModelProperty(value = "标签")
	List<String> labels;

	@ApiModelProperty(value = "事件处理状态")
	Integer alarmDealState;
	@ApiModelProperty(value = "责任人名称")
	String userName;
	
	@ApiModelProperty(value = "责任人code")
	String userCode;

	@ApiModelProperty(value = "处理时限 剩余时间 单位s")
	Integer timeLimitNum;// 处理时限
	// 是否督促
	@ApiModelProperty(value = "是否督促")
	Boolean isUrge;

	@ApiModelProperty(value = "督促起始时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date beginUrgeTime;

	@ApiModelProperty(value = "督促结束时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date endUrgeTime;

	// 是否督办
	@ApiModelProperty(value = "是否督办")
	Boolean isSupervise;

	// 已读
	@ApiModelProperty(value = "是否已读")
	Boolean isRead;
	//是否协办
	@ApiModelProperty(value = "是否协办")
	Boolean isAssist;
	@ApiModelProperty(value = "仅看关注的资产")
	Boolean isJustAssetOfConcern;

	@ApiModelProperty(value = "仅看关注的事件名称")
	Boolean isJustEventNameOfConcern;

	@ApiModelProperty(value = "起始时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date beginTime;

	@ApiModelProperty(value = "结束时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date endTime;
	
	
	@ApiModelProperty(value = "督办起始时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date beginSuperviseTime;

	@ApiModelProperty(value = "督办结束时间")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	Date endSuperviseTime; 
	

	@ApiModelProperty(value = "设备类型")
	String deviceType;
	
	
	
	@ApiModelProperty(value = "应用系统名称")
	String 	applicationId;

	@ApiModelProperty(value = "事件类型")
	Integer eventType;

	@ApiModelProperty(value = "事件名称")
	String eventName;

	@ApiModelProperty(value = "关键字(支持标签、ip、事件名称)")
	String keyWordAgg;
	
	@ApiModelProperty(value = "关键字(支持事件名称、责任人、部门)")
	String keyWordDetail;

	@ApiModelProperty(value = "dstIP")
	private String dstIp;

	@ApiModelProperty(value = "dstIP")
	private String srcIp;
	
	
	@ApiModelProperty(value = "是否完成处理")
	private Boolean isDealt;
	
	@ApiModelProperty(value = "eventCodeBeginLike")
	private String 	eventCodeBeginLike;
	@ApiModelProperty(value = "事件id")
	private String 	eventId;
	@ApiModelProperty(value = "eventTableName")
	private String 	eventTableName;

	// 自查自评结果中关联查事件
	@ApiModelProperty(value="多个事件ID")
	private List<String> eventIds;
	@ApiModelProperty(value = "rangIp")
	private String 	rangIp;
}
