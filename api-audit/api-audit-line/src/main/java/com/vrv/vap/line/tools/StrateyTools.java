package com.vrv.vap.line.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.client.RedisCacheTools;
import com.vrv.vap.line.config.MessageConfig;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.fegin.AdminFeign;
import com.vrv.vap.line.fegin.ApiDataClient;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.mapper.BaseLineResultMapper;
import com.vrv.vap.line.mapper.BaseLineSpecialMapper;
import com.vrv.vap.line.mapper.StrategyConfigMapper;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.schedule.task.StrategyTask;
import com.vrv.vap.line.service.CommonService;
import com.vrv.vap.line.service.KafkaSenderService;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.VData;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.joda.time.DateTimeZone;
import org.quartz.JobDataMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.annotation.Resource;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.util.*;
import java.util.stream.Collectors;

public class StrateyTools {
    private static Environment env = VapLineApplication.getApplicationContext().getBean(Environment.class);
    //private BaseLineSourceMapper baseLineSourceMapper = VapXcSchedularApplication.getApplicationContext().getBean(BaseLineSourceMapper.class);
    private BaseLineMapper baseLineMapper = VapLineApplication.getApplicationContext().getBean(BaseLineMapper.class);
    private CommonService commonService = VapLineApplication.getApplicationContext().getBean(CommonService.class);
    //private BaseLineSourceFieldService baseLineSourceFieldService = VapXcSchedularApplication.getApplicationContext().getBean(BaseLineSourceFieldService.class);
    private KafkaSenderService kafkaSenderService = VapLineApplication.getApplicationContext().getBean(KafkaSenderService.class);
    private RedisCacheTools redisCacheTools = VapLineApplication.getApplicationContext().getBean(RedisCacheTools.class);
    private BaseLineResultMapper baseLineResultMapper = VapLineApplication.getApplicationContext().getBean(BaseLineResultMapper.class);
    private ApiDataClient apiDataClient = VapLineApplication.getApplicationContext().getBean(ApiDataClient.class);
    private BaseLineSpecialMapper baseLineSpecialMapper = VapLineApplication.getApplicationContext().getBean(BaseLineSpecialMapper.class);
    private MessageConfig messageConfig = VapLineApplication.getApplicationContext().getBean(MessageConfig.class);
    private StrategyConfigMapper strategyConfigMapper = VapLineApplication.getApplicationContext().getBean(StrategyConfigMapper.class);
    private AdminFeign adminFeign = VapLineApplication.getApplicationContext().getBean(AdminFeign.class);
    private RedisTemplate redisTemplate = VapLineApplication.getApplicationContext().getBean(StringRedisTemplate.class);
    private static final String CACHE_PERSON_ZJG_KEY = "_BASEINFO:BASE_PERSON_ZJG:ALL";
    private String PRE_CN = "_";
    private String PRE_SM = LineConstants.NAME_PRE.PRE_SM;
    private String PRE_LINE = LineConstants.NAME_PRE.PRE_LINE;
    private String TIME_FIELD = "insert_time";
    private final String indexSufFormat = "-yyyy";
    private static final Log log = LogFactory.getLog(StrategyTask.class);
    private String MESSAGE_TOPIC = "filter-data-baseline";
    private String FIELD_NAME = "暂无";
    private String SOURCE_NAME_END = "-*";
    private String ALARM_TOPIC = "flink-wiki-demo";
    private String ALARM_EVENT = "event-type-topic";

