package com.vrv.vap.xc.service.impl;

import com.github.xtool.collect.Lists;
import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.ExcelInfo;
import com.vrv.vap.toolkit.excel.out.ExcelData;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.excel.out.WriteHandler;
import com.vrv.vap.toolkit.tools.PathTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.AppDevService;
import com.vrv.vap.xc.tools.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AppDevServiceImpl implements AppDevService {
    private static final Log log = LogFactory.getLog(AppDevServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public VList<Map<String, String>> operationDetail(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "operation-audit", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("report_dev_ip", model.getDevIp()));
        }
        query.must(QueryBuilders.termQuery("resource_opt", "0"));
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("std_dev_type_group", model.getDevTypeGroup()));
        }
        if(StringUtils.isNotEmpty(model.getUserAccount())){
            query.must(QueryBuilders.wildcardQuery("user_account","*"+model.getUserAccount()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getSip())){
            query.must(QueryBuilders.termQuery("resource_ip", model.getSip()));
        }
        if(StringUtils.isNotEmpty(model.getProtocol())){
            query.must(QueryBuilders.termQuery("operation_protocol", model.getProtocol()));
        }
        if(StringUtils.isNotEmpty(model.getPort())){
            query.must(QueryBuilders.termQuery("operation_port", model.getPort()));
        }
        if(StringUtils.isNotEmpty(model.getUrl())){
            query.must(QueryBuilders.wildcardQuery("operation_url_list","*"+model.getUrl()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getRecord())){
            query.must(QueryBuilders.wildcardQuery("operation_record","*"+model.getRecord()+"*"));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        long total = 0;
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            if(list != null){
                BoolQueryBuilder lastQuery = new BoolQueryBuilder();
                StringBuilder sessions = new StringBuilder();
                for(Map<String, String> m: list){
                    sessions.append(m.get("session_id")).append(",");
                }
                lastQuery.must(QueryBuilders.termsQuery("session_id", sessions.toString().split(",")));
                lastQuery.must(QueryBuilders.termQuery("resource_opt", "2"));
                queryModel.setQueryBuilder(lastQuery);
                SearchResponse lastResponse = wrapper.getSearchResponse(queryModel);
                if (lastResponse != null) {
                    List<Map<String, String>> lastList = wrapper.wrapResponse(lastResponse.getHits());
                    if(lastList != null && lastList.size() > 0){
                        Map<String, String> logoutMap = lastList.stream().collect(Collectors.toMap(r -> r.get("session_id"), r -> r.get("event_time")));
                        for(Map<String, String> m: list){
                            m.put("logout_time",logoutMap.get(m.get("session_id")));
                        }
                    }
                }
            }
            total = searchResponse.getHits().getTotalHits().value;
            return VoBuilder.vl(total,list);
        }
        return VoBuilder.vl(total, new ArrayList<>());
    }

    @Override
    public VData<Export.Progress> operationDetailExport(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "operation-audit", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("report_dev_ip", model.getDevIp()));
        }
        query.must(QueryBuilders.termQuery("resource_opt", "0"));
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("std_dev_type_group", model.getDevTypeGroup()));
        }
        if(StringUtils.isNotEmpty(model.getUserAccount())){
            query.must(QueryBuilders.wildcardQuery("user_account","*"+model.getUserAccount()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getSip())){
            query.must(QueryBuilders.termQuery("resource_ip", model.getSip()));
        }
        if(StringUtils.isNotEmpty(model.getProtocol())){
            query.must(QueryBuilders.termQuery("operation_protocol", model.getProtocol()));
        }
        if(StringUtils.isNotEmpty(model.getPort())){
            query.must(QueryBuilders.termQuery("operation_port", model.getPort()));
        }
        if(StringUtils.isNotEmpty(model.getUrl())){
            query.must(QueryBuilders.wildcardQuery("operation_url_list","*"+model.getUrl()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getRecord())){
            query.must(QueryBuilders.wildcardQuery("operation_record","*"+model.getRecord()+"*"));
        }
        queryModel.setQueryBuilder(query);
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
        BoolQueryBuilder lastQuery = new BoolQueryBuilder();
        StringBuffer sessions = new StringBuffer();
        for(Map<String, String> m: allDate){
            sessions.append(m.get("session_id")).append(",");
        }
        lastQuery.must(QueryBuilders.termsQuery("session_id", sessions.toString().split(",")));
        lastQuery.must(QueryBuilders.termQuery("resource_opt", "2"));
        queryModel.setQueryBuilder(lastQuery);
        SearchResponse searchResponse2 = null;
        List<Map<String, String>> allDate2 = new ArrayList<>();
        while (true) {
            searchResponse2 = wrapper.scrollQuery(queryModel, searchResponse2 == null ? null : searchResponse2.getScrollId());
            SearchHits hits = searchResponse2.getHits();
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse2.getHits(), "event_time");
            if (hits.getHits() == null || hits.getHits().length == 0) {
                break;
            }
            allDate2.addAll(list);
        }
        if(allDate2.size() > 0){
            Map<String, String> logoutMap = allDate2.stream().collect(Collectors.toMap(r -> r.get("session_id"), r -> r.get("event_time")));
            for(Map<String, String> m: allDate){
                m.put("logout_time",logoutMap.get(m.get("session_id")));
            }
        }

        final long totalSize = allDate.size();
        List<ExcelData> list = new ArrayList<>();
        ExcelInfo info = new ExcelInfo(
                ExcelEnum.APP_NETWORK_DETAIL,
                PathTools.getExcelPath(ExcelEnum.APP_NETWORK_DETAIL.getFilename()));
        ExcelData data = new ExcelData(info, totalSize, new ArrayList<>());
        list.add(data);
        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = new ArrayList<>();
            Object tmp2;
            for (String filedName : ExcelEnum.APP_NETWORK_DETAIL.getFields()) {
                tmp2 = map.get(filedName);
                innerList.add(null == tmp2 ? "" : tmp2.toString());
            }
            return innerList.toArray(new String[0]);
        }).start(
                WriteHandler.fun(p -> {
                    final int batch = 1000;
                    Lists.partition(allDate,batch).forEach(l ->{
                        try{
                            p.writeBatchBean(0, l);
                        }catch (Exception e){
                            log.error(e.getMessage(),e);
                        }

                    });
                }, redisTemplate)));
    }

    @Override
    public VData<List<Map<String, Object>>> fileRelationGap(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-app-file", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDevTypeGroup()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.twoLevelAgg(queryModel, wrapper, "dip", "file_dir", 1000, 10, "count", true);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> fileLevelCount(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-app-file", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getFileDir())){
            query.must(QueryBuilders.termQuery("file_dir", model.getFileDir()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"classification_level",100,"level","count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> fileTypeCount(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-app-file", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getFileDir())){
            query.must(QueryBuilders.termQuery("file_dir", model.getFileDir()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"file_type",100,"fileType","count");
        return VoBuilder.vd(list);
    }

    @Override
    public VList<Map<String, String>> fileDetail(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-app-file", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getFileDir())){
            query.must(QueryBuilders.termQuery("file_dir", model.getFileDir()));
        }
        if(StringUtils.isNotEmpty(model.getFileType())){
            query.must(QueryBuilders.termQuery("file_type", model.getFileType()));
        }
        if(StringUtils.isNotEmpty(model.getFileLevel())){
            query.must(QueryBuilders.termQuery("classification_level", model.getFileLevel()));
        }
        if(StringUtils.isNotEmpty(model.getDip())){
            query.must(QueryBuilders.termQuery("dst_std_dev_ip", model.getDip()));
        }
        if(StringUtils.isNotEmpty(model.getFileInfo())){
            BoolQueryBuilder sdquery = new BoolQueryBuilder();
            sdquery.should(QueryBuilders.wildcardQuery("file_name","*"+model.getFileInfo()+"*"));
            sdquery.should(QueryBuilders.wildcardQuery("file_hash","*"+model.getFileInfo()+"*"));
            query.must(sdquery);
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        long total = 0;
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            total = searchResponse.getHits().getTotalHits().value;
            return VoBuilder.vl(total,list);
        }
        return VoBuilder.vl(total, new ArrayList<>());
    }

    @Override
    public VData<Export.Progress> fileDetailExport(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-app-file", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getFileDir())){
            query.must(QueryBuilders.termQuery("file_dir", model.getFileDir()));
        }
        if(StringUtils.isNotEmpty(model.getFileType())){
            query.must(QueryBuilders.termQuery("file_type", model.getFileType()));
        }
        if(StringUtils.isNotEmpty(model.getFileLevel())){
            query.must(QueryBuilders.termQuery("classification_level", model.getFileLevel()));
        }
        if(StringUtils.isNotEmpty(model.getDip())){
            query.must(QueryBuilders.termQuery("dst_std_dev_ip", model.getDip()));
        }
        if(StringUtils.isNotEmpty(model.getFileInfo())){
            BoolQueryBuilder sdquery = new BoolQueryBuilder();
            sdquery.should(QueryBuilders.wildcardQuery("file_name","*"+model.getFileInfo()+"*"));
            sdquery.should(QueryBuilders.wildcardQuery("file_hash","*"+model.getFileInfo()+"*"));
            query.must(sdquery);
        }
        queryModel.setQueryBuilder(query);
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
        final long totalSize = allDate.size();
        List<ExcelData> list = new ArrayList<>();
        ExcelInfo info = new ExcelInfo(
                ExcelEnum.APP_FILE_DETAIL,
                PathTools.getExcelPath(ExcelEnum.APP_FILE_DETAIL.getFilename()));
        ExcelData data = new ExcelData(info, totalSize, new ArrayList<>());
        list.add(data);
        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = new ArrayList<>();
            Object tmp2;
            for (String filedName : ExcelEnum.APP_FILE_DETAIL.getFields()) {
                Object cellValue = map.get(filedName);
                if (filedName.equals("file_dir")) {
                    innerList.add(cellValue == null ? "" : DictTools.translate("68ccdf6f-89ef-4528-a973-3ca4fb4509c3", cellValue.toString()));
                } else {
                    tmp2 = cellValue;
                    innerList.add(null == tmp2 ? "" : tmp2.toString());
                }
            }
            return innerList.toArray(new String[0]);
        }).start(
                WriteHandler.fun(p -> {
                    final int batch = 1000;
                    Lists.partition(allDate,batch).forEach(l ->{
                        try{
                            p.writeBatchBean(0, l);
                        }catch (Exception e){
                            log.error(e.getMessage(),e);
                        }
                    });
                }, redisTemplate)));
    }

    @Override
    public VData<List<Map<String, Object>>> interactiveRelationGap(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDevTypeGroup()));
        }
        List<Map<String, Object>> list = QueryTools.simpleAggAndTopHit(queryModel, wrapper, "dip", 5000, "ip", "count", "username".split(","));
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> interactiveProtocol(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDevTypeGroup()));
        }
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"app_protocol",100,"protocol","count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> netflowBytesCount(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDevTypeGroup()));
        }
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "dip",
                10, "total_byte", "ip", "count", null);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> interactiveTrend(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDevTypeGroup()));
        }
        String inter = "1h";
        String tfm = "yyyy-MM-dd HH";
        switch (model.getInterval()) {
            case "2":
                inter = "1d";
                tfm = "yyyy-MM-dd";
                break;
            case "3":
                inter = "1M";
                tfm = "yyyy-MM";
                break;
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time",
                new DateHistogramInterval(inter), tfm, 8, "date", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VList<Map<String, String>> interactiveDetail(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDevTypeGroup()));
        }
        if(StringUtils.isNotEmpty(model.getDip())){
            query.must(QueryBuilders.termQuery("dip", model.getDip()));
        }
        if(StringUtils.isNotEmpty(model.getDport())){
            query.must(QueryBuilders.termQuery("dport", model.getDport()));
        }
        if(StringUtils.isNotEmpty(model.getProtoclol())){
            query.must(QueryBuilders.termQuery("app_protocol", model.getProtoclol()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        long total = 0;
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            total = searchResponse.getHits().getTotalHits().value;
            return VoBuilder.vl(total,list);
        }
        return VoBuilder.vl(total, new ArrayList<>());
    }

    @Override
    public VData<Export.Progress> interactiveDetailExport(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        if(StringUtils.isNotEmpty(model.getDevTypeGroup())){
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDevTypeGroup()));
        }
        if(StringUtils.isNotEmpty(model.getDip())){
            query.must(QueryBuilders.termQuery("dip", model.getDip()));
        }
        if(StringUtils.isNotEmpty(model.getDport())){
            query.must(QueryBuilders.termQuery("dport", model.getDport()));
        }
        if(StringUtils.isNotEmpty(model.getProtoclol())){
            query.must(QueryBuilders.termQuery("app_protocol", model.getProtoclol()));
        }
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
        final long totalSize = allDate.size();
        List<ExcelData> list = new ArrayList<>();
        ExcelInfo info = new ExcelInfo(
                ExcelEnum.INTETIVE_DETAIL,
                PathTools.getExcelPath(ExcelEnum.INTETIVE_DETAIL.getFilename()));
        ExcelData data = new ExcelData(info, totalSize, new ArrayList<>());
        list.add(data);
        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = new ArrayList<>();
            Object tmp2;
            for (String filedName : ExcelEnum.INTETIVE_DETAIL.getFields()) {
                Object cellValue = map.get(filedName);
                if (filedName.equals("app_protocol")) {
                    innerList.add(cellValue == null ? "" : DictTools.translate("68ccdf6f-89ef-4528-a973-3ca4fb4509c6", cellValue.toString()));
                } else {
                    tmp2 = cellValue;
                    innerList.add(null == tmp2 ? "" : tmp2.toString());
                }
            }
            return innerList.toArray(new String[0]);
        }).start(
                WriteHandler.fun(p -> {
                    final int batch = 1000;
                    Lists.partition(allDate,batch).forEach(l ->{
                        try{
                            p.writeBatchBean(0, l);
                        }catch (Exception e){
                            log.error(e.getMessage(),e);
                        }
                    });
                }, redisTemplate)));
    }

    @Override
    public VData<List<Map<String, Object>>> visitRelationGap(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-http", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.simpleAggAndTopHit(queryModel, wrapper, "dip", 5000, "ip", "count", "username".split(","));
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> visitTrend(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-http", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        String inter = "1h";
        String tfm = "yyyy-MM-dd HH";
        switch (model.getInterval()) {
            case "2":
                inter = "1d";
                tfm = "yyyy-MM-dd";
                break;
            case "3":
                inter = "1M";
                tfm = "yyyy-MM";
                break;
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time", new DateHistogramInterval(inter), tfm, 8, "date", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VList<Map<String, Object>> visitDetail(SysRelationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-http", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.simpleAggWithMaxMinWithTopHit(queryModel, wrapper, "sess_id", "event_time", 10000, "sessId", "count", "username,dip".split(","));
        List<Map<String, Object>> pageList = ListPageTools.pageList(list, model.getMyStart(), model.getMyCount());
        for(Map<String, Object> m : pageList){
            long maxTime = ((Double)m.get("max_event_time")).longValue();
            long minTime = ((Double)m.get("min_event_time")).longValue();
            String time = DateTools.minseconds2Date(minTime, null);
            m.put("time",time);
            //计算时间差
            m.put("duration",DateTools.formatTime(maxTime-minTime));
        }
        return VoBuilder.vl(list.size(),pageList);
    }

}
