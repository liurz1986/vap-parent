package com.vrv.logVO.venustech.ids.resultVO;

import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月3日 下午3:27:34 
* 类说明    IDS 返回vo
*/
@LogDesc(value="ids log vo",tableName="idsLogVO",topicName="v1-IdsLogVO")
@Data
public class IdsLogVO {
      
	@FieldDesc("开始时间")
	private Timestamp start_time;
	@FieldDesc("结束时间")
	private Timestamp end_time;
	@FieldDesc("目的IP")
	private String src_ip;
	@FieldDesc("目的端口")
	private String src_port;
	@FieldDesc("目的IP")
	private String dst_ip;
	@FieldDesc("目的端口")
	private String dst_port;
	@FieldDesc("数量")
	private Long cnt;
}
