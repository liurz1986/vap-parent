package com.vrv.rule.util;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.vrv.rule.ruleInfo.udf.UdfFunctionUtil;
import com.vrv.rule.vo.AggregateOperator;
import com.vrv.rule.vo.FieldInfoVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;

import com.vrv.rule.model.EventColumn;
import com.vrv.rule.model.EventTable;
import com.vrv.rule.model.filter.Column;
import com.vrv.rule.model.filter.Exchanges;
import com.vrv.rule.model.filter.FilterConfigObject;
import com.vrv.rule.model.filter.Tables;
import com.vrv.rule.ruleInfo.exchangeType.flatMap.RowInfo;

/**
 * FieldInfo工具类
 *
 * @author wd-pc
 */
public class FieldInfoUtil {


    private static final Logger logger = LoggerFactory.getLogger(FieldInfoUtil.class);

    /***
     * 获得table属性根据Columns
     * @param tables
     * @param columnList
     * @param depth
     * @return
     */
    public static List<FieldInfoVO> getTableFieldInfoVOsByColumns(Tables tables, List<Column> columnList, int depth) {
        List<FieldInfoVO> fieldInVOs = new ArrayList<>();
        for (Column column : columnList) {
            if (depth < 3) {
                FieldInfoVO fieldInfoVO = new FieldInfoVO();
                //	fieldInfoVO.setTableId(tables.getId());
                fieldInfoVO.setTableName(tables.getName());
                fieldInfoVO.setFieldType(column.getDataType());
                fieldInfoVO.setFieldName(column.getName());
                fieldInfoVO.setOrder(column.getOrder());
                fieldInfoVO.setEventTime(column.getEventTime());
                String type = column.getDataType();
                String aggType = column.getAggType();
                fieldInfoVO.setAggType(aggType);
                if (aggType != null && aggType.equals("folds")) {
                    String exp = column.getExp();
                    if (StringUtils.isNotEmpty(exp)) {
                        Gson gson = new Gson();
                        AggregateOperator aggregateOperator = gson.fromJson(exp, AggregateOperator.class);
                        fieldInfoVO.setExpression(aggregateOperator);
                    }
                } else {
                    fieldInfoVO.setOrdinaryExpression(column.getExp());
                }
                if (type.equals("pojo") || type.equals("pojoArray")) {
                    List<Column> childColumn = column.getChildColumn();
                    List<FieldInfoVO> childFieldsColumns = getTableFieldInfoVOsByColumns(tables, childColumn, depth + 1);
                    RowInfo rowInfo = new RowInfo();
                    rowInfo.setFields(childFieldsColumns);
                    fieldInfoVO.setChildFields(childFieldsColumns);
                    fieldInfoVO.setHints(rowInfo);
                }
                fieldInVOs.add(fieldInfoVO);
            }
        }
        return fieldInVOs;
    }


    /**
     * 获得Table对应的FieldInfoVOs
     *
     * @param tableArray
     * @param tables
     * @param depth
     * @return
     */
    public static List<FieldInfoVO> getTableFieldInfoVOs(Tables[][] tableArray, Tables tables, int depth) {
        List<Column> columnList = tables.getColumn();
        List<FieldInfoVO> fieldInVOs = new ArrayList<>();
        for (Column column : columnList) {
            if (depth < 3) {
                FieldInfoVO fieldInfoVO = new FieldInfoVO();
                //	fieldInfoVO.setTableId(tables.getId());
                fieldInfoVO.setTableName(tables.getName());
                fieldInfoVO.setFieldType(column.getDataType());
                fieldInfoVO.setFieldName(column.getName());
                fieldInfoVO.setOrder(column.getOrder());
                fieldInfoVO.setEventTime(column.getEventTime());
                String type = column.getDataType();
                String aggType = column.getAggType();
                fieldInfoVO.setAggType(aggType);
                if (aggType.equals("folds")) {
                    String exp = column.getExp();
                    if (StringUtils.isNotEmpty(exp)) {
                        Gson gson = new Gson();
                        AggregateOperator aggregateOperator = gson.fromJson(exp, AggregateOperator.class);
                        fieldInfoVO.setExpression(aggregateOperator);
                    }
                } else {
                    fieldInfoVO.setOrdinaryExpression(column.getExp());
                }
                if (type.equals("pojo") || type.equals("pojoArray")) {
                    String dataHint = column.getDataHint(); //table的guid
                    for (int i = 0; i < tableArray.length; i++) {
                        for (int j = 0; j < tableArray[i].length; j++) {
                            Tables childTable = tableArray[i][j];
                            if (childTable.getId().equals(dataHint)) {
                                List<FieldInfoVO> fieldInfoVOs = getTableFieldInfoVOs(tableArray, childTable, depth + 1);
                                RowInfo rowInfo = new RowInfo();
                                rowInfo.setFields(fieldInfoVOs);
                                fieldInfoVO.setChildFields(fieldInfoVOs);
                                fieldInfoVO.setHints(rowInfo);
                            }
                        }
                    }
                }
                fieldInVOs.add(fieldInfoVO);
            }
        }
        return fieldInVOs;
    }

