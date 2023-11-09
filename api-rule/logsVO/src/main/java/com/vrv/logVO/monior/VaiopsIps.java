package com.vrv.logVO.monior;

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
@LogDesc(value="IPS性能日志",tableName="e_vaiops_ips",topicName="e-vaiops-ips")
public class VaiopsIps {
    @FieldDesc("设备id")
    private	String 	assetGuid;
    @FieldDesc("设备IP")
    @RelateField("src_Ip")
    private	String 	collectorIp;
    @FieldDesc("采集时间")
    private	String indate;
    @FieldDesc("CPU利用率")
    private	String avgBusy1;
    @FieldDesc("内存利用率")
    private	String h3cEntityExtMemUsage;
    @FieldDesc("总端口数")
    private	String ifNumber;
    @FieldDesc("运行时长")
    private	String runningTime;
    @FieldDesc("服务器可达")
    private	String icmpPing;
    @FieldDesc("表名")
    private	String event_Table_Name;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
