package com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.AlarmEventAttribute;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementAggService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.alarm.AlarmDealAggregationRow;
import com.vrv.vap.alarmdeal.business.analysis.vo.EventDetailQueryVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.util.ExcelUtil;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.enums.FieldType;
import com.vrv.vap.es.util.ElasticSearchUtil;
import com.vrv.vap.es.util.page.PageRes_ES;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.es.vo.SearchField;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.PageRes;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.util.IOUtils;
import org.elasticsearch.ElasticsearchStatusException;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Cardinality;
import org.elasticsearch.search.aggregations.metrics.NumericMetricsAggregation;
import org.elasticsearch.search.aggregations.pipeline.BucketSortPipelineAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月24日 14:14
 */
@Service
public class AlarmEventManagementAggServiceImpl implements AlarmEventManagementAggService {
    // 日志
    private final Logger logger = LoggerFactory.getLogger(AlarmEventManagementAggServiceImpl.class);

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    AlarmEventManagementForESService alarmEventManagementForEsService;

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    // 导出文件表头
    private static final String HEADER_STR = "事件类型,事件名称,发生时间,合并次数,事件等级,事件标签,处置状态";

    // json 转换格式化
    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();

    // 基础
    public String getBaseField() {
        return "";
    }

    /**
     * 获得告警事件(聚合)列表
     * @param query
     * @return
     */
    @Override
    public PageRes<AlarmDealAggregationRow> getAlarmDealAggetStatisticsCountgregationPager(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys = getQueryConditionEs(query);
        // 事件类型 事件名称 事件dstip //srcIp
        QueryBuilder queryBuilder = ElasticSearchUtil.toQueryBuilder(querys);
        SearchResponse searchResponse = null;
        int limit = query.getCount_();
        int offset = query.getStart_();
        int size = offset + limit;

        AggregationBuilder minAlarmRiskLevel = AggregationBuilders.min("min_alarmRiskLevel")
                .field("alarmRiskLevel");
        AggregationBuilder minAlarmDealState = AggregationBuilders.min("min_alarmDealState")
                .field("alarmDealState");
        AggregationBuilder maxEventCreattime = AggregationBuilders.max("max_eventCreattime")
                .field("eventCreattime");

        // 传入一下新的聚合函数进行聚合,使用bucket_sort实现
        TermsAggregationBuilder groupByFieldAgg = AggregationBuilders.terms("group_by_field").field("aggField")
                .size(size);
        groupByFieldAgg.shardSize(1000000);
        groupByFieldAgg.subAggregation(minAlarmRiskLevel);
        groupByFieldAgg.subAggregation(minAlarmDealState);
        groupByFieldAgg.subAggregation(maxEventCreattime);
        // 定义分页条件
        BucketSortPipelineAggregationBuilder bucketSort = new BucketSortPipelineAggregationBuilder("bucket_sort", null)
                .from(offset).size(limit);
        // 添加分页内容
        groupByFieldAgg.subAggregation(bucketSort);
        // 指定agg
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder().size(0);

        searchSourceBuilder.aggregation(groupByFieldAgg);
        searchSourceBuilder.aggregation(AggregationBuilders.cardinality("distinct_by_field").field("aggField"));
        searchSourceBuilder.query(queryBuilder);
        searchSourceBuilder.trackTotalHits(true);
        logger.info(searchSourceBuilder.toString());

        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices(alarmEventManagementForEsService.getIndexName());
        searchRequest.source(searchSourceBuilder);

        RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(500000).setSocketTimeout(1000000).build();
        RequestOptions options = RequestOptions.DEFAULT.toBuilder().setRequestConfig(requestConfig).build();

        try {
            searchResponse = restHighLevelClient.search(searchRequest, options);
            Terms aggregation = searchResponse.getAggregations().get("group_by_field");
            List<AlarmDealAggregationRow> rows = new ArrayList<>();

            List<? extends MultiBucketsAggregation.Bucket> buckets = ((MultiBucketsAggregation) aggregation)
                    .getBuckets();
            for (MultiBucketsAggregation.Bucket bucket : buckets) {
                AlarmDealAggregationRow row = new AlarmDealAggregationRow();
                String keyAsString = bucket.getKeyAsString();
                if (!StringUtils.isEmpty(keyAsString)) {
                    String[] values = keyAsString.split("@#@#@");
                    if (values.length == 4) {
                        row.setEventType(Integer.parseInt(values[0]));
                        row.setEventName(values[1]);
                        row.setDstIp(values[2]);
                        row.setSrcIp(values[3]);
                        Long count = bucket.getDocCount();
                        row.setMergeCount(count.intValue());
                        int alarmRiskLevel = 1;
                        Aggregation childtermsLevel = bucket.getAggregations().get("min_alarmRiskLevel");
                        String level = ((org.elasticsearch.search.aggregations.metrics.ParsedMin) childtermsLevel)
                                .getValueAsString();
                        if (!StringUtils.isEmpty(level) && !"Infinity".equals(level)) {
                            Float parseFloat = Float.parseFloat(level);
                            alarmRiskLevel = parseFloat.intValue();
                        }
                        row.setAlarmRiskLevel(alarmRiskLevel);
                        int alarmDealState = 0;
                        Aggregation childtermsState = bucket.getAggregations().get("min_alarmDealState");
                        String state = ((NumericMetricsAggregation.SingleValue) childtermsState).getValueAsString();
                        if (!StringUtils.isEmpty(state) && !"Infinity".equals(state)) {
                            Float parseFloat = Float.parseFloat(state);
                            alarmDealState = parseFloat.intValue();
                        }
                        Aggregation childCreattime = bucket.getAggregations().get("max_eventCreattime");
                        String createTime = ((NumericMetricsAggregation.SingleValue) childCreattime).getValueAsString();
                        row.setEventCreatetime(DateUtils.parseDate(createTime, "yyyy-MM-dd HH:mm:ss"));
                        row.setAlarmDealState(alarmDealState);
                        rows.add(row);
                    }
                }

            }
            // 获取出查询结果
            long groupTotal = ((Cardinality) searchResponse.getAggregations().get("distinct_by_field")).getValue();

            PageRes<AlarmDealAggregationRow> result = new PageRes<AlarmDealAggregationRow>();

            result.setCode("0");
            result.setList(rows);
            result.setTotal(groupTotal);
            result.setMessage("查询成功");
            return result;
        } catch (ElasticsearchStatusException ese) {
            RestStatus status = ese.status();
            if (status.getStatus() == 404) {
                PageRes<AlarmDealAggregationRow> aggregationPage = new PageRes<>();
                aggregationPage.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
                aggregationPage.setTotal(0L);
                aggregationPage.setMessage(ResultCodeEnum.SUCCESS.getMsg());
                return aggregationPage;
            }
            throw ese;
        }catch (Exception e) {
            logger.error("getDocs异常错误：{}", e);
            PageRes<AlarmDealAggregationRow> aggregationPage = new PageRes<>();
            aggregationPage.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode().toString());
            aggregationPage.setTotal(0L);
            aggregationPage.setMessage(ResultCodeEnum.UNKNOW_FAILED.getMsg());
            return aggregationPage;
        }
    }

    /**
     * 处理查询条件
     * @param query
     * @return
     */
    private List<QueryCondition_ES> getQueryConditionEs(EventDetailQueryVO query) {
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getQuerys(query);
        if("8888888".equals(query.getOrgTreeCode())){
            querys.add(QueryCondition_ES.isNull("unitList.unitDepartSubCode"));
        }
        querys.addAll(alarmEventManagementForEsService.getDataPermissions());
        return querys;
    }

    /**
     * 统计数量
     * @param query
     * @return
     */
    @Override
    public List<NameValue> getStatisticsCount(EventDetailQueryVO query) {
        List<NameValue> result = new ArrayList<>();
        List<QueryCondition_ES> querys = getQueryConditionEs(query);
        long total = alarmEventManagementForEsService.count(querys);
        result.add(new NameValue(Long.toString(total), "total"));

        SearchField searchField = new SearchField(getBaseField() + "eventName", FieldType.ObjectDistinctCount, 0, 200,
                null);
        List<Map<String, Object>> queryStatistics = alarmEventManagementForEsService.queryStatistics(querys, searchField);
        int distinctCount = 0;
        if (!queryStatistics.isEmpty()) {
            String docCount = queryStatistics.get(0).get("doc_count").toString();
            Float fcount = Float.parseFloat(docCount);
            distinctCount = fcount.intValue();
        }
        result.add(new NameValue(Integer.toString(distinctCount), "eventNameDistinctCount"));
        return result;
    }

    /**
     * 生成导出文件
     * @param query
     * @param request
     * @return java.lang.String
     */
    @Override
    public String createReportFile(EventDetailQueryVO query, HttpServletRequest request) {
        List<AlarmDealAggregationRow> aggDataRows = alarmEventManagementForEsService.getAggDataRows(query);
        if(aggDataRows!=null)
        {
            String token= UUIDUtils.get32UUID();
            String filePath = getFilePath(token);
            OutputStream out = null;
            HSSFWorkbook workbook = null;
            try {
                out = new FileOutputStream(filePath);
                List<List<Object>> result=new ArrayList<>();
                List<String> headers = new ArrayList<String>(Arrays.asList(HEADER_STR.split(",")));
                String[] alarmRiskLevels=new String[]{"","低风险","一般风险","中风险","高风险","紧急风险"};
                String[] alarmDealStates=new String[]{"未处置","处置中","已挂起","已处置"};
                String[] alarmEventTypes=new String[]{"","配置合规性事件","网络安全异常事件","用户行为异常事件","运维行为异常事件","应用服务异常事件","跨单位互联异常事件"};
                aggDataRows.forEach(event -> {
                    List<Object> row = new ArrayList<>();
                    row.add(alarmEventTypes[event.getEventType()]);
                    row.add(event.getEventName());
                    row.add(DateUtil.format(event.getEventCreatetime(), "yyyy-MM-dd HH:mm:ss"));
                    row.add(event.getMergeCount());
                    row.add(alarmRiskLevels[event.getAlarmRiskLevel() == null ? 1 : event.getAlarmRiskLevel()]);
                    row.add(event.getLabels());
                    row.add(alarmDealStates[event.getAlarmDealState()]);
                    result.add(row);
                });
                // 生成Excel POI生成对象
                workbook = new HSSFWorkbook();
                ExcelUtil.exportExcel(workbook, 0, "事件详情", headers, result, out);
                workbook.write(out);
            }catch (Exception e) {
                logger.error("IOException:", e);
                throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "文件构造异常");
            }finally {
                IOUtils.closeQuietly(workbook);
                IOUtils.closeQuietly(out);
            }
            return token;
        }else{
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "数据查询异常");
        }
    }

    /**
     * 导出文件
     * @param fileName
     * @param request
     * @param response
     */
    @Override
    public void downloadReportFile(String fileName, HttpServletRequest request, HttpServletResponse response) {
        FileUtil.downLoadFile(fileName + ".xls", fileConfiguration.getFilePath(), response);
    }

    /**
     * 获取关注事件统计
     * @param query
     * @param top
     * @return
     */
    @Override
    public List<NameValue> getEventNameOfConcern(EventDetailQueryVO query, Integer top) {
        List<QueryCondition_ES> querys = getQueryConditionEs(query);
        List<String>  concerns=new ArrayList<>();
        concerns.add("事件名称");
        concerns.add("127.0.11.2");
        querys.add(QueryCondition_ES.in(getBaseField()+"eventName", concerns));
        List<NameValue> statisticsByStringField = alarmEventManagementForEsService.getStatisticsByStringField(querys,top,getBaseField()+"eventName");
        return statisticsByStringField;
    }

    /**
     * 获取所有事件名称（不判断数据权限）
     * @param query
     * @param top
     * @return
     */
    @Override
    public List<NameValue> getDistinctEventName(EventDetailQueryVO query, Integer top) {
        User currentUser = SessionUtil.getCurrentUser();
        if(currentUser==null)
        {
            throw new AlarmDealException(ResultCodeEnum.UNKNOW_FAILED.getCode(), "用户未登录");
        }
        List<QueryCondition_ES> querys = alarmEventManagementForEsService.getQuerys(query);

        if(top==0)
        {
            top=1000;
        }
        String groupByName=getBaseField()+"eventName";
        return alarmEventManagementForEsService.getStatisticsByStringField(querys,top,groupByName);
    }

    /**
     * 获得告警事件列表（不判断数据权限）
     * @param query
     * @return
     */
    @Override
    public PageRes_ES<AlarmEventAttribute> getAllAlarmDealPager(EventDetailQueryVO query) {
        User currentUser = SessionUtil.getCurrentUser();
        if(currentUser==null)
        {
            PageRes_ES<AlarmEventAttribute>  result=new PageRes_ES<>();
            result.setCode(ResultCodeEnum.UNKNOW_FAILED.getCode().toString());
            result.setMessage("用户未登录");
            result.setTotal(0L);

            return result;
        }
        PageRes_ES<AlarmEventAttribute> pageQueryResult = alarmEventManagementForEsService.getPageQueryResult(query, query,false);
        return pageQueryResult;
    }

    public String getFilePath(String token) {
        String fileName = token + ".xls";// 文件名称
        String filePath = Paths.get(fileConfiguration.getFilePath(), fileName).toString();
        return filePath;
    }
}
