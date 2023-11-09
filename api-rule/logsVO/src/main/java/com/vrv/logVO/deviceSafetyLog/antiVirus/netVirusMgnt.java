package com.vrv.logVO.deviceSafetyLog.antiVirus;

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
@LogDesc(value="防病毒-登录审计日志",tableName="net_virus_mgnt",topicName="net-virus-mgnt")
public class netVirusMgnt {
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

    @FieldDesc("设备IP")
    @RelateField("src_Ip")
    private	String 	device_ip;
    @FieldDesc("设备名称")
    private String device_name;
    @FieldDesc("登录IP")
    private	String 	login_ip;
    @FieldDesc("用户名")
    private	String login_user;
    /*  @FieldDesc("发生时间")
  private	String 	event_time;*/
    @FieldDesc("日志级别")
    private	String event_level;
    @FieldDesc("操作类型")
    private	String opt_type;
    @FieldDesc("模块（进程）")
    private	String opt_process;
    @FieldDesc("操作命令")
    private	String opt_condition;
    @FieldDesc("命令结果")
    private	String opt_result;
    @FieldDesc("信息")
    private	String opt_msg;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
