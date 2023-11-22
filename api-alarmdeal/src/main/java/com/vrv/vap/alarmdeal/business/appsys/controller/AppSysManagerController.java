package com.vrv.vap.alarmdeal.business.appsys.controller;

import com.alibaba.fastjson.JSON;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.job.AppNetFlowAuditJob;
import com.vrv.vap.alarmdeal.business.appsys.model.AppAccountManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppResourceManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppRoleManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.*;
import com.vrv.vap.alarmdeal.business.appsys.service.impl.FeignService;
import com.vrv.vap.alarmdeal.business.appsys.util.EnumTransferUtil;
import com.vrv.vap.alarmdeal.business.appsys.vo.*;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.BaseDataRedisCacheService;
import com.vrv.vap.alarmdeal.business.asset.util.ExportExcelUtils;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetSearchVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.jpa.common.FileHeaderUtil;
import com.vrv.vap.jpa.web.NameValue;
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
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lps 2021/8/9
 */

@Api(description = "应用系统管理")
@RestController
@RequestMapping(value="/appSysManager")
public class AppSysManagerController  extends AbstractAppSysController<AppSysManager,Integer> {

    private static Logger logger= LoggerFactory.getLogger(AppSysManagerController.class);



    @Autowired
    private AppSysManagerService appSysManagerService;


    @Autowired
    private AppAccountManageService appAccountManageService;

    @Autowired
    private AppRoleManageService appRoleManageService;

    @Autowired
    private AppResourceManageService appResourceManageService;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private FileConfiguration fileConfiguration;

    @Autowired
    private AssetDao assetDao;

    @Autowired
    private AssetService assetService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private ClassifiedLevelService classifiedLevelService;
    @Autowired
    private BaseDataRedisCacheService  baseDataRedisCacheService;
    @Autowired
    private AlarmEventManagementService alarmEventManagementService;
    @Override
    protected List<String> exportExcelHeaders() {
        return AppSysManagerVo.HEADERS;
    }

    @Override
    protected String[] getKeys() {
        return AppSysManagerVo.KEYS;
    }

    @Override
    protected String getSheetName(){
        return AppSysManagerVo.APP_SYS_MANAGER;
    }


    @Override
    protected List<BaseDictAll> getProtectLevelAll() {
        return null;
    }

    @Override
    protected List<BaseDictAll> getSecretLevelAll() {
        return classifiedLevelService.getAppAll();
    }

    @Override
    protected String[] getProtectLevelAllCodeValue() {
        return new String[0];
    }

    @Override
    protected String[] getSecretLevelAllCodeValue() {
        return classifiedLevelService.getAppSecretLevelAllValues();
    }

    @Override
    protected List<String> getBaseSecurityDomain() {
        return new ArrayList<>();
    }

    @Override
    public AbstractBaseService<AppSysManager,Integer> getService(){
        return appSysManagerService;
    }

