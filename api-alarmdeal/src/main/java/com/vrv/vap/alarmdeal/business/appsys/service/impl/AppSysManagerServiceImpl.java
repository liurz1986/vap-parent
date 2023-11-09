package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AppSysManagerCacheVo;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.enums.AlarmDealStateEnum;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.SelfConcernAsset;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.AlarmEventManagementForESService;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.analysis.server.SelfConcernAssetService;
import com.vrv.vap.alarmdeal.business.appsys.dao.AppSysManagerDao;
import com.vrv.vap.alarmdeal.business.appsys.model.AppAccountManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppResourceManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppRoleManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.repository.AppSysManagerRepository;
import com.vrv.vap.alarmdeal.business.appsys.service.*;
import com.vrv.vap.alarmdeal.business.appsys.vo.*;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelUtils;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.impl.AssetServiceImpl;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetExportVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.common.model.User;
import com.vrv.vap.es.util.page.QueryCondition_ES;
import com.vrv.vap.exportAndImport.excel.exception.ExcelException;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author lps 2021/8/9
 */

@Service
@Transactional
public class AppSysManagerServiceImpl extends AbstractBaseServiceImpl<AppSysManager, Integer> implements AppSysManagerService {

    private static Logger logger = LoggerFactory.getLogger(AppSysManagerServiceImpl.class);

    @Autowired
    private AppSysManagerRepository appSysManagerRepository;

    @Autowired
    private AppSysManagerDao appSysManagerDao;
    @Autowired
    private ClassifiedLevelService classifiedLevelService;
    @Autowired
    private FeignService feignService;
    @Autowired
    private AppRoleManageService appRoleManageService;
    @Autowired
    private AppAccountManageService appAccountManageService;
    @Autowired
    private AppResourceManageService appResourceManageService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private AlarmEventManagementForESService alarmEventManagementForESService;
    @Autowired
    AlarmEventManagementForESService alarmEventManagementForEsService;
    @Autowired
    private SelfConcernAssetService selfConcernAssetService;

    @Autowired
    private FileConfiguration fileConfiguration;

    private Map<String, String> deptMap = new HashMap<>();

    private static String APP_ID = "appId";

    private List<BaseDictAll> classifiedLevels = null;

    private List<AppSysManager> apps = null;

    @Override
    public AppSysManagerRepository getRepository() {
        return appSysManagerRepository;
    }

    @Override
    public List<AppSysManagerCacheVo> getAppSysManagerList() {

        return appSysManagerDao.getAppSysManagerList();
    }

    /**
     * 应用系统分页查询
     *
     * @param appSysManagerQueryVo
     * @return
     */
    @Override
    public PageRes<AppSysManagerVo> getAppSysManagerPage(AppSysManagerQueryVo appSysManagerQueryVo) {
        PageRes<AppSysManagerVo> pageRes = appSysManagerDao.getAppSysManagerPage(appSysManagerQueryVo);
        List<AppSysManagerVo> list = pageRes.getList();
        for (AppSysManagerVo appSysManagerVo : list) {
            String serverId = appSysManagerVo.getServiceId();
            if (StringUtils.isNotBlank(serverId)) {
                appSysManagerVo.setServerCount(getServerCount(serverId));
            } else {
                appSysManagerVo.setServerCount(0);
            }

        }
        pageRes.setList(list);
        return pageRes;
    }

