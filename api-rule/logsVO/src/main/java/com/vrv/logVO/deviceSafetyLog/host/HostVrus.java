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
@LogDesc(value="主机防病毒日志",tableName="host_virus",topicName="host-virus")
public class HostVrus {
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
    @FieldDesc("病毒库版本")
    private	String 	virus_lib_version;
    @FieldDesc("厂商名称")
    private	String product_version;
    @FieldDesc("病毒类型")
    private	String 	virus_type;
    @FieldDesc("病毒名称")
    private	String virus_name;
    @FieldDesc("病毒行为")
    private	String virus_act;
    @FieldDesc("文件路径")
    private	String 	threat_file_path;
    @FieldDesc("文件md5")
    private	String 	threat_file_md5;
    @FieldDesc("文件名称")
    private	String threat_file_name;
    @FieldDesc("文件类型")
    private	String 	threat_file_type;
    @FieldDesc("威胁等级")
    private	String event_level;
    @FieldDesc("处理结果")
    private	String handle_result;
    @FieldDesc("处理方式")
    private	String 	handle_method;
    @FieldDesc("设备ip")
    @RelateField("src_Ip")
    private	String device_ip;
    @FieldDesc("设备mac")
    private	String device_mac;
    @FieldDesc("发现方式")
    private	String 	find_method;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
