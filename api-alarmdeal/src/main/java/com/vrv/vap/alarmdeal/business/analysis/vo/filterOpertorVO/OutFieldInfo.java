package com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO;

import lombok.Data;
/**
 * 最终输出结果
 * @author wd-pc
 *
 */

@Data
public class OutFieldInfo {
     
	private String fieldName; //字段名称
	private String fieldType; //字段类型
	private String fieldLabel; //字段标识
	private Integer order; //排序
	
}
