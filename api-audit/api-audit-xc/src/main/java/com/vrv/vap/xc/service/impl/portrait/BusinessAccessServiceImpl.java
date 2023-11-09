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
import com.vrv.vap.xc.service.portrait.BusinessAccessService;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BusinessAccessServiceImpl implements BusinessAccessService {
    @Resource
    private FileTransferServiceImpl fileTransferService;

    public static Pair<EsQueryModel, QueryTools.QueryWrapper> buildQuery(ObjectPortraitModel model, BuildQuery buildQuery) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, buildQuery.getIndex(), "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(model.getAppNo()).to(m -> query.must(QueryBuilders.termsQuery("src_std_sys_id", model.getAppNo())));
        map.from(model.getSip()).to(m -> query.must(QueryBuilders.termQuery("sip", model.getSip())));
        map.from(model.getDevTypeGroup()).to(m -> query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDevTypeGroup())));
        map.from(model.getFileName()).to(m -> query.must(QueryBuilders.wildcardQuery("file_name", "*" + model.getFileName() + "*")));
        map.from(model.getFileMd5()).to(m -> query.must(QueryBuilders.wildcardQuery("file_md5", "*" + model.getFileMd5() + "*")));
        map.from(model.getFileLevel()).to(m -> query.must(QueryBuilders.termQuery("classification_level_code", model.getFileLevel())));
        map.from(model.getFileType()).to(m -> query.must(QueryBuilders.termQuery("file_type", model.getFileType())));
        map.from(model.getFileDir()).to(m -> query.must(QueryBuilders.termQuery("file_dir", model.getFileDir())));
        map.from(model.getUrl()).to(m->query.must(QueryBuilders.termQuery("url", model.getUrl())));
        queryModel.setQueryBuilder(query);
        return Pair.of(queryModel, wrapper);
    }
    @Override
    public VData<List<Map<String, Object>>> diagram(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_HTTP);
        buildQuery.setDiagram(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(pair.getFirst(), pair.getSecond(), "dip",
                "dst_std_sys_name", 10000, 10, "count", "dst_std_dev_type_group".split(","));
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> urlTop(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_HTTP);
        buildQuery.setAggField("url");
        buildQuery.setKeyField("url");
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.simpleAggregation(pair.getFirst(), pair.getSecond(), entry);
        List<Map<String, Object>> top10 = list.stream().limit(10).collect(Collectors.toList());
        return VoBuilder.vd(top10);
    }

    @Override
    public VData<List<Map<String, Object>>> trend(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_HTTP);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.dateAgg(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VList<Map<String, String>> detail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_HTTP);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,false);
    }

    @Override
    public VData<Export.Progress> export(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_HTTP);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(),model, true);
        List<Map<String, String>> resultSortList = mapVList.getList();
        return fileTransferService.export(resultSortList, ExcelEnum.BUSINESS_VISIT_DETAIL, model.getExportName());
    }
}
