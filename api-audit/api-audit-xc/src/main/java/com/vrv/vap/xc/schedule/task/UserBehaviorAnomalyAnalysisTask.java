package com.vrv.vap.xc.schedule.task;

import cn.hutool.core.collection.IterUtil;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.custom.DataCleanMapper;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.service.CommonService;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 用户行为异常分析及基线分析
 * Created by lizj on 2021/8/5
 */
public class UserBehaviorAnomalyAnalysisTask extends BaseTask {

    private CommonService commonService = VapXcApplication.getApplicationContext().getBean(CommonService.class);

    private DataCleanMapper dataCleanDao = VapXcApplication.getApplicationContext().getBean(DataCleanMapper.class);

    private final String indexSufFormat = "-yyyy";
    private int interval = 30;
    /**
     * 倍数(正负均方差倍数范围)
     */
    private int multiple = 2;

    private ThreadLocal<Set<String>> strategys = new ThreadLocal<>();

    private void initSetting(){
        try {
            interval = Integer.parseInt(dataCleanDao.getConfById("base_line_user_interval"));
            multiple = Integer.parseInt(dataCleanDao.getConfById("base_line_user_multiple"));
        } catch (NumberFormatException e) {
        }
    }

    public void fillStrategy(String strategy) {
        if (strategys.get() == null) {
            strategys.set(new HashSet<>());
        }
        strategys.get().add(strategy);
    }

    @Override
    public void run(String jobName) {
        initSetting();
        // 按各指标逐个分析

        // -----------------------------------------------------
        // 用户访问其他用户终端开启的服务，如文件共享、FTP （待告警实现，参考其逻辑，该指标无须计算基线）
        // todo..
        // -----------------------------------------------------

        // -----------------------------------------------------
        // 用户访问非授权应用（待告警实现，参考其逻辑，该指标无须计算基线）
        // todo..
        // -----------------------------------------------------

        // -----------------------------------------------------
        // 用户异常打印/刻录文件 - 用户使用打印设备清单
        printList();
        // -----------------------------------------------------

        // -----------------------------------------------------
        // 用户异常打印/刻录文件 - 用户使用刻录设备清单
        // 计算前一天个体用户刻录设备清单，并入库 （用户、刻录设备、刻录次数）
        // 计算前一天总体用户刻录设备清单，并入库 （刻录设备、刻录次数）
        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述）
        // 计算最新基线，并入库
        burnList();
        // -----------------------------------------------------

        // -----------------------------------------------------
        // 用户异常打印/刻录文件 - 用户打印/刻录文件类型清单
        // 计算前一天个体用户打印/刻录文件类型清单，并入库 （用户、打印/刻录文件类型、次数）
        // 计算前一天总体用户打印/刻录文件类型清单，并入库 （打印/刻录文件类型、次数）
        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述）
        // 计算最新基线，并入库
        printFileTypeList("0");
        printFileTypeList("1");
        // -----------------------------------------------------

        // -----------------------------------------------------
        // 用户异常打印/刻录文件 - 用户打印/刻录文件大小
        // 计算前一天个体用户打印/刻录文件大小，并入库 （用户、打印/刻录文件大小最小值、打印/刻录文件大小最大值）
        // 计算前一天总体用户打印/刻录文件大小，并入库 （打印/刻录文件大小最小值、打印/刻录文件大小最大值）
        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述）
        // 计算最新基线，并入库
        // -----------------------------------------------------
        printFileSize("0");
        printFileSize("1");

        // -----------------------------------------------------
        // 用户异常打印/刻录文件 - 用户打印/刻录文件频次
        // 计算前一天个体用户打印/刻录文件频次，并入库 （用户、打印/刻录文件次数）
        // 计算前一天总体用户打印/刻录文件频次，并入库 （打印/刻录文件次数）
        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述）
        // 计算最新基线，并入库
        // -----------------------------------------------------
        printFileFreList("0");
        printFileFreList("1");

        // -----------------------------------------------------
        // 用户异常打印/刻录文件 - 用户打印/刻录文件时段次数
        // 计算前一天个体用户打印/刻录文件时段次数，并入库 （用户、时段、打印/刻录文件次数最小值、打印/刻录文件次数均值、打印/刻录文件次数最大值）
        // 计算前一天总体用户打印/刻录文件频次，并入库 （时段、打印/刻录文件次数最小值、打印/刻录文件次数均值、打印/刻录文件次数最大值）
        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述）
        // 计算最新基线，并入库
        // -----------------------------------------------------
        //username(type=0时为空值),count(打印/刻录总次数),count_avg(平均次数), count_min(最小次数),file_size_max(最大次数),time_bucket(时段, 整型从0到23, 0代表0点到1点时段),start_time,end_time,
        // interval,type(0=群体基线, 1=个体基线),op_type(0=打印/1=刻录),insert_time
        printFileTimeFre("0");
        printFileTimeFre("1");

        // -----------------------------------------------------
        // 处理文件业务范围
        // 计算前一天个体用户文件业务范围，并入库 （用户、文件业务）
        // 计算前一天总体用户文件业务范围，并入库 （文件业务）
        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述）
        // 计算最新基线，并入库
        // -----------------------------------------------------
        businessList();

        // -----------------------------------------------------
        // 用户下载应用系统数据数量
        // 计算前一天个体用户下载应用系统数据数量，并入库 （用户、下载应用系统数据数量最小值、下载应用系统数据数量最大值）
        // 计算前一天总体用户下载应用系统数据数量，并入库 （下载应用系统数据数量最小值、下载应用系统数据数量最大值）
        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述）
        // 计算最新基线，并入库
        // -----------------------------------------------------
        downloadList();

        writeRecommendStrategys();

    }

