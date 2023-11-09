package com.vrv.vap.alarmdeal.business.flow.core.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 任务处理者guid
 * @author wd-pc
 *
 */
@Entity
@Data
@Table(name="business_task_candidate")
public class BusinessTaskCandidate{
	@Id
	@Column(name="id",length = 50)
	private String id; //主键guid
	@Column(name="busi_task_id")
	private String busiTaskId; //businessTask的guid
	@Column(name="task_id")
	private String taskId; //任务taskId
	@Column(name="candidate")
	private String candidate; //任务处理者guid
	@Column(name="candidate_name")
	private String candidateName; //候选人人名
	@Column(name="create_date")
	private Date createDate;  // 候选人任务时间
	@Column(name="assign_type")
	private Integer assignType; //1-业务触发,2-处理者触发
}
