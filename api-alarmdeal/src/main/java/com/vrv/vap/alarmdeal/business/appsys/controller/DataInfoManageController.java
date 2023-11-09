package com.vrv.vap.alarmdeal.business.appsys.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.model.DataInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.service.AbstractBaseService;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.business.appsys.service.DataInfoManageService;
import com.vrv.vap.alarmdeal.business.appsys.vo.DataInfoManageVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.utils.dozer.MapperUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/10
 */
@Api(description = "数据信息管理")
@RequestMapping("/dataInfoManage")
@RestController
public class DataInfoManageController  extends AbstractAppSysController<DataInfoManage, Integer> {

    private static Logger logger= LoggerFactory.getLogger(DataInfoManageController.class);

    @Autowired
    private DataInfoManageService dataInfoManageService;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private MessageService messageService;

    @Autowired
    private ClassifiedLevelService classifiedLevelService;

    @Override
    public AbstractBaseService<DataInfoManage,Integer> getService(){
        return dataInfoManageService;
    }

    @Override
    protected List<String> exportExcelHeaders() {
        return DataInfoManageVo.HEADERS;
    }

    @Override
    protected String[] getKeys() {
        return DataInfoManageVo.KEYS;
    }

    @Override
    protected String getSheetName(){
        return DataInfoManageVo.DATA_INFO_MANAGE;
    }


    @Override
    protected List<BaseDictAll> getProtectLevelAll() {
        return null;
    }

    @Override
    protected List<BaseDictAll> getSecretLevelAll() {
        return classifiedLevelService.getDataInfoAll();
    }

    @Override
    protected String[] getProtectLevelAllCodeValue() {
        return new String[0];
    }

    @Override
    protected String[] getSecretLevelAllCodeValue() {
        return classifiedLevelService.getDataInfoSecretLevelAllValues();
    }

    @Override
    protected List<String> getBaseSecurityDomain() {
        return new ArrayList<>();
    }


    final String[] DISALLOWED_FIELDS = new String[]{"", "",
            ""};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }

    /**
     * 数据信息管理 分页查询
     * @param
     * @return
     */
    @PostMapping("/getPage")
    @ApiOperation(value="分页查询",notes="")
    @SysRequestLog(description="数据信息管理-分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<DataInfoManage> getDataInfoManagePage(@RequestBody DataInfoManageVo dataInfoManageVo){
        return dataInfoManageService.getDataInfoManagePage(dataInfoManageVo);
    }

    /**
     * 新增数据信息
     * @param dataInfoManageVo
     * @return
     */
    @PutMapping("")
    @ApiOperation(value="新增数据信息",notes="")
    @SysRequestLog(description="数据信息管理-新增数据信息", actionType = ActionType.ADD,manually=false)
    public Result<DataInfoManage> addDataInfoManage(@RequestBody DataInfoManageVo dataInfoManageVo){
        DataInfoManage dataInfoManage=mapperUtil.map(dataInfoManageVo,DataInfoManage.class);
        dataInfoManage.setCreateTime(new Date());
        dataInfoManage.setDataSourceType(1); // 手动录入
        dataInfoManageService.save(dataInfoManage);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("file");
        return ResultUtil.success(dataInfoManage);
    }

    /**
     * 编辑数据信息
     * @param dataInfoManageVo
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="编辑数据信息",notes="")
    @SysRequestLog(description="数据信息管理-编辑数据信息", actionType = ActionType.UPDATE,manually=false)
    public Result<DataInfoManage> editRoleManage(@RequestBody DataInfoManageVo dataInfoManageVo){
        DataInfoManage dataInfoManage=dataInfoManageService.getOne(dataInfoManageVo.getId());
        dataInfoManage=mapperUtil.map(dataInfoManageVo,dataInfoManage.getClass());
        dataInfoManageService.save(dataInfoManage);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("file");
        return ResultUtil.success(dataInfoManage);
    }

    /**
     * 删除数据信息
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value="删除数据信息",notes="")
    @SysRequestLog(description="数据信息管理-删除数据信息", actionType = ActionType.DELETE,manually=false)
    public Result<Boolean> deleteRoleManage(@PathVariable("id") String id){
        dataInfoManageService.delete(Integer.valueOf(id));
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("file");
        return ResultUtil.success(true);
    }



    @PostMapping(value="/checkImportData")
    @ApiOperation(value="数据导入校验",notes="")
    @SysRequestLog(description="数据信息管理-数据导入校验", actionType = ActionType.IMPORT,manually=false)
    public Result<Map<String, List<Map<String, Object>>>> checkImportDataFile(@RequestParam("file") MultipartFile file){
        Map<String, List<Map<String, Object>>> map = dataInfoManageService.checkImportData(file);
        if(map==null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "导入文件解析异常");
        }
        Result<Map<String, List<Map<String, Object>>>> result = new Result<>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
        result.setData(map);
        return result;
    }

    @PostMapping(value="/saveList")
    @ApiOperation(value="数据信息管理-导入数据入库",notes="")
    @SysRequestLog(description="数据信息管理导入数据入库", actionType = ActionType.IMPORT,manually=false)
    public Result<Boolean> saveList(@RequestBody List<Map<String,Object>> list){
        List<DataInfoManage> datas = new ArrayList<>();
        for(Map<String,Object> map : list){
            DataInfoManage dataInfoManage=mapperUtil.map(map,DataInfoManage.class);
            dataInfoManage.setCreateTime(new Date());
            datas.add(dataInfoManage);
        }
        dataInfoManageService.save(datas);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("file");
        return ResultUtil.success(true);
    }

    /**
     *
     * @param dataInfoManageVo
     * @return
     */
    @PostMapping("/exportDataExcel")
    @ApiOperation(value="数据信息管理数据导出",notes="")
    @SysRequestLog(description="数据信息管理-数据导出", actionType = ActionType.EXPORT,manually=false)
    public Result<String> exportDataExcel(@RequestBody DataInfoManageVo dataInfoManageVo){
        dataInfoManageVo.setCount_(100000);
        PageRes<DataInfoManage> dataInfoManageVoPageRes=getDataInfoManagePage(dataInfoManageVo);
        List<DataInfoManage> dataInfoManageList=dataInfoManageVoPageRes.getList();
        List<Map<String,Object>> mapList=new Gson().fromJson(gson.toJson(dataInfoManageList),new TypeToken<List<Map<String,Object>>>(){}.getType());
        String fileName = constructExcelData(mapList,DataInfoManageVo.KEYS,DataInfoManageVo.DATA_INFO_MANAGE);
        return ResultUtil.success(fileName);
    }


}
