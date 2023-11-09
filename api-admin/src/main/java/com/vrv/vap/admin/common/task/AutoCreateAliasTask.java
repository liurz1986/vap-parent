package com.vrv.vap.admin.common.task;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.admin.common.constant.AliasTypeConstants;
import com.vrv.vap.admin.common.manager.ESClient;
import com.vrv.vap.admin.common.properties.AutoCreateAliasConfig;
import com.vrv.vap.admin.common.util.ES7Tools;
import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.common.util.TimeTools;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.nio.entity.NStringEntity;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.Response;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.json.JsonXContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lilang
 * @date 2020/4/14
 * @description
 */
@Component
public class AutoCreateAliasTask {

    private static final Logger log = LoggerFactory.getLogger(AutoCreateAliasTask.class);

    @Autowired
    private AutoCreateAliasConfig createAliasConfig;

    private Map<String,AutoCreateAliasConfig.AliasConfig> aliasConfig;

    @PostConstruct
    public void initAliasConfig(){
        aliasConfig = createAliasConfig.getAliasConfig();
    }


    @Scheduled(cron = "0 0/5 * * * ?")
    public void createAlias() {
        ES7Tools.QueryWrapper queryWrapper = ES7Tools.build();
        try {
            log.info("start checking elasticsearch index alias...");

            if (aliasConfig != null) {
                Iterator iterator = aliasConfig.keySet().iterator();
                while (iterator.hasNext()) {
                    String tailUrl = "_alias";
                    String indexName = (String) iterator.next();
                    String type = aliasConfig.get(indexName).getType();
                    if (StringUtils.isEmpty(type)) {
                        type = AliasTypeConstants.TYPE_DAY;
                    }
                    String indexNameFormat = this.getIndexNameFormat(type);
                    if (AliasTypeConstants.TYPE_YEAR.equals(type)) {
                        tailUrl = indexName + "*" + TimeTools.formatDate(new Date(),indexNameFormat) + "/"+tailUrl;
                    } else {
                        if(createAliasConfig.isCurrentMonth()){
                            tailUrl = indexName + "*" + TimeTools.formatDate(new Date(),indexNameFormat) + "/"+tailUrl;
                        } else {
                            tailUrl = indexName + "*/"+tailUrl;
                        }
                    }
                    Optional<JSONObject> opt = ES7Tools.simpleGetQueryHttp(tailUrl);
                    this.generateAlias(indexName,opt,queryWrapper,type);
                }
            }

            log.info("finish refresh elasticsearch index alias ...");
        } catch (Exception e) {
            log.error("", e);
        }
    }

    private void generateAlias(String indexName,Optional<JSONObject> opt,ES7Tools.QueryWrapper queryWrapper,String type) {
        if (opt.isPresent()) {
            String reg = this.getReg(type);
            Pattern pattern = Pattern.compile(reg);

            Map<String, String> indexCreate = new HashMap<>();
            if (opt.get() != null && opt.get().entrySet() != null) {
                opt.get().entrySet().forEach(e -> {
                    String index = e.getKey();
                    Matcher matcher = pattern.matcher(index);
                    //未匹配到索引
                    if (index.startsWith("searchguard") || !matcher.find()) {
                        return;
                    }
                    String group = matcher.group();
                    String indexPrefix = index.replace(group, "");
                    if(!aliasConfig.containsKey(indexPrefix.substring(0,indexPrefix.length()-1))){
                        //根据配置的索引来创建别名
                        return;
                    }

                    JSONObject tmp = (JSONObject) e.getValue();
                    if (null != tmp) {
                        JSONObject aliases = tmp.getJSONObject("aliases");
                        if (null != aliases && !aliases.isEmpty()) {
                            //已有别名则退出此次操作
                            return;
                        }
                    }
                    if (AliasTypeConstants.TYPE_YEAR.equals(type)) {
                        List<String> allDay = this.getAllDay();
                        for (String day : allDay) {
                            indexCreate.put(indexPrefix + day, day);
                        }
                    } else {
                        Date date = TimeTools.parseDate(group, this.getIndexNameFormat(type));
                        Calendar calendar = Calendar.getInstance();
                        calendar.clear();
                        calendar.setTime(date);
                        int firstDay = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                        int lastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                        for (int i = firstDay; i <= lastDay; i++) {
                            String day = group + (i < 10 ? (".0" + i) : ("." + i));
                            indexCreate.put(indexPrefix + day, day);
                        }
                    }
                });
            }

            log.info(indexName + "需要创建的索引别名" + indexCreate.keySet().size());
            postMultiAlias(queryWrapper, indexCreate,type);

        } else {
            ESClient esClient = SpringContextUtil.getBean(ESClient.class);
            log.info("索引未创建或es未连接上，请检查连接配置及索引！ip地址：" + esClient.getIPS() + ";端口号：" + esClient.getPORT() + ";索引名称：" + indexName);
        }
    }

    private List<String> getAllDay() {
        List<String> result = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
        int year = Integer.parseInt(TimeTools.formatDate(new Date(),"yyyy"));
        int m = 1;
        while (m <= 12)
        {
            int month = m;
            Calendar cal = Calendar.getInstance();
            cal.clear();//清除信息
            cal.set(Calendar.YEAR,year);
            // 1月从0开始
            cal.set(Calendar.MONTH,month-1);
            // 设置为1号,当前日期既为本月第一天
            cal.set(Calendar.DAY_OF_MONTH,1);
            result.add(sdf.format(cal.getTime()));
            int count=cal.getActualMaximum(Calendar.DAY_OF_MONTH);
            for (int j = 0;j <= (count - 2);)
            {
                cal.add(Calendar.DAY_OF_MONTH,+1);
                j++;
                result.add(sdf.format(cal.getTime()));
            }
            m++;
        }
        return result;
    }

    private String getIndexNameFormat(String type) {
        String indexNameFormat = "";
        switch (type) {
            case AliasTypeConstants.TYPE_DAY :
                indexNameFormat = "yyyy.MM";
                break;
            case AliasTypeConstants.TYPE_MONTH :
                indexNameFormat = "yyyy.MM";
                break;
            case AliasTypeConstants.TYPE_YEAR :
                indexNameFormat = "yyyy";
                break;
        }
        return indexNameFormat;
    }

    private String getReg(String type) {
        String reg = "";
        switch (type) {
            case AliasTypeConstants.TYPE_DAY :
                reg = "[1-9]\\d{3}(.)((0[13578]|1[02])|(0[469]|11)|(02))$";
                break;
            case AliasTypeConstants.TYPE_MONTH :
                reg = "[1-9]\\d{3}(.)((0[13578]|1[02])|(0[469]|11)|(02))$";
                break;
            case AliasTypeConstants.TYPE_YEAR :
                reg = "[1-9]\\d{3}$";
                break;
        }
        return reg;
    }

    private void postMultiAlias(ES7Tools.QueryWrapper queryWrapper, Map<String, String> indexCreate, String type) {
        if(indexCreate.isEmpty()){
            return;
        }
        String defaultTimeField = createAliasConfig.getDefaultTimeField();
        if (StringUtils.isEmpty(defaultTimeField)) {
            defaultTimeField = "event_time";
        }
        try {
            HttpEntity entity = new NStringEntity(createMultiAliasJson(indexCreate, defaultTimeField,type), ContentType.APPLICATION_JSON);
            Request request = new Request("post", "/_aliases");
            request.setEntity(entity);

            Response response = queryWrapper.getClient().getLowLevelClient().performRequest(request );
            log.info("",response);
        } catch (IOException e) {
            log.error("",e);
        }

    }

    private String createMultiAliasJson(Map<String, String> indexCreate,final String defaultField,String type) {

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
                    String index;
                    if (AliasTypeConstants.TYPE_YEAR.equals(type)) {
                        index = indexAlias.substring(0, indexAlias.length() - 6);
                    } else {
                        index = indexAlias.substring(0, indexAlias.length() - 3);
                    }
                    String indexPrefix = indexAlias.substring(0, indexAlias.length() - 11);
                    AutoCreateAliasConfig.AliasConfig aliasConfig = this.aliasConfig.get(indexPrefix);

//                    String timeField = indexConfig.getTimeField().getOrDefault(indexAlias.substring(0, indexAlias.length() - 11),defaultField);
                    String timeField = aliasConfig.getTimeField();
                    String day = r.getValue();

                    // 给每个别名添加当天的时间过滤
                    Date startDate = sdf.parse(day + " 00:00:00");
                    Date endDate = sdf.parse(day + " 24:00:00");
                    String start = null;
                    String end = null;
                    if(TimeTools.UTC_PTN.equals(aliasConfig.getTimeFormat())){
                        start = TimeTools.getUtcTimeString(startDate);
                        end = TimeTools.getUtcTimeString(endDate);
                    } else {
                        start = TimeTools.format(startDate,aliasConfig.getTimeFormat());
                        end = TimeTools.format(endDate,aliasConfig.getTimeFormat());
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
                    log.error("",e);
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
