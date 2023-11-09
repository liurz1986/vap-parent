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
@LogDesc(value="linux主机毒日志",tableName="host_linux",topicName="host-linux")
public class HostLinux {
    @FieldDesc("上报设备IP")
    @RelateField("src_Ip")
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
    @FieldDesc("用户")
    private	String 	user;
    @FieldDesc("日志类型")
    private	String logtype;
    @FieldDesc("信息")
    private	String 	detail;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
