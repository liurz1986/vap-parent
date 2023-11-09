package com.vrv.vap.line.tools;

import avro.shaded.com.google.common.collect.Lists;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.mapper.JUserLogsMapper;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.mapper.BaseLineFrequentAttrMapper;
import com.vrv.vap.line.mapper.BaseLineFrequentMapper;
import com.vrv.vap.line.mapper.BaseLineScoreMapper;
import com.vrv.vap.line.model.*;
import com.vrv.vap.line.service.BaseLineFrequentAttrService;
import com.vrv.vap.line.service.BaseLineFrequentService;
import com.vrv.vap.line.service.CommonService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class AbnormalLineAnalysis{

    private BaseLineFrequentMapper baseLineFrequentMapper = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentMapper.class);
    private CommonService commonService = VapLineApplication.getApplicationContext().getBean(CommonService.class);
    private BaseLineScoreMapper baseLineScoreMapper = VapLineApplication.getApplicationContext().getBean(BaseLineScoreMapper.class);
    private BaseLineFrequentAttrMapper baseLineFrequentAttrMapper = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentAttrMapper.class);
    private BaseLineFrequentService baseLineFrequentService = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentService.class);
    private BaseLineFrequentAttrService baseLineFrequentAttrService = VapLineApplication.getApplicationContext().getBean(BaseLineFrequentAttrService.class);
    private JUserLogsMapper jUserLogsMapper = VapLineApplication.getApplicationContext().getBean(JUserLogsMapper.class);
    private int timeSplit = 4;//切分时间间隔 单位 s
    private int towLevelTimeSplit = 10;//二级切分时间间隔 单位 min
    private int days = 60;//处理数据天数
    private int BATCH = 1000;//单次入库量
    private String index = "netflow-http-2022";//处理数据天数
    private String timeField = "event_time";//处理数据天数
    private String urlField = "url";//主体字段
    private String separator = ",";//项分隔符
    private String userField = "sip";//项分隔符
    private String pckField = "content_length";//项分隔符
    private Integer count = 10000;//项分隔符
//    private String start = "2022-05-27 00:00:00";
    private String resultIndex = "base-line-sequence";
    private final String indexSufFormat = "-yyyy";
    private String itemSeparator = "#";//项集分隔符
    private static ExecutorService exec = Executors.newFixedThreadPool(1);

    /**
     * 频繁项挖掘
     * @return
     */
    public Map<String,Object> analysis(){
        //System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "200");
        Map<String,Object> result = new HashMap<>();
        List<JUserLogs> uss = new ArrayList<>();
        Date now = new Date();
        long size = 0;
        //读取日志数据
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        //查询用户
        EsQueryModel userModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.mustNot(QueryBuilders.termQuery(urlField,""));
        query.must(QueryBuilders.termQuery("host","192.168.119.209"));
        query.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
        userModel.setQueryBuilder(query);
//        userModel.setStartTime(TimeTools.parseDate2(start));
        List<Map<String, Object>> users = QueryTools.simpleAgg(userModel, wrapper, userField, 10000, "userId", "count");
//        List<Map<String, Object>> users = new ArrayList<>();
//        Map<String,Object> mk = new HashMap<>();
//        mk.put("userId","192.168.119.119");
//        users.add(mk);
        for(Map<String, Object> map : users){
            long userSize = 0;
            JUserLogs udo = new JUserLogs();
            udo.setType("1");
            List<BaseLineFrequentAttr> attrs = new ArrayList<>();
            List<BaseLineFrequent> frequents = new ArrayList<>();
            String userId = map.get("userId") != null ? map.get("userId").toString() : "";
            exec.execute(new ItemRunable(userId,wrapper));
        }
        /*
        if(CollectionUtils.isNotEmpty(uss)){
            jUserLogsMapper.saveBatch4List(uss);
        }*/
        //result.put("logs",uss);
        result.put("total",size);
        return result;
    }

    public String getTime(){
        return TimeTools.format2(new Date());
    }

    public void renderData2(){
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        //查询用户
        List<BaseLineScore> scores = new ArrayList<>();
        EsQueryModel userModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);
        BoolQueryBuilder query1 = new BoolQueryBuilder();
        query1.mustNot(QueryBuilders.termQuery(urlField,""));
        query1.must(QueryBuilders.termQuery("host","192.168.119.209"));
        query1.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
        userModel.setQueryBuilder(query1);
        List<Map<String, Object>> users = QueryTools.simpleAgg(userModel, wrapper, userField, 10000, "userId", "count");
