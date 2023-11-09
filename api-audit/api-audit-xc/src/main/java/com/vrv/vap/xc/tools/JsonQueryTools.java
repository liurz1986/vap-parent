package com.vrv.vap.xc.tools;

import com.google.gson.Gson;
import com.vrv.vap.xc.constants.QueryTypeConstants;
import com.vrv.vap.xc.model.JsonQueryModel;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 通过json构造QueryBuilder
 *
 * @author xw
 * @date 2016年7月11日
 */
public class JsonQueryTools {
    private static Log log = LogFactory.getLog(JsonQueryTools.class);
    /**
     * 通配符
     */
    private static final String WILDCARD = "*";

    /**
     * 分隔符
     */
    private static final String SPLIT = ",";

    /**
     * 正则查询标识
     */
    private static final String REGEXP_MARK = "%";

    /**
     * 精确查询标识
     */
    private static final String ACC_MARK = "_";

    /**
     * 加密标识
     */
    private static final String ENCODING_MARK = "*";

    /**
     * 数字标识
     */
    private static final String NUM_MARK = "#";

    /**
     * 时间范围查询标识
     */
    private static final String TIME_MARK = "@";

    /**
     * 小于等于 替换符
     */
    private static final String LESS_THAN = "|lte";

    /**
     * 大于等于 替换符
     */
    private static final String GREATER_THAN = "|gte";

    /**
     * in查询标识
     */
    private static final String IN_QUERY_MARK = "^";

    /**
     * @param queryJsonString <br>
     *                        {<br>
     *                        &nbsp;&nbsp;"and":{"uri":"http://192.168.118.126:9200/_all/Z_"
     *                        },<br>
     *                        &nbsp;&nbsp;"or":{"ip":"192.168.118,192.168.119.123"},<br>
     *                        &nbsp;&nbsp;"not":{"_dst":"192.168.12.11"},<br>
     *                        &nbsp;&nbsp;"range":{"@timestamp":["2015/08/31 00:00:00",
     *                        "2016/08/25 16:18:29"]},<br>
     *                        &nbsp;&nbsp;"miss":["host"]<br>
     *                        }
     * @return
     */
    public static QueryBuilder getQueryBuilder(String queryJsonString) {
        JsonQueryModel jsonQueryModel = buildQueryEntity(queryJsonString);

        if (null == jsonQueryModel) {
            log.info("查询条件解析失败:" + queryJsonString);
            return null;
        }

        return getQueryBuilder(jsonQueryModel);
    }

    /**
     * @param jsonQueryModel
     * @return
     */
    public static QueryBuilder getQueryBuilder(JsonQueryModel jsonQueryModel) {
        return getQueryBuilder(jsonQueryModel, QueryBuilders.boolQuery());
    }

    /**
     * @param jsonQueryModel
     * @param query
     * @return
     */
    public static QueryBuilder getQueryBuilder(JsonQueryModel jsonQueryModel, BoolQueryBuilder query) {
        buildOr(jsonQueryModel, query);
        buildAnd(jsonQueryModel, query);
        buildNot(jsonQueryModel, query);
        buildMiss(jsonQueryModel, query);
        buildNotMiss(jsonQueryModel, query);

        return query;
    }

    @SuppressWarnings("unchecked")
    private static JsonQueryModel buildQueryEntity(Map<String, Object> data) {
        JsonQueryModel jsonQueryModel = new JsonQueryModel();
        jsonQueryModel.setAnd((Map<String, Object>) data.get(QueryTypeConstants.S_AND));
        jsonQueryModel.setOr((Map<String, Object>) data.get(QueryTypeConstants.S_OR));
        jsonQueryModel.setNot((Map<String, Object>) data.get(QueryTypeConstants.S_NOT));

        Object list = data.get(QueryTypeConstants.S_MISS);
        if (null != list) {
            jsonQueryModel.setMiss(((List<String>) list).toArray(new String[0]));
        }

        return jsonQueryModel;
    }

