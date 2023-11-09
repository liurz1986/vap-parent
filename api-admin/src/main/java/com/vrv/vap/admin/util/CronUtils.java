package com.vrv.vap.admin.util;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.TriggerBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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

    public static String getLastTriggerTime(String cron,int n){
        if(!CronExpression.isValidExpression(cron)){
            return null;
        }
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity("Caclulate Date").withSchedule(CronScheduleBuilder.cronSchedule(cron)).build();
        Date time0 = trigger.getStartTime();
        Date time1 = trigger.getFireTimeAfter(time0);
        Date time2 = trigger.getFireTimeAfter(time1);
        Date time3 = trigger.getFireTimeAfter(time2);
        long l = time1.getTime() -(time3.getTime() -time2.getTime())*n;
		Date date = new Date(l);
		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    public static void main(String[] args) throws ParseException {
        String cron = "19 10 14 * * ?";
//        System.out.println(TimeTools.format2(lastExecution(cron)));
        System.out.println(getLastTriggerTime(cron,2));
    }
}
