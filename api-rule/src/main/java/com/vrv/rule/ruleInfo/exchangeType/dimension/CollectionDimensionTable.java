package com.vrv.rule.ruleInfo.exchangeType.dimension;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.rule.model.DimensionCompletionConfig;
import com.vrv.rule.util.FieldInfoUtil;
import com.vrv.rule.util.FilterOperatorUtil;
import com.vrv.rule.util.JdbcSingeConnectionUtil;
import com.vrv.rule.util.RedissonSingleUtil;
import com.vrv.rule.util.gson.GsonUtil;
import com.vrv.rule.vo.DimensionKeyVO;
import com.vrv.rule.vo.FieldInfoVO;
import com.vrv.rule.vo.LogicOperator;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.async.ResultFuture;
import org.apache.flink.streaming.api.functions.async.RichAsyncFunction;
import org.apache.flink.types.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 维表连接异步操作
 *
 * @author wd-pc
 */
public class CollectionDimensionTable extends RichAsyncFunction<Row, Row> {

    /**
     *
     */
    private static final long serialVersionUID = 1L;


    private static final Logger logger = LoggerFactory.getLogger(CollectionDimensionTable.class);

    private static final Gson gson = GsonUtil.getGson();


    private List<FieldInfoVO> inputFields;
    private List<FieldInfoVO> outputFileds;
    private DimensionCompletionConfig dimensionCompletionConfig;
    private String filterCode;
    private String ruleCode;


    public CollectionDimensionTable(List<FieldInfoVO> inputFields, List<FieldInfoVO> outputFileds, DimensionCompletionConfig dimensionCompletionConfig, String filterCode, String ruleCode) {
        this.dimensionCompletionConfig = dimensionCompletionConfig;
        this.inputFields = inputFields;
        this.outputFileds = outputFileds;
        this.filterCode = filterCode;
        this.ruleCode = ruleCode;
    }