    /**
     * @param tableName
     * @param tableId
     * @param order
     * @param roomType  timeRoom/idRoom(两种不同的盒子)
     * @param fieldType mapArray/mapMap(两种不同类型)
     * @return
     */
    public static FieldInfoVO getRoomFieldInfo(String tableName, String tableId, Integer order, String roomType, String fieldType) {
        FieldInfoVO fileInfo = new FieldInfoVO();
        fileInfo.setFieldName(roomType);
        fileInfo.setFieldType(fieldType);
        fileInfo.setOrder(order);
        fileInfo.setTableName(tableName);
        //	fileInfo.setTableId(tableId);
        fileInfo.setAggType("ordinary");
        return fileInfo;
    }


    /**
     * 获得资源类型的FieldInfoVOs（不依赖Jdbc）
     *
     * @param columns
     * @param eventTableName
     * @param depth
     * @return
     */
    public static List<FieldInfoVO> getSourceFieldInfoVOsNoJdbc(List<Column> columns, String eventTableName, int depth) {
        {
            List<FieldInfoVO> fieldInVOs = new ArrayList<>();
            for (Column column : columns) {
                if (depth < 3) { //TODO 只能有三层
                    String type = column.getDataType();
                    FieldInfoVO fieldInfoVO = new FieldInfoVO();
                    fieldInfoVO.setTableName(eventTableName);
                    fieldInfoVO.setFieldType(type);
                    fieldInfoVO.setFieldName(column.getName());
                    fieldInfoVO.setAggType("ordinary");
                    fieldInfoVO.setOrder(column.getOrder());
                    fieldInfoVO.setEventTime(column.getEventTime());
                    if (type.equals("pojo") || type.equals("pojoArray")) { //
                        String dataHintName = column.getDataHint();    //
                        List<Column> childColumn = column.getChildColumn();
                        List<FieldInfoVO> fieldInfoVOs = getSourceFieldInfoVOsNoJdbc(childColumn, dataHintName, depth + 1);
                        fieldInfoVO.setChildFields(fieldInfoVOs);
                        RowInfo rowInfo = new RowInfo();
                        rowInfo.setFields(fieldInfoVOs);
                        fieldInfoVO.setHints(rowInfo);
                    }
                    fieldInVOs.add(fieldInfoVO);
                }
            }
            return fieldInVOs;
        }
    }

