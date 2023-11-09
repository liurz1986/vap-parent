package com.vrv.vap.xc.service.impl.portrait;

import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.BuildQuery;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.ExchangeDTO;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.NetworkBoundaryService;
import com.vrv.vap.xc.tools.QueryTools;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

@Service
public class NetworkBoundaryServiceImpl implements NetworkBoundaryService {
    @Resource
    private FileTransferServiceImpl fileTransferService;

    @Override
    public VData<List<Map<String, Object>>> relationshipDiagram(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP,LogTypeConstants.NET_FLOW_UDP});
        buildQuery.setDiagram(true);
        buildQuery.setMultipleIndex(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(pair.getFirst(), pair.getSecond(), "dip",
                "dst_std_dev_type_group", 10000, 10, "count",
                "dst_std_dev_type_group".split(","));
        return VoBuilder.vd(list);
    }

    @Override
    public VList<Map<String, String>> networkVisitDetail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setMultipleIndex(true);
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP,LogTypeConstants.NET_FLOW_UDP});
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,false);
    }

    @Override
    public VData<Export.Progress> networkVisitDetailExport(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setMultipleIndex(true);
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP,LogTypeConstants.NET_FLOW_UDP});
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,true);
        List<Map<String, String>> resultSortList = mapVList.getList();
        return fileTransferService.export(resultSortList, ExcelEnum.DEV_VISIT_DETAIL, model.getExportName());
    }

    @Override
    public VData<List<Map<String, Object>>> visitProtocolAndPort(ObjectPortraitModel model, String aggField, String keyField) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setMultipleIndex(true);
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP,LogTypeConstants.NET_FLOW_UDP});
        buildQuery.setAggField(aggField);
        buildQuery.setKeyField(keyField);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.simpleAggregation(pair.getFirst(), pair.getSecond(),entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> pckSizeRanking(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setMultipleIndex(true);
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP, LogTypeConstants.NET_FLOW_UDP});
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg2(pair.getFirst(), pair.getSecond(), "dip",
                100, "total_byte", "dip", "count");
        return VoBuilder.vd(list);
    }
    @Override
    public VData<List<Map<String, Object>>> visitTrend(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP,LogTypeConstants.NET_FLOW_UDP});
        buildQuery.setMultipleIndex(true);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.dateAgg(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> sessionTimes(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setMultipleIndex(true);
        buildQuery.setIndexes(new String[]{LogTypeConstants.NET_FLOW_TCP,LogTypeConstants.NET_FLOW_UDP});
        buildQuery.setAggField("dst_std_dev_type_group");
        buildQuery.setKeyField("dstStdDevTypeGroup");
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.simpleAggregation(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

}
