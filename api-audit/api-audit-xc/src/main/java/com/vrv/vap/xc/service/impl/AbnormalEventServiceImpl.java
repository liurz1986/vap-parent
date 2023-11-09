package com.vrv.vap.xc.service.impl;

import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.EsResult;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.PageModel;
import com.vrv.vap.xc.service.AbnormalEventService;
import com.vrv.vap.xc.tools.QueryTools;
import com.vrv.vap.xc.vo.AbnormalEventQuery;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AbnormalEventServiceImpl implements AbnormalEventService {

    private static final String INDEX = "alarmeventmanagement";

    @Override
    public VData<List<Map<String, Object>>> abnormalTag(AbnormalEventQuery query) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, query, INDEX,true);
        BoolQueryBuilder qb = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(query.getAppId())){
            qb.must(QueryBuilders.termsQuery("extendEntityAttr.application_id", query.getAppId()));
        }
        if(StringUtils.isNotEmpty(query.getUserId())){
            qb.must(QueryBuilders.termsQuery("extendEntityAttr.staff_id", query.getUserId()));
        }
        queryModel.setQueryBuilder(qb);
        return VoBuilder.vd(QueryTools.simpleAgg(queryModel, wrapper, "riskEventName", 9999, "tagName", "count"));
    }

    @Override
    public EsResult abnormalEvent(AbnormalEventQuery query) {
        // 默认根据时间倒序
        if (StringUtils.isEmpty(query.getOrder())) {
            query.setOrder("triggerTime");
            query.setBy("desc");
        }
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = buildQueryModel(wrapper, query, INDEX,true);
        BoolQueryBuilder qb = new BoolQueryBuilder();
        if(StringUtils.isNotEmpty(query.getAppId())){
            qb.must(QueryBuilders.termsQuery("extendEntityAttr.application_id", query.getAppId()));
        }
        if(StringUtils.isNotEmpty(query.getUserId())){
            qb.must(QueryBuilders.termsQuery("extendEntityAttr.staff_id", query.getUserId()));
        }
        queryModel.setQueryBuilder(qb);
        SearchResponse response = wrapper.getSearchResponse(queryModel);
        EsResult esResult = wrapper.wrapResult(response, queryModel);
        return esResult;
    }

    private EsQueryModel buildQueryModel(QueryTools.QueryWrapper wrapper, PageModel model, String indexs, Boolean useTime) {
        EsQueryModel queryModel = new EsQueryModel();
        // 告警索引时间需要格式化
        queryModel.setNeedTimeFormat(true);
        queryModel.setTimeFormat("yyyy-MM-dd HH:mm:ss");
        queryModel.setStart(model.getMyStart());
        queryModel.setCount(model.getMyCount());
        if (model.getMyStartTime() != null && model.getMyEndTime() != null) {
            queryModel.setStartTime(model.getMyStartTime());
            queryModel.setEndTime(model.getMyEndTime());
        } else {
            queryModel.setStartTime(TimeTools.getNowBeforeByDay(7));
            queryModel.setEndTime(TimeTools.getNowBeforeByDay2(0));
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
        queryModel.setIndexName(indexs);
        // 设置时间字段
        queryModel.setTimeField("triggerTime");
        queryModel.setUseFilter(false);
        queryModel.setUseTimeRange(true);
        queryModel.setTypeName(QueryTools.TYPE);
        wrapper.setTimeRangeFilter(queryModel);
        queryModel.setQueryBuilder(QueryBuilders.boolQuery());
        return queryModel;
    }
}