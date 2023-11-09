package com.vrv.vap.xc.tools;

import org.quartz.CronExpression;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

/**
 *cron表达式工具类
 *
 */
public class CronUtils
{
    /**
     * 返回一个布尔值代表一个给定的Cron表达式的有效性
     *
     * @param cronExpression Cron表达式
     * @return boolean 表达式是否有效
     */
    public static boolean isValid(String cronExpression)
    {
        return CronExpression.isValidExpression(cronExpression);
    }

    /**
     * 返回一个字符串值,表示该消息无效Cron表达式给出有效性
     *
     * @param cronExpression Cron表达式
     * @return String 无效时返回表达式错误描述,如果有效返回null
     */
    public static String getInvalidMessage(String cronExpression)
    {
        try
        {
            new CronExpression(cronExpression);
            return null;
        }
        catch (ParseException pe)
        {
            return pe.getMessage();
        }
    }

    /**
     * 返回下一个执行时间根据给定的Cron表达式
     *
     * @param cronExpression Cron表达式
     * @return Date 下次Cron表达式执行时间
     */
    public static Date getNextExecution(String cronExpression)
    {
        try
        {
            CronExpression cron = new CronExpression(cronExpression);
            return cron.getNextValidTimeAfter(new Date(System.currentTimeMillis()));
        }
        catch (ParseException e)
        {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

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
}
