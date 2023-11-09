package com.vrv.vap.line.tools;

import com.vrv.vap.toolkit.tools.TimeTools;

import java.util.Calendar;
import java.util.Date;

public class MyTimeTools {

    public static Date addSecond(Date date,int second){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND,second);
        return cal.getTime();
    }

    public static Date addMini(Date date,int mins){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE,mins);
        return cal.getTime();
    }

    public static Date getDateBeforeByDay(Date date , int day) {
        Calendar cal = Calendar.getInstance();
        if(date != null){
            cal.setTime(date);
        }
        cal.add(Calendar.DATE, -day);
        return cal.getTime();
    }


    public static int getBeforeDays(Date before){
        int now = Integer.parseInt(TimeTools.format(new Date(),"yyyyMMdd"));
        int beforeDay = Integer.parseInt(TimeTools.format(before,"yyyyMMdd"));
        return now - beforeDay;
    }

    public static Date getNextSummaryEndTime(Date date,int min){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE,min);
        cal.add(Calendar.MILLISECOND,-1);
        return cal.getTime();
    }

    public static void main(String[] args) {
        Date d = new Date();



    }
}
