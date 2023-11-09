package com.vrv.vap.line.tools;

import avro.shaded.com.google.common.collect.Lists;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.mapper.BaseLineFrequentAttrMapper;
import com.vrv.vap.line.mapper.BaseLineFrequentMapper;
import com.vrv.vap.line.mapper.BaseLineScoreMapper;
import com.vrv.vap.line.mapper.JUserLogsMapper;
import com.vrv.vap.line.model.BaseLineFrequent;
import com.vrv.vap.line.model.BaseLineFrequentAttr;
import com.vrv.vap.line.model.EsQueryModel;
import com.vrv.vap.line.model.JUserLogs;
import com.vrv.vap.line.service.BaseLineFrequentAttrService;
import com.vrv.vap.line.service.BaseLineFrequentService;
import com.vrv.vap.line.service.CommonService;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ItemRunable4Flink implements Runnable{

    private int timeSplit = 3;//切分时间间隔 单位 s
    private int towLevelTimeSplit = 5;//二级切分时间间隔 单位 min
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
    private String uKey;
    private RestHighLevelClient client = null;


    public ItemRunable4Flink(String uKey, RestHighLevelClient client) {
        this.uKey = uKey;
        this.client = client;
    }

    public ItemRunable4Flink() {
    }

    @Override
    public void run() {
        Date now = new Date();
        long userSize = 0;
        JUserLogs udo = new JUserLogs();
        udo.setType("1");
        List<BaseLineFrequentAttr> attrs = new ArrayList<>();
        List<BaseLineFrequent> frequents = new ArrayList<>();
        String userId = this.uKey;
        System.out.println("#########处理用户："+userId+"开始########");
        udo.setUserKey(userId);

        BoolQueryBuilder query = new BoolQueryBuilder();
        //query.must(QueryBuilders.termQuery(userField,userId));
        query.mustNot(QueryBuilders.termQuery(urlField,""));
//            query.must(QueryBuilders.termQuery("host","192.168.119.209"));
//            query.must(QueryBuilders.wildcardQuery("url","/IMS/*"));
        final EsQueryModel queryModel = buildQueryModel2(index, timeField, TimeTools.TIME_FMT_1,days);
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = null;
        List<List<Map<String, String>>> towSplitDatas = new ArrayList<>();
        List<Map<String, String>> itemList = new ArrayList<>();
        Date startTime = null;
        Date endTime = null;
        long querys = System.currentTimeMillis();
        MyEsTools esTools = new MyEsTools();
        while (true) {
            System.out.println("#########滚动查询开始########");
            searchResponse = esTools.scrollQuery(queryModel, searchResponse == null ? null : searchResponse.getScrollId(),this.client);
            SearchHits hits = searchResponse.getHits();
            List<Map<String, String>> list = esTools.wrapResponse(searchResponse.getHits(), timeField);
            System.out.println("#########滚动查询结束########");
            if (hits.getHits() == null || hits.getHits().length == 0) {
                break;
            }
            userSize += list.size();
            int i = 0;
            for(Map<String, String> mp : list){
                i++;
                Date srcdate = TimeTools.parseDate(mp.get(timeField), TimeTools.TIME_FMT_2);
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
        long querye = System.currentTimeMillis();
        udo.setQueryTime(querye-querys);
        udo.setSize(userSize);
        if(CollectionUtils.isNotEmpty(itemList)){
            towSplitDatas.add(itemList);
        }
        //查询url数据并按时间切分
        long splits = System.currentTimeMillis();
        List<String> urls = new ArrayList<>();
        for(List<Map<String, String>> itemdata  : towSplitDatas){
            StringBuffer item = new StringBuffer();
            Date preDate = null;
            Date nextDate = null;
            List<Map<String,String>> tem = new ArrayList<>();
            int i = 0;
            for(Map<String, String> m : itemdata){
                i++;
                if(m.containsKey(urlField) && StringUtils.isNotEmpty(m.get(urlField))){
                    Date srcdate = TimeTools.parseDate(m.get(timeField), TimeTools.TIME_FMT_2);
                    if(preDate == null){
                        preDate = srcdate;
                    }
                    if(nextDate == null){
                        nextDate = addTime(preDate);
                    }
                    if(srcdate.before(nextDate)){
                        tem.add(m);
                    }else{
                        safe2List(item,tem,attrs);
                        tem = new ArrayList<>();
                        tem.add(m);
                        nextDate = addTime(srcdate);
                    }
                }
                if(i == itemdata.size()){
                    safe2List(item,tem,attrs);
                }
            }
            if(item.length() != 0){
                urls.add(item.toString());
            }
        }
        long splite = System.currentTimeMillis();
        udo.setSplitTime(splite-splits);
        //先验算法计算频繁项
        //Apriori apriori = new Apriori(urls);
        System.out.println(" #########计算频繁项开始########");
        long ans = System.currentTimeMillis();
        List<Row> rows = AlinkTools.apriori4Alink(urls);
        long ane = System.currentTimeMillis();
        //Map<List<String>,Integer> frequentCollectionMap = apriori.getFC();
        System.out.println(" #########计算频繁项结束########");
        StringBuffer urlStr = new StringBuffer();
        udo.setAnalysisTime(ane-ans);
        long fs = System.currentTimeMillis();
        for(String url : urls){
            urlStr.append(url).append(itemSeparator);
        }
        String urlsall = urlStr.toString();
        System.out.println("#########频繁项共计:"+rows.size()+"条########");
        AtomicInteger a = new AtomicInteger(1);
        rows.parallelStream().forEach((r) ->{
            //System.out.println("当前线程："+Thread.currentThread().getName());
            String item = r.getField(0).toString();
            //查找连续性
            //System.out.println(a.getAndIncrement());
            if(urlsall.indexOf(item) > -1){
                //System.out.println("严格连续");
                BaseLineFrequent record = new BaseLineFrequent();
                record.setUserId(userId);
                record.setFrequents(item);
                record.setCount(Integer.parseInt(r.getField(1).toString()));
                record.setIsContinue(LineConstants.CONTINUE.YES);
                record.setTime(now);
                record.setType("1");
                frequents.add(record);
            }
            //System.out.println("本次查找结束");
        });
        long fe = System.currentTimeMillis();
        udo.setFilterTime(fe-fs);
        if(CollectionUtils.isNotEmpty(attrs)){
            // 统计结果分批入库
            System.out.println("#########item入库开始########");
            saveData(attrs,"item");
            //Lists.partition(attrs, BATCH).forEach(l -> baseLineFrequentAttrMapper.saveBatch4List(l));
            System.out.println("#########item入库结束########");
        }
        if(CollectionUtils.isNotEmpty(frequents)){
            // 统计结果分批入库
            System.out.println("#########频繁项入库开始########");
            saveData(frequents,"frequents");
            //Lists.partition(frequents, BATCH).forEach(l -> baseLineFrequentMapper.saveBatch4List(l));
            System.out.println("#########频繁项入库结束########");
        }
        long ue = System.currentTimeMillis();
        udo.setSaveTime(ue-fe);
        System.out.println("#########处理用户："+userId+"结束########");
        saveData(udo,"juser");
        //this.jUserLogsMapper.insert(udo);
        //uss.add(udo);
    }

    private void saveData(Object o,String pre){
        try{
            String dir = "/vrv/tmpdata/";
            File foder = new File(dir);
            if(!foder.exists()){
                foder.mkdirs();
            }
            File f = new File(dir+pre+System.currentTimeMillis()+".json");
            if(!f.exists()){
                f.createNewFile();
            }
            FileOutputStream fos = null;
            Writer write = null;
            try{
                fos = new FileOutputStream(f);
                write = new OutputStreamWriter(fos, "UTF-8");
                write.write(JSONObject.toJSONString(o));
                write.flush();
            }catch (Exception v){
                v.printStackTrace();
            }finally {
                if(fos != null){
                    fos.close();
                }
                if(write != null){
                    write.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    private Date addTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.SECOND,timeSplit);
        return cal.getTime();
    }

    private EsQueryModel buildQueryModel2(String index, String time,String format,Integer day) {
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
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        queryModel.setQueryFields(new String[]{urlField,timeField,pckField});
        queryModel.setLimitResultFields(true);
        queryModel.setResultFields(new String[]{urlField,timeField,pckField});
        return queryModel;
    }

    private Date addTowLevelTime(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.MINUTE,towLevelTimeSplit);
        return cal.getTime();
    }

    private String compressList2Str(List<String> list){
        StringBuffer str = new StringBuffer();
        list.forEach(s ->{
            str.append(s);
        });
        return Base64Util.compressString(str.toString());
    }

    private String filterParam(String url){
        int flag = url.indexOf("?");
        if(flag == -1){
            return url;
        }else{
            return url.substring(0,flag);
        }
    }


    private void safe2List(StringBuffer item,List<Map<String,String>> tem,List<BaseLineFrequentAttr> attrs){
        if(CollectionUtils.isNotEmpty(tem)){
            if(item.length() != 0){
                item.append(separator);
            }
            StringBuffer str = new StringBuffer();
            int allhr = 0;
            float pckall = 0;
            for(Map<String,String> map : tem){
                str.append(filterParam(map.get(urlField)));
                //频繁项属性计算
                int hr = Integer.parseInt(TimeTools.format(TimeTools.parseDate(map.get(timeField), TimeTools.TIME_FMT_2),"HH"));
                allhr = allhr+hr;
                String pck = map.get(pckField);
                if(StringUtils.isNotEmpty(pck)){
                    pckall += Float.valueOf(pck);
                }
            }
            String startTime = tem.get(0).get(timeField);
            String endTime = tem.get(tem.size()-1).get(timeField);
            String id = Base64Util.compressString(StringUtil.sort(str.toString()));
            int hour = Math.round(allhr/tem.size());
            //attrs.add(new BaseLineFrequentAttr(id,hour,pckall,startTime,endTime));
            item.append(id);
        }
    }
}
