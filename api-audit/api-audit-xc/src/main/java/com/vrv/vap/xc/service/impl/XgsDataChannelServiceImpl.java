package com.vrv.vap.xc.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.xc.config.XgsConfig;
import com.vrv.vap.xc.mapper.core.XgsDataChannelMapper;
import com.vrv.vap.xc.model.CascadeStrategyReceive;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.service.XgsDataChannelService;
import com.vrv.vap.xc.tools.AESUtil;
import com.vrv.vap.xc.tools.QueryTools;
import com.vrv.vap.xc.tools.QueryTools.QueryWrapper;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.config.SaslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * xgs摘要接口
 * xgs综合审计首页数据接口
 *
 * @author lil
 * @date 2021年3月26日
 */
@Service
public class XgsDataChannelServiceImpl implements XgsDataChannelService {
    private static final Log log = LogFactory.getLog(XgsDataChannelServiceImpl.class);

    @Autowired
    private XgsDataChannelMapper xgsDataChannelDao;
    @Autowired
    private XgsConfig xgsConfig;

    @Override
    public Map<String, Object> esDataSummary() {
        QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(0);
        queryModel.setCount(5);
        queryModel.setIndexNames(new String[]{"secaut_alert_*", "secaut_flow_*", "secaut_log_*"});
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(false);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHit(queryModel, wrapper, "number",
                100, "number", "count", new String[]{"name"});
        Map<String, Object> map = new HashMap<>();
        map.put("summary", list);
        map.put("id", xgsDataChannelDao.queryLocalId());
        return map;
    }

    @Override
    public Map<String, Object> dataCount() {
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> alertMap = this.dataCount("secaut_alert_*");
        Map<String, Object> flowMap = this.dataCount("secaut_flow_*");
        Map<String, Object> logMap = this.dataCount("secaut_log_*");
        result.put("alert", alertMap);
        result.put("flow", flowMap);
        result.put("log", logMap);
        return result;
    }

