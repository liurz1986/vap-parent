package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.constant.PageConstants;
import com.vrv.vap.admin.common.excel.ExcelInfo;
import com.vrv.vap.admin.common.excel.out.ExcelData;
import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.common.excel.out.WriteHandler;
import com.vrv.vap.admin.common.util.ES7Tools;
import com.vrv.vap.admin.common.util.PathTools;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.ExploreService;
import com.vrv.vap.admin.service.IndexService;
import com.vrv.vap.admin.vo.ListQuery;
import com.vrv.vap.admin.vo.QueryModel;
import com.vrv.vap.admin.vo.StatisticsQuery;
import com.vrv.vap.admin.vo.TrendQuery;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by lizj on 2018/07/11.
 */
@Service
@Transactional
public class ExploreServiceImpl extends BaseServiceImpl<Entity> implements ExploreService {

    public static final String TOTAL = "total";
    @Resource
    private IndexService indexService;

    Logger log = LoggerFactory.getLogger(ExploreServiceImpl.class);

    private static final Integer COUNT = 3000;

    @Override
    public List<StatisticsModel> queryStatistics(List<Edge> edges, StatisticsQuery param) {
        List<StatisticsModel> result = new ArrayList<>();
        if (edges.size() > 0) {
            for (Edge edge : edges) {
                result.add(this.getStatstics(edge, param));
            }
        }
        return result;
    }

