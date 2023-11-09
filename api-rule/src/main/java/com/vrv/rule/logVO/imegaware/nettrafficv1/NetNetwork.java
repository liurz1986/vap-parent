package com.vrv.rule.logVO.imegaware.nettrafficv1;

import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月3日 下午3:55:20 
* 类说明 
*/
@LogDesc("网络工作")
@Data
public class NetNetwork {
	 @FieldDesc("时间")  
	 private String  time;
	 @FieldDesc("源IP")
	 private	String 	src_ip;
	 @FieldDesc("TTL")
	 private	String 	ttl;
	 @FieldDesc("窗口")
	 private	String 	window;
	 @FieldDesc("浏览器")
	 private	String 	browser;
	 @FieldDesc("xforward")
	 private	String 	x_forward;
	 @FieldDesc("AJB生产者")
	 private	String 	ajb_producer;
	 @FieldDesc("AJB主机")
	 private	String 	ajb_host;
	 @FieldDesc("vrv接收时间")
	 private	String 	vrv_receive_time;
	 @FieldDesc("dt")
	 private	String 	dt;
	 @FieldDesc("省份")
	 private	String 	province;
	 @FieldDesc("处理时间")
	 private Timestamp triggerTime;
}
