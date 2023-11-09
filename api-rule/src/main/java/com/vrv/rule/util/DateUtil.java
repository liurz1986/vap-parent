package com.vrv.rule.util;

import java.lang.reflect.Type;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.time.DateUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

/** * 
* @author wudi 
* E‐mail:wudi@vrvmail.com.cn 
* @version 创建时间：2018年12月20日 上午11:52:59 
* 类说明   时间处理类
*/
public class DateUtil {

	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";

	public static final String UTC_DATE_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS";

	public static final String yyyy_mm_dd = "yyyy-MM-dd";  //天，周

	public static final String yyyy_mm = "yyyy-MM";   //月

	public static final String yyyy_mm_dd_hh = "yyyy-MM-dd HH";  //小时

	public static final String UTC_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

	/**
	 * timeStamp转成Date类型
	 * @param time
	 * @return
	 */
	public static Date timeStampTransferDate(Timestamp time) {
		Date date = time;
		return date;
	}


	public static String utcToDefaultFormat(String utcDate){
		SimpleDateFormat utcFormat = new SimpleDateFormat(UTC_TIME);
		SimpleDateFormat sdf = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
		try {
			Date date = utcFormat.parse(utcDate);
			return sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 普通时间格式转换成UTC时间格式
	 * @param defaultDate
	 * @return
	 */
	public static String parseDefaultFormatToUTC(String defaultDate){
		SimpleDateFormat utcFormat = new SimpleDateFormat(DEFAULT_DATE_PATTERN);
		SimpleDateFormat sdf = new SimpleDateFormat(UTC_TIME);
		try {
			Date date = utcFormat.parse(defaultDate);
			return sdf.format(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	public static String format(Date date) {
		SimpleDateFormat formatTool = new SimpleDateFormat();
		formatTool.applyPattern(DEFAULT_DATE_PATTERN);
		return formatTool.format(date);
	}

	public static String format(Date date,String pattern) {
		SimpleDateFormat formatTool = new SimpleDateFormat();
		formatTool.applyPattern(pattern);
		return formatTool.format(date);
	}

	/**
	 * 获得对应的时间戳
	 * @param strDate
	 * @return
	 * @throws ParseException
	 */
	public static Timestamp formatTimeStamp(String strDate) throws ParseException {
		String formatyyyymmdd = format(new Date(), yyyy_mm_dd);
		formatyyyymmdd = formatyyyymmdd+" "+strDate;
		SimpleDateFormat sDateFormat=new SimpleDateFormat(DEFAULT_DATE_PATTERN);
		Date date = sDateFormat.parse(formatyyyymmdd);
		Timestamp ts = new Timestamp(date.getTime());
		return ts;
	}

	/**
	 * 把时间戳转换成为字符串日期格式
	 * @param timeStamp
	 * @return
	 */
	public static String timeStampToDateString(Long timeStamp){
		SimpleDateFormat sdf = new SimpleDateFormat(DateUtil.DEFAULT_DATE_PATTERN);
		Date date = new Date(timeStamp);
		String formatted = sdf.format(date);
		return formatted;
	}



	public static void main(String[] args) throws ParseException {
		String result = utcToDefaultFormat("2017-11-27T03:16:03.944Z");
		boolean dateFormatValid = isDateFormatValid(result, DEFAULT_DATE_PATTERN);
		System.out.println(dateFormatValid);
//		GsonBuilder gsonBuilder = new GsonBuilder();
//		gsonBuilder.setDateFormat("yyyy-MM-dd hh:mm:ss");
//		gsonBuilder.registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter());
//		Gson GSON = gsonBuilder.create();
//		String json = GSON.toJson(new Timestamp((new Date()).getTime()));
//		System.out.println(json);
		//Z代表UTC统一时间:2017-11-27T03:16:03.944Z
//		String time = "2019-09-17T11:54:19.000+0800";
//		Date parseDate2 = DateUtil.parseDate(time.split("\\+")[0], UTC_DATE_PATTERN);
//
//		//Date parseDate = DateUtils.parseDate(time.split("\\.")[0], "yyyy-MM-dd'T'HH:mm:ss");
//		System.out.println(parseDate2);

		String format = DateUtil.format(DateUtil.addHours(new Date(), -1), DateUtil.yyyy_mm_dd_hh);


		int currentMonth = DateUtil.getCurrentMonth();
		int currentMonthDays = DateUtil.getCurrentMonthDays(currentMonth);
		Date date = DateUtils.addDays(new Date(), -currentMonthDays);
		System.out.println(date);


	}


	/**
	 * 获得当前月份
	 * @return
	 */
	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获得当前月份天数
	 * @return
	 */
	public static int getCurrentMonthDays(int month){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.MONTH, month-1);
		return cal.getActualMaximum(Calendar.DAY_OF_MONTH);
	}


	/**
	 * 解析时间字段过长的问题
	 * @return
	 */
	public static Gson parseGsonTime(){
		Gson gson = new GsonBuilder().registerTypeAdapter(Date.class, new JsonDeserializer<Date>() {
			@Override
			public Date deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
					throws JsonParseException {
				return new Date(json.getAsJsonPrimitive().getAsLong());
			}
		}).registerTypeAdapter(Timestamp.class,new TimestampTypeAdapter()).setDateFormat("yyyy-MM-dd HH:mm:ss").create();
		return gson;
	}

	/**
	 * 字符串转时间戳
	 * @param time
	 * @param pattern
	 * @return
	 */
	public static Long getTimestamp(String time,String pattern) {
		Long timestamp = null;
		try {
			timestamp = new SimpleDateFormat(pattern).parse(time).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return timestamp;
	}

	public static Date parseDate(String str, String patten) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat(patten);
		Calendar cd = Calendar.getInstance();
		cd.setTime(sdf.parse(str));
		return cd.getTime();
	}


	/**
	 * 时间增加小时
	 *
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addHours(Date date, int n) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		cd.add(Calendar.HOUR, n);// 增加小时
		return cd.getTime();
	}

	/**
	 * 增加天
	 *
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addDay(Date date, int n) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		cd.add(Calendar.DATE, n);// 增加天数
		return cd.getTime();
	}


	/**
	 * 时间增加月
	 *
	 * @param date
	 * @param n
	 * @return
	 */
	public static Date addMouth(Date date, int n) {
		Calendar cd = Calendar.getInstance();
		cd.setTime(date);
		cd.add(Calendar.MONTH, n);// 增加小时
		return cd.getTime();
	}


	public static boolean isDateFormatValid(String dateString, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);
		sdf.setLenient(false); // 设置为严格模式，不接受不合法的日期时间格式
		try {
			Date date = sdf.parse(dateString);
			// 如果成功解析，则表示日期时间格式有效
			return true;
		} catch (ParseException e) {
			// 解析失败，日期时间格式无效
			return false;
		}
	}




}
