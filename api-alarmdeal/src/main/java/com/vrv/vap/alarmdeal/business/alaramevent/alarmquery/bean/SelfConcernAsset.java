package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "self_concern_asset")
public class SelfConcernAsset {
	
	@Id
	@Column(name="guid")
	private String guid;
	
	
	@Column(name="user_id")
	private String userId;
	
	@Column(name="ip")
	private String ip;

	@Column(name="type")
	private Integer type; //0资产ip 1应用系统id 2网络边界id
	
}