    /**
     * 写入推荐策略
     */
    protected void writeRecommendStrategys() {
        Set<String> recommendStrategys = strategys.get();
        if (recommendStrategys == null || recommendStrategys.isEmpty()) {
            return;
        }
        Date currentTime = new Date();
        List<Map<String, Object>> recommendStrategyList = new ArrayList<>();
        for (String recommendStrategy : recommendStrategys) {
            String[] strategy = recommendStrategy.split("->");
            String code = strategy[0];
            String content = strategy[1];
            Map<String, Object> item = new HashMap<>();
            item.put("code", code);
            item.put("type", "用户行为异常");
            item.put("content", content);
            item.put("recommend_time", currentTime);
            item.put("insert_time", currentTime);
            recommendStrategyList.add(item);
        }
        try {
            QueryTools.writeData(recommendStrategyList, "recommend-strategy", QueryTools.build());
            recommendStrategys.clear();
        } catch (Exception e) {
        }
    }

    private void printList() {

        String printAuditPrefix = "print-audit";
        String printAuditSummaryPrefix = "summary-print-audit";
        String baseLineIndexPrefix = "base-line-print-burn";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, printAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        setCondition(queryModel, "op_type", "0");

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        String deviceIpField = "dev_ip";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （用户、打印设备、打印次数、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriUserIdField, deviceIpField, 5000, 1000, countField,
                new String[]{"dev_name"});

        // 计算前一天总体用户打印设备清单，并入库 （打印设备、打印次数）
        //  合计上述结果
        Map<String, Map<String, Object>> all = personResultList.stream()
                .map(r -> {
                    Map<String, Object> allItem = new HashMap<>();
                    allItem.putAll(r);
                    allItem.put("type", 0);
                    allItem.remove(oriUserIdField);
                    return allItem;
                })
                .collect(Collectors.toMap(r -> String.valueOf(r.get(deviceIpField)), r -> r, (a, b) -> {
                    a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
                    return a;
                }));

        // 写入printAuditSummary
        //   "username", "dev_ip", "dev_name", "total_count", "data_time", "insert_time", "type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(userIdField, r.get(oriUserIdField));
            item.put("dev_ip", r.get("dev_ip"));
            item.put("dev_name", r.get("dev_name"));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("op_type", "0");
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        int size = personResultList.size();
        List<Map<String, Object>> allDevResultList = all.values().stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put("dev_ip", r.get("dev_ip"));
            item.put("dev_name", r.get("dev_name"));
            item.put(countField, Long.valueOf(String.valueOf(r.get(countField))) / size);
            item.put("data_time", dataTime);
            item.put("type", 0);
            item.put("op_type", "0");
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix + "-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "username", "dev_ip", "dev_name", "total_count", "start_time", "end_time", "interval", "insert_time", "type", "op_type"
        PageModel modelOld = new PageModel();
        modelOld.setMyStartTime(yesterday);
        modelOld.setMyEndTime(TimeTools.getNowBeforeByDay2(1));

        EsQueryModel queryModeOld = QueryTools.buildQueryModel(wrapper, modelOld, baseLineIndexPrefix, "insert_time", TimeTools.TIME_FMT_2, false);
        setCondition(queryModeOld, "op_type", "0");
        queryModeOld.setCount(9999);
        List<Map<String, Object>> abnormalList = new ArrayList<>();
        SearchResponse searchBaseResponse = wrapper.getSearchResponse(queryModeOld);
        if (searchBaseResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchBaseResponse.getHits(), "insert_time");
            // 基线中所有的用户(不在这个范围内的就当新用户去比, 新用户用群体基线去判断是否异常)
            List<String> userList = list.parallelStream().map(r -> r.get("username")).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")) + r.get("dev_ip"), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("dev_ip")), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));
                String ud = user + personRes.get("dev_ip");
                if (userList.contains(user) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!userList.contains(user) && allLineLast.containsKey(personRes.get("dev_ip"))) {
                    continue;
                }
                /*//异常类型
                Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "用户异常打印设备清单");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("u1->用户异常打印/刻录文件（综合设备、文件类型、时间、频次、大小）");
                break;
            }
        }

        /*String abnormalIndex = abnormalIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            QueryTools.writeData(abnormalList, abnormalIndex, wrapper);
        } catch (Exception e) {
        }*/

