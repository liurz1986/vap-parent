package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
@Entity
@Table(name = "event_table")
@Data
public class EventTable implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name="id",length = 50)
	private String id;
	@Column(name="name",length = 255)
	private String name;
	@Column(name="label",length = 255)
	private String label;
	@Column(name="description",length = 500)
	private String description;
	@Column(name="is_multi_table",length = 500)
	private Boolean multiTable;
	@Column(name="type")
	private Integer type;
	@Column(name="groupName")
	private String groupName;
	@Column(name="devicetype")
	private String devicetype;
	@Column(name="devicetypelevel")
	private String devicetypelevel;
	@Column(name="eventtype")
	private String eventtype;
	@Column(name="eventtypelevel")
	private String eventtypelevel;
	@Column(name="index_name")
	private String indexName; //索引名称
	@Column(name="index_type")
	private String indexType; //索引名称
	@Column(name="topic_name")
	private String topicName; 
	@Column(name="monitor")
	private Boolean monitor; //监控名称
	@Column(name="formatter")
	private String formatter; //字段
//	@OneToMany(cascade={CascadeType.ALL},fetch=FetchType.EAGER, mappedBy = "eventTableId")
//	private Set<EventColumn> eventColumns;
	@Column(name="version")
	private Integer version; //版本
	@Column(name = "data_source")
	private  String dataSource;  //数据来源

	@Column(name="data_type")
	private  String dataType;  //数据来类型（es/mysql）


}
