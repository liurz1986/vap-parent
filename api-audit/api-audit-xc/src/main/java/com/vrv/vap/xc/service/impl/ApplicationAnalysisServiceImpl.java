package com.vrv.vap.xc.service.impl;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.EsResult;
import com.vrv.vap.xc.mapper.core.DepartSecretInfoTotalMapper;
import com.vrv.vap.xc.mapper.core.DepartVisitAppTotalMapper;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.pojo.DepartSecretInfoTotal;
import com.vrv.vap.xc.pojo.DepartVisitAppTotal;
import com.vrv.vap.xc.service.ApplicationAnalysisService;
import com.vrv.vap.xc.tools.DateTools;
import com.vrv.vap.xc.tools.QueryTools;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.elasticsearch.search.aggregations.bucket.histogram.Histogram;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.joda.time.DateTimeZone;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ApplicationAnalysisServiceImpl implements ApplicationAnalysisService {

    @Autowired
    private DepartSecretInfoTotalMapper departSecretInfoTotalMapper;
    @Autowired
    private DepartVisitAppTotalMapper departVisitAppTotalMapper;

    /**
     * 分页查询用户访问ip列表
     * @param model
     * @return
     */
    @Override
    public VList<Map<String, String>> userHisVisits(UserVisitModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getUsername())){
            query.must(QueryBuilders.termsQuery("username", model.getUsername()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            return VoBuilder.vl(list.size(), list);
        }
        return VoBuilder.vl(0, new ArrayList<>());
    }

    /**
     * 账户登陆口令尝试次数
     * @return
     */
    @Override
    public VList<Map<String, String>> aggLoginPwd(ApplicationModel pageModel) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, pageModel, "netflow-http",true);
        TermsAggregationBuilder appAgg = new TermsAggregationBuilder("username");
        appAgg.field("username");
        TermsAggregationBuilder subAgg = new TermsAggregationBuilder("dip");
        subAgg.field("dip");
        appAgg.subAggregation(subAgg);
        appAgg.subAggregation(AggregationBuilders.topHits("data").size(1));
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(pageModel.getAppNo())){
            query.must(QueryBuilders.termsQuery("dst_std_sys_id", pageModel.getAppNo()));
        }
        query.must(QueryBuilders.termsQuery("res_code", "1"));
        query.mustNot(QueryBuilders.termsQuery("src_std_user_level", "-1"));
        queryModel.setAggregationBuilder(appAgg);
        queryModel.setUseAggre(true);
        queryModel.setQueryBuilder(query);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        List<Map<String, String>> list = new ArrayList<>();
        if (aggMap != null && aggMap.containsKey("aggregations")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("aggregations");
            if (dataAggMap != null && dataAggMap.containsKey("username")) {
                Map<String, Object> bucketsMap = (Map<String, Object>) dataAggMap.get("username");
                if (bucketsMap.containsKey("buckets")) {
                    List<Map<String, Object>> aggItems = (List<Map<String, Object>>) bucketsMap.get("buckets");
                    for (Map<String, Object> aggItem : aggItems) {
                        Map<String, Object> oiMap = (Map<String, Object>) aggItem.get("dip");
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>)((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        if (oiMap.containsKey("buckets")) {
                            List<Map<String, Object>> detailList = (List<Map<String, Object>>) oiMap.get("buckets");
                            for (Map<String, Object> detailMap : detailList) {
                                Map<String, String> map = new HashMap<>();
                                map.put("dip", (String) detailMap.get("key"));
                                map.put("username", aggItem.get("key").toString());
                                map.put("time", row.get("event_time") == null ? "" : row.get("event_time").toString());
                                map.put("level", row.get("src_std_user_level") == null ? "" : row.get("src_std_user_level").toString());
                                map.put("count", detailMap.get("doc_count").toString());
                                list.add(map);
                            }
                        }
                    }
                }
            }
        }
        return VoBuilder.vl(list.size(),list);
    }

    /**
     * 历史访问入口地址
     * @param appModel
     * @return
     */
    @Override
    public VList<Map<String, Object>> hisVisitsUrl(ApplicationModel appModel) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, appModel, "netflow-http*",false);
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.existsQuery("url"));
        if(StringUtils.isNotEmpty(appModel.getAppNo())){
            query.must(QueryBuilders.termsQuery("dst_std_sys_id", appModel.getAppNo()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = this.genDataMap(queryModel, wrapper, new String[]{"url"}, new String[]{"url"});
        return VoBuilder.vl(maps.size(),maps);
    }

    /**
     * 用户历史通信使用的入口地址,ip,协议名,及端口号
     * @param appModel
     * @return
     */
    @Override
    public VList<Map<String, Object>> hisVisitAgreePort(ApplicationModel appModel) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, appModel, "netflow-http*",false);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(appModel.getAppNo())){
            query.must(QueryBuilders.termsQuery("dst_std_sys_id", appModel.getAppNo()));
        }
        if(StringUtils.isNotEmpty(appModel.getSrcAppNo())){
            query.must(QueryBuilders.termsQuery("src_std_sys_id", appModel.getSrcAppNo()));
        }
        if(StringUtils.isNotEmpty(appModel.getUsername())){
            query.must(QueryBuilders.termsQuery("username", appModel.getUsername()));
        }
        queryModel.setQueryBuilder(query);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (aggMap != null && aggMap.containsKey("hits")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("hits");
            if (dataAggMap != null && dataAggMap.containsKey("hits")) {
                List<Map<String, Object>> aggItems = (List<Map<String, Object>>) dataAggMap.get("hits");
                for(Map<String, Object> m : aggItems){
                    if(m != null && m.containsKey("_source")){
                        Map<String, Object> detail = (Map<String, Object>) m.get("_source");
                        list.add(detail);
                    }
                }
            }
        }
        return VoBuilder.vl(list.size(),list);
    }

    /**
     * 用户周月文件上传下载数量
     * @param fileModel
     * @return
     */
    @Override
    public VData<Long> userFileCount(FileModel fileModel) {
        //DOTO 系统编号
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        Date starttime;
        if("0".equals(fileModel.getTime())){
            //七天
            starttime = DateTools.genDateDaysBefore(7);
        }else{
            //一个月
            starttime = DateTools.genDateMonthBefore(1);
        }
        model.setMyStartTime(starttime);
        model.setMyEndTime(DateTools.genDateEndDaysBefore(1));
        BoolQueryBuilder query = new BoolQueryBuilder();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        query.must(QueryBuilders.termsQuery("username", fileModel.getUsername()));
        query.must(QueryBuilders.termsQuery("file_dir", fileModel.getType()));
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        Long total = 0L;
        if (searchResponse != null && searchResponse.getHits() != null) {
            total = searchResponse.getHits().getTotalHits().value;
        }
        return VoBuilder.vd(total);
    }

    /**
     * 用户月周访问次数
     * @return
     */
    @Override
    public VData<Long> userVisitCount(TimeModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        Date starttime;
        if("0".equals(model.getTime())){
            //七天
            starttime = DateTools.genDateDaysBefore(7);
        }else{
            //一个月
            starttime = DateTools.genDateMonthBefore(1);
        }
        model.setMyStartTime(starttime);
        model.setMyEndTime(DateTools.genDateEndDaysBefore(1));
        BoolQueryBuilder query = new BoolQueryBuilder();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        query.must(QueryBuilders.termsQuery("username", model.getUsername()));
        if(StringUtils.isNotEmpty(model.getAppNo())){
            query.must(QueryBuilders.termsQuery("dst_std_sys_id", model.getAppNo()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        Long total = 0L;
        if (searchResponse != null && searchResponse.getHits() != null) {
            total = searchResponse.getHits().getTotalHits().value;
        }
        return VoBuilder.vd(total);
    }

    /**
     * 用户最近一次访问记录
     * @param
     * @return
     */
    @Override
    public VData<Map<String, Object>> userVisitRecently(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        model.setOrder("event_time");
        model.setBy("desc");
        BoolQueryBuilder query = new BoolQueryBuilder();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http*",false);
        query.must(QueryBuilders.termsQuery("username", model.getUsername()));
        query.must(QueryBuilders.termsQuery("dst_std_sys_id", model.getAppNo()));
        queryModel.setQueryBuilder(query);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        Map<String, Object> result = new HashMap<String, Object>();
        if (aggMap != null && aggMap.containsKey("hits")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("hits");
            if (dataAggMap != null && dataAggMap.containsKey("hits")) {
                List<Map<String, Object>> aggItems = (List<Map<String, Object>>) dataAggMap.get("hits");
                if(CollectionUtils.isNotEmpty(aggItems)){
                    Map<String, Object> detail = aggItems.get(0);
                    if(detail.containsKey("_source")){
                        detail = (Map<String, Object>)detail.get("_source");
                    }
                    if(detail != null){
                        result.put("time",detail.get("event_time"));
                        result.put("url",detail.get("url"));
                        result.put("protocol",detail.get("app_protocol"));
                        result.put("port",detail.get("dport"));
                        result.put("sport",detail.get("sport"));
                        result.put("appname",detail.get("dst_std_sys_name"));
                    }
                }
            }
        }
        return VoBuilder.vd(result);
    }

    /**
     * 后台服务列表
     * @param pageModel
     * @return
     */
    @Override
    public VList<Map<String, Object>> serverList(ApplicationModel pageModel) {
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(pageModel.getAppNo())){
            query.must(QueryBuilders.termsQuery("dst_std_sys_id", pageModel.getAppNo()));
            query.must(QueryBuilders.termsQuery("src_std_sys_id", pageModel.getAppNo()));
        }
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, pageModel, "netflow-http",true);
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggWithTopHitAndConcatField(queryModel, wrapper, "dst_std_sys_name", 100, "servername", "count", "dip", "ips", new String[]{"app_protocol", "dport"},new String[]{"app_protocol", "dport"});
        return VoBuilder.vl(list.size(),list);
    }

    /**
     * 应用最近一次被访问记录
     * @return
     */
    @Override
    public VData<Map<String, Object>> serverVisitRecently(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        model.setOrder("event_time");
        model.setBy("desc");
        BoolQueryBuilder query = new BoolQueryBuilder();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        //query.must(QueryBuilders.existsQuery("sip"));
        query.must(QueryBuilders.termsQuery("dst_std_sys_id", model.getAppNo()));
        if(StringUtils.isNotEmpty(model.getSrcAppNo())){
            query.must(QueryBuilders.termsQuery("src_std_sys_id", model.getSrcAppNo()));
        }
        queryModel.setQueryBuilder(query);
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        Map<String, Object> result = new HashMap<String, Object>();
        if (aggMap != null && aggMap.containsKey("hits")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("hits");
            if (dataAggMap != null && dataAggMap.containsKey("hits")) {
                List<Map<String, Object>> aggItems = (List<Map<String, Object>>) dataAggMap.get("hits");
                if(CollectionUtils.isNotEmpty(aggItems)){
                    Map<String, Object> detail = aggItems.get(0);
                    detail = (Map<String, Object>)detail.get("_source");
                    if(detail != null){
                        result.put("time",detail.get("event_time"));
                        result.put("sip",detail.get("url"));
                        result.put("protocol",detail.get("app_protocol"));
                        result.put("sport",detail.get("sport"));
                        result.put("appname",detail.get("app_name"));
                    }
                }
            }
        }
        return VoBuilder.vd(result);
    }

    /**
     *  其他应用列表
     * @param pageModel
     * @return
     */
    @Override
    public VList<Map<String, Object>> otherServerList(ApplicationModel pageModel) {
        BoolQueryBuilder query = new BoolQueryBuilder();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        query.mustNot(QueryBuilders.termQuery("src_std_sys_id", pageModel.getAppNo()));
        query.must(QueryBuilders.termQuery("dst_std_sys_id", pageModel.getAppNo()));
        EsQueryModel queryModel = buildQueryModel(wrapper, pageModel, "netflow-http",true);
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = simpleAggWithTopHitAndConcatField(queryModel, wrapper, "src_std_sys_id", pageModel.getMyCount(), "appno", "count", "dip", "ips", new String[]{"src_std_dev_level","src_std_sys_name"},new String[]{"src_std_dev_level","app_name"});
        return VoBuilder.vl(list.size(),list);
    }

    /**
     * 应用访问趋势
     * @param tmodel
     * @return
     */
    @Override
    public VList<Map<String, Object>> appVisitTrend(TimeModel tmodel) {
        List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        Date starttime;
        if("0".equals(tmodel.getTime())){
            //七天
            starttime = DateTools.genDateDaysBefore(7);
        }else{
            //一个月
            starttime = DateTools.genDateMonthBefore(1);
        }
        model.setMyStartTime(starttime);
        model.setMyEndTime(DateTools.genDateEndDaysBefore(1));
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(tmodel.getAppNo())){
            query.must(QueryBuilders.termsQuery("dst_std_sys_id", tmodel.getAppNo()));
        }
        if(StringUtils.isNotEmpty(tmodel.getSrcAppNo())){
            query.must(QueryBuilders.termsQuery("src_std_sys_id", tmodel.getSrcAppNo()));
        }
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        queryModel.setQueryBuilder(query);
        int intervalDay = 1;
        DateHistogramAggregationBuilder aggTime = AggregationBuilders.dateHistogram("aggTime").field("event_time")
                .timeZone(DateTimeZone.forOffsetHours(8).toTimeZone().toZoneId()).format("yyyy-MM-dd").dateHistogramInterval(DateHistogramInterval.DAY);
        queryModel.setAggregationBuilder(aggTime);
        // 查询
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        if (null == response || response.getHits().getHits().length == 0) {
            return VoBuilder.vl(0,list);
        }

        // 解析聚合结果
        Histogram idTerms = response.getAggregations().get("aggTime");
        Map<String, Object> map = null;
        for (Histogram.Bucket temp : idTerms.getBuckets()) {
            map = new HashMap<>(1);
            map.put("timePeriod", temp.getKeyAsString());
            map.put("count", temp.getDocCount());
            list.add(map);
        }

        return VoBuilder.vl(list.size(),list);
    }

    /**
     * 文件上传下载趋势
     * @param tmodel
     * @return
     */
    @Override
    public VData<Map<String, Object>> fileTrend(TimeModel tmodel) {
        List<Map<String,Object>> slist = new ArrayList<Map<String,Object>>();
        List<Map<String,Object>> xlist = new ArrayList<Map<String,Object>>();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        if(tmodel.getMyStartTime() == null || tmodel.getMyEndTime() == null){
            Date starttime;
            if("0".equals(tmodel.getTime())){
                //七天
                starttime = DateTools.genDateDaysBefore(7);
            }else{
                //一个月
                starttime = DateTools.genDateMonthBefore(1);
            }
            model.setMyStartTime(starttime);
            model.setMyEndTime(DateTools.genDateEndDaysBefore(1));
        }
        BoolQueryBuilder sd = new BoolQueryBuilder();
        BoolQueryBuilder squery = new BoolQueryBuilder();
        BoolQueryBuilder xquery = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(tmodel.getDepartCode())){
            /*
            squery.must(QueryBuilders.termsQuery("src_std_org_code", tmodel.getDepartCode()));
            xquery.must(QueryBuilders.termsQuery("src_std_org_code", tmodel.getDepartCode()));*/
            sd.should(QueryBuilders.termsQuery("visit_type", "2"));
            sd.should(QueryBuilders.termsQuery("visit_type", "3"));
            xquery.must(sd);
            squery.must(sd);
        }
        if(StringUtils.isNotEmpty(tmodel.getAppNo())){
            squery.must(QueryBuilders.termsQuery("dst_std_sys_id", tmodel.getAppNo()));
            xquery.must(QueryBuilders.termsQuery("dst_std_sys_id", tmodel.getAppNo()));
        }
        squery.must(QueryBuilders.termsQuery("file_dir", "1"));
        xquery.must(QueryBuilders.termsQuery("file_dir", "2"));
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        queryModel.setQueryBuilder(squery);
        int intervalDay = 1;
        DateHistogramAggregationBuilder aggTime = AggregationBuilders.dateHistogram("aggTime").field("event_time")
                .timeZone(DateTimeZone.forOffsetHours(8).toTimeZone().toZoneId()).format("yyyy-MM-dd").dateHistogramInterval(DateHistogramInterval.DAY);
        queryModel.setAggregationBuilder(aggTime);
        // 查询
        SearchResponse response1 = wrapper.getSearchResponse(queryModel);
        queryModel.setQueryBuilder(xquery);
        SearchResponse response2 = wrapper.getSearchResponse(queryModel);
        if (response1 != null && response1.getHits().getHits().length > 0) {
            // 解析聚合结果
            Histogram idTerms = response1.getAggregations().get("aggTime");
            Map<String, Object> map = null;
            for (Histogram.Bucket temp : idTerms.getBuckets()) {
                map = new HashMap<>(1);
                map.put("timePeriod", temp.getKeyAsString());
                map.put("count", temp.getDocCount());
                slist.add(map);
            }
        }

        if (response2 != null && response2.getHits().getHits().length > 0) {
            // 解析聚合结果
            Histogram idTerms = response2.getAggregations().get("aggTime");
            Map<String, Object> map = null;
            for (Histogram.Bucket temp : idTerms.getBuckets()) {
                map = new HashMap<>(1);
                map.put("timePeriod", temp.getKeyAsString());
                map.put("count", temp.getDocCount());
                xlist.add(map);
            }
        }
        Map<String,Object> data = new HashMap<String,Object>();
        data.put("uploadTrend",slist);
        data.put("downloadTrend",xlist);
        return VoBuilder.vd(data);
    }


    /**
     * 文件业务列表密级
     * @param appModel
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> fileListLevel(ApplicationModel appModel) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, appModel, "netflow-app-file",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(appModel.getAppNo())){
            query.must(QueryBuilders.termsQuery("src_std_sys_id", appModel.getAppNo()));
        }
        query.mustNot(QueryBuilders.termQuery("classification_level", ""));
        query.must(QueryBuilders.existsQuery("business_list"));
        queryModel.setQueryBuilder(query);
        //List<Map<String, Object>> maps = this.genDataMap(queryModel, wrapper, "business_list,classification_level".split(","), "fileBusinessList,fileLevel".split(","));
        List<Map<String, Object>> maps = this.simpleAggWithTopHitAndConcatField(queryModel, wrapper, "classification_level", 1000, "level", "count", "business_list", "businessList", new String[]{},null);
        return VoBuilder.vd(maps);
    }

    /**
     * 互联访问趋势
     * @param tmodel
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> interconTrend(TimeModel tmodel) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        PageModel model = new PageModel();
        Date starttime;
        if("0".equals(tmodel.getTime())){
            //七天
            starttime = DateTools.genDateDaysBefore(7);
        }else{
            //一个月
            starttime = DateTools.genDateMonthBefore(1);
        }
        model.setMyStartTime(starttime);
        model.setMyEndTime(DateTools.genDateEndDaysBefore(1));
        BoolQueryBuilder query = new BoolQueryBuilder();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        query.must(sd);
        /*
        if(StringUtils.isNotEmpty(tmodel.getDepartCode())){
            query.must(QueryBuilders.termsQuery("dst_std_org_code", tmodel.getDepartCode()));
        }*/
        queryModel.setQueryBuilder(query);
        int intervalDay = 1;
        DateHistogramAggregationBuilder aggTime = AggregationBuilders.dateHistogram("aggTime").field("event_time")
        .timeZone(DateTimeZone.forOffsetHours(8).toTimeZone().toZoneId()).format("yyyy-MM-dd").dateHistogramInterval(DateHistogramInterval.DAY);

        queryModel.setAggregationBuilder(aggTime);
        // 查询 response.getHits().getHits().length > 0
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        if (response != null && response.getHits().getHits().length > 0) {
            // 解析聚合结果
            Histogram idTerms = response.getAggregations().get("aggTime");
            Map<String, Object> map = null;
            for (Histogram.Bucket temp : idTerms.getBuckets()) {
                map = new HashMap<>(1);
                map.put("timePeriod", temp.getKeyAsString());
                map.put("count", temp.getDocCount());
                list.add(map);
            }
        }
        return VoBuilder.vd(list);
    }

    /**
     * 每对联通IP采用的协议及端口号
     * @param model
     * @return
     */
    @Override
    public EsResult interconProPortList(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        /*if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("dst_std_org_code", model.getDepartCode()));
        }*/
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        query.must(sd);
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        EsResult esResult = wrapper.wrapResult(searchResponse, queryModel);
        return esResult;
    }

    /**
     * 协议次数分布
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> protocolCount(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        model.setMyCount(0);
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        /*
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("dst_std_org_code", model.getDepartCode()));
        }*/
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        query.must(sd);
        queryModel.setQueryBuilder(query);
            List<Map<String, Object>> maps = QueryTools.simpleAgg(queryModel, wrapper, "app_protocol", 1000, "protocol", "count");
        return VoBuilder.vd(maps);
    }

    /**
     * 内外ip数
     * @param model
     * @return
     */
    @Override
    public VData<Map<String, Object>> ipCount(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termQuery("visit_type", "2"));
        sd.should(QueryBuilders.termQuery("visit_type", "3"));
        query.must(sd);
        queryModel.setQueryBuilder(query);
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> maps = QueryTools.twoLevelAggToHits(queryModel,wrapper,"dip","sip",100,100,"count",new String[]{"src_std_org_code","dst_std_org_code"});
        Set<String> inips = new HashSet<>();
        Set<String> outips = new HashSet<>();
        for(Map<String, Object> m : maps){
            //内部ip统计
            /*if(StringUtils.isNotEmpty(model.getDepartCode())){
                if(model.getDepartCode().equals(m.get("src_std_org_code"))){
                    if(m.get("sip") != null && StringUtils.isNotEmpty(m.get("sip").toString())){
                        inips.add(m.get("sip").toString());
                    }
                }else if(model.getDepartCode().equals(m.get("dst_std_org_code"))){
                    if(m.get("dip") != null && StringUtils.isNotEmpty(m.get("dip").toString())){
                        inips.add(m.get("dip").toString());
                    }
                }
            }*/
            if(m.get("src_std_org_code") != null && StringUtils.isNotEmpty(m.get("src_std_org_code").toString())){
                if(m.get("sip") != null && StringUtils.isNotEmpty(m.get("sip").toString())){
                    inips.add(m.get("sip").toString());
                }
            }
            if(m.get("dst_std_org_code") != null && StringUtils.isNotEmpty(m.get("dst_std_org_code").toString())){
                if(m.get("dip") != null && StringUtils.isNotEmpty(m.get("dip").toString())){
                    inips.add(m.get("dip").toString());
                }
            }
            //外部ip统计
            if(m.get("src_std_org_code") == null || StringUtils.isEmpty(m.get("src_std_org_code").toString())){
                if(m.get("sip") != null && StringUtils.isNotEmpty(m.get("sip").toString())){
                    outips.add(m.get("sip").toString());
                }
            }
            if(m.get("dst_std_org_code") == null || StringUtils.isEmpty(m.get("dst_std_org_code").toString())){
                if(m.get("dip") != null && StringUtils.isNotEmpty(m.get("dip").toString())){
                    outips.add(m.get("dip").toString());
                }
            }
        }
        queryModel.setQueryBuilder(query);
        result.put("inips",inips);
        result.put("outips",outips);
        return VoBuilder.vd(result);
    }

    /**
     * 端口top排行
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> portTop(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        /*
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("dst_std_org_code", model.getDepartCode()));
        }*/
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        query.must(sd);
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = QueryTools.simpleAgg(queryModel, wrapper, "dport", 50, "port", "count");
        //排序
        Collections.sort(maps, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                long count1 = Long.valueOf(o1.get("count").toString());
                long count2 = Long.valueOf(o2.get("count").toString());
                if(count1 > count2){
                    return -1;
                }else if(count1 == count2){
                    return 0;
                }else{
                    return 1;
                }
            }
        });
        return VoBuilder.vd(maps);
    }

    /**
     * （输入/输出）每个密级的业务个数
     * @param model
     * @return
     */
    @Override
    public VData<Map<String, Object>> inoutlevelcount(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        BoolQueryBuilder inquery = new BoolQueryBuilder();
        BoolQueryBuilder outquery = new BoolQueryBuilder();
        /*
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            inquery.must(QueryBuilders.termsQuery("src_std_org_code", model.getDepartCode()));
            outquery.must(QueryBuilders.termsQuery("src_std_org_code", model.getDepartCode()));
        }*/
        inquery.must(QueryBuilders.termsQuery("file_dir", "2"));
        outquery.must(QueryBuilders.termsQuery("file_dir", "1"));
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        inquery.must(sd);
        outquery.must(sd);
        queryModel.setQueryBuilder(inquery);
        //输入
        //List<Map<String, Object>> inmaps = QueryTools.twoLevelAgg(queryModel, wrapper, "classification_level", "business_list", 100, 100, "count");
        List<Map<String, Object>> inmaps = simpleAggWithTopHitAndConcatField(queryModel, wrapper, "classification_level", 1000, "level", "count", "business_list", "type", null,null);
        queryModel.setQueryBuilder(outquery);
        //输出
        List<Map<String, Object>> outmaps = simpleAggWithTopHitAndConcatField(queryModel, wrapper, "classification_level", 1000, "level", "count", "business_list", "type", null,null);
        //List<Map<String, Object>> outmaps = QueryTools.twoLevelAgg(queryModel, wrapper, "classification_level", "business_list", 100, 100, "count");
        Map<String, Object> indata = new HashMap<>();
        Map<String, Object> outdata = new HashMap<>();
        inmaps.forEach( e ->{
            Object key = e.get("level");
            int count = 0;
            if(key != null && StringUtils.isNotEmpty(key.toString())){
                if(e.get("type") != null && StringUtils.isNotEmpty(e.get("type").toString())){
                    Set<String> t = new HashSet<>();
                    String[] types = e.get("type").toString().split(",");
                    Collections.addAll(t, types);
                    count = t.size();
                }
            }
            indata.put(key.toString(),count);
        });
        outmaps.forEach( e ->{
            Object key = e.get("level");
            int count = 0;
            if(key != null && StringUtils.isNotEmpty(key.toString())){
                if(e.get("type") != null && StringUtils.isNotEmpty(e.get("type").toString())){
                    Set<String> t = new HashSet<>();
                    String[] types = e.get("type").toString().split(",");
                    Collections.addAll(t, types);
                    count = t.size();
                }
            }
            outdata.put(key.toString(),count);
        });
        /*
        outmaps.forEach( e ->{
            Object key = e.get("classification_level");
            if(key != null && StringUtils.isNotEmpty(key.toString())){
                Object c = e.get("count");
                Integer count = (c == null ? 0 : Integer.parseInt(c.toString()));
                if(outdata.containsKey(key.toString())){
                    Integer oldcount =  Integer.parseInt(outdata.get(key.toString()).toString());
                    outdata.put(key.toString(),oldcount+count);
                }else{
                    outdata.put(key.toString(),count);
                }
            }
        });*/
        Map<String,Object> result = new HashMap<>();
        result.put("indata",indata);
        result.put("outdata",outdata);
        return VoBuilder.vd(result);
    }

    /**
     * 密级-用户数量分布
     * @param model
     * @return
     */
    @Override
    public VData<Map<String, Integer>> userleveldata(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file*",false);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("src_std_org_code", model.getDepartCode()));
        }
        Map<String,Integer> data = new HashMap<>();
        List<Map<String, Object>> maps = QueryTools.twoLevelAgg(queryModel, wrapper, "classification_level", "username", 1000, 1000, "count");
        maps.forEach(e ->{
            Object key = e.get("classification_level");
            if(key != null && StringUtils.isNotEmpty(key.toString())){
                if(data.containsKey(key.toString())){
                    data.put(key.toString(),data.get(key.toString())+1);
                }else{
                    data.put(key.toString(),1);
                }
            }
        });
        return VoBuilder.vd(data);
    }

    /**
     * 用户-涉密信息数量TOP
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> userFileTop(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file*",false);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("src_std_org_code", model.getDepartCode()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = QueryTools.simpleAgg(queryModel, wrapper, "username", 1000, "username", "count");
        //排序
        Collections.sort(maps, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                long count1 = Long.valueOf(o1.get("count").toString());
                long count2 = Long.valueOf(o2.get("count").toString());
                if(count1 > count2){
                    return -1;
                }else if(count1 == count2){
                    return 0;
                }else{
                    return 1;
                }
            }
        });
        return VoBuilder.vd(maps);
    }

    /**
     * 业务用户数列表
     * @return
     */
    @Override
    public VData<Map<String, Integer>> userBusinessList(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file*",false);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("src_std_org_code", model.getDepartCode()));
        }
        queryModel.setQueryBuilder(query);
        Map<String,Integer> data = new HashMap<>();
        List<Map<String, Object>> list = simpleAggWithTopHitAndConcatField(queryModel, wrapper, "username", 100, "username", "count", "business_list", "types", null,null);
        list.forEach(e ->{
            Object keys = e.get("types");
            if(keys != null && StringUtils.isNotEmpty(keys.toString())){
                Set<String> t = new HashSet<>();
                String[] types = e.get("types").toString().split(",");
                //去重
                Collections.addAll(t, types);
                t.forEach(p ->{
                    if(data.containsKey(p)){
                        data.put(p,data.get(p)+1);
                    }else{
                        data.put(p,1);
                    }
                });
            }
        });
        return VoBuilder.vd(data);
    }

    /**
     * 密级-应用数量分布
     * @return
     */
    @Override
    public VData<Map<String, Integer>> appleveldata(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("dst_std_org_code", model.getDepartCode()));
        }
        queryModel.setQueryBuilder(query);
        Map<String,Integer> data = new HashMap<>();
        List<Map<String, Object>> maps = QueryTools.twoLevelAgg(queryModel, wrapper, "classification_level", "src_std_sys_id", 1000, 1000, "count");
        maps.forEach(e ->{
            Object key = e.get("classification_level");
            if(key != null && StringUtils.isNotEmpty(key.toString())){
                if(data.containsKey(key.toString())){
                    data.put(key.toString(),data.get(key.toString())+1);
                }else{
                    data.put(key.toString(),1);
                }
            }
        });
        return VoBuilder.vd(data);
    }

    /**
     * 应用-涉密信息数量TOP
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> appFileTop(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        BoolQueryBuilder query = new BoolQueryBuilder();

        if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("dst_std_org_code", model.getDepartCode()));
        }
        queryModel.setQueryBuilder(query);
        //query.must(QueryBuilders.existsQuery("file_name"));
        List<Map<String, Object>> maps = simpleAggWithTopHit(queryModel, wrapper, "src_std_sys_id", 1000,"appno","count",new String[]{"src_std_sys_name"},new String[]{"appname"});

        //排序
        Collections.sort(maps, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                long count1 = Long.valueOf(o1.get("count").toString());
                long count2 = Long.valueOf(o1.get("count").toString());
                if(count1 > count2){
                    return -1;
                }else if(count1 == count2){
                    return 0;
                }else{
                    return 1;
                }
            }
        });
        return VoBuilder.vd(maps);
    }

    /**
     * 业务应用数列表
     * @return
     */
    @Override
    public VData<Map<String, Integer>> appBusinessList(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file*",false);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            query.must(QueryBuilders.termsQuery("dst_std_org_code", model.getDepartCode()));
        }
        queryModel.setQueryBuilder(query);
        Map<String,Integer> data = new HashMap<>();
        List<Map<String, Object>> list = simpleAggWithTopHitAndConcatField(queryModel, wrapper, "app_name", 100, "appname", "count", "business_list", "types", null,null);
        list.forEach(e ->{
            Object keys = e.get("types");
            if(keys != null && StringUtils.isNotEmpty(keys.toString())){
                Set<String> t = new HashSet<>();
                String[] types = e.get("types").toString().split(",");
                //去重
                Collections.addAll(t, types);
                t.forEach(p ->{
                    if(data.containsKey(p)){
                        data.put(p,data.get(p)+1);
                    }else{
                        data.put(p,1);
                    }
                });
            }
        });
        return VoBuilder.vd(data);
    }

    /**
     * 后台通信次数
     * @param model
     * @return
     */
    @Override
    public VData<Long> appSignalNum(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getAppNo())){
            query.must(QueryBuilders.termsQuery("dst_std_sys_id", model.getAppNo()));
            query.must(QueryBuilders.termsQuery("src_std_sys_id", model.getAppNo()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        Long total = 0L;
        if (searchResponse != null && searchResponse.getHits() != null) {
            total = searchResponse.getHits().getTotalHits().value;
        }
        return VoBuilder.vd(total);
    }

    /**
     * 应用访问次数分布和应用个数
     * @return
     */
    @Override
    public VData<Map<String, Object>> appVisitCount(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getAppNo())){
            query.must(QueryBuilders.termQuery("dst_std_sys_id", model.getAppNo()));
            query.mustNot(QueryBuilders.termQuery("src_std_sys_id", model.getAppNo()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = simpleAggWithTopHit(queryModel, wrapper, "src_std_sys_id", 1000, "appno", "count",new String[]{"src_std_sys_name"},new String[]{"appname"});
        Map<String, Object> result = new HashMap<>();
        result.put("appnum",maps.size());
        result.put("appdata",maps);
        return VoBuilder.vd(result);
    }

    /**
     * 文件上传下载数量分布
     * @return
     */
    @Override
    public VData<Map<String, Object>> fileDownUploadData(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        BoolQueryBuilder outquery = new BoolQueryBuilder();
        BoolQueryBuilder inquery = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getAppNo())){
            outquery.must(QueryBuilders.termsQuery("dst_std_sys_id", model.getAppNo()));
            inquery.must(QueryBuilders.termsQuery("dst_std_sys_id", model.getAppNo()));
        }
        outquery.must(QueryBuilders.termsQuery("file_dir", "1"));
        inquery.must(QueryBuilders.termsQuery("file_dir", "2"));
        queryModel.setQueryBuilder(outquery);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        Long uptotal = 0L;
        if (searchResponse != null && searchResponse.getHits() != null) {
            uptotal = searchResponse.getHits().getTotalHits().value;
        }
        queryModel.setQueryBuilder(inquery);
        Long downtotal = 0L;
        SearchResponse searchResponse2 = wrapper.getSearchResponse(queryModel);
        if (searchResponse2 != null && searchResponse2.getHits() != null) {
            downtotal = searchResponse2.getHits().getTotalHits().value;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("upload",uptotal);
        result.put("download",downtotal);
        return VoBuilder.vd(result);
    }

    /**
     * 业务类别、密级分布
     * @return
     */
    @Override
    public VData<Map<String, Object>> businessLevelData(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getAppNo())){
            query.must(QueryBuilders.termsQuery("src_std_sys_id", model.getAppNo()));
        }
        Map<String, Object> result = new HashMap<>();
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> inmaps = simpleAggWithTopHitAndConcatField(queryModel, wrapper, "classification_level", 100, "level", "count", "business_list", "type", null,null);
        for (Map<String, Object> m : inmaps){
            Object level = m.get("level");
            Object type = m.get("type");
            if(level != null && StringUtils.isNotEmpty(level.toString())){
                Integer num = 0;
                if(type != null && StringUtils.isNotEmpty(type.toString()) ){
                    Set<String> total = new HashSet<>();
                    String[] types = type.toString().split(",");
                    Collections.addAll(total, types);
                    num = total.size();
                }
                result.put(level.toString(),num);
            }
        }
        return VoBuilder.vd(result);
    }


    /**
     * 每个业务的最高密级
     * @param
     * @return
     */
    @Override
    public VData<Map<String, Integer>> businessLevelCount(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            BoolQueryBuilder sd = new BoolQueryBuilder();
            sd.should(QueryBuilders.termsQuery("dst_std_org_code", model.getDepartCode()));
            sd.should(QueryBuilders.termsQuery("src_std_org_code", model.getDepartCode()));
            query.must(sd);
        }
        Map<String,Integer> levelMap = new HashMap<>();
        levelMap.put("绝密",0);
        levelMap.put("机密",1);
        levelMap.put("秘密",2);
        levelMap.put("内部",3);
        levelMap.put("公开",4);
        Map<String, Integer> result = new HashMap<>();
        List<Map<String, Object>> inmaps = simpleAggWithTopHitAndConcatField(queryModel, wrapper, "classification_level", 100, "level", "count", "business_list", "type", null,null);
        for (Map<String, Object> m : inmaps){
            Object level = m.get("level");
            Object type = m.get("type");
            if(level != null && StringUtils.isNotEmpty(level.toString())){
                //密级
                Integer lnum = levelMap.get(level.toString());
                if(type != null && StringUtils.isNotEmpty(type.toString()) ){
                    Set<String> total = new HashSet<>();
                    String[] types = type.toString().split(",");
                    Collections.addAll(total, types);
                    total.forEach(e ->{
                        if(result.containsKey(e)){
                            //原密级
                            Integer oldnum = result.get(e);
                            Integer levelnum = lnum < oldnum ? lnum : oldnum;
                            result.put(e,levelnum);
                        }else{
                            result.put(e,4);
                        }
                    });
                }
            }
        }
        return VoBuilder.vd(result);
    }

    @Override
    public VData<Map<String, Integer>> departBaseInfo(ApplicationModel model) {
        Map<String, Integer> map = new HashMap<>();
        map.put("appNum",0);
        map.put("fileInfoCount",0);
        map.put("dealNum",0);
        map.put("businessCount",0);
        if(StringUtils.isNotEmpty(model.getDepartCode())){
            DepartVisitAppTotal visit = this.departVisitAppTotalMapper.selectById(model.getDepartCode());
            DepartSecretInfoTotal info =  this.departSecretInfoTotalMapper.selectById(model.getDepartCode());
            if(visit != null){
                map.put("appNum",visit.getAppCount().intValue());
            }
            if(info != null){
                map.put("fileInfoCount",info.getSecretFileCount().intValue());
                map.put("dealNum",info.getSecretFileNum().intValue());
                map.put("businessCount",info.getBusinessCount());
            }
        }
        return VoBuilder.vd(map);
    }

    /**
     * 通信详情
     * @param model
     * @return
     */
    @Override
    public VData<List<Map<String, Object>>> visitDetail(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        model.setBy("sip");
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        query.must(sd);
        if(StringUtils.isNotEmpty(model.getIp())){
            BoolQueryBuilder sdip = new BoolQueryBuilder();
            sdip.should(QueryBuilders.termQuery("sip", model.getIp()));
            sdip.should(QueryBuilders.termQuery("dip", model.getIp()));
            query.must(sdip);
        }
        /*
        if(StringUtils.isNotEmpty(departno)){
            query.mustNot(QueryBuilders.termsQuery("dst_std_org_code", departno));
        }*/
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> maps = fourLevelAggAndTopHit(queryModel, wrapper, "sip", "dip", "app_protocol", "dport", 10, "count","src_std_org_name,dst_std_org_name".split(","));
        return VoBuilder.vd(maps);
    }

    /**
     * 其他应用访问本应用次数
     * @param model
     * @return
     */
    @Override
    public VData<Long> otherSysVisitNum(ApplicationModel model) {
        Long num = 0L;
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-http",true);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getAppNo())){
            query.must(QueryBuilders.termQuery("dst_std_sys_id", model.getAppNo()));
        }
        if(StringUtils.isNotEmpty(model.getSrcAppNo())){
            query.must(QueryBuilders.termQuery("src_std_sys_id", model.getSrcAppNo()));
        }
        queryModel.setQueryBuilder(query);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        if (response != null && response.getHits() != null) {
            num = response.getHits().getTotalHits().value;
        }
        return VoBuilder.vd(num);
    }

    /**
     * 输入/输出、密级-文件分布饼图
     * @param model
     * @return
     */
    @Override
    public VData<Map<String, Object>> inAndOutFileDate(ApplicationModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, model, "netflow-app-file",true);
        BoolQueryBuilder outquery = new BoolQueryBuilder();
        BoolQueryBuilder inquery = new BoolQueryBuilder();
        /*
        if(StringUtils.isNotEmpty(model.getAppNo())){
            outquery.must(QueryBuilders.termsQuery("dst_std_sys_id", model.getAppNo()));
            inquery.must(QueryBuilders.termsQuery("dst_std_sys_id", model.getAppNo()));
        }

        if(StringUtils.isNotEmpty(model.getDepartCode())){
            outquery.must(QueryBuilders.termsQuery("src_std_org_code", model.getDepartCode()));
            inquery.must(QueryBuilders.termsQuery("src_std_org_code", model.getDepartCode()));
        }*/
        //outquery.must(QueryBuilders.existsQuery("file_list"));
        //inquery.must(QueryBuilders.existsQuery("file_list"));
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        inquery.must(sd);
        outquery.must(sd);
        outquery.must(QueryBuilders.termsQuery("file_dir", "2"));
        inquery.must(QueryBuilders.termsQuery("file_dir", "1"));
        queryModel.setQueryBuilder(outquery);
        List<Map<String, Object>> outmaps = QueryTools.simpleAgg(queryModel, wrapper, "classification_level", 1000, "level", "count");
        //List<Map<String, Object>> outmaps = this.genDataMap(queryModel, wrapper, new String[]{"file_list"}, new String[]{"fileList"});
        queryModel.setQueryBuilder(inquery);
        //List<Map<String, Object>> inmaps = this.genDataMap(queryModel, wrapper, new String[]{"file_list"}, new String[]{"fileList"});
        List<Map<String, Object>> inmaps = QueryTools.simpleAgg(queryModel, wrapper, "classification_level", 1000, "level", "count");
        Integer outfilenum = 0;
        Integer infilenum = 0;
        Map<String,Long> outmp = new HashMap<String,Long>();
        Map<String,Long> inmp = new HashMap<String,Long>();
        for(Map<String, Object> m : outmaps){
            Object count = m.get("count");
            Object level = m.get("level");
            if(count != null && StringUtils.isNotEmpty(count.toString())){
                outfilenum += Integer.parseInt(count.toString());
            }
            if(level != null && StringUtils.isNotEmpty(level.toString())){
                if(outmp.get(level.toString()) != null){
                    outmp.put(level.toString(),outmp.get(level.toString())+1L);
                }else{
                    outmp.put(level.toString(),1L);
                }
            }
        }
        for(Map<String, Object> m : inmaps){
            Object count = m.get("count");
            Object level = m.get("level");
            if(count != null && StringUtils.isNotEmpty(count.toString())){
                infilenum += Integer.parseInt(count.toString());
            }
            if(level != null && StringUtils.isNotEmpty(level.toString())){
                if(inmp.get(level.toString()) != null){
                    inmp.put(level.toString(),inmp.get(level.toString())+1L);
                }else{
                    inmp.put(level.toString(),1L);
                }
            }

        }

        /*
        for(Map<String, Object> m : outmaps){
            Object files = m.get("fileList");
            if(files != null && StringUtils.isNotEmpty(files.toString())){
                JSONArray array = JSONArray.parseArray(files.toString());
                outfilenum += array.size();
                array.forEach( e -> {
                    JSONObject o = (JSONObject)e;
                    Object level = o.get("classification_level");
                    if(level != null && StringUtils.isNotEmpty(level.toString())){
                        if(outmp.get(level.toString()) != null){
                            outmp.put(level.toString(),outmp.get(level.toString())+1L);
                        }else{
                            outmp.put(level.toString(),1L);
                        }
                    }
                });
            }
        }

        for(Map<String, Object> m : inmaps){
            Object files = m.get("fileList");
            if(files != null && StringUtils.isNotEmpty(files.toString())){
                JSONArray array = JSONArray.parseArray(files.toString());
                infilenum += array.size();
                array.forEach( e -> {
                    JSONObject o = (JSONObject)e;
                    Object level = o.get("classification_level");
                    if(level != null && StringUtils.isNotEmpty(level.toString())){
                        if(inmp.get(level.toString()) != null){
                            inmp.put(level.toString(),inmp.get(level.toString())+1L);
                        }else{
                            inmp.put(level.toString(),1L);
                        }
                    }
                });
            }
        }*/
        List<Map> outdata = new ArrayList<>();
        List<Map> indata = new ArrayList<>();
        outmp.keySet().forEach(s ->{
            Map m = new HashMap();
            m.put("level",s);
            m.put("count",outmp.get(s));
            outdata.add(m);
        });

        inmp.keySet().forEach(s ->{
            Map m = new HashMap();
            m.put("level",s);
            m.put("count",inmp.get(s));
            indata.add(m);
        });
        Map<String, Object> fileNum = new HashMap<>();
        fileNum.put("in",infilenum);
        fileNum.put("out",outfilenum);
        Map<String, Object> result = new HashMap<>();
        result.put("fileNum",fileNum);
        result.put("outdata",outmaps);
        result.put("indata",inmaps);
        return VoBuilder.vd(result);
    }

    private EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, PageModel model, String indexs,Boolean useTime) {
        EsQueryModel queryModel = new EsQueryModel();
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        } else {
            queryModel.setStartTime(TimeTools.getNowBeforeByDay(7));
            queryModel.setEndTime(TimeTools.getNowBeforeByDay2(1));
        }

        if(StringUtils.isNotEmpty(model.getBy())){
            queryModel.setSort(true);
            if("desc".equals(model.getBy())){
                queryModel.setSortOrder(SortOrder.DESC);
            }else{
                queryModel.setSortOrder(SortOrder.ASC);
            }
        }
        if(StringUtils.isNotEmpty(model.getOrder())){
            queryModel.setSortFields(new String[]{model.getOrder()});
        }
        if(useTime && queryModel.getStartTime() != null && queryModel.getEndTime() != null){
            List<String> allIndexList = new ArrayList<>();
            String[] ins = indexs.split(",");
            if (ins.length > 0) {
                for (String index : ins) {
                    allIndexList.addAll(wrapper.getIndexNames(index, queryModel.getStartTime(), queryModel.getEndTime()));
                }
            }
            if (!allIndexList.isEmpty()) {
                queryModel.setIndexNames(allIndexList.toArray(new String[allIndexList.size()]));
            }
        }else{
            queryModel.setIndexNames(indexs.split(","));
        }
        // 设置时间字段
        queryModel.setTimeField("event_time");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        //queryModel.setTypeName("_doc");
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }

    private static List<Map<String, Object>> simpleAggWithTopHitAndConcatField(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField,String concatField,String concatKey, String[] rowFields,String[] keys) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        TermsAggregationBuilder conagg = new TermsAggregationBuilder("conagg");
        conagg.field(concatField).size(size);
        agg.subAggregation(conagg);
        agg.subAggregation(AggregationBuilders.topHits("data").size(1));
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
                        List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>)((Map<String, Object>) aggItem.get("data")).get("hits")).get("hits");
                        Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                        Map<String, Object> tmp = new HashMap<>();
                        tmp.put(keyField, aggItem.get("key"));
                        tmp.put(valueField, aggItem.get("doc_count"));
                        if (rowFields != null && rowFields.length > 0) {
                            for(int i = 0; i<rowFields.length; i++){
                                String fd = rowFields[i];
                                if(keys != null){
                                    tmp.put(keys[i], row.get(fd));
                                }else{
                                    tmp.put(fd, row.get(fd));
                                }
                            }
                        }
                        StringBuffer concats = new StringBuffer();
                        if(aggItem.containsKey("conagg")){
                            Map<String, Object> conaggMap = (Map<String, Object>) aggItem.get("conagg");
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
        }
        return result;
    }

    private List<Map<String,Object>> genDataMap(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper,String[] keys,String[] labels){
        Map<String, Object> aggMap = wrapper.getAggResponse(queryModel);
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        if (aggMap != null && aggMap.containsKey("hits")) {
            Map<String, Object> dataAggMap = (Map<String, Object>) aggMap.get("hits");
            if (dataAggMap != null && dataAggMap.containsKey("hits")) {
                List<Map<String, Object>> aggItems = (List<Map<String, Object>>) dataAggMap.get("hits");
                for(Map<String, Object> m : aggItems){
                    if(m != null && m.containsKey("_source")){
                        Map<String, Object> detail = (Map<String, Object>) m.get("_source");
                        Map<String, Object> data = new HashMap<String, Object>();
                        for(int i = 0;i < keys.length;i++){
                            data.put(labels[i],detail.get(keys[i]));
                        }
                        list.add(data);
                    }
                }
            }
        }
        return list;
    }

    /**
     * 4级聚合
     * @param queryModel
     * @param wrapper
     * @param oneAggField
     * @param twoAggField
     * @param threeAggField
     * @param fourAggField
     * @param size
     * @param valueField
     * @return
     */
    public static List<Map<String, Object>> fourLevelAggAndTopHit(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String oneAggField, String twoAggField, String threeAggField,String fourAggField,int size, String valueField,String [] rowFields) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder oneAgg = new TermsAggregationBuilder("oneAgg");
        oneAgg.field(oneAggField).size(size);
        TermsAggregationBuilder secondAgg = new TermsAggregationBuilder("secondAgg");
        secondAgg.field(twoAggField).size(size);
        TermsAggregationBuilder threeAgg = new TermsAggregationBuilder("threeAgg");
        threeAgg.field(threeAggField).size(size);
        secondAgg.subAggregation(threeAgg);
        TermsAggregationBuilder fourAgg = new TermsAggregationBuilder("fourAgg");
        fourAgg.field(fourAggField).size(size);
        threeAgg.subAggregation(fourAgg);
        fourAgg.subAggregation(AggregationBuilders.topHits("data").size(1));

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
                                twoAggItems.forEach(twoitem -> {
                                    if (twoitem.containsKey("threeAgg")) {
                                        Map<String, Object> threeAggMap = (Map<String, Object>) twoitem.get("threeAgg");
                                        if(threeAggMap.containsKey("buckets")){
                                            List<Map<String, Object>> threeAggItems = (List<Map<String, Object>>) threeAggMap.get("buckets");
                                            threeAggItems.forEach(threeItem ->{
                                                if (threeItem.containsKey("fourAgg")) {
                                                    Map<String, Object> fourAggMap = (Map<String, Object>) threeItem.get("fourAgg");
                                                    if(fourAggMap.containsKey("buckets")){
                                                        List<Map<String, Object>> fourAggItems = (List<Map<String, Object>>) fourAggMap.get("buckets");
                                                        fourAggItems.forEach(fourItem ->{
                                                            Map<String, Object> tmp = new HashMap<>();
                                                            List<Map<String, Object>> topRow = (List<Map<String, Object>>) ((Map<String, Map<String, Object>>) ((Map<String, Object>) fourItem.get("data")).get("hits")).get("hits");
                                                            Map<String, Object> row = (Map<String, Object>) topRow.get(0).get("_source");
                                                            if (rowFields != null && rowFields.length > 0) {
                                                                for (int i = 0;i<rowFields.length;i++) {
                                                                    String fd = rowFields[i];
                                                                    tmp.put(fd, row.get(fd));
                                                                }
                                                            }
                                                            tmp.put(oneAggField, aggItem.get("key"));
                                                            tmp.put(twoAggField, twoitem.get("key"));
                                                            tmp.put(threeAggField, threeItem.get("key"));
                                                            tmp.put(fourAggField, fourItem.get("key"));
                                                            tmp.put(valueField, fourItem.get("doc_count"));
                                                            result.add(tmp);
                                                        });
                                                    }
                                                }
                                            });
                                        }
                                    }

                                });
                            }
                        }
                    });
                }
            }
        }
        return result;
    }

    public static List<Map<String, Object>> simpleAggWithTopHit(EsQueryModel queryModel, QueryTools.QueryWrapper wrapper, String aggField, int size, String keyField, String valueField, String[] rowFields,String[] keys) {
        List<Map<String, Object>> result = new ArrayList<>();
        TermsAggregationBuilder agg = new TermsAggregationBuilder("agg");
        agg.field(aggField).size(size);
        agg.subAggregation(AggregationBuilders.topHits("data").size(1));
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
}