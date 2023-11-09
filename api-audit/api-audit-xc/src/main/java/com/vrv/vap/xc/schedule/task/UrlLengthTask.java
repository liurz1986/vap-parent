package com.vrv.vap.xc.schedule.task;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.mapper.core.DevCopyInfoMapper;
import com.vrv.vap.xc.mapper.core.UrlParamLengthMapper;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.pojo.DevCopyInfo;
import com.vrv.vap.xc.pojo.UrlParamLength;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 参数平均长度统计分析
 * Created by jiangcz on 2021/8/19
 */
public class UrlLengthTask extends BaseTask {

    private UrlParamLengthMapper urlParamLengthMapper = VapXcApplication.getApplicationContext().getBean(UrlParamLengthMapper.class);

    // 索引
    private final static String[] INDEXES = new String[]{"netflow-http"};

    // 批量入库大小
    private final static int BATCH = 500;

    @Override
    void run(String jobName) {
        log.info("task UrlLengthTask start");
        // 统计前一天参数平均长度
        Date now = new Date();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, INDEXES, "event_time");
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        query.mustNot(QueryBuilders.termQuery("param_length", "0"));
        queryModel.setQueryBuilder(query);
        Map<String, Object> map = QueryTools.simpleAvgAgg(queryModel, wrapper, new String[]{"param_length"}, new String[]{"length"});
        UrlParamLength record = new UrlParamLength();
        record.setTime(new Date());
        record.setDataTime(genDataTime());
        Double length = parseDouble(map.get("length"));
        UrlParamLength preparam = urlParamLengthMapper.selectById(genpreDataTime());
        if(preparam != null){
            Double prelength = preparam.getLength();
            Double avg = (length+prelength)/2;
            String format = String.format("%.2f", avg);
            length = Double.valueOf(format);
        }
        record.setLength(length);
        UrlParamLength oldurl = urlParamLengthMapper.selectById(record.getDataTime());
        if(oldurl != null){
            urlParamLengthMapper.deleteById(record.getDataTime());
        }
        urlParamLengthMapper.insert(record);
        log.info("task UrlLengthTask end");
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

    private Double parseDouble(Object o){
        if(o == null){
            return 0.0;
        }
        return Double.valueOf(o.toString());
    }


    private String genDataTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-1);
        return sdf.format(c.getTime());
    }

    private String genpreDataTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE,-2);
        return sdf.format(c.getTime());
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
