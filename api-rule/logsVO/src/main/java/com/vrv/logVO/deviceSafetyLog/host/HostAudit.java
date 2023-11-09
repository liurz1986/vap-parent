package com.vrv.logVO.deviceSafetyLog.host;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;
import lombok.Data;

import java.sql.Timestamp;

/**
 *  @author zhouwu
 *  @version 创建时间：2019年8月12日 下午10:13:20  
 */
@Data
@LogDesc(value="主机审计日志",tableName="host_audit",topicName="host-audit")
public class HostAudit {
    @FieldDesc("上报设备IP")
    private String  report_ip;
    @FieldDesc("上报设备IP转换")
    private	String 	report_ip_num;
    @FieldDesc("入库时间")
    private	String 	indate;
    @FieldDesc("发生时间")
    private	String 	event_time;
    @FieldDesc("来源")
    private String msg_src;
    @FieldDesc("原始字段")
    private	String 	report_msg;
    @FieldDesc("安全域关联IP")
    private	String safety_margin_ip;
    @FieldDesc("安全级别")
    private	String 	security_level;
    @FieldDesc("日志类别")
    private	String log_type;
    @FieldDesc("安全域")
    private	String safety_margin;
  /*  @FieldDesc("发现时间")
    private	String 	event_time;*/
    @FieldDesc("用户名")
    private	String 	user_name;
    @FieldDesc("事件类型")
    private	String event_type;
    @FieldDesc("角色名称")
    private	String 	role_name;
    @FieldDesc("设备ip")
    @RelateField("src_Ip")
    private	String device_ip;
    @FieldDesc("操作动作")
    private	String action;
    @FieldDesc("操作类型")
    private	String 	operation;
    @FieldDesc("操作结果")
    private	String 	result;
    @FieldDesc("主机名称")
    private	String host_name;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