    @Override
    public Map<String, Object> queryList(Edge edge, ListQuery param) {
        Map<String, Object> result = new HashMap<>();
        QueryModel queryModel = new QueryModel();
        queryModel.setStart_(param.getStart_());
        queryModel.setCount_(param.getCount_());
        List<String> indexs = ES7Tools.getIndexList(edge.getIndexName(), edge.getTimeField(), String.valueOf(param.getStartTime().getTime()), String.valueOf(param.getEndTime().getTime()));
        BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(edge.getSearchField(), param.getValue()));
        //安全域过滤
        this.generateDomainQuery(query,edge.getIndexName());
        //内置条件拼装
        String builtInCondition = edge.getBuiltInCondition();
        this.generateBulitInCondition(builtInCondition,query);
        queryModel.setTimeField(edge.getTimeField());
        queryModel.setStartTime(param.getStartTime());
        queryModel.setEndTime(param.getEndTime());
        queryModel.setIndexNames(indexs.toArray(new String[indexs.size()]));
        queryModel.setQueryBuilder(query);
        queryModel.setUseTimeRange(true);
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{edge.getTimeField()});
        queryModel.setSortOrder(SortOrder.DESC);
        //目标字段聚合
        Integer goalFieldAggr = edge.getGoalFieldAggr();
        if (goalFieldAggr.equals(PageConstants.IS_OK) && param.isUseFieldAggr()) {
            result = this.getGoalFieldAggr(edge,queryModel,result);
        } else {
            ES7Tools.QueryWrapper wrapper = ES7Tools.build();
            SearchResponse response = wrapper.getSearchResponse(queryModel);
            if (null == response) {
                result.put("list", null);
                result.put(TOTAL, 0);
            } else {
                result.put("list", this.buildResultDataList(response, edge));
                result.put(TOTAL, response.getHits().getTotalHits());
            }
        }
        return result;
    }

    @Override
    public Export.Progress exportList(Edge edge, ListQuery param,String[] fields,String[] fieldDesc,Map<String,Map<String,String>> dicMap) {
        QueryModel queryModel = new QueryModel();
        queryModel.setStart_(param.getStart_());
        queryModel.setCount_(100);
        List<String> indexs = ES7Tools.getIndexList(edge.getIndexName(), edge.getTimeField(), String.valueOf(param.getStartTime().getTime()), String.valueOf(param.getEndTime().getTime()));
        BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(edge.getSearchField(), param.getValue()));
        if(StringUtils.isNotEmpty(edge.getGoalField()) && StringUtils.isNotEmpty(param.getGoalFieldValue())) {
            query = query.must(QueryBuilders.termQuery(edge.getGoalField(), param.getGoalFieldValue()));
        }
        //获取所有的字段
        List<Map<String, String>> allFieldsList = this.getAllFieldList(edge.getIndexName());
        final List<String> timeList = getTimeFields(allFieldsList);
        //安全域过滤
        this.generateDomainQuery(query,edge.getIndexName());
        //内置条件拼装
        String builtInCondition = edge.getBuiltInCondition();
        this.generateBulitInCondition(builtInCondition,query);
        queryModel.setTimeField(edge.getTimeField());
        queryModel.setStartTime(param.getStartTime());
        queryModel.setEndTime(param.getEndTime());
        queryModel.setIndexNames(indexs.toArray(new String[indexs.size()]));
        queryModel.setQueryBuilder(query);
        queryModel.setUseTimeRange(true);
        TimeValue keepAlive = new TimeValue(ES7Tools.ES_CACHE_TIME);
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        SearchResponse response = wrapper.getSearchResponseScroll(queryModel,keepAlive);
        String scrollId = response.getScrollId();
        final long total = response.getHits().getTotalHits().value > ES7Tools.getExportMax() ? ES7Tools.getExportMax()  : response
                .getHits().getTotalHits().value;
        List<ExcelData> excelDataList = new ArrayList<>();
        ExcelInfo info = new ExcelInfo("探索明细" ,fields,fieldDesc,"明细",true, PathTools.getExcelPath("探索明细"),null);
        ExcelData data = new ExcelData(info, total, new ArrayList<>());
        excelDataList.add(data);
        Export.Progress progress = Export.build(excelDataList).start(WriteHandler.fun(p -> {
            long leftCount = total;
            List<ResultModel> resultModels =  this.buildResultDataList(response, edge);
            int count = toExcel(resultModels, p, fields,dicMap,timeList,leftCount);

            while (true) {
                if (count >= total) {
                    break;
                }
                SearchResponse response2 = wrapper.getSearchResponseScrollById(scrollId,keepAlive);
                List<ResultModel> resultModels2 =  this.buildResultDataList(response2, edge);
                leftCount = total - count;
                count += toExcel(resultModels2, p, fields,dicMap,timeList,leftCount);
            }

        }));
        return  progress;
    }


    private int toExcel(List<ResultModel> resultModels, Export.Progress pro, final String[] fields,Map<String,Map<String,String>> dicMap,List<String> timeFields,long leftCount) {
        int num = 0;
        List<Map<String,Object>> writeList = new ArrayList<>();
        for (ResultModel resultModel : resultModels) {
            num++;
            try {
                Map<String, Object> dataOrigin = JSON.parseObject(resultModel.getDataJson(),Map.class);
                Map<String,Object> data = new HashedMap();
                for(int i=0;i<fields.length;i++){
                    String field = fields[i];
                    Object value =  dataOrigin.get(field);
                    if(dicMap.containsKey(field) && value!=null&&dicMap.get(field).containsKey(value)){
                        value = dicMap.get(field).get(value);
                    }
                    if(timeFields!=null&&timeFields.contains(fields[i])){
                        value = TimeTools.utc2Local(value);
                    }
                    data.put(fields[i],value);
                }
                writeList.add(data);

            } catch ( IllegalArgumentException  e) {
                log.error("", e);
            }
            if (num >= leftCount) {
                break;
            }
        }
        pro.writeBatchMap(0,writeList);
        return num;
    }

    private  List<String> getTimeFields(List<Map<String,String>> fieldMapList){

        List<String> dateFields = new ArrayList<>();
        for(Map<String,String> fieldMap :fieldMapList){
            String type = fieldMap.get("type");
            String name = fieldMap.get("name");
            if(type!=null&&type.equals("date")){
                dateFields.add(name);
            }
        }
        return dateFields;

    }

    private List<Map<String, String>> getAllFieldList(String index) {
        List<Map<String, String>> allFieldsList = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper();
        String[] indexArr = index.split(",");
        if (indexArr != null && indexArr.length > 0) {
            for (int i = 0;i < indexArr.length;i++) {
                List<DiscoverIndex> indexList = indexService.findByProperty(DiscoverIndex.class,"indexid",indexArr[i]);
                if (!CollectionUtils.isEmpty(indexList)) {
                    DiscoverIndex discoverIndex = indexList.get(0);
                    String allFeidlStr = discoverIndex.getIndexfields();
                    try {
                        List<Map<String, String>> fieldsList = new ArrayList<>();
                        fieldsList = mapper.readValue(allFeidlStr, fieldsList.getClass());
                        allFieldsList.addAll(fieldsList);

                    } catch (IOException e) {
                        log.error("",e);
                    }
                }
            }
        }
        return allFieldsList;
    }

    @Override
    public Map<String, Object> queryDetail(Edge edge, ListQuery param) {
        Map<String, Object> result = new HashMap<>();
        QueryModel queryModel = new QueryModel();
        queryModel.setStart_(param.getStart_());
        queryModel.setCount_(param.getCount_());
        List<String> indexs = ES7Tools.getIndexList(edge.getIndexName(), edge.getTimeField(), String.valueOf(param.getStartTime().getTime()), String.valueOf(param.getEndTime().getTime()));
        BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(edge.getSearchField(), param.getValue()));
        query = query.must(QueryBuilders.termQuery(edge.getGoalField(), param.getGoalFieldValue()));
        //安全域过滤
        this.generateDomainQuery(query,edge.getIndexName());
        //内置条件拼装
        String builtInCondition = edge.getBuiltInCondition();
        this.generateBulitInCondition(builtInCondition,query);
        queryModel.setTimeField(edge.getTimeField());
        queryModel.setStartTime(param.getStartTime());
        queryModel.setEndTime(param.getEndTime());
        queryModel.setIndexNames(indexs.toArray(new String[indexs.size()]));
        queryModel.setQueryBuilder(query);
        queryModel.setUseTimeRange(true);
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        if (null == response) {
            result.put("list", null);
            result.put(TOTAL, 0);
        } else {
            result.put("list", this.buildResultDataList(response, edge));
            result.put(TOTAL, response.getHits().getTotalHits());
        }

        return result;
    }

    @Override
    public List<TrendModel> queryTrend(Edge edge, TrendQuery param) {
        List<TrendModel> result = new ArrayList<>();
        QueryModel queryModel = new QueryModel();
        List<String> indexs = ES7Tools.getIndexList(edge.getIndexName(), edge.getTimeField(), String.valueOf(param.getStartTime().getTime()), String.valueOf(param.getEndTime().getTime()));
        BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(edge.getSearchField(), param.getSearchValue())).must(QueryBuilders.termQuery(edge.getGoalField(), param.getGoalValue()));
        //安全域过滤
        this.generateDomainQuery(query,edge.getIndexName());
        //内置条件拼装
        String builtInCondition = edge.getBuiltInCondition();
        this.generateBulitInCondition(builtInCondition,query);
        queryModel.setTimeField(edge.getTimeField());
        queryModel.setStartTime(param.getStartTime());
        queryModel.setEndTime(param.getEndTime());
        queryModel.setIndexNames(indexs.toArray(new String[indexs.size()]));
        queryModel.setQueryBuilder(query);
        queryModel.setUseTimeRange(true);
        // 按时间分桶统计
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(edge.getTimeField());
        dateAgg.dateHistogramInterval(DateHistogramInterval.DAY);
        dateAgg.timeZone(ZoneId.of("Asia/Shanghai"));
        dateAgg.format("yyyy-MM-dd HH:mm:ss");
        queryModel.setAggregationBuilder(dateAgg);
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        Response response = wrapper.getAggResponse(queryModel);
        if (response != null) {
            try {
                String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> aggMap = mapper.readValue(responseStr, Map.class);
                if (aggMap != null && aggMap.containsKey("aggregations")){
                    Map<String, Object> dataAggMap = (Map<String, Object> )aggMap.get("aggregations");
                    if(dataAggMap!=null&&dataAggMap.containsKey("dateAgg"))
                    {
                        Map<String, Object> bucketsMap = (Map<String, Object> )dataAggMap.get("dateAgg");
                        if(bucketsMap.containsKey("buckets")) {
                            List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                            aggItems.stream().forEach(item->{
                                TrendModel model = new TrendModel();
                                model.setDateFlag(String.valueOf(item.get("key_as_string")).substring(0, 10));
                                model.setCount(Long.valueOf(String.valueOf(item.get("doc_count"))));
                                result.add(model);
                            });
                        }
                    }
                }


            } catch (IOException e) {
                log.error("",e);
            }

        }
        return result;
    }

    /**
     * 安全域查询
     * @param query
     * @param indexName
     * @return
     */
    private void generateDomainQuery(BoolQueryBuilder query,String indexName) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        Map userExtends = (Map) session.getAttribute(Global.SESSION.USER_EXTENDS);
        List<String> codeList = new ArrayList<>();
        if (userExtends != null && userExtends.get("authorityType") != null && userExtends.get("authorityType").equals(PageConstants.IS_OK)) {
            Map userDomain = (Map) session.getAttribute(Global.SESSION.DOMAIN);
            if (userDomain != null) {
                Iterator iterator = userDomain.keySet().iterator();
                while (iterator.hasNext()) {
                    String domainCode = (String) iterator.next();
                    codeList.add(domainCode);
                }
            }
        }
        List<DiscoverIndex> indexList = indexService.findByProperty(DiscoverIndex.class,"indexid",indexName);
        if (CollectionUtils.isEmpty(indexList)) {
            return;
        }
        DiscoverIndex discoverIndex = indexList.get(0);
        String domainFieldName = discoverIndex.getDomainFieldName();
        if (CollectionUtils.isNotEmpty(codeList) && StringUtils.isNotEmpty(domainFieldName)) {
            query.must(QueryBuilders.termsQuery(domainFieldName,codeList));
        }
        return;
    }
    public Map<String, Object> getGoalFieldAggr(Edge edge, QueryModel queryModel, Map<String, Object> result) {
        if (edge.getGoalField() != null) {
            TermsAggregationBuilder goalFieldAgg = AggregationBuilders.terms("goalFieldAgg").field(edge.getGoalField());

            goalFieldAgg.size(COUNT);
            queryModel.setAggregationBuilder(goalFieldAgg);
            ES7Tools.QueryWrapper wrapper = ES7Tools.build();
            Response response = wrapper.getAggResponse(queryModel);
            if (response != null) {
                try {
                    String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, Object> aggMap = mapper.readValue(responseStr, Map.class);
                    if (aggMap != null && aggMap.containsKey("aggregations")) {
                        Map<String, Object> goalAggMap = (Map<String, Object>) aggMap.get("aggregations");
                        if (goalAggMap != null && goalAggMap.containsKey("goalFieldAgg")) {
                            Map<String, Object> bucketsMap = (Map<String, Object>) goalAggMap.get("goalFieldAgg");
                            if (bucketsMap.containsKey("buckets")) {
                                List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                                if (CollectionUtils.isNotEmpty(aggItems)) {
                                    aggItems = aggItems.stream().filter(item -> !"".equals(item.get("key"))).collect(Collectors.toList());
                                    Integer size = aggItems.size();
                                    Integer from = size >= queryModel.getStart_() ? queryModel.getStart_() : size;
                                    Integer to = size >= (queryModel.getStart_() + queryModel.getCount_()) ? queryModel.getStart_() + queryModel.getCount_() : size;
                                    List<Map<String, Object>> subList = aggItems.subList(from,to);
                                    result.put("list",subList);
                                    result.put("total",aggItems.size());
                                    return result;
                                }
                            }
                        }
                    }
                } catch (IOException e) {
                    log.error("", e);
                }
            }
        }
        result.put("list", null);
        result.put("total", 0);
        return result;
    }

    private List<ResultModel> buildResultDataList(SearchResponse response, Edge edge) {
        List<ResultModel> result = new ArrayList<>();
        String contentField = edge.getGoalField();
        Iterator<SearchHit> it = response.getHits().iterator();
        while (it.hasNext()) {
            ResultModel model = new ResultModel();
            SearchHit sh = it.next();
            Map<String, Object> resource = sh.getSourceAsMap();
            model.setDataJson(new Gson().toJson(resource));
            // 类似于复制粘贴这种关系，没有目标字段
            if (contentField != null &&  resource!=null ) {
                model.setContent(  resource.get(contentField) == null?"":resource.get(contentField).toString());
            }
            result.add(model);
        }
        return result;
    }

    private StatisticsModel getStatstics(Edge edge, StatisticsQuery param) {
        StatisticsModel result = new StatisticsModel();
        result.setEdgeId(edge.getId());
        result.setEdgeName(edge.getName());
        QueryModel queryModel = new QueryModel();
        List<String> indexs = ES7Tools.getIndexList(edge.getIndexName(), edge.getTimeField(), String.valueOf(param.getStartTime().getTime()), String.valueOf(param.getEndTime().getTime()));
        BoolQueryBuilder query = QueryBuilders.boolQuery().must(QueryBuilders.termQuery(edge.getSearchField(), param.getValue()));
        //安全域过滤
        this.generateDomainQuery(query,edge.getIndexName());
        //内置条件拼装
        String builtInCondition = edge.getBuiltInCondition();
        this.generateBulitInCondition(builtInCondition,query);
        queryModel.setTimeField(edge.getTimeField());
        queryModel.setStartTime(param.getStartTime());
        queryModel.setEndTime(param.getEndTime());
        queryModel.setIndexNames(indexs.toArray(new String[indexs.size()]));
        queryModel.setQueryBuilder(query);
        queryModel.setCount_(0);
        queryModel.setUseTimeRange(true);
        //目标字段聚合
        Integer goalFieldAggr = edge.getGoalFieldAggr();
        if (goalFieldAggr.equals(PageConstants.IS_OK)) {
            Map<String,Object> aggResult = new HashMap<>();
            aggResult = this.getGoalFieldAggr(edge,queryModel,aggResult);
            result.setCount(Long.valueOf((Integer) aggResult.get(TOTAL)));
        } else {
            ES7Tools.QueryWrapper wrapper = ES7Tools.build();
            SearchResponse response = wrapper.getSearchResponse(queryModel);
            if (null == response) {
                result.setCount(0L);
            } else {
                result.setCount(response.getHits().getTotalHits().value);
            }
        }
        return result;
    }

    private void generateBulitInCondition(String builtInCondition,BoolQueryBuilder query) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (StringUtils.isNotEmpty(builtInCondition)) {
                Map<String, Object> map = objectMapper.readValue(JsonSanitizer.sanitize(builtInCondition), Map.class);
                if (map.containsKey("must") && map.get("must") != null) {
                    List<Map<String, Object>> musts = (List) map.get("must");
                    List<QueryBuilder> musts_translate = this.translateESSentense(musts);
                    for (QueryBuilder queryBuilder : musts_translate) {
                        query.must(queryBuilder);
                    }
                }
                if (map.containsKey("should") && map.get("should") != null) {
                    List<Map<String,Object>> shoulds = (List) map.get("should");
                    List<QueryBuilder> should_translate = this.translateESSentense(shoulds);
                    for (QueryBuilder queryBuilder : should_translate) {
                        query.should(queryBuilder);
                    }
                }
                if (map.containsKey("must_not") && map.get("must_not") != null) {
                    List<Map<String,Object>> mustNots = (List) map.get("must_not");
                    List<QueryBuilder> mustNot_translate = this.translateESSentense(mustNots);
                    for (QueryBuilder queryBuilder : mustNot_translate) {
                        query.mustNot(queryBuilder);
                    }
                }
            }
        } catch (IOException e) {
            log.error("",e);
        }
    }

    /**
     * 把自定义的语句转换成ES语句
     *
     * @param sentenceList
     * @return
     */
    private List<QueryBuilder> translateESSentense(List<Map<String, Object>> sentenceList) {
        List<QueryBuilder> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sentenceList)) {
            for (Map<String, Object> sentence : sentenceList) {
                String field = (String) sentence.get("field");
                String oper = (String) sentence.get("oper");
                String value = (String) sentence.get("value");
                QueryBuilder queryBuilder = null;
                if (">".equals(oper)) {
                    queryBuilder = QueryBuilders.rangeQuery(field).gt(value);
                } else if (">=".equals(oper)) {
                    queryBuilder = QueryBuilders.rangeQuery(field).gte(value);
                } else if ("<".equals(oper)) {
                    queryBuilder = QueryBuilders.rangeQuery(field).lt(value);
                } else if ("<=".equals(oper)) {
                    queryBuilder = QueryBuilders.rangeQuery(field).lte(value);
                } else if ("=".equals(oper)) {
                    queryBuilder = QueryBuilders.termQuery(field,value);
                } else if ("like".equals(oper)) {
                    queryBuilder = QueryBuilders.wildcardQuery(field,value);
                } else if ("phrase_like".equals(oper)) {
                    queryBuilder = QueryBuilders.prefixQuery(field,value);
                } else if ("exist".equals(oper)) {
                    queryBuilder = QueryBuilders.existsQuery(field);
                }
                result.add(queryBuilder);
            }
        }
        return result;
    }

}
