package com.vrv.vap.xc.service.impl.portrait;

import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.portrait.SecurityService;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class SecurityServiceImpl implements SecurityService {

    @Resource
    private FileTransferServiceImpl fileTransferService;

    private Pair<EsQueryModel, QueryTools.QueryWrapper> buildQuery(ObjectPortraitModel model, BuildQuery buildQuery) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, buildQuery.getIndex(), "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        if (StringUtils.isEmpty(model.getDip())) {
            query.must(QueryBuilders.termQuery("sip", model.getDevIp()));
        } else {
            query.must(QueryBuilders.termQuery("dip", model.getDip()));
        }
        if (StringUtils.isEmpty(model.getDstStdDevTypeGroup())) {
            query.must(QueryBuilders.termQuery("src_std_dev_type_group", model.getDevTypeGroup()));
        } else {
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDstStdDevTypeGroup()));
        }
        map.from(model.getProtocol()).to(m-> query.must(QueryBuilders.termQuery("app_protocol", model.getProtocol())));
        map.from(model.getSport()).to(m-> query.must(QueryBuilders.termQuery("sport", model.getSport())));
        map.from(model.getDport()).to(m-> query.must(QueryBuilders.termQuery("dport", model.getDport())));
        queryModel.setQueryBuilder(query);
        return Pair.of(queryModel, wrapper);
    }

    private VData<List<Map<String, Object>>> getListVData(ObjectPortraitModel model, BuildQuery buildQuery) {
        buildQuery.setSize(100);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        EsQueryModel queryModel = pair.getFirst();
        QueryTools.QueryWrapper queryWrapper = pair.getSecond();
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list;
        if (buildQuery.isDate()) {
            list = QueryTools.dateAgg(queryModel, queryWrapper, entry);
        } else {
            list = QueryTools.simpleAggregation(queryModel, queryWrapper, entry);
        }
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> timesAnalysis(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_TCP);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        buildQuery.setAggField("dip");
        buildQuery.setKeyField("ip");
        return getListVData(model, buildQuery);
    }

    @Override
    public VData<List<Map<String, Object>>> timesTop(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_TCP);
        buildQuery.setAggField("dip");
        buildQuery.setKeyField("ip");
        return getListVData(model, buildQuery);
    }

    @Override
    public VData<List<Map<String, Object>>> serverProtocolAndPort(ObjectPortraitModel model, String aggField, String keyField) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_TCP);
        buildQuery.setAggField(aggField);
        buildQuery.setKeyField(keyField);
        return getListVData(model, buildQuery);
    }

    @Override
    public VList<Map<String, String>> serviceDetail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_TCP);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,false);
    }

    @Override
    public VData<Export.Progress> export(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_TCP);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,true);
        return fileTransferService.export(mapVList.getList(), ExcelEnum.SECU_DETAIL, model.getExportName());
    }
}
