package com.vrv.vap.xc.tools;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.model.PageModel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateTools {

    public static Date genDateDaysBefore(int days){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-days);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date genDateDaysBefore(int days,Date date){
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH,-days);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date genDateEndDaysBefore(int days){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-days);
        c.set(Calendar.HOUR_OF_DAY, 23);
        c.set(Calendar.MINUTE, 59);
        c.set(Calendar.SECOND, 59);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date genDateMonthBefore(int months){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH,-months);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static String createIndex(String pre){
        String[] indexs = pre.split(",");
        StringBuffer sb = new StringBuffer();
        for(int i = 0 ;i < indexs.length; i++){
            if(i == 0){
                sb.append(indexs[i]).append("-2021.08.17");
            }else{
                sb.append(",").append(indexs[i]).append("-2021.08.17");
            }
        }
        return sb.toString();
    }

    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }

    public static int dealStartEndTime(PageModel record){
        int days = TimeTools.getDays(record.getMyStartTime(), record.getMyEndTime());
        record.setMyStartTime(genDateDaysBefore(days,record.getMyStartTime()));
        return days;
    }

    public static String genDateStringBefore(int days){
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH,-days);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return TimeTools.format(c.getTime(),TimeTools.DATE_FMT);
    }

    public static Date getDateAfterByDay(Date date , int day) {
        Calendar cal = Calendar.getInstance();
        if(date != null){
            cal.setTime(date);
        }
        cal.add(Calendar.DATE, day);
        return cal.getTime();
    }

    public static String minseconds2Date(Long minseconds,String format) {
        if(minseconds == null ){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        Date date = new Date();
        date.setTime(minseconds);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
    public static String formatTime(Long ms) {
        int ss = 1000;
        int mi = ss * 60;
        Integer hh = mi * 60;
        Integer dd = hh * 24;

        Long day = ms / dd;
        Long hour = (ms - day * dd) / hh;
        long minute = (ms - day * dd - hour * hh) / mi;
        long second = (ms - day * dd - hour * hh - minute * mi) / ss;
        long milliSecond = ms - day * dd - hour * hh - minute * mi - second * ss;

        StringBuilder sb = new StringBuilder();
        if(day > 0) {
            sb.append(day).append("天");
        }
        if(hour > 0) {
            sb.append(hour).append("小时");
        }
        if(minute > 0) {
            sb.append(minute).append("分");
        }
        if(second > 0) {
            sb.append(second).append("秒");
        }
        if(milliSecond > 0) {
            sb.append(milliSecond).append("毫秒");
        }
        return sb.toString();
    }
    public static void main(String[] args) {

    }
}
