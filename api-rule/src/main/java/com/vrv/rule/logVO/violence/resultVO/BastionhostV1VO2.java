package com.vrv.rule.logVO.violence.resultVO;

import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月2日 下午2:12:40 
* 类说明 
*/
@LogDesc("暴力破解返回VO2")
@Data
public class BastionhostV1VO2 {
	  @FieldDesc("开始时间")
	  private Timestamp start_time;
	  @FieldDesc("结束时间")
	  private Timestamp end_time;
      @FieldDesc("协议名称")
      private String protocol_name;
  	  @FieldDesc("服务器IP")
      private String   service_ip;
  	  @FieldDesc("服务器端口")
      private String service_port;
  	  @FieldDesc("数量")
  	  private Long cnt;
}
