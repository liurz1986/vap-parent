package com.vrv.logVO.monior;

import java.sql.Timestamp;
import java.util.List;

import com.vrv.logVO.LogDesc;

import lombok.Data;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2019年10月12日 下午1:52:54 
* 类说明 
*/
@LogDesc(value="交换机单体事件",tableName="switchVOSimple",topicName="switchVOSimple")
@Data
public class SwitchInputPOJO {

	private String assetGuid;
    private String inDate;
    private String icmpPing;
    private String collectorIp;
    private String instanceId;
    private String dataPickerPlugin;
    private String event_Table_Name;
    private String runningTime;
    private String avgBusy1;
    private String h3cEntityExtMemUsage;
    private String ifNumber;
    private String ifIndex;
    private String ifDescr;
    private String ifType;
    private String ifMtu;
    private String ifSpeed;
    private String ifPhysAddress;
    private String ifAdminStatus;
    private String ifOperStatus;
    private String ifLastChange;
    private String ifInOctets;
    private String ifInUcastPkts;
    private String ifInDiscards;
    private Timestamp triggerTime;
 


}
