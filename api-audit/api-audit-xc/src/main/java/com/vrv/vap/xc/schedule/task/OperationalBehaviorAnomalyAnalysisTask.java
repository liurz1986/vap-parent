package com.vrv.vap.xc.schedule.task;

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
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 运维行为异常分析及基线分析
 * Created by cz on 2021/8/12
 */
public class OperationalBehaviorAnomalyAnalysisTask extends BaseTask {

    private CommonService commonService = VapXcApplication.getApplicationContext().getBean(CommonService.class);
    private DataCleanMapper dataCleanDao = VapXcApplication.getApplicationContext().getBean(DataCleanMapper.class);

    private final String indexSufFormat = "-yyyy";
    private int interval = 30;
    /**
     * 倍数(正负均方差倍数范围)
     */
    private int multiple = 2;

    private ThreadLocal<Set> strategys = new ThreadLocal<>();

    public void fillStrategy(String strategy) {
        if (strategys.get() == null) {
            strategys.set(new HashSet());
        }
        strategys.get().add(strategy);
    }

    private void initSetting(){
        try {
            interval = Integer.parseInt(dataCleanDao.getConfById("base_line_user_interval"));
            multiple = Integer.parseInt(dataCleanDao.getConfById("base_line_user_multiple"));
        } catch (NumberFormatException e) {
        }
    }

