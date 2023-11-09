package com.vrv.vap.xc.service.impl;

import com.github.xtool.collect.Lists;
import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.ExcelInfo;
import com.vrv.vap.toolkit.excel.out.ExcelData;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.excel.out.WriteHandler;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.tools.PathTools;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.PrintStatisticsService;
import com.vrv.vap.xc.tools.DictTools;
import com.vrv.vap.xc.tools.ExcelValHandleTools;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.SumAggregationBuilder;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class PrintStatisticsServiceImpl implements PrintStatisticsService {
    private static final Log log = LogFactory.getLog(PrintStatisticsServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;

    private String PRINT_INDEX = "print-audit";

    public EsQueryModel commonBuildModel(QueryTools.QueryWrapper wrapper,PageModel model){
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", "0"));
        queryModel.setQueryBuilder(query);
        return queryModel;
    }

    /**
     * 按时间统计打印数量
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> printNumByTime(PrintTimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "yyyy-MM-dd HH";
        }else if("3".equals(model.getInteval())){
            inter = "1M";
            tfm = "yyyy-MM";
        }
        //QueryTools.dateAndSumAgg(queryModel,wrapper,"event_time","file_num",new DateHistogramInterval(inter), tfm, 8,)
        List<Map<String, Object>> list = sumAggDate(queryModel, wrapper, "file_num", "event_time", "date", new DateHistogramInterval(inter), tfm, 8, "count");
        //List<Map<String,Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time", new DateHistogramInterval(inter), tfm, 8, "date", "count");
        return VoBuilder.vd(list);
    }

    /**
     * 按打印数量统计用户排行
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> printOrderByUser(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "std_user_no", 10, "file_num", "userNo", "count", new String[]{"username"});
        //List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count",new String[]{"username"},new String[]{"username"});
        return VoBuilder.vd(list);
    }

    /**
     * 按部门统计打印数量
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> printOrderByOrg(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "std_org_code", 10, "file_num", "orgCode", "count", new String[]{"std_org_name"});
        return VoBuilder.vd(list);
    }

    /**
     * 按设备统计打印数量
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> printOrderByDev(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "dev_ip", 10, "file_num", "devIp", "count", new String[]{"dev_name"});
        //List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "dev_ip", 10, "devIp", "count",new String[]{"dev_name"},new String[]{"devName"});
        return VoBuilder.vd(list);
    }

    /**
     * 按时间统计打印设备数量
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> printDevByTime(PrintTimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "yyyy-MM-dd HH";
        }else if("3".equals(model.getInteval())){
            inter = "1M";
            tfm = "yyyy-MM";
        }
        //List<Map<String, Object>> list = aggAndDate(queryModel, wrapper, "dev_name", "event_time", "date", 10, new DateHistogramInterval(inter), tfm, 8, "count");
        List<Map<String, Object>> list = QueryTools.simpleDateTermAndSumAgg(queryModel, wrapper, "event_time", "date", new DateHistogramInterval(inter), 8, tfm, "dev_name", 100, "file_num", "devName", "count", null);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printUserNonWorkTime(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        buildTimeQuery(queryModel);
        List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count",new String[]{"username"},new String[]{"username"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printFileType(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "file_type", 100, "fileType", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printFileDoc(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", "0"));
        query.must(QueryBuilders.termQuery("file_type", "doc"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count",new String[]{"username"},new String[]{"username"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printFilePdf(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", "0"));
        query.must(QueryBuilders.termQuery("file_type", "pdf"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count",new String[]{"username"},new String[]{"username"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printFileTypeTotal(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = twoLevelAggToHitsAndOrder(queryModel, wrapper, "std_user_no", "file_type", 10, 100, "count", new String[]{"username"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printFileTypeCountTrend(PrintTimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "yyyy-MM-dd HH";
        }else if("3".equals(model.getInteval())){
            inter = "1M";
            tfm = "yyyy-MM";
        }
        List<Map<String, Object>> list = aggAndDate(queryModel, wrapper, "file_type", "event_time", "date", 10, new DateHistogramInterval(inter), tfm, 8, "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printFileLevel(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "file_level", 100, "fileLevel", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printCountByLevel(FileLevelModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", "0"));
        if(StringUtils.isNotEmpty(model.getLevel())){
            query.must(QueryBuilders.termQuery("file_level", model.getLevel()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count",new String[]{"username"},new String[]{"username"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printCountByLevelTrend(PrintTimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "yyyy-MM-dd HH";
        }else if("3".equals(model.getInteval())){
            inter = "1M";
            tfm = "yyyy-MM";
        }
        List<Map<String, Object>> list = aggAndDate(queryModel, wrapper, "file_level", "event_time", "date", 10, new DateHistogramInterval(inter), tfm, 8, "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printLevelByUser(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = twoLevelAggToHitsAndOrder(queryModel, wrapper, "std_user_no", "file_level", 10, 100, "count", new String[]{"username"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printCountByTime(PrintTimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "yyyy-MM-dd HH";
        }else if("3".equals(model.getInteval())){
            inter = "1M";
            tfm = "yyyy-MM";
        }
        List<Map<String,Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time", new DateHistogramInterval(inter), tfm, 8, "date", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printCountByUser(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count",new String[]{"username"},new String[]{"username"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printCountByOrg(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_org_code", 10, "orgCode", "count",new String[]{"std_org_name"},new String[]{"orgName"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printResultInfo(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel,wrapper,"op_result",10,"result","count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printResultUser(PrintResultModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", "0"));
        if(StringUtils.isNotEmpty(model.getResult())){
            query.must(QueryBuilders.termQuery("op_result", model.getResult()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count",new String[]{"username"},new String[]{"username"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printResultTrend(PrintTimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "yyyy-MM-dd HH";
        }else if("3".equals(model.getInteval())){
            inter = "1M";
            tfm = "yyyy-MM";
        }
        List<Map<String, Object>> list = aggAndDate(queryModel, wrapper, "op_result", "event_time", "date", 10, new DateHistogramInterval(inter), tfm, 8, "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printResultByOrg(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = twoLevelAggToHitsAndOrder(queryModel, wrapper, "std_org_code", "op_result", 100, 100, "count", new String[]{"std_org_name"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printOrburnTrend(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", model.getOpType()));
        queryModel.setQueryBuilder(query);
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "yyyy-MM-dd HH";
        }else if("3".equals(model.getInteval())){
            inter = "1M";
            tfm = "yyyy-MM";
        }
        List<Map<String, Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time", new DateHistogramInterval(inter), tfm, 8, "date", "count");
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String,Object>>> printCountByHour(PrintTimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        String inter = "1d";
        String tfm = "yyyy-MM-dd";
        if("1".equals(model.getInteval())){
            inter = "1h";
            tfm = "yyyy-MM-dd HH";
        }else if("3".equals(model.getInteval())){
            inter = "1M";
            tfm = "yyyy-MM";
        }
        List<Map<String, Object>> list = aggAndDate(queryModel, wrapper, "op_hour", "event_time", "date", 100, new DateHistogramInterval(inter), tfm, 8, "count");
        LinkedHashMap<String,Map<String,Integer>> result = new LinkedHashMap<>();
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach( l ->{
                String date = l.get("date").toString();
                int opHour = Integer.parseInt(l.get("opHour").toString());
                int count = Integer.parseInt(l.get("count").toString());
                String key = "其他";
                if(8 <= opHour && opHour < 10){
                    key = "8:00-10:00";
                }else if(opHour >= 10 && opHour < 12){
                    key = "10:00-12:00";
                }else if(opHour >= 12 && opHour < 14){
                    key = "12:00-14:00";
                }else if(opHour >= 14 && opHour < 16){
                    key = "14:00-16:00";
                }else if(opHour >= 16 && opHour < 18){
                    key = "16:00-18:00";
                }
                if(result.containsKey(date)){
                    Map<String, Integer> dateMap = result.get(date);
                    if(dateMap.containsKey(key)){
                        dateMap.put(key,dateMap.get(key)+count);
                    }else{
                        dateMap.put(key,count);
                    }
                }else{
                    Map<String, Integer> dateMap = new HashMap<>();
                    dateMap.put(key,count);
                    result.put(date,dateMap);
                }
            });
        }
        List<Map<String,Object>> list1 = new ArrayList<>();
        for(Map.Entry<String, Map<String, Integer>> en : result.entrySet()){
            Map<String,Object> d = new HashMap<>();
            d.put("date",en.getKey());
            d.put("value",en.getValue());
            list1.add(d);
        }
        return VoBuilder.vd(list1);
    }

    @Override
    public VData<List<Map<String, Object>>> printFileSizeInfo(PageModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "file_name", 100000, "file_num", "fileName", "count", new String[]{"file_size"});
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printFileSize(PrintSizeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        long maxSize = 0;
        long minSize = 0;
        List<Map<String, Object>> result = new ArrayList<>();
        //查询最大文件
        model.setOrder("file_size");
        model.setBy("desc");
        model.setMyCount(1);
        EsQueryModel maxModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        SearchResponse searchResponse = wrapper.getSearchResponse(maxModel);
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            if(CollectionUtils.isNotEmpty(list)){
                maxSize = Long.parseLong(list.get(0).get("file_size").toString());
            }
        }
        //查询最小文件
        model.setBy("asc");
        EsQueryModel minModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        SearchResponse searchResponse2 = wrapper.getSearchResponse(minModel);
        if (searchResponse2 != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse2.getHits());
            if(CollectionUtils.isNotEmpty(list)){
                minSize = Long.parseLong(list.get(0).get("file_size").toString());
            }
        }
        if(maxSize == 0){
            return VoBuilder.vd(result);
        }
        long perd = (maxSize-minSize)/3;
        if(perd == 0){
            Map<String,Object> map = new HashMap<>();
            map.put("key",maxSize);
            map.put("value",searchResponse2.getHits().getTotalHits().value);
            result.add(map);
            return VoBuilder.vd(result);
        }
        long[] array = new long[]{minSize,minSize+perd,minSize+perd*2,maxSize};
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        for(int i = 0 ; i<3 ;i++){
            BoolQueryBuilder query = new BoolQueryBuilder();
            query.must(QueryBuilders.termQuery("op_type", "0"));
            long min = array[i];
            long max = array[i+1];
            query.must(QueryBuilders.rangeQuery("file_size").gte(min).lt(max));
            queryModel.setQueryBuilder(query);
            SearchResponse response = wrapper.getSearchResponse(queryModel);
            long total = 0;
            if (response != null && response.getHits() != null) {
                total = response.getHits().getTotalHits().value;
            }
            Map<String,Object> map = new HashMap<>();
            map.put("key",min+"-"+max);
            map.put("value",total);
            result.add(map);
        }
        return VoBuilder.vd(result);
    }

    @Override
    public VList<Map<String, String>> printDetail(PrintDetailModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getFileName())){
            query.must(QueryBuilders.wildcardQuery("file_name","*"+model.getFileName()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getUsername())){
            query.must(QueryBuilders.wildcardQuery("username","*"+model.getUsername()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getDevName())){
            query.must(QueryBuilders.wildcardQuery("dev_name","*"+model.getDevName()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("dev_ip",model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getFileType())){
            query.must(QueryBuilders.termQuery("file_type",model.getFileType()));
        }
        if(StringUtils.isNotEmpty(model.getFileLevel())){
            query.must(QueryBuilders.termQuery("file_level",model.getFileLevel()));
        }
        if(StringUtils.isNotEmpty(model.getFileSzie())){
            String[] split = model.getFileSzie().split(",");
            long min = Long.parseLong(split[0]);
            long max = Long.parseLong(split[1]);
            query.must(QueryBuilders.rangeQuery("file_size").gte(min).lte(max));
        }
        if(StringUtils.isNotEmpty(model.getBusiness())){
            query.must(QueryBuilders.termQuery("business_list",model.getBusiness()));
        }
        if(StringUtils.isNotEmpty(model.getFileNum())){
            String[] split = model.getFileNum().split(",");
            long min = Long.parseLong(split[0]);
            long max = Long.parseLong(split[1]);
            query.must(QueryBuilders.rangeQuery("file_num").gte(min).lte(max));
        }
        if(StringUtils.isNotEmpty(model.getTerminalType())){
            query.must(QueryBuilders.termQuery("std_terminal_type",model.getTerminalType()));
        }
        if(StringUtils.isNotEmpty(model.getUserNo())){
            query.must(QueryBuilders.termQuery("std_user_no",model.getUserNo()));
        }
        if(StringUtils.isNotEmpty(model.getOrgCode())){
            query.must(QueryBuilders.termQuery("std_org_code",model.getOrgCode()));
        }
        if(StringUtils.isNotEmpty(model.getOpResult())){
            query.must(QueryBuilders.termQuery("op_result",model.getOpResult()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), "event_time");
            long total = searchResponse.getHits().getTotalHits().value;
            return VoBuilder.vl(total, list);
        }
        return VoBuilder.vl(0, new ArrayList<>());
    }

    @Override
    public VData<Export.Progress> exportDetail(PrintDetailModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, PRINT_INDEX, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getFileName())){
            query.must(QueryBuilders.wildcardQuery("file_name","*"+model.getFileName()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getUsername())){
            query.must(QueryBuilders.wildcardQuery("username","*"+model.getUsername()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getDevName())){
            query.must(QueryBuilders.wildcardQuery("dev_name","*"+model.getDevName()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("dev_ip",model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getFileType())){
            query.must(QueryBuilders.termQuery("file_type",model.getFileType()));
        }
        if(StringUtils.isNotEmpty(model.getFileLevel())){
            query.must(QueryBuilders.termQuery("file_level",model.getFileLevel()));
        }
        if(StringUtils.isNotEmpty(model.getFileSzie())){
            String[] split = model.getFileSzie().split(",");
            long min = Long.parseLong(split[0]);
            long max = Long.parseLong(split[1]);
            query.must(QueryBuilders.rangeQuery("file_size").gte(min).lte(max));
        }
        if(StringUtils.isNotEmpty(model.getBusiness())){
            query.must(QueryBuilders.termQuery("business_list",model.getBusiness()));
        }
        if(StringUtils.isNotEmpty(model.getFileNum())){
            String[] split = model.getFileNum().split(",");
            long min = Long.parseLong(split[0]);
            long max = Long.parseLong(split[1]);
            query.must(QueryBuilders.rangeQuery("file_num").gte(min).lte(max));
        }
        if(StringUtils.isNotEmpty(model.getTerminalType())){
            query.must(QueryBuilders.termQuery("std_terminal_type",model.getTerminalType()));
        }
        if(StringUtils.isNotEmpty(model.getUserNo())){
            query.must(QueryBuilders.termQuery("std_user_no",model.getUserNo()));
        }
        if(StringUtils.isNotEmpty(model.getOrgCode())){
            query.must(QueryBuilders.termQuery("std_org_code",model.getOrgCode()));
        }
        if(StringUtils.isNotEmpty(model.getOpResult())){
            query.must(QueryBuilders.termQuery("op_result",model.getOpResult()));
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
                ExcelEnum.PRINT_DETAIL,
                PathTools.getExcelPath(ExcelEnum.PRINT_DETAIL.getFilename()));
        ExcelData data = new ExcelData(info, totalSize, new ArrayList<>());
        list.add(data);
        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = new ArrayList<>();
            Object tmp2 = null;
            ExcelValHandleTools ect = new ExcelValHandleTools();
            for (String filedName : ExcelEnum.PRINT_DETAIL.getFields()) {
                Object cellValue = map.get(filedName);
                switch (filedName) {
                    case "file_level":
                        innerList.add(cellValue == null ? "" : DictTools.translate("f5a4ae5b-3cee-a84f-7471-8f23ezjg0500",cellValue.toString()));
                        break;
                    case "data_source":
                        innerList.add(cellValue == null ? "" : "0".equals(cellValue.toString()) ? "主审" : "打印刻录");
                        break;
                    case "op_type":
                        innerList.add(cellValue == null ? "" : DictTools.translate("199424e5-0631-c06e-89c9-c1f33aa7a510",cellValue.toString()));
                        break;
                    case "op_result":
                        innerList.add(cellValue == null ? "" : DictTools.translate("f18ce10c-4ddf-867b-df2d-94b278d0000e",cellValue.toString()));
                        break;
                    case "std_user_type":
                        innerList.add(cellValue == null ? "" : DictTools.translate("f5a4ae5b-3cee-a84f-7471-8f23ezjg0200",cellValue.toString()));
                        break;
                    case "std_dev_type_group":
                        innerList.add(cellValue == null ? "" : DictTools.translate("467d3000-3bc8-9129-9356-3e9fb82ab6c5",cellValue.toString()));
                        break;
                    case "std_dev_safety_marign":
                        innerList.add(cellValue == null ? "" : DictTools.translate("19abd31b-0d0c-47af-b80d-4ce4074ebf57",cellValue.toString()));
                        break;
                    case "std_terminal_type":
                        innerList.add(cellValue == null ? "" : DictTools.translate("3dd5a42b-a1a3-53d8-113a-2232668ca8d9",cellValue.toString()));
                        break;

                    default:
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


    public static List<Map<String, Object>> simpleAggWithTopHitAndOrder(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField, String[] rowFields,String[] keys) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.subAggregation(AggregationBuilders.topHits("data").size(1));
        agg.order(BucketOrder.count(false));
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        if (rowFields != null && rowFields.length > 0) {
                            for (int i = 0;i<rowFields.length;i++) {
                                String fd = rowFields[i];
                                String k = keys[i];
                                tmp.put(k, row.get(fd));
                            }
                        }
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }




    public static List<Map<String, Object>> simpleAggAndOrder(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String keyFieldRe, String valueFieldRe) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.order(BucketOrder.count(false));
        queryModel.setAggregationBuilder(agg);
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("agg")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("agg");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    aggItems.forEach(aggItem -> {
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyFieldRe, aggItem.get("key"));
                        tmp.put(valueFieldRe, aggItem.get("doc_count"));
                        result.add(tmp);
                    });
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> aggAndDate(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, String dateField,String dateFieldKey, int oneAggSize, DateHistogramInterval interval, String dateFormat, int offset,String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(aggField).size(oneAggSize);
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.offset(offset);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(dateFormat);
        queryModel.setAggregationBuilder(dateAgg.subAggregation(oneAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("oneAgg")) {
                            Map<String, Object> twoAggMap = (Map<String, Object>) aggItem.get("oneAgg");
                            if (twoAggMap.containsKey("buckets")) {
                                List<Map<String, Object>> twoAggItems = (List<Map<String, Object>>) twoAggMap.get("buckets");
                                twoAggItems.forEach(item -> {
                                    Map<String, Object> tmp = new HashMap<>();
                                    tmp.put(CommonTools.underLineToCamel(aggField), item.get("key"));
                                    tmp.put(dateFieldKey, aggItem.get("key_as_string"));
                                    tmp.put(valueField, item.get("doc_count"));
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

    /**
     * 构建非工作时间条件
     * @param queryModel
     */
    private void buildTimeQuery(EsQueryModel queryModel) {
        BoolQueryBuilder query = QueryBuilders.boolQuery();
        String startTime = "09:00:00";
        String endTime = "18:00:00";
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(queryModel.getStartTime());
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(queryModel.getEndTime());
        while (cal1.before(cal2)) {
            String startDate = TimeTools.formatDateFmt(cal1.getTime());
            cal1.add(Calendar.DATE, 1);
            String secondDate = TimeTools.formatDateFmt(cal1.getTime());
            query.should(QueryBuilders.rangeQuery(queryModel.getTimeField()).from(TimeTools.parseDate2(startDate + " " + endTime))
                .to(TimeTools.parseDate2(secondDate + " " + startTime)));
        }
        queryModel.setQueryBuilder(query);
        queryModel.setUseTimeRange(false);
    }

    public static List<Map<String, Object>> twoLevelAggToHitsAndOrder(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, int oneAggSize, int secondAggSize, String valueField, String[] rowFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(oneAggSize);
        oneAgg.order(BucketOrder.count(false));
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(secondAggSize);
        secondAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        queryModel.setAggregationBuilder(oneAgg.subAggregation(secondAgg));
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

    public static List<Map<String, Object>> sumAggDate(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String sumField, String dateField,String dateFieldKey, DateHistogramInterval interval, String dateFormat, int offset,String valueField) {
        List<Map<String, Object>> result = new ArrayList<>();
        SumAggregationBuilder sumAgg = AggregationBuilders.sum("sumAgg").field(sumField);
        DateHistogramAggregationBuilder dateAgg = AggregationBuilders.dateHistogram("dateAgg");
        dateAgg.field(dateField);
        dateAgg.dateHistogramInterval(interval);
        dateAgg.offset(offset);
        dateAgg.timeZone(DateTimeZone.forOffsetHours(offset).toTimeZone().toZoneId());
        dateAgg.minDocCount(0);
        dateAgg.format(dateFormat);
        queryModel.setAggregationBuilder(dateAgg.subAggregation(sumAgg));
        queryModel.setUseAggre(true);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("dateAgg")) {
                Map<String, Object> oneAggMap = (Map<String, Object>) dataAggMap.get("dateAgg");
                if (oneAggMap.containsKey("buckets")) {
                    List<Map<String, Object>> oneAggItems = (List<Map<String, Object>>) oneAggMap.get("buckets");
                    oneAggItems.forEach(aggItem -> {
                        if (aggItem.containsKey("sumAgg")) {
                            Map<String, Object> tmp = new HashMap<>();
                            tmp.put(dateFieldKey, aggItem.get("key_as_string"));
                            tmp.put(valueField, ((Map<String, Double>) aggItem.get("sumAgg")).get("value"));
                            result.add(tmp);
                        }
                    });
                }
            }
        }
        return result;
    }
}
