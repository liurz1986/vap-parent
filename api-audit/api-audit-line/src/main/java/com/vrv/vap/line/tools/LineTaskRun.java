package com.vrv.vap.line.tools;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.vrv.vap.line.config.MessageConfig;
import com.vrv.vap.line.mapper.BaseLineSpecialMapper;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.client.RedisCacheTools;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.fegin.ApiDataClient;
import com.vrv.vap.line.mapper.BaseLineMapper;
import com.vrv.vap.line.mapper.BaseLineResultMapper;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.service.CommonService;
import com.vrv.vap.line.service.KafkaSenderService;
import com.vrv.vap.toolkit.vo.VList;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.document.DateTools;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.script.Script;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.quartz.JobDataMap;
import org.springframework.core.env.Environment;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class LineTaskRun{

    private static Environment env = VapLineApplication.getApplicationContext().getBean(Environment.class);
    //private BaseLineSourceMapper baseLineSourceMapper = VapXcSchedularApplication.getApplicationContext().getBean(BaseLineSourceMapper.class);
    private BaseLineMapper baseLineMapper = VapLineApplication.getApplicationContext().getBean(BaseLineMapper.class);
    private CommonService commonService = VapLineApplication.getApplicationContext().getBean(CommonService.class);
    //private BaseLineSourceFieldService baseLineSourceFieldService = VapXcSchedularApplication.getApplicationContext().getBean(BaseLineSourceFieldService.class);
    private KafkaSenderService kafkaSenderService = VapLineApplication.getApplicationContext().getBean(KafkaSenderService.class);
    private RedisCacheTools redisCacheTools = VapLineApplication.getApplicationContext().getBean(RedisCacheTools.class);
    private BaseLineResultMapper baseLineResultMapper = VapLineApplication.getApplicationContext().getBean(BaseLineResultMapper.class);
    private static ApiDataClient apiDataClient = VapLineApplication.getApplicationContext().getBean(ApiDataClient.class);
    private BaseLineSpecialMapper baseLineSpecialMapper = VapLineApplication.getApplicationContext().getBean(BaseLineSpecialMapper.class);
    private MessageConfig messageConfig = VapLineApplication.getApplicationContext().getBean(MessageConfig.class);
    private String PRE_CN = "_";
    private String PRE_SM = LineConstants.NAME_PRE.PRE_SM;
    private String PRE_LINE = LineConstants.NAME_PRE.PRE_LINE;
    private String TIME_FIELD = "insert_time";
    private final String indexSufFormat = "-yyyy";
    private static final Log log = LogFactory.getLog(LineTaskRun.class);
    private String MESSAGE_TOPIC = "filter-data-baseline";
    private String FIELD_NAME = "暂无";
    private String SOURCE_NAME_END = "-*";
    private static Map<Integer,Source> sourceMap = new HashMap<>();
    private static Map<String,Source> sourceNameMap = new HashMap<>();

    public static void initSource(){
        try{
            VData<List<Source>> listVData = apiDataClient.queryAllSource();
            List<Source> sourceVList = listVData.getData();
            List<Source> sourceVList2 = sourceVList.stream().filter(r -> StringUtils.isNotEmpty(r.getTopicName())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(sourceVList)){
                sourceMap = sourceVList.stream().collect(Collectors.toMap(r -> r.getId(), r-> r));
                List<Source> collect = sourceVList2.stream().collect(
                        Collectors.collectingAndThen(
                                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(Source::getTopicName))), ArrayList::new)
                );
                sourceNameMap = collect.stream().collect(Collectors.toMap(r -> r.getTopicName(), r-> r));
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
    }

    static {
        initSource();
    }

    public static Map<String,Source> getSourceNameMap(){
        return sourceNameMap;
    }


    public List<Map<String,Object>> queryLineEsData(Integer id){
        Map<String,Object> map = new HashMap<>();

        BaseLine line = baseLineMapper.selectById(id);
        List<Map<String, Object>> datas = new ArrayList<>();
        if(LineConstants.SOURCE_TYPE.ES.equals(line.getSourceType())){
            JSONArray arrays = JSONArray.parseArray(line.getConfig());
            Integer days = line.getDays();
            QueryTools.QueryWrapper wrapper = QueryTools.build();
            String alias = line.getAlias();
            JSONArray colsArray = JSONArray.parseArray(line.getSaveColumns());
            Map<String,String> resultMap = colsArray.stream().collect(Collectors.toMap(i -> {JSONObject a= (JSONObject)i;String k = a.getString("src");return k;} , i -> {JSONObject a= (JSONObject)i;String k = a.getString("dest");return k;}));
            String timeSlot = line.getTimeSlot();
            Integer multiple = line.getMultiple();
            //计算中间结果
            arrays.forEach(e -> {
                JSONObject obj = (JSONObject) e;
                Integer index = obj.getInteger("indexId");
                BaseLineSource source = getData(index);
                String column = obj.getString("column");
                //source.getName()
                final EsQueryModel queryModel = buildQueryModel(wrapper, source.getName(), source.getTimeField(), TimeTools.TIME_FMT_1, /*CronTools.getPeriodByCron(line.getCron())*/60, 0);
                List<Map<String, String>> mappings = buildQueryAgg(queryModel, obj, source, alias, timeSlot);
                log.info("#####mappings：" + JSONObject.toJSONString(mappings));
                Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
                if (aggMap != null && aggMap.containsKey("aggregations")) {
                    Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
                    log.info("#####es结果：" + JSONObject.toJSONString(dataAggMap));
                    List<Map<String, Object>> list = ResultUtils.spreadAggregationAsList(dataAggMap,column);
                    log.info("#####list结果：" + JSONObject.toJSONString(list));
                    datas.addAll(list);
                }
            });
            return datas;
        }else{
            return new BaseLine4MysqlUtil().doline(line,new Date());
        }
    }

    public boolean checkStatus(BaseLine line,BaseLineResult lineResult){
        String config = line.getConfig();
        if(StringUtils.isEmpty(config)){
            return false;
        }
        Map<String, String> hashMap = null;
        try{
            String vapSourceKey = StringUtils.isNotEmpty(messageConfig.getVapSourceKey()) ? messageConfig.getVapSourceKey() : "vap_source_message";
            hashMap = redisCacheTools.getHashMap(vapSourceKey);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        if(hashMap != null){
            List<JSONObject> jsonObjects = JSONArray.parseArray(config, JSONObject.class);
            for(JSONObject source : jsonObjects){
                String sourceId = source.getString("indexId");
                if(hashMap.containsKey(sourceId)){
                    JSONObject sourceStatus = JSONObject.parseObject(hashMap.get(sourceId), JSONObject.class);
                    int openStatus = sourceStatus.getIntValue("open_status");
                    int dataStatus = sourceStatus.getIntValue("data_status");
                    if(openStatus * dataStatus == 0){
                        String message = "基线："+line.getId()+" 的数据源："+sourceId + "不满足条件；openStatus="+openStatus+"dataStatus="+dataStatus;
                        log.info(message);
                        lineResult.setMessage(message);
                        line.setWorkStatus("2");
                        line.setWorkMsg("基线数据源不满足");
                        return false;
                    }
                }
            }
        }
        line.setWorkStatus("1");
        return true;
    }

    private List<Map<String, Object>> splitBusinessType(Map<String, Object> map){
        List<Map<String, Object>> result = new ArrayList<>();
        if(map == null){
            return result;
        }
        if(!map.containsKey("business_list")){
            result.add(map);
            return result;
        }
        String types = (String)map.get("business_list");
        String[] typeArray = types.split(",");
        if(typeArray == null || typeArray.length == 0){
            result.add(map);
            return result;
        }
        for(String type : typeArray){
            Map<String, Object> items = new TreeMap<>();
            items.putAll(map);
            items.put("business_list",type);
            result.add(items);
        }
        return result;
    }

    private List<Map<String, Object>> renderLineData(Integer type,QueryTools.QueryWrapper wrapper,EsQueryModel queryModel,BaseLine line,Date time){
        List<Map<String, String>> mapKeys = renderPersonalLine(line,wrapper,queryModel);
        List<Map<String, Object>> personDatas = new ArrayList<>();
        List<Map<String, Object>> personResult = new ArrayList<>();
        log.info("#####person-mappings："+JSONObject.toJSONString(mapKeys));
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            log.info("#####person-es结果："+JSONObject.toJSONString(dataAggMap));
            List<Map<String, Object>> list = ResultUtils.spreadAggregationAsList(dataAggMap,null);
            log.info("#####person-list结果："+JSONObject.toJSONString(list));
            personDatas.addAll(list);
        }
        personDatas.forEach(d ->{
            Map<String, Object> item = new HashMap<>();
            mapKeys.forEach(m ->{
                if(m.containsKey("key") && m.containsKey("alias")){
                    String aKey = m.get("alias");
                    item.put(aKey,d.get(m.get("key")));
                    if(m.containsKey("dev")){
                        Double dev = (Double)d.get(m.get("key"));
                        Double avg = (Double)d.get(m.get("avg"));
                        Double min = avg - dev*line.getMultiple();
                        Double max = avg + dev*line.getMultiple();
                        item.put(aKey+"_avg",avg);
                        item.put(aKey+"_min",min);
                        item.put(aKey+"_max",max);
                        item.put(aKey+"_total",(Double)d.get(m.get("sum")));
                    }
                }
            });
            item.put("insert_time",time);
            item.put("start_time",TimeTools.getNowBeforeByDay(line.getDays()));
            item.put("end_time",TimeTools.getNowBeforeByDay2(1));
            item.put("interval_num",line.getDays());
            item.put("type",type);
            item.put("guid",UUID.randomUUID());
            personResult.add(item);
        });
        return personResult;
    }


    private Map<String,Object> renderGroupLine(List<Map<String,Object>> datas, BaseLine line){
        int all = datas.size();
        Map<String,Double> caMap = new HashMap<>();
        Map<String,Object> resultMap = new HashMap<>();
        datas.forEach(e ->{
            e.keySet().forEach(k ->{
                Object v = e.get(k);
                if(v instanceof Integer || v instanceof Double || v instanceof Float){
                    Double dv = Double.valueOf(v.toString());
                    if(caMap.containsKey(k)){
                        caMap.put(k,caMap.get(k)+dv);
                    }else{
                        caMap.put(k,dv);
                    }
                }
            });
        });
        caMap.keySet().forEach(k ->{
            Double v = caMap.get(k);
            Double var1 = v/all;
            resultMap.put(k,new BigDecimal(var1).setScale(2,BigDecimal.ROUND_HALF_UP).doubleValue());
        });
        resultMap.put("type","1");
        return resultMap;
    }

    private void createTemplate(BaseLine line){
        String endIndex = PRE_LINE+line.getSaveIndex();
        JSONArray array = JSONArray.parseArray(line.getSaveColumns());
        if(CollectionUtils.isNotEmpty(array)){
            List<EsColumns> cols = new ArrayList<>();
            array.forEach(e -> {
                JSONObject o = (JSONObject) e;
                cols.add(new EsColumns(o.getString("dest"),o.getString("type"),StringUtils.isNotEmpty(o.getString("format")) ? o.getString("format") : null,o.getString("description")));
            });
            EsColumns typeCols = new EsColumns("type","keyword");
            typeCols.setTitle("基线类型");
            cols.add(typeCols);
            if(!commonService.indexTemplateExists(endIndex)){
                log.info("创建模板："+endIndex);
                commonService.createTemplate(new EsTemplate(endIndex, cols));
            }
            if(!sourceIsExists(endIndex+SOURCE_NAME_END)){
                log.info("api-data添加数据源");
                int id = addSource2Data(endIndex,line.getName()+"结果表");
                log.info("api-data添加字段");
                addField2Data(cols,id);
            }
        }
    }

    private void createProcessTemplate(BaseLine line){
        String endIndex = PRE_SM+line.getSaveIndex();
        JSONArray array = JSONArray.parseArray(line.getSaveColumns());
        if(CollectionUtils.isNotEmpty(array)){
            List<EsColumns> cols = new ArrayList<>();
            array.forEach(e -> {
                JSONObject o = (JSONObject) e;
                boolean isMediate = o.getBooleanValue("mediate");
                if(isMediate){
                    cols.add(new EsColumns(o.getString("dest"),o.getString("type"),StringUtils.isNotEmpty(o.getString("format")) ? o.getString("format") : null,o.getString("description")));
                }
            });
            if(!commonService.indexTemplateExists(endIndex)){
                log.info("创建模板："+endIndex);
                commonService.createTemplate(new EsTemplate(endIndex, cols));
            }
            if(!sourceIsExists(endIndex+SOURCE_NAME_END)){
                log.info("api-data添加数据源");
                int id = addSource2Data(endIndex,line.getName()+"中间值表");
                log.info("api-data添加字段");
                addField2Data(cols,id);
            }
        }
    }

    private List<Map<String,String>> buildQueryAgg(EsQueryModel queryModel,JSONObject obj,BaseLineSource source,String alias,String timeSolt){
        List<Map<String,String>> keyList = new ArrayList<>();//key映射集合
        Map<String,String> mainKey = new HashMap<>();
        Map<String,String> mainDocKey = new HashMap<>();
        mainKey.put("alias",alias);
        mainKey.put("key","key");
        mainDocKey.put("alias",alias+"_doc_count");
        mainDocKey.put("key","doc_count");
        keyList.add(mainKey);
        keyList.add(mainDocKey);
        String interv = "";
        String dataFormat = "";
        if("2".equals(timeSolt)){
            interv = "1d";
            dataFormat = "yyyy-MM-dd";
        }else{
            interv = "1h";
            dataFormat = "H";
        }
        queryModel.setUseAggre(true);
        String filter = obj.getString("filter");
        //标识字段
        String column = obj.getString("column");
        String type = obj.getString("type");
        String value = obj.getString("value");
        JSONArray aggs = obj.getJSONArray("calculation");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if(StringUtils.isNotEmpty(type) && StringUtils.isNotEmpty(filter)){
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
        }else if(obj.containsKey("conditions")){
            JSONArray conditions = JSONArray.parseArray(obj.getString("conditions"));
            conditions.forEach(e ->{
                JSONObject i = (JSONObject)e;
                String f = i.getString("filter");
                String t = i.getString("type");
                String v = i.getString("value");
                if(StringUtils.isNotEmpty(f) && StringUtils.isNotEmpty(t)){
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
                        case LineConstants.FILTER_TYPE.N_EQ:
                            if (StringUtils.isNotBlank(v)){
                                query.mustNot(QueryBuilders.termQuery(f, v));
                            }else {
                                query.must(QueryBuilders.existsQuery(f));
                            }
                            break;
                    }
                }
            });
        }
        if(obj.containsKey("script")){
            query.filter(QueryBuilders.scriptQuery(new Script(obj.getString("script"))));
        }
        queryModel.setQueryBuilder(query);
        AbstractAggregationBuilder pre = null;
        if(!"无".equals(column)){
            pre = AggregationBuilders.terms(LineConstants.AGG_NAME.TERMS+PRE_CN+column).field(column).size(100000);
            queryModel.setAggregationBuilder(pre);
        }
        if(aggs == null){
            return keyList;
        }
        List<SourceField> sourceFields = this.apiDataClient.getFields(Integer.parseInt(source.getId())).getData();
        Map<String,String> aliaMaps = new HashMap<>();
        sourceFields.forEach(e -> {
            if(StringUtils.isEmpty(e.getAlias())){
                aliaMaps.put(e.getField(),e.getField());
            }else{
                aliaMaps.put(e.getField(),e.getAlias());
            }
        });
        boolean top = false;
        //取出非top算法 （top算法放在最后一层）
        List<Object> faggs = aggs.stream().filter(s->(!LineConstants.AGG_TYPE.TOP.equals(((JSONObject)s).getString("algorithm")))).collect(Collectors.toList());
        //取出top算法
        List<Object> topaggs = aggs.stream().filter(s->(LineConstants.AGG_TYPE.TOP.equals(((JSONObject)s).getString("algorithm")))).collect(Collectors.toList());
        //按聚合层级排序
        faggs.sort(Comparator.comparing(b -> ((JSONObject) b).getIntValue("aggLevel")));
        if(CollectionUtils.isNotEmpty(topaggs)){
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
        for(Object e : faggs){
            JSONObject b = (JSONObject)e;
            //取出算法
            String algorithm = b.getString("algorithm");
            //拼key映射关系
            String col = b.getString("column");
            Map<String,String> keys = new HashMap<>();
            keys.put("alias",aliaMaps.get(col));
            //拼聚合层级
            int aggLevel = b.getIntValue("aggLevel");
            AbstractAggregationBuilder builder = null;
            if(StringUtils.isNotEmpty(prePath)){
                cn = "_";
            }
            if(preTerms != null && aggLevel > level){
                pre = preTerms;
                prePath = prePath + cn + LineConstants.AGG_NAME.TERMS+PRE_CN+preCol+"_buckets";
            }
            if(StringUtils.isNotEmpty(prePath)){
                cn = "_";
            }
            switch (algorithm) {
                case LineConstants.AGG_TYPE.TERMS:
                    builder = AggregationBuilders.terms(LineConstants.AGG_NAME.TERMS+PRE_CN+col).field(col).size(100000);
                    preCol = col;
                    preTerms = builder;
                    String cnn = "";
                    if(StringUtils.isNotEmpty(endPath)){
                        cnn = "_";
                    }
                    endPath = endPath + cnn + LineConstants.AGG_NAME.TERMS +PRE_CN+col + "_buckets";
                    keys.put("key",endPath+"_key");
                    Map<String,String> keys_count1 = new HashMap<>();
                    keys_count1.put("alias",aliaMaps.get(col)+"_doc_count");
                    keys_count1.put("key",endPath+"_doc_count");
                    keyList.add(keys_count1);
                    break;
                case LineConstants.AGG_TYPE.SUM:
                    builder = AggregationBuilders.sum(LineConstants.AGG_NAME.SUM+PRE_CN+col).field(col);
                    keys.put("key",prePath+cn+LineConstants.AGG_NAME.SUM+PRE_CN+col+"_value");
                    break;
                case LineConstants.AGG_TYPE.COUNT:
                    builder = AggregationBuilders.cardinality(LineConstants.AGG_NAME.COUNT+PRE_CN+col).field(col);
                    keys.put("key",prePath+cn+LineConstants.AGG_NAME.COUNT+PRE_CN+col+"_value");
                    break;
                case LineConstants.AGG_TYPE.AVG:
                    builder = AggregationBuilders.avg(LineConstants.AGG_NAME.AVG+PRE_CN+col).field(col);
                    keys.put("key",prePath+cn+LineConstants.AGG_NAME.AVG+PRE_CN+col+"_value");
                    break;
                case LineConstants.AGG_TYPE.DATA:
                    builder = AggregationBuilders.dateHistogram(LineConstants.AGG_NAME.DATA+PRE_CN+col).field(col)
                            .timeZone(DateTimeZone.forOffsetHours(8).toTimeZone().toZoneId()).format(dataFormat).dateHistogramInterval(new DateHistogramInterval(interv));
                    keys.put("key",prePath+cn+LineConstants.AGG_NAME.DATA+PRE_CN+col+"_buckets_key_as_string");
                    Map<String,String> keys_count2 = new HashMap<>();
                    keys_count2.put("alias",aliaMaps.get(col)+"_doc_count");
                    keys_count2.put("key",prePath+cn+LineConstants.AGG_NAME.DATA+PRE_CN+col+"_buckets_doc_count");
                    keyList.add(keys_count2);
                    break;
                default:
            }
            if(pre != null && builder != null){
                pre.subAggregation(builder);
            }else if(builder != null){
                pre = builder;
            }
            level = aggLevel;
            keyList.add(keys);
        }
        if(top){//存在取值情况
            if(preTerms != null){
                preTerms.subAggregation(AggregationBuilders.topHits(LineConstants.AGG_NAME.TOP).size(1));
            }else if(pre != null){
                pre.subAggregation(AggregationBuilders.topHits(LineConstants.AGG_NAME.TOP).size(1));
            }else {
                queryModel.setAggregationBuilder(AggregationBuilders.topHits(LineConstants.AGG_NAME.TOP).size(1));
            }
        }
        if("无".equals(column) && pre != null){
            queryModel.setAggregationBuilder(pre);
        }
        for(Object o : topaggs){
            JSONObject j = (JSONObject)o;
            Map<String,String> m = new HashMap<>();
            m.put("alias",aliaMaps.get(j.getString("column")));
            //m.put("key",prePath+"_hits_hits__source_"+e);
            if(StringUtils.isEmpty(endPath)){
                m.put("key","top_hits_hits__source_"+j.getString("column"));
            }else{
                m.put("key",endPath+"_top_hits_hits__source_"+j.getString("column"));
            }
            keyList.add(m);
        };
        return keyList;
    }

    private List<Map<String,String>> renderPersonalLine(BaseLine line,QueryTools.QueryWrapper wrapper,EsQueryModel queryModel){
        List<Map<String,String>> keyList = new ArrayList<>();//key映射集合
        List<LineSaveModel> list = JSONArray.parseArray(line.getSaveColumns(), LineSaveModel.class);
        //取出计算为均值 最大最小值的字段 然后排除为0的数据
        List<LineSaveModel> conditions = list.stream().filter(s->( LineConstants.AGG_TYPE.AVG.equals(s.getAggType()) || LineConstants.AGG_TYPE.DEV.equals(s.getAggType()))).collect(Collectors.toList());
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if(CollectionUtils.isNotEmpty(conditions)){
            conditions.forEach(e ->{
                query.mustNot(QueryBuilders.termQuery(e.getDest(),0));
            });
        }
        queryModel.setQueryBuilder(query);

        //取出top算法
        boolean top = false;
        List<LineSaveModel> topModel = list.stream().filter(s->("5".equals(s.getAggType()))).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(topModel)){
            //存在top算法
            top = true;
        }
        //取出非top算法
        List<LineSaveModel> unTopModel = list.stream().filter(s->(s.getAggType() != null && !"5".equals(s.getAggType()))).collect(Collectors.toList());
        if(unTopModel == null){
            return keyList;
        }
        //存在均值算法 默认增加求和算法
        List<LineSaveModel> avgmodel = list.stream().filter(s->("4".equals(s.getAggType()))).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(avgmodel)){
            LineSaveModel avg = avgmodel.get(0);
            LineSaveModel model = new LineSaveModel(avg.getSrc(),avg.getDest(),avg.getType(),"总数",LineConstants.AGG_TYPE.SUM,avg.getLevel());
            unTopModel.add(model);
        }
        unTopModel.sort(Comparator.comparing(b -> (b.getLevel())));
        LineSaveModel mainModel = unTopModel.get(0);
        AbstractAggregationBuilder pre = AggregationBuilders.terms(LineConstants.AGG_NAME.TERMS+PRE_CN+mainModel.getDest()).field(mainModel.getDest());
        Map<String,String> mainKey = new HashMap<>();
        mainKey.put("alias",mainModel.getDest());
        mainKey.put("key","key");
        keyList.add(mainKey);
        queryModel.setAggregationBuilder(pre);
        //记录上一次分桶聚合
        unTopModel.remove(0);
        AbstractAggregationBuilder preTerms = null;
        int level = 1;
        String prePath = "";
        String endPath = "";
        String preCol = "";
        String cn = "";
        String interv = "";
        if("1".equals(line.getTimeSlot())){
            interv = "1d";
        }else{
            interv = "1h";
        }
        for(LineSaveModel e : unTopModel){
            //取出算法
            String algorithm = e.getAggType();
            //拼key映射关系
            String col = e.getDest();
            Map<String,String> keys = new HashMap<>();
            keys.put("alias",col);
            //拼聚合层级
            int aggLevel = e.getLevel();
            AbstractAggregationBuilder builder = null;
            if(StringUtils.isNotEmpty(prePath)){
                cn = "_";
            }
            if(preTerms != null && aggLevel > level){
                pre = preTerms;
                prePath = prePath + cn + LineConstants.AGG_NAME.TERMS+PRE_CN+preCol+"_buckets";
            }
            if(StringUtils.isNotEmpty(prePath)){
                cn = "_";
            }
            switch (algorithm) {
                case LineConstants.AGG_TYPE.TERMS:
                    builder = AggregationBuilders.terms(LineConstants.AGG_NAME.TERMS+PRE_CN+col).field(col);
                    preCol = col;
                    preTerms = builder;
                    String cnn = "";
                    if(StringUtils.isNotEmpty(endPath)){
                        cnn = "_";
                    }
                    endPath = endPath + cnn + LineConstants.AGG_NAME.TERMS +PRE_CN+col + "_buckets";
                    keys.put("key",endPath+"_key");
                    Map<String,String> keys_count1 = new HashMap<>();
                    break;
                case LineConstants.AGG_TYPE.AVG:
                    keys.put("alias",col+"_avg");
                    builder = AggregationBuilders.avg(LineConstants.AGG_NAME.AVG+PRE_CN+col).field(col);
                    keys.put("key",prePath+cn+LineConstants.AGG_NAME.AVG+PRE_CN+col+"_value");
                    addAvgCol(line,col);
                    break;
                case LineConstants.AGG_TYPE.SUM:
                    keys.put("alias",col+"_total");
                    builder = AggregationBuilders.sum(LineConstants.AGG_NAME.SUM+PRE_CN+col).field(col);
                    keys.put("key",prePath+cn+LineConstants.AGG_NAME.SUM+PRE_CN+col+"_value");
                    break;
                case LineConstants.AGG_TYPE.DEV:
                    builder = AggregationBuilders.extendedStats(LineConstants.AGG_NAME.DEV+PRE_CN+col).field(col);
                    keys.put("key",prePath+cn+LineConstants.AGG_NAME.DEV+PRE_CN+col+"_std_deviation");
                    keys.put("dev","true");
                    keys.put("avg",prePath+cn+LineConstants.AGG_NAME.DEV+PRE_CN+col+"_avg");
                    keys.put("sum",prePath+cn+LineConstants.AGG_NAME.DEV+PRE_CN+col+"_sum");
                    addDevCol(line,col);
                    break;
                case LineConstants.AGG_TYPE.DATA:
                    builder = AggregationBuilders.dateHistogram(LineConstants.AGG_NAME.DATA+PRE_CN+col).field(col)
                            .timeZone(DateTimeZone.forOffsetHours(8).toTimeZone().toZoneId()).format(TimeTools.TIME_FMT_2).dateHistogramInterval(new DateHistogramInterval(interv));
                    keys.put("key",prePath+cn+LineConstants.AGG_NAME.DATA+PRE_CN+col+"_buckets_key_as_string");
                    Map<String,String> keys_count2 = new HashMap<>();
                    keys_count2.put("alias",col+"_doc_count");
                    keys_count2.put("key",prePath+cn+LineConstants.AGG_NAME.DATA+PRE_CN+col+"_buckets_doc_count");
                    keyList.add(keys_count2);
                    break;
                default:
            }
            if(pre != null && builder != null){
                pre.subAggregation(builder);
            }
            level = aggLevel;
            keyList.add(keys);
        }
        if(top){//存在取值情况
            if(preTerms != null){
                preTerms.subAggregation(AggregationBuilders.topHits(LineConstants.AGG_NAME.TOP).size(1));
            }else{
                pre.subAggregation(AggregationBuilders.topHits(LineConstants.AGG_NAME.TOP).size(1));
            }
        }
        for(LineSaveModel l : topModel){
            Map<String,String> m = new HashMap<>();
            m.put("alias",l.getDest());
            //m.put("key",prePath+"_hits_hits__source_"+e);
            if(StringUtils.isEmpty(endPath)){
                m.put("key","top_hits_hits__source_"+l.getDest());
            }else{
                m.put("key",endPath+"_top_hits_hits__source_"+l.getDest());
            }
            keyList.add(m);
        };
        //加totalcount
        /*
        Map<String,String> totalKey = new HashMap<>();
        totalKey.put("alias","total_count");
        totalKey.put("key",endPath+"_doc_count");
        keyList.add(totalKey);*/
        return keyList;
    }

    public void addAvgCol(BaseLine line,String col){
        List<LineSaveModel> models = JSONArray.parseArray(line.getSaveColumns(), LineSaveModel.class);
        models.add(new LineSaveModel(col+"_avg",col+"_avg","double","均值"));
        models.add(new LineSaveModel(col+"_total",col+"_total","double","总值"));
        line.setSaveColumns(JSONObject.toJSONString(models));
    }

    public void addDevCol(BaseLine line,String col){
        List<LineSaveModel> models = JSONArray.parseArray(line.getSaveColumns(), LineSaveModel.class);
        models.add(new LineSaveModel(col+"_avg",col+"_avg","double","均值"));
        models.add(new LineSaveModel(col+"_max",col+"_max","double","最大值"));
        models.add(new LineSaveModel(col+"_min",col+"_min","double","最小值"));
        models.add(new LineSaveModel(col+"_total",col+"_total","double","总值"));
        line.setSaveColumns(JSONObject.toJSONString(models));
    }

    public int addSource2Data(String index,String title){
        Source source = new Source();
        source.setDataType(2);
        source.setName(index+SOURCE_NAME_END);
        source.setType(1);
        source.setTimeField(TIME_FIELD);
        source.setTitle(title);
        source.setTopicName(index);
        source.setTimeFormat(TimeTools.TIME_FMT_2);
        VData<Source> sourceVData = this.apiDataClient.addSource(source);
        return sourceVData.getData().getId();
    }

    public void addField2Data(List<EsColumns> cols,int sourceId){
        int i = 1;
        for(EsColumns c : cols){
            SourceField field = new SourceField();
            field.setField(c.getKey());
            field.setOrigin(c.getType());
            field.setType(c.getType());
            field.setSourceId(sourceId);
            field.setName(StringUtils.isNotEmpty(c.getTitle()) ? c.getTitle() : FIELD_NAME);
            field.setAnalysisType(parseType(c.getType()));
            field.setAnalysisSort(i);
            field.setAnalysisTypeLength(field.getAnalysisType().length());
            this.apiDataClient.addField(field);
            i++;
        }
        //增加guid
        SourceField field = new SourceField();
        field.setField("guid");
        field.setOrigin("keyword");
        field.setType("keyword");
        field.setSourceId(sourceId);
        field.setName("guid");
        field.setAnalysisType("varchar");
        field.setAnalysisSort(0);
        field.setAnalysisTypeLength(7);
        this.apiDataClient.addField(field);
    }

    public boolean sourceIsExists(String name){
        boolean exists = false;
        SourceQuery query = new SourceQuery();
        query.setName(name);
        List<Source> sources = this.apiDataClient.querySource(query).getList();
        if(CollectionUtils.isNotEmpty(sources)){
            for(Source s : sources){
                if(name.equals(s.getName())){
                    exists = true;
                    break;
                }
            }
        }
        return exists;
    }

    public BaseLineSource getData(Integer id){
        //Source source = this.apiDataClient.getSourceById(id).getData();
        Source source = sourceMap.get(id);
        if(source == null){
            source = this.apiDataClient.getSourceById(id).getData();
            if(source == null){
                log.error("数据源："+id+"不存在");
            }else{
                sourceMap.put(id,source);
            }
        }
        BaseLineSource s = new BaseLineSource();
        String name = source.getName();
        if(name.lastIndexOf("-*") > -1){
            name = name.replaceAll("-\\*","");
        }
        s.setName(name);
        s.setType(String.valueOf(source.getType()));
        s.setTimeField(source.getTimeField());
        s.setTimeFormat(source.getTimeFormat());
        s.setId(source.getId().toString());
        return s;
    }

    private EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, String index, String time,String format,Integer day,Integer endDay) {
        //day = 180;
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(day));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(endDay));
        List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        //queryModel.setTimeFormat(format);
        //queryModel.setNeedTimeFormat(true);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public String parseType(String type) {
        String result = "";
        switch (type) {
            case "keyword":
                result = "varchar";
                break;
            case "text":
                result = "varchar";
                break;
            case "date":
                result = "varchar";
                break;
            case "long":
                result = "int";
                break;
            case "double":
                result = "double";
                break;
            case "float":
                result = "float";
                break;
        }
        return result;
    }

    public void renderDataTime(){
        QueryWrapper<BaseLine> q = new QueryWrapper<>();
        q.in("id",Arrays.asList("104,105".split(",")));
        List<BaseLine> baseLines = this.baseLineMapper.selectList(q);
        for(BaseLine line:baseLines){
            List<JSONObject> configs = JSONArray.parseArray(line.getSaveColumns(), JSONObject.class);
            JSONObject dataTime = new JSONObject();
            dataTime.put("level",9999);
            dataTime.put("src","data_time");
            dataTime.put("mediate",false);
            dataTime.put("count",false);
            dataTime.put("description","数据日期");
            dataTime.put("main",false);
            dataTime.put("dest","data_time");
            dataTime.put("type","keyword");
            boolean flag = true;
            for(JSONObject j : configs){
                String src = j.getString("src");
                if("data_time".equals(src)){
                    flag = false;
                    break;
                }
            }
            if(flag){
                configs.add(dataTime);
                JSONObject fields = JSONObject.parseObject(line.getFields());
                JSONArray result = fields.getJSONArray("result");
                result.add(dataTime);
                JSONArray mediate = fields.getJSONArray("mediate");
                JSONObject dataTime2 = new JSONObject();
                dataTime2.put("level",9999);
                dataTime2.put("src","data_time");
                dataTime2.put("mediate",false);
                dataTime2.put("count",false);
                dataTime2.put("description","数据日期");
                dataTime2.put("main",false);
                dataTime2.put("dest","data_time");
                dataTime2.put("type","keyword");
                mediate.add(dataTime2);
                Map<String,Object> newFile = new HashMap<>();
                newFile.put("result",result);
                newFile.put("mediate",mediate);
                line.setSaveColumns(JSONObject.toJSONString(configs));
                line.setFields(JSONObject.toJSONString(newFile));
                this.baseLineMapper.updateById(line);
            }
        }
    }

    public void runLine(BaseLine line){
        int perod = CronTools.getPeriodByCron(line.getCron());
        Date startTime = TimeTools.getNowBeforeByDay(perod);
        Date endTime = TimeTools.getNowBeforeByDay2(1);
        Integer dataCycle = line.getDataCycle();
        if(dataCycle != null && !dataCycle.equals(0)){
            Date dataEndTime = MyTimeTools.getNextSummaryEndTime(startTime,dataCycle);
            while (startTime.before(endTime)){
               if(dataEndTime.after(endTime)){
                   runByTime(line,null,null,startTime,endTime);
               }else{
                   runByTime(line,null,null,startTime,dataEndTime);
               }
               startTime = MyTimeTools.addMini(startTime,dataCycle);
               dataEndTime = MyTimeTools.addMini(dataEndTime,dataCycle);
            }
        }else {
            runByTime(line,null,null,startTime,endTime);
        }
    }

    public void runLineTest(BaseLine line){
        //int perod = CronTools.getPeriodByCron(line.getCron());
        Date startTime = TimeTools.getNowBeforeByDay(90);
        Date endTime = TimeTools.getNowBeforeByDay2(1);
        Integer dataCycle = line.getDataCycle();
        if(dataCycle != null && !dataCycle.equals(0)){
            Date dataEndTime = MyTimeTools.getNextSummaryEndTime(startTime,dataCycle);
            while (startTime.before(endTime)){
               if(dataEndTime.after(endTime)){
                   runByTime(line,null,null,startTime,endTime);
               }else{
                   runByTime(line,null,null,startTime,dataEndTime);
               }
               startTime = MyTimeTools.addMini(startTime,dataCycle);
               dataEndTime = MyTimeTools.addMini(dataEndTime,dataCycle);
            }
        }else {
            runByTime(line,null,null,startTime,endTime);
        }
    }

    /**
     *
     * @param line 基线对象
     * @param baseLineResult 基线结果对象 失败重运行时需传 其余情况传 null
     */
    public void runByTime(BaseLine line,BaseLineResult baseLineResult,Integer summaryNum,Date startTime,Date endTime) {
        Date currentTime = new Date();
        BaseLineResult lineResult = new BaseLineResult();
        BaseLineUtil lineUtil = new BaseLineUtil();
        int resultSize=0;
        try{
            //增加运行次数
            if(baseLineResult == null){
                line.setRunNum(line.getRunNum()+1);
                summaryNum = line.getRunNum();
            }
            final Integer num = summaryNum;
            JSONObject resultConfig = new JSONObject();
            resultConfig.put("startTime",TimeTools.format(startTime,"yyyy-MM-dd HH:mm:ss SSS"));
            resultConfig.put("endTime",TimeTools.format(endTime,"yyyy-MM-dd HH:mm:ss SSS"));
            resultConfig.put("summaryNum",line.getRunNum());
            lineResult.setConfig(JSONObject.toJSONString(resultConfig));
            int id = line.getId();
            String saveCols = line.getSaveColumns();
            lineResult.setBaseLineId(id);
            lineResult.setTime(new Date());
            log.info("基线任务执行开始id："+id);
            if(!checkStatus(line,lineResult)){
                lineResult.setResult(LineConstants.LINE_RESULT.FAILED);
                baseLineMapper.updateById(line);
                if(baseLineResult == null){
                    //无结果记录时 为执行基线 新增本次基线结果记录
                    baseLineResultMapper.insert(lineResult);
                }else{
                    //有结果记录时为失败重新触发基线 更新结果记录
                    baseLineResult.setMessage(lineResult.getMessage());
                    baseLineResult.setResult(lineResult.getResult());
                    baseLineResultMapper.updateById(baseLineResult);
                }
                return;
            }
            QueryTools.QueryWrapper personwrapper = QueryTools.build();
            try{
                BaseLineUtil.clearDatas(line,personwrapper);
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            if(LineConstants.LINE_TYPE.TS.equals(line.getType())){
                //内置模型
                BaseLineSpecial baseLineSpecial = baseLineSpecialMapper.selectById(line.getSpecialId());
                if(LineConstants.FRAME_TYPE.FLINK.equals(baseLineSpecial.getFrame())){
                    //提交flink任务
                    lineResult.setMessage(new BaseLineUtil().runSpecialTaskBySsh(baseLineSpecial,JSONObject.parseObject(line.getSpecialParam(),HashMap.class)));
                }else if(LineConstants.FRAME_TYPE.JAVA.equals(baseLineSpecial.getFrame())){
                    //提交java任务
                    Map<String,Object> params = new HashMap<>();
                    if(StringUtils.isNotEmpty(line.getSpecialParam())){
                        params = JSONObject.parseObject(line.getSpecialParam(),HashMap.class);
                    }
                    params.put("id",line.getId());
                    String classes = baseLineSpecial.getMainClass();
                    new Special4JavaTools().execute(params,classes);
                }
            }else{
                List<Map<String, Object>> personList = null;
                Date yesterday = MyTimeTools.getDateBeforeByDay(currentTime,1);
                String summaryIndex = PRE_SM + line.getSaveIndex();
                String endIndex = PRE_LINE + line.getSaveIndex();
                String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
                //解析基线
                if(StringUtils.isEmpty(line.getConfig())){
                    throw new RuntimeException("无基线配置");
                }
                if(LineConstants.SOURCE_TYPE.ES.equals(line.getSourceType())){
                    //es类型
                    JSONArray arrays = JSONArray.parseArray(line.getConfig());
                    //获取需要校验不为零字段 用来排除无效数据
                    List<JSONObject> columnList = JSONArray.parseArray(line.getSaveColumns(), JSONObject.class);
                    List<String> notZeroCols = columnList.stream().filter(s -> s.getBooleanValue("notZero")).map(i -> {
                        return i.getString("dest");
                    }).collect(Collectors.toList());
                    Integer days = line.getDays();
                    List<Map<String, Object>> result = new ArrayList<>();
                    QueryTools.QueryWrapper wrapper = QueryTools.build();
                    String alias = line.getAlias();
                    JSONArray colsArray = JSONArray.parseArray(line.getSaveColumns());
                    Map<String,String> resultMap = colsArray.stream().collect(Collectors.toMap(i -> {JSONObject a= (JSONObject)i;String k = a.getString("src");return k;} , i -> {JSONObject a= (JSONObject)i;String k = a.getString("dest");return k;}));

                    String timeSlot = line.getTimeSlot();
                    Integer multiple = line.getMultiple();
                    //计算中间结果
                    StringBuffer names = new StringBuffer();
                    arrays.forEach(e -> {
                        JSONObject obj = (JSONObject) e;
                        Integer index = obj.getInteger("indexId");
                        BaseLineSource source = getData(index);
                        names.append(source.getName()).append(",");
                    });
                    JSONObject obj = (JSONObject)arrays.get(0);
                    Integer index = obj.getInteger("indexId");
                    BaseLineSource source = getData(index);
                    final EsQueryModel queryModel = buildQueryModelByDate(wrapper, names.toString().split(","), source.getTimeField(),TimeTools.TIME_FMT_1, startTime,endTime);
                    String column = obj.getString("column");
                    List<Map<String, String>> mappings =  buildQueryAgg(queryModel,obj,source,alias,timeSlot);
                    if(queryModel.getAggregationBuilder() == null){
                        //无聚合
                        SearchResponse response = wrapper.getSearchResponse(queryModel);
                        long total = 0;
                        if (response != null && response.getHits() != null) {
                            total = response.getHits().getTotalHits().value;
                        }
                        Map<String, Object> item = new HashMap<>();
                        item.put("count",total);
                        item.put("insert_time",currentTime);
                        item.put("data_time",dataTime);
                        item.put("guid",UUID.randomUUID());
                        result.add(item);
                    }else{
                        log.info("#####mappings："+JSONObject.toJSONString(mappings));
                        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
                        if (aggMap != null && aggMap.containsKey("aggregations")) {
                            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
                            List<Map<String, Object>> list = ResultUtils.spreadAggregationAsList(dataAggMap,column);
                            if(CollectionUtils.isNotEmpty(list)){
                                list.forEach(da ->{
                                    Map<String, Object> item = new HashMap<>();
                                    mappings.forEach(m ->{
                                        if(m.containsKey("key") && m.containsKey("alias")){
                                            String aKey = resultMap.get(m.get("alias"));
                                            if(aKey == null){
                                                return;
                                            }
                                            item.put(aKey,da.get(m.get("key")));
                                        }
                                    });
                                    item.put("insert_time",currentTime);
                                    item.put("data_time",dataTime);
                                    item.put("summary_num",num);
                                    item.put("guid",UUID.randomUUID());
                                    //判断是否是无效数据
                                    boolean flag = false;
                                    for(String col : notZeroCols){
                                        Object colValue = item.get(col);
                                        if(colValue == null || Integer.parseInt(colValue.toString()) == 0){
                                            //所有无效字段均为null或者0则该数据无效
                                            flag = true;
                                        }else{
                                            //有一个无效字段有值则数据有效
                                            flag = false;
                                            break;
                                        }
                                    }
                                    if(flag){
                                        //无效数据不保存，更新summaryNum
                                        updateSummaryNum(item,line);
                                    }else{
                                        if(JSONObject.toJSONString(mappings).indexOf("business_list") > -1){
                                            //包含business_type 业务需要切分business_type
                                            result.addAll(splitBusinessType(item));
                                        }else{
                                            result.add(item);
                                        }
                                    }
                                });
                            }
                        }
                    }
                    log.info("中间值结果："+result.size());
                    if (result.size()>0){
                        resultSize=1;
                    }
                    if(LineConstants.SAVE_TYPE.ES.equals(line.getSaveType()) || LineConstants.SAVE_TYPE.ES_AND_KAFAK.equals(line.getSaveType())){
                        String processIndex = summaryIndex + TimeTools.format(yesterday, indexSufFormat);
                        //创建中间值索引模板
                        createProcessTemplate(line);
                        //保存中间结果
                        log.info("保存中间结果开始");
                        commonService.create365Alias(processIndex, summaryIndex + "-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
                        QueryTools.writeData(result, processIndex, wrapper);
                        log.info("#####入库"+processIndex+"数据："+result.size()+"条");
                    }
                    if(line.getSaveType().equals(LineConstants.SAVE_TYPE.MYSQL_AND_KAFAK) || line.getSaveType().equals(LineConstants.SAVE_TYPE.ES_AND_KAFAK)){
                        kafkaSenderService.batchSend(summaryIndex,result);
                    }
                    if(LineConstants.SAVE_TYPE.MYSQL.equals(line.getSaveType()) || LineConstants.SAVE_TYPE.MYSQL_AND_KAFAK.equals(line.getSaveType())){
                        //创建mysql表
                        lineUtil.createTable(line);
                        //数据入mysql
                        lineUtil.save2mysql(line,result,LineConstants.TABLE_TYPE.SM);
                    }
                    //###################根据中间结果计算个体基线数据###################
                    if(LineConstants.SAVE_TYPE.ES.equals(line.getSaveType()) || LineConstants.SAVE_TYPE.ES_AND_KAFAK.equals(line.getSaveType())){
                        log.info("计算个体基线");
                        EsQueryModel personQueryModel = buildQueryModel(wrapper, summaryIndex, "insert_time",TimeTools.TIME_FMT_2,line.getDays()*CronTools.getPeriodByCron(line.getCron()),0);
                        personList = renderLineData(1, personwrapper, personQueryModel, line, currentTime);
                        if("1".equals(line.getOpenGroup())){
                            log.info("计算群体基线");
                            //计算群体基线
                            List<LineSaveModel> lineSaveModels = JSONArray.parseArray(line.getSaveColumns(), LineSaveModel.class);
                            List<LineSaveModel> termsModel = lineSaveModels.stream().filter(s->(LineConstants.AGG_TYPE.TERMS.equals(s.getAggType()))).collect(Collectors.toList());
                            if(CollectionUtils.isNotEmpty(termsModel)){
                                if(termsModel.size() <= 1){
                                    //只有标识字段
                                    lineSaveModels.remove(termsModel.get(0));
                                    lineSaveModels.add(new LineSaveModel("op_type","op_type","keyword",null,"1",1));
                                    line.setSaveColumns(JSONObject.toJSONString(lineSaveModels));
                                }else{
                                    //多个字段聚合
                                    List<LineSaveModel> mainModel = termsModel.stream().filter(s->(s.isMain())).collect(Collectors.toList());
                                    lineSaveModels.remove(mainModel.get(0));
                                    line.setSaveColumns(JSONObject.toJSONString(lineSaveModels));
                                }
                            }
                            List<Map<String, Object>> groupList = renderLineData(0,personwrapper, personQueryModel, line, currentTime);
                            if(CollectionUtils.isNotEmpty(groupList)){
                                personList.addAll(groupList);
                            }
                        }
                    }else{
                        //personList = BaseLineUtil.renderDataFromMysql(line,currentTime);
                        personList = BaseLineUtil.renderResultFromMysqlByDay(line,currentTime,startTime,endTime);
                    }
                }else {
                    //mysql类型
                    personList = new BaseLine4MysqlUtil().doline(line,currentTime);
                }
                String resultIndex = endIndex + TimeTools.format(yesterday, indexSufFormat);
                //保存基线数据
                switch (line.getSaveType()) {
                    case LineConstants.SAVE_TYPE.ES:
                        log.info("入es");
                        //创建基线索引模板
                        createTemplate(line);
                        //创建基线索引别名
                        commonService.create365Alias(resultIndex, endIndex+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
                        QueryTools.writeData(personList, resultIndex, personwrapper);
                        break;
                    case LineConstants.SAVE_TYPE.KAFKA:
                        log.info("入kafka");
                        kafkaSenderService.batchSend(endIndex,personList);
                        break;
                    case LineConstants.SAVE_TYPE.ES_AND_KAFAK:
                        log.info("入es+kafka");
                        //创建基线索引模板
                        createTemplate(line);
                        //创建基线索引别名
                        commonService.create365Alias(resultIndex, endIndex+"-", "insert_time", TimeTools.TIME_FMT_2, dataTime.substring(0, 4), true);
                        QueryTools.writeData(personList, resultIndex, personwrapper);
                        kafkaSenderService.batchSend(endIndex,personList);
                        break;
                    case LineConstants.SAVE_TYPE.MYSQL:
                        //创建mysql表
                        lineUtil.createTable(line);
                        //数据入mysql
                        lineUtil.save2mysql(line,personList,LineConstants.TABLE_TYPE.LINE);
                        break;
                    case LineConstants.SAVE_TYPE.MYSQL_AND_KAFAK:
                        //创建mysql表
                        lineUtil.createTable(line);
                        //数据入mysql
                        lineUtil.save2mysql(line,personList,LineConstants.TABLE_TYPE.LINE);
                         kafkaSenderService.batchSend(endIndex,personList);
                        break;

                }
                log.info("#####入库"+resultIndex+"数据："+personList.size()+"条");
            }
            //更新基线进度
            line.setSaveColumns(saveCols);
            //推送数据
            new LineMessageTools().sendMessage(line,currentTime,resultSize);
            lineResult.setResult(LineConstants.LINE_RESULT.SUCCESS);
            log.info("基线任务执行结束id："+id);
        }catch (Exception var1){
            lineResult.setResult(LineConstants.LINE_RESULT.FAILED);
            log.error(var1.getMessage(),var1);
        }finally {
            this.baseLineMapper.updateById(line);
            if(baseLineResult == null){
                //无结果记录时 为执行基线 新增本次基线结果记录
                baseLineResultMapper.insert(lineResult);
            }else{
                //有结果记录时为失败重新触发基线 更新结果记录
                baseLineResult.setMessage(lineResult.getMessage());
                baseLineResult.setResult(lineResult.getResult());
                baseLineResultMapper.updateById(baseLineResult);
            }
        }
    }

    public EsQueryModel buildQueryModelByNames(QueryTools.QueryWrapper wrapper, String[] indexs, String time,String format,Integer day,Integer endDay) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(day));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(endDay));
        List<String> indexList = wrapper.getIndexNames(indexs, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public EsQueryModel buildQueryModelByDate(QueryTools.QueryWrapper wrapper, String[] indexs, String time,String format,Date startTime,Date endTime) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(startTime);
        queryModel.setEndTime(endTime);
        List<String> indexList = wrapper.getIndexNames(indexs, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public void updateSummaryNum(Map<String,Object> data,BaseLine line){
        String sumaryIndex = LineConstants.NAME_PRE.PRE_SM+line.getSaveIndex();
        List<JSONObject> cols = JSONArray.parseArray(line.getSaveColumns(), JSONObject.class);
        List<String> termCols = cols.stream().filter(r -> LineConstants.AGG_TYPE.TERMS.equals(r.getString("aggType"))).map(i -> {
            return i.getString("dest");
        }).collect(Collectors.toList());
        if(LineConstants.SAVE_TYPE.MYSQL.equals(line.getSaveType()) || LineConstants.SAVE_TYPE.MYSQL_AND_KAFAK.equals(line.getSaveType())){
            //更新mysql中间值表summary_num
            new BaseLineUtil().createTable(line);
            Map<String,Object> param = new HashMap<>();
            param.put("table",sumaryIndex.replaceAll("-","_"));
            if(CollectionUtils.isNotEmpty(termCols)){
                StringBuffer where = new StringBuffer();
                int i = 0;
                for(String s : termCols){
                    if(data.containsKey(s)){
                        Object v = data.get(s);
                        if(i == 0){
                            where.append("WHERE ");
                        }else{
                            where.append("AND ");
                        }
                        where.append(s).append(" = ").append("'").append(v).append("'");
                        i++;
                    }
                }
                if(StringUtils.isNotEmpty(where.toString())){
                    param.put("where",where.toString());
                }
            }
            int maxSummary = this.baseLineMapper.queryMaxSummary(param);
            int gap = line.getRunNum()-maxSummary;
            param.put("gap",gap);
            this.baseLineMapper.updateSummary(param);
        }else{
            //更新es中间值表summary_num
            QueryTools.QueryWrapper wrapper = QueryTools.build();
            EsQueryModel queryModel = buildQueryModelNoTime(wrapper, sumaryIndex, "insert_time",1,true);
            if(CollectionUtils.isNotEmpty(termCols)){
                buildModelByTerms(queryModel,termCols,data);
            }
            SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
            if (searchResponse != null) {
                List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
                Object num = list.get(0).get("summary_num");
                if(num != null){
                    int summaryNum = Integer.parseInt(num.toString());
                    int gap = line.getRunNum()-summaryNum;
                    //更新summaryNum
                    queryModel.setSort(false);
                    queryModel.setCount(1000);
                    SearchResponse scoreResponse = null;
                    while (true) {
                        scoreResponse = wrapper.scrollQuery(queryModel, scoreResponse == null ? null : scoreResponse.getScrollId());
                        SearchHits hits = scoreResponse.getHits();
                        List<Map<String, String>> records = wrapper.wrapResponse(scoreResponse.getHits(), "insert_time");
                        if (hits.getHits() == null || hits.getHits().length == 0) {
                            break;
                        }
                        BulkRequest request = new BulkRequest();
                        for(Map<String, String> item : records){
                            if(item.containsKey("summary_num")){
                                Integer oldSummaryNum = Integer.parseInt(item.get("summary_num"));
                                Integer newNum = oldSummaryNum+gap;
                                String idx = item.get("_index");
                                String id = item.get("_id");
                                item.remove("_index");
                                item.remove("_id");
                                item.put("summary_num",newNum.toString());
                                request.add(new UpdateRequest(idx, QueryTools.TYPE, id).doc(JSONObject.toJSONString(item), XContentType.JSON));
                            }
                        }
                        BulkResponse response = EsCurdTools.batch(request);
                        if (response.hasFailures()) {
                            String s = response.buildFailureMessage();
                            log.error(s);
                        } else {
                            log.info("成功更新es日志数据量: " + list.size());
                        }
                    }
                }
            }
        }

    }

    void buildModelByTerms(EsQueryModel queryModel,List<String> termCols,Map<String,Object> data){
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        for(String s : termCols){
            if(data.containsKey(s)){
                query.must(QueryBuilders.termQuery(s,data.get(s)));
            }
        }
        queryModel.setQueryBuilder(query);
    }


    private EsQueryModel buildQueryModelNoTime(QueryTools.QueryWrapper wrapper, String index, String time,int count,Boolean sort) {
        EsQueryModel queryModel = new EsQueryModel();
        // 设置时间字段
        queryModel.setCount(count);
        if(sort){
            queryModel.setSortOrder(SortOrder.DESC);
            queryModel.setSort(true);
            queryModel.setSortFields(new String[]{"summary_num"});
        }
        queryModel.setIndexName(index);
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(false);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }
}
