package com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.bean.dimension.DimensionTableInfo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.dao.impl.AlarmAnalysisDao;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.service.AlarmAnalysisService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.DataRow;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.NameValueBean;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmanalysis.vo.ParamsColumn;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.DimensionTableColumn;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmDealAggregationRow;
import com.vrv.vap.alarmdeal.business.analysis.server.DimensionTableService;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.frameworks.contract.dataSource.DataSource;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.alarmdeal.frameworks.feign.DataSourceFegin;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.service.ElasticSearchMapManage;
import com.vrv.vap.es.service.ElasticSearchRestClient;
import com.vrv.vap.es.service.ElasticSearchRestClientService;
import com.vrv.vap.es.util.DateUtil;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.IndexsInfoVO;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultObjVO;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import lombok.SneakyThrows;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.ParsedMin;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  
 *
 * @author wudi 
 *  E‐mail:wudi@vrvmail.com.cn
 *  @version 创建时间：2019年5月27日 下午3:20:20
 *  类说明     威胁大屏业务类
 */
@Service
public class AlarmAnalysisServiceImpl implements AlarmAnalysisService {

    @Autowired
    private AlarmAnalysisDao alarmAnalysisDao;

    @Autowired
    private ElasticSearchRestClient elasticSearchRestClient;

    @Autowired
    private ElasticSearchMapManage elasticSearchMapManage;

    @Autowired
    private DimensionTableService dimensionTableService;

    @Autowired
    private AppSysManagerService appSysManagerService;

    @Autowired
    private AssetService assetService;

    @Autowired
    private AdminFeign authFeign;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DataSourceFegin dataSourceFegin;

    private static Logger logger = LoggerFactory.getLogger(AlarmAnalysisServiceImpl.class);

    /**
     * 按威胁类型统计威胁值的和
     *
     * @return
     */
    @Override
    public Result<List<Map<String, Object>>> queryThreatValueByThreatType() {
        List<Map<String, Object>> list = alarmAnalysisDao.queryThreatValueByThreatType();
        Result<List<Map<String, Object>>> result = ResultUtil.success(list);
        return result;
    }

    /**
     * 根据威胁等级统计威胁等级的个数
     *
     * @return
     */
    @Override
    public Result<List<Map<String, Object>>> queryThreatLevelCountByThreatLevel() {
        List<Map<String, Object>> list = alarmAnalysisDao.queryThreatLevelCountByThreatLevel();
        Result<List<Map<String, Object>>> result = ResultUtil.success(list);
        return result;
    }

    /**
     * 根据部门进行威胁排分组
     *
     * @return
     */
    @Override
    public Result<List<Map<String, Object>>> queryThreatRankByDepartMent() {
        List<Map<String, Object>> list = alarmAnalysisDao.queryThreatRankByDepartMent();
        Result<List<Map<String, Object>>> result = ResultUtil.success(list);
        return result;
    }

    /**
     * 根据负责人进行威胁排名
     *
     * @return
     */
    @Override
    public Result<List<Map<String, Object>>> queryThreatRankByEmployee() {
        List<Map<String, Object>> list = alarmAnalysisDao.queryThreatRankByEmployee();
        Result<List<Map<String, Object>>> result = ResultUtil.success(list);
        return result;
    }

    @Override
    public List<DimensionTableColumn> getDimensionTableColumns(String dimensionTableName) {

        return alarmAnalysisDao.getDimensionTableColumns(dimensionTableName);
    }

    @Override
    public List<DataRow> getDimensionTableData(String dimensionTableName, String ruleId, List<ParamsColumn> columns) {
        List<DataRow> dimensionTableData = alarmAnalysisDao.getDimensionTableData(dimensionTableName, ruleId, columns);
        return dimensionTableData;
    }

    @Override
    public List<DataRow> getDimensionTableData(String dimensionTableName, String ruleId,String filterCode, List<ParamsColumn> columns) {
        List<DataRow> dimensionTableData = alarmAnalysisDao.getDimensionTableData(dimensionTableName, ruleId, filterCode,columns);
        return dimensionTableData;
    }

    @Override
    public void saveDimensionTableData(String dimensionTableName, String ruleId, List<DataRow> rows) {
        alarmAnalysisDao.saveDimensionTableData(dimensionTableName, ruleId, rows);
    }

