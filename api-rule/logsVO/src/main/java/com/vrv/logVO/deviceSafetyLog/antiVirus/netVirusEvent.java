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
@LogDesc(value="防病毒-病毒检测日志",tableName="net_virus_event",topicName="net-virus-event")
public class netVirusEvent {
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

    @FieldDesc("源IP（客户端IP）")
    @RelateField("src_Ip")
    private	String 	src_ip;
    @FieldDesc("源区域")
    private String src_area;
    @FieldDesc("目的IP（服务器IP）")
    @RelateField("dst_Ip")
    private	String 	dst_ip;
    @FieldDesc("目标区域")
    private	String dst_area;
    @FieldDesc("源port")
    @RelateField("src_port")
    private	String 	src_port;
    @FieldDesc("目标port")
    @RelateField("dst_port")
    private String dst_port;
    @FieldDesc("传输层协议")
    private	String transport_protocol;
    /*  @FieldDesc("发生时间")
  private	String 	event_time;*/
    @FieldDesc("应用层协议")
    private	String app_protocol;
    @FieldDesc("病毒库版本信息")
    private	String virus_lib_version;
    @FieldDesc("产品软件版本信息")
    private	String product_version;
    @FieldDesc("检出类型")
    private	String detect_type;
    @FieldDesc("病毒名称")
    private	String virus_name;
    @FieldDesc("病毒类别")
    private	String virus_type;
    @FieldDesc("病毒行为")
    private	String virus_act;
    @FieldDesc("威胁文件名称")
    private	String threat_file_name;
    @FieldDesc("威胁文件大小")
    private	String threat_file_size;
    @FieldDesc("文件类型（word、pdf等）")
    private	String threat_file_type;
    @FieldDesc("文件md5值")
    private	String threat_file_md5;
    @FieldDesc("检出对象")
    private	String detect_object;
    @FieldDesc("事件描述")
    private	String event_detail;
    @FieldDesc("威胁等级")
    private	String event_level;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