    /**
     * 获得资源类型FieldInfoVOs
     *
     * @param columnList
     * @param eventTableName
     * @return
     */
    public static List<FieldInfoVO> getSourceFieldInfoVOs(List<EventColumn> columnList, String eventTableName, int depth) {
        List<FieldInfoVO> fieldInVOs = new ArrayList<>();
        for (EventColumn eventColumn : columnList) {
            if (depth < 3) { //TODO 只能有三层
                FieldInfoVO fieldInfoVO = new FieldInfoVO();
                //fieldInfoVO.setTableId(eventColumn.getEventTableId());
                fieldInfoVO.setTableName(eventTableName);
                fieldInfoVO.setFieldType(eventColumn.getType());
                fieldInfoVO.setFieldName(eventColumn.getName());
                fieldInfoVO.setAggType("ordinary");
                fieldInfoVO.setOrder(eventColumn.getOrder());
                fieldInfoVO.setEventTime(eventColumn.getEventTime());
                String type = eventColumn.getType();
                if (type.equals("pojo") || type.equals("pojoArray")) { //
                    String dataHint = eventColumn.getDataHint();
                    String dataHintSql = JdbcSqlConstant.EVENT_TABLE_COLUMN_SQL;
                    List<String> dataHints = new ArrayList<>();
                    dataHints.add(dataHint);
                    List<EventColumn> dataHintForEventColumn = JdbcSingeConnectionUtil.getInstance().querySqlForEventColumn(dataHintSql, dataHints);
                    EventTable dataHintEventTable = getEventTable(dataHints);
                    String name = dataHintEventTable.getName();
                    List<FieldInfoVO> fieldInfoVOs = getSourceFieldInfoVOs(dataHintForEventColumn, name, depth + 1);
                    fieldInfoVO.setChildFields(fieldInfoVOs);
                    RowInfo rowInfo = new RowInfo();
                    rowInfo.setFields(fieldInfoVOs);
                    fieldInfoVO.setHints(rowInfo);
                }
                fieldInVOs.add(fieldInfoVO);
            }
        }
        return fieldInVOs;
    }

    /**
     * 获得eventTableName;
     *
     * @param sourceList
     * @return
     */
    public static EventTable getEventTable(List<String> sourceList) {
        String tableSql = JdbcSqlConstant.EVENT_TABLE_SQL;
        List<EventTable> tableList = JdbcSingeConnectionUtil.getInstance().querySqlForEventtable(tableSql, sourceList);
        if (tableList.size() == 1) {
            EventTable eventTable = tableList.get(0);
            return eventTable;
        } else {
            throw new RuntimeException("event_table查询个数多余1个，请检查！");
        }
    }

    /**
     * 根据名称获得对应的属性
     *
     * @param name
     * @param fields
     * @return
     */
    public static FieldInfoVO getFieldInfoVO(String name, List<FieldInfoVO> fields) {
        FieldInfoVO field = null;
        for (FieldInfoVO fieldInfoVO : fields) {
            String fieldName = fieldInfoVO.getFieldName();
            if (fieldName.equals(name)) {
                field = fieldInfoVO;
                break;
            }
        }
        return field;
    }

    /**
     * 获得eventtime属性里欸包
     *
     * @param fields
     * @return
     */
    public static FieldInfoVO getEventTimeFieldInfoVO(List<FieldInfoVO> fields) {
        FieldInfoVO field = null;
        for (FieldInfoVO fieldInfoVO : fields) {
            Boolean eventTime = fieldInfoVO.getEventTime();
            if (eventTime != null && eventTime) {
                field = fieldInfoVO;
                break;
            }
//			if(fieldType.equals("datetime")) {
//			}
        }
        return field;
    }

    /**
     * 获得数据流当中对应字段的值
     *
     * @param row
     * @param inputFields
     * @param eventField
     * @return
     */
    public static String getDataStreamFieldInfoValue(Row row, List<FieldInfoVO> inputFields, String eventField) {
        int order = 0;
        for (FieldInfoVO fieldInfoVO : inputFields) {
            String fieldName = fieldInfoVO.getFieldName();
            String[] eventFieldsplit = eventField.split("\\.");
            if (fieldName.equals(eventFieldsplit[1])) {
                order = fieldInfoVO.getOrder();
                break;
            }
        }
        //logger.info("order值：{};eventField值：{}", order,eventField);
        Object fieldValue = row.getField(order);
        if (fieldValue != null) {
            String inputValue = row.getField(order).toString();
            return inputValue;
        } else {
            return "";
        }
    }