    @Override
    public void saveDimensionTableData(String dimensionTableName, String ruleId, String filterCode, List<DataRow> rows) {
        alarmAnalysisDao.saveDimensionTableData(dimensionTableName, ruleId,filterCode, rows);
    }

    @Override
    public void saveDimensionTableData(String dimensionTableName,String  ruleId,String filterCode,List<DataRow> rows,boolean isSync) {
        alarmAnalysisDao.saveDimensionTableData(dimensionTableName, ruleId,filterCode, rows,isSync);
    }

    @Override
    public List<DataRow> getBaselineDataRows(String dimensionTableName, List<ParamsColumn> columns) {
        List<DataRow> dimensionTableData = alarmAnalysisDao.getDimensionTableData(dimensionTableName, columns);
        return dimensionTableData;
    }

    @Override
    public List<DataRow> getBaselineDataRows(String dimensionTableName) {
        try {
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("nameEn", dimensionTableName));
            conditions.add(QueryCondition.eq("tableType", "baseline"));
            List<DimensionTableInfo> dimensionTableInfos = dimensionTableService.findAll(conditions);
            if (dimensionTableInfos != null && !dimensionTableInfos.isEmpty()) {
                DimensionTableInfo dimensionTableInfo = dimensionTableInfos.get(0);

                String baselineIndex = dimensionTableInfo.getBaselineIndex();
                if (StringUtils.isEmpty(baselineIndex)) {
                    baselineIndex = dimensionTableInfo.getNameEn().replace('_', '-');
                }
                Map<String,Object> param = new HashMap<>();
                param.put("name",baselineIndex);
                ResultObjVO<List<DataSource>> data = dataSourceFegin.querySource(param);
                List<DataSource> dataSources = data.getData();
                if(CollectionUtils.isEmpty(dataSources)){
                    return new ArrayList<>();
                }
                DataSource dataSource = dataSources.get(0);
                List<DataRow> result = null;
                if(dataSource.getType() == 1){
                    result = getEsData(baselineIndex,dimensionTableName);
                }else if(dataSource.getType() == 2){
                    result = getDataForDb(baselineIndex);
                }
                return result;

            } else {
                logger.error("未找到对应的维表信息：" + dimensionTableName);
            }
        } catch (Exception e) {
            // TODO: handle exception
            logger.error("获取基线数据失败", e);
        }
        return new ArrayList<>();
    }

    public List<DataRow> getDataForDb(String index){
        Map<String,Object> map = new HashMap<>();
        map.put("name",index);
        ResultObjVO<List<DataSource>> data = dataSourceFegin.querySource(map);
        List<DataSource> dataSources = data.getData();
        if(CollectionUtils.isEmpty(dataSources)){
            return new ArrayList<>();
        }
        DataSource dataSource = dataSources.get(0);
        String tableName = dataSource.getTopicAlias();
        String baselineTime = getDbSunTime(tableName);
        if(StringUtils.isBlank(baselineTime)){
            return new ArrayList<>();
        }
        String sql = "select * from " + tableName + " where insert_time = '"+baselineTime+"';";
        List<Map<String,Object>> baselineData = jdbcTemplate.queryForList(sql);

        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("baselineIndex",index));
        List<DimensionTableInfo> dimensionTableInfos = dimensionTableService.findAll(conditions);
        if(CollectionUtils.isEmpty(dimensionTableInfos)){
            return new ArrayList<>();
        }
        String dimensionTableName = dimensionTableInfos.get(0).getName();
        List<DataRow> dataRows = getDataRows(dimensionTableName,baselineData);
        return dataRows;
    }

    private String getDbSunTime(String tableName){
        String sql = "select max('insert_time') as max_time from " + tableName+";";
        List<Map<String,Object>> baselineData = jdbcTemplate.queryForList(sql);
        if(CollectionUtils.isEmpty(baselineData)){
            return null;
        }
        return String.valueOf(baselineData.get(0).get("max_time"));
    }

    public List<DataRow> getEsData(String baselineIndex,String dimensionTableName){
        String[] indexArr = this.getIndexListByBaseIndexName(baselineIndex);
        if (indexArr == null || indexArr.length == 0) {
            logger.warn("未查找到任何索引：" + baselineIndex);
            return new ArrayList<>();
        }
        String maxCreateTime = getBaselineMaxCreateTime(indexArr);
        logger.info("maxCreateTime={}",maxCreateTime);
        if (StringUtils.isEmpty(maxCreateTime)) {
            logger.debug("未获取到最大时间");
            return new ArrayList<>();
        }

        // 得到最大时间
        List<QueryCondition_ES> cons = new ArrayList<>();
        cons.add(QueryCondition_ES.eq("insert_time", maxCreateTime));
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(cons);
        SortBuilder sortBuilder = null;

        SearchResponse searchResponse = elasticSearchRestClient.getDocs(indexArr, queryBuilder, sortBuilder,
                null, 0, 10000);
        SearchHit[] searchHits = searchResponse.getHits().getHits();
        List<Map<String,Object>> data = new ArrayList<>();
        for(SearchHit hit : searchHits){
            data.add(hit.getSourceAsMap());
        }
        List<DataRow> result = getDataRows(dimensionTableName, data);
        return result;
    }

    @Override
    public List<DataRow> getDataRows(String dimensionTableName, List<Map<String,Object>> data) {
        List<DataRow> result = new ArrayList<>();
        VData<List<BasePersonZjg>> allBasePersonZjg = authFeign.getAllPerson();

        List<BasePersonZjg> allPerson = new ArrayList<>();
        if (allBasePersonZjg != null && "0".equals(allBasePersonZjg.getCode())) {
            allPerson = allBasePersonZjg.getData();
        } else {
            logger.error("getAllBasePersonZjg出现异常");
        }

        List<AppSysManager> appList =appSysManagerService.findAll();
        List<Asset> assetList =assetService.findAll();

        List<Map<String, Object>> mapType0 = new ArrayList<>();//存储没有填写用户名称的数据
        Map<String, String> usernameMap = new HashMap<>();//存储所es查询到的用户名称
        Map<String,String> appMap = new HashMap<>();
        Map<String,String> assetMap = new HashMap<>();
        for (Map<String,Object> sourceAsMap : data) {
            if(sourceAsMap.containsKey("type") && 1==Integer.valueOf(String.valueOf(sourceAsMap.get("type")))){
                // 个人基线
                if(sourceAsMap.containsKey("username") ){
                    usernameMap.put(sourceAsMap.get("username").toString(), sourceAsMap.get("username").toString());
                }else if(sourceAsMap.containsKey("app_id")){
                    appMap.put(sourceAsMap.get("app_id").toString(), sourceAsMap.get("app_id").toString());
                }else if(sourceAsMap.containsKey("dev_ip")){
                    assetMap.put(sourceAsMap.get("dev_ip").toString(), sourceAsMap.get("dev_ip").toString());
                }
                DataRow row = new DataRow();
                List<NameValueBean> cells = new ArrayList<>();
                for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
                    if (entry.getValue() != null) {
                        cells.add(new NameValueBean(entry.getValue(), entry.getKey()));
                    } else {
                        cells.add(new NameValueBean("", entry.getKey()));
                    }
                }
                row.setRow(cells);
                result.add(row);

            }else if(sourceAsMap.containsKey("type")&& 0==Integer.valueOf(String.valueOf(sourceAsMap.get("type")))){
                // 群体基线
                mapType0.add(sourceAsMap);
            }else{
                DataRow row = new DataRow();
                List<NameValueBean> cells = new ArrayList<>();
                for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
                    if (entry.getValue() != null) {
                        cells.add(new NameValueBean(entry.getValue(), entry.getKey()));
                    } else {
                        cells.add(new NameValueBean("", entry.getKey()));
                    }
                }
                row.setRow(cells);
                result.add(row);
            }
        }

        String sql = "select COLUMN_NAME,column_comment  from information_schema.COLUMNS where table_name = '"+ dimensionTableName +"'";
        List<Map<String,Object>> clomnLists =jdbcTemplate.queryForList(sql);
        List<String> columns = new ArrayList<>();
        clomnLists.stream().forEach(item->{
            columns.add(String.valueOf(item.get("COLUMN_NAME")));
        });

        for (Map<String, Object> sourceAsMap : mapType0) {
            if(columns.contains("username")){
                for (BasePersonZjg person : allPerson) {
                    if (usernameMap.containsKey(person.getUserNo()))//如果已经有 这个人 则不添加数据
                    {
                        continue;
                    }

                    DataRow row = new DataRow();
                    List<NameValueBean> cells = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
                        if (entry.getValue() != null) {
                            cells.add(new NameValueBean(entry.getValue(), entry.getKey()));
                        } else {
                            cells.add(new NameValueBean("", entry.getKey()));
                        }
                    }
                    cells.add(new NameValueBean(person.getUserNo(), "username"));
                    row.setRow(cells);
                    result.add(row);
                }
            }else if(columns.contains("app_id")){
                for (AppSysManager person : appList) {
                    if (appMap.containsKey(person.getAppNo()))//如果已经有 这个人 则不添加数据
                    {
                        continue;
                    }
                    DataRow row = new DataRow();
                    List<NameValueBean> cells = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
                        if (entry.getValue() != null) {
                            cells.add(new NameValueBean(entry.getValue(), entry.getKey()));
                        } else {
                            cells.add(new NameValueBean("", entry.getKey()));
                        }
                    }
                    cells.add(new NameValueBean(person.getAppNo(), "app_id"));
                    row.setRow(cells);
                    result.add(row);
                }
            }else if(columns.contains("dev_ip")){
                for (Asset asset : assetList) {
                    if (assetMap.containsKey(asset.getIp()))//如果已经有 这个人 则不添加数据
                    {
                        continue;
                    }
                    DataRow row = new DataRow();
                    List<NameValueBean> cells = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : sourceAsMap.entrySet()) {
                        if (entry.getValue() != null) {
                            cells.add(new NameValueBean(entry.getValue(), entry.getKey()));
                        } else {
                            cells.add(new NameValueBean("", entry.getKey()));
                        }
                    }
                    cells.add(new NameValueBean(asset.getIp(), "dev_ip"));
                    row.setRow(cells);
                    result.add(row);
                }
            }
        }
        return result;
    }

    private String getBaselineMaxCreateTime(String[] indexArr) {
        try {
            IndexsInfoVO indexsInfoVO = new IndexsInfoVO();
            indexsInfoVO.setIndex(indexArr);
            // List<Map<String,Object>> list = elasticSearchMapManage.findAll(indexsInfoVO,null);
            List<Map<String,Object>> list = elasticSearchMapManage.queryStatistics(indexArr[0],null,new SearchField("insert_time",FieldType.String,null));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            if(CollectionUtils.isNotEmpty(list)){
                Collections.sort(list, new Comparator<Map<String, Object>>() {
                    @SneakyThrows
                    @Override
                    public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                        Object endTime1 = o1.get("insert_time");
                        Object endTime2 = o2.get("insert_time");
                        Date date1 = sdf.parse(String.valueOf(endTime1).replace("T"," ").replace(".000Z",""));
                        Date date2 = sdf.parse(String.valueOf(endTime2).replace("T"," ").replace(".000Z",""));
                        return date2.compareTo(date1);
                    }
                });
                return String.valueOf(list.get(0).get("insert_time")).replace("T"," ").replace(".000Z","");
            }
        } catch (Exception e) {
            logger.error("获取索引中最大数据时间失败", e);
        }
        return "";
    }

    /**
     * 根据基本的索引名称获得对应一系列索引集合
     *
     * @param baseIndexName
     * @return
     */
    private String[] getIndexListByBaseIndexName(String baseIndexName) {
        String[] allIndex = elasticSearchRestClient.getAllIndex();
        List<String> list = new ArrayList<>();
        for (String index : allIndex) { //判断每个index,是否包括了baseIndexName
            if (index.contains(baseIndexName) || index.contains(baseIndexName.toLowerCase())) {
                list.add(index);
            }
        }
        String[] array = new String[list.size()];
        String[] indexArray = list.toArray(array);
        return indexArray;
    }

    /**
     * @return isStarted (0/1)  eventRuleId  eventRuleParentId
     */
    @Override
    public List<Map<String, Object>> getEventRuleStartedStatistics() {
        return alarmAnalysisDao.getEventRuleStartedStatistics();
    }

}
