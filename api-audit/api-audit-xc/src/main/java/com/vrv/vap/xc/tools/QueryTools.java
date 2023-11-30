package com.vrv.vap.xc.tools;

import cn.hutool.core.collection.IterUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vrv.vap.toolkit.constant.Common;
import com.vrv.vap.toolkit.constant.FileSizeEnum;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.tools.DataSize;
import com.vrv.vap.toolkit.tools.SessionTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.EsResult;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.client.ElasticSearchManager;
import com.vrv.vap.xc.config.IndexConfig;
import com.vrv.vap.xc.config.MultiClusterConfig;
import com.vrv.vap.xc.config.PermissionMappingConfig;
import com.vrv.vap.xc.init.IndexCache;
import com.vrv.vap.xc.model.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.reindex.BulkByScrollResponse;
import org.elasticsearch.index.reindex.DeleteByQueryRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.*;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.*;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

//import com.vrv.vap.xc.compatible.TermsAggregationBuilder5;

/**
 * es查询工具
 *
 * @author xw
 * @date 2015年10月20日
 */
public final class QueryTools {
    private static Log log = LogFactory.getLog(QueryTools.class);

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * es scroll 查询 缓存时间
     */
    public static final long ES_CACHE_TIME = 600000;

    private static final int SIZE = 10000;

    public static final String INDEX = "${index}";
    public static final String TIME = "${time}";
    public static final String TYPE = "_doc";

    /**
     * 多集群情况下，索引名格式为：集群名:索引名
     */
    private static final String CLUSTER_INDEX_CONNECTOR = ":";

    /**
     * 索引时间格式正则
     */
    private static final String INDEX_DATE_REG = "-\\d{4}\\.\\d{2}\\.\\d{2}$|-\\d{4}-\\d{2}-\\d{2}$|-\\d{4}/\\d{2}/\\d{2}$|-\\d{4}\\.\\d{2}$|-\\d{4}-\\d{2}$|-\\d{4}/\\d{2}$";

    private static PermissionMappingConfig permissionMappingConfig = VapXcApplication.getApplicationContext().getBean(PermissionMappingConfig.class);

    private static MultiClusterConfig multiClusterConfig = VapXcApplication.getApplicationContext().getBean(MultiClusterConfig.class);

    private static Map<String, String[]> esMapping = permissionMappingConfig.getEsMapping();

    public static QueryWrapper build(ElasticSearchManager client, IndexConfig indexConfig) {
        return new QueryWrapper(ElasticSearchManager.getClient(), indexConfig);
    }

    public static QueryWrapper build(ElasticSearchManager client) {
        return build(client, VapXcApplication.getApplicationContext().getBean(IndexConfig.class));
    }

    public static QueryWrapper build(IndexConfig indexConfig) {
        return build(VapXcApplication.getApplicationContext().getBean(ElasticSearchManager.class), indexConfig);
    }

    public static QueryWrapper build() {
        return build(VapXcApplication.getApplicationContext().getBean(ElasticSearchManager.class),
                VapXcApplication.getApplicationContext().getBean(IndexConfig.class));
    }

    /**
     * es的一些封装
     *
     * @author xw
     * @date 2018年3月29日
     */
    public static class QueryWrapper {
        private RestHighLevelClient client;
        private IndexConfig indexConfig;
        private boolean timeAfterIndex;

        public QueryWrapper(RestHighLevelClient client, IndexConfig indexConfig) {
            this.client = client;
            this.indexConfig = indexConfig;
            timeAfterIndex = indexConfig.getIndexFormat().indexOf(TIME) > indexConfig.getIndexFormat().indexOf(INDEX);
        }

        /**
         * 获取es链接客户端
         *
         * @return
         */
        public RestHighLevelClient getClient() {
            return client;
        }

        /**
         * 获取es配置
         *
         * @return
         */
        public IndexConfig getIndexConfig() {
            return indexConfig;
        }

        /**
         * 将es查询结果用map封装
         *
         * @param response
         * @param queryModel
         * @return
         */
        public EsResult wrapResult(SearchResponse response, EsQueryModel queryModel) {
            EsResult esResult = new EsResult();

            List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>(10);
            if (null == response) {
                esResult.setTotal(0);
                return esResult;
            }

            if (null != response.getHits()) {
                log.debug("total:" + response.getHits().getTotalHits());
                // 超过最大返回值,重新设值
                if (queryModel.isOverFlow() || response.getHits().getTotalHits().value > indexConfig.getResultTotal()) {
                    esResult.setTotal(indexConfig.getResultTotal());
                } else {
                    esResult.setTotal(response.getHits().getTotalHits().value);
                }
                esResult.setTotalAcc(response.getHits().getTotalHits().value);
                esResult.setStart(queryModel.getStart());
                esResult.setCount(queryModel.getCount());
                if (queryModel.isLimitResultFields()) {
                    // 限制返回字段,直接取值
                    log.debug("查询ES,不从_source中取值");
                    datas = filteringField(response.getHits(), queryModel.getTimeField());
                } else {
                    log.debug("查询ES,从_source中取值");
                    datas = filteringField(response.getHits(), queryModel.getResultFields(), queryModel.isNeedSource(),
                            queryModel.getTimeField());
                }
            }

            esResult.setList(datas);
            return esResult;
        }

        /**
         * 将es查询结果用map封装
         *
         * @param response
         * @param queryModel
         * @return
         */
        public List<Map<String, String>> wrapResultAsList(SearchResponse response, EsQueryModel queryModel) {
            String[] fields = queryModel.getResultFields();

            List<Map<String, String>> datas = new ArrayList<Map<String, String>>(10);
            if (null == response) {
                return null;
            }

            if (null != response.getHits()) {
                log.debug("total:" + response.getHits().getTotalHits());
                if (queryModel.isLimitResultFields()) {
                    // 限制返回字段,直接取值
                    log.debug("查询ES,不从_source中取值");
                    datas = filteringField(response.getHits(), queryModel.getTimeField());
                } else {
                    log.debug("查询ES,从_source中取值");
                    datas = filteringField(response.getHits(), fields, queryModel.isNeedSource(),
                            queryModel.getTimeField());
                }
            }
            return datas;
        }

        /**
         * 结果封装为map
         *
         * @param hits
         * @param timeField
         * @return
         */
        @SuppressWarnings("unchecked")
        private <T> List<Map<String, T>> filteringField(SearchHits hits, String timeField) {
            List<Map<String, T>> datas = new ArrayList<Map<String, T>>(10);
            for (SearchHit hit : hits) {
                Map<String, T> tmpData = new HashMap<String, T>();
                tmpData.put("_index", (T) hit.getIndex());
                for (Entry<String, DocumentField> tmp : hit.getFields().entrySet()) {
                    checkAndSetValue(tmpData, hit.getSourceAsMap(), tmp.getKey());
                }

                utc2gmt(tmpData, timeField);

                datas.add(tmpData);
            }
            return datas;
        }

        /**
         * ***单位转换方法 只换算到GB 大于1024GB或者小于1KB的都按byte
         * <p>
         * Title: assembleDataForSize
         * </p>
         *
         * @return void
         * ****
         * @author to Du
         * @date 2016年11月28日 上午11:18:12
         */
        @SuppressWarnings("unchecked")
        private <T> void assembleDataForSize(Map<String, T> tmpData, Object sourceData, String key) {
            if (sourceData != null) {
                Long value = 0L;
                if (!"".equals(sourceData.toString())) {
                    value = Long.parseLong(sourceData.toString());
                }

                DecimalFormat df = new DecimalFormat("######0.0000");
                if (value >= 1024 && value < 1048576) {
                    tmpData.put(key, (T) df.format(value / 1024d));
                    tmpData.put("FUnit", (T) "KB");
                } else if (value >= 1048576 && value < 1073741824) {
                    tmpData.put(key, (T) df.format((value / 1024d) / 2014d));
                    tmpData.put("FUnit", (T) "MB");
                } else if (value >= 1073741824 && value < 1099511627776L) {
                    tmpData.put(key, (T) df.format(((value / 1024d) / 2014d) / 1024d));
                    tmpData.put("FUnit", (T) "GB");
                } else {
                    tmpData.put(key, (T) value);
                    tmpData.put("FUnit", (T) "byte");
                }
            }
        }

        /**
         * 结果封装为map (从)
         *
         * @param hits
         * @param timeField
         * @return
         */
        public List<Map<String, String>> wrapResponse(SearchHits hits, String timeField) {
            List<Map<String, String>> datas = new ArrayList<Map<String, String>>(10);
            for (SearchHit hit : hits) {
                Map<String, String> tmpData = new HashMap<String, String>();
                tmpData.put("_index", hit.getIndex());
                tmpData.put("_id", hit.getId());
                for (Entry<String, Object> tmp : hit.getSourceAsMap().entrySet()) {
                    tmpData.put(tmp.getKey(), null != tmp.getValue() ? tmp.getValue().toString() : "");
                }

                utc2gmt(tmpData, timeField);

                datas.add(tmpData);
            }
            return datas;
        }

        /**
         * 结果封装为map (从)
         *
         * @param hits
         * @return
         */
        public List<Map<String, String>> wrapResponse(SearchHits hits) {
            List<Map<String, String>> datas = new ArrayList<Map<String, String>>(10);
            for (SearchHit hit : hits) {
                Map<String, String> tmpData = new HashMap<String, String>();
                tmpData.put("_index", hit.getIndex());
                tmpData.put("_id", hit.getId());
                for (Entry<String, Object> tmp : hit.getSourceAsMap().entrySet()) {
                    tmpData.put(tmp.getKey(), null != tmp.getValue() ? tmp.getValue().toString() : "");
                }
                datas.add(tmpData);
            }
            return datas;
        }

        /**
         * 结果封装为map,指定字段名
         *
         * @param hits
         * @param timeField
         * @param params
         * @return
         */
        public List<Map<String, String>> response2MapStringValue(SearchHits hits, String timeField, String[] params) {
            List<Map<String, String>> datas = new ArrayList<Map<String, String>>(10);
            for (SearchHit hit : hits) {
                Map<String, String> tmpData = new HashMap<String, String>();
                Map<String, Object> resouce = hit.getSourceAsMap();
                String index = hit.getIndex();
                tmpData.put("_index", hit.getIndex());
                for (String tmp : params) {
                    if (index.startsWith("webaudit-2")) {
                        switch (tmp) {
                            case "PlugIn":
                            case "operate_condition":
                                tmpData.put("operate_condition",
                                        resouce.get("PlugIn") == null ? "" : (String) resouce.get("PlugIn"));
                                break;
                            case "UserName":
                                break;
                            case "user_name":
                                tmpData.put("user_name",
                                        resouce.get("UserName") == null ? "" : (String) resouce.get("UserName"));
                                break;
                            case "UserID":
                                break;
                            case "user_id":
                                tmpData.put("user_id", resouce.get("UserID") == null ? "" : (String) resouce.get("UserID"));
                                break;
                            case "ni":
                                break;
                            case "terminal_id":
                                tmpData.put("terminal_id", resouce.get("ni") == null ? "" : (String) resouce.get("ni"));
                                break;
                            default:
                        }
                    }
                    if (!"_index".equals(tmp)) {
                        tmpData.put(tmp, resouce.get(tmp) == null ? "" : (String) resouce.get(tmp));
                    }
                }

                utc2gmt(tmpData, timeField);

                datas.add(tmpData);
            }
            return datas;
        }

        /**
         * 过滤结果字段
         *
         * @param hits
         * @param fields
         * @param needSource
         * @return
         */
        @SuppressWarnings("unchecked")
        private <T> List<Map<String, T>> filteringField(SearchHits hits, String[] fields, boolean needSource,
                                                        String timeField) {
            List<Map<String, T>> datas = new ArrayList<Map<String, T>>(10);

            List<String> fieldsCheck = getResultFields(fields);

            // 如果字段数组为空则封装查询到的全部字段,否则只获取数组中字段的值
            if (fieldsCheck.size() == 0) {
                for (SearchHit hit : hits) {
                    Map<String, T> tmpData = new HashMap<String, T>();
                    tmpData.put("_index", (T) hit.getIndex());
                    for (Entry<String, Object> tmp : hit.getSourceAsMap().entrySet()) {
                        checkAndSetValue(tmpData, hit.getSourceAsMap(), tmp.getKey());
                    }

                    utc2gmt(tmpData, timeField);

                    datas.add(tmpData);
                }
                return datas;
            }
            // 需要原始值
            if (needSource) {
                for (SearchHit hit : hits) {
                    Map<String, T> tmpData = new HashMap<String, T>();
                    tmpData.put("_source", (T) hit.getSourceAsString());
                    tmpData.put("_index", (T) hit.getIndex());
                    for (String tmp : fieldsCheck) {
                        checkAndSetValue(tmpData, hit.getSourceAsMap(), tmp);
                    }

                    utc2gmt(tmpData, timeField);

                    datas.add(tmpData);
                }
                return datas;
            }
            // 不需要原始值
            for (SearchHit hit : hits) {
                Map<String, T> tmpData = new HashMap<String, T>();
                tmpData.put("_index", (T) hit.getIndex());
                for (String tmp : fieldsCheck) {
                    checkAndSetValue(tmpData, hit.getSourceAsMap(), tmp);
                }

                utc2gmt(tmpData, timeField);

                datas.add(tmpData);
            }

            return datas;
        }

        @SuppressWarnings("unchecked")
        private <T> void checkAndSetValue(Map<String, T> result, Map<String, Object> hit, String key) {
            if ("FSize".equals(key)) {
                assembleDataForSize(result, hit.get(key), "FSize");
                return;
            }

            String index = (String) result.get("_index");

            // 使webaudit和巨龙索引数据兼容,故新增字段
            if (index.startsWith("webaudit-2")) {
                switch (key) {
                    case "PlugIn":
                    case "operate_condition":
                        result.put("operate_condition", (T) hit.get("PlugIn"));
                        return;
                    case "UserName":
                    case "user_name":
                        result.put("user_name", (T) hit.get("UserName"));
                        return;
                    case "UserID":
                    case "user_id":
                        result.put("user_id", (T) hit.get("UserID"));
                        return;
                    case "ni":
                    case "terminal_id":
                        result.put("terminal_id", (T) hit.get("ni"));
                        return;
                }
            }

            if (!"_index".equals(key)) {
                result.put(key, (T) hit.get(key));
            }
        }

