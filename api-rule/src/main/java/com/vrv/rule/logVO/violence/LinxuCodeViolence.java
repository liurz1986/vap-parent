package com.vrv.rule.logVO.violence;

import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年5月30日 上午10:07:25 
* 类说明  三峡项目-Windows 操作系统密码暴力破解告警
*/
@LogDesc("Linux密码暴力破解告警")
@Data
public class LinxuCodeViolence {
      
	@FieldDesc("开始时间")
	private Timestamp start_time;
	@FieldDesc("结束时间")
	private Timestamp end_time;
	@FieldDesc("上报IP")
	private String  report_ip;
	@FieldDesc("上报时间")
	private Timestamp report_time;
	@FieldDesc("日志类型")
	private String type;
	@FieldDesc("登陆用户名")
	private String account;
	@FieldDesc("登陆发生ip")
	private String ip;
	@FieldDesc("登陆时间")
	private Timestamp loginTime;
	@FieldDesc("原始日志")
	private String message;
	@FieldDesc("数量")
	private Long cnt;

}
