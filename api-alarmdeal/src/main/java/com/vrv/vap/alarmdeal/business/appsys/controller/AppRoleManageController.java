package com.vrv.vap.alarmdeal.business.appsys.controller;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.model.AppRoleManage;
import com.vrv.vap.alarmdeal.business.appsys.service.AbstractBaseService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppRoleManageService;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppRoleManageQueryVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppRoleManageVo;
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
import org.apache.commons.lang3.StringUtils;
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
@Api(description = "应用系统角色管理")
@RestController
@RequestMapping(value="/appRoleManage")
public class AppRoleManageController extends AbstractAppSysController<AppRoleManage,String>  {

    private static Logger logger= LoggerFactory.getLogger(AppRoleManageController.class);

    @Autowired
    private AppRoleManageService appRoleManageService;

    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private MessageService messageService;
    @Override
    public AbstractBaseService<AppRoleManage,String> getService(){
        return appRoleManageService;
    }

    final String[] DISALLOWED_FIELDS = new String[]{"", "",
            ""};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }

    @Override
    protected List<String> exportExcelHeaders() {
        Integer size= AppRoleManageVo.HEADERS.size();
        return AppRoleManageVo.HEADERS.subList(0,size-1);
    }

    @Override
    protected String[] getKeys() {
        Integer size=AppRoleManageVo.KEYS.length;
        return Arrays.copyOf(AppRoleManageVo.KEYS,size-1);
    }


    @Override
    protected String getSheetName(){
        return AppRoleManageVo.APP_ROLE_MANAGE;
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
     * 应用系统角色管理 分页查询
     * @param appRoleManageQueryVo
     * @return
     */
    @PostMapping("/getPage")
    @ApiOperation(value="应用系统角色管理分页查询",notes="")
    @SysRequestLog(description="应用系统角色管理-分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<AppRoleManage> getPage(@RequestBody AppRoleManageQueryVo appRoleManageQueryVo){
        return appRoleManageService.getAppRoleManagePage(appRoleManageQueryVo);
    }

    /**
     * 新增系统角色
     * @param appRoleManageVo
     * @return
     */
    @PutMapping("")
    @ApiOperation(value="新增系统角色",notes="")
    @SysRequestLog(description="应用系统角色管理-新增系统角色", actionType = ActionType.ADD,manually=false)
    public Result<AppRoleManage> addAppRoleManage(@RequestBody AppRoleManageVo appRoleManageVo){
        AppRoleManage appRoleManage=mapperUtil.map(appRoleManageVo,AppRoleManage.class);
        appRoleManage.setGuid(UUIDUtils.get32UUID());
        appRoleManage.setCreateTime(new Date());
        appRoleManageService.save(appRoleManage);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(appRoleManage);
    }

    /**
     * 编辑系统角色
     * @param appRoleManageVo
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="编辑系统角色",notes="")
    @SysRequestLog(description="应用系统角色管理-编辑系统角色", actionType = ActionType.UPDATE,manually=false)
    public Result<AppRoleManage> editRoleManage(@RequestBody AppRoleManageVo appRoleManageVo){
        AppRoleManage appRoleManage=appRoleManageService.getOne(appRoleManageVo.getGuid());
        appRoleManage=mapperUtil.map(appRoleManageVo,appRoleManage.getClass());
        appRoleManageService.save(appRoleManage);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(appRoleManage);
    }

    /**
     * 删除系统角色
     * @param guid
     * @return
     */
    @DeleteMapping("/{guid}")
    @ApiOperation(value="删除系统角色",notes="")
    @SysRequestLog(description="应用系统角色管理-删除系统角色", actionType = ActionType.DELETE,manually=false)
    public Result<Boolean> deleteRoleManage(@PathVariable("guid") String guid){
        appRoleManageService.delete(guid);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }

    /**
     * 获取所有角色
     * @param guid
     * @return
     */
    @GetMapping("/getAllRole/{appId}")
    @ApiOperation(value="获取所有角色",notes="")
    @SysRequestLog(description="应用系统角色管理-获取所有角色", actionType = ActionType.SELECT,manually=false)
    public Result<List<AppRoleManage>> getAllRole(@PathVariable("appId") Integer appId){
       List<AppRoleManage> appRoleManageList=appRoleManageService.getAllByAppId(appId);
       return ResultUtil.success(appRoleManageList);
    }


    @PostMapping(value="/checkImportData")
    @ApiOperation(value="应用系统角色管理数据导入校验",notes="")
    @SysRequestLog(description="应用系统角色管理-导入校验", actionType = ActionType.IMPORT,manually=false)
    public Result<Map<String, List<Map<String, Object>>>> checkImportDataFile(@RequestParam("file") MultipartFile file){
        Map<String, List<Map<String, Object>>> map = appRoleManageService.checkImportData(file);
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
    @ApiOperation(value="应用系统角色管理导入数据入库",notes="")
    @SysRequestLog(description="应用系统角色管理-导入数据入库", actionType = ActionType.IMPORT,manually=false)
    public Result<Boolean> saveList(@RequestBody List<Map<String,Object>> list){
        for(Map<String,Object> map : list){
            mapEnumTransfer(map,null,null);
            if(StringUtils.isBlank(map.get("cancelTime").toString())){
                map.remove("cancelTime");
            }
            AppRoleManage appRoleManage=gson.fromJson(JSON.toJSONString(map),AppRoleManage.class);
            appRoleManage.setGuid(UUIDUtils.get32UUID());
            appRoleManage.setCreateTime(new Date());
            appRoleManageService.save(appRoleManage);
        }
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }




}
