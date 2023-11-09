package com.vrv.logVO.monior;

import java.sql.Timestamp;

import com.vrv.logVO.FieldDesc;
import com.vrv.logVO.LogDesc;
import com.vrv.logVO.RelateField;

import lombok.Data;

/**
 * @author TB
 * @date 2019/10/11 10:00
 */
@Data
@LogDesc(value="交换机事件",tableName="switch",topicName="switchVO")
public class SwitchVo {

	@FieldDesc("资产GUID")
    private String assetGuid;
	@FieldDesc("日志产生时间")
    private String inDate;
	@FieldDesc("icmpPing")
    private String icmpPing;
	@FieldDesc("收集IP")
	@RelateField("src_Ip")
    private String collectorIp;
	@FieldDesc("instanceId")
    private String instanceId;
	@FieldDesc("dataPickerPlugin")
    private String dataPickerPlugin;
	@FieldDesc("表名")
    private String event_Table_Name;
	@FieldDesc("运行时间")
    private String runningTime;
	@FieldDesc("avgBusy1")
    private String avgBusy1;
	@FieldDesc("h3cEntityExtMemUsage")
    private String h3cEntityExtMemUsage;
	@FieldDesc("ifNumber")
    private Integer ifNumber;
	@FieldDesc("运行详细信息")
    private RunningDetails runningDetails;
	@FieldDesc("ifEntryList")
    private IfEntry[] ifEntryList;
	@FieldDesc("处理时间")
    private Timestamp triggerTime;
	@FieldDesc("处理时间")
	private String guid;

}
