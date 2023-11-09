package com.vrv.vap.alarmdeal.business.asset.service.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.service.*;
import com.vrv.vap.alarmdeal.business.asset.util.AssetUtil;
import com.vrv.vap.alarmdeal.business.asset.util.AssetValidateUtil;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.CustomSettings;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.SessionUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * 资产导入新
 * 2021-09-114
 */
@Service
@Transactional
public class AssetImportServiceImpl implements AssetImportService {
    private static Logger logger = LoggerFactory.getLogger(AssetImportServiceImpl.class);

    @Autowired
    private AssetTypeService assetTypeService;
    @Autowired
    private AssetBaseDataService assetBaseDataService;
    @Autowired
    private AssetTypeGroupService assetTypeGroupService;
    @Autowired
    AssetSettingsService assetSettingsService;
    @Autowired
    private AssetDao assetDao;
    @Autowired
    private FileConfiguration fileConfiguration;
    @Autowired
    private AssetClassifiedLevel assetClassifiedLevel;

    // 资产导入多线程校验：每个线程检验的数据
    private int threadHandleNum = 500;

    /**

     * 解析资产导入数据
     *
     * @param file
     * @return
     */
    @Override
    public Map<String, List<Map<String, Object>>> parseImportAssetInfo(MultipartFile file) throws IOException {
        logger.info("parseImportAssetInfo start");
        Map<String, List<Map<String, Object>>> map = new HashMap<>();
        // 执行数据解析、校验处理
        InputStream inputStream = null;
        try {
            // 执行数据解析、校验处理
            inputStream = file.getInputStream();
            List<Map<String, Object>> dataList = getAllAssetList(inputStream);
            // 获取校验成功的数据
            List<Map<String, Object>> selectTrueList = selectAssetInfo("true", dataList);
            // 获取校验失败的数据
            List<Map<String, Object>> selectFalseInfo = selectAssetInfo("false", dataList);
            map.put("true", selectTrueList);
            map.put("false", selectFalseInfo);
            // sheet页不能存在的
            List<Map<String, Object>> selectSheet = selectAssetInfo("sheet", dataList);
            if (selectSheet.size() > 0) {
                map.put("sheet", selectSheet);
            }
            // 文件保存到服务器上 2021-10-22
            if (selectTrueList.size() > 0) {
                saveFileToServer(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流
            if (inputStream != null) {
                inputStream.close();
            }
        }
        logger.info("parseImportAssetInfo end");
        return map;
    }

    private void saveFileToServer(MultipartFile file) {
        OutputStream out = null;
        try {
            String operaterName = SessionUtil.getCurrentUser().getName();
            String dataStr = DateUtil.format(new Date(), "yyyyMMddHHmmss");
            String orgName = SessionUtil.getCurrentUser().getOrgCode();
            String filepath = fileConfiguration.getFilePath() + "/uplod";
            String fileName = "asset_" + operaterName + "_" + dataStr + "_" + orgName + ".xls";
            String filePath = filepath + "/" + fileName;
            File targetFile = new File(filepath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            out = new FileOutputStream(filePath);
            out.write(file.getBytes());
        } catch (Exception e) {
            logger.error("saveFileToServerException: {}", e);
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // 执行数据解析、校验处理
    private List<Map<String, Object>> getAllAssetList(InputStream inputStream) {
        List<Map<String, Object>> dataList = new ArrayList<>();
        Map<String, List<Map<String, Object>>> list = getImportAssetInfoList(inputStream);
        for (Map.Entry<String, List<Map<String, Object>>> entry : list.entrySet()) {
            List<Map<String, Object>> data = entry.getValue();
            dataList.addAll(data);
        }
        return dataList;
    }

    private Map<String, List<Map<String, Object>>> getImportAssetInfoList(InputStream inputStream) {
        HSSFWorkbook workbook = null;
        try {
            workbook = new HSSFWorkbook(inputStream);
        } catch (IOException e) {
            logger.error("IOException: {}", e);
        }
        // 第一步，获得Title
        Map<String, List<String>> excelTitle = ImportExcelUtil.getExcelTitle(workbook);
        // 第二步，根据资产类型获得资产的分类
        Map<String, List<List<String>>> excelContent = ImportExcelUtil.getExcelContent(workbook);
        // 第三步, 根据title获得content获得整合数据
        NameValue type = assetSettingsService.getSettingScope();
        String settingName = type.getName();  // 获取当前配置的力度
        if(!"AssetTypeGroup".equals(settingName)){
            throw new AlarmDealException(-1,"目前模板作用域配置只支持一级资产类型,当前作用域为："+settingName);
        }
        Map<String, List<Map<String, Object>>> sheetNoExsit = new HashMap<String, List<Map<String, Object>>>(); // 记录sheet页找不到对应的资产类型
        Map<String, List<Map<String, Object>>> assetColletions = getAssetInfos(excelTitle, excelContent, settingName, sheetNoExsit);
        // 第四步，验证Asset信息，添加是否满足要求
        Map<String, List<Map<String, Object>>> validateDatas = importDataVlidata(assetColletions, settingName);
        // 对于存在sheet页找不到对应的资产类型的处理：
        if (sheetNoExsit.size() > 0) {
            validateDatas.putAll(sheetNoExsit);
        }
        return validateDatas;
    }

    /**
     * 根据表头和sheet内容获取资产信息key--value格式
     *
     * @param titles  表头标题
     * @param content 解析的sheet内容
     * @return
     */
    private Map<String, List<Map<String, Object>>> getAssetInfos(Map<String, List<String>> titles, Map<String, List<List<String>>> content, String settingName, Map<String, List<Map<String, Object>>> sheetNoExsit) {
        Map<String, List<Map<String, Object>>> parentMap = new HashMap<>();
        Set<String> sheets = titles.keySet();
        for (String sheet : sheets) {
            List<String> _titles = titles.get(sheet);
            List<List<String>> table = content.get(sheet);
            if (table.isEmpty()) {
                continue;
            }
            // 判断sheetName是否存在：不存在记录，存在的进行数据解析处理
            Map<String, Object> msg = null;
            List<Map<String, Object>> sheetParam = null;
            if (!isExistSheetName(sheet)) {
                msg = new HashMap<>();
                sheetParam = new ArrayList<>();
                msg.put("sheetName", sheet);
                msg.put("stateDescripe", "当前sheet没有找到对应的资产类型,不执行解析");
                msg.put("state", "sheet");
                msg.put("guid", UUIDUtils.get32UUID());
                sheetParam.add(msg);
                sheetNoExsit.put(sheet, sheetParam);
                logger.info("sheet页没有找到对应的资产类型：" + sheet);
                continue;
            }
            List<CustomSettings> customSettings = new ArrayList<CustomSettings>(); // 模板列配置信息
            Map<String, String> colums = getTemplateCloumsByAssetTypeGroup(sheet, customSettings); // 根据sheeet名称获取模板的列
            Map<String, String> _Colums = new HashMap<>();
            _Colums.putAll(colums);
            List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
            for (List<String> item : table) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 0; i < _titles.size(); i++) {
                    String title = _titles.get(i);
                    if (_Colums.containsKey(title)) {
                        if (item.size() > i) {
                            row.put(_Colums.get(title), item.get(i)==null?"":item.get(i).trim()); // 去掉前后空格, 2023-06-19
                        } else {
                            row.put(_Colums.get(title), "");
                        }
                    }
                }
                //补充
                row.put("uniqueCode", sheet);
                row.put("customSettings", customSettings);
                rows.add(row);
            }
            parentMap.put(sheet, rows);
        }
        return parentMap;
    }

    /**
     * 剔除隐藏的sheet页：
     * 1.sheet页名称在资产类型中存在，表示不是隐藏的
     *
     * @param uniquecode
     * @return 2021-08-18
     */
    private boolean isExistSheetName(String uniquecode) {
        NameValue type = assetSettingsService.getSettingScope();
        List<QueryCondition> con1 = new ArrayList<>();
        switch (type.getName()) {
            case "AssetTypeGroup":
                con1.add(QueryCondition.eq("uniqueCode", uniquecode));
                List<AssetTypeGroup> assetTypeGroups = assetTypeGroupService.findAll(con1);
                if (null == assetTypeGroups || assetTypeGroups.size() == 0) {
                    return false;
                }
                return true;
            case "AssetType":
                con1.add(QueryCondition.eq("uniqueCode", uniquecode));
                List<AssetType> assetTypes = assetTypeService.findAll(con1);
                if (null == assetTypes || assetTypes.size() == 0) {
                    return false;
                }
                return true;
            default:
                break;
        }
        return false;
    }

    /**
     * 根据sheet页名称获取对应配置的列信息（基本信息）
     *
     * @param uniquecode     sheet页名称（资产类型汇总uniquecode的值）
     * @param customSettings
     * @return map
     * 2021- 09 -16
     */
    private Map<String, String> getTemplateCloumsByAssetTypeGroup(String uniquecode, List<CustomSettings> customSettings) {
        List<QueryCondition> con1 = new ArrayList<>();
        String treeCode = "";
        List<CustomSettings> columns = null;
        con1.add(QueryCondition.eq("uniqueCode", uniquecode));
        List<AssetTypeGroup> assetTypeGroups = assetTypeGroupService.findAll(con1);
        if (null == assetTypeGroups || assetTypeGroups.size() == 0) {
            return new HashMap<String, String>();
        }
        treeCode = assetTypeGroups.get(0).getTreeCode();
        columns = assetSettingsService.getExcelColumns(treeCode, "assetTypeGroup", assetTypeGroups.get(0).getGuid());
        customSettings.addAll(columns);
        return getCloumns(columns);
    }

    // 模板中存在责任人姓名，增加责任人编号；目前模板没有配置责任人编号，手动加上
    private Map<String, String> getCloumns(List<CustomSettings> temp) {
        Map<String, String> params = new HashMap<String, String>();
        for (CustomSettings cus : temp) {
            params.put(cus.getTitle(), cus.getName());
            // 增加责任人姓名存在，解析对应责任人code，模板中没有code 2021-09-01
            if ("responsibleName".equalsIgnoreCase(cus.getName())) {
                params.put("责任人编号", "responsibleCode");
            }
        }
        return params;
    }

    /**
     * 导入数据的校验  2021-09-13
     * map中一个可以对应一个sheet页
     *
     * @param assetColletions
     */
    private Map<String, List<Map<String, Object>>> importDataVlidata(Map<String, List<Map<String, Object>>> assetColletions, String settingName) {
        // 初始化数据
        logger.info("init data start");
        // 获取安全域数据
        List<BaseSecurityDomain> domains = assetBaseDataService.queryAllDomain();
        // 获取所有资产信息：ip、mac、序列号、二级资产类型treeCode、品牌型号名称
        List<Map<String, Object>> assets = assetDao.allAssetDataValidata();
        // 获取所有一级资产类型
        List<AssetTypeGroup> groups = assetBaseDataService.queyAllAssetTypeGroup();
        // 获取所有二级资产类型
        List<AssetType> types = assetBaseDataService.queryAllAssetType();
        // 资产所有涉密等级
        List<BaseDictAll> baseDictAlls =assetClassifiedLevel.findAll();
        // 获取所有用户信息 2021-08-23
        List<BasePersonZjg> basePersonZjgList = assetBaseDataService.queryAllPerson();
        Map<String, List<Map<String, Object>>> assetColletionsCopy = new HashMap<>(); // 校验数据重复性用
        assetColletionsCopy = assetColletions;
        // 校验的结果
        Map<String, List<Map<String, Object>>> assetColletionsValidates = new HashMap<>();
        // 按sheet进行数据校验
        for (Map.Entry<String, List<Map<String, Object>>> entry : assetColletions.entrySet()) {
            List<Map<String, Object>> value = entry.getValue();
            // 获取sheet也对应模板表titile
            List<CustomSettings> tempTitles = (List<CustomSettings>) value.get(0).get("customSettings");
            // 循环处理每个sheet页数据
            // 采用多线程处理
            List<Map<String, Object>> validateResultThread = getValidateResultThread(assets, groups, types,basePersonZjgList, tempTitles, settingName, assetColletionsCopy, value, entry.getKey(), domains,baseDictAlls);
            assetColletionsValidates.put(entry.getKey(), validateResultThread);
        }
        logger.info("exc data validate end");
        return assetColletionsValidates;
    }

    // 采用带有返回值得线程处理
    private List<Map<String, Object>> getValidateResultThread(List<Map<String, Object>> assets, List<AssetTypeGroup> groups, List<AssetType> types,  List<BasePersonZjg> basePersonZjgList, List<CustomSettings> tempTitles, String settingName, Map<String, List<Map<String, Object>>> assetColletionsCopy, List<Map<String, Object>> value, String key, List<BaseSecurityDomain> domains,List<BaseDictAll> baseDictAlls ) {
        List<Map<String, Object>> validateDatas = new ArrayList<>();
        List<Future<List<Map<String, Object>>>> list = new ArrayList<Future<List<Map<String, Object>>>>();//Future<List<Map<String, Object>>>是线程池执行后产生的结果类型，通过该类型的对象可以获取线程的返回的值
        ExecutorService pool = Executors.newFixedThreadPool(20);//线程池，产生10个线程备用
        int count = value.size();
        logger.info( "{}校验数量：{}",key , count);
        if (count <= threadHandleNum) {
            Future<List<Map<String, Object>>> future = getFutureValidate(pool, assets, groups, types, basePersonZjgList, tempTitles, settingName, assetColletionsCopy, value, key, domains,baseDictAlls);
            list.add(future);
        } else {
            int xh = count / threadHandleNum;
            int index = 0;
            logger.info("处理批次：" + (xh + 1));
            for (int i = 0; i < xh + 1; i++) {
                int startIndex = index * threadHandleNum;
                int endIndex = index * threadHandleNum + threadHandleNum;
                if (endIndex > count) {
                    endIndex = count;
                }
                List<Map<String, Object>> newData = value.subList(startIndex, endIndex);
                if (null != newData && newData.size() > 0) {
                    Future<List<Map<String, Object>>> future = getFutureValidate(pool, assets, groups, types,basePersonZjgList, tempTitles, settingName, assetColletionsCopy, newData, key, domains,baseDictAlls);
                    list.add(future);
                    index++;
                }
            }
        }
        // 获取多线程执行后的数据
        for (Future<List<Map<String, Object>>> futureData : list) {
            List<Map<String, Object>> data = null;
            try {
                data = futureData.get();
                validateDatas.addAll(data);
            } catch (Exception e) {
                logger.error("获取future的值异常", e);
            }
        }
        return validateDatas;
    }

    // 线程处理
    private Future<List<Map<String, Object>>> getFutureValidate(ExecutorService pool, List<Map<String, Object>> assets, List<AssetTypeGroup> groups, List<AssetType> types, List<BasePersonZjg> basePersonZjgList, List<CustomSettings> tempTitles, String settingName, Map<String, List<Map<String, Object>>> assetColletionsCopy, List<Map<String, Object>> value, String key, List<BaseSecurityDomain> domains,List<BaseDictAll> baseDictAlls) {
        long startTime = System.currentTimeMillis();
        Future<List<Map<String, Object>>> future = pool.submit(new Callable() {
            @Override
            public List<Map<String, Object>> call() throws Exception {
                return excThreadValidateResult(assets, groups, types, basePersonZjgList, tempTitles, settingName, assetColletionsCopy, value, key, domains, baseDictAlls);
            }
        });
        logger.info("单个线程校验时间：" + (System.currentTimeMillis() - startTime));
        return future;
    }

    // 按sheet页执行数据校验处理
    private List<Map<String, Object>> excThreadValidateResult(List<Map<String, Object>> assets, List<AssetTypeGroup> groups, List<AssetType> types, List<BasePersonZjg> basePersonZjgList, List<CustomSettings> tempTitles, String settingName, Map<String,
            List<Map<String, Object>>> assetColletionsCopy, List<Map<String, Object>> value, String key, List<BaseSecurityDomain> domains,List<BaseDictAll> baseDictAlls) {
        Map<String, Map<String, Object>> currentMap = new HashMap<>(); // 当前数据，主要是为了导入数据重复用的
        for (Map<String, Object> map : value) {
            // 数据校验
            currentMap = new HashMap<>();
            currentMap.put(key, map);
            // 安全域自动补充处理：根据名称自动补充guid，id，subcode、domainname
            boolean result = securityNameHandle(domains, map);
            if(!result){
                logger.info("安全域校验失败："+map.get("stateDescripe"));
                continue;
            }
            boolean validateResult = assetValidate(map, assets, groups, types, basePersonZjgList, tempTitles, currentMap, settingName, assetColletionsCopy,baseDictAlls,key);
            if (!validateResult) {
                // 清除多余数据
                if (null != map.get("customSettings")) {
                    map.remove("customSettings");
                }
                continue;
            }
            // 扩展属性字段列处理:主要是为了保存时扩展属性时，识别出那些扩展属性字段
            customColumns(map);
            // 清除多余数据
            if (null != map.get("customSettings")) {
                map.remove("customSettings");
            }
            // 校验成功的，标识state为true；
            map.put("stateDescripe", "");
            map.put("state", true);
            map.put("guid", UUIDUtils.get32UUID());
        }
        return value;
    }

    /**
     * 数据校验处理
     *
     * @param map               当前数据
     * @param assets            所有资产信息
     * @param groups            所有一级类型
     * @param types             所有二级类型
     * @param basePersonZjgList 所有人员信息
     * @param tempTitles        模板列
     * @param currentMap        当前数据：校验重复用
     * @param settingName       当前配置的力度
     * @param assetColletions   所有解析的数据：校验重复用
     * @return boolean
     */
    private boolean assetValidate(Map<String, Object> map, List<Map<String, Object>> assets, List<AssetTypeGroup> groups, List<AssetType> types, List<BasePersonZjg> basePersonZjgList, List<CustomSettings> tempTitles,
                                  Map<String, Map<String, Object>> currentMap, String settingName, Map<String, List<Map<String, Object>>> assetColletions,List<BaseDictAll> baseDictAlls,String typeGroupUnicode) {
        // 资产类型是否存在校验
        AssetTypeGroup group = getAssetTypeGroupByUniqueCode(groups, typeGroupUnicode);
        String groupTreeCode = group.getTreeCode();
        // 获取对应的一级,放在tags字段中，策略维表用到改字段 涂2023-09-19
        map.put("tags",groupTreeCode);
        String assetTypeName = map.get("assetTypeName") == null ? "" : String.valueOf(map.get("assetTypeName")); // 资产类型名称
        AssetType assetType = getAssetTypeByName(types, assetTypeName,groupTreeCode);
        if (null == assetType) {
            validateAssetError(map, "一级资产类型："+ typeGroupUnicode+"下，二级资产类型："+assetTypeName + "不存在");
            return false;
        }
        map.put("assetType",assetType.getGuid()); // 二级资产类型guid
        map.put("typeUnicode",assetType.getUniqueCode()); // 二级资产类型uniqueCode
        map.put("assetTypeTreeCode",assetType.getTreeCode()); // 二级资产类型treeCode
        // 导入数据重复性校验：IP、mac、序列号(包括ip、mac格式校验)
        String serialNumberTitile = getTileByName(tempTitles, "serialNumber");
        boolean dupRes = duplicateImportDataValidate(assetColletions, map, serialNumberTitile, currentMap);
        if (!dupRes) {
            return false;
        }
        // 必填子段校验
        boolean isMust = isMustWriteValidate(map, tempTitles);
        if (!isMust) {
            return false;
        }
        // 重复性校验：ip、mac、序列号
        boolean dataDuplicationResult = dataDuplicateDataValidate(map, assets,serialNumberTitile);
        if(!dataDuplicationResult){
            return false;
        }
        // 有效性校验：责任人、责任单位校验;涉密等级、是否安装终端客、终端类型、国产校验及相关转义、资产价值五性进行转义处理
        boolean validityValidateRes = validityValidate(basePersonZjgList, map, tempTitles,baseDictAlls);
        if(!validityValidateRes){
            return false;
        }
        // 安装操作时间处理
        boolean osSetupTimeRes = osSetupTimeHandle(map);
        if(!osSetupTimeRes){
            return false;
        }
        // 设备序列号不允许输入汉字和特殊字符
        boolean serialNumberRes = serialNumberFormateValidate(map,serialNumberTitile);
        if(!serialNumberRes){
            return false;
        }
        // 管理入口URL格式校验 20230719
        String manageInletUrlTitile = getTileByName(tempTitles, "manageInletUrl");
        return manageInletUrlValidate(map,manageInletUrlTitile);
    }

    /**
     * 管理入口URL校验
     * @param map
     * @param titile
     * @return
     * @Data 2023-07-19
     */
    private boolean manageInletUrlValidate(Map<String, Object> map, String titile) {
        String manageInletUrl = map.get("manageInletUrl") == null ? "" : String.valueOf(map.get("manageInletUrl"));
        if(StringUtils.isEmpty(manageInletUrl)){
            return true;
        }
        boolean manageInletUrlRes = AssetUtil.checkUrl(manageInletUrl);
        if (!manageInletUrlRes){
            validateAssetError(map, titile+"格式异常！");
            return false;
        }
        return true;
    }

    /**
     * 设备序列号不允许输入汉字和特殊字符 2022-04-27
     * @param map
     * @return
     */
    private boolean serialNumberFormateValidate(Map<String, Object> map,String serialNumberTitile) {
        String serialNumber = map.get("serialNumber") == null ? "" : String.valueOf(map.get("serialNumber"));
        Result<String> formatValidateResult = AssetValidateUtil.serialNumberFormat(serialNumber);
        if (formatValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            validateAssetError(map, serialNumberTitile+"存在特殊字符！");
            return false;
        }
        return true;
    }

    // 安装操作时间处理
    private boolean osSetupTimeHandle(Map<String, Object> map) {
        String osSetupTime = map.get("osSetuptime") == null?"":String.valueOf(map.get("osSetuptime"));
        if(StringUtils.isEmpty(osSetupTime)){
            return true;
        }
        try{
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");;
            map.put("osSetuptime", sdf.parse(osSetupTime));
        }catch(Exception e){
            logger.error("操作系统安装时间解析异常,{}",e);
            validateAssetError(map, "操作系统安装时间解析异常,"+osSetupTime);
            return false;
        }
        return true;
    }


    // 错误提示：展示模板中titile的值
    private String getTileByName(List<CustomSettings> tempTitles, String name) {
        for (CustomSettings cus : tempTitles) {
            if (name.equalsIgnoreCase(cus.getName())) {
                return cus.getTitle();
            }
        }
        return "";
    }


    // 安全域自动补充处理：根据名称自动补充guid，id
    private boolean securityNameHandle(List<BaseSecurityDomain> domains, Map<String, Object> map) {
        Object securityNameObj = map.get("securityName");
        String securityName = securityNameObj == null?"":String.valueOf(securityNameObj);
        if(StringUtils.isEmpty(securityName)){
            return true;
        }
        BaseSecurityDomain domain = getDomainByName(domains,securityName);
        if(null == domain){
            validateAssetError(map, "安全域名称不存在,"+securityName);
            return false;
        }
        // 存在的话进行补全处理
        map.put("securityGuid", domain.getCode());
        map.put("securityId", domain.getId());
        map.put("domainSubCode", domain.getSubCode());
        map.put("domainName",domain.getDomainName()); //补全安全域名称 20220629
        return true;
    }

    private BaseSecurityDomain getDomainByName(List<BaseSecurityDomain> domains, String securityName) {
        for (BaseSecurityDomain domain : domains) {
            if (domain.getDomainName().equals(securityName)) {
                return domain;
            }
        }
        return null;
    }

    //导入数据中IP、mac、序列号重复性校验
    private boolean duplicateImportDataValidate(Map<String, List<Map<String, Object>>> assetColletions, Map<String, Object> map, String serialNumberTitile, Map<String, Map<String, Object>> currentMap) {
        // ip重复校验
        boolean ipRes = ipDuplicateImportDataValidate(map, assetColletions, currentMap);
        if (!ipRes) {
            return false;
        }
        // mac重复校验
        boolean macRes = macDuplicateImportDataValidate(map, assetColletions, currentMap);
        if (!macRes) {
            return false;
        }
        // 系列号重复校验
        boolean serRes = serDuplicateImportDataValidate(map, assetColletions, serialNumberTitile, currentMap);
        if (!serRes) {
            return false;
        }
        return true;
    }


    // ip重复性校验：针对导入数据，首先会校验ip格式
    private boolean ipDuplicateImportDataValidate(Map<String, Object> map, Map<String, List<Map<String, Object>>> assetColletions, Map<String, Map<String, Object>> currentMap) {
        String ip = map.get("ip") == null ? "" : String.valueOf(map.get("ip"));
        // 为空不处理
        if (StringUtils.isEmpty(ip)) {
            return true;
        }
        // ip格式校验
        boolean checkIPResult = AssetUtil.checkIP(ip);
        if (!checkIPResult) {
            validateAssetError(map, "ip格式异常");
            return false;
        }
        // ip导入重复
        for (Map.Entry<String, List<Map<String, Object>>> entry : assetColletions.entrySet()) {
            List<Map<String, Object>> value = entry.getValue();
            for (Map<String, Object> val : value) {
                if (null == val.get("ip") || StringUtils.isEmpty(String.valueOf(val.get("ip")))) {
                    continue;
                }
                // 自己排除
                if (null != currentMap.get(entry.getKey()) && currentMap.get(entry.getKey()).equals(val)) {
                    continue;
                }
                String valIp = String.valueOf(val.get("ip"));
                if (ip.equals(valIp)) {
                    validateAssetError(map, "导入的数据中存在重复ip：" + ip);
                    return false;
                }
            }
        }
        return true;
    }

    // mac重复性校验：针对导入数据，首先会校验mac格式
    private boolean macDuplicateImportDataValidate(Map<String, Object> map, Map<String, List<Map<String, Object>>> assetColletions, Map<String, Map<String, Object>> currentMap) {
        String mac = map.get("mac") == null ? "" : String.valueOf(map.get("mac"));
        Result<String> macFormat = AssetValidateUtil.macFormat(mac);
        // 格式校验
        if (ResultCodeEnum.UNKNOW_FAILED.getCode().equals(macFormat.getCode())) {
            validateAssetError(map, "MAC地址格式异常");
            return false;
        }
        // mac导入重复
        for (Map.Entry<String, List<Map<String, Object>>> entry : assetColletions.entrySet()) {
            List<Map<String, Object>> value = entry.getValue();
            for (Map<String, Object> val : value) {
                if (null == val.get("mac") || StringUtils.isEmpty(String.valueOf(val.get("mac")))) {
                    continue;
                }
                // 自己排除
                if (null != currentMap.get(entry.getKey()) && currentMap.get(entry.getKey()).equals(val)) {
                    continue;
                }
                String valMac = String.valueOf(val.get("mac"));
                if (mac.equals(valMac)) {
                    validateAssetError(map, "导入的数据中存在重复mac：" + mac);
                    return false;
                }
            }
        }
        return true;
    }

    // 系列号重复校验：针对导入数据
    private boolean serDuplicateImportDataValidate(Map<String, Object> map, Map<String, List<Map<String, Object>>> assetColletions, String serialNumberTitile, Map<String, Map<String, Object>> currentMap) {
        String serialNumber = map.get("serialNumber") == null ? "" : String.valueOf(map.get("serialNumber"));
        // 为空不处理
        if (StringUtils.isEmpty(serialNumber)) {
            return true;
        }
        // mac导入重复
        for (Map.Entry<String, List<Map<String, Object>>> entry : assetColletions.entrySet()) {
            List<Map<String, Object>> value = entry.getValue();
            for (Map<String, Object> val : value) {
                if (null == val.get("serialNumber") || StringUtils.isEmpty(String.valueOf(val.get("serialNumber")))) {
                    continue;
                }
                if (null != currentMap.get(entry.getKey()) && currentMap.get(entry.getKey()).equals(val)) { // 自己排除
                    continue;
                }
                String valSerialNumber = String.valueOf(val.get("serialNumber"));
                if (serialNumber.equals(valSerialNumber)) {
                    validateAssetError(map, "导入的数据中存在重复" + serialNumberTitile + ":" + serialNumber);
                    return false;
                }
            }
        }
        return true;
    }

    // 必填字段校验
    private boolean isMustWriteValidate(Map<String, Object> map, List<CustomSettings> templates) {
        for (CustomSettings settings : templates) {
            String nameTep = settings.getName();
            boolean isMust = settings.getIsMust();
            if (!isMust) {
                continue;
            }
            if (null == map.get(nameTep) || StringUtils.isEmpty(String.valueOf(map.get(nameTep)))) {
                validateAssetError(map, settings.getTitle() + "不能为空");
                return false;
            }
        }
        return true;
    }


    /**
     * 重复性校验：ip、mac、序列号
     * @param map
     * @param assets
     * @param serialNumberTitile
     * @return
     */
    private boolean dataDuplicateDataValidate(Map<String, Object> map, List<Map<String, Object>> assets,String serialNumberTitile) {
        // ip重复校验
        boolean ipRes = ipDuplicateDataValidate(map, assets);
        if (!ipRes) {
            return false;
        }
        // mac重复校验
        boolean macRes = macDuplicateDataValidate(map, assets);
        if (!macRes) {
            return false;
        }
        // 系列号重复校验
        boolean serialNumberRes = serialNumberDuplicateDataValidate(map, assets, serialNumberTitile);
        if (!serialNumberRes) {
            return false;
        }
        return true;

    }

    // p重复校验：针对数据库资产
    private boolean ipDuplicateDataValidate(Map<String, Object> map, List<Map<String, Object>> assets) {
        String ip = map.get("ip") == null ? "" : String.valueOf(map.get("ip"));
        // 为空不处理
        if (StringUtils.isEmpty(ip)) {
            return true;
        }
        //硬件资产类型中已存在该IP的资产，这是一级类型
        for (Map<String, Object> asset : assets) {
            String typeTreeCode = asset.get("typeTreeCode") == null ? "" : String.valueOf(asset.get("typeTreeCode"));
            if (StringUtils.isEmpty(typeTreeCode)) {
                continue;
            }
            if (ip.equals(asset.get("ip"))) {
                validateAssetError(map, "已存在该IP的资产！");
                return false;
            }
        }
        return true;
    }

    // mac重复校验：针对数据库资产
    private boolean macDuplicateDataValidate(Map<String, Object> map, List<Map<String, Object>> assets) {
        String mac = map.get("mac") == null ? "" : String.valueOf(map.get("mac"));
        // 为空不处理
        if (StringUtils.isEmpty(mac)) {
            return true;
        }
        //硬件资产类型中已存在该MAC的资产
        for (Map<String, Object> asset : assets) {
            String typeTreeCode = asset.get("typeTreeCode") == null ? "" : String.valueOf(asset.get("typeTreeCode"));
            if (StringUtils.isEmpty(typeTreeCode)) {
                continue;
            }
            if (mac.equals(asset.get("mac"))) {
                validateAssetError(map, "已存在该MAC的资产！");
                return false;
            }
        }
        return true;
    }

    // 系列号重复校验
    private boolean serialNumberDuplicateDataValidate(Map<String, Object> map, List<Map<String, Object>> assets, String serialNumberTitile) {
        String serialNumber = map.get("serialNumber") == null ? "" : String.valueOf(map.get("serialNumber"));
        // 为空不处理
        if (StringUtils.isEmpty(serialNumber)) {
            return true;
        }
        for (Map<String, Object> asset : assets) {
            String serlNumber = asset.get("serialNumber") == null ? "" : String.valueOf(asset.get("serialNumber"));
            if (StringUtils.isEmpty(serlNumber)) {
                continue;
            }
            if (serlNumber.equalsIgnoreCase(serialNumber)) {
                validateAssetError(map, serialNumberTitile + "重复！");
                return false;
            }
        }
        return true;
    }

    /**
     *  责任人、责任单位校验
     *  是否国产校验，检验成功进行转换
     *  是否安装终端客户端校验，检验成功进行转换
     *  终端类型校验，检验成功进行转换
     *  涉及等级校验，检验成功进行转换
     *  资产价值五性进行转义处理
     * @param basePersonZjgList
     * @param map
     * @param tempTitles
     * @return
     */
    private boolean validityValidate(List<BasePersonZjg> basePersonZjgList, Map<String, Object> map, List<CustomSettings> tempTitles,List<BaseDictAll> baseDictAlls){
        // 责任人、责任单位校验
        boolean responsibleCodeRes = responsibleCodeValidate(basePersonZjgList, map, tempTitles);
        if (!responsibleCodeRes) {
            return false;
        }
        // 是否国产校验，检验成功进行转换
        boolean termTypeRes = termTypeValidate(map);
        if (!termTypeRes) {
            return false;
        }
        // 是否安装终端客户端校验，检验成功进行转换
        boolean isMonitorAgentRes =isMonitorAgentValidate(map);
        if (!isMonitorAgentRes) {
            return false;
        }
        // 终端类型校验，检验成功进行转换
        boolean terminalTypeRes =terminalTypeValidate(map);
        if (!terminalTypeRes) {
            return false;
        }
        // 涉及等级校验，检验成功进行转换
        boolean equipmentIntensiveRes = equipmentIntensiveValidate(map,baseDictAlls);
        if (!equipmentIntensiveRes) {
            return false;
        }
        // 资产价值五性进行转义处理
        worthsTransfer(map);
        return true;
    }



    // 是否国产：1：表示国产 2：非国产
    private boolean termTypeValidate(Map<String, Object> map){
        String termType = map.get("termType")==null?"":String.valueOf(map.get("termType"));
        if(StringUtils.isEmpty(termType)){
            return true;
        }
        String termTypeCode = AssetValidateUtil.getTermTypeCodeByValue(termType);
        if(StringUtils.isEmpty(termTypeCode)){
            validateAssetError(map,  "是否国产的值不合法："+termType);
            return false;
        }
        // 校验符合进行转换
        map.put("termType",termTypeCode);
        return true;
    }

    // 是否安装终端客户端，检验成功进行转换
    private boolean isMonitorAgentValidate(Map<String, Object> map){
        String isMonitorAgent = map.get("isMonitorAgent")==null?"":String.valueOf(map.get("isMonitorAgent"));
        if(StringUtils.isEmpty(isMonitorAgent)){
            return true;
        }
        String isMonitorAgentCode = AssetValidateUtil.getIsMonitorAgentValueValidate(isMonitorAgent);
        if(StringUtils.isEmpty(isMonitorAgentCode)){
            validateAssetError(map,  "是否安装终端客户端的值不合法："+isMonitorAgent);
            return false;
        }
        // 校验符合进行转换
        map.put("isMonitorAgent",isMonitorAgentCode);
        return true;
    }
    // 终端类型，检验成功进行转换
    private boolean terminalTypeValidate(Map<String, Object> map){
        String terminalType = map.get("terminalType")==null?"":String.valueOf(map.get("terminalType"));
        if(StringUtils.isEmpty(terminalType)){
            return true;
        }
        String terminalTypeCode = AssetValidateUtil.getTerminalTypeCodeByValue(terminalType);
        if(StringUtils.isEmpty(terminalTypeCode)){
            validateAssetError(map,  "终端类型的值不合法："+terminalType);
            return false;
        }
        // 校验符合进行转换
        map.put("terminalType",terminalTypeCode);
        return true;
    }
    // 涉及等级校验，检验成功进行转换
    private boolean equipmentIntensiveValidate(Map<String, Object> map,List<BaseDictAll> baseDictAlls){
        String equipmentIntensive = map.get("equipmentIntensive")==null?"":String.valueOf(map.get("equipmentIntensive"));
        if(StringUtils.isEmpty(equipmentIntensive)){
            return true;
        }
        String equipmentIntensiveCode = assetClassifiedLevel.getCodeByValue(equipmentIntensive,baseDictAlls);
        if(StringUtils.isEmpty(equipmentIntensiveCode)){
            validateAssetError(map, "涉密等级的值不合法,"+equipmentIntensive);
            return false;
        }
        // 校验成功进行转换
        map.put("equipmentIntensive", equipmentIntensiveCode);
        return true;
    }
    /**
     * 资产价值五性进行转义处理  2023-1-5
     * @param map
     */
    private void worthsTransfer(Map<String, Object> map) {
        // 机密性
        String secrecy = map.get("secrecy")==null?"":String.valueOf(map.get("secrecy"));
        map.put("secrecy",AssetValidateUtil.worthCodeByName(secrecy));
        // 可用性
        String availability = map.get("availability")==null?"":String.valueOf(map.get("availability"));
        map.put("availability",AssetValidateUtil.worthCodeByName(availability));
        // 业务重要性
        String importance = map.get("importance")==null?"":String.valueOf(map.get("importance"));
        map.put("importance",AssetValidateUtil.worthCodeByName(importance));
        // 系统资产业务承载性
        String loadBear = map.get("loadBear")==null?"":String.valueOf(map.get("loadBear"));
        map.put("loadBear",AssetValidateUtil.worthCodeByName(loadBear));
        // 完整性
        String integrity = map.get("integrity")==null?"":String.valueOf(map.get("integrity"));
        map.put("integrity",AssetValidateUtil.worthCodeByName(integrity));
    }
    /**
     * 责任人编号、姓名、单位关联校验
     * 1.  责任人编号与姓名同时存在
     * 2. 责任人编号存在前提下：责任人编号与责任名称匹配校验；责任人与单位匹配校验；单位不存在的话，根据责任人自动填充单位名称、单位code
     *
     * @param basePersonZjgList
     * @param map
     * @param tempTitles
     * @return
     */
    private boolean responsibleCodeValidate(List<BasePersonZjg> basePersonZjgList, Map<String, Object> map, List<CustomSettings> tempTitles) {
        String responsibleNameTitile = getTileByName(tempTitles, "responsibleName");
        String orgNameTitile = getTileByName(tempTitles, "orgName");
        String responsibleCode = map.get("responsibleCode")== null?"":String.valueOf(map.get("responsibleCode"));
        String responsibleName = map.get("responsibleName")== null?"":String.valueOf(map.get("responsibleName"));
        // 责任人编号与姓名同时存在
        if ((StringUtils.isEmpty(responsibleCode)) && (StringUtils.isNotEmpty(responsibleName))) {
            validateAssetError(map, "责任人编号必填");
            return false;
        }
        // 责任人编号与姓名同时存在
        if ((StringUtils.isEmpty(responsibleName)) && (StringUtils.isNotEmpty(responsibleCode))) {
            validateAssetError(map, "责任人编号和" + responsibleNameTitile + "同时存在");
            return false;
        }
        // 责任人编号存在前提下：责任人编号与责任名称匹配校验；责任人与单位匹配校验；单位不存在的话，根据责任人自动填充单位名称、单位code
        if (StringUtils.isNotEmpty(responsibleCode)) {// 责任人Code不为空情况下
            BasePersonZjg person = getPersonByCode(responsibleCode, basePersonZjgList);
            if (null == person) {
                validateAssetError(map, "责任人编号" + responsibleCode + "不存在");
                return false;
            }
            if (null != map.get("responsibleName") && StringUtils.isNotEmpty(String.valueOf(map.get("responsibleName")))) {
                String userName = person.getUserName();
                if (!String.valueOf(map.get("responsibleName")).trim().equalsIgnoreCase(userName)) {
                    validateAssetError(map, "责任人编号" + responsibleCode + "与" + responsibleNameTitile + map.get("responsibleName") + "不匹配");
                    return false;
                }
            }
            // 责任单位存在的话，校验是不是当前责任人的责任单位
            if (null != map.get("orgName") && StringUtils.isNotEmpty(String.valueOf(map.get("orgName")))) {
                if (!String.valueOf(map.get("orgName")).equalsIgnoreCase(person.getOrgName())) {
                    validateAssetError(map, person.getUserName() + " 所在" + orgNameTitile + "不是" + map.get("orgName"));
                    return false;
                }
            }
            map.put("responsibleName", person.getUserName());
            map.put("orgName", person.getOrgName());
            map.put("orgCode", person.getOrgCode());
        }
        return true;
    }

    private BasePersonZjg getPersonByCode(String userNo, List<BasePersonZjg> basePersonZjgList) {
        for (BasePersonZjg zig : basePersonZjgList) {
            if (userNo.equalsIgnoreCase(zig.getUserNo())) {
                return zig;
            }
        }
        return null;
    }

    /**
     * 扩展属性字段列处理:主要是为了保存时扩展属性时，识别出那些扩展属性字段
     *
     * @param map
     */
    private void customColumns(Map<String, Object> map) {
        if (null != map.get("customSettings") && map.get("customSettings") instanceof List) {
            List<CustomSettings> tempdata = (List<CustomSettings>) map.get("customSettings");
            if (null == tempdata || tempdata.size() <= 0) {
                return;
            }
            List<String> customsColumns = new ArrayList<String>();
            for (CustomSettings custom : tempdata) {
                if ("custom".equalsIgnoreCase(custom.getAttributeType())) {
                    if (!customsColumns.contains(custom.getName())) {
                        customsColumns.add(custom.getName());
                    }
                }
            }
            if (customsColumns.size() > 0) {
                map.put("customsColumns", customsColumns);
            }
        }
    }


    private AssetType getAssetTypeByName(List<AssetType> types, String assetTypeName,String groupTreeCode) {
        groupTreeCode = groupTreeCode+"-";
        for (AssetType assetType : types) {
            if (assetTypeName.equalsIgnoreCase(assetType.getName()) && assetType.getTreeCode().contains(groupTreeCode)) {
                return assetType;
            }
        }
        return null;
    }

    private AssetTypeGroup getAssetTypeGroupByUniqueCode(List<AssetTypeGroup> types, String uniquecode) {
        for (AssetTypeGroup group : types) {
            if (uniquecode.equalsIgnoreCase(group.getUniqueCode())) {
                return group;
            }
        }
        return null;
    }

    private AssetType getAssetTypeByUniqueCode(List<AssetType> types, String uniquecode) {
        for (AssetType type : types) {
            if (uniquecode.equalsIgnoreCase(type.getUniqueCode())) {
                return type;
            }
        }
        return null;
    }

    private List<Map<String, Object>> selectAssetInfo(String type, List<Map<String, Object>> dataList) {
        List<Map<String, Object>> selectList = new ArrayList<>();
        for (Map<String, Object> map : dataList) {
            String result = map.get("state").toString();
            if (result.equals(type)) {
                selectList.add(map);
            }
        }
        return selectList;
    }

    private void validateAssetError(Map<String, Object> map, String stateDescripe) {
        map.put("stateDescripe", stateDescripe);
        map.put("state", false);
        map.put("guid", UUIDUtils.get32UUID());
    }
}
