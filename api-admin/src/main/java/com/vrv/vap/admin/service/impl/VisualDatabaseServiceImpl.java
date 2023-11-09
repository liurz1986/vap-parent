package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.model.BaseDictAll;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.common.excel.ExcelInfo;
import com.vrv.vap.admin.common.excel.out.ExcelData;
import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.common.excel.out.WriteHandler;
import com.vrv.vap.admin.common.util.ES7Tools;
import com.vrv.vap.admin.common.util.PathTools;
import com.vrv.vap.admin.common.util.VisualDatabaseUtils;
import com.vrv.vap.admin.mapper.VisualDatabaseMapper;
import com.vrv.vap.admin.model.VisualDatabaseConnection;
import com.vrv.vap.admin.service.VisualDatabaseService;
import com.vrv.vap.admin.vo.DatabaseQuery;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class VisualDatabaseServiceImpl extends BaseServiceImpl<VisualDatabaseConnection> implements VisualDatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(VisualDatabaseServiceImpl.class);
    public static final String SELECT = "select ";
    public static final String STR_SPACE = " ";
    public static final String QUERY = "query";

    @Resource
    private VisualDatabaseMapper visualDatabaseMapper;

    @Autowired
    public VisualDatabaseUtils visualDatabaseUtils;

    @Autowired
    private BaseDictAllService baseDictAllService;

    @Override
    public String generateQuerySql(DatabaseQuery databaseQuery) {
        String order_ = databaseQuery.getOrder_();
        String by_ = databaseQuery.getBy_();
        String queryJsonStr = databaseQuery.getQueryJsonStr();
        if (StringUtils.isEmpty(queryJsonStr)) {
            return "";
        }
        String sql = new String(SELECT);
        StringBuffer distinctSql = new StringBuffer(STR_SPACE);
        StringBuffer columnSql = new StringBuffer(STR_SPACE);
        StringBuffer tableSql = new StringBuffer(STR_SPACE);
        StringBuffer filterSql = new StringBuffer(STR_SPACE);
        StringBuffer limitSql = new StringBuffer(STR_SPACE);
        StringBuffer groupSql = new StringBuffer(STR_SPACE);
        StringBuffer orderSql = new StringBuffer(STR_SPACE);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(JsonSanitizer.sanitize(queryJsonStr), Map.class);
            if (jsonMap.containsKey(QUERY)) {
                Map<String, Object> queryMap = (Map<String, Object>) jsonMap.get(QUERY);
                // 去重
                distinctSql = this.generateDistinctSql(queryMap, distinctSql);
                // 查询表单
                tableSql = this.generateTableSql(queryMap, tableSql);
                // 查询列
                columnSql = this.generateColumnSql(queryMap, columnSql, true);
                // 过滤条件
                filterSql = this.generateFilterSql(queryMap, filterSql);
                // 查询条数
                limitSql = this.generateLimitSql(queryMap, limitSql);
                // 聚合
                groupSql = this.generateGroupSql(queryMap, groupSql);
                // 排序
                orderSql = this.generateOrderSql(queryMap, orderSql);
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        if (StringUtils.isEmpty(columnSql.toString().trim())) {
            columnSql.append(" * ");
        }
        if (StringUtils.isNotEmpty(distinctSql.toString().trim())) {
            sql += distinctSql.toString();
        }
        if (StringUtils.isNotEmpty(columnSql.toString().trim())) {
            Integer columnLen = columnSql.toString().length();
            sql += STR_SPACE + columnSql.toString().substring(0, columnLen - 1);
        }
        if (StringUtils.isNotEmpty(tableSql.toString().trim())) {
            sql += " from " + tableSql.toString();
        }
        sql += " where ";
        if (StringUtils.isNotEmpty(filterSql.toString().trim())) {
            sql += filterSql.toString();
        } else {
            sql += " 1=1 ";
        }
        if (StringUtils.isNotEmpty(groupSql.toString().trim())) {
            Integer groupLen = groupSql.toString().length();
            sql += " group by " + groupSql.toString().substring(0, groupLen - 1);
        }
        if (StringUtils.isNotEmpty(order_)) {
            sql += " order by " + order_;
            if (StringUtils.isNotEmpty(by_)) {
                sql += STR_SPACE + by_;
            }
        } else {
            // 前端将order,by移到外层，保持框架一致性
            if (StringUtils.isNotEmpty(orderSql.toString().trim())) {
                Integer orderLen = orderSql.toString().length();
                sql += " order by " + orderSql.toString().substring(0, orderLen - 1);
            }
        }
        if (StringUtils.isNotEmpty(limitSql.toString().trim())) {
            sql += " limit " + limitSql.toString();
        }
        return sql;
    }

    @Override
    public String generateDataDetailSql(DatabaseQuery databaseQuery) {
        String queryJsonStr = databaseQuery.getQueryJsonStr();
        Integer start = databaseQuery.getStart_();
        Integer count = databaseQuery.getCount_();
        String order = databaseQuery.getOrder_();
        String by = databaseQuery.getBy_();
        String sql = new String(SELECT);
        StringBuffer distinctSql = new StringBuffer(STR_SPACE);
        StringBuffer columnSql = new StringBuffer(STR_SPACE);
        StringBuffer tableSql = new StringBuffer(STR_SPACE);
        StringBuffer filterSql = new StringBuffer(STR_SPACE);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(JsonSanitizer.sanitize(queryJsonStr), Map.class);
            if (jsonMap.containsKey(QUERY)) {
                Map<String, Object> queryMap = (Map<String, Object>) jsonMap.get(QUERY);
                // 去重
                distinctSql = this.generateDistinctSql(queryMap, distinctSql);
                // 查询表单
                tableSql = this.generateTableSql(queryMap, tableSql);
                // 查询列
                columnSql = this.generateColumnSql(queryMap, columnSql, false);
                // 过滤条件
                filterSql = this.generateFilterSql(queryMap, filterSql);
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        if (StringUtils.isEmpty(columnSql.toString().trim())) {
            columnSql.append(" * ");
        }
        if (StringUtils.isNotEmpty(distinctSql.toString().trim())) {
            sql += distinctSql.toString();
        }
        if (StringUtils.isNotEmpty(columnSql.toString().trim())) {
            Integer columnLen = columnSql.toString().length();
            sql += STR_SPACE + columnSql.toString().substring(0, columnLen - 1);
        }
        if (StringUtils.isNotEmpty(tableSql.toString().trim())) {
            sql += " from " + tableSql.toString();
        }
        sql += " where ";
        if (StringUtils.isNotEmpty(filterSql.toString().trim())) {
            sql += filterSql.toString();
        } else {
            sql += " 1=1 ";
        }
        if (StringUtils.isNotEmpty(order)) {
            sql += " order by " + order;
            if (StringUtils.isNotEmpty(by)) {
                sql += STR_SPACE + by;
            }
        }
        sql += " limit " + start + "," + count;
        return sql;
    }

    @Override
    public String generateDataCountSql(String queryJsonStr) {
        String sql = new String(SELECT);
        StringBuffer distinctSql = new StringBuffer(STR_SPACE);
        StringBuffer columnSql = new StringBuffer(STR_SPACE);
        StringBuffer tableSql = new StringBuffer(STR_SPACE);
        StringBuffer filterSql = new StringBuffer(STR_SPACE);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> jsonMap = objectMapper.readValue(JsonSanitizer.sanitize(queryJsonStr), Map.class);
            if (jsonMap.containsKey(QUERY)) {
                Map<String, Object> queryMap = (Map<String, Object>) jsonMap.get(QUERY);
                // 去重
                distinctSql = this.generateDistinctSql(queryMap, distinctSql);
                // 查询表单
                tableSql = this.generateTableSql(queryMap, tableSql);
                // 过滤条件
                filterSql = this.generateFilterSql(queryMap, filterSql);
            }
        } catch (IOException e) {
            logger.error("", e);
        }
        if (StringUtils.isNotEmpty(distinctSql.toString().trim())) {
            sql += distinctSql.toString();
        }
        sql += " count(*) ";
        if (StringUtils.isNotEmpty(tableSql.toString().trim())) {
            sql += " from " + tableSql.toString();
        }
        sql += " where ";
        if (StringUtils.isNotEmpty(filterSql.toString().trim())) {
            sql += filterSql.toString();
        } else {
            sql += " 1=1 ";
        }
        return sql;
    }

    /**
     * 去重
     *
     * @param queryMap
     * @param distinctSql
     * @return
     */
    private StringBuffer generateDistinctSql(Map<String, Object> queryMap, StringBuffer distinctSql) {
        boolean useDistinct = queryMap.containsKey("useDistinct") ? (boolean) queryMap.get("useDistinct") : false;
        if (useDistinct) {
            distinctSql.append(" distinct ");
        }
        return distinctSql;
    }

    /**
     * 查询表单
     *
     * @param queryMap
     * @param tableSql
     * @return
     */
    private StringBuffer generateTableSql(Map<String, Object> queryMap, StringBuffer tableSql) {
        if (queryMap.containsKey("source")) {
            Map<String, Object> sourceMap = (Map<String, Object>) queryMap.get("source");
            String tableName = (String) sourceMap.get("tableName");
            String alias = (String) sourceMap.get("alias");
            tableSql.append(STR_SPACE + "`" + tableName + "`" + STR_SPACE);
            if (StringUtils.isNotEmpty(alias)) {
                tableSql.append(" as " + "`" + alias + "`" + STR_SPACE);
            }
        }
        return tableSql;
    }

    /**
     * 查询列
     *
     * @param queryMap
     * @param columnSql
     * @return
     */
    private StringBuffer generateColumnSql(Map<String, Object> queryMap, StringBuffer columnSql, boolean useFunction) {
        if (queryMap.containsKey("columns")) {
            List<Map<String, Object>> columnList = (List<Map<String, Object>>) queryMap.get("columns");
            if (CollectionUtils.isNotEmpty(columnList)) {
                for (Map<String, Object> column : columnList) {
                    String name = (String) column.get("name");
                    String fromAlias = (String) column.get("fromAlias");
                    String des = (String) column.get("des");
                    String func = (String) column.get("func");
                    String format = (String) column.get("format");
                    // 数量去重支持
                    Boolean useDistinct = (Boolean) column.get("useDistinct") != null ? (Boolean) column.get("useDistinct") : false;
                    // 表别名
                    if (StringUtils.isNotEmpty(fromAlias)) {
                        columnSql.append(STR_SPACE + "`" + fromAlias + "`" + ".");
                    }
                    // 列名
                    if (StringUtils.isNotEmpty(name)) {
                        if (StringUtils.isNotEmpty(func) && useFunction) {
                            // 日期格式化
                            if (func.equalsIgnoreCase("DATE_FORMAT")) {
                                columnSql.append(func + "(" + "`" + name + "`" + ",'" + format + "')");
                            } else {
                                if (useDistinct) {
                                    columnSql.append(func + "(" + " distinct " + "`" + name + "`" + ")");
                                } else {
                                    columnSql.append(func + "(" + "`" + name + "`" + ")");
                                }
                            }
                        } else {
                            columnSql.append("`" + name + "`");
                        }
                    }
                    // 列别名
                    if (StringUtils.isNotEmpty(des)) {
                        columnSql.append(" as " + "`" + des + "`");
                    }
                    columnSql.append(",");
                }
            }
        }
        return columnSql;
    }

    /**
     * 过滤条件
     *
     * @param queryMap
     * @param filterSql
     */
    private StringBuffer generateFilterSql(Map<String, Object> queryMap, StringBuffer filterSql) {
        if (queryMap.containsKey("filter")) {
            Map<String, Object> boolMap = (Map<String, Object>) queryMap.get("filter");
            List<Map<String, Object>> mustList = new ArrayList<>();
            List<Map<String, Object>> shouldList;
            // must条件拼装
            if (boolMap.containsKey("must")) {
                mustList = (List<Map<String, Object>>) boolMap.get("must");
                filterSql = this.parseFilterRange(mustList, filterSql, " and ");
            }
            // should条件拼装
            if (boolMap.containsKey("should")) {
                shouldList = (List<Map<String, Object>>) boolMap.get("should");
                if (CollectionUtils.isNotEmpty(mustList) && CollectionUtils.isNotEmpty(shouldList)) {
                    filterSql.append(" and (");
                }
                filterSql = this.parseFilterRange(shouldList, filterSql, " or ");
                if (CollectionUtils.isNotEmpty(mustList) && CollectionUtils.isNotEmpty(shouldList)) {
                    filterSql.append(" )");
                }
            }
            // mustNot条件拼装
            if (boolMap.containsKey("must_not")) {
                List<Map<String, Object>> mustNotList = (List<Map<String, Object>>) boolMap.get("must_not");
            }
        }
        return filterSql;
    }

    /**
     * 解析过滤条件中的范围
     *
     * @param filterSql
     * @return
     */
    private StringBuffer parseFilterRange(List<Map<String, Object>> list, StringBuffer filterSql, String expr) {
        if (CollectionUtils.isNotEmpty(list)) {
            int index = 0;
            for (Map map : list) {
                String rangeOper = (String) map.get("rangeOper");
                List<Map<String, Object>> ranges = (List<Map<String, Object>>) map.get("range");
                if (index != 0) {
                    filterSql.append(STR_SPACE + expr);
                }
                if (ranges.size() > 1) {
                    filterSql.append("(");
                }
                if (CollectionUtils.isNotEmpty(ranges)) {
                    int i = 0;
                    for (Map<String, Object> range : ranges) {
                        String fieldName = (String) range.get("fieldName");
                        String oper = (String) range.get("oper");
                        String value = String.valueOf(range.get("value"));
                        String format = (String) range.get("format");
                        if (StringUtils.isNotEmpty(oper)) {
                            if (StringUtils.isNotEmpty(format)) {
                                filterSql.append(STR_SPACE + "DATE_FORMAT(" + "`" + fieldName + "`" + ",'" + format + "')");
                            } else {
                                filterSql.append(STR_SPACE + "`" + fieldName + "`" + STR_SPACE);
                            }
                        }
                        if (StringUtils.isNotEmpty(oper)) {
                            if (oper.equals("isNotNull")) {
                                filterSql.append(" <> '' and " + "`" + fieldName + "`" + " is not null");
                            } else if (oper.equals("not_in")) {
                                filterSql.append(" not in ");
                            } else {
                                filterSql.append(STR_SPACE + oper);
                            }
                        }
                        if (StringUtils.isNotEmpty(value)) {
                            if (oper.equals("in") || oper.equals("not_in")) {
                                String[] values = value.split(",");
                                filterSql.append(" ( ");
                                for (int x = 0; x < values.length; x++) {
                                    if (x == 0) {
                                        filterSql.append(" '" + values[x] + "' ");
                                    } else {
                                        filterSql.append(" , '" + values[x] + "' ");
                                    }
                                }
                                filterSql.append(" ) ");
                            } else {
                                filterSql.append(" '" + value + "' ");
                            }
                        }

                        if (i + 1 < ranges.size()) {
                            filterSql.append(STR_SPACE + rangeOper);
                        }
                        i++;
                    }

                }
                if (ranges.size() > 1) {
                    filterSql.append(")");
                }
                index++;
            }
        }
        return filterSql;
    }

    /**
     * 查询条数
     *
     * @param queryMap
     * @param limitSql
     * @return
     */
    private StringBuffer generateLimitSql(Map<String, Object> queryMap, StringBuffer limitSql) {
        if (!queryMap.containsKey("size") && !queryMap.containsKey("from")) {
            return limitSql;
        }
        Integer size = queryMap.containsKey("size") ? (Integer) queryMap.get("size") : 10;
        Integer from = queryMap.containsKey("from") ? (Integer) queryMap.get("from") : 0;
        limitSql.append(STR_SPACE + from + "," + size);
        return limitSql;
    }

    /**
     * 字段聚合
     *
     * @param queryMap
     * @param groupSql
     * @return
     */
    private StringBuffer generateGroupSql(Map<String, Object> queryMap, StringBuffer groupSql) {
        if (queryMap.containsKey("group")) {
            List<Map<String, Object>> columnList = (List<Map<String, Object>>) queryMap.get("group");
            if (CollectionUtils.isNotEmpty(columnList)) {
                for (Map<String, Object> column : columnList) {
                    String name = (String) column.get("name");
                    String des = (String) column.get("des");
                    String func = (String) column.get("func");
                    String format = (String) column.get("format");
                    // 列别名
                    if (StringUtils.isNotEmpty(des)) {
                        groupSql.append(STR_SPACE + "`" + des + "`");
                    } else {
                        if (StringUtils.isNotEmpty(func)) {
                            // 日期格式化
                            if (func.equalsIgnoreCase("DATE_FORMAT")) {
                                groupSql.append(func + "(" + "`" + name + "`" + ",'" + format + "')");
                            } else {
                                if (func.equalsIgnoreCase("CEIL")) {
                                    groupSql.append(func + "(" + name + ")");
                                } else {
                                    groupSql.append(func + "(" + "`" + name + "`" + ")");
                                }
                            }
                        } else {
                            groupSql.append(STR_SPACE + "`" + name + "`");
                        }
                    }
                    groupSql.append(",");
                }
            }
        }
        return groupSql;
    }

    /**
     * 排序
     *
     * @param queryMap
     * @param orderSql
     * @return
     */
    private StringBuffer generateOrderSql(Map<String, Object> queryMap, StringBuffer orderSql) {
        if (queryMap.containsKey("order")) {
            List<Map<String, Object>> orders = (List<Map<String, Object>>) queryMap.get("order");
            if (CollectionUtils.isNotEmpty(orders)) {
                for (Map<String, Object> order : orders) {
                    String name = (String) order.get("name");
                    String des = (String) order.get("des");
                    String sort = (String) order.get("sort");
                    if (StringUtils.isNotEmpty(des)) {
                        orderSql.append(STR_SPACE + "`" + des + "`");
                    } else {
                        orderSql.append(STR_SPACE + "`" + name + "`");
                    }
                    if (StringUtils.isNotEmpty(sort)) {
                        orderSql.append(STR_SPACE + sort);
                    }
                    orderSql.append(",");
                }
            }
        }
        return orderSql;
    }

    @Override
    public Export.Progress exportList(DatabaseQuery databaseQuery) {
        String queryJsonStr = databaseQuery.getQueryJsonStr();
        String[] params = databaseQuery.getParam();
        Integer connectionId = databaseQuery.getId();
        //字段配置
        VisualDatabaseConnection visualDatabaseConnection = visualDatabaseMapper.selectByPrimaryKey(connectionId);
        if (visualDatabaseConnection == null) {
            logger.info("数据库连接不存在！");
            return null;
        }
        String tableFieldJson = visualDatabaseConnection.getTableFieldJson();
        Gson gson = new Gson();
        List<Map<String, Object>> fieldList = gson.fromJson(tableFieldJson, List.class);
        //数据字典
        Map<String, Map<String, String>> dicMap = this.getDictMap(fieldList);
        //字段名称及描述
        String[] resultFields = databaseQuery.getResultFields();
        String[] fields = this.getNameFields(fieldList, resultFields);
        String[] fieldDesc = this.getNameDescs(fieldList, resultFields);
        // top数据处理
        String topStr = databaseQuery.getTopList();
        String[] topFields = this.getTopNameFields(topStr, "field");
        String[] topFieldDesc = this.getTopNameFields(topStr, "title");
        Map<String, Object> topMap = gson.fromJson(JsonSanitizer.sanitize(topStr), Map.class);
        List<Map<String, Object>> topDataList = (List<Map<String, Object>>) topMap.get("data");

        List<ExcelData> excelDataList = new ArrayList<>();
        ExcelInfo topInfo = new ExcelInfo("小组件明细", topFields, topFieldDesc, "TOP数据", true, PathTools.getExcelPath("小组件明细"), null);
        int topCount = topMap != null ? topDataList.size() : 0;
        ExcelData topData = new ExcelData(topInfo, topCount, new ArrayList<>());
        excelDataList.add(topData);
        Export.Progress progress = null;
        if (StringUtils.isNotEmpty(queryJsonStr)) {
            if (params != null && params.length > 0) {
                List<List<Map>> resultList = new ArrayList<>();
                int j = 0;
                for (String param : params) {
                    DatabaseQuery dbQuery = new DatabaseQuery();
                    BeanUtils.copyProperties(databaseQuery,dbQuery);
                    String queryStr = databaseQuery.getQueryJsonStr();
                    queryStr = queryStr.replace("${param}", param);
                    dbQuery.setQueryJsonStr(queryStr);
                    String querySql = this.generateQuerySql(dbQuery);
                    List<Map> result = visualDatabaseUtils.queryData(querySql, connectionId);
                    int total = result.size() > ES7Tools.getExportMax() ? ES7Tools.getExportMax() : result.size();
                    resultList.add(result);
                    String sheetName = topDataList.size() >= j + 1 ? (String) topDataList.get(j).get("key") : "详情";
                    ExcelInfo info = new ExcelInfo("小组件明细", fields, fieldDesc, sheetName, true, PathTools.getExcelPath("小组件明细"), null);
                    ExcelData data = new ExcelData(info, total, new ArrayList<>());
                    excelDataList.add(data);
                    j++;
                }

                progress = Export.build(excelDataList).start(WriteHandler.fun(p -> {
                    //top数据
                    toTopExcel(topDataList, p);
                    int i = 1;
                    for (List<Map> result : resultList) {
                        final int index = i;
                        toExcel(result, p, fields, dicMap, index);
                        i++;
                    }
                }));
                return progress;
            }
        } else {
            progress = Export.build(excelDataList).start(WriteHandler.fun(p -> {
                //top数据
                toTopExcel(topDataList, p);
            }));
            return progress;
        }
        return progress;
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

    private void toExcel(List<Map> resultList, Export.Progress pro, String[] fields, Map<String, Map<String, String>> dicMap, int index) {
        List<Map<String, Object>> writeList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(resultList)) {
            for (Map<String, Object> result : resultList) {
                Map<String, Object> data = new HashMap<>();
                for (int i = 0; i < fields.length; i++) {
                    String value = String.valueOf(result.get(fields[i]));
                    //数据字典处理
                    if (dicMap.containsKey(fields[i]) && value != null && dicMap.get(fields[i]).containsKey(value)) {
                        value = dicMap.get(fields[i]).get(value);
                    }
                    data.put(fields[i], value);
                }
                writeList.add(data);
            }
        }
        pro.writeBatchMap(index, writeList);
    }

    private String[] getTopNameFields(String topStr, String fieldName) {
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

    private String[] getNameFields(List<Map<String, Object>> fieldList, String[] resultFields) {
        List<String> nameFields = new ArrayList<>();
        fieldList.stream().filter(p -> "true".equals(p.get("displayed"))).forEach(p -> {
            if (resultFields != null && resultFields.length > 0) {
                if (Arrays.asList(resultFields).contains(p.get("field"))) {
                    nameFields.add((String) p.get("field"));
                }
            } else {
                nameFields.add((String) p.get("field"));
            }
        });
        return nameFields.toArray(new String[nameFields.size()]);
    }

    private String[] getNameDescs(List<Map<String, Object>> fieldList, String[] resultFields) {
        List<String> nameDescs = new ArrayList<>();
        fieldList.stream().filter(p -> "true".equals(p.get("displayed"))).forEach(p -> {
            if (resultFields != null && resultFields.length > 0) {
                if (Arrays.asList(resultFields).contains(p.get("field"))) {
                    nameDescs.add((String) (StringUtils.isEmpty((String) p.get("comment")) ? p.get("field") : p.get("comment")));
                }
            } else {
                nameDescs.add((String) (StringUtils.isEmpty((String) p.get("comment")) ? p.get("field") : p.get("comment")));
            }
        });
        return nameDescs.toArray(new String[nameDescs.size()]);
    }

    private Map<String, Map<String, String>> getDictMap(List<Map<String, Object>> fieldList) {
        // 获取数据字典
        Map<String, Map<String, String>> dicMap = new HashMap<>();
        List<BaseDictAll> dictAllVoList = baseDictAllService.findAll();
        fieldList.stream().filter(p -> "true".equals(p.get("displayed"))).forEach(p -> {
            if (StringUtils.isNotEmpty((String) p.get("format"))) {
                Map<String, String> dicFieldMap = new HashedMap();
                dicMap.put((String) p.get("field"), dicFieldMap);
                dictAllVoList.stream().filter(d -> p.get("format").equals(d.getParentType())).forEach(d -> {
                    dicFieldMap.put(d.getCode(), d.getCodeValue());
                });
            }
        });
        return dicMap;
    }

    @Override
    public List<Map> queryData(String sql) {
        return visualDatabaseMapper.querySql(sql);
    }

    @Override
    public Integer queryDataCount(String sql) {
        return visualDatabaseMapper.queryDataCount(sql);
    }
}
