package com.vrv.vap.alarmdeal.business.flow.processdef.vo;

import lombok.Data;

import java.util.Map;

/**
 * 工单表单数据结构
 * @author wd-pc
 *
 */
@Data
public class FormVO {

	private String key;
	private Map<String,Object> context;
}