    @Override
    public PageRes<AppSysManagerVo> getAppSysManagerImgPage(AppSysManagerQueryVo appSysManagerQueryVo) {
        PageRes<AppSysManagerVo> pageRes = appSysManagerDao.getAppSysManagerImgPage(appSysManagerQueryVo);
        List<AppSysManagerVo> list = pageRes.getList();
        for (AppSysManagerVo appSysManagerVo : list) {
            List<QueryCondition> queryConditions = new ArrayList<>();
            queryConditions.add(QueryCondition.eq("appId", appSysManagerVo.getId()));
            List<AppAccountManage> all = appAccountManageService.findAll(queryConditions);
            if (all.size() > 0) {
                List<String> strings = all.stream().map(a -> a.getName()).collect(Collectors.toList());
                String join = StringUtils.join(strings, ",");
                appSysManagerVo.setPersonName(join);
            }
            //事件数
            Integer num = 0;
            if (StringUtils.isNotBlank(appSysManagerVo.getServiceId())) {
                List<QueryCondition> assetQ = new ArrayList<>();
                assetQ.add(QueryCondition.in("guid", Arrays.asList(appSysManagerVo.getServiceId().split(","))));
                List<Asset> assetServiceAll = assetService.findAll(assetQ);
                if (assetServiceAll.size() > 0) {
                    //补全资产ips
                    List<String> strings1 = assetServiceAll.stream().map(a -> a.getIp()).collect(Collectors.toList());
                    if (strings1.size()>0){
                        appSysManagerVo.setIps(StringUtils.join(strings1,","));
                    }
                    List<String> strings = assetServiceAll.stream().map(a -> a.getIp()).collect(Collectors.toList());
                    num = getEventCount(strings);
                }
            }
            appSysManagerVo.setCountEvent(num);
            //是否关注
            User currentUser = SessionUtil.getCurrentUser();
            Long countByUserIP = selfConcernAssetService.getCountByUserIP(String.valueOf(currentUser.getId()), 1, String.valueOf(appSysManagerVo.getId()));
            if (countByUserIP>0){
                appSysManagerVo.setIsJustAssetOfConcern(true);
            }
        }
        pageRes.setList(list);
        return pageRes;
    }