//        List<Map<String, Object>> users = new ArrayList<>();
//        userModel.setStartTime(TimeTools.parseDate2(start));
//        Map<String,Object> mk = new HashMap<>();
//        mk.put("userId","192.168.118.99");
//        users.add(mk);
        for(Map<String, Object> map : users){
            String userId = map.get("userId") != null ? map.get("userId").toString() : "";
            //查询url数据并按时间切分
            List<List<List<Map<String,Object>>>> urls = new ArrayList<>();
            BoolQueryBuilder query = new BoolQueryBuilder();
            query.must(QueryBuilders.termQuery(userField,userId));
            query.mustNot(QueryBuilders.termQuery(urlField,""));
            query.must(QueryBuilders.termQuery("host","192.168.119.209"));
            query.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
            final EsQueryModel queryModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);
            queryModel.setQueryBuilder(query);
            SearchResponse searchResponse = null;
            List<List<Map<String, String>>> towSplitDatas = new ArrayList<>();
            List<Map<String, String>> itemList = new ArrayList<>();
            Date startTime = null;
            Date endTime = null;
            while (true) {
                searchResponse = wrapper.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId());
                SearchHits hits = searchResponse.getHits();
                List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), timeField);
                if (hits.getHits() == null || hits.getHits().length == 0) {
                    break;
                }
                int i = 0;
                for(Map<String, String> mp : list){
                    i++;
                    Date srcdate = TimeTools.parseDate(mp.get(timeField), TimeTools.TIME_FMT_1);
                    //二级分隔
                    if(startTime == null){
                        startTime = srcdate;
                    }
                    if(endTime == null){
                        endTime = addTowLevelTime(startTime);
                    }
                    if(srcdate.before(endTime)){
                        itemList.add(mp);
                    }else{
                        towSplitDatas.add(itemList);
                        itemList = new ArrayList<>();
                        itemList.add(mp);
                        endTime = addTowLevelTime(srcdate);
                    }
                }

            }
            if(CollectionUtils.isNotEmpty(itemList)){
                towSplitDatas.add(itemList);
            }
            for(List<Map<String, String>> list : towSplitDatas){
                Date preDate = null;
                Date nextDate = null;
                List<List<Map<String,Object>>> towLevelList = new ArrayList<>();
                List<Map<String,Object>> tem = new ArrayList<>();
                int i = 0;
                for(Map<String, String> m : list)
                {
                    i++;
                    Date srcdate = dec8H(TimeTools.parseDate(m.get(timeField), TimeTools.TIME_FMT_1));
                    int h = Integer.parseInt(TimeTools.format(srcdate,"HH"));
                    if(preDate == null){
                        preDate = srcdate;
                    }
                    if(nextDate == null){
                        nextDate = addTime(preDate);
                    }
                    if(srcdate.before(nextDate)){
                        Map<String,Object> v = new HashMap<>();
                        v.put(urlField,filterParam(m.get(urlField)));
                        v.put("hour",h);
                        //v.put("time",TimeTools.format2(srcdate));
                        v.put("time",m.get(timeField));
                        tem.add(v);
                    }else{
                        towLevelList.add(tem);
                        tem = new ArrayList<>();
                        Map<String,Object> v = new HashMap<>();
                        v.put(urlField,filterParam(m.get(urlField)));
                        v.put("hour",h);
                        v.put("time",m.get(timeField));
                        tem.add(v);
                        nextDate = addTime(srcdate);
                    }
                    if(i == list.size()){
                        towLevelList.add(tem);
                    }
                }
                if(CollectionUtils.isNotEmpty(towLevelList)){
                    urls.add(towLevelList);
                }
            }/*
            urls.forEach(l ->{
                System.out.println("#####################");
                l.forEach(i ->{
                    i.forEach(u ->{
                        String ul = u.get(urlField).toString();
                        String time = u.get("time").toString();
                        System.out.println(time+">>>"+ul);
                    });
                    System.out.println("");
                });
                System.out.println("#####################");
            });*/


            urls.forEach(l -> {
                if(CollectionUtils.isNotEmpty(l)){
                    BaseLineScore baseLineScore = buildItemData(l, userId);
                    if(baseLineScore != null){
                        scores.add(baseLineScore);
                    }
                }
            });
        }
        Date yesterday = TimeTools.getNowBeforeByDay(1);
        String dataTime = TimeTools.format(yesterday, "yyyy-MM-dd");
        String index = resultIndex + TimeTools.format(yesterday, indexSufFormat);
        /*QueryTools.QueryWrapper rwrapper = QueryTools.build();
        if(!commonService.indexTemplateExists(resultIndex)){
            //创建索引模板
            createEsTem();
        }
        try{
            commonService.create365Alias(resultIndex+TimeTools.format(yesterday, indexSufFormat), resultIndex+"-", "time", TimeTools.TIME_FMT_1, dataTime.substring(0, 4), true);
            QueryTools.writeData(scores, index, rwrapper);
        }catch (Exception e){
            e.printStackTrace();
        }*/
        Lists.partition(scores, BATCH).forEach(l -> baseLineScoreMapper.saveBatch4List(l));
    }

    public void printUrl(){
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        //查询用户
        List<BaseLineScore> scores = new ArrayList<>();
        EsQueryModel userModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);
        BoolQueryBuilder query1 = new BoolQueryBuilder();
        query1.mustNot(QueryBuilders.termQuery(urlField,""));
        query1.must(QueryBuilders.termQuery("host","192.168.119.209"));
        query1.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
        userModel.setQueryBuilder(query1);
