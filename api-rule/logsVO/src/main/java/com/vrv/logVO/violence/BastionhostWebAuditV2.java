package com.vrv.logVO.violence;

import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月2日 上午11:54:22 
* 类说明 暴力破解 
*/
@LogDesc(value="暴力破解-日志2",tableName="forceBrokenLog2",topicName="v1-BastionhostWebAuditV2")
@Data
public class BastionhostWebAuditV2 {

	@FieldDesc("产生时间")
	private Timestamp   gen_time;                 
	@FieldDesc("授权账号")
	private String   auth_account;
	@FieldDesc("方法")
	private String   method;                 
	@FieldDesc("规则协议")
	private String   app_protocol;           
	@FieldDesc("客户端IP")
	private String   client_ip;           
	@FieldDesc("服务器IP")
	private String   service_ip;
	@FieldDesc("客户端IP")
	private String   client_port  ;           
	@FieldDesc("服务器端口")
	private String   service_port ;           
	@FieldDesc("方向")
	private String   direction;
	@FieldDesc("URL")
	private String   url;           
	@FieldDesc("提交方式")
	private String   post;
	
}
