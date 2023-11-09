package com.vrv.vap.line.tools;

import com.vrv.vap.toolkit.tools.TimeTools;
import org.quartz.CronExpression;

import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

public class CronTools {

    public static int getPeriodByCron(String cronExpression){
        int result = 1;
        try{
            CronExpression cron = new CronExpression(cronExpression);
            Date time1 = cron.getNextValidTimeAfter(new Date(System.currentTimeMillis()));
            Date time2 = cron.getNextValidTimeAfter(time1);
            int cycle = calculateDaysGap(time1,time2);
            if(cycle > 1 && cycle < 8){
                result = 7;
            }else if(cycle >= 8 && cycle <= 31){
                result = 30;
            }else if(cycle > 31){
                result = 365;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }


    public static int calculateDaysGap(Date start, Date end) {
        Calendar s = Calendar.getInstance();
        s.setTime(start);
        s.set(Calendar.HOUR,0);
        s.set(Calendar.MINUTE,0);
        s.set(Calendar.SECOND,0);
        s.set(Calendar.MILLISECOND,0);
        start = s.getTime();

        Calendar e = Calendar.getInstance();
        e.setTime(end);
        e.set(Calendar.HOUR,0);
        e.set(Calendar.MINUTE,0);
        e.set(Calendar.SECOND,0);
        e.set(Calendar.MILLISECOND,0);
        end = e.getTime();

        final long ONE_DAY_MILLIS = 1000L * 60 * 60 * 24;
    // 此处要注意，去掉时分秒的差值影响，此处采用先换算为天再相减的方式
        long gapDays = Math.abs(end.getTime()/ONE_DAY_MILLIS - start.getTime()/ONE_DAY_MILLIS);
        return Long.valueOf(gapDays).intValue();
    }
    public static void main(String[] args) {
        //System.out.println(getPeriodByCron("0 10 0 * ? 10"));
    }
}
