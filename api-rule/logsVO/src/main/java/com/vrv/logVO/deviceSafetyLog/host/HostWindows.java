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
@LogDesc(value="windows主机日志",tableName="host_windows",topicName="host-windows")
public class HostWindows {
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
    @FieldDesc("上报时间")
    private	String 	report_time;
    @FieldDesc("类别")
    private	String type;
    @FieldDesc("时间")
    private	String 	time;
    @FieldDesc("主机名")
    private	String hostname;
    @FieldDesc("关键字")
    private	String keywords;
    @FieldDesc("事件类别")
    private	String 	eventType;
    @FieldDesc("安全级别编码")
    private	String 	severityValue;
    @FieldDesc("安全事件")
    private	String severity;
    @FieldDesc("事件id")
    private	String 	eventID;
    @FieldDesc("来源")
    private	String sourceName;
    @FieldDesc("任务")
    private	String task;
    @FieldDesc("记录id")
    private	String 	recordNumber;
    @FieldDesc("进程id")
    private	String processID;
    @FieldDesc("线程ID")
    private	String threadID;
    @FieldDesc("日志级别")
    private	String 	channel;
    @FieldDesc("消息")
    private	String 	message;
    @FieldDesc("事件接收时间")
    private	String 	eventReceivedTime;
    @FieldDesc("源模块名")
    private	String 	sourceModuleName;
    @FieldDesc("源模块类型")
    private	String 	sourceModuleType;
    @FieldDesc("上报IP")
    @RelateField("src_Ip")
    private	String 	hostIP;
    @FieldDesc("发现方式")
    private	String 	opcodeValue;
    @FieldDesc("来源的唯一编码")
    private	String 	providerGuid;
    @FieldDesc("操作码")
    private	String 	version;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
