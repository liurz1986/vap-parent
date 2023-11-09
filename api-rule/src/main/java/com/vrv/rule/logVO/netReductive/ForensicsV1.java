package com.vrv.rule.logVO.netReductive;

import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月3日 上午10:00:50 
* 类说明  网络还原日志类
*/
@Data
@LogDesc(" 网络还原日志类")
public class ForensicsV1 {
    
	@FieldDesc("时间戳")
	private  Timestamp   tm;
	@FieldDesc("源IP")
    private  String   src_ip;
	@FieldDesc("源端口")
    private  String   src_port;
	@FieldDesc("目的IP")
    private  String   dst_ip;
	@FieldDesc("目的端口")
    private  String   dst_port;
	@FieldDesc("应用协议")
    private  String   app_protocol;
	@FieldDesc("传输协议")
    private  String   transport_protocol;
}
