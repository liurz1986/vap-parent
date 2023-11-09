package com.vrv.vap.alarmdeal.business.buinesssystem.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * 业务系统
 * @author vrv
 *
 * 2022-11-16
 */

@Data
@Entity
@Table(name="busisystem_combination")
public class BuinessSystem implements Serializable {
   
	private static final long serialVersionUID = 4493632059623255592L;
	
	@Id
	@Column(name="guid")
	private String guid;

	@Column(name="sys_name")
	private String sysName;  // 业务系统名称

	@Column(name="iportance_level")
	private String iportanceLevel;//重要级别

	@Column(name="maintainer_id")
	private String maintainerId;//责任人Id

	@Column(name="maintainer")
	private String maintainer;//责任人

	@Column(name="description")
	private String description;//业务描述
	
	@Column(name="parent_id")
	private String parentId;//父级业务系统
	
	@Column(name="domain_code")
	private String domainCode;//绑定的安全域code

	@Column(name="domain_name")
	private String domainName;//绑定的安全域名称
	
	@Column(name="create_user_id")
	private String createUserId;//创建人

	@Column(name="create_time")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;//创建时间
}