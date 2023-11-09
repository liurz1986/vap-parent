package com.vrv.rule.logVO.rising;

import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月3日 上午11:25:35 
* 类说明 
*/
@Data
@LogDesc("瑞星病毒精简版本")
public class VirusV1Simple {
	/**
	 * 
	 */
	@FieldDesc("发现时间")
	private Timestamp virus_detect_time;
	@FieldDesc("协议类型")
    private    String protocal_type   ;
    @FieldDesc("源IPV4")
    private    String src_ip_v4 ;
    @FieldDesc("目的IPV4")
    private    String dst_ip_v4 ;
    @FieldDesc("源端口")
    private    Long  src_port;
    @FieldDesc("目的端口")
    private    Long  dst_port; 
    @FieldDesc("病毒类型")
    private  String virus_type;
    
}