    /**
     * 获得输入数据表名
     *
     * @param filterConfigObject
     * @param exchanges
     * @return
     */
    public static String getInputTableName(FilterConfigObject filterConfigObject, Exchanges exchanges) {
        List<String> sources = exchanges.getSources();
        if (sources.size() == 1) {
            String source = sources.get(0);
            Tables[][] tables = filterConfigObject.getTables();
            for (int i = 0; i < tables.length; i++) {
                for (int j = 0; j < tables[i].length; j++) {
                    Tables table = tables[i][j];
                    String id = table.getId();
                    if (id.equals(source)) {
                        return table.getName();
                    }
                }
            }
            return null;
        } else {
            throw new RuntimeException("该过滤映射规则多个源，不符合规则，请检查！");
        }
    }


    /**
     * 获得输出数据表名
     *
     * @param filterConfigObject
     * @param exchanges
     * @return
     */
    public static String getOutputTableName(FilterConfigObject filterConfigObject, Exchanges exchanges) {
        String target = exchanges.getTarget();
        if (StringUtils.isNotEmpty(target)) {
            Tables[][] tables = filterConfigObject.getTables();
            for (int i = 0; i < tables.length; i++) {
                for (int j = 0; j < tables[i].length; j++) {
                    Tables table = tables[i][j];
                    String id = table.getId();
                    if (id.equals(target)) {
                        return table.getName();
                    }
                }
            }
            throw new RuntimeException("该过滤映射规则没有对应的target，不符合规则，请检查！");
        } else {
            throw new RuntimeException("该过滤映源target为空，不符合规则，请检查！");
        }
    }


    /**
     * 获得查询的字段以字符串形式出现
     *
     * @param filterConfigObject
     * @param exchanges
     * @return
     */
    public static String getFieldInfosStr(FilterConfigObject filterConfigObject, Exchanges exchanges, String roomType) {
        List<Column> columns = getOutputTableColumns(filterConfigObject, exchanges);
        List<String> fields = new ArrayList<>();
        for (Column column : columns) {
            String exp = column.getExp();
            String name = column.getName();
            String aggType = column.getAggType();
            if (aggType.equals("folds")) { //褶皱属性
                name = name + " as " + name;
            } else {   //普通属性
                if (StringUtils.isNotEmpty(exp)) {
                    name = exp + " as " + name;
                }
            }
            fields.add(name);
        }
        addRoomType(roomType, fields);
        String[] field = fields.toArray(new String[fields.size()]);
        String fieldsStr = ArrayUtil.join(field, ",");
        return fieldsStr;
    }


    /**
     * 添加对应的roomType
     *
     * @param roomType
     * @param fields
     */
    private static void addRoomType(String roomType, List<String> fields) {
        switch (roomType) {
            case RoomInfoConstant.ID_ROOM_TYPE:
                fields.add("idRoom as idRoom");
                break;
            case RoomInfoConstant.TIME_ROOM_TYPE:
                fields.add("timeRoom as timeRoom");
                break;
            default:
                break;
        }
    }

    /**
     * 获得输入数据的表结构的列
     *
     * @param filterConfigObject
     * @param exchanges
     * @return
     */
    private static List<Column> getOutputTableColumns(FilterConfigObject filterConfigObject, Exchanges exchanges) {
        String target = exchanges.getTarget();
        if (StringUtils.isNotEmpty(target)) {
            Tables[][] tables = filterConfigObject.getTables();
            for (int i = 0; i < tables.length; i++) {
                for (int j = 0; j < tables[i].length; j++) {
                    Tables table = tables[i][j];
                    String id = table.getId();
                    if (id.equals(target)) {
                        List<Column> columns = table.getColumn();
                        return columns;
                    }
                }
            }
            throw new RuntimeException(target + "该源数据没有对应的table表，请检查！");
        } else {
            throw new RuntimeException("该过滤映射规则多个源，不符合规则，请检查！");
        }
    }


    /**
     * 获取重复成员通过列名进行比较
     *
     * @param first
     * @param second
     * @return
     */
    public static List<FieldInfoVO> getRepeatFieldInfoVOsByFieldName(List<FieldInfoVO> first, List<FieldInfoVO> second) {
        List<FieldInfoVO> list = new LinkedList<>();
        for (FieldInfoVO fieldInfoVO : first) {
            String fieldName = fieldInfoVO.getFieldName();
            for (FieldInfoVO fieldInfoVO2 : second) {
                String fieldName2 = fieldInfoVO2.getFieldName();
                if (fieldName2.equals(fieldName) && !fieldName2.equals("idRoom")) {
                    list.add(fieldInfoVO2);
                    break;
                }
            }
        }
        return list;
    }


