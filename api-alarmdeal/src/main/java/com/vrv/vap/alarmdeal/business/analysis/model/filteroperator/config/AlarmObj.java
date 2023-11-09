package com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config;

import java.io.Serializable;
import java.util.List;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;

import com.vrv.vap.alarmdeal.business.analysis.vo.RiskRuleEditVO;
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
	private RiskRuleEditVO riskRuleEditVO; //输入加输出
	private RiskEventRule riskEventRule; //最后存储
	private List<ExtendParam> extendParams; //额外属性
	
}
