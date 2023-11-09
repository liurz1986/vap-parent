package com.vrv.vap.amonitor.tools;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.vrv.vap.amonitor.config.IndexConfig;
import com.vrv.vap.amonitor.config.MultiClusterConfig;
import com.vrv.vap.amonitor.VapMonitorApplication;
import com.vrv.vap.amonitor.es.client.ElasticSearchManager;
import com.vrv.vap.amonitor.es.client.EsClient;
import com.vrv.vap.amonitor.es.common.client.SearchRequest5;
import com.vrv.vap.amonitor.es.compatible.RequestConverters5;
import com.vrv.vap.amonitor.es.compatible.SearchSourceBuilder5;
import com.vrv.vap.amonitor.es.compatible.TermsAggregationBuilder5;
import com.vrv.vap.amonitor.es.compatible.VersionFit5To7;
import com.vrv.vap.amonitor.es.init.IndexCache;
import com.vrv.vap.amonitor.model.PageModel;
import com.vrv.vap.amonitor.model.es.QueryModel;
import com.vrv.vap.toolkit.constant.Common;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.EsResult;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.action.support.WriteRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.ResponseException;
import org.elasticsearch.client.ResponseListener;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.CheckedFunction;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.DeprecationHandler;
import org.elasticsearch.common.xcontent.NamedXContentRegistry;
import org.elasticsearch.common.xcontent.XContentParser;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.metrics.cardinality.CardinalityAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.sum.SumAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.DocValueFieldsContext;
import org.elasticsearch.search.sort.SortBuilders;
import org.joda.time.DateTimeZone;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

/**
 * es查询工具
 *
 * @author xw
 * @date 2015年10月20日
 */
public final class QueryTools {
    private static Log log = LogFactory.getLog(QueryTools.class);

    public static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * es scroll 查询 缓存时间
     */
    public static final long ES_CACHE_TIME = 600000;

    public static final String INDEX = "${index}";
    public static final String TIME = "${time}";
    public static final String TYPE = EsClient.getVersion().equals("7") ? "_doc" : "logs";

    /**
     * 多集群情况下，索引名格式为：集群名:索引名
     */
    private static final String CLUSTER_INDEX_CONNECTOR = ":";

    /**
     * 索引时间格式正则
     */
    private static final String INDEX_DATE_REG = "-\\d{4}\\.\\d{2}\\.\\d{2}$|-\\d{4}-\\d{2}-\\d{2}$|-\\d{4}/\\d{2}/\\d{2}$|-\\d{4}\\.\\d{2}$|-\\d{4}-\\d{2}$|-\\d{4}/\\d{2}$";

    private static MultiClusterConfig multiClusterConfig = VapMonitorApplication.getApplicationContext().getBean(MultiClusterConfig.class);

    public static QueryWrapper build(ElasticSearchManager client, IndexConfig indexConfig) {
        return new QueryWrapper(ElasticSearchManager.getClient(), indexConfig);
    }

    public static QueryWrapper build(ElasticSearchManager client) {
        return build(client, VapMonitorApplication.getApplicationContext().getBean(IndexConfig.class));
    }

    public static QueryWrapper build(IndexConfig indexConfig) {
        return build(VapMonitorApplication.getApplicationContext().getBean(ElasticSearchManager.class), indexConfig);
    }

