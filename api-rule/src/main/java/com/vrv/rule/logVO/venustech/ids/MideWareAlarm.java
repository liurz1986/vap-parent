package com.vrv.rule.logVO.venustech.ids;

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
@LogDesc("中间件-异常访问告警")
@Data
public class MideWareAlarm {
      
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
	@FieldDesc("源IP")
	private String srcIP;
	@FieldDesc("访问时间")
	private Timestamp  requestTime;
	@FieldDesc("访问方法")
	private String httpMethod;
	@FieldDesc("URL")
	private String url;
	@FieldDesc("响应状态码")
	private String responseCode;
	@FieldDesc("响应时间")
	private String responseTime;
	@FieldDesc("服务器IP")
	private String serverIP;
	@FieldDesc("数量")
	private Long cnt;
	@FieldDesc("时间1")
	private Timestamp time1;
	@FieldDesc("时间2")
	private Timestamp time2;
	@FieldDesc("处理时间")
	private Timestamp triggerTime;

}
