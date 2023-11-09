package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.config.IndexIndicesConfig;
import com.vrv.vap.admin.common.constant.PageConstants;
import com.vrv.vap.admin.common.excel.ExcelInfo;
import com.vrv.vap.admin.common.excel.out.ExcelData;
import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.common.excel.out.WriteHandler;
import com.vrv.vap.admin.common.manager.ElasticSearchManager;
import com.vrv.vap.admin.common.util.*;
import com.vrv.vap.admin.model.BaseDictAll;
import com.vrv.vap.admin.model.DiscoverIndex;
import com.vrv.vap.admin.model.HeatModel;
import com.vrv.vap.admin.model.IndexTopic;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.service.IndexService;
import com.vrv.vap.admin.service.IndexTopicService;
import com.vrv.vap.admin.service.SearchService;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.ConditionGenerateQuery;
import com.vrv.vap.admin.vo.EsSearchQuery;
import com.vrv.vap.admin.vo.QueryModel;
import com.vrv.vap.common.constant.Global;
import net.sf.json.JSONArray;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.*;
import org.slf4j.Logger;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.*;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.plugins.SearchPlugin;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchModule;
import org.elasticsearch.search.aggregations.AbstractAggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Transactional
public class SearchServiceImpl implements SearchService {

    public static final String SHOULD = "should";
    public static final String MUST_NOT = "must_not";
    public static final String MUST = "must";
    public static final String BOOL = "bool";
    public static final String AUTHORITY_TYPE = "authorityType";
    public static final String QUERY = "query";
    public static final String FIELDS = "fields";
    public static final String TOTAL = "total";
    public static final String HITS = "hits";
    public static final String TRACK_TOTAL_HITS = "track_total_hits";
    private static Logger log = LoggerFactory.getLogger(SearchServiceImpl.class);
    //kibana索引
	@Value("${elk.vap.index}")
    private String INDEX;
	//分段时间
    @Value("${elk.segmentDay:1}")
	private Integer SEGMENT_DAY;

    @Value("${version:audit}")
    private String VERSIION;

    @Value("${elk.vap.filePath:/opt/SecAudit/vrv/vap/esFile}")
    private String filePath;

    @Value("${elk.zipPwd:123qwz}")
    private String zipPwd;

    //信创
    private String VERSION_XC = "audit_xc";

    private final static String INDEX_LEVEL = "indices";

    private final static String TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    private final static Integer GROUP_NUM_CONST = 50;

    private static final String NO_DOMAIN = "NODOMAIN";

    private static final String MODE_NORMAL = "normal";

    private static final String MODE_TOPIC = "topic";

    // 字段top统计总条数
    private final static Integer FIELD_QUERY_SIZE = 1000;

    private String fields;

    @Resource
    private IndexService indexService;

    @Resource
    private IndexTopicService indexTopicService;

    @Autowired
    private BaseDictAllService baseDictAllService;

    private static IndexIndicesConfig indicesConfig = null;

    private static IndexIndicesConfig getIndicesConfig() {
        if (indicesConfig == null) {
            indicesConfig = SpringContextUtil.getBean(IndexIndicesConfig.class);
        }
        return indicesConfig;
    }

    @Override
    public String searchGlobalContent(EsSearchQuery esSearchQuery) {
        final String queryJsonStr = this.parseQueryStr(esSearchQuery);
        //查询结果
        List<String> list = this.parseTitle(esSearchQuery.getIndex());
        if (CollectionUtils.isNotEmpty(list)) {
            return this.searchGlobalContent(list, queryJsonStr);
        }
        return null;
    }

    /**
     * 搜索内容
     */
    public String searchGlobalContent(List<String> indexList, String queryJsonStr) {
        String indexStr = StringUtils.join(indexList.toArray(), ",");
        if (StringUtils.isNotEmpty(indexStr)) {
            String method="POST";
            String endpoint = "/"+indexStr+"/_search";
            HttpEntity entity = new NStringEntity(queryJsonStr, ContentType.APPLICATION_JSON);
            try {

                log.info("================查询条件-索引:"+ LogForgingUtil.validLog(indexStr));
                log.info("================查询条件-内容:"+ LogForgingUtil.validLog(queryJsonStr));
                Request request = new Request(method,endpoint);
                request.setEntity(entity);
                Response response = ElasticSearchManager.getClient().getLowLevelClient().performRequest(request);
                String responseStr = EntityUtils.toString(response.getEntity());
                log.info("================查询结果-response:"+responseStr);
                return responseStr;
            } catch (IOException e) {
                log.info("================查询异常================");
                log.error("",e);
            }
        }

        return "";

    }

    @Override
    public List<Map<String, Object>> searchTopicCount(EsSearchQuery esSearchQuery) {
        List<Map<String,Object>> resultList = new ArrayList<>();
        Integer topicId = esSearchQuery.getTopicId();
        List<IndexTopic> topicList = indexTopicService.findByProperty(IndexTopic.class,"parentId",topicId);
        if (CollectionUtils.isNotEmpty(topicList)) {
            for (IndexTopic indexTopic : topicList) {
                Map<String,Object> result = new HashMap<>();
                Integer id = indexTopic.getId();
                Integer total = 0;
                String queryJsonStr = this.parseQueryStr(esSearchQuery);
                if (StringUtils.isNotEmpty(indexTopic.getIndexId())) {
                    String indexes = indexTopic.getIndexId();
                    String startTime = esSearchQuery.getStartTime();
                    String endTime = esSearchQuery.getEndTime();
                    List<String> list = this.queryIndexListByTime(indexes,startTime,endTime);
                    if (CollectionUtils.isNotEmpty(list)) {
                        total = this.getContentCount(list, queryJsonStr);
                    }
                }
                result.put("id",id);
                result.put(TOTAL,total);
                resultList.add(result);
            }
        }
        return resultList;
    }

    private List<String> parseTitle(String title) {
        if (StringUtils.isEmpty(title)) {
            return null;
        }
        JSONArray array = JSONArray.fromObject(title);
        List<String> list = array.toList(array);
        return list;
    }

    private String parseQueryStr(EsSearchQuery esSearchQuery) {
        String queryJsonStr;
        String mode = esSearchQuery.getMode();
        if (StringUtils.isEmpty(mode) || MODE_NORMAL.equals(mode) || MODE_TOPIC.equals(mode)) {
            queryJsonStr = this.escapeQueryStr(esSearchQuery.getQueryJsonStr());
        } else {
            queryJsonStr = this.escapeQueryProStr(esSearchQuery.getQueryJsonStr());
        }
        return queryJsonStr;
    }