    /**
     * 获得字符串类型的元素（主要用distinctcount,sum,avg,min,max数值算法字段）
     *
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param expressField
     * @return
     */
    public static String getInputFieldValueByString(Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String expressField) {
        String result = null;
        for (FieldInfoVO fieldInfoVO : inputFieldInfoVOs) {
            if (fieldInfoVO.getFieldName().equals(expressField)) {
                Integer fieldOrder = fieldInfoVO.getOrder();
                Object fieldValue = inputRow.getField(fieldOrder);
                if (fieldValue != null) {
                    result = fieldValue.toString();
                }
                break;
            }
        }
        return result;
    }

    /**
     * 获得输入字段的数字
     *
     * @param inputRow
     * @param inputFieldInfoVOs
     * @param outRowField
     * @return
     */
    public static Long getInputFieldValue(Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String outRowField) {
        Long fieldSum = 0L;
        for (FieldInfoVO fieldInfoVO : inputFieldInfoVOs) {
            if (fieldInfoVO.getFieldName().equals(outRowField)) {
                Integer fieldOrder = fieldInfoVO.getOrder();
                Object fieldValue = inputRow.getField(fieldOrder);
                fieldSum = UdfFunctionUtil.getRelateValue(fieldValue);
                break;
            }
        }
        return fieldSum;
    }


    public static Double getInputFieldDoubleValue(Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String outRowField) {
        Double fieldSum = 0.00d;
        for (FieldInfoVO fieldInfoVO : inputFieldInfoVOs) {
            if (fieldInfoVO.getFieldName().equals(outRowField)) {
                Integer fieldOrder = fieldInfoVO.getOrder();
                Object fieldValue = inputRow.getField(fieldOrder);
                fieldSum = UdfFunctionUtil.getDoubleValue(fieldValue);
                break;
            }
        }
        return fieldSum;
    }


    public static Map<String, String> getInputFieldValueByMap(Row inputRow, List<FieldInfoVO> inputFieldInfoVOs, String expressField) {
            Map<String,String> map = new HashMap<>();
            String[] fieldArr = expressField.split(",");
            for (String field : fieldArr) {
                for (FieldInfoVO fieldInfoVO : inputFieldInfoVOs) {
                    if (fieldInfoVO.getFieldName().equals(field)) {
                        Integer fieldOrder = fieldInfoVO.getOrder();
                        Object fieldValue = inputRow.getField(fieldOrder);
                        if (fieldValue!=null){
                            map.put(field,fieldValue.toString());   
                        }
                        break;
                    }
                }
            }
        return map;
    }


    /**
     * 替换掉日志当中的占位符
     * @param highLevelSearchCon
     * @param row
     * @param inputFields
     */
    public static String setLogFieldValueByHighLevelSearchCon(String highLevelSearchCon,Row row,List<FieldInfoVO> inputFields){
        if(StringUtils.isEmpty(highLevelSearchCon)){
            return highLevelSearchCon;
        }
        List<String> regxResult = getRegxResult(highLevelSearchCon);
        if(regxResult.size()>0){
            for (String s : regxResult) {
                String inputFieldValue = getDataStreamFieldInfoValue(row, inputFields, s);
                highLevelSearchCon = highLevelSearchCon.replace("${"+s+"}",inputFieldValue);
            }
        }
        return highLevelSearchCon;
    }

    /**
     * 占位符测试
     * @param highLevelSearchCon
     * @return
     */
    private static List<String> getRegxResult(String highLevelSearchCon) {
        Pattern pattern = Pattern.compile("\\$\\{(.*?)\\}");
        Matcher matcher = pattern.matcher(highLevelSearchCon);
        List<String> list = new ArrayList<>();
        while (matcher.find()){
            list.add(matcher.group(1));
        }
        return list;
    }


}