    private Map<String, Object> dataCount(String index) {
        QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(1));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(0));
        queryModel.setIndexNames(new String[]{index});
        queryModel.setUseFilter(false);
        queryModel.setTimeField("created");
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        List<Map<String, Object>> list = QueryTools.dateAgg(queryModel, wrapper, "created", DateHistogramInterval.DAY, "yyyy-MM-dd", 8, "date", "count");
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> tmp = new HashMap<>();
        String todayStr = TimeTools.format(new Date(), "yyyy-MM-dd");
        if (list == null || list.size() == 0) {
            result.put("today", 0);
            result.put("yesterday", 0);
        } else if (list.size() == 1) {
            tmp = list.get(0);
            if (todayStr.equals(tmp.get("date"))) {
                result.put("today", tmp.get("count"));
                result.put("yesterday", 0);
            } else {
                result.put("today", 0);
                result.put("yesterday", tmp.get("count"));
            }
        } else {
            for (Map<String, Object> m : list) {
                if (todayStr.equals(m.get("date"))) {
                    result.put("today", m.get("count"));
                } else {
                    result.put("yesterday", m.get("count"));
                }
            }
        }
        return result;
    }


    @Override
    public List<Map<String, Object>> dataLevel(PageModel model) {
        EsQueryModel queryModel = new EsQueryModel();
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        } else {
            queryModel.setStartTime(TimeTools.getNowBeforeByDay(0));
            queryModel.setEndTime(TimeTools.getNowBeforeByDay2(0));
        }
        queryModel.setTimeField("created");
        //queryModel.setIndexNames(new String[]{"secaut_alert_*", "secaut_flow_*", "secaut_log_*"});
        queryModel.setIndexName("secaut_alert_*");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.mustNot(QueryBuilders.termsQuery("level", "5","6","7"));
        queryModel.setQueryBuilder(queryBuilder);
        QueryWrapper wrapper = QueryTools.build();
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "level", 100, "level", "count");
        return list;
    }

    @Override
    public List<Map<String, Object>> dataKind(PageModel model) {
        EsQueryModel queryModel = new EsQueryModel();
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        } else {
            queryModel.setStartTime(TimeTools.getNowBeforeByDay(0));
            queryModel.setEndTime(TimeTools.getNowBeforeByDay2(0));
        }
        queryModel.setTimeField("created");
        queryModel.setIndexNames(new String[]{"secaut_alert_*", "secaut_flow_*", "secaut_log_*"});
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        QueryWrapper wrapper = QueryTools.build();
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "kind", 100, "kind", "count");
        return list;
    }

    @Override
    public List<Map<String, Object>> dataAlert(PageModel model) {
        QueryWrapper wrapper = QueryTools.build();
        List<Map<String, Object>> list = new ArrayList<>();
        String[] indes = new String[]{"sec_alert_config","sec_alert_campaign","sec_alert_malware","sec_alert_data"};
        for (String index : indes) {
            Map<String, Object> map = new HashMap<>();
            EsQueryModel queryModel = buildQueryModel2(index, model, wrapper);
            int total = QueryTools.getHits(queryModel, wrapper);
            map.put(index, total);
        }
        return list;
    }

    @Override
    public Map<String, Object> dataTop() {
        Map<String, Object> map = new HashMap<>();
        QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(0));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(0));
        queryModel.setTimeField("created");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        String[] indexs = new String[]{"secaut_alert_*", "secaut_flow_*", "secaut_log_*"};
        for (String index : indexs) {
            queryModel.setIndexName(index);
            List<Map<String, Object>> list = QueryTools.simpleAggWithTopHit(queryModel, wrapper, "number",
                5, "number", "count", new String[]{"name"});
            map.put(index.replace("_*", ""), list);
        }
        return map;
    }

    @Override
    public List<Map<String, Object>> dataTrend() {
        QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByHour(24));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(0));
        queryModel.setTimeField("created");
        queryModel.setIndexNames(new String[]{"secaut_alert_*", "secaut_flow_*", "secaut_log_*"});
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        List<Map<String, Object>> list = QueryTools.dateAgg(queryModel, wrapper, "created",
                DateHistogramInterval.HOUR, "yyyy-MM-dd HH", 8, "time", "count");
        return list;
    }

    @Override
    public void esDataSendKafka() {
        //读取可执行策略
        List<CascadeStrategyReceive> policies = xgsDataChannelDao.queryCascadeStrategy();
        if (!policies.isEmpty()) {
            KafkaProducer<String, String> producer = getKafkaProducer(policies.get(0).getKafka());
            if (producer != null) {
                producer.send(new ProducerRecord<>("test", "testConnect!!!!!!!"), (recordMetadata, e) -> {
                    log.error("testConnect kafka error => ", e);
                });
                String sid = xgsDataChannelDao.queryLocalId();
                for (CascadeStrategyReceive receive : policies) {
                    String index;
                    String topicName;
                    if (StringUtils.isEmpty(receive.getType())) {
                        continue;
                    }
                    switch (receive.getType()) {
                        case "flow":
                            index = "secaut_flow";
                            topicName = "cascade_flow";
                            break;
                        case "alert":
                            index = "secaut_alert";
                            topicName = "cascade_alert";
                            break;
                        case "log":
                            index = "secaut_log";
                            topicName = "cascade_log";
                            break;
                        default:
                            index = "";
                            topicName = "";
                    }
                    //log.info("index => " + index + " startTime => " + receive.getStartTime() + " endTime => " + receive.getEndTime());
                    if (!"".equals(index)) {
                        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                        queryBuilder.must(QueryBuilders.termQuery("number", receive.getNumber()));
                        if (0 != receive.getOpCode()) {
                            queryBuilder.must(QueryBuilders.termQuery("op_code", receive.getOpCode()));
                        }
                        if (0 != receive.getKind()) {
                            queryBuilder.must(QueryBuilders.termQuery("kind", receive.getKind()));
                        }
                        if (0 != receive.getLevel()) {
                            queryBuilder.must(QueryBuilders.termQuery("level", receive.getLevel()));
                        }
                        Date startTime = StringUtils.isNotEmpty(receive.getStartTime())
                                ? new Date(Long.valueOf(receive.getStartTime())) : TimeTools.parseDate2(xgsConfig.getStartTime());
                        //如果endTime为空，且执行过，startTime则去time_flag
                        if (StringUtils.isEmpty(receive.getEndTime()) && 1 == receive.getExecuteStatus()) {
                            startTime = TimeTools.parseDate2(receive.getTimeFlag());
                        }
                        Date endTime = StringUtils.isNotEmpty(receive.getEndTime())
                                ? new Date(Long.valueOf(receive.getEndTime())) : new Date();
                        log.info("ExecuteStatus => " + receive.getExecuteStatus() + " startTime => " + startTime
                                + " endTime => " + endTime);
                        EsQueryModel queryModel = buildQueryModel(index, startTime, endTime);
                        queryModel.setQueryBuilder(queryBuilder);
                        getEsDataSend(queryModel, topicName, receive, producer, sid);
                    }
                }
            }
            producer.close();
        }
    }

    @Override
    public List<Map<String, String>> dataAlertLastInfo(PageModel model) {
        QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setCount(model.getMyCount());
        queryModel.setStart(0);
        queryModel.setIndexName("secaut_alert_*");
        queryModel.setTimeField("created");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{"created"});
        queryModel.setSortOrder(SortOrder.DESC);
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        queryBuilder.must(QueryBuilders.termQuery("level", "1"));
        queryModel.setQueryBuilder(queryBuilder);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        if (response != null && response.getHits() != null) {
            return wrapper.wrapResponse(response.getHits(), "created");
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public void test() {
        String json = "{\"ip\":\"10.30.0.92\",\"port\":\"9092\",\"userName\":\"admin\",\"passWord\":\"`vrv@12345\"}";
        KafkaProducer<String, String> producer = getKafkaProducer(json);
        log.info("********* test start ******* ");
        for (int i =0;i<100;i++) {
            producer.send(new ProducerRecord<>("test", json));
        }
        log.info("********* test start ******* ");
    }

    private void getEsDataSend(EsQueryModel queryModel, String topicName, CascadeStrategyReceive receive,
                               KafkaProducer<String, String> producer, String sid) {
        Map<String, Object> map = new HashMap<>();
        if (StringUtils.isEmpty(queryModel.getIndexName()) && null == queryModel.getIndexNames()) {
            log.error("获取es数据失败，无匹配索引");
            map.put("time_flag", queryModel.getEndTime());
        } else {
            SearchResponse response = null;
            Date timeFLag = queryModel.getStartTime();

            while (true) {
                try {
                    response = QueryTools.build().scrollQuery(queryModel, response == null ? null : response.getScrollId());
                    if (null != response && null != response.getHits()) {
                        SearchHits hits = response.getHits();
                        log.info("************" + hits.getHits().length);
                        if (null != hits.getHits() && hits.getHits().length != 0) {
                            Map<String, Object> sendData = new HashMap<>();
                            sendData.put("id", sid);
                            sendData.put("sid", receive.getPuid());
                            List<Map<String, Object>> dataList = new ArrayList<>(10);
                            Map<String, Object> subMap = new HashMap<>();
                            for (SearchHit searchHit : hits.getHits()) {
                                Map<String, Object> tmpData = searchHit.getSourceAsMap();
                                Date created;
                                if (tmpData.get("created") instanceof Long) {
                                    created = new Date((Long) tmpData.get("created"));
                                } else {
                                    created = new Date(Long.valueOf((String) tmpData.get("created")));
                                }
                                if (created.after(timeFLag)) {
                                    timeFLag = created;
                                }
                                dataList.add(tmpData);
                                if (dataList.size() == xgsConfig.getDataBatchSize()) {
                                    subMap.put("count", dataList.size());
                                    subMap.put("data", dataList);
                                    sendData.put("raw", subMap);
                                    log.info("********* producer send start ******* ");
                                    producer.send(new ProducerRecord<>(topicName, JSONObject.toJSONString(sendData)));
                                    log.info("********* producer send end ******* ");
                                    subMap.clear();
                                    dataList.clear();
                                }
                            }
                            if (dataList.size() > 0) {
                                subMap.put("count", dataList.size());
                                subMap.put("data", dataList);
                                sendData.put("raw", subMap);
                                producer.send(new ProducerRecord<>(topicName, JSONObject.toJSONString(sendData)));
                            }
                            producer.flush();
                        } else {
                            break;
                        }
                    } else {
                        break;
                    }
                } catch (Exception e) {
                    log.error("======error========> ", e);
                }
            }

            map.put("time_flag", timeFLag);
        }
        map.put("pk", receive.getId());
        map.put("execute_status", 1);
        HashMap<String,Object> param = new HashMap<>();
        map.entrySet().forEach(s ->{
            param.put(s.getKey(),s.getValue());
        });
        xgsDataChannelDao.updateTimeFlag(param);
    }

    private KafkaProducer getKafkaProducer(String kafka) {
        //log.info("kafka info => " + kafka);
        Map<String, Object> kafkaInfo = JSONObject.parseObject(kafka, Map.class);

        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaInfo.get("ip") + ":" + kafkaInfo.get("port"));
        props.put(ProducerConfig.RETRIES_CONFIG, xgsConfig.getRetries());
        props.put(ProducerConfig.BATCH_SIZE_CONFIG, xgsConfig.getKafkaBatchSize());
        props.put(ProducerConfig.LINGER_MS_CONFIG, 1000);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT");
        props.put(SaslConfigs.SASL_MECHANISM, "PLAIN");

        String userName;
        String pwd;
        try {
            userName = AESUtil.Decrypt((String) kafkaInfo.get("userName"), (String) kafkaInfo.get("key"));
            pwd = AESUtil.Decrypt((String) kafkaInfo.get("passWord"), (String) kafkaInfo.get("key"));
            props.put(SaslConfigs.SASL_JAAS_CONFIG,
                    "org.apache.kafka.common.security.plain.PlainLoginModule required username=\"" + userName
                            + "\" password=\"" + pwd + "\";");
            log.error("userName => " + userName + " pwd => " + pwd);
            return new KafkaProducer<String, String>(props);
        } catch (Exception e) {
            log.error("", e);
            return null;
        }
    }

    private EsQueryModel buildQueryModel(String indexName, Date startTime, Date endTime) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setCount(10000);
        queryModel.setStart(0);
        queryModel.setStartTime(startTime);
        queryModel.setEndTime(endTime);
        //区分索引日期格式
        int dayOrMonth = indexName.equals("secaut_flow") ? 1 : 2;
        String timeFormat = indexName.equals("secaut_flow") ? "yyyyMMdd" : "yyyyMM";
        List<String> indexList = wrapper.getIndexNames(indexName, queryModel.getStartTime(), queryModel.getEndTime(),
                dayOrMonth, timeFormat, "${index}_${time}");
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField("created");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    private EsQueryModel buildQueryModel2(String indexName, PageModel model, QueryWrapper wrapper) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setCount(10);
        queryModel.setStart(0);
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        } else {
            queryModel.setStartTime(TimeTools.getNowBeforeByDay(0));
            queryModel.setEndTime(TimeTools.getNowBeforeByDay2(0));
        }
        List<String> indexList = wrapper.getIndexNames(indexName, queryModel.getStartTime(), queryModel.getEndTime(),
            2, "yyyy.MM", "${index}-${time}");
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField("event_time");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

}