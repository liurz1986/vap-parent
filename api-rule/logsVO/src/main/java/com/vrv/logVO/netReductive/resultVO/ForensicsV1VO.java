package com.vrv.logVO.netReductive.resultVO;

import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月3日 上午10:26:11 
* 类说明 
*/
@LogDesc(value="暴力破解返回VO1",tableName="forceBroken",topicName="v1-ForensicsV1VO")
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
