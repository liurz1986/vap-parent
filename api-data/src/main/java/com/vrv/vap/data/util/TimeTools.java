package com.vrv.vap.data.util;


import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public final class TimeTools {

    private static final Logger log = LoggerFactory.getLogger(TimeTools.class);


    public static final String UTC_PTN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final String GMT_PTN = "yyyy-MM-dd HH:mm:ss";


    public static final int ONE_DAY = 1000 * 60 * 60 * 24;


    private static final Set<String> FORMAT_CHECKER = new HashSet<>(Arrays.asList(new String[]{
            UTC_PTN,
            GMT_PTN,
            "yyyy-MM-dd HH:mm:ss.SSSZ",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy-MM-dd",
            "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
            "yyyy.MM.dd G 'at' HH:mm:ss z",
    }));
    /**
     * 缓存格式化对象
     */
    private static final Map<String, SimpleDateFormat> FORMAT_MAP = new HashMap();


    /**
     * 根据格式化字符，获取一个格式化实例
     */
    private static SimpleDateFormat getFormatter(String format) {
        if (FORMAT_MAP.containsKey(format)) {
            return FORMAT_MAP.get(format);
        }
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        FORMAT_MAP.put(format, formatter);
        return formatter;

    }

    private static SimpleDateFormat DEFAULT_FORMAT = getFormatter("yyyy-MM-dd");

    public static String format(Date date) {
        return format(date, GMT_PTN);
    }

    public static String format(Date date, String format) {
        if (null == date) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }


    public static Date parse(String dateString) {
        return parse(dateString, getPattern(dateString));

    }

    public static Date parse(String dateString, String format) {
        if (StringUtils.isEmpty(dateString)) {
            return null;
        }
        try {
            return getFormatter(format).parse(dateString);
        } catch (ParseException e) {
            log.error("",e);
        }
        return null;
    }

    /**
     * 得到几天前的时间
     */
    public static Date getDateBefore(Date date, int day) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - day);
        return now.getTime();
    }

    /**
     * 通过撞库找出能匹配的 Pattern
     */
    public static String getPattern(String dateString) {
        for (String format : FORMAT_CHECKER) {
            try {
                getFormatter(format).parse(dateString);
                return format;
            } catch (ParseException e) {
                log.error("",e);
            }

        }
        return GMT_PTN;
    }

    /**
     * 格式化时间
     *
     * @param date
     * @return
     */
    public static String formatDate(Date date, String format) {
        if (null == date) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * utc 转 本地时间
     *
     * @param utcTime
     * @return
     */
    public static String utc2Local(String utcTime) {
        return utc2Local(utcTime, UTC_PTN, GMT_PTN);
    }

    public static String utc2Local(String utcTime, String utcTimePatten, String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            gpsUTCDate = utcFormater.parse(utcTime);
        } catch (ParseException e) {
            try {
                if (null == utcTime || utcTime.length() < 23) {
                    return utcTime;
                }
                utcTime = utcTime.substring(0, 23) + "Z";
                gpsUTCDate = utcFormater.parse(utcTime);
            } catch (ParseException e2) {
                log.error("时间格式错误", e2);
                return utcTime;
            }
        }
        SimpleDateFormat localFormater = new SimpleDateFormat(localTimePatten);
        localFormater.setTimeZone(TimeZone.getDefault());
        String localTime = localFormater.format(gpsUTCDate.getTime());
        return localTime;
    }
}