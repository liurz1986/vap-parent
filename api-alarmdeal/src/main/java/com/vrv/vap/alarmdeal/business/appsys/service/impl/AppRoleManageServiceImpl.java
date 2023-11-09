package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.appsys.enums.ResourceTypeEnum;
import com.vrv.vap.alarmdeal.business.appsys.model.AppResourceManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppRoleManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.repository.AppRoleManageRepository;
import com.vrv.vap.alarmdeal.business.appsys.service.AppRoleManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.util.EnumTransferUtil;
import com.vrv.vap.alarmdeal.business.appsys.util.MapTypeAdapter;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppRoleManageQueryVo;
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
import java.util.stream.Collectors;

/**
 * @author lps 2021/8/10
 */

@Service
@Transactional
public class AppRoleManageServiceImpl extends AbstractBaseServiceImpl<AppRoleManage,String> implements AppRoleManageService {
    private static Logger logger = LoggerFactory.getLogger(AppRoleManageServiceImpl.class);
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new MapTypeAdapter()).create();

    @Autowired
    private AppRoleManageRepository appRoleManageRepository;

    @Autowired
    private AppSysManagerService appSysManagerService;

    @Autowired
    private MapperUtil mapper;

    private List<AppRoleManage> roles;


    @Override
    public AppRoleManageRepository getRepository(){
        return  appRoleManageRepository;
    }


    /**
     * 应用角色分页查询
     * @param appRoleManageQueryVo
     * @return
     */
    @Override
    public PageRes<AppRoleManage> getAppRoleManagePage(AppRoleManageQueryVo appRoleManageQueryVo){
        PageReq pager = mapper.map(appRoleManageQueryVo, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        List<QueryCondition> queryConditionList=new ArrayList<>();
        Integer appId=appRoleManageQueryVo.getAppId();
        String appName=appRoleManageQueryVo.getAppName();
        String roleName=appRoleManageQueryVo.getRoleName();
        if(appId!=null){
            queryConditionList.add(QueryCondition.eq("appId",appId));
        }
        if(StringUtils.isNotBlank(appName)){
            queryConditionList.add(QueryCondition.like("appName",appName));
        }
        if(StringUtils.isNotBlank(roleName)){
            queryConditionList.add(QueryCondition.like("roleName",roleName));
        }
        Page<AppRoleManage> page=findAll(queryConditionList,pager.getPageable());
        return PageRes.toRes(page);
    }

    /**
     * 应用角色
     * @param appId
     * @return
     */
    @Override
    public List<String> getRoleNames(Integer appId){
        List<QueryCondition> conditions=new ArrayList<>();
        conditions.add(QueryCondition.eq("appId",appId));
        List<AppRoleManage> appRoleManages=findAll(conditions);
        List<String> roleNames=appRoleManages.stream().map(item->item.getRoleName()).collect(Collectors.toList());
        return roleNames;
    }

    /**
     * 应用角色
     * @param appId
     * @return
     */
    @Override
    public List<AppRoleManage> getAllByAppId(Integer appId){
       List<QueryCondition> conditions=new ArrayList<>();
       conditions.add(QueryCondition.eq("appId",appId));
       List<AppRoleManage> appRoleManages=findAll(conditions);
       return appRoleManages;
    }

    /**
     * 导入应用系统，关联导入
     * @param list
     * @param appId
     */
    @Override
    public void getImportData(List<Map<String,Object>> list, Integer appId,List<AppRoleManage> appRoleManages,String oldId,String appName){
        for(Map<String,Object> map : list){
            Map<String,Object> newMap=new HashMap<>();
            mapper.copy(map,newMap);
            for(Map.Entry<String,Object> entry : map.entrySet()){
                String key=entry.getKey();
                Object object=entry.getValue();
                if(object!=null){
                    String val= EnumTransferUtil.nameTransfer(key,object,null,null) ;
                    newMap.put(key,val);
                }
            }
            if(newMap.containsKey("cancelTime") &&StringUtils.isBlank(newMap.get("cancelTime").toString())){
                newMap.remove("cancelTime");
            }
            newMap.remove("appId");
            AppRoleManage appRoleManage=gson.fromJson(JSON.toJSONString(newMap),AppRoleManage.class);
            appRoleManage.setAppId(appId);
            appRoleManage.setAppName(appName);
            appRoleManage.setGuid(UUIDUtils.get32UUID());
            appRoleManage.setCreateTime(new Date());
            appRoleManages.add(appRoleManage);
        }
    }




    /**
     *通过name查询角色
     */
    @Override
    public AppRoleManage getRoleByNameAndAppId(Integer appId, String roleName){
          List<QueryCondition> conditions=new ArrayList<>();
          conditions.add(QueryCondition.eq("roleName",roleName));
          conditions.add(QueryCondition.eq("appId",appId));
          List<AppRoleManage> appRoleManages=findAll(conditions);
          if(appRoleManages.size()==1){
              return appRoleManages.get(0);
          }else {
            throw new RuntimeException("appId"+appId+";roleName:"+roleName+",数据重复或不存在");
          }
    }


    /**
     * 导入校验重构
     * 2022-09-26
     * @param file
     * @return
     */
    @Override
    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file) {
        logger.info("应用角色导入数据校验开始");
        HSSFSheet sheet= null;
        String appId= null;
        String appName=null;
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
            appName = appsys.getAppName();
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
        List<Map<String,Object>> datas = getAppRoleData(excelContent,appId,appName);
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
     * 应用系统导入关联应用角色新增
     * @param roles
     * @return
     */
    @Override
    public Map<String, Object> roleValidate(List<Map<String, Object>> roles) {
        Map<String,Object> result = new HashMap<>();
        result.put("status","success");
        List<String> rolenNames = new ArrayList<>();
        if(CollectionUtils.isEmpty(roles)){
            result.put("roles",rolenNames);
            return result;
        }
        // 初始化数据
        initData();
        for(Map<String, Object> data : roles){
            String roleName = data.get("roleName")==null?"":String.valueOf(data.get("roleName"));
            if(rolenNames.contains(roleName)){
                result.put("status","error");
                result.put("msg","应用角色中存在角色名称重复,"+roleName);
                return result;
            }
            rolenNames.add(roleName);
            Map<String,Object> vResult = validateRoleColumn(data);
            if("error".equals(vResult.get("status"))){
                result.put("status","error");
                result.put("msg",data.get("reason"));
                return result;
            }
        }
        result.put("roles",rolenNames);
        return result;
    }
    private Map<String,Object> validateRoleColumn(Map<String, Object> map) {
        Map<String,Object> result = new HashMap<>();
        result.put("status","success");
        Set<String> keys= map.keySet();
        for(String key : keys){
            if(!validateData(key,map)){
                result.put("status","error");
                return result;
            }
        }
        return result;
    }

    public boolean validateData(String key, Map<String, Object> map) {
        String value = map.get(key)==null?"":String.valueOf(map.get(key));
        Map<String,String> validateResult = isMust(key,value);
        // 必填校验
        if(!"success".equals(validateResult.get("status"))){
            map.put("reason",validateResult.get("message"));
            return false;
        }
        // 有效性校验及转换
        Map<String,String> validateValidity =validateValidityNew(key,value);
        if(!"success".equals(validateValidity.get("status"))){
            map.put("reason",validateValidity.get("message"));
            return false;
        }
        return true;
    }

    /**
     * 角色名称重复性校验--导入数据中
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
            Object roleNameObj = data.get("roleName");
            if(org.springframework.util.StringUtils.isEmpty(roleNameObj)){
                continue;
            }
            String roleName = String.valueOf(roleNameObj);
            if(appNos.contains(roleName)){
                data.put("reason","导入数据中角色名称重复");
                repeatDatas.add(data);
            }else{
                appNos.add(roleName);
            }
        }
        if(repeatDatas.size() >0){
            dataList.removeAll(repeatDatas);
        }
        return repeatDatas;
    }
    private List<Map<String, Object>> getAppRoleData(List<List<String>> excelContent, String appId ,String appName) {
        String[] keys ={"roleName","appRoleDesc","createTime","cancelTime"};
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

    private void initData() {
        roles = this.findAll();
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

    /**
     * 数据校验：
     * "角色名称", "角色描述", "创建时间", "注销时间","应用系统id"
     * "roleName","appRoleDesc","createTime","cancelTime","appId"
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
     * 必填："roleName","appRoleDesc","createTime"
     *      "角色名称", "角色描述", "创建时间"
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> isMust(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "roleName":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("角色名称:"+value+"不能为空");
                }
                break;
            case "appRoleDesc":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("角色描述:"+value+"不能为空");
                }
                break;
            case "createTime":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("创建时间:"+value+"不能为空");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    /**
     * 有效性： "roleName","createTime","cancelTime"
     *  "角色名称",  "创建时间"，"注销时间"
     *  roleName：重复校验
     *  createTime : 格式
     *  cancelTime：格式
     * @param key
     * @param value
     * @param map
     * @return
     */
    private Map<String, String> validateValidity(String key, String value,Map<String, Object> map,String appId) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "roleName":
                if(roleNameExist(value,appId)){
                    return returnEroorResult("角色名称:"+value+"重复");
                }
                break;
            case "createTime":
                if(StringUtils.isNotBlank(value)&&!isDateVail(value)){
                    return returnEroorResult("创建时间:"+value+"格式错误");
                }
                break;
            case "cancelTime":
                if(StringUtils.isNotBlank(value)&&!isDateVail(value)){
                    return returnEroorResult("注销时间:"+value+"格式错误");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    /**
     *有效性：应用系统新增时关联的
     *  "roleName","createTime","cancelTime"
     *  "角色名称",  "创建时间"，"注销时间"
     *  createTime : 格式
     *  cancelTime：格式
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> validateValidityNew(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "createTime":
                if(StringUtils.isNotBlank(value)&&!isDateVail(value)){
                    return returnEroorResult("创建时间:"+value+"格式错误");
                }
                break;
            case "cancelTime":
                if(StringUtils.isNotBlank(value)&&!isDateVail(value)){
                    return returnEroorResult("注销时间:"+value+"格式错误");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    /**
     * 角色名称重复校验
     * @param value
     * @return
     */
    private boolean roleNameExist(String value,String appId) {
        if(CollectionUtils.isEmpty(roles)){
            return false;
        }
        value= value.trim();
        appId=appId.trim();
        for(AppRoleManage role : roles){
            if(value.equals(role.getRoleName().trim())&&appId.equals(String.valueOf(role.getAppId()))){
                return true;
            }
        }
        return false;
    }


    @Override
    protected void dataChangeSendMsg() {

    }
}



