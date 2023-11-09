package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.AppBusinessHisMapper;
import com.vrv.vap.xc.mapper.core.DevCopyInfoMapper;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.pojo.AppBusinessHis;
import com.vrv.vap.xc.pojo.AppBusinessTotal;
import com.vrv.vap.xc.pojo.DevCopyInfo;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import java.util.*;

/**
 * 设备拷贝数据量统计分析
 * Created by jiangcz on 2021/8/19
 */
public class DevCopyTask extends BaseTask {

    private DevCopyInfoMapper copyInfoMapper = VapXcApplication.getApplicationContext().getBean(DevCopyInfoMapper.class);

    // 索引
    private final static String INDEX = "disk-copy";

    // 批量入库大小
    private final static int BATCH = 500;

    @Override
    void run(String jobName) {
        // 统计前一天设备拷贝数据量
        log.info("task DevCopyTask start");
        Date now = new Date();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, INDEX.split(","), "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = QueryTools.twoLevelAgg(queryModel, wrapper, "ukeyid", "dst_ip", 9999, 9999, "count", null);
        List<DevCopyInfo> list = new ArrayList<>();
        maps.forEach(e ->{
            DevCopyInfo info = new DevCopyInfo();
            info.setUserKey(StringValueOf(e.get("ukeyid")));
            info.setDstIp(StringValueOf(e.get("dst_ip")));
            info.setCount(Integer.parseInt(StringValueOf(e.get("count"))));
            list.add(info);
        });

        if (list != null && list.size() > 0) {
            // 统计结果分批入库
            for (int i = 0; i <= list.size() / BATCH; i++) {
                if (i == list.size() / BATCH) {
                    if (list.subList(i * BATCH, list.size()).size() > 0) {
                        copyInfoMapper.saveBatch4List(list.subList(i * BATCH, list.size()));
                    }
                } else {
                    copyInfoMapper.saveBatch4List(list.subList(i * BATCH, (i + 1) * BATCH));
                }
            }
        }
        log.info("task DevCopyTask end");
    }

    private EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, String[] indexs, String time) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(1));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(1));
        List<String> allIndexList = new ArrayList<>();
        if (indexs.length > 0) {
            for (String index : indexs) {
                allIndexList.addAll(wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime()));
            }
        }
        if (!allIndexList.isEmpty()) {
            queryModel.setIndexNames(allIndexList.toArray(new String[allIndexList.size()]));
        }
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    private String StringValueOf(Object o){
        return o != null ? o.toString() : "";
    }

    private Long parseCount(Object o){
        if(o == null){
            return 0L;
        }
        return Long.parseLong(o.toString());
    }

    public static List<Map<String, Object>> twoLevelAggToHitsAndConcat(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, int oneAggSize, int secondAggSize, String valueField, String concatField,String concatKey,String[] rowFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        secondAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        TermsAggregationBuilder conagg = new TermsAggregationBuilder("conagg");
        conagg.field(concatField).size(secondAggSize);
        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg).subAggregation(conagg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("oneAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("oneAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("secondAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("secondAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>)((Map<String, Object>) item.get("data")).get("hits")).get("hits");
                                    Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(oneAggField, aggItem.get("key"));
                                    tmp.put(twoAggField, item.get("key"));
                                    tmp.put(valueField, item.get("doc_count"));
                                    if (rowFields != null && rowFields.length > 0) {
                                        for (String fd : rowFields) {
                                            tmp.put(fd, row.get(fd));
                                        }
                                    }
                                    StringBuffer concats = new StringBuffer();
                                    if(item.containsKey("conagg")){
                                        Map<String, Object> conaggMap = (Map<String, Object>) item.get("conagg");
                                        if(conaggMap.containsKey("buckets")){
                                            List<Map<String, Object>> detailList = (List<Map<String, Object>>) conaggMap.get("buckets");
                                            if(detailList != null && detailList.size() > 0){
                                                for(int i = 0; i < detailList.size();i++){
                                                    Map<String, Object> detailMap = detailList.get(i);
                                                    if(i == 0){
                                                        concats.append(detailMap.get("key"));
                                                    }else{
                                                        concats.append(",").append(detailMap.get("key"));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    tmp.put(concatKey,concats.toString());
                                    result.add(tmp);
                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

}
