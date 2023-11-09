package com.vrv.vap.alarmdeal.business.flow.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Data
@Table(name="business_task")
public class BusinessTask {
	@Id
	@Column(name="id",length = 50)
	private String id;
	@Column(name="busi_key")
	private String busiKey;  // 业务关键字，比如ticket
	@Column(name="busi_id")
	private String busiId;   // 业务实体id。现在存放的是activiti流程实例id
	@Column(name="task_id")
	private String taskId;   // 任务id
	@Column(name="task_define_key")
	private String taskDefineKey;  // 任务节点定义的id
	@Column(name="task_defind_name")
	private String taskDefindName; // 任务节点定义的名称
	@Column(name="create_date")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createDate;
	@OneToMany(cascade=CascadeType.ALL, mappedBy = "busiTaskId",fetch = FetchType.EAGER)
	private Set<BusinessTaskCandidate> candidates;
	@Column(name="dead_date")
	private Date deadDate;   // 截止日期，可以为null，表示没有截止日期。
	@Column(name="actions")
	private String actions;   // 竖线      |   分隔

	@Column(name="task_type")
	@Enumerated(EnumType.STRING)
	private BusinessTaskType taskType;  // 节点任务，流任务（包括撤销等）
	@Column(name="execution_id")
	private String executionId;  // 执行流id
	@Column(name="task_code")
	private String taskCode;     // 任务编码。。当任务的创建和销毁不能通过taskId来完成的时候，需要通过该字段来完成
	@Column(name="related_infos")
	private String relatedInfos;   // 关联的数据。比如在撤销任务中，通过这个字段存信号量
	@ManyToOne(cascade =CascadeType.MERGE,fetch = FetchType.EAGER)  // 级联由all改为MERGE ，jpa升级报错 2022-09-08
	private BusinessIntance instance;  // 这里存放的是实例业务对象

	@Column(name = "deadline_date")
	private Date deadlineDate; //逾期时间
	
	@Column(name="context_key")
	private String contextKey;
	
	@Column(name="context_id")
	private String contextId;

	@Column(name="context_label")
	private String contextLabel;
	
}
