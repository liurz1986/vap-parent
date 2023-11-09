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
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用异常分析及基线分析
 * Created by cz on 2021/8/12
 */
public class AppAnomalyAnalysisTask extends BaseTask {

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
        // 历史通信协议
        // -----------------------------------------------------
        protocolList();

        // -----------------------------------------------------
        // 历史被访问端口清单
        // -----------------------------------------------------
        portList();

        // -----------------------------------------------------
        // 历史处理文件业务范围
        // -----------------------------------------------------
        businessList();

        // -----------------------------------------------------
        // 用户越权访问非授权页面
        // -----------------------------------------------------
        accessList();

        // -----------------------------------------------------
        // 历史被访问设备清单
        // -----------------------------------------------------
        dstIpList();

        writeRecommendStrategys();

    }

    /**
     * 写入推荐策略
     */
    protected void writeRecommendStrategys() {
        Set<String> recommendStrategys = strategys.get();
        if(recommendStrategys==null || recommendStrategys.isEmpty()){
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
            item.put("type", "应用异常");
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

    private void accessList() {
        
        String netAuditPrefix2 = "netflow-http";

        String printAuditSummaryPrefix = "summary-app-ua-visit";
        String baseLineIndexPrefix = "base-line-app-ua-visit";
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
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, netAuditPrefix2, "event_time", TimeTools.TIME_FMT_1, false);
        //buildMutlQueryModel(wrapper, model, queryModel, netAuditPrefix6);

        String usernameField = "username";
        String oriUserIdField = "std_user_no";

        String urlField = "url";
        String oriUserTypeField = "src_std_user_type";
        String userTypeField = "user_type";
        String uriField = "uri";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、端口、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriUserIdField, urlField, 5000, 50000, countField,
                new String[]{oriUserTypeField});

        // 写入auditSummary
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(usernameField, r.get(oriUserIdField));
            item.put(userTypeField, r.get(oriUserTypeField));
            item.put(uriField, r.get(urlField));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        // 计算前一天总体用户打印设备清单，并入库 （协议）
        //  合计上述结果
        Collection<Map<String, Object>> total1 = personResultList.stream().filter(f -> "2".equals(String.valueOf(f.get(userTypeField)))).map(r -> {
            Map<String, Object> item = new HashMap<>(5);
            item.put(userTypeField, 2);
            item.put(uriField, r.get(uriField));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toMap(r -> String.valueOf(r.get(uriField)), r -> r, (a, b) -> {
            a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
            return a;
        })).values();
        Collection<Map<String, Object>> total2 = personResultList.stream().filter(f -> "1".equals(String.valueOf(f.get(userTypeField)))).map(r -> {
            Map<String, Object> item = new HashMap<>(5);
            item.put(userTypeField, 1);
            item.put(uriField, r.get(uriField));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toMap(r -> String.valueOf(r.get(uriField)), r -> r, (a, b) -> {
            a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
            return a;
        })).values();
        List<Map<String, Object>> allDevResultList = new ArrayList<>(total1);
        allDevResultList.addAll(total2);

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix+"-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "app_id", "business_type", "port", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        PageModel modelOld = new PageModel();
        modelOld.setMyStartTime(yesterday);
        modelOld.setMyEndTime(TimeTools.getNowBeforeByDay2(1));

        EsQueryModel queryModeOld = QueryTools.buildQueryModel(wrapper, modelOld, baseLineIndexPrefix, "insert_time", TimeTools.TIME_FMT_2, false);
        queryModeOld.setCount(9999);
        SearchResponse searchBaseResponse = wrapper.getSearchResponse(queryModeOld);
        if (searchBaseResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchBaseResponse.getHits(), "insert_time");
            // 基线中所有的用户(不在这个范围内的就当新用户去比, 新用户用群体基线去判断是否异常)
            List<String> appList = list.parallelStream().map(r -> r.get(usernameField)).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(usernameField)) + r.get(uriField), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type")))
                    && "1".equals(String.valueOf(f.get(userTypeField)))
            ).collect(Collectors.toMap(r -> String.valueOf(r.get(uriField)), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allAdminLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type")))
                    && "2".equals(String.valueOf(f.get(userTypeField)))
            ).collect(Collectors.toMap(r -> String.valueOf(r.get(uriField)), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String appId = String.valueOf(personRes.get(usernameField));
                String userType = String.valueOf(personRes.get(userTypeField));
                String ud = appId + personRes.get(uriField);
                if (appList.contains(appId)) {
                    if (personLineLast.containsKey(ud)) {
                        continue;
                    }
                } else {
                    if ("1".equals(userType) && !allLineLast.containsKey(personRes.get(uriField))) {
                        //异常类型
                        fillStrategy("app4->用户越权访问非授权页面");
                        break;
                    } else if ("2".equals(userType) && !allAdminLineLast.containsKey(personRes.get(uriField))) {
                        //异常类型
                        fillStrategy("app5->管理员访问业务页面");
                        break;
                    }
                }


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
        //      总体基线清单1
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "type", 0);
        setCondition(queryModeNew0, userTypeField, 2);
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, uriField, 5000, new String[]{countField},
                new String[]{countField}, uriField, new String[]{userTypeField});

        //      总体基线清单2
        EsQueryModel queryModeNew01 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew01, "type", 0);
        setCondition(queryModeNew01, userTypeField, 1);
        List<Map<String, Object>> baselineAll0 = QueryTools.simpleTermAndSumAgg(queryModeNew01, wrapper, uriField, 5000, new String[]{countField},
                new String[]{countField}, uriField, new String[]{userTypeField});
        baselineAll.addAll(baselineAll0);

        //      个体基线清单
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, usernameField, 5000, uriField,
                new String[]{countField}, new String[]{countField}, usernameField, new String[]{userTypeField});

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        // "username", "business_type", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put(uriField, r.get(uriField));
            item.put(userTypeField, r.get(userTypeField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        //app_id,app_name,port(端口),total_count(次数), start_time,end_time,interval,type(0=群体基线, 1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(usernameField, r.get(usernameField));
            item.put(userTypeField, r.get(userTypeField));
            item.put(uriField, r.get(uriField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }


    private void protocolList() {
        
        String netAuditPrefix2 = "netflow-http";

        //String fortIndex = "fort-audit";

        String printAuditSummaryPrefix = "summary-app-protocol-audit";
        String baseLineIndexPrefix = "base-line-app-protocol";
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
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, netAuditPrefix2, "event_time", TimeTools.TIME_FMT_1, false);
        //EsQueryModel fileQueryModel = QueryTools.buildQueryModel(wrapper, model, netAuditPrefix6, "event_time", TimeTools.TIME_FMT_1, false);
        List<String> auditIndexNames = new ArrayList<>();
        Collections.addAll(auditIndexNames, queryModel.getIndexNames());
        //Collections.addAll(auditIndexNames, fileQueryModel.getIndexNames());
        queryModel.setIndexNames(auditIndexNames.toArray(new String[]{}));

        //EsQueryModel fileQueryModel2 = QueryTools.buildQueryModel(wrapper, model, fortIndex, "event_time", TimeTools.TIME_FMT_1);

        String appNameField = "app_name";
        String appIdField = "app_id";
        String oriAppIdField = "std_sys_id";
        String appProtocol = "app_protocol";
        // 通信类型（对内、应用内部、外部） appNameField
        String netType = "net_type";
        String oriNetType = "std_communication_type";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （应用名称、通信类型（对内、应用内部、外部）、协议、次数、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.threeLevelAgg(queryModel, wrapper, oriAppIdField, oriNetType, appProtocol, new String[]{appIdField, netType,
                        appProtocol},
                5000, 5000, 500, countField, new String[]{appNameField});

        //堡垒机的聚合字段可能要根据实际调整 todo
        /*List<Map<String, Object>> personResultList3 = QueryTools.threeLevelAgg(fileQueryModel2, wrapper, appIdField, netType, appProtocol, new String[]{appIdField, netType,
        appProtocol},
                5000, 5000, 500, countField, new String[]{appNameField});
        personResultList.addAll(personResultList3);*/

        // 写入printAuditSummary
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(appIdField, r.get(appIdField));
            item.put(appNameField, r.get(appNameField));
            item.put(netType, r.get(netType));
            item.put(appProtocol, r.get(appProtocol));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        // 计算前一天总体用户打印设备清单，并入库 （协议）
        //  合计上述结果
        List<Map<String, Object>> allDevResultList = personResultList.stream()
                .map(r -> {
                    Map<String, Object> allItem = new HashMap<>();
                    allItem.putAll(r);
                    allItem.put("type", 0);
                    allItem.remove(appIdField);
                    allItem.remove(appNameField);
                    return allItem;
                })
                .collect(Collectors.toMap(r -> String.valueOf(r.get(netType)) + "_" + r.get(appProtocol), r -> r,
                        (a, b) -> {
                            a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
                            a.put("data_time", dataTime);
                            a.put("type", 0);
                            a.put("insert_time", currentTime);
                            a.remove(appIdField);
                            a.remove(appNameField);
                            return a;
                        })).values().stream().collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix+"-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
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
            List<String> appList = list.parallelStream().map(r -> r.get(appIdField)).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(appIdField)) + r.get(netType) + r.get(appProtocol), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> r.get(netType) + String.valueOf(r.get(appProtocol)), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(appIdField));
                String ud = user + personRes.get(netType) + personRes.get(appProtocol);
                String wud = String.valueOf(personRes.get(netType)) + personRes.get(appProtocol);
                if (appList.contains(user) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!appList.contains(user) && allLineLast.containsKey(wud)) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>(8);
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "用户处理文件业务超出日常范围");
                item.put("obj_type", "p");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("app1->异常通信协议");
                break;
            }

        }

        /*String abnormalIndex = abnormalIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            QueryTools.writeData(abnormalList, abnormalIndex, wrapper);
        } catch (Exception e) {
        }*/

        // 计算最新基线，并入库
        //          应用名称、通信类型（对内、应用内部、外部）、协议、次数
        PageModel modelNew = new PageModel();
        setStartEndTime(interval, modelNew);
        EsQueryModel queryModeNew0 = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew0, "type", 0);
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        //      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSubSumAgg(queryModeNew0, wrapper, netType, 5000, appProtocol,
                new String[]{countField}, new String[]{countField}, netType);

        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSub2SumAgg(queryModeNew, wrapper, appIdField, 5000, netType, appProtocol,
                new String[]{countField}, new String[]{countField}, appIdField, new String[]{appNameField});

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put(netType, r.get(netType));
            item.put(appProtocol, r.get(appProtocol));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        //app_id,app_name,net_type(通信类型（对内、应用内部、外部）,具体存的数值还是文本未知?),app_protocol(协议),total_count(次数), start_time,end_time,interval,type(0=群体基线, 1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(appIdField, r.get(appIdField));
            item.put(appNameField, r.get(appNameField));
            item.put(netType, r.get(netType));
            item.put(appProtocol, r.get(appProtocol));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
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

    private void portList() {
        
        String netAuditPrefix2 = "netflow-http";

        String printAuditSummaryPrefix = "summary-app-port-audit";
        String baseLineIndexPrefix = "base-line-app-port";
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
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, netAuditPrefix2, "event_time", TimeTools.TIME_FMT_1, false);

        String appIdField = "app_id";
        String oriAppIdField = "std_sys_id";
        String appNameField = "app_name";
        String targetPortField = "dport";
        String portField = "dst_port";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、端口、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriAppIdField, targetPortField, 5000, 50000, countField,
                new String[]{appNameField});

        // 写入auditSummary
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(appIdField, r.get(oriAppIdField));
            item.put(appNameField, r.get(appNameField));
            item.put(portField, r.get(targetPortField));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        // 计算前一天总体用户打印设备清单，并入库 （协议）
        //  合计上述结果
        List<Map<String, Object>> allDevResultList = personResultList.stream().map(r -> r.get(portField)).distinct().map(r -> {
            Map<String, Object> item = new HashMap<>(5);
            item.put(portField, r);
            item.put("data_time", dataTime);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix+"-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "app_id", "business_type", "port", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
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
            List<String> appList = list.parallelStream().map(r -> r.get(appIdField)).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(appIdField)) + r.get(portField), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(portField)), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String appId = String.valueOf(personRes.get(appIdField));
                String ud = appId + personRes.get(portField);
                if (appList.contains(appId) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!appList.contains(appId) && allLineLast.containsKey(personRes.get(portField))) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "历史被访问端口应用异常");
                item.put("obj_type", "app");
                item.put("obj", appId);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("app2->新增服务端口");
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
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, portField, 5000, new String[]{countField},
                new String[]{countField}, portField, null);
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, appIdField, 5000, portField,
                new String[]{countField}, new String[]{countField}, appIdField, new String[]{appNameField});

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        // "username", "business_type", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put("port", r.get(portField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        //app_id,app_name,port(端口),total_count(次数), start_time,end_time,interval,type(0=群体基线, 1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(appIdField, r.get(appIdField));
            item.put(appNameField, r.get(appNameField));
            item.put("port", r.get(portField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void dstIpList() {

        String netAuditPrefix2 = "netflow-http";

        String printAuditSummaryPrefix = "summary-app-ip-audit";
        String baseLineIndexPrefix = "base-line-app-ip";
        Date currentTime = new Date();
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(yesterday);
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, netAuditPrefix2, "event_time", TimeTools.TIME_FMT_1, false);

        String appIdField = "app_id";
        String oriAppIdField = "std_sys_id";
        String appNameField = "app_name";
        String targetIPField = "dip";
        String ipField = "dst_ip";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、端口、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriAppIdField, targetIPField, 5000, 50000, countField,
                new String[]{appNameField});

        // 写入auditSummary
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(appIdField, r.get(oriAppIdField));
            item.put(appNameField, r.get(appNameField));
            item.put(ipField, r.get(targetIPField));
            item.put(countField, r.get(countField));
            item.put("data_time", dataTime);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix+"-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线
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
            List<String> appList = list.parallelStream().map(r -> r.get(appIdField)).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(appIdField)) + r.get(ipField), r -> r, (a, b) -> a));

            for (Map<String, Object> personRes : personResultList) {
                String appId = String.valueOf(personRes.get(appIdField));
                String ud = appId + personRes.get(ipField);
                if (appList.contains(appId) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!appList.contains(appId)) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "历史被访问端口应用异常");
                item.put("obj_type", "app");
                item.put("obj", appId);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("app6->历史被访问设备清单异常");
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
        /*//      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, ipField, 5000, new String[]{countField},
                new String[]{countField}, ipField, null);*/
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, appIdField, 5000, ipField,
                new String[]{countField}, new String[]{countField}, appIdField, new String[]{appNameField});

        //app_id,app_name,ip(目标IP),total_count(次数), start_time,end_time,interval,type,insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(appIdField, r.get(appIdField));
            item.put(appNameField, r.get(appNameField));
            item.put("ip", r.get(ipField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            //QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void businessList() {
        
        String fileIndex = "netflow-app-file";
        String printAuditSummaryPrefix = "summary-app-business-audit";
        String baseLineIndexPrefix = "base-line-app-business";
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

        String appIdField = "app_id";
        String oriAppIdField = "std_sys_id";
        String businessTypeField = "business_type";
        String businessListField = "business_list";
        String appNameField = "app_name";

        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （用户、业务类型、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriAppIdField, businessListField, 5000, 5000, countField, new
                String[]{appNameField});

        // 写入printAuditSummary
        //   "username", "business_type", "data_time", "insert_time", "type"
        personResultList = personResultList.stream().flatMap(r -> {
            List<Map<String, Object>> list = new ArrayList<>();
            for (String singleBusinessType : r.get(businessListField).toString().split(",")) {
                Map<String, Object> item = new HashMap<>(8);
                item.put(appIdField, r.get(oriAppIdField));
                item.put(appNameField, r.get(appNameField));
                item.put(businessTypeField, singleBusinessType);
                item.put(countField, r.get(countField));
                item.put("data_time", dataTime);
                item.put("type", 1);
                item.put("insert_time", currentTime);
                list.add(item);
            }
            return list.stream();
        }).collect(Collectors.toMap(r -> String.valueOf(r.get(appIdField)) + r.get(businessTypeField), r -> r, (a, b) -> {
            a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
            return a;
        })).values().stream().collect(Collectors.toList());

        // 计算前一天总体用户打印设备清单，并入库 （业务类型）
        //  合计上述结果
        List<Map<String, Object>> allDevResultList = personResultList.stream()
                .map(r -> {
                    Map<String, Object> allItem = new HashMap<>();
                    allItem.putAll(r);
                    allItem.put("type", 0);
                    allItem.remove(appIdField);
                    allItem.remove(appNameField);
                    return allItem;
                })
                .collect(Collectors.toMap(r -> String.valueOf(r.get(businessTypeField)), r -> r, (a, b) -> {
                    a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
                    return a;
                })).values().stream().collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix+"-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
            QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
        } catch (Exception e) {
        }

        // 对比基线分析个体用户是否异常，异常数据入库 （异常类型、主体类型、主体、异常描述, 日期）
        //     查询上一次的基线 "app_id", "business_type", "app_name", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
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
            List<String> userList = list.parallelStream().map(r -> r.get(appIdField)).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(appIdField)) + r.get(businessTypeField), r -> r, (a, b) -> a));
            Map<String, Map<String, String>> allLineLast = list.stream().filter(f -> "0".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(businessTypeField)), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String user = String.valueOf(personRes.get(appIdField));
                String ud = user + personRes.get(businessTypeField);
                if (userList.contains(user) && personLineLast.containsKey(ud)) {
                    continue;
                } else if (!userList.contains(user) && allLineLast.containsKey(personRes.get(businessTypeField))) {
                    continue;
                }
                //异常类型
                /*Map<String, Object> item = new HashMap<>();
                item.put("abnormal_type", "1");
                item.put("abnormal_desc", "应用超出历史处理文件业务范围");
                item.put("obj_type", "app");
                item.put("obj", user);
                item.put("data_time", dataTime);
                item.put("insert_time", currentTime);*/
                fillStrategy("app3->处理业务范围之外的文件");
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
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, businessTypeField, 5000, new String[]{countField},
                new String[]{countField}, businessTypeField, null);
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, appIdField, 5000, businessTypeField,
                new String[]{countField}, new String[]{countField}, appIdField, new String[]{appNameField});

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        // "app_id", "business_type", "app_name", "total_count", "start_time", "end_time", "interval", "insert_time", "type"
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put(businessTypeField, r.get(businessTypeField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 0);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        //app_id,app_name,business_type(业务类型),total_count(次数), start_time,end_time,interval,type(0=群体基线, 1=个体基线),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(appIdField, r.get(appIdField));
            item.put(appNameField, r.get(appNameField));
            item.put(businessTypeField, r.get(businessTypeField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
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