    final String[] DISALLOWED_FIELDS = new String[]{"", "",
            ""};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }


    @PostMapping(value = "/getPage")
    @ApiOperation(value="应用系统管理分页查询",notes="")
    @SysRequestLog(description="应用系统管理-分页查询", actionType = ActionType.SELECT,manually=false)
    public PageRes<AppSysManagerVo> getAppSysManagerPage(@RequestBody AppSysManagerQueryVo appSysManagerQueryVo){
        return appSysManagerService.getAppSysManagerPage(appSysManagerQueryVo);
    }
    /**
     * 新标准画像应用系统列表
     * @param appSysManagerQueryVo
     * @return
     */
    @PostMapping(value = "/getImgPage")
    @ApiOperation(value="新标准画像应用系统列表",notes="")
    @SysRequestLog(description="新标准画像应用系统列表", actionType = ActionType.SELECT,manually=false)
    public PageRes<AppSysManagerVo> getAppSysManagerImgPage(@RequestBody AppSysManagerQueryVo appSysManagerQueryVo){
        return appSysManagerService.getAppSysManagerImgPage(appSysManagerQueryVo);
    }
    /**
     * 导出新标准画像资产文件
     * @param assetSearchVO
     * @return
     */
    @PostMapping(value="/exportNewAssetInfo")
    @ApiOperation(value="生成应用系统导出文件",notes="")
    @SysRequestLog(description="生成应用系统导出文件", actionType = ActionType.EXPORT,manually=false)
    public  Result<String> exportNewAssetInfo(@RequestBody AppSysManagerQueryVo appSysManagerQueryVo){
        return appSysManagerService.exportNewAssetInfo(appSysManagerQueryVo);
    }
    @GetMapping("/getAppAlarmEventTop10")
    @ApiOperation(value = "应用系统事件数量top10", notes = "")
    @SysRequestLog(description="应用系统事件数量top10", actionType = ActionType.SELECT,manually=false)
    public Result<List<NameValue>>  getAppAlarmEventTop10() {
        return ResultUtil.success(appSysManagerService.getAppAlarmEventTop10());
    }

    /**
     * 新增应用系统
     * @param appSysManagerVo
     * @return
     */
    @PutMapping("")
    @ApiOperation(value="新增应用系统",notes="")
    @SysRequestLog(description="应用系统管理-新增应用系统", actionType = ActionType.ADD,manually=false)
    public Result<AppSysManagerVo> addDataInfoManage(@RequestBody AppSysManagerVo appSysManagerVo){
        AppSysManager appSysManager=mapperUtil.map(appSysManagerVo,AppSysManager.class);
        appSysManager.setCreateTime(new Date());
        Integer curMaxId = appSysManagerService.getCurrentMaxId();
        Integer appId = curMaxId+1;
        appSysManager.setId(appId); // 增加主键id
        appSysManager.setDataSourceType(1);
        List<Integer> ids = new ArrayList<>();
        ids.add(appId);
        appSysManagerService.deleteRefByAppIds(ids); // 防止脏数据，清除关联数据 2023-08
        appSysManagerService.save(appSysManager);
        appSysManagerVo=mapperUtil.map(appSysManager,AppSysManagerVo.class);
        // 缓存数据 2022-08-09
        baseDataRedisCacheService.addAppSysManager(appSysManager);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("app");

        return ResultUtil.success(appSysManagerVo);
    }

    /**
     * 编辑应用系统
     * @param appSysManagerEditVo
     * @return
     */
    @PostMapping("")
    @ApiOperation(value="编辑应用系统",notes="")
    @SysRequestLog(description="应用系统管理-编辑应用系统", actionType = ActionType.UPDATE,manually=false)
    public Result<AppSysManagerVo> editRoleManage(@RequestBody AppSysManagerEditVo appSysManagerEditVo){
        AppSysManager appSysManager=appSysManagerService.getOne(appSysManagerEditVo.getId());
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("ip",appSysManagerEditVo.getIp()));
        long count = assetService.count(queryConditions);
        if (count==0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"资产基础数据不存在该应用ip");
        }
        String oldAppNo = appSysManager.getAppNo();
        mapperUtil.copy(appSysManagerEditVo,appSysManager);
        appSysManagerService.save(appSysManager);
        AppSysManagerVo sysManagerVo=appSysManagerService.queryOne(appSysManager.getId());
        String severIds=sysManagerVo.getServiceId();
        if(StringUtils.isNotBlank(severIds)){
            sysManagerVo.setServerCount(severIds.split(",").length);
        }else{
            sysManagerVo.setServerCount(0);
        }
        // 缓存数据 2022-08-09
        baseDataRedisCacheService.editAppSysManager(appSysManager,oldAppNo);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(sysManagerVo);
    }

    /**
     * 删除数据信息
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    @ApiOperation(value="删除应用系统",notes="id-应用系统id")
    @SysRequestLog(description="应用系统管理-删除应用系统", actionType = ActionType.DELETE,manually=false)
    public Result<Boolean> deleteRoleManage(@PathVariable("id") String id){
        AppSysManager appSysManager=appSysManagerService.getOne(Integer.valueOf(id));
        if(null == appSysManager){
            return ResultUtil.error(-1,"数据不存在!");
        }
        // 删除
        appSysManagerService.deleteData(id);
        // 缓存数据 2022-08-09
        baseDataRedisCacheService.delAppSysManager(appSysManager);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }
    @GetMapping("/{id}")
    @ApiOperation(value="应用系统基本信息查询",notes="id-应用系统id")
    @SysRequestLog(description="应用系统管理-应用系统基本信息查询", actionType = ActionType.SELECT,manually=false)
    public Result<AppSysManagerVo> getAppSysManager(@PathVariable("id") Integer id){
        AppSysManagerVo appSysManagerVo=appSysManagerService.queryOne(id);
        return ResultUtil.success(appSysManagerVo);

    }

    @GetMapping("/countServerGroupByType/{id}")
    @ApiOperation(value="服务器厂商分布",notes="id-应用系统id")
    @SysRequestLog(description="服务器厂商分布", actionType = ActionType.SELECT,manually=false)
    public Result<Map<String,Object>> countServerGroupByType(@PathVariable("id") Integer id){
        Map<String,Object> map=appSysManagerService.countServerGroupByType(id);
        return ResultUtil.success(map);

    }



    /**
     * 增加服务器
     * @param map
     * @return
     */
    @PostMapping("/addServer")
    @ApiOperation(value="增加服务器",notes="参数: id-应用系统id; serverIds:资产id(多个以逗号分隔)")
    @SysRequestLog(description="增加服务器", actionType = ActionType.ADD,manually=false)
    public Result<AppSysManager> updateServer(@RequestBody Map<String,Object> map){
        String idStr=map.get("id").toString();
        String serverIds=map.get("serverIds").toString();
        AppSysManager appSysManager=appSysManagerService.getOne(Integer.valueOf(idStr));
        if(StringUtils.isNotBlank(appSysManager.getServiceId())){
            serverIds=appSysManager.getServiceId()+","+serverIds;
        }
        // 增加去重处理 2023-5-23
        String[] idArr = serverIds.split(",");
        List<String> idList=new ArrayList<>(Arrays.asList(idArr));
        appSysManager.setServiceId(serverIdDuplicateHandle(idList));
        appSysManagerService.save(appSysManager);
        // 缓存数据 2022-08-09
        baseDataRedisCacheService.addServer(appSysManager,serverIds);
        // 数据变更消息推送 2022-08-09
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(appSysManager);
    }

    /**
     * 服务器id去重处理
     * @param serverIds
     * @return
     */
    private String serverIdDuplicateHandle(List<String> serverIds) {
        Set<String> ids = new HashSet<>();
        for(int i= 0;i < serverIds.size();i++){
            ids.add(serverIds.get(i));
        }
        String serverGuids = StringUtils.join(ids.toArray(),",");
        return serverGuids;
    }

    /**
     * 删除应用服务器
     */
    @DeleteMapping("/deleteServer")
    @ApiOperation(value="删除服务器",notes="参数: id-应用系统id; serverIds:资产id(多个以逗号分隔)")
    @SysRequestLog(description="删除服务器", actionType = ActionType.DELETE,manually=false)
    public Result<Boolean> deleteServer(@RequestBody Map<String,Object> map){
        String idStr=map.get("id").toString();
        String serverIds=map.get("serverIds").toString();
        AppSysManager appSysManager=appSysManagerService.getOne(Integer.valueOf(idStr));
        String[] idArr=appSysManager.getServiceId().split(",");
        List<String> idList=new ArrayList<>(Arrays.asList(idArr));
        idList.remove(serverIds);
        appSysManager.setServiceId(serverIdDuplicateHandle(idList));
        appSysManagerService.save(appSysManager);
        // 缓存数据 2022-08-09
        baseDataRedisCacheService.delServer(appSysManager,serverIds);
        // 数据变更消息推送 2022-08-09
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }

    /**
     *
     * @param appSysManagerQueryVo
     * @return
     */
    @PostMapping("/exportDataExcel")
    @ApiOperation(value="应用系统管理数据导出",notes="")
    @SysRequestLog(description="应用系统管理数据导出", actionType = ActionType.EXPORT,manually=false)
    public Result<String> exportDataExcel(@RequestBody AppSysManagerQueryVo appSysManagerQueryVo){
        appSysManagerQueryVo.setCount_(100000);
        PageRes<AppSysManagerVo> appSysManagerVoPageRes=appSysManagerService.getAppSysManagerPage(appSysManagerQueryVo);
        List<AppSysManagerVo> appSysManagerList=appSysManagerVoPageRes.getList();
        List<Map<String,Object>> mapList=gson.fromJson(JSON.toJSONString(appSysManagerList),new TypeToken<List<Map<String,Object>>>(){}.getType());
        //创建空的excel文件
        String fileName=AppSysManagerVo.APP_SYS_MANAGER+".xls";
        //String filePath = "D:\\"+fileName;
        String filePath= fileConfiguration.getFilePath()+ File.separator+fileName;
        OutputStream out = null;
        HSSFWorkbook workbook = null;
        try {
            out = new FileOutputStream(filePath);
            // 生成Excel
            workbook = new HSSFWorkbook();
            constructExcel(mapList,AppSysManagerVo.KEYS,AppSysManagerVo.HEADERS,AppSysManagerVo.APP_SYS_MANAGER,workbook);
            List<AppAccountManage> appAccountManageAll=new ArrayList<>();
            List<AppRoleManage> appRoleManageAll=new ArrayList<>();
            List<AppResourceManage> appResourceManageAll=new ArrayList<>();
            List<AppServerVo> serverLists = new ArrayList<>();
            for(AppSysManagerVo appSysManagerVo: appSysManagerList){
                Integer appId=appSysManagerVo.getId();
                List<AppAccountManage> appAccountManageList=appAccountManageService.getAllByAppId(appId);
                appAccountManageAll.addAll(appAccountManageList);
                List<AppRoleManage> appRoleManageList=appRoleManageService.getAllByAppId(appId);
                appRoleManageAll.addAll(appRoleManageList);
                List<AppResourceManage> appResourceManageList=appResourceManageService.getAllByAppId(appId);
                appResourceManageAll.addAll(appResourceManageList);
                // 增加服务器导出 2022-05-05
                addServerData(appSysManagerVo,serverLists);
            }
            List<Map<String,Object>> mapList2=gson.fromJson(gson.toJson(appRoleManageAll),new TypeToken<List<Map<String,Object>>>(){}.getType());
            constructExcel(mapList2, AppRoleManageVo.KEYS,AppRoleManageVo.HEADERS,AppRoleManageVo.APP_ROLE_MANAGE,workbook);
            List<Map<String,Object>> mapList1=gson.fromJson(gson.toJson(appAccountManageAll),new TypeToken<List<Map<String,Object>>>(){}.getType());
            constructExcel(mapList1, AppAccountManageVo.KEYS,AppAccountManageVo.HEADERS,AppAccountManageVo.APP_ACCOUNT_MANAGE,workbook);
            List<Map<String,Object>> mapList3=gson.fromJson(gson.toJson(appResourceManageAll),new TypeToken<List<Map<String,Object>>>(){}.getType());
            constructExcel(mapList3, AppResourceManageVo.KEYS,AppResourceManageVo.HEADERS,AppResourceManageVo.APP_RESOURCE_MANAGE,workbook);
            List<Map<String,Object>> mapList4=gson.fromJson(gson.toJson(serverLists),new TypeToken<List<Map<String,Object>>>(){}.getType());
            constructExcel(mapList4, AppServerVo.KEYS,AppServerVo.HEADERS,AppServerVo.APP_RESOURCE_MANAGE,workbook); // 服务器
            workbook.write(out);
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(workbook != null){
                try {
                    workbook.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(out != null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return ResultUtil.success(fileName);
    }

    private void addServerData(AppSysManagerVo appSysManagerVo, List<AppServerVo> serverLists) {
        String sertviceId = appSysManagerVo.getServiceId();
        if(StringUtils.isEmpty(sertviceId)){
           return;
        }
        List<AppServerVo> datas = assetDao.getAssetServer(sertviceId.split(","));
        if(null == datas || datas.size() == 0){
            return;
        }
        serverLists.addAll(datas);
    }


    public void constructExcel(List<Map<String, Object>> mapList, String[] keys, List<String> headers, String sheetName, HSSFWorkbook workbook) {
        List<List<String>> result=mapListEnumTransfer(mapList, keys);
        ExportExcelUtils eeu = new ExportExcelUtils();
        exportExcelByEntities(workbook,eeu,sheetName,headers,result);

    }

    /**
     * 数据导入校验重构
     * 2022-09-26
     * @param file
     * @return
     */
    @PostMapping(value="/checkImportData")
    @ApiOperation(value="应用系统管理数据导入校验",notes="")
    @SysRequestLog(description="应用系统管理数据导入校验", actionType = ActionType.IMPORT,manually=false)
    public Result<AppImportResultVO> checkImportData(@RequestParam("file") MultipartFile file){
        AppImportResultVO map = appSysManagerService.checkImportData(file);
        if(map==null) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "导入文件解析异常");
        }
        Result<AppImportResultVO> result = new Result<AppImportResultVO>();
        result.setCode(ResultCodeEnum.SUCCESS.getCode());
        result.setMsg(ResultCodeEnum.SUCCESS.getMsg());
        result.setData(map);
        return result;
    }
    @Autowired
    private FeignService feignService;

    @PostMapping(value="/saveList")
    @ApiOperation(value="应用系统管理导入数据入库",notes="")
    @SysRequestLog(description="应用系统管理导入数据入库", actionType = ActionType.IMPORT,manually=false)
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> saveList(@RequestBody AppImportResultVO list){
        Integer curMaxId = appSysManagerService.getCurrentMaxId(); // 获取当前最大的id
        if(null == list || null == list.getTrueList() ||  list.getTrueList().size() == 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "数据为空");
        }
        List<Map<String,Object>> appLists = list.getTrueList();
        Map<String,Map<String,List<Map<String,Object>>>> relations = list.getRelations();
        Map<String,List<Map<String,Object>>> appAccountManageMapSaves=new HashMap<>();
        Map<String,List<Map<String,Object>>> appRoleManageMapSaves=new HashMap<>();
        Map<String,List<Map<String,Object>>> appResourceManageMapSaves=new HashMap<>();
        if(null != relations && relations.size() > 0){
            appRoleManageMapSaves = relations.get("appRole")==null?new HashMap<>():relations.get("appRole");
            appAccountManageMapSaves =  relations.get("appAccount")==null?new HashMap<>():relations.get("appAccount");
            appResourceManageMapSaves =  relations.get("appResource")==null?new HashMap<>():relations.get("appResource");
        }
        List<AppSysManager> appSysManagers = new ArrayList<>();
        List<AppRoleManage> appRoleManages = new ArrayList<>();
        List<AppAccountManage> appAccountManages = new ArrayList<>();
        List<AppResourceManage> appResourceManages = new ArrayList<>();
        Map<String, String> personMapName = feignService.getPersonMapName();
        // 构造数据
        for(Map<String,Object> map : appLists){
            String oldId=map.get("id").toString();
            map.remove("id");
            AppSysManager appSysManager=mapperUtil.map(map,AppSysManager.class);
            appSysManager.setCreateTime(new Date());
            if (StringUtils.isNotBlank(appSysManager.getPersonName())){
                appSysManager.setPersonCode(personMapName.get(appSysManager.getPersonName()));
            }
            curMaxId++;
            appSysManager.setId(curMaxId);
            appSysManager.setDataSourceType(1);
            List<Map<String,Object>> mapList3=appRoleManageMapSaves.get(oldId);
            if(mapList3!=null){
                appRoleManageService.getImportData(mapList3,appSysManager.getId(),appRoleManages,oldId,appSysManager.getAppName());
            }
            List<Map<String,Object>> mapList1=appAccountManageMapSaves.get(oldId);
            if(mapList1!=null){
                appAccountManageService.getImportData(mapList1,appSysManager.getId(),appAccountManages,appRoleManages,oldId,appSysManager.getAppName());
            }
            List<Map<String,Object>> mapList2=appResourceManageMapSaves.get(oldId);
            if(mapList2!=null){
                appResourceManageService.getImportData(mapList2,appSysManager.getId(),appResourceManages,appSysManager.getAppName());
            }
            appSysManagers.add(appSysManager);
        }
        // 执行数据保存
        batchSaveDatas(appSysManagers,appRoleManages,appAccountManages,appResourceManages);
        // 数据变更消息推送 2022-06-01
        messageService.sendKafkaMsg("app");
        return ResultUtil.success(true);
    }

    /**
     * 执行数据保存
     * @param appSysManagers
     * @param appRoleManages
     * @param appAccountManages
     * @param appResourceManages
     */
    private void batchSaveDatas(List<AppSysManager> appSysManagers, List<AppRoleManage> appRoleManages, List<AppAccountManage> appAccountManages, List<AppResourceManage> appResourceManages) {
        if(CollectionUtils.isNotEmpty(appSysManagers)){
            appSysManagerService.save(appSysManagers);
            // 清理历史关联数据,防止数据库脏数据
            deleteRefAppId(appSysManagers);
        }
        if(CollectionUtils.isNotEmpty(appRoleManages)){
            appRoleManageService.save(appRoleManages);
        }
        if(CollectionUtils.isNotEmpty(appAccountManages)){
            appAccountManageService.save(appAccountManages);
        }
        if(CollectionUtils.isNotEmpty(appResourceManages)){
            appResourceManageService.save(appResourceManages);
        }
        // 增加缓存 2022-08-09
        new  Thread(new Runnable() {
            @Override
            public void run() {
                baseDataRedisCacheService.addAppSysManagers(appSysManagers);
            }
        }).start();

    }

    private void deleteRefAppId(List<AppSysManager> appSysManagers) {
        List<Integer> appIds = appSysManagers.stream().map(item -> item.getId()).collect(Collectors.toList());
        appSysManagerService.deleteRefByAppIds(appIds);
    }


    @GetMapping("/getServerList/{id}")
    @ApiOperation(value="服务器列表数据",notes="")
    @SysRequestLog(description="服务器列表数据", actionType = ActionType.SELECT,manually=false)
    public Result<List<Map<String,Object>>> getServerList(@PathVariable("id") Integer id){
        List<Map<String,Object>> assetList=appSysManagerService.getServerList(id);
        return ResultUtil.success(assetList);

    }

    @GetMapping("/countServerSize/{id}")
    @ApiOperation(value="统计应用服务器数量",notes="")
    @SysRequestLog(description="统计应用服务器数量", actionType = ActionType.SELECT,manually=false)
    public Result<Integer> countServerSize(@PathVariable("id") Integer id){
        Integer size=0;
        AppSysManager appSysManager=appSysManagerService.getOne(id);
        String serverIds=appSysManager.getServiceId();
        if(!StringUtils.isBlank(serverIds)){
            size=serverIds.split(",").length;
        }
        return ResultUtil.success(size);

    }

    @GetMapping("countAppSys")
    @ApiOperation(value="统计应用数量",notes="")
    @SysRequestLog(description="统计应用数量", actionType = ActionType.SELECT,manually=false)
    public Result<Long> countAppSys(){
        Long appCount=appSysManagerService.count();
        return ResultUtil.success(appCount);
    }
    @Autowired
    private AppNetFlowAuditJob appNetFlowAuditJob;
    @PostMapping("test")
    @ApiOperation(value="统计应用数量",notes="")
    @SysRequestLog(description="统计应用数量", actionType = ActionType.SELECT,manually=false)
    public Result<Long> test() throws Exception {
        appNetFlowAuditJob.appNetFlowAuditTask();
        return ResultUtil.success(1L);
    }
    @GetMapping("getAllAppSys")
    @ApiOperation(value="获取所以应用系统",notes="")
    @SysRequestLog(description="获取所以应用系统", actionType = ActionType.SELECT,manually=false)
    public Result<List<AppSysManager>> getAllAppSys(){
        List<AppSysManager> all = appSysManagerService.findAll();
        return ResultUtil.successList(all);
    }
}
