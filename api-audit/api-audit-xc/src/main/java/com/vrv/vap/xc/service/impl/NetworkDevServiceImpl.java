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
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.SecurityModel;
import com.vrv.vap.xc.service.NetworkDevService;
import com.vrv.vap.xc.tools.DictTools;
import com.vrv.vap.xc.tools.ExcelValHandleTools;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class NetworkDevServiceImpl implements NetworkDevService {
    private static final Log log = LogFactory.getLog(NetworkDevServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;

    public EsQueryModel sort(BoolQueryBuilder query, EsQueryModel queryModel) {
        queryModel.setQueryBuilder(query);
        queryModel.setSortFields(new String[]{"event_time"});
        queryModel.setSort(true);
        queryModel.setSortOrder(SortOrder.DESC);
        return queryModel;
    }
    @Override
    public VList<Map<String, String>> operationDetail(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "operation-audit", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("resource_ip", model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getUserAccount())){
            query.must(QueryBuilders.wildcardQuery("user_account","*"+model.getUserAccount()+"*"));
        }
        if (StringUtils.isNotEmpty(model.getConnType())) {
            query.must(QueryBuilders.termQuery("conn_type", DictTools.translate("68ccdf6f-89ef-4528-a973-3ca4fb4509c6", model.getConnType()).toLowerCase()));
        }
        if(StringUtils.isNotEmpty(model.getOrder())){
            query.must(QueryBuilders.termQuery("opt_detail", model.getOrder()));
        }
        if(StringUtils.isNotEmpty(model.getUrl())){
            query.must(QueryBuilders.wildcardQuery("operation_url_list","*"+model.getUrl()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getRecord())){
            query.must(QueryBuilders.wildcardQuery("operation_record","*"+model.getRecord()+"*"));
        }
        queryModel.setQueryBuilder(query);
        queryModel = sort(query, queryModel);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        long total = 0;
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits());
            total = searchResponse.getHits().getTotalHits().value;
            return VoBuilder.vl(total, list);
        }
        return VoBuilder.vl(total, new ArrayList<>());
    }

    @Override
    public VData<Export.Progress> operationDetailExport(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "operation-audit", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("resource_ip", model.getDevIp()));
        }
        if(StringUtils.isNotEmpty(model.getUserAccount())){
            query.must(QueryBuilders.wildcardQuery("user_account","*"+model.getUserAccount()+"*"));
        }
        if(StringUtils.isNotEmpty(model.getConnType())){
            query.must(QueryBuilders.termQuery("conn_type", model.getConnType()));
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

        final long totalSize = allDate.size();
        List<ExcelData> list = new ArrayList<>();
        ExcelInfo info = new ExcelInfo(
                ExcelEnum.NETWORK_DETAIL,
                PathTools.getExcelPath(ExcelEnum.NETWORK_DETAIL.getFilename()));
        ExcelData data = new ExcelData(info, totalSize, new ArrayList<>());
        list.add(data);
        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = new ArrayList<>();
            Object tmp2;
            for (String filedName : ExcelEnum.NETWORK_DETAIL.getFields()) {
                tmp2 = map.get(filedName);
                innerList.add(null == tmp2 ? "" : tmp2.toString());
            }
            return innerList.toArray(new String[0]);
        }).start(
                WriteHandler.fun(p -> {
                    final int batch = 1000;
                    Lists.partition(allDate, batch).forEach(l -> {
                        try {
                            p.writeBatchBean(0, l);
                        } catch (Exception e) {
                            log.error(e.getMessage(), e);
                        }

                    });
                }, redisTemplate)));
    }

    @Override
    public VList<Map<String, String>> interconnectionNetInfo(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        query.must(sd);
        if(StringUtils.isNotEmpty(model.getSip())){
            query.must(QueryBuilders.termQuery("sip", model.getSip()));
        }
        if(StringUtils.isNotEmpty(model.getDip())){
            query.must(QueryBuilders.termQuery("dip", model.getDip()));
        }
        if(StringUtils.isNotEmpty(model.getBytes())){
            String[] split = model.getBytes().split(",");
            long min = Long.parseLong(split[0]);
            long max = Long.parseLong(split[1]);
            query.must(QueryBuilders.rangeQuery("total_byte").gte(min).lte(max));
        }
        if(StringUtils.isNotEmpty(model.getPcks())){
            String[] split = model.getPcks().split(",");
            long min = Long.parseLong(split[0]);
            long max = Long.parseLong(split[1]);
            query.must(QueryBuilders.rangeQuery("total_pkt").gte(min).lte(max));
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
    public VData<Export.Progress> interconnectionNetInfoExport(SecurityModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, "netflow-tcp", "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(model.getDevIp())){
            query.must(QueryBuilders.termQuery("device_ip", model.getDevIp()));
        }
        BoolQueryBuilder sd = new BoolQueryBuilder();
        sd.should(QueryBuilders.termsQuery("visit_type", "2"));
        sd.should(QueryBuilders.termsQuery("visit_type", "3"));
        query.must(sd);
        if(StringUtils.isNotEmpty(model.getSip())){
            query.must(QueryBuilders.termQuery("sip", model.getSip()));
        }
        if(StringUtils.isNotEmpty(model.getDip())){
            query.must(QueryBuilders.termQuery("dip", model.getDip()));
        }
        if(StringUtils.isNotEmpty(model.getBytes())){
            String[] split = model.getBytes().split(",");
            long min = Long.parseLong(split[0]);
            long max = Long.parseLong(split[1]);
            query.must(QueryBuilders.rangeQuery("total_byte").gte(min).lte(max));
        }
        if(StringUtils.isNotEmpty(model.getPcks())){
            String[] split = model.getPcks().split(",");
            long min = Long.parseLong(split[0]);
            long max = Long.parseLong(split[1]);
            query.must(QueryBuilders.rangeQuery("total_pkt").gte(min).lte(max));
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
                ExcelEnum.INTERCONN_NET_DETAIL,
                PathTools.getExcelPath(ExcelEnum.INTERCONN_NET_DETAIL.getFilename()));
        ExcelData data = new ExcelData(info, totalSize, new ArrayList<>());
        list.add(data);
        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = new ArrayList<>();
            Object tmp2;
            for (String filedName : ExcelEnum.INTERCONN_NET_DETAIL.getFields()) {
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
}