//        List<Map<String, Object>> users = QueryTools.simpleAgg(userModel, wrapper, userField, 10000, "userId", "count");
        List<Map<String, Object>> users = new ArrayList<>();
//        userModel.setStartTime(TimeTools.parseDate2(start));
        Map<String,Object> mk = new HashMap<>();
        mk.put("userId","192.168.118.99");
        users.add(mk);
        for(Map<String, Object> map : users) {
            String userId = map.get("userId") != null ? map.get("userId").toString() : "";
            //查询url数据并按时间切分
            List<List<List<Map<String, Object>>>> urls = new ArrayList<>();
            BoolQueryBuilder query = new BoolQueryBuilder();
            query.must(QueryBuilders.termQuery(userField, userId));
            query.mustNot(QueryBuilders.termQuery(urlField, ""));
            query.must(QueryBuilders.termQuery("host", "192.168.119.209"));
            query.must(QueryBuilders.wildcardQuery("url", "/IMS/*"));
            final EsQueryModel queryModel = buildQueryModel2(wrapper, index, timeField, TimeTools.TIME_FMT_1, days);
            queryModel.setQueryBuilder(query);
            SearchResponse searchResponse = null;
            List<List<Map<String, String>>> towSplitDatas = new ArrayList<>();
            List<Map<String, String>> itemList = new ArrayList<>();
            Date startTime = null;
            Date endTime = null;
            while (true) {
                searchResponse = wrapper.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId());
                SearchHits hits = searchResponse.getHits();
                List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), timeField);
                if (hits.getHits() == null || hits.getHits().length == 0) {
                    break;
                }
                int i = 0;
                for (Map<String, String> mp : list) {
                    i++;
                    Date srcdate = TimeTools.parseDate(mp.get(timeField), TimeTools.TIME_FMT_1);
                    //二级分隔
                    if (startTime == null) {
                        startTime = srcdate;
                    }
                    if (endTime == null) {
                        endTime = addTowLevelTime(startTime);
                    }
                    if (srcdate.before(endTime)) {
                        itemList.add(mp);
                    } else {
                        towSplitDatas.add(itemList);
                        itemList = new ArrayList<>();
                        itemList.add(mp);
                        endTime = addTowLevelTime(srcdate);
                    }
                }

            }
            if (CollectionUtils.isNotEmpty(itemList)) {
                towSplitDatas.add(itemList);
            }
            for (List<Map<String, String>> list : towSplitDatas) {
                Date preDate = null;
                Date nextDate = null;
                List<List<Map<String, Object>>> towLevelList = new ArrayList<>();
                List<Map<String, Object>> tem = new ArrayList<>();
                int i = 0;
                for (Map<String, String> m : list) {
                    i++;
                    Date srcdate = dec8H(TimeTools.parseDate(m.get(timeField), TimeTools.TIME_FMT_1));
                    int h = Integer.parseInt(TimeTools.format(srcdate, "HH"));
                    if (preDate == null) {
                        preDate = srcdate;
                    }
                    if (nextDate == null) {
                        nextDate = addTime(preDate);
                    }
                    if (srcdate.before(nextDate)) {
                        Map<String, Object> v = new HashMap<>();
                        v.put(urlField, filterParam(m.get(urlField)));
                        v.put("hour", h);
                        v.put("time", TimeTools.format2(srcdate));
                        tem.add(v);
                    } else {
                        towLevelList.add(tem);
                        tem = new ArrayList<>();
                        Map<String, Object> v = new HashMap<>();
                        v.put(urlField, filterParam(m.get(urlField)));
                        v.put("hour", h);
                        v.put("time", TimeTools.format2(srcdate));
                        tem.add(v);
                        nextDate = addTime(srcdate);
                    }
                    if (i == list.size()) {
                        towLevelList.add(tem);
                    }
                }
                if (CollectionUtils.isNotEmpty(towLevelList)) {
                    urls.add(towLevelList);
                }
            }
            urls.forEach(l -> {
                System.out.println("#####################");
                l.forEach(i -> {
                    i.forEach(u -> {
                        String ul = u.get(urlField).toString();
                        String time = u.get("time").toString();
                        System.out.println(time + ">>>" + ul);
                    });
                    System.out.println("");
                });
                System.out.println("#####################");
            });
        }
    }


    public void createEsTem(){
        List<EsColumns> cols = new ArrayList<>();
        cols.add(new EsColumns("similarityScore","double"));
        cols.add(new EsColumns("frequentId","keyword"));
        cols.add(new EsColumns("hourScore","double"));
        cols.add(new EsColumns("packgeScore","double"));
        cols.add(new EsColumns("compress","keyword"));
        cols.add(new EsColumns("userKey","keyword"));
        cols.add(new EsColumns("time","date"));
        commonService.createTemplate(new EsTemplate(resultIndex, cols));
    }


    public void renderData(){
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        //查询用户
        List<BaseLineScore> scores = new ArrayList<>();
        EsQueryModel userModel = buildQueryModel2(wrapper, index, timeField,TimeTools.TIME_FMT_1,days);
        //List<Map<String, Object>> users = QueryTools.simpleAgg(userModel, wrapper, userField, 10000, "userId", "count");
        List<Map<String, Object>> users = new ArrayList<>();
        //userModel.setStartTime(TimeTools.parseDate2(start));
        Map<String,Object> mk = new HashMap<>();
        mk.put("userId","192.168.119.119");
        users.add(mk);
        for(Map<String, Object> map : users){
            String userId = map.get("userId") != null ? map.get("userId").toString() : "";
            //查询url数据并按时间切分
            List<List<List<Map<String,Object>>>> urls = new ArrayList<>();
            Date startTime = userModel.getStartTime();
            Date endTime = addTowLevelTime(startTime);
            while (endTime.before(new Date())){
                Date preDate = null;
                Date nextDate = null;
                List<List<Map<String,Object>>> towLevelList = new ArrayList<>();
                BoolQueryBuilder query = new BoolQueryBuilder();
                query.must(QueryBuilders.termQuery(userField,userId));
                final EsQueryModel queryModel = buildQueryModel(wrapper, index, timeField, TimeTools.TIME_FMT_1,startTime,endTime);
                queryModel.setQueryBuilder(query);
                /*if(queryModel.getIndexNames().length == 0){
                    continue;
                }*/
                SearchResponse searchResponse = null;
                List<Map<String,Object>> tem = new ArrayList<>();
                startTime = endTime;
                endTime = addTowLevelTime(startTime);
                while (true){
                    searchResponse = wrapper.scrollQuery(queryModel,searchResponse == null ? null : searchResponse.getScrollId());
                    SearchHits hits = searchResponse.getHits();
                    List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), timeField);
                    /*System.out.println("################################list size###########");
                    System.out.println(list.size());
                    System.out.println("################################list size###########");
                    */
                    if (hits.getHits() == null || hits.getHits().length == 0) {
                        break;
                    }
                    if(CollectionUtils.isNotEmpty(list)){
                        int i = 0;
                        for(Map<String, String> m : list)
                        {
                            i++;
                            if(m.containsKey(urlField) && StringUtils.isNotEmpty(m.get(urlField))){
                                Date srcdate = dec8H(TimeTools.parseDate(m.get(timeField), TimeTools.TIME_FMT_1));
                                int h = Integer.parseInt(TimeTools.format(srcdate,"HH"));
                                if(preDate == null){
                                    preDate = srcdate;
                                }
                                if(nextDate == null){
                                    nextDate = addTime(preDate);
                                }
                                if(srcdate.before(nextDate)){
                                    Map<String,Object> v = new HashMap<>();
                                    v.put(urlField,filterParam(m.get(urlField)));
                                    v.put("hour",h);
                                    v.put("time",TimeTools.format2(srcdate));
                                    tem.add(v);
                                }else{
                                    towLevelList.add(tem);
                                    tem = new ArrayList<>();
                                    Map<String,Object> v = new HashMap<>();
                                    v.put(urlField,filterParam(m.get(urlField)));
                                    v.put("hour",h);
                                    v.put("time",TimeTools.format2(srcdate));
                                    tem.add(v);
                                    nextDate = addTime(srcdate);
                                }
                            }
                            if(i == list.size()){
                                towLevelList.add(tem);
                            }
                        }
                    }
                }
                if(CollectionUtils.isNotEmpty(towLevelList)){
                    urls.add(towLevelList);
                }
            }
            urls.forEach(l -> {
                if(CollectionUtils.isNotEmpty(l)){
                    scores.add(buildItemData(l,userId));
                }
            });
