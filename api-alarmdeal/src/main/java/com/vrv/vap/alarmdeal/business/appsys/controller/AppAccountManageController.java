package com.vrv.vap.alarmdeal.business.appsys.controller;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.model.AppAccountManage;
import com.vrv.vap.alarmdeal.business.appsys.service.AbstractBaseService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppAccountManageService;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppAccountManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerQueryVo;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
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
@Api(description = "应用系统账号管理")
@RestController
@RequestMapping(value="/appAccountManage")
public class AppAccountManageController extends AbstractAppSysController<AppAccountManage,String>{

    private static Logger logger= LoggerFactory.getLogger(AppAccountManageController.class);

    @Autowired
    private AppAccountManageService appAccountManageService;


    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private MessageService messageService;

    @Override
    public AbstractBaseService<AppAccountManage,String> getService(){
        return appAccountManageService;
    }


    final String[] DISALLOWED_FIELDS = new String[]{"", "",
            ""};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }

    @Override
    protected List<String> exportExcelHeaders() {
        Integer size= AppAccountManageVo.HEADERS.size();
        return AppAccountManageVo.HEADERS.subList(0,size-1);
    }

    @Override
    protected String[] getKeys() {
        Integer size=AppAccountManageVo.KEYS.length;
        return Arrays.copyOf(AppAccountManageVo.KEYS,size-1);
    }

    @Override
    protected String getSheetName(){
        return AppAccountManageVo.APP_ACCOUNT_MANAGE;
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
     * 应用系统账号管理 分页查询
     * @param appAccountManageVo
     * @return
     */
    @PostMapping("/getPage")
    @ApiOperation(value="应用系统账号管理-分页查询",notes="")
    @SysRequestLog(description="应用系统账号管理-分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<AppAccountManage> getAppAccountManagePage(@RequestBody AppAccountManageVo appAccountManageVo){
        return appAccountManageService.getAppAccountManagePage(appAccountManageVo);
    }

    @PostMapping(value = "/getAppAccountAssetPage")
    @ApiOperation(value="账户应用分页查询",notes="")
    @SysRequestLog(description="应用系统账号管理-账户应用分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<Map<String,Object>> getAppAccountAssetPage(@RequestBody AppSysManagerQueryVo appSysManagerQueryVo){
        return appAccountManageService.getAppAccountAssetPage(appSysManagerQueryVo);
    }


    /**
     * 新增系统账号管理
     * @param appAccountManageVo
     * @return
     */
    @PutMapping("")
    @ApiOperation(value="新增系统账号管理",notes="")
    @SysRequestLog(description="应用系统账号管理-新增系统账号管理", actionType = ActionType.ADD,manually=false)
    public Result<AppAccountManage> addAppAccountManage(@RequestBody AppAccountManageVo appAccountManageVo){
        AppAccountManage appAccountManage=mapperUtil.map(appAccountManageVo,AppAccountManage.class);
        appAccountManage.setGuid(UUIDUtils.get32UUID());
        appAccountManage.setCreateTime(new Date());
        appAccountManageService.save(appAccountManage);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(appAccountManage);
    }

    /**
     * 编辑系统账号管理
     * @param appAccountManageVo
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="编辑系统账号管理",notes="")
    @SysRequestLog(description="应用系统账号管理-编辑系统账号管理", actionType = ActionType.UPDATE,manually=false)
    public Result<AppAccountManage> editRoleManage(@RequestBody AppAccountManageVo appAccountManageVo){
        AppAccountManage appAccountManage=appAccountManageService.getOne(appAccountManageVo.getGuid());
        appAccountManage=mapperUtil.map(appAccountManageVo,appAccountManage.getClass());
        appAccountManageService.save(appAccountManage);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(appAccountManage);
    }

    /**
     * 删除应用系统资源
     * @param guid
     * @return
     */
    @DeleteMapping("/{guid}")
    @ApiOperation(value="删除系统账号管理",notes="")
    @SysRequestLog(description="应用系统账号管理-删除系统账号管理", actionType = ActionType.DELETE,manually=false)
    public Result<Boolean> deleteRoleManage(@PathVariable("guid") String guid){
        appAccountManageService.delete(guid);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }
    /**
     * 数据导入校验重构
     * 2022-09-26
     * @param file
     * @return
     */
    @PostMapping(value="/checkImportData")
    @ApiOperation(value="应用系统账号管理数据导入校验",notes="")
    @SysRequestLog(description="应用系统账号管理-数据导入校验", actionType = ActionType.IMPORT,manually=false)
    public Result<Map<String, List<Map<String, Object>>>> checkImportDataFile(@RequestParam("file") MultipartFile file){
        Map<String, List<Map<String, Object>>> map = appAccountManageService.checkImportData(file);
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
    @ApiOperation(value="应用系统账号管理导入数据入库",notes="")
    @SysRequestLog(description="应用系统账号管理-导入数据入库", actionType = ActionType.IMPORT,manually=false)
    public Result<Boolean> saveList(@RequestBody List<Map<String,Object>> list){
        appAccountManageService.saveList(list);
        // 数据变更消息推送 2023-05-23
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }



    @GetMapping("/countAccountByRoleIdAndAppId/{appId}/{roleId}")
    @ApiOperation(value="根据引用系统id和角色id统计账号",notes="")
    @SysRequestLog(description="应用系统账号管理-根据引用系统id和角色id统计账号", actionType = ActionType.SELECT,manually=false)
    public Result<Integer> countAccountByRoleIdAndAppId(@PathVariable("appId") String appId, @PathVariable("roleId") String roleId){
        List<QueryCondition> conditionList=new ArrayList<>();
        conditionList.add(QueryCondition.eq("appId",appId));
        conditionList.add(QueryCondition.eq("appRoleId",roleId));
        Long count=appAccountManageService.count(conditionList);
        return ResultUtil.success(count.intValue());
    }



}
