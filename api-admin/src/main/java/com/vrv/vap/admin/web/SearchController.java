package com.vrv.vap.admin.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.util.FileFilterUtil;
import com.vrv.vap.admin.vo.*;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.common.constant.ErrorCode;
import com.vrv.vap.admin.common.constant.PageConstants;
import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.common.manager.ESClient;
import com.vrv.vap.admin.common.util.ES7Tools;
import com.vrv.vap.admin.common.util.JsonQueryTools;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.common.properties.DisplayResultConfig;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.client.Request;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 前台搜索功能控制器
 *
 * @author lilang
 * @date 2018年2月6日
 */
@RestController
@RequestMapping(path = "/search")
public class SearchController extends ApiController {

    @Autowired
    DictionaryService dictionaryService;

    @Resource
    LinkTypeList linkTypeList;

    @Resource
    IndexFieldTeamList indexFieldTeamList;

    @Resource
    DisplayResultConfig displayResultConfig;

    private static Logger log = LoggerFactory.getLogger(SearchController.class);

    @Autowired
    SearchService searchService;

    @Autowired
    IndexService indexService;

    @Autowired
    ConditionService conditionService;

    @Autowired
    DiscoverRecordService discoverRecordService;

    @Autowired
    IndexTopicService indexTopicService;

    @Value("${index.netflow:netflow-*}")
    private String indexNetflow;

    /**
     * 查询默认索引
     *
     * @param
     * @return
     */
    @SuppressWarnings("rawtypes")
    @GetMapping("/index/default")
    @ApiOperation("查询默认索引")
    @SysRequestLog(description = "查询默认索引",actionType = ActionType.SELECT)
    public VData queryDefaultIndexName() {
        IndexQuery param = new IndexQuery();
        param.setDefaultindex(PageConstants.IS_OK);
        Example example = this.pageQuery(param, DiscoverIndex.class);
        List<DiscoverIndex> discoverIndexList = indexService.findByExample(example);
        if (CollectionUtils.isNotEmpty(discoverIndexList)) {
            return this.vData(discoverIndexList.get(0));
        }
        return this.vData(false);
    }

    /**
     * 查询索引列表
     *
     * @return
     */
    @ApiOperation("查询索引列表")
    @SuppressWarnings("rawtypes")
    @GetMapping(path = "/index")
    @SysRequestLog(description = "查询索引列表",actionType = ActionType.SELECT)
    public VList queryIndexList() {
        IndexQuery param = new IndexQuery();
        param.setCount_(PageConstants.NO_PAGE_COUNT);
        param.setOrder_("indexid");
        param.setBy_("asc");
        Example example = this.pageQuery(param, DiscoverIndex.class);
        return this.vList(indexService.findByExample(example));
    }

    /**
     * 查询索引字段
     *
     * @param param
     * @return
     */
    @ApiOperation("查询索引字段")
    @SuppressWarnings("rawtypes")
    @PostMapping(path = "/index/field")
    @SysRequestLog(description = "查询索引字段",actionType = ActionType.SELECT)
    public VData queryIndexFieldById(@RequestBody IndexQuery param) {
        Example example = this.pageQuery(param, DiscoverIndex.class);
        List<DiscoverIndex> indexList = indexService.findByExample(example);
        if (!CollectionUtils.isEmpty(indexList)) {
            return this.vData(indexList.get(0));
        }
        return this.vData(false);
    }


    /**
     * 导出es文档
     *
     * @param
     * @return
     */
    @ApiOperation("导出es文档")
    @PostMapping(path = "export")
    @SysRequestLog(description = "导出es搜索文档",actionType = ActionType.SELECT)
    public VData export(@RequestBody EsSearchQuery esSearchQuery) {
        Export.Progress progress = this.searchService.export(esSearchQuery);
        return this.vData(progress);
    }

