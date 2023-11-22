package com.vrv.vap.alarmdeal.business.baseauth.service.impl;



import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.QueueUtil;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.model.InternetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.service.InternetInfoManageService;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelUtils;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.business.baseauth.dao.BaseAuthOverviewV2Dao;
import com.vrv.vap.alarmdeal.business.baseauth.enums.BaseAuthEnum;
import com.vrv.vap.alarmdeal.business.baseauth.enums.OptEnum;
import com.vrv.vap.alarmdeal.business.baseauth.model.*;
import com.vrv.vap.alarmdeal.business.baseauth.service.*;
import com.vrv.vap.alarmdeal.business.baseauth.util.BaseAuthUtil;
import com.vrv.vap.alarmdeal.business.baseauth.util.PValidUtil;
import com.vrv.vap.alarmdeal.business.baseauth.vo.*;
import com.vrv.vap.alarmdeal.business.baseauth.vo.export.*;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthAppQueryVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthInternetQueryVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthPrintBurnQueryVo;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.exportAndImport.excel.exception.ExcelException;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections.ArrayStack;
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
import java.text.ParseException;
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
    @Autowired
    private BaseAuthConfigService baseAuthConfigService;
    @Autowired
    private BaseAuthOverviewV2Dao baseAuthOverviewV2Dao;
    @Autowired
    private MapperUtil mapper;

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
        try {
            QueueUtil.putAuth(code);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Result<String> exportInfo(Map<String, Object> map) {
        Object responsibleNameO = map.get("responsibleName");
        Object ipO = map.get("ip");
        String responsibleName="";
        String ip="";
        if (responsibleNameO!=null){
            responsibleName=responsibleNameO.toString();
        }
        if (ipO!=null){
            ip=ipO.toString();
        }
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
                    if (list==null){
                        return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导出数据为空，无法导出");
                    }
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
                    if (list==null){
                        return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导出数据为空，无法导出");
                    }
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
                    if (list==null){
                        return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导出数据为空，无法导出");
                    }
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
                    if (list==null){
                        return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导出数据为空，无法导出");
                    }
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
                    if (list==null){
                        return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"导出数据为空，无法导出");
                    }
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

    @Override
    public void dealData(List<Integer> saveList) {
        for (Integer integer:saveList){
            BaseAuthEnum baseAuthEnumByCode = BaseAuthEnum.getBaseAuthEnumByCode(integer);
            Integer baseAuthType = baseAuthEnumByCode.getBaseAuthType();
            Integer opt = baseAuthEnumByCode.getOpt();
            logger.info("授权数据变动：{}", baseAuthEnumByCode.getName());
            if (integer==1 ||integer==2){
                List<QueryCondition> queryConditions=new ArrayList<>();
                queryConditions.add(QueryCondition.eq("type",integer));
                queryConditions.add(QueryCondition.eq("decide",0));
                List<BaseAuthPrintBurn> all = authPrintBurnService.findAll(queryConditions);
                if (all.size()>0){
                    List<String> strings = all.stream().map(a -> a.getIp()).collect(Collectors.toList());
                    List<Asset> assets=getAssetByIps(strings);
                    if (assets.size()>0){
                        List<BaseAuthConfig> baseAuthConfigs=new ArrayList<>();
                        for (BaseAuthPrintBurn baseAuthPrintBurn:all){
                            List<Asset> assetList = assets.stream().filter(a -> a.getIp().equals(baseAuthPrintBurn.getIp())).collect(Collectors.toList());
                            if (assetList.size()>0){
                                Asset asset = assetList.get(0);
                                BaseAuthConfig baseAuthConfig=new BaseAuthConfig();
                                baseAuthConfig.setCreateTime(baseAuthPrintBurn.getCreateTime());
                                baseAuthConfig.setSrcObj(asset.getResponsibleCode());
                                baseAuthConfig.setSrcObjLabel(asset.getResponsibleName());
                                baseAuthConfig.setDstObj(asset.getIp());
                                baseAuthConfig.setDstObjLabel(asset.getName());
                                baseAuthConfig.setTypeId(baseAuthType);
                                baseAuthConfig.setOpt(opt);
                                baseAuthConfigs.add(baseAuthConfig);
                            }
                        }
                        deleteBaseAuthConfigByType(baseAuthType);
                        baseAuthConfigService.save(baseAuthConfigs);
                    }
                }
            }
            if (integer==3){
                List<BaseAuthApp> all = baseAuthAppService.findAll();
                List<BaseAuthApp> in = all.stream().filter(m -> m.getType().equals(0)).collect(Collectors.toList());
                List<BaseAuthApp> out = all.stream().filter(m -> m.getType().equals(1)).collect(Collectors.toList());
                if (in.size()>0){
                    //内部设备访问应用系统
                    List<BaseAuthConfig> baseAuthConfigs=new ArrayList<>();
                    for (BaseAuthApp baseAuthApp:in){
                        Asset asset=getAssetByIp(baseAuthApp.getIp());
                        AppSysManager appSysManager = appSysManagerService.getOne(baseAuthApp.getAppId());
                        if (appSysManager!=null){
                            BaseAuthConfig baseAuthConfig=new BaseAuthConfig();
                            baseAuthConfig.setCreateTime(baseAuthApp.getCreateTime());
                            baseAuthConfig.setSrcObj(baseAuthApp.getIp());
                            if (asset!=null){
                                baseAuthConfig.setSrcObjLabel(asset.getName());
                            }
                            baseAuthConfig.setDstObj(appSysManager.getAppNo());
                            baseAuthConfig.setDstObjLabel(appSysManager.getAppName());
                            baseAuthConfig.setTypeId(baseAuthType);
                            baseAuthConfig.setOpt(opt);
                            baseAuthConfigs.add(baseAuthConfig);
                        }
                        deleteBaseAuthConfigByType(baseAuthType);
                        baseAuthConfigService.save(baseAuthConfigs);
                    }
                }
                if (out.size()>0){
                    //外部设备访问应用系统
                    List<BaseAuthConfig> baseAuthConfigs=new ArrayList<>();
                    for (BaseAuthApp baseAuthApp:out){
                        Asset asset=getAssetByIp(baseAuthApp.getIp());
                        AppSysManager appSysManager = appSysManagerService.getOne(baseAuthApp.getAppId());
                        if (appSysManager!=null){
                            BaseAuthConfig baseAuthConfig=new BaseAuthConfig();
                            baseAuthConfig.setCreateTime(baseAuthApp.getCreateTime());
                            baseAuthConfig.setSrcObj(baseAuthApp.getIp());
                            if (asset!=null){
                                baseAuthConfig.setSrcObjLabel(asset.getName());
                            }
                            baseAuthConfig.setDstObj(appSysManager.getAppNo());
                            baseAuthConfig.setDstObjLabel(appSysManager.getAppName());
                            baseAuthConfig.setTypeId(145);
                            baseAuthConfig.setOpt(opt);
                            baseAuthConfigs.add(baseAuthConfig);
                        }
                        deleteBaseAuthConfigByType(145);
                        baseAuthConfigService.save(baseAuthConfigs);
                    }
                }
            }
            if (integer==4){
                List<BaseAuthInternet> baseAuthInternets = baseAuthInternetService.findAll();
                if (baseAuthInternets.size()>0){
                    List<BaseAuthConfig> baseAuthConfigs=new ArrayList<>();
                    List<InternetInfoManage> internetInfoManageServiceAll = internetInfoManageService.findAll();
                    for (BaseAuthInternet baseAuthInternet:baseAuthInternets){
                        Asset asset=getAssetByIp(baseAuthInternet.getIp());
                        List<InternetInfoManage> internetInfoManages = internetInfoManageServiceAll.stream().filter(p -> p.getId().equals(baseAuthInternet.getInternetId())).collect(Collectors.toList());
                        if (internetInfoManages.size()>0){
                            InternetInfoManage internetInfoManage = internetInfoManages.get(0);
                            BaseAuthConfig baseAuthConfig=new BaseAuthConfig();
                            baseAuthConfig.setCreateTime(baseAuthInternet.getCreateTime());
                            baseAuthConfig.setDstObj(internetInfoManage.getId().toString());
                            baseAuthConfig.setDstObjLabel(internetInfoManage.getInternetName());
                            baseAuthConfig.setSrcObj(baseAuthInternet.getIp());
                            baseAuthConfig.setTypeId(baseAuthType);
                            if (asset!=null){
                                baseAuthConfig.setSrcObjLabel(asset.getName());
                            }
                            baseAuthConfig.setOpt(opt);
                            baseAuthConfigs.add(baseAuthConfig);
                        }
                    }
                    if (baseAuthConfigs.size()>0){
                        deleteBaseAuthConfigByType(baseAuthType);
                        baseAuthConfigService.save(baseAuthConfigs);
                    }
                }

            }
            if (integer==5){
                List<BaseAuthOperation> all = baseAuthOperationService.findAll();
                List<BaseAuthOperation> collect = all.stream().filter(a -> a.getType().equals(1)).collect(Collectors.toList());
                List<BaseAuthOperation> assetCollect = all.stream().filter(a -> a.getType().equals(0)).collect(Collectors.toList());
                if (collect.size()>0){
                    List<BaseAuthConfig> baseAuthConfigs=new ArrayList<>();
                    for (BaseAuthOperation baseAuthOperation:collect){
                        Asset asset=getAssetByIp(baseAuthOperation.getIp());
                        AppSysManager appSysManager=appSysManagerService.getAppByIp(baseAuthOperation.getDstIp());
                        if (appSysManager!=null){
                            BaseAuthConfig baseAuthConfig=new BaseAuthConfig();
                            baseAuthConfig.setCreateTime(new Date());
                            baseAuthConfig.setSrcObj(baseAuthOperation.getIp());
                            if (asset!=null){
                                baseAuthConfig.setSrcObjLabel(asset.getName());
                            }
                            baseAuthConfig.setDstObj(appSysManager.getAppNo());
                            baseAuthConfig.setDstObjLabel(appSysManager.getAppName());
                            baseAuthConfig.setTypeId(baseAuthType);
                            baseAuthConfig.setOpt(opt);
                            baseAuthConfigs.add(baseAuthConfig);
                        }
                        deleteBaseAuthConfigByType(baseAuthType);
                        baseAuthConfigService.save(baseAuthConfigs);
                    }
                }
                if (assetCollect.size()>0){
                    List<BaseAuthConfig> baseAuthConfigs=new ArrayList<>();
                    for (BaseAuthOperation baseAuthOperation:assetCollect){
                        Asset asset=getAssetByIp(baseAuthOperation.getIp());
                        Asset assetByIp = getAssetByIp(baseAuthOperation.getDstIp());
                        if (assetByIp!=null){
                            BaseAuthConfig baseAuthConfig=new BaseAuthConfig();
                            baseAuthConfig.setCreateTime(baseAuthOperation.getCreateTime());
                            baseAuthConfig.setSrcObj(baseAuthOperation.getIp());
                            if (asset!=null){
                                baseAuthConfig.setSrcObjLabel(asset.getName());
                            }
                            baseAuthConfig.setDstObj(baseAuthOperation.getIp());
                            baseAuthConfig.setDstObjLabel(assetByIp.getName());
                            baseAuthConfig.setTypeId(147);
                            baseAuthConfig.setOpt(opt);
                            baseAuthConfigs.add(baseAuthConfig);
                        }
                        deleteBaseAuthConfigByType(147);
                        baseAuthConfigService.save(baseAuthConfigs);
                    }
                }
            }
        }

    }


    private Asset getAssetByIp(String ip) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("ip",ip));
        List<Asset> assetServiceAll = assetService.findAll(queryConditions);
        if (assetServiceAll.size()>0){
            return assetServiceAll.get(0);
        }
        return null;
    }


    private List<Asset> getAssetByIps(List<String> strings) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.in("ip",strings));
        List<Asset> assetServiceAll = assetService.findAll(queryConditions);
        return assetServiceAll;
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
            fileName="运维权限审批登记信息";
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
        String sql = "select ip from base_auth_print_burn where type=2;";
        return jdbcTemplate.queryForList(sql, String.class);
    }
    private void deleteBaseAuthConfigByType(Integer baseAuthEnumByCode) {
        String sql="delete from base_auth_config where type_id="+baseAuthEnumByCode+";";
        jdbcTemplate.update(sql);
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
    @Override
    public Result<Map<String, Object>> getTotalStatisticsV2(String type) throws ParseException {
        Map<String, Object> mapRes = new HashMap<>();
        switch (type){
            case  BaseAuthUtil.TYPE_ALL:  // 所有
                return getAllTotalAndTrend();
            case  BaseAuthUtil.TYPE_PRINT:  // 打印
                return getPrintBrunTotalAndTrend(1,0);
            case  BaseAuthUtil.TYPE_BURN: // e刻录
                return getPrintBrunTotalAndTrend(2,0);
            case  BaseAuthUtil.TYPE_INTER: //网路互联
                return getTotalAndTrend("base_auth_internet");
            case  BaseAuthUtil.TYPE_ACCESS: // 访问
                return getTotalAndTrend("base_auth_app");
            case  BaseAuthUtil.TYPE_MAINT: // 运维
                return getTotalAndTrend("base_auth_operation");
            default:
                return ResultUtil.success(mapRes);
        }
    }

    @Override
    public Result<Map<String, Object>> getPrintStatistics() throws ParseException{
        return getPrintBrunTotalAndTrend(1,1);
    }

    @Override
    public Result<Map<String, Object>> getBurnStatistics() throws ParseException{
        return getPrintBrunTotalAndTrend(2,1);
    }

    @Override
    public Result<Map<String, Object>> getAccessHostStatistics() throws ParseException{
        return getAppTotalAndTrend(0);

    }

    @Override
    public Result<Map<String, Object>> getExternalAssetStatistics() throws ParseException{
        return getAppTotalAndTrend(1);

    }

    @Override
    public Result<List<CoordinateVO>> getMaintenFlagCountStatistics() throws ParseException{
        return ResultUtil.successList(baseAuthOverviewV2Dao.getMaintenFlagCountStatistics());
    }

    @Override
    public List<TrendResultVO> getMaintenFlagMonthStatistics() throws ParseException{
        List<TrendResultVO> result = new ArrayStack();
        // 一个月时间按天、ip分组统计结果
        List<TrendExtendVO> list = baseAuthOverviewV2Dao.getMaintenFlagMonthStatistics();
        if(CollectionUtils.isEmpty(list)){
            return result;
        }
        // ip分组
        Map<String,List<TrendExtendVO>> groupDatas = list.stream().collect(Collectors.groupingBy(TrendExtendVO::getFlag));
        Set<String> ips = groupDatas.keySet();
        TrendResultVO trendVO = null;
        // 获取x轴数据
        List<String> dataXs = BaseAuthUtil.getMonthDataX();
        for(String ip : ips){
            if(StringUtils.isEmpty(ip)){
                continue;
            }
            trendVO = new TrendResultVO();
            trendVO.setName(ip);
            List<TrendExtendVO> datas = groupDatas.get(ip);
            List<TrendVO> trends = mapper.mapList(datas,TrendVO.class);
            List<CoordinateVO> resList = dataSupplementObj(trends,dataXs);
            trendVO.setCoords(resList);
            result.add(trendVO);
        }
        return result;
    }

    private Result<Map<String, Object>> getAppTotalAndTrend(int i) throws ParseException{
        Map<String, Object> mapRes = new HashMap<>();
        // 总数统计及差异统计
        Map<String, Object> totalNum = new HashMap<>();
        int total = baseAuthOverviewV2Dao.getAppTotal(true,i);
        int lastTotal = baseAuthOverviewV2Dao.getAppTotal(false,i);
        totalNum.put("total",total);
        totalNum.put("diffCount",(total-lastTotal));
        // 近一个月趋势统计
        List<TrendVO> list = baseAuthOverviewV2Dao.getAppTrend(i);
        List<String> dataXs =BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list,dataXs);
        mapRes.put("count",totalNum);
        mapRes.put("trend",trendDatas);
        return ResultUtil.success(mapRes);
    }

    private Result<Map<String, Object>> getAllTotalAndTrend() throws ParseException{
        Map<String, Object> mapRes = new HashMap<>();
        // 总数统计及差异统计
        Map<String, Object> totalNum = new HashMap<>();
        int total = baseAuthOverviewV2Dao.getAllTotal(true);
        int lastTotal = baseAuthOverviewV2Dao.getAllTotal(false);
        totalNum.put("total",total);
        totalNum.put("diffCount",(total-lastTotal));
        // 近一个月趋势统计
        List<TrendVO> list = baseAuthOverviewV2Dao.getAllTrend();
        List<String> dataXs =BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list,dataXs);
        mapRes.put("count",totalNum);
        mapRes.put("trend",trendDatas);
        return ResultUtil.success(mapRes);
    }

    private Result<Map<String, Object>> getTotalAndTrend(String table) throws ParseException {
        Map<String, Object> mapRes = new HashMap<>();
        // 总数统计及差异统计
        Map<String, Object> totalNum = new HashMap<>();
        int total = baseAuthOverviewV2Dao.getTotal(true,table);
        int lastTotal = baseAuthOverviewV2Dao.getTotal(false,table);
        totalNum.put("total",total);
        totalNum.put("diffCount",(total-lastTotal));
        // 近一个月趋势统计
        List<TrendVO> list = baseAuthOverviewV2Dao.getTrend(table);
        List<String> dataXs =BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list,dataXs);
        mapRes.put("count",totalNum);
        mapRes.put("trend",trendDatas);
        return ResultUtil.success(mapRes);
    }

    private Result<Map<String, Object>> getPrintBrunTotalAndTrend(Integer type,Integer b) throws ParseException {
        Map<String, Object> mapRes = new HashMap<>();
        // 总数统计及差异统计
        Map<String, Object> totalNum = new HashMap<>();
        int total = baseAuthOverviewV2Dao.getPrintBrunTotal(type,b,true);
        int lastTotal = baseAuthOverviewV2Dao.getPrintBrunTotal(type,b,false);
        totalNum.put("total",total);
        totalNum.put("diffCount",(total-lastTotal));
        // 近一个月趋势统计
        List<TrendVO> list = baseAuthOverviewV2Dao.getPrintBrunTrend(type,b);
        List<String> dataXs =BaseAuthUtil.getMonthDataX();
        List<CoordinateVO> trendDatas = dataSupplementObj(list,dataXs);
        mapRes.put("count",totalNum);
        mapRes.put("trend",trendDatas);
        return ResultUtil.success(mapRes);
    }
    private  List<CoordinateVO>  dataSupplementObj(List<TrendVO> datas , List<String> dataXs) throws ParseException {
        List<CoordinateVO> allDatas = new ArrayList<CoordinateVO>();
        CoordinateVO data = null;
        for(int i= 0;i < dataXs.size(); i++){
            data = new CoordinateVO();
            dataHandleObj(datas,data,dataXs.get(i));
            allDatas.add(data);
        }
        return allDatas;
    }

    private void dataHandleObj(List<TrendVO> datas, CoordinateVO data, String name) {
        int number =  getNumObj(datas,name);
        data.setDataY(number+"");
        data.setDataX(name);
    }

    private int getNumObj(List<TrendVO> datas, String name) {
        if(CollectionUtils.isEmpty(datas)){
            return 0;
        }
        for(TrendVO param : datas){
            String dataName =param.getName();
            if(name.equals(dataName)){
                return param.getNumber();
            }
        }
        return  0;
    }
}
