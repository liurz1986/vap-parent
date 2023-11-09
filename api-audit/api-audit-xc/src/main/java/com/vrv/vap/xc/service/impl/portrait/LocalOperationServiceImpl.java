package com.vrv.vap.xc.service.impl.portrait;

import cn.hutool.core.util.ArrayUtil;
import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.portrait.LocalOperationService;
import com.vrv.vap.xc.tools.QueryTools;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LocalOperationServiceImpl implements LocalOperationService {
    @Resource
    private FileTransferServiceImpl fileTransferService;

    private Pair<EsQueryModel, QueryTools.QueryWrapper> buildQuery(ObjectPortraitModel model, BuildQuery buildQuery, boolean work) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, buildQuery.getIndex(), "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        map.from(model.getDevIp()).to(m -> query.must(QueryBuilders.termQuery("dev_ip", model.getDevIp())));
        map.from(model.getDevTypeGroup()).to(m -> query.must(QueryBuilders.termQuery("std_dev_type_group", model.getDevTypeGroup())));
        map.from(model.getOpType()).to(m -> query.must(QueryBuilders.termQuery("op_type", model.getOpType())));
        map.from(model.getLoginResult()).to(m -> query.must(QueryBuilders.termQuery("op_result", model.getLoginResult())));
        map.from(model.getFileLevel()).to(m -> query.must(QueryBuilders.termQuery("file_level", model.getFileLevel())));
        map.from(model.getFileName()).to(m -> query.must(QueryBuilders.termQuery("file_name", "*" + model.getFileName() + "*")));
        map.from(model.getVidPidInfo()).to(m -> {
            BoolQueryBuilder builder = new BoolQueryBuilder();
            builder.should(QueryBuilders.wildcardQuery("std_dyperiph_vid", "*" + model.getVidPidInfo() + "*"));
            builder.should(QueryBuilders.wildcardQuery("std_dyperiph_pid", "*" + model.getVidPidInfo() + "*"));
            query.must(builder);
        });
        if (model.isDedicatedMedia()){
            BoolQueryBuilder sd = new BoolQueryBuilder();
            // 移动介质密级 0-内部、1-秘密、2-机密、3-绝密、 4-公开
            sd.should(QueryBuilders.termsQuery("u_level", "0", "1", "2", "3", "4"));
            query.must(sd);
        }
        if (!work) QueryTools.buildNonWorkTimeQuery(queryModel, query,model.getInterval());
        queryModel.setQueryBuilder(query);
        return Pair.of(queryModel, wrapper);
    }

    private VData<List<Map<String, Object>>> getListVData(ObjectPortraitModel model, BuildQuery buildQuery, boolean work) {
        buildQuery.setSize(100);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, work);
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
    public VData<List<Map<String, Object>>> loginCount(ObjectPortraitModel model, boolean work) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.TERMINAL_LOGIN);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        return getListVData(model, buildQuery, work);
    }

    @Override
    public VList<Map<String, String>> loginCountDetail(ObjectPortraitModel model, boolean work) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.TERMINAL_LOGIN);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, work);
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,false);
    }

    @Override
    public VData<Export.Progress> loginCountDetailExport(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.TERMINAL_LOGIN);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery,true);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,true);
        List<Map<String, String>> allDate = mapVList.getList();
        return fileTransferService.export(allDate, ExcelEnum.LOGIN_DETAIL, model.getExportName());
    }

    @Override
    public VData<List<Map<String, Object>>> mediumUse(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.SPECIALUDISK_USE);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        buildQuery.setAggField("std_dyperiph_sn");
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, true);
        EsQueryModel queryModel = pair.getFirst();
        QueryTools.QueryWrapper queryWrapper = pair.getSecond();
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.cardinalityAndDateAgg(queryModel, queryWrapper, entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> mediumUseFrequency(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.SPECIALUDISK_USE);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        return getListVData(model, buildQuery, model.isDedicatedMedia());
    }

    @Override
    public VList<Map<String, String>> mediumUseDetail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.SPECIALUDISK_USE);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, model.isDedicatedMedia());
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,false);
    }

    @Override
    public VList<Map<String, String>> mediumUseNumDetail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.SPECIALUDISK_USE);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, model.isDedicatedMedia());
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model, true);
        List<Map<String, String>> list = mapVList.getList();
        if (ArrayUtil.isNotEmpty(list)) {
            Collection<Map<String, String>> sn = list.stream().distinct()
                    .collect(Collectors.toMap(map -> map.get("std_dyperiph_sn"), m -> m, (k, v) -> k)).values();
            return VoBuilder.vl(sn.size(), new ArrayList<>(sn));
        }
        return VoBuilder.vl(0, new ArrayList<>());
    }

    @Override
    public VData<Export.Progress> mediumUseDetailExport(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.SPECIALUDISK_USE);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery,true);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,true);
        List<Map<String, String>> allDate = mapVList.getList();
        return fileTransferService.export(allDate, ExcelEnum.DEDICATED_MEDIA_DETAIL, model.getExportName());
    }

    @Override
    public VData<Export.Progress> mediumUseNumDetailExport(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.SPECIALUDISK_USE);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, true);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model, true);
        List<Map<String, String>> exportList = mapVList.getList();
        Collection<Map<String, String>> sn = exportList.stream().distinct()
                .collect(Collectors.toMap(map -> map.get("std_dyperiph_sn"), m -> m, (k, v) -> k)).values();
        return fileTransferService.export(new ArrayList<>(sn), ExcelEnum.DEDICATED_MEDIA_DETAIL, model.getExportName());
    }

    @Override
    public VData<List<Map<String, Object>>> printOrBurnCount(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.PRINT_AUDIT);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery,true);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        entry.setSumAddField("file_num");
        List<Map<String, Object>> list = QueryTools.sumAggDate(pair.getFirst(), pair.getSecond(), entry,false);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> printOrBurnFrequency(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.PRINT_AUDIT);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        return getListVData(model, buildQuery, true);
    }

    @Override
    public VList<Map<String, String>> printOrBurnCountDetail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.PRINT_AUDIT);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery, true);
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,false);
    }

    @Override
    public VData<Export.Progress> printOrBurnCountDetailExport(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.PRINT_AUDIT);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery,true);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,true);
        List<Map<String, String>> allDate = mapVList.getList();
        return fileTransferService.export(allDate, Objects.equals("1", model.getOpType())
                ? ExcelEnum.DEV_BURN_DETAIL : ExcelEnum.DEV_PRINT_DETAIL, model.getExportName());
    }

    @Override
    public VData<List<Map<String, Object>>> printOrBurnLevelCount(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.PRINT_AUDIT);
        buildQuery.setAggField("file_level");
        buildQuery.setKeyField("fileLevel");
        return getListVData(model, buildQuery, true);
    }
}
