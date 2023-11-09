package com.vrv.vap.xc.service.impl;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.mapper.BaseLineMapper;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.LineModel;
import com.vrv.vap.xc.model.ObjectAnalyseModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.service.ObjectAnalyseService;
import com.vrv.vap.xc.tools.DateTools;
import com.vrv.vap.xc.tools.QueryTools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.vrv.vap.xc.tools.TrendTools;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ObjectAnalyseServiceImpl implements ObjectAnalyseService {

    @Autowired
    private BaseLineMapper baseLineMapper;

    private String LINE_PRE = "base_line_";

    @Override
    public VData<List<Map<String, Object>>> queryVisitAppIpAndAcount(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, new String[]{"netflow-http"}, "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断管理员
        if (StringUtils.isNotEmpty(record.getUserType())) {
            query.must(QueryBuilders.termQuery("std_user_type", record.getUserType()));
        }
        //判断目标单位是否本单位 0-本单位；1-互联单位
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            query.must(QueryBuilders.termQuery("std_is_same_unit", record.getIsLocalOrg()));
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", record.getDeviceType().split(",")));
            } else {
                if ("3".equals(record.getDeviceType())) {
                    query.mustNot(QueryBuilders.termQuery("dst_std_sys_id",""));
                } else {
                    query.must(QueryBuilders.termQuery("dst_std_dev_type_group", record.getDeviceType()));
                }
            }
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.twoLevelAgg(queryModel, wrapper, "sip", "username", 1000,500,"count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<Map<String, List<String>>> queryUseIpAndAccount(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "terminal-login", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            query.must(QueryBuilders.termQuery("std_dev_type", record.getDeviceType()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> esList = QueryTools.simpleAgg(queryModel, wrapper,"dev_ip",500,"ip","count");
        List<String> ipList = new ArrayList<>();
        for (Map<String, Object> map : esList) {
            ipList.add((String) map.get("ip"));
        }
        List<Map<String, Object>> esList2 = QueryTools.simpleAgg(queryModel, wrapper,"username",500,"username","count");
        List<String> userList = new ArrayList<>();
        for (Map<String, Object> map : esList2) {
            userList.add((String) map.get("username"));
        }
        Map<String, List<String>> result = new HashMap<>();
        result.put("ipList", ipList);
        result.put("userList", userList);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryFileUpOrDown(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "netflow-file", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //增加协议判断 属于文件传输协议
//        query.must(QueryBuilders.termQuery("log_type", 9));
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断文件传输方向
        if (record.getFileDir() != null) {
            query.must(QueryBuilders.termQuery("file_dir", record.getFileDir()));
        }
        //判断目标单位是否本单位 0-本单位；1-互联单位
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            query.must(QueryBuilders.termQuery("std_is_same_unit", record.getIsLocalOrg()));
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", record.getDeviceType().split(",")));
            } else {
                if ("3".equals(record.getDeviceType())) {
                    query.mustNot(QueryBuilders.termQuery("dst_std_sys_id",""));
                } else {
                    query.must(QueryBuilders.termQuery("dst_std_dev_type_group", record.getDeviceType()));
                }
            }
        }
        queryModel.setQueryBuilder(query);
        long total = 0;
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        if (response != null && response.getHits() != null) {
            total = response.getHits().getTotalHits().value;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        return VoBuilder.vd(map);
    }

    @Override
    public VData<Map<String, Object>> queryLoginAvg(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "terminal-login", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //判断数据为登录的
        query.must(QueryBuilders.termQuery("op_type", 1));
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        queryModel.setQueryBuilder(query);
        long total = 0;
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        if (response != null && response.getHits() != null) {
            total = response.getHits().getTotalHits().value;
        }
        int day = TimeTools.getDays(record.getMyStartTime(), record.getMyEndTime());
        long avg = total / day;
        Map<String, Object> map = new HashMap<>();
        map.put("avg", avg);
        return VoBuilder.vd(map);
    }

    @Override
    public VData<List<Map<String, String>>> queryLoginDetailInfo(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "terminal-login", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        queryModel.setResultFields(new String[]{"username","dev_ip","login_time"});
        queryModel.setSortFields(new String[]{"login_time"});
        queryModel.setSort(true);
        queryModel.setSortOrder(SortOrder.DESC);
        queryModel.setQueryBuilder(query);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        List<Map<String, String>> list = wrapper.wrapResultAsList(response, queryModel);
        if (list != null && list.size() > 0) {
            for (Map<String, String> map : list) {
                wrapper.utc2gmt(map, "login_time");
            }
        }
        return VoBuilder.vd(list == null ? new ArrayList<>() : list);
    }

    @Override
    public VData<List<Map<String, Object>>> queryHistoryVisitProtoAndPort(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, record);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断管理员
        if (StringUtils.isNotEmpty(record.getUserType())) {
            query.must(QueryBuilders.termQuery("std_user_type", record.getUserType()));
        }
        //判断目标单位是否本单位 0-本单位；1-互联单位
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            query.must(QueryBuilders.termQuery("std_is_same_unit", record.getIsLocalOrg()));
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", record.getDeviceType().split(",")));
            } else {
                if ("3".equals(record.getDeviceType())) {
                    query.mustNot(QueryBuilders.termQuery("dst_std_sys_id",""));
                } else {
                    query.must(QueryBuilders.termQuery("dst_std_dev_type_group", record.getDeviceType()));
                }
            }
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.twoLevelAgg(queryModel, wrapper, "app_protocol", "dport", 100,5000,"count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, String>>> queryHistoryVisitAddress(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, new String[]{"netflow-http"}, "event_time");
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{"event_time"});
        queryModel.setSortOrder(SortOrder.DESC);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.mustNot(QueryBuilders.termQuery("url",""));
        //增加协议判断 属于http会话协议
