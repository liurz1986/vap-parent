package com.vrv.logVO.deviceSafetyLog.db;

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
@LogDesc(value="数据库审计日志",tableName="db_audit",topicName="db-audit")
public class DbAudit {
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

    @FieldDesc("记录标识")
    private	String 	num_id;
    @FieldDesc("数据库账户")
    private String db_account;
    @FieldDesc("客户端ip")
    @RelateField("src_Ip")
    private	String 	client_ip;
    @FieldDesc("客户端端口")
    private	String client_port;
    @FieldDesc("服务器ip")
    private	String server_ip;
    @FieldDesc("服务器端口")
    private	String server_port;
    @FieldDesc("操作时间")
    private	String operation_time;
    @FieldDesc("数据库实例名")
    private	String instance_name;
    @FieldDesc("数据库名称")
    private	String db_name;
    @FieldDesc("sql语句")
    private	String operation_sql;
    @FieldDesc("链接方式")
    private	String conn_type;
    @FieldDesc("影响行数")
    private	String affect_rows;


    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
