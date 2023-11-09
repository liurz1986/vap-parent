package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 拦截关键字
 * @author wd-pc
 *
 */

@Entity
@Table(name = "interrupt_key")
@Data
public class InterruptKey {

	@Id
    @Column(name="guid")
    private String guid;  //主键guid
	@Column(name="keyword")
	private String keyword; //拦截关键字
}
