package com.vrv.vap.admin.common.util;

import com.vrv.vap.admin.util.CleanUtil;
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
