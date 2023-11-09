package com.vrv.vap.monitor.server.common.util;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CmdExecute {
	public static  String executeCmd(String cmd) {
		List<String> queryExecuteCmd = ShellExecuteScript.queryExecuteCmd(CleanUtil.cleanString(cmd));
		if(queryExecuteCmd!=null) {
			return StringUtils.join(queryExecuteCmd,"\r\n");
		}else {
			return "";
		}
	}
}
