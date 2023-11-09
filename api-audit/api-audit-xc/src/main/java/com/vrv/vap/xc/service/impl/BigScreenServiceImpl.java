package com.vrv.vap.xc.service.impl;


import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.AttackModel;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.model.PrintTimeModel;
import com.vrv.vap.xc.service.BigScreenService;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BigScreenServiceImpl implements BigScreenService {
    @Override
    public VData<Integer> attackIpCount(AttackModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "attack-audit", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getSysId())){
            query.must(QueryBuilders.termQuery("std_sys_id", model.getSysId()));
        }
        queryModel.setQueryBuilder(query);
        queryModel.setCount(0);
        int num = QueryTools.simpleCardinalityAggAndReturnLong(queryModel, wrapper, "attack_ip", 2000);
        return VoBuilder.vd(num);
    }

    @Override
    public VData<List<Map<String, Object>>> attackSysCount(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "attack-audit", "event_time");
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_sys_id", 10, "sysId", "count",new String[]{"std_sys_name"},new String[]{"sysName"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> attackType(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "attack-audit", "event_time");
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "attack_type", 1000, "type", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> attackTrend(PrintTimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "attack-audit", "event_time");
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "HH";
        }
        List<Map<String,Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time", new DateHistogramInterval(inter), tfm, 8, "date", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> userVisitDev(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModelMult(wrapper, model, "netflow-tcp,netflow-udp".split(","), "event_time");
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "dip", 100, "type", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> cpuTrend(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelNoTime(wrapper, model,"monitor-asset-v2-cpu", "event_time","yyyy-MM-dd HH:mm:ss");
        queryModel.setCount(0);
        List<Map<String, Object>> list = QueryTools.simpleTermAndAvgAgg(queryModel, wrapper, "dev_ip", 10000, new String[]{"cpu_use"}, new String[]{"avg"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> memoryTrend(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelNoTime(wrapper, model,"monitor-asset-v2-memory", "event_time","yyyy-MM-dd HH:mm:ss");
        queryModel.setCount(0);
        List<Map<String, Object>> list = QueryTools.simpleTermAndAvgAgg(queryModel, wrapper, "dev_ip", 10000, new String[]{"memory_use"}, new String[]{"avg"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> diskTrend(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelNoTime(wrapper, model,"monitor-asset-v2-disk", "event_time","yyyy-MM-dd HH:mm:ss");
        queryModel.setCount(0);
        List<Map<String, Object>> list = QueryTools.simpleTermAndAvgAgg(queryModel, wrapper, "dev_ip", 10000, new String[]{"disk_used"}, new String[]{"avg"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> visitRank(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModelByNetflow(wrapper, model);
        List<Map<String,Object>> list = QueryTools.simpleAgg(queryModel, wrapper,"dip",100,"ip","count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> attackDistribution(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "attack-audit", "event_time");
        List<Map<String, Object>> list = QueryTools.twoLevelAgg(queryModel, wrapper, "sip", "dip", 1000, 1000, "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> attackMonitor(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        model.setOrder("event_time");
        model.setBy("desc");
        model.setMyCount(100);
        //cpu
        EsQueryModel queryModel = buildQueryModelNoTime(wrapper, model,"monitor-asset-v2-cpu", "event_time","yyyy-MM-dd HH:mm:ss");
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        Map<String,Map<String,Object>> datas = new HashMap<>();
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(e ->{
                    String devId = e.get("dev_id");
                    if(datas.containsKey(devId)){
                        datas.get(devId).put("cpu",e.get("cpu_use"));
                    }else{
                        Map<String,Object> d = new HashMap<>();
                        d.put("cpu",e.get("cpu_use"));
                        datas.put(devId,d);
                    }
                });
            }
        }
        //内存
        EsQueryModel memoryQueryModel = buildQueryModelNoTime(wrapper, model,"monitor-asset-v2-memory", "event_time","yyyy-MM-dd HH:mm:ss");
        SearchResponse memorySearchResponse = wrapper.getSearchResponse(memoryQueryModel);
        if (memorySearchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(memorySearchResponse.getHits());
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(e ->{
                    String devId = e.get("dev_id");
                    if(datas.containsKey(devId)){
                        datas.get(devId).put("memory",e.get("memory_use"));
                    }else{
                        Map<String,Object> d = new HashMap<>();
                        d.put("memory",e.get("memory_use"));
                        datas.put(devId,d);
                    }
                });
            }
        }

        //磁盘
        /*EsQueryModel diskQueryModel = buildQueryModelNoTime(wrapper, model,"monitor-asset-v2-disk", "event_time","yyyy-MM-dd HH:mm:ss");
        SearchResponse diskSearchResponse = wrapper.getSearchResponse(diskQueryModel);
        if (diskSearchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(diskSearchResponse.getHits());
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(e ->{
                    String devId = e.get("dev_id");
                    if(datas.containsKey(devId)){
                        datas.get(devId).put("disk",e.get("disk_used"));
                    }else{
                        Map<String,Object> d = new HashMap<>();
                        d.put("disk",e.get("disk_used"));
                        datas.put(devId,d);
                    }
                });
            }
        }*/

        //monitor-asset-v2-net 字节
        EsQueryModel netqueryModel = buildQueryModelNoTime(wrapper, model,"monitor-asset-v2-net", "event_time","yyyy-MM-dd HH:mm:ss");
        SearchResponse netsearchResponse = wrapper.getSearchResponse(netqueryModel);
        if (netsearchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(netsearchResponse.getHits());
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach( e ->{
                    String devId = e.get("dev_id");
                    if(e.containsKey("net_interface_out_octets")){
                        List out = JSONObject.parseObject(e.get("net_interface_out_octets"), List.class);
                        if(CollectionUtils.isNotEmpty(out)){
                            long total = 0;
                            for(Object o : out){
                                total += Long.parseLong(o.toString());
                            }
                            if(datas.containsKey(devId)){
                                datas.get(devId).put("out",total);
                            }else{
                                Map<String,Object> d = new HashMap<>();
                                d.put("out",total);
                                datas.put(devId,d);
                            }
                        }
                    }
                    if(e.containsKey("net_interface_in_octets")){
                        List out = JSONObject.parseObject(e.get("net_interface_in_octets"), List.class);
                        if(CollectionUtils.isNotEmpty(out)){
                            long total = 0;
                            for(Object o : out){
                                total += Long.parseLong(o.toString());
                            }
                            if(datas.containsKey(devId)){
                                datas.get(devId).put("in",total);
                            }else{
                                Map<String,Object> d = new HashMap<>();
                                d.put("in",total);
                                datas.put(devId,d);
                            }
                        }
                    }

                });
            }
        }

        //状态
        EsQueryModel connectQueryModel = buildQueryModelNoTime(wrapper, model,"monitor-asset-v2-connect", "event_time","yyyy-MM-dd HH:mm:ss");
        SearchResponse connectSearchResponse = wrapper.getSearchResponse(connectQueryModel);
        if (connectSearchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(connectSearchResponse.getHits());
            if(CollectionUtils.isNotEmpty(list)){
                list.forEach(e ->{
                    String devId = e.get("dev_id");
                    if(datas.containsKey(devId)){
                        datas.get(devId).put("status",e.get("reachable"));
                        datas.get(devId).put("devIp",e.get("dev_ip"));
                    }else{
                        Map<String,Object> d = new HashMap<>();
                        d.put("status",e.get("reachable"));
                        d.put("devIp",e.get("dev_ip"));
                        datas.put(devId,d);
                    }
                });
            }
        }
        List<Map<String, Object>> collect = datas.values().stream().filter(f -> f.size() == 6).collect(Collectors.toList());
        return VoBuilder.vd(collect);
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

    public static EsQueryModel buildQueryModelNoTime(QueryTools.QueryWrapper wrapper, PageModel model,String index, String time,String timeFormat) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        }
        queryModel.setIndexName(index);
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setNeedTimeFormat(true);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTimeFormat(timeFormat);
        queryModel.setTypeName(QueryTools.TYPE);
        queryModel.setQueryBuilder(new BoolQueryBuilder());
        if(StringUtils.isNotEmpty(model.getOrder())){
            queryModel.setSort(true);
            queryModel.setSortFields(new String[]{model.getOrder()});
            queryModel.setSortOrder(SortOrder.DESC.toString().equalsIgnoreCase(model.getBy())?SortOrder.DESC:SortOrder.ASC);
        }
        return queryModel;
    }
}
