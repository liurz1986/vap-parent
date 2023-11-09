package com.vrv.vap.toolkit.tools;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;

/**
 * 时间工具
 *
 * @author xw
 * @date 2015年10月9日
 */
public final class TimeTools {
    private static Log log = LogFactory.getLog(TimeTools.class);

    /**
     * 一小时的分钟数
     */
    public static final int HOUR_MIN = 60;

    /**
     * 一天的小时数
     */
    public static final int DAY_HOUR = 24;

    /**
     * 一个月的天数(31)
     */
    public static final int MONTH_DAY = 31;

    /**
     * 一年的天数(365)
     */
    public static final int YEAR_DAY = 365;

    /**
     * 毫秒数
     */
    public static final long MS = 1000;

    /**
     * 一分钟的毫秒数
     */
    public static final long MIN_MS = 60 * MS;

    /**
     * 一小时的毫秒数
     */
    public static final long HOUR_MS = 60 * MIN_MS;

    /**
     * 一天的毫秒数
     */
    public static final long DAY_MS = 24 * HOUR_MS;

    /**
     * 时间格式 yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    public static final String TIME_FMT_1 = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * 时间格式 yyyy-MM-dd HH:mm:ss
     */
    public static final String TIME_FMT_2 = "yyyy-MM-dd HH:mm:ss";

    /**
     * 时间格式 yyyy/MM/dd HH:mm:ss
     */
    public static final String TIME_FMT_3 = "yyyy/MM/dd HH:mm:ss";

    /**
     * 时间格式 yyyyMMddHHmmss
     */
    public static final String TIME_FMT_4 = "yyyyMMddHHmmss";

    /**
     * 时间格式yyyy-MM-dd 00:00:00
     */
    public static final String TIME_FMT_5 = "yyyy-MM-dd 00:00:00";

    /**
     * 时间格式 yyyy-MM-dd 23:59:59
     */
    public static final String TIME_FMT_6 = "yyyy-MM-dd 23:59:59";

    public static final String TIME_FMT_7 = "MM-dd HH:00";

    /**
     * 日期格式
     */
    public static final String DATE_FMT = "yyyy-MM-dd";

    /**
     * 时间格式 yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     */
    public static final String TIME_FMT_8 = "yyyy-MM-dd'T'HH:mm:ss.SSS'+0800'";

