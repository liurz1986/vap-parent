package com.vrv.vap.data.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.data.constant.SOURCE_TYPE;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.*;
import com.vrv.vap.data.service.*;
import com.vrv.vap.data.component.ConvertElastic;
import com.vrv.vap.data.util.TimeTools;
import com.vrv.vap.data.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/discover")
@Api(value = "【探索】对象/关系管理", tags = "【探索】对象/关系管理")
public class DiscoverController extends ApiController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private DiscoverEntityService entityService;

    @Autowired
    private DiscoverEntityRelService relService;

    @Autowired
    private DiscoverEdgeService edgeService;

    @Autowired
    private DiscoverRecordService recordService;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private ContentService contentService;


    @Autowired
    private ConvertElastic convertElastic;


    private final int AGG_COUNT = 50;

    @ApiOperation(value = "实体-获取全部")
    @GetMapping(value = "/entity")
    public VData<List<DiscoverEntity>> getEntity() {
        return this.vData(entityService.findAll());
    }

    @ApiOperation(value = "实体-查询")
    @PostMapping(value = "/entity")
    public VList<DiscoverEntity> queryEntity(@RequestBody EntityQuery query) {
        if (StringUtils.isBlank(query.getOrder_()) || StringUtils.isBlank(query.getBy_())) {
            query.setOrder_("id");
            query.setBy_("desc");
        }
        Example example = this.pageQuery(query, DiscoverEntity.class);
        return this.vList(entityService.findByExample(example));
    }

    @ApiOperation(value = "实体-增加")
    @PutMapping(value = "/entity")
    public VData<DiscoverEntity> addEntity(@RequestBody DiscoverEntity entity) {
        int result = entityService.save(entity);
        if (result == 1) {
            return this.vData(entity);
        }
        return this.vData(false);
    }


    @ApiOperation(value = "实体-修改")
    @PatchMapping(value = "/entity")
    public Result updateEntity(@RequestBody DiscoverEntity entity) {
        int result = entityService.updateSelective(entity);
        return this.result(result == 1);
    }


    @ApiOperation(value = "实体-删除")
    @DeleteMapping(value = "/entity")
    public Result deleteEntity(@RequestBody DeleteQuery ids) {
        int result = entityService.deleteByIds(ids.getIds());
        return this.result(result > 0);
    }


    @ApiOperation(value = "关系-全部")
    @GetMapping(value = "/edge")
    public VData<List<DiscoverEdge>> getEdge() {
        return this.vData(edgeService.findAll());
    }

    @ApiOperation(value = "关系-查询")
    @PostMapping(value = "/edge")
    public VList<DiscoverEdge> queryEdge(@RequestBody EdgeQuery query) {
        if (StringUtils.isBlank(query.getOrder_()) || StringUtils.isBlank(query.getBy_())) {
            query.setOrder_("id");
            query.setBy_("desc");
        }
        Example example = this.pageQuery(query, DiscoverEdge.class);
        return this.vList(edgeService.findByExample(example));
    }

    @ApiOperation(value = "关系-增加")
    @PutMapping(value = "/edge")
    public VData<DiscoverEdge> addEdge(@RequestBody DiscoverEdge edge) {
        int result = edgeService.save(edge);
        if (result == 1) {
            return this.vData(edge);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "关系-修改")
    @PatchMapping(value = "/edge")
    public Result updateEdge(@RequestBody DiscoverEdge edge) {
        int result = edgeService.updateSelective(edge);
        return this.result(result == 1);
    }

    @ApiOperation(value = "关系-删除")
    @DeleteMapping(value = "/edge")
    public Result deleteEdge(@RequestBody DeleteQuery ids) {
        int result = edgeService.deleteByIds(ids.getIds());
        return this.result(result > 0);
    }

    @ApiOperation(value = "预置实体数据源")
    @GetMapping(value = "/rel")
    public VData<List<DiscoverEntityRel>> getRel() {
        return this.vData(relService.findAll());
    }

    @ApiOperation(value = "实体索引-查询")
    @PostMapping(value = "/rel")
    public VList<DiscoverEntityRel> queryRel(@RequestBody EntityRelQuery query) {
        if (StringUtils.isBlank(query.getOrder_()) || StringUtils.isBlank(query.getBy_())) {
            query.setOrder_("id");
            query.setBy_("desc");
        }
        Example example = this.pageQuery(query, DiscoverEntityRel.class);
        return this.vList(relService.findByExample(example));
    }

    @ApiOperation(value = "实体索引-增加")
    @PutMapping(value = "/rel")
    public VData<DiscoverEntityRel> addRel(@RequestBody DiscoverEntityRel entityRel) {
        int result = relService.save(entityRel);
        if (result == 1) {
            return this.vData(entityRel);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "实体索引-修改")
    @PatchMapping(value = "/rel")
    public Result updateRel(@RequestBody DiscoverEntityRel entityRel) {
        int result = relService.updateSelective(entityRel);
        return this.result(result == 1);
    }

    @ApiOperation(value = "实体索引-删除")
    @DeleteMapping(value = "/rel")
    public Result deleteRel(@RequestBody DeleteQuery ids) {
        int result = relService.deleteByIds(ids.getIds());
        return this.result(result > 0);
    }


    @ApiOperation(value = "探索记录-获取")
    @GetMapping(value = "/record")
    public VData<List<DiscoverRecord>> getRecord() {
        return this.vData(recordService.findAll());
    }

//    @ApiOperation(value = "实体索引-查询")
//    @PostMapping(value = "/record")
//    public VList<DiscoverRecord> queryRecord(@RequestBody Query query) {
//        if(StringUtils.isBlank(query.getOrder_())||StringUtils.isBlank(query.getBy_())){
//            query.setOrder_("id");
//            query.setBy_("desc");
//        }
//        Example example = this.pageQuery(query, DiscoverEntityRel.class);
//        return this.vList(recordService.findByExample(example));
//    }

    @ApiOperation(value = "实体索引-增加")
    @PutMapping(value = "/record")
    public VData<DiscoverRecord> addRecord(HttpServletRequest request, @RequestBody DiscoverRecord record) {
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user != null) {
            record.setUserId(user.getId());
        }
        record.setCreateTime(new Date());
        int result = recordService.save(record);
        if (result == 1) {
            return this.vData(record);
        }
        return this.vData(false);
    }

//    @ApiOperation(value = "实体索引-修改")
//    @PatchMapping(value = "/record")
//    public Result updateRecord(@RequestBody DiscoverRecord record) {
//        int result = recordService.updateSelective(record);
//        return this.result(result == 1);
//    }

    @ApiOperation(value = "探索记录-删除")
    @DeleteMapping(value = "/record")
    public Result deleteRecord(@RequestBody DeleteQuery ids) {
        if ("ALL".equals(ids.getIds())) {
            List<DiscoverRecord> recordList = recordService.findAll();
            if (CollectionUtils.isNotEmpty(recordList)) {
                for (DiscoverRecord record : recordList) {
                    recordService.deleteById(record.getId());
                }
            }
            return this.result(true);
        }
        int result = recordService.deleteByIds(ids.getIds());
        return this.result(result > 0);
    }


    @ApiOperation(value = "探索-概览")
    @PostMapping(value = "/summary")
    public VData<Map<Integer, CommonResponse>> getSummary(@RequestBody DiscoverSummary query) throws ApiException {
        Map<Integer, CommonResponse> summary = new HashMap<>();
        Example example = new Example(DiscoverEdge.class);
        example.createCriteria().andEqualTo("searchEntityId", query.getEntityId());
        List<DiscoverEdge> edges = edgeService.findByExample(example);
        for (DiscoverEdge edge : edges) {
            Source source = sourceService.findById(edge.getSourceId());
            if (source == null) {
                logger.error("EDGE : " + edge.getId() + "  has no source defined!");
                continue;
            }
            if (source.getType() == SOURCE_TYPE.ELASTIC_BUILT) {
                summary.put(edge.getId(), this.summaryElastic(query, source, edge));
            } else if (source.getType() == SOURCE_TYPE.MYSQL_BUILT) {
                summary.put(edge.getId(), this.summarySql(query, source, edge));
            } else {
                logger.error("EDGE : " + edge.getId() + "  has no source defined!");
                continue;
            }
        }
        return this.vData(summary);
    }

    private String ELASTIC_QUERY_CONDITION = "{\"bool\":{\"must\":[{\"range\":{\"%s\":{\"gte\":%d,\"lte\":%d,\"format\":\"epoch_millis\"}}},{\"term\":{\"%s\":\"%s\"}}]}}";

    // 概览 - Elastic Search
    private CommonResponse summaryElastic(DiscoverSummary query, Source source, DiscoverEdge edge) throws ApiException {
        CommonResponse response = new CommonResponse();
        List<String> indexes = new ArrayList<>();
        if (source.getName().endsWith("-*")) {
            indexes.add(source.getName().substring(0, source.getName().length() - 2));
        } else if (source.getName().endsWith("*")) {
            indexes.add(source.getName().substring(0, source.getName().length() - 1));
        } else {
            indexes.add(source.getName());
        }
        LinkedHashSet<String> segments = contentService.querySegmentsByTime(indexes, query.getStartTime(), query.getEndTime());
        response.setTotal(0);
        response.setTotalAcc(0);
        if (segments.size() == 0) {
            return response;
        }
        String queryCondition = String.format(ELASTIC_QUERY_CONDITION, source.getTimeField(), query.getStartTime().getTime(), query.getEndTime().getTime(), edge.getSearchField(), query.getValue());
        response.setSegment(segments);
        response.setQuery(queryCondition);
        response = contentService.elasticTotal(response);
        if (response.getTotal() == 0) {
            response.setSegment(SYSTEM.EMPTY_SET);
            return response;
        }
        String targetField = edge.getGoalField();
        if (edge.getAgg() && !StringUtils.isBlank(targetField)) {
            response.setAggs(convertElastic.aggTerm(targetField, AGG_COUNT));
        }
        return response;
    }

    // 概览  SQL
    private CommonResponse summarySql(DiscoverSummary query, Source source, DiscoverEdge edge) {
        CommonResponse response = new CommonResponse();
        LinkedHashSet<String> segments = new LinkedHashSet<>();
        segments.add(source.getName());
        response.setSegment(segments);
        WhereGroup where = new WhereGroup("AND");
        where.add(new WhereItem(">", source.getTimeField(), TimeTools.format(query.getStartTime())));
        where.add(new WhereItem("<", source.getTimeField(), TimeTools.format(query.getEndTime())));
        where.add(new WhereItem("=", edge.getSearchField(), query.getValue()));
        int total = contentService.sqlCount(source.getName(), where);
        response.setTotalAcc(total);
        response.setTotal(total);
        if (total == 0) {
            return response;
        }
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            response.setQuery(mapper.writeValueAsString(where));
        } catch (JsonProcessingException e) {
//            e.printStackTrace();
        }
        String targetField = edge.getGoalField();
        if (edge.getAgg() && !StringUtils.isBlank(targetField)) {
            SqlGroup group = new SqlGroup();
            group.setOrder_(targetField);
            group.setBy_(null);
            group.setField(new SqlGroupField(targetField, "count", "doc_count", false));
            group.setTable(source.getName());
            try {
                response.setAggs(mapper.writeValueAsString(group));
            } catch (JsonProcessingException e) {
            }
        }
        return response;
    }

}
