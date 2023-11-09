package com.vrv.vap.data.service.impl;

import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.data.common.excel.ExcelInfo;
import com.vrv.vap.data.common.excel.out.ExcelData;
import com.vrv.vap.data.common.excel.out.Export;
import com.vrv.vap.data.common.excel.out.WriteHandler;
import com.vrv.vap.data.component.ConvertElastic;
import com.vrv.vap.data.component.ConvertSQL;
import com.vrv.vap.data.component.ESManager;
import com.vrv.vap.data.component.ESTools;
import com.vrv.vap.data.component.config.DictConfig;
import com.vrv.vap.data.component.config.IndexSliceConfig;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.constant.SOURCE_TYPE;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.mapper.SourceMapper;
import com.vrv.vap.data.mapper.SystemConfigMapper;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.service.ContentService;
import com.vrv.vap.data.service.SourceFieldService;
import com.vrv.vap.data.service.SourceService;
import com.vrv.vap.data.util.ExcelUtil;
import com.vrv.vap.data.util.PathTools;
import com.vrv.vap.data.util.TimeTools;
import com.vrv.vap.data.vo.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.ehcache.Cache;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Service
public class ContentServiceImpl implements ContentService {


    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${elk.segmentDay:1}")
    private Integer SEGMENT_DAY;

    @Value("${elk.total}")
    private int MAX;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private SourceFieldService sourceFieldService;

    @Autowired
    private IndexSliceConfig indexSliceConfig;

    @Autowired
    private ESTools esTools;

    @Autowired
    DictConfig dictConfig;

    @Resource
    private SourceMapper sourceMapper;

    @Autowired
    Cache<Integer, Source> SOURCE_MAP;


    @Autowired
    private ConvertElastic convertElastic;

    @Autowired
    private ConvertSQL convertSQL;

    @Autowired
    private SystemConfigMapper systemConfigMapper;

    private Pattern ES5 = Pattern.compile("hits\":.+\"total\":(\\d+)");                 // ES5里面 匹配总数

    private Pattern ES6 = Pattern.compile("hits\":.+\"total\":.+\"value\":(\\d+)");     // ES6里面 匹配总数

    private static final String SEVEN = "7";
    // 搜索模块
    private static final String MODEL_SEARCH = "search";

    @Autowired
    private StringRedisTemplate redisTpl;

    @Autowired
    private Export export;

    /**
     * 生成搜索语句 Elastic Search
     */
    @Override
    public CommonResponse generateElastic(CommonRequest query) throws ApiException {
        CommonResponse response = new CommonResponse();
        List<String> index = new ArrayList<>();
        String timeField = null;
        String domainField = null;
        for (int sourceId : query.getSource()) {
            Source source = sourceService.findById(sourceId);
            timeField = source.getTimeField();
            domainField = source.getDomainField();
            if (source == null) {
                // 容错性过滤
//                throw new ApiException(ErrorCode.NOT_FOUND_INDEX);
                continue;
            }
            String indexName = source.getName();
            if (indexName.endsWith("-*")) {
                index.add(indexName.substring(0, indexName.length() - 2));
            } else if (indexName.endsWith("*")) {
                index.add(indexName.substring(0, indexName.length() - 1));
            } else {
                index.add(indexName);
            }
        }
        LinkedHashSet<String> segment = this.querySegmentsByTime(index, query.getStartTime(), query.getEndTime());
        response.setSegment(segment);
        if (segment.size() > 0) {
            response.setQuery(buildEsAuthQuery(convertElastic.buildQueryParam(query, timeField,domainField),query.getModuleAuth(),query.getSource().get(0)));
            //response.setQuery(buildEsAuthQuery(convertElastic.buildQueryParam(query, timeField,domainField),"account,dept,manage",1));
            if (query.isAgg()) {
                response.setAggs(convertElastic.aggTimeField(timeField, query.getStartTime(), query.getEndTime(), null));
            }
        }
        return response;
    }


