package com.vrv.logVO.violence;

import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月2日 上午11:54:22 
* 类说明 暴力破解 
*/
@LogDesc(value="暴力破解-日志1",tableName="forceBrokenLog1",topicName="v1-BastionhostV1")
@Data
public class BastionhostV1 {

	@FieldDesc("开始时间")
	private Timestamp  begin_time;
	@FieldDesc("协议名称")
    private String   protocol_name;
	@FieldDesc("用户IP")
    private String  user_ip;
	@FieldDesc("服务器IP")
    private String   service_ip;
	@FieldDesc("用户端口")
    private String   user_port;
	@FieldDesc("服务器端口")
    private String   service_port;
	@FieldDesc("处理时间")
	private Timestamp  triggerTime;
}
