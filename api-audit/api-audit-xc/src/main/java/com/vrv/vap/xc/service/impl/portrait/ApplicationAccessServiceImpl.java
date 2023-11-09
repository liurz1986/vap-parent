package com.vrv.vap.xc.service.impl.portrait;

import cn.hutool.core.collection.IterUtil;
import com.vrv.vap.toolkit.constant.ExcelEnum;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.pojo.AppSysManager;
import com.vrv.vap.xc.service.portrait.ApplicationAccessService;
import com.vrv.vap.xc.service.IAppSysManagerService;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApplicationAccessServiceImpl implements ApplicationAccessService {

    @Resource
    private FileTransferServiceImpl fileTransferService;
    @Resource
    private IAppSysManagerService iAppSysManagerService;

    public static Pair<EsQueryModel, QueryTools.QueryWrapper> buildQuery(ObjectPortraitModel model, BuildQuery buildQuery) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel;
        if (buildQuery.isMultipleIndex()) {
            queryModel = QueryTools.buildQueryModelMult(wrapper, model, buildQuery.getIndexes(), "event_time");
        } else {
            queryModel = QueryTools.buildQueryModel(wrapper, model, buildQuery.getIndex(), "event_time");
        }
        BoolQueryBuilder query = new BoolQueryBuilder();
        if (buildQuery.isDiagram() && StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("sip", model.getDevIp()));
            query.mustNot(QueryBuilders.termQuery("dip", model.getDevIp()));
            // 应用系统访问
        } else if (StringUtils.isNotEmpty(model.getAppNo())) {
            query.must(QueryBuilders.termsQuery("src_std_sys_id", model.getAppNo()));
        } else if (StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("sip", model.getDevIp()));
        } else if (StringUtils.isNotEmpty(model.getRangIp())) {
            String rangIp = model.getRangIp();
            if (rangIp.contains(",")) {
                String[] rangIpList = rangIp.split(",");
                QueryTools.buildIpRangeQuery(rangIpList, query);
            } else {
                QueryTools.buildIpRangeQuery(new String[]{rangIp}, query);
            }
        } else if (IterUtil.isNotEmpty(model.getIpRangeList())) {
            List<CommunicationModel> ipRangeList = model.getIpRangeList();
            String[] list = ipRangeList.stream().map(CommunicationModel::getRangeIps).toArray(String[]::new);
            QueryTools.buildIpRangeQuery2(list, query);
        }
        if (model.isSecret()) {
            BoolQueryBuilder sd = new BoolQueryBuilder();
            sd.should(QueryBuilders.termsQuery("classification_level", "机密", "绝密", "秘密"));
            query.must(sd);
        }
        if (StringUtils.isNotEmpty(model.getDevTypeGroup())) {
            query.must(QueryBuilders.termQuery("src_std_dev_type_group", model.getDevTypeGroup()));
        }
        if (StringUtils.isNotEmpty(model.getDstStdDevTypeGroup())) {
            query.must(QueryBuilders.termQuery("dst_std_dev_type_group", model.getDstStdDevTypeGroup()));
        }
        if (StringUtils.isNotEmpty(model.getDstStdSysName())) {
            query.must(QueryBuilders.termQuery("dst_std_sys_name", model.getDstStdSysName()));
        }
        if (StringUtils.isNotEmpty(model.getDip())) {
            query.must(QueryBuilders.termQuery("dip", model.getDip()));
        }
        if(StringUtils.isNotEmpty(model.getFileInfo())){
            BoolQueryBuilder queryBuilder = new BoolQueryBuilder();
            queryBuilder.should(QueryBuilders.wildcardQuery("file_name", "*" + model.getFileInfo() + "*"));
            queryBuilder.should(QueryBuilders.wildcardQuery("file_md5", "*" + model.getFileInfo() + "*"));
            query.must(queryBuilder);
        }
        if (StringUtils.isNotEmpty(model.getFileLevel())) {
            query.must(QueryBuilders.termQuery("classification_level_code", model.getFileLevel()));
        }
        if (StringUtils.isNotEmpty(model.getFileType())) {
            query.must(QueryBuilders.termQuery("file_type", model.getFileType()));
        }
        if (StringUtils.isNotEmpty(model.getFileDir())) {
            query.must(QueryBuilders.termQuery("file_dir", model.getFileDir()));
        }
        if(StringUtils.isNotEmpty(model.getSport())){
            query.must(QueryBuilders.termQuery("sport", model.getSport()));
        }
        if (StringUtils.isNotEmpty(model.getDport())) {
            query.must(QueryBuilders.termQuery("dport", model.getDport()));
        }
        if (StringUtils.isNotEmpty(model.getProtocol())) {
            query.must(QueryBuilders.termQuery("app_protocol", model.getProtocol()));
        }
        if (StringUtils.isNotEmpty(model.getUrl())) {
            query.must(QueryBuilders.termQuery("url", model.getUrl()));
        }
        if (StringUtils.isNotEmpty(model.getFileSize())) {
            QueryTools.buildFileSizeQuery(model.getFileSize(), query);
        }
        queryModel.setQueryBuilder(query);
        return Pair.of(queryModel, wrapper);
    }

    public List<Map<String, String>> getAppNameByAppId(List<Map<String, String>> details){
        List<AppSysManager> list = iAppSysManagerService.list();
        if (IterUtil.isEmpty(list) && IterUtil.isEmpty(details)) return new ArrayList<>();
        for (AppSysManager app : list) {
            for (Map<String, String> entry : details) {
                if (Objects.equals(app.getAppNo(), entry.get("dst_std_sys_id"))) {
                    entry.put("app_url", app.getAppUrl());
                    entry.put("operation_url", app.getOperationUrl());
                }
            }
        }
        return details;
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
    public VData<List<Map<String, Object>>> urlTimes(ObjectPortraitModel model) {
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
        buildQuery.setAggField("dst_std_sys_name");
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.aggAndDate(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> durationTrend(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_HTTP);
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.twoLevelAggAndDateAgg2(pair.getFirst(), pair.getSecond(), "dip",
                "dst_std_sys_name", 10000, 10, entry.getDateField(),entry.getDateFieldKey(),
                entry.getDateFormat(), entry.getInterval(), entry.getOffset(), true);
        List<Map<String, Object>> maps = assembleData(model, list);
        return VoBuilder.vd(maps);
    }

    public List<Map<String, Object>> assembleData(ObjectPortraitModel model, List<Map<String, Object>> list) {
        List<Map<String, Object>> durationList = getDuration(model);
        return list.stream().peek(entry -> durationList.stream()
                .filter(duration -> entry.get("dip").equals(duration.get("dip")))
                .findFirst()
                .ifPresent(duration -> entry.put("duration", duration.get("duration")))).collect(Collectors.toList());
    }

    private List<Map<String, Object>> getDuration(ObjectPortraitModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel2(wrapper, model, LogTypeConstants.APP_VISIT_COUNT);
        BoolQueryBuilder query = new BoolQueryBuilder();
        if (StringUtils.isNotEmpty(model.getDevIp())) {
            query.must(QueryBuilders.termQuery("sip", model.getDevIp()));
        }
        if (Objects.nonNull(model.getMyStartTime()) && Objects.nonNull(model.getMyEndTime())) {
            long startTimeStamp = model.getMyStartTime().toInstant().toEpochMilli() / 1000;
            long endTimeStamp = model.getMyEndTime().toInstant().toEpochMilli() / 1000;
            query.must(QueryBuilders.rangeQuery("start_time").gt(startTimeStamp).lt(endTimeStamp));
        }
        queryModel.setQueryBuilder(query);
        return QueryTools.simpleAgg(queryModel, wrapper, "dip", 1000, "ip", "count");
    }

    @Override
    public VList<Map<String, String>> detail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_HTTP);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model, false);
        List<Map<String, String>> result = getAppNameByAppId(mapVList.getList());
        mapVList.setList(result);
        return mapVList;
    }

    @Override
    public VData<Export.Progress> export(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_HTTP);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = buildQuery(model, buildQuery);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,true);
        List<Map<String, String>> result = getAppNameByAppId(mapVList.getList());
        return fileTransferService.export(result, ExcelEnum.SYS_VISIT_DETAIL, model.getExportName());
    }
}
