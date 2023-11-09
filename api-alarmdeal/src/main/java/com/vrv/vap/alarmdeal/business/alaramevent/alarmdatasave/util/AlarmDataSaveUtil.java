package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 梁国露
 * @date 2021年11月01日 15:43
 */
public class AlarmDataSaveUtil {
    public static boolean runStatus = true;

    /**
     * 判断版本
     *
     * @param version 版本号
     * @return 处理后的版本号
     */
    public static String getVersion(String version){
        if (StringUtils.isNotEmpty(version)) {
            String pattern = "(?<=\"version\":).*?(?=,\")";

            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(version);
            if (m.find()) {
                return  m.group();
            }
        }
        return "";
    }
}