    /**
     * 按小时偏移时间
     *
     * @Date time
     * @int hour
     */
    public static Date setOffectHour(Date time, int hour) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(time);
        cal.add(Calendar.HOUR, hour);
        return cal.getTime();
    }

    /**
     * 获取UTC时间
     *
     * @return
     * @Date time
     */
    public static Date gmtToUtcTime(Date time) {
        return setOffectHour(time, -8);
    }

    /**
     * GMT转UTC时间
     *
     * @return
     * @Date time
     */
    public static String gmtToUtcTimeAsString(Date time) {
        return format2(gmtToUtcTime(time));
    }

    /**
     * GMT转UTC时间 输出utc的时间格式格式
     *
     * @return
     * @Date time
     */
    public static String gmtToUtcTimeAsString2(Date time) {
        return format1(gmtToUtcTime(time));
    }

    /**
     * 获取GMT时间
     *
     * @return
     * @Date time
     */
    public static Date utcToGmtTime(Date time) {
        return setOffectHour(time, 8);
    }

    public static String format7(Date date) {
        return format(date, TIME_FMT_7);
    }

    /**
     * UTC转GMT时间
     *
     * @return
     * @String utcTime
     */
    public static String utcToGmtTimeAsString(String utcTime) {
        return utcToGmtTime(utcTime, TIME_FMT_1, TIME_FMT_2);
    }

    private static String utcToGmtTime(String utcTime, String utcTimePatten, String localTimePatten) {
        SimpleDateFormat utcFormater = new SimpleDateFormat(utcTimePatten);
        utcFormater.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date gpsUTCDate = null;
        try {
            // utc格式不正确，如：2019-05-31T16:02:55Z
            if (utcTime.length() == 20 && utcTime.indexOf("T") > -1 && utcTime.endsWith("Z")) {
                utcTime = utcTime.replace("Z", ".000Z");
            }
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

    /**
     * 获取今天时间
     *
     * @return
     */
    public static Date getNow() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 获取指定时间n秒后的时间
     */
    public static Date getAfterTimeBySecond(Date date, int second) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND, second);
        return cal.getTime();
    }

    /**
     * 获取当前n分钟前的时间
     */
    public static Date getNowBeforeByMinute(int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MINUTE, 0 - min);
        return cal.getTime();
    }

    /**
     * 获取指定时间前n分钟前的时间
     */
    public static Date getNowBeforeByMinute(Date date, int min) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MINUTE, 0 - min);
        return cal.getTime();
    }

    /**
     * String 转 Calendar
     */
    public static Calendar stringToCalendar(String dateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Calendar calendar = Calendar.getInstance();
            Date date = sdf.parse(dateStr);
            calendar.setTime(date);
            return calendar;
        } catch (ParseException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 获取传入时间的前几分钟时间
     */
    public static Date getTimeBeforeByMinute(int min, Calendar cal) {
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MINUTE, 0 - min);
        return cal.getTime();
    }

    /**
     * 获取当前n分钟前的时间不包含秒
     *
     * @return
     * @int min
     */
    public static Date getNowBeforeByMinuteAbs(int min) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.add(Calendar.MINUTE, 0 - min);
        return cal.getTime();
    }

    /**
     * 获取当前n小时前的时间
     *
     * @return
     * @int hour
     */
    public static Date getNowBeforeByHour(int hour) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, 0 - hour);
        return cal.getTime();
    }

    /**
     * 获取当前n小时前的时间不包含分钟
     *
     * @return
     * @int hour
     */
    public static Date getNowBeforeByHourAbs(int hour) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.add(Calendar.HOUR, 0 - hour);
        return cal.getTime();
    }

    /**
     * 获取指定date的n天前的时间(开始时间)
     *
     * @return
     * @int day
     */
    public static Date getNowBeforeByDate(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取指定date的n天前的时间(开始时间)
     *
     * @return
     * @int day
     */
    public static Date getNowBeforeByDate2(Date date, int day) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前n天前的时间(开始时间)
     *
     * @return
     * @int day
     */
    public static Date getNowBeforeByDay(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前n天前的时间（结束时间）
     *
     * @return
     * @int day
     */
    public static Date getNowBeforeByDay2(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前n周前的时间 (有问题实际是7天前的数据)
     *
     * @return
     * @int week
     */
    public static Date getNowBeforeByWeek(int week) {
        return getNowBeforeByDay(week * 7);
    }

    /**
     * 获取当前n月前的时间
     *
     * @return
     * @int month
     */
    public static Date getNowBeforeByMonth(int month) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, 0 - month);
        return cal.getTime();
    }

    /**
     * 获取一段时间的天数,最少返回1天
     *
     * @return
     */
    public static int getDays(Date start, Date end, int offset) {
        if (null == start || null == end) {
            return 1;
        }

        int day = new BigDecimal(end.getTime() - start.getTime()).divide(new BigDecimal(DAY_MS), 2).intValue();

        return 0 == day ? 1 : (day + offset);
    }

    /**
     * 获取一段时间的天数,最少返回1天
     *
     * @return
     */
    public static int getDays(Date start, Date end) {
        return getDays(start, end, 0);
    }

    /**
     * 获取一段时间的月数,最少返回1月
     *
     * @return
     */
    public static int getMonths(Date start, Date end) {
        int months = 1;

        Calendar st = Calendar.getInstance();
        st.setTime(start);

        Calendar ed = Calendar.getInstance();
        ed.setTime(end);

        while (st.before(ed)) {
            st.add(Calendar.MONTH, 1);
            months++;
        }

        return months;
    }

    /**
     * 获取一段时间毫秒数
     *
     * @return
     */
    public static long getMillisecond(Date start, Date end) {
        return end.getTime() - start.getTime();
    }

    /**
     * 格式化时间 yyyy/MM/dd HH:mm:ss
     *
     * @return
     * @Date date
     */
    public static String format(Date date) {
        return format(date, TIME_FMT_3);
    }

    /**
     * 格式化时间 yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @long time
     */
    public static String format2(long time) {
        return format2(new Date(time));
    }

    public static String format7(long time) {
        return format7(new Date(time));
    }

    /**
     * 格式化时间 yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @Date date
     */
    public static String format2(Date date) {
        return format(date, TIME_FMT_2);
    }

    /**
     * 格式化时间 yyyy-MM-dd'T'HH:mm:ss.SSS'Z'
     *
     * @return
     * @Date date
     */
    public static String format1(Date date) {
        return format(date, TIME_FMT_1);
    }

    /**
     * 格式化时间
     *
     * @return
     * @Date date   时间
     * @String format 格式
     */
    public static String format(Date date, String format) {
        if (null == date) {
            return null;
        }
        return new SimpleDateFormat(format).format(date);
    }

    /**
     * 格式化时间yyyyMMddHHmmss
     *
     * @return
     * @Date date
     */
    public static String formatTimeStamp(Date date) {
        return new SimpleDateFormat(TIME_FMT_4).format(date);
    }

    /**
     * 将指定时间分为num段
     *
     * @return
     * @Date start 开始时间
     * @Date end   结束时间
     * @int num   分段个数
     */
    public static List<Date> splitTimeRange(Date start, Date end, int num) {
        int part = Math.max(num, 1);
        List<Date> list = new ArrayList<Date>(part);
        list.add(start);
        Calendar c1 = Calendar.getInstance();
        c1.setTime(start);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(end);
        long millisecond = getMillisecond(start, end) / part;
        int mode = Calendar.MILLISECOND;
        int offset = (int) millisecond;
        if (millisecond > Integer.MAX_VALUE) {
            mode = Calendar.MINUTE;
            offset = (int) (millisecond / MIN_MS);
        }

        while (c1.before(c2)) {
            c1.add(mode, offset);
            list.add(c1.getTime());
        }

        if (list.size() < part) {
            list.add(c2.getTime());
        }
        return list;
    }

    /**
     * 获取本周第一天的日期
     *
     * @return
     */
    public static Date getFirstDayOfWeek() {
        int dayOfWeek = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        return TimeTools.getNowBeforeByDay(dayOfWeek - 2);
    }

    /**
     * 获取n周前的星期一
     *
     * @return
     */
    public static Date getFirstDayOfBeforeWeek(int n) {
        Date date = TimeTools.getFirstDayOfWeek();
        return TimeTools.getNowBeforeByDate(date, n * 7);
    }

    /**
     * 获取本月第一天的日期
     *
     * @return
     */
    public static Date getFirstDayOfMonth() {
        int dayOfMonth = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        return TimeTools.getNowBeforeByDay(dayOfMonth - 1);
    }

    /**
     * 获取指定月份的第一天的日期
     *
     * @return
     */
    public static Date getFirstDayOfMonth(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取上一个月第一天的日期 2017-05-01 00:00:00
     *
     * @return
     */
    public static Date getFirstDayOfBeforeMonth() {
        return TimeTools.getFirstDayOfMonth(TimeTools.getNowBeforeByMonth(1));
    }

    /**
     * 获取指定日期开始时间,yyyy-MM-dd 00:00:00
     *
     * @return
     */
    public static String getStartTimeForDate(Date date) {
        return format(date, TIME_FMT_5);
    }

    /**
     * 获取指定日期最后时间,yyyy-MM-dd 23:59:59
     *
     * @return
     */
    public static String getEndTimeForDate(Date date) {
        return format(date, TIME_FMT_6);
    }

    /**
     * 字符串转Date类型 yyyyMMddHHmmss
     *
     * @return
     * @String time
     */
    public static Date parseDate(String time) {
        return TimeTools.parseDate(time, TIME_FMT_4);
    }

    /**
     * 字符串转Date类型 yyyy-MM-dd HH:mm:ss
     *
     * @param time
     * @return
     */
    public static Date parseDate2(String time) {
        return TimeTools.parseDate(time, TIME_FMT_2);
    }

    /**
     * 日期转字符串
     *
     * @return
     */
    public static String formatDateFmt(Date date) {
        return format(date, DATE_FMT);
    }

    /**
     * 字符串转Date类型
     *
     * @return
     * @String time   时间
     * @String format 格式
     */
    public static Date parseDate(String time, String format) {
        try {
            return new SimpleDateFormat(format).parse(time);
        } catch (ParseException e) {
            try {
                return DateTime.parse(time).toDate();
            } catch (Exception e2) {
                log.error("", e2);
            }
        }
        return null;
    }

    /**
     * 获取本月最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth() {
        return getLastDayOfMonth(TimeTools.getNowBeforeByMonth(0));
    }

    /**
     * 获取指定月最后一天
     *
     * @return
     */
    public static Date getLastDayOfMonth(Date nextMonth) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(nextMonth);
        cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + 1);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        cal.add(Calendar.DATE, -1);
        return cal.getTime();
    }

    /**
     * 获取指定日期的前一个月的第一天的日期
     *
     * @return
     */
    public static Date getFirstDayOfMonth(Date date, int month) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.MONTH, 0 - month);
        return cal.getTime();
    }

    /**
     * 获取上月最后一天 2017-05-31 23:59:59
     *
     * @return
     */
    public static Date getLastDayOfBeforeMonth() {
        return TimeTools.getLastDayOfMonth(TimeTools.getNowBeforeByMonth(1));
    }

    /**
     * 中国标准时间（yyyy-MM-dd'T'HH:mm:ss.SSS'+0800'） 转  yyyy-MM-dd HH:mm:ss
     *
     * @return
     * @String timeStr
     */
    public static String chineseTimeFormat(String timeStr) {
        SimpleDateFormat srcFormater = new SimpleDateFormat(TIME_FMT_8);
        SimpleDateFormat dstFormater = new SimpleDateFormat(TIME_FMT_2);
        String result = "";
        try {
            Date time = srcFormater.parse(timeStr);
            result = dstFormater.format(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static void main(String[] args) {
        System.out.println(getFirstDayOfMonth(TimeTools.parseDate("20200701", "yyyyMMdd"), 1));
    }
}
