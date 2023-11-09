package com.vrv.vap.alarmdeal.business.analysis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 对应的时间关联个数
 * @author wd-pc
 *
 */
@Data
@Entity
@Table(name = "time_count")
public class TimeCount {
     
	@Id
	@Column(name="guid")
	private String guid;
	@Column(name="timeParam")
	private String timeParam; //时间属性（YYYY-MM-DD）
	@Column(name="timeCount")
	private Integer timeCount; //随机个数
	
}