    public Integer getContentCount(List<String> indexList, String queryJsonStr) {
        String content = this.searchGlobalContent(indexList,queryJsonStr);
        ObjectMapper objectMapper = new ObjectMapper();
        int total = 0;
        try {
            Map<String,Object> body = objectMapper.readValue(content,Map.class);
            if (body != null && body.containsKey(HITS)) {
                Map<String,Object> hits = (Map<String, Object>) body.get(HITS);
                if (hits != null && hits.containsKey(TOTAL) ) {
                    if(hits.get(TOTAL) instanceof Map){
                        total = (int)((Map) hits.get(TOTAL)).get("value");
                    }else {
                        total = (int) hits.get(TOTAL);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }

    @Override
    public List<Map<String, Object>> searchFiledList(EsSearchQuery esSearchQuery) {
        final String index = esSearchQuery.getIndex();
        if (StringUtils.isNotEmpty(index)) {
            final String timeFieldName = esSearchQuery.getTimeFieldName();
            final String startTime = esSearchQuery.getStartTime();
            final String endTime = esSearchQuery.getEndTime();
            final String fieldName = esSearchQuery.getFieldName();
            final String queryStr = StringUtils.strip(this.escapeFieldQueryStr(esSearchQuery.getQueryJsonStr()),"{}");
            Date start = TimeTools.toDate(startTime, TIME_FORMAT);
            Date end = TimeTools.toDate(endTime, TIME_FORMAT);
            final Integer count = esSearchQuery.getCount() != null ? esSearchQuery.getCount() : 10;
            //时间范围内索引
            List<String> indexList = this.queryIndexListByTime(index, startTime, endTime);
            //查询串
            String queryJsonStr = "{\"size\": " + count + ",\"query\": {\"bool\": {"+queryStr+",\"filter\": [{\"range\": {\"" + timeFieldName + "\": {\"gte\": \"" + start.getTime() + "\",\"lte\": \"" + end.getTime() + "\",\"format\": \"epoch_millis\"}}}]}},\n" +
                    "\"aggregations\": {\"CATEGORY\": {\"terms\": {\"field\": \"" + fieldName + "\",\"size\": " + FIELD_QUERY_SIZE + ",\"order\": {\"_count\": \"desc\"}}}}}";
            log.info(LogForgingUtil.validLog(queryJsonStr));
            List<Map<String,Object>> resultList = this.searchFiledContent(indexList, queryJsonStr);
            if (CollectionUtils.isNotEmpty(resultList)) {
                if (count < resultList.size()) {
                    return resultList.subList(0,count);
                } else {
                    return resultList;
                }
            }
            return null;
        }
        return null;
    }

    @Override
    public List<Map<String,Object>> searchFiledContent(List<String> indexList, String queryJsonStr) {
        List<Map<String,Object>> resultList = new ArrayList<>();
        int total = 0;
        String responsStr = this.searchGlobalContent(indexList,queryJsonStr);
        if (StringUtils.isNotEmpty(responsStr)) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String,Object> body = objectMapper.readValue(responsStr,Map.class);
                if (body != null && body.containsKey("aggregations")) {
                    Map<String,Object> aggs = (Map<String, Object>) body.get("aggregations");
                    if (aggs != null && aggs.containsKey("CATEGORY")) {
                        Map<String,Object> category = (Map<String, Object>) aggs.get("CATEGORY");
                        if (category != null && category.containsKey("buckets")) {
                            resultList = (List<Map<String, Object>>) category.get("buckets");
                            if (CollectionUtils.isNotEmpty(resultList)) {
                                for (Map<String,Object> bucket : resultList) {
                                    bucket.put("count",bucket.get("doc_count"));
                                }
                                return resultList;
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("",e);
            }
        }
        return resultList;
    }

    @Override
	public List<String> queryIndexListByTime(String index, String startTime, String endTime) {
        Date start = TimeTools.toDate(startTime,TIME_FORMAT);
        Date end = TimeTools.toDate(endTime,TIME_FORMAT);
        QueryModel queryModel = new QueryModel();
        queryModel.setStartTime(start);
        queryModel.setEndTime(end);
        queryModel.setIndexName(index);
        List<String> indexList = Arrays.asList(ES7Tools.getIndexNames(queryModel));
        return indexList;
	}

    @Override
    public List<Object> querySegIndexListByTime(String index, String startTime, String endTime) {
        Set<List<String>> resultList = new HashSet<>();
        Date start = TimeTools.toDate(startTime,TIME_FORMAT);
        Date end = TimeTools.toDate(endTime,TIME_FORMAT);
        // 分片起止时间
        Date segStart = TimeTools.getDateBefore(end,SEGMENT_DAY - 1);
        Date segEnd = end;
        // 过滤索引名称后面的"*"以及"-*"
        index = filterIndexName(index);
        // 主题下包含按月查询的索引时，不分段
        if (hasMonthIndex(index)) {
            resultList.add(queryIndexListByTime(index,startTime,endTime));
            return Arrays.asList(resultList.toArray());
        }
        QueryModel queryModel = new QueryModel();
        queryModel.setIndexName(index);
        while (segStart.after(start)) {
            queryModel.setStartTime(segStart);
            queryModel.setEndTime(segEnd);
            List<String> indexList = Arrays.asList(ES7Tools.getIndexNames(queryModel));
            if (CollectionUtils.isNotEmpty(indexList)) {
                resultList.add(indexList);
            }
            segEnd = TimeTools.getDateBefore(segStart,1);
            segStart = TimeTools.getDateBefore(segEnd,SEGMENT_DAY - 1);
        }
        queryModel.setStartTime(start);
        queryModel.setEndTime(segEnd);
        List<String> indexList = Arrays.asList(ES7Tools.getIndexNames(queryModel));
        if (CollectionUtils.isNotEmpty(indexList)) {
            resultList.add(indexList);
        }
        return Arrays.asList(resultList.toArray());
    }

    private boolean hasMonthIndex(String indexes) {
        Map<String,IndexIndicesConfig.AliasConfig> aliasConfig = getIndicesConfig().getAliasConfig();
        String[] indexNames = indexes.split(",");
        if (indexNames.length > 0) {
            for (String indexName : indexNames) {
                if(aliasConfig.containsKey(indexName)) {
                    String timeFormat = aliasConfig.get(indexName).getTimeFormat();
                    if (StringUtils.isNotEmpty(timeFormat) && timeFormat.length() < 10) {
                        return true;
                    }
                }

            }
        }
        return false;
    }

    private String filterIndexName(String indexes) {
        String result = "";
        String[] indexNames = indexes.split(",");
        if (indexNames.length > 0) {
            for (String indexName : indexNames) {
                if (indexName.endsWith("-*")) {
                    indexName = indexName.substring(0,indexName.indexOf("-*"));
                }
                if (indexName.endsWith("*")) {
                    indexName = indexName.substring(0,indexName.indexOf("*"));
                }
                result += indexName + ",";
            }
            if (result.length() > 0) {
                result = result.substring(0,result.length() - 1);
            }
        }
        return result;
    }

    @Override
    public Export.Progress export(EsSearchQuery esSearchQuery) {
        final String index =esSearchQuery.getIndex();
        final String startTime = esSearchQuery.getStartTime();
        final String endTime = esSearchQuery.getEndTime();
        final String selectFields = esSearchQuery.getSelectFields();

        String queryJsonStr = this.parseQueryStr(esSearchQuery);

        // 获取数据字典
       List<BaseDictAll> vData = baseDictAllService.findAll();
        if (vData == null) {
            return null;
        }

        // 获取所有parentType
        Map<String, Map<String, String>> dicMap = this.getDictMap(vData);
        //获取所有的字段
        List<Map<String, String>> allFieldsList = this.getAllFieldList(index);

        List<String> indexNameList = queryIndexListByTime(index,startTime,endTime);
        final String[][] fields = getFieldConfig(Arrays.asList(selectFields.split(",")),allFieldsList);
        Map<String,String> fieldMap =  getMapFields(Arrays.asList(selectFields.split(",")),allFieldsList);
        final List<String> timeList = getTimeFields(allFieldsList);
        final RestHighLevelClient client = ElasticSearchManager
                .getClient();
        TimeValue keepAlive = new TimeValue(ES7Tools.ES_CACHE_TIME);
        SearchResponse response =searchScroll(indexNameList,queryJsonStr,client,keepAlive);
        if (response == null) {
            return null;
        }
        String scrollId = response.getScrollId();
        final long total = response.getHits().getTotalHits().value > ES7Tools.getExportMax() ? ES7Tools.getExportMax()  : response
                .getHits().getTotalHits().value;
        List<ExcelData> excelDataList = new ArrayList<>();
        ExcelInfo info = new ExcelInfo("搜索明细" ,fields[0],fields[1],"明细",true,PathTools.getExcelPath("搜索明细"),null);
        ExcelData data = new ExcelData(info, total, new ArrayList<>());
        excelDataList.add(data);
        Export.Progress progress = Export.build(excelDataList).start(WriteHandler.fun(p -> {
            SearchResponse response2 = null;
            long leftCount = total;
            int count = toExcel(response, p, fields[0],fieldMap,dicMap,timeList,allFieldsList,leftCount);

            while (true) {
                if (count >= total) {
                    break;
                }
                SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(keepAlive);
                try {
                    response2 = client.searchScroll(searchScrollRequest, RequestOptions.DEFAULT.toBuilder().build());
                } catch (IOException e) {
                    log.error("",e);
                }
                leftCount = total - count;
                count += toExcel(response2, p, fields[0],fieldMap,dicMap,timeList,allFieldsList,leftCount);
            }
        }));
        return  progress;
    }


    @Override
    public Map exportCSV(EsSearchQuery esSearchQuery) {
        Map<String, Object> resMap = new HashMap<>();
        final String index =esSearchQuery.getIndex();
        final String startTime = esSearchQuery.getStartTime();
        final String endTime = esSearchQuery.getEndTime();

        String queryJsonStr = this.parseQueryStr(esSearchQuery);
        log.info("导出查询内容：" + LogForgingUtil.validLog(queryJsonStr));

        List<String> indexNameList = queryIndexListByTime(index,startTime,endTime);
        final RestHighLevelClient client = ElasticSearchManager.getClient();
        TimeValue keepAlive = new TimeValue(ES7Tools.ES_CACHE_TIME);
        SearchResponse response = searchScroll(indexNameList,queryJsonStr,client,keepAlive);
        if(response == null) {
            log.info("滚动查询失败");
            return resMap;
        }

        if (response.getHits().getTotalHits().value == 0) {
            log.info("查询数据为空");
            return resMap;
        }

        int page = (int) response.getHits().getTotalHits().value / 3000;

        String dateStr = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String folder = filePath + File.separator + "export" + File.separator + dateStr;
        toCSV(response, folder);

        String scrollId = response.getScrollId();
        for (int i = 0; i < page; i++) {
            //再次发送请求,并使用上次搜索结果的ScrollId
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(keepAlive);
            try {
                response = client.searchScroll(searchScrollRequest, RequestOptions.DEFAULT.toBuilder().build());
                toCSV(response, folder);
            } catch (IOException e) {
                log.error("滚动查询失败",e);
            }
        }

        // 打包
        FileUtils.createZipFile(folder + ".zip", folder, zipPwd);
        FileUtils.deleteDirectory(folder);
        resMap.put("fileName", dateStr);
        return resMap;
    }

    @Override
    public void importData(MultipartFile file) {
        RestHighLevelClient client = ElasticSearchManager.getClient();
        String folder = filePath + File.separator + "import" + File.separator
                + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        if (FileUtils.uploadFile(file, folder + ".zip")
                && FileUtils.unZipFile(folder + ".zip", folder, zipPwd)) {
            File[] files = new File(folder).listFiles();
            for (File fileInfo : files) {
                try (BufferedReader br = new BufferedReader(new FileReader(fileInfo))) {
                    BulkRequest bulkRequest = new BulkRequest();
                    Map<String, Object> valuesMap = new HashMap<>(64);
                    // 第一行字段
                    String[] fields = br.readLine().split(",");

                    String line;
                    int num = 1;
                    while ((line = br.readLine()) != null) {
                        String[] filedValues = line.split(",");
                        IndexRequest request = new IndexRequest();
                        request.index(filedValues[0]).type(filedValues[1]).id(filedValues[2]);
                        GetRequest getRequest = new GetRequest();
                        getRequest.index(filedValues[0]).type(filedValues[1]).id(filedValues[2]);
                        if (client.exists(getRequest, RequestOptions.DEFAULT.toBuilder().build())) {
                            continue;
                        }

                        for (int filedNum = 3; filedNum < filedValues.length; filedNum++) {
                            valuesMap.put(fields[filedNum], filedValues[filedNum].replace("\"", ""));
                        }
                        // 导入数据
                        request.source(valuesMap);
                        bulkRequest.add(request);
                        valuesMap.clear();
                        if (num % 2000 == 0) {
                            // 批量导入
                            client.bulk(bulkRequest, RequestOptions.DEFAULT.toBuilder().build());
                        }
                        num++;
                    }
                    // 导入每个文件最后不足2000条的数据
                    if (bulkRequest.requests().size() > 0) {
                        client.bulk(bulkRequest, RequestOptions.DEFAULT.toBuilder().build());
                    }
                } catch (IOException e) {
                    log.error("数据导入异常,文件名称：" + fileInfo.getName(), e);
                }
            }
            FileUtils.deleteDirectory(folder);
        }
    }

    @Override
    public void downloadFile(String fileName, HttpServletResponse response) {
        FileUtils.downloadFile(Paths.get(filePath, "export", CleanUtil.cleanString(fileName) + ".zip").toString(), response);
    }

    private void toCSV (SearchResponse response, String folder) {
        Map<String, List<SearchHit>> map = new LinkedHashMap<>();
        response.getHits().forEach(hit -> {
            map.computeIfAbsent(hit.getIndex(), k -> new ArrayList<>()).add(hit);
        });

        // 创建文件存放路径
        File dir = new File(folder);
        if(!dir.exists()){
            dir.mkdirs();
        }
        map.forEach((k, v) -> {
            String fileName = k + ".csv";
            FileWriter fw = null;
            try {
                File file = new File(Paths.get(folder, fileName).toString());
                if (file.exists()) {
                    fw = new FileWriter(file, true);
                } else {
                    fw = new FileWriter(file);
                    // 微软的excel文件需要通过文件头的bom来识别编码，所以写文件时，需要先写入bom头
                    byte[] uft8bom = {(byte) 0xef, (byte) 0xbb, (byte) 0xbf};
                    fw.write(new String(uft8bom));
                    if (v.size() > 0) {
                        fields = getFields(v.get(0));
                        fw.write(fields + "\r\n");
                    }
                }

                if (v.size() > 0) {
                    writeValues(v, fw, fields);
                }
            } catch (Exception e) {
                log.error("csv文件生成失败，文件名：" + fileName, e);
            } finally {
                if (fw != null) {
                    try {
                        fw.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private String getFields(SearchHit hit){
        StringBuilder fields = new StringBuilder();
        fields.append("index").append(",").append("type").append(",").append("id").append(",");
        Map<String,Object> map = hit.getSourceAsMap();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            String key = entry.getKey();
            fields.append(key).append(",");
        }
        return fields.deleteCharAt(fields.lastIndexOf(",")).toString();
    }

    private void writeValues(List<SearchHit> hits, FileWriter fw, String fields) throws IOException {
        if (StringUtils.isEmpty(fields)) {
            return;
        }
        StringBuilder values = new StringBuilder();

        for (SearchHit hit : hits) {
            values.append("\"").append(Optional.ofNullable(hit.getIndex()).orElse("")).append("\"").append(",");
            values.append("\"").append(Optional.ofNullable(hit.getType()).orElse("")).append("\"").append(",");
            values.append("\"").append(Optional.ofNullable(hit.getId()).orElse("")).append("\"").append(",");
            Map<String,Object> map = hit.getSourceAsMap();
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                values.append("\"").append(Optional.ofNullable(entry.getValue()).orElse("")).append("\"").append(",");
            }
            values.replace(values.length() - 1, values.length(), "\r\n");
            fw.write(values.toString());
            values.setLength(0);
        }
        fw.flush();
    }

    private Map<String, Map<String, String>> getDictMap(List<BaseDictAll> dictAllVoList) {
        Map<String, Map<String, String>> dicMap = new HashMap<>();
        Set parentTypeSet = new HashSet();
        if (CollectionUtils.isNotEmpty(dictAllVoList)) {
            for (BaseDictAll baseDictAll : dictAllVoList) {
                parentTypeSet.add(baseDictAll.getParentType());
            }
        }
        Iterator iterator = parentTypeSet.iterator();
        while (iterator.hasNext()) {
            String parentType = (String) iterator.next();
            Map<String, String> fieldMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(dictAllVoList)) {
                for (BaseDictAll baseDictAllVo : dictAllVoList) {
                    if (parentType.equals(baseDictAllVo.getParentType())) {
                        fieldMap.put(baseDictAllVo.getCode(),baseDictAllVo.getCodeValue());
                    }
                }
            }
            dicMap.put(parentType, fieldMap);
        }
        return dicMap;
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


    private  String[][] getFieldConfig(List<String> selectFields, List<Map<String,String>> allFieldList){

            String[][] fields = new String[2][selectFields.size()];
            int i =0;
            for(Map<String,String> fieldMap :allFieldList){
                if(selectFields.contains(fieldMap.get("name"))) {
                    fields[0][i] = fieldMap.get("name");
                    String value = fieldMap.get("nameDesc");
                    if(StringUtils.isEmpty(value)){
                        value = fieldMap.get("name");
                    }
                    fields[1][i] = value;
                    i++;
                    if (i == selectFields.size()) {
                        break;
                    }
                }
            }
            return fields;

    }

    @Override
    public Map<String, Object> generateQueryCondition(ConditionGenerateQuery query) {
        Map<String, Object> result = new HashMap<>();
        final String index = query.getIndex();
        final String startTime = query.getStartTime();
        final String endTime = query.getEndTime();
        //获取时间区间内索引
        List<String> indexList = this.queryIndexListByTime(index, startTime, endTime);
        //获取分段的时间区间索引
        List<Object> segIndexList = this.querySegIndexListByTime(index, startTime, endTime);
        //组装查询语句
        Map<String, Object> resultMap = this.generateQueryStr(query);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            result.put("indexList", objectMapper.writeValueAsString(indexList));
            result.put("segIndexList",objectMapper.writeValueAsString(segIndexList));
            result.put("queryStr", objectMapper.writeValueAsString(resultMap));
        } catch (JsonProcessingException e) {
            log.error("",e);
        }
        return result;
    }

    public Map<String, Object> generateQueryStr(ConditionGenerateQuery query) {
        Map<String, Object> resultMap = new LinkedHashMap<>();
        final Integer from = query.getStart_();
        final Integer size = query.getCount_();
        //分页参数组装
        resultMap.put("from", from);
        resultMap.put("size", size);
        //query语句生成
        resultMap.put(QUERY, this.generateQuery(query));
        //排序
        if (StringUtils.isNotEmpty(query.timeFieldName)) {
            resultMap.put("sort", this.generateSort(query.timeFieldName));
        }
        //聚合
//        if (StringUtils.isNotEmpty(query.timeFieldName)) {
//            resultMap.put("aggs", this.generateAggs(timeFieldName, startTime, endTime));
//        }
        //放开ES7最大命中条数
        if (size == 0) {
            resultMap.put("track_total_hits",true);
        }
        return resultMap;
    }

    /**
     * query语句生成
     *
     * @param
     * @return
     */
    private Map<String, Object> generateQuery(ConditionGenerateQuery query) {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> queryMap = new HashMap<>();
        Map<String, Object> boolMap = new HashMap<>();
        List<Map<String, Object>> mustList;
        List<Map<String, Object>> mustNotList = new ArrayList<>();
        List<Map<String, Object>> shouldList = new ArrayList<>();
        try {

            if (StringUtils.isNotEmpty(query.getQueryStr())) {
                Map<String, Object> map = objectMapper.readValue(JsonSanitizer.sanitize(query.getQueryStr()), Map.class);
                String mode = (String) map.get("mode");
                //关键词参数组装
                String querystring = (String) map.get("querystring");
                //暂时屏蔽
//                List<String> fields = map.containsKey("fields") ? (List<String>) map.get("fields") : null;
                List<String> fields = new ArrayList<>();
                mustList = this.generateCommonQuery(mode, querystring,fields, query);
                //must参数组装
                if (map.containsKey(MUST) && map.get(MUST) != null) {
                    List<Map<String, Object>> musts = (List) map.get(MUST);
                    List<Map<String, Object>> musts_translate = this.translateESSentense(musts);
                    if (!CollectionUtils.isEmpty(musts_translate)) {
                        for (Map<String, Object> must_translate : musts_translate) {
                            mustList.add(must_translate);
                        }
                    }
                }
                //安全域过滤
                String domainFieldName = query.getDomainFieldName();
                mustList = this.generateDomainQuery(domainFieldName,mustList);
                //should参数组装
                if (map.containsKey(SHOULD) && map.get(SHOULD) != null) {
                    List<Map<String, Object>> shoulds = (List) map.get(SHOULD);
                    shouldList = this.translateESSentense(shoulds);
                }
                //must_not参数组装
                if (map.containsKey(MUST_NOT) && map.get(MUST_NOT) != null) {
                    List<Map<String, Object>> mustNots = (List) map.get(MUST_NOT);
                    mustNotList = this.translateESSentense(mustNots);
                }
                boolMap.put(MUST, mustList);
                boolMap.put(SHOULD, shouldList);
                boolMap.put(MUST_NOT, mustNotList);
                queryMap.put(BOOL, boolMap);
            }
        } catch (IOException e) {
            log.error("",e);
        }
        return queryMap;
    }


    private List<Map<String, Object>> generateDomainQuery(String domainFieldName,List<Map<String, Object>> mustList) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        List<String> codeList = new ArrayList<>();
        Set<String> orgCodeSet = (Set<String>) session.getAttribute("_ORG");
        if (orgCodeSet != null) {
            Iterator iterator = orgCodeSet.iterator();
            while (iterator.hasNext()) {
                String orgCode = (String) iterator.next();
                codeList.add(orgCode);
            }
        }
        if (CollectionUtils.isNotEmpty(codeList) && StringUtils.isNotEmpty(domainFieldName)) {
            Map<String,Object> domainTerm = new HashMap<>();
            Map<String,Object> domainMap = new HashMap<>();
            domainMap.put(domainFieldName,codeList);
            domainTerm.put("terms",domainMap);
            mustList.add(domainTerm);
        }
        return mustList;
    }

    private List<Map<String, Object>> generateCommonQuery(String mode, String querystring,List<String> fields, ConditionGenerateQuery query) {
        List<Map<String, Object>> commonQuery = new ArrayList<>();

        Map<String, Object> queryStringMap = new HashMap<>();
        Map<String, Object> queryString = new HashMap<>();
        queryString.put(QUERY, querystring);
        queryString.put("analyze_wildcard", true);
        //常规模式
        if (StringUtils.isNotEmpty(mode) && (MODE_NORMAL.equals(mode) || MODE_TOPIC.equals(mode))) {
            queryString.put("minimum_should_match", "100%");
//            queryString.put("auto_generate_phrase_queries", true);
        }
        // 检索指定字段（隐藏字段不检索）
        if (CollectionUtils.isNotEmpty(fields)) {
            queryString.put(FIELDS,fields);
        }
        queryStringMap.put("query_string", queryString);
        if (!"*".equals(querystring)) {
            commonQuery.add(queryStringMap);
        }

        if (StringUtils.isNotEmpty(query.getTimeFieldName())) {

            Map<String, Object> map = new HashMap<>();
            Map<String, Object> rangeMap = new HashMap<>();
            Map<String, Object> timeMap = new HashMap<>();

            timeMap.put("from", query.startTime);
            timeMap.put("to", query.endTime);
            timeMap.put("format", "yyyy-MM-dd HH:mm:ss");
            timeMap.put("time_zone", "+08:00");
//            timeMap.put("gte", TimeTools.toDate(query.startTime, TimeTools.GMT_PTN).getTime());
//            timeMap.put("lte", TimeTools.toDate(query.endTime, TimeTools.GMT_PTN).getTime());
//            timeMap.put("format", "epoch_millis");
            rangeMap.put(query.getTimeFieldName(), timeMap);
            map.put("range", rangeMap);
            commonQuery.add(map);
        }
        return commonQuery;
    }


    private Map<String, Object> generateSort(String timeField) {
        Map<String, Object> sortMap = new HashMap<>();
        Map<String, Object> sortFieldInfo = new HashMap<>();
        sortFieldInfo.put("order", "desc");
        sortFieldInfo.put("unmapped_type", "boolean");
        sortMap.put(timeField, sortFieldInfo);
        return sortMap;
    }


    /**
     * 高亮语句生成
     *
     * @return
     */
    private Map<String, Object> generateHighlight() {
        Map<String, Object> highlight = new HashMap<>();
        List<String> preTagList = new ArrayList<>();
        preTagList.add("@kibana-highlighted-field@");
        highlight.put("pre_tags", preTagList);
        List<String> postTagList = new ArrayList<>();
        postTagList.add("@/kibana-highlighted-field@");
        highlight.put("post_tags", postTagList);
        Map<String, Object> fields = new HashMap<>();
        fields.put("*", new HashMap<>());
        highlight.put(FIELDS, fields);
        highlight.put("require_field_match", false);
        highlight.put("fragment_size", 2147483647);
        return highlight;
    }

    /**
     * 聚合语句生成
     *
     * @param timeFieldName
     * @param startTime
     * @param endTime
     * @return
     */
    private Map<String, Object> generateAggs(String timeFieldName, String startTime, String endTime) {
        Map<String, Object> aggs = new HashMap<>();
        Map<String, Object> customGroup = new HashMap<>();
        Map<String, Object> date_histogram = new HashMap<>();
        date_histogram.put("field", timeFieldName);
        String interval = this.timeRangeCompute(startTime, endTime);
        date_histogram.put("interval", interval);
        date_histogram.put("time_zone", "Asia/Shanghai");
        date_histogram.put("min_doc_count", 0);
        Map<String, Object> extended_bounds = new HashMap<>();
        extended_bounds.put("min", TimeTools.toDate(startTime, TimeTools.GMT_PTN).getTime());
        extended_bounds.put("max", TimeTools.toDate(endTime, TimeTools.GMT_PTN).getTime());
        date_histogram.put("extended_bounds", extended_bounds);
        customGroup.put("date_histogram", date_histogram);
        aggs.put("customGroup", customGroup);
        return aggs;
    }

    /**
     * 时间区间计算
     *
     * @param startTime
     * @param endTime
     * @return
     */
    private String timeRangeCompute(String startTime, String endTime) {
        long date3 = TimeTools.toDate(endTime, TimeTools.GMT_PTN).getTime() - TimeTools.toDate(startTime, TimeTools.GMT_PTN).getTime();
        long year = (long) Math.floor(date3 / (365 * 24 * 3600 * 1000));
        long days = (long) Math.floor(date3 / (24 * 3600 * 1000));
        long hours = (long) Math.floor(date3 / (3600 * 1000));
        long minutes = (long) Math.floor(date3 / (60 * 1000));
        long seconds = Math.round(date3 / 1000);
        String result = "";
        long floorYear = (long) Math.floor(year / GROUP_NUM_CONST);
        long floorDays = (long) Math.floor(days / GROUP_NUM_CONST);
        long floorHours = (long) Math.floor(hours / GROUP_NUM_CONST);
        long floorMinutes = (long) Math.floor(minutes / GROUP_NUM_CONST);
        long floorSeconds = (long) Math.floor(seconds / GROUP_NUM_CONST);
        if (floorYear > 1) {
            //按月分组
            result = "1M";
        } else if (floorDays > 1) {
            //按天分组
            result = floorDays + "d";
        } else if (floorHours > 1) {
            //按小时分组
            result = floorHours + "h";
        } else if (floorMinutes > 1) {
            //按分钟分组
            result = floorMinutes + "m";
        } else if (floorSeconds > 1) {
            //按秒分组
            result = floorSeconds + "s";
        }
        return result;
    }

    /**
     * 存储字段语句生成
     *
     * @return
     */
    private List<String> generateStoredFields() {
        List<String> fieldList = new ArrayList<>();
        fieldList.add("*");
        fieldList.add("_source");
        return fieldList;
    }

    /**
     * script字段语句生成
     *
     * @return
     */
    private Map<String, Object> generateScriptFields() {
        Map<String, Object> script_fields = new HashMap<>();
        return script_fields;
    }

    /**
     * docvalue字段语句生成
     *
     * @return
     */
    private List<String> generateDocValueFields() {
        List<String> docValueList = new ArrayList<>();
        return docValueList;
    }

    /**
     * 把自定义的语句转换成ES语句
     *
     * @param sentenceList
     * @return
     */
    private List<Map<String, Object>> translateESSentense(List<Map<String, Object>> sentenceList) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (!CollectionUtils.isEmpty(sentenceList)) {
            for (Map<String, Object> sentence : sentenceList) {
                Map<String, Object> sentenceMap = new HashMap<>();
                Map<String, Object> _sentence = new HashMap<>();
                String field = (String) sentence.get("field");
                String oper = (String) sentence.get("oper");
                String value = (String) sentence.get("value");
                //大于、大于等于、小于、小于等于
                if (">".equals(oper) || ">=".equals(oper) || "<".equals(oper) || "<=".equals(oper)) {
                    Map<String, Object> range = new HashedMap();
                    _sentence.put(this.translateOper(oper), value);
                    sentenceMap.put(field, _sentence);
                    range.put("range", sentenceMap);
                    result.add(range);
                }
                //模糊匹配
                if ("like".equals(oper)) {
                    //信工所版本字段模糊匹配前后增加通配符
//                    if (value.matches("[\u4E00-\u9FA5]+")) {
//                        _sentence.put(field, value);
//                    } else {
                        _sentence.put(field,"*" + value + "*");
//                    }
                    sentenceMap.put(this.translateOper(oper), _sentence);
                    result.add(sentenceMap);
                }
                //前缀匹配
                if ("suffix_like".equals(oper)) {
                    _sentence.put(field, "*" + value);
                    sentenceMap.put(this.translateOper(oper), _sentence);
                    result.add(sentenceMap);
                }
                //等于、前缀匹配
                if ("=".equals(oper) || "phrase_like".equals(oper)) {
                    _sentence.put(field, value);
                    sentenceMap.put(this.translateOper(oper), _sentence);
                    result.add(sentenceMap);
                }
                //字段是否存在
                if ("exist".equals(oper)) {
                    _sentence.put("field", field);
                    sentenceMap.put(this.translateOper(oper), _sentence);
                    result.add(sentenceMap);
                }
                //范围in查询
                if ("in".equals(oper)) {
                    _sentence.put(field, Arrays.asList(value.split(",")));
                    sentenceMap.put(this.translateOper(oper), _sentence);
                    result.add(sentenceMap);
                }
            }
        }
        return result;
    }

    /**
     * 操作符转换
     *
     * @param oper
     * @return
     */
    private String translateOper(String oper) {
        String result = "";
        switch (oper) {
            case ">":
                result = "gt";
                break;
            case ">=":
                result = "gte";
                break;
            case "<":
                result = "lt";
                break;
            case "<=":
                result = "lte";
                break;
            case "=":
                result = "term";
                break;
            case "like":
                result = "wildcard";
                break;
            case "phrase_like":
                result = "prefix";
                break;
            case "suffix_like":
                result = "wildcard";
                break;
            case "exist":
                result = "exist";
                break;
            case "in":
                result = "terms";
                break;
        }
        return result;
    }

    private Map<String,String> getMapFields(List<String> selectFields, List<Map<String,String>> allFieldList) {

        Map<String, String> map = new HashedMap();
        for (Map<String, String> fieldMap : allFieldList) {
            String name = fieldMap.get("name");
            String format = fieldMap.get("format");
            if (format != null && !format.equals("") && selectFields.contains(name)) {
                map.put(name, format);
            }
        }
        return map;

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

    private SearchResponse searchScroll (List<String> indexList, String queryJsonStr, RestHighLevelClient client, TimeValue keepAlive) {
        List<SearchPlugin> plugins = new ArrayList<>();

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        SearchModule searchModule = new SearchModule(Settings.EMPTY, false,plugins);
        List<NamedXContentRegistry.Entry> entryList = searchModule.getNamedXContents();
        NamedXContentRegistry.Entry queryStringEntry = null;

        try (XContentParser parser = XContentFactory.xContent(XContentType.JSON).createParser(new NamedXContentRegistry(entryList),DeprecationHandler.THROW_UNSUPPORTED_OPERATION, queryJsonStr)) {
            searchSourceBuilder.parseXContent(parser);
            searchSourceBuilder.size(3000);
            SearchRequest searchRequest = new SearchRequest(indexList.toArray(new String[0]), searchSourceBuilder);
            searchRequest.scroll(keepAlive);
            final SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
            return searchResponse;

        } catch (IOException e) {
            log.error("",e);
        }
        return null;

    }

    private int toExcel(SearchResponse response, Export.Progress pro, final String[] fields,Map<String,String> fieldMap,Map<String,Map<String,String>> mapDic,List<String> timeFields,List<Map<String,String>> allFields,long leftCount) {
        int num = 0;
        List<Map<String,Object>> writeList = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            num++;
            try {
                Map<String, Object> data = hit.getSourceAsMap();
                data.put("_index", hit.getIndex());
                //data.put("_source", hit.getSourceAsString());
                data.put("_type", hit.getType());
                data.put("_score", hit.getScore());
                data.put("_id", hit.getId());
                List<String> list=Arrays.asList(fields);
                if(list.contains("_source")){
                    String sourceString = getSourceString(data,allFields,mapDic,timeFields);
                    data.put("_source", sourceString);
                }

                String[] v = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    //遍历属性得到值
                    //v[i] = null == tmp ? "" : tmp.toString();
                    String value = "";
                    if(data.containsKey(fields[i])){
                        value = data.get(fields[i])==null?"":data.get(fields[i]).toString();
                    }
                    if(fieldMap!=null&&fieldMap.containsKey(fields[i])){
                        String fieldMapName = fieldMap.get(fields[i]);
                        if(mapDic!=null&&mapDic.containsKey(fieldMapName)&&mapDic.get(fieldMapName).containsKey(value)){
                            value = mapDic.get(fieldMapName).get(value);
                        }
                    }
                    if(timeFields!=null&&timeFields.contains(fields[i])){
                        value = TimeTools.utc2Local(value);
                    }
                    v[i] = value;
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


    private String getSourceString(Map<String,Object> data,List<Map<String,String>> allFields,Map<String,Map<String,String>> mapDic,List<String> timeFields){
        Map<String,String> sourceMap = new HashedMap();
        for(Map<String,String> fieldMap:allFields){
            String name = fieldMap.get("name");
            String nameDesc = fieldMap.get("nameDesc");
            String type = fieldMap.get("type");
            String format = fieldMap.get("format");
            if(data.containsKey(name)){
                String value = data.get(name)==null?"":data.get(name).toString();
                if(mapDic.containsKey(name)&&mapDic.get(name).containsKey(value)){
                    value = mapDic.get(name).get(value);
                }
                if("date".equals(type)){
                    value = TimeTools.utc2Local(value);
                }
                sourceMap.put(nameDesc,value);
            }
        }
        ObjectMapper json = new ObjectMapper();
        try {
            return json.writeValueAsString(sourceMap);
        } catch (IOException e) {
            log.error("",e);
        }
        return null;
        //return sourceMap.toString();
    }

    /**
     * 字段top关键词处理
     * @param queryJsonStr
     * @return
     */
    public String escapeFieldQueryStr(String queryJsonStr) {
        if (StringUtils.isNotEmpty(queryJsonStr)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String,Object> queryJsonMap = objectMapper.readValue(JsonSanitizer.sanitize(queryJsonStr),Map.class);
                if (queryJsonMap.containsKey(MUST)) {
                    List<Map<String,Object>> mustList = (List<Map<String, Object>>) queryJsonMap.get(MUST);
                    if (CollectionUtils.isNotEmpty(mustList)) {
                        for (Map<String,Object> mustMap : mustList) {
                            if (mustMap.containsKey("query_string")) {
                                Map<String,Object> queryStringMap = (Map<String, Object>) mustMap.get("query_string");
                                if (queryStringMap.containsKey(QUERY)) {
                                    String queryString = (String) queryStringMap.get(QUERY);
                                    queryStringMap.put(QUERY,CleanUtil.cleanString(addQueryLike(queryString)));
                                }
                            }
                        }
                    }
                }
                return objectMapper.writeValueAsString(queryJsonMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return queryJsonStr;
    }


    public String escapeQueryProStr(String queryJsonStr) {
        if (StringUtils.isNotEmpty(queryJsonStr)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String,Object> queryJsonMap = objectMapper.readValue(JsonSanitizer.sanitize(queryJsonStr),Map.class);
                Integer size = 0;
                if (queryJsonMap.containsKey("size")) {
                    size = (Integer) queryJsonMap.get("size");
                }
                // 放开ES7最大命中条数
                if (size == 0 && !queryJsonMap.containsKey(TRACK_TOTAL_HITS)) {
                    queryJsonMap.put(TRACK_TOTAL_HITS,true);
                }
                return objectMapper.writeValueAsString(queryJsonMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return queryJsonStr;
    }
    /**
     * 搜索关键词特殊字符转义
     * @param queryJsonStr
     * @return
     */
    @Override
    public String escapeQueryStr(String queryJsonStr) {
        if (StringUtils.isNotEmpty(queryJsonStr)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String,Object> queryJsonMap = objectMapper.readValue(JsonSanitizer.sanitize(queryJsonStr),Map.class);
                Integer size = 0;
                if (queryJsonMap.containsKey("size")) {
                    size = (Integer) queryJsonMap.get("size");
                }
                if (queryJsonMap.containsKey(QUERY)) {
                    Map<String,Object> query = (Map<String, Object>) queryJsonMap.get(QUERY);
                    if (query.containsKey(BOOL)) {
                        Map<String,Object> bool = (Map<String, Object>) query.get(BOOL);
                        if (bool.containsKey(MUST)) {
                            List<Map<String,Object>> mustList = (List<Map<String, Object>>) bool.get(MUST);
                            if (CollectionUtils.isNotEmpty(mustList)) {
                                for (Map<String,Object> mustMap : mustList) {
                                    if (mustMap.containsKey("query_string")) {
                                        Map<String,Object> queryStringMap = (Map<String, Object>) mustMap.get("query_string");
                                        if (queryStringMap.containsKey(QUERY)) {
                                            String queryString = (String) queryStringMap.get(QUERY);
                                            queryStringMap.put(QUERY,CleanUtil.cleanString(addQueryLike(queryString)));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                // 放开ES7最大命中条数
                if (size == 0 && !queryJsonMap.containsKey(TRACK_TOTAL_HITS)) {
                    queryJsonMap.put(TRACK_TOTAL_HITS,true);
                }
                return objectMapper.writeValueAsString(queryJsonMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return queryJsonStr;
    }

    /**
     * 关键词模糊查询
     * @param queryStr
     * @return
     */
    private String addQueryLike(String queryStr) {
        String result = "";
        if (StringUtils.isEmpty(queryStr)) {
            return queryStr;
        }
        String[] queryStrArr = queryStr.split(" ");
        for (String keyword : queryStrArr) {
            String escapeKeyword = escape(keyword).trim();
            //数字
            if (StringUtils.isNumeric(escapeKeyword)) {
                keyword = escapeKeyword;
            } else {
                keyword = addWildcard(escapeKeyword);
            }
            result += keyword + " ";
        }
        return result.trim();
    }

    /**
     * 关键字前后增加通配符
     * @param keyword
     * @return
     */
    private String addWildcard(String keyword) {
        if (!keyword.startsWith("*") && !keyword.startsWith("?")) {
            keyword = "*" + keyword;
        }
        if (!keyword.endsWith("*") && !keyword.endsWith("?")) {
            keyword = keyword + "*";
        }
        return keyword;
    }

    /**
     * 特殊字符转义
     * @param s
     * @return
     */
    private String escape(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            // These characters are part of the query syntax and must be escaped
            if (c == '\\' || c == '+' || c == '-' || c == '!' || c == '(' || c == ')' || c == ':'
                    || c == '^' || c == '[' || c == ']' || c == '\"' || c == '{' || c == '}' || c == '~'
                    || c == '?' || c == '|' || c == '&' || c == '/') {
                sb.append('\\');
            }
            sb.append(c);
        }
        return sb.toString();
    }

    @Override
    public Long queryTodayCount() {
        long total = 0;
        String date = TimeTools.formatDate(new Date(),"yyyy-MM-dd");
        String startTime = date + " 00:00:00";
        String endTime = date + " 23:59:59";
        QueryModel queryModel = new QueryModel();
        queryModel.setStartTime(TimeTools.toDate(startTime,TimeTools.GMT_PTN));
        queryModel.setEndTime(TimeTools.toDate(endTime,TimeTools.GMT_PTN));
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        List<DiscoverIndex> indexList = indexService.findAll();
        if (CollectionUtils.isNotEmpty(indexList)) {
            for (DiscoverIndex index : indexList) {
                String indexName = index.getIndexid();
                if (!"*".equals(indexName)) {
                    BoolQueryBuilder query = QueryBuilders.boolQuery();
                    QueryModel qModel = new QueryModel();
                    BeanUtils.copyProperties(queryModel,qModel);
                    qModel.setIndexName(indexName);
                    qModel.setTimeField(index.getTimefieldname());
                    //安全域过滤
                    this.generateDomainQuery(query,indexName);
                    qModel.setQueryBuilder(query);
                    wrapper.setTimeRangeFilter(qModel);
                    // 生成索引
                    qModel.setIndexNames(ES7Tools.getIndexNames(qModel));
                    SearchResponse searchResponse = wrapper.getSearchResponse(qModel);
                    long result = searchResponse.getHits().getTotalHits().value;
                    total += result;
                }
            }
        }

        return total;
    }

    @Override
    public Long queryTotal(QueryModel queryModel) {
        long total = 0;
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        String indexNames = queryModel.getIndexName();
        String[] names = indexNames.split(",");
            for (String name : names) {
                List<DiscoverIndex> indexList = indexService.findByProperty(DiscoverIndex.class,"indexid",name);
                if (CollectionUtils.isNotEmpty(indexList)) {
                    DiscoverIndex discoverIndex = indexList.get(0);
                    BoolQueryBuilder query = QueryBuilders.boolQuery();
                    QueryModel qModel = new QueryModel();
                    BeanUtils.copyProperties(queryModel,qModel);
                    qModel.setIndexName(name);
                    qModel.setTimeField(discoverIndex.getTimefieldname());
                    //安全域过滤
//                    this.generateDomainQuery(query,name);
                    qModel.setQueryBuilder(query);
                    wrapper.setTimeRangeFilter(qModel);
                    // 生成索引
                    qModel.setIndexNames(ES7Tools.getIndexNames(qModel));
                    SearchResponse searchResponse = wrapper.getSearchResponse(buildQueryModel(wrapper, qModel));
                    long result = searchResponse.getHits().getTotalHits().value;
                    total += result;
                }
            }
        return total;
    }

    @Override
    public List<Map<String, Object>> queryTotalTrend(QueryModel queryModel) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        Date startTime = queryModel.getStartTime();
        Date endTime = queryModel.getEndTime();
        List<DiscoverIndex> indexList = indexService.findAll();
        if (CollectionUtils.isNotEmpty(indexList)) {
            for (DiscoverIndex discoverIndex : indexList) {
                String index = discoverIndex.getIndexid();
                String timeField = discoverIndex.getTimefieldname();
                QueryModel model = QueryTools.buildCommonQueryModel(startTime, endTime, index, timeField);
                BoolQueryBuilder boolQuery = new BoolQueryBuilder();
                model.setQueryBuilder(boolQuery);
                List<Map<String, Object>> dataList = QueryTools.dateAgg(model, wrapper, timeField, DateHistogramInterval.DAY, "yyyy-MM-dd", 8, "date", "count");
                if (CollectionUtils.isNotEmpty(dataList)) {
                    for (Map map : dataList) {
                        if (CollectionUtils.isNotEmpty(resultList)) {
                            boolean exist = false;
                            for (Map<String,Object> result : resultList) {
                                String date = (String) map.get("date");
                                if (date.equals(result.get("date"))) {
                                    long sum = Long.valueOf(result.get("count").toString()) + Long.valueOf(map.get("count").toString());
                                    result.put("count",sum);
                                    exist = true;
                                }
                            }
                            if (!exist) {
                                resultList.add(map);
                            }
                        } else {
                            resultList.add(map);
                        }
                    }
                }
            }
        }
        return resultList;
    }

    @Override
    public Long query24Total(QueryModel queryModel) {
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        Date startTime = queryModel.getStartTime();
        Date endTime = queryModel.getEndTime();
        QueryModel qModel = QueryTools.buildCommonQueryModel(startTime, endTime, "*", "event_time");
        SearchResponse searchResponse = wrapper.getSearchResponse(qModel);
        long total = searchResponse.getHits().getTotalHits().value;
        QueryModel model = QueryTools.buildCommonQueryModel(startTime, endTime, "*", "insert_time");
        total += wrapper.getSearchResponse(model).getHits().getTotalHits().value;
        return total;
    }

    @Override
    public Map<String, Object> queryDayTrend(QueryModel queryModel) {
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> resultList = new ArrayList<>();
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        Date startTime = queryModel.getStartTime();
        Date endTime = queryModel.getEndTime();
        List<DiscoverIndex> indexList = indexService.findAll();
        if (CollectionUtils.isNotEmpty(indexList)) {
            for (DiscoverIndex discoverIndex : indexList) {
                String index = discoverIndex.getIndexid();
                String timeField = discoverIndex.getTimefieldname();
                QueryModel model = QueryTools.buildCommonQueryModel(startTime, endTime, index, timeField);
                BoolQueryBuilder boolQuery = new BoolQueryBuilder();
                model.setQueryBuilder(boolQuery);
                List<Map<String, Object>> dataList = QueryTools.dateAgg(model, wrapper, timeField, DateHistogramInterval.HOUR, "yyyy-MM-dd HH:mm:ss", 8, "date", "count");
                if (CollectionUtils.isNotEmpty(dataList)) {
                    for (Map map : dataList) {
                        if (CollectionUtils.isNotEmpty(resultList)) {
                            boolean exist = false;
                            for (Map<String,Object> result : resultList) {
                                String date = (String) map.get("date");
                                if (date.equals(result.get("date"))) {
                                    long sum = Long.valueOf(result.get("count").toString()) + Long.valueOf(map.get("count").toString());
                                    result.put("count",sum);
                                    exist = true;
                                }
                            }
                            if (!exist) {
                                resultList.add(map);
                            }
                        } else {
                            resultList.add(map);
                        }
                    }
                }
            }
        }

        resultMap.put("dataList", resultList);
        resultMap.put("totalCount", resultList.stream().mapToLong(p -> Long.valueOf(p.get("count").toString())).sum());
        return resultMap;
    }

    @Override
    public Long querySum(QueryModel queryModel) {
        long sum = 0;
        String fieldName = queryModel.getAggFieldName();
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        String indexNames = queryModel.getIndexName();
        String[] names = indexNames.split(",");
        for (String name : names) {
            List<DiscoverIndex> indexList = indexService.findByProperty(DiscoverIndex.class,"indexid",name);
            if (CollectionUtils.isNotEmpty(indexList)) {
                DiscoverIndex discoverIndex = indexList.get(0);
                BoolQueryBuilder query = QueryBuilders.boolQuery();
                QueryModel qModel = new QueryModel();
                BeanUtils.copyProperties(queryModel,qModel);
                qModel.setIndexName(name);
                qModel.setTimeField(discoverIndex.getTimefieldname());
                AbstractAggregationBuilder subAgg = AggregationBuilders.sum("sum_field").field(fieldName);
                qModel.setAggregationBuilder(subAgg);
                //安全域过滤
//                this.generateDomainQuery(query,name);
                qModel.setQueryBuilder(query);
                wrapper.setTimeRangeFilter(qModel);
                // 生成索引
                qModel.setIndexNames(ES7Tools.getIndexNames(qModel));
                Response response = wrapper.getAggResponse(buildQueryModel(wrapper, qModel));
                if (response != null) {
                    try {
                        String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Object> aggMap = mapper.readValue(responseStr, Map.class);
                        if (aggMap != null && aggMap.containsKey("aggregations")){
                            Map<String, Object> dataAggMap = (Map<String, Object> )aggMap.get("aggregations");
                            if(dataAggMap!=null&&dataAggMap.containsKey("sum_field"))
                            {
                                Map<String, Object> map = (Map<String, Object> )dataAggMap.get("sum_field");
                                if(map.containsKey("value")) {
                                    BigDecimal bd = new BigDecimal(map.get("value").toString());
                                    sum += Long.parseLong(bd.stripTrailingZeros().toPlainString());
                                }
                            }
                        }
                    } catch (IOException e) {
                        log.error("",e);
                    }
                }
            }
        }
        return sum;
    }

    /**
     * 封装QueryModel
     *
     * @param wrapper
     * @Param queryModel
     * @return
     */
    private QueryModel buildQueryModel(ES7Tools.QueryWrapper wrapper, final QueryModel queryModel) {
        wrapper.setTimeRangeFilter(queryModel);
        // 生成索引
        queryModel.setIndexNames(ES7Tools.getIndexNames(queryModel));
        // 处理查询语句
        queryModel.setQueryBuilder(JsonQueryTools.getQueryBuilder(queryModel.getQuery()));
        if (StringUtils.isNotEmpty(queryModel.getOrderType()) && "asc".equals(queryModel.getOrderType().toLowerCase(Locale.ENGLISH))) {
            queryModel.setSortOrder(SortOrder.ASC);
        } else {
            if (queryModel.getSortOrder() == null) {
                queryModel.setSortOrder(SortOrder.DESC);
            }
        }
        return queryModel;
    }

    private void generateDomainQuery(BoolQueryBuilder query,String indexName) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        Map userExtends = (Map) session.getAttribute(Global.SESSION.USER_EXTENDS);
        List<String> codeList = new ArrayList<>();
        if (userExtends != null && userExtends.get(AUTHORITY_TYPE) != null && userExtends.get(AUTHORITY_TYPE).equals(PageConstants.IS_OK)) {
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

    /**
     * 匹配ip截取的3位或2位
     * @param ipAddress
     * @return
     */
    public boolean matchPartIp(String ipAddress) {
        String ip2 = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){2}";
        Pattern pattern2 = Pattern.compile(ip2);
        Matcher matcher2 = pattern2.matcher(ipAddress);
        String ip1 = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){1}";
        Pattern pattern1 = Pattern.compile(ip1);
        Matcher matcher1 = pattern1.matcher(ipAddress);
        return matcher2.matches() || matcher1.matches();
    }

    @Override
    public List<Map<String, Object>> queryHeat(HeatModel heatModel) {
        List<Map<String,Object>> result = new ArrayList<>();
        QueryModel queryModel = new QueryModel();
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        queryModel.setStart_(0);
        queryModel.setCount_(0);
        queryModel.setStartTime(heatModel.getStartTime());
        queryModel.setEndTime(heatModel.getEndTime());
        queryModel.setIndexName(heatModel.getIndexName());
        queryModel.setTimeField(heatModel.getTimeField());
        queryModel.setIndexNames(ES7Tools.getIndexNames(queryModel));
        queryModel.setQueryBuilder(query);
        // 按时间分桶统计
        if (StringUtils.isNotEmpty(heatModel.getDateField())) {
            DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
            dateAgg.field(heatModel.getTimeField());
            dateAgg.dateHistogramInterval(DateHistogramInterval.DAY);
            dateAgg.timeZone(ZoneId.of("Asia/Shanghai"));
            dateAgg.format("MM.dd");
            if (StringUtils.isNotEmpty(heatModel.getTermField())) {
                TermsAggregationBuilder termsAggregation = AggregationBuilders.terms("term_field").field(heatModel.getTermField()).size(3000);
//                TermsAggregationFit5.suit5(termsAggregation);
                dateAgg.subAggregation(termsAggregation);
            }
            queryModel.setAggregationBuilder(dateAgg);
        }
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        Response response = wrapper.getAggResponse(queryModel);
        if (response != null) {
            try {
                String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> aggMap = mapper.readValue(responseStr, Map.class);
                if (aggMap != null && aggMap.containsKey("aggregations")) {
                    Map<String, Object> goalAggMap = (Map<String, Object>) aggMap.get("aggregations");
                    if (goalAggMap != null && goalAggMap.containsKey("dateAgg")) {
                        Map<String, Object> bucketsMap = (Map<String, Object>) goalAggMap.get("dateAgg");
                        if (bucketsMap.containsKey("buckets")) {
                            List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                            if (CollectionUtils.isNotEmpty(aggItems)) {
                                for (Map<String,Object> aggItem : aggItems) {
                                    String date = (String) aggItem.get("key_as_string");
                                    Map<String,Object> termMap = (Map<String, Object>) aggItem.get("term_field");
                                    List<Map<String,Object>> subBuckets = (List<Map<String, Object>>) termMap.get("buckets");
                                    Map<String,Object> resultMap = new HashMap<>();
                                    resultMap.put("date",date);
                                    resultMap.put("list",subBuckets);
                                    result.add(resultMap);
                                }
                            }
                        }
                    }
                }
            } catch (IOException e) {
                log.error("", e);
            }
        }
        return result;
    }
}
