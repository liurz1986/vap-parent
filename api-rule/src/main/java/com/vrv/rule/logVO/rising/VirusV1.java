package com.vrv.rule.logVO.rising;

import java.io.Serializable;
import java.sql.Timestamp;

import com.vrv.rule.logVO.FieldDesc;
import com.vrv.rule.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年1月3日 上午10:43:12 
* 类说明  瑞星-病毒日志
*/
@Data
@LogDesc("瑞星-病毒V1日志")
public class VirusV1 implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	@FieldDesc("主机时间")
	private    String time_host ;
	@FieldDesc("生产方名称")
    private    String manufact_name   ;
	@FieldDesc("设备ID")
    private    String device_id ;
	@FieldDesc("产品版本")
    private    String product_version      ;
	@FieldDesc("病毒库版本")
    private    String virus_lib_version    ;
	@FieldDesc("设备唯一编号")
    private    String device_only_id  ;
	@FieldDesc("告警等级")
    private    String threat_level    ;
	@FieldDesc("描述")
    private    String description   ;
	@FieldDesc("ID")
    private    String id ;
	@FieldDesc("病毒发现时间")
    private    String virus_detect_time    ;
	@FieldDesc("威胁文件大小")
    private    String threat_file_size     ;
	@FieldDesc("协议类型")
    private    String protocal_type   ;
	@FieldDesc("威胁存储文件名称")
    private    String threat_storefile_name       ;
	@FieldDesc("威胁存储文件类型")
    private    String threat_file_type     ;
    @FieldDesc("源IPV4")
    private    String src_ip_v4 ;
    @FieldDesc("源MAC")
    private    String src_mac  ;
    @FieldDesc("源端口")
    private    Long  src_port;
    @FieldDesc("目的IPV4")
    private    String dst_ip_v4 ;
    @FieldDesc("目的MAC")
    private    String dst_mac  ;
    @FieldDesc("目的端口")
    private    Long  dst_port; 
    @FieldDesc("发现类型")
    private    String detect_type     ;
    @FieldDesc("病毒名称")
    private    String virus_name      ;
    @FieldDesc("病毒操作")
    private    String virus_action    ;
    @FieldDesc("病毒类型")
    private    String virus_type      ;
    @FieldDesc("发现实体")
    private    String detection_object     ;
    @FieldDesc("AJB生产者")
    private    String ajb_producer    ;
    @FieldDesc("AJB主机")
    private    String ajb_host ;
    @FieldDesc("VRV接收时间")
    private    String vrv_receive_time     ;
    @FieldDesc("时间")
    private    String dt ;
    @FieldDesc("省份")
    private    String province ;
    @FieldDesc("处理时间")
    private Timestamp triggerTime;

}
