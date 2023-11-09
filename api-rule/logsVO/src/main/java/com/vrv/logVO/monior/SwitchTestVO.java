package com.vrv.logVO.monior;

import java.sql.Timestamp;
import java.util.List;

import lombok.Data;

/**
 * * 
 * 
 * @author wudi   E‐mail:wudi@vrvmail.com.cn
 *          @version 创建时间：2019年10月14日 上午9:57:29  类说明
 */
@Data
public class SwitchTestVO {

	private String assetGuid;
	private String ifNumber;
	private RunningDetailsTest runningDetailsTest;
	private IfEntryTest[] ifEntryList;

	@Data
	public static class RunningDetailsTest {
		private String startTime;
		private String activeSessions;
		private String bytesReceived;
		private String bytesSent;
	}

	@Data
	public static class IfEntryTest {
		private String ifIndex;
		private String ifDescr;
		private String ifType;
		private String ifMtu;

	}

}