    @Override
    public List<NameValue> getAppAlarmEventTop10() {
        List<NameValue> nameValues = new ArrayList<>();
        List<AppSysManager> all = findAll();
        if (all.size() > 0) {
            List<String> guid = new ArrayList<>();
            for (AppSysManager appSysManager : all) {
                if (StringUtils.isNotBlank(appSysManager.getServiceId())){
                    guid.addAll(Arrays.asList(appSysManager.getServiceId().split(",")));
                }
            }
            List<QueryCondition> queryConditions = new ArrayList<>();
            if (guid.size()>0){
                queryConditions.add(QueryCondition.in("guid", guid));
            }else {
                queryConditions.add(QueryCondition.isNull("guid"));
            }
            List<Asset> assetServiceAll = assetService.findAll(queryConditions);
            if (assetServiceAll.size() > 0) {
                List<String> strings = assetServiceAll.stream().map(a -> a.getIp()).collect(Collectors.toList());
                List<QueryCondition_ES> querys = new ArrayList<>();
                querys.add(QueryCondition_ES.in("principalIp", strings));
                querys.add(QueryCondition_ES.notEq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
                Map<String, Long> principalIp = alarmEventManagementForEsService.getCountGroupNumByFieldSize(alarmEventManagementForEsService.getIndexName(), "principalIp", querys, 10);
                List<Map.Entry<String, Long>> list = new ArrayList<Map.Entry<String, Long>>(principalIp.entrySet());
                Collections.sort(list, new Comparator<Map.Entry<String, Long>>() {
                    //降序排序
                    @Override
                    public int compare(Map.Entry<String, Long> o1,
                                       Map.Entry<String, Long> o2) {
                        return o2.getValue().compareTo(o1.getValue());
                    }

                });
                for (Map.Entry<String, Long> entry : list) {
                    nameValues.add(new NameValue(entry.getValue().toString(), entry.getKey()));
                }
            }


        }
        return nameValues;
    }


    @Override
    public Result<String> exportNewAssetInfo(AppSysManagerQueryVo appSysManagerQueryVo) {
        String fileName = "应用系统信息" + com.vrv.vap.exportAndImport.excel.util.DateUtils.date2Str(new Date(), "yyyyMMddHHmmss");
        String rootPath = fileConfiguration.getAsset();
        File targetFile = new File(rootPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        String filePath = Paths.get(rootPath, fileName).toString();
        List<AppSysManagerVo> appSysManagerImgList = appSysManagerDao.getAppSysManagerImgList(appSysManagerQueryVo);
        List<AppSysManagerExportVo> assetExportVOS = new ArrayList<>();
        for (AppSysManagerVo appSysManagerVo : appSysManagerImgList) {
            List<QueryCondition> queryConditions = new ArrayList<>();
            queryConditions.add(QueryCondition.eq("appId", appSysManagerVo.getId()));
            List<AppAccountManage> all = appAccountManageService.findAll(queryConditions);
            if (all.size() > 0) {
                List<String> strings = all.stream().map(a -> a.getName()).collect(Collectors.toList());
                String join = StringUtils.join(strings, ",");
                appSysManagerVo.setPersonName(join);
            }
            //事件数
            Integer num = 0;
            if (StringUtils.isNotBlank(appSysManagerVo.getServiceId())) {
                List<QueryCondition> assetQ = new ArrayList<>();
                assetQ.add(QueryCondition.in("guid", Arrays.asList(appSysManagerVo.getServiceId().split(","))));
                List<Asset> assetServiceAll = assetService.findAll(assetQ);
                if (assetServiceAll.size() > 0) {
                    List<String> strings = assetServiceAll.stream().map(a -> a.getIp()).collect(Collectors.toList());
                    num = getEventCount(strings);
                }
            }
            appSysManagerVo.setCountEvent(num);
            AppSysManagerExportVo assetExportVO = new AppSysManagerExportVo();
            BeanUtil.copyProperties(appSysManagerVo, assetExportVO);
            if (appSysManagerVo != null && appSysManagerVo.getSecretLevel() != null) {
                switch (appSysManagerVo.getSecretLevel()) {
                    case "4":
                        assetExportVO.setSecretLevel("非密");
                        break;
                    case "3":
                        assetExportVO.setSecretLevel("内部");
                        break;
                    case "2":
                        assetExportVO.setSecretLevel("秘密");
                        break;
                    case "1":
                        assetExportVO.setSecretLevel("机密");
                        break;
                    case "0":
                        assetExportVO.setSecretLevel("绝密");
                        break;
                    default:
                        break;

                }
            }
//            assetExportVOS.add(assetExportVO);
        }
        try {
            ExportExcelUtils.getInstance().createExcel(assetExportVOS, AppSysManagerExportVo.class, filePath);
            return ResultUtil.success(fileName);
        } catch (ExcelException | IOException | NoSuchFieldException | IllegalAccessException e) {
            logger.error("导出excel异常", e);
        }
        return ResultUtil.error(-1, "导出excel异常");
    }


    private Integer getEventCount(List<String> strings) {
        List<QueryCondition_ES> querys = new ArrayList<>();
        querys.add(QueryCondition_ES.in("principalIp", strings));
        querys.add(QueryCondition_ES.notEq("alarmDealState", AlarmDealStateEnum.PROCESSED.getCode()));
        long count = alarmEventManagementForESService.count(querys);
        return Math.toIntExact(count);
    }

    /**
     * 服务器id去重处理,计算数量
     *
     * @param serverIds
     * @return
     */
    private int getServerCount(String serverIds) {
        String[] idArr = serverIds.split(",");
        List<String> idList = new ArrayList<>(Arrays.asList(idArr));
        Set<String> ids = new HashSet<>();
        for (int i = 0; i < idList.size(); i++) {
            ids.add(idList.get(i));
        }
        return ids.size();
    }

    /**
     * 批量存储
     *
     * @param list
     */
    @Override
    public void saveList(List<Map<String, Object>> list) {
        List<AppSysManager> appSysManager = new Gson().fromJson(JSON.toJSONString(list), new TypeToken<List<AppSysManagerVo>>() {
        }.getType());
        save(appSysManager);
    }


    /**
     * 查询应用系统信息
     *
     * @param id 应用系统id
     * @return
     */
    @Override
    public AppSysManagerVo queryOne(Integer id) {
        return appSysManagerDao.queryOne(id);
    }

    /**
     * 某应用服务器厂商分布数据、
     *
     * @param id 应用系统id
     * @return
     */
    @Override
    public Map<String, Object> countServerGroupByType(Integer id) {
        return appSysManagerDao.countServerGroupByType(id);
    }

    /**
     * 服务器列表数据
     *
     * @param id
     * @return
     */
    @Override
    public List<Map<String, Object>> getServerList(Integer id) {
        return appSysManagerDao.getServerList(id);
    }

    /**
     * 资产删除时，删除对应服务器数据
     */
    @Override
    public Boolean deleteAppServers(String guid) {
        List<QueryCondition> conditionList = new ArrayList<>();
        conditionList.add(QueryCondition.like("serviceId", guid));
        List<AppSysManager> appSysManagerList = findAll(conditionList);
        for (AppSysManager appSysManager : appSysManagerList) {
            String serverIds = appSysManager.getServiceId();
            serverIds = serverIds.replace(guid + ",", "");
            serverIds = serverIds.replace("," + guid, "");
            serverIds = serverIds.replace(guid, "");
            appSysManager.setServiceId(serverIds);
            save(appSysManager);
        }
        return true;
    }
    /**
     * * 资产批量删除时，删除对应服务器数据
     * @param guids 资产id
     * @return
     */
    @Override
    public Boolean batchDeleteAppServers(List<String> guids){
        for(String guid : guids){
            deleteAppServers(guid);
        }
        return true;
    }

    @Override
    protected void dataChangeSendMsg() {
    }

    /**
     * 获取应用系统当前最大的id
     *
     * @return
     */
    public int getCurrentMaxId() {
        return appSysManagerDao.getCurrentMaxId();
    }

    /**
     * 应用系统导入数据校验
     * @param file
     * @return
     */
    @Override
    public AppImportResultVO checkImportData(MultipartFile file) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        InputStream inputStream = null;
        AppImportResultVO appImportResultVO = null;
        try {
            //读取写excel数据
            inputStream = file.getInputStream();
            // 解析数据
            Map<String, Object> dataList = getAllDataList(inputStream);
            // 应用系统数据去重处理
            List<Map<String, Object>> repeatDatas = repeatHandle(dataList);
            initData();
            //excel数据校验:dataList为提出重复的数据
            appImportResultVO = checkData(dataList);
            // 存在重复校验数据，将结果加入最终校验结果中
            if (CollectionUtils.isNotEmpty(repeatDatas)) {
                List<Map<String, Object>> failDatas = appImportResultVO.getFalseList();
                failDatas.addAll(repeatDatas);
                appImportResultVO.setFalseList(failDatas);
            }
        } catch (Exception e) {
            logger.error("文件上传出现错误：", e);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return appImportResultVO;
    }

    @Override
    public void deleteData(String id) {
        this.delete(Integer.valueOf(id));
        // 删除关联的角色、账户、资源
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq("appId", Integer.valueOf(id)));
        List<AppAccountManage> accounts = appAccountManageService.findAll(conditions);
        if (CollectionUtils.isNotEmpty(accounts)) {
            appAccountManageService.deleteInBatch(accounts);
        }
        List<AppRoleManage> appRoleManages = appRoleManageService.findAll(conditions);
        if (CollectionUtils.isNotEmpty(appRoleManages)) {
            appRoleManageService.deleteInBatch(appRoleManages);
        }
        List<AppResourceManage> resourceManages = appResourceManageService.findAll(conditions);
        if (CollectionUtils.isNotEmpty(resourceManages)) {
            appResourceManageService.deleteInBatch(resourceManages);
        }
    }


    private void initData() {
        classifiedLevels = classifiedLevelService.getAppAll();
        apps = this.findAll();
        deptMap = feignService.getDeptMap();
    }

    /**
     * 解析数据
     * 1.应用系统信息sheet页中数据
     * 2. 应用账号sheet页中数据
     * 3. 应用角色sheet页中数据
     * 4. 应用资源sheet页中数据
     * 5. 过滤出应用系统信息对应的关联角色、账号、资源数据，非应用系统信息关联的角色、账号、资源数据抛弃掉
     *
     * 2023-08-08
     * @param inputStream
     * @return
     */
    private Map<String, Object> getAllDataList(InputStream inputStream) {
        Map<String, Object> dataList = new HashMap<>();
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
            Map<String, List<List<String>>> excelTitle = ImportExcelUtil.getExcelContent(workbook);
            // 获取excel中应用系统信息sheet页中数据
            List<List<String>> appSysManagerVoList = excelTitle.get(AppSysManagerVo.APP_SYS_MANAGER);
            List<Map<String, Object>> appSysManagers = listToMap(AppSysManagerVo.KEYS, appSysManagerVoList);
            List<String> appIdImports = getImportAppId(appSysManagers);
            // 获取excel中应用账号sheet页中数据
            List<List<String>> appAccountManageVoList = excelTitle.get(AppAccountManageVo.APP_ACCOUNT_MANAGE);
            List<Map<String, Object>> mapList2 = listToMap(AppAccountManageVo.KEYS, appAccountManageVoList);
            // 获取excel中应用角色sheet页中数据
            List<List<String>> appRoleManageVoList = excelTitle.get(AppRoleManageVo.APP_ROLE_MANAGE);
            List<Map<String, Object>> mapList3 = listToMap(AppRoleManageVo.KEYS, appRoleManageVoList);
            // 获取excel中应用资源sheet页中数据
            List<List<String>> appResourceManageVoList = excelTitle.get(AppResourceManageVo.APP_RESOURCE_MANAGE);
            List<Map<String, Object>> mapList4 = listToMap(AppResourceManageVo.KEYS, appResourceManageVoList);
            // 过滤出应用系统信息对应的关联角色、账号、资源数据，并且进行分组
            Map<String, List<Map<String, Object>>> appAccountManageMap = mapList2.stream().filter(item -> appIdImports.contains(item.get("appId"))).collect(Collectors.groupingBy((Map m) -> (String) m.get(APP_ID)));
            Map<String, List<Map<String, Object>>> appRoleManageMap = mapList3.stream().filter(item -> appIdImports.contains(item.get("appId"))).collect(Collectors.groupingBy((Map m) -> (String) m.get(APP_ID)));
            Map<String, List<Map<String, Object>>> appResourceManageMap = mapList4.stream().filter(item -> appIdImports.contains(item.get("appId"))).collect(Collectors.groupingBy((Map m) -> (String) m.get(APP_ID)));
            dataList.put("appSys",appSysManagers);
            dataList.put("appAccount",appAccountManageMap);
            dataList.put("appRole",appRoleManageMap);
            dataList.put("appResource",appResourceManageMap);
            return dataList;
        } catch (IOException e) {
            logger.error("应用系统导入数据解析异常: ", e);
            throw new AlarmDealException(-1, "应用系统导入数据解析异常");
        }
    }

