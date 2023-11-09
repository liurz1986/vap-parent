package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "conf_attack_mapping")
@Data
public class ConfAttackMapping {
	
	@Id
	@Column
	private Integer id;
	@Column(name="event_code")
	private String eventCode;
	@Column(name="type")
    private String type;
	@Column(name="type_name")
    private String typeName;
	@Column(name="ip_type")
    private String ipType;
}
