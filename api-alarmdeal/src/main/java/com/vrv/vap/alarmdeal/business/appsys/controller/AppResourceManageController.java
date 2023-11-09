package com.vrv.vap.alarmdeal.business.appsys.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.model.AppResourceManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AbstractBaseService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppResourceManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppResourceManageVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.jpa.common.UUIDUtils;
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

import java.util.*;

/**
 * @author lps 2021/8/10
 */
@Api(description = "应用系统资源管理")
@RestController
@RequestMapping(value="/appResourceManage")
public class AppResourceManageController extends AbstractAppSysController<AppResourceManage,String> {

    private static Logger logger= LoggerFactory.getLogger(AppResourceManageController.class);


    @Autowired
    private AppResourceManageService appResourceManageService;
    @Autowired
    private AppSysManagerService appSysManagerService;
    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private MessageService messageService;
    @Override
    public AbstractBaseService<AppResourceManage,String> getService(){
        return appResourceManageService;
    }

    final String[] DISALLOWED_FIELDS = new String[]{"", "",""};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }

    @Override
    protected List<String> exportExcelHeaders() {
        int size= AppResourceManageVo.HEADERS.size();
        return AppResourceManageVo.HEADERS.subList(0,size-1);
    }

    @Override
    protected String[] getKeys() {
        int size=AppResourceManageVo.APP_RESOURCE_MANAGE.length();
        return Arrays.copyOf(AppResourceManageVo.KEYS,size-1);
    }

    @Override
    protected String getSheetName(){
        return AppResourceManageVo.APP_RESOURCE_MANAGE;
    }


    @Override
    protected List<BaseDictAll> getProtectLevelAll() {
        return null;
    }

    @Override
    protected List<BaseDictAll> getSecretLevelAll() {
        return null;
    }

    @Override
    protected String[] getProtectLevelAllCodeValue() {
        return new String[0];
    }

    @Override
    protected String[] getSecretLevelAllCodeValue() {
        return new String[0];
    }

    @Override
    protected List<String> getBaseSecurityDomain() {
        return new ArrayList<>();
    }


    /**
     * 应用系统资源管理 分页查询
     * @param appResourceManageVo
     * @return
     */
    @PostMapping("/getPage")
    @ApiOperation(value="应用系统资源管理分页查询",notes="")
    @SysRequestLog(description="应用系统资源管理-分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<AppResourceManage> getAppResourceManagePage(@RequestBody AppResourceManageVo appResourceManageVo){
        return appResourceManageService.getAppResourceManagePage(appResourceManageVo);
    }

    /**
     * 新增应用系统资源
     * @param appResourceManageVo
     * @return
     */
    @PutMapping("")
    @ApiOperation(value="新增应用系统资源",notes="")
    @SysRequestLog(description="应用系统资源管理-新增应用系统资源", actionType = ActionType.ADD,manually=false)
    public Result<AppResourceManage> addappResourceManage(@RequestBody AppResourceManageVo appResourceManageVo){
        AppResourceManage appResourceManage=mapperUtil.map(appResourceManageVo,AppResourceManage.class);
        appResourceManage.setGuid(UUIDUtils.get32UUID());
        appResourceManage.setCreateTime(new Date());
        appResourceManageService.save(appResourceManage);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(appResourceManage);
    }

    /**
     * 编辑应用系统资源
     * @param appResourceManageVo
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="编辑应用系统资源",notes="")
    @SysRequestLog(description="应用系统资源管理-编辑应用系统资源", actionType = ActionType.UPDATE,manually=false)
    public Result<AppResourceManage> editRoleManage(@RequestBody AppResourceManageVo appResourceManageVo){
        AppResourceManage appResourceManage=appResourceManageService.getOne(appResourceManageVo.getGuid());
        appResourceManage=mapperUtil.map(appResourceManageVo,appResourceManage.getClass());
        appResourceManageService.save(appResourceManage);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(appResourceManage);
    }

    /**
     * 删除应用系统资源
     * @param guid
     * @return
     */
    @DeleteMapping("/{guid}")
    @ApiOperation(value="删除应用系统资源",notes="")
    @SysRequestLog(description="应用系统资源管理-删除应用系统资源", actionType = ActionType.DELETE,manually=false)
    public Result<Boolean> deleteRoleManage(@PathVariable("guid") String guid){
        appResourceManageService.delete(guid);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }


    @PostMapping(value="/checkImportData")
    @ApiOperation(value="应用系统资源管理数据导入校验",notes="")
    @SysRequestLog(description="应用系统资源管理-数据导入校验", actionType = ActionType.IMPORT,manually=false)
    public Result<Map<String, List<Map<String, Object>>>> checkImportDataFile(@RequestParam("file") MultipartFile file){
        Map<String, List<Map<String, Object>>> map = appResourceManageService.checkImportData(file);
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
    @ApiOperation(value="应用系统资源管理导入数据入库",notes="")
    @SysRequestLog(description="应用系统资源管理-导入数据入库", actionType = ActionType.IMPORT,manually=false)
    public Result<Boolean> saveList(@RequestBody List<Map<String,Object>> list){
        List<AppResourceManage> datas = new ArrayList<>();
        for(Map<String,Object> map : list){
            AppResourceManage appResourceManage=gson.fromJson(JSON.toJSONString(map),AppResourceManage.class);
            appResourceManage.setGuid(UUIDUtils.get32UUID());
            appResourceManage.setCreateTime(new Date());
            datas.add(appResourceManage);
        }
        appResourceManageService.save(datas);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }


    /**
     * 某应用资源类型分布数据
     * @return
     */
    @GetMapping("/countResourceGroupByType/{appId}")
    @ApiOperation(value="应用系统资源管理-统计资源类型数量",notes="")
    @SysRequestLog(description="应用系统资源管理-统计资源类型数量", actionType = ActionType.SELECT,manually=false)
    public Map<String,Object>  countResourceGroupByType(@PathVariable("appId") Integer appId){
        return appResourceManageService.countResourceGroupByType(appId);
    }


}
