package com.vrv.vap.alarmdeal.business.flow.processdef.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Table(name="my_ticket")
@Data
public class MyTicket {
	@Id
	private String guid;
	private String name; // 通过名称来作为一类的依赖
	@Column(name="ticket_version")
	private Integer ticketVersion;
	@Column(name="ticket_status")
	@Enumerated(EnumType.STRING)
	private ProcessStateEnum ticketStatus;
	private String mark;
	@Column(name="order_num")
	private Integer orderNum;
	@Column(name="create_user")
	private String createUser;
	@Column(name="create_time")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	@Column(name="update_user")
	private String updateUser;
	@Column(name="update_time")
	private Date updateTime;
	@Column(name="privildge_type")
	@Enumerated(EnumType.STRING)
	private PrivildgeTypeEnum privildgeType;
	@Embedded
	private MyTicketForminfo forminfo;  // 表单信息
	@Column(name="model_id")
	private String modelId;   // 自定义流程模型guid
	@Column(name="deploy_id")
	private String deployId;  // 自定义流程发布id
	@Transient
	private Integer count;
	@Transient
	private String personSelect;  // 使用的是MyticketPrivildge表来保存，这里只做传参
	@Column(name="used")
	private Boolean used;      // 是否用过，启用过就为true；为true的不能改为false
	@Column(name="deadline_time")
	private Integer deadlineTime;
	@Column(name="canedit_deadline")
	private  Integer caneditDeadline;
	@Column(name = "ticket_name_rule")
	private String ticketNameRule;
	@Column(name = "can_edit_ticket_name")
	private Boolean cantEditTicketName;
	// 流程所有节点信息
	@Column(name = "flow_content")
	private String flowContent;
	// 节点状态
	@Column(name = "node_status")
	private String nodeStatus;
	// 工单2.0新增字段 2022-04-20
	@Column(name = "bpmn_path")
	private String bpmnPath;  //文件路径 // 新增两个字段存在bpmn信息 2021-11-2
	@Column(name = "bpmn_info")
	private String bpmnInfo;  //bpmn文件的信息
	@Column(name = "bpmn_json")
	private String bpmnJson; //bpmnJson信息（业务信息）
	@Column(name = "process_version")
	private String processVersion; //流程版本（old：老版本；new：新版本）
	// 内外部工单
	@Column(name = "ticket_type")
	private String ticketType; // 工单类型：内部流程、外部流程 "1"， 表示内部流程，"2"表示外部流程
	
}
