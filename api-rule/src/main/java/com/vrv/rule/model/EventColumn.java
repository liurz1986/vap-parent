package com.vrv.rule.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 事件表列字段
 * @author wd-pc
 *
 */
@Data
public class EventColumn implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	 private String id;
     private String name;
     private String label;
     private String type;
     private String len;
     private Boolean primaryKey;
     private Boolean notNull;
	 private String eventTableId;   //关联eventTable
	 private Boolean srcIp; //源IP
	 private Boolean dstIp; //源IP
	 private Boolean relateIp; //关联IP
	 private Boolean timeLine; //是否是时间字段
	 private String dataHint; //子表顺序
	 private  Integer order;  //排序
	 private Boolean  eventTime; //是否是事件时间
}
