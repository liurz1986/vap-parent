package com.vrv.vap.admin.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.common.config.IndexIndicesConfig;
import com.vrv.vap.admin.common.constant.PageConstants;
import com.vrv.vap.admin.common.manager.ESClient;
import com.vrv.vap.admin.common.manager.ElasticSearchManager;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.EsResult;
import com.vrv.vap.admin.vo.QueryModel;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchScrollRequest;
import org.elasticsearch.client.*;
import org.elasticsearch.common.document.DocumentField;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.IndexNotFoundException;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Map.Entry;

/**
 * es的一些封装
 *
 * @author xw
 * @date 2015年10月20日
 */
@Component
public final class ES7Tools {
	private static final String EMPTY_STRING = "";
	private static Logger log = LoggerFactory.getLogger(ES7Tools.class);


    /**
     * 保存存在的索引
     */
    private static Set<String> indexNames_exist = new HashSet<String>();
    /**
     * 保存不存在的索引
     */
    private static Set<String> indexNames_not = new HashSet<String>();

    /**
     * 索引按天时间格式
     */
    @Value("${index.timeformat}")
    public String FORMAT;

    /**
     * 索引按月时间格式
     */
    @Value("${index.monthformat}")
    public String FORMAT_MONTH;

    /**
     * 索引名称格式
     */
    @Value("${index.format}")
    public String INDEX_NAME_FORMATE;

    /**
     * 索引与中文名称对应关系
     */
    @Value("${index.ref.name}")
    public String[] INDEX_NAME_REF;

    /**
     * es导出最大数量
     */
    @Value("${elk.max}")
    public int ES_EXPORT_MAX;

    @Value("${elk.total}")
    public int ES_TOTAL;

	// 索引时间格式正则
	private static final String INDEX_DATE_REG = "-\\d{4}\\.\\d{2}\\.\\d{2}$|-\\d{4}-\\d{2}-\\d{2}$|-\\d{4}/\\d{2}/\\d{2}$|-\\d{4}\\.\\d{2}$|-\\d{4}-\\d{2}$|-\\d{4}/\\d{2}$";

	/**
	 * es scroll 查询 缓存时间
	 */
	public static final long ES_CACHE_TIME = 60000;

	private static IndexIndicesConfig indicesConfig = null;

	private static ES7Tools eSTools;

	@PostConstruct
	public void init() {
		eSTools = this;
		eSTools.FORMAT = FORMAT;
		eSTools.FORMAT_MONTH = FORMAT_MONTH;
		eSTools.INDEX_NAME_FORMATE = INDEX_NAME_FORMATE;
		eSTools.INDEX_NAME_REF = INDEX_NAME_REF;
		eSTools.ES_EXPORT_MAX = ES_EXPORT_MAX;
		eSTools.ES_TOTAL = ES_TOTAL;
	}

	private static IndexIndicesConfig getIndicesConfig() {
		if (indicesConfig == null) {
			indicesConfig = SpringContextUtil.getApplicationContext().getBean(IndexIndicesConfig.class);
		}
		return indicesConfig;
	}


