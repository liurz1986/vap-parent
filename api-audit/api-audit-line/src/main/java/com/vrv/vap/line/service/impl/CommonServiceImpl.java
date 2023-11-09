package com.vrv.vap.line.service.impl;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jcraft.jsch.JSchException;
import com.vrv.vap.toolkit.config.PathConfig;
import com.vrv.vap.toolkit.tools.*;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.line.client.ElasticSearchManager;
import com.vrv.vap.line.config.AutoCreateAliasConfig;
import com.vrv.vap.line.config.IndexConfig;
import com.vrv.vap.line.model.EsTemplate;
import com.vrv.vap.line.model.Source;
import com.vrv.vap.line.service.CommonService;
import com.vrv.vap.line.tools.EsCurdTools;
import com.vrv.vap.line.tools.QueryTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * es公共服务实现
 * Created by lizj on 2019/9/16.
 */
@Service
public class CommonServiceImpl implements CommonService {

    private static final Log log = LogFactory.getLog(CommonServiceImpl.class);

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

    @Autowired
    private ElasticSearchManager esClient;
    @Autowired
    private IndexConfig indexConfig;
    @Autowired
    private PathConfig pathConfig;
    @Autowired
    private AutoCreateAliasConfig createAliasConfig;

    private Map<String, AutoCreateAliasConfig.AliasConfig> aliasConfig;

    /**
     * 匹配xxx-yyyy.MM 和 xxx-yyyy.MM.dd
     */
    private static String regexStr = "(((.*)-\\d\\d\\d\\d)\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01]))|(((.*)-\\d\\d\\d\\d)(0[1-9]|1[012])|((.*)_\\d\\d\\d\\d)(0[1-9]|1[012])(0[1-9]|[12][0-9]|3[01]))|(((.*)_\\d\\d\\d\\d)(0[1-9]|1[012]))";

    private static String regexCsvStr = "((((.*)-\\d\\d\\d\\d)\\.(0[1-9]|1[012])\\.(0[1-9]|[12][0-9]|3[01]))|(((.*)-\\d\\d\\d\\d)\\.(0[1-9]|1[012])))(.csv)";

    private static Pattern patternDay = Pattern.compile("[1-9]\\d{3}(.)(((0[13578]|1[02])(.)([0-2]\\d|3[01]))|((0[469]|11)(.)([0-2]\\d|30))|(02([01]\\d|2[0-8])))");


    // 推送相关配置
    @Value("${push.url:https://localhost:8780/push/user}")
    private String pushUrl;

    @Value("${push.userId:31}")
    private Integer pushUserId;


    @PostConstruct
    public void initAliasConfig() {
        aliasConfig = createAliasConfig.getAliasConfig();
    }

    @Override
    public Result createAlias() {
        QueryTools.QueryWrapper queryWrapper = QueryTools.build(esClient, indexConfig);
        try {
            log.info("start checking elasticsearch index alias...");
//            String tailUrl = "_aliases";
            //高版本是_alias
            String tailUrl = "_alias";
            if (createAliasConfig.isCurrentMonth()) {
                tailUrl = "*" + TimeTools.format(new Date(), "yyyy.MM") + "/" + tailUrl;
            }
            //Optional<JSONObject> opt = queryWrapper.lowLevelResponseValue("", "_aliases");
            Optional<JSONObject> opt = EsCurdTools.simpleGetQueryHttp(tailUrl);
            if (opt.isPresent()) {
//                String regDay = "[1-9]\\d{3}(((0[13578]|1[02])([0-2]\\d|3[01]))|((0[469]|11)([0-2]\\d|30))|(02([01]\\d|2[0-8])))$";
                String regMonth = "[1-9]\\d{3}(.)((0[13578]|1[02])|(0[469]|11)|(02))$";
//                Pattern patternDay = Pattern.compile(regDay);
                Pattern patternMonth = Pattern.compile(regMonth);

                Map<String, String> indexCreate = new HashMap<>();
//                List<String> indexCreate = new ArrayList<>();
                opt.get().entrySet().forEach(e -> {
                    String index = e.getKey();
                    Matcher matcher = patternMonth.matcher(index);
                    //未匹配到月索引
                    if (index.startsWith("searchguard") || !matcher.find()) {
                        return;
                    }
                    String month = matcher.group();
                    String indexPrefix = index.replace(month, "");
                    if (!aliasConfig.containsKey(indexPrefix.substring(0, indexPrefix.length() - 1))) {
                        //根据配置的索引来创建别名
                        return;
                    }

                    log.info(index + " : setting max_result_window=1000000000...");
                    queryWrapper.setWindowMaxResult(index);

                    Date date = TimeTools.parseDate(month, "yyyy.MM");
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(date);
                    int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                    int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                    JSONObject tmp = (JSONObject) e.getValue();
                    if (null != tmp) {
                        JSONObject aliases = tmp.getJSONObject("aliases");
                        if (null != aliases && !aliases.isEmpty()) {
                            //已有别名则退出此次操作
                            return;
                        }
                    }
                    for (int i = firstDay; i <= lastDay; i++) {
                        String day = month + (i < 10 ? (".0" + i) : ("." + i));
                        indexCreate.put(indexPrefix + day, day);
                    }

                });

                log.info("需要创建的索引别名" + indexCreate.keySet().size());
                postMultiAlias(queryWrapper, indexCreate);

            }
            log.info("finish refresh elasticsearch index alias ...");
        } catch (Exception e) {
            log.error("", e);
        }

        return null;
    }

