package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.model.BaseDictAll;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.admin.common.excel.ExcelInfo;
import com.vrv.vap.admin.common.excel.out.ExcelData;
import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.common.excel.out.WriteHandler;
import com.vrv.vap.admin.common.util.ES7Tools;
import com.vrv.vap.admin.common.util.JsonQueryTools;
import com.vrv.vap.admin.common.util.PathTools;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.mapper.VisualWidgetMapper;
import com.vrv.vap.admin.model.DiscoverIndex;
import com.vrv.vap.admin.model.DiscoverIndexField;
import com.vrv.vap.admin.model.VisualWidgetModel;
import com.vrv.vap.admin.service.IndexService;
import com.vrv.vap.admin.service.VisualWidgetService;
import com.vrv.vap.admin.vo.ExportWidgetVO;
import com.vrv.vap.admin.vo.QueryModel;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;


/**
 * Created by CodeGenerator on 2018/03/26.
 */
@Service
@Transactional
public class VisualWidgetImpl extends BaseServiceImpl<VisualWidgetModel> implements VisualWidgetService {

    private static final Logger log = LoggerFactory.getLogger(VisualWidgetImpl.class);

    @Resource
    private VisualWidgetMapper visualWidgetMapper;

    @Autowired
    IndexService indexService;

    @Autowired
    private BaseDictAllService baseDictAllService;

    /**
     * 索引名称格式
     */
    @Value("${index.format}")
    public String INDEX_NAME_FORMATE;

    @Override
    public Export.Progress exportList(ExportWidgetVO exportWidgetVO) {
        String[] params = exportWidgetVO.getParam();
        QueryModel queryModel = new QueryModel();
        BeanUtils.copyProperties(exportWidgetVO, queryModel);
        queryModel.setStart_(queryModel.getStart_());
        queryModel.setCount_(1000);
        // 排序
        queryModel.setSort(true);
        queryModel.setSortFields(new String[]{queryModel.getTimeField()});
        // 索引字段
        String indexName = queryModel.getIndexName();
        DiscoverIndex discoverIndex = new DiscoverIndex();
        discoverIndex.setIndexid(indexName);
        discoverIndex = indexService.findOne(discoverIndex);
        if (discoverIndex == null) {
            log.info("找不到对应的索引！");
            return null;
        }
        String indexField = discoverIndex.getIndexfields();
        List<DiscoverIndexField> discoverIndexFieldList = JSON.parseArray(indexField, DiscoverIndexField.class);
        String[] resultFields = queryModel.getResultFields();
        // 数据字典
        Map<String, Map<String, String>> dicMap = this.getDictMap(discoverIndexFieldList);
        // 字段名称及描述
        String[] fields = this.getNameFields(discoverIndexFieldList,resultFields);
        String[] fieldDesc = this.getNameDescs(discoverIndexFieldList,resultFields);
        // 需要格式处理的时间字段
        List<Map<String, String>> allFieldsList = this.getAllFieldList(indexName);
        final List<String> timeList = getTimeFields(allFieldsList);
        // top数据处理
        Gson gson = new Gson();
        String topStr = exportWidgetVO.getTopList();
        String[] topFields = this.getTopNameFields(topStr,"field");
        String[] topFieldDesc = this.getTopNameFields(topStr,"title");
        Map<String, Object> topMap = gson.fromJson(JsonSanitizer.sanitize(topStr), Map.class);
        List<Map<String, Object>> topDataList = (List<Map<String, Object>>) topMap.get("data");

        List<ExcelData> excelDataList = new ArrayList<>();
        ExcelInfo topInfo = new ExcelInfo("小组件明细", topFields, topFieldDesc, "TOP数据", true, PathTools.getExcelPath("小组件明细"), null);
        int topCount = topMap != null ? topDataList.size() : 0;
        ExcelData topData = new ExcelData(topInfo, topCount, new ArrayList<>());
        excelDataList.add(topData);

        Export.Progress progress = null;
        TimeValue keepAlive = new TimeValue(ES7Tools.ES_CACHE_TIME);
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        if (StringUtils.isNotEmpty(queryModel.getQuery())) {
            if (params != null && params.length > 0) {
                List<SearchResponse> searchResponseList = new ArrayList<>();
                List<String> scrollIdList = new ArrayList<>();
                List<Long> totalList = new ArrayList<>();
                for (String param : params) {
                    int j = 0;
                    QueryModel qModel = new QueryModel();
                    BeanUtils.copyProperties(queryModel, qModel);
                    String query = queryModel.getQuery();
                    query = query.replace("${param}", param);
                    qModel.setQuery(query);
                    qModel.setUseTimeRange(true);
                    qModel.setIndexName(indexName);
                    SearchResponse response = wrapper.getSearchResponseScroll(buildQueryModel(wrapper, qModel),keepAlive);
                    String scrollId = response.getScrollId();
                    long total = response.getHits().getTotalHits().value > ES7Tools.getExportMax() ? ES7Tools.getExportMax() : response
                            .getHits().getTotalHits().value;
                    searchResponseList.add(response);
                    scrollIdList.add(scrollId);
                    totalList.add(total);
                    String sheetName = topDataList.size() >= j+1 ? (String) topDataList.get(j).get("key") : "详情";
                    ExcelInfo info = new ExcelInfo("小组件明细", fields, fieldDesc, sheetName, true, PathTools.getExcelPath("小组件明细"), null);
                    ExcelData data = new ExcelData(info, total, new ArrayList<>());
                    excelDataList.add(data);
                    j++;
                }

                progress = Export.build(excelDataList);
                progress.start(WriteHandler.fun(p -> {
                    int i = 0;
                    for (SearchResponse response : searchResponseList) {
                        final int index = i + 1;
                        final long total = totalList.get(i);
                        final String scrollId = scrollIdList.get(i);
                        if (index == 1) {
                            //top数据
                            toTopExcel(topDataList, p);
                        }
                        if (StringUtils.isNotEmpty(queryModel.getQuery())) {
                            long leftCount = total;
                            int count = toExcel(response, p, fields, dicMap, timeList, leftCount, index);
                            while (true && count > 0) {
                                if (count >= total) {
                                    break;
                                }
                                SearchResponse response2 = wrapper.getSearchResponseScrollById(scrollId, keepAlive);
                                leftCount = total - count;
                                count += toExcel(response2, p, fields, dicMap, timeList, leftCount, index);
                            }
                        }
                        i++;
                    }
                }));
            }
        } else {
            progress = Export.build(excelDataList);
            progress.start(WriteHandler.fun(p -> {
                //top数据
                toTopExcel(topDataList, p);
            }));
        }
        return progress;
    }

