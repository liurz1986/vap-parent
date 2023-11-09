package com.vrv.vap.monitor.agent.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 时间工具
 * 
 * @author xw
 * @date 2015年10月9日
 */
@Component
@Slf4j
public final class TimeTools {

	private static TimeTools timeTools;
	
	private TimeTools() {
	}

	public static final int HOUR_MIN = 60;
	public static final int DAY_HOUR = 24;
	public static final int MONTH_DAY = 31;
	public static final int YEAR_DAY = 365;

	// es查询开始时间上限
//	private static Date STARTTIME = null;
	// 毫秒数
	public static final long MS = 1000;
	// 一分钟的毫秒数
	public static final long MIN_MS = 60 * MS;
	// 一小时的毫秒数
	public static final long HOUR_MS = 60 * MIN_MS;
	// 一天的毫秒数
	public static final long DAY_MS = 24 * HOUR_MS;

	public static final String UTC_PTN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
	public static final String GMT_PTN = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME_PATTERN = "yyyy-MM-dd";
	public static final String TIMESTAMP = "yyyyMMddHHmmss";
	public static final String TIME_CH = "yyyy年MM月dd日";
	
	@PostConstruct 
	public void init(){
		timeTools = this;
	}

	/**
	 * 构造查询时间范围
	 * 
	 * @param timeRange
	 */
	public static Date[] buildTime(String timeRange) {
		String tmpRange = timeRange;
		if (StringUtils.isEmpty(tmpRange)) {
			tmpRange = TimeTools.formatTimeStamp(getNowBeforeByMonth(2)) + ";" + TimeTools.formatTimeStamp(getNow());
		}

		String[] date = tmpRange.split(";");
		Date[] range = new Date[2];
		if (date.length == 2 && StringUtils.isNumeric(date[0]) && Pattern.matches("[yMdHms]", date[1])) {
			range[0] = TimeTools.userTimeToDate(Integer.parseInt(date[0]), date[1]);

			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.MILLISECOND, 0);
			cal.set(Calendar.SECOND, 0);

			range[1] = cal.getTime();
		} else {
			SimpleDateFormat sdf = new SimpleDateFormat(TIMESTAMP);
			try {
				Date startTime = sdf.parse(date[0]);
				Date endTime = sdf.parse(date[1]);

				if (startTime.after(endTime)) {
					Date tmp = (Date) startTime.clone();
					startTime = endTime;
					endTime = tmp;
				}
				range[0] = startTime;
				range[1] = endTime;

			} catch (ParseException e) {
				log.error("", e);
			}
		}

		return range;
	}

	/**
	 * 构造查询时间范围(所有日期)
	 * 
	 * @param timeRange
	 */
	public static Date[] buildTimes(String timeRange, int field) {
		Date[] range = buildTime(timeRange);
		List<Date> list = new ArrayList<>();
		Calendar cal1 = Calendar.getInstance();
		cal1.setTime(range[0]);
		Calendar cal2 = Calendar.getInstance();
		cal2.setTime(range[1]);

		while (cal1.before(cal2)) {
			list.add(cal1.getTime());
			cal1.add(field, 1);
		}
		return list.toArray(new Date[0]);
	}

	/**
	 * 按小时偏移时间
	 * 
	 * @param time
	 * @param hour
	 */
	public static Date hourOffset(Date time, int hour) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(time);
		cal.add(Calendar.HOUR, hour);
		return cal.getTime();
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
	 * 获取当前n分钟前的时间
	 * 
	 * @param min
	 * @return
	 */
	public static Date getNowBeforeByMinute(int min) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.add(Calendar.MINUTE, 0 - min);
		return cal.getTime();
	}

	/**
	 * 获取当前n分钟前的时间不包含秒
	 * 
	 * @param min
	 * @return
	 */
	public static Date getNowBeforeByMinuteAbs(int min) {
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MILLISECOND, 0);
		cal.set(Calendar.SECOND, 0);
		cal.add(Calendar.MINUTE, 0 - min);
		return cal.getTime();
	}

	/**
	 * 获取当前n小时前的时间不包含分钟
	 * 
	 * @param hour
	 * @return
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
	 * 获取当前n天前的时间(开始时间)
	 * 
	 * @param day
	 * @return
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
	 * 获取当前n月前的时间
	 * 
	 * @param month
	 * @return
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
	 * @param start
	 * @Param end
	 * @return
	 */
	public static int getDays(Date start, Date end, int offset) {
		if (null == start || null == end) {
			return 1;
		}

		int day = new BigDecimal(end.getTime() - start.getTime()).divide(new BigDecimal(24 * 60 * 60 * 1000), 2)
				.intValue();

		return 0 == day ? 1 : (day + offset);
	}

	/**
	 * 获取一段时间的天数,最少返回1天
	 * 
	 * @param start
	 * @Param end
	 * @return
	 */
	public static int getDays(Date start, Date end) {
		return getDays(start, end, 0);
	}

	/**
	 * 格式化时间 yyyy-MM-dd HH:mm:ss
	 * 
	 * @param date
	 * @return
	 */
	public static String format2(Date date) {
		return formatDate(date, GMT_PTN);
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
	 * 格式化时间
	 *
	 * @param date
	 * @return
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
	 * @param date
	 * @return
	 */
	public static String formatTimeStamp(Date date) {
		return new SimpleDateFormat(TIMESTAMP).format(date);
	}

	/**
	 * 将单位时间转换为具体时间
	 * 
	 * @param val
	 * @param unit
	 * @return
	 */
	public static Date userTimeToDate(int val, String unit) {
		switch (unit) {
		// 年
		case "y":
			return TimeTools.getNowBeforeByMonth(12 * val);
		// 月
		case "M":
			return TimeTools.getNowBeforeByMonth(val);
		// 日
		case "d":
			return TimeTools.getNowBeforeByDay(val);
		// 时
		case "H":
			return TimeTools.getNowBeforeByHourAbs(val);
		// 分
		case "m":
			return TimeTools.getNowBeforeByMinuteAbs(val);
		// 默认 分
		default:
			return TimeTools.getNowBeforeByMinuteAbs(val);
		}
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
	 * 字符串转Date类型 yyyy-MM-dd HH:mm:ss
	 *
	 * @param time
	 * @return
	 */
	public static Date parseDate5(String time) {
		return TimeTools.parseDate(time, GMT_PTN);
	}


	public static Date parseDate(String time, String format) {
		try {
			return (new SimpleDateFormat(format)).parse(time);
		} catch (ParseException e) {

		}
		return null;
	}

	/**
	 * 字符串转时间类型 转换如下格式： 2016-02-15 03:35:00:9650
	 * 
	 * @param timeStr
	 * @throws ParseException
	 */
	public static Object parseDate3(String timeStr) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
		return df.parse(timeStr);
	}

	/**
	 * 字符串转时间类型 转换如下格式： 2016-02-15 03:35:00:9650
	 * 
	 * @param timeStr
	 * @throws ParseException
	 */
	public static Date parseDate4(String timeStr) throws ParseException {
		SimpleDateFormat df = new SimpleDateFormat(TIME_PATTERN);
		return df.parse(timeStr);
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
	 * 获取上月最后一天 2017-05-31 23:59:59
	 * 
	 * @return
	 */
	public static Date getLastDayOfBeforeMonth() {
		return TimeTools.getLastDayOfMonth(TimeTools.getNowBeforeByMonth(1));
	}

}
