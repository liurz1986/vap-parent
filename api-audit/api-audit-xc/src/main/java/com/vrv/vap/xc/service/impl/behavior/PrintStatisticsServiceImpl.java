package com.vrv.vap.xc.service.impl.behavior;

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
import com.vrv.vap.xc.model.*;
import com.vrv.vap.xc.service.behavior.PrintStatisticsService;
import com.vrv.vap.xc.tools.DictTools;
import com.vrv.vap.xc.tools.QueryTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.histogram.DateHistogramInterval;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PrintStatisticsServiceImpl implements PrintStatisticsService {
    private static final Log log = LogFactory.getLog(PrintStatisticsServiceImpl.class);
    @Autowired
    private RedisTemplate redisTemplate;

    private static final Map<String, Function<Object, String>> fieldNameToDictId = new HashMap<>();

    static {
        fieldNameToDictId.put("file_level", v -> dict(v, "f5a4ae5b-3cee-a84f-7471-8f23ezjg0500"));
        fieldNameToDictId.put("op_type", v -> dict(v, "199424e5-0631-c06e-89c9-c1f33aa7a510"));
        fieldNameToDictId.put("op_result", v -> dict(v, "f18ce10c-4ddf-867b-df2d-94b278d0000e"));
        fieldNameToDictId.put("std_user_type", v -> dict(v, "f5a4ae5b-3cee-a84f-7471-8f23ezjg0200"));
        fieldNameToDictId.put("std_dev_type_group", v -> dict(v, "467d3000-3bc8-9129-9356-3e9fb82ab6c5"));
        fieldNameToDictId.put("std_dev_safety_marign", v -> dict(v, "19abd31b-0d0c-47af-b80d-4ce4074ebf57"));
        fieldNameToDictId.put("std_terminal_type", v -> dict(v, "3dd5a42b-a1a3-53d8-113a-2232668ca8d9"));
        fieldNameToDictId.put("data_source", v -> "0".equals(v.toString()) ? "主审" : "打印刻录");
        fieldNameToDictId.put("default", v -> null == v ? "" : v.toString());
    }

    public EsQueryModel commonBuildModel(QueryTools.QueryWrapper wrapper, PrintBurnModel model) {
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", model.getOpType()));
        queryModel.setQueryBuilder(query);
        return queryModel;
    }

    private Pair<DateHistogramInterval, String> buildDateHistogram(String interval) {
        String inter = DateHistogramInterval.DAY.toString();
        String tfm = "yyyy-MM-dd";
        if ("1".equals(interval)) {
            inter = DateHistogramInterval.HOUR.toString();
            tfm = "yyyy-MM-dd HH";
        } else if ("3".equals(interval)) {
            inter = DateHistogramInterval.MONTH.toString();
            tfm = "yyyy-MM";
        }
        return Pair.of(new DateHistogramInterval(inter), tfm);
    }

    /**
     * 按时间统计打印数量
     *
     * @param model
     * @return
     */
    @Override
    public VList<Map<String, Object>> printNumByTime(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        Pair<DateHistogramInterval, String> pair = buildDateHistogram(model.getInterval());
        List<Map<String, Object>> list = QueryTools.dateAndSumAgg(queryModel, wrapper, "file_num", "event_time", "date", pair.getFirst(), pair.getSecond(), 8);
        return VoBuilder.vl(list.size(), list);
    }

    /**
     * 按打印数量统计用户排行
     *
     * @param model
     * @return
     */
    @Override
    public VList<Map<String, Object>> printOrderByUser(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "std_user_no", 10, "file_num", "userNo", "count", new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    /**
     * 按部门统计打印数量
     *
     * @param model
     * @return
     */
    @Override
    public VList<Map<String, Object>> printOrderByOrg(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "std_org_code", 10, "file_num", "orgCode", "count", new String[]{"std_org_name"});
        return VoBuilder.vl(list.size(), list);
    }

    /**
     * 按设备统计打印数量
     *
     * @param model
     * @return
     */
    @Override
    public VList<Map<String, Object>> printOrderByDev(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "dev_ip", 10, "file_num", "devIp", "count", new String[]{"dev_name"});
        return VoBuilder.vl(list.size(), list);
    }

    /**
     * 按时间统计打印设备数量
     *
     * @param model
     * @return
     */
    @Override
    public VList<Map<String, Object>> printDevByTime(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        Pair<DateHistogramInterval, String> pair = buildDateHistogram(model.getInterval());
        List<Map<String, Object>> list = QueryTools.simpleDateTermAndSumAgg(queryModel, wrapper, "event_time", "date", pair.getFirst(), 8, pair.getSecond(), "dev_name", 100, "file_num", "devName", "count", null);
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printUserNonWorkTime(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        BoolQueryBuilder queryBuilder = (BoolQueryBuilder) queryModel.getQueryBuilder();
        QueryTools.buildNonWorkTimeQuery(queryModel, queryBuilder, model.getInterval());
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count", new String[]{"username"}, new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printFileType(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "file_type", 100, "fileType", "count");
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printFileDoc(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", model.getOpType()));
        query.must(QueryBuilders.termQuery("file_type", "doc"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count", new String[]{"username"}, new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printFilePdf(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", model.getOpType()));
        query.must(QueryBuilders.termQuery("file_type", "pdf"));
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count", new String[]{"username"}, new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printFileTypeTotal(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_user_no", "file_type", 10, 100, "count", new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printFileTypeCountTrend(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        Pair<DateHistogramInterval, String> pair = buildDateHistogram(model.getInterval());
        List<Map<String, Object>> list = QueryTools.aggAndDate(queryModel, wrapper, "file_type", "event_time", "date", 10, pair.getFirst(), pair.getSecond(), 8, "count");
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printFileLevel(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "file_level", 100, "fileLevel", "count");
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printCountByLevel(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", model.getOpType()));
        if (StringUtils.isNotEmpty(model.getLevel())) {
            query.must(QueryBuilders.termQuery("file_level", model.getLevel()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count", new String[]{"username"}, new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printCountByLevelTrend(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        Pair<DateHistogramInterval, String> pair = buildDateHistogram(model.getInterval());
        List<Map<String, Object>> list = QueryTools.aggAndDate(queryModel, wrapper, "file_level", "event_time", "date", 10, pair.getFirst(), pair.getSecond(), 8, "count");
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printLevelByUser(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_user_no", "file_level", 10, 100, "count", new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printCountByTime(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        Pair<DateHistogramInterval, String> pair = buildDateHistogram(model.getInterval());
        List<Map<String, Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time", pair.getFirst(), pair.getSecond(), 8, "date", "count");
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printCountByUser(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count", new String[]{"username"}, new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printCountByOrg(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_org_code", 10, "orgCode", "count", new String[]{"std_org_name"}, new String[]{"orgName"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printResultInfo(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleAgg(queryModel, wrapper, "op_result", 10, "result", "count");
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printResultUser(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", model.getOpType()));
        if (StringUtils.isNotEmpty(model.getResult())) {
            query.must(QueryBuilders.termQuery("op_result", model.getResult()));
        }
        queryModel.setQueryBuilder(query);
        List<Map<String, Object>> list = QueryTools.simpleAggWithTopHitAndOrder(queryModel, wrapper, "std_user_no", 10, "userNo", "count", new String[]{"username"}, new String[]{"username"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printResultTrend(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        Pair<DateHistogramInterval, String> pair = buildDateHistogram(model.getInterval());
        List<Map<String, Object>> list = QueryTools.aggAndDate(queryModel, wrapper, "op_result", "event_time", "date", 10, pair.getFirst(), pair.getSecond(), 8, "count");
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printResultByOrg(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.twoLevelAggToHits(queryModel, wrapper, "std_org_code", "op_result", 100, 100, "count", new String[]{"std_org_name"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printOrburnTrend(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        query.must(QueryBuilders.termQuery("op_type", model.getOpType()));
        queryModel.setQueryBuilder(query);
        Pair<DateHistogramInterval, String> pair = buildDateHistogram(model.getInterval());
        List<Map<String, Object>> list = QueryTools.dateAgg(queryModel, wrapper, "event_time", pair.getFirst(), pair.getSecond(), 8, "date", "count");
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printCountByHour(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        Pair<DateHistogramInterval, String> pair = buildDateHistogram(model.getInterval());
        List<Map<String, Object>> queryList = QueryTools.aggAndDate(queryModel, wrapper, "op_hour", "event_time", "date", 100, pair.getFirst(), pair.getSecond(), 8, "count");
        LinkedHashMap<String, Map<String, Integer>> result = queryList.stream().collect(LinkedHashMap::new, (map, data) -> {
            String date = data.get("date").toString();
            int opHour = Integer.parseInt(data.get("opHour").toString());
            int count = Integer.parseInt(data.get("count").toString());
            String key = "其他";
            if (8 <= opHour && opHour < 10) {
                key = "8:00-10:00";
            } else if (opHour >= 10 && opHour < 12) {
                key = "10:00-12:00";
            } else if (opHour >= 12 && opHour < 14) {
                key = "12:00-14:00";
            } else if (opHour >= 14 && opHour < 16) {
                key = "14:00-16:00";
            } else if (opHour >= 16 && opHour < 18) {
                key = "16:00-18:00";
            }
            map.computeIfAbsent(date, k -> new HashMap<>()).merge(key, count, Integer::sum);
        }, Map::putAll);
        List<Map<String, Object>> resultList = result.entrySet().stream().map(entry -> {
            Map<String, Object> d = new HashMap<>();
            d.put("date", entry.getKey());
            d.put("value", entry.getValue());
            return d;
        }).collect(Collectors.toList());
        return VoBuilder.vl(resultList.size(), resultList);
    }


    @Override
    public VList<Map<String, Object>> printFileSizeInfo(PrintBurnModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = commonBuildModel(wrapper, model);
        List<Map<String, Object>> list = QueryTools.simpleTermAndSumAgg(queryModel, wrapper, "file_name", 100000, "file_num", "fileName", "count", new String[]{"file_size"});
        return VoBuilder.vl(list.size(), list);
    }

    @Override
    public VList<Map<String, Object>> printFileSize(PrintBurnModel model) {
        List<Map<String, Object>> result = new ArrayList<>();
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel maxModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        Map<String, Number> maps = QueryTools.statsAggregation(maxModel, wrapper, "file_size");
        long maxSize = maps.get("max").longValue();
        long minSize = maps.get("min").longValue();
        long pend = (maxSize - minSize) / 3;
        if (pend == 0) {
            Map<String, Object> map = new HashMap<>();
            map.put("key", maxSize);
            map.put("value", maps.get("count"));
            result.add(map);
            return VoBuilder.vl(result.size(), result);
        }
        long[] array = new long[]{minSize, minSize + pend, minSize + pend * 2, maxSize};
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        for (int i = 0; i < 3; i++) {
            BoolQueryBuilder query = new BoolQueryBuilder();
            query.must(QueryBuilders.termQuery("op_type", model.getOpType()));
            long min = array[i];
            long max = array[i + 1];
            query.must(QueryBuilders.rangeQuery("file_size").gte(min).lt(max));
            queryModel.setQueryBuilder(query);
            SearchResponse response = wrapper.getSearchResponse(queryModel);
            long total = 0;
            if (response != null && response.getHits() != null) {
                total = response.getHits().getTotalHits().value;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("key", min + "-" + max);
            map.put("value", total);
            result.add(map);
        }
        return VoBuilder.vl(result.size(), result);
    }

    @Override
    public VList<Map<String, String>> printDetail(PrintDetailModel model) {
        Pair<EsQueryModel, QueryTools.QueryWrapper> query = buildQuery(model);
        EsQueryModel queryModel = query.getFirst();
        QueryTools.QueryWrapper wrapper = query.getSecond();
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModel);
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), "event_time");
            long total = searchResponse.getHits().getTotalHits().value;
            return VoBuilder.vl(total, list);
        }
        return VoBuilder.vl(0, new ArrayList<>());
    }


    private Pair<EsQueryModel, QueryTools.QueryWrapper> buildQuery(PrintDetailModel model) {
        QueryTools.QueryWrapper wrapper = QueryTools.build();
        EsQueryModel queryModel = QueryTools.buildQueryModel(wrapper, model, LogTypeConstants.PRINT_AUDIT, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        String fileSize = model.getFileSzie();
        String fileNum = model.getFileNum();
        PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
        mapper.from(model.getFileName()).to(m -> query.must(QueryBuilders.wildcardQuery("file_name", "*" + model.getFileName() + "*")));
        mapper.from(model.getUsername()).to(m -> query.must(QueryBuilders.wildcardQuery("username", "*" + model.getUsername() + "*")));
        mapper.from(model.getDevName()).to(m -> query.must(QueryBuilders.wildcardQuery("dev_name", "*" + model.getDevName() + "*")));
        mapper.from(model.getDevIp()).to(m -> query.must(QueryBuilders.termQuery("dev_ip", model.getDevIp())));
        mapper.from(model.getFileType()).to(m -> query.must(QueryBuilders.termQuery("file_type", model.getFileType())));
        mapper.from(model.getFileLevel()).to(m -> query.must(QueryBuilders.termQuery("file_level", model.getFileLevel())));
        mapper.from(fileSize).to(m -> query.must(QueryBuilders.rangeQuery("file_size").gte(split(fileSize).getFirst()).lte(split(fileSize).getSecond())));
        mapper.from(fileNum).to(m -> query.must(QueryBuilders.rangeQuery("file_num").gte(split(fileNum).getFirst()).lte(split(fileNum).getSecond())));
        mapper.from(model.getBusiness()).to(m -> query.must(QueryBuilders.termQuery("business_list", model.getBusiness())));
        mapper.from(model.getTerminalType()).to(m -> query.must(QueryBuilders.termQuery("std_terminal_type", model.getTerminalType())));
        mapper.from(model.getUserNo()).to(m -> query.must(QueryBuilders.termQuery("std_user_no", model.getUserNo())));
        mapper.from(model.getOrgCode()).to(m -> query.must(QueryBuilders.termQuery("std_org_code", model.getOrgCode())));
        mapper.from(model.getOpResult()).to(m -> query.must(QueryBuilders.termQuery("op_result", model.getOpResult())));
        mapper.from(model.getOpType()).to(m -> query.must(QueryBuilders.termQuery("op_type", model.getOpType())));
        queryModel.setQueryBuilder(query);
        return Pair.of(queryModel, wrapper);
    }


    public Pair<Long, Long> split(String fileNum) {
        String[] split = fileNum.split(",");
        long min = Long.parseLong(split[0]);
        long max = Long.parseLong(split[1]);
        return Pair.of(min, max);
    }


    @Override
    public VData<Export.Progress> exportDetail(PrintDetailModel model) {
        Pair<EsQueryModel, QueryTools.QueryWrapper> query = buildQuery(model);
        EsQueryModel queryModel = query.getFirst();
        QueryTools.QueryWrapper wrapper = query.getSecond();
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
        ExcelInfo info = new ExcelInfo(ExcelEnum.PRINT_DETAIL, PathTools.getExcelPath(model.getModalTitle()));
        ExcelData data = new ExcelData(info, totalSize, new ArrayList<>());
        list.add(data);
        return VoBuilder.vd(Export.build(list, map -> {
            List<String> innerList = escape(ExcelEnum.PRINT_DETAIL, map);
            return innerList.toArray(new String[0]);
        }).start(WriteHandler.fun(p -> {
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
