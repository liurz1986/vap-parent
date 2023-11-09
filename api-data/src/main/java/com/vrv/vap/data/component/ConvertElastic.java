package com.vrv.vap.data.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.util.TimeTools;
import com.vrv.vap.data.vo.CommonRequest;
import com.vrv.vap.data.vo.RequestParam;
import com.vrv.vap.data.vo.RequestParamItem;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Component
public class ConvertElastic {

    private static final Logger log = LoggerFactory.getLogger(ConvertElastic.class);

    @Autowired
    private Cache<Integer, HashMap<String, SourceField>> sourceFieldMapCache;

    @Autowired
    private StringRedisTemplate redisTpl;

    private final Map<String, String> OPERATION_MAP = new HashMap();              // 常用操作符号转义

    public static final String AUTHORITY_TYPE = "authorityType";

    public static final String SINGLE_DOMAIN = "singleDomain";

    public static final Integer OK = 1;

    private static final String SEVEN = "7";

    public ConvertElastic() {

        OPERATION_MAP.put(">", "gt");
        OPERATION_MAP.put(">=", "gte");
        OPERATION_MAP.put("<", "lt");
        OPERATION_MAP.put("<=", "lte");
        OPERATION_MAP.put("between", "between");
        OPERATION_MAP.put("=", "term");
        OPERATION_MAP.put("like", "wildcard");
        OPERATION_MAP.put("prefix_like", "prefix");
        OPERATION_MAP.put("suffix_like", "wildcard");
        OPERATION_MAP.put("exist", "exist");
        OPERATION_MAP.put("in", "terms");
    }

    // 最小用于周期统计 Group 的个数
    private final static Integer GROUP_NUM_CONST = 50;


