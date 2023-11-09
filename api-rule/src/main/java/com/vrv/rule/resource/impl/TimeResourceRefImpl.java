package com.vrv.rule.resource.impl;


import com.vrv.rule.resource.ObjectResourceConst;
import com.vrv.rule.resource.TimeResourceRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class TimeResourceRefImpl implements TimeResourceRef {


    private Logger logger = LoggerFactory.getLogger(TimeResourceRefImpl.class);
    
    String[] content;


    public void setContent(String[] content){
        this.content=content;
    }

    /**
     *时间资源匹配
     * @param fieldValue
     * @param true--代表等于，false--代表不等于
     */
    @Override
    public  boolean computer(Object fieldValue,Boolean opt){
        boolean bool=true;
        Date date=(Date)fieldValue;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr=simpleDateFormat.format(date);
        List<String> timeList=new ArrayList<>();
        for(String time:content) {
            bool = true;
            timeList = cutTime(timeStr);
            String[] timeArray = time.split("~");
            List<String> startList = cutTime(timeArray[0]);
            List<String> endList = cutTime(timeArray[1]);
            int type=Integer.parseInt(startList.get(0));
            if (type==ObjectResourceConst.type_week) {
                List<String> weekList = dayToWeek(timeStr);
                timeList.remove(2);
                timeList.addAll(2, weekList);
            }
            int size = timeList.size();
            startList.remove(0);
            int j=0;
            for (int i = 0; i < endList.size(); i++) {
                if(endList.get(i).equals("*")|| (i==endList.size()-1)) {
                    if(j+1<i){
                        int k=i;
                        if(!endList.get(i).equals("*")){
                            k++;
                        }
                        List<String> timeEndRegionList=endList.subList(j+1,k);
                        List<String> timeStartRegionList=startList.subList(j+1,k);
                        List<String> currentTimeList=timeList.subList(j+1,k);
                        String timeEndRegionStr=transferTimeStr(timeEndRegionList);
                        String timeStartRegionStr=transferTimeStr(timeStartRegionList);
                        String currentTimeStr=transferTimeStr(currentTimeList);
                        if(currentTimeStr.compareTo(timeStartRegionStr)<0||currentTimeStr.compareTo(timeEndRegionStr)>0){
                            bool = !bool;
                            break;
                        }
                    }
                    j=i;
                }
            }
            if(bool){
                break;
            }
        }
        if(!opt){
            bool=!bool;
        }
        return  bool;
    }

    private String transferTimeStr(List<String> list) {
        String result="";
        for(String str :list){
            String pattern = "[0-9]+";
            if (str.matches(pattern)) {
                result+=str.length()==2?str:"0"+str;
            }

        }
        return result;
    }

    /**
     * 截取时间当中的数字
     * @return
     */
    public List<String> cutTime(String time){
        List<String> timeList=new ArrayList<>();
        Pattern pattern = Pattern.compile("[0-9,*]{1,4}");
        Matcher matcher = pattern.matcher(time);
        while(matcher.find()){
            timeList.add(matcher.group(0));
        }
        return timeList;
    }




    /**
     * 某一天转换当月第几周，周几
     */
    public List<String> dayToWeek(String dateStr){
        try {
            List<String> arrayList=new ArrayList<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date simDate = format.parse(dateStr);
            Calendar cal = Calendar.getInstance();
            cal.setTime(simDate);
            int weekDay = cal.get(Calendar.DAY_OF_WEEK);
            cal.setFirstDayOfWeek(Calendar.MONDAY);
            boolean isFirstSunday = (cal.getFirstDayOfWeek() == Calendar.MONDAY);
            if(isFirstSunday) {
                weekDay = weekDay - 1;
                if (weekDay == 0) {
                    weekDay = 7;
                }
            }
            int week = cal.get(Calendar.WEEK_OF_MONTH);
            arrayList.add(String.valueOf(week));
            arrayList.add(String.valueOf(weekDay));
            return  arrayList;
        } catch (ParseException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


}