    private void buildMutlQueryModel(QueryTools.QueryWrapper wrapper, PageModel model, EsQueryModel queryModel, String... netAuditPrefix) {
        List<String> auditIndexNames = new ArrayList<>();
        Collections.addAll(auditIndexNames, queryModel.getIndexNames());
        for (String auditPrefix : netAuditPrefix) {
            EsQueryModel queryModel1 = QueryTools.buildQueryModel(wrapper, model, auditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
            if (queryModel1.getIndexNames() == null) {
                continue;
            }
            Collections.addAll(auditIndexNames, queryModel1.getIndexNames());
        }
        queryModel.setIndexNames(auditIndexNames.toArray(new String[]{}));
    }

    @Override
    public void run(String jobName) {
        initSetting();
        // 按各指标逐个分析

        // -----------------------------------------------------
        // 历史运维协议
        // -----------------------------------------------------
        protocolList();

        // -----------------------------------------------------
        // 历史运维端口
        // -----------------------------------------------------
        portList();

        // -----------------------------------------------------
        // 文件导入数量
        // -----------------------------------------------------
        statsFileCount();

        // -----------------------------------------------------
        // 文件刻录数据量
        // -----------------------------------------------------
        statsBurnFileCount("1");

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
            item.put("type", "运维行为异常");
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

    private void statsFileCount() {

        String fileAuditPrefix = "netflow-file";
        String fileAuditPrefix2 = "netflow-app-file";

        String printAuditSummaryPrefix = "summary-admin-file-copy-audit";
        String baseLineIndexPrefix = "base-line-admin-file-copy";
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
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, fileAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        setConditions(queryModel,"src_std_user_type",new Integer[]{1,2});
        buildMutlQueryModel(wrapper, model, queryModel, fileAuditPrefix2);

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        //String fileSizeField = "file_size";
        String countField = "count";
        // 计算前一天个体用户打印/刻录文件类型清单，并入库 （用户、打印/刻录文件次数、日期）
//        List<Map<String, Object>> personResultList = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, userIdField, 5000, new String[]{fileSizeField},
//                new String[]{fileSizeField}, userIdField, null);

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
                item.put("abnormal_desc", "管理员文件导入数量超出范围");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("op3->管理员使用运维终端大量拷贝或刻录文件");
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
            item.put(countField, (Double) r.get("sum"));
            item.put("count_avg", r.get("avg"));
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
        //username(type=0时为空值),count(导入文件总数量),count_avg(平均数量), count_min(最小数量),count_max(最大数量),start_time,end_time,interval,type(0=群体基线, 1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(userIdField, r.get(userIdField));
            item.put(countField, (Double) r.get("sum"));
            item.put("count_avg", r.get("avg"));
            item.put("count_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("count_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("total_count", (Double) r.get("sum"));
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

    private void statsBurnFileCount(String opType) {
        int interval = 7;
        String printAuditPrefix = "print-audit";
        String printAuditSummaryPrefix = "summary-admin-file-burn-audit";
        String baseLineIndexPrefix = "base-line-admin-file-burn";
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
        setConditions(queryModel,"std_user_type",new Integer[]{1,2});
        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        //String fileSizeField = "file_size";
        String countField = "count";
        // 计算前一天个体用户打印/刻录文件类型清单，并入库 （用户、刻录文件次数、日期）
//        List<Map<String, Object>> personResultList = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, userIdField, 5000, new String[]{fileSizeField},
//                new String[]{fileSizeField}, userIdField, null);

        List<Map<String, Object>> personResultList = QueryTools.simpleAggAndTopHit(queryModel, wrapper, oriUserIdField, 5000, userIdField, countField, null);

        // 计算前一天总体用户打印/刻录文件类型清单，并入库 （刻录文件次数、日期）
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
        itemAll.put(countField,  userCount == 0 ? 0 : totalCount / userCount);
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
        queryModeOld.setCount(9999);
        setCondition(queryModeOld, "op_type", opType);
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
                item.put("abnormal_desc", "管理员文件导入数量超出范围");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("op3->管理员使用运维终端大量拷贝或刻录文件");
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
            item.put(countField, (Double) r.get("sum"));
            item.put("count_avg", r.get("avg"));
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
        //username(type=0时为空值),count(刻录文件总数量),count_avg(平均数量), count_min(最小数量),count_max(最大数量),start_time,end_time,interval,type(0=群体基线, 1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(8);
            item.put(userIdField, r.get(userIdField));
            item.put(countField, (Double) r.get("sum"));
            item.put("count_avg", r.get("avg"));
            item.put("count_min", (Double) r.get("avg") - (Double) r.get("std_deviation") * multiple);
            item.put("count_max", (Double) r.get("avg") + (Double) r.get("std_deviation") * multiple);
            item.put("total_count", (Double) r.get("sum"));
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

    private void protocolList() {
        int interval = 7;
        String netAuditPrefix = "netflow-tcp";
        String netAuditPrefix1 = "netflow-udp";
        String netAuditPrefix2 = "netflow-http";
        String netAuditPrefix3 = "netflow-dns";
        String netAuditPrefix4 = "netflow-email";
        String netAuditPrefix5 = "netflow-db";

        String fileIndex = "netflow-app-file";
//        String fortIndex = "fort-audit";

        String printAuditSummaryPrefix = "summary-admin-protocol-audit";
        String baseLineIndexPrefix = "base-line-admin-protocol";
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
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, netAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        // 管理员类型
        setConditions(queryModel,"src_std_user_type",new Integer[]{1,2});
        buildMutlQueryModel(wrapper, model, queryModel, netAuditPrefix1, netAuditPrefix2, netAuditPrefix3, netAuditPrefix4, netAuditPrefix5/*, netAuditPrefix6*/);

        EsQueryModel fileQueryModel = QueryTools.buildQueryModel(wrapper, model, fileIndex, "event_time", TimeTools.TIME_FMT_1, false);
        setConditions(fileQueryModel, "src_std_user_type", new Integer[]{1,2});

//        EsQueryModel fileQueryModel2 = QueryTools.buildQueryModel(wrapper, model, fortIndex, "event_time", TimeTools.TIME_FMT_1, false);
//        setCondition(fileQueryModel2, "user_type", 2);

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        String appProtocol = "app_protocol";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、协议、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriUserIdField, appProtocol, 5000, 500, countField, null);
        //personResultList
        List<Map<String, Object>> personResultList2 = QueryTools.twoLevelAgg(fileQueryModel, wrapper, oriUserIdField, appProtocol, 5000, 5000, countField, null);
//        List<Map<String, Object>> personResultList3 = QueryTools.twoLevelAgg(fileQueryModel2, wrapper, oriUserIdField, appProtocol, 5000, 5000, countField, null);
        personResultList.addAll(personResultList2);
//        personResultList.addAll(personResultList3);

        // 写入printAuditSummary
        //   "username", "business_type", "data_time", "insert_time", "type"
        // 写入printAuditSummary
        //   "username", "file_type", "count", "data_time", "insert_time", "type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(userIdField, r.get(oriUserIdField));
            item.put(appProtocol, r.get(appProtocol));
            //item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());


        // 计算前一天总体用户打印设备清单，并入库 （协议）
        //  合计上述结果
        List<Map<String, Object>> allDevResultList = personResultList.stream().map(r -> r.get(appProtocol)).distinct().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(appProtocol, r);
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
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")) + r.get(appProtocol), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(appProtocol)), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));
                String ud = user + personRes.get(appProtocol);
                if (userList.contains(user) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!userList.contains(user) && allLineLast.containsKey(personRes.get(appProtocol))) {
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
                fillStrategy("op1->使用异常协议运维");
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
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        //      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, appProtocol, 5000, new String[]{},
                new String[]{}, appProtocol, null);
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, userIdField, 5000, appProtocol,
                new String[]{}, new String[]{}, "username");

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        // "username", "business_type", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put("protocol", r.get(appProtocol));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        //username(type=0时为空值),protocol(协议),start_time,end_time,interval(30, 即30天),type(0=群体基线/1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put("username", r.get("username"));
            item.put("protocol", r.get(appProtocol));
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

    private void portList() {
        int interval = 7;
        String netAuditPrefix = "netflow-tcp";
        String netAuditPrefix1 = "netflow-udp";
        String netAuditPrefix2 = "netflow-http";
        String netAuditPrefix3 = "netflow-dns";
        String netAuditPrefix4 = "netflow-email";
        String netAuditPrefix5 = "netflow-db";

        String fileIndex = "netflow-app-file";

        String printAuditSummaryPrefix = "summary-admin-port-audit";
        String baseLineIndexPrefix = "base-line-admin-port";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, netAuditPrefix, "event_time", TimeTools.TIME_FMT_1, false);
        // 管理员类型
        setConditions(queryModel,"src_std_user_type",new Integer[]{1,2});
        buildMutlQueryModel(wrapper, model, queryModel, netAuditPrefix1, netAuditPrefix2, netAuditPrefix3, netAuditPrefix4, netAuditPrefix5/*, netAuditPrefix6*/);

        EsQueryModel fileQueryModel = QueryTools.buildQueryModel(wrapper, model, fileIndex, "event_time", TimeTools.TIME_FMT_1, false);
        setConditions(fileQueryModel, "src_std_user_type", new Integer[]{1,2});

        String oriUserIdField = "std_user_no";
        String userIdField = "username";
        String dport = "dport";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、端口、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriUserIdField, dport, 5000, 500, countField, null);
        //personResultList
        List<Map<String, Object>> personResultList2 = QueryTools.twoLevelAgg(fileQueryModel, wrapper, oriUserIdField, dport, 5000, 5000, countField, null);
        personResultList.addAll(personResultList2);

        // 写入printAuditSummary
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(userIdField, r.get(oriUserIdField));
            item.put(dport, r.get(dport));
            //item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());


        // 计算前一天总体用户打印设备清单，并入库 （协议）
        // 合计上述结果
        List<Map<String, Object>> allDevResultList = personResultList.stream().map(r -> r.get(dport)).distinct().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(dport, r);
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
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("username")) + r.get("port"), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get("port")), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(userIdField));
                String ud = user + personRes.get("port");
                if (userList.contains(user) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!userList.contains(user) && allLineLast.containsKey(personRes.get("port"))) {
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
                fillStrategy("op2->使用异常端口运维");
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
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        //      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, dport, 5000, new String[]{},
                new String[]{}, dport, null);
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, userIdField, 5000, dport,
                new String[]{}, new String[]{}, "username");

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        // "username", "business_type", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        Map<String, String> devMap = new HashMap<>(1000);
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put("port", r.get(dport));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        //username(type=0时为空值),port(运维端口),start_time,end_time,interval(30, 即30天),type(0=群体基线/1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put("username", r.get("username"));
            item.put("port", r.get(dport));
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


    private void setCondition(EsQueryModel queryModel4, String field, Object value) {
        BoolQueryBuilder query = (BoolQueryBuilder) queryModel4.getQueryBuilder();
        query.filter(QueryBuilders.termQuery(field, value));
    }

    private void setConditions(EsQueryModel queryModel4, String field, Object[] values) {
        BoolQueryBuilder query = (BoolQueryBuilder) queryModel4.getQueryBuilder();
        query.filter(QueryBuilders.termsQuery(field, values));
    }
}
