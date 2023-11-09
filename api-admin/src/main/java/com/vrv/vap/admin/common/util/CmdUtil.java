package com.vrv.vap.admin.common.util;

import com.vrv.vap.admin.util.CleanUtil;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author lilang
 * @date 2021/4/23
 * @description
 */
public class CmdUtil {

    private static final Logger logger = LoggerFactory.getLogger(CmdUtil.class);

    public static List<String> runShell(String shStr) {
        List<String> strList = new ArrayList<>();
        try {
            String[] cmd = new String[]{"/bin/sh","-c",shStr};
            Process process = Runtime.getRuntime().exec(CleanUtil.cleanStrArray(cmd),null,null);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            process.waitFor();
            while ((line = input.readLine()) != null){
                strList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return strList;
    }

    public static String runShellCmd(String shStr) {
        List<String> strList = new ArrayList<>();
        try {
            logger.info("执行命令：" + shStr);
            String[] cmd = new String[]{"/bin/sh","-c",shStr};
            Process process = Runtime.getRuntime().exec(CleanUtil.cleanStrArray(cmd),null,null);
            InputStreamReader ir = new InputStreamReader(process.getInputStream());
            LineNumberReader input = new LineNumberReader(ir);
            String line;
            process.waitFor(1,TimeUnit.MINUTES);
            while ((line = input.readLine()) != null){
                strList.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isNotEmpty(strList)) {
            logger.info("执行结果：" + strList.get(0));
            return strList.get(0);
        }
        return "";
    }
}