    @SuppressWarnings("unchecked")
    private static void buildOr(JsonQueryModel jsonQueryModel, BoolQueryBuilder query) {
        Map<String, Object> orMap = jsonQueryModel.getOr();
        if (null == orMap || orMap.size() == 0) {
            return;
        }
        query.minimumShouldMatch("1");
        for (Map.Entry<String, Object> entity : orMap.entrySet()) {
            if (entity.getKey().equals(QueryTypeConstants._Q)) {
                BoolQueryBuilder query2 = QueryBuilders.boolQuery();

                JsonQueryModel jsonQueryModel2 = buildQueryEntity((Map<String, Object>) entity.getValue());

                query.should(getQueryBuilder(jsonQueryModel2, query2));
                continue;
            }
            // in查询
            if (entity.getKey().startsWith(IN_QUERY_MARK)) {
                String valStr = entity.getValue().toString();
                String key = entity.getKey();
                String[] vals = valStr.split(SPLIT);
                key = key.substring(1, key.length());
                if (vals.length > 0) {
                    query.should(buildQueryBuilder4In(key, vals));
                }
            } else {
                for (String val : entity.getValue().toString().split(SPLIT)) {
                    if (!val.isEmpty()) {
                        query.should(buildQueryBuilder(entity.getKey(), val));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void buildAnd(JsonQueryModel jsonQueryModel, BoolQueryBuilder query) {
        Map<String, Object> andMap = jsonQueryModel.getAnd();
        if (null == andMap) {
            return;
        }
        for (Map.Entry<String, Object> entity : andMap.entrySet()) {
            if (entity.getKey().equals(QueryTypeConstants._Q)) {
                BoolQueryBuilder query2 = QueryBuilders.boolQuery();

                JsonQueryModel jsonQueryModel2 = buildQueryEntity((Map<String, Object>) entity.getValue());
                query.must(getQueryBuilder(jsonQueryModel2, query2));
                continue;
            }
            // in查询
            if (entity.getKey().startsWith(IN_QUERY_MARK)) {
                String valStr = entity.getValue().toString();
                String key = entity.getKey();
                String[] vals = valStr.split(SPLIT);
                key = key.substring(1, key.length());
                if (vals.length > 0) {
                    query.must(buildQueryBuilder4In(key, vals));
                }
            } else {
                for (String val : entity.getValue().toString().split(SPLIT)) {
                    if (!val.isEmpty()) {
                        query.must(buildQueryBuilder(entity.getKey(), val));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static void buildNot(JsonQueryModel jsonQueryModel, BoolQueryBuilder query) {
        Map<String, Object> notMap = jsonQueryModel.getNot();
        if (null == notMap) {
            return;
        }
        for (Map.Entry<String, Object> entity : notMap.entrySet()) {
            if (entity.getKey().equals(QueryTypeConstants._Q)) {
                BoolQueryBuilder query2 = QueryBuilders.boolQuery();

                JsonQueryModel jsonQueryModel2 = buildQueryEntity((Map<String, Object>) entity.getValue());
                query.mustNot(getQueryBuilder(jsonQueryModel2, query2));
                continue;
            }
            // not in查询
            if (entity.getKey().startsWith(IN_QUERY_MARK)) {
                String valStr = entity.getValue().toString();
                String key = entity.getKey();
                String[] vals = valStr.split(SPLIT);
                key = key.substring(1, key.length());
                if (vals.length > 0) {
                    query.mustNot(buildQueryBuilder4In(key, vals));
                }
            } else {
                for (String val : entity.getValue().toString().split(SPLIT)) {
                    query.mustNot(buildQueryBuilder(entity.getKey(), val));
                }
            }
        }
    }

    private static void buildMiss(JsonQueryModel jsonQueryModel, BoolQueryBuilder query) {
        String[] miss = jsonQueryModel.getMiss();
        if (null == miss) {
            return;
        }
        for (String m : miss) {
            query.mustNot(QueryBuilders.existsQuery(m));
        }
    }

    private static void buildNotMiss(JsonQueryModel jsonQueryModel, BoolQueryBuilder query) {
        String[] notMiss = jsonQueryModel.getNotMiss();
        if (null == notMiss) {
            return;
        }
        for (String m : notMiss) {
            query.must(QueryBuilders.existsQuery(m));
        }
    }

    private static JsonQueryModel buildQueryEntity(String query) {
        Gson gson = new Gson();
        try {
            return gson.fromJson(query, JsonQueryModel.class);
        } catch (Exception e) {
            return null;
        }
    }

    private static QueryBuilder buildQueryBuilder(String key, String val) {
        // 精确查询
        if (key.startsWith(ACC_MARK)) {
            key = key.substring(1, key.length());
            if (key.startsWith(ENCODING_MARK)) {
                val = CommonTools.decodeBase64(val);
                return QueryBuilders.termQuery(key.substring(1, key.length()), val);
            } else {
                return QueryBuilders.termQuery(key, val);
            }
        }
        // 数字
        else if (key.startsWith(NUM_MARK)) {
            key = key.substring(1, key.length());
            if (key.indexOf(LESS_THAN) > -1) {
                key = key.replace(LESS_THAN, "");
                return QueryBuilders.rangeQuery(key).lte(Double.parseDouble(val));
            } else if (key.indexOf(GREATER_THAN) > -1) {
                key = key.replace(GREATER_THAN, "");
                return QueryBuilders.rangeQuery(key).gte(Double.parseDouble(val));
            } else {
                return QueryBuilders.termQuery(key, val);
            }
        }
        // 时间范围查询
        else if (key.startsWith(TIME_MARK)) {
            key = key.substring(1, key.length());
            if (val.indexOf("-") > -1) {
                String[] times = val.split("-");
                Date startTime = TimeTools.parseDate(times[0]);
                Date endTime = TimeTools.parseDate(times[1]);
                return QueryBuilders.rangeQuery(key).from(startTime).to(endTime);
            } else {
                return QueryBuilders.boolQuery();
            }
        }
        // 正则查询
        else if (key.startsWith(REGEXP_MARK)) {
            key = key.substring(1, key.length());
            return QueryBuilders.regexpQuery(key, val);
        }
        // 模糊查询
        else {
            if (key.startsWith(ENCODING_MARK)) {
                val = CommonTools.decodeBase64(val);
                // 值不为空时，加上通配符
                if (!val.isEmpty()) {
                    val = WILDCARD + val + WILDCARD;
                }
                return QueryBuilders.wildcardQuery(key.substring(1, key.length()), val);
            } else {
                // 值不为空时，加上通配符
                if (!val.isEmpty()) {
                    val = WILDCARD + val + WILDCARD;
                }
                return QueryBuilders.wildcardQuery(key, val);
            }
        }
    }

    private static QueryBuilder buildQueryBuilder4In(String key, String[] val) {
        if (key.startsWith(ENCODING_MARK)) {
            val = CommonTools.decodeBase64(val);
            return QueryBuilders.termsQuery(key.substring(1, key.length()), val);
        } else {
            return QueryBuilders.termsQuery(key, val);
        }
    }

    /**
     * example:
     * <pre>
     * [{unitIdent=000000000000, unitDepartSubCode=001001, unitName=武汉市保密办, unitDepartName=汉口区保密办, unitGeoIdent=JG000001}]
     * <out> [{
     * 	"unitIdent": "000000000000",
     * 	"unitDepartSubCode": "001001",
     * 	"unitName": "武汉市保密办",
     * 	"unitDepartName": "汉口区保密办",
     * 	"unitGeoIdent": "JG000001"
     * }]
     * </out>
     * @param nonStandardJson
     * @return
     */
    public static String convertToStandardJson(String nonStandardJson) {
        Pattern pattern = Pattern.compile("\\{([^}]*)\\}");
        Matcher matcher = pattern.matcher(nonStandardJson);

        List<String> standardJsonItems = new ArrayList<>();
        while (matcher.find()) {
            String item = matcher.group(1);
            String[] keyValuePairs = item.split(",");
            List<String> standardKeyValuePairs = new ArrayList<>();
            for (String keyValuePair : keyValuePairs) {
                String[] parts = keyValuePair.split("=");
                String key = parts[0].trim();
                String value = parts[1].trim();
                standardKeyValuePairs.add("\"" + key + "\": \"" + value + "\"");
            }
            standardJsonItems.add("{" + String.join(", ", standardKeyValuePairs) + "}");
        }

        return "[" + String.join(", ", standardJsonItems) + "]";
    }

}
