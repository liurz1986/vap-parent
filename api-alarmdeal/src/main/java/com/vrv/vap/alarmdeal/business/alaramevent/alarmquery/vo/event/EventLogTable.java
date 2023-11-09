package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.event;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年6月18日 下午4:24:06 
* 类说明 日志表名
*/
@Data
public class EventLogTable {

	private String tableName; //表名
	private String tag; //标签
	private String type; //类型
}