    /**
     * 导出es数据
     *
     * @param
     * @return
     */
    @ApiOperation("导出es数据")
    @PostMapping(path = "/export/csv")
    @SysRequestLog(description = "导出es数据",actionType = ActionType.SELECT)
    public VData exportCSV(@RequestBody EsSearchQuery esSearchQuery) {
        return this.vData(this.searchService.exportCSV(esSearchQuery));
    }

    @ApiOperation("导入es数据")
    @PostMapping(path = "/import")
    @SysRequestLog(description = "导入es数据",actionType = ActionType.SELECT)
    public VData importData(@RequestParam("file") MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        // 扫描备注：已做文件格式白名单校验
        if (!FileFilterUtil.validFileType(fileType)) {
            return this.vData(false);
        }
        this.searchService.importData(file);
        return this.vData(true);
    }

    @ApiOperation("下载文件")
    @GetMapping(path = "/download/{fileName}")
    @SysRequestLog(description = "下载文件",actionType = ActionType.SELECT)
    public void downloadFile(@PathVariable("fileName") String filePath, HttpServletResponse response) {
        this.searchService.downloadFile(filePath, response);
    }

    @ApiOperation("生成查询语句")
    @PostMapping(path = "/condition/generate")
    public VData generateQueryCondition(@RequestBody ConditionGenerateQuery query) {
        Map<String, Object> result = searchService.generateQueryCondition(query);
        return this.vData(result);
    }

    @ApiOperation("获取链接类型配置")
    @GetMapping(path = "/linkType/config")
    @SysRequestLog(description = "获取链接类型配置",actionType = ActionType.SELECT)
    public VData getLinkTypeConfig() {
        return this.vData(linkTypeList.getList());
    }

    @ApiOperation("获取字段分组")
    @GetMapping(path = "/index/field/team")
    @SysRequestLog(description = "获取字段分组",actionType = ActionType.SELECT)
    public VData getIndexFieldTeam() {
        return this.vData(indexFieldTeamList.getList());
    }

    @ApiOperation("获取展示按钮顺序及冻结列配置")
    @GetMapping(path = "/display/config")
    public VData getDisplayResultConfig() {
        return this.vData(displayResultConfig);
    }

    /**
     * 根据时间查询索引列表
     *
     * @param
     * @return
     */
    @ApiOperation("根据时间查询索引列表")
    @PostMapping(path = "/index")
    @SysRequestLog(description = "根据时间查询索引列表",actionType = ActionType.SELECT)
    public VData queryIndexListByTime(@RequestBody final EsSearchQuery esSearchQuery) {
        final String index = esSearchQuery.getIndex();
        final String startTime = esSearchQuery.getStartTime();
        final String endTime = esSearchQuery.getEndTime();
        return this.vData(searchService.queryIndexListByTime(index, startTime, endTime));
    }


    /**
     * @param queryModel
     * @return
     */
    @ApiOperation("根据sql执行")
    @SuppressWarnings({"rawtypes", "unchecked"})
    @PostMapping(path = "/sql/execute")
    @SysRequestLog(description = "查询SQL",actionType = ActionType.SELECT)
    public Result sqlExec(@RequestBody QueryModel queryModel) {
        if(StringUtils.isEmpty(queryModel.getQuery())){
            return this.result(ErrorCode.QSL_NULL);
        }
        Map<String,Object> responseMap = ES7Tools.nlpSqlExec(queryModel.getQuery());
        if(responseMap==null|| responseMap.containsKey("__error")){
            return new Result("-1", "语法错误");
        }
//        if(responseMap.containsKey("__error")){
//            return new Result("-1", responseMap.get("__error").toString());
//        }
        return this.vData(responseMap);
    }
    /**
     * 查询全部内容
     *
     * @param
     * @return
     * @throws
     */
    @ApiOperation("查询全部内容")
    @PostMapping(path = "/content")
    @SysRequestLog(description = "查询全部内容",actionType = ActionType.SELECT)
    public VData searchGlobalContent(@RequestBody EsSearchQuery esSearchQuery) {
        String result = searchService.searchGlobalContent(esSearchQuery);
        return this.vData(result);
    }

