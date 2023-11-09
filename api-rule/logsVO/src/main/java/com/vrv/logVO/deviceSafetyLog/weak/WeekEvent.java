package com.vrv.logVO.deviceSafetyLog.weak;

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
@LogDesc(value="脆弱性扫描设备扫描结果日志",tableName="weak_event",topicName="weak-event")
public class WeekEvent {
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
    private	String 	device_ip;
    @FieldDesc("设备名称")
    private String device_name;
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
   /* @FieldDesc("发生时间")
    private	String event_time;*/
    @FieldDesc("被扫描系统域名")
    private	String dst_domain_name;
    @FieldDesc("扫描任务ID")
    private	String scan_id;
    @FieldDesc("扫描任务类型")
    private	String scan_type;
    @FieldDesc("漏洞名称")
    private	String loophole_name;
    @FieldDesc("漏洞类型")
    private	String loophole_type;
    @FieldDesc("漏洞对应的CVE ID")
    private	String loophole_cve_id;
    @FieldDesc("漏洞风险等级")
    private	String loophole_risk_level;
    @FieldDesc("建议修补方法")
    private	String loophole_reepair;
    @FieldDesc("漏洞描述")
    private	String loophole_desc;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
