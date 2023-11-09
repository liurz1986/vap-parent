package com.vrv.vap.alarmdeal.business.analysis.server.objectresource.impl;

import com.vrv.vap.alarmdeal.business.analysis.enums.ObjectResourceConst;
import com.vrv.vap.alarmdeal.business.analysis.server.objectresource.TimeResourceRef;
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

    private static final String CUTTIME_PATTERN="[0-9,*]{1,4}";

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
                //将具体日期转换为当月的第几周，周几
                List<String> weekList = dayToWeek(timeStr);
                timeList.remove(2);
                timeList.addAll(2, weekList);
            }
            int size = timeList.size();
            startList.remove(0);
            int j=0;
            /**
             * 思路：‘*’项代表的是某个时间段的一个动态循环，
             * 只需比较结合需要比较的时刻，
             * 比较具体的时间段即可。
             */
            for (int i = 0; i < endList.size(); i++) {
                //以“*”为分隔符，切割list，分段比较
                if(endList.get(i).equals("*")|| (i==endList.size()-1)) {
                    int k=i;
                    //本次分隔到了最后，但未查到"*"
                    if(!endList.get(i).equals("*")){
                        k++;
                    }
                    //截取该子分段的值，并拼接比较
                    List<String> timeEndRegionList=endList.subList(j,k);
                    List<String> timeStartRegionList=startList.subList(j,k);
                    List<String> currentTimeList=timeList.subList(j,k);
                    String timeEndRegionStr=transferTimeStr(timeEndRegionList);
                    String timeStartRegionStr=transferTimeStr(timeStartRegionList);
                    String currentTimeStr=transferTimeStr(currentTimeList);
                    if(currentTimeStr.compareTo(timeStartRegionStr)<0||currentTimeStr.compareTo(timeEndRegionStr)>0){
                        bool = !bool;
                        break;
                    }
                    //下一次分隔比较的起点index
                    j=i+1;
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
                result+=str.length()==1?"0"+str:str;
            }

        }
        return result;
    }

    /**
     * 截取时间当中的数字和“*”
     * @return
     */
    public List<String> cutTime(String time){
        List<String> timeList=new ArrayList<>();
        Matcher matcher = Pattern.compile(CUTTIME_PATTERN).matcher(time);
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
