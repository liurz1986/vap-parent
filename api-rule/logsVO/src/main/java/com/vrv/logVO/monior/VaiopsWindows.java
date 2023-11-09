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
@LogDesc(value="windows性能日志",tableName="e_vaiops_windows",topicName="e-vaiops-windows")
public class VaiopsWindows {
    @FieldDesc("设备id")
    private	String 	assetGuid;
    @FieldDesc("设备IP")
    @RelateField("src_Ip")
    private	String 	collectorIp;
    @FieldDesc("CPU核数")
    private	String 	cpuCount;
    @FieldDesc("进程数")
    private String cpuProcessorNum;
    @FieldDesc("CPU使用率")
    private	String 	cpuUsedRate;
    @FieldDesc("逻辑磁盘总容量")
    private	String 	diskTotal;
    @FieldDesc("逻辑磁盘已使用容量")
    private	String diskUsed;
    @FieldDesc("磁盘利用率")
    private	String diskUsedRate;
    @FieldDesc("表名")
    private	String event_Table_Name;
    @FieldDesc("服务器可达")
    private	String icmpPing;
    @FieldDesc("采集时间")
    private	String inDate;
    @FieldDesc("内存利用率")
    private	String ramPercentAge;
    @FieldDesc("内存大小")
    private	String ramSize;
    @FieldDesc("已用内存")
    private	String ramUsed;
    @FieldDesc("运行时长")
    private	String runningTime;

    @FieldDesc("处理时间")
    private Timestamp triggerTime;
}
