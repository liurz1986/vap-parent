package com.vrv.rule.logVO.netReductive.resultVO;

import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;
import com.vrv.rule.logVO.violence.resultVO.BastionhostV1VO1;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月3日 上午10:26:11 
* 类说明 
*/
@LogDesc("暴力破解返回VO1")
@Data
public class ForensicsV1VO {

	@FieldDesc("开始时间")
	private  Timestamp start_time;
	@FieldDesc("结束时间")
	private  Timestamp end_time;
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
	@FieldDesc("数量")
	private Long cnt;
}
