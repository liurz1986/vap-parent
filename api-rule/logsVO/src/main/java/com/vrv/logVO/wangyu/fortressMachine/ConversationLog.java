package com.vrv.logVO.wangyu.fortressMachine;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月26日 下午4:33:24 
* 类说明  堡垒机会话日志
*/
@LogDesc(value="堡垒机会话日志",tableName="fortressMachine",topicName="v1-ConversationLog")
@Data
public class ConversationLog {
    
	@FieldDesc("攻击目标IP")
	private String service_ip;
	@FieldDesc("攻击源IP")
	private String user_ip;
	@FieldDesc("硬编码攻击事件描述")
	private String desc;
	@FieldDesc("根据查询结果数量作为攻击次数")
	private Long num;
	@FieldDesc("攻击目标端口")
	private String service_port;
	@FieldDesc("会话guid，用于将一次会话的所有数据合并")
	private String session_guid;
}
