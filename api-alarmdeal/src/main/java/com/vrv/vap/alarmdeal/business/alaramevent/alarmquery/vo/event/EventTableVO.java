package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 事件表VO
 * @author wd-pc
 *
 */
@Data
@ApiModel(value="事件表对象")
public class EventTableVO {
	@ApiModelProperty(value="事件表主键ID")    
	private String id;
	@ApiModelProperty(value="事件表名称D") 
	private String name;
	@ApiModelProperty(value="事件表标签") 
	private String label;
	@ApiModelProperty(value="事件表描述") 
	private String description;
	@ApiModelProperty(value="是否是符合表") 
	private Boolean multiTable;
	@ApiModelProperty(value="事件表类型") 
	private Integer type;
	@ApiModelProperty(value="事件表组名")
	private String groupName;
	@ApiModelProperty(value="事件表设备类型") 
	private String devicetype;
	@ApiModelProperty(value="事件表设备等级") 
	private String devicetypelevel;
	@ApiModelProperty(value="事件类型") 
	private String eventtype;
	@ApiModelProperty(value="事件类型等级") 
	private String eventtypelevel;
	@ApiModelProperty(value="索引名称") 
	private String indexName; //索引名称
	@ApiModelProperty(value="topic名称") 
	private String topicName; 
	@ApiModelProperty(value="监控名称") 
	private String monitor; //监控名称
	@ApiModelProperty(value="事件表排序字段") 
	private String order_;    // 排序字段
	@ApiModelProperty(value="事件表排序顺序") 
	private String by_;   // 排序顺序
	@ApiModelProperty(value="事件表起始行") 
	private Integer start_;//
	@ApiModelProperty(value="每页个数")
	private Integer count_;
	@ApiModelProperty(value="数据来源")
	private String dataSource;
	@ApiModelProperty(value="数据来类型（es/mysql）")
	private  String dataType;  //数据来类型（es/mysql）

}
