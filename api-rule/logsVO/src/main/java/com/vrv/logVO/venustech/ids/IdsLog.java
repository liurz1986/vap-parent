package com.vrv.logVO.venustech.ids; 
/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月26日 下午2:42:29 
* 类说明 
*/

import java.io.Serializable;
import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;
@LogDesc(value="IDS日志",tableName="idsLog",topicName="v1-IdsLog")
@Data
public class IdsLog implements Serializable {
   
	
	private static final long serialVersionUID = 1L;
	
	@FieldDesc("目的IP")
	private String dstIp;
	@FieldDesc("源IP")
    private String srcIp;
	@FieldDesc("攻击类型描述")
	private String attackId;
	@FieldDesc("攻击次数")
	private Long count;
	@FieldDesc("攻击描述")
	private String subject;
	@FieldDesc("处理时间")
	private Timestamp triggerTime;
}