    /**
     * 查询下级主题数据
     *
     * @param
     * @return
     * @throws
     */
    @ApiOperation("查询下级主题数据")
    @PostMapping(path = "/topic/sub")
    public VData searchTopicCount(@RequestBody EsSearchQuery esSearchQuery) {
        List<Map<String,Object>> resultList = searchService.searchTopicCount(esSearchQuery);
        return this.vData(resultList);
    }

    /**
     * 查询全部内容，返回ESResult
     * @param queryModel
     * @return
     */
    @ApiOperation("查询全部内容")
    @SuppressWarnings({"rawtypes", "unchecked"})
    @PostMapping(path = "/content/search")
    @SysRequestLog(description = "查询全部内容",actionType = ActionType.SELECT)
    public EsResult searchContent(@RequestBody QueryModel queryModel) {
        ES7Tools.QueryWrapper wrapper = ES7Tools.build();
        EsResult esResult = wrapper.wrapResult(wrapper.getSearchResponse(buildQueryModel(wrapper, queryModel)),
                queryModel);
        return esResult;
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
        if (StringUtils.isNotEmpty(queryModel.getOrderType()) && "asc".equals(queryModel.getOrderType().toLowerCase(Locale.ENGLISH))) {
            queryModel.setSortOrder(SortOrder.ASC);
        } else {
            if (queryModel.getSortOrder() == null) {
                queryModel.setSortOrder(SortOrder.DESC);
            }
        }
        return queryModel;
    }

    /**
     * 查询指定字段内容列表
     *
     * @param
     * @return
     */
    @ApiOperation("查询指定字段内容列表")
    @PostMapping(path = "/field/query")
    @SysRequestLog(description = "查询指定字段内容列表",actionType = ActionType.SELECT)
    public Result searchFiledList(@RequestBody EsSearchQuery  esSearchQuery) {
        List<Map<String,Object>> resultList = searchService.searchFiledList(esSearchQuery);
        return this.vData(resultList);
    }


    /**
     * 保存查询条件
     *
     * @param condition
     * @return
     */
    @ApiOperation("保存查询条件")
    @PutMapping(path = "/condition")
    @SysRequestLog(description = "保存查询条件",actionType = ActionType.ADD)
    public Result saveCondition(@RequestBody Condition condition) {
        condition.setSearchCount(1);
        condition.setSearchTime(TimeTools.format2(new Date()));
        int result = conditionService.save(condition);
        return this.result(result == 1);
    }

    @ApiOperation("点击查询条件")
    @PostMapping(path = "/condition/click")
    @SysRequestLog(description = "点击查询条件",actionType = ActionType.SELECT)
    public Result clickCondition(@RequestBody Condition param) {
        Condition condition = conditionService.findById(param.getId());
        if (condition != null) {
            Integer searchCount = condition.getSearchCount() != null ? condition.getSearchCount() + 1 : 1;
            condition.setSearchCount(searchCount);
            condition.setSearchTime(TimeTools.format2(new Date()));
            int result = conditionService.updateSelective(condition);
            return this.result(result == 1);
        }
        return this.result(false);
    }

    /**
     * 查询条件列表
     *
     * @return
     */
    @ApiOperation("查询条件列表")
    @SuppressWarnings("rawtypes")
    @PostMapping(path = "/condition")
    @SysRequestLog(description = "查询条件列表",actionType = ActionType.SELECT)
    public VList queryConditionList(@RequestBody ConditionQuery param) {
        param.setCount_(PageConstants.NO_PAGE_COUNT);
        param.setOrder_("id");
        param.setBy_("desc");
        Example example = this.pageQuery(param, Condition.class);
        List<Condition> conditionList = conditionService.findByExample(example);
        return this.vList(conditionList);
    }

