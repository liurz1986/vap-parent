package com.vrv.vap.alarmdeal.business.flow.monitor.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.PrivildgeTypeEnum;
import com.vrv.vap.alarmdeal.business.flow.processdef.model.ProcessStateEnum;
import lombok.Data;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.util.Date;

@Data
public class MyTicketFormVO {
    
	private String guid;
	private String name;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyyMMddHHmmss")
	private Date create_time;
	private String create_user;
	private String deploy_id;
	private String form_data;
	private String form_type;
	private String inner_guid;
	private String mark;
	private String model_id;
	private String order_num;
	@Enumerated(EnumType.STRING)
	private PrivildgeTypeEnum privildge_type;
	@Enumerated(EnumType.STRING)
	private ProcessStateEnum ticket_status;
	private Integer ticket_version;
	@JsonFormat(timezone = "GMT+8", pattern = "yyyyMMddHHmmss")
	private Date update_time;
	private String update_user;
	private Integer used;
	private String template_guid;
	// 流程所有节点信息
	private String flowContent;
	// 节点状态
	private String nodeStatus;
	// 新增两个字段存在bpmn信息 2021-11-2
	private String bpmnPath;  //文件路径
	private String bpmnInfo;  //bpmn文件的信息
	private String bpmnJson; //bpmnJson信息（业务信息）
}
