package com.vrv.rule.logVO.honeypot.chanting;

import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月28日 上午9:35:52 
* 类说明 
*/
@LogDesc("蜜罐-scan日志")
@Data
public class ScanLog {
	@FieldDesc("蜜罐资源")
    private String  source;
	@FieldDesc("ID")
    private String id;
	@FieldDesc("类型")
    private String type;
	@FieldDesc("事件类型展示名称")
    private EventType event_type_display_name;
	@FieldDesc("开始时间")
	private Timestamp start_time;
	@FieldDesc("结束时间")
	private Timestamp end_time;
	@FieldDesc("代理")
	private String agent;
	@FieldDesc("源IP")
	private String src_ip;
	@FieldDesc("源端口")
	private String src_port;
	@FieldDesc("目的IP")
	private String dest_ip;
	@FieldDesc("目的端口")
	private String dest_port;
	@FieldDesc("处理时间")
	private Timestamp triggerTime;
	
    @Data
    public class EventType{
    	private String cn;
    	private String en;
    }
    
}




