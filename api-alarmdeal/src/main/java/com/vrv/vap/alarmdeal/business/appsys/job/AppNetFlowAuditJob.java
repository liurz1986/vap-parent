package com.vrv.vap.alarmdeal.business.appsys.job;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONException;
import cn.hutool.json.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmModel.model.WarnResultBaseLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.RiskEventRule;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.analysis.server.TbConfService;
import com.vrv.vap.alarmdeal.business.analysis.server.core.bean.WarnResultLogVo;
import com.vrv.vap.alarmdeal.business.appsys.model.AppResourceManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppResourceManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.util.PatternTools;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetDetailVO;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.es.service.ElasticSearchMapManage;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.common.IpUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.bytes.BytesReference;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.script.Script;
import org.elasticsearch.script.ScriptType;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedTopHits;
import org.elasticsearch.search.aggregations.metrics.TopHitsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Wrapper;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 应用系统存在异常入口页面登录分析
 */
@Component
public class AppNetFlowAuditJob implements SchedulingConfigurer {
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    private Logger logger = LoggerFactory.getLogger(AppNetFlowAuditJob.class);
    @Autowired
    private ElasticSearchMapManage elasticSearchMapManage;
    @Autowired
    private RestHighLevelClient restHighLevelClient;
    public static final String index = "netflow-http-*";
    public static final String ruleCode = "应用系统存在异常入口页面登录_02129a59e3b541c1296a48bb874cd3w2";
    public static final String event_index="app_abnormal_page";
    public static final String topicAias = "netflow-http";
    @Value("${netflowalarm.fiest:20}")
    private Integer minTime;
    @Autowired
    private AppResourceManageService appResourceManageService;
    @Autowired
    private AppSysManagerService appSysManagerService;
    @Autowired
    private TbConfService tbConfService;
    @Autowired
    private RiskEventRuleService riskEventRuleService;
    @Autowired
    private AssetService assetService;

    @Override
    public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
        scheduledTaskRegistrar.addTriggerTask(
                //1.添加任务内容(Runnable)
                () -> {
                    try {
                        appNetFlowAuditTask();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                },
                //2.设置执行周期(Trigger)
                triggerContext -> {
                    //2.1 从数据库获取执行周期
                    String cron = getProcessJob("appNetFlowAuditTask");
                    //2.3 返回执行周期(Date)
                    return new CronTrigger(cron).nextExecutionTime(triggerContext);
                }
        );
    }