    public void run(int id) {
        log.info("策略配置离线任务开始："+id);
        int d = 0;
        LineTaskRun lineUtil = new LineTaskRun();
        StrategyConfig line = strategyConfigMapper.selectById(id);
        if("1".equals(line.getCustom()) && StringUtils.isNotEmpty(line.getCustomClass())){
            Map<String,Object> params = new HashMap<>();
            params.put("id",id);
            try{
                new Special4JavaTools().execute(params,line.getCustomClass());
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            return;
        }
        int day = line.getDay();
        if(day == 0){
            day = 1;
        }
        Date currentTime = new Date();
        List<Map<String, Object>> personList = null;
        Date yesterday = MyTimeTools.getDateBeforeByDay(currentTime, 1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        //解析基线
        if (StringUtils.isEmpty(line.getConfig())) {
            throw new RuntimeException("无配置");
        }
        //es类型
        JSONArray arrays = JSONArray.parseArray(line.getConfig());
        List<Map<String, Object>> result = new ArrayList<>();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        String alias = line.getAlias();
        JSONArray colsArray = JSONArray.parseArray(line.getSaveColumns());
        Map<String, String> resultMap = colsArray.stream().collect(Collectors.toMap(i -> {
            JSONObject a = (JSONObject) i;
            String k = a.getString("src");
            return k;
        }, i -> {
            JSONObject a = (JSONObject) i;
            String k = a.getString("dest");
            return k;
        }));
        //计算中间结果
        StringBuffer names = new StringBuffer();
        arrays.forEach(e -> {
            JSONObject obj = (JSONObject) e;
            Integer index = obj.getInteger("indexId");
            BaseLineSource source = lineUtil.getData(index);
            names.append(source.getName()).append(",");
        });
        JSONObject obj = (JSONObject) arrays.get(0);
        Integer index = obj.getInteger("indexId");
        BaseLineSource source = lineUtil.getData(index);
        final EsQueryModel queryModel = lineUtil.buildQueryModelByNames(wrapper, names.toString().split(","), source.getTimeField(), TimeTools.TIME_FMT_1, day, d + 1);
        String column = obj.getString("column");
        List<Map<String, String>> mappings = buildQueryAgg(queryModel, obj, source, alias, "2");
        log.info("#####mappings：" + JSONObject.toJSONString(mappings));
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        JSONObject aggMapJosn = JSONObject.parseObject(JSONObject.toJSONString(aggMap));
        int total = aggMapJosn.getJSONObject("hits").getJSONObject("total").getIntValue("value");
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            log.info("#####es结果：" + JSONObject.toJSONString(dataAggMap));
            List<Map<String, Object>> list = ResultUtils.spreadAggregationAsList(dataAggMap,column);
            log.info("#####list结果：" + JSONObject.toJSONString(list));
            if (CollectionUtils.isNotEmpty(list)) {
                list.forEach(da -> {
                    Map<String, Object> item = new HashMap<>();
                    mappings.forEach(m -> {
                        if (m.containsKey("key") && m.containsKey("alias")) {
                            String aKey = resultMap.get(m.get("alias"));
                            if (aKey == null) {
                                return;
                            }
                            item.put(aKey, da.get(m.get("key")));
                        }
                    });
                    item.put("total",total);
                    item.put("insert_time", currentTime);
                    item.put("data_time", dataTime);
                    item.put("guid", UUID.randomUUID());
                    result.add(item);
                });
            }
        }
        //规则对比
        JSONObject configObj = JSONArray.parseArray(line.getConfig(),JSONObject.class).get(0);
        List<String> tremsFields = parseTermFields(configObj);
        if(StringUtils.isNotEmpty(line.getContrast())){
            JSONObject contrast = JSONObject.parseObject(line.getContrast());
            if(contrast.containsKey("local")){
                //当前统计数据对比
                for(Map<String, Object> item : result){
                    String script = contrast.getString("local");
                    script = buildScript(item,script);
                    if(executeScript(script)){
                        //匹配上推送告警
                        //组装查询条件 查询原始记录id
                        EsQueryModel model = copyByModel(wrapper, queryModel);
                        BoolQueryBuilder builder = buildCommonQueryBuilder(configObj);
                        for(String field : tremsFields){
                            Object value = item.get(resultMap.get(field));
                            if(value != null)
                                builder.must(QueryBuilders.termQuery(field, value));
                        }
                        model.setQueryBuilder(builder);
                        SearchResponse searchResponse = null;
                        List<String> ids = new ArrayList<>();
                        Map<String,String> data = null;
                        while (true) {
                            searchResponse = wrapper.scrollQuery(model, searchResponse == null ? null : searchResponse.getScrollId());
                            SearchHits hits = searchResponse.getHits();
                            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), source.getTimeField());
                            if (hits.getHits() == null || hits.getHits().length == 0) {
                                break;
                            }
                            if(data == null){
                                data = list.get(0);
                            }
                            if(CollectionUtils.isNotEmpty(list)){
                                ids.addAll(list.stream().map(m ->{
                                    return m.get("_id");
                                }).collect(Collectors.toList()));
                            }
                        }
                        //推送告警
                        push2alarm(line.getRuleCode(),data,ids);
                    }
                }
            }else if(contrast.containsKey("line")){
                //与基线数据对比
                JSONObject lineConfig = contrast.getJSONObject("line");
                int lineId = lineConfig.getIntValue("id");
                BaseLine baseLine = baseLineMapper.selectById(lineId);
                String table = (LineConstants.NAME_PRE.PRE_LINE+baseLine.getSaveIndex()).replaceAll("-","_");
                //查询基线数据
                Map<String,Object> param = new HashMap<>();
                param.put("table",table);
                param.put("startTime",TimeTools.format2(TimeTools.getNowBeforeByDay(1)));
                param.put("endTime",TimeTools.format2(TimeTools.getNowBeforeByDay2(1)));
                List<Map<String, Object>> maps = baseLineMapper.selectLineData(param);
                JSONArray joins = lineConfig.getJSONArray("join");
                List<String> lineField = new ArrayList<>();
                List<String> dataField = new ArrayList<>();
                for(Object o : joins){
                    JSONObject vj = JSONObject.parseObject(o.toString());
                    lineField.add(vj.getString("src"));
                    dataField.add(vj.getString("dst"));
                }
                Map<String, Map<String, Object>> lineDataMap = maps.stream().collect(Collectors.toMap(r -> buildKeyByField(r, lineField), r -> r));
                //数据对比
                for(Map<String, Object> item : result){
                    String key = buildKeyByField(item,dataField);
                    if(lineDataMap.containsKey(key)){
                        Map<String, Object> lineDataItem = lineDataMap.get(key);
                        lineDataItem.putAll(item);
                        String script = lineConfig.getString("script");
                        script = buildScript(lineDataItem,script);
                        if(executeScript(script)){
                            //匹配上推送告警
                            EsQueryModel model = copyByModel(wrapper, queryModel);
                            BoolQueryBuilder builder = buildCommonQueryBuilder(configObj);
                            for(String field : tremsFields){
                                Object value = item.get(resultMap.get(field));
                                if(value != null)
                                    builder.must(QueryBuilders.termQuery(field, value));
                            }
                            model.setQueryBuilder(builder);
                            SearchResponse searchResponse = null;
                            List<String> ids = new ArrayList<>();
                            Map<String,String> data = null;
                            while (true) {
                                searchResponse = wrapper.scrollQuery(model, searchResponse == null ? null : searchResponse.getScrollId());
                                SearchHits hits = searchResponse.getHits();
                                List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), source.getTimeField());
                                if (hits.getHits() == null || hits.getHits().length == 0) {
                                    break;
                                }
                                if(data == null){
                                    data = list.get(0);
                                }
                                if(CollectionUtils.isNotEmpty(list)){
                                    ids.addAll(list.stream().map(m ->{
                                        return m.get("_id");
                                    }).collect(Collectors.toList()));
                                }
                            }
                            //推送告警
                            push2alarm(line.getRuleCode(),data,ids);
                        }
                    }
                }
            }
        }
    }

    public String buildScript(Map<String,Object> item,String script){
        String result = script;
        for(Map.Entry<String, Object> e : item.entrySet()) {
            result = result.replaceAll(e.getKey(),e.getValue().toString());
        }
        return result;
    }

    public String buildKeyByField(Map<String,Object> map,List<String> fields){
        StringBuilder key = new StringBuilder();
        for(String field : fields){
            key.append(map.get(field));
        }
        return key.toString();
    }

    public boolean executeScript(String script){
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("js");
        boolean flag = false;
        try{
            Object result = engine.eval(script);
            flag = Boolean.valueOf(result.toString());
        }catch (Exception exp){
            throw new RuntimeException(exp.getMessage());
        }
        return flag;
    }

    private List<Map<String, String>> buildQueryAgg (EsQueryModel queryModel, JSONObject obj, BaseLineSource
            source, String alias, String timeSolt){
        List<Map<String, String>> keyList = new ArrayList<>();//key映射集合
        Map<String, String> mainKey = new HashMap<>();
        Map<String, String> mainDocKey = new HashMap<>();
        mainKey.put("alias", alias);
        mainKey.put("key", "key");
        mainDocKey.put("alias", alias + "_doc_count");
        mainDocKey.put("key", "doc_count");
        keyList.add(mainKey);
        keyList.add(mainDocKey);
        String interv = "";
        String dataFormat = "";
        if ("2".equals(timeSolt)) {
            interv = "1d";
            dataFormat = "yyyy-MM-dd";
        } else {
            interv = "1h";
            dataFormat = "H";
        }
        String column = obj.getString("column");
        queryModel.setUseAggre(true);
        JSONArray aggs = obj.getJSONArray("calculation");
        queryModel.setQueryBuilder(buildCommonQueryBuilder(obj));
        AbstractAggregationBuilder pre = null;
        if(!"无".equals(column)){
            pre = AggregationBuilders.terms(LineConstants.AGG_NAME.TERMS + PRE_CN + column).field(column).size(100000);
        }
        if (aggs == null) {
            return keyList;
        }
        List<SourceField> sourceFields = this.apiDataClient.getFields(Integer.parseInt(source.getId())).getData();
        Map<String, String> aliaMaps = new HashMap<>();
        sourceFields.forEach(e -> {
            if (StringUtils.isEmpty(e.getAlias())) {
                aliaMaps.put(e.getField(), e.getField());
            } else {
                aliaMaps.put(e.getField(), e.getAlias());
            }
        });
        boolean top = false;
        //取出非top算法 （top算法放在最后一层）
        List<Object> faggs = aggs.stream().filter(s -> (!LineConstants.AGG_TYPE.TOP.equals(((JSONObject) s).getString("algorithm")))).collect(Collectors.toList());
        //取出top算法
        List<Object> topaggs = aggs.stream().filter(s -> (LineConstants.AGG_TYPE.TOP.equals(((JSONObject) s).getString("algorithm")))).collect(Collectors.toList());
        //按聚合层级排序
        faggs.sort(Comparator.comparing(b -> ((JSONObject) b).getIntValue("aggLevel")));
        if (CollectionUtils.isNotEmpty(topaggs)) {
            //存在top算法
            top = true;
        }
        //记录上一次分桶聚合
        AbstractAggregationBuilder preTerms = null;
        int level = 0;
        String prePath = "";
        String endPath = "";
        String preCol = "";
        String cn = "";
        for (Object e : faggs) {
            JSONObject b = (JSONObject) e;
            //取出算法
            String algorithm = b.getString("algorithm");
            //拼key映射关系
            String col = b.getString("column");
            Map<String, String> keys = new HashMap<>();
            keys.put("alias", aliaMaps.get(col));
            //拼聚合层级
            int aggLevel = b.getIntValue("aggLevel");
            AbstractAggregationBuilder builder = null;
            if (StringUtils.isNotEmpty(prePath)) {
                cn = "_";
            }
            if (preTerms != null && aggLevel > level) {
                pre = preTerms;
                prePath = prePath + cn + LineConstants.AGG_NAME.TERMS + PRE_CN + preCol + "_buckets";
            }
            if (StringUtils.isNotEmpty(prePath)) {
                cn = "_";
            }
            switch (algorithm) {
                case LineConstants.AGG_TYPE.TERMS:
                    builder = AggregationBuilders.terms(LineConstants.AGG_NAME.TERMS + PRE_CN + col).field(col).size(100000);
                    preCol = col;
                    preTerms = builder;
                    String cnn = "";
                    if (StringUtils.isNotEmpty(endPath)) {
                        cnn = "_";
                    }
                    endPath = endPath + cnn + LineConstants.AGG_NAME.TERMS + PRE_CN + col + "_buckets";
                    keys.put("key", endPath + "_key");
                    Map<String, String> keys_count1 = new HashMap<>();
                    keys_count1.put("alias", aliaMaps.get(col) + "_doc_count");
                    keys_count1.put("key", endPath + "_doc_count");
                    keyList.add(keys_count1);
                    break;
                case LineConstants.AGG_TYPE.SUM:
                    builder = AggregationBuilders.sum(LineConstants.AGG_NAME.SUM + PRE_CN + col).field(col);
                    keys.put("key", prePath + cn + LineConstants.AGG_NAME.SUM + PRE_CN + col + "_value");
                    break;
                case LineConstants.AGG_TYPE.COUNT:
                    builder = AggregationBuilders.cardinality(LineConstants.AGG_NAME.COUNT + PRE_CN + col).field(col);
                    keys.put("key", prePath + cn + LineConstants.AGG_NAME.COUNT + PRE_CN + col + "_value");
                    break;
                case LineConstants.AGG_TYPE.AVG:
                    builder = AggregationBuilders.avg(LineConstants.AGG_NAME.AVG + PRE_CN + col).field(col);
                    keys.put("key", prePath + cn + LineConstants.AGG_NAME.AVG + PRE_CN + col + "_value");
                    break;
                case LineConstants.AGG_TYPE.DATA:
                    builder = AggregationBuilders.dateHistogram(LineConstants.AGG_NAME.DATA + PRE_CN + col).field(col)
                            .timeZone(DateTimeZone.forOffsetHours(8).toTimeZone().toZoneId()).format(dataFormat).dateHistogramInterval(new DateHistogramInterval(interv));
                    keys.put("key", prePath + cn + LineConstants.AGG_NAME.DATA + PRE_CN + col + "_buckets_key_as_string");
                    Map<String, String> keys_count2 = new HashMap<>();
                    keys_count2.put("alias", aliaMaps.get(col) + "_doc_count");
                    keys_count2.put("key", prePath + cn + LineConstants.AGG_NAME.DATA + PRE_CN + col + "_buckets_doc_count");
                    keyList.add(keys_count2);
                    break;
                default:
            }
            if (pre != null && builder != null) {
                pre.subAggregation(builder);
            }else{
                pre = builder;
            }
            level = aggLevel;
            keyList.add(keys);
        }
        if (top) {//存在取值情况
            if (preTerms != null) {
                preTerms.subAggregation(AggregationBuilders.topHits(LineConstants.AGG_NAME.TOP).size(1));
            } else {
                pre.subAggregation(AggregationBuilders.topHits(LineConstants.AGG_NAME.TOP).size(1));
            }
        }
        for (Object o : topaggs) {
            JSONObject j = (JSONObject) o;
            Map<String, String> m = new HashMap<>();
            m.put("alias", aliaMaps.get(j.getString("column")));
            //m.put("key",prePath+"_hits_hits__source_"+e);
            if (StringUtils.isEmpty(endPath)) {
                m.put("key", "top_hits_hits__source_" + j.getString("column"));
            } else {
                m.put("key", endPath + "_top_hits_hits__source_" + j.getString("column"));
            }
            keyList.add(m);
        }
        queryModel.setAggregationBuilder(pre);
        return keyList;
    }

    public List<String> parseTermFields(JSONObject configObj){
        JSONArray aggs = configObj.getJSONArray("calculation");
        List<String> terms = aggs.stream().filter(s -> (LineConstants.AGG_TYPE.TERMS.equals(((JSONObject) s).getString("algorithm")))).map(v ->{
            return ((JSONObject) v).getString("column");
        }).collect(Collectors.toList());
        if(terms == null){
            terms = new ArrayList<>();
        }
        if(!"无".equals(configObj.getString("column"))){
            terms.add(configObj.getString("column"));
        }
        return terms;
    }
    
    public void push2alarm(String ruleCode,Map<String,String> data,List<String> ids){
        if(ids == null){
            ids = new ArrayList<>();
            ids.add(data.get("_id"));
        }
        Map<String,Object> alarmInfo = new HashMap<>();
        alarmInfo.put("resultGuid",UUID.randomUUID());
        alarmInfo.put("ruleCode",ruleCode);
        String index = "sem_log";
        if(data != null){
            alarmInfo.put("src_ips",data.get("sip"));
            alarmInfo.put("dstIps",data.get("dip"));
            alarmInfo.put("src_ports",data.get("sport"));
            alarmInfo.put("dst_ports",data.get("dport"));
            alarmInfo.put("relatedIps",data.get("sip"));
            index = data.get("_index");
        }
        alarmInfo.put("triggerTime",TimeTools.format2(new Date()));
        Map<String,Object> idRoom = new HashMap<>();
        idRoom.put(index,ids);
        alarmInfo.put("idRoom",idRoom);
        //alarmInfo.put("extendParams","");
        String message = JSONObject.toJSONString(alarmInfo);
        log.info("推送告警："+message);
        kafkaSenderService.send(ALARM_TOPIC,message);
    }
    public static final String event_index="network_password_plaintext";
    public  List<BasePersonZjg> basePersonZjgs=new ArrayList<>();
    public void push2alarm(String ruleCode,Map<String,String> data,List<String> ids,String index,String psw){
        if(ids == null){
            ids = new ArrayList<>();
            ids.add(data.get("_id"));
        }
        Map<String,Object> alarmInfo = new HashMap<>();
        String string = UUID.randomUUID().toString();
        alarmInfo.put("resultGuid",string+"|"+event_index);
        alarmInfo.put("ruleCode",ruleCode);
        if(data != null){
            alarmInfo.put("src_ips",data.get("sip"));
            alarmInfo.put("dstIps",data.get("dip"));
            alarmInfo.put("src_ports",data.get("sport"));
            alarmInfo.put("dst_ports",data.get("dport"));
            alarmInfo.put("relatedIps",data.get("sip"));
        }
        alarmInfo.put("triggerTime",TimeTools.format2(new Date()));
        Map<String,Object> idRoom = new HashMap<>();
        idRoom.put(index,ids);
        alarmInfo.put("idRoom",idRoom);
        //alarmInfo.put("extendParams","");
        //构建描述与触发条件
        structurePrincipleAndalamDesc(alarmInfo,data);
        String message = JSONObject.toJSONString(alarmInfo);
        log.info("推送告警："+message);
        kafkaSenderService.send(ALARM_TOPIC,message);
        //构建事件对象
        JSONObject event=structureEvent(data,string,psw);
        kafkaSenderService.send(ALARM_EVENT, event.toString());
    }

    private void structurePrincipleAndalamDesc(Map<String, Object> alarmInfo, Map<String, String> data) {
        String principle="在event_time，设备dip允许用户使用口令明文登录。";
        principle=principle.replace("event_time",data.get("event_time") == null ? "" : data.get("event_time").toString());
        principle=principle.replace("dip",data.get("dip") == null ? "" : data.get("dip").toString());
        String alamDesc="在event_time，设备dip允许用户使用口令明文登录，触发了网络中存在口令明文";
        alamDesc=alamDesc.replace("event_time",data.get("event_time") == null ? "" :data.get("event_time").toString());
        alamDesc=alamDesc.replace("dip",data.get("dip") == null ? "" :data.get("dip").toString());
        alarmInfo.put("principle",principle);
        alarmInfo.put("alamDesc",alamDesc);
    }

    private JSONObject structureEvent(Map<String, String> map, String string,String psw) {

        Map<String,Object> jsonObjectMap=new HashMap<>();
        jsonObjectMap.put("indexName",event_index);
        Map<String,Object> content=new HashMap<>();
        content.put("resultGuid",string);
        if (StringUtils.isNotBlank(map.get("dip"))){
            content.put("ip",map.get("dip"));
        }
        if (StringUtils.isNotBlank(map.get("dst_std_dev_level"))){
            content.put("security_level",map.get("dst_std_dev_level"));
        }
        if (StringUtils.isNotBlank(map.get("app_protocol"))){
            content.put("use_protocol",map.get("app_protocol"));
        }
        if (StringUtils.isNotBlank(map.get("dport"))){
            content.put("use_port",map.get("dport"));
        }
        content.put("password_plaintext",psw);
        if (StringUtils.isNotBlank(map.get("dst_std_user_no"))){
            if (basePersonZjgs.size()==0){
                Object o = redisTemplate.opsForValue().get(CACHE_PERSON_ZJG_KEY);
                List<BasePersonZjg> list = JSON.parseArray(o.toString(),BasePersonZjg.class);
                basePersonZjgs = list;
            }
            List<BasePersonZjg> dstStdUserNo = basePersonZjgs.stream().filter(a -> a.getUserNo().equals(map.get("dst_std_user_no"))).collect(Collectors.toList());
            if (dstStdUserNo.size()>0){
                BasePersonZjg basePersonZjg = dstStdUserNo.get(0);
                content.put("use_name",basePersonZjg.getUserName());
                content.put("department",basePersonZjg.getOrgName());
            }
        }
        JSONObject jsonObject = new JSONObject(content);
        jsonObjectMap.put("content",jsonObject.toString());
        JSONObject object = new JSONObject(jsonObjectMap);
        return object;
    }

    public BoolQueryBuilder buildCommonQueryBuilder(JSONObject obj){
        String filter = obj.getString("filter");
        //标识字段
        String column = obj.getString("column");
        String type = obj.getString("type");
        String value = obj.getString("value");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(filter)) {
            switch (type) {
                case LineConstants.FILTER_TYPE.EQ:
                    query.must(QueryBuilders.termQuery(filter, value));
                    break;
                case LineConstants.FILTER_TYPE.GT:
                    query.filter(QueryBuilders.rangeQuery(filter).gt(value));
                    break;
                case LineConstants.FILTER_TYPE.LT:
                    query.filter(QueryBuilders.rangeQuery(filter).lt(value));
                    break;
                case LineConstants.FILTER_TYPE.IN:
                    String[] conditions = value.split(",");
                    query.must(QueryBuilders.termsQuery(filter, conditions));
                    break;
            }
        } else if (obj.containsKey("conditions")) {
            JSONArray conditions = JSONArray.parseArray(obj.getString("conditions"));
            conditions.forEach(e -> {
                JSONObject i = (JSONObject) e;
                String f = i.getString("filter");
                String t = i.getString("type");
                String v = i.getString("value");
                if (StringUtils.isNotEmpty(f) && StringUtils.isNotEmpty(t)) {
                    switch (t) {
                        case LineConstants.FILTER_TYPE.EQ:
                            query.must(QueryBuilders.termQuery(f, v));
                            break;
                        case LineConstants.FILTER_TYPE.GT:
                            query.filter(QueryBuilders.rangeQuery(f).gt(v));
                            break;
                        case LineConstants.FILTER_TYPE.LT:
                            query.filter(QueryBuilders.rangeQuery(f).lt(v));
                            break;
                        case LineConstants.FILTER_TYPE.IN:
                            String[] cs = v.split(",");
                            query.must(QueryBuilders.termsQuery(f, cs));
                            break;
                    }
                }
            });
        }
        if (obj.containsKey("script")) {
            query.filter(QueryBuilders.scriptQuery(new Script(obj.getString("script"))));
        }
        return query;
    }

    public EsQueryModel copyByModel(QueryTools.QueryWrapper wrapper, EsQueryModel model) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(model.getStartTime());
        queryModel.setEndTime(model.getEndTime());
        queryModel.setIndexNames(model.getIndexNames());
        // 设置时间字段
        queryModel.setTimeField(model.getTimeField());
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setCount(10000);
        return queryModel;
    }
}