    /**
     * 获取Excele中应用系统信息的中所有的应用系统Id的值，主要目的是为了获取关联的角色、账户、资源数据
     * @param dataList
     * @return
     */
    private List<String> getImportAppId(List<Map<String, Object>> dataList) {
        List<String> appIds = new ArrayList<>();
        for(Map<String,Object> map : dataList){
            String appId = String.valueOf(map.get("id"));
            if(!appIds.contains(appId)){
                appIds.add(appId);
            }
        }
        return appIds;
    }

    private List<Map<String, Object>> listToMap(String[] keys, List<List<String>> list) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (List<String> elementList : list) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], elementList.get(i));
            }
            dataList.add(map);
        }
        return dataList;
    }

    /**
     * 数据去重处理
     * 1. 应用系统： 应用id不能重复，它会关联角色、账号、资源；应用编号不能重复
     * @param dataList
     * @return
     */
    protected List<Map<String, Object>> repeatHandle(Map<String, Object> dataList) {
        List<Map<String, Object>> appSys =  (List<Map<String, Object>>)dataList.get("appSys");
        List<Map<String, Object>> repeatDatas = new ArrayList<Map<String, Object>>();
        if (null == dataList || dataList.size() == 0) {
            return null;
        }
        List<String> appNos = new ArrayList<String>();
        List<String> ids = new ArrayList<String>();
        for (Map<String, Object> data : appSys) {
            // 应用编号
            boolean status = false;
            Object appNoObj = data.get("appNo");
            if (!org.springframework.util.StringUtils.isEmpty(appNoObj)) {
                String appNo = String.valueOf(appNoObj);
                if (appNos.contains(appNo)) {
                    data.put("reason", "导入数据中应用编号重复:" + appNo);
                    repeatDatas.add(data);
                    status = true;
                } else {
                    appNos.add(appNo);
                }
            }
            // 应用系统id
            Object idObj = data.get("id");
            if (!org.springframework.util.StringUtils.isEmpty(idObj)) {
                String id = String.valueOf(idObj);
                if (ids.contains(id)) {
                    if (!status) {
                        data.put("reason", "导入数据中应用系统id重复:" + id);
                        repeatDatas.add(data);
                    }
                } else {
                    ids.add(id);
                }
            }
        }
        if (repeatDatas.size() > 0) {
            appSys.removeAll(repeatDatas);
        }
        return repeatDatas;
    }

    /**
     * 应用系统相关校验
     * 1. 应用系统信息校验
     * 2. 应用系统信息校验成功后，相关角色、账号、资源校验
     * @param dataList
     * @return
     */
    private AppImportResultVO checkData(Map<String, Object> dataList) {
        List<Map<String, Object>> appSys =  (List<Map<String, Object>>)dataList.get("appSys");
        AppImportResultVO result = new AppImportResultVO();
        List<Map<String, Object>> trueList = new ArrayList<>();
        List<Map<String, Object>> falseList = new ArrayList<>();
        Map<String, Map<String, List<Map<String, Object>>>> extendDatas = new HashMap<>();
        Map<String, List<Map<String, Object>>> appAccountManageMapSaves = new HashMap<>();
        Map<String, List<Map<String, Object>>> appRoleManageMapSaves = new HashMap<>();
        Map<String, List<Map<String, Object>>> appResourceManageMapSaves = new HashMap<>();
        if (CollectionUtils.isEmpty(appSys)) {
            return result;
        }
        for (Map<String, Object> map : appSys) {
            if (!checkColumn(map)) { // 应用系统校验
                falseList.add(map);
            } else {
                // 应用系统校验成功后，对应的账号，角色、资源校验
                relationDataCheck(falseList, trueList, map,dataList,appAccountManageMapSaves,appRoleManageMapSaves,appResourceManageMapSaves);
            }
        }
        extendDatas.put("appRole", appRoleManageMapSaves);
        extendDatas.put("appAccount", appAccountManageMapSaves);
        extendDatas.put("appResource", appResourceManageMapSaves);
        result.setTrueList(trueList);
        result.setFalseList(falseList);
        result.setRelations(extendDatas);
        return result;
    }

    /**
     * 应用系统校验成功后，对应的账号，角色、资源校验
     *
     * @param falseList
     * @param trueList
     * @return
     */
    private void relationDataCheck(List<Map<String, Object>> falseList, List<Map<String, Object>> trueList, Map<String, Object> map, Map<String, Object> dataList,Map<String,
            List<Map<String, Object>>> appAccountManageMapSaves,Map<String,List<Map<String, Object>>> appRoleManageMapSaves,Map<String,List<Map<String, Object>>> appResourceManageMapSaves) {
        String id = map.get("id").toString();
        Map<String, List<Map<String, Object>>> appAccount =  (Map<String, List<Map<String, Object>>>)dataList.get("appAccount");
        Map<String, List<Map<String, Object>>> appRole =  (Map<String, List<Map<String, Object>>>)dataList.get("appRole");
        Map<String, List<Map<String, Object>>> appResource =  (Map<String, List<Map<String, Object>>>)dataList.get("appResource");

        // 应用角色
        List<Map<String, Object>> roles = appRole.get(id);
        Map<String, Object> vResult = appRoleManageService.roleValidate(roles);
        if ("error".equals(vResult.get("status"))) {
            map.put("reason", "应用系统关联应用角色校验失败," + vResult.get("msg"));
            falseList.add(map);
            return;
        }
        List<String> roleNames = (List<String>) vResult.get("roles");
        // 应用账号（角色名称需要应用角色中角色名称）
        List<Map<String, Object>> accounts = appAccount.get(id);
        Map<String, Object> AResult = appAccountManageService.accountValidate(roleNames, accounts);
        if ("error".equals(AResult.get("status"))) {
            map.put("reason", "应用系统关联应用账号校验失败," + AResult.get("msg"));
            falseList.add(map);
            return;
        }
        // 应用资源
        List<Map<String, Object>> resources = appResource.get(id);
        Map<String, Object> result = appResourceManageService.resourceValidate(resources);
        if ("error".equals(result.get("status"))) {
            map.put("reason", "应用系统关联应用资源校验失败," + result.get("msg"));
            falseList.add(map);
            return;
        }
        appAccountManageMapSaves.put(id, accounts);
        appRoleManageMapSaves.put(id, roles);
        appResourceManageMapSaves.put(id, resources);
        trueList.add(map);
    }

    /**
     * 应用系统校验
     *
     * @param map
     * @return
     */
    private boolean checkColumn(Map<String, Object> map) {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (!validateData(key, map)) {
                return false;
            }
        }
        return true;

    }

    private boolean validateData(String key, Map<String, Object> map) {
        String value = map.get(key) == null ? "" : String.valueOf(map.get(key));
        Map<String, String> validateResult = isMust(key, value);
        // 必填校验
        if (!"success".equals(validateResult.get("status"))) {
            map.put("reason", validateResult.get("message"));
            return false;
        }
        // 有效性校验及转换
        Map<String, String> validateValidity = validateValidity(key, value, map);
        if (!"success".equals(validateValidity.get("status"))) {
            map.put("reason", validateValidity.get("message"));
            return false;
        }
        return true;
    }

    /**
     * 应用系统信息校验
     * 必填："appNo","appName","departmentName","secretLevel","secretCompany","domainName"
     * ,"应用编号", "应用名称", "单位名称", "涉密等级","涉密厂商","域名"
     *
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> isMust(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key) {
            case "appNo":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("应用编号:" + value + "不能为空");
                }
                break;
            case "appName":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("应用名称:" + value + "不能为空");
                }
                break;
            case "departmentName":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("单位名称:" + value + "不能为空");
                }
                break;
            case "secretLevel":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("涉密等级:" + value + "不能为空");
                }
                break;
            case "secretCompany":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("涉密厂商:" + value + "不能为空");
                }
                break;
            case "domainName":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("域名:" + value + "不能为空");
                }
                break;
            default:
                break;
        }
        result.put("status", "success");
        return result;
    }

    /**
     * 应用系统信息有效性校验
     * 有效性： "appNo","appName","departmentName","secretLevel","secretCompany","domainName"
     * "应用编号", "应用名称", "单位名称", "涉密等级","涉密厂商","域名"
     * appNo：重复校验
     * secretLevel : 有效性
     * domainName：格式
     * departmentName:有效性
     *
     * @param key
     * @param value
     * @param map
     * @return
     */
    private Map<String, String> validateValidity(String key, String value, Map<String, Object> map) {
        Map<String, String> result = new HashMap<>();
        switch (key) {
            case "appNo":
                if (appNoExist(value)) {
                    return returnEroorResult("应用编号:" + value + "重复");
                }
                break;
            case "departmentName":
                if (!deptMap.containsKey(value)) {
                    return returnEroorResult("单位名称:" + value + "不存在");
                }
                break;
            case "secretLevel":
                // 有效性
                String code = getCodeByValue(value.trim(), classifiedLevels);
                if (StringUtils.isBlank(code)) {
                    return returnEroorResult("涉密等级:" + value + "不存在");
                } else {
                    map.put(key, code); // 执行转换
                }
                break;
            case "domainName":
                if (StringUtils.isNotBlank(value) && !urlFormatValidate(value)) {
                    return returnEroorResult("域名:" + value + "格式错误");
                }
                break;
            default:
                break;
        }
        result.put("status", "success");
        return result;
    }

    private boolean appNoExist(String value) {
        if (CollectionUtils.isEmpty(apps)) {
            return false;
        }
        value = value.trim();
        for (AppSysManager app : apps) {
            if (value.equals(app.getAppNo().trim())) {
                return true;
            }
        }
        return false;

    }

    public String getCodeByValue(String value, List<BaseDictAll> datas) {
        for (BaseDictAll data : datas) {
            if (value.equalsIgnoreCase(data.getCodeValue())) {
                return data.getCode();
            }
        }
        return null;
    }
    @Override
    public void deleteRefByAppIds(List<Integer> appIds) {
        appSysManagerDao.deleteRefByAppIds(appIds);
    }
    /**
     * 应用系统统计---审批类型功能
     *
     * @date 2023-08
     * @return
     */
    @Override
    public Result<List<Map<String, Object>>> getAppsAuth() {
        return ResultUtil.successList(appSysManagerDao.getAppsAuth());
    }

}