    @ApiOperation("删除查询条件")
    @DeleteMapping(path = "/condition")
    @SysRequestLog(description = "删除查询条件",actionType = ActionType.DELETE)
    public Result deleteCondition(@RequestBody DeleteQuery param) {
        int result = conditionService.deleteByIds(param.getIds());
        return this.vData(result == 1);
    }

    /**
     * 记录查询日志
     *
     * @param
     * @return
     * @throws
     */
    @ApiOperation("记录查询日志")
    @SuppressWarnings({"rawtypes", "unchecked"})
    @PutMapping(path = "/record")
    @SysRequestLog(description = "记录查询日志",actionType = ActionType.ADD)
    public Result saveRecord( @RequestBody DiscoverRecord record) {
        boolean result = discoverRecordService.saveRecord(record);
        return this.result(result);
    }

    /**
     * 查询日志
     *
     * @param param
     * @return
     * @throws
     */
    @ApiOperation("查询日志列表")
    @SuppressWarnings({"rawtypes", "unchecked"})
    @PostMapping(path = "/record")
    @SysRequestLog(description = "查询日志列表",actionType = ActionType.SELECT)
    public VList qeuryRecord(HttpServletRequest request, @RequestBody RecordQuery param) {
        param.setOrder_("searchTime");
        param.setBy_("desc");
        if (param.getCount_() == 0) {
            param.setCount_(10);
        }

        Example example = this.pageQuery(param, DiscoverRecord.class);
        HttpSession session = request.getSession();
        Example.Criteria criteria = example.createCriteria();
        if (session.getAttribute(Global.SESSION.USER) != null) {
            User userinfo = (User) session.getAttribute(Global.SESSION.USER);
            if (userinfo != null) {
                criteria.andEqualTo("account", userinfo.getAccount());
            }
        }
        return this.vList(discoverRecordService.findByExample(example));

    }

    /**
     * 获取当天日志总量
     * @return
     */
    @ApiOperation("获取当天日志总量")
    @GetMapping(path = "/total/count")
    @SysRequestLog(description = "获取当天日志总量",actionType = ActionType.SELECT)
    public VData queryTodayCount() {
        Map map = new HashMap();
        Long count = searchService.queryTodayCount();
        map.put("count",count);
        return this.vData(map);
    }


    /**
     * 获取区间段内日志总量
     * @param queryModel
     * @return
     */
    @ApiOperation("获取区间段内日志总量")
    @PostMapping(path = "/total")
    @SysRequestLog(description = "获取区间段内日志总量",actionType = ActionType.SELECT)
    public VData queryTotal(@RequestBody QueryModel queryModel) {
        Long count = searchService.queryTotal(queryModel);
        return this.vData(count);
    }


    @ApiOperation("获取区间段内日志趋势")
    @PostMapping(path = "/total/trend")
    @SysRequestLog(description = "获取区间段内日志趋势",actionType = ActionType.SELECT)
    public VData queryTotalTrend(@RequestBody QueryModel queryModel) {
        List<Map<String,Object>> resultList = searchService.queryTotalTrend(queryModel);
        return this.vData(resultList);
    }

    @ApiOperation("获取近24小时内日志总量")
    @PostMapping(path = "/daytime/total")
    @SysRequestLog(description = "获取区间段内日志趋势",actionType = ActionType.SELECT)
    public VData query24Total(@RequestBody QueryModel queryModel) {
        return this.vData(searchService.query24Total(queryModel));
    }

    @ApiOperation("获取近24小时内日志趋势")
    @PostMapping(path = "/day/trend")
    @SysRequestLog(description = "获取区间段内日志趋势",actionType = ActionType.SELECT)
    public VData queryDayTrend(@RequestBody QueryModel queryModel) {
        return this.vData(searchService.queryDayTrend(queryModel));
    }

    /**
     * 获取区间段内指定字段值总和
     * @param queryModel
     * @return
     */
    @ApiOperation("获取区间段内指定字段值总和")
    @PostMapping(path = "/sum")
    @SysRequestLog(description = "获取区间段内字段值总和",actionType = ActionType.SELECT)
    public VData querySum(@RequestBody QueryModel queryModel) {
        Long sum = searchService.querySum(queryModel);
        return this.vData(sum);
    }