/*
            urls.forEach(l ->{
                System.out.println("#####################");
                l.forEach(i ->{
                    i.forEach(u ->{
                        String ul = u.get(urlField).toString();
                        String time = u.get("time").toString();
                        System.out.println(time+">>>"+ul);
                    });
                    System.out.println("");
                });
                System.out.println("#####################");
            });*/
        }
        Lists.partition(scores, BATCH).forEach(l -> baseLineScoreMapper.saveBatch4List(l));
    }


    private BaseLineScore buildItemData(List<List<Map<String,Object>>> items,String key){
        String startTime = items.get(0).get(0).get("time").toString();
        //startTime = TimeTools.format1(TimeTools.gmtToUtcTime(TimeTools.parseDate2(startTime)));
        List<Map<String, Object>> maps = items.get(items.size() - 1);
        String endTime = maps.get(maps.size()-1).get("time").toString();
        //endTime = TimeTools.format1(TimeTools.gmtToUtcTime(TimeTools.parseDate2(endTime)));
        BaseLineScore score = new BaseLineScore();
        //查询用户频繁项
        score.setStartTime(startTime);
        score.setEndTime(endTime);
        List<BaseLineFrequent> userFrequent = baseLineFrequentService.findUserFrequent(key);
        //计算相似度得分
        Integer frequentId = null;
        float similarity = 0f;
        String frequents = "";
        Map<String, Object> itemMap = renderItem(items);
        for(BaseLineFrequent f : userFrequent){
            float s = SimilarityHelper.jaccard4list(f.getFrequents(), itemMap.get("md5").toString());
            //float s = AnotherCompare.cosListFromString(f.getFrequents(), itemMap.get("md5").toString());
            if(s > similarity){
                similarity = s;
                frequentId = f.getId();
                frequents = f.getFrequents();
            }
        }
        //计算时间维度得分
        float[] attrScores = doAttrScore((Integer) itemMap.get("avgHour"),(Float)(itemMap.get("pckAll")), frequents,key);
        score.setCompress(itemMap.get("md5").toString());
        score.setFrequentId(frequentId);
        score.setSimilarityScore(similarity);
        score.setHourScore(attrScores[0]);
        score.setPackgeScore(attrScores[1]);
        score.setUserKey(key);
        score.setTime(TimeTools.format(TimeTools.gmtToUtcTime(new Date()),TimeTools.TIME_FMT_1));
        score.setType("1");
        return score;
    }

    private float[] doAttrScore(int hour,float pck,String frequents,String userId){
        List<BaseLineFrequentAttr> attrs = baseLineFrequentAttrService.findByFrequents(Arrays.asList(frequents.split(separator)),userId);
        double[] hds = new double[attrs.size()];
        double[] pds = new double[attrs.size()];
        float hourScore = 0;
        float pckScore = 0;
        int i = 0;
        for(BaseLineFrequentAttr attr : attrs){
            hds[i] = Double.valueOf(attr.getHour());
            pds[i] = Double.valueOf(attr.getPck());
            i++;
        }
        if(hds.length > 1){
            hourScore = StandardTool.standardValue(hds,hour);
        }else if(hds.length == 1){
            hourScore = StandardTool.defaultValue(hour,hds[0]);
        }
        if(pds.length > 1){
            pckScore = StandardTool.standardValue(pds,pck);
        }else if(pds.length == 1){
            pckScore = StandardTool.defaultValue(pck,pds[0]);
        }
        return new float[]{hourScore,pckScore};
    }

    /**
     * 计算序列属性
     * @param items
     * @return
     */
    private Map<String,Object> renderItem(List<List<Map<String,Object>>> items){
        Map<String,Object> result = new HashMap<>();
        StringBuffer str = new StringBuffer();
        int hourTotal = 0;
        float pckAll = 0;
        int size = 0;
        for(List<Map<String,Object>> list : items){
            StringBuffer tem = new StringBuffer();
            List<String> uul = new ArrayList<>();
            for(Map<String,Object> m : list){
                uul.add(m.get(urlField).toString());
                //tem.append(m.get(urlField).toString());
                int h = (int)m.get("hour");
                hourTotal = hourTotal+h;
                //累计包大小
                Object pck = m.get(pckField);
                if(pck != null && StringUtils.isNotEmpty(pck.toString())){
                    pckAll += Float.valueOf(m.get(pckField).toString());
                }
                size += 1;
            }
            Collections.sort(uul);
            uul.forEach(s ->{
                tem.append(s);
            });
            String md5 = Base64Util.compressString(tem.toString());
            str.append(md5).append(separator);
        }
        int avgHour = Math.round(hourTotal/size);
        result.put("md5",str.toString().substring(0,str.length()-1));
        result.put("avgHour",avgHour);
        result.put("pckAll",pckAll);
        return result;
    }

    private void safe2List(StringBuffer item,List<String> tem){
        if(CollectionUtils.isNotEmpty(tem)){
            if(item.length() != 0){
                item.append(separator);
            }
            item.append(compressList2Str(tem));
        }
    }

