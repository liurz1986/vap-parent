package com.vrv.vap.data.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.constant.SOURCE_TYPE;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.service.ContentService;
import com.vrv.vap.data.service.SourceFieldService;
import com.vrv.vap.data.service.SourceService;
import com.vrv.vap.data.vo.*;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/content")
@Api(value = "【内容引擎】数据获取/聚合/明细 查询&导出", tags = "【内容引擎】数据获取/聚合/明细查询")
public class ContentController extends ApiController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ContentService contentService;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private SourceFieldService sourceFieldService;

    @ApiOperation(value = "搜索生成器")
    @PostMapping(value = "/generate")
    @SysRequestLog(description = "查询搜索结果", actionType = ActionType.SELECT)
    public VData<CommonResponse> generate(@RequestBody CommonRequest query) throws ApiException {
        MODE searchMode = this.searchMode(query.getSource());
        if (searchMode == MODE.MYSQL) {
            return this.vData(contentService.generateSQL(query));
        }
        CommonResponse response = contentService.generateElastic(query);
        CommonResponse totalResponse = contentService.elasticTotal(response);
        return this.vData(totalResponse);
    }


    @ApiOperation(value = "搜索 ES 语法")
    @PostMapping(value = "/es/group", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String elasticGroup(@RequestBody ElasticParam param) {
        return contentService.elasticSearch(param);
    }


    @ApiOperation(value = "搜索 ES 语法")
    @PostMapping(value = "/es", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    @SysRequestLog(description = "查询搜索结果", actionType = ActionType.SELECT)
    public String elasticSearch(@RequestBody ElasticParam param) {
        SyslogSenderUtils.sendSelectSyslog();
        return contentService.elasticSearch(param);
    }


    @ApiOperation(value = "导出 ES 语法(直接写流)")
    @PostMapping(value = "/es/export", consumes = "multipart/form-data", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @ResponseBody
    @SysRequestLog(description = "导出搜索结果", actionType = ActionType.EXPORT)
    public void elasticSearch(HttpServletResponse response,
                              @ApiParam(value = "文件名称") @RequestParam("fileName") String fileName,
                              @ApiParam(value = "索引片段，以逗号区分", required = true) @RequestParam("index") String index,
                              @ApiParam(value = "查询体", required = true) @RequestParam("query") String query
    ) throws ApiException {
        SyslogSenderUtils.sendExportSyslog();
        if (StringUtils.isBlank(index)) {
            return;
        }
        ElasticParam param = new ElasticParam();
        param.setIndex(index.split(","));
        param.setQuery(query);
        if (fileName == null) {
            fileName = "查询数据-" + System.currentTimeMillis();
        }
        try {
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8") + ".xlsx";
            response.setHeader("content-disposition", "attachment; filename=" + fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");   //  xlsx
            this.contentService.esExport(response.getOutputStream(), param);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @ApiOperation(value = "导出 ES 语法(滚动查询)")
    @PostMapping(value = "/es/export/scroll")
    @ResponseBody
    public VData elasticSearchScroll(@RequestBody ElasticParam param) throws ApiException {
        if (param.getIndex() == null || param.getIndex().length == 0) {
            return this.vData(false);
        }
        return this.vData(this.contentService.esExport(param));
    }


    @ApiOperation(value = "导出 标准 SQL 语法(直接写流)")
    @PostMapping(value = "/sql/export", consumes = "multipart/form-data", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
    @ResponseBody
    @SysRequestLog(description = "导出搜索结果", actionType = ActionType.EXPORT)
    public void sqlExport(HttpServletResponse response,
                          @ApiParam(value = "文件名称") @RequestParam("fileName") String fileName,
                          @ApiParam(value = "数据源ID", required = true) @RequestParam("sourceId") Integer sourceId,
                          @ApiParam(value = "查询体", required = true) @RequestParam("query") String query
    ) throws ApiException {
        SyslogSenderUtils.sendExportSyslog();
        if (fileName == null) {
            fileName = "查询数据-" + System.currentTimeMillis();
        }
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            SqlQuery param = mapper.readValue(query, SqlQuery.class);
            fileName = java.net.URLEncoder.encode(fileName, "UTF-8") + ".xlsx";
            response.setHeader("content-disposition", "attachment; filename=" + fileName);
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");   //  xlsx

            this.contentService.sqlExport(response.getOutputStream(), sourceId, param);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "查询 标准 SQL 语法")
    @PostMapping(value = "/sql")
    public VList sqlList(@RequestBody SqlQuery param) throws ApiException {
        return contentService.sqlList(param);
    }

    @ApiOperation(value = "查询 标准 SQL 语法")
    @PostMapping(value = "/sql/query")
    public VData<List> sqlQuery(@RequestBody Map<String, String> param) throws ApiException {
        String sql = param.get("sql");
        return this.vData(contentService.execQuery(sql));
//        return contentService.sqlList(param);
    }

    @ApiOperation(value = "聚合 标准 SQL 语法")
    @PostMapping(value = "/sql/group")
    public VData sqlGroup(@RequestBody SqlGroup param) throws ApiException {
        return contentService.sqlGroup(param);
    }

    enum MODE {MYSQL, ELASTIC}

    // 根据传的数据源，判断是哪 种搜索模式
    private MODE searchMode(List<Integer> sourceIds) throws ApiException {
        int size = sourceIds.size();
        if (size == 0) {
            throw new ApiException(ErrorCode.NOT_FOUND_INDEX.getResult().getCode(),ErrorCode.NOT_FOUND_INDEX.getResult().getMessage());
        }
        if (size == 1) {
            Source source = sourceService.findById(sourceIds.get(0));
            if (source == null) {
                throw new ApiException(ErrorCode.NOT_FOUND_INDEX.getResult().getCode(),ErrorCode.NOT_FOUND_INDEX.getResult().getMessage());
            }
            if (source.getType() == SOURCE_TYPE.ELASTIC_BUILT) {
                return MODE.ELASTIC;
            }
            if (source.getType() == SOURCE_TYPE.MYSQL_BUILT) {
//                if (StringUtils.isBlank(source.getTimeField())) {
//                    throw new ApiException(ErrorCode.MYSQL_TIME_FIELD_EMPTY);
//                }
                return MODE.MYSQL;
            }
            throw new ApiException(ErrorCode.NOT_FOUND_INDEX.getResult().getCode(),ErrorCode.NOT_FOUND_INDEX.getResult().getMessage());
        }
        String timeField = null;
        for (int sourceId : sourceIds) {
            Source source = sourceService.findById(sourceId);
            if (source == null) {
                // 容错性过滤
//                throw new ApiException(ErrorCode.NOT_FOUND_INDEX);
                continue;
            }
            if (source.getType() != SOURCE_TYPE.ELASTIC_BUILT) {
                throw new ApiException(ErrorCode.ERROR_QUERY_WRONG_TYPE.getResult().getCode(),ErrorCode.ERROR_QUERY_WRONG_TYPE.getResult().getMessage());
            }
            if (timeField == null) {
                timeField = source.getTimeField();
            } else if (!timeField.equalsIgnoreCase(source.getTimeField())) {
                throw new ApiException(ErrorCode.ERROR_QUERY_DIFFERENT_TIME.getResult().getCode(),ErrorCode.ERROR_QUERY_DIFFERENT_TIME.getResult().getMessage());
            }
        }
        if (timeField == null) {
            throw new ApiException(ErrorCode.ERROR_QUERY_WRONG_TYPE.getResult().getCode(),ErrorCode.ERROR_QUERY_WRONG_TYPE.getResult().getMessage());
        }
        return MODE.ELASTIC;
    }

}
