package com.vrv.rule.model;

import java.io.Serializable;
import java.util.List;
import lombok.Data;

@Data
public class AlarmObj implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String srcIp; //源IP
	private String dstIp; //目的IP
	private String relateIp; //关联IP
	private String srcPort; //源端口
	private String dstPort; //目的端口
	private RiskEventRule riskEventRule; //规则信息
	private List<ExtendParam> extendParams; //额外属性
	
}
