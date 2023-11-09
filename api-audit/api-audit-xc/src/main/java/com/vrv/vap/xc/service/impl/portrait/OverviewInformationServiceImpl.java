package com.vrv.vap.xc.service.impl.portrait;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.fegin.ApiAuditMonitor;
import com.vrv.vap.xc.model.DevModel;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.service.portrait.OverviewInformationService;
import com.vrv.vap.xc.tools.QueryTools;
import com.vrv.vap.xc.vo.Monitor2DataQuery;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class OverviewInformationServiceImpl implements OverviewInformationService {

    @Autowired
    private ApiAuditMonitor apiAuditMonitor;

    @Override
    public VData<List<Map<String, String>>> softwareInstallation(DevModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelNoTime(wrapper, "software-audit-*", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if (StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("dev_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            return VoBuilder.vd(list);
        }
        return VoBuilder.vd(new ArrayList<>());
    }

    @Override
    public VData<List<Map<String, String>>> virusInfection(DevModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelNoTime(wrapper, "net-virus-*", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if (StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("dev_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            return VoBuilder.vd(list);
        }
        return VoBuilder.vd(new ArrayList<>());
    }

    @Override
    public VData<List<Map<String, Object>>> fileLevel(DevModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelNoTime(wrapper, "file-audit-*", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if (StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("dev_ip", model.getDevIp()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "file_level", 100, "fileLevel", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VList<Map<String, String>> fileLevelDetail(ObjectPortraitModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelPage(wrapper, model, "file-audit-*", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if (StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("dev_ip", model.getDevIp()));
        }
        if (StringUtils.isNotEmpty(model.getFileLevel())) {
            query.must(QueryBuilders.termQuery("file_level", model.getFileLevel()));
        }
        queryModel.setQueryBuilder(query);
        return QueryTools.searchResponse(queryModel, wrapper, model);
    }

    @Override
    public VData<Map<String, Object>> cpuInfo(DevModel model) {
        Map<String, Object> data = new HashMap<>();
        Monitor2DataQuery query = new Monitor2DataQuery();
        //查询cpu使用率趋势
        query.setAssetId(model.getDevId());
        query.setMyStartTime(model.getMyStartTime());
        query.setMyEndTime(model.getMyEndTime());
        query.setViewType("trend");
        query.setIndicators("cpu_use");
        VData<Map<String, Object>> mapVData = this.apiAuditMonitor.queryCpuInfo(query);
        //查询cpu使用率 核心数
        query.setIndicators("cpu_use,cpu_count,process_count");
        query.setViewType("val");
        VData<Map<String, Object>> info = this.apiAuditMonitor.queryCpuInfo(query);
        data.put("trend", mapVData.getData());
        data.put("base", info.getData());
        return VoBuilder.vd(data);
    }

    @Override
    public VData<Map<String, Object>> memoryInfo(DevModel model) {
        Map<String, Object> data = new HashMap<>();
        Monitor2DataQuery query = new Monitor2DataQuery();
        //查询内存使用率趋势
        query.setAssetId(model.getDevId());
        query.setMyStartTime(model.getMyStartTime());
        query.setMyEndTime(model.getMyEndTime());
        query.setViewType("trend");
        query.setIndicators("memory_percent");
        VData<Map<String, Object>> trend = this.apiAuditMonitor.queryCpuInfo(query);
        //查询内存使用详情
        query.setViewType("val");
        query.setIndicators("memory_total,memory_use");
        VData<Map<String, Object>> base = this.apiAuditMonitor.queryCpuInfo(query);
        data.put("trend", trend.getData());
        data.put("base", base.getData());
        return VoBuilder.vd(data);
    }

    @Override
    public VData<List<Map<String, Object>>> diskInfo(DevModel model) {
        Monitor2DataQuery query = new Monitor2DataQuery();
        //查询磁盘使用情况
        query.setAssetId(model.getDevId());
        query.setViewType("disk");
        query.setIndicators("disk_detail");
        VData<Map<String, Object>> base = this.apiAuditMonitor.queryCpuInfo(query);
        float total = 0f;
        float used = 0f;
        if (base.getData() != null && base.getData().containsKey("list")) {
            ArrayList<Map<String, Object>> infoDatas = (ArrayList<Map<String, Object>>) base.getData().get("list");
            if (CollectionUtils.isNotEmpty(infoDatas)) {
                HashMap<String, String> item = (HashMap<String, String>) infoDatas.get(0).get("monitor_data");
                if (item != null && item.containsKey("disk_detail")) {
                    List<JSONObject> diskDetail = JSONArray.parseArray(item.get("disk_detail"), JSONObject.class);
                    if (CollectionUtils.isNotEmpty(diskDetail)) {
                        for (JSONObject o : diskDetail) {
                            Float v1 = o.getFloat("total");
                            Float v2 = o.getFloat("used");
                            if (v1 != null && v1 > 0) {
                                total += v1;
                            }
                            if (v2 != null && v2 > 0) {
                                used += v2;
                            }
                        }
                    }
                }
            }
        }
        if (total != 0) {
            List<Map<String, Object>> list = new ArrayList<>();
            Map<String, Object> m1 = new HashMap<>();
            m1.put("name", "已使用");
            m1.put("value", used);
            Map<String, Object> m2 = new HashMap<>();
            m2.put("name", "未使用");
            m2.put("value", total - used);
            list.add(m1);
            list.add(m2);
            return VoBuilder.vd(list);
        }
        return VoBuilder.vd(new ArrayList<>());
    }

    public String nu(String s) {
        return s == null ? "" : s;
    }

    @Override
    public VList<Map<String,String>> trajectoryAnalysis(DevModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelMult(wrapper, model, new String[]{"terminal-login","operation-audit", "print-audit", "netflow-http", "netflow-file", "specialudisk-use"});
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            BoolQueryBuilder sd = QueryBuilders.boolQuery();
            sd.should(QueryBuilders.termQuery("report_dev_ip",model.getDevIp()));
            sd.should(QueryBuilders.termQuery("sip",model.getDevIp()));
            sd.should(QueryBuilders.termQuery("device_ip",model.getDevIp()));
            query.must(sd);
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        long total = 0;
        if (searchResponse != null) {
            List<Map<String, String>> result = new ArrayList<>();
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            for(Map<String, String> m : list){
                Map<String,String> cmap = new HashMap<>();
                String index = m.get("_index");
                String op = "未知";
                String type = "1";
                if(index.contains("terminal-login")){
                    type = "1";
                    op = nu(m.get("username"))+"用户"+("1".equals(m.get("op_type")) ? "登录" : "退出") +nu(m.get("dev_ip"));
                }else if(index.contains("print-audit")){
                    type = "2";
                    op = nu(m.get("dev_ip")) + ("0".equals(m.get("op_type")) ? "打印" : "刻录") + "文件";
                }else if(index.contains("netflow-http")){
                    type = "3";
                    op = nu(m.get("sip"))+ nu(m.get("src_std_dev_type")) + "访问" +nu(m.get("dip"))+ nu(m.get("dst_std_dev_type"));
                }else if(index.contains("netflow-file")){
                    type = "4";
                    op = nu(m.get("sip")) + "从" +nu(m.get("dip")) + ("1".equals(m.get("file_dir")) ? "上传" : "下载") + "文件";
                }else if(index.contains("specialudisk-use")){
                    type = "5";
                    op = nu(m.get("dev_ip")) + "接入" + ("1".equals(m.get("op_code")) ? "红盘" : "通用U盘");
                }else if(index.contains("operation-audit")){
                    type = "6";
                    String opt = m.get("resource_opt");
                    String user = m.get("user_account");
                    switch (opt) {
                        case "0":
                            op = nu(user) + "登录";
                            break;
                        case "1":
                            op = nu(user) + "执行了："+nu(m.get("opt_detail"));
                            break;
                        case "2":
                            op = nu(user) + "登出";
                            break;
                        default:
                    }
                }
                cmap.put("time",m.get("event_time"));
                cmap.put("op",op);
                cmap.put("type",type);
                result.add(cmap);
            }
            total = searchResponse.getHits().getTotalHits().value;
            return VoBuilder.vl(total,result);
        }
        return VoBuilder.vl(total,new ArrayList<>());
    }

    public static EsQueryModel buildQueryModelMult(QueryTools.QueryWrapper wrapper, PageModel model,String[] indexs) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(1000);
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        List<String> indexList = wrapper.getIndexNames(indexs, queryModel.getStartTime(), queryModel.getEndTime());

        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }
        if(StringUtils.isNotEmpty(model.getOrder())){
            queryModel.setSort(true);
            queryModel.setSortFields(new String[]{model.getOrder()});
            queryModel.setSortOrder(SortOrder.DESC.toString().equalsIgnoreCase(model.getBy())?SortOrder.DESC:SortOrder.ASC);
        }
        // 设置时间字段
        queryModel.setTimeField("event_time");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }


    public static EsQueryModel buildQueryModelNoTime(QueryTools.QueryWrapper wrapper, String index, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(0);
        queryModel.setCount(10000);
        queryModel.setIndexName(index);
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public static EsQueryModel buildQueryModelPage(QueryTools.QueryWrapper wrapper, PageModel model, String index, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
            List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
            if (!indexList.isEmpty()) {
                queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
            }
        } else {
            queryModel.setIndexName(index);
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
}
