package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月19日 下午1:59:03 
* 类说明   关联sqlVO 
*/
@Data
public class RelateSqlVO {
   
	private String fieldName; //字段名称
	private String relate; //关系(等于,不等于,模糊匹配)
	private String value; //等于相关值
	private String append; //拼接(包括and和or)
}