    @ApiOperation("可信热力图")
    @PostMapping(path = "/heat")
    @SysRequestLog(description = "可信热力图",actionType = ActionType.SELECT)
    public VData queryHeat(@RequestBody HeatModel heatModel) {
        List<Map<String,Object>> result = searchService.queryHeat(heatModel);
        return this.vData(result);
    }

    /**
     * 删除内容
     * @param deleteIndexParamVo
     * @return Result
     */
    @ApiOperation("删除内容")
    @DeleteMapping(path = "/content")
    @SysRequestLog(description = "删除内容",actionType = ActionType.DELETE)
    public Result deleteContent(@RequestBody DeleteIndexParamVo deleteIndexParamVo) {
        //Map<String, Object> paramMap = JSON.parseObject(deleteParam);
        String index = deleteIndexParamVo.getIndex();
        String type = deleteIndexParamVo.getType();
        String id = deleteIndexParamVo.getId();
        if (StringUtils.isEmpty(index) || StringUtils.isEmpty(type) || StringUtils.isEmpty(id)) {
            log.error("删除失败,参数错误");
            return ErrorCode.PARAM_NULL.getResult();
        }
        log.info("删除内容 index:" + index + "  type:" + type + "  id:" + id);

        DeleteRequest deleteRequest = new DeleteRequest(index, type, id);
        try {
            ESClient.getInstance().delete(deleteRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            log.error("删除失败", e);
            return new Result("1", "删除失败");
        }
        return new Result("0", "删除成功");
    }

    /**
     * 流量统计
     * @return Result
     */
    @ApiOperation("流量统计")
    @GetMapping(path = "/netflow")
    @SysRequestLog(description = "流量统计",actionType = ActionType.SELECT)
    public Result netflowSearch() {
        List<Map<String, Object>> resList = new ArrayList<>();
        Map<String, Object> resultMap = new HashMap<>();
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd'T'00:00:00.000'Z'");
        SimpleDateFormat dateFormat2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SearchRequest searchRequest = new SearchRequest();
        //searchRequest.indices("abnormal-visit-*,app-audit-*");
        //searchRequest.types("logs");
        searchRequest.indices(indexNetflow);
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        sourceBuilder.query(QueryBuilders.rangeQuery("event_time")
                .from(dateFormat1.format(new Date())).to(dateFormat2.format(new Date())));
        //sourceBuilder.query(QueryBuilders.rangeQuery("event_time")
        //        .from("2021-02-20T11:44:09.382Z").to("2021-04-20T11:44:09.382Z"));
        searchRequest.source(sourceBuilder);
        try {
            SearchResponse searchResponse = ESClient.getInstance().search(searchRequest,RequestOptions.DEFAULT);
            long totalToday = searchResponse.getHits().getTotalHits().value;
            resultMap.put("totalToday", totalToday);
            RestClient restClient = ESClient.getInstance().getLowLevelClient();
            Response response = restClient.performRequest(new Request("GET",
                    "_cat/indices/" + indexNetflow + "?v&h=index,docs.count,store.size&bytes=kb&format=json&pretty"));
            ObjectMapper objectMapper = new ObjectMapper();
            List<Map<String, Object>> resultList = objectMapper.readValue(response.getEntity().getContent(), List.class);
            long total = 0;
            long storeSize = 0;
            for (Map<String, Object> map : resultList) {
                total += Long.parseLong(map.get("docs.count").toString());
                storeSize += Long.parseLong(map.get("store.size").toString());
            }
            resultMap.put("total", total);
            resultMap.put("storeSize", storeSize);
            resList.add(resultMap);
        } catch (Exception e) {
            log.error("查询失败", e);
            return this.vList(new ArrayList<>());
        }
        return this.vList(resList,resList.size());
    }
}