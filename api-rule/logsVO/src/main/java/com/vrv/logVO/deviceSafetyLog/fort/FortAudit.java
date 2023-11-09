package com.vrv.logVO.deviceSafetyLog.fort;

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
@LogDesc(value="运维审计堡垒机",tableName="fort_audit",topicName="fort-audit")
public class FortAudit {
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

    @FieldDesc("堡垒机系统用户名")
    private	String 	user_account;
    @FieldDesc("客户端ip")
    @RelateField("src_Ip")
    private	String 	client_ip;
    @FieldDesc("堡垒机操作类型")
    private	String client_opt;
    @FieldDesc("会话ID号")
    private	String session_id;
   /* @FieldDesc("发生时间")
    private	String event_time;*/
    @FieldDesc("资源账号")
    private	String resource_count;
    @FieldDesc("资源IP")
    private	String resource_ip;
    @FieldDesc("资源操作类型")
    private	String resource_opt;
    @FieldDesc("命令详情")
    private	String opt_detail;
    @FieldDesc("返回结果")
    private	String opt_result;
    @FieldDesc("链接主机协议")
    private	String conn_type;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
