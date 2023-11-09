package com.vrv.vap.alarmdeal.business.flow.core.model;

import com.vrv.vap.jpa.baseservice.StringMatcherType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 流程实例
 * @author lijihong
 *
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name="business_intance")
public class BusinessIntance {
	@Id
	@Column(name="guid")
	private String guid;
	@Column(name="code")
	private String code;   // 工单编号
	@Column(name="name")
	private String name;   // 工单名称
	@Column(name="process_instance_id")
	private String processInstanceId;  // 流程实例id
	@Column(name="process_def_guid")
	private String processDefGuid; //流程定义guid
	@Column(name="process_def_name")
	@StringMatcherType(StringMatcher.EXACT)
	private String processDefName; //流程定义名称
	@Column(name="create_user_name")
	private String createUserName;//流程实例创建人名称
	@Column(name="create_user_id")
	@StringMatcherType(StringMatcher.EXACT)
	private String createUserId;//流程实例创建人guid
	@Column(name="create_date")
	private Date createDate;
	@Column(name="stat_enum")
	@Enumerated(EnumType.STRING)
	private BusinessInstanceStatEnum statEnum;
	@Column(name="busi_args",columnDefinition = "text")
	private String busiArgs;    // 表单的json字符串
	@Column(name="deal_peoples",columnDefinition = "text")
	private String dealPeoples;   // 经手人，每次经手都直接附加，逗号分隔

	@Column(name="finish_date")
	private Date finishDate; //归档时间
	@Column(name="deadline_date")
	private  Date deadlineDate; //逾期时间
	
	@Column(name="context_key")
	private String contextKey;
	
	@Column(name="context_id")
	private String contextId;
	
	@Column(name="context_label")
	private String contextLabel;
	
	
	
}
