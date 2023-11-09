package com.vrv.vap.data.controller;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.data.component.config.DictConfig;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.DiscoverEntity;
import com.vrv.vap.data.model.Maintain;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.service.MaintainService;
import com.vrv.vap.data.service.SourceFieldService;
import com.vrv.vap.data.service.SourceService;
import com.vrv.vap.data.util.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/maintain")
@Api(value = "【数管】配置管理", tags = "【数管】配置管理")
public class MaintainController extends ApiController {

    @Autowired
    MaintainService maintainService;

    @Autowired
    SourceService sourceService;

    @Autowired
    SourceFieldService sourceFieldService;

    @Autowired
    DictConfig dictConfig;

    @Autowired
    DataSource dataSource;

    @ApiOperation(value = "管理配置-查询")
    @PostMapping
    public VList<Maintain> query(@RequestBody Query query) {
        if (StringUtils.isBlank(query.getOrder_()) || StringUtils.isBlank(query.getBy_())) {
            query.setOrder_("id");
            query.setBy_("desc");
        }
        Example example = this.pageQuery(query, DiscoverEntity.class);
        return this.vList(maintainService.findByExample(example));
    }

    @ApiOperation(value = "管理配置-获取全部")
    @GetMapping
    public VData<List<Maintain>> getAll() {
        return this.vData(maintainService.findAll());
    }

    @ApiOperation(value = "管理配置-获取单条")
    @GetMapping(value = "/{id}")
    public VData<Maintain> get(@PathVariable("id") Integer id) {
        return this.vData(maintainService.findById(id));
    }

    @ApiOperation(value = "管理配置-添加")
    @PutMapping
    public VData<Maintain> add(@RequestBody Maintain maintain) {
        int result = maintainService.save(maintain);
        if (result == 1) {
            return this.vData(maintain);
        }
        return this.vData(false);
    }


    @ApiOperation(value = "管理配置-修改")
    @PatchMapping
    public Result update(@RequestBody Maintain maintain) {
        int result = maintainService.updateSelective(maintain);
        return this.result(result == 1);
    }

    @ApiOperation(value = "管理配置-删除")
    @DeleteMapping
    public Result delete(@RequestBody DeleteQuery delete) {
        int count = maintainService.deleteByIds(delete.getIds());
        return this.result(count > 0);
    }


    @ApiOperation(value = "数据导入模板下载")
    @GetMapping(value = "/template/{maintainId}")
    @ResponseBody
    public void importTemplate(HttpServletResponse response, @PathVariable Integer maintainId) throws ApiException {
        Maintain maintain = this.getMaintain(maintainId);
        List<SourceField> fields = this.getFields(maintain.getSourceId());
//        response.setContentType("application/vnd.ms-excel");                                                      //  xls
        response.setContentType("application/application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");   //  xlsx
        try {
            String fileName = java.net.URLEncoder.encode(maintain.getName() + "-数据导入模板", "UTF-8") + ".xlsx";
            response.setHeader("content-disposition", "attachment; filename=" + fileName);
            ExcelUtil.createTemplate(response.getOutputStream(), maintain.getName(), maintain.getPrimaryKey(), fields);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "导入 标准 SQL 语法")
    @PostMapping(value = "/import/{maintainId}", consumes = "multipart/*", headers = "content-type=multipart/form-data")
    public VData<Integer> sheetImport(@PathVariable("maintainId") Integer maintainId, @RequestParam("file") MultipartFile file) throws ApiException {
        Maintain maintain = this.getMaintain(maintainId);
        Source source = this.getSource(maintain.getSourceId());
        List<SourceField> fields = this.getFields(maintain.getSourceId());
        try {
            boolean isXlsx = false;
            if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
                isXlsx = true;
            } else if ("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet".equalsIgnoreCase(file.getContentType())) {
                isXlsx = false;
            } else if (file.getOriginalFilename().endsWith("xlsx")) {
                isXlsx = true;
            }
            return this.vData(ExcelUtil.importData(file.getInputStream(), isXlsx, dataSource, maintain, source, fields, dictConfig));
        } catch (IOException e) {
            throw new ApiException(ErrorCode.MAINTAIN_TEMPLATE_IO_ERROR.getResult().getCode(),ErrorCode.MAINTAIN_TEMPLATE_IO_ERROR.getResult().getMessage());
        }

    }


    @ApiOperation(value = "插入 标准 SQL 语法")
    @PutMapping(value = "/sheet/{maintainId}")
    public VData<Map> sqlInsert(@PathVariable("maintainId") Integer maintainId, @RequestBody Map data) throws ApiException {
        Maintain maintain = this.getMaintain(maintainId);
        Source source = this.getSource(maintain.getSourceId());
        List<SourceField> fields = this.getFields(maintain.getSourceId());
        int insertId = maintainService.execInsert(source.getName(), fields, data);
        data.put(maintain.getPrimaryKey(), insertId);
        return this.vData(data);
    }

    @ApiOperation(value = "修改 标准 SQL 语法")
    @PatchMapping(value = "/sheet/{maintainId}")
    public Result sqlUpdate(@PathVariable("maintainId") Integer maintainId, @RequestBody Map data) throws ApiException {
        Maintain maintain = this.getMaintain(maintainId);
        Source source = this.getSource(maintain.getSourceId());
        List<SourceField> fields = this.getFields(maintain.getSourceId());
        int result = maintainService.execUpdate(source.getName(), maintain.getPrimaryKey(), fields, data);
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除 标准 SQL 语法")
    @DeleteMapping(value = "/sheet/{maintainId}")
    public Result sqlDelete(@PathVariable("maintainId") Integer maintainId, @RequestBody DeleteQuery ids) throws ApiException {
        Maintain maintain = this.getMaintain(maintainId);
        Source source = this.getSource(maintain.getSourceId());
        List<SourceField> fields = this.getFields(maintain.getSourceId());
        int result = maintainService.execDelete(source.getName(), maintain.getPrimaryKey(), ids.getIds().split(","));
        return this.result(result > 0);
    }


    // 通用方法（THROW）
    private Maintain getMaintain(Integer maintainId) throws ApiException {
        Maintain maintain = maintainService.findById(maintainId);
        if (maintain == null) throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
        return maintain;
    }

    private Source getSource(Integer sourceId) throws ApiException {
        Source source = sourceService.findById(sourceId);
        if (source == null) throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
        return source;
    }

    private List<SourceField> getFields(Integer sourceId) throws ApiException {
        List<SourceField> fields = sourceFieldService.findAllBySourceId(sourceId);
        if (fields == null || fields.size() == 0) throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
        return fields;
    }


}
