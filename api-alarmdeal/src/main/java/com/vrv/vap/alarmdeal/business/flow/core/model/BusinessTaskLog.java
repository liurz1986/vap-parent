package com.vrv.vap.alarmdeal.business.flow.core.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 工单历史进程表
 * @author wd-pc
 *
 */
@Entity
@Data
@Table(name="business_task_log")
public class BusinessTaskLog {
    @Id    
	private String id;
	@Column(name = "people_name")
    private String peopleName; //操作人名称
	@Column(name = "people_id")
	private String peopleId; //操作人Guid
	private String action; //操作动作
	@Column(name = "task_define_key")
	private String taskDefineKey;  // 任务节点定义的id
	@Column(name = "task_defind_name")
	private String taskDefindName; // 任务节点定义的名称
	private String advice; //说明备注
	@Column(name = "process_key")
	private String processKey; //流程标识
	@Column(name = "process_instance_id")
	private String processInstanceId; //流程实例Id
	private String time;
	private String operation; //操作动作
	@Column(name = "deadline_date")
	private Date deadlineDate; //逾期时间
	@Column(name = "finish_date")
	private Date finishDate; //归档时间
	@Column(name = "params")
	private String params; //归档时间
	
	@Column(name="context_key")
	private String contextKey;
	
	@Column(name="context_id")
	private String contextId;
	
	@Column(name="context_label")
	private String contextLabel;
	
}
