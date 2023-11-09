package com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.AlarmAnalysisService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.DataRow;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.NameValueBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.RedisUtil;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.EventTabelService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.FilterSourceStatus;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.bean.FilterSourceStatusInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.FilterSourceStatusService;
import com.vrv.vap.alarmdeal.business.alaramevent.strategy.service.repository.FilterSourceStatusRepository;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableService;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSource;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.DataSourceFegin;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2022年04月06日 18:04
 */
@Service
public class FilterSourceStatusServiceImpl extends BaseServiceImpl<FilterSourceStatus, String> implements FilterSourceStatusService {
    private static Logger logger = LoggerFactory.getLogger(FilterSourceStatusServiceImpl.class);
    @Autowired
    private FilterSourceStatusRepository filterSourceStatusRepository;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private EventTabelService eventTabelService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DimensionTableService dimensionTableService;

    @Autowired
    private ElasticSearchRestClient elasticSearchRestClient;

    @Autowired
    private AlarmAnalysisService alarmAnalysisService;

    @Autowired
    private DataSourceFegin dataSourceFegin;

    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForEsService;

    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    public BaseRepository<FilterSourceStatus, String> getRepository() {
        return filterSourceStatusRepository;
    }

    @Override
    public boolean getFilterSourceStatusByRedis(String sourceId) {
        Object status = redisUtil.hget("vap_source_message",sourceId);
        if(status != null){
            FilterSourceStatusInfo filterSourceStatusinfo = gson.fromJson(String.valueOf(status), FilterSourceStatusInfo.class);
            saveFilterSourceStatus(filterSourceStatusinfo);
            if(filterSourceStatusinfo.getData_status() == 1){
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFilterSourceStatusMsgByRedis(String sourceId) {
        Object status = redisUtil.hget("vap_source_message",sourceId);
        if(status != null){
            FilterSourceStatusInfo filterSourceStatusinfo = gson.fromJson(String.valueOf(status), FilterSourceStatusInfo.class);
            saveFilterSourceStatus(filterSourceStatusinfo);
            if(filterSourceStatusinfo.getData_status() == 0){
                // 不满足条件
                return filterSourceStatusinfo.getDataTopicName()+"["+filterSourceStatusinfo.getMsg()+"]";
            }
        }else {
           logger.info("vap_source_message当中没有数据，请检查！");
        }
        return null;
    }

    /**
     * 保存规则数据源状态信息
     * @param filterSourceStatusInfo
     */
    @Override
    public void saveFilterSourceStatus(FilterSourceStatusInfo filterSourceStatusInfo){
        // 删除该数据源的历史数据
        String sql = "delete from filter_source_status where data_source_id = {0};";
        sql = sql.replace("{0}",String.valueOf(filterSourceStatusInfo.getDataSourceId()));
        jdbcTemplate.execute(sql);
        FilterSourceStatus filterSourceStatus = new FilterSourceStatus();
        filterSourceStatus.setDataStatus(filterSourceStatusInfo.getData_status());
        filterSourceStatus.setDataSourceId(filterSourceStatusInfo.getDataSourceId());
        filterSourceStatus.setOpenStatus(filterSourceStatusInfo.getOpen_status());
        filterSourceStatus.setMsg(filterSourceStatusInfo.getMsg());
        filterSourceStatus.setId(UUIDUtils.get32UUID());
        save(filterSourceStatus);
    }

    @Override
    public String filterChange(String indexName, String insertTime) {
        String tableName = synchroData(indexName,insertTime);
        return tableName;
    }

    /**
     * 通过基线map 同步数据
     * @param indexName
     * @param insertTime
     * @return
     */
    private String synchroData(String indexName, String insertTime){
        // 1、获取索引与最新数据时间
        List<DataSource> dataSources = getDataSources(indexName);
        // 通过索引，查询表名
        String tableName = getTableName(indexName);
        if (tableName == null){
            logger.error("{}索引不存在对应的维表信息！",indexName);
            return null;
        }
        logger.warn("{}，维表查询成功！", tableName);
        // 根据维表配置，获取数据时间
        int days = getTablesDataDays(indexName);
        if(days > 1){
            String time = getTime(indexName,days,dataSources);
            if(StringUtils.isNotBlank(time)){
                insertTime = time;
            }
        }
        // 查询并保存数据
        queryAndSaveData(indexName, insertTime,tableName,days, dataSources);
        return tableName;
    }

    public String getTime(String baselineIndex,int days,List<DataSource> dataSources){
        if(days != 0){
            String times = getDataTime(baselineIndex,days,dataSources);
            if(StringUtils.isNotBlank(times)){
                return times;
            }
        }
        return null;
    }

    /**
     * 通过变动索引名称查询数据源
     * @param baselineIndex
     * @return
     */
    private List<DataSource> getDataSources(String baselineIndex) {
        Map<String,Object> conditionParam = new HashMap<>();
        conditionParam.put("name", baselineIndex);
        List<DataSource> dataSources = null;
        try {
            ResultObjVO<List<DataSource>> datas = dataSourceFegin.querySource(conditionParam);
            dataSources = datas.getList();
        }catch (Exception ex){
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(),"data source fegin 接口调用失败！");
        }
        return dataSources;
    }

    public int getTablesDataDays(String baselineIndex){
        List<QueryCondition> dimTableCondition = new ArrayList<>();
        dimTableCondition.add(QueryCondition.eq("baselineIndex", baselineIndex));
        List<DimensionTableInfo> display = dimensionTableService.findAll(dimTableCondition);
        if (CollectionUtils.isEmpty(display)) {
            return 0;
        }
        int days = display.get(0).getDays();
        return days;
    }

    public void handleDataTime(List<DataRow> dataRows){
        for(DataRow dataRow : dataRows){
            DataRow newDataRow = new DataRow();
            List<NameValueBean> nameValueBeans = dataRow.getRow();
            nameValueBeans.stream().forEach(item->{
                if("insert_time".equals(item.getName())){
                    Object obj = item.getValue();
                    String time = String.valueOf(obj);
                    time = time.substring(0,10);
                    item.setValue(time);
                }
            });
            newDataRow.setRow(nameValueBeans);
        }
    }

    public void queryAndSaveData(String baselineIndex,String baselineTime,String  tableName,int days,List<DataSource> dataSources){
        List<QueryCondition_ES> queryConditionEs = new ArrayList<>();
        queryConditionEs.add(QueryCondition_ES.in("insert_time", new ArrayList<>(Arrays.asList(baselineTime.split(",")))));
        // 1.5 通过tableName 判断data_source类型
        List<DataRow> baselineResult = getDataRows(baselineIndex, baselineTime, queryConditionEs,dataSources);
        // 数据转换
        if(days > 1){
            handleDataTime(baselineResult);
        }

        // 2、查询数据
        if (CollectionUtils.isNotEmpty(baselineResult)) {
            logger.info("{}，维表{}时间范围数据查询成功！", baselineIndex,baselineTime);
            // 3、保存数据
            alarmAnalysisService.saveDimensionTableData(tableName, null, null, baselineResult,true);
        }
    }

    /**
     * 获取数据时间
     * @param baselineIndex
     * @return
     */
    private String getDataTime(String baselineIndex,int days,List<DataSource> dataSources){
        if(CollectionUtils.isNotEmpty(dataSources)){
            DataSource dataSource = dataSources.get(0);
            List<String> timeList = new ArrayList<>();
            if(dataSource.getType() == 1){
                // 同步es数据
                List<QueryCondition_ES> conditionEs = new ArrayList<>();
                Map<String,Long> timeGroup = alarmEventManagementForEsService.getCountGroupByField(baselineIndex+"*","insert_time",conditionEs);
                timeGroup.forEach((time,count)->{
                    timeList.add(time);
                });
            }else if(dataSource.getType() == 2){
                // 同步mysql数据
                String tableName = dataSource.getTopicAlias();
                String sql = "select DATE_FORMAT(insert_time,\"%Y-%m-%d %H:%i:%s\") as time from " + tableName + " group by insert_time;";
                List<Map<String,Object>> baselineData = jdbcTemplate.queryForList(sql);
                baselineData.stream().forEach(item->{
                    String time = String.valueOf(item.get("time"));
                    timeList.add(time);
                });
            }
            // 排序
            if(CollectionUtils.isNotEmpty(timeList)){
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                Collections.sort(timeList, (s1, s2) -> LocalDateTime.parse(s2, formatter).
                        compareTo(LocalDateTime.parse(s1, formatter)));
                List<String> times = timeList.stream().limit(days).collect(Collectors.toList());
                return String.join(",",times);
            }
        }
        return null;
    }

    private List<DataRow> getDataRows(String baselineIndex, String baselineTime, List<QueryCondition_ES> queryConditionEs,List<DataSource> dataSources) {
        List<DataRow> baselineResult = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(dataSources)){
            DataSource dataSource = dataSources.get(0);
            if(dataSource.getType() == 1){
                // 同步es数据
                List<DataRow> data = getDataForEs(baselineIndex, queryConditionEs);
                baselineResult.addAll(data);
            }else if(dataSource.getType() == 2){
                // 同步mysql数据
                List<DataRow> data =getDataForDb(baselineIndex, baselineTime);
                baselineResult.addAll(data);
            }
        }
        return baselineResult;
    }

    private String getTableName(String baselineIndex) {
        List<QueryCondition> dimTableCondition = new ArrayList<>();
        dimTableCondition.add(QueryCondition.eq("baselineIndex", baselineIndex));
        List<DimensionTableInfo> display = dimensionTableService.findAll(dimTableCondition);
        List<String> tableNames = display.stream().map(DimensionTableInfo::getNameEn).distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tableNames)) {
            return null;
        }
        String tableName = tableNames.get(0);
        return tableName;
    }


    public List<DataRow> getDataForDb(String index, String baselineTime){
        Map<String,Object> map = new HashMap<>();
        map.put("name",index);
        ResultObjVO<List<DataSource>> data = dataSourceFegin.querySource(map);
        List<DataSource> dataSources = data.getList();
        if(CollectionUtils.isEmpty(dataSources)){
            return new ArrayList<>();
        }
        DataSource dataSource = dataSources.get(0);
        String tableName = dataSource.getTopicAlias();
        String[] timeArr = baselineTime.split(",");
        List<String> times = new ArrayList<>(Arrays.asList(timeArr));
        String timeStr = String.join("','",times);
        String sql = "select * from " + tableName + " where date_format(insert_time,'%Y-%m-%d %H:%i:%s') in ({0});";
        sql = sql.replace("{0}","'"+timeStr+"'");
        List<Map<String,Object>> baselineData = jdbcTemplate.queryForList(sql);

        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("baselineIndex",index));
        List<DimensionTableInfo> dimensionTableInfos = dimensionTableService.findAll(conditions);
        if(CollectionUtils.isEmpty(dimensionTableInfos)){
            return new ArrayList<>();
        }
        String dimensionTableName = dimensionTableInfos.get(0).getName();
        List<DataRow> dataRows = alarmAnalysisService.getDataRows(dimensionTableName,baselineData);
        return dataRows;
    }

    public List<DataRow> getDataForEs(String index, List<QueryCondition_ES> queryConditionEs) {
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(queryConditionEs);
        SearchResponse searchResponse = elasticSearchRestClient.getDocs(new String[]{index+"*"}, queryBuilder, null, null, 0, 1000);
        // 没有查到数据则返回
        if (searchResponse == null) {
            return new ArrayList<>();
        }

        // 1、获取数据
        SearchHit[] esResult = searchResponse.getHits().getHits();
        List<SearchHit> esResults = new CopyOnWriteArrayList<>(Arrays.asList(esResult));
        if (CollectionUtils.isEmpty(esResults)) {
            return new ArrayList<>();
        }
        List<Map<String,Object>> data = new ArrayList<>();
        for(SearchHit hit : esResult){
            data.add(hit.getSourceAsMap());
        }
        // 2、进行转换
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("baselineIndex",index));
        List<DimensionTableInfo> dimensionTableInfos = dimensionTableService.findAll(conditions);
        if(CollectionUtils.isEmpty(dimensionTableInfos)){
            return new ArrayList<>();
        }
        String tableName = dimensionTableInfos.get(0).getNameEn();
        List<DataRow> result = alarmAnalysisService.getDataRows(tableName,data);
        return result;
    }
}
