package com.vrv.logVO.honeypot.chanting;

import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月28日 上午10:33:09 
* 类说明 
*/
@LogDesc(value="蜜罐入侵",tableName="honeyPotIntrusion",topicName="v1-IntrusionLog")
@Data
public class IntrusionLog {

	@FieldDesc("蜜罐资源")
    private String  source;
	@FieldDesc("ID")
    private String id;
	@FieldDesc("事件类型")
    private String event_type;
	@FieldDesc("事件类型展示名称")
    private EventType event_type_display_name;
	@FieldDesc("时间")
	private Timestamp time;
	@FieldDesc("代理")
	private String agent;
	@FieldDesc("源IP")
	@RelateField("src_Ip")
	private String src_ip;
	@FieldDesc("源端口")
	private String src_port;
	@FieldDesc("源MAC")
	private String src_mac;
	@FieldDesc("目的IP")
	private String dest_Ip;
	@FieldDesc("目的端口")
	private String dest_port;
	@FieldDesc("风险等级")
	private String risk_level;
	@FieldDesc("连接ID")
	private String conn_id;
	@FieldDesc("处理时间")
	private Timestamp triggerTime;
	
    @Data
    public class EventType{
    	private String cn;
    	private String en;
    }
    


}
