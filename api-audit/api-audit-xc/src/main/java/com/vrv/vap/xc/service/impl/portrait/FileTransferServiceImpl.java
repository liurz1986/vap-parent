package com.vrv.vap.xc.service.impl.portrait;

import cn.hutool.core.date.DateTime;
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
import com.vrv.vap.xc.constants.LogTypeConstants;
import com.vrv.vap.xc.model.BuildQuery;
import com.vrv.vap.xc.model.EsQueryModel;
import com.vrv.vap.xc.model.ExchangeDTO;
import com.vrv.vap.xc.model.ObjectPortraitModel;
import com.vrv.vap.xc.service.portrait.FileTransferService;
import com.vrv.vap.xc.tools.DictTools;
import com.vrv.vap.xc.tools.PrettyMemoryUtil;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class FileTransferServiceImpl implements FileTransferService {

    private static final Log log = LogFactory.getLog(LocalOperationServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;

    private static final Map<String, Function<Object, String>> fieldNameToDictId = new HashMap<>();

    static {
        fieldNameToDictId.put("file_dir", v -> dict(v, "68ccdf6f-89ef-4528-a973-3ca4fb4509c3"));
        fieldNameToDictId.put("file_type", v -> dict(v, "68ccdf6f-89ef-4528-a973-3ca4fb4509c4"));
        fieldNameToDictId.put("app_protocol", v -> dict(v, "68ccdf6f-89ef-4528-a973-3ca4fb4509c6"));
        fieldNameToDictId.put("u_level", v -> dict(v, "a81c5d1e-c7e4-f272-89b5-39c0e2bf7be8"));
        fieldNameToDictId.put("file_level", v -> dict(v, "f5a4ae5b-3cee-a84f-7471-8f23ezjg0500"));
        fieldNameToDictId.put("op_result", v -> dict(v, "f18ce10c-4ddf-867b-df2d-94b278d0000e"));
        fieldNameToDictId.put("dst_std_dev_type_group", v -> dict(v, "467d3000-3bc8-9129-9356-3e9fb82ab6c5"));
        fieldNameToDictId.put("std_dev_type_group", v -> dict(v, "467d3000-3bc8-9129-9356-3e9fb82ab6c5"));
        fieldNameToDictId.put("resource_type_group", v -> dict(v, "467d3000-3bc8-9129-9356-3e9fb82ab6c5"));
        fieldNameToDictId.put("src_std_dev_type_group", v -> dict(v, "467d3000-3bc8-9129-9356-3e9fb82ab6c5"));

        fieldNameToDictId.put("total_byte", v -> {
            double value = Double.parseDouble(v.toString());
            return PrettyMemoryUtil.prettyByteSize((long) value);
        });
        fieldNameToDictId.put("default", v -> null == v ? "" : v.toString());
    }
    @Override
    public VData<List<Map<String, Object>>> fileDiagram(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setDiagram(true);
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_FILE);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(pair.getFirst(), pair.getSecond(), "dip",
                "file_dir", 10000, 10, "count", "dst_std_dev_type_group".split(","));
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> fileUpDownTrend(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_FILE);
        buildQuery.setAggField("file_dir");
        buildQuery.setKeyField("fileDir");
        buildQuery.setInterval(model.getInterval());
        buildQuery.setDate(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        List<Map<String, Object>> list = QueryTools.aggAndDate(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VData<List<Map<String, Object>>> fileInfo(ObjectPortraitModel model, String aggField, String keyField) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_FILE);
        buildQuery.setAggField(aggField);
        buildQuery.setKeyField(keyField);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        ExchangeDTO entry = QueryTools.buildQueryCondition(buildQuery);
        if ("fileSize".equals(keyField)){
            entry.setCalc(true);
        }
        List<Map<String, Object>> list = QueryTools.simpleAggregation(pair.getFirst(), pair.getSecond(), entry);
        return VoBuilder.vd(list);
    }

    @Override
    public VList<Map<String, String>> fileTransferDetail(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_FILE);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        return QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,false);
    }

    @Override
    public VData<Export.Progress> fileTransferDetailExport(ObjectPortraitModel model) {
        BuildQuery buildQuery = new BuildQuery();
        buildQuery.setIndex(LogTypeConstants.NET_FLOW_FILE);
        buildQuery.setSort(true);
        Pair<EsQueryModel, QueryTools.QueryWrapper> pair = QueryTools.buildQuery(model, buildQuery);
        VList<Map<String, String>> mapVList = QueryTools.searchResponse(pair.getFirst(), pair.getSecond(), model,true);
        List<Map<String, String>> allDate = mapVList.getList();
        return export(allDate, ExcelEnum.FILE_TRANS_DETAIL, model.getExportName());
    }

    public VData<Export.Progress> export(List<Map<String, String>> allDate, ExcelEnum excelEnum, String exportName) {
        exportName = exportName.contains("/") ? exportName.replace("/", "|") : exportName;
        List<Map<String, String>> resultSortList = allDate.stream().sorted(Comparator.comparing(e -> e.get("event_time"),
                Comparator.reverseOrder())).collect(Collectors.toList());
        int index = 0;
        List<Map<String, String>> exportList = new ArrayList<>();
        for (Map<String, String> entry : resultSortList) {
            entry.put("num", String.valueOf(++index));
            if (entry.containsKey("login_time")) {
                DateTime dateTime = new DateTime(entry.get("login_time"));
                DateTime gmt8DateTime = dateTime.setTimeZone(TimeZone.getTimeZone("GMT+8"));
                entry.put("login_time", gmt8DateTime.toString());
            }
            if (entry.containsKey("file_size")) {
                String fileSize = PrettyMemoryUtil.prettyByteSize(Long.parseLong(entry.get("file_size")));
                entry.put("file_size", fileSize);
            }
            exportList.add(entry);
        }
        final long totalSize = exportList.size();
        List<ExcelData> list = new ArrayList<>();
        ExcelInfo info = new ExcelInfo(excelEnum, PathTools.getExcelPath(exportName));
        ExcelData data = new ExcelData(info, totalSize, new ArrayList<>());
        list.add(data);
        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = escape(excelEnum, map);
            return innerList.toArray(new String[0]);
        }).start(WriteHandler.fun(p -> {
            final int batch = 1000;
            Lists.partition(exportList, batch).forEach(l -> {
                try {
                    p.writeBatchBean(0, l);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            });
        }, redisTemplate)));
    }

    /**
     * 字段转义
     *
     * @param excelEnum
     * @param map
     * @return
     */
    private List<String> escape(ExcelEnum excelEnum, Map<String, Object> map) {
        return Arrays.stream(excelEnum.getFields()).map(s -> fieldNameToDictId.getOrDefault(s, fieldNameToDictId.get("default")).apply(map.get(s))).collect(Collectors.toList());
    }

    private static String dict(Object v, String dictId) {
        return v == null ? "" : DictTools.translate(dictId, v.toString());
    }
}
