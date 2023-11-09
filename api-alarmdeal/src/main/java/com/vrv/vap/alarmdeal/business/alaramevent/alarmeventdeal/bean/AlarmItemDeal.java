package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.bean;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "alarm_item_deal")
public class AlarmItemDeal implements Serializable {
       
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@Id
    @Column
	private String guid; //主键
    @Column(name="item_type")
	private String itemType; //告警类型
    @Column(name="item_status")
	private String itemStatus; //告警状态;  // 初始状态是0，成功状态为true，失败状态为false
    @Column(name="item_people")
    private String itemPeople; //告警处置人
    @Column(name="item_people_id")
    private String itemPeopleId; //告警处置人ID
    @Column(name="happen_time")
    private String happenTime; //发生事件
    @Column(name="deal_guid")
    private String dealGuid; //告警guid
    @Column(name="json_info")
	private String jsonInfo; //告警信息
    @Column(name="last_exe_time")
    private String lastExeTime; //最后执行事件
}
