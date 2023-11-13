package com.vrv.vap.alarmdeal.business.baseauth.service.impl;



import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.model.InternetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.service.InternetInfoManageService;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelUtils;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthApp;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthOperation;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthPrintBurn;
import com.vrv.vap.alarmdeal.business.baseauth.service.*;
import com.vrv.vap.alarmdeal.business.baseauth.util.PValidUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthAppVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthInternetVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthPrintBurnVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthoOperationVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.export.*;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthAppQueryVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthInternetQueryVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthPrintBurnQueryVo;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.exportAndImport.excel.exception.ExcelException;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BaseAuthServiceImpl implements BaseAuthService {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthServiceImpl.class);
    @Autowired
    private BaseAuthPrintBurnService authPrintBurnService;
    private List<String> printIp = null;
    private List<String> brunIp = null;
    private List<String> assetIps = null;
    private List<String> appIps = null;
    private List<String> appName = null;
    private List<String> appNameAuth = null;
    private List<String> internetName = null;
    private List<String> internetNameAuth = null;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private AssetService assetService;
    @Autowired
    private BaseAuthAppService baseAuthAppService;
    @Autowired
    private AppSysManagerService appSysManagerService;
    @Autowired
    private InternetInfoManageService internetInfoManageService;
    @Autowired
    private BaseAuthInternetService baseAuthInternetService;
    @Autowired
    private BaseAuthOperationService baseAuthOperationService;
    @Autowired
    private FileConfiguration fileConfiguration;

    @Override
    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file, Integer code) {
        logger.info("审批信息导入数据校验开始");
        HSSFSheet sheet = null;

        try {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            sheet = workbook.getSheet(getNameInfoByCode(code));
        } catch (IOException e) {
            logger.error("IOException: {}", e);
            return null;
        }
        if (null == sheet) {
            logger.error("导入数据为空，当前文件sheet," + getNameInfoByCode(code) + "不存在");
            throw new AlarmDealException(-1, "当前sheet页不存在," + getNameInfoByCode(code));
        }
        // 初始化数据
        initData(code);
        List<List<String>> excelContent = ImportExcelUtil.getExcelContent(sheet);
        // 数据组装
        List<Map<String, Object>> datas = getAssembleData(excelContent, getKeyInfoByCode(code));
        // 数据去重处理
        List<Map<String, Object>> repeatDatas = new ArrayList<>();
        if (code == 5) {
            repeatDatas = repeatHandleCode5(datas, getCheakName(code));
        } else {
            repeatDatas = repeatHandle(datas, getCheakName(code));
        }
        // 数据校验
        Map<String, List<Map<String, Object>>> result = checkData(datas, code);
        // 存在重复校验数据，将结果加入最终校验结果中
        if (null != repeatDatas) {
            List<Map<String, Object>> failDatas = result.get("false");
            failDatas.addAll(repeatDatas);
            result.put("false", failDatas);
        }
        return result;
    }


    @Override
    public void saveList(Map<String, Object> map) {
        Integer code = (Integer) map.get("code");
        List<Map<String, Object>> list = (List<Map<String, Object>>) map.get("data");
        List<BaseAuthPrintBurn> datas = new ArrayList<>();
        List<BaseAuthOperation> baseAuthOperations = new ArrayList<>();
        switch (code) {
            case 1:
                for (Map<String, Object> objectMap : list) {
                    BaseAuthPrintBurn baseAuthPrintBurn = mapperUtil.map(objectMap, BaseAuthPrintBurn.class);
                    String decideCN = objectMap.get("decideCN").toString();
                    if (decideCN.equals("是")) {
                        baseAuthPrintBurn.setDecide(0);
                    } else {
                        baseAuthPrintBurn.setDecide(1);
                    }
                    baseAuthPrintBurn.setCreateTime(new Date());
                    baseAuthPrintBurn.setType(1);
                    datas.add(baseAuthPrintBurn);
                }
                authPrintBurnService.save(datas);
                break;
            case 2:
                for (Map<String, Object> objectMap : list) {
                    BaseAuthPrintBurn baseAuthPrintBurn = mapperUtil.map(objectMap, BaseAuthPrintBurn.class);
                    String decideCN = objectMap.get("decideCN").toString();
                    if (decideCN.equals("是")) {
                        baseAuthPrintBurn.setDecide(0);
                    } else {
                        baseAuthPrintBurn.setDecide(1);
                    }
                    baseAuthPrintBurn.setCreateTime(new Date());
                    baseAuthPrintBurn.setType(2);
                    datas.add(baseAuthPrintBurn);
                }
                authPrintBurnService.save(datas);
                break;
            case 5:
                for (Map<String, Object> objectMap : list) {
                    BaseAuthOperation baseAuthPrintBurn = mapperUtil.map(objectMap, BaseAuthOperation.class);
                    if (objectMap.get("assetType").toString().equals("应用系统")){
                        baseAuthPrintBurn.setType(1);
                    }else {
                        baseAuthPrintBurn.setType(0);
                    }
                    baseAuthPrintBurn.setCreateTime(new Date());
                    baseAuthOperations.add(baseAuthPrintBurn);
                }
                baseAuthOperationService.save(baseAuthOperations);
            case 3:
                for (Map<String, Object> objectMap : list) {
                    BaseAuthAppVo baseAuthApp = mapperUtil.map(objectMap, BaseAuthAppVo.class);
                    baseAuthAppService.addAuthAppByName(baseAuthApp);
                }
                // 必填
            case 4:
                for (Map<String, Object> objectMap : list) {
                    BaseAuthInternetVo baseAuthInternet = mapperUtil.map(objectMap, BaseAuthInternetVo.class);
                    baseAuthInternetService.addAuthInterneByName(baseAuthInternet);
                }
            default:
                break;
        }
    }

    @Override
    public Result<String> exportInfo(Map<String, Object> map) {
        String responsibleName = map.get("responsibleName").toString();
        String ip = map.get("ip").toString();
        Integer code = (Integer) map.get("code");
        String fileName =getFileNameBycode(code);
        String rootPath = fileConfiguration.getAsset();
        File targetFile = new File(rootPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        String filePath = Paths.get(rootPath, fileName).toString();
        switch (code) {
            case 1:
                try {
                    BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo = new BaseAuthPrintBurnQueryVo();
                    baseAuthPrintBurnQueryVo.setCount_(100000);
                    baseAuthPrintBurnQueryVo.setType(1);
                    if (StringUtils.isNotBlank(ip)) {
                        baseAuthPrintBurnQueryVo.setIp(ip);
                    }
                    if (StringUtils.isNotBlank(responsibleName)) {
                        baseAuthPrintBurnQueryVo.setResponsibleName(responsibleName);
                    }
                    PageRes<BaseAuthPrintBurnVo> pager = authPrintBurnService.getPager(baseAuthPrintBurnQueryVo);
                    List<BaseAuthPrintBurnVo> list = pager.getList();
                    List<BaseAuthPrintExport> baseAuthPrintExports = new ArrayList<>();
                    if (list.size() > 0) {
                        for (BaseAuthPrintBurnVo baseAuthPrintBurnVo : list) {
                            BaseAuthPrintExport baseAuthPrintExport = new BaseAuthPrintExport();
                            BeanUtils.copyProperties(baseAuthPrintBurnVo, baseAuthPrintExport);
                            if (baseAuthPrintBurnVo.getDecide() == 0) {
                                baseAuthPrintExport.setDecideCN("是");
                            } else {
                                baseAuthPrintExport.setDecideCN("否");
                            }
                            baseAuthPrintExports.add(baseAuthPrintExport);
                        }
                    }
                    ExportExcelUtils.getInstance().createExcel(baseAuthPrintExports, BaseAuthPrintExport.class, filePath);
                    return ResultUtil.success(fileName);
                } catch (ExcelException | IOException | NoSuchFieldException | IllegalAccessException e) {
                    logger.error("导出excel异常", e);
                }
                break;
            case 2:
                try {
                    BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo = new BaseAuthPrintBurnQueryVo();
                    baseAuthPrintBurnQueryVo.setCount_(100000);
                    baseAuthPrintBurnQueryVo.setType(2);
                    if (StringUtils.isNotBlank(ip)) {
                        baseAuthPrintBurnQueryVo.setIp(ip);
                    }
                    if (StringUtils.isNotBlank(responsibleName)) {
                        baseAuthPrintBurnQueryVo.setResponsibleName(responsibleName);
                    }
                    PageRes<BaseAuthPrintBurnVo> pager = authPrintBurnService.getPager(baseAuthPrintBurnQueryVo);
                    List<BaseAuthPrintBurnVo> list = pager.getList();
                    List<BaseAuthBurnExport> baseAuthPrintExports = new ArrayList<>();
                    if (list.size() > 0) {
                        for (BaseAuthPrintBurnVo baseAuthPrintBurnVo : list) {
                            BaseAuthBurnExport baseAuthPrintExport = new BaseAuthBurnExport();
                            BeanUtils.copyProperties(baseAuthPrintBurnVo, baseAuthPrintExport);
                            if (baseAuthPrintBurnVo.getDecide() == 0) {
                                baseAuthPrintExport.setDecideCN("是");
                            } else {
                                baseAuthPrintExport.setDecideCN("否");
                            }
                            baseAuthPrintExports.add(baseAuthPrintExport);
                        }
                    }
                    ExportExcelUtils.getInstance().createExcel(baseAuthPrintExports, BaseAuthBurnExport.class, filePath);
                    return ResultUtil.success(fileName);
                } catch (ExcelException | IOException | NoSuchFieldException | IllegalAccessException e) {
                    logger.error("导出excel异常", e);
                }
                break;
            case 3:
                try {
                    BaseAuthAppQueryVo baseAuthAppQueryVo = new BaseAuthAppQueryVo();
                    baseAuthAppQueryVo.setCount_(100000);
                    if (StringUtils.isNotBlank(ip)) {
                        baseAuthAppQueryVo.setIp(ip);
                    }
                    PageRes<BaseAuthAppVo> pager = baseAuthAppService.getPager(baseAuthAppQueryVo);
                    List<BaseAuthAppExport> baseAuthAppExports = new ArrayList<>();
                    List<BaseAuthAppVo> list = pager.getList();
                    if (list.size() > 0) {
                        for (BaseAuthAppVo baseAuthAppVo : list) {
                            BaseAuthAppExport baseAuthAppExport = new BaseAuthAppExport();
                            BeanUtils.copyProperties(baseAuthAppVo, baseAuthAppExport);
                            baseAuthAppExports.add(baseAuthAppExport);
                        }
                    }
                    ExportExcelUtils.getInstance().createExcel(baseAuthAppExports, BaseAuthAppExport.class, filePath);
                    return ResultUtil.success(fileName);
                } catch (ExcelException | IOException | NoSuchFieldException | IllegalAccessException e) {
                    logger.error("导出excel异常", e);
                }
                // 必填
                break;
            case 4:
                try {
                    BaseAuthInternetQueryVo baseAuthInternetQueryVo = new BaseAuthInternetQueryVo();
                    baseAuthInternetQueryVo.setCount_(100000);
                    if (StringUtils.isNotBlank(ip)) {
                        baseAuthInternetQueryVo.setIp(ip);
                    }
                    PageRes<BaseAuthInternetVo> pageRes = baseAuthInternetService.intPage(baseAuthInternetQueryVo);
                    List<BaseAuthInternetExport> baseAuthAppExports = new ArrayList<>();
                    List<BaseAuthInternetVo> list = pageRes.getList();
                    if (list.size() > 0) {
                        for (BaseAuthInternetVo baseAuthAppVo : list) {
                            BaseAuthInternetExport baseAuthInternetExport = new BaseAuthInternetExport();
                            BeanUtils.copyProperties(baseAuthAppVo, baseAuthInternetExport);
                            baseAuthAppExports.add(baseAuthInternetExport);
                        }
                    }
                    ExportExcelUtils.getInstance().createExcel(baseAuthAppExports, BaseAuthInternetExport.class, filePath);
                    return ResultUtil.success(fileName);
                } catch (ExcelException | IOException | NoSuchFieldException | IllegalAccessException e) {
                    logger.error("导出excel异常", e);
                }
                break;
            case 5:
                try {
                    BaseAuthInternetQueryVo baseAuthInternetQueryVo = new BaseAuthInternetQueryVo();
                    baseAuthInternetQueryVo.setCount_(100000);
                    if (StringUtils.isNotBlank(ip)) {
                        baseAuthInternetQueryVo.setIp(ip);
                    }
                    PageRes<BaseAuthoOperationVo> pageRes = baseAuthOperationService.operationPage(baseAuthInternetQueryVo);
                    List<BaseAuthOperationExport> baseAuthOperationExports = new ArrayList<>();
                    List<BaseAuthoOperationVo> list = pageRes.getList();
                    if (list.size() > 0) {
                        for (BaseAuthoOperationVo baseAuthAppVo : list) {
                            BaseAuthOperationExport baseAuthInternetExport = new BaseAuthOperationExport();
                            BeanUtils.copyProperties(baseAuthAppVo, baseAuthInternetExport);
                            baseAuthOperationExports.add(baseAuthInternetExport);
                        }
                    }
                    ExportExcelUtils.getInstance().createExcel(baseAuthOperationExports, BaseAuthOperationExport.class, filePath);
                    return ResultUtil.success(fileName);
                } catch (ExcelException | IOException | NoSuchFieldException | IllegalAccessException e) {
                    logger.error("导出excel异常", e);
                }
                break;
            default:
                break;
        }
        return null;
    }

    private String getFileNameBycode(Integer code) {
        String fileName = "";

        if (code == 1 ) {
            fileName="打印权限审批信息";
        }
        if (code == 2 ) {
            fileName = "刻录权限审批信息";
        }
        if (code == 3) {
            fileName="应用访问权限审批信息";
        }
        if (code == 4) {
            fileName="网络互联权限审批信息";
        }
        if (code == 5) {
            fileName="运维权限审批信息";
        }
        return fileName+com.vrv.vap.exportAndImport.excel.util.DateUtils.date2Str(new Date(), "yyyyMMddHHmmss");
    }

    private void initData(Integer code) {
        if (code == 1 || code == 2) {
            printIp = getPrintIps();
            brunIp = getBrunIps();
            assetIps = getAssetAllIps();
        }
        if (code == 3) {
            appName = getAppName();
            appNameAuth = getAppNameAuth();
        }
        if (code == 4) {
            internetName = getInternetName();
            internetNameAuth = getinternetNameAuth();
        }
        if (code == 5) {
            assetIps = getAssetAllIps();
            appIps = getAppIps();
        }
    }

    private List<String> getinternetNameAuth() {
        List<Integer> internetIdAuth = getInternetIdAuth();
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.in("id", internetIdAuth));
        List<InternetInfoManage> all = internetInfoManageService.findAll(queryConditions);
        if (all.size() > 0) {
            return all.stream().map(a -> a.getInternetName()).collect(Collectors.toList());
        }
        return null;
    }

    private List<String> getInternetName() {
        List<InternetInfoManage> all = internetInfoManageService.findAll();
        if (all.size() > 0) {
            return all.stream().map(a -> a.getInternetName()).collect(Collectors.toList());
        }
        return null;
    }

    private List<String> getAppNameAuth() {
        List<BaseAuthApp> all = baseAuthAppService.findAll();
        List<Integer> collect = all.stream().map(a -> a.getAppId()).collect(Collectors.toList());
        if (collect.size() > 0) {
            List<QueryCondition> queryConditions = new ArrayList<>();
            queryConditions.add(QueryCondition.in("id", collect));
            List<AppSysManager> appSysManagers = appSysManagerService.findAll(queryConditions);
            if (appSysManagers.size() > 0) {
                return appSysManagers.stream().map(q -> q.getAppName()).collect(Collectors.toList());
            }
        }
        return null;
    }

    private List<String> getAppName() {
        String sql = "select app_name from app_sys_manager ";
        return jdbcTemplate.queryForList(sql, String.class);
    }
    private List<String> getAppIps() {
        String sql = "select domain_name from app_sys_manager ";
        return jdbcTemplate.queryForList(sql, String.class);
    }
    private List<Integer> getInternetIdAuth() {
        String sql = "select internet_id from base_auth_internet  GROUP BY internet_id";
        return jdbcTemplate.queryForList(sql, Integer.class);
    }

    private List<String> getAssetAllIps() {
        String sql = "select ip from asset where IP is not null;";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    private List<String> getPrintIps() {
        String sql = "select ip from base_auth_print_burn where type=1;";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    private List<String> getBrunIps() {
        String sql = "select ip from base_auth_print_burn where type=1;";
        return jdbcTemplate.queryForList(sql, String.class);
    }

    private Map<String, List<Map<String, Object>>> checkData(List<Map<String, Object>> datas, Integer code) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        List<Map<String, Object>> trueList = new ArrayList<>();
        List<Map<String, Object>> falseList = new ArrayList<>();
        for (Map<String, Object> map : datas) {
            if (!checkColumn(map, code)) {
                falseList.add(map);
            } else {
                trueList.add(map);
            }
        }
        result.put("true", trueList);
        result.put("false", falseList);
        return result;
    }

    private boolean checkColumn(Map<String, Object> map, Integer code) {
        Set<String> keys = map.keySet();
        for (String key : keys) {
            if (!validateData(key, map, code)) {
                return false;
            }
        }
        return true;

    }

    private boolean validateData(String key, Map<String, Object> map, Integer code) {
        String value = map.get(key) == null ? "" : String.valueOf(map.get(key));
        Map<String, String> validateResult = isMust(key, value);
        // 必填校验
        if (!"success".equals(validateResult.get("status"))) {
            map.put("reason", validateResult.get("message"));
            return false;
        }
        // 有效性校验及转换
        Map<String, String> validateValidity = validateValidity(key, value, map, code);
        if (!"success".equals(validateValidity.get("status"))) {
            map.put("reason", validateValidity.get("message"));
            return false;
        }
        return true;
    }

    private Map<String, String> validateValidity(String key, String value, Map<String, Object> map, Integer code) {
        Map<String, String> result = new HashMap<>();
        switch (key) {
            case "ip":
                if (code == 1) {
                    if (isExistPrintIp(value)) {
                        return returnEroorResult("设备ip:" + value + "已存在打印审批信息");
                    }
                }
                if (code == 2) {
                    if (isExistBrunIp(value)) {
                        return returnEroorResult("设备ip:" + value + "已存在刻录审批信息");
                    }
                }
                if (checkAssetIp(value)) {
                    return returnEroorResult("设备ip:" + value + "在资产信息不存在");
                }
                if (!PValidUtil.isIPValid(value)) {
                    return returnEroorResult("ip:" + value + "格式错误");
                }
                break;
            case "dstIp":
                if (map.get("assetType").toString().equals("应用系统")){
                    if (checkAssetAppIp(value)) {
                        return returnEroorResult("设备ip:" + value + "在应用系统信息不存在");
                    }
                }else {
                    if (checkAssetIp(value)) {
                        return returnEroorResult("设备ip:" + value + "在资产信息不存在");
                    }
                }
                if (!PValidUtil.isIPValid(value)) {
                    return returnEroorResult("ip:" + value + "格式错误");
                }
                if (checkAuthOPData(value, map.get("ip"),map.get("assetType"))) {
                    return returnEroorResult("系统已存在相同运维审批信息");
                }
                break;
            case "appName":
                if (code == 3) {
                    if (isExistAppName(value)) {
                        return returnEroorResult("应用系统不存在:" + value);
                    }
                    if (isExistAppNameAuth(value)) {
                        return returnEroorResult("应用系统:" + value + "已存在审批授权信息");
                    }
                }
                break;
            case "insideIp":
                if (code == 3) {
                    if (!PValidUtil.isIPValid(value)) {
                        return returnEroorResult("内部授权ip:" + value + "格式错误");
                    }
                    if (PValidUtil.hasDuplicate(value)) {
                        return returnEroorResult("内部授权ip:" + value + "存在重复ip");
                    }
                }
                break;
            case "outIp":
                if (code == 3) {
                    if (!PValidUtil.isIPValid(value)) {
                        return returnEroorResult("外部授权ip:" + value + "格式错误");
                    }
                    if (PValidUtil.hasDuplicate(value)) {
                        return returnEroorResult("外部授权ip:" + value + "存在重复ip");
                    }
                }
                break;
            case "internetName":
                if (code == 4) {
                    if (isExistInternetName(value)) {
                        return returnEroorResult("互联单位名称:" + value + "不存在");
                    }
                    if (isExistInternetNameAuth(value)) {
                        return returnEroorResult("该互联单位:" + value + "已存在审批信息");
                    }
                }
                break;
            case "ips":
                if (code == 4) {
                    if (!PValidUtil.isIPValid(value)) {
                        return returnEroorResult("允许接入设备ip:" + value + "格式错误");
                    }
                    if (PValidUtil.hasDuplicate(value)) {
                        return returnEroorResult("允许接入设备ip:" + value + "存在重复ip");
                    }
                }
                break;
            default:
                break;
        }
        result.put("status", "success");
        return result;
    }

    private boolean checkAssetAppIp(String value) {
        if (CollectionUtils.isEmpty(assetIps)) {
            return true;
        }
        if (appIps.contains(value)) {
            return false;
        }
        return true;
    }

    private boolean checkAuthOPData(String value, Object ip,Object type) {
        Integer assetType=0;
        if (type.toString().equals("应用系统")){
            assetType=1;
        }
        List<QueryCondition> queryConditions = new ArrayList<>();
        queryConditions.add(QueryCondition.eq("ip", ip.toString()));
        queryConditions.add(QueryCondition.eq("dstIp", value));
        queryConditions.add(QueryCondition.eq("type", assetType));
        long count = baseAuthOperationService.count(queryConditions);
        if (count > 0) {
            return true;
        }
        return false;
    }

    private boolean isExistInternetNameAuth(String value) {
        if (CollectionUtils.isEmpty(internetNameAuth)) {
            return false;
        }
        if (internetNameAuth.contains(value)) {
            return true;
        }
        return false;
    }

    private boolean isExistInternetName(String value) {
        if (CollectionUtils.isEmpty(internetName)) {
            return true;
        }
        if (internetName.contains(value)) {
            return false;
        }
        return true;
    }

    private boolean isExistAppNameAuth(String value) {
        if (CollectionUtils.isEmpty(appNameAuth)) {
            return false;
        }
        if (appNameAuth.contains(value)) {
            return true;
        }
        return false;
    }

    private boolean isExistAppName(String value) {
        if (CollectionUtils.isEmpty(appName)) {
            return true;
        }
        if (appName.contains(value)) {
            return false;
        }
        return true;
    }

    private boolean checkAssetIp(String value) {
        if (CollectionUtils.isEmpty(assetIps)) {
            return true;
        }
        if (assetIps.contains(value)) {
            return false;
        }
        return true;
    }

    private boolean isExistPrintIp(String value) {
        if (CollectionUtils.isEmpty(printIp)) {
            return false;
        }
        if (printIp.contains(value)) {
            return true;
        }
        return false;
    }

    private boolean isExistBrunIp(String value) {
        if (CollectionUtils.isEmpty(brunIp)) {
            return false;
        }
        if (brunIp.contains(value)) {
            return true;
        }
        return false;
    }

    private Map<String, String> isMust(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key) {
            case "ip":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("ip:" + value + "不能为空");
                }
                break;
            case "decideCN":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("是否允许:" + value + "不能为空");
                }
                break;
            case "assetType":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("资产类型:" + value + "不能为空");
                }
                break;
            case "appName":
                // 必填
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("应用系统名称:" + value + "不能为空");
                }
                break;
            case "insideIp":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("内部授权ip:" + value + "不能为空");
                }
                break;

            case "outIp":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("外部授权ip:" + value + "不能为空");
                }
                break;
            case "name":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("互联网络名称:" + value + "不能为空");
                }
                break;
            case "internetName":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("互联单位名称:" + value + "不能为空");
                }
                break;
            case "ips":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("允许接入设备ip:" + value + "不能为空");
                }
                break;
            case "dstIp":
                if (StringUtils.isBlank(value)) {
                    return returnEroorResult("运维对象ip:" + value + "不能为空");
                }
                break;
            default:
                break;
        }
        result.put("status", "success");
        return result;
    }

    public Map<String, String> returnEroorResult(String message) {
        Map<String, String> result = new HashMap<>();
        result.put("message", message);
        result.put("status", "error");
        return result;
    }

    private String getCheakName(Integer code) {
        switch (code) {
            case 1:
            case 2:
                return "ip";
            case 3:
                // 必填
                return "appName";
            case 4:
                return "internetName";
            case 5:
                return "ip,dstIp";
            default:
                break;
        }
        return null;
    }

    protected List<Map<String, Object>> repeatHandle(List<Map<String, Object>> dataList, String name) {
        List<Map<String, Object>> repeatDatas = new ArrayList<Map<String, Object>>();
        if (null == dataList || dataList.size() == 0) {
            return null;
        }
        List<String> appNos = new ArrayList<String>();
        for (Map<String, Object> data : dataList) {
            Object internetNameObj = data.get(name);
            if (org.springframework.util.StringUtils.isEmpty(internetNameObj)) {
                continue;
            }
            String internetName = String.valueOf(internetNameObj);
            if (appNos.contains(internetName)) {
                data.put("reason", "导入数据重复");
                repeatDatas.add(data);
            } else {
                appNos.add(internetName);
            }
        }
        if (repeatDatas.size() > 0) {
            dataList.removeAll(repeatDatas);
        }
        return repeatDatas;
    }

    private List<Map<String, Object>> repeatHandleCode5(List<Map<String, Object>> dataList, String cheakName) {
        String[] split = cheakName.split(",");
        List<Map<String, Object>> repeatDatas = new ArrayList<Map<String, Object>>();
        if (null == dataList || dataList.size() == 0) {
            return null;
        }
        List<String> appNos = new ArrayList<String>();
        for (Map<String, Object> data : dataList) {
            String internetNameObj0 = data.get(split[0]).toString();
            String internetNameObj1 = data.get(split[1]).toString();
            if (StringUtils.isEmpty(internetNameObj0) || StringUtils.isEmpty(internetNameObj1)) {
                continue;
            }
            String internetName = internetNameObj0 + internetNameObj1;
            if (appNos.contains(internetName)) {
                data.put("reason", "导入数据重复");
                repeatDatas.add(data);
            } else {
                appNos.add(internetNameObj0 + internetNameObj1);
            }
        }
        if (repeatDatas.size() > 0) {
            dataList.removeAll(repeatDatas);
        }
        return repeatDatas;
    }

    private String getNameInfoByCode(Integer code) {
        switch (code) {
            case 1:
                return BaseAuthPrintBurnVo.PRINT_INFO_MANAGE;
            case 2:
                return BaseAuthPrintBurnVo.BURN_INFO_MANAGE;
            case 3:
                // 必填
                return BaseAuthAppVo.INFO_MANAGE;

            case 4:
                return BaseAuthInternetVo.INFO_MANAGE;
            case 5:
                return BaseAuthoOperationVo.INFO_MANAGE;
            default:
                break;
        }
        return null;
    }

    private String[] getKeyInfoByCode(Integer code) {
        switch (code) {
            case 1:
                return BaseAuthPrintBurnVo.KEYS;
            case 2:
                return BaseAuthPrintBurnVo.KEYS;
            case 3:
                // 必填
                return BaseAuthAppVo.KEYS;

            case 4:
                return BaseAuthInternetVo.KEYS;
            case 5:
                return BaseAuthoOperationVo.KEYS;
            default:
                break;
        }
        return null;
    }

    private List<Map<String, Object>> getAssembleData(List<List<String>> excelContent, String[] keys) {
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        for (List<String> data : excelContent) {
            Map<String, Object> map = new HashMap<>();
            for (int i = 0; i < keys.length; i++) {
                map.put(keys[i], data.get(i));
            }
            dataList.add(map);
        }
        return dataList;
    }

}