    /**
     * 获取导出最大量
     *
     * @return
     */
    public static int getExportMax() {
        return eSTools.ES_EXPORT_MAX;
    }
    /**
     * 将es查询结果用map封装
     *
     * @param queryModel
     * @param response
     * @return
     */
    public static Map<String, Object> wrapResult(SearchResponse response, QueryModel queryModel) {
        Map<String, Object> result = new HashMap<String, Object>();

        List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>(10);
        if (null == response) {
            result.put(PageConstants.TOTAL, 0);
            return result;
        }

        if (null != response.getHits()) {
            log.debug("total:" + response.getHits().getTotalHits());
            // 超过最大返回值,重新设值
            if (queryModel.isOverFlow() || response.getHits().getTotalHits().value > ES7Tools.getPageTotal()) {
                result.put(PageConstants.TOTAL, ES7Tools.getPageTotal());
            } else {
                result.put(PageConstants.TOTAL, response.getHits().getTotalHits());
            }
            result.put(PageConstants.TOTAL_ACC, response.getHits().getTotalHits());
            result.put(PageConstants.START, queryModel.getStart_());
            result.put(PageConstants.COUNT, queryModel.getCount_());
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

        result.put(PageConstants.DATAS, datas);
        return result;
    }

    /**
     * 将es查询结果用map封装
     *
     * @param queryModel
     * @param response
     * @return
     */
    public static List<Map<String, String>> wrapResultAsList(SearchResponse response, QueryModel queryModel) {
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
                datas = filteringField(response.getHits(), fields, queryModel.isNeedSource(), queryModel.getTimeField());
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
    private static <T> List<Map<String, T>> filteringField(SearchHits hits, String timeField) {
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
     * @author to Du
     * @date 2016年11月28日 上午11:18:12
     * @return void
     *****
     */
    @SuppressWarnings("unchecked")
    private static <T> void assembleDataForSize(Map<String, T> tmpData, Object sourceData, String key) {
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
                tmpData.put(key, (T) df.format(compute(value) / 2014d));
                tmpData.put("FUnit", (T) "MB");
            } else if (value >= 1073741824 && value < 1099511627776L) {
                tmpData.put(key, (T) df.format(compute2(value) / 1024d));
                tmpData.put("FUnit", (T) "GB");
            } else {
                tmpData.put(key, (T) value);
                tmpData.put("FUnit", (T) "byte");
            }
        }
    }

    private static double compute(long factor) {
        return factor / 1024;
    }

    private static double compute2(long factor) {
        return (factor / 1024) / 2014;
    }

    /**
     * 结果封装为map (从)
     *
     * @param hits
     * @param timeField
     * @return
     */
    public static List<Map<String, String>> response2MapStringValue(SearchHits hits, String timeField) {
        List<Map<String, String>> datas = new ArrayList<Map<String, String>>(10);
        for (SearchHit hit : hits) {
            Map<String, String> tmpData = new HashMap<String, String>();
            tmpData.put("_index", hit.getIndex());
            for (Entry<String, Object> tmp : hit.getSourceAsMap().entrySet()) {
                tmpData.put(tmp.getKey(), null != tmp.getValue() ? tmp.getValue().toString() : "");
            }

            utc2gmt(tmpData, timeField);

            datas.add(tmpData);
        }
        return datas;
    }

    /**
     * 结果封装为map,指定字段名
     *
     * @param hits
     * @param timeField
     * @return
     */
    public static List<Map<String, String>> response2MapStringValue(SearchHits hits, String timeField, String[] params) {
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
                        case "UserName":
                        case "user_name":
                            tmpData.put("user_name",
                                    resouce.get("UserName") == null ? "" : (String) resouce.get("UserName"));
                        case "UserID":
                        case "user_id":
                            tmpData.put("user_id", resouce.get("UserID") == null ? "" : (String) resouce.get("UserID"));
                        case "ni":
                        case "terminal_id":
                            tmpData.put("terminal_id", resouce.get("ni") == null ? "" : (String) resouce.get("ni"));
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
    private static <T> List<Map<String, T>> filteringField(SearchHits hits, String[] fields, boolean needSource,
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
    private static <T> void checkAndSetValue(Map<String, T> result, Map<String, Object> hit, String key) {
        if (key.equals("FSize")) {
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
    public static <T> void utc2gmt(Map<String, T> tmpData, String timeField) {
        Object time = tmpData.get(timeField);
        if (null == time) {
            return;
        }
        String gmtTime = "";
        if (time.toString().length() == 10) {
            gmtTime = time.toString();
        } else {
            gmtTime = TimeTools.utc2Local(time.toString());
        }
        tmpData.put(timeField, (T) gmtTime);
    }

    private static List<String> getResultFields(String[] fields) {
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
     * 根据时间范围以及索引名称,生成索引列表
     * @param indexName
     * @param startTime
     * @param endTime
     * @return
     */
    public static String[] getIndexNames(String indexName, Date startTime, Date endTime) {
        QueryModel queryModel = new QueryModel();
        queryModel.setIndexName(indexName);
        queryModel.setStartTime(startTime);
        queryModel.setEndTime(endTime);
        return ES7Tools.getIndexNames(queryModel);
    }

    /**
     * 根据时间范围以及索引名称,生成索引列表(支持多个索引)
     * @param indexNames
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> getIndexNames(String[] indexNames, Date startTime, Date endTime) {
        List<String> indexList = new ArrayList<String>();
        for (String name : indexNames) {
            if (name.length() > 0) {
                String[] names = ES7Tools.getIndexNames(name, startTime, endTime);
                List<String> tmp = Arrays.asList(names);
                indexList.addAll(tmp);
            }
        }
        return indexList;
    }

	/**
	 * 根据时间范围以及索引名称,生成索引列表
	 *
	 * @param queryModel
	 * @return
	 */
	public static String[] getIndexNames(QueryModel queryModel) {
		Map<String,IndexIndicesConfig.AliasConfig> aliasConfig = getIndicesConfig().getAliasConfig();
		if (null == queryModel.getStartTime() || null == queryModel.getEndTime() || null == queryModel.getIndexName()) {
			return null;
		}

		String names = filterIndexName(queryModel.getIndexName());

		String[] indexNames = names.split(",");
        Set<String> indexs = new LinkedHashSet<String>();
        for (String indexName : indexNames) {
            Calendar cal1 = Calendar.getInstance();
            cal1.setTime(queryModel.getStartTime());

            Calendar cal2 = Calendar.getInstance();
            cal2.setTime(queryModel.getEndTime());

            String indexNameFormate = eSTools.INDEX_NAME_FORMATE;
            String indexNameNoTime = indexNameFormate.replace("[*NAME*]", indexName);

            String tmpIndexNameFormat = null;
            String tmpIndexName = null;
			String timeFormat = null;
			if (aliasConfig != null && aliasConfig.containsKey(indexName)) {
				timeFormat = aliasConfig.get(indexName).getTimeFormat();
			} else {
				timeFormat = eSTools.FORMAT;
			}
            while (cal1.before(cal2)) {
                tmpIndexNameFormat = indexNameNoTime;
				tmpIndexName = tmpIndexNameFormat.replace("[*TIME*]", TimeTools.formatDate(cal1.getTime(), timeFormat));
                if (ES7Tools.isIndexExists(tmpIndexName, cal1.getTime(), timeFormat)) {
                    indexs.add(tmpIndexName);
                }
                cal1.add(Calendar.DATE, 1);
            }

            tmpIndexNameFormat = indexNameNoTime;
			tmpIndexName = tmpIndexNameFormat.replace("[*TIME*]", TimeTools.formatDate(cal2.getTime(), timeFormat));
            if (ES7Tools.isIndexExists(tmpIndexName, cal2.getTime(), timeFormat)) {
                indexs.add(tmpIndexName);
            }

        }

        String[] indexArr = indexs.toArray(new String[0]);
        if (log.isDebugEnabled()) {
            log.debug(queryModel.getIndexName() + ":" + Arrays.toString(indexArr));
        }
        return indexArr;
	}

	public static String filterIndexName(String indexes) {
		String result = EMPTY_STRING;
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

	/**
	 * 判断索引是否存在
	 *
	 * @param indexName
	 * @return
	 */
	public static boolean isIndexExists(String indexName, Date time, String timeFormat) {
		if (time.before(TimeTools.getStartTime())) {
			return false;
		}

		return isIndexExists(indexName,timeFormat);
	}

	/**
	 * 判断索引是否存在
	 *
	 * @param indexName
	 * @return
	 */
	public static boolean isIndexExists(String indexName,String timeFormat) {
		if (null == indexName) {
			return false;
		}
		if (indexNames_exist.contains(indexName)) {
			return true;
		}
		if (indexNames_not.contains(indexName)) {
			boolean isOk = doubleCheck(indexName,timeFormat);
			if (isOk) {
				indexNames_not.remove(indexName);
			}
			return isOk;
		}
		boolean exist = isExistIndex(indexName);
		if (exist) {
			indexNames_exist.add(indexName);
		} else {
			indexNames_not.add(indexName);
		}
		return exist;
	}

	/**
	 * 对当日的索引进行二次校验,防止后续生成后导致判断错误
	 *
	 * @param indexName
	 * @return
	 */
	private static boolean doubleCheck(String indexName,String timeFormat) {
		boolean isBefore;
		boolean isAfter;
		if (timeFormat.length() < 10) {
			return isExistIndex(indexName);
		}
		int indexNameLen = indexName.length();
		int timeFormatLen = timeFormat.length();
		isBefore = TimeTools.toDate(indexName.substring(indexNameLen - timeFormatLen,indexNameLen), timeFormat).before(TimeTools.getNowBeforeByDay(-1));
		isAfter = TimeTools.toDate(indexName.substring(indexNameLen - timeFormatLen,indexNameLen), timeFormat).after(TimeTools.getNowBeforeByDay(2));
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
    public static void checkSizeAndReset(QueryModel queryModel) {
        int start = queryModel.getStart_();
        int count = queryModel.getCount_();
        int total = ES7Tools.getPageTotal();
        if (start + count > total) {
            queryModel.setStart_(total - count);
            queryModel.setOverFlow(true);
        }
    }

    /**
     * 判断指定索引是否存在
     *
     * @param index
     * @return
     */
    public static boolean isExistIndex(String index) {
        RestClient restClient = ElasticSearchManager.getClient().getLowLevelClient();
        try {
            Request request = new Request("get", index);
            Response response = restClient.performRequest(request);
            return 200 == response.getStatusLine().getStatusCode();
        } catch (IOException e) {
            if (e instanceof ResponseException) {
                if (404 != ((ResponseException) e).getResponse().getStatusLine().getStatusCode()) {
                    log.error("", e);
                }
            }
            return false;
        }
    }

    /**
     * 获取配置的最大返回值数量
     *
     * @return
     */
    public static int getPageTotal() {
        return eSTools.ES_EXPORT_MAX;
    }

    public static Set<String> getIndexNames_exist() {
        return indexNames_exist;
    }

    public static Set<String> getIndexNames_not() {
        return indexNames_not;
    }

    /**
     * 获取距离当前日期n天前的索引名称
     *
     * @param indexName
     * @param day
     * @return
     */
    public static String getIndexName(String indexName, int day) {
        String format =  eSTools.FORMAT;
        String time = new SimpleDateFormat(format).format(TimeTools.getNowBeforeByDay(day));
        return indexName + time;
    }



    public static Response deleteByQuery(String index, QueryBuilder queryBuilder) {
        RestClient restClient = ElasticSearchManager.getClient().getLowLevelClient();
        String query = "{\"query\": " + queryBuilder.toString() + "}";
        log.debug(query);
        HttpEntity entity = new StringEntity(query, ContentType.APPLICATION_JSON);
        try

        {
            Request request = new Request("post", index + "/_delete_by_query");
            request.addParameters(Collections.<String, String> emptyMap());
            request.setEntity(entity);
            Response resp = restClient.performRequest(request);
            log.debug("",resp);
            return resp;
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }



    public static Optional<JSONObject> simpleGetQueryHttp(String tailUrl) {
        ESClient esClient = SpringContextUtil.getApplicationContext().getBean(ESClient.class);

        HttpURLConnection connection = null;
        String url1 = "http://" + getIp(esClient.getIPS()) + ":" + esClient.getPORT() + "/" + tailUrl;
        InputStream inStream = null;
        try {
            URL url = new URL(url1);
            connection = (HttpURLConnection) url.openConnection();

            if (org.apache.commons.lang3.StringUtils.isNotEmpty(esClient.getUSER()) && org.apache.commons.lang3.StringUtils.isNotEmpty(esClient.getPPP())) {
                String userAndPwd = esClient.getUSER() + ":" + esClient.getPPP();
                byte[] authEncBytes = Base64.getEncoder().encode(userAndPwd.getBytes("utf-8"));
                StringBuffer sb = new StringBuffer();
                for (byte str : authEncBytes) {
                    sb.append((char) str);
                }
                //String authStringEnc = new String(authEncBytes);
                connection.setRequestProperty("Authorization", "Basic " + sb.toString());
            }
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                inStream = connection.getInputStream();
                return Optional.of(JSON.parseObject(inStream, JSONObject.class));
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
            } catch (Exception e) {

            }
            try {
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (Exception e) {

            }
        }
        return Optional.empty();
    }

    private static String getIp(String ip) {
        if(ip.contains(",")){
            return ip.split(",")[0];
        }
        return ip;
    }

    public static QueryWrapper build() {
        return build(SpringContextUtil.getApplicationContext().getBean(ElasticSearchManager.class));
    }

    public static QueryWrapper build(ElasticSearchManager client) {
        return new QueryWrapper(client.getClient());
    }

    /**
     * es的一些封装
     *
     * @author xw
     *
     * @date 2018年3月29日
     */
    public static class QueryWrapper {
        private RestHighLevelClient client;

        public QueryWrapper(RestHighLevelClient client) {
            this.client = client;
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
                if(headers!=null && headers.length>0) {
                    Arrays.stream(headers).forEach(header -> {
                        builder.addHeader(header.getName(),header.getValue());
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
         * @param key
         *            分隔符 ":"
         * @return
         */
        public <T> T getJSONObjectValue(JSONObject obj, String key) {
            return getJSONObjectValue(obj, key, null);
        }

        /**
         * 获取json值
         *
         * @param obj
         * @param key
         *            分隔符 ":"
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
            return lowLevelResponseValue(key, "GET", endpoint, clazz, new Header[0]);
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
            queryModel.setFilterBuilder(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(sDate).to(eDate));
        }

        /**
         * 搜索,该方法有最大返回值限制,默认为10000
         *
         * @param queryModel
         * @return
         */
        public SearchResponse getSearchResponse(QueryModel queryModel) {
            checkSizeAndReset(queryModel);

            if ((null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length)
                    && (org.apache.commons.lang3.StringUtils.isEmpty(queryModel.getIndexName()))) {
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
                    log.error(LogForgingUtil.validLog(new StringBuffer("index missing !!!").append(" [indexName:")
                            .append(queryModel.getIndexName()).append("][indexNames:")
                            .append(Arrays.toString(queryModel.getIndexNames())).append("]").toString()));
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
        public SearchResponse getSearchResponseScroll(QueryModel queryModel, TimeValue keepAlive) {
            ES7Tools.checkSizeAndReset(queryModel);

            if ((null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length)
                    && (StringUtils.isEmpty(queryModel.getIndexName()))) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return null;
            }

            RestHighLevelClient client = ElasticSearchManager.getClient();
            SearchRequest request = buildQuery(queryModel);

            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }

            SearchResponse response = null;
            try {
                request.scroll(keepAlive);
                response = client.search(request,  RequestOptions.DEFAULT.toBuilder().build());
            } catch (IndexNotFoundException e) {
                if (log.isDebugEnabled()) {
                    log.debug("index missing !!!", e);
                } else {
                    log.error(new StringBuffer("index missing !!!").append(" [indexName:")
                            .append(queryModel.getIndexName()).append("][indexNames:")
                            .append(Arrays.toString(queryModel.getIndexNames())).append("]").toString());
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
         * @return
         */
        public SearchResponse getSearchResponseScrollById(String scrollId, TimeValue keepAlive) {
            SearchScrollRequest searchScrollRequest = new SearchScrollRequest(scrollId);
            searchScrollRequest.scroll(keepAlive);
            RestHighLevelClient client = ElasticSearchManager.getClient();
            SearchResponse response = null;
            try {

                response = client.searchScroll(searchScrollRequest,RequestOptions.DEFAULT.toBuilder().build());
            } catch (IndexNotFoundException e) {
                if (log.isDebugEnabled()) {
                    log.debug("index missing !!!", e);
                } else {

                }
            } catch (Exception e) {
                log.error("", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + response + "***********\n");
            }
            return response;
        }

        public Response getAggResponse(QueryModel queryModel) {

            ES7Tools.checkSizeAndReset(queryModel);

            if ((null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length)
                    && (StringUtils.isEmpty(queryModel.getIndexName()))) {
                if (log.isDebugEnabled()) {
                    log.debug("indexNames and indexName is null or 0");
                }
                return null;
            }

            RestHighLevelClient client = ElasticSearchManager.getClient();
            SearchRequest request = buildQuery(queryModel);

            if (log.isDebugEnabled()) {
                log.debug("***********\n" + request + "***********\n");
            }

            Response response = null;
            try {
                response = search(request.indices(),request.source().toString());
            } catch (IndexNotFoundException e) {
                if (log.isDebugEnabled()) {
                    log.debug("index missing !!!", e);
                } else {
                    log.error(LogForgingUtil.validLog(new StringBuffer("index missing !!!").append(" [indexName:")
                            .append(queryModel.getIndexName()).append("][indexNames:")
                            .append(Arrays.toString(queryModel.getIndexNames())).append("]").toString()));
                }
            } catch (Exception e) {
                log.error("", e);
            }
            if (log.isDebugEnabled()) {
                log.debug("***********\n" + response + "***********\n");
            }
            return response;
        }

        public Map<String, Object> getAggResponse4Map(QueryModel queryModel) {
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
            log.info("***********\n" + request + "***********\n");
            Response response = null;
            try {
                response = search(request.indices(), request.source().toString());
            } catch (IndexNotFoundException e) {
                if (log.isDebugEnabled()) {
                    log.debug("index missing !!!", e);
                } else {
                    log.error("",new StringBuffer("index missing !!!").append(" [indexName:")
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
                    ObjectMapper mapper = new ObjectMapper();
                    aggMap = mapper.readValue(responseStr, Map.class);
                } catch (IOException e) {
                    log.error("", e);
                }
            }

            return aggMap;
        }


        public Response search(String[] indexList, String queryJsonStr) {

            String indexStr = StringUtils.join(indexList, ",");
            String method="POST";
            String endpoint = "/"+indexStr+"/_search";
            HttpEntity entity = new NStringEntity(queryJsonStr, ContentType.APPLICATION_JSON);
            try {
                Request request = new Request(method,endpoint);
                request.setEntity(entity);
                Response response = ElasticSearchManager.getClient().getLowLevelClient().performRequest(request);
                return response;
            } catch (IOException e) {
                log.error("",e);
            }

            return null;

        }

        private SearchRequest buildQuery(QueryModel queryModel) {
            SearchRequest request = null;
            SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
            searchSourceBuilder.from(queryModel.getStart_()).size(queryModel.getCount_());
            if (null == queryModel.getIndexNames() || 0 == queryModel.getIndexNames().length) {
                request = new SearchRequest(queryModel.getIndexName());
            } else {
                request = new SearchRequest(queryModel.getIndexNames());
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

//            if (org.apache.commons.lang3.StringUtils.isNotEmpty(queryModel.getTypeName())) {
//                request.types(queryModel.getTypeName());
//            }
            // 放开ES7最大命中条数
            searchSourceBuilder.trackTotalHits(true);
            return request.source(searchSourceBuilder);
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
            queryBuilder.must(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(queryModel.getStartTime())
                    .to(queryModel.getEndTime()));
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
                esResult.setTotal((int) response.getHits().getTotalHits().value);
                // 超过最大返回值,重新设值
                if (queryModel.isOverFlow() || response.getHits().getTotalHits().value > eSTools.ES_TOTAL) {
                    esResult.setTotal(eSTools.ES_TOTAL);
                } else {
                    esResult.setTotal((int) response.getHits().getTotalHits().value);
                }
                esResult.setTotalAcc(response.getHits().getTotalHits().value);
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
            esResult.setCode("0");
            return esResult;
        }

    }

    // public static void main(String[] args) {
    // QueryBuilder qb =
    // QueryBuilders.boolQuery().must(QueryBuilders.termQuery("reg_id",
    // "611515"));
    // System.out.println(deleteByQuery("yysj-julong-new-2017.12", qb));
    // System.out.println("com/vrv/vap/aserver/thrift/AuditServiceImpl$2.class".matches("^com/vrv/vap/.*/thrift/.*\\.class$"));
    // }




    public  static  List<String> getIndexList(String index, String timeFieldName, String startTime, String endTime){
;
        QueryModel queryModel = new QueryModel();
        queryModel.setStartTime(new Date(Long.parseLong(startTime)));
        queryModel.setEndTime(new Date(Long.parseLong(endTime)));
        queryModel.setIndexName(index);
        String[] indexs = ES7Tools.getIndexNames(queryModel);
        if(indexs!=null) {
            List<String> indexList = Arrays.asList(indexs);
            return indexList;
        }
        return new ArrayList<>();


//
//        StringBuilder stringBuilder = new StringBuilder();
//        stringBuilder.append("{");
//        stringBuilder.append("\"fields\" : [\""+timeFieldName+"\"],");
//        stringBuilder.append("\"index_constraints\" : {");
//        stringBuilder.append("\""+timeFieldName+"\" : {");
//        stringBuilder.append("\"max_value\" : {");
//        stringBuilder.append("\"gte\" : "+startTime+"");
//        stringBuilder.append("},");
//        stringBuilder.append("\"min_value\" : {");
//        stringBuilder.append("\"lt\" : "+endTime+"");
//        stringBuilder.append("}}}}");
//        String method="POST";
//        String endpoint = "/"+index+"/_field_stats?level=indices";
//        HttpEntity entity = new NStringEntity(stringBuilder.toString(), ContentType.APPLICATION_JSON);
//        List<String> rlist = new ArrayList<>();
//        try {
//            Request request = new Request(method,endpoint);
//            request.setEntity(entity);
//            Response response = ElasticSearchManager.getClient().getLowLevelClient().performRequest(request);
//            String responseStr = EntityUtils.toString(response.getEntity());
//            rlist = resultToList(responseStr);
//
//        } catch (IOException e) {
//            log.error("",e);
//        }
//        if(rlist.size() == 0){
//            rlist.add(index);
//        }
//        return rlist;
    }


    public  static  Map<String,Object> nlpSqlExec(String sql){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(""+sql+"");
        String method="POST";
        log.info("sql查询语句=======：" + LogForgingUtil.validLog(stringBuilder.toString()));
        String endpoint = "/_sql";
        HttpEntity entity = new NStringEntity(stringBuilder.toString(), ContentType.APPLICATION_JSON);
        try {
            Request request = new Request(method,endpoint);
            request.setEntity(entity);
            Response response = ElasticSearchManager.getClient().getLowLevelClient().performRequest(request);
            String responseStr = EntityUtils.toString(response.getEntity());
            return  JsonUtil.jsonToMap(responseStr);

        } catch (Exception e) {
            log.error(EMPTY_STRING,e);
            Map<String,Object> errMap = new HashMap<>();
            errMap.put("__error",e.getMessage());
            return errMap;
        }
    }



    private static  List<String> resultToList(String  resultStr){
        List<String> list = new ArrayList<>();
        if(resultStr == null)
            return list;
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String,Object> objectMap = objectMapper.readValue(resultStr,Map.class);
            if(!objectMap.containsKey("indices"))
                return list;
            Map<String,Object> indexMap = (Map<String,Object>)objectMap.get("indices");
            if(indexMap == null)
                return list;
            list = new ArrayList(indexMap.keySet());

        } catch (IOException e) {
            log.error("",e);
        }
        return list;
    }

}
