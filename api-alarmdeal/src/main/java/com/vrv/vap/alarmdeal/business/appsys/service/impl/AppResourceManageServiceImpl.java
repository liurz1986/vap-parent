package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.appsys.dao.AppSysManagerDao;
import com.vrv.vap.alarmdeal.business.appsys.enums.ResourceTypeEnum;
import com.vrv.vap.alarmdeal.business.appsys.model.AppResourceManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.repository.AppResourceManageRepository;
import com.vrv.vap.alarmdeal.business.appsys.service.AppResourceManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.util.EnumTransferUtil;
import com.vrv.vap.alarmdeal.business.appsys.util.MapTypeAdapter;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppResourceManageVo;
import com.vrv.vap.alarmdeal.business.asset.util.AssetUtil;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

/**
 * @author lps 2021/8/10
 */

@Service
@Transactional
public class AppResourceManageServiceImpl extends AbstractBaseServiceImpl<AppResourceManage,String> implements AppResourceManageService {
    private static Logger logger = LoggerFactory.getLogger(AppResourceManageServiceImpl.class);
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new MapTypeAdapter()).create();

    @Autowired
    private AppResourceManageRepository appResourceManageRepository;

    @Autowired
    private AppSysManagerDao appSysManagerDao;

    @Autowired
    private AppSysManagerService appSysManagerService;



    @Autowired
    private MapperUtil mapperUtil;

    private List<AppResourceManage> appResources;

    @Override
    public AppResourceManageRepository getRepository(){
        return appResourceManageRepository;
    }

    /**
     * 应用系统资源分页查询
     * @param appResourceManageVo
     * @return
     */
    @Override
    public PageRes<AppResourceManage> getAppResourceManagePage(AppResourceManageVo appResourceManageVo){
        List<QueryCondition> conditionList=new ArrayList<>();
        Integer appId=appResourceManageVo.getAppId();
        String appResourceNo=appResourceManageVo.getAppResourceNo();
        Integer resourceType=appResourceManageVo.getResourceType();
        if(appId!=null){
            conditionList.add(QueryCondition.eq("appId",appId));
        }
        if(StringUtils.isNotBlank(appResourceNo)){
            conditionList.add(QueryCondition.like("appResourceNo",appResourceNo));
        }
        if(!org.springframework.util.StringUtils.isEmpty(resourceType)){
            conditionList.add(QueryCondition.eq("resourceType",resourceType));
        }
        PageReq pager = mapperUtil.map(appResourceManageVo, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        Page<AppResourceManage> page=findAll(conditionList,pager.getPageable());
        return PageRes.toRes(page);
    }

    /**
     * 查询应用系统所有资源
     * @param appId
     * @return
     */
    @Override
    public List<AppResourceManage> getAllByAppId(Integer appId){
        List<QueryCondition> queryConditionList=new ArrayList<>();
        queryConditionList.add(QueryCondition.eq("appId",appId));
        return  findAll(queryConditionList);
    }

    /**
     * 导入应用系统，关联导入
     * @param list
     * @param appId
     */
    @Override
    public void getImportData(List<Map<String,Object>> list, Integer appId, List<AppResourceManage> appResourceManages,String appName){
        for(Map<String,Object> map : list) {
            Map<String,Object> newMap=new HashMap<>();
            mapperUtil.copy(map,newMap);
            for (Map.Entry<String, Object> entry : map.entrySet()) {
                String key = entry.getKey();
                Object object = entry.getValue();
                if (object != null) {
                    String val = EnumTransferUtil.nameTransfer(key, object,null,null);
                    newMap.put(key, val);
                }
            }
            newMap.remove("appId");
            AppResourceManage appResourceManage = gson.fromJson(JSON.toJSONString(newMap), AppResourceManage.class);
            appResourceManage.setAppId(appId);
            appResourceManage.setAppName(appName);
            appResourceManage.setGuid(UUIDUtils.get32UUID());
            appResourceManage.setCreateTime(new Date());
            appResourceManages.add(appResourceManage);
        }
    }

    /**
     * 某应用资源类型分布数据
     * @return
     */
   @Override
   public Map<String,Object> countResourceGroupByType(Integer appId){
        return appSysManagerDao.countResourceGroupByType(appId);

    }

    /**
     * 导入校验重构
     * 2022-09-26
     * @param file
     * @return
     */
    @Override
    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file) {
        logger.info("资源管理导入数据校验开始");
        HSSFSheet sheet= null;
        String appId= null;
        String appName= null;
        try {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            appId  = getSheetName(workbook);  // 获取第一个sheet页名称
            if(StringUtils.isEmpty(appId)){
                logger.info("excel不存在sheet");
                return null;
            }
            // 账户管理中sheet名称就是应用系统id，校验对应的应用系统是否存在
            AppSysManager appsys= appSysManagerService.getOne(Integer.parseInt(appId));
            if(null ==appsys){
                logger.error("对应的应用系统不存在，"+appId);
                throw new AlarmDealException(-1,"对应的应用系统不存在，"+appId);
            }
            appName= appsys.getAppName();
            sheet = workbook.getSheet(appId);
        } catch (IOException e) {
            logger.error("IOException: {}", e);
            return null;
        }
        // 初始化数据
        initData();
        // 获取excel数据
        List<List<String>> excelContent = ImportExcelUtil.getExcelContent(sheet);
        // 数据组装
        List<Map<String,Object>> datas = getAppResourceData(excelContent,appId,appName);
        // 数据去重处理
        List<Map<String, Object>> repeatDatas = repeatHandle(datas);
        // 数据校验
        Map<String,List<Map<String,Object>>> result=checkData(datas,appId);
        // 存在重复校验数据，将结果加入最终校验结果中
        if(null != repeatDatas){
            List<Map<String, Object>> failDatas = result.get("false");
            failDatas.addAll(repeatDatas);
            result.put("false",failDatas);
        }
        return result;
    }
    /**
     *  应用资源校验
     * @param accounts
     * @return
     */
    @Override
    public Map<String, Object> resourceValidate(List<Map<String, Object>> accounts) {
        Map<String,Object> result = new HashMap<>();
        result.put("status","success");
        if(CollectionUtils.isEmpty(accounts)){
            return result;
        }
        List<String> resourceNos = new ArrayList<>(); // 导入资源编号重复
        for(Map<String, Object> data : accounts){
            String appResourceNo=  data.get("appResourceNo")==null?"":String.valueOf(data.get("appResourceNo"));
            if(resourceNos.contains(appResourceNo)){
                result.put("status","error");
                result.put("msg","应用资源资产编号重复，"+appResourceNo);
                return result;
            }
            resourceNos.add(appResourceNo);
            Map<String,Object> vResult = validateResourceColumn(data);
            if("error".equals(vResult.get("status"))){
                result.put("status","error");
                result.put("msg",data.get("reason"));
                return result;
            }
        }
        return result;
    }
    private Map<String, Object> validateResourceColumn(Map<String, Object> data) {
        Map<String,Object> result = new HashMap<>();
        result.put("status","success");
        Set<String> keys= data.keySet();
        for(String key : keys){
            if(!validateData(key,data)){
                result.put("status","error");
                return result;
            }
        }
        return result;
    }


    private boolean validateData(String key, Map<String, Object> map) {
        String value = map.get(key)==null?"":String.valueOf(map.get(key));
        Map<String,String> validateResult = isMust(key,value);
        // 必填校验
        if(!"success".equals(validateResult.get("status"))){
            map.put("reason",validateResult.get("message"));
            return false;
        }
        // 有效性校验及转换
        Map<String,String> validateValidity =validateValidityNew(key,value,map);
        if(!"success".equals(validateValidity.get("status"))){
            map.put("reason",validateValidity.get("message"));
            return false;
        }
        return true;
    }

    private Map<String, String> validateValidityNew(String key, String value, Map<String, Object> map) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "appResourceUrl":
                if(!urlFormatValidate(value)){
                    return returnEroorResult("URL:"+value+"格式不正确");
                }
                break;
            case "resourceType":
                if(ResourceTypeEnum.getCodeByName(value) .equalsIgnoreCase("-1")){
                    return returnEroorResult("资源类别:"+value+"不存在");
                }
                map.put(key,ResourceTypeEnum.getCodeByName(value)); //转换
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }


    /**
     * 资源编号重复性校验--导入数据中
     * @param dataList
     * @return
     */
    protected  List<Map<String, Object>> repeatHandle(List<Map<String, Object>> dataList) {
        List<Map<String, Object>> repeatDatas = new ArrayList<Map<String, Object>>();
        if(null == dataList || dataList.size() == 0){
            return null;
        }
        List<String> appNos = new ArrayList<String>();
        for(Map<String, Object> data: dataList){
            Object appResourceNoObj = data.get("appResourceNo");
            if(org.springframework.util.StringUtils.isEmpty(appResourceNoObj)){
                continue;
            }
            String appResourceNo = String.valueOf(appResourceNoObj);
            if(appNos.contains(appResourceNo)){
                data.put("reason","导入数据中资源编号重复");
                repeatDatas.add(data);
            }else{
                appNos.add(appResourceNo);
            }
        }
        if(repeatDatas.size() >0){
            dataList.removeAll(repeatDatas);
        }
        return repeatDatas;
    }
    // 获取第一个非隐藏的sheet
    private String getSheetName(HSSFWorkbook workbook) {
        int sheets =  workbook.getNumberOfSheets();
        for(int i =0 ;i < sheets;i++){
            HSSFSheet sheet = workbook.getSheetAt(i);
            if(sheet.isColumnHidden(i)){ // 隐藏sheet不处理
                continue;
            }
            return sheet.getSheetName();
        }
        return  "";
    }

    private void initData() {
        appResources = this.findAll();

    }
    private List<Map<String, Object>> getAppResourceData(List<List<String>> excelContent,String appId, String appName) {
        String[] keys ={"appResourceNo","appResourceUrl","resourceType"};
        List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
        for(List<String> data : excelContent){
            Map<String,Object> map=new HashMap<>();
            for(int i=0;i<keys.length;i++){
                map.put(keys[i],data.get(i));
            }
            map.put("appId",appId);
            map.put("appName",appName);
            dataList.add(map);
        }
        return dataList;
    }
    /**
     * 数据校验：
     * "资源编号", "URL", "资源类别","应用系统id"
     * "appResourceNo","appResourceUrl","resourceType","appId"
     * @param datas
     * @return
     */
    private Map<String, List<Map<String, Object>>> checkData(List<Map<String, Object>> datas,String appId) {
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        List<Map<String,Object>> trueList=new ArrayList<>();
        List<Map<String,Object>> falseList=new ArrayList<>();
        for(Map<String,Object> map : datas){
            if(!checkColumn(map,appId)){
                falseList.add(map);
            }else{
                trueList.add(map);
            }
        }
        result.put("true",trueList);
        result.put("false",falseList);
        return result;
    }
    private boolean checkColumn(Map<String, Object> map,String appId) {
        Set<String> keys= map.keySet();
        for(String key : keys){
            if(!validateData(key,map,appId)){
                return false;
            }
        }
        return true;

    }
    private boolean validateData(String key, Map<String, Object> map,String appId) {
        String value = map.get(key)==null?"":String.valueOf(map.get(key));
        Map<String,String> validateResult = isMust(key,value);
        // 必填校验
        if(!"success".equals(validateResult.get("status"))){
            map.put("reason",validateResult.get("message"));
            return false;
        }
        // 有效性校验及转换
        Map<String,String> validateValidity =validateValidity(key,value,map,appId);
        if(!"success".equals(validateValidity.get("status"))){
            map.put("reason",validateValidity.get("message"));
            return false;
        }
        return true;
    }
    /**
     * 必填："appResourceNo","appResourceUrl","resourceType"
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> isMust(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "appResourceNo":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("资源编号:"+value+"不能为空");
                }
                break;
            case "appResourceUrl":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("URL:"+value+"不能为空");
                }
                break;
            case "resourceType":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("资源类别:"+value+"不能为空");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    /**
     * 有效性： "appResourceNo","appResourceUrl","resourceType"
     *  appResourceNo：重复校验
     *  appResourceUrl : 格式
     *  resourceType：有效性
     * @param key
     * @param value
     * @param map
     * @return
     */
    private Map<String, String> validateValidity(String key, String value,Map<String, Object> map,String appId) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "appResourceNo":
                if(appResourceNoExist(value,appId)){
                    return returnEroorResult("资源编号:"+value+"重复");
                }
                break;
            case "appResourceUrl":
                if(!urlFormatValidate(value)){
                    return returnEroorResult("URL:"+value+"格式不正确");
                }
                break;
            case "resourceType":
                if(ResourceTypeEnum.getCodeByName(value) .equalsIgnoreCase("-1")){
                    return returnEroorResult("资源类别:"+value+"不存在");
                }
                map.put(key,ResourceTypeEnum.getCodeByName(value));  //转换
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }




    /**
     * 资源编号重复校验
     * @param value
     * @return
     */
    private boolean appResourceNoExist(String value,String appId) {
        if(CollectionUtils.isEmpty(appResources)){
            return false;
        }
        value = value.trim();
        appId= appId.trim();
        for(AppResourceManage resourceManage : appResources){
            if(value.equals(resourceManage.getAppResourceNo().trim())&&appId.equals(String.valueOf(resourceManage.getAppId()))){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void dataChangeSendMsg() {

    }
}
