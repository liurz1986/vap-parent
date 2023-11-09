package com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.config.ExecutorConfig;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.controller.AlarmCommonDataController;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.service.AlarmCommonDataService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.ChangeRiskReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.FilterFieldReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.req.FilterSourceReq;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.res.FilterSourceRes;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.FilterOperator;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventColumService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.util.PageReqESUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.RuleFilterService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.vo.RuleFilter;
import com.vrv.vap.alarmdeal.business.analysis.model.EventColumn;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Column;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Exchanges;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.FilterConfigObject;
import com.vrv.vap.alarmdeal.business.analysis.model.filteroperator.config.Tables;
import com.vrv.vap.alarmdeal.business.analysis.server.FilterOperatorService;
import com.vrv.vap.alarmdeal.business.analysis.vo.filterOpertorVO.OutFieldInfo;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSource;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSourceField;
import com.vrv.vap.alarmdeal.frameworks.feign.DataSourceFegin;
import com.vrv.vap.alarmdeal.frameworks.util.RedissonSingleUtil;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.page.PageReq_ES;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

/**
 * @author: 梁国露
 * @since: 2022/11/28 15:40
 * @description:
 */
@Service
public class AlarmCommonDataServiceImpl implements AlarmCommonDataService {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(AlarmCommonDataServiceImpl.class);
    /**
     * gson对象
     */
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Autowired
    private FilterOperatorService filterOperatorService;

    @Autowired
    private RuleFilterService ruleFilterService;

    @Autowired
    private EventColumService eventColumService;

    @Autowired
    private MapperUtil mapperUtil;
//
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedissonSingleUtil redissonSingleUtil;

    @Autowired
    private DataSourceFegin dataSourceFegin;

    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForESService;

    @Autowired
    private ElasticSearchRestClient elasticSearchRestClient;

    @Autowired
    ElasticSearchRestClientService elasticSearchRestService;


    @Override
    public Boolean filterColumnFresh(FilterFieldReq req) {
        logger.info("filterColumnFresh start!");
        List<QueryCondition> conditionList = new ArrayList<>();
        conditionList.add(QueryCondition.eq("deleteFlag",true));
        if(CollectionUtils.isNotEmpty(req.getFilterIds())){
            conditionList.add(QueryCondition.in("guid",req.getFilterIds()));
        }
        List<FilterOperator> filterOperators = filterOperatorService.findAll(conditionList);
        logger.info("遍历规则进行规则字段重排序,size={}",filterOperators.size());
        for(FilterOperator filterOperator : filterOperators){
            String source = filterOperator.getSourceIds();
            List<String> sourceIds = gson.fromJson(source,List.class);
            String sourceId = sourceIds.get(0);
            // 获取字段
            List<EventColumn> fields =  eventColumService.getEventColumnCurr(sourceId);
            // 获取规则字段信息
            String filterConfig = filterOperator.getFilterConfig();
            FilterConfigObject filterConfigObject = gson.fromJson(filterConfig,FilterConfigObject.class);
            Tables[][] tables = filterConfigObject.getTables();
            List<Column> columns = updateField(fields);
            for(int i=0;i<tables.length;i++){
                for(int j =0;j<tables[i].length;j++){
                    // 遍历得到数据节点信息
                    Tables table = tables[i][j];
                    List<Column> columnList = table.getColumn();
                    List<Column> configColumns = updateColumn(columnList,columns);
                    table.setColumn(configColumns);
                }
            }
            filterConfigObject.setTables(tables);
            filterOperator.setFilterConfig(gson.toJson(filterConfigObject));

            List<OutFieldInfo> old = JSONArray.parseArray(filterOperator.getOutFieldInfos(), OutFieldInfo.class);
            List<OutFieldInfo> newOutFieldInfo = updateOutFieldInfos(fields,old);
            filterOperator.setOutFieldInfos(gson.toJson(newOutFieldInfo));
            filterOperatorService.save(filterOperator);
        }
        return true;
    }

