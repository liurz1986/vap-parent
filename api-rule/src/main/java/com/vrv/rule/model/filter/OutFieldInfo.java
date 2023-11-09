package com.vrv.rule.model.filter;

import java.io.Serializable;

import lombok.Data;
/**
 * 最终输出结果
 * @author wd-pc
 *
 */

@Data
public class OutFieldInfo implements Serializable {
     
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String fieldName; //字段名称
	private String fieldType; //字段类型
	private Integer order; //排序
}
