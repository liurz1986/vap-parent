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
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用异常分析及基线分析
 * Created by cz on 2021/8/12
 */
public class ConnectAnomalyAnalysisTask extends BaseTask {

    private CommonService commonService = VapXcApplication.getApplicationContext().getBean(CommonService.class);
    private DataCleanMapper dataCleanDao = VapXcApplication.getApplicationContext().getBean(DataCleanMapper.class);
    //private final String indexSufFormat = "-yyyy.MM.dd";
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
        // 互联通信设备IP范围
        // -----------------------------------------------------
        unknownIpList();

        // -----------------------------------------------------
        // 互联通信IP对清单
        // -----------------------------------------------------
        ipEachOtherList();

        // -----------------------------------------------------
        // 互联通信协议清单
        // -----------------------------------------------------
        protocolList();

        // -----------------------------------------------------
        // 互联通信端口清单
        // -----------------------------------------------------
        portList();

        // -----------------------------------------------------
        // 互联通信输入文件范围
        // -----------------------------------------------------
        fileInList(2);

        // -----------------------------------------------------
        // 互联通信输出文件范围
        // -----------------------------------------------------
        fileInList(1);

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
            item.put("type", "互联互通异常");
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

    private void unknownIpList() {

        String netAuditPrefix = "netflow-tcp";
        String netAuditPrefix1 = "netflow-udp";
        String netAuditPrefix2 = "netflow-http";
        String netAuditPrefix3 = "netflow-dns";
        String netAuditPrefix4 = "netflow-email";
        String netAuditPrefix5 = "netflow-db";

        String printAuditSummaryPrefix = "summary-print-un-ip-audit";
        String baseLineIndexPrefix = "base-line-dev-ip";
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
        buildMutlQueryModel(wrapper, model, queryModel, netAuditPrefix1, netAuditPrefix2, netAuditPrefix3, netAuditPrefix4, netAuditPrefix5);

        String deviceIPField = "src_ip";
        String oriIPField = "sip";
        String orgField = "src_std_org_code";
        String countField = "count";
        // 计算前一天个体用户打印/刻录文件类型清单，并入库 （用户、打印/刻录文件次数、日期）
//        List<Map<String, Object>> personResultList = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, userIdField, 5000, new String[]{fileSizeField},
//                new String[]{fileSizeField}, userIdField, null);

        List<Map<String, Object>> personResultList = QueryTools.simpleAggAndTopHit(queryModel, wrapper, oriIPField, 5000, deviceIPField, countField, new String[]{orgField});

        // 计算前一天总体用户打印/刻录文件类型清单，并入库 （打印/刻录文件次数、日期）
        //  合计上述结果
        long totalCount = personResultList.stream().collect(Collectors.summarizingLong(r -> Long.valueOf(String.valueOf(r.get(countField))))).getSum();

        // 写入printAuditSummary
        //   "username", "count", "data_time", "insert_time", "type", "op_type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(5);
            item.put(deviceIPField, r.get(deviceIPField));
            item.put(orgField, r.get(orgField));
            item.put(countField, r.get(countField));
            item.put("type", 1);
            item.put("data_time", dataTime);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        List<Map<String, Object>> allDevResultList = new ArrayList<>();
        Map<String, Object> itemAll = new HashMap<>(4);
        itemAll.put(countField, totalCount);
        itemAll.put("type", 0);
        itemAll.put("data_time", dataTime);
        itemAll.put("insert_time", currentTime);
        allDevResultList.add(itemAll);

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
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(deviceIPField)), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String appId = String.valueOf(personRes.get(deviceIPField));
                if (personLineLast.containsKey(appId)) {
                    continue;
                }
                //异常类型
                fillStrategy("c1->互联边界存在非授权或者未知设备");
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
        setCondition(queryModeNew0, "type", 1);
        //      总体基线清单
        List<Map<String, Object>> baselineAll = QueryTools.simpleTermAndSumAgg(queryModeNew0, wrapper, deviceIPField, 5000, new String[]{countField},
                new String[]{countField}, deviceIPField, null);

