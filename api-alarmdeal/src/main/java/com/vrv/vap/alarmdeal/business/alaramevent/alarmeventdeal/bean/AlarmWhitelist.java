package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Entity
@Data
@Table(name = "alarm_whitelist")
public class AlarmWhitelist implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @Column
    private String guid;
    @Column(name="eventCategoryId")
    private String eventCategoryId;
    @Column(name="title")
    private String title;
    @Column(name="srcIp")
    private String srcIp;
    @Column(name="destIp")
    private String destIp;

    @Column(name="update_time")
    private String updateTime;

}