    private String getProcessJob(String appNetFlowAuditTask) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("key", appNetFlowAuditTask));
        List<TbConf> all = tbConfService.findAll(queryConditions);
        if (all.size() > 0) {
            return all.get(0).getValue();
        }
        return "0 01 0 * * ?";
    }

    /**
     * 应用系统存在异常入口页面登录分析任务  netflow-http-*
     */
    public void appNetFlowAuditTask() throws Exception {
        //获取策略状态判断任务是否执行
        if (!getRiskEventRuleStatus(ruleCode)) {
            return;
        }
        logger.warn("--------------应用系统存在异常入口页面登录分析任务开始-----------------------------");
        List<Map<String, Object>> mapList = new ArrayList<>();
        //获取当天每个应用每个用户第一次的访问日志记录集合,且成功访问
        List<Map<String, Object>> netFlowVos = getFirstAppUserNetFlowsLog();
        for (Map<String, Object> map : netFlowVos) {
            //根据第一次的访问日志记录查询最近20分钟访问记录作为首访序列
            List<Map<String, Object>> firstSequences = getFirstSequence(map);
            //剔除序列当中的静态资源与空url
            List<Map<String, Object>> middleSequences = eliminateSequences(firstSequences);
            //比较应用入口与序列url，获取发送到kafka数据
            List<Map<String, Object>> sendSequences = compareSequences(middleSequences, map);
            mapList.addAll(sendSequences);
        }
        //告警和事件对象发送到kafka
        sendAlarmToKafka(mapList);
    }

    private Boolean getRiskEventRuleStatus(String ruleCode) {
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("ruleCode", ruleCode));
        List<RiskEventRule> all = riskEventRuleService.findAll(queryConditions);
        if (all.size() > 0) {
            RiskEventRule riskEventRule = all.get(0);
            if (riskEventRule.getStarted().equals("1")) {
                return true;
            }
        }
        return false;
    }

    private List<Map<String, Object>> compareSequences(List<Map<String, Object>> middleSequences, Map<String, Object> map) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        //查询对应应用系统资源去除http，端口后部分url链接集合
        List<String> appResourceUrls = findAppResource(map);
        if (middleSequences.size() > 0 && appResourceUrls.size() > 0) {
            for (Map<String, Object> objectMap : middleSequences) {
                //比较应用入口与序列url
                Boolean b = cheakSequencel(objectMap, appResourceUrls);
                if (!b) {
                    mapList.add(objectMap);
                }
            }
        }
        return mapList;
    }

    private Boolean cheakSequencel(Map<String, Object> objectMap, List<String> appResourceUrls) {
        String url = objectMap.get("url").toString();
        String s = dealUrl(url);
        if (appResourceUrls.contains(s)) {
            return true;
        }
        return false;
    }

    private List<String> findAppResource(Map<String, Object> map) {
        List<String> strings = new ArrayList<>();
        List<QueryCondition> queryConditionList = new ArrayList<>();
        queryConditionList.add(QueryCondition.eq("appNo", map.get("dst_std_sys_id")));
        List<AppSysManager> all1 = appSysManagerService.findAll(queryConditionList);
        if (all1.size() > 0) {
            AppSysManager appSysManager = all1.get(0);
            //修改为operation_url
//            List<QueryCondition> queryConditions = new ArrayList<>();
//            queryConditions.add(QueryCondition.eq("appId", appSysManager.getId()));
//            List<AppResourceManage> resourceManageServiceAll = appResourceManageService.findAll(queryConditions);
            if (appSysManager!=null&&StringUtils.isNotBlank(appSysManager.getOperationUrl())) {
                List<String> urls=new ArrayList<>();
                urls.add(appSysManager.getOperationUrl());
//                List<String> urls = resourceManageServiceAll.stream().map(r -> r.getAppResourceUrl()).collect(Collectors.toList());
                for (String s : urls) {
                    s = dealUrl(s);
                    strings.add(s);
                }
            }
        }
        return strings;
    }

    //过滤.png .gif .js  .css  静态资源
    private List<Map<String, Object>> eliminateSequences(List<Map<String, Object>> firstSequences) {
        String[] strings = new String[]{".png", ".gif", ".js", ".css", ".jpg", ".jsp", ".ico"};
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map<String, Object> map : firstSequences) {
            boolean b = false;
            Object url = map.get("url");
            if (url != null) {
                String urlString = url.toString();
                for (String s : strings) {
                    if (urlString.contains(s)) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    mapList.add(map);
                }
            }
        }
        return mapList;

    }

    //根据第一次的访问日志记录查询最近20分钟访问记录作为首访序列
    private List<Map<String, Object>> getFirstSequence(Map<String, Object> map) throws Exception {
        String time = map.get("time").toString();
        Date startTimeStr = DateUtil.parseDate(time, DateUtil.UTC_TIME);
        Date nowBeforeByMinute = DateUtil.addMinutes(startTimeStr, minTime);
        String endTimeStr = DateUtil.format(nowBeforeByMinute, DateUtil.UTC_TIME);
        List<QueryCondition_ES> queryConditionEs = getConditionEsList(time, endTimeStr);
        queryConditionEs.add(QueryCondition_ES.eq("dst_std_sys_id", map.get("dst_std_sys_id")));
        queryConditionEs.add(QueryCondition_ES.eq("src_std_user_no", map.get("src_std_user_no")));
        List<Map<String, Object>> all = elasticSearchMapManage.findAll(index, queryConditionEs);
        return all;
    }

    //获取当天每个应用每个用户第一次的访问日志记录集合
    private List<Map<String, Object>> getFirstAppUserNetFlowsLog() {
        Date startTime = getNowBeforeByDay(1);
        Date endTime = getNowBeforeByDay2(1);
        String startTimeSt = DateUtil.format(startTime, DateUtil.UTC_TIME);
        String endTimeSt = DateUtil.format(endTime, DateUtil.UTC_TIME);
        List<QueryCondition_ES> queryConditionEsList = getConditionEsList(startTimeSt, endTimeSt);
        //额外查询字段
        String[] strings = new String[]{"time", "src_std_user_no", "dst_std_sys_id"};
        List<Map<String, Object>> netFlowVos = getCountGroupByFields(index, "src_std_user_no", "dst_std_sys_id", queryConditionEsList, strings, "time");
        return netFlowVos;
    }

    private String dealUrl(String url) {
        //截取url ？去掉url参数
        if (url.contains("?")) {
            url = url.split("\\?")[0];
        }
        //判断是否包含http,如果过包含只截取请求资源部分 /upload/xxxx
        if (url.contains("http")) {
            url = PatternTools.patternCheak(url);
        }
        return url;
    }

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private void sendAlarmToKafka(List<Map<String, Object>> netFlowVos) {
        if (netFlowVos.size() > 0) {
            for (Map<String, Object> map : netFlowVos) {
                try {
                    WarnResultBaseLogTmpVO warnResultLogVO = new WarnResultBaseLogTmpVO();
                    String string = UUID.randomUUID().toString();
                    warnResultLogVO.setResultGuid(string+"|"+event_index);
                    warnResultLogVO.setRuleCode(ruleCode);
                    warnResultLogVO.setSrc_ips(map.get("sip") == null ? "" : map.get("sip").toString());
                    warnResultLogVO.setDstIps(map.get("dip") == null ? "" : map.get("dip").toString());
                    warnResultLogVO.setSrc_ports(map.get("sport") == null ? "" : map.get("sport").toString());
                    warnResultLogVO.setDst_ports(map.get("dport") == null ? "" : map.get("dport").toString());
                    warnResultLogVO.setRelatedIps(map.get("sip") == null ? "" : map.get("sip").toString());
                    String format = "";
                    if (map.get("time") != null) {
                        String time = map.get("time").toString();
                        format = DateUtil.parseUTC(time);
                    }
                    String[] strings = new String[]{map.get("guid").toString()};
                    Map<String, String[]> idRoom = new HashMap<>();
                    idRoom.put(topicAias, strings);
                    warnResultLogVO.setIdRoom(idRoom);
                    //构建描述与触发条件
                    structurePrincipleAndalamDesc(warnResultLogVO,map);
                    String json = gson.toJson(warnResultLogVO);
                    JSONObject object = new JSONObject(json);
                    if (StringUtils.isNotBlank(format)) {
                        object.put("triggerTime", format);
                    }
                    //发送数据到kafka
                    kafkaTemplate.send("flink-wiki-demo", object.toString());
                    //构建事件对象
                    JSONObject event=structureEvent(map,string);
                    System.out.println(event.toString());
                    kafkaTemplate.send("event-type-topic", event.toString());
                    logger.info("异常入口页面登录url:" + object);
                } catch (JSONException e) {
                    logger.info("发送kafka数据失败");
                    throw new RuntimeException(e);
                }
            }
        }
    }
    private void structurePrincipleAndalamDesc(WarnResultBaseLogTmpVO warnResultLogVO, Map<String, Object> map) {
        String principle="在event_time，发现sip通过非正常入口页面：url成功直接访问应用系统：sysAppNamedip的系统资源";
        principle=principle.replace("event_time",map.get("event_time") == null ? "" : map.get("event_time").toString());
        principle=principle.replace("sip",map.get("sip") == null ? "" : map.get("sip").toString());
        principle=principle.replace("url;",map.get("url") == null ? "" : map.get("url").toString());
        principle=principle.replace("sysAppName",map.get("dst_std_sys_name") == null ? "" : map.get("dst_std_sys_name").toString());
        principle=principle.replace("dip",map.get("dip") == null ? "" : map.get("dip").toString());
        warnResultLogVO.setPrinciple(principle);
        String alamDesc="在event_time，orgName部门用户user名下应用系统：sysAppName，被sip通过非正常入口页面：url成功直接访问系统资源，触发了应用系统存在异常入口页面登录";
        alamDesc=alamDesc.replace("event_time",map.get("event_time") == null ? "" :map.get("event_time").toString());
        alamDesc=alamDesc.replace("sip",map.get("sip") == null ? "" :map.get("sip").toString());
        alamDesc=alamDesc.replace("url",map.get("url") == null ? "" :map.get("url").toString());
        alamDesc=alamDesc.replace("sysAppName",map.get("dst_std_sys_name") == null ? "" :map.get("dst_std_sys_name").toString());
        if (map.get("sip")!=null){
            AssetDetailVO sip = assetService.getOneAssetDetailByIp(map.get("sip").toString());
            if (sip!=null){
                Asset asset = sip.getAsset();
                if (asset!=null){
                    alamDesc=alamDesc.replace("user",asset.getResponsibleName());
                    alamDesc= alamDesc.replace("orgName",asset.getOrgName());
                }
            }
        }
        warnResultLogVO.setAlamDesc(alamDesc);
    }

    private JSONObject structureEvent(Map<String, Object> map, String string) {
        Map<String,Object> jsonObjectMap=new HashMap<>();
        jsonObjectMap.put("indexName",event_index);
        Map<String,Object> content=new HashMap<>();
        content.put("resultGuid",string);
        content.put("app_name",map.get("dst_std_sys_name"));
        if (map.get("dip")!=null){
            content.put("app_id",map.get("dip"));
            AssetDetailVO dip = assetService.getOneAssetDetailByIp(map.get("dip").toString());
            if (dip!=null){
                Asset asset = dip.getAsset();
                if (asset!=null){
                    content.put("app_user",asset.getResponsibleName());
                    content.put("department",asset.getOrgName());
                }
            }
            if (map.get("sip")!=null){
                AssetDetailVO sip = assetService.getOneAssetDetailByIp(map.get("sip").toString());
                if (sip!=null){
                    Asset asset = sip.getAsset();
                    if (asset!=null){
                        content.put("src_ip",map.get("sip"));
                        content.put("src_user",asset.getResponsibleName());
                        content.put("src_department",asset.getOrgName());
                    }
                }
            }
            content.put("src_security_level",map.get("src_std_dev_level"));
            List<QueryCondition> queryConditionList = new ArrayList<>();
            queryConditionList.add(QueryCondition.eq("appNo", map.get("dst_std_sys_id")));
            List<AppSysManager> all1 = appSysManagerService.findAll(queryConditionList);
            if (all1.size()>0){
                content.put("app_entry_address",all1.get(0).getOperationUrl());
                content.put("app_security_level",all1.get(0).getSecretLevel());
            }
            content.put("visit_time",map.get("event_time"));
            List<String> strings=new ArrayList<>();
            strings.add(map.get("url").toString());
            content.put("visit_url_list",strings);
        }
        JSONObject jsonObject = new JSONObject(content);
        jsonObjectMap.put("content",jsonObject.toString());
        JSONObject object = new JSONObject(jsonObjectMap);
        return object;
    }


    public String getIndexName() {
        return index;
    }

    private List<Map<String, Object>> getCountGroupByFields(String index, String fildGroup1, String fildGroup2, List<QueryCondition_ES> queryConditionEsList, String[] filed, String sortFeile) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.notNull(fildGroup1));
        querys.add(QueryCondition_ES.notNull(fildGroup2));
        if (CollectionUtils.isNotEmpty(queryConditionEsList)) {
            querys.addAll(queryConditionEsList);
        }
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        Script queryScript = initScript(new String[]{fildGroup1});
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").script(queryScript).size(10000);
        TermsAggregationBuilder group_by_field2 = AggregationBuilders.terms("group_by_field2").field(fildGroup2);
        TopHitsAggregationBuilder sample = AggregationBuilders.topHits("sample").size(1);
        if (StringUtils.isNotBlank(sortFeile)) {
            sample.sort(sortFeile, SortOrder.ASC);
        }
        if (filed.length > 0) {
            sample.fetchSource(filed, null);
        }
        group_by_field2.subAggregation(sample);
        groupByFieldAgg.subAggregation(group_by_field2);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);
        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.query(queryBuilder);
        // 查询条件
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(index);
        searchRequest.source(searchSourceBuilder);
        try {
            SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            if (searchResponse != null && searchResponse.getAggregations() != null) {
                Terms terms = searchResponse.getAggregations().get("group_by_field");
                if (terms.getBuckets().size() != 0) {
                    for (int i = 0; i < terms.getBuckets().size(); i++) {
                        Terms terms1 = terms.getBuckets().get(i).getAggregations().get("group_by_field2");
                        for (int j = 0; j < terms1.getBuckets().size(); j++) {
                            ParsedTopHits sample1 = terms1.getBuckets().get(j).getAggregations().get("sample");
                            SearchHits hits = sample1.getHits();
                            SearchHit[] hits1 = hits.getHits();
                            SearchHit searchHits = hits1[0];
                            Map<String, Object> sourceAsMap = searchHits.getSourceAsMap();
                            mapList.add(sourceAsMap);
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mapList;
    }

    private List<QueryCondition_ES> getConditionEsList(String startTimeSt, String endTimeSt) {
        List<QueryCondition_ES> queryConditionEsList = new ArrayList<>();
        queryConditionEsList.add(QueryCondition_ES.notNull("guid"));
        queryConditionEsList.add(QueryCondition_ES.ge("time", startTimeSt));
        queryConditionEsList.add(QueryCondition_ES.le("time", endTimeSt));
        queryConditionEsList.add(QueryCondition_ES.le("http_res_code",300));
//        queryConditionEsList.add(QueryCondition_ES.not(QueryCondition_ES.likeBegin("http_res_code","3")));
//        queryConditionEsList.add(QueryCondition_ES.not(QueryCondition_ES.likeBegin("http_res_code","4")));
//        queryConditionEsList.add(QueryCondition_ES.not(QueryCondition_ES.likeBegin("http_res_code","5")));
        return queryConditionEsList;
    }

    public static Script initScript(String[] groupByFields) {
        /**
         * 分隔符
         */
        final String spiltSymbol = "@#@#@";
        // 定义script
        StringBuilder scriptBuilder = new StringBuilder();
        for (int i = 0; i < groupByFields.length; i++) {
            if (i != 0) {
                scriptBuilder.append("+'" + spiltSymbol + "'+");
            }
            scriptBuilder.append(String.format("doc['%s'].value", groupByFields[i]));
        }
        return new Script(ScriptType.INLINE, "painless", scriptBuilder.toString(), new HashMap<>(0));
    }

    public static Date getNowBeforeByDay(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前n天前的时间（结束时间）
     *
     * @return
     * @int day
     */
    public static Date getNowBeforeByDay2(int day) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DATE, (0 - day));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
