package com.vrv.vap.alarmdeal.business.flow.processdef.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 自定义流程的权限表
 * @author lijihong
 *
 */
@Entity
@Data
@Table(name="my_ticket_privildge")
public class MyTicketPrivildge {
	@Id
	private String guid;
	@Column(name="my_ticket_guid")
	private String myTicketGuid;  // 关联的自定义流程guid
	@Column(name="user_type")
	private String userType;  // user,role
	@Column(name="data_guid")
	private String dataGuid;   // 根据指定的数据类型，保存的数据的guid

}