//        query.must(QueryBuilders.termQuery("log_type", 5));
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断管理员
        if (StringUtils.isNotEmpty(record.getUserType())) {
            query.must(QueryBuilders.termQuery("std_user_type", record.getUserType()));
        }
        //判断目标单位是否本单位 0-本单位；1-互联单位
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            query.must(QueryBuilders.termQuery("std_is_same_unit", record.getIsLocalOrg()));
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", record.getDeviceType().split(",")));
            } else {
                if ("3".equals(record.getDeviceType())) {
                    query.mustNot(QueryBuilders.termQuery("dst_std_sys_id",""));
                } else {
                    query.must(QueryBuilders.termQuery("dst_std_dev_type_group", record.getDeviceType()));
                }
            }
        }
        queryModel.setQueryBuilder(query);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        List<Map<String, String>> list = wrapper.wrapResponse(response.getHits(), "event_time");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<Map<String, Object>> queryVisitCount(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, record);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断管理员
        if (StringUtils.isNotEmpty(record.getUserType())) {
            query.must(QueryBuilders.termQuery("std_user_type", record.getUserType()));
        }
        //判断目标单位是否本单位 0-本单位；1-互联单位
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            query.must(QueryBuilders.termQuery("std_is_same_unit", record.getIsLocalOrg()));
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", record.getDeviceType().split(",")));
            } else {
                if ("3".equals(record.getDeviceType())) {
                    query.mustNot(QueryBuilders.termQuery("dst_std_sys_id",""));
                } else {
                    query.must(QueryBuilders.termQuery("dst_std_dev_type_group", record.getDeviceType()));
                }
            }
        }
        queryModel.setQueryBuilder(query);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        long total = 0;
        if (response != null && response.getHits() != null) {
            total = response.getHits().getTotalHits().value;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        return VoBuilder.vd(map);
    }

    @Override
    public VData<List<Map<String, Object>>> queryVisitAppNameIpSecret(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, new String[]{"netflow-http"}, "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断目标单位是否本单位 0-本单位；1-互联单位
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            query.must(QueryBuilders.termQuery("std_is_same_unit", record.getIsLocalOrg()));
        }
        query.mustNot(QueryBuilders.termQuery("dst_std_sys_name", ""));
        queryModel.setQueryBuilder(query);
        List<Map<String,Object>> list = QueryTools.twoLevelAggToHits(queryModel,wrapper, "dst_std_sys_name",
                "dip",500,500,"count", new String[]{"dst_std_sys_secret_level"});
        //返回 src_std_dev_ip、dst_std_sys_name、dst_std_sys_secret_level
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> queryVisitDeviceNameIpSecret(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, record);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", record.getDeviceType().split(",")));
            } else {
                if ("3".equals(record.getDeviceType())) {
                    query.mustNot(QueryBuilders.termQuery("dst_std_sys_id",""));
                } else {
                    query.must(QueryBuilders.termQuery("dst_std_dev_type_group", record.getDeviceType()));
                }
            }
        }
        queryModel.setQueryBuilder(query);
        List<Map<String,Object>> list = QueryTools.twoLevelAggToHits(queryModel,wrapper, "username",
                "dst_std_dev_ip",500,500,"count", new String[]{"dst_std_dev_level"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> queryFileLocalBusiness(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "file-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        //判断管理员
        if (StringUtils.isNotEmpty(record.getUserType())) {
            query.must(QueryBuilders.termQuery("std_user_type", record.getUserType()));
        }
        queryModel.setQueryBuilder(query);
        //0-公开、1-内部、2-秘密、3-机密、4-绝密
        List<Map<String, Object>> list = QueryTools.twoLevelAgg(queryModel, wrapper, "business_list", "file_level", 500,100,"count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, String>>> queryFileImportList(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "netflow-file", "event_time");
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{"event_time"});
        queryModel.setSortOrder(SortOrder.DESC);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //增加协议判断 属于文件传输协议
//        query.must(QueryBuilders.termQuery("log_type", 9));
        query.must(QueryBuilders.termQuery("file_dir", "2"));
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        List<Map<String,String>> list = new ArrayList<>();
        if (response != null && response.getHits() != null) {
            list = wrapper.wrapResponse(response.getHits(), "event_time");
        }
        return VoBuilder.vd(list);
    }

    @Override
    public VData<Map<String, Object>> queryFileExportCount(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "print-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        //0-打印、1-刻录
        if (record.getPrintType() != null) {
            query.must(QueryBuilders.termQuery("op_type", record.getPrintType()));
        }
        //判断管理员
        if (StringUtils.isNotEmpty(record.getUserType())) {
            query.must(QueryBuilders.termQuery("std_user_type", record.getUserType()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        long total = 0;
        if (response != null && response.getHits() != null) {
            total = response.getHits().getTotalHits().value;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("total", total);
        return VoBuilder.vd(map);
    }

    @Override
    public VData<List<Map<String, String>>> queryFileExportList(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "print-audit", "event_time");
        queryModel.setSortFields(new String[]{"event_time"});
        queryModel.setSort(true);
        queryModel.setSortOrder(SortOrder.DESC);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        //0-打印、1-刻录
        if (record.getPrintType() != null) {
            query.must(QueryBuilders.termQuery("op_type", record.getPrintType()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        List<Map<String,String>> list = new ArrayList<>();
        if (response != null && response.getHits() != null) {
            list = wrapper.wrapResponse(response.getHits(), "event_time");
        }

        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> queryFileImportTrend(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "netflow-file", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //判断文件传输方向
        query.must(QueryBuilders.termQuery("file_dir", "2"));
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String,Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time", DateHistogramInterval.DAY, "yyyy-MM-dd", 8, "date", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<Map<String, List<Map<String, Object>>>> queryFileExportTrend(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "print-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        //0-打印、1-刻录
        query.must(QueryBuilders.termQuery("op_type", 0));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> printList = QueryTools.cardinalityAndDateAgg(queryModel,wrapper,"event_time",DateHistogramInterval.DAY,"yyyy-MM-dd",
                8, "file_name", "date", "count");


        BoolQueryBuilder query2 = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query2.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        //0-打印、1-刻录
        query2.must(QueryBuilders.termQuery("op_type", 1));
        queryModel.setQueryBuilder(query2);

        List<Map<String, Object>> printList2 = QueryTools.cardinalityAndDateAgg(queryModel,wrapper,"event_time",DateHistogramInterval.DAY,"yyyy-MM-dd",
                8, "file_name", "date", "count");
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        map.put("print", printList);
        map.put("burn", printList2);
        return VoBuilder.vd(map);
    }

    @Override
    public VData<Map<String, Object>> queryOperationMethodCount(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        //主机登录日志统计
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "operation-audit", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();

        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            query.must(QueryBuilders.termQuery("std_dev_type_group", record.getDeviceType()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"operation_type",50,"type","count");
        Map<String,Object> map = new HashMap<>();
        for (Map<String, Object> objectMap : list) {
            map.put((String) objectMap.get("type"), objectMap.get("count"));
        }
        return VoBuilder.vd(map);
    }

    public static EsQueryModel buildQueryModelByNetflow(QueryTools.QueryWrapper wrapper, PageModel model) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = wrapper.getIndexNames(new String[]{"netflow-db","netflow-dns","netflow-email","netflow-http","netflow-tcp","netflow-udp"}

            , queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField("event_time");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    @Override
    public VData<List<Map<String, Object>>> loginAvgTrend(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        int days = DateTools.dealStartEndTime(record);
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "terminal-login", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //判断数据为登录的
        query.must(QueryBuilders.termQuery("op_type", 1));
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("std_user_no", record.getUserAcount()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = QueryTools.dateAgg(queryModel, wrapper, "event_time", DateHistogramInterval.DAY, "yyyy-MM-dd", 8, "date", "count");
        List<Map<String, Object>> result = TrendTools.buildAvgTrend(maps, days);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> visitTrend(ObjectAnalyseModel record){
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        int days = DateTools.dealStartEndTime(record);
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, record);
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断管理员
        if (StringUtils.isNotEmpty(record.getUserType())) {
            query.must(QueryBuilders.termQuery("std_user_type", record.getUserType()));
        }
        //判断目标单位是否本单位 0-本单位；1-互联单位
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            query.must(QueryBuilders.termQuery("std_is_same_unit", record.getIsLocalOrg()));
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", record.getDeviceType().split(",")));
            } else {
                if ("3".equals(record.getDeviceType())) {
                    query.mustNot(QueryBuilders.termQuery("dst_std_sys_id",""));
                } else {
                    query.must(QueryBuilders.termQuery("dst_std_dev_type_group", record.getDeviceType()));
                }
            }
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> trend = QueryTools.dateAgg(queryModel, wrapper, "event_time", DateHistogramInterval.DAY, "yyyy-MM-dd", 8, "date", "count");
        LineModel lineModel = TrendTools.rendLineModel(record);
        TrendTools.rendOrgDevRoleCondition(record,lineModel);
        lineModel.setFields("count_avg");
        lineModel.setTable(LINE_PRE+"visit_count");
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("trend",trend);
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Object>> queryFileUpOrDownTrend(ObjectAnalyseModel record) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        int days = DateTools.dealStartEndTime(record);
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, record, "netflow-file", "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        //增加协议判断 属于文件传输协议
//        query.must(QueryBuilders.termQuery("log_type", 9));
        if (StringUtils.isNotEmpty(record.getUserAcount())) {
            query.must(QueryBuilders.termQuery("src_std_user_no", record.getUserAcount()));
        }
        //判断文件传输方向
        if (record.getFileDir() != null) {
            query.must(QueryBuilders.termQuery("file_dir", record.getFileDir()));
        }
        //判断目标单位是否本单位 0-本单位；1-互联单位
        if (StringUtils.isNotEmpty(record.getIsLocalOrg())) {
            query.must(QueryBuilders.termQuery("std_is_same_unit", record.getIsLocalOrg()));
        }
        //判断设备类型
        if (StringUtils.isNotEmpty(record.getDeviceType())) {
            if (record.getDeviceType().contains(",")) {
                query.must(QueryBuilders.termsQuery("dst_std_dev_type_group", record.getDeviceType().split(",")));
            } else {
                if ("3".equals(record.getDeviceType())) {
                    query.mustNot(QueryBuilders.termQuery("dst_std_sys_id",""));
                } else {
                    query.must(QueryBuilders.termQuery("dst_std_dev_type_group", record.getDeviceType()));
                }
            }
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> trend = QueryTools.dateAgg(queryModel, wrapper, "event_time", DateHistogramInterval.DAY, "yyyy-MM-dd", 8, "date", "count");
        LineModel lineModel = TrendTools.rendLineModel(record);
        lineModel.setFields("count_avg");
        lineModel.setTable(LINE_PRE+"user_file");
        TrendTools.rendOrgDevRoleCondition(record,lineModel);
        if (record.getFileDir() != null) {
            String dir = " and file_dir = '"+record.getFileDir() + "'";
            lineModel.appendWhere(dir);
        }
        List<Map<String, Object>> personLine = baseLineMapper.queryLineBysql(lineModel);
        lineModel.setType("0");
        List<Map<String, Object>> groupLine = baseLineMapper.queryLineBysql(lineModel);
        Map<String,Object> result = new HashMap<>();
        result.put("trend",trend);
        result.put("personLine",personLine);
        result.put("groupLine",groupLine);
        return VoBuilder.vd(result);
    }


}