    @Override
    public Map<String,List<FilterSourceRes>> updateFilterColumn(FilterSourceReq req) {
        logger.info("updateFilterColumn start !");
        Map<String,List<FilterSourceRes>> resultMap = new HashMap<>();
        if(req.getSource().isEmpty()){
            return resultMap;
        }
        for(Map.Entry<String,List<String>> entry :req.getSource().entrySet()){
            String sourceId = entry.getKey();
            List<String> updateFields = entry.getValue();
            List<FilterSourceRes> result = new ArrayList<>();
            // 获取字段
            List<EventColumn> fields =  eventColumService.getEventColumnCurr(sourceId);
            logger.info("通过传入数据源id ={},获取数据源字段={}",sourceId,fields.size());
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.like("sourceIds",sourceId));
            conditions.add(QueryCondition.eq("deleteFlag",true));
            List<FilterOperator> filterOperators = filterOperatorService.findAll(conditions);
            logger.info("遍历该数据源的全部规则,size={}",filterOperators.size());
            for(FilterOperator filterOperator : filterOperators){
                // 获取options信息
                // 获取规则字段信息
                String filterConfig = filterOperator.getFilterConfig();
                FilterConfigObject filterConfigObject = gson.fromJson(filterConfig,FilterConfigObject.class);
                Exchanges[][] exchanges = filterConfigObject.getExchanges();
                for(int i=0;i<exchanges.length;i++){
                    for(int j =0;j<exchanges[i].length;j++){
                        // 遍历得到数据节点信息
                        Exchanges exchange = exchanges[i][j];
                        String options = exchange.getOptions();
                        if(StringUtils.isNotBlank(options)){
                            for(String name : updateFields){
                                if(options.contains(name)){
                                    FilterSourceRes res = new FilterSourceRes();
                                    res.setFilterId(filterOperator.getGuid());
                                    res.setFilterName(filterOperator.getLabel());
                                    result.add(res);
                                }
                            }
                        }
                    }
                }
            }
            logger.info("数据源:{},需要手动处理的规则，size={}",sourceId,result.size());
            // 过滤出，能够自动处理的规则
            List<FilterOperator> filterOperatorList = new ArrayList<>();
            if(CollectionUtils.isNotEmpty(result)){
                List<String> ids = result.stream().map(FilterSourceRes::getFilterId).collect(toList());
                for(FilterOperator filterOperator:filterOperators){
                    if(!ids.contains(filterOperator.getGuid())){
                        filterOperatorList.add(filterOperator);
                    }
                }
            }
            if(CollectionUtils.isEmpty(filterOperatorList)){
                filterOperatorList.addAll(filterOperators);
            }
            logger.info("数据源:{},能够自动处理的规则，size={}",sourceId,filterOperatorList.size());
            // 自动处理
            for(FilterOperator filterOperator : filterOperatorList){
                String filterConfig = filterOperator.getFilterConfig();
                FilterConfigObject filterConfigObject = gson.fromJson(filterConfig,FilterConfigObject.class);
                Tables[][] tables = filterConfigObject.getTables();
                List<Column> columns = updateField(fields);
                for(int i=0;i<tables.length;i++){
                    for(int j =0;j<tables[i].length;j++){
                        // 遍历得到数据节点信息
                        if(i==0 && j==0){
                            Tables table = tables[i][j];
                            table.setColumn(columns);
                            continue;
                        }
                        Tables table = tables[i][j];
                        List<Column> columnList = table.getColumn();
                        List<Column> configColumns = updateColumn(columnList,columns);
                        table.setColumn(configColumns);
                    }
                }
                filterConfigObject.setTables(tables);
                filterOperator.setFilterConfig(gson.toJson(filterConfigObject));
                List<OutFieldInfo> old = JSONArray.parseArray(filterOperator.getOutFieldInfos(), OutFieldInfo.class);
                List<OutFieldInfo> newOutFieldInfo = updateOutFieldInfos(fields,old);
                filterOperator.setOutFieldInfos(gson.toJson(newOutFieldInfo));
                filterOperatorService.save(filterOperator);
            }
            List<FilterSourceRes> result1 = result.stream().distinct().collect(toList());
            resultMap.put(sourceId,result1);
        }
        return resultMap;
    }

    @Override
    public List<String> changeRiskList(ChangeRiskReq req) {
        List<String> result = new ArrayList<>();
        List<String> oneList = req.getOneList();
        List<String> twoList = req.getTwoList();
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.in("ruleId",oneList));
        List<RuleFilter> oneRuleFilter =ruleFilterService.findAll(conditions);
        List<String> oneFilters = oneRuleFilter.stream().map(RuleFilter::getFilterCode).collect(toList());

        conditions.clear();
        List<String> twoFilters = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(twoList)){
            conditions.add(QueryCondition.in("ruleId",twoList));
            List<RuleFilter> twoRuleFilter =ruleFilterService.findAll(conditions);
            List<String> twoFilters1 = twoRuleFilter.stream().map(RuleFilter::getFilterCode).collect(toList());
            twoFilters.addAll(twoFilters1);
        }

        // 计算差集
        List<String> cj= twoFilters.stream().filter(item -> !oneFilters.contains(item)).collect(toList());
        if(CollectionUtils.isNotEmpty(cj)){
            List<QueryCondition> queryConditions = new ArrayList<>();
            queryConditions.add(QueryCondition.in("code",cj));
            queryConditions.add(QueryCondition.eq("deleteFlag",true));
            List<FilterOperator> filterOperators = filterOperatorService.findAll(queryConditions);
            List<String> names = filterOperators.stream().map(item->item.getLabel().concat("(").concat(item.getCode()).concat(")")).collect(toList());
            result.addAll(names);
        }
        return result;
    }

    @Override
    public List<String> deleteRedisData(String name) {
        List<String> keys = redissonSingleUtil.findList(name);
        redissonSingleUtil.deleteByPrex(name);
        return keys;
    }

    @Override
    public String getRedisData(String name) {
        String value = redissonSingleUtil.get(name);
        return value;
    }

    @Override
    public List<String> getRedisKeysData(String name) {

        List<String> keys = redissonSingleUtil.findList(name);
        return keys;
    }

    @Override
    public Set<String> getRedisKeysList(String name) {
        Set<Object> list = redisUtil.sGet(name+"*");
        Set<String> result = new HashSet<>();
        for(Object obj : list){
            result.add(String.valueOf(obj));
        }
        return result;
    }

    @Override
    public Boolean getDataSource() {
        List<DataSource> dataSources = dataSourceFegin.getSource().getData();
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("deleteFlag",true));
        List<FilterOperator> filterOperatorList = filterOperatorService.findAll();
        filterOperatorList.stream().forEach(item->{
            // 增加topicname
            String filterConfig = item.getFilterConfig();
            FilterConfigObject filterConfigObject = gson.fromJson(filterConfig,FilterConfigObject.class);
            Tables[][] tables = filterConfigObject.getTables();
            List<String> sourceIds = new ArrayList<>();
            for(int i=0;i<tables.length;i++){
                for(int j =0;j<tables[i].length;j++){
                    Tables tables2 = tables[i][j];
                    String name = tables2.getName();
                    String topicName = getTopicNameByName(dataSources,name);
                    String sourceId = getSourceIdByName(dataSources,name);
                    if(StringUtils.isBlank(topicName) && StringUtils.isNotBlank(name)){
                        topicName = getTopicNameByName(dataSources,name.substring(0,name.length()-1));
                        sourceId = getSourceIdByName(dataSources,name.substring(0,name.length()-1));
                    }
                    if(StringUtils.isNotBlank(topicName)){
                        tables2.setTopicName(topicName);
                    }
                    if(StringUtils.isNotBlank(sourceId)){
                        sourceIds.add(sourceId);
                        tables2.setEventTableId(sourceId);
                    }

                    List<Column> columnList = tables2.getColumn();
                    if(StringUtils.isNotBlank(sourceId)){
                        String finalSourceId = sourceId;
                        columnList.stream().forEach(column -> {
                            String id = getColumeIdByName(column.getName(), finalSourceId);
                            if(StringUtils.isNotBlank(id)){
                                column.setId(id);
                            }
                        });
                        Collections.sort(columnList, Comparator.comparing(Column::getOrder));
                        tables2.setColumn(columnList);
                    }
                }
            }
            filterConfigObject.setTables(tables);
            item.setFilterConfig(gson.toJson(filterConfigObject));
            sourceIds = sourceIds.stream().distinct().collect(Collectors.toList());
            // 修改数据源id
            if(CollectionUtils.isNotEmpty(sourceIds)){
                item.setSourceIds(gson.toJson(sourceIds));
            }
            filterOperatorService.save(item);
        });
        return true;
    }

    @Override
    public Boolean handleAlarmEsData() {
        List<QueryCondition_ES> param = new ArrayList<>();
        long count = alarmEventManagementForESService.count(param);
        List<AlarmEventAttribute> result = new ArrayList<>();
        if(count <= 1000){
            List<AlarmEventAttribute> list = alarmEventManagementForESService.findAll(param);
            result.addAll(list);
        }else{
            long pageNum = count % 1000 == 0 ? count / 1000 : count / 1000 + 1;
            long fromIndex, toIndex;
            for (long i = 0; i < pageNum; i++) {
                fromIndex = i * 1000;
                toIndex = Math.min(count, fromIndex + 1000);
                List<AlarmEventAttribute> list = getAlarmEventAttributeData(fromIndex,1000);
                result.addAll(list);
            }
        }
        elasticSearchRestClient.delIndexByIndexName(alarmEventManagementForESService.getIndexName());
        saveAlarmData(count,result);
        return true;
    }

    public void saveAlarmData(long count,List<AlarmEventAttribute> result){
        ThreadPoolExecutor executor = new ThreadPoolExecutor(2,3,5, TimeUnit.MICROSECONDS,new LinkedBlockingDeque<>(100));
        long pageNum = count % 1000 == 0 ? count / 1000 : count / 1000 + 1;
        long fromIndex, toIndex;
        for (long i = 0; i < pageNum; i++) {
            fromIndex = i * 1000;
            toIndex = Math.min(count, fromIndex + 1000);
            List<AlarmEventAttribute> subList =result.subList(Integer.valueOf(String.valueOf(fromIndex)),Integer.valueOf(String.valueOf(toIndex)));
            // 开启线程调用
            Runnable runnable = () -> alarmEventManagementForESService.addList(alarmEventManagementForESService.getIndexName(),subList);
            executor.execute(runnable);
        }

    }

    public List<AlarmEventAttribute> getAlarmEventAttributeData(long start,long size){
        List<QueryCondition_ES> param = new ArrayList<>();
        PageReq pageReq = new PageReq();
        pageReq.setStart_(Integer.valueOf(String.valueOf(start)));
        pageReq.setCount_(Integer.valueOf(String.valueOf(size)));
        PageReq_ES pageQuery = PageReqESUtil.getPageReq_ES(pageReq);
        PageRes_ES<AlarmEventAttribute> findByPage = elasticSearchRestService.findByPage(pageQuery, param);
        List<AlarmEventAttribute> list = findByPage.getList();
        return list;
    }

    public String getTopicNameByName(List<DataSource> dataSources, String name){
        if(StringUtils.isBlank(name)){
            return null;
        }
        List<DataSource> dataSources1 = dataSources.stream().filter(item->name.equals(item.getTopicAlias())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(dataSources1)){
            DataSource dataSource = dataSources1.get(0);
            return dataSource.getTopicName();
        }
        return null;
    }

    public String getSourceIdByName(List<DataSource> dataSources,String name){
        if(StringUtils.isBlank(name)){
            return null;
        }
        List<DataSource> dataSources1 = dataSources.stream().filter(item->name.equals(item.getTopicAlias())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(dataSources1)){
            DataSource dataSource = dataSources1.get(0);
            return String.valueOf(dataSource.getId());
        }
        return null;
    }

    public String getColumeIdByName(String name,String sourceId){
        List<DataSourceField> dataSourceFields = dataSourceFegin.getFieldBySourceId(sourceId).getData();
        List<DataSourceField> dataSources1 = dataSourceFields.stream().filter(item->name.equals(item.getField())).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(dataSources1)){
            DataSourceField dataSource = dataSources1.get(0);
            return String.valueOf(dataSource.getId());
        }
        return null;
    }

    public List<OutFieldInfo> updateOutFieldInfos(List<EventColumn> fields,List<OutFieldInfo> outFieldInfos){
        List<OutFieldInfo> result = new ArrayList<>();
        for(int i=0;i<fields.size();i++){
            OutFieldInfo outFieldInfo = new OutFieldInfo();
            outFieldInfo.setFieldName(fields.get(i).getName());
            outFieldInfo.setFieldLabel(fields.get(i).getLabel());
            outFieldInfo.setFieldType(fields.get(i).getType());
            outFieldInfo.setOrder(i);
        }
        OutFieldInfo last = outFieldInfos.get(outFieldInfos.size()-1);
        result.add(last);
        Collections.sort(result,Comparator.comparing(OutFieldInfo::getOrder));
        return result;
    }

    public List<Column> updateField(List<EventColumn> fields){
        List<Column> result = new ArrayList<>();
        for(EventColumn field : fields){
            Column column = new Column();
            column.setId(String.valueOf(field.getId()));
            column.setExp(field.getName());
            column.setName(field.getName());
            column.setLabel(field.getLabel());
            column.setDataType(field.getType());
            column.setEventTime(false);
            column.setOrder(field.getOrder());
            result.add(column);
        }
        return result;
    }

    public List<Column> updateColumn(List<Column> configColumns,List<Column> oneColumns){
        List<Column> addList = new ArrayList<>();
        List<Column> baseList = new ArrayList<>();
        for(Column column : configColumns){
            String id = column.getId();
            if(id.startsWith("col")){
                addList.add(column);
            }
        }
        String expStartStr = "";
        String aggType = "";

        for(int i =0;i<3;i++){
            Column column = configColumns.get(i);
            String[] expArr = column.getExp().split("\\.");
            if(expArr.length>1 && StringUtils.isBlank(expStartStr)){
                expStartStr = expArr[0];
            }
            if(StringUtils.isBlank(aggType)){
                aggType=column.getAggType();
            }
        }

        for(Column column : oneColumns){
            Column columnNew =mapperUtil.map(column,Column.class);
            List<Column> oldFilter = configColumns.stream().filter(item->item.getName().equals(column.getName())).collect(toList());
            if(StringUtils.isNotBlank(expStartStr)){
                String name = column.getExp();
                columnNew.setExp(expStartStr.concat(".").concat(name));
                if(StringUtils.isNotBlank(aggType)){
                    columnNew.setAggType(aggType);
                }
            }else{
                if(CollectionUtils.isNotEmpty(oldFilter)){
                    columnNew.setAggType(oldFilter.get(0).getAggType());
                    columnNew.setExp(oldFilter.get(0).getExp());
                }
            }
            baseList.add(columnNew);
        }

        baseList.addAll(addList);
        List<Column> resultList = freshColumn(baseList);
        return resultList;
    }

    /**
     * 字段排序
     * @param columns
     * @return
     */
    public List<Column> freshColumn(List<Column> columns){
        List<Column> list = new ArrayList<>();
        for(int i=0 ;i<columns.size();i++){
            Column column = columns.get(i);
            column.setOrder(i);
            list.add(column);
        }
        Collections.sort(list, Comparator.comparing(Column::getOrder));
        return list;
    }
}
