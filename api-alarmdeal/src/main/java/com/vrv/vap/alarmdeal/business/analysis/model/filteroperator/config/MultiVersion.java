package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import lombok.Data;

@Data
public class MultiVersion {
  
	private String type; //版本类型
	private Integer version; //版本号
	private String name; //版本的名称
	private String code; //对应世界
	private String ideVersion; //ide版本号
	
	
}
