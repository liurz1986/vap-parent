package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.xc.model.DeleteModel;
import com.vrv.vap.xc.model.FileInfoModel;
import com.vrv.vap.xc.pojo.DataSourceManager;
import com.vrv.vap.xc.pojo.WhiteList;
import com.vrv.vap.xc.service.DataSourceManagerService;
import com.vrv.vap.xc.vo.DataSourceManagerQuery;
import com.vrv.vap.xc.vo.WhiteListQuery;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.excel.out.Export;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;

@RestController
public class DataSourceManagerController {

    private Log log = LogFactory.getLog(DataSourceManagerController.class);

    @Autowired
    private DataSourceManagerService dataSourceManagerService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @PostMapping("data_source")
    @ApiOperation("查询数据源列表")
    public VList<DataSourceManager> selectDataSource(@RequestBody DataSourceManagerQuery record) {
        VList<DataSourceManager> list = null;
        try {
            list = dataSourceManagerService.selectDataSourceListByPage(record);
        } catch (NumberFormatException e) {
            log.error("", e);
            list = new VList<DataSourceManager>(0, Collections.emptyList());
        }
        return list;
    }


    @DeleteMapping("data_source")
    @ApiOperation("删除数据源")
    public Result deleteDataSource(@RequestBody DeleteModel deleteModel) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            dataSourceManagerService.deleteDataSourceById(deleteModel.getIntegerId());
        } catch (NumberFormatException e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }


    @PutMapping("data_source")
    @ApiOperation("新增数据源")
    public Result addDataSource(@RequestBody DataSourceManager record) {
        try {
            dataSourceManagerService.insertDataSource(record);
        } catch (Exception e) {
            log.error("", e);
        }
        return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
    }

    @PatchMapping("data_source")
    @ApiOperation("修改数据源")
    public Result updateDataSource(@RequestBody DataSourceManager record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            dataSourceManagerService.updateDataSourceByKey(record);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @PostMapping("data_source/export")
    @ApiOperation("导出数据源")
    public VData<Export.Progress> exportDataSource(@RequestBody DataSourceManagerQuery record) {
        return dataSourceManagerService.exportDataSource(record);
    }

    @PostMapping("data_source/import")
    @ApiOperation("导入数据源")
    public Result importDataSource(@RequestBody FileInfoModel fileInfoModel) {
        if (StringUtils.isEmpty(fileInfoModel.getLocalPath())) {
            return VoBuilder.result(RetMsgEnum.ERROR_PARAM);
        }
        try {
            return dataSourceManagerService.importDataSource(fileInfoModel);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.result(RetMsgEnum.IMPORT_ERROR);
        }
    }

    @PostMapping("data_source2/json_import")
    @ApiOperation("定时任务导入json数据源")
    public Result readJsonDataAndStore() {
        try {
            dataSourceManagerService.readJsonDataAndStore();
            return VoBuilder.result(RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.result(RetMsgEnum.IMPORT_ERROR);
        }
    }

    @PostMapping("data_source2/export")
    @ApiOperation("导出数据源")
    public VData<Export.Progress> exportDataSource2(@RequestBody WhiteListQuery record) {
        return dataSourceManagerService.exportDataSource2(record);
    }

    @PostMapping("data_source2/import")
    @ApiOperation("导入数据源")
    public Result importDataSource2(@RequestBody FileInfoModel fileInfoModel) {
        if (StringUtils.isEmpty(fileInfoModel.getLocalPath())) {
            return VoBuilder.result(RetMsgEnum.ERROR_PARAM);
        }
        try {
            return dataSourceManagerService.importDataSource2(fileInfoModel);
        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.result(RetMsgEnum.IMPORT_ERROR);
        }
    }

    @PostMapping("data_source2")
    @ApiOperation("查询数据源列表2")
    public VList<WhiteList> selectDataSourceWhiteList(@RequestBody WhiteListQuery record) {
        VList<WhiteList> list = null;
        try {
            list = dataSourceManagerService.selectWhitelistListByPage(record);
        } catch (NumberFormatException e) {
            log.error("", e);
            list = new VList<WhiteList>(0, Collections.emptyList());
        }
        return list;
    }


    @DeleteMapping("data_source2")
    @ApiOperation("删除数据源2")
    public Result deleteDataSourceWhiteList(@RequestBody DeleteModel deleteModel) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            if (StringUtils.isEmpty(deleteModel.getIds())) {
                return VoBuilder.result(RetMsgEnum.ERROR_PARAM);
            }
            for (Integer id : deleteModel.getIntegerIdList()) {
                int success = dataSourceManagerService.deleteWhitelistById(id);
                if (success != 1) {
                    log.error("该数据源已开启或不存在,id=" + id);
                }
            }
        } catch (NumberFormatException e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }


    @PutMapping("data_source2")
    @ApiOperation("新增数据源2")
    public Result addDataSourceWhiteList(@RequestBody WhiteList record) {
        try {
            dataSourceManagerService.insertWhitelist(record);
        } catch (Exception e) {
            log.error("", e);
        }
        return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
    }

    @PatchMapping("data_source2")
    @ApiOperation("修改数据源2")
    public Result updateDataSourceWhiteList(@RequestBody WhiteList record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            dataSourceManagerService.updateWhitelistByKey(record);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

}
