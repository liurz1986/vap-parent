package com.vrv.vap.alarmdeal.business.analysis.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Table(name = "event_column")
@Data
public class EventColumn implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	 @Column(name="id",length = 50)
	 private String id;
	 @Column(name="name",length = 50)
     private String name;
	 @Column(name="label",length = 50)
     private String label;
	 @Column(name="type",length = 50)
     private String type;
	 @Column(name="len",length = 50)
     private String len;
	 @Column(name="is_primary_key",length = 50)
     private Boolean primaryKey;
	 @Column(name="not_null",length = 50)
     private Boolean notNull;
//	 @ManyToOne(cascade={CascadeType.MERGE,CascadeType.REFRESH},optional=false)//可选属性optional=false,表示company不能为空
//     @JoinColumn(name="EventTable")
	 @Column(name="EventTable",length = 50)
	 private String eventTableId;   //关联eventTable
	 @Column(name="srcIp",length = 50)
	 private Boolean srcIp; //源IP
	 @Column(name="dstIp",length = 50)
	 private Boolean dstIp; //源IP
	 @Column(name="relateIp",length = 50)
	 private Boolean relateIp; //关联IP
	 @Column(name="timeLine",length = 50)
	 private Boolean timeLine; //是否是时间字段
	@Column(name="dataHint",length = 50)  //字表guid
	private String dataHint;
	@Column(name="col_order",length = 50)
	private  Integer order;  //排序
	@Column(name="is_event_time",length = 50)
	private Boolean eventTime; //是否是事件时间
	@Column(name = "is_show")
	private  Boolean isShow;

}