    private final static String TERMS = "{\"terms\":{\"field\":\"%s\",\"size\":%d,\"min_doc_count\":1,\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":[{\"_count\":\"desc\"}]}}";


    public String aggTerm(String field) {
        return aggTerm(field, 10);
    }

    public String aggTerm(String field, int size) {
        return String.format(TERMS, field, size);
    }

    /**
     * 生成 query 语句
     */
    public String buildQueryParam(CommonRequest request, String timeField, String domainField) {
        JsonObject bool = new JsonObject();
        JsonArray must = buidMust(request, timeField,domainField);

        RequestParam param = request.getParam();
        if (param == null) {
            bool.add("must", must);
            JsonObject q = new JsonObject();
            q.add("bool", bool);
            return q.toString();
        }

        List sourceIds = request.getSource();
        if (param.getMust() != null && param.getMust().length > 0) {
            parseQueryParam(sourceIds, must, param.getMust());
        }
        if (param.getMust_not() != null && param.getMust_not().length > 0) {
            JsonArray must_not = new JsonArray();
            parseQueryParam(sourceIds, must_not, param.getMust_not());
            if (must_not.size() > 0) {
                bool.add("must_not", must_not);
            }
        }
        if (param.getShould() != null && param.getShould().length > 0) {
            JsonArray should = new JsonArray();
            parseQueryParam(sourceIds, should, param.getShould());
            if (should.size() > 0) {
                bool.add("should", should);
                bool.addProperty("minimum_should_match", 1);
            }
        }
        bool.add("must", must);
        JsonObject q = new JsonObject();
        q.add("bool", bool);
        return q.toString();
    }


    private JsonArray buidMust(CommonRequest query, String timeField,String domainField) {
        JsonArray root = new JsonArray();
        JsonObject timeCondition = new JsonObject();
        timeCondition.addProperty("gte", query.getStartTime().getTime());
        timeCondition.addProperty("lte", query.getEndTime().getTime());
        timeCondition.addProperty("format", "epoch_millis");
        JsonObject filed = new JsonObject();
        filed.add(timeField, timeCondition);
        JsonObject range = new JsonObject();
        range.add("range", filed);
        root.add(range);
        // 安全域过滤
        JsonObject domainObject = StringUtils.isEmpty(domainField) ? null : generateDomainQuery(domainField);
        if (domainObject != null && domainObject.has("terms")) {
            root.add(generateDomainQuery(domainField));
        }
        if (query.getParam() == null || StringUtils.isEmpty(query.getParam().getQ()) || "*".equals(query.getParam().getQ().trim())) {
            return root;
        }
        JsonObject param = new JsonObject();
        String q = query.getParam().getQ().trim();
        param.addProperty("query", escape(q));
        param.addProperty("analyze_wildcard", true);
        if (!query.isProfessor()) {
            param.addProperty("minimum_should_match", "100%");
            String version = redisTpl.opsForValue().get(SYSTEM.ES_VERSION);
            if (version.compareTo(SEVEN) < 0) {
                param.addProperty("auto_generate_phrase_queries", true);
            }
        }
        JsonObject query_string = new JsonObject();
        query_string.add("query_string", param);
        root.add(query_string);

        return root;
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

    private JsonObject generateDomainQuery(String domainField) {
        JsonObject domainTerm = new JsonObject();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        HttpSession session = request.getSession();
        Map userExtends = (Map) session.getAttribute(Global.SESSION.USER_EXTENDS);
        JsonArray codeList = new JsonArray();
        if (userExtends != null && userExtends.get(AUTHORITY_TYPE) != null && userExtends.get(AUTHORITY_TYPE).equals(OK)) {
            Map userDomain;
            Integer singleDomain = (Integer) userExtends.get(SINGLE_DOMAIN);
            // 默认单个节点，开启时为层级
            if (singleDomain != null && singleDomain.equals(OK)) {
                userDomain = (Map) session.getAttribute("_TOP_DOMAIN");
                if (userDomain != null) {
                    Set<String> codeValue = (Set<String>) userDomain.get("code");
                    String subCodeValueStr = (String) userDomain.get("subCode");
                    if (CollectionUtils.isNotEmpty(codeValue)) {
                        Iterator iter = codeValue.iterator();
                        while (iter.hasNext()) {
                            String code =(String) iter.next();
                            log.debug("domain:"+code);
                            codeList.add(code);
                        }
                    }
                    if (org.apache.commons.lang.StringUtils.isNotEmpty(subCodeValueStr)) {
                        Boolean flag = Arrays.stream(subCodeValueStr.split(",")).anyMatch(subCode -> org.apache.commons.lang.StringUtils.isNotEmpty(subCode) && subCode.length() < 4);
                        if (flag) {
                            log.debug("top domain");
                            codeList = new JsonArray();
                        }
                    }
                }
            } else {
                userDomain = (Map) session.getAttribute(Global.SESSION.DOMAIN);
                if (userDomain != null) {
                    Iterator iterator = userDomain.keySet().iterator();
                    while (iterator.hasNext()) {
                        String domainCode = (String) iterator.next();
                        log.debug("domain2:"+domainCode);
                        codeList.add(domainCode);
                    }
                }
            }

        }
        if (codeList.size() > 0 && org.apache.commons.lang.StringUtils.isNotEmpty(domainField)) {
            JsonObject domainObject = new JsonObject();
            domainObject.add(domainField,codeList);
            domainTerm.add("terms",domainObject);
        }
        return domainTerm;
    }


    public String aggTimeField(String timeFiled, Date start, Date end, String interval) {
        JsonObject extended_bounds = new JsonObject();
        extended_bounds.addProperty("min", start.getTime());
        extended_bounds.addProperty("max", end.getTime());

        JsonObject date_histogram = new JsonObject();
        date_histogram.add("extended_bounds", extended_bounds);
        date_histogram.addProperty("interval", StringUtils.isEmpty(interval) ? timeInterval(start, end) : interval);
        date_histogram.addProperty("time_zone", "Asia/Shanghai");
        date_histogram.addProperty("format", "yyyy-MM-dd HH:mm:ss");
        date_histogram.addProperty("min_doc_count", 0);
        date_histogram.addProperty("field", timeFiled);

        JsonObject bucket = new JsonObject();
        bucket.add("date_histogram", date_histogram);

        return bucket.toString();
    }


    /**
     * 自动计算时间区间
     */
    private String timeInterval(Date start, Date end) {
        long seconds = Math.abs((end.getTime() - start.getTime()) / 1000);
        if (seconds < GROUP_NUM_CONST) {
            return (int) seconds + "s";
        }
        long minutes = seconds / 60;
        if (minutes < GROUP_NUM_CONST) {
            return (int) (seconds / GROUP_NUM_CONST) + "s";
        }
        long hours = minutes / 60;
        if (hours < GROUP_NUM_CONST) {
            return (int) (minutes / GROUP_NUM_CONST) + "m";
        }
        long days = hours / 24;
        if (days < GROUP_NUM_CONST) {
            return (int) (hours / GROUP_NUM_CONST) + "h";
        }
        if (days > 366) {
            return "1M";
        }
        return (int) (days / GROUP_NUM_CONST) + "d";
    }


    /**
     * 把自定义的语句转换成ES语句
     */
    private void parseQueryParam(List<Integer> sourceIds, JsonArray array, RequestParamItem[] items) {
        for (RequestParamItem req : items) {
            String operation = req.getOperation();
            if (!OPERATION_MAP.containsKey(operation)) {
                continue;
            }

            String action = OPERATION_MAP.get(operation);
            String field = req.getField();
            String value = req.getValue();
            JsonObject condition = new JsonObject();
            JsonObject fKey = new JsonObject();
            JsonObject root = new JsonObject();

            SourceField sf = null;
            for (int id : sourceIds) {
                if (sourceFieldMapCache.containsKey(id)) {
                    if (sourceFieldMapCache.get(id).containsKey(field)) {
                        sf = sourceFieldMapCache.get(id).get(field);
                        break;
                    }
                }
            }
            switch (operation) {
                case ">":
                case "<":
                case ">=":
                case "<=":
                    if (sf == null) {
                        condition.addProperty(action, value);
                        fKey.add(field, condition);
                        root.add("range", fKey);
                        break;
                    }
                    if ("date".equals(sf.getType())) {
                        long time;
                        if (isNumeric(value)) {
                            time = Long.valueOf(value);
                        } else {
                            time = TimeTools.parse(value).getTime();
                        }
                        condition.addProperty(action, time);
                        condition.addProperty("format", "epoch_millis");
                    } else {
                        // TODO LONG DOUBLE
                        condition.addProperty(action, value);
                    }

                    fKey.add(field, condition);
                    root.add("range", fKey);
                    break;
                case "between":
                    String[] ptns = value.split(",");
                    if(ptns.length!=2){
                        break;
                    }
                    String min = ptns[0];
                    String max = ptns[1];
                    if (sf == null) {
                        condition.addProperty("gte", min);
                        condition.addProperty("lte", max);
                        fKey.add(field, condition);
                        root.add("range", fKey);
                        break;
                    }
                    if ("date".equals(sf.getType())) {
                        long _min = TimeTools.parse(min).getTime();
                        long _max = TimeTools.parse(max).getTime();
                        condition.addProperty("gte", _min);
                        condition.addProperty("lte", _max);
                        condition.addProperty("format", "epoch_millis");
                    } else {
                        // TODO LONG DOUBLE
                        condition.addProperty("gte", min);
                        condition.addProperty("lte", max);
                    }
                    fKey.add(field, condition);
                    root.add("range", fKey);
                    break;
                case "like":
                    condition.addProperty(field, "*" + value + "*");
                    root.add(action, condition);
                    break;
                case "suffix_like":
                    condition.addProperty(field, "*" + value);
                    root.add(action, condition);
                    break;
                case "prefix_like":
                case "=":
                    condition.addProperty(field, value);
                    root.add(action, condition);
                    break;
                case "exist":
                    condition.addProperty("field", field);
                    root.add(action, condition);
                    break;
                case "in":
                    JsonArray list = new JsonArray();
                    for (String ptn : value.split(",")) {
                        list.add(ptn);
                    }
                    condition.add(field, list);
                    root.add(action, condition);
                    break;
                default:
                    continue;
            }
            array.add(root);
        }

    }

    public final static boolean isNumeric(String s) {
        if (s != null && !"".equals(s.trim())) {
            return s.matches("^[0-9]*$");
        }
        return false;
    }
}