    /**
     * 封装QueryModel
     *
     * @param wrapper
     * @Param queryModel
     * @return
     */
    private QueryModel buildQueryModel(ES7Tools.QueryWrapper wrapper, final QueryModel queryModel) {
        wrapper.setTimeRangeFilter(queryModel);
        // 生成索引
        queryModel.setIndexNames(ES7Tools.getIndexNames(queryModel));
        // 处理查询语句
        queryModel.setQueryBuilder(JsonQueryTools.getQueryBuilder(queryModel.getQuery()));
        if (StringUtils.isNotEmpty(queryModel.getOrderType()) && "asc".equals(queryModel.getOrderType())) {
            queryModel.setSortOrder(SortOrder.ASC);
        } else {
            queryModel.setSortOrder(SortOrder.DESC);
        }
        return queryModel;
    }

    private void toTopExcel(List<Map<String, Object>> topList, Export.Progress pro) {
        List<Map<String, Object>> writeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(topList)) {
            for (Map<String, Object> top : topList) {
                writeList.add(top);
            }
        }
        pro.writeBatchMap(0, writeList);
    }

    private int toExcel(SearchResponse response, Export.Progress pro, final String[] fields, Map<String, Map<String, String>> dicMap, List<String> timeFields, long leftCount, int index) {
        int num = 0;
        List<Map<String, Object>> writeList = new ArrayList<>();
        for (SearchHit hit : response.getHits()) {
            num++;
            try {
                Map<String, Object> data = hit.getSourceAsMap();
                String[] v = new String[fields.length];
                for (int i = 0; i < fields.length; i++) {
                    //遍历属性得到值
                    String value = "";
                    String field = fields[i];
                    if (data.containsKey(fields[i])) {
                        value = data.get(fields[i]) == null ? "" : data.get(fields[i]).toString();
                    }
                    //数据字典处理
                    if (dicMap.containsKey(field) && value != null && dicMap.get(field).containsKey(value)) {
                        value = dicMap.get(field).get(value);
                    }
                    //时间格式处理
                    if (timeFields != null && timeFields.contains(fields[i])) {
                        value = TimeTools.utc2Local(value);
                    }
                    v[i] = value;
                    data.put(fields[i], value);
                }
                writeList.add(data);
            } catch (IllegalArgumentException e) {
                log.error("", e);
            }
            if (num >= leftCount) {
                break;
            }
        }
        pro.writeBatchMap(index, writeList);
        return num;
    }

    private String[] getNameFields(List<DiscoverIndexField> discoverIndexFieldList,String[] resultFields) {
        List<String> nameFields = new ArrayList<>();
        discoverIndexFieldList.stream().filter(p -> p.isDisplayed() || "true".equals(p.isDisplayed())).forEach(p -> {
            if (resultFields != null && resultFields.length > 0) {
                if (Arrays.asList(resultFields).contains(p.getName())) {
                    nameFields.add(p.getName());
                }
            } else {
                nameFields.add(p.getName());
            }
        });
        return nameFields.toArray(new String[nameFields.size()]);
    }

    private String[] getNameDescs(List<DiscoverIndexField> discoverIndexFieldList,String[] resultFields) {
        List<String> nameDescs = new ArrayList<>();
        discoverIndexFieldList.stream().filter(p -> p.isDisplayed() || "true".equals(p.isDisplayed())).forEach(p -> {
            if (resultFields != null && resultFields.length > 0) {
                if (Arrays.asList(resultFields).contains(p.getName())) {
                    nameDescs.add(StringUtils.isEmpty(p.getNameDesc()) ? p.getName() : p.getNameDesc());
                }
            } else {
                nameDescs.add(StringUtils.isEmpty(p.getNameDesc()) ? p.getName() : p.getNameDesc());
            }
        });
        return nameDescs.toArray(new String[nameDescs.size()]);
    }

    private String[] getTopNameFields(String topStr,String fieldName) {
        // top数据处理
        Gson gson = new Gson();
        Map<String, Object> topMap = gson.fromJson(JsonSanitizer.sanitize(topStr), Map.class);
        List fieldList = new ArrayList();
        if (topMap != null) {
            List<Map<String, Object>> defineList = (List<Map<String, Object>>) topMap.get("define");
            if (CollectionUtils.isNotEmpty(defineList)) {
                for (Map<String, Object> define : defineList) {
                    String field = (String) define.get(fieldName);
                    fieldList.add(field);
                }
            }
        }
        return (String[]) fieldList.toArray(new String[fieldList.size()]);
    }

    private Map<String, Map<String, String>> getDictMap(List<DiscoverIndexField> discoverIndexFieldList) {
        // 获取数据字典
        Map<String, Map<String, String>> dicMap = new HashMap<>();
        List<BaseDictAll> dictAllVoList = baseDictAllService.findAll();
        discoverIndexFieldList.stream().filter(p -> p.isDisplayed() || "true".equals(p.isDisplayed())).forEach(p -> {
            if (StringUtils.isNotEmpty(p.getFormat())) {
                Map<String, String> dicFieldMap = new HashedMap();
                dicMap.put(p.getName(), dicFieldMap);
                dictAllVoList.stream().filter(d -> p.getFormat().equals(d.getParentType())).forEach(d -> {
                    dicFieldMap.put(d.getCode(), d.getCodeValue());
                });

            }
        });
        return dicMap;
    }

    private List<Map<String, String>> getAllFieldList(String index) {
        List<Map<String, String>> allFieldsList = new ArrayList<>();
        final ObjectMapper mapper = new ObjectMapper();
        String[] indexArr = index.split(",");
        if (indexArr != null && indexArr.length > 0) {
            for (int i = 0; i < indexArr.length; i++) {
                List<DiscoverIndex> indexList = indexService.findByProperty(DiscoverIndex.class, "indexid", indexArr[i]);
                if (!CollectionUtils.isEmpty(indexList)) {
                    DiscoverIndex discoverIndex = indexList.get(0);
                    String allFeidlStr = discoverIndex.getIndexfields();
                    try {
                        List<Map<String, String>> fieldsList = new ArrayList<>();
                        fieldsList = mapper.readValue(allFeidlStr, fieldsList.getClass());
                        allFieldsList.addAll(fieldsList);

                    } catch (IOException e) {
                        log.error("", e);
                    }
                }
            }
        }
        return allFieldsList;
    }

    private List<String> getTimeFields(List<Map<String, String>> fieldMapList) {
        List<String> dateFields = new ArrayList<>();
        for (Map<String, String> fieldMap : fieldMapList) {
            String type = fieldMap.get("type");
            String name = fieldMap.get("name");
            if (type != null && type.equals("date")) {
                dateFields.add(name);
            }
        }
        return dateFields;
    }

}