    public CollectionDimensionTable() {

    }


    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);


    }


    @Override
    public void asyncInvoke(Row inputs, ResultFuture<Row> resultFuture) throws Exception {
        dealResult(inputs, resultFuture);

    }


    /**
     * 超时补全方式
     * @param input
     * @param resultFuture
     */
    private void dealResultByTimeOutDeal(Row input, ResultFuture<Row> resultFuture) {
        input = getConvertRow(input,dimensionCompletionConfig);
        List<Row> results = new ArrayList<>();
        results.add(input);
        resultFuture.complete(results);
    }

    /**
     * 异步IO请求方式
     * @param input
     * @param resultFuture
     */
    private void dealResult(Row input, ResultFuture<Row> resultFuture) {
        CompletableFuture.supplyAsync(() -> {
            Row convertRow = getConvertRow(input, dimensionCompletionConfig);
            List<Row> results = new ArrayList<>();
            results.add(convertRow);
            return results;
        }).thenAccept((List<Row> dbResult) -> {
            // 设置请求完成时的回调: 将结果传递给 collector
            resultFuture.complete(dbResult);
        });
    }

    /**
     * 获得转换后的row
     * @param input
     * @param dimensionCompletionConfig
     * @return
     */
    private Row getConvertRow(Row input,DimensionCompletionConfig dimensionCompletionConfig) {
        String dimensionTableName = dimensionCompletionConfig.getDimensionTableName();
        String dimensionFieldName = dimensionCompletionConfig.getDimensionFieldName();
        String filterCon = dimensionCompletionConfig.getFilterCon();
        String dimensionAliasName = dimensionCompletionConfig.getDimensionAliasName();
        String highLevelSearchCon = dimensionCompletionConfig.getHighLevelSearchCon();
        String filterConditions = getDimensionFilterConditions(dimensionCompletionConfig, input, inputFields);
        StringBuffer sb = new StringBuffer();
        List<Map<String, Object>> list = getList(dimensionTableName, dimensionFieldName, filterCon, dimensionAliasName, filterConditions, highLevelSearchCon,sb,input);
        //TODO 进行集合操作
        Row resultRow = getConvertResultRow(input, dimensionCompletionConfig, dimensionAliasName, list);
        return resultRow;
    }


    /**
     * 将map对象当中的非字符串类型的值都转成string类型
     * @param list
     */
    private void convertMapElement(List<Map<String,Object>> list){
        for (Map<String, Object> map : list) {
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                Object value = map.get(key);
                if(value!=null && !(value instanceof String)){
                    map.put(key, value.toString());
                }
            }
        }
    }

    /**
     * 获得转换的结果Row
     * @param input
     * @param dimensionCompletionConfig
     * @param dimensionAliasName
     * @param list
     * @return
     */
    private Row getConvertResultRow(Row input, DimensionCompletionConfig dimensionCompletionConfig, String dimensionAliasName, List<Map<String, Object>> list) {
        Boolean collectionOutput = dimensionCompletionConfig.getCollectionOutput();
        if(collectionOutput){ //是结合输出
            convertMapElement(list);//进行一次转换对于集合输出的结果
            String collectionOutputType = dimensionCompletionConfig.getCollectionOutputType();
            String[] dimensionFieldArray = dimensionAliasName.split(",");  //筛选字段为
            if("stringArray".equals(collectionOutputType) && dimensionFieldArray.length==1){  //补充成字符串数组（只包括一个筛选字段）
                Row newRow = getStringArrayRow(input, list, dimensionFieldArray);
                return newRow;
            }else{  //补充成对象数组（只包括一个筛选字段）
                Row newRow =  getObjectRow(input, list);
                return newRow;
            }
        }else{ //非集合输出只取查询输出的第一条
            Row newRow = getSimpleRow(input, list);
            return newRow;

        }
    }


    /**
     * 从维表当中获得对应的数据
     * @param dimensionTableName
     * @param dimensionFieldName
     * @param filterCon
     * @param dimensionAliasName
     * @param filterConditions
     * @param sb
     * @return
     */
    private List<Map<String, Object>> getList(String dimensionTableName, String dimensionFieldName, String filterCon, String dimensionAliasName, String filterConditions,String highLevelSearchCon, StringBuffer sb,Row input) {
        List<Map<String, Object>> list = new ArrayList<>();
        String highLevelCondtion = FieldInfoUtil.setLogFieldValueByHighLevelSearchCon(highLevelSearchCon,input,inputFields);
        DimensionKeyVO dimensionKeyVO = DimensionKeyVO.builder().dimensionTableName(dimensionTableName)
                .dimensionFieldName(dimensionFieldName)
                .filterCondition(filterConditions)
                .highFilterCondition(highLevelCondtion)
                .filterCode(filterCode)
                .ruleCode(ruleCode)
                .build();
        String dataKey = FilterOperatorUtil.getDimensionDataKey(dimensionKeyVO);
        if (RedissonSingleUtil.getInstance().exists(dataKey)) {
            String json = null;
            try{
                json = RedissonSingleUtil.getInstance().get(dataKey);
                setDimentionList(list, json);
            }catch (Exception e){
                // logger.error("json解析出现错误：{}",json);
                 json = DimensionUtil.subCacheVO(json);
                 setDimentionList(list, json);
            }
        } else {
            String sql = getFilterDimensionSql(dimensionTableName, dimensionFieldName, filterConditions, sb, filterCon, dimensionAliasName,highLevelSearchCon,input);
            logger.info("维表对应得值sql:{}", sql);
            try {
                 list = JdbcSingeConnectionUtil.getInstance().querySqlForList(sql);
            } catch (Exception e) {
                logger.error("sql访问异常，请检查！ sql：{},异常报错:{}", sql, e);
            }
            RedissonSingleUtil.getInstance().set(dataKey, gson.toJson(list));
        }
        return list;
    }

    /**
     * 获得对象row元素
     * @param input
     * @param list
     * @return
     */
    private Row getObjectRow(Row input, List<Map<String, Object>> list) {
        Object idRoomField = input.getField(input.getArity() - 1);
        Map<String,Object>[] results = list.toArray(new  Map[list.size()]);  //将list转换成数组

        Row newRow = new Row(outputFileds.size());
        for (int i = 0; i < input.getArity() - 1; i++) {
            newRow.setField(i, input.getField(i));
        }
        int begin = inputFields.size() - 1;
        newRow.setField(begin, results);
        //日志相关数据
        newRow.setField(outputFileds.size() - 1, idRoomField);
        return newRow;
    }

    /**
     * 获得StringArray类型row
     * @param input
     * @param list
     * @param dimensionFieldArray
     * @return
     */
    private Row getStringArrayRow(Row input, List<Map<String, Object>> list, String[] dimensionFieldArray) {
        List<String> fields = new ArrayList<>();
        for (Map<String, Object> map : list) {
            Object o = map.get(dimensionFieldArray[0]);
            fields.add(o.toString());
        }
        //TODO 转换成string数组
        String[] fieldsArr = fields.toArray(new String[fields.size()]);

        Object idRoomField = input.getField(input.getArity() - 1);
        Row newRow = new Row(outputFileds.size());
        for (int i = 0; i < input.getArity() - 1; i++) {
            newRow.setField(i, input.getField(i));
        }
        int begin = inputFields.size() - 1;
        newRow.setField(begin, fieldsArr);
        //日志相关数据
        newRow.setField(outputFileds.size() - 1, idRoomField);
        return newRow;
    }

    /**
     * 获得单条数据
     * @param input
     * @param list
     * @return
     */
    private Row getSimpleRow(Row input, List<Map<String, Object>> list) {
        Row newRow = new Row(outputFileds.size());
        if(list.size()>0){
            Map<String, Object> map = list.get(0);
            Object idRoomField = input.getField(input.getArity() - 1);
            for (int i = 0; i < input.getArity() - 1; i++) {
                newRow.setField(i, input.getField(i));
            }
            Set<String> keySet = map.keySet();
            List<String> result = new ArrayList<>(keySet);
            int size = keySet.size();
            int begin = inputFields.size() - 1;
            int j = 0;
            for (int i = begin; i < begin + size; i++, j++) {
                String key = result.get(j);
                newRow.setField(i, map.get(key));
            }
            newRow.setField(outputFileds.size() - 1, idRoomField);
        }else{
            Object idRoomField = input.getField(input.getArity() - 1);
            for (int i = 0; i < input.getArity() - 1; i++) {
                newRow.setField(i, input.getField(i));
            }
            int begin = inputFields.size() - 1;
            newRow.setField(begin, null);
            //日志相关数据
            newRow.setField(outputFileds.size() - 1, idRoomField);
        }
        return newRow;
    }

    /**
     * 设置dimensionList
     * @param list
     * @param json
     */
    private void setDimentionList(List<Map<String, Object>> list, String json) {
        Type listType = new TypeToken<List<Map<String,Object>>>(){}.getType();
        List<Map<String, Object>> mapList = gson.fromJson(json,listType);
        list.addAll(mapList);
    }



    /**
     * 获得过滤维表
     *
     * @param dimensionTableName
     * @param dimensionFieldName
     * @param filterConditions
     * @param sb
     * @param filterCon
     * @param dimensionAliasName
     */
    private String getFilterDimensionSql(String dimensionTableName, String dimensionFieldName, String filterConditions,
                                         StringBuffer sb,String filterCon,String dimensionAliasName,String highFilterConditions,Row input) {
        if (StringUtils.isBlank(dimensionAliasName)){
            dimensionAliasName=dimensionFieldName;
        }
        String fieldNameAsAliasName=getFieldNameAsAliasName(dimensionFieldName,dimensionAliasName);

        sb.append("select").append(" ").append(fieldNameAsAliasName).append(" ").append("from").append(" ").append(dimensionTableName).append(" ").append("where").append(" ").append("1=1");
        //逻辑树模式
        if (StringUtils.isNotEmpty(filterConditions)) {
            sb.append(" ").append("and").append(" ").append(filterConditions);
        }

        //高级模式
        if(StringUtils.isNotEmpty(highFilterConditions)){
            highFilterConditions = FieldInfoUtil.setLogFieldValueByHighLevelSearchCon(highFilterConditions, input,inputFields);
            sb.append(" ").append(highFilterConditions);
        }

        //维表过滤条件模式
        if (StringUtils.isNotBlank(filterCon)){
            sb.append(" ").append("and").append(" ").append(filterCon);
        }
//        if(dimensionTableName.startsWith("baseline")){
//            String ruleId = ruleCode.split("-")[0];
//            String newRuleCode = ruleId.split("_")[1];
//            sb.append(" and filter_code='"+filterCode).append("' ").append("and rule_code='"+newRuleCode+"'");
//        }
        if (sb.indexOf("limit")<0){
            sb.append(" limit 0,1000");
        }
        String sql = sb.toString();
        return sql;
    }


    private String getFieldNameAsAliasName(String dimensionFieldName, String dimensionAliasName) {
        List<String> strings=new ArrayList<>();
        String[] dimensionFieldNames = dimensionFieldName.split(",");
        String[] dimensionAliasNames = dimensionAliasName.split(",");
        for (int i = 0; i <dimensionFieldNames.length ; i++) {
            strings.add(dimensionFieldNames[i]+" "+"as"+" "+dimensionAliasNames[i]);
        }
        return StringUtils.join(strings,",");
    }

    @Override
    public void close() throws Exception {
        super.close();
    }

    /**
     * 获得维表过滤条件
     *
     * @param dimensionCompletionConfig
     * @param row
     * @param inputFields
     * @return
     */
    private String getDimensionFilterConditions(DimensionCompletionConfig  dimensionCompletionConfig, Row row, List<FieldInfoVO> inputFields) {
        LogicOperator loginExp = dimensionCompletionConfig.getLoginExp();
        if (loginExp != null) {
            String dimensionFilterCondition = loginExp.getDimensionFilterCondition(row, inputFields);
            return dimensionFilterCondition;
        } else {
            return null;
        }
    }

    @Override
    public void timeout(Row input, ResultFuture<Row> resultFuture) throws Exception {
        dealResultByTimeOutDeal(input, resultFuture);
    }


}