        // 计算最新基线，并入库
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "op_type", "0");
        setCondition(queryModeNew0, "type", 0);
        //      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg2(queryModeNew0, wrapper, deviceIpField, 5000, "data_time", new String[]{countField},
                new String[]{"total_count"}, deviceIpField, new String[]{"dev_name"});
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "op_type", "0");
        setCondition(queryModeNew, "type", 1);
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, userIdField, 5000, deviceIpField,
                new String[]{countField}, new String[]{"total_count"}, "username", "dev_name");

        //用户A+设备IP+设备名称+打印次数（个体基线）
        //* 设备IP+设备名称+打印次数（群体基线）
        // "username", "dev_ip", "dev_name", "total_count", "start_time", "end_time", "interval", "insert_time", "type", "op_type"
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put("dev_ip", r.get("dev_ip"));
            item.put("dev_name", r.get("dev_name"));
            item.put("total_count", r.get("total_count"));

            int days = (int) r.get("data_time" + "Count");
            Double avgCount = ((Double) item.get("total_count")) / days;
            item.put("avg_count", avgCount);

            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("op_type", "0");
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        // username,dev_ip,dev_name,total_count,start_time,end_time,interval,type,op_type,insert_time (用户username+设备清单dev_ip+打印次数total_count)
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put("username", r.get("username"));
            item.put("dev_ip", r.get("dev_ip"));
            item.put("dev_name", r.get("dev_name"));
            item.put("total_count", r.get("total_count"));
            int count = (int) r.get("dev_ip" + "Count");
            Double avgCount = ((Double) item.get("total_count")) / count;
            item.put("avg_count", avgCount);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("op_type", "0");
            item.put("insert_time", currentTime);

            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void burnList() {

        String printAuditPrefix = "print-audit";
        String printAuditSummaryPrefix = "summary-print-audit";
        String baseLineIndexPrefix = "base-line-print-burn";
        String abnormalIndexPrefix = "abnormal-data";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, printAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        setCondition(queryModel, "op_type", "1");

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        String deviceIpField = "ip";
        String countField = "count";
        // 计算前一天个体用户刻录设备清单，并入库 （用户、刻录设备、刻录次数、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriUserIdField, deviceIpField, 5000, 1000, countField,
                new String[]{"dev_name"});
        //personResultList

        // 计算前一天总体用户刻录设备清单，并入库 （刻录设备、刻录次数）
        //  合计上述结果
        Map<String, Map<String, Object>> all = personResultList.stream().collect(Collectors.toMap(r -> String.valueOf(r.get(deviceIpField)), r -> r, (a, b) -> {
            a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
            return a;
        }));

        // 写入printAuditSummary
        //   "username", "dev_ip", "dev_name", "total_count", "data_time", "insert_time", "type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(userIdField, r.get("std_user_no"));
            item.put("dev_ip", r.get("dev_ip"));
            item.put("dev_name", r.get("dev_name"));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("op_type", "1");
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        int size = personResultList.size();
        List<Map<String, Object>> allDevResultList = all.values().stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put("dev_ip", r.get("dev_ip"));
            item.put("dev_name", r.get("dev_name"));
            item.put(countField, Long.valueOf(String.valueOf(r.get(countField))) / size);
            item.put("data_time", dataTime);
            item.put("type", 0);
            item.put("op_type", "1");
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix + "-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "username", "dev_ip", "dev_name", "total_count", "start_time", "end_time", "interval", "insert_time", "type", "op_type"
        PageModel modelOld = new PageModel();
        modelOld.setMyStartTime(yesterday);
        modelOld.setMyEndTime(TimeTools.getNowBeforeByDay2(1));

        EsQueryModel queryModeOld = QueryTools.buildQueryModel(wrapper, modelOld, baseLineIndexPrefix, "insert_time", TimeTools.TIME_FMT_2, false);
        setCondition(queryModeOld, "op_type", "1");
        queryModeOld.setCount(9999);
        List<Map<String, Object>> abnormalList = new ArrayList<>();
        SearchResponse searchBaseResponse = wrapper.getSearchResponse(queryModeOld);
        if (searchBaseResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchBaseResponse.getHits(), "insert_time");
            // 基线中所有的用户(不在这个范围内的就当新用户去比, 新用户用群体基线去判断是否异常)
            List<String> userList = list.parallelStream().map(r -> r.get("username")).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")) + r.get("dev_ip"), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("dev_ip")), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));
                String ud = user + personRes.get("dev_ip");
                if (userList.contains(user) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!userList.contains(user) && allLineLast.containsKey(personRes.get("dev_ip"))) {
                    continue;
                }
                /*//异常类型
                Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "用户异常刻录设备清单");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("u1->用户异常打印/刻录文件（综合设备、文件类型、时间、频次、大小）");
                break;
            }

        }

        /*String abnormalIndex = abnormalIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            QueryTools.writeData(abnormalList, abnormalIndex, wrapper);
        } catch (Exception e) {
        }*/

        // 计算最新基线，并入库
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "op_type", "1");
        setCondition(queryModeNew0, "type", 0);
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "op_type", "1");
        setCondition(queryModeNew, "type", 1);
        //      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, deviceIpField, 5000, new String[]{countField},
                new String[]{countField}, deviceIpField, new String[]{"dev_name"});
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, userIdField, 5000, deviceIpField,
                new String[]{countField}, new String[]{countField}, "username", new String[]{"dev_name"});

        //用户A+设备IP+设备名称+刻录次数（个体基线）
        //* 设备IP+设备名称+刻录次数（群体基线）
        // "username", "dev_ip", "dev_name", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        Map<String, String> devMap = new HashMap<>(1000);
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put("dev_ip", r.get("dev_ip"));
            item.put("dev_name", r.get("dev_name"));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("op_type", "1");
            item.put("insert_time", currentTime);
            devMap.put(String.valueOf(r.get("dev_ip")), String.valueOf(r.get("dev_name")));
            return item;
        }).collect(Collectors.toList());

        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put("username", r.get("username"));
            item.put("dev_ip", r.get("dev_ip"));
            item.put("dev_name", devMap.get(String.valueOf(r.get("dev_ip"))));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("op_type", "1");
            item.put("insert_time", currentTime);

            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void printFileTypeList(String opType) {

        String printAuditPrefix = "print-audit";
        String printAuditSummaryPrefix = "summary-print-file-type-audit";
        String baseLineIndexPrefix = "base-line-print-file-type";
        String abnormalIndexPrefix = "abnormal-data";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, printAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        setCondition(queryModel, "op_type", opType);

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        String fileTypeField = "file_type";
        String countField = "count";
        // 计算前一天个体用户打印/刻录文件类型清单，并入库 （用户、打印/刻录文件类型、次数）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriUserIdField, fileTypeField, 5000, 5000, countField, null);

        // 计算前一天总体用户打印/刻录文件类型清单，并入库 （打印/刻录文件类型、次数）
        //  合计上述结果
        Map<String, Map<String, Object>> all = personResultList.stream()
                .map(r -> {
                    Map<String, Object> allItem = new HashMap<>();
                    allItem.putAll(r);
                    allItem.put("type", 0);
                    allItem.remove(oriUserIdField);
                    return allItem;
                })
                .collect(Collectors.toMap(r -> String.valueOf(r.get(fileTypeField)), r -> r, (a, b) -> {
                    a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
                    return a;
                }));

        // 写入printAuditSummary
        //   "username", "file_type", "count", "data_time", "insert_time", "type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(userIdField, r.get("std_user_no"));
            item.put(fileTypeField, r.get(fileTypeField));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        List<Map<String, Object>> allDevResultList = all.values().stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(fileTypeField, r.get(fileTypeField));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 0);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix + "-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "username", "file_type", "count", "data_time", "insert_time", "type"
        PageModel modelOld = new PageModel();
        modelOld.setMyStartTime(yesterday);
        modelOld.setMyEndTime(TimeTools.getNowBeforeByDay2(1));

        EsQueryModel queryModeOld = QueryTools.buildQueryModel(wrapper, modelOld, baseLineIndexPrefix, "insert_time", TimeTools.TIME_FMT_2, false);
        setCondition(queryModeOld, "op_type", opType);
        queryModeOld.setCount(9999);
        List<Map<String, Object>> abnormalList = new ArrayList<>();
        SearchResponse searchBaseResponse = wrapper.getSearchResponse(queryModeOld);
        if (searchBaseResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchBaseResponse.getHits(), "insert_time");
            // 基线中所有的用户(不在这个范围内的就当新用户去比, 新用户用群体基线去判断是否异常)
            List<String> userList = list.parallelStream().map(r -> r.get("username")).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")) + r.get(fileTypeField), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(fileTypeField)), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));
                String ud = user + personRes.get(fileTypeField);
                if (userList.contains(user) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!userList.contains(user) && allLineLast.containsKey(personRes.get(fileTypeField))) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "用户异常打印/刻录文件类型清单");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("u1->用户异常打印/刻录文件（综合设备、文件类型、时间、频次、大小）");
                break;
            }
        }

        /*String abnormalIndex = abnormalIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            QueryTools.writeData(abnormalList, abnormalIndex, wrapper);
        } catch (Exception e) {
        }*/

        // 计算最新基线，并入库
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "type", 0);
        setCondition(queryModeNew0, "op_type", opType);
        //      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, fileTypeField, 5000, new String[]{countField},
                new String[]{"total_count"}, fileTypeField, null);

        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        setCondition(queryModeNew, "op_type", opType);
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, userIdField, 5000, fileTypeField,
                new String[]{countField}, new String[]{"total_count"}, userIdField, null);

        //用户A+设备IP+设备名称+打印次数（个体基线）
        //* 设备IP+设备名称+打印次数（群体基线）
        // "username", "file_type", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        Map<String, String> devMap = new HashMap<>(1000);
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(fileTypeField, r.get(fileTypeField));
            item.put("total_count", r.get("total_count"));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        //username(type=0时为空值),file_type,total_count,start_time,end_time,interval,type(0=群体基线, 1=个体基线),op_type(0=打印/1=刻录),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put("username", r.get(userIdField));
            item.put(fileTypeField, r.get(fileTypeField));
            item.put("total_count", r.get("total_count"));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void printFileSize(String opType) {

        String printAuditPrefix = "print-audit";
        String printAuditSummaryPrefix = "summary-print-file-size-audit";
        String baseLineIndexPrefix = "base-line-print-file-size";
        String abnormalIndexPrefix = "abnormal-data";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, printAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        setCondition(queryModel, "op_type", opType);

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        String fileSizeField = "file_size";
        //String countField = "count";
        // 计算前一天个体用户打印/刻录文件类型清单，并入库 （用户、打印/刻录文件大小、日期）
        List<Map<String, Object>> personResultList = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, oriUserIdField, 5000, new String[]{fileSizeField},
                new String[]{fileSizeField}, userIdField, null);

        if (IterUtil.isEmpty(personResultList)) return;

        // 计算前一天总体用户打印/刻录文件类型清单，并入库 （打印/刻录文件大小、日期）
        //  合计上述结果
        long totalFileSize = personResultList.stream().collect(Collectors.summarizingLong(r -> ((Double) r.get(fileSizeField)).longValue())).getSum();

        // 写入printAuditSummary
        //   "username", "file_size", "data_time", "insert_time", "type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(6);
            item.put(userIdField, r.get(userIdField));
            item.put(fileSizeField, r.get(fileSizeField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        int userCount = personResultList.size();
        List<Map<String, Object>> allDevResultList = new ArrayList<>();
        Map<String, Object> itemAll = new HashMap<>(5);
        itemAll.put(fileSizeField, totalFileSize / userCount);
        itemAll.put("type", 0);
        itemAll.put("op_type", opType);
        itemAll.put("data_time", dataTime);
        itemAll.put("insert_time", currentTime);
        allDevResultList.add(itemAll);

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix + "-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "username", "file_size_min", "file_size_max", "data_time", "insert_time", "type"
        PageModel modelOld = new PageModel();
        modelOld.setMyStartTime(yesterday);
        modelOld.setMyEndTime(TimeTools.getNowBeforeByDay2(1));

        EsQueryModel queryModeOld = QueryTools.buildQueryModel(wrapper, modelOld, baseLineIndexPrefix, "insert_time", TimeTools.TIME_FMT_2, false);
        setCondition(queryModeOld, "op_type", opType);
        queryModeOld.setCount(9999);
        List<Map<String, Object>> abnormalList = new ArrayList<>();
        SearchResponse searchBaseResponse = wrapper.getSearchResponse(queryModeOld);
        if (searchBaseResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchBaseResponse.getHits(), "insert_time");
            // 基线中所有的用户(不在这个范围内的就当新用户去比, 新用户用群体基线去判断是否异常)
            Map<String, Map<String, Double>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")), r -> {
                        Map<String, Double> p = new HashMap<>();
                        p.put("max", Double.valueOf(r.get("file_size_max")));
                        p.put("min", Double.valueOf(r.get("file_size_min")));
                        return p;
                    }, (a, b) -> a));

            Optional<Map<String, Double>> allLineLastOp = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .map(r -> {
                        Map<String, Double> p = new HashMap<>();
                        p.put("max", Double.valueOf(r.get("file_size_max")));
                        p.put("min", Double.valueOf(r.get("file_size_min")));
                        return p;
                    }).findAny();


            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));

                Long size = Double.valueOf(String.valueOf(personRes.get(fileSizeField))).longValue();
                boolean abnormal = false;
                if (personLineLast.containsKey(user)) {
                    Map<String, Double> range = personLineLast.get(user);
                    //超出最大最小范围为异常 true
                    abnormal = size < range.get("min") || size > range.get("max") ? true : abnormal;
                }

                if (allLineLastOp.isPresent()) {
                    Map<String, Double> allLineLast = allLineLastOp.get();
                    abnormal = size < allLineLast.get("min") || size > allLineLast.get("max") ? true : abnormal;

                }
                if (!abnormal) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "用户异常打印文件大小超出范围");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("u1->用户异常打印/刻录文件（综合设备、文件类型、时间、频次、大小）");
                break;
            }

        }

        /*String abnormalIndex = abnormalIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            QueryTools.writeData(abnormalList, abnormalIndex, wrapper);
        } catch (Exception e) {
        }*/

        // 计算最新基线，并入库
        //      "username", "file_size_min", "file_size_max", "insert_time", "type"
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "type", 0);
        setCondition(queryModeNew0, "op_type", opType);
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        setCondition(queryModeNew, "op_type", opType);

        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndExtendStatsAgg(queryModeNew0, wrapper, "type", 5000, fileSizeField,
                "type", null);

        //* 文件大小最小值+文件大小最大值（群体基线）
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(fileSizeField, r.get("sum"));
            item.put("file_size_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("file_size_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndExtendStatsAgg(queryModeNew, wrapper, userIdField, 5000, fileSizeField,
                userIdField, null);

        //用户A+文件大小最小值+文件大小最大值（个体基线）
        //username(type=0时为空值),file_size(总大小),file_size_min(文件大小最小值),file_size_max(文件大小最小值),start_time,end_time,interval,type(0=群体基线, 1=个体基线),op_type
        // (0=打印/1=刻录),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(userIdField, r.get(userIdField));
            item.put(fileSizeField, r.get("sum"));
            item.put("file_size_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("file_size_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void printFileFreList(String opType) {
        //int opType = 0; 0-打印、1-刻录

        String printAuditPrefix = "print-audit";
        String printAuditSummaryPrefix = "summary-print-file-fre-audit";
        String baseLineIndexPrefix = "base-line-print-file-fre";
        String abnormalIndexPrefix = "abnormal-data";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, printAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        setCondition(queryModel, "op_type", opType);

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        //String fileSizeField = "file_size";
        String countField = "count";
        // 计算前一天个体用户打印/刻录文件类型清单，并入库 （用户、打印/刻录文件次数、日期）
//        List<Map<String, Object>> personResultList = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, userIdField, 5000, new String[]{fileSizeField},
//                new String[]{fileSizeField}, userIdField, null);

        List<Map<String, Object>> personResultList = QueryTools.simpleAggAndTopHit(queryModel, wrapper, oriUserIdField, 5000, userIdField, countField, null);

        if (IterUtil.isEmpty(personResultList)) return;
        // 计算前一天总体用户打印/刻录文件类型清单，并入库 （打印/刻录文件次数、日期）
        //  合计上述结果
        long totalCount = personResultList.stream().collect(Collectors.summarizingLong(r -> Long.valueOf(String.valueOf(r.get(countField))))).getSum();

        // 写入printAuditSummary
        //   "username", "count", "data_time", "insert_time", "type", "op_type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(5);
            item.put(userIdField, r.get(userIdField));
            item.put(countField, r.get(countField));
            item.put("type", 1);
            item.put("op_type", opType);
            item.put("data_time", dataTime);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        int userCount = personResultList.size();
        List<Map<String, Object>> allDevResultList = new ArrayList<>();
        Map<String, Object> itemAll = new HashMap<>(4);
        itemAll.put(countField, totalCount / userCount);
        itemAll.put("type", 0);
        itemAll.put("op_type", opType);
        itemAll.put("data_time", dataTime);
        itemAll.put("insert_time", currentTime);
        allDevResultList.add(itemAll);

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix + "-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "username",  "count_min", "count_max", "data_time", "insert_time", "type", "op_type"
        PageModel modelOld = new PageModel();
        modelOld.setMyStartTime(yesterday);
        modelOld.setMyEndTime(TimeTools.getNowBeforeByDay2(1));

        EsQueryModel queryModeOld = QueryTools.buildQueryModel(wrapper, modelOld, baseLineIndexPrefix, "insert_time", TimeTools.TIME_FMT_2, false);
        setCondition(queryModeOld, "op_type", opType);
        queryModeOld.setCount(9999);
        List<Map<String, Object>> abnormalList = new ArrayList<>();
        SearchResponse searchBaseResponse = wrapper.getSearchResponse(queryModeOld);
        if (searchBaseResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchBaseResponse.getHits(), "insert_time");
            // 基线中所有的用户(不在这个范围内的就当新用户去比, 新用户用群体基线去判断是否异常)
            Map<String, Map<String, Double>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")), r -> {
                        Map<String, Double> p = new HashMap<>(2);
                        p.put("max", Double.valueOf(r.get("count_max")));
                        p.put("min", Double.valueOf(r.get("count_min")));
                        return p;
                    }, (a, b) -> a));

            Optional<Map<String, Double>> allLineLastOp = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .map(r -> {
                        Map<String, Double> p = new HashMap<>(2);
                        p.put("max", Double.valueOf(r.get("count_max")));
                        p.put("min", Double.valueOf(r.get("count_min")));
                        return p;
                    }).findAny();


            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));
                Long size = Long.valueOf(String.valueOf(personRes.get(countField)));
                boolean abnormal = false;
                if (personLineLast.containsKey(user)) {
                    Map<String, Double> range = personLineLast.get(user);
                    //超出最大最小范围为异常 true
                    abnormal = size < range.get("min") || size > range.get("max") ? true : abnormal;
                }

                if (allLineLastOp.isPresent()) {
                    Map<String, Double> allLineLast = allLineLastOp.get();
                    abnormal = size < allLineLast.get("min") || size > allLineLast.get("max") ? true : abnormal;

                }
                if (!abnormal) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", opType == 0 ? "打印" : "刻录" + "文件频次超出范围");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("u1->用户异常打印/刻录文件（综合设备、文件类型、时间、频次、大小）");
                break;
            }

        }

        /*String abnormalIndex = abnormalIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            QueryTools.writeData(abnormalList, abnormalIndex, wrapper);
        } catch (Exception e) {
        }*/

        // 计算最新基线，并入库
        //      "username", "count_min", "count_max", "insert_time", "type"
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "type", 0);
        setCondition(queryModeNew0, "op_type", opType);
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        setCondition(queryModeNew, "op_type", opType);

        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndExtendStatsAgg(queryModeNew0, wrapper, "type", 5000, countField,
                "type", null);


        //* 文件大小最小值+文件大小最大值（群体基线）
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(countField, r.get("count"));
            item.put("count_avg", r.get("avg"));
            item.put("count_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("count_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndExtendStatsAgg(queryModeNew, wrapper, userIdField, 5000, countField,
                userIdField, null);

        //用户A+最小次数+文件大小最大值（个体基线）
        //username(type=0时为空值),count(打印/刻录总次数),count_avg(平均次数), count_min(最小次数),count_max(最大次数),start_time,end_time,interval,type(0=群体基线, 1=个体基线),op_type
        // (0=打印/1=刻录),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(userIdField, r.get(userIdField));
            item.put(countField, r.get("count"));
            item.put("count_avg", r.get("avg"));
            item.put("count_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("count_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void printFileTimeFre(String opType) {
        //int opType = 0; 0-打印、1-刻录

        String printAuditPrefix = "print-audit";
        String printAuditSummaryPrefix = "summary-print-file-brand-audit";
        String baseLineIndexPrefix = "base-line-print-file-brand";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, printAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        setCondition(queryModel, "op_type", opType);

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        String hourField = "hour";
        String timeBucketField = "time_bucket";
        String countField = "count";
        // 计算前一天个体用户24小时段 打印/刻录文件次数清单，并入库
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriUserIdField, hourField, 5000, 1000, countField, null);

        if (IterUtil.isEmpty(personResultList)) return;
        // 计算前一天总体用户24小时段 打印/刻录文件次数清单，并入库
        //  合计上述结果

        // 写入printAuditSummary
        //   "username","hour", "count", "data_time", "insert_time", "type", "op_type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(userIdField, r.get(oriUserIdField));
            item.put(hourField, r.get(hourField));
            item.put(countField, r.get(countField));
            item.put("type", 1);
            item.put("op_type", opType);
            item.put("data_time", dataTime);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        Map<String, Map<String, Object>> all = personResultList.stream()
                .map(r -> {
                    Map<String, Object> allItem = new HashMap<>();
                    allItem.putAll(r);
                    allItem.put("type", 0);
                    allItem.remove(userIdField);
                    return allItem;
                })
                .collect(Collectors.toMap(r -> String.valueOf(r.get(hourField)), r -> r, (a, b) -> {
                    a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
                    return a;
                }));

        long userCount = personResultList.stream().map(r -> r.get(userIdField)).distinct().count();
        List<Map<String, Object>> allDevResultList = all.values().stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(hourField, r.get(hourField));
            item.put(countField, Long.valueOf(String.valueOf(r.get(countField))) / userCount);
            item.put("data_time", dataTime);
            item.put("type", 0);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix + "-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 计算最新基线，并入库
        //      username(type=0时为空值),count(打印/刻录总次数),count_avg(平均次数), count_min(最小次数),file_size_max(最大次数),time_bucket(时段, 整型从0到23, 0代表0点到1点时段),start_time,end_time,interval,type
        // (0=群体基线, 1=个体基线),op_type(0=打印/1=刻录),insert_time
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "type", 0);
        setCondition(queryModeNew0, "op_type", opType);
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        setCondition(queryModeNew, "op_type", opType);

        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndExtendStatsAgg(queryModeNew0, wrapper, hourField, 5000, countField,
                timeBucketField, null);


        //* 群体基线
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(16);
            item.put(timeBucketField, r.get(timeBucketField));
            item.put(countField, r.get("count"));
            item.put("count_avg", r.get("avg"));
            item.put("count_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("count_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        List<Map<String, Object>> baselinePerson = QueryTools.twoLevelTermAndExtendStatsAgg(queryModeNew, wrapper, userIdField, 5000,
                hourField, 30, countField, userIdField, timeBucketField, null);

        // 个体基线
        //username(type=0时为空值),count(打印/刻录总次数),count_avg(平均次数), count_min(最小次数),file_size_max(最大次数),time_bucket(时段, 整型从0到23, 0代表0点到1点时段),
        // start_time,end_time,interval,type(0=群体基线, 1=个体基线),op_type(0=打印/1=刻录),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(16);
            item.put(userIdField, r.get(userIdField));
            item.put(timeBucketField, r.get(timeBucketField));
            item.put(countField, r.get("count"));
            item.put("count_avg", r.get("avg"));
            item.put("count_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("count_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("op_type", opType);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void businessList() {

        String printAuditPrefix = "print-audit";
        String fileIndex = "file-audit";
        String file2Index = "netflow-app-file";
        String printAuditSummaryPrefix = "summary-person-business-audit";
        String baseLineIndexPrefix = "base-line-user-business";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, printAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        EsQueryModel fileQueryModel = QueryTools.buildQueryModel(wrapper, model, fileIndex, "event_time", TimeTools.TIME_FMT_1, false);
        EsQueryModel fileQueryModel2 = QueryTools.buildQueryModel(wrapper, model, file2Index, "event_time", TimeTools.TIME_FMT_1, false);

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        String businessType = "business_list";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （用户、业务类型、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriUserIdField, businessType, 5000, 5000, countField, null);
        //personResultList
        List<Map<String, Object>> personResultList2 = QueryTools.twoLevelAgg(fileQueryModel, wrapper, oriUserIdField, businessType, 5000, 5000, countField, null);
        List<Map<String, Object>> personResultList3 = QueryTools.twoLevelAgg(fileQueryModel2, wrapper, oriUserIdField, businessType, 5000, 5000, countField, null);
        personResultList.addAll(personResultList2);
        personResultList.addAll(personResultList3);

        // 写入printAuditSummary
        //   "username", "business_type", "data_time", "insert_time", "type"
        personResultList = personResultList.stream().flatMap(r -> {
            List<String> newDatas = new ArrayList<>();
            for (String singleBusinessType : r.get(businessType).toString().split(",")) {
                newDatas.add(r.get(oriUserIdField) + "," + singleBusinessType);
            }
            return newDatas.stream();
        }).distinct().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            String[] split = r.split(",");
            item.put(userIdField, split[0]);
            item.put(businessType, split[1]);
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        // 计算前一天总体用户打印设备清单，并入库 （业务类型）
        //  合计上述结果
        List<Map<String, Object>> allDevResultList = personResultList.stream().map(r -> r.get(businessType)).distinct().map(r -> {
            Map<String, Object> item = new HashMap<>(5);
            item.put(businessType, r);
            item.put("data_time", dataTime);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix + "-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "username", "business_type", "dev_name", "total_count", "start_time", "end_time", "interval", "insert_time", "type", "op_type"
        PageModel modelOld = new PageModel();
        modelOld.setMyStartTime(yesterday);
        modelOld.setMyEndTime(TimeTools.getNowBeforeByDay2(1));

        EsQueryModel queryModeOld = QueryTools.buildQueryModel(wrapper, modelOld, baseLineIndexPrefix, "insert_time", TimeTools.TIME_FMT_2, false);
        queryModeOld.setCount(9999);
        List<Map<String, Object>> abnormalList = new ArrayList<>();
        SearchResponse searchBaseResponse = wrapper.getSearchResponse(queryModeOld);
        if (searchBaseResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchBaseResponse.getHits(), "insert_time");
            // 基线中所有的用户(不在这个范围内的就当新用户去比, 新用户用群体基线去判断是否异常)
            List<String> userList = list.parallelStream().map(r -> r.get("username")).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")) + r.get(businessType), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(businessType)), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));
                String ud = user + personRes.get(businessType);
                if (userList.contains(user) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!userList.contains(user) && allLineLast.containsKey(personRes.get(businessType))) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "用户处理文件业务超出日常范围");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("u2->用户处理超出日常业务范围文件");
                break;
            }

        }

        /*String abnormalIndex = abnormalIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            QueryTools.writeData(abnormalList, abnormalIndex, wrapper);
        } catch (Exception e) {
        }*/

        // 计算最新基线，并入库
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        //setCondition(queryModeNew0, "op_type", 0);
        setCondition(queryModeNew0, "type", 0);
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        //setCondition(queryModeNew, "op_type", 0);
        setCondition(queryModeNew, "type", 1);
        //      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, businessType, 5000, new String[]{countField},
                new String[]{"total_count"}, businessType, new String[]{"dev_name"});
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, userIdField, 5000, businessType,
                new String[]{countField}, new String[]{"total_count"}, "username", null);

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        // "username", "business_type", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        Map<String, String> devMap = new HashMap<>(1000);
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put(businessType, r.get(businessType));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            devMap.put(String.valueOf(r.get("dev_ip")), String.valueOf(r.get("dev_name")));
            return item;
        }).collect(Collectors.toList());

        // username(type=0时为空值),business_type(业务类型,单个的),start_time,end_time,interval(30, 即30天),type(0=群体基线/1=个体基线),op_type(0=打印/1=刻录),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put("username", r.get("username"));
            item.put(businessType, r.get(businessType));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void setStartEndTime(int interval, PageModel model) {
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(TimeTools.getNowBeforeByDay(interval));
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
    }

    private void downloadList() {
        //int opType = 0; 0-打印、1-刻录

//        String printAuditPrefix = "print-audit";
        String fileIndex = "netflow-app-file";

        String printAuditSummaryPrefix = "summary-file-download-audit";
        String baseLineIndexPrefix = "base-line-user-file-download";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, fileIndex, "event_time", TimeTools.TIME_FMT_1, false);
        //文件传输方向(2下载)
        setCondition(queryModel, "file_dir", 2);

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        //String fileSizeField = "file_size";
        String countField = "count";
        // 计算前一天个体用户打印/刻录文件类型清单，并入库 （用户、打印/刻录文件次数、日期）
        List<Map<String, Object>> personResultList = QueryTools.simpleAggAndTopHit(queryModel, wrapper, oriUserIdField, 5000, userIdField, countField, null);

        // 计算前一天总体用户打印/刻录文件类型清单，并入库 （打印/刻录文件次数、日期）
        //  合计上述结果
        long totalCount = personResultList.stream().collect(Collectors.summarizingLong(r -> Long.valueOf(String.valueOf(r.get(countField))))).getSum();

        // 写入printAuditSummary
        //   "username", "count", "data_time", "insert_time", "type", "op_type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(5);
            item.put(userIdField, r.get(userIdField));
            item.put(countField, r.get(countField));
            item.put("type", 1);
            item.put("data_time", dataTime);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        int userCount = personResultList.size();
        List<Map<String, Object>> allDevResultList = new ArrayList<>();
        Map<String, Object> itemAll = new HashMap<>(4);
        itemAll.put(countField, userCount == 0 ? 0 : totalCount / userCount);
        itemAll.put("type", 0);
        itemAll.put("data_time", dataTime);
        itemAll.put("insert_time", currentTime);
        allDevResultList.add(itemAll);

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix + "-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "username",  "count_min", "count_max", "data_time", "insert_time", "type", "op_type"
        PageModel modelOld = new PageModel();
        modelOld.setMyStartTime(yesterday);
        modelOld.setMyEndTime(TimeTools.getNowBeforeByDay2(1));

        EsQueryModel queryModeOld = QueryTools.buildQueryModel(wrapper, modelOld, baseLineIndexPrefix, "insert_time", TimeTools.TIME_FMT_2, false);
        queryModeOld.setCount(9999);
        List<Map<String, Object>> abnormalList = new ArrayList<>();
        SearchResponse searchBaseResponse = wrapper.getSearchResponse(queryModeOld);
        if (searchBaseResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchBaseResponse.getHits(), "insert_time");
            // 基线中所有的用户(不在这个范围内的就当新用户去比, 新用户用群体基线去判断是否异常)
            Map<String, Map<String, Double>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")), r -> {
                        Map<String, Double> p = new HashMap<>(2);
                        p.put("max", Double.valueOf(r.get("count_max")));
                        p.put("min", Double.valueOf(r.get("count_min")));
                        return p;
                    }, (a, b) -> a));

            Optional<Map<String, Double>> allLineLastOp = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .map(r -> {
                        Map<String, Double> p = new HashMap<>(2);
                        p.put("max", Double.valueOf(r.get("count_max")));
                        p.put("min", Double.valueOf(r.get("count_min")));
                        return p;
                    }).findAny();


            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));
                Long size = Long.valueOf(String.valueOf(personRes.get(countField)));
                boolean abnormal = false;
                if (personLineLast.containsKey(user)) {
                    Map<String, Double> range = personLineLast.get(user);
                    //超出最大最小范围为异常 true
                    abnormal = size < range.get("min") || size > range.get("max") ? true : abnormal;
                }

                if (allLineLastOp.isPresent()) {
                    Map<String, Double> allLineLast = allLineLastOp.get();
                    abnormal = size < allLineLast.get("min") || size > allLineLast.get("max") ? true : abnormal;

                }
                if (!abnormal) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "用户下载应用系统数据数量异常");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("u3->用户下载应用系统文件数量异常");
                break;
            }

        }

        /*String abnormalIndex = abnormalIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            QueryTools.writeData(abnormalList, abnormalIndex, wrapper);
        } catch (Exception e) {
        }*/

        // 计算最新基线，并入库
        //      "username", "count_min", "count_max", "insert_time", "type"
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "type", 0);
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);

        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndExtendStatsAgg(queryModeNew0, wrapper, "type", 5000, countField,
                "type", null);


        //* 文件大小最小值+文件大小最大值（群体基线）
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(countField, r.get(countField));
            item.put("count_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("count_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndExtendStatsAgg(queryModeNew, wrapper, userIdField, 5000, countField,
                userIdField, null);

        //用户A+文件大小最小值+文件大小最大值（个体基线）
        //username(type=0时为空值),count(下载总数量),count_avg(平均数量), count_min(最小数量),count_max(最大数量),start_time,end_time,interval,type(0=群体基线, 1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(userIdField, r.get(userIdField));
            item.put(countField, r.get("count"));
            item.put("count_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("count_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void setCondition(EsQueryModel queryModel4, String field, Object value) {
        BoolQueryBuilder query = (BoolQueryBuilder) queryModel4.getQueryBuilder();
        query.filter(QueryBuilders.termQuery(field, value));
    }
}