    public static QueryWrapper build() {
        return build(VapMonitorApplication.getApplicationContext().getBean(ElasticSearchManager.class),
                VapMonitorApplication.getApplicationContext().getBean(IndexConfig.class));
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
        public EsResult wrapResult(SearchResponse response, QueryModel queryModel) {
            EsResult esResult = new EsResult();

            List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>(10);
            if (null == response) {
                esResult.setTotal(0);
                return esResult;
            }

            if (null != response.getHits()) {
                log.debug("total:" + response.getHits().getTotalHits());
                // 超过最大返回值,重新设值
                if (queryModel.isOverFlow() || response.getHits().getTotalHits() > indexConfig.getResultTotal()) {
                    esResult.setTotal(indexConfig.getResultTotal());
                } else {
                    esResult.setTotal(response.getHits().getTotalHits());
                }
                esResult.setTotalAcc(response.getHits().getTotalHits());
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
        public List<Map<String, String>> wrapResultAsList(SearchResponse response, QueryModel queryModel) {
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
                tmpData.remove("_index");
                tmpData.remove("_id");
                datas.add(tmpData);
            }
            return datas;
        }

        /**
         * 结果封装为map (从)
         *
         * @param hits
         * @param timeField
         * @param camel     是否转驼峰
         * @return
         */
        public List<Map<String, String>> wrapResponse(SearchHits hits, String timeField, boolean camel) {
            List<Map<String, String>> datas = new ArrayList<Map<String, String>>(10);
            for (SearchHit hit : hits) {
                Map<String, String> tmpData = new HashMap<String, String>();
                tmpData.put("_index", hit.getIndex());
                tmpData.put("_id", hit.getId());
                for (Entry<String, Object> tmp : hit.getSourceAsMap().entrySet()) {
                    tmpData.put(camel ? CommonTools.underLineToCamel(tmp.getKey()) : tmp.getKey(), null != tmp.getValue() ? tmp.getValue().toString() : "");
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
        private <T> void utc2gmt(Map<String, T> tmpData, String timeField) {
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
        public AcknowledgedResponse getDeleteResponse(QueryModel queryModel) {
            if (StringUtils.isEmpty(queryModel.getIndexName())) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return null;
            }
            DeleteIndexRequest request = new DeleteIndexRequest(queryModel.getIndexName());
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }
            log.info("***** request ******\n" + request + "***********\n");
            AcknowledgedResponse response = null;
            try {
                response = client.indices().delete(request, RequestOptions.DEFAULT);
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
        public SearchResponse getSearchResponse(QueryModel queryModel) {
            checkPermissionFilter(queryModel);
            checkSizeAndReset(queryModel);
            if ((null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length)
                    && (StringUtils.isEmpty(queryModel.getIndexName()))) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return null;
            }
            SearchRequest5 request = buildQuery5(queryModel);
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }
            log.info("***** request ******\n" + request + "***********\n");
            SearchResponse response = null;
            try {
                //response = client.search(request);
                Response resp = client.getLowLevelClient().performRequest(RequestConverters5.search(request, "_search"));
                response = parseEntity(resp.getEntity(), SearchResponse::fromXContent, client);
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
         * es Scroll 查询
         *
         * @param queryModel
         * @param scrollId
         * @return
         */
        public SearchResponse scrollQuery(QueryModel queryModel, String scrollId) {
            checkPermissionFilter(queryModel);
            // 缓存时间 分页查询必须在该缓存时间之内进行
            TimeValue keepAlive = new TimeValue(ES_CACHE_TIME);
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest();
            SearchResponse response = null;
            if (scrollId != null) {
                searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(keepAlive);
                try {
                    response = client.searchScroll(searchScrollRequest);
                } catch (IOException e) {
                    log.error("", e);
                }
            } else {
                SearchRequest5 request = buildQuery5(queryModel);
                request.scroll(keepAlive);
                if (log.isDebugEnabled()) {
                    log.debug("***********\n" + request + "***********\n");
                }
                try {
                    //response = client.search(request);
                    Response resp = client.getLowLevelClient().performRequest(RequestConverters5.search(request, "_search"));
                    response = parseEntity(resp.getEntity(), SearchResponse::fromXContent, client);
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

        protected final SearchResponse parseEntity(final HttpEntity entity,
                                                   final CheckedFunction<XContentParser, org.elasticsearch.action.search.SearchResponse, IOException> entityParser, RestHighLevelClient client) throws IOException {
            if (entity == null) {
                throw new IllegalStateException("Response body expected but not returned");
            }
            if (entity.getContentType() == null) {
                throw new IllegalStateException("Elasticsearch didn't return the [Content-Type] header, unable to parse response body");
            }
            XContentType xContentType = XContentType.fromMediaTypeOrFormat(entity.getContentType().getValue());
            if (xContentType == null) {
                throw new IllegalStateException("Unsupported Content-Type: " + entity.getContentType().getValue());
            }

            NamedXContentRegistry registry = getNamedXContentRegistry(client);
            byte[] resBytes = EntityUtils.toByteArray(entity);
            try (XContentParser parser = xContentType.xContent().createParser(registry, DEPRECATION_HANDLER, resBytes)) {
                SearchResponse resp = entityParser.apply(parser);
                try (XContentParser parser2 = xContentType.xContent().createParser(registry, DEPRECATION_HANDLER, resBytes)) {
                    //兼容处理total (XContentParser只能使用一次, 所以再创建一个)
                    resp.getHits().totalHits = QueryTools.getCompatibleTotal(parser2.map());
                }
                return resp;
            }
        }

        private NamedXContentRegistry getNamedXContentRegistry(RestHighLevelClient client) {
            try {
                Field registryField = client.getClass().getDeclaredField("registry");
                registryField.setAccessible(true);
                return (NamedXContentRegistry) registryField.get(client);
            } catch (NoSuchFieldException e) {
            } catch (IllegalAccessException e) {
            }
            return null;
        }

        private static final DeprecationHandler DEPRECATION_HANDLER = new DeprecationHandler() {
            @Override
            public void usedDeprecatedName(String usedName, String modernName) {
            }

            @Override
            public void usedDeprecatedField(String usedName, String replacedWith) {
            }

            @Override
            public void deprecated(String message, Object... params) {
            }
        };

        private void fillFields(SearchResponse response, List<DocValueFieldsContext.FieldAndFormat> fieldAndFormats) {
            HashMap<String, DocumentField> fields = new HashMap<>(fieldAndFormats.size());
            for (DocValueFieldsContext.FieldAndFormat fieldAndFormat : fieldAndFormats) {
                String field = fieldAndFormat.field;
                fields.put(field, new DocumentField(field, new ArrayList<>(2)));

            }
            for (SearchHit hit : response.getHits()) {
                hit.fields(fields);
            }
        }

        /**
         * es Scroll 查询（不校验权限）
         *
         * @param queryModel
         * @param scrollId
         * @return
         */
        public SearchResponse scrollQueryNoPermission(QueryModel queryModel, String scrollId) {
            // 缓存时间 分页查询必须在该缓存时间之内进行
            TimeValue keepAlive = new TimeValue(ES_CACHE_TIME);
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest();
            SearchResponse response = null;
            if (scrollId != null) {
                searchScrollRequest = new SearchScrollRequest(scrollId);
                searchScrollRequest.scroll(keepAlive);
                try {
                    response = client.searchScroll(searchScrollRequest);
                } catch (IOException e) {
                    log.error("", e);
                }
            } else {
                SearchRequest5 request = buildQuery5(queryModel);
                request.scroll(keepAlive);
                if (log.isDebugEnabled()) {
                    log.debug("***********\n" + request + "***********\n");
                }
                try {
                    Response resp = client.getLowLevelClient().performRequest(RequestConverters5.search(request, "_search"));
                    response = parseEntity(resp.getEntity(), SearchResponse::fromXContent, client);
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

        public Map<String, Object> getAggResponse(QueryModel queryModel) {
            checkPermissionFilter(queryModel);
            checkSizeAndReset(queryModel);
            if ((null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length)
                    && (org.apache.commons.lang.StringUtils.isEmpty(queryModel.getIndexName()))) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return null;
            }
            SearchRequest5 request = buildQuery5(queryModel);
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }
            log.info("***********\n" + request + "***********\n");
            Response response = null;
            try {
                String queryJsonStr = VersionFit5To7.removeUnSupportKey(request.source().toString());
                response = search(request.indices(), queryJsonStr);
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

            Map<String, Object> aggMap = new HashMap<>();
            if (response != null) {
                try {
                    String responseStr = EntityUtils.toString(response.getEntity(), "utf-8");
//					log.info("*****  responseStr  ******\n" + responseStr + "***********\n");
                    //ObjectMapper mapper = new ObjectMapper();
                    aggMap = objectMapper.readValue(responseStr, Map.class);
                } catch (IOException e) {
                    log.error("", e);
                }
            }

            return aggMap;
        }

        public void deleteByQueryAsync(QueryModel queryModel) {
            checkPermissionFilter(queryModel);
            queryModel.setStart(-1);
            queryModel.setCount(10000000);
            if ((null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length)
                    && (org.apache.commons.lang.StringUtils.isEmpty(queryModel.getIndexName()))) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return;
            }
            queryModel.getQueryBuilder().toString();
            SearchRequest5 request = buildQuery5(queryModel);
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }
            log.info("***********删除数据\n" + request + "***********\n");
            try {
                String queryJsonStr = VersionFit5To7.removeUnSupportKey(request.source().toString());
                deleteByQuery(request.indices(), queryJsonStr);
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

        /**
         * 检查是否需要过滤数据权限
         *
         * @param queryModel
         */
        private void checkPermissionFilter(QueryModel queryModel) {
            return;
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
                Response response = ElasticSearchManager.getClient().getLowLevelClient().performRequest(method, endpoint, Collections.emptyMap(), entity);
                return response;
            } catch (IOException e) {
                log.error("", e);
            }

            return null;

        }

        public void deleteByQuery(String[] indexList, String queryJsonStr) {
            String indexStr = org.apache.commons.lang.StringUtils.join(indexList, ",");
            String method = "POST";
            String endpoint = "/" + indexStr + "/_delete_by_query?scroll_size=5000";
            HttpEntity entity = new NStringEntity(queryJsonStr, ContentType.APPLICATION_JSON);
            try {
                ResponseListener responseListener = new ResponseListener() {
                    @Override
                    public void onSuccess(Response response) {
                        log.info(response);
                    }

                    @Override
                    public void onFailure(Exception exception) {
                        log.error("", exception);
                    }
                };
                ElasticSearchManager.getClient().getLowLevelClient().performRequestAsync(method, endpoint, Collections.emptyMap(), entity, responseListener);
            } catch (Exception e) {
                log.error("", e);
            }
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
                Response response = ElasticSearchManager.getClient().getLowLevelClient().performRequest(method, endpoint, Collections.emptyMap(), entity);
                return response;
            } catch (IOException e) {
                log.error("", e);
            }
            return null;

        }

        private SearchRequest buildQuery(QueryModel queryModel) {
            SearchRequest request = null;
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            if (queryModel.getStart() > -1) {
                searchSourceBuilder.from(queryModel.getStart()).size(queryModel.getCount());
            }
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
                queryModel.getMulAggregationBuilders().forEach(r -> {
                    searchSourceBuilder.aggregation(r);
                });
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
                if (null != queryModel.getResultFields() && 0 != queryModel.getResultFields().length) {
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

        private SearchRequest5 buildQuery5(QueryModel queryModel) {
            SearchRequest5 request = null;
            SearchSourceBuilder5 searchSourceBuilder = new SearchSourceBuilder5();
            if (queryModel.getStart() > -1) {
                searchSourceBuilder.from(queryModel.getStart()).size(queryModel.getCount());
            }
            if (null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length) {
                request = new SearchRequest5(indexRoute(queryModel.getIndexName()));
            } else {
                request = new SearchRequest5(indexRoute(queryModel.getIndexNames()));
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
                queryModel.getMulAggregationBuilders().forEach(r -> {
                    searchSourceBuilder.aggregation(r);
                });
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
                if (null != queryModel.getResultFields() && 0 != queryModel.getResultFields().length) {
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
        public List<String> getIndexNames(QueryModel queryModel) {
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
            if (IndexCache.indexInExistCache(indexName)) {
                return true;
            }
            if (IndexCache.indexInNotExistCache(indexName)) {
                boolean isOk;
                if (a == 1) {
                    isOk = doubleCheck(indexName, timeFormat);
                } else {
                    isOk = doubleCheckMonth(indexName, timeFormat);
                }
                if (isOk) {
                    IndexCache.modifyCache(indexName, null);
                    //IndexCache.getIndexNamesNotExist().remove(indexName);
                }
                return isOk;
            }
            boolean exist = isExistIndex(indexName);
            if (exist) {
                IndexCache.modifyCache(indexName, null);
                //IndexCache.getIndexNamesExist().add(indexName);
            } else {
                IndexCache.modifyCache(null, indexName);
                //IndexCache.getIndexNamesNotExist().add(indexName);
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
            if (IndexCache.indexInExistCache(indexName)) {
                return true;
            }
            if (IndexCache.indexInNotExistCache(indexName)) {
                boolean isOk;
                if (a == 1) {
                    isOk = doubleCheck(indexName);
                } else {
//                    isOk = doubleCheckMonth(indexName);
                    isOk = isExistIndex(indexName);
                }
                if (isOk) {
                    IndexCache.modifyCache(indexName, null);
                    //IndexCache.getIndexNamesNotExist().remove(indexName);
                }
                return isOk;
            }
            boolean exist = isExistIndex(indexName);
            if (exist) {
                IndexCache.modifyCache(indexName, null);
                //IndexCache.getIndexNamesExist().add(indexName);
            } else {
                IndexCache.modifyCache(null, indexName);
                //IndexCache.getIndexNamesNotExist().add(indexName);
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
            // 任务详情类索引直接校验
            if (indexName.startsWith("task-detail-")) {
                return isExistIndex(indexName);
            }
            try {
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
            } catch (Exception e) {
                return isExistIndex(indexName);
            }
        }

        /**
         * 对当日的索引进行二次校验,防止后续生成后导致判断错误
         *
         * @param indexName
         * @return
         */
        private boolean doubleCheck(String indexName, String timeForamt) {
            // 任务详情类索引直接校验
            if (indexName.startsWith("task-detail-")) {
                return isExistIndex(indexName);
            }
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
            // 任务详情类索引直接校验
            if (indexName.startsWith("task-detail-")) {
                return isExistIndex(indexName);
            }
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
            // 任务详情类索引直接校验
            if (indexName.startsWith("task-detail-")) {
                return isExistIndex(indexName);
            }
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
        public void checkSizeAndReset(QueryModel queryModel) {
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
                // 多集群情况下
                if (multiClusterConfig.isOpen()) {
                    response = restClient.performRequest(Common.GET_METHOD, indexRoute(index) + "/_count");
                } else {
                    response = restClient.performRequest(Common.GET_METHOD, indexRoute(index));
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
            String time = TimeTools.format(date, indexConfig.getTimeForamt());
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
        public QueryModel buildQueryModel(String index, String[] resultFields) {
            QueryModel queryModel = new QueryModel();

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
        public void addTimeRange(QueryModel queryModel, BoolQueryBuilder queryBuilder) {
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
        public void setTimeRangeFilter(QueryModel queryModel) {
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
        public void setTimeRangeFilter(QueryModel queryModel, String timeFormat) {
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
                return Optional.of(client.getLowLevelClient().performRequest(method, endpoint, headers));
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
     * 兼容es7的total
     *
     * @param aggMap
     * @return
     */
    public static Long getCompatibleTotal(Map<String, Object> aggMap) {
        long todayTotal = 0L;
        Map<String, Object> hitsMap = (Map<String, Object>) aggMap.get("hits");
        if (hitsMap != null && hitsMap.containsKey("total")) {
            Object total = hitsMap.get("total");
            if (total instanceof Map) {
                todayTotal = Long.parseLong(((Map) total).get("value").toString());
            } else {
                todayTotal = Long.parseLong(total.toString());
            }
        }
        return todayTotal;
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
    public static QueryModel buildQueryModel(QueryWrapper wrapper, PageModel model, String indexName, String[] indexNames, String typeName, String timeField, boolean needTimeFormat, String timeFormat) {
        QueryModel queryModel = new QueryModel();
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

    /**
     * 构造es查询model
     *
     * @param wrapper
     * @param model
     * @param index
     * @param time
     * @return
     */
    public static QueryModel buildQueryModel(QueryWrapper wrapper, PageModel model, String index, String time) {
        QueryModel queryModel = new QueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }

        queryModel.setIndexName(index);

        /*List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }*/
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setNeedTimeFormat(true);
        queryModel.setTimeFormat(TimeTools.TIME_FMT_2);
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
    public static QueryModel buildQueryModel(QueryWrapper wrapper, PageModel model, String[] indexs, String time) {
        QueryModel queryModel = new QueryModel();
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
    public static QueryModel buildQueryModel(QueryWrapper wrapper, PageModel model, String index) {
        QueryModel queryModel = new QueryModel();
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
    public static QueryModel buildQueryModel(QueryWrapper wrapper, PageModel model, String index, String time, String timeFormat) {
        QueryModel queryModel = new QueryModel();
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
        queryModel.setNeedTimeFormat(true);
        queryModel.setTimeFormat(timeFormat);
//        wrapper.setTimeRangeFilter(queryModel, timeFormat);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public static void setTimeCondition(QueryModel queryModel, String timeField) {
        BoolQueryBuilder query = (BoolQueryBuilder) queryModel.getQueryBuilder();
        query.filter(QueryBuilders.rangeQuery(timeField != null ? timeField : queryModel.getTimeField()).from(queryModel.getStartTime()).to(queryModel.getEndTime()));
    }

    public static void setQueryCondition(String split, BoolQueryBuilder query, String key, String values, boolean wildcard) {
        if (StringUtils.isEmpty(values)) {
            return;
        }

        if (values.indexOf(split) > 0) {
            query.must(buildMultiShouldQuery(split, key, values, wildcard));
        } else {
            query.must(wildcard ? QueryBuilders.wildcardQuery(key, "*" + values + "*") : QueryBuilders.termQuery(key, values));
        }
    }

    public static void setQueryConditionRight(String split, BoolQueryBuilder query, String key, String values, boolean wildcardRight) {
        if (StringUtils.isEmpty(values)) {
            return;
        }

        if (values.indexOf(split) > 0) {
            query.must(buildMultiShouldQueryRight(split, key, values, wildcardRight));
        } else {
            query.must(wildcardRight ? QueryBuilders.wildcardQuery(key, values + "*") : QueryBuilders.termQuery(key, values));
        }
    }


    public static void setQueryNotCondition(String split, BoolQueryBuilder query, String key, String values, boolean wildcard) {
        if (StringUtils.isEmpty(values)) {
            return;
        }
        if (values.indexOf(split) > 0) {
            query.mustNot(buildMultiShouldQuery(split, key, values, wildcard));
        } else {
            query.mustNot(wildcard ? QueryBuilders.wildcardQuery(key, "*" + values + "*") : QueryBuilders.termQuery(key, values));
        }
    }

    public static void buildShouldAuditQuery(String split, BoolQueryBuilder query, String key, String values, boolean wildcard) {
        if (StringUtils.isEmpty(values)) {
            return;
        }
        if (values.indexOf(split) > 0) {
            query.should(buildMultiShouldQuery(split, key, values, wildcard));
        } else {
            query.should(wildcard ? QueryBuilders.wildcardQuery(key, "*" + values + "*") : QueryBuilders.termQuery(key, values));
        }
    }

    private static BoolQueryBuilder buildMultiShouldQuery(String split, String key, String values, boolean wildcard) {
        BoolQueryBuilder shouldQuery = new BoolQueryBuilder();
        shouldQuery.minimumShouldMatch(1);
        for (String v : values.split(split)) {
            shouldQuery.should(wildcard ? QueryBuilders.wildcardQuery(key, "*" + v + "*") : QueryBuilders.termQuery(key, v));
        }
        return shouldQuery;
    }

    private static BoolQueryBuilder buildMultiShouldQueryRight(String split, String key, String values, boolean wildcard) {
        BoolQueryBuilder shouldQuery = new BoolQueryBuilder();
        shouldQuery.minimumShouldMatch(1);
        for (String v : values.split(split)) {
            shouldQuery.should(wildcard ? QueryBuilders.wildcardQuery(key, v + "*") : QueryBuilders.termQuery(key, v));
        }
        return shouldQuery;
    }

    /**
     * 取总数
     */
    public static int getHits(QueryModel queryModel, QueryWrapper wrapper) {
        int result = 0;
        Map<String, Object> responseMap = wrapper.getAggResponse(queryModel);
        if (responseMap != null) {
            Map<String, Object> hitsMap = (Map<String, Object>) responseMap.get("hits");
            result = getCompatibleTotal(responseMap).intValue();
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
    public static List<Map<String, Object>> simpleAgg(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String keyFieldRe, String valueFieldRe) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
        agg.field(aggField).size(size);
        agg.setAggOrder(BucketOrder.count(false));
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
    public static List<Map<String, Object>> simpleAgg(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, int minDocSize, String keyFieldRe, String valueFieldRe) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
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
    public static List<Map<String, Object>> simpleAggAndTopHit(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String keyField, String valueField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
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
    public static List<Map<String, Object>> simpleTermAndSumAgg(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String sumAggField, String keyFieldRe, String sumFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
        agg.field(aggField).size(size);
        if (topHitFields != null) {
            agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        }
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumAggField);
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
                        tmp.put(sumFieldRe, ((Map<String, Double>) aggItem.get("sumAgg")).get("value"));

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
     * 简单sum聚合
     *
     * @param queryModel
     * @param wrapper
     * @param sumAggFields
     * @param sumFieldsRe
     * @return
     */
    public static Map<String, Object> simpleSumAgg(QueryModel queryModel, QueryWrapper wrapper, String[] sumAggFields, String[] sumFieldsRe) {
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
    public static List<Map<String, Object>> simpleTermAndSumAgg(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String[] sumAggFields, String[] sumFieldsRe, String keyFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
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
    public static List<Map<String, Object>> simpleTermAndSumCarAgg(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String cardinalityAggField, String[] sumAggFields, String[] sumFieldsRe, String keyFieldRe, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
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
    public static List<Map<String, Object>> simpleCardinalityAgg2(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String cardinalityAggField1, String cardinalityAggField2, String keyField, String cardinalityField1, String cardinalityField2, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
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
    public static List<Map<String, Object>> simpleCardinalityAgg(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String cardinalityAggField, String keyField, String cardinalityField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
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
    public static List<Map<String, Object>> simpleAggWithTopHit(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
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
    public static List<Map<String, Object>> twoLevelAgg(QueryModel queryModel, QueryWrapper wrapper, String oneAggField, String twoAggField, int oneAggSize, int secondAggSize, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 oneAgg = new TermsAggregationBuilder5("oneAgg", null);
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder5 secondAgg = new TermsAggregationBuilder5("secondAgg", null);
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

    public static List<Map<String, Object>> cardinalityAndDateAgg(QueryModel queryModel, QueryWrapper wrapper, String dateAggField, DateHistogramInterval interval, String dateFormat, int offset, String cardinalityAggField, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset));
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
    public static List<Map<String, Object>> dateAgg(QueryModel queryModel, QueryWrapper wrapper, String dateAggField, DateHistogramInterval interval, String dateFormat, int offset, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.offset(offset);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset));
        dateAgg.minDocCount(0);
        dateAgg.format(dateFormat);
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
                        tmp.put(keyField, date);
                        tmp.put(valueField, aggItem.get("doc_count"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    /**
     * 简单时间分桶加和统计
     *
     * @param queryModel
     * @param wrapper
     * @param dateAggField
     * @param sumAggField
     * @param interval
     * @param dateFormat
     * @param offset
     * @param keyField
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> dateAndSumAgg(QueryModel queryModel, QueryWrapper wrapper, String dateAggField, String sumAggField, DateHistogramInterval interval, String dateFormat, int offset, String keyField, String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateAggField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset));
        dateAgg.minDocCount(0);
        dateAgg.format(dateFormat);

        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumAggField);
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
                        tmp.put(keyField, date);
                        tmp.put(valueField, aggItem.get("doc_count"));
                        tmp.put(sumAggField, ((Map<String, Double>) aggItem.get("sumAgg")).get("value"));
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
    public static Map<String, Object> simpleCardinalityAgg(QueryModel queryModel, QueryWrapper wrapper, String cardinalityAggField, int threshold, String valueField) {
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
    public static int simpleCardinalityAggAndReturnLong(QueryModel queryModel, QueryWrapper wrapper, String cardinalityAggField, int threshold) {
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
    public static List<Map<String, Object>> simpleAggWithCardinality(QueryModel queryModel, QueryWrapper wrapper, String aggField, int size, String countField, String keyField, String valueField, String[] topHitFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder5 agg = new TermsAggregationBuilder5("agg", null);
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
    public static QueryModel buildQueryModelByDay(QueryWrapper wrapper, PageModel model, String index, String time) {
        QueryModel queryModel = new QueryModel();
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
     * es按天查询索引model
     *
     * @param wrapper
     * @param model
     * @param index
     * @param time
     * @return
     */
    public static QueryModel buildQueryModelAll(QueryWrapper wrapper, PageModel model, String index, String time) {
        QueryModel queryModel = new QueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        queryModel.setIndexName(index + "*");
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
    public static long getCount(QueryModel model, QueryWrapper wrapper) {
        long result = 0;
        SearchResponse response = wrapper.getSearchResponse(model);
        if (response != null) {
            result = response.getHits().getTotalHits();
        }
        return result;
    }

    /**
     * 写入数据
     */
    public static long writeData(List<?> data, String index, String type, QueryWrapper wrapper, WriteRequest.RefreshPolicy refreshPolicy) throws Exception {
        if (data == null || data.isEmpty()) {
            return -1;
        }
        RestHighLevelClient client = wrapper.client;
        BulkRequest request = new BulkRequest();
        request.setRefreshPolicy(refreshPolicy == null ? WriteRequest.RefreshPolicy.IMMEDIATE : refreshPolicy);
        log.debug("写入目标索引:" + index);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(TimeTools.TIME_FMT_2)).setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        for (Object logModel : data) {
            IndexRequest source = new IndexRequest(index, type);
            if (logModel instanceof Map) {
                Object id = ((Map) logModel).remove("id");
                if (id != null) {
                    source.id(String.valueOf(id));
                }
            }
            source.source(objectMapper.writeValueAsBytes(logModel), Requests.INDEX_CONTENT_TYPE);
            request.add(source);
        }

        BulkResponse responses = client.bulk(request);
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

    /**
     * 写入数据
     */
    public static long writeData2(List<Map<String, Object>> data, String type, QueryWrapper wrapper, WriteRequest.RefreshPolicy refreshPolicy) throws Exception {
        if (data == null || data.isEmpty()) {
            return -1;
        }
        RestHighLevelClient client = wrapper.client;
        BulkRequest request = new BulkRequest();

        request.setRefreshPolicy(refreshPolicy == null ? WriteRequest.RefreshPolicy.IMMEDIATE : refreshPolicy);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat(TimeTools.TIME_FMT_2)).setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        for (Object logModel : data) {
            IndexRequest source = new IndexRequest(null, type);
            if (logModel instanceof Map) {
                Object id = ((Map) logModel).remove("id");
                String index = (String) ((Map) logModel).remove("index");
                source.index(index);
                if (id != null) {
                    source.id(String.valueOf(id));
                }
            }

            if (source.index() == null) {
                continue;
            }
            source.source(objectMapper.writeValueAsBytes(logModel), Requests.INDEX_CONTENT_TYPE);
            request.add(source);
        }

        BulkResponse responses = client.bulk(request);
        if (responses.hasFailures()) {
            String s = responses.buildFailureMessage();
            log.error(s);
            return 0;
        } else {
            log.info("成功入es日志数据量: " + data.size());
        }
        return data.size();
    }
}
