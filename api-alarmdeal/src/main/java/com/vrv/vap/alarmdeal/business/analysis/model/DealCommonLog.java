package com.vrv.vap.alarmdeal.business.analysis.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

/**
 * 告警日志记录
 * @author wd-pc
 *
 */
@Entity
@Data
@Table(name = "Deal_Common_Log")
public class DealCommonLog {

    @Id
    @Column
	private String guid;
    @Column(name="happen_time")
	private String happenTime;
    @Column(name="deal_instance_id")
    private String dealInstanceId;
    @Column(name="item_type")
    private String itemType;
    @Column(name="lastversion_flag")
    private String lastversionFlag;
    @Column(name="json_info")
    private String jsonInfo;
    @Column(name="item_guid")
    private String itemGuid;
	
	
}