//    private void safe2List(StringBuffer item,List<Map<String,String>> tem,List<BaseLineFrequentAttr> attrs){
//        if(CollectionUtils.isNotEmpty(tem)){
//            if(item.length() != 0){
//                item.append(separator);
//            }
//            StringBuffer str = new StringBuffer();
//            int allhr = 0;
//            float pckall = 0;
//            for(Map<String,String> map : tem){
//                str.append(filterParam(map.get(urlField)));
//                //频繁项属性计算
//                int hr = Integer.parseInt(TimeTools.format(TimeTools.parseDate(map.get(timeField), TimeTools.TIME_FMT_1),"HH"));
//                allhr = allhr+hr;
//                String pck = map.get(pckField);
//                if(StringUtils.isNotEmpty(pck)){
//                    pckall += Float.valueOf(pck);
//                }
//            }
//            String startTime = tem.get(0).get(timeField);
//            String endTime = tem.get(tem.size()-1).get(timeField);
//            String id = Base64Util.compressString(StringUtil.sort(str.toString()));
//            int hour = Math.round(allhr/tem.size());
//            attrs.add(new BaseLineFrequentAttr(id,hour,pckall,startTime,endTime));
//            item.append(id);
//        }
//    }

    private String filterParam(String url){
        int flag = url.indexOf("?");
        if(flag == -1){
            return url;
        }else{
            return url.substring(0,flag);
        }
    }

    private String list2string(List<String> list){
        StringBuffer s = new StringBuffer();
        list.forEach(t ->{
            s.append(t).append(separator);
        });
        return s.toString().substring(0,s.length()-1);
    }

    private String compressList2Str(List<String> list){
        StringBuffer str = new StringBuffer();
        list.forEach(s ->{
            str.append(s);
        });
        return Base64Util.compressString(str.toString());
    }

    private Date addTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND,timeSplit);
        return cal.getTime();
    }

    private Date addTowLevelTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE,towLevelTimeSplit);
        return cal.getTime();
    }


    private EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, String index, String time,String format,Date startTime,Date endTime) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(startTime);
        queryModel.setEndTime(endTime);
        /*List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));
        }*/
        queryModel.setIndexName(index);
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTimeFormat(format);
        queryModel.setNeedTimeFormat(true);
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{time});
        queryModel.setSortOrder(SortOrder.ASC);
        queryModel.setCount(count);
        queryModel.setStart(0);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    private EsQueryModel buildQueryModel2(QueryTools.QueryWrapper wrapper, String index, String time,String format,Integer day) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStartTime(TimeTools.getNowBeforeByDay(day-1));
        queryModel.setEndTime(TimeTools.getNowBeforeByDay2(1));
        /*List<String> indexList = wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime());
        if (!indexList.isEmpty()) {
            queryModel.setIndexNames(indexList.toArray(new String[indexList.size()]));

        }*/
        queryModel.setIndexName(index);
        // 设置时间字段
        queryModel.setTimeField(time);
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTimeFormat(format);
        queryModel.setNeedTimeFormat(true);
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{time});
        queryModel.setSortOrder(SortOrder.ASC);
        queryModel.setCount(count);
        queryModel.setStart(0);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    public Date dec8H(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        //cal.add(Calendar.HOUR,-8);
        return cal.getTime();
    }

    public static void main(String[] args) {
        Date s = new Date();
        Date e = new Date();
        System.out.println();
    }

}
