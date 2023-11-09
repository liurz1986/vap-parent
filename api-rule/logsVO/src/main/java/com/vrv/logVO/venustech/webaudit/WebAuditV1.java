package com.vrv.logVO.venustech.webaudit;

import java.io.Serializable;
import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月29日 上午10:53:21 
* 类说明 启明星辰-web安全审计
*/
@Data
@LogDesc(value="IDS日志-WebauditV1",tableName="idsLogWebauditV1",topicName="v1-WebAuditV1")
public class WebAuditV1 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	 @FieldDesc("日志时间")
	 private String syslog_time;
	 @FieldDesc("产生IP")
	 private String gen_ip;
	 @FieldDesc("协议")
	 private String protocol ;
	 @FieldDesc("产生时间")
	 private String gen_time ;
	 @FieldDesc("事件ID")
	 private String event_id ;
	 @FieldDesc("SESSION_ID")
	 private String session_id ;
	 @FieldDesc("传输协议")
	 private String transport_protocol;
	 @FieldDesc("app协议")
	 private String app_protocol;
	 @FieldDesc("事件等级")
	 private String event_level;
	 @FieldDesc("设备IP")
	 private String dev_ip;
	 @FieldDesc("服务器IP")
	 private String server_ip;
	 @FieldDesc("客户端IP")
	 private String client_ip;
	 @FieldDesc("服务器端口")
	 private Long server_port;
	 @FieldDesc("重定向端口")
	 private Long redirect_port;
	 @FieldDesc("客户端端口")
	 private Long client_port;
	 @FieldDesc("服务器MAC")
	 private String server_mac ;
	 @FieldDesc("客户端MAC")
	 private String client_mac ;
	 @FieldDesc("规则集合名称")
	 private String ruleset_name;
	 @FieldDesc("规则名称")
	 private String rule_name;
	 @FieldDesc("BIZ账号")
	 private String biz_account;
	 @FieldDesc("AUTH账号")
	 private String auth_account;
	 @FieldDesc("策略ID")
	 private Long policy_id;
	 @FieldDesc("规则ID")
	 private Long rule_id;
	 @FieldDesc("规则模板ID")
	 private Long rule_templet_id ;
	 @FieldDesc("方向")
	 private String direction;
	 @FieldDesc("反应时间")
	 private Long response_time;
	 @FieldDesc("错误编码")
	 private Long error_code ;
	 @FieldDesc("模块")
	 private String block;
	 @FieldDesc("URL")
	 private String url;
	 @FieldDesc("主机版本")
	 private String host ;
	 @FieldDesc("相关联")
	 private String referer;
	 @FieldDesc("方法")
	 private String method ;
	 @FieldDesc("COOKIE")
	 private String cookie;
	 @FieldDesc("邮编")
	 private String post ;
	 @FieldDesc("响应内容类型")
	 private String req_content_type;
	 @FieldDesc("请求内容类型")
	 private String res_content_type;
	 @FieldDesc("AJB生产者")
	 private String ajb_producer;
	 @FieldDesc("AJB主机")
	 private String ajb_host ;
	 @FieldDesc("VRV接收时间")
	 private String vrv_receive_time;
	 @FieldDesc("DT")
	 private String dt ;
	 @FieldDesc("省份")
	 private String province ;
	 @FieldDesc("处理时间")
	 private Timestamp triggerTime;


}
