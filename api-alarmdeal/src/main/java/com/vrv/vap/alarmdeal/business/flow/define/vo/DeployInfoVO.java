package com.vrv.vap.alarmdeal.business.flow.define.vo;

import lombok.Data;

/**
 * 发布流程相关信息 
 * @author Administrator
 *
 */
@Data
public class DeployInfoVO {

	private String bpmnPath; //bpmn文件路径
	private String bpmnName; //bpmn文件名称
}
