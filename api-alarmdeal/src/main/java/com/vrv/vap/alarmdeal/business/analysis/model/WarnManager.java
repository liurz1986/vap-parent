package com.vrv.vap.alarmdeal.business.analysis.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "warn_manager")
@Data
public class WarnManager implements Serializable  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
	@Column(length = 50)
	private String guid; //主键guid
	@Column(length = 255,name="warn_name")
	private String warnName; //告警名称
	@Column(length = 255,name="warn_super_type")
	private String warnSuperType; //告警高等级
	@Column(length = 255,name="warn_type")
	private String warnType; //告警类型
	@Column(length = 255,name="warn_level")
	private String warnLevel; //告警等级
	@Column(length = 255,name="warn_status")
	private String warnStatus; //告警状态
	@Column(length = 255,name="warn_status_name")
 	private String warnStatusName; //告警状态名称
	@Column(length = 255,name="create_time")
	private String createTime; //创建时间
	@Column(length = 255,name="creater")
	private String creater; //创建人
	@Column(length = 255,name="warn_range")
	private String warn_range; //范围
	@Column(length = 255,name="file")
	private String file; //文件名称
	@Column(length = 255,name="published_person")
	private String publishedPerson; //发布人
	@Column(length = 255,name="published_time")
	private String publishedTime; //发布时间
	@Column(length = 255,name="come_from")
	private String comeFrom ; //来源
	@Column(length = 255,name="warn_detail")
	private String warnDetail; //告警细节
	@Column(length = 500,name="possible_result")
	private String possibleResult; //可能结果
	@Column(length = 500,name="solution")
	private String solution; //解决办法
	@Column(length = 255,name="warn_level_name")
	private String warnLevelName; //告警等级名称
	@Column(length = 255,name="warn_type_name")
	private String warnTypeName ; //告警类型名称
	
}