        //用户A+ xx（个体基线）
        baselineAll = baselineAll.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(9);
            item.put("dev_ip", r.get(deviceIPField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            //item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());

        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselineAll, baseLineIndex, wrapper);
        } catch (Exception e) {
        }
    }

    private void setStartEndTime(int interval, PageModel model) {
        if (model.getMyStartTime() == null || model.getMyEndTime() == null) {
            model.setMyStartTime(TimeTools.getNowBeforeByDay(interval));
            model.setMyEndTime(TimeTools.getNowBeforeByDay2(1));
        }
    }

    private void ipEachOtherList() {


        String netAuditPrefix = "netflow-tcp";
        String netAuditPrefix1 = "netflow-udp";
        String netAuditPrefix2 = "netflow-http";
        String netAuditPrefix3 = "netflow-dns";
        String netAuditPrefix4 = "netflow-email";
        String netAuditPrefix5 = "netflow-db";


        String printAuditSummaryPrefix = "summary-ip-pair-audit";
        String baseLineIndexPrefix = "base-line-ip-pair";
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
        buildMutlQueryModel(wrapper, model, queryModel, netAuditPrefix1, netAuditPrefix2, netAuditPrefix3, netAuditPrefix4, netAuditPrefix5);

        String srcIpField = "src_ip";
        String oriSrcIpField = "sip";
        String srcOrgField = "src_org";
        String oriSrcOrgField = "src_std_org_code";
        String dstOrgField = "dst_org";
        String oriDstOrgField = "dst_std_org_code";

        String dstIpField = "dst_ip";
        String oriDstIpField = "dip";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、端口、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.twoLevelAgg(queryModel, wrapper, oriSrcIpField, oriDstIpField, 5000, 500, countField,
                new String[]{oriSrcOrgField, oriDstOrgField});

        // 写入printAuditSummary
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(srcIpField, r.get(oriSrcIpField));
            item.put(dstIpField, r.get(oriDstIpField));
            item.put(srcOrgField, r.get(oriSrcOrgField));
            item.put(dstOrgField, r.get(oriDstOrgField));
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
            //QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
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
            List<String> appList = list.parallelStream().map(r -> r.get(srcIpField)).distinct().collect(Collectors.toList());
            Map<String, Map<String, String>> personLineLast = list.stream().filter(f -> "1".equals(String.valueOf(f.get("type"))))
                    .collect(Collectors.toMap(r -> String.valueOf(r.get(srcIpField)) + r.get(dstIpField), r -> r, (a, b) -> a));
            for (Map<String, Object> personRes : personResultList) {
                String srcIp = String.valueOf(personRes.get(srcIpField));
                String ud = srcIp + personRes.get(dstIpField);
                if (personLineLast.containsKey(ud)) {
                    continue;
                }
                //异常类型
                fillStrategy("c2->互联边界存在异常通信关系（IP对）");
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
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        setCondition(queryModeNew, "type", 1);
        //      个体基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSubSumAgg(queryModeNew, wrapper, srcIpField, 5000, dstIpField,
                new String[]{countField}, new String[]{countField}, srcIpField, new String[]{srcOrgField});

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        //src_org(单位A),dst_org(单位B),src_ip,dst_ip,total_count(次数),start_time,end_time,interval(30, 即30天),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(srcIpField, r.get(srcIpField));
            item.put(srcOrgField, r.get(srcOrgField));
            item.put(dstIpField, r.get(dstIpField));
            item.put(dstOrgField, r.get(dstOrgField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            //item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }

    }

    private void protocolList() {

        String netAuditPrefix = "netflow-tcp";
        String netAuditPrefix1 = "netflow-udp";
        String netAuditPrefix2 = "netflow-http";
        String netAuditPrefix3 = "netflow-dns";
        String netAuditPrefix4 = "netflow-email";
        String netAuditPrefix5 = "netflow-db";

        String printAuditSummaryPrefix = "summary-protocol-relative-audit";
        String baseLineIndexPrefix = "base-line-ip-protocol-relative";
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
        buildMutlQueryModel(wrapper, model, queryModel, netAuditPrefix1, netAuditPrefix2, netAuditPrefix3, netAuditPrefix4, netAuditPrefix5);

        String srcOrgField = "src_org";
        String dstOrgField = "dst_org";
        String oriSrcOrgField = "src_std_org_code";
        String oriDstOrgField = "dst_std_org_code";
        String appProtocol = "app_protocol";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、端口、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.threeLevelAgg(queryModel, wrapper, oriSrcOrgField, oriDstOrgField, appProtocol, null,
                5000, 5000, 500, countField, null);

        // 写入printAuditSummary
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(srcOrgField, r.get(oriSrcOrgField));
            item.put(dstOrgField, r.get(oriDstOrgField));
            item.put(appProtocol, r.get(appProtocol));
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
            //QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
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
            List<String> existList = list.parallelStream().map(r -> String.valueOf(r.get(srcOrgField)) + r.get(dstOrgField) + r.get(appProtocol)).collect(Collectors.toList());
            for (Map<String, Object> personRes : personResultList) {
                String srcIp = String.valueOf(personRes.get(srcOrgField));
                String ud = srcIp + personRes.get(dstOrgField) + personRes.get(appProtocol);
                if (existList.contains(ud)) {
                    continue;
                }
                //异常类型
                fillStrategy("c3->互联边界存在异常访问协议");
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
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        //setCondition(queryModeNew, "type", 1);
        //       基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSub2SumAgg(queryModeNew, wrapper, srcOrgField, 5000, dstOrgField, appProtocol,
                new String[]{countField}, new String[]{countField}, srcOrgField, null);

        //用户A+ xx（个体基线）
        //* xx（群体基线）
        //src_org(单位A),dst_org(单位B),app_protocol(协议), total_count(次数),start_time,end_time,interval(30, 即30天),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(srcOrgField, r.get(srcOrgField));
            item.put(dstOrgField, r.get(dstOrgField));
            item.put(appProtocol, r.get(appProtocol));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            //item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }

    }

    private void portList() {

        String netAuditPrefix = "netflow-tcp";
        String netAuditPrefix1 = "netflow-udp";
        String netAuditPrefix2 = "netflow-http";
        String netAuditPrefix3 = "netflow-dns";
        String netAuditPrefix4 = "netflow-email";
        String netAuditPrefix5 = "netflow-db";

        String printAuditSummaryPrefix = "summary-port-relative-audit";
        String baseLineIndexPrefix = "base-line-ip-port-relative";
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
        buildMutlQueryModel(wrapper, model, queryModel, netAuditPrefix1, netAuditPrefix2, netAuditPrefix3, netAuditPrefix4, netAuditPrefix5);

        String srcOrgField = "src_org";
        String dstOrgField = "dst_org";
        String oriSrcOrgField = "src_std_org_code";
        String oriDstOrgField = "dst_std_org_code";
        String portField = "dst_port";
        String oriPortField = "dport";
        String countField = "count";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、端口、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.threeLevelAgg(queryModel, wrapper, oriSrcOrgField, oriDstOrgField, oriPortField, null,
                5000, 5000, 500, countField, null);

        // 写入printAuditSummary
        //   "username", "business_type", "data_time", "insert_time", "type"
        personResultList = personResultList.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(7);
            item.put(srcOrgField, r.get(oriSrcOrgField));
            item.put(dstOrgField, r.get(oriDstOrgField));
            item.put(portField, r.get(oriPortField));
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
            //QueryTools.writeData(allDevResultList, printAuditSummaryIndex, wrapper);
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
            List<String> existList = list.parallelStream().map(r -> String.valueOf(r.get(srcOrgField)) + r.get(dstOrgField) + r.get(portField)).collect(Collectors.toList());
            for (Map<String, Object> personRes : personResultList) {
                String srcIp = String.valueOf(personRes.get(srcOrgField));
                String ud = srcIp + personRes.get(dstOrgField) + personRes.get(portField);
                if (existList.contains(ud)) {
                    continue;
                }
                //异常类型
                fillStrategy("c4->互联边界存在异常访问端口");
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
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        //setCondition(queryModeNew, "type", 1);
        //       基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSub2SumAgg(queryModeNew, wrapper, srcOrgField, 5000, dstOrgField, portField,
                new String[]{countField}, new String[]{countField}, srcOrgField, null);

        //src_org(单位A),dst_org(单位B),dst_port(目标端口), total_count(次数),start_time,end_time,interval(30, 即30天),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(srcOrgField, r.get(srcOrgField));
            item.put(dstOrgField, r.get(dstOrgField));
            item.put(portField, r.get(portField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            //item.put("type", 1);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }

    }

    private void fileInList(int fileDir) {
        //int fileDir = 2; 上传/发送	1 ,下载/接收	2

        String printAuditPrefix = "netflow-app-file";

        String printAuditSummaryPrefix = "summary-file-in-relative-audit";
        String baseLineIndexPrefix = "base-line-file-in-out-business";
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
        // 传入或输出 即下载或上传
        setCondition(queryModel, "file_dir", fileDir);
        /*EsQueryModel fileQueryModel = QueryTools.buildQueryModel(wrapper, model, fileIndex, "event_time", TimeTools.TIME_FMT_1);
        List<String> auditIndexNames = new ArrayList<>();
        Collections.addAll(auditIndexNames, queryModel.getIndexNames());
        Collections.addAll(auditIndexNames, fileQueryModel.getIndexNames());
        queryModel.setIndexNames(auditIndexNames.toArray(new String[]{}));*/

        String srcOrgField = "src_org";
        String dstOrgField = "dst_org";
        String oriSrcOrgField = "src_std_org_code";
        String oriDstOrgField = "dst_std_org_code";
        String businessTypeField = "business_type";
        String businessListField = "business_list";
        String countField = "count";
        String fileDirField = "file_dir";
        // 计算前一天个体用户打印设备清单，并入库 （管理员、端口、日期、入库时间）
        List<Map<String, Object>> personResultList = QueryTools.threeLevelAgg(queryModel, wrapper, oriSrcOrgField, oriDstOrgField, businessListField, null,
                5000, 5000, 5000, countField, null);

        // 写入printAuditSummary
        personResultList = personResultList.stream().flatMap(r -> {
            List<Map<String, Object>> list = new ArrayList<>();
            // 业务类型分拆
            for (String singleBusinessType : r.get(businessListField).toString().split(",")) {
                Map<String, Object> item = new HashMap<>(10);
                item.put(srcOrgField, r.get(oriSrcOrgField));
                item.put(dstOrgField, r.get(oriDstOrgField));
                item.put(countField, r.get(countField));
                item.put(businessTypeField, singleBusinessType);
                item.put(fileDirField, fileDir);
                item.put("data_time", dataTime);
                item.put("type", 1);
                item.put("insert_time", currentTime);
                list.add(item);
            }
            return list.stream();
        }).collect(Collectors.toMap(r -> String.valueOf(r.get(srcOrgField)) + r.get(dstOrgField) + r.get(businessTypeField), r -> r, (a, b) -> {
            a.put(countField, Long.valueOf(String.valueOf(a.get(countField))) + Long.valueOf(String.valueOf(b.get(countField))));
            return a;
        })).values().stream().collect(Collectors.toList());

        String printAuditSummaryIndex = printAuditSummaryPrefix + TimeTools.format(yesterday, indexSufFormat);
        try {
            commonService.create365Alias(printAuditSummaryIndex, printAuditSummaryPrefix+"-", "data_time", "yyyy-MM-dd", dataTime.substring(0, 4), true);
            QueryTools.writeData(personResultList, printAuditSummaryIndex, wrapper);
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
            List<String> existList = list.parallelStream().map(r -> String.valueOf(r.get(srcOrgField)) + r.get(dstOrgField) + r.get(businessTypeField)).collect(Collectors.toList
                    ());
            for (Map<String, Object> personRes : personResultList) {
                String srcIp = String.valueOf(personRes.get(srcOrgField));
                String ud = srcIp + personRes.get(dstOrgField) + personRes.get(businessTypeField);
                if (existList.contains(ud)) {
                    continue;
                }
                //异常类型
                fillStrategy("c5->互联边界流转超出业务范围文件");
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
        EsQueryModel queryModeNew = QueryTools.buildQueryModel(wrapper, modelNew, printAuditSummaryPrefix, "data_time", "yyyy-MM-dd", false);
        //setCondition(queryModeNew, "type", 1);
        //       基线清单
        List<Map<String, Object>> baselinePerson = QueryTools.simpleTermAndSub2SumAgg(queryModeNew, wrapper, srcOrgField, 5000, dstOrgField, businessTypeField,
                new String[]{countField}, new String[]{countField}, srcOrgField, null);

        //用户A+ xx（个体基线）
        //src_org(单位A),dst_org(单位B),business_type(业务类型), total_count(次数),file_dir(1=输入/2=输出), start_time,end_time,interval(30, 即30天),insert_time
        baselinePerson = baselinePerson.stream().map(r -> {
            Map<String, Object> item = new HashMap<>(10);
            item.put(srcOrgField, r.get(srcOrgField));
            item.put(dstOrgField, r.get(dstOrgField));
            item.put(businessTypeField, r.get(businessTypeField));
            item.put("total_count", r.get(countField));
            item.put("start_time", modelNew.getMyStartTime());
            item.put("end_time", modelNew.getMyEndTime());
            item.put("interval", interval);
            //item.put("type", 1);
            item.put(fileDirField, fileDir);
            item.put("insert_time", currentTime);
            return item;
        }).collect(Collectors.toList());
        String baseLineIndex = baseLineIndexPrefix + TimeTools.format(currentTime, indexSufFormat);
        try {
            commonService.create365Alias(baseLineIndex, baseLineIndexPrefix+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
            QueryTools.writeData(baselinePerson, baseLineIndex, wrapper);
        } catch (Exception e) {
        }

    }

    private void setCondition(EsQueryModel queryModel4, String field, Object value) {
        BoolQueryBuilder query = (BoolQueryBuilder) queryModel4.getQueryBuilder();
        query.filter(QueryBuilders.termQuery(field, value));
    }
}
