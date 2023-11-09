package com.vrv.logVO.deviceSafetyLog.NetflowProbe;

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
@LogDesc(value="流量日志",tableName="probe_net",topicName="probe-net")
public class ProbeNet {
    @FieldDesc("上报设备IP")
    private String  report_ip;
    @FieldDesc("上报设备IP转换")
    private	String 	report_ip_num;
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


    @FieldDesc("请求方法")
    private	String 	http_method;
    @FieldDesc("访问的URL")
    private	String 	url;
    @FieldDesc("应用id")
    private	String 	app_id;
    @FieldDesc("请求头")
    private	Long req_head;
    @FieldDesc("http请求参数")
    private	Long req_body;
    @FieldDesc("响应码")
    private	Long status_code;
    @FieldDesc("http类型")
    private	Long http_type;
    @FieldDesc("响应头")
    private	Long resp_header;
    @FieldDesc("响应体")
    private	Long resp_body;
    @FieldDesc("响应时长")
    private	String resp_times;
    @FieldDesc("源ip")
    @RelateField("src_Ip")
    private	String 	src_ip;
    @FieldDesc("源区域")
    private	String src_area;
    @FieldDesc("目的IP")
    @RelateField("dst_Ip")
    private	String dst_ip;
    @FieldDesc("目标区域")
    private	String 	dst_area;
    @FieldDesc("源port")
    @RelateField("src_port")
    private	String 	src_port;
    @FieldDesc("目标port")
    @RelateField("dst_port")
    private String dst_port;
    @FieldDesc("发生小时")
    private	Long time_slot;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
    
    
    public static void main(String[] args) {
    	
	}
    
    
}
