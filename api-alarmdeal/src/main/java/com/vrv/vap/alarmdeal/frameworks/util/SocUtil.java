package com.vrv.vap.alarmdeal.frameworks.util;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.AnalysisSort;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;

public class SocUtil {
    private static Logger logger = LoggerFactory.getLogger(SocUtil.class);

    public static List<Map<String, Object>> getTimeFullMapForField(Date timeBegin, Date timeEnd, String field, long timeSpan, String timeFormat,
                                                                   List<Map<String, Object>> maps) {
        List<String> list = new ArrayList<>();

        for (Map<String, Object> map : maps) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey().equals(field)) {
                    list.add(entry.getValue().toString());
                }
            }
        }

        List<String> dayList = new ArrayList<>();
        for (Date date = timeBegin; date.before(timeEnd)
                || date.equals(timeEnd); date = DateUtil.addMillSeconds(date, timeSpan)) {
            String str = DateUtil.format(date, timeFormat);
            dayList.add(str);
        }

        dayList.removeAll(list);

        for (String str : dayList) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put(field, str);
            result.put("doc_count", 0);
            maps.add(result);
            AnalysisSort alarmSort = new AnalysisSort(field);
            Collections.sort(maps, alarmSort);
        }
        return maps;

    }

    public static List<Map<String, Object>> getTimeFullMapForField2(Date timeBegin, Date timeEnd, String field, Integer timeSpan, String timeFormat,
                                                                    List<Map<String, Object>> maps) {
        List<String> list = new ArrayList<>();

        for (Map<String, Object> map : maps) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey().equals(field)) {
                    list.add(entry.getValue().toString());
                }
            }
        }

        List<String> dayList = new ArrayList<>();
        for (Date date = timeBegin; date.before(timeEnd)
                || date.equals(timeEnd); date = DateUtil.addHours(date, timeSpan)) {
            String str = DateUtil.format(date, timeFormat);
            dayList.add(str);
        }

        dayList.removeAll(list);

        for (String str : dayList) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put(field, str);
            result.put("doc_count", 0);
            maps.add(result);
            AnalysisSort alarmSort = new AnalysisSort(field);
            Collections.sort(maps, alarmSort);
        }
        return maps;

    }

    public static List<Map<String, Object>> getTimeFullMap(Date timeBegin, Date timeEnd, long timeSpan, String timeFormat,
                                                           List<Map<String, Object>> maps) {
        List<String> list = new ArrayList<>();

        for (Map<String, Object> map : maps) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                if (entry.getKey().equals("triggerTime")) {
                    list.add(entry.getValue().toString());
                }
            }
        }

        List<String> dayList = new ArrayList<>();
        for (Date date = timeBegin; date.before(timeEnd)
                || date.equals(timeEnd); date = DateUtil.addMillSeconds(date, timeSpan)) {
            String str = DateUtil.format(date, timeFormat);
            dayList.add(str);
        }

        dayList.removeAll(list);

        for (String str : dayList) {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("triggerTime", str);
            result.put("doc_count", 0);
            maps.add(result);
            AnalysisSort alarmSort = new AnalysisSort("triggerTime");
            Collections.sort(maps, alarmSort);
        }
        return maps;

    }

    /**
     * 告警处置补全处理
     *
     * @param list
     * @param timeList
     * @param extrasInfo
     * @param flag
     */
    public static void dealAlarmdealInfo(List<Map<String, Object>> list, List<String> timeList, String extrasInfo, String flag) {
        List<String> times = new ArrayList<>();
        times.addAll(timeList);
        List<String> strList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                if (key.equals("triggerTime")) {
                    String name = entry.getValue().toString();
                    strList.add(name);
                }
            }
        }
        // 补齐对应的数据
        List<String> repeatElemets = new ArrayList<String>();
        for (String existDay : strList) {
            for (String day : times) {
                if (day.equals(existDay)) {
                    repeatElemets.add(existDay);
                }
            }
        }
        times.removeAll(repeatElemets);
        for (String day : times) {
            Map<String, Object> map = new HashMap<>();
            map.put("triggerTime", day);
            map.put("doc_count", 0);
            //map.put("statusEnum", 0);
            if (extrasInfo != null) {
                map.put(extrasInfo, 0);
            }
            list.add(map);
        }
        times = timeList;
        //排序
        AnalysisSort alarmSort = new AnalysisSort(flag);
        Collections.sort(list, alarmSort);
    }


    /**
     * 告警信息补全处理
     *
     * @param list
     * @param timeList
     * @param extrasInfo
     * @param flag
     */
    public static void dealAnalysisResult(List<Map<String, Object>> list, List<String> timeList, String extrasInfo, String flag) {
        List<String> strList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                if (key.equals("triggerTime")) {
                    String name = entry.getValue().toString();
                    strList.add(name);
                }
            }
        }
        // 补齐对应的数据
        List<String> repeatElemets = new ArrayList<String>();
        for (String existDay : strList) {
            for (String day : timeList) {
                if (day.equals(existDay)) {
                    repeatElemets.add(existDay);
                }
            }
        }
        timeList.removeAll(repeatElemets);
        for (String day : timeList) {
            Map<String, Object> map = new HashMap<>();
            map.put("triggerTime", day);
            map.put("doc_count", 0);
            map.put("statusEnum", 0);
            if (extrasInfo != null) {
                map.put(extrasInfo, 0);
            }
            list.add(map);
        }
        //排序
        AnalysisSort alarmSort = new AnalysisSort(flag);
        Collections.sort(list, alarmSort);
    }


    /**
     * 补全工具
     *
     * @param list
     * @param timeList
     * @param time_field
     * @param static_field
     */
    public static void completionUtil(List<Map<String, Object>> list, List<String> timeList, String time_field, String static_field) {
        List<String> strList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey().toString();
                if (key.equals(time_field)) {
                    String value = entry.getValue().toString();
                    strList.add(value);
                }
            }
        }
        // 补齐对应的数据
        List<String> repeatElemets = new ArrayList<String>();
        for (String existDay : strList) {
            for (String day : timeList) {
                if (day.equals(existDay)) {
                    repeatElemets.add(existDay);
                }
            }
        }
        timeList.removeAll(repeatElemets);
        for (String day : timeList) {
            Map<String, Object> map = new HashMap<>();
            map.put(time_field, day);
            map.put(static_field, 0);
            list.add(map);
        }
        //排序
        AnalysisSort alarmSort = new AnalysisSort(time_field);
        Collections.sort(list, alarmSort);
    }


    public static Map<String, Object> getFeedBackTime(long beginTime) {
        long endTime = System.currentTimeMillis();
        long consume = endTime - beginTime;
        float consumeTime = (float) consume / 1000;
        Map<String, Object> consumeMap = new HashMap<String, Object>();
        consumeMap.put("name", "consume");
        consumeMap.put("value", consumeTime);
        return consumeMap;
    }


    /**
     * 转换获得的结果
     *
     * @param list
     */
    public static List<Map<String, Object>> transferAnalysisResult(List<Map<String, Object>> list) {
        List<Map<String, Object>> mapLists = new ArrayList<>();
        //1.时间去重处理
        Set<String> set = new HashSet<>();
        for (Map<String, Object> map : list) {
            String triggerTime = map.get("triggerTime").toString();
            set.add(triggerTime);
        }
        //2.根据时间进行组装
        for (String time : set) {
            Map<String, Object> map = new HashMap<>();
            List<Map<String, Object>> mapList = new ArrayList<>();
            map.put("triggerTime", time);
            for (Map<String, Object> parentMap : list) {
                String triggerTime = parentMap.get("triggerTime").toString();
                if (triggerTime.equals(time)) {
                    Map<String, Object> childrenMap = new HashMap<>();
                    Object statusEnum = parentMap.get("statusEnum");
                    if (statusEnum == null) {
                        statusEnum = "0";
                    }
                    childrenMap.put("statusEnum", statusEnum);
                    childrenMap.put("doc_count", parentMap.get("doc_count"));
                    mapList.add(childrenMap);
                }
            }
            map.put("statusEnum", mapList);
            mapLists.add(map);
        }
        return mapLists;
    }


    public static void dealAlarmLevelInfo(List<Map<String, Object>> list) {
        List<String> test = new ArrayList<>();
        for (Map<String, Object> map2 : list) {
            String statusEnum = map2.get("statusEnum").toString();
            test.add(statusEnum);
        }
        List<String> asList = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            asList.add(String.valueOf(i));
        }
        List<String> repeatElemets = new ArrayList<String>();
        for (String string : test) {
            for (String arrays : asList) {
                if (string.equals(arrays)) {
                    repeatElemets.add(string);
                }
            }
        }
        asList.removeAll(repeatElemets);

        for (String str : asList) {
            Map<String, Object> maps = new HashMap<>();
            maps.put("statusEnum", Integer.valueOf(str));
            maps.put("doc_count", 0);
            list.add(maps);
        }
        AnalysisSort alarmSort = new AnalysisSort("statusEnum");
        Collections.sort(list, alarmSort);
    }


    /**
     * 对告警信息进行处理
     *
     * @param list
     */
    public static void dealAlarmInfo(List<Map<String, Object>> list, String keyword) {
        for (Map<String, Object> map : list) {
            List<String> test = new ArrayList<>();
            List<Map<String, Object>> status_list = (List<Map<String, Object>>) map.get(keyword);
            for (Map<String, Object> map2 : status_list) {
                String statusEnum = map2.get(keyword).toString();
                test.add(statusEnum);
            }
            List<String> asList = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                asList.add(String.valueOf(i));
            }
            List<String> repeatElemets = new ArrayList<String>();
            for (String string : test) {
                for (String arrays : asList) {
                    if (string.equals(arrays)) {
                        repeatElemets.add(string);
                    }
                }
            }
            asList.removeAll(repeatElemets);

            List<Map<String, Object>> mapList = (List<Map<String, Object>>) map.get(keyword);
            for (String str : asList) {
                Map<String, Object> maps = new HashMap<>();
                maps.put(keyword, Integer.valueOf(str));
                maps.put("doc_count", 0);
                mapList.add(maps);
            }
            AnalysisSort alarmSort = new AnalysisSort(keyword);
            Collections.sort(mapList, alarmSort);
        }

    }

    /**
     * 返回Boolean值的结果
     *
     * @param result
     * @return
     */
    public static Result<Boolean> getBooleanResult(Boolean result) {
        Result<Boolean> booelean_result = new Result<>();
        if (result) {
            booelean_result.setCode(ResultCodeEnum.SUCCESS.getCode());
            booelean_result.setData(true);
            booelean_result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
        } else {
            booelean_result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode());
            booelean_result.setData(false);
            booelean_result.setMsg(ResultCodeEnum.UNKNOW_FAILED.getMsg());
        }

        return booelean_result;
    }


    private static void tree(File f, int level) {       //递归

        String preStr = "";
        for (int i = 0; i < level; i++) {
            preStr += "    ";
        }

        File[] childs = f.listFiles();
        //System.out.println(childs[1].getName());
        for (int i = 0; i < childs.length; i++) {
            System.out.println(preStr + childs[i].getName());
            if (childs[i].isDirectory()) {
                tree(childs[i], level + 1);
            }
        }
    }


    public static String format(Date date) {
        SimpleDateFormat formatTool = new SimpleDateFormat();
        //formatTool.applyPattern("yyyy-MM-dd HH:mm:ss");
        //"2019-08-31T11:06:47.382Z"
        date = DateUtils.addHours(date, -8);
        formatTool.applyPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return formatTool.format(date);
    }


    public static boolean judgeJobNameIsExist(String job_name, List<String> list) {
        List<Integer> count = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.startsWith("---")) {
                count.add(i); //在---虚线之间的为对应的job的位置
            }
        }
        if (count.size() == 2) {
            list = list.subList(count.get(0), count.get(1));//在---虚线之间的为对应的job的内容
            for (String job_content : list) {
                if (job_content.contains(job_name)) { //job_content当中是否包含对应的job_name
                    return true;
                }
            }
        }
        return false;
    }


    /**
     * 获得正在启动的Jobs
     *
     * @param list
     * @return
     */
    public static List<String> getRunningJobs(List<String> list) {

        List<Integer> count = new ArrayList<>();
        String job_Id = null;
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.startsWith("---")) {
                count.add(i); //在---虚线之间的为对应的job的位置
            }
        }
        if (count.size() == 2) {
            list = list.subList(count.get(0), count.get(1));//在---虚线之间的为对应的job的内容
        }
        return list;
    }

    /**
     * 通过job_name获得对应的job
     * 这个地方存在问题是 a 可以停止a_1 a_2
     *
     * @param job_name
     * @param list
     */
    public static String getJobId(String job_name, List<String> list) {
        //可能有相同的重复的任务，通过获取所有的，保证不会出现重复的。
        List<Integer> count = new ArrayList<>();
        String job_Id = null;
        for (int i = 0; i < list.size(); i++) {
            String str = list.get(i);
            if (str.startsWith("---")) {
                count.add(i); //在---虚线之间的为对应的job的位置
            }
        }
        if (count.size() == 2) {
            list = list.subList(count.get(0), count.get(1));//在---虚线之间的为对应的job的内容
            for (String job_content : list) {
                String[] split = job_content.split(" : ");
                if (split.length == 3) {
                    String jobName = split[2];
                    try {
                        jobName = jobName.substring(0, jobName.indexOf("("));
                        jobName=jobName.trim();
                    } catch (Exception e) {
                       logger.error("flink job list中解析得到的jobName={}",jobName);
                    }
                    if(job_name.equals(jobName)){
                        job_Id = split[1];
                        break;
                    }
                }
            }
        }
        return job_Id;
    }

    /**
     * 获取flink任务列表
     */
    public static List<String> getJobIdList(String jobName, List<String> jobList) {
        //根据flink list获取的flink任务列表获取
        List<String> jobNameList = getRunningJobs(jobList);
        List<String> jobIdList = new ArrayList<>();
        Set<String> flinkJobSet = new HashSet<>();
        for (String name : jobNameList) {
            if (name.contains(jobName)) {
                String[] split = name.split(" : ");
                if (split.length == 3) {
                    String flinkJobName = split[2];
                    flinkJobSet.add(flinkJobName);
                }
            }
        }
        for (String flinkJobName : flinkJobSet) {
            for (String name : jobNameList) {
                if (name.contains(flinkJobName)) {
                    String[] split = name.split(" : ");
                    if (split.length == 3) {
                        String jobId = split[1];
                        jobIdList.add(jobId);
                    }
                    break;
                }
            }
        }
        for (String jobId : jobIdList) {
            logger.info("###############getJobIdList jobId={}", jobId);
        }
        return jobIdList;
    }


}
