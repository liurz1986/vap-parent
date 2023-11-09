package com.vrv.vap.alarmdeal.business.flow.processdef.model;

import org.activiti.engine.impl.persistence.entity.ModelEntity;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年3月4日 下午4:37:52 
* 类说明 流程导出VO 
*/
@Data
public class ProcessExportVOStr {

	private ModelEntity newModel; 
    private byte[] modelEditorSource; //model编辑资源(byte[])
    private byte[] modelEditorSourceExtra; //model编辑额外资源(byte[])
    private MyTicket ticket; //流程信息
	
	
	
}