    // es 搜索
    @Override
    public String elasticSearch(ElasticParam query) {
        if (query.getIndex() == null || query.getIndex().length == 0) {
            return SYSTEM.ERROR;
        }
        String indexStr = esTools.multiIndexRoute(query.getIndex());
        if (StringUtils.isEmpty(indexStr)) {
            return SYSTEM.ERROR;
        }
        String endpoint = "/" + indexStr + "/_search";
        HttpEntity entity = new NStringEntity(this.processTotalHits(query.getQuery(),query.getModel()), ContentType.APPLICATION_JSON);
        try {
            logger.info("================查询条件-索引:" + indexStr);
            logger.info("================查询条件-内容:" + this.processTotalHits(query.getQuery(),query.getModel()));
            Response response = ESManager.sendPost(endpoint,entity);
            String responseStr = EntityUtils.toString(response.getEntity());
            logger.info("================查询结果-response:" + responseStr);
            return responseStr;
        } catch (IOException e) {
            logger.info("================查询异常================");
            logger.error(e.getMessage(), e);
        }
        return SYSTEM.ERROR;
    }

    // es 搜索
    @Override
    public Response scrollSearch(ElasticParam query) {
        if (query.getIndex() == null || query.getIndex().length == 0) {
            return null;
        }
        String indexStr = esTools.multiIndexRoute(query.getIndex());
        if (StringUtils.isEmpty(indexStr)) {
            return null;
        }
        String endpoint = "/" + indexStr + "/_search";
        String entityStr = this.processTotalHits(query.getQuery(),query.getModel());
        try {
            logger.info("================查询条件-索引:" + indexStr);
            logger.info("================查询条件-内容:" + this.processTotalHits(query.getQuery(),query.getModel()));
            Response response = ESManager.scrollSearch(endpoint,entityStr,ESTools.ES_CACHE_TIME);
            return response;
        } catch (IOException e) {
            logger.info("================查询异常================");
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    // es 计算总数
    @Override
    public CommonResponse elasticTotal(CommonResponse response) throws ApiException {
        if (response.getSegment().size() == 0) {
            return response;
        }
        CommonResponse responseWithTotal = new CommonResponse();
        LinkedHashSet<String> segments = response.getSegment();

        String version = redisTpl.opsForValue().get(SYSTEM.ES_VERSION);
        String paramForTotal = "{\"from\":0,\"size\":0,\"query\":" + response.getQuery() + "}";
        if (version.compareTo(SEVEN) >= 0) {
            paramForTotal = "{\"from\":0,\"size\":0,\"query\":" + response.getQuery() + ",\"track_total_hits\":\"true\"}";
        }
        int totalAcc = 0;
        ElasticParam param = new ElasticParam();
        param.setQuery(paramForTotal);
        String indexNames = segments.stream().reduce((a,b) -> a + "," + b).get();
        param.setIndex(new String[]{indexNames});
        String totalResult = this.elasticSearch(param);
        Matcher matcher5 = ES5.matcher(totalResult);
        if (matcher5.find()) {
            totalAcc = Integer.parseInt(matcher5.group(1));
        } else {
            Matcher matcher6 = ES6.matcher(totalResult);
            if (matcher6.find()) {
                totalAcc = Integer.parseInt(matcher6.group(1));
            }
        }
        responseWithTotal.setTotalAcc(totalAcc);
        responseWithTotal.setTotal(totalAcc > MAX ? MAX : totalAcc);
        responseWithTotal.setAggs(response.getAggs());
        responseWithTotal.setSegment(segments);
        responseWithTotal.setQuery(response.getQuery());
        return responseWithTotal;
    }

    @Override
    public CommonResponse generateSQL(CommonRequest query) throws ApiException {
        CommonResponse response = new CommonResponse();
        Source source = this.sourceService.findById(query.getSource().get(0));
        String tableName = source.getName();
        String timeField = source.getTimeField();
        LinkedHashSet segment = new LinkedHashSet<>();
        segment.add(tableName);
        response.setSegment(segment);
        List<SourceField> sourceFields = sourceFieldService.findAllBySourceId(source.getId());
        String where;
        WhereCondition condition = convertSQL.buildWhere(query, sourceFields, timeField);
        try {
            ObjectMapper mapper = new ObjectMapper();
            where = mapper.writeValueAsString(condition);
            response.setQuery(where);
        } catch (JsonProcessingException e) {
            throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
        }
        if (query.isAgg()) {
            response.setAggs(convertSQL.buildAgg(tableName, timeField, query.getStartTime(), query.getEndTime(), where));
        }
        int total = this.sqlCount(tableName, condition);
        response.setTotal(total);
        response.setTotalAcc(total);
        return response;
    }

    /**
     * 获取全部索引名称片段
     */
    @Override
    public LinkedHashSet<String> querySegmentsByTime(List<String> sources, Date startTime, Date endTime) {
        if (indexSliceConfig.hasMonthIndex(sources)) {
            return esTools.getIndexesByTime(sources, startTime, endTime);
        }
        LinkedHashSet<String> segment = new LinkedHashSet<>();
        long end = endTime.getTime();
        long current = end - TimeTools.ONE_DAY * SEGMENT_DAY;
        final long _start = startTime.getTime();
        while (current > _start) {
            LinkedHashSet<String> indexList = esTools.getIndexesByTime(sources, new Date(current), new Date(end));
            segment.addAll(indexList);
            end = current;
            current = end - TimeTools.ONE_DAY * SEGMENT_DAY;
        }
        if (end > _start) {
            LinkedHashSet<String> indexList = esTools.getIndexesByTime(sources, startTime, new Date(end));
            segment.addAll(indexList);
        }
        return segment;
    }

    @Override
    public List<Map<String, Object>> execQuery(String sql) throws ApiException {
        if (!sql.trim().toLowerCase().startsWith("select")) {
            throw new ApiException(ErrorCode.SQL_WRONG_SYNTAX.getResult().getCode(),ErrorCode.SQL_WRONG_SYNTAX.getResult().getMessage());
        }
        try {
            return sourceMapper.execQuery(sql);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
        }
    }


    @Override
    public VList sqlList(SqlQuery query) throws ApiException {
        VList result = new VList();
        WhereCondition where;
        try {
            where = convertSQL.parseWhere(buildSqlAuthCondition(query.getWhere(),query.getModuleAuth(), query.getSourceId()));
        } catch (JsonProcessingException e) {
            throw new ApiException(ErrorCode.SQL_WRONG_WHERE.getResult().getCode(),ErrorCode.SQL_WRONG_WHERE.getResult().getMessage());
        }
        String SQL = query.toSQL(where.toString());
        logger.info("================查询语句-列表:" + SQL);
        List list = sourceMapper.execQuery(SQL);
        result.setList(list);
        result.setCode("0");
        if (query.isTotal_()) {
            // 暂时用不上，后面查 Detail 时备用
            result.setTotal(this.sqlCount(query.getTable(), where));
        }
        return result;
    }

    @Override
    public VData sqlGroup(SqlGroup param) throws ApiException {
        VData result = new VData();
        WhereCondition where;
        try {
            where = convertSQL.parseWhere(buildSqlAuthCondition(param.getWhere(),param.getModuleAuth(),param.getSourceId()));
        } catch (JsonProcessingException e) {
            throw new ApiException(ErrorCode.SQL_WRONG_WHERE.getResult().getCode(),ErrorCode.SQL_WRONG_WHERE.getResult().getMessage());
        }
        String SQL = param.toSQL(where.toString());
        logger.info("================查询语句-聚合:" + SQL);
        List list = sourceMapper.execQuery(SQL);
        result.setData(list);
        result.setCode("0");
        return result;
    }

    @Override
    public void sqlExport(OutputStream outputStream, int sourceId, SqlQuery query) throws ApiException {
        List<SourceField> fields = sourceFieldService.findAllBySourceId(sourceId);
        WhereCondition where;
        try {
            where = convertSQL.parseWhere(query.getWhere());
        } catch (JsonProcessingException e) {
            throw new ApiException(ErrorCode.SQL_WRONG_WHERE.getResult().getCode(),ErrorCode.SQL_WRONG_WHERE.getResult().getMessage());
        }
        String SQL = query.toSqlNoLimit(where.toString());
        logger.info("================导出数据-查询语句:" + SQL);
        List<Map<String, Object>> list = sourceMapper.execQuery(SQL);
        ExcelUtil.exportData(outputStream, fields, list, dictConfig);
        System.out.println(list);
    }

    @Override
    public void esExport(OutputStream outputStream, ElasticParam query) throws ApiException {
        List<SourceField> fields =  this.getSourceFields(query);
        String result = this.elasticSearch(query);
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode root = mapper.readTree(result);
            List<JsonNode> list = root.get("hits").get("hits").findValues("_source");
            ExcelUtil.exportData(outputStream, fields, list, dictConfig);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Export.Progress esExport(ElasticParam query) {
        List<SourceField> fieldList = this.getSourceFields(query);
        final String[][] fields = getFieldConfig(fieldList);
        // 时间字段
        final List<String> timeList = getTimeFields(fieldList);
        // 字典映射字段
        Map<String,String> fieldMap =  getMapFields(fieldList);
        this.processExportSize(query);
        Response response = this.scrollSearch(query);
        if(response == null) {
            logger.info("滚动查询失败");
            return null;
        }
        EsQueryResult esQueryResult = ESManager.toEsQueryResult(response);
        List<EsSourceVo> hits = esQueryResult.getHits();
        String scrollId = esQueryResult.getScrollId();
        final long total = esQueryResult.getTotal() > MAX ? MAX : esQueryResult.getTotal();
        List<ExcelData> excelDataList = new ArrayList<>();
        ExcelInfo info = new ExcelInfo("搜索明细" ,fields[0],fields[1],"明细",true, PathTools.getExcelPath("搜索明细"),null);
        ExcelData data = new ExcelData(info, total, new ArrayList<>());
        excelDataList.add(data);
        Export.Progress progress = export.build(excelDataList).start(WriteHandler.fun(p -> {
            long leftCount = total;
            int count = toExcel(hits, p, fields[0],fieldMap,timeList,fieldList,leftCount);

            while (true) {
                if (count >= total) {
                    break;
                }
                try {
                    Response response2 = ESManager.scrollSearchById(scrollId,ESTools.ES_CACHE_TIME);
                    EsQueryResult queryResult = ESManager.toEsQueryResult(response2);
                    List<EsSourceVo> queryHits = queryResult.getHits();
                    leftCount = total - count;
                    count += toExcel(queryHits, p, fields[0],fieldMap,timeList,fieldList,leftCount);
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
        }));
        logger.info("返回导出任务ID" + progress.getWorkId());
        return  progress;
    }

    private List<SourceField> getSourceFields(ElasticParam query) {
        Set<Integer> sourceIds = new HashSet<>();
        List<SourceField> fieldList = new ArrayList<>();
        Iterator<Source> it = sourceMapper.selectAll().iterator();
        for (String index : query.getIndex()) {
            while (it.hasNext()) {
                Source source = it.next();
                System.out.println(source.getName());
                if (source.getType() != SOURCE_TYPE.ELASTIC_BUILT) {
                    continue;
                }
                String prefix = source.getName();
                if (prefix.endsWith("-*")) {
                    prefix = prefix.substring(0, prefix.length() - 2);
                } else if (prefix.endsWith("*")) {
                    prefix = prefix.substring(0, prefix.length() - 1);
                }
                if (index.startsWith(prefix)) {
                    sourceIds.add(source.getId());
                    break;
                }
            }
        }
        Set<String> used = new HashSet<>();
        for (int sourceId : sourceIds) {
            List<SourceField> sourceFields = sourceFieldService.findAllBySourceId(sourceId);
            for (SourceField field : sourceFields) {
                if (field.getShow() && StringUtils.isNotBlank(field.getName()) && !used.contains(field.getField())) {
                    used.add(field.getField());
                    fieldList.add(field);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(fieldList)) {
            fieldList.sort((i, o) -> i.getSort() - o.getSort());
        }
        return fieldList;
    }

    private int toExcel(List<EsSourceVo> hits, Export.Progress pro, final String[] fields, Map<String,String> fieldMap, List<String> timeFields, List<SourceField> fieldList, long leftCount) {
        int num = 0;
        List<Map<String,Object>> writeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(hits)) {
            for (EsSourceVo hit : hits) {
                num++;
                try {
                    Map<String, Object> data = hit.getSource();
                    data.put("_index", hit.getIndex());
                    //data.put("_source", hit.getSourceAsString());
                    data.put("_type", hit.getType());
//                data.put("_score", hit.getScore());
                    data.put("_id", hit.getId());
                    List<String> list=Arrays.asList(fields);
                    if (list.contains("_source")) {
                        String sourceString = getSourceString(data,fieldList,timeFields);
                        data.put("_source", sourceString);
                    }

                    String[] v = new String[fields.length];
                    for (int i = 0; i < fields.length; i++) {
                        //遍历属性得到值
                        //v[i] = null == tmp ? "" : tmp.toString();
                        String value = "";
                        if (data.containsKey(fields[i])) {
                            value = data.get(fields[i]) == null ? "" : data.get(fields[i]).toString();
                        }
                        if (fieldMap != null && fieldMap.containsKey(fields[i])) {
                            String fieldMapName = fieldMap.get(fields[i]);
                            value = dictConfig.getString(fieldMapName,value);
                        }
                        if (timeFields != null && timeFields.contains(fields[i])) {
                            value = TimeTools.utc2Local(value);
                        }
                        v[i] = value;
                        data.put(fields[i],value);

                    }
                    writeList.add(data);

                } catch ( IllegalArgumentException  e) {
                    logger.error("", e);
                }
                if (num >= leftCount) {
                    break;
                }
            }
        }
        pro.writeBatchMap(0,writeList);
        return num;
    }

    private Map<String,String> getMapFields(List<SourceField> fieldList) {
        Map<String, String> map = new HashedMap();
        for (SourceField field : fieldList) {
            String name = field.getField();
            String format = field.getDict();
            if (format != null && !format.equals("")) {
                map.put(name, format);
            }
        }
        return map;
    }


    private  List<String> getTimeFields(List<SourceField> fieldList) {
        List<String> dateFields = new ArrayList<>();
        for(SourceField field : fieldList){
            String type = field.getType();
            String name = field.getField();
            if(type != null && type.equals("date")){
                dateFields.add(name);
            }
        }
        return dateFields;
    }

    private String getSourceString(Map<String,Object> data,List<SourceField> fieldList,List<String> timeFields){
        Map<String,String> sourceMap = new HashedMap();
        for(SourceField field : fieldList){
            String name = field.getField();
            String nameDesc = field.getName();
            String type = field.getType();
            String format = field.getDict();
            if(data.containsKey(name)){
                String value = data.get(name)== null ? "" : data.get(name).toString();
                value = dictConfig.getString(name,value);
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
            logger.error("",e);
        }
        return null;
        //return sourceMap.toString();
    }

    private  String[][] getFieldConfig(List<SourceField> sourceFields){
        String[][] fields = new String[2][sourceFields.size()];
        int i = 0;
        for(SourceField field : sourceFields){
            fields[0][i] = field.getField();
            fields[1][i] = field.getName();
            i++;
        }
        return fields;

    }

    // 查询 TOTAL COUNT
    @Override
    public int sqlCount(String tableName, WhereCondition condition) {
        String whereStr = condition.toString();
        if (StringUtils.isNotBlank(whereStr)) {
            whereStr = "WHERE " + whereStr;
        }
        String COUNT = "SELECT COUNT(0) AS total FROM %s  %s";
        String SQL = String.format(COUNT, tableName, whereStr);
        logger.info("================查询语句-计数:" + SQL);
        List<Map<String, Object>> result = sourceMapper.execQuery(SQL);
        return Integer.valueOf(result.get(0).get("total").toString());
    }

    /**
     * 兼容处理总条数
     * @param queryJsonStr
     * @return
     */
    public String processTotalHits(String queryJsonStr,String model) {
        if (org.apache.commons.lang.StringUtils.isNotEmpty(queryJsonStr)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String,Object> queryJsonMap = objectMapper.readValue(queryJsonStr,Map.class);
                String version = redisTpl.opsForValue().get(SYSTEM.ES_VERSION);
                // 最大命中条数，搜索模块总数和内容接口分开
                if (version.compareTo(SEVEN) >= 0 && !queryJsonMap.containsKey(SYSTEM.TRACK_TOTAL_HITS)
                        && !MODEL_SEARCH.equals(model)) {
                    queryJsonMap.put(SYSTEM.TRACK_TOTAL_HITS,true);
                }
                return objectMapper.writeValueAsString(queryJsonMap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return queryJsonStr;
    }

    /**
     * 处理导出查询大小
     * @param query
     * @return
     */
    private void processExportSize(ElasticParam query) {
        String queryJsonStr = query.getQuery();
        if (StringUtils.isNotEmpty(queryJsonStr)) {
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                Map<String,Object> queryJsonMap = objectMapper.readValue(queryJsonStr,Map.class);
                queryJsonMap.put("size",5000);
                queryJsonStr = objectMapper.writeValueAsString(queryJsonMap);
                query.setQuery(queryJsonStr);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String buildEsAuthQuery(String query, String moduleAuth, Integer sourceId) {
        /*if (StringUtils.isEmpty(moduleAuth)) {
            moduleAuth = systemConfigMapper.selectByPrimaryKey("data_auth_strategy").getConfValue();
        }*/

        if (StringUtils.isEmpty(moduleAuth)) return query;

        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

        Map<String, Object> queryMap = (Map<String, Object>) JSONUtil.parse(query);
        List<Map<String, Object>> mustList = (List<Map<String, Object>>)((Map<String, Object>)queryMap.get("bool")).get("must");

        Map<String, Object> boolMap = new HashMap<>();
        Map<String, Object> shouldMap = new HashMap<>();
        List<Map<String, Object>> shouldList = new ArrayList<>();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Global.SESSION.USER);

        List<SourceField> sourceFieldList = sourceFieldService.findAllBySourceId(sourceId);
        if (CollectionUtils.isEmpty(sourceFieldList)) return query;
        Map<Integer, List<SourceField>> fieldMarkMap = sourceFieldList.stream().collect(Collectors.groupingBy(SourceField::getAuthMark));

        String[] auths = moduleAuth.split(",");
        for (String auth : auths) {
            if (auth.equals("account") && fieldMarkMap.containsKey(1)) {
                List<SourceField> sourceFields = fieldMarkMap.get(1);
                sourceFields.stream().forEach(p -> {
                    Map<String, Object> filterMap = new HashMap<>();
                    Map<String, Object> termMap = new HashMap<>();
                    termMap.put(p.getField(), user.getAccount());
                    filterMap.put("term", termMap);
                    shouldList.add(filterMap);
                });
            } else if (auth.equals("dept") && fieldMarkMap.containsKey(2)) {
                List<SourceField> sourceFields = fieldMarkMap.get(2);
                sourceFields.stream().forEach(p -> {
                    Map<String, Object> filterMap = new HashMap<>();
                    Map<String, Object> termMap = new HashMap<>();
                    termMap.put(p.getField(), user.getOrgCode());
                    filterMap.put("term", termMap);
                    shouldList.add(filterMap);
                });
            } else if (auth.equals("manage") && fieldMarkMap.containsKey(2)) {
                Set<String> orgManageSet = (Set<String>)session.getAttribute("_ORG");
                if (CollectionUtils.isEmpty(orgManageSet)) continue;
                List<SourceField> sourceFields = fieldMarkMap.get(2);
                sourceFields.stream().forEach(p -> {
                    Map<String, Object> filterMap = new HashMap<>();
                    Map<String, Object> termMap = new HashMap<>();
                    termMap.put(p.getField(), orgManageSet);
                    filterMap.put("terms", termMap);
                    shouldList.add(filterMap);
                });
            }



            /*List<DataSourceMark> dataSourceMarkList = sourceToFieldMap.get(sourceId + "&" + auth);
            if (CollectionUtils.isEmpty(dataSourceMarkList)) continue;

            for (DataSourceMark data : dataSourceMarkList) {
                Map<String, Object> filterMap = new HashMap<>();
                Map<String, Object> termMap = new HashMap<>();

                if (auth.equals("account")) {
                    termMap.put(data.getField(), user.getAccount());
                    filterMap.put("term", termMap);
                } else if (auth.equals("dept")) {
                    termMap.put(data.getField(), user.getOrgCode());
                    filterMap.put("term", termMap);
                } else if (auth.equals("manage")) {
                    Set<String> orgManageSet = (Set<String>)session.getAttribute("_ORG");
                    if (CollectionUtils.isEmpty(orgManageSet)) continue;
                    termMap.put(data.getField(), orgManageSet);
                    filterMap.put("terms", termMap);
                } else {
                    continue;
                }

                shouldList.add(filterMap);
            }*/
        }

        if (CollectionUtils.isEmpty(shouldList)) return query;

        shouldMap.put("should", shouldList);
        boolMap.put("bool", shouldMap);
        mustList.add(boolMap);

        return JSONUtil.toJsonStr(queryMap);
    }

    private String buildSqlAuthCondition(String conditon, String moduleAuth, Integer sourceId) {
        /*if (StringUtils.isEmpty(moduleAuth)) {
            moduleAuth = systemConfigMapper.selectByPrimaryKey("data_auth_strategy").getConfValue();
        }*/

        if (StringUtils.isEmpty(moduleAuth)) return conditon;

        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();

        Map<String, Object> conditionMap = (Map<String, Object>) JSONUtil.parse(conditon);
        List<Map<String, Object>> itemList = (List<Map<String, Object>>)conditionMap.get("items");
        Map<String, Object> itemAuthMap = new HashMap<>();
        itemAuthMap.put("operation", "OR");
        List<Map<String, Object>> itemAuthList = new ArrayList<>();

        HttpSession session = request.getSession();
        User user = (User) session.getAttribute(Global.SESSION.USER);

        List<SourceField> sourceFieldList = sourceFieldService.findAllBySourceId(sourceId);
        if (CollectionUtils.isEmpty(sourceFieldList)) return conditon;
        Map<Integer, List<SourceField>> fieldMarkMap = sourceFieldList.stream().collect(Collectors.groupingBy(SourceField::getAuthMark));


        String[] auths = moduleAuth.split(",");

        for (String auth : auths) {
            if (auth.equals("account") && fieldMarkMap.containsKey(1)) {
                List<SourceField> sourceFields = fieldMarkMap.get(1);
                sourceFields.stream().forEach(p -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("operation", "=");
                    itemMap.put("field", p.getField());
                    itemMap.put("value", user.getAccount());
                    itemAuthList.add(itemMap);
                });
            } else if (auth.equals("dept") && fieldMarkMap.containsKey(2)) {
                List<SourceField> sourceFields = fieldMarkMap.get(2);
                sourceFields.stream().forEach(p -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("operation", "=");
                    itemMap.put("field", p.getField());
                    itemMap.put("value", user.getOrgCode());
                    itemAuthList.add(itemMap);
                });
            } else if (auth.equals("manage") && fieldMarkMap.containsKey(2)) {
                Set<String> orgManageSet = (Set<String>) session.getAttribute("_ORG");
                if (CollectionUtils.isEmpty(orgManageSet)) continue;
                List<SourceField> sourceFields = fieldMarkMap.get(2);
                sourceFields.stream().forEach(p -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("operation", "in");
                    itemMap.put("field", p.getField());
                    itemMap.put("value", orgManageSet.stream().map(orgCode -> "\"" + orgCode + "\"").collect(Collectors.joining(",")));
                    itemAuthList.add(itemMap);
                });
            }


            /*List<DataSourceMark> dataSourceMarkList = sourceToFieldMap.get(sourceId + "&" + auth);
            if (CollectionUtils.isEmpty(dataSourceMarkList)) continue;

            for (DataSourceMark data : dataSourceMarkList) {
                Map<String, Object> itemMap = new HashMap<>();

                if (auth.equals("account")) {
                    itemMap.put("operation", "=");
                    itemMap.put("field", data.getField());
                    itemMap.put("value", user.getAccount());
                } else if (auth.equals("dept")) {
                    itemMap.put("operation", "=");
                    itemMap.put("field", data.getField());
                    itemMap.put("value", user.getOrgCode());
                } else if (auth.equals("manage")) {
                    Set<String> orgManageSet = (Set<String>) session.getAttribute("_ORG");
                    if (CollectionUtils.isEmpty(orgManageSet)) continue;
                    itemMap.put("operation", "in");
                    itemMap.put("field", data.getField());
                    itemMap.put("value", orgManageSet.stream().map(orgCode -> "\"" + orgCode + "\"").collect(Collectors.joining(",")));
                } else {
                    continue;
                }

                itemAuthList.add(itemMap);
            }*/
        }

        if (CollectionUtils.isEmpty(itemAuthList)) return conditon;

        itemAuthMap.put("items", itemAuthList);
        itemList.add(itemAuthMap);

        return JSONUtil.toJsonStr(conditionMap);
    }
}