        @SuppressWarnings("unchecked")
        public <T> void utc2gmt(Map<String, T> tmpData, String timeField) {
            Object time = tmpData.get(timeField);
            if (null == time) {
                return;
            }
            String gmtTime = "";
            if (time.toString().length() == 10) {
                gmtTime = time.toString();
            } else if (time.toString().indexOf("+0800") > -1) {
                gmtTime = TimeTools.chineseTimeFormat(time.toString());
            } else {
                gmtTime = TimeTools.utcToGmtTimeAsString(time.toString());
            }
            tmpData.put(timeField, (T) gmtTime);
        }

        private List<String> getResultFields(String[] fields) {
            List<String> fieldsCheck = new ArrayList<String>();
            if (null != fields) {
                for (String tmp : fields) {
                    if (StringUtils.isEmpty(tmp)) {
                        continue;
                    }
                    fieldsCheck.add(tmp);
                }
            }
            return fieldsCheck;
        }

        /**
         * 删除索引
         *
         * @param queryModel
         * @return
         */
        public BulkByScrollResponse getDeleteResponse(EsQueryModel queryModel) {
            if (StringUtils.isEmpty(queryModel.getIndexName())) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return null;
            }
            DeleteByQueryRequest request = new DeleteByQueryRequest(queryModel.getIndexName());
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }
            log.info("***** request ******\n" + request + "***********\n");
            BulkByScrollResponse response = null;
            try {
                response =
                        client.deleteByQuery(request, RequestOptions.DEFAULT);
            } catch (IndexNotFoundException e) {
                if (log.isDebugEnabled()) {
                    log.debug("index missing !!!", e);
                } else {
                    log.error(new StringBuffer("index missing !!!").append(" [indexName:")
                            .append(queryModel.getIndexName()).append("][indexNames:")
                            .append(Arrays.toString(queryModel.getIndexNames())).append("]"));
                }
            } catch (Exception e) {
                log.error("", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + response + "***********\n");
            }
            return response;
        }

        /**
         * 搜索,该方法有最大返回值限制,默认为10000
         *
         * @param queryModel
         * @return
         */
        public SearchResponse getSearchResponse(EsQueryModel queryModel) {
            checkPermissionFilter(queryModel);
            checkSizeAndReset(queryModel);
            if ((null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length)
                    && (StringUtils.isEmpty(queryModel.getIndexName()))) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return null;
            }
            SearchRequest request = buildQuery(queryModel);
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }
            log.info("***** request ******\n" + request + "***********\n");
            SearchResponse response = null;
            try {
                response = client.search(request, RequestOptions.DEFAULT);
            } catch (IndexNotFoundException e) {
                if (log.isDebugEnabled()) {
                    log.debug("index missing !!!", e);
                } else {
                    log.error(new StringBuffer("index missing !!!").append(" [indexName:")
                            .append(queryModel.getIndexName()).append("][indexNames:")
                            .append(Arrays.toString(queryModel.getIndexNames())).append("]"));
                }
                IndexCache.refreshCache(QueryTools.build());
            } catch (Exception e) {
                log.error("", e);
                IndexCache.refreshCache(QueryTools.build());
            }
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + response + "***********\n");
            }
            return response;
        }

        /**
         * es Scroll 查询
         *
         * @param queryModel
         * @param scrollId
         * @return
         */
        public SearchResponse scrollQuery(EsQueryModel queryModel, String scrollId) {
            checkPermissionFilter(queryModel);
            // 缓存时间 分页查询必须在该缓存时间之内进行
            TimeValue keepAlive = new TimeValue(ES_CACHE_TIME);
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest();
            SearchResponse response = null;
            if (scrollId != null) {
                searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(keepAlive);
                try {
                    response = client.searchScroll(searchScrollRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    log.error("", e);
                }
            } else {
                SearchRequest request = buildQuery(queryModel);
                request.scroll(keepAlive);
                if (log.isDebugEnabled()) {
                    log.debug("***********\n" + request + "***********\n");
                }
                log.info("***** request ******\n" + request + "***********\n");
                try {
                    response = client.search(request, RequestOptions.DEFAULT);
                } catch (IndexNotFoundException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("index missing !!!", e);
                    } else {
                        log.error(new StringBuffer("index missing !!!").append(" [indexName:")
                                .append(queryModel.getIndexName()).append("][indexNames:")
                                .append(Arrays.toString(queryModel.getIndexNames())).append("]"));
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }
            return response;
        }

        /**
         * es Scroll 查询（不校验权限）
         *
         * @param queryModel
         * @param scrollId
         * @return
         */
        public SearchResponse scrollQueryNoPermission(EsQueryModel queryModel, String scrollId) {
            // 缓存时间 分页查询必须在该缓存时间之内进行
            TimeValue keepAlive = new TimeValue(ES_CACHE_TIME);
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest();
            SearchResponse response = null;
            if (scrollId != null) {
                searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(keepAlive);
                try {
                    response = client.searchScroll(searchScrollRequest, RequestOptions.DEFAULT);
                } catch (IOException e) {
                    log.error("", e);
                }
            } else {
                SearchRequest request = buildQuery(queryModel);
                request.scroll(keepAlive);
                if (log.isDebugEnabled()) {
                    log.debug("***********\n" + request + "***********\n");
                }
                try {
                    response = client.search(request, RequestOptions.DEFAULT);
                } catch (IndexNotFoundException e) {
                    if (log.isDebugEnabled()) {
                        log.debug("index missing !!!", e);
                    } else {
                        log.error(new StringBuffer("index missing !!!").append(" [indexName:")
                                .append(queryModel.getIndexName()).append("][indexNames:")
                                .append(Arrays.toString(queryModel.getIndexNames())).append("]"));
                    }
                } catch (Exception e) {
                    log.error("", e);
                }
            }

            return response;
        }

        public Map<String, Object> getAggResponse(EsQueryModel queryModel) {
            checkPermissionFilter(queryModel);
            checkSizeAndReset(queryModel);
            if ((null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length)
                    && (org.apache.commons.lang.StringUtils.isEmpty(queryModel.getIndexName()))) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return null;
            }
            SearchRequest request = buildQuery(queryModel);
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }
            log.info("***********\n" + request + "***********\n");
            Response response = null;
            try {
                response = search(request.indices(), request.source().toString());
            } catch (IndexNotFoundException e) {
                if (log.isDebugEnabled()) {
                    log.debug("index missing !!!", e);
                } else {
                    log.error(new StringBuffer("index missing !!!").append(" [indexName:")
                            .append(queryModel.getIndexName()).append("][indexNames:")
                            .append(Arrays.toString(queryModel.getIndexNames())).append("]"));
                }
                IndexCache.refreshCache(QueryTools.build());
            } catch (Exception e) {
                log.error("", e);
                IndexCache.refreshCache(QueryTools.build());
            }
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + response + "***********\n");
            }

            Map<String, Object> aggMap = new HashMap<>();
            if (response != null) {
                try {
                    String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
//					log.info("*****  responseStr  ******\n" + responseStr + "***********\n");
                    ObjectMapper mapper = new ObjectMapper();
                    aggMap = mapper.readValue(responseStr, Map.class);
                } catch (IOException e) {
                    log.error("", e);
                }
            }

            return aggMap;
        }

        /**
         * 检查是否需要过滤数据权限
         *
         * @param queryModel
         */
        private void checkPermissionFilter(EsQueryModel queryModel) {
            if (SessionTools.isPermissionCheck()) {
                Set<String> securityDomains = SessionTools.getSecurityDomains();
                if (securityDomains.size() > 0) {
                    buildFilter(queryModel, securityDomains);
                }
            }
        }

        /**
         * 构造数据权限过滤条件
         *
         * @param queryModel
         * @param securityDomainList
         */
        private void buildFilter(EsQueryModel queryModel, Set<String> securityDomainList) {
            Set<String> esIndexs = esMapping.keySet();
            boolean isFilter = false;
            Set<String> fields = new HashSet<>();
            if (queryModel != null) {
                // 多索引情况
                if (queryModel.getIndexNames() != null && queryModel.getIndexNames().length > 0) {
                    for (String index : queryModel.getIndexNames()) {
                        String indexR = revert(index);
                        if (esIndexs.contains(indexR)) {
                            isFilter = true;
                            fields.addAll(Arrays.asList(esMapping.get(indexR)));
                        }
                    }
                    // 单索引情况
                } else if (queryModel.getIndexName() != null && !"".equals(queryModel.getIndexName())) {
                    if (esIndexs.contains(revert(queryModel.getIndexName()))) {
                        isFilter = true;
                        fields.addAll(Arrays.asList(esMapping.get(revert(queryModel.getIndexName()))));
                    }
                }
            }

            // 添加过滤条件
            if (isFilter && fields.size() > 0) {
                BoolQueryBuilder query = (BoolQueryBuilder) queryModel.getQueryBuilder();
                if (query == null) {
                    query = QueryBuilders.boolQuery();
                    queryModel.setQueryBuilder(query);
                }
                BoolQueryBuilder shouldQuery = QueryBuilders.boolQuery();
                for (String field : fields) {
                    shouldQuery.should(QueryBuilders.termsQuery(field, securityDomainList));
                }
                query.must(shouldQuery);
            }
        }

        /**
         * 还原索引，去掉时间后缀
         *
         * @param index
         * @return
         */
        private String revert(String index) {
            String result = "";
            if (index.indexOf("-") > -1) {
                // 告警索引
                if (index.startsWith("warnresulttmp")) {
                    result = "warnresulttmp";
                } else {
                    result = index.substring(0, index.lastIndexOf("-"));
                }
            }
            return result;
        }

        public Response search(String[] indexList, String queryJsonStr) {

            String indexStr = org.apache.commons.lang.StringUtils.join(indexList, ",");
            String method = "POST";
            String endpoint = "/" + indexStr + "/_search";
            HttpEntity entity = new NStringEntity(queryJsonStr, ContentType.APPLICATION_JSON);
            try {
                Request request = new Request(method, endpoint);
                request.setEntity(entity);
                return ElasticSearchManager.getClient().getLowLevelClient().performRequest(request);
            } catch (IOException e) {
                log.error("", e);
            }

            return null;

        }

        public SearchResponse search2(String[] indexList,SearchSourceBuilder builder) {
            try {
                SearchRequest request = new SearchRequest(indexList);
                request.source(builder);
                return ElasticSearchManager.getClient().search(request, RequestOptions.DEFAULT);
            } catch (IOException e) {
                log.error("", e);
            }
            return null;
        }

        /**
         * 修改默认的查询限制
         *
         * @return
         */
        public Response setWindowMaxResult(String index) {
            try {
                String method = "PUT";
                index = index != null ? index : "";
                String endpoint = "/" + index + "*/_settings";
                HttpEntity entity = new NStringEntity("{ \"max_result_window\": 1000000000 }", ContentType.APPLICATION_JSON);
                Request request = new Request(method, endpoint);
                request.setEntity(entity);
                return ElasticSearchManager.getClient().getLowLevelClient().performRequest(request);
            } catch (IOException e) {
                log.error("", e);
            }
            return null;

        }

        private SearchRequest buildQuery(EsQueryModel queryModel) {
            SearchRequest request = null;
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.trackTotalHits(true);
            searchSourceBuilder.from(queryModel.getStart()).size(queryModel.getCount());
            if (null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length) {
                request = new SearchRequest(indexRoute(queryModel.getIndexName()));
            } else {
                request = new SearchRequest(indexRoute(queryModel.getIndexNames()));
            }

            // 是否排序
            if (queryModel.isSort()) {
                if (null != queryModel.getSortFields() && 0 < queryModel.getSortFields().length) {
                    searchSourceBuilder.sort(
                            SortBuilders.fieldSort(queryModel.getSortFields()[0]).order(queryModel.getSortOrder()));
                }
            }
            // 是否添加聚合查询
            if (queryModel.isUseAggre() && null != queryModel.getAggregationBuilder()) {
                searchSourceBuilder.aggregation(queryModel.getAggregationBuilder());
            }
            // 是否添加多个聚合查询
            if (queryModel.isUseAggre() && null != queryModel.getMulAggregationBuilders()) {
                queryModel.getMulAggregationBuilders().forEach(searchSourceBuilder::aggregation);
            }
            // 是否使用过滤器
            if (queryModel.isUseFilter() && null != queryModel.getFilterBuilder()) {
                searchSourceBuilder.postFilter(queryModel.getFilterBuilder());
            }
            // 是否添加时间段
            if (queryModel.isUseTimeRange()) {
                if (queryModel.getQueryBuilder() instanceof BoolQueryBuilder) {
                    addTimeRange(queryModel, (BoolQueryBuilder) queryModel.getQueryBuilder());
                }
            }

            searchSourceBuilder.query(queryModel.getQueryBuilder());

            // 是否限制返回字段
            if (queryModel.isLimitResultFields()) {
                if (ArrayUtil.isNotEmpty(queryModel.getResultFields())) {
                    for (String field : queryModel.getResultFields()) {
                        searchSourceBuilder.docValueField(field);
                    }
                }
            }

            if (StringUtils.isNotEmpty(queryModel.getTypeName())) {
                request.types(queryModel.getTypeName());
            }
            return request.source(searchSourceBuilder);
        }

        /**
         * 多集群情况下索引路由
         *
         * @param indexName
         * @return
         */
        private String indexRoute(String indexName) {
            if (multiClusterConfig.isOpen()) {
                String clusterName = multiClusterConfig.getClusterNameByIndex(getIndex(indexName));
                if (StringUtils.isNotEmpty(clusterName)) {
                    return multiClusterConfig.getClusterNameByIndex(getIndex(indexName)) + CLUSTER_INDEX_CONNECTOR + indexName;
                } else {
                    return indexName;
                }
            } else {
                return indexName;
            }
        }

        /**
         * 完整索引名去除日期
         *
         * @param indexName
         * @return
         */
        private String getIndex(String indexName) {
            return indexName.replaceAll(INDEX_DATE_REG, "");
        }


        /**
         * 多集群情况下索引路由
         *
         * @param indexNames
         * @return
         */
        private String[] indexRoute(String[] indexNames) {
            if (multiClusterConfig.isOpen()) {
                List<String> resultIndexs = new ArrayList<>();
                if (indexNames != null && indexNames.length > 0) {
                    for (String indexName : indexNames) {
                        resultIndexs.add(indexRoute(indexName));
                    }
                }
                return resultIndexs.toArray(new String[0]);
            } else {
                return indexNames;
            }
        }

        /**
         * 根据时间范围以及indexName和indexNames,生成索引列表
         *
         * @param queryModel
         * @return
         */
        public List<String> getIndexNames(EsQueryModel queryModel) {
            List<String> indexList = new ArrayList<>();
            if (StringUtils.isNotEmpty(queryModel.getIndexName())) {
                indexList.addAll(
                        getIndexNames(queryModel.getIndexName(), queryModel.getStartTime(), queryModel.getEndTime()));
            }
            if (null != queryModel.getIndexNames()) {
                indexList.addAll(
                        getIndexNames(queryModel.getIndexNames(), queryModel.getStartTime(), queryModel.getEndTime()));
            }
            return indexList;
        }

        /**
         * 根据时间范围以及索引名称,生成索引列表(支持多个索引)
         *
         * @param indexNames
         * @param startTime
         * @param endTime
         * @return
         */
        public List<String> getIndexNames(String[] indexNames, Date startTime, Date endTime) {
            List<String> indexList = new ArrayList<String>();
            for (String name : indexNames) {
                indexList.addAll(getIndexNames(name, startTime, endTime));
            }
            return indexList;
        }

        /**
         * 根据时间范围以及索引名称,生成索引列表
         *
         * @param indexName
         * @param startTime
         * @param endTime
         * @return
         */
        public List<String> getIndexNames(String indexName, Date startTime, Date endTime) {
            if (StringUtils.isEmpty(indexName)) {
                return new ArrayList<>(0);
            }
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(startTime);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(endTime);

            Set<String> indexs = new HashSet<String>();
            String tmpIndexName = null;
            while (cal1.before(cal2)) {
                tmpIndexName = getIndexName(indexName, cal1.getTime());
                if (isIndexExists(tmpIndexName, cal1.getTime(), 1)) {
                    indexs.add(tmpIndexName);
                }
                cal1.add(Calendar.DATE, 1);
            }

            tmpIndexName = getIndexName(indexName, cal2.getTime());
            if (isIndexExists(tmpIndexName, cal2.getTime(), 1)) {
                indexs.add(tmpIndexName);
            }

            if (log.isDebugEnabled()) {
                log.debug(indexName + ":" + indexs);
            }

            return new ArrayList<>(indexs);
        }

        /**
         * 根据时间范围以及索引名称,生成索引列表
         *
         * @param indexName
         * @param startTime
         * @param endTime
         * @return
         */
        public List<String> getIndexNames(String indexName, Date startTime, Date endTime, int dayOrMonth, String timeFormat, String indexFormat) {
            if (StringUtils.isEmpty(indexName)) {
                return new ArrayList<>(0);
            }
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(startTime);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(endTime);

            Set<String> indexs = new HashSet<String>();
            String tmpIndexName = null;
            while (cal1.before(cal2)) {
                tmpIndexName = getIndexNameByDateFormat(indexName, cal1.getTime(), timeFormat, indexFormat);
//                if(dayOrMonth==2){
//                    tmpIndexName = tmpIndexName.substring(0, tmpIndexName.length() - 2);
//                }
                if (isIndexExists(tmpIndexName, cal1.getTime(), dayOrMonth, timeFormat)) {
                    indexs.add(tmpIndexName);
                }
                if (dayOrMonth == 2) {
                    cal1.add(Calendar.MONTH, 1);
                    cal1.set(Calendar.DATE, 1);
                } else {
                    cal1.add(Calendar.DATE, 1);
                }
            }

            tmpIndexName = getIndexNameByDateFormat(indexName, cal2.getTime(), timeFormat, indexFormat);
            if (isIndexExists(tmpIndexName, cal2.getTime(), dayOrMonth, timeFormat)) {
                indexs.add(tmpIndexName);
            }

            if (log.isDebugEnabled()) {
                log.debug(indexName + ":" + indexs);
            }

            return new ArrayList<>(indexs);
        }

        /**
         * 指定时间格式，根据时间范围以及索引名称,生成索引列表
         *
         * @param indexName
         * @param startTime
         * @param endTime
         * @param dateFormat
         * @return
         */
        public List<String> getIndexNamesByDateFormat(String indexName, Date startTime, Date endTime, String dateFormat) {
            if (StringUtils.isEmpty(indexName)) {
                return new ArrayList<>(0);
            }
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(startTime);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(endTime);

            Set<String> indexs = new HashSet<String>();
            String tmpIndexName = null;
            while (cal1.before(cal2)) {
                tmpIndexName = getIndexNameByDateFormat(indexName, cal1.getTime(), dateFormat);
                if (isIndexExists(tmpIndexName, cal1.getTime(), 1)) {
                    indexs.add(tmpIndexName);
                }
                cal1.add(Calendar.DATE, 1);
            }

            tmpIndexName = getIndexNameByDateFormat(indexName, cal2.getTime(), dateFormat);
            if (isIndexExists(tmpIndexName, cal2.getTime(), 1)) {
                indexs.add(tmpIndexName);
            }

            if (log.isDebugEnabled()) {
                log.debug(indexName + ":" + indexs);
            }

            return new ArrayList<>(indexs);
        }

        /**
         * 根据时间范围以及索引名称,生成月索引列表
         *
         * @param indexName
         * @param startTime
         * @param endTime
         * @return
         */
        public List<String> getMonthIndexNames(String indexName, Date startTime, Date endTime) {
            if (StringUtils.isEmpty(indexName)) {
                return new ArrayList<>(0);
            }
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(startTime);

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(endTime);

            Set<String> indexs = new HashSet<String>();
            String tmpIndexName = null;
            while (cal1.before(cal2)) {
                tmpIndexName = getIndexName(indexName, cal1.getTime());
                tmpIndexName = tmpIndexName.substring(0, tmpIndexName.length() - 3);
                if (isIndexExists(tmpIndexName, cal1.getTime(), 2)) {
                    indexs.add(tmpIndexName);
                }
                cal1.add(Calendar.MONTH, 1);
            }

            tmpIndexName = getIndexName(indexName, cal2.getTime());
            tmpIndexName = tmpIndexName.substring(0, tmpIndexName.length() - 3);
            if (isIndexExists(tmpIndexName, cal2.getTime(), 2)) {
                indexs.add(tmpIndexName);
            }

            if (log.isDebugEnabled()) {
                log.debug(indexName + ":" + indexs);
            }

            return new ArrayList<>(indexs);
        }

        /**
         * 判断索引是否存在,使用缓存判断
         *
         * @param indexName
         * @return
         */
        public boolean isIndexExists(String indexName, Date time, int a) {
            if (time.before(indexConfig.getStartTime())) {
                return false;
            }

            return isIndexExists(indexName, a);
        }

        /**
         * 判断索引是否存在,使用缓存判断
         *
         * @param indexName
         * @return
         */
        public boolean isIndexExists(String indexName, Date time, int a, String timeFormat) {
            if (time.before(indexConfig.getStartTime())) {
                return false;
            }

            return isIndexExists(indexName, a, timeFormat);
        }

        /**
         * 判断索引是否存在
         *
         * @param indexName
         * @return
         */
        public boolean isIndexExists(String indexName, int a, String timeFormat) {
            if (StringUtils.isEmpty(indexName)) {
                return false;
            }
            if (IndexCache.getIndexNamesExist().contains(indexName)) {
                return true;
            }
            if (IndexCache.getIndexNamesNotExist().contains(indexName)) {
                boolean isOk;
                if (a == 1) {
                    isOk = doubleCheck(indexName, timeFormat);
                } else {
                    isOk = doubleCheckMonth(indexName, timeFormat);
                }
                if (isOk) {
                    IndexCache.getIndexNamesNotExist().remove(indexName);
                }
                return isOk;
            }
            boolean exist = isExistIndex(indexName);
            if (exist) {
                IndexCache.getIndexNamesExist().add(indexName);
            } else {
                IndexCache.getIndexNamesNotExist().add(indexName);
            }
            return exist;
        }

        /**
         * 判断索引是否存在
         *
         * @param indexName
         * @return
         */
        public boolean isIndexExists(String indexName, int a) {
            if (StringUtils.isEmpty(indexName)) {
                return false;
            }
            if (IndexCache.getIndexNamesExist().contains(indexName)) {
                return true;
            }
            if (IndexCache.getIndexNamesNotExist().contains(indexName)) {
                boolean isOk;
                if (a == 1) {
                    isOk = doubleCheck(indexName);
                } else {
//                    isOk = doubleCheckMonth(indexName);
                    isOk = isExistIndex(indexName);
                }
                if (isOk) {
                    IndexCache.getIndexNamesNotExist().remove(indexName);
                }
                return isOk;
            }
            boolean exist = isExistIndex(indexName);
            if (exist) {
                IndexCache.getIndexNamesExist().add(indexName);
            } else {
                IndexCache.getIndexNamesNotExist().add(indexName);
            }
            return exist;
        }

        /**
         * 对当日的索引进行二次校验,防止后续生成后导致判断错误
         *
         * @param indexName
         * @return
         */
        private boolean doubleCheck(String indexName) {
            String time = timeAfterIndex
                    ? indexName.substring(indexName.length() - indexConfig.getTimeForamt().length())
                    : indexName.substring(0, indexConfig.getTimeForamt().length());
            boolean isBefore = TimeTools.parseDate(time, indexConfig.getTimeForamt())
                    .before(TimeTools.getNowBeforeByDay(-1));
            //isAfter判断会导致延迟录入的索引查不到, 先注释掉 updated 20210122
//            boolean isAfter = TimeTools.parseDate(time, indexConfig.getTimeForamt()).after(TimeTools.getNowBeforeByDay(2));
            if (isBefore) {
                return isExistIndex(indexName);
            }
            return false;
        }

        /**
         * 对当日的索引进行二次校验,防止后续生成后导致判断错误
         *
         * @param indexName
         * @return
         */
        private boolean doubleCheck(String indexName, String timeForamt) {
            String time = timeAfterIndex
                    ? indexName.substring(indexName.length() - timeForamt.length())
                    : indexName.substring(0, timeForamt.length());
            boolean isBefore = TimeTools.parseDate(time, timeForamt)
                    .before(TimeTools.getNowBeforeByDay(-1));
            boolean isAfter = TimeTools.parseDate(time, timeForamt).after(TimeTools.getNowBeforeByDay(2));
            if (isBefore && isAfter) {
                return isExistIndex(indexName);
            }
            return false;
        }

        private boolean doubleCheckMonth(String indexName, String timeFormat) {
            String time = timeAfterIndex
                    ? indexName.substring(indexName.length() - timeFormat.length())
                    : indexName.substring(0, timeFormat.length());
            boolean isBefore = TimeTools.parseDate(time, timeFormat)
                    .before(TimeTools.getNowBeforeByMonth(-1));
            boolean isAfter = TimeTools.parseDate(time, timeFormat).after(TimeTools.getNowBeforeByMonth(2));
            if (isBefore && isAfter) {
                return isExistIndex(indexName);
            }
            return false;
        }

        private boolean doubleCheckMonth(String indexName) {
            String time = timeAfterIndex
                    ? indexName.substring(indexName.length() - indexConfig.getTimeFormatMonth().length())
                    : indexName.substring(0, indexConfig.getTimeFormatMonth().length());
            boolean isBefore = TimeTools.parseDate(time, indexConfig.getTimeFormatMonth())
                    .before(TimeTools.getNowBeforeByMonth(-1));
            boolean isAfter = TimeTools.parseDate(time, indexConfig.getTimeFormatMonth()).after(TimeTools.getNowBeforeByMonth(2));
            if (isBefore && isAfter) {
                return isExistIndex(indexName);
            }
            return false;
        }

        /**
         * 校验是否超过最大返回值数量 超过会重新赋值
         *
         * @param queryModel
         */
        public void checkSizeAndReset(EsQueryModel queryModel) {
            int start = queryModel.getStart();
            int count = queryModel.getCount();
            int total = indexConfig.getResultTotal();
            if (start + count > total) {
                // 超出时，最大只能查到配置的指定条数据
                int newCount = count - ((start + count) - total);
                queryModel.setCount(newCount > 0 ? newCount : 0);
                queryModel.setOverFlow(true);
            }
        }

        /**
         * 判断指定索引是否存在,直接查询es
         *
         * @param index
         * @return
         */
        public boolean isExistIndex(String index) {
            RestClient restClient = client.getLowLevelClient();
            Response response = null;
            try {
                Request request = null;
                // 多集群情况下
                if (multiClusterConfig.isOpen()) {
                    request = new Request("get", indexRoute(index) + "/_count");
                    response = restClient.performRequest(request);
                } else {
                    request = new Request("get", indexRoute(index));
                    response = restClient.performRequest(request);
                }
                return 200 == response.getStatusLine().getStatusCode();
            } catch (ResponseException e) {
                if (404 != e.getResponse().getStatusLine().getStatusCode()) {
                    log.error("", e);
                }
                return false;
            } catch (IOException e) {
                log.error("", e);
                return false;
            }
        }

        /**
         * 获取距离当前日期n天前的索引名称
         *
         * @param indexName
         * @param day
         * @return
         */
        public String getIndexName(String indexName, int day) {
            return getIndexName(indexName, TimeTools.getNowBeforeByDay(day));
        }

        /**
         * 获取距离当前日期n天前的索引名称
         *
         * @param indexName
         * @return
         */
        public String getIndexName(String indexName) {

            if (isIndexExists(indexName, 1)) {
                return indexName;
            }
            return null;
        }


        /**
         * 获取索引名称
         *
         * @param indexName
         * @param date
         * @return
         */
        public String getIndexName(String indexName, Date date) {
            // 兼容告警索引格式warnresulttmp-yyyy-MM-dd
            String time = TimeTools.format(date, indexConfig.getTimeForamt());
            if ("warnresulttmp".equals(indexName)) {
                time = TimeTools.format(date, "yyyy-MM-dd");
            }
            return getIndexName(indexName, time);
        }

        /**
         * 指定时间格式，获取索引名称
         *
         * @param indexName
         * @param date
         * @return
         */
        public String getIndexNameByDateFormat(String indexName, Date date, String dateFormat) {
            String time = TimeTools.format(date, dateFormat);
            return getIndexName(indexName, time);
        }

        /**
         * 指定时间格式，获取索引名称
         *
         * @param indexName
         * @param date
         * @return
         */
        public String getIndexNameByDateFormat(String indexName, Date date, String dateFormat, String indexFormat) {
            String time = TimeTools.format(date, dateFormat);
            return getIndexName(indexName, time, indexFormat);
        }

        /**
         * 获取索引名称
         *
         * @param indexName
         * @return
         */
        public String getIndexName(String indexName, String time, String indexFormat) {
            StringBuilder sb = new StringBuilder(indexFormat);
            int s1 = sb.indexOf(INDEX);
            int s2 = s1 + INDEX.length();
            sb.replace(s1, s2, indexName);
            int s3 = sb.indexOf(TIME);
            int s4 = s3 + TIME.length();
            return sb.replace(s3, s4, time).toString();
        }

        /**
         * 获取索引名称
         *
         * @param indexName
         * @param time
         * @return
         */
        public String getIndexName(String indexName, String time) {
            StringBuilder sb = new StringBuilder(indexConfig.getIndexFormat());
            int s1 = sb.indexOf(INDEX);
            int s2 = s1 + INDEX.length();
            sb.replace(s1, s2, indexName);
            int s3 = sb.indexOf(TIME);
            int s4 = s3 + TIME.length();
            return sb.replace(s3, s4, time).toString();
        }

        /**
         * 构建es查询对象
         *
         * @param index
         * @param resultFields
         * @return
         */
        public EsQueryModel buildQueryModel(String index, String[] resultFields) {
            EsQueryModel queryModel = new EsQueryModel();

            queryModel.setIndexName(index);

            queryModel.setCount(indexConfig.getResultMax());
            queryModel.setStart(0);
            queryModel.setTypeName(TYPE);
            queryModel.setResultFields(resultFields);

            // 处理查询语句
            queryModel.setQueryBuilder(JsonQueryTools.getQueryBuilder(queryModel.getQuery()));

            queryModel.setUseAggre(false);
            queryModel.setUseTimeRange(false);
            queryModel.setUseFilter(false);
            queryModel.setLimitResultFields(true);
            return queryModel;
        }

        /**
         * 添加时间范围过滤
         *
         * @param queryModel
         * @param queryBuilder
         */
        public void addTimeRange(EsQueryModel queryModel, BoolQueryBuilder queryBuilder) {
            if (null == queryModel.getStartTime() || null == queryModel.getEndTime()) {
                return;
            }
            if (queryModel.isNeedTimeFormat()) {
                String sDate = TimeTools.format(queryModel.getStartTime(), queryModel.getTimeFormat());
                String eDate = TimeTools.format(queryModel.getEndTime(), queryModel.getTimeFormat());
                queryBuilder.must(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(sDate).to(eDate));
            } else {
                queryBuilder.must(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(queryModel.getStartTime())
                        .to(queryModel.getEndTime()));
            }
        }

        /**
         * 添加时间范围过滤
         *
         * @param queryModel
         */
        public void setTimeRangeFilter(EsQueryModel queryModel) {
            if (null == queryModel.getStartTime() || null == queryModel.getEndTime()) {
                return;
            }
            Date sDate = queryModel.getStartTime();
            Date eDate = queryModel.getEndTime();
            // dm-netflow 时间格式为yyyy-MM-dd
            if ("dm-netflow".equals(queryModel.getIndexName())) {
                queryModel.setFilterBuilder(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(TimeTools.format(sDate, "yyyy-MM-dd")).to(TimeTools.format(eDate, "yyyy-MM-dd")));
            } else {
                queryModel.setFilterBuilder(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(sDate).to(eDate));
            }
        }

        /**
         * 添加时间范围过滤
         *
         * @param queryModel
         */
        public void setTimeRangeFilter(EsQueryModel queryModel, String timeFormat) {
            if (null == queryModel.getStartTime() || null == queryModel.getEndTime()) {
                return;
            }
            String sDate = TimeTools.format(queryModel.getStartTime(), timeFormat);
            String eDate = TimeTools.format(queryModel.getEndTime(), timeFormat);
            queryModel.setFilterBuilder(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(sDate).to(eDate));
        }

        /**
         * es 查询结果转Json
         *
         * @param resp
         * @return
         */
        public Optional<JSONObject> wrapResponse(Response resp) {
            try {
                return Optional.of(JSON.parseObject(resp.getEntity().getContent(), JSONObject.class));
            } catch (UnsupportedOperationException | IOException e) {
                log.error("", e);
            }
            return Optional.empty();
        }

        /**
         * lowlevelclient 查询es
         *
         * @param method
         * @param endpoint
         * @param headers
         * @return
         */
        public Optional<Response> lowLevelRequest(String method, String endpoint, Header... headers) {
            try {
                Request request = new Request(method, endpoint);
                RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();
                if (headers != null && headers.length > 0) {
                    Arrays.stream(headers).forEach(header -> {
                        builder.addHeader(header.getName(), header.getValue());
                    });

                }
                request.setOptions(builder);
                return Optional.of(client.getLowLevelClient().performRequest(request));
            } catch (IOException e) {
                log.error("", e);
            }
            return Optional.empty();
        }

        /**
         * 获取json值
         *
         * @param obj
         * @param key 分隔符 ":"
         * @return
         */
        public <T> T getJSONObjectValue(JSONObject obj, String key) {
            return getJSONObjectValue(obj, key, null);
        }

        /**
         * 获取json值
         *
         * @param obj
         * @param key 分隔符 ":"
         * @return
         */
        @SuppressWarnings("unchecked")
        public <T> T getJSONObjectValue(JSONObject obj, String key, Class<T> clazz) {
            if (StringUtils.isEmpty(key)) {
                return (T) obj;
            }
            String[] ks = key.split(":");
            JSONObject objTmp = obj;
            Object v = null;
            int last = ks.length - 1;
            for (int i = 0; i < ks.length; i++) {
                if (i == last) {
                    if (null != clazz) {
                        v = objTmp.getObject(ks[i], clazz);
                    } else {
                        v = objTmp.get(ks[i]);
                    }
                    break;
                }
                objTmp = objTmp.getJSONObject(ks[i]);
            }
            return (T) v;
        }

        /**
         * 查询es并获取结果
         *
         * @param key
         * @param method
         * @param endpoint
         * @param headers
         * @return
         */
        public <T> Optional<T> lowLevelResponseValue(String key, String method, String endpoint, Class<T> clazz,
                                                     Header[] headers) {
            Optional<Response> resp = lowLevelRequest(method, endpoint, headers);
            if (resp.isPresent()) {
                Optional<JSONObject> jobj = wrapResponse(resp.get());
                if (jobj.isPresent()) {
                    return Optional.of(getJSONObjectValue(jobj.get(), key, clazz));
                }
            }
            return Optional.empty();
        }

        /**
         * 查询es并获取结果,get 请求
         *
         * @param key
         * @param endpoint
         * @return
         */
        public <T> Optional<T> lowLevelResponseValue(String key, String endpoint) {
            return lowLevelResponseValue(key, endpoint, null);
        }

        /**
         * 查询es并获取结果,get 请求
         *
         * @param key
         * @param endpoint
         * @return
         */
        public <T> Optional<T> lowLevelResponseValue(String key, String endpoint, Class<T> clazz) {
            return lowLevelResponseValue(key, Common.GET, endpoint, clazz, new Header[0]);
        }

    }

    /**
     * 构造es查询model
     *
     * @param wrapper
     * @param model
     * @param indexName
     * @param indexNames
     * @param typeName
     * @param timeField
     * @param needTimeFormat
     * @param timeFormat
     * @return
     */
    public static EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, PageModel model, String indexName, String[] indexNames, String typeName, String timeField, boolean needTimeFormat, String timeFormat) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        queryModel.setIndexNames(indexNames);
        queryModel.setIndexName(indexName);
        // 设置时间字段
        queryModel.setTimeField(timeField);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(typeName);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (null != model.getMyStartTime() && null != model.getMyEndTime()) {
            if (needTimeFormat) {
                queryModel.setNeedTimeFormat(needTimeFormat);
                queryModel.setTimeFormat(timeFormat);
            }
        }
        queryModel.setQueryBuilder(query);
        return queryModel;
    }


    public static EsQueryModel buildQueryModel2(QueryTools.QueryWrapper wrapper, PageModel model, String index) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(false);
        queryModel.setTypeName(QueryTools.TYPE);
        if (StringUtils.isNotEmpty(model.getOrder())) {
            queryModel.setSort(true);
            queryModel.setSortFields(new String[]{model.getOrder()});
            queryModel.setSortOrder(SortOrder.DESC.toString().equalsIgnoreCase(model.getBy()) ? SortOrder.DESC : SortOrder.ASC);
        }
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    /**
     * 构造es查询model
     *
     * @param wrapper
     * @param model
     * @param index
     * @param time
     * @return
     */
    public static EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, PageModel model, String index, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        if (StringUtils.isNotEmpty(model.getOrder())) {
            queryModel.setSort(true);
            queryModel.setSortFields(new String[]{model.getOrder()});
            queryModel.setSortOrder(SortOrder.DESC.toString().equalsIgnoreCase(model.getBy()) ? SortOrder.DESC : SortOrder.ASC);
        }
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    /**
     * 构造es查询model
     *
     * @param wrapper
     * @param model
     * @param index
     * @param time
     * @return
     */
    public static EsQueryModel buildQueryModelByMonth(QueryTools.QueryWrapper wrapper, PageModel model, String index, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = wrapper.getMonthIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
//        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    /**
     * 构造es查询model
     *
     * @param wrapper
     * @param model
     * @param indexs
     * @param time
     * @return
     */
    public static EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, PageModel model, String[] indexs, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }

        List<String> allIndexList = new ArrayList<>();
        if (indexs.length > 0) {
            for (String index : indexs) {
                allIndexList.addAll(wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime()));
            }
        }

        if (!allIndexList.isEmpty()) {
            queryModel.setIndexNames(allIndexList.toArray(new String[allIndexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
//        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    /**
     * 构造es查询model
     *
     * @param wrapper
     * @param model
     * @param index
     * @return
     */
    public static EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, PageModel model, String index) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        String indexName = wrapper.getIndexName(index);

        if (StringUtils.isNotEmpty(indexName)) {
            queryModel.setIndexName(indexName);
        }

        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(false);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    /**
     * 构造es查询model
     *
     * @param wrapper
     * @param model
     * @param index
     * @param time
     * @return
     */
    public static EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, PageModel model, String index, String time, String timeFormat, boolean isMonthIndex) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = isMonthIndex ? wrapper.getMonthIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime()) : wrapper.getIndexNames(index, queryModel
                .getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        } else {
            queryModel.setIndexName(index);
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setNeedTimeFormat(true);
        queryModel.setTimeFormat(timeFormat);
//        wrapper.setTimeRangeFilter(queryModel, timeFormat);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    /**
     * 取总数
     */
    public static int getHits(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper) {
        int result = 0;
        Map<String, Object> responseMap = wrapper.getAggResponse(queryModel);
        if (responseMap != null) {
            Map<String, Object> hitsMap = (Map<String, Object>) responseMap.get("hits");
            result = (Integer) hitsMap.get("total");
        }
        return result;
    }

    /**
     * 统计文件大小数量
     *
     * @param aggItem
     * @return
     */
    public static String count(Map<String, Object> aggItem) {
        Integer key = (Integer) aggItem.get("key");
        return FileSizeEnum.toLabel(key);
    }

    /**
     * 简单聚合
     *
     * @param queryModel
     * @param wrapper
     * @param entry
     * @return
     */
    public static List<Map<String, Object>> simpleAggregation(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, ExchangeDTO entry) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(entry.getAggField()).size(entry.getAggSize());
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("agg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
        if (bucketsMap == null || !bucketsMap.containsKey("buckets")) return result;
        List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
        if (entry.isCalc()) {
            aggItems.stream().collect(Collectors.toMap(QueryTools::count, map -> (Integer) map.get("doc_count"), Integer::sum)).forEach((k, v) -> {
                if (!Objects.equals("0", k)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(entry.getKeyField(), k);
                    map.put(entry.getValueField(), v);
                    result.add(map);
                }
            });
        } else {
            aggItems.forEach(aggItem -> result.add(MapUtil.builder(entry.getKeyField(), aggItem.get("key"))
                    .put(entry.getValueField(), aggItem.get("doc_count")).build()));
        }
        return result;
    }

    /**
     * 简单聚合
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param keyFieldRe
     * @param valueFieldRe
     * @return
     */
    public static List<Map<String, Object>> simpleAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                      String aggField, int size, String keyFieldRe, String valueFieldRe) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("agg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
        if (bucketsMap.containsKey("buckets")) {
            List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
            aggItems.forEach(aggItem -> {
                Map<String, Object> tmp = new HashMap<>();
                tmp.put(keyFieldRe, aggItem.get("key"));
                tmp.put(valueFieldRe, aggItem.get("doc_count"));
                result.add(tmp);
            });
        }
        return result;
    }

    /**
     * 简单聚合(minDocSize过滤)
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param minDocSize
     * @param keyFieldRe
     * @param valueFieldRe
     * @return
     */
    public static List<Map<String, Object>> simpleAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, int minDocSize, String keyFieldRe, String valueFieldRe) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.minDocCount(minDocSize);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyFieldRe, aggItem.get("key"));
                        tmp.put(valueFieldRe, aggItem.get("doc_count"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合+tophit
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param keyField
     * @param valueField
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleAggAndTopHit(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                               String aggField, int size, String keyField, String valueField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(CommonTools.underLineToCamel(topHitFields[i]), row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> sumAggDate(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                       ExchangeDTO entry, boolean pretty) {
        List<Map<String, Object>> result = new ArrayList<>();
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(entry.getSumAddField());
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(entry.getDateField());
        dateAgg.dateHistogramInterval(entry.getInterval());
        dateAgg.offset(entry.getOffset());
        dateAgg.timeZone(DateTimeZone.forOffsetHours(entry.getOffset()).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(entry.getDateFormat());
        queryModel.setAggregationBuilder(dateAgg.subAggregation(sumAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null && !dataAggMap.containsKey("dateAgg")) return result;
        Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("dateAgg");
        if (oneAggMap == null && !oneAggMap.containsKey("buckets")) return result;
        List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
        oneAggItems.forEach(aggItem -> {
            if (!aggItem.containsKey("sumAgg")) return;
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(entry.getDateFieldKey(), aggItem.get("key_as_string"));
            Number number = ((Map<String, Double>) aggItem.get("sumAgg")).get("value");
            if (pretty) {
                DataSize dataSize = DataSize.ofBytes(number.longValue());
                Double decimalValue = dataSize.toGigabytes();
                tmp.put(entry.getValueField(), decimalValue);
            } else {
                tmp.put(entry.getValueField(), number.doubleValue());
            }
            result.add(tmp);
        });
        return result;
    }

    public static List<Map<String, Object>> simpleTermAndSumAggDate(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, ExchangeDTO entry) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(entry.getDateField());
        dateAgg.dateHistogramInterval(entry.getInterval());
        dateAgg.offset(entry.getOffset());
        dateAgg.timeZone(DateTimeZone.forOffsetHours(entry.getOffset()).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(entry.getDateFormat());
        for (String sumAggField : entry.getSumAggFields()) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg_" + sumAggField).field(sumAggField);
            queryModel.addMulAggregationBuilders(sumAgg);
            dateAgg.subAggregation(sumAgg);
        }
        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("dateAgg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
        if (!bucketsMap.containsKey("buckets")) return result;
        List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
        Map<String, Double> totals = aggItems.stream().collect(Collectors.toMap(map -> map.get("key_as_string").toString(), QueryTools::total, Double::sum));
        for (Map.Entry<String, Double> total : totals.entrySet()) {
            HashMap<String, Object> map = MapUtil.newHashMap();
            map.put(entry.getDateFieldKey(), total.getKey());
            map.put(entry.getValueField(), total.getValue());
            result.add(map);
        }
        return result;
    }

    /**
     * 简单聚合+加和
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param sumAggField
     * @param keyFieldRe
     * @param sumFieldRe
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleTermAndSumAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                                String aggField, int size, String sumAggField,
                                                                String keyFieldRe, String sumFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.order(BucketOrder.aggregation("sumAgg", false));
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumAggField);
        agg.subAggregation(sumAgg);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("agg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
        if (!bucketsMap.containsKey("buckets")) return result;
        List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
        aggItems.forEach(aggItem -> {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(keyFieldRe, aggItem.get("key"));
            tmp.put(sumFieldRe, aggItem.get("doc_count"));
            if (topHitFields != null) {
                List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                for (String topHitField : topHitFields) {
                    tmp.put(CommonTools.underLineToCamel(topHitField), row.get(topHitField));
                }
            }
            result.add(tmp);
        });
        return result;
    }

    public static List<Map<String, Object>> simpleTermAndSumAgg2(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                                String aggField, int size, String sumAggField,
                                                                String keyFieldRe, String sumFieldRe) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.order(BucketOrder.aggregation("sumAgg", false));
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumAggField);
        agg.subAggregation(sumAgg);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("agg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
        if (!bucketsMap.containsKey("buckets")) return result;
        List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
        aggItems.forEach(aggItem -> {
            Map<String, Object> tmp = new HashMap<>();
            tmp.put(keyFieldRe, aggItem.get("key"));
            tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));
            Number number = ((Map<String, Double>) aggItem.get("sumAgg")).get("value");
            DataSize dataSize = DataSize.ofBytes(number.longValue());
            Double decimalValue = dataSize.toGigabytes();
            tmp.put(sumFieldRe, decimalValue);
            result.add(tmp);
        });
        return result;
    }

    /**
     * 简单sum聚合
     *
     * @param queryModel
     * @param wrapper
     * @param sumAggFields
     * @param sumFieldsRe
     * @return
     */
    public static Map<String, Object> simpleSumAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                   String[] sumAggFields, String[] sumFieldsRe) {
        Map<String, Object> result = new HashMap<>();
        for (String sumAggField : sumAggFields) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg_" + sumAggField).field(sumAggField);
            queryModel.addMulAggregationBuilders(sumAgg);
        }
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> aggItems = (Map<String, Object>) aggMap.get("aggregations");
            for (int i = 0; i < sumAggFields.length; i++) {
                String sumAggField = sumAggFields[i];
                result.put(sumFieldsRe[i], ((Map<String, Double>) aggItems.get("sumAgg_" + sumAggField)).get("value"));
            }
        }
        return result;
    }

    /**
     * 简单avg
     *
     * @param queryModel
     * @param wrapper
     * @param avgFields
     * @param avgFieldsRe
     * @return
     */
    public static Map<String, Object> simpleAvgAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String[] avgFields, String[] avgFieldsRe) {
        Map<String, Object> result = new HashMap<>();

        for (String avgAggField : avgFields) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("avgAgg_" + avgAggField).field(avgAggField);
            queryModel.addMulAggregationBuilders(sumAgg);
        }
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> aggItems = (Map<String, Object>) aggMap.get("aggregations");
            for (int i = 0; i < avgFields.length; i++) {
                String avgAggField = avgFields[i];
                result.put(avgFieldsRe[i], ((Map<String, Double>) aggItems.get("avgAgg_" + avgAggField)).get("value"));
            }
        }
        return result;
    }

    /**
     * 聚合求和
     *
     * @param queryModel
     * @param wrapper
     * @param ex
     * @return
     */
    public static List<Map<String, Object>> aggregationSum(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                           ExchangeDTO ex, Map<String, List<Pair<Long, Long>>> pairMap,
                                                           List<CommunicationModel> ipRangeList) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(ex.getAggField()).size(ex.getAggSize());
        for (String sumAggField : ex.getSumAggFields()) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg_" + sumAggField).field(sumAggField);
            agg.subAggregation(sumAgg);
        }
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("agg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
        if (bucketsMap.containsKey("buckets")) {
            List<Map<String,Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
            Map<String, Double> totals = aggItems.stream().collect(Collectors.toMap(map -> {
                Number number = (Number) map.get("key");
                long ipNum = number.longValue();
                for (Map.Entry<String, List<Pair<Long, Long>>> entry : pairMap.entrySet()) {
                    List<Pair<Long, Long>> ranges = entry.getValue();
                    for (Pair<Long, Long> range : ranges) {
                        if (ipNum >= range.getFirst() && ipNum <= range.getSecond()) {
                            return entry.getKey();
                        }
                    }
                }
                return "0";
            }, QueryTools::total, Double::sum));
            for (Map.Entry<String, Double> entry : totals.entrySet()) {
                HashMap<String, Object> map = MapUtil.newHashMap();
                map.put("ipRange", entry.getKey());
                map.put("totalPkt", entry.getValue());
                ipRangeList.stream().filter(m -> entry.getKey().equals(m.getRangeIps())).findFirst()
                        .ifPresent(m -> map.put("name", m.getName()));
                result.add(map);
            }
        }
        return result;
    }

    public static Double total(Map<String,Object> aggItem){
        Double clientTotalPkt = ((Map<String, Double>) aggItem.get("sumAgg_client_total_pkt")).get("value");
        Double serverTotalPkt = ((Map<String, Double>) aggItem.get("sumAgg_server_total_pkt")).get("value");
        return clientTotalPkt + serverTotalPkt;
    }

    /**
     * 简单聚合+加和
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param sumAggFields
     * @param keyFieldRe
     * @param sumFieldsRe
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleTermAndSumAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                                String aggField, int size, String[] sumAggFields, String[] sumFieldsRe,
                                                                String keyFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        for (String sumAggField : sumAggFields) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg_" + sumAggField).field(sumAggField);
            agg.subAggregation(sumAgg);
        }
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyFieldRe, aggItem.get("key"));
                        tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));

                        for (int i = 0; i < sumAggFields.length; i++) {
                            String sumAggField = sumAggFields[i];
                            tmp.put(sumFieldsRe[i], ((Map<String, Double>) aggItem.get("sumAgg_" + sumAggField)).get("value"));
                        }
                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(topHitFields[i], row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合+加和
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param sumAggFields
     * @param keyFieldRe
     * @param sumFieldsRe
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleTermAndSumAgg2(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String cardField, String[] sumAggFields, String[] sumFieldsRe, String keyFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        agg.subAggregation(AggregationBuilders.cardinality("card_agg").field(cardField));

        for (String sumAggField : sumAggFields) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg_" + sumAggField).field(sumAggField);
            agg.subAggregation(sumAgg);
        }
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyFieldRe, aggItem.get("key"));
                        tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));
                        tmp.put(cardField + "Count", ((Map<String, Double>) aggItem.get("card_agg")).get("value"));
                        for (int i = 0; i < sumAggFields.length; i++) {
                            String sumAggField = sumAggFields[i];
                            tmp.put(sumFieldsRe[i], ((Map<String, Double>) aggItem.get("sumAgg_" + sumAggField)).get("value"));
                        }
                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(topHitFields[i], row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合+加和
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param keyFieldRe
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleTermAndStatsAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String sumAggField, String keyFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        StatsAggregationBuilder sumAgg = AggregationBuilders.stats("sumAgg_" + sumAggField).field(sumAggField);
        agg.subAggregation(sumAgg);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyFieldRe, aggItem.get("key"));
                        tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));

                        tmp.putAll((Map<String, Object>) aggItem.get("sumAgg_" + sumAggField));
                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(topHitFields[i], row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合 + 基本统计(count max min avg sum) 以及 平方和、方差、标准差、平均值加/减两个标准差的区间
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param statsAggField 基本统计(count max min avg sum) 以及 平方和、方差、标准差、平均值加/减两个标准差的区间
     * @param keyFieldRe
     * @param topHitFields  (驼峰字段名返回)
     * @return 比正常返回多出的统计指标:
     * "count": 4,
     * "min": 80,
     * "max": 263,
     * "avg": 171.5,
     * "sum": 686,
     * "sum_of_squares": 151138,
     * "variance": 8372.25,
     * "std_deviation": 91.5,
     * "std_deviation_bounds": {
     * "upper": 354.5,
     * "lower": -11.5
     * }
     */
    public static List<Map<String, Object>> simpleTermAndExtendStatsAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String statsAggField, String keyFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        //包含基本统计(count max min avg sum) 以及 平方和、方差、标准差、平均值加/减两个标准差的区间
        ExtendedStatsAggregationBuilder sumAgg = AggregationBuilders.extendedStats("sumAgg_" + statsAggField).field(statsAggField);
        agg.subAggregation(sumAgg);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyFieldRe, aggItem.get("key"));
                        tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));

                        tmp.putAll((Map<String, Object>) aggItem.get("sumAgg_" + statsAggField));
                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(CommonTools.underLineToCamel(topHitFields[i]), row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 两级聚合 + 基本统计(count max min avg sum) 以及 平方和、方差、标准差、平均值加/减两个标准差的区间
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param statsAggField 基本统计(count max min avg sum) 以及 平方和、方差、标准差、平均值加/减两个标准差的区间
     * @param keyFieldRe
     * @param topHitFields  (驼峰字段名返回)
     * @return 比正常返回多出的统计指标:
     * "count": 4,
     * "min": 80,
     * "max": 263,
     * "avg": 171.5,
     * "sum": 686,
     * "sum_of_squares": 151138,
     * "variance": 8372.25,
     * "std_deviation": 91.5,
     * "std_deviation_bounds": {
     * "upper": 354.5,
     * "lower": -11.5
     * }
     */
    public static List<Map<String, Object>> twoLevelTermAndExtendStatsAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size,
                                                                          String secondAggField, int secondSize,
                                                                          String statsAggField, String keyFieldRe, String secondkeyFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);

        TermsAggregationBuilder subAgg = new TermsAggregationBuilder("sub_agg");
        subAgg.field(secondAggField).size(secondSize);
        agg.subAggregation(subAgg);

        if (topHitFields != null) {
            subAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        //包含基本统计(count max min avg sum) 以及 平方和、方差、标准差、平均值加/减两个标准差的区间
        ExtendedStatsAggregationBuilder sumAgg = AggregationBuilders.extendedStats("sumAgg_" + statsAggField).field(statsAggField);
        subAgg.subAggregation(sumAgg);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("sub_agg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("sub_agg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(keyFieldRe, aggItem.get("key"));
                                    tmp.put(secondkeyFieldRe, item.get("key"));
                                    tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));
                                    tmp.putAll((Map<String, Object>) item.get("sumAgg_" + statsAggField));
                                    if (topHitFields != null) {
                                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) item.get("data")).get("hits")).get("hits");
                                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                        for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                            tmp.put(CommonTools.underLineToCamel(topHitFields[i]), row.get(topHitFields[i]));
                                        }
                                    }
                                    result.add(tmp);

                                });

                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> simpleTermAndSubSumAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                                   String aggField, int size, String subAggField,
                                                                   String[] sumAggFields, String[] sumFieldsRe,
                                                                   String keyFieldRe, String... topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);

        TermsAggregationBuilder subAgg = new TermsAggregationBuilder("sub_agg");
        subAgg.field(subAggField).size(size);
        agg.subAggregation(subAgg);

        if (topHitFields != null) {
            subAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        for (String sumAggField : sumAggFields) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg_" + sumAggField).field(sumAggField);
            subAgg.subAggregation(sumAgg);
        }
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("sub_agg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("sub_agg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    result.add(tmp);
                                    tmp.put(keyFieldRe, aggItem.get("key"));
                                    tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));

                                    for (int i = 0; i < sumAggFields.length; i++) {
                                        String sumAggField = sumAggFields[i];
                                        tmp.put(sumFieldsRe[i], ((Map<String, Double>) item.get("sumAgg_" + sumAggField)).get("value"));
                                    }
                                    tmp.put(subAggField, item.get("key"));
                                    tmp.put(subAggField + "Count", item.get("doc_count"));

                                    if (topHitFields != null) {
                                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) item.get("data")).get("hits")).get("hits");
                                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                        for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                            tmp.put(topHitFields[i], row.get(topHitFields[i]));
                                        }
                                    }
                                });
                            }
                        }

                    });
                }
            }
        }
        return result;
    }


    public static List<Map<String, Object>> simpleTermAndSub2SumAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String subAggField, String subSubAggField,
                                                                    String[] sumAggFields, String[] sumFieldsRe, String keyFieldRe, String... topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);

        TermsAggregationBuilder subAgg = new TermsAggregationBuilder("sub_agg");
        subAgg.field(subAggField).size(size);
        agg.subAggregation(subAgg);

        TermsAggregationBuilder subSubAgg = new TermsAggregationBuilder("sub_sub_agg");
        subSubAgg.field(subSubAggField).size(size);
        subAgg.subAggregation(subSubAgg);

        if (topHitFields != null) {
            subSubAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        for (String sumAggField : sumAggFields) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg_" + sumAggField).field(sumAggField);
            subSubAgg.subAggregation(sumAgg);
        }
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("sub_agg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("sub_agg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                List<Map<String, Object>> apps = new ArrayList<>();
                                twoAggItems.forEach(item -> {
                                    if (item.containsKey("sub_sub_agg")) {
                                        Map<String, Object> threeAggMap = (Map<String, Object>) item.get("sub_sub_agg");
                                        if (threeAggMap.containsKey("buckets")) {
                                            List<Map<String, Object>> threeAggItems = (List<Map<String, Object>>) threeAggMap.get("buckets");
                                            threeAggItems.forEach(nestedItem -> {
                                                Map<String, Object> tmp = new HashMap<>();
                                                tmp.put(keyFieldRe, aggItem.get("key"));
                                                tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));

                                                if (topHitFields != null) {
                                                    List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) nestedItem.get("data")).get("hits")).get("hits");
                                                    Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                                    for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                                        tmp.put(topHitFields[i], row.get(topHitFields[i]));
                                                    }
                                                }

                                                for (int i = 0; i < sumAggFields.length; i++) {
                                                    String sumAggField = sumAggFields[i];
                                                    tmp.put(sumFieldsRe[i], ((Map<String, Double>) nestedItem.get("sumAgg_" + sumAggField)).get("value"));
                                                }
                                                tmp.put(subAggField, item.get("key"));
                                                tmp.put(subSubAggField, nestedItem.get("key"));
                                                result.add(tmp);
                                            });
                                        }
                                    }
                                });
                            }
                        }

                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合+加和+去重
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param sumAggFields
     * @param keyFieldRe
     * @param sumFieldsRe
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleTermAndSumCarAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String cardinalityAggField, String[] sumAggFields, String[] sumFieldsRe, String keyFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        for (String sumAggField : sumAggFields) {
            SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg_" + sumAggField).field(sumAggField);
            agg.subAggregation(sumAgg);
        }
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg1").field(cardinalityAggField).precisionThreshold(2000);
        agg.subAggregation(cardinalityAgg);

        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);

        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyFieldRe, aggItem.get("key"));
                        tmp.put(keyFieldRe + "Count", aggItem.get("doc_count"));

                        for (int i = 0; i < sumAggFields.length; i++) {
                            String sumAggField = sumAggFields[i];
                            tmp.put(sumFieldsRe[i], ((Map<String, Double>) aggItem.get("sumAgg_" + sumAggField)).get("value"));
                        }

                        tmp.put(CommonTools.underLineToCamel(cardinalityAggField) + "Count", ((Map<String, Double>) aggItem.get("cardinalityAgg1")).get("value"));
                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(CommonTools.underLineToCamel(topHitFields[i]), row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合+去重个数2
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param cardinalityAggField1
     * @param cardinalityAggField2
     * @param keyField
     * @param cardinalityField1
     * @param cardinalityField2
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleCardinalityAgg2(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String cardinalityAggField1, String cardinalityAggField2, String keyField, String cardinalityField1, String cardinalityField2, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        CardinalityAggregationBuilder cardinalityAgg1 = AggregationBuilders.cardinality("cardinalityAgg1").field(cardinalityAggField1).precisionThreshold(2000);
        CardinalityAggregationBuilder cardinalityAgg2 = AggregationBuilders.cardinality("cardinalityAgg2").field(cardinalityAggField2).precisionThreshold(2000);
        agg.subAggregation(cardinalityAgg1);
        agg.subAggregation(cardinalityAgg2);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(keyField + "Count", aggItem.get("doc_count"));
                        tmp.put(cardinalityField1, ((Map<String, Double>) aggItem.get("cardinalityAgg1")).get("value"));
                        tmp.put(cardinalityField2, ((Map<String, Double>) aggItem.get("cardinalityAgg2")).get("value"));

                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(CommonTools.underLineToCamel(topHitFields[i]), row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合+去重个数
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param cardinalityAggField
     * @param keyField
     * @param cardinalityField
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleCardinalityAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String cardinalityAggField, String keyField, String cardinalityField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(cardinalityAggField).precisionThreshold(2000);
        agg.subAggregation(cardinalityAgg);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(keyField + "Count", aggItem.get("doc_count"));
                        tmp.put(cardinalityField, ((Map<String, Double>) aggItem.get("cardinalityAgg")).get("value"));

                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(CommonTools.underLineToCamel(topHitFields[i]), row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合(每组带出一行数据)
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param keyField
     * @param valueField
     * @return Map(keyField - > keyName, valueField - > count, " row " - > Map)
     */
    public static List<Map<String, Object>> simpleAggWithTopHit(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);

        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        tmp.put("row", row);
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单聚合(每组带出一行数据)
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param keyField
     * @param valueField
     * @return Map(keyField - > keyName, valueField - > count, " row " - > Map)
     */
    public static List<Map<String, Object>> simpleAggWithTopHit(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField, String[] rowFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);

        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        if (rowFields != null && rowFields.length > 0) {
                            for (String fd : rowFields) {
                                tmp.put(fd, row.get(fd));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 两级聚合
     *
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param oneAggSize
     * @param secondAggSize
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> twoLevelAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, int oneAggSize, int secondAggSize, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("secondAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(oneAggField, aggItem.get("key"));
                                    tmp.put(twoAggField, item.get("key"));
                                    tmp.put(valueField, item.get("doc_count"));
                                    result.add(tmp);
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    /**
     * 两级聚合，结果key是否转换驼峰
     *
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param oneAggSize
     * @param secondAggSize
     * @param valueField
     * @param transform
     * @return
     */
    public static List<Map<String, Object>> twoLevelAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, int oneAggSize, int secondAggSize, String valueField, boolean transform) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("secondAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(transform ? CommonTools.underLineToCamel(oneAggField) : oneAggField, aggItem.get("key"));
                                    tmp.put(transform ? CommonTools.underLineToCamel(twoAggField) : twoAggField, item.get("key"));
                                    tmp.put(valueField, item.get("doc_count"));
                                    result.add(tmp);
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> twoLevelAggAndDateAgg2(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                                   String oneAggField, String twoAggField, int oneAggSize,
                                                                   int secondAggSize, String dateAggField, String dateFieldKey,
                                                                   String dateFormat, DateHistogramInterval interval,
                                                                   int offset, boolean transform) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
        dateAgg.format(dateFormat);
        queryModel.setAggregationBuilder(dateAgg.subAggregation(oneAgg.subAggregation(secondAgg)));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations"))  return result;
        Map<String, Object> aggregations = (Map<String, Object>) aggMap.get("aggregations");
        if (aggregations == null || !aggregations.containsKey("dateAgg")) return result;
        Map<String, Object> dateAggMap = (Map<String, Object>) aggregations.get("dateAgg");
        if (!dateAggMap.containsKey("buckets")) return result;
        List<Map<String, Object>> dateAggItems = (List<Map<String, Object>>) dateAggMap.get("buckets");
        dateAggItems.forEach(dateItem -> {
            if (dateItem.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dateItem.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(oneAggItem -> {
                        if (oneAggItem.containsKey("secondAgg")) {
                            Map<String, Object> bucketsMap = (Map<String, Object>) oneAggItem.get("secondAgg");
                            if (bucketsMap.containsKey("buckets")) {
                                List<Map<String, Object>> secondAggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                                secondAggItems.forEach(secondAggItem -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(dateFieldKey, dateItem.get("key_as_string"));
                                    tmp.put(transform ? CommonTools.underLineToCamel(oneAggField) : oneAggField, oneAggItem.get("key"));
                                    tmp.put(transform ? CommonTools.underLineToCamel(twoAggField) : twoAggField, secondAggItem.get("key"));
                                    result.add(tmp);
                                });
                            }
                        }
                    });
                }
            }
        });
        return result;
    }

    /**
     * 两级聚合+时间分桶，结果key是否转换驼峰
     *
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param oneAggSize
     * @param secondAggSize
     * @param valueField
     * @param transform
     * @return
     */
    public static List<Map<String, Object>> twoLevelAggAndDateAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, int oneAggSize, int secondAggSize, String valueField, String dateAggField, String dateFormat, DateHistogramInterval interval, int offset, boolean transform) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
//        dateAgg.dateHistogramInterval(interval);
        dateAgg.calendarInterval(interval);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
        dateAgg.format(dateFormat);
        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg.subAggregation(dateAgg)));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("secondAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    if (item.containsKey("dateAgg")) {
                                        Map<String, Object> bucketsMap = (Map<String, Object>) item.get("dateAgg");
                                        if (bucketsMap.containsKey("buckets")) {
                                            List<Map<String, Object>> dateAggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                                            dateAggItems.forEach(dateItem -> {
                                                String date = dateItem.get("key_as_string").toString();
                                                Map<String, Object> tmp = new HashMap<>();
                                                tmp.put(transform ? CommonTools.underLineToCamel(dateAggField) : dateAggField, date);
                                                tmp.put(transform ? CommonTools.underLineToCamel(oneAggField) : oneAggField, aggItem.get("key"));
                                                tmp.put(transform ? CommonTools.underLineToCamel(twoAggField) : twoAggField, item.get("key"));
                                                tmp.put(valueField, dateItem.get("doc_count"));
                                                result.add(tmp);
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    /**
     * 两级聚合 带出一行数据
     *
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param oneAggSize
     * @param secondAggSize
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> twoLevelAggToHits(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                              String oneAggField, String twoAggField, int oneAggSize,
                                                              int secondAggSize, String valueField, String[] rowFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        secondAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("oneAgg")) return result;
        Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
        if (!oneAggMap.containsKey("buckets")) return result;
        List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
        oneAggItems.forEach(aggItem -> {
            if (aggItem.containsKey("secondAgg")) {
                Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                if (twoAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                    twoAggItems.forEach(item -> {
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) item.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(oneAggField, aggItem.get("key"));
                        tmp.put(twoAggField, item.get("key"));
                        tmp.put(valueField, item.get("doc_count"));
                        if (rowFields != null) {
                            for (String fd : rowFields) {
                                tmp.put(fd, row.get(fd));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        });
        return result;
    }

    /**
     * 两级聚合+tophits
     *
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param oneAggSize
     * @param secondAggSize
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> twoLevelAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, int oneAggSize, int secondAggSize,
                                                        String valueField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        if (topHitFields != null) {
            secondAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("secondAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(oneAggField, aggItem.get("key"));
                                    tmp.put(twoAggField, item.get("key"));
                                    tmp.put(valueField, item.get("doc_count"));

                                    if (topHitFields != null) {
                                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) item.get("data")).get("hits")).get("hits");
                                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                        for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                            tmp.put(topHitFields[i], row.get(topHitFields[i]));
                                        }
                                    }

                                    result.add(tmp);
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    /**
     * 三级聚合+tophits
     *
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param oneAggSize
     * @param secondAggSize
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> threeLevelAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, String threeAggField,
                                                          String[] aggFieldsDesc,
                                                          int oneAggSize, int secondAggSize, int thirdAggSize, String valueField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        TermsAggregationBuilder thirdAgg = new TermsAggregationBuilder("thirdAgg");
        thirdAgg.field(threeAggField).size(thirdAggSize);

        if (topHitFields != null) {
            thirdAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }

        if (aggFieldsDesc == null || aggFieldsDesc.length < 3) {
            aggFieldsDesc = new String[]{oneAggField, twoAggField, threeAggField};
        }

        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg.subAggregation(thirdAgg)));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    String[] finalAggFieldDesc = aggFieldsDesc;
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("secondAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    if (item.containsKey("thirdAgg")) {
                                        Map<String, Object> threeAggMap = (Map<String, Object>) item.get("thirdAgg");
                                        if (threeAggMap.containsKey("buckets")) {
                                            List<Map<String, Object>> threeAggItems = (List<Map<String, Object>>) threeAggMap.get("buckets");
                                            threeAggItems.forEach(nestedItem -> {
                                                Map<String, Object> tmp = new HashMap<>(16);
                                                tmp.put(finalAggFieldDesc[0], aggItem.get("key"));
                                                tmp.put(finalAggFieldDesc[1], item.get("key"));
                                                tmp.put(finalAggFieldDesc[2], nestedItem.get("key"));
                                                tmp.put(valueField, nestedItem.get("doc_count"));

                                                if (topHitFields != null) {
                                                    List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) nestedItem.get("data")).get("hits")).get("hits");
                                                    Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                                    for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                                        tmp.put(topHitFields[i], row.get(topHitFields[i]));
                                                    }
                                                }
                                                result.add(tmp);
                                            });
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    /**
     * 时间分桶+去重个数统计
     *
     * @param queryModel
     * @param wrapper
     * @param dateAggField        时间分桶字段
     * @param interval            时间间隔
     * @param dateFormat          时间格式
     * @param offset              时区偏移量 8代表数据里是utc时间  0代表数据里是北京数据
     * @param cardinalityAggField 去重统计字段
     * @param keyField            返回的keyField
     * @param valueField          返回的valueField
     * @return
     */

    public static List<Map<String, Object>> cardinalityAndDateAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String dateAggField, DateHistogramInterval interval, String dateFormat, int offset, String cardinalityAggField, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
//        dateAgg.timeZone(ZoneId.of("CTT"));
        dateAgg.format(dateFormat);
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(cardinalityAggField).precisionThreshold(2000);
        dateAgg.subAggregation(cardinalityAgg);
        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        String date = aggItem.get("key_as_string").toString();
                        Map ca = (Map) aggItem.get("cardinalityAgg");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, date);
                        tmp.put(valueField, ca.get("value"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> dateAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, ExchangeDTO entry) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(entry.getDateField());
        dateAgg.dateHistogramInterval(entry.getInterval());
        dateAgg.offset(entry.getOffset());
        dateAgg.timeZone(DateTimeZone.forOffsetHours(entry.getOffset()).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(entry.getDateFormat());
        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("dateAgg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
        if (bucketsMap.containsKey("buckets")) {
            List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
            aggItems.forEach(aggItem -> {
                Map<String, Object> tmp = new HashMap<>();
                tmp.put(entry.getDateFieldKey(), aggItem.get("key_as_string").toString());
                tmp.put(entry.getValueField(), aggItem.get("doc_count"));
                result.add(tmp);
            });
        }
        return result;
    }

    /**
     * 简单时间分桶统计
     *
     * @param queryModel
     * @param wrapper
     * @param dateAggField
     * @param interval
     * @param dateFormat
     * @param offset       时区偏移量 8代表数据里是utc时间  0代表数据里是北京数据
     * @param keyField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> dateAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,
                                                    String dateAggField, DateHistogramInterval interval,
                                                    String dateFormat, int offset, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.offset(offset);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(dateFormat);
        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("dateAgg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
        if (bucketsMap.containsKey("buckets")) {
            List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
            aggItems.forEach(aggItem -> {
                String date = aggItem.get("key_as_string").toString();
                Map<String, Object> tmp = new HashMap<>();
                tmp.put(keyField, date);
                tmp.put(valueField, aggItem.get("doc_count"));
                result.add(tmp);
            });
        }
        return result;
    }

    /**
     * 简单时间分桶加和统计
     *
     * @param queryModel
     * @param wrapper
     * @param interval
     * @param dateFormat
     * @param offset
     * @return
     */
    public static List<Map<String, Object>> dateAndSumAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String sumField,
                                                          String dateField, String dateFieldKey, DateHistogramInterval interval,
                                                          String dateFormat, int offset) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(dateFormat);

        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumField);
        dateAgg.subAggregation(sumAgg);

        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        String date = aggItem.get("key_as_string").toString();
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(dateFieldKey, date);
                        tmp.put("count", aggItem.get("doc_count"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单去重统计
     *
     * @param queryModel
     * @param wrapper
     * @param cardinalityAggField 去重统计的字段
     * @param threshold           精度
     * @param valueField          返回的key
     * @return
     */
    public static Map<String, Object> simpleCardinalityAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String cardinalityAggField, int threshold, String valueField) {
        Map<String, Object> result = new HashMap<>();
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(cardinalityAggField).precisionThreshold(threshold);
        queryModel.setAggregationBuilder(cardinalityAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> response = wrapper.getAggResponse(queryModel);
        if (response != null && response.containsKey("aggregations")) {
            Map<String, Object> aggMap = (Map<String, Object>) response.get("aggregations");
            if (aggMap != null && aggMap.containsKey("cardinalityAgg")) {
                Map ca = (Map) aggMap.get("cardinalityAgg");
                result.put(valueField, ca.get("value"));
            }
        }
        return result;
    }

    /**
     * 简单去重统计，直接返回统计结果
     *
     * @param queryModel
     * @param wrapper
     * @param cardinalityAggField 去重统计的字段
     * @param threshold           精度
     * @return
     */
    public static int simpleCardinalityAggAndReturnLong(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String cardinalityAggField, int threshold) {
        int result = 0;
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(cardinalityAggField).precisionThreshold(threshold);
        queryModel.setAggregationBuilder(cardinalityAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> response = wrapper.getAggResponse(queryModel);
        if (response != null && response.containsKey("aggregations")) {
            Map<String, Object> aggMap = (Map<String, Object>) response.get("aggregations");
            if (aggMap != null && aggMap.containsKey("cardinalityAgg")) {
                Map ca = (Map) aggMap.get("cardinalityAgg");
                result = (int) ca.get("value");
            }
        }
        return result;
    }

    /**
     * 两级聚合+cardinality+tophis
     *
     * @param queryModel
     * @param wrapper
     * @param aggField
     * @param size
     * @param countField
     * @param keyField
     * @param valueField
     * @param topHitFields
     * @return
     */
    public static List<Map<String, Object>> simpleAggWithCardinality(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String countField, String keyField, String valueField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        AggregationBuilder countAgg = AggregationBuilders.cardinality("countAgg").field(countField);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        agg.subAggregation(countAgg);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        tmp.put("second_count", ((Map<String, Double>) aggItem.get("countAgg")).get("value"));
                        if (topHitFields != null) {
                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                            for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                tmp.put(CommonTools.underLineToCamel(topHitFields[i]), row.get(topHitFields[i]));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * es按天查询索引model
     *
     * @param wrapper
     * @param model
     * @param index
     * @param time
     * @return
     */
    public static EsQueryModel buildQueryModelByDay(QueryTools.QueryWrapper wrapper, PageModel model, String index, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
//        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    /**
     * 获取总数
     *
     * @param model
     * @param wrapper
     */
    public static long getCount(EsQueryModel model, QueryTools.QueryWrapper wrapper) {
        long result = 0;
        SearchResponse response = wrapper.getSearchResponse(model);
        if (response != null) {
            result = response.getHits().getTotalHits().value;
        }
        return result;
    }

    /**
     * 写入数据
     */
    public static long writeData(List<?> data, String index, QueryTools.QueryWrapper wrapper) throws Exception {
        if (data == null || data.isEmpty()) {
            return -1;
        }
        RestHighLevelClient client = wrapper.client;
        BulkRequest request = new BulkRequest();
        request.setRefreshPolicy(WriteRequest.RefreshPolicy.IMMEDIATE);
        log.debug("写入目标索引:" + index);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(TimeTools.TIME_FMT_2)).setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        for (Object logModel : data) {
            IndexRequest source = new IndexRequest(index);
            if (logModel instanceof Map) {
                Object id = ((Map) logModel).remove("id");
                if (id != null) {
                    source.id(String.valueOf(id));
                }
            }
            source.source(objectMapper.writeValueAsBytes(logModel), Requests.INDEX_CONTENT_TYPE);
            request.add(source);
        }

        BulkResponse responses = client.bulk(request, RequestOptions.DEFAULT);
        if (responses.hasFailures()) {
            String s = responses.buildFailureMessage();
            log.error(s);
            return 0;
        } else {
            log.info("成功入es日志数据量: " + index + " " + data.size());
        }
        //Thread.sleep(1000);
        return data.size();
    }


    public static List<Map<String, Object>> simpleDateTermAndSumAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String dateField, String dateFieldKey, DateHistogramInterval interval, int offset, String dateFormat, String aggField, int aggSize, String sumAggField, String keyFieldRe, String sumFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.offset(offset);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(dateFormat);
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(aggSize);
        agg.order(BucketOrder.aggregation("sumAgg", false));
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumAggField);
        agg.subAggregation(sumAgg);
        queryModel.setAggregationBuilder(dateAgg.subAggregation(agg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> firstDateMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (firstDateMap.containsKey("buckets")) {
                    List<Map<String, Object>> firstaggItems = (List<Map<String, Object>>) firstDateMap.get("buckets");
                    firstaggItems.forEach(fitem -> {
                        if (fitem != null && fitem.containsKey("agg")) {
                            Map<String, Object> bucketsMap = (Map<String, Object>) fitem.get("agg");
                            if (bucketsMap.containsKey("buckets")) {
                                List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                                aggItems.forEach(aggItem -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(keyFieldRe, aggItem.get("key"));
                                    tmp.put(dateFieldKey, fitem.get("key_as_string"));
                                    tmp.put(sumFieldRe, aggItem.get("doc_count"));
                                    if (topHitFields != null) {
                                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                        for (int i = 0; topHitFields != null && i < topHitFields.length; i++) {
                                            tmp.put(CommonTools.underLineToCamel(topHitFields[i]), row.get(topHitFields[i]));
                                        }
                                    }
                                    result.add(tmp);
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> simpleAggWithTopHitAndOrder(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField, String[] rowFields, String[] keys) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        agg.order(BucketOrder.count(false));
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        if (rowFields != null && rowFields.length > 0) {
                            for (int i = 0; i < rowFields.length; i++) {
                                String fd = rowFields[i];
                                String k = keys[i];
                                tmp.put(k, row.get(fd));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> simpleTermAndAvgAgg(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String[] avgFields, String[] avgFieldsRe) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        for (String avgAggField : avgFields) {
            AvgAggregationBuilder avg = AggregationBuilders.avg("avgAgg_" + avgAggField);
            avg.field(avgAggField);
            agg.subAggregation(avg);
        }
        queryModel.setUseAggre(true);
        queryModel.setAggregationBuilder(agg);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(CommonTools.underLineToCamel(aggField), aItem.get("key"));
                        for (int i = 0; i < avgFields.length; i++) {
                            String avgAggField = avgFields[i];
                            tmp.put(avgFieldsRe[i], ((Map<String, Double>) aItem.get("avgAgg_" + avgAggField)).get("value"));
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 时间分桶+去重个数统计
     *
     * @param queryModel
     * @param wrapper
     * @param entry
     * @return
     */
    public static List<Map<String, Object>> cardinalityAndDateAgg(EsQueryModel queryModel, QueryWrapper wrapper, ExchangeDTO entry) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(entry.getDateField());
        dateAgg.calendarInterval(entry.getInterval());
        dateAgg.timeZone(DateTimeZone.forOffsetHours(entry.getOffset()).toTimeZone().toZoneId());
        dateAgg.format(entry.getDateFormat());
        CardinalityAggregationBuilder cardinalityAgg = AggregationBuilders.cardinality("cardinalityAgg").field(entry.getAggField()).precisionThreshold(2000);
        dateAgg.subAggregation(cardinalityAgg);
        queryModel.setAggregationBuilder(dateAgg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("dateAgg")) return result;
        Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("dateAgg");
        if (bucketsMap.containsKey("buckets")) {
            List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
            aggItems.forEach(aggItem -> {
                String date = aggItem.get("key_as_string").toString();
                Map ca = (Map) aggItem.get("cardinalityAgg");
                Map<String, Object> tmp = new HashMap<>();
                tmp.put(entry.getDateFieldKey(), date);
                tmp.put(entry.getValueField(), ca.get("value"));
                result.add(tmp);
            });
        }
        return result;
    }

    public static List<Map<String, Object>> aggAndDate(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, String dateField,String dateFieldKey, int oneAggSize, DateHistogramInterval interval, String dateFormat, int offset,String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(aggField).size(oneAggSize);
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.offset(offset);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(dateFormat);
        queryModel.setAggregationBuilder(dateAgg.subAggregation(oneAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("oneAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("oneAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(CommonTools.underLineToCamel(aggField), item.get("key"));
                                    tmp.put(dateFieldKey, aggItem.get("key_as_string"));
                                    tmp.put(valueField, item.get("doc_count"));
                                    result.add(tmp);
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    public static EsQueryModel fillCondition(EsQueryModel queryModel, ExchangeDTO entry) {
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        if (StringUtils.isNotEmpty(entry.getAggField())) {
            agg.field(entry.getAggField()).size(entry.getAggSize());
        }
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        if (StringUtils.isNotEmpty(entry.getDateField())) {
            dateAgg.field(entry.getDateField());
        }
        dateAgg.calendarInterval(entry.getInterval());
        dateAgg.offset(entry.getOffset());
        dateAgg.timeZone(DateTimeZone.forOffsetHours(entry.getOffset()).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(entry.getDateFormat());
        queryModel.setAggregationBuilder(dateAgg.subAggregation(agg));
        queryModel.setUseAggre(true);
        return queryModel;
    }

    public static List<Map<String, Object>> aggAndDate(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, ExchangeDTO entry) {
        List<Map<String, Object>> result = new ArrayList<>();
        EsQueryModel model = fillCondition(queryModel, entry);
        Map<String, Object> aggMap = wrapper.getAggResponse(model);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap == null || !dataAggMap.containsKey("dateAgg")) return result;
        Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("dateAgg");
        if (!oneAggMap.containsKey("buckets")) return result;
        List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
        oneAggItems.forEach(aggItem -> {
            if (aggItem.containsKey("agg")) {
                Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("agg");
                if (twoAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                    twoAggItems.forEach(item -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(CommonTools.underLineToCamel(entry.getAggField()), item.get("key"));
                        tmp.put(entry.getDateFieldKey(), aggItem.get("key_as_string"));
                        tmp.put(entry.getValueField(), item.get("doc_count"));
                        result.add(tmp);
                    });
                }
            }
        });
        return result;
    }

    public static Calendar setTime(Calendar calendar,int hour,int minute,int second){
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, second);
        return calendar;
    }

    /**
     * 构建非工作时间查询条件
     *
     * @param queryModel 查询参数
     * @param query      查询条件
     * @param interval   时间标识
     */
    public static void buildNonWorkTimeQuery(EsQueryModel queryModel, BoolQueryBuilder query, String interval) {
        BoolQueryBuilder sh = new BoolQueryBuilder();
        Calendar start = Calendar.getInstance();
        start.setTime(queryModel.getStartTime());
        Calendar end = Calendar.getInstance();
        end.setTime(queryModel.getEndTime());
        List<Pair<Object,Object>> datePair = new ArrayList<>();
        boolean sameDay = Objects.equals(start.get(Calendar.DAY_OF_YEAR), end.get(Calendar.DAY_OF_YEAR))
                && Objects.equals(start.get(Calendar.YEAR), end.get(Calendar.YEAR))
                && Objects.equals(start.get(Calendar.MONTH), end.get(Calendar.MONTH));
        if (sameDay) {
            switch (interval) {
                case "1":
                    datePair.add(Pair.of(queryModel.getStartTime().toInstant(), queryModel.getEndTime().toInstant()));
                    break;
                case "2":
                case "3":
                    Pair<Object, Object> pair = Pair.of(setTime(start, 0, 0, 0).getTime(), setTime(start, 9, 0, 0).getTime());
                    Pair<Object, Object> pair2 = Pair.of(setTime(start, 18, 0, 0).getTime(), setTime(start, 23, 59, 59).getTime());
                    datePair.add(pair);
                    datePair.add(pair2);
                    break;
                default:
                    buildDefaultQuery(start, end, datePair);
                    break;
            }
        } else {
            buildDefaultQuery(start, end, datePair);
        }
        for (Pair<Object, Object> pair : datePair) {
            sh.should(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(pair.getFirst()).to(pair.getSecond()));
        }
        query.must(sh);
        queryModel.setQueryBuilder(query);
        queryModel.setUseTimeRange(false);
    }

    public static void buildDefaultQuery(Calendar cal1, Calendar cal2, List<Pair<Object, Object>> datePair) {
        while (cal1.before(cal2)) {
            Date start = setTime(cal1, 18, 0, 0).getTime();
            cal1.add(Calendar.DATE, 1);
            Date end = setTime(cal1, 9, 0, 0).getTime();
            datePair.add(Pair.of(start, end));
        }
    }

    public static List<Map<String, Object>> simpleAggWithMaxMinWithTopHit(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, String maxMinField, int size, String keyField, String valueField, String[] rowFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        MaxAggregationBuilder max = AggregationBuilders.max("max_" + maxMinField).field(maxMinField);
        MinAggregationBuilder min = AggregationBuilders.min("min_" + maxMinField).field(maxMinField);
        agg.subAggregation(max);
        agg.subAggregation(min);
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        Map<String, Object> maxSource = (Map<String, Object>) aggItem.get("max_" + maxMinField);
                        Map<String, Object> minSource = (Map<String, Object>) aggItem.get("min_" + maxMinField);
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        tmp.put("max_" + maxMinField, maxSource.get("value"));
                        tmp.put("min_" + maxMinField, minSource.get("value"));
                        if (rowFields != null) {
                            for (String fd : rowFields) {
                                tmp.put(CommonTools.underLineToCamel(fd), row.get(fd));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    public static Map<String, Number> statsAggregation(EsQueryModel queryModel, QueryWrapper wrapper, String aggField) {
        Map<String, Number> result = new HashMap<>();
        StatsAggregationBuilder aggregation = AggregationBuilders.stats("stats_agg").field(aggField);
        queryModel.setAggregationBuilder(aggregation);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap == null || !aggMap.containsKey("aggregations")) return result;
        Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
        if (dataAggMap != null && dataAggMap.containsKey("stats_agg")) {
            return (Map<String, Number>) dataAggMap.get("stats_agg");
        }
        return result;
    }

    /**
     * 4级聚合
     *
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param threeAggField
     * @param fourAggField
     * @param size
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> fourLevelAggAndTopHit(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, String threeAggField, String fourAggField, int size, String valueField, String[] rowFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(size);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(size);
        TermsAggregationBuilder threeAgg = new TermsAggregationBuilder("threeAgg");
        threeAgg.field(threeAggField).size(size);
        secondAgg.subAggregation(threeAgg);
        TermsAggregationBuilder fourAgg = new TermsAggregationBuilder("fourAgg");
        fourAgg.field(fourAggField).size(size);
        threeAgg.subAggregation(fourAgg);
        fourAgg.subAggregation(AggregationBuilders.topHits("data").size(1));

        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("secondAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(twoitem -> {
                                    if (twoitem.containsKey("threeAgg")) {
                                        Map<String, Object> threeAggMap = (Map<String, Object>) twoitem.get("threeAgg");
                                        if (threeAggMap.containsKey("buckets")) {
                                            List<Map<String, Object>> threeAggItems = (List<Map<String, Object>>) threeAggMap.get("buckets");
                                            threeAggItems.forEach(threeItem -> {
                                                if (threeItem.containsKey("fourAgg")) {
                                                    Map<String, Object> fourAggMap = (Map<String, Object>) threeItem.get("fourAgg");
                                                    if (fourAggMap.containsKey("buckets")) {
                                                        List<Map<String, Object>> fourAggItems = (List<Map<String, Object>>) fourAggMap.get("buckets");
                                                        fourAggItems.forEach(fourItem -> {
                                                            Map<String, Object> tmp = new HashMap<>();
                                                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) fourItem.get("data")).get("hits")).get("hits");
                                                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                                            if (rowFields != null && rowFields.length > 0) {
                                                                for (int i = 0; i < rowFields.length; i++) {
                                                                    String fd = rowFields[i];
                                                                    tmp.put(CommonTools.underLineToCamel(fd), row.get(fd));
                                                                }
                                                            }
                                                            tmp.put(CommonTools.underLineToCamel(oneAggField), aggItem.get("key"));
                                                            tmp.put(CommonTools.underLineToCamel(twoAggField), twoitem.get("key"));
                                                            tmp.put(CommonTools.underLineToCamel(threeAggField), threeItem.get("key"));
                                                            tmp.put(CommonTools.underLineToCamel(fourAggField), fourItem.get("key"));
                                                            tmp.put(valueField, fourItem.get("doc_count"));
                                                            result.add(tmp);
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }

                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    /**
     * 构造es查询model
     *
     * @param wrapper
     * @param model
     * @param index
     * @param time
     * @return
     */
    public static EsQueryModel buildQueryModelMult(QueryTools.QueryWrapper wrapper, PageModel model, String[] index, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        if (StringUtils.isNotEmpty(model.getOrder())) {
            queryModel.setSort(true);
            queryModel.setSortFields(new String[]{model.getOrder()});
            queryModel.setSortOrder(SortOrder.DESC.toString().equalsIgnoreCase(model.getBy()) ? SortOrder.DESC : SortOrder.ASC);
        }
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public static ExchangeDTO buildQueryCondition(BuildQuery buildQuery) {
        String inter = "1h";
        String format = "yyyy-MM-dd HH";
        ExchangeDTO entry = new ExchangeDTO();
        if (buildQuery.isDate()) {
            switch (buildQuery.getInterval()) {
                case "2":
                    inter = "1d";
                    format = "yyyy-MM-dd";
                    break;
                case "3":
                    inter = "1M";
                    format = "yyyy-MM";
                    break;
            }
            entry.setDateField("event_time");
            entry.setDateFieldKey("date");
            entry.setInterval(new DateHistogramInterval(inter));
            entry.setDateFormat(format);
            entry.setOffset(8);
        } else {
            entry.setKeyField(buildQuery.getKeyField());
        }
        entry.setAggSize(buildQuery.getSize() == 0 ? SIZE : buildQuery.getSize());
        entry.setAggField(buildQuery.getAggField());
        entry.setValueField("count");
        return entry;
    }

    public static VList<Map<String, String>> searchResponse(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, ObjectPortraitModel model, boolean scroll) {
        if (scroll) {
            queryModel.setCount(10000);
            SearchResponse searchResponse = null;
            List<Map<String, String>> allDate = new ArrayList<>();
            while (true) {
                searchResponse = wrapper.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId());
                SearchHits hits = searchResponse.getHits();
                List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), "event_time");
                if (hits.getHits() == null || hits.getHits().length == 0) {
                    break;
                }
                allDate.addAll(list);
            }
            List<Map<String, String>> resultSortList = allDate.stream().sorted(Comparator.comparing(e -> e.get("event_time"),
                    Comparator.reverseOrder())).collect(Collectors.toList());
            return new VList<>(resultSortList.size(), resultSortList);
        } else {
            queryModel.setSort(true);
            if (Objects.equals("login_time", model.getOrder())) {
                queryModel.setSortFields(new String[]{"login_time"});
            } else if (Objects.equals("event_time", model.getOrder())) {
                queryModel.setSortFields(new String[]{"event_time"});
            } else {
                queryModel.setSortFields(new String[]{model.getOrder()});
            }
            SortOrder sortOrder = SortOrder.fromString(model.getBy());
            queryModel.setSortOrder(sortOrder);
            SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
            long total = 0;
            if (searchResponse != null) {
                List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
                total = searchResponse.getHits().getTotalHits().value;
                transferFileSize(list, model.getFlow());
                return VoBuilder.vl(total, list);
            }
            return VoBuilder.vl(total, new ArrayList<>());
        }
    }

    public static VList<Map<String, String>> searchResponse(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, ObjectPortraitModel model) {
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        long total = 0;
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            total = searchResponse.getHits().getTotalHits().value;
            transferFileSize(list, model.getFlow());
            return VoBuilder.vl(total, list);
        }
        return VoBuilder.vl(total, new ArrayList<>());
    }

    public static void transferFileSize(List<Map<String, String>> list, String flow) {
        if (IterUtil.isEmpty(list)) return;
        list.forEach(element -> {
            switch (flow) {
                // 流量大小
                case "1":
                    double totalByte = Double.parseDouble(element.get("total_byte"));
                    String totalBytePretty = PrettyMemoryUtil.prettyByteSize((long) totalByte);
                    element.put("total_byte", totalBytePretty);
                    break;
                // 总字节数
                case "2":
                    double clientTotalByte = Double.parseDouble(element.get("client_total_byte"));
                    double serverTotalByte = Double.parseDouble(element.get("server_total_byte"));
                    element.put("server_total_byte", PrettyMemoryUtil.prettyByteSize((long) serverTotalByte));
                    element.put("client_total_byte", PrettyMemoryUtil.prettyByteSize((long) clientTotalByte));
                    break;
                default:
                    if (element.containsKey("file_size")) {
                        String fileSize = PrettyMemoryUtil.prettyByteSize(Long.parseLong(element.get("file_size")));
                        element.put("file_size", fileSize);
                    }
                    break;
            }
        });
    }

    public static void buildFileSizeQuery(String fileSize, BoolQueryBuilder query) {
        FileSizeEnum size = FileSizeEnum.toFileSize(fileSize);
        query.must(QueryBuilders.rangeQuery("file_size").gt(size.getFrom()).lt(size.getTo()));
    }

    public static void buildIpRangeQuery(String[] rangeIpList, BoolQueryBuilder query) {
        BoolQueryBuilder sd = new BoolQueryBuilder();
        for (String s : rangeIpList) {
            String[] ipRange = s.split("-");
            long beginIp = transferIpNumber(ipRange[0]);
            long endIp = transferIpNumber(ipRange[1]);
            sd.should(QueryBuilders.rangeQuery("sip_num").gt(beginIp).lt(endIp));
        }
        query.must(sd);
    }

    public static void buildIpRangeQuery2(String[] rangeIpList, BoolQueryBuilder query) {
        BoolQueryBuilder sd = new BoolQueryBuilder();
        for (String rangeIps : rangeIpList) {
            if (StrUtil.isEmpty(rangeIps)) continue;
            if (rangeIps.contains(",")) {
                String[] split = rangeIps.split(",");
                for (String s : split) {
                    String[] ipRange = s.split("-");
                    long beginIp = QueryTools.transferIpNumber(ipRange[0]);
                    long endIp = QueryTools.transferIpNumber(ipRange[1]);
                    sd.should(QueryBuilders.rangeQuery("sip_num").gte(beginIp).lte(endIp));
                }
            } else {
                String[] ipRange = rangeIps.split("-");
                long beginIp = QueryTools.transferIpNumber(ipRange[0]);
                long endIp = QueryTools.transferIpNumber(ipRange[1]);
                sd.should(QueryBuilders.rangeQuery("sip_num").gte(beginIp).lte(endIp));
            }
        }
        query.must(sd);
    }

    public static long transferIpNumber(String ipAddress) {
        long ipNumber = 0;
        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            byte[] bytes = inetAddress.getAddress();
            for (byte b : bytes) {
                ipNumber <<= 8;
                ipNumber |= b & 0xFF;
            }
            return ipNumber;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return ipNumber;
    }

    public static Pair<EsQueryModel, QueryWrapper> buildQuery(ObjectPortraitModel model, BuildQuery buildQuery) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel;
        if (buildQuery.isMultipleIndex()) {
            queryModel = QueryTools.buildQueryModelMult(wrapper, model, buildQuery.getIndexes(), "event_time");
        } else {
            queryModel = QueryTools.buildQueryModel(wrapper, model, buildQuery.getIndex(), "event_time");
        }
        BoolQueryBuilder query = new BoolQueryBuilder();
        if (buildQuery.isDiagram() && StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("sip", model.getDevIp()));
            query.mustNot(QueryBuilders.termQuery("dip", model.getDevIp()));
            // 应用系统访问
        } else if (StringUtils.isNotEmpty(model.getAppNo())) {
            query.must(QueryBuilders.termsQuery("src_std_sys_id", model.getAppNo()));
        } else if (StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("sip", model.getDevIp()));
        } else if (StringUtils.isNotEmpty(model.getRangIp())) {
            String rangIp = model.getRangIp();
            if (rangIp.contains(",")) {
                String[] rangIpList = rangIp.split(",");
                buildIpRangeQuery(rangIpList, query);
            } else {
                buildIpRangeQuery(new String[]{rangIp}, query);
            }
        } else if (IterUtil.isNotEmpty(model.getIpRangeList())) {
            List<CommunicationModel> ipRangeList = model.getIpRangeList();
            String[] list = ipRangeList.stream().map(CommunicationModel::getRangeIps).toArray(String[]::new);
            buildIpRangeQuery2(list, query);
        }
        if (model.isSecret()) {
            BoolQueryBuilder sd = new BoolQueryBuilder();
            sd.should(QueryBuilders.termsQuery("classification_level", "机密", "绝密", "秘密"));
            query.must(sd);
        }
        if (StringUtils.isNotEmpty(model.getDevTypeGroup())) {
            query.must(QueryBuilders.termQuery("src_std_dev_type_group", model.getDevTypeGroup()));
        }
        if (StringUtils.isNotEmpty(model.getDstStdDevTypeGroup())) {
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDstStdDevTypeGroup()));
        }
        if (StringUtils.isNotEmpty(model.getDstStdSysName())) {
            query.must(QueryBuilders.termQuery("dst_std_sys_name", model.getDstStdSysName()));
        }
        if (StringUtils.isNotEmpty(model.getDip())) {
            query.must(QueryBuilders.termQuery("dip", model.getDip()));
        }
        if(StringUtils.isNotEmpty(model.getFileInfo())){
            BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
            queryBuilder.should(QueryBuilders.wildcardQuery("file_name", "*" + model.getFileInfo() + "*"));
            queryBuilder.should(QueryBuilders.wildcardQuery("file_md5", "*" + model.getFileInfo() + "*"));
            query.must(queryBuilder);
        }
        if (StringUtils.isNotEmpty(model.getFileLevel())) {
            query.must(QueryBuilders.termQuery("classification_level_code", model.getFileLevel()));
        }
        if (StringUtils.isNotEmpty(model.getFileType())) {
            query.must(QueryBuilders.termQuery("file_type", model.getFileType()));
        }
        if (StringUtils.isNotEmpty(model.getFileDir())) {
            query.must(QueryBuilders.termQuery("file_dir", model.getFileDir()));
        }
        if(StringUtils.isNotEmpty(model.getSport())){
            query.must(QueryBuilders.termQuery("sport", model.getSport()));
        }
        if (StringUtils.isNotEmpty(model.getDport())) {
            query.must(QueryBuilders.termQuery("dport", model.getDport()));
        }
        if (StringUtils.isNotEmpty(model.getProtocol())) {
            query.must(QueryBuilders.termQuery("app_protocol", model.getProtocol()));
        }
        if (StringUtils.isNotEmpty(model.getUrl())) {
            query.must(QueryBuilders.termQuery("url", model.getUrl()));
        }
        if (StringUtils.isNotEmpty(model.getFileSize())) {
            buildFileSizeQuery(model.getFileSize(), query);
        }
        queryModel.setQueryBuilder(query);
        return Pair.of(queryModel, wrapper);
    }

}