    @Override
    public Map create365Alias(String index, String indexPrefix, String timeField, String timeFormat, String year, boolean force) {
        Map resEntity = new HashMap<String, Object>();
        QueryTools.QueryWrapper queryWrapper = QueryTools.build(esClient, indexConfig);
        try {
            Map aliasRes = queryAliias(queryWrapper, index);

            if (aliasRes != null) {
                Map<String, String> indexCreate = new HashMap<>();

                Date date = TimeTools.parseDate(year, "yyyy");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);

                if (aliasRes.containsKey(index)) {
                    Map object = (Map) aliasRes.get(index);
                    if (!((Map) object.get("aliases")).isEmpty()) {
                        // 已有别名则退出此次操作
                        resEntity.put("code", "100");
                        resEntity.put("warn", "已有别名, 请检查已有的是否正确");
                        resEntity.putAll(aliasRes);
                        return resEntity;
                    }
                } else {
                    resEntity.put("warn", index + "索引不存在");
                    resEntity.put("code", "404");
                    if (force) {
                        Request createRequest = new Request("put", index);
                        //request.setEntity(entity);
                        Response response = queryWrapper.getClient().getLowLevelClient().performRequest(createRequest);
                        log.info(index + "索引不存在, 已新建");
                        resEntity.put("warn", index + "索引不存在, 已自动创建");
                    } else {
                        return resEntity;
                    }
                }

                for (int m = 0; m < 12; m++) {
                    calendar.set(Calendar.MONTH, m);
                    int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                    int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

                    int mon = m + 1;
                    String month = year + (mon < 10 ? (".0" + mon) : ("." + mon));
                    indexCreate.put(indexPrefix + month, month);
                    for (int i = firstDay; i <= lastDay; i++) {
                        String day = month + (i < 10 ? (".0" + i) : ("." + i));
                        indexCreate.put(indexPrefix + day, day);
                    }
                }
//                log.info(index + "需要创建的索引别名" + indexCreate.keySet().size());
                log.debug(index + "需要创建的索引别名" + indexCreate.keySet());
                resEntity.put("message", postMultiAlias(queryWrapper, timeField, timeFormat, indexCreate, index));
                resEntity.put("code", "200");
            }
            log.info("finish refresh elasticsearch index alias ...");
        } catch (Exception e) {
            log.error("", e);
        }
        return resEntity;
    }

    private Map queryAliias(QueryTools.QueryWrapper queryWrapper, String index) {
        log.info("start to check elasticsearch index alias...");
        String tailUrl = "_alias";
        tailUrl = index + "/" + tailUrl;

        try {
            Request request = new Request("get", tailUrl);
            //request.setEntity(entity);
            Response response = queryWrapper.getClient().getLowLevelClient().performRequest(request);
            return (Map) JSON.parse(EntityUtils.toString(response.getEntity()));
        } catch (IOException e) {
            log.error("", e);
        }
        return Collections.emptyMap();
    }

    private String postMultiAlias(QueryTools.QueryWrapper queryWrapper, String defaultField, String timeFormat, Map<String, String> indexCreate, String targetIndex) {
        if (indexCreate.isEmpty()) {
            return "";
        }
        String defaultTimeField = defaultField;
        try {
            HttpEntity entity = new NStringEntity(createMultiAliasJson(indexCreate, defaultTimeField, timeFormat, targetIndex), ContentType.APPLICATION_JSON);
            Request request = new Request("post", "/_aliases");
            request.setEntity(entity);
            Response response = queryWrapper.getClient().getLowLevelClient().performRequest(request);
            log.info(response);
            return response.toString();
        } catch (IOException e) {
            log.error("", e);
        }
        return "";
    }

    private String createMultiAliasJson(Map<String, String> indexCreate, final String defaultField, final String timeFormat, String targetIndex) {

        IndexRequest indexRequest = new IndexRequest();
        String source = null;
        try {
            final XContentBuilder builder = JsonXContent.contentBuilder().startObject().startArray("actions");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            indexCreate.entrySet().forEach(r -> {
                try {
                    String indexAlias = r.getKey();
                    String index = targetIndex;
                    String indexPrefix = indexAlias.substring(0, indexAlias.length() - 11);

                    String timeField = defaultField;
                    String day = r.getValue();

                    if (day.length() > 8) {
                        // 给每个别名添加当天的时间过滤
                        Date startDate = sdf.parse(day + " 00:00:00");
                        Date endDate = sdf.parse(day + " 23:59:59");
                        String start = null;
                        String end = null;
                        String timeFormat2 = timeFormat;
                        if (TimeTools.TIME_FMT_1.equals(timeFormat2)) {
                            start = TimeTools.gmtToUtcTimeAsString2(startDate);
                            end = TimeTools.gmtToUtcTimeAsString2(endDate);
                        } else {
                            start = TimeTools.format(startDate, timeFormat2);
                            end = TimeTools.format(endDate, timeFormat2);
                        }

                        builder.startObject().startObject("add").field("index", index).field("alias", indexAlias).startObject("filter").startObject("range")
                                .startObject(timeField).field("from", start).field("to", end).field("include_lower", "true").field("include_upper", "true")
                                .field("boost", "1").endObject().endObject().endObject().endObject().endObject();
                    } else {
                        //按月的索引别名
                        // 给每个别名月索引添加当月的时间过滤
                        Date startDate = sdf.parse(day + ".01" + " 00:00:00");
                        Date endDate = TimeTools.getLastDayOfMonth(startDate);
                        String start = null;
                        String end = null;
                        String timeFormat2 = timeFormat;
                        if (TimeTools.TIME_FMT_1.equals(timeFormat2)) {
                            start = TimeTools.gmtToUtcTimeAsString2(startDate);
                            end = TimeTools.gmtToUtcTimeAsString2(endDate);
                        } else {
                            start = TimeTools.format(startDate, timeFormat2);
                            end = TimeTools.format(endDate, timeFormat2);
                        }

                        builder.startObject().startObject("add").field("index", index).field("alias", indexAlias).startObject("filter").startObject("range")
                                .startObject(timeField).field("from", start).field("to", end).field("include_lower", "true").field("include_upper", "true")
                                .field("boost", "1").endObject().endObject().endObject().endObject().endObject();

                    }

                } catch (Exception e) {
                    log.error("构建别名的语句时错误", e);
                }
            });
            builder.endArray().endObject();

            indexRequest.source(builder);
            // 生成json字符串
            source = indexRequest.source().utf8ToString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return source;
    }


    @Override
    public Result createTemplate(EsTemplate template) {
        Result re = new Result();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        RestClient lowLevelClient = wrapper.getClient().getLowLevelClient();
        String tailUrl = "/_template/"+template.getName();
        Request request = new Request("put", tailUrl);
        HttpEntity entity = new NStringEntity(template.toTemplateJson(), ContentType.APPLICATION_JSON);
        request.setEntity(entity);
        try{
            lowLevelClient.performRequest(request);
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return re;
    }

    @Override
    public boolean indexTemplateExists(String templateName) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        RestClient lowLevelClient = wrapper.getClient().getLowLevelClient();
        Request request = new Request(HttpHead.METHOD_NAME, "_template/" + templateName);
        try{
            Response response = lowLevelClient.performRequest(request);
            if (200 == response.getStatusLine().getStatusCode()) {
                return true;
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }
        return false;
    }

    @Override
    public boolean add2ApiData(String name) {
        Source source = new Source();
        source.setDataType(2);
        source.setName(name);

        return false;
    }

    /**
     * 初始化es服务器的ssh会话
     *
     * @return
     * @throws JSchException
     */

    private String createSingleAliasJson(String field, String from, String to) {
        IndexRequest indexRequest = new IndexRequest();
        XContentBuilder builder = null;
        try {
            builder = JsonXContent.contentBuilder()
                    .startObject()
                    .startObject("filter")
                    .startObject("range")
                    .startObject("event_time")
                    .field("from", from)
                    .field("to", to)
                    .field("include_lower", "true")
                    .field("include_upper", "true")
                    .field("boost", "1")
                    .endObject()
                    .endObject()
                    .endObject()
                    .endObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
        indexRequest.source(builder);
        // 生成json字符串
        String source = indexRequest.source().utf8ToString();
        return source;
    }

    private void postMultiAlias(QueryTools.QueryWrapper queryWrapper, Map<String, String> indexCreate) {
        if (indexCreate.isEmpty()) {
            return;
        }
        String defaultTimeField = indexConfig.getTimeField().getOrDefault("default", "event_time");
        try {
            HttpEntity entity = new NStringEntity(createMultiAliasJson(indexCreate, defaultTimeField), ContentType.APPLICATION_JSON);
            Request request = new Request("post", "/_aliases");
            request.setEntity(entity);
//                request.addParameters(params);
//            Response response = queryWrapper.getClient().getLowLevelClient().performRequest("post", "/_aliases", Collections.emptyMap(), entity);
            Response response = queryWrapper.getClient().getLowLevelClient().performRequest(request);
            log.info(response);
        } catch (IOException e) {
            log.error(e);
        }

    }

    private String createMultiAliasJson(Map<String, String> indexCreate, final String defaultField) {

        IndexRequest indexRequest = new IndexRequest();
        String source = null;
        try {
            final XContentBuilder builder = JsonXContent.contentBuilder()
                    .startObject()
                    .startArray("actions");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
            indexCreate.entrySet().forEach(r -> {
                try {
                    String indexAlias = r.getKey();
                    String index = indexAlias.substring(0, indexAlias.length() - 3);
                    String indexPrefix = indexAlias.substring(0, indexAlias.length() - 11);
                    AutoCreateAliasConfig.AliasConfig aliasConfig = this.aliasConfig.get(indexPrefix);

//                    String timeField = indexConfig.getTimeField().getOrDefault(indexAlias.substring(0, indexAlias.length() - 11),defaultField);
                    String timeField = aliasConfig.getTimeField();
                    String day = r.getValue();

                    // 给每个别名添加当天的时间过滤
                    Date startDate = sdf.parse(day + " 00:00:00");
                    Date endDate = sdf.parse(day + " 23:59:59");
                    String start = null;
                    String end = null;
                    if (TimeTools.TIME_FMT_1.equals(aliasConfig.getTimeFormat())) {
                        start = TimeTools.gmtToUtcTimeAsString2(startDate);
                        end = TimeTools.gmtToUtcTimeAsString2(endDate);
                    } else {
                        start = TimeTools.format(startDate, aliasConfig.getTimeFormat());
                        end = TimeTools.format(endDate, aliasConfig.getTimeFormat());
                    }

                    builder.startObject().startObject("add")
                            .field("index", index)
                            .field("alias", indexAlias)
                            .startObject("filter")
                            .startObject("range")
                            .startObject(timeField)
                            .field("from", start)
                            .field("to", end)
                            .field("include_lower", "true")
                            .field("include_upper", "true")
                            .field("boost", "1")
                            .endObject()
                            .endObject()
                            .endObject()
                            .endObject()
                            .endObject();

                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    log.error(e);
                }
            });
            builder.endArray().endObject();

            indexRequest.source(builder);
            // 生成json字符串
            source = indexRequest.source().utf8ToString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return source;
    }
}
