package com.vrv.logVO.rising.resultVO;

import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月3日 上午11:25:35 
* 类说明 
*/
@Data
@LogDesc(value="病毒VO",tableName="virusVO",topicName="v1-VirusV1VO")
public class VirusV1VO {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@FieldDesc("开始时间")
	private Timestamp start_time;
	@FieldDesc("结束时间")
	private Timestamp end_time;
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
    private    String virus_type;
    @FieldDesc("数量")
    private Long cnt;
}
