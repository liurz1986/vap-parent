package com.vrv.vap.alarmdeal.business.buinesssystem.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 业务系统
 * @author vrv
 *
 */

@Data
public class BuinessSystemVO {
	private String guid;

	private String sysName;  // 业务系统名称

	private String iportanceLevel;//重要级别

	private String maintainerId;//责任人Id

	private String maintainer;//责任人

	private String description;//业务描述

	private String parentId;//父级业务系统

	private String domainCode;//绑定的安全域code

	private String domainName;//绑定的安全域名称

	private String createUserId;//创建人

	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	private Date createTime;//创建时间
}