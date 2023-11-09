package com.vrv.vap.data.service.impl;

import cn.hutool.json.JSONUtil;
import com.vrv.vap.data.service.ContentService;
import com.vrv.vap.data.service.ScreenDataService;
import com.vrv.vap.data.vo.CommonRequest;
import com.vrv.vap.data.vo.ElasticParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class ScreenDataServiceImpl implements ScreenDataService {

    @Autowired
    private ContentService contentService;

    // 最小用于周期统计 Group 的个数
    private final static Integer GROUP_NUM_CONST = 50;


    @Override
    public Map getNetflowInfo(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"netflow-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"total_byte\":{\"sum\":{\"field\":\"total_byte\"}}," +
                "\"client_total_byte\":{\"sum\":{\"field\":\"client_total_byte\"}}," +
                "\"server_total_byte\":{\"sum\":{\"field\":\"server_total_byte\"}}," +
                "\"session_num\":{\"cardinality\":{\"field\":\"sess_id\"}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        Map<String, Object> aggsMap = (Map<String, Object>)resMap.get("aggregations");
        result.put("total_byte", ((Map<String, Object>)aggsMap.get("total_byte")).get("value"));
        result.put("client_total_byte", ((Map<String, Object>)aggsMap.get("client_total_byte")).get("value"));
        result.put("server_total_byte", ((Map<String, Object>)aggsMap.get("server_total_byte")).get("value"));
        result.put("session_num", ((Map<String, Object>)aggsMap.get("session_num")).get("value"));

        return result;
    }

    @Override
    public Map getDataTrend(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"netflow-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"data_trend\":{\"date_histogram\":{\"field\":\"event_time\",\"interval\":\"" + query.getInterval() + "\"," +
                "\"format\":\"" + intervalFormat(query.getInterval()) + "\",\"time_zone\":\"+08:00\"}," +
                "\"aggs\":{\"client_total_byte\":{\"sum\":{\"field\":\"client_total_byte\"}}," +
                "\"server_total_byte\":{\"sum\":{\"field\":\"server_total_byte\"}}," +
                "\"total_byte\":{\"sum\":{\"field\":\"total_byte\"}}}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_trend")).get("buckets");
        result.put("data_trend", dataList);

        return result;
    }

    @Override
    public Map getDataTop(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"netflow-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"top_sip\":{\"terms\":{\"field\":\"sip\",\"size\":10,\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":{\"_count\":\"desc\"}}}," +
                "\"top_dip\":{\"terms\":{\"field\":\"dip\",\"size\":10,\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":{\"_count\":\"desc\"}}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        Map<String, Object> dataMap = (Map<String, Object>)resMap.get("aggregations");
        result.put("top_sip", ((Map<String, Object>)dataMap.get("top_sip")).get("buckets"));
        result.put("top_dip", ((Map<String, Object>)dataMap.get("top_dip")).get("buckets"));

        return result;
    }

    @Override
    public Map getDataProtocol(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"netflow-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"protocol\":{\"terms\":{\"field\":\"report_log_type\",\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":{\"_count\":\"desc\"}}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("protocol")).get("buckets");
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Map<String, Object> map : dataList) {
            String key = transLogType(map.get("key").toString());
            if (key == null) continue;
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put(key, map.get("doc_count"));
            mapList.add(dataMap);
        }
        result.put("protocol", mapList);

        return result;
    }

    @Override
    public Map getDataNetflowNew(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"netflow-*"};
        String queryJsonStr = "{\"from\":0,\"size\":100,\"query\":{\"bool\":{\"must\":[{\"range\":" +
                "{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"_source\":[\"event_time\",\"client_total_byte\",\"server_total_byte\",\"total_byte\"]," +
                "\"sort\":{\"event_time\":{\"order\":\"desc\"},\"_score\":{\"order\":\"desc\"}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)resMap.get("hits")).get("hits");
        result.put("data", dataList);

        return result;
    }

    @Override
    public Map getDataAppVisitTop(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"weblogin-audit-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"data_top\":{\"terms\":{\"field\":\"app_name\",\"size\":10,\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":{\"_count\":\"desc\"}}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_top")).get("buckets");
        result.put("data_top", dataList);

        return result;
    }

    @Override
    public Map getDataAppVisitSrcTop(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"weblogin-audit-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"data_top\":{\"terms\":{\"field\":\"auth_ip\",\"size\":10,\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":{\"_count\":\"desc\"}}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_top")).get("buckets");
        result.put("data_top", dataList);

        return result;
    }

    @Override
    public Map getDataTerminalLoginTop(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"terminal-login-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}},{\"term\":{\"op_type\":\"1\"}}]}}," +
                "\"aggs\":{\"data_top\":{\"terms\":{\"field\":\"std_dev_safety_marign\",\"size\":10,\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":{\"_count\":\"desc\"}}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_top")).get("buckets");
        result.put("data_top", dataList);

        return result;
    }

    @Override
    public Map getDataTerminalLoginTrend(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"terminal-login-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}},{\"term\":{\"op_type\":\"1\"}}]}}," +
                "\"aggs\":{\"data_trend\":{\"date_histogram\":{\"field\":\"event_time\",\"interval\":\"" + query.getInterval() + "\"," +
                "\"format\":\"" + intervalFormat(query.getInterval()) +"\",\"time_zone\":\"+08:00\"}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_trend")).get("buckets");
        result.put("data_trend", dataList);

        return result;
    }

    @Override
    public Map getDataUserLogTop(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"adm-operate-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"data_top\":{\"terms\":{\"field\":\"op_description\",\"size\":10,\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":{\"_count\":\"desc\"}}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_top")).get("buckets");
        result.put("data_top", dataList);

        return result;
    }

    @Override
    public Map getDataVisitNum(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"weblogin-audit-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"data_num\":{\"cardinality\":{\"field\":\"std_user_no\"}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        result.put("user_num", ((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_num")).get("value"));

        return result;
    }

    @Override
    public Map getDataAttackInfo(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"attack-audit-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"data_num\":{\"cardinality\":{\"field\":\"attack_ip\"}}},\"track_total_hits\":true}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        result.put("attack_num", ((Map<String, Object>)((Map<String, Object>)resMap.get("hits")).get("total")).get("value"));
        result.put("attack_ip_num", ((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_num")).get("value"));

        return result;
    }

    @Override
    public Map getDataAttackIpTop(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"attack-audit-*"};
        String queryJsonStr = "{\"from\":0,\"size\":0,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"aggs\":{\"data_top\":{\"terms\":{\"field\":\"attack_ip\",\"size\":10,\"min_doc_count\":1," +
                "\"shard_min_doc_count\":0,\"show_term_doc_count_error\":false,\"order\":{\"_count\":\"desc\"}}}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)((Map<String, Object>)resMap.get("aggregations")).get("data_top")).get("buckets");
        result.put("data_top", dataList);

        return result;
    }

    @Override
    public Map getDataAttackNew(CommonRequest query) {
        Map<String, Object> result = new HashMap<>();
        String[] index = new String[]{"attack-audit-*"};
        String queryJsonStr = "{\"from\":0,\"size\":100,\"query\":{\"bool\":{\"must\":[{\"range\"" +
                ":{\"event_time\":{\"gte\":" + query.getStartTime().getTime() +
                ",\"lte\":" + query.getEndTime().getTime() + ",\"format\":\"epoch_millis\"}}}]}}," +
                "\"_source\":[\"event_time\",\"attack_name\",\"attack_ip\"]," +
                "\"sort\":{\"event_time\":{\"order\":\"desc\"},\"_score\":{\"order\":\"desc\"}}}";
        ElasticParam param = new ElasticParam();
        param.setQuery(queryJsonStr);
        param.setIndex(index);

        String response = contentService.elasticSearch(param);

        Map<String, Object> resMap = JSONUtil.parseObj(response);
        if (resMap.containsKey("code")) {
            return resMap;
        }

        List<Map<String, Object>> dataList = (List<Map<String, Object>>)((Map<String, Object>)resMap.get("hits")).get("hits");
        result.put("data", dataList);

        return result;
    }

    private String transLogType(String logType) {
        switch (logType) {
            case "DT012":
                return "TCP协议";
            case "DT013":
                return "UDP协议";
            case "DT014":
                return "HTTP协议";
            case "DT015":
                return "DNS协议";
            case "DT016":
                return "邮件协议";
            case "DT017":
                return "数据库协议";
            case "DT018":
                return "SSL解密协议";
            case "DT019":
                return "文件传输协议";
            default:
                return null;
        }
    }

    private String intervalFormat(String interval) {
        if (interval.contains("h") || interval.contains("m")) {
            return "yyyy-MM-dd HH:mm:ss";
        }

        if (interval.contains("d")) {
            return "yyyy-MM-dd";
        }

        if (interval.contains("M")) {
            return "yyyy-MM";
        }

        return "yyyy-MM-dd HH:mm:ss";
    }

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
}
