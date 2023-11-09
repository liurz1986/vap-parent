package com.vrv.rule.model;

import java.io.Serializable;

import lombok.Data;
/**
 * 事件表结构
 * @author wd-pc
 *
 */
@Data
public class EventTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String label;
	private String description;
	private Boolean multiTable;
	private Integer type;
	private String groupName;
	private String devicetype;
	private String devicetypelevel;
	private String eventtype;
	private String eventtypelevel;
	private String indexName; //索引名称
	private String topicName; 
	private Boolean monitor; //监控名称
	private String formatter; //字段
	private Integer version; //版本
}
