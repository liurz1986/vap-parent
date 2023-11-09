package com.vrv.vap.line.tools;

import com.vrv.vap.toolkit.tools.RemoteSSHTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;

public class LoadTool {
    public static final Log log = LogFactory.getLog(LoadTool.class);

    public static boolean importDatabase2(String hostIP, String userName, String password, String loadSql) {
        StringBuilder stringBuilder=new StringBuilder();
        stringBuilder.append("mysql").append(" -h").append(hostIP);
        stringBuilder.append(" -u").append(userName).append(" -p").append(password).append(" -e\"").append(loadSql).append("\"");
        try {
            RemoteSSHTools localTools = RemoteSSHTools.build("localhost",22,"","");
            String message = localTools.localExecuteCmd(stringBuilder.toString(),true);
            log.info("执行成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }
}
