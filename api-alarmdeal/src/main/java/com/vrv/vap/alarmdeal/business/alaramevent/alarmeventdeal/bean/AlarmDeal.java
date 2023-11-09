package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "alarm_deal")
public class AlarmDeal implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
    @Column
	private String guid;
    @Column(name="create_time")
	private String createTime;
    @Column(name="end_time")
	private String endTime;
    @Column(name="create_people")
	private String createPeople;
    @Column(name="deal_status")
    private String dealStatus;
    @Column(name="alarm_guid")
    private String alarmGuid;
    @Column(name="deal_detail")
    private String dealDetail;
    @Column(name="risk_event_name")
    private String riskEventName;
    @Column(name="deal_type")
    private String dealType;
    @Column(name="create_people_Id")
    private String createPeopleId;
    @Column(name="dead_line")
    private String deadLine;
    @Column(name="deal_person")
    private String dealPerson;
    @Column(name="role_id")
    private String roleId;
    @Column(name="role_name")
    private String roleName;
}
