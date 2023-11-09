package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.alarmdeal.business.appsys.dao.AppSysManagerDao;
import com.vrv.vap.alarmdeal.business.appsys.model.AppAccountManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppResourceManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppRoleManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.repository.AppAccountManageRepository;
import com.vrv.vap.alarmdeal.business.appsys.service.AppAccountManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppRoleManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.appsys.util.EnumTransferUtil;
import com.vrv.vap.alarmdeal.business.appsys.util.MapTypeAdapter;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppAccountManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerQueryVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.InternetInfoManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.NetInfoManageVo;
import com.vrv.vap.alarmdeal.business.asset.util.AssetUtil;
import com.vrv.vap.alarmdeal.business.asset.util.ImportExcelUtil;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
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
public class AppAccountManageServiceImpl extends AbstractBaseServiceImpl<AppAccountManage,String> implements AppAccountManageService {
    private static Logger logger = LoggerFactory.getLogger(AppAccountManageServiceImpl.class);
    private static Gson gson = new GsonBuilder()
            .registerTypeAdapter(new TypeToken<Map<String, Object>>() {
            }.getType(), new MapTypeAdapter()).create();

    @Autowired
    private AppAccountManageRepository appAccountManageRepository;

    @Autowired
    private AppRoleManageService appRoleManageService;

    @Autowired
    private AppSysManagerDao appSysManagerDao;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private AppSysManagerService appSysManagerService;

    @Autowired
    private FeignService feignService;
    @Autowired
    private AdminFeign adminFeign;

    // 员工
    private  Map<String,String> personMap=null;
    //角色
    private List<AppRoleManage> roles;
    // 账户
    private List<AppAccountManage> appAccounts = null;

    @Override
    public AppAccountManageRepository getRepository(){
        return appAccountManageRepository;
    }

    /**
     * 应用账号分页查询
     * @param appAccountManageVo
     * @return
     */
    @Override
    public PageRes<AppAccountManage> getAppAccountManagePage(AppAccountManageVo appAccountManageVo){
        List<QueryCondition> conditions=new ArrayList<>();
        String accountName=appAccountManageVo.getAccountName();
        String personNo=appAccountManageVo.getPersonNo();
        Integer appId=appAccountManageVo.getAppId();
        String appRoleId=appAccountManageVo.getAppRoleId();
        if(StringUtils.isNotBlank(accountName)){
            conditions.add(QueryCondition.like("accountName",accountName));
        }
        if(StringUtils.isNotBlank(personNo)){
            conditions.add(QueryCondition.eq("personNo",personNo));
        }
        if(appId!=null){
            conditions.add(QueryCondition.eq("appId",appId));
        }
        if(StringUtils.isNotBlank(appRoleId)){
            conditions.add(QueryCondition.eq("appRoleId",appRoleId));
        }
        PageReq pager = mapperUtil.map(appAccountManageVo, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        Page<AppAccountManage> page=findAll(conditions,pager.getPageable());
        List<AppAccountManage> content = page.getContent();
        if (content.size()>0){
            for (AppAccountManage appAccountManageap:content){
                String personNo1 = appAccountManageap.getPersonNo();
                if (StringUtils.isNotBlank(personNo1)){
                    VData<List<BasePersonZjg>> allPerson = adminFeign.getAllPerson();
                    List<BasePersonZjg> data = allPerson.getData();
                    List<BasePersonZjg> collect = data.stream().filter(a -> a.getUserNo().equals(personNo1)).collect(Collectors.toList());
                    if (collect.size()>0){
                        appAccountManageap.setOrgName(collect.get(0).getOrgName());
                    }
                }
            }
        }
        return PageRes.toRes(page);
    }

    @Override
    public PageRes<Map<String,Object>> getAppAccountAssetPage(AppSysManagerQueryVo appSysManagerQueryVo){
        PageRes<Map<String, Object>> appAccountAssetPage = appSysManagerDao.getAppAccountAssetPage(appSysManagerQueryVo);
        return appAccountAssetPage;
    }


    /**
     * 查询应用下账户
     * @param appId
     * @return
     */
    @Override
    public List<AppAccountManage> getAllByAppId(Integer appId){
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
    public void getImportData(List<Map<String,Object>> list, Integer appId,List<AppAccountManage> appAccountManages,List<AppRoleManage> appRoleManages,String oldId,String appName){
        for(Map<String,Object> map : list){
            Map<String,Object> newMap=new HashMap<>();
            mapperUtil.copy(map,newMap);
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
            AppAccountManage appAccountManage=gson.fromJson(JSON.toJSONString(newMap),AppAccountManage.class);
            AppRoleManage appRoleManage= getRoleByNameAndAppId(appId,appAccountManage.getAppRoleName(),appRoleManages);
            if(null == appRoleManage){
                throw new RuntimeException("应用账号录入中，excel中应用系统id为"+oldId+",roleName:"+appRoleManage.getRoleName()+",在导入的应用角色中不存在");
            }
            appAccountManage.setAppId(appId);
            // appId获取appName的值
            appAccountManage.setAppName(appName);
            appAccountManage.setAppRoleId(appRoleManage.getGuid());
            appAccountManage.setGuid(UUIDUtils.get32UUID());
            appAccountManage.setCreateTime(new Date());
            appAccountManages.add(appAccountManage);
        }
    }

    private AppRoleManage getRoleByNameAndAppId(Integer appId, String appRoleName, List<AppRoleManage> appRoleManages) {
        for(AppRoleManage role : appRoleManages){
            Integer appIdO = role.getAppId();
            String roleName = role.getRoleName();
            if(appId.equals(appIdO)&&appRoleName.equals(roleName)){
                return role;
            }
        }
        return null;
    }

    /**
     * 导入数据保存    2022-04-25
     * @param list
     */
    @Override
    public void saveList(List<Map<String, Object>> list){
        if(null == list || list.size() == 0){
            return;
        }
        List<AppAccountManage> appAccountManages= new ArrayList<>();
        for(Map<String,Object> map : list){
            if(StringUtils.isBlank(map.get("cancelTime").toString())){
                map.remove("cancelTime");
            }
            AppAccountManage appAccountManage=gson.fromJson(JSON.toJSONString(map),AppAccountManage.class);
            appAccountManage.setGuid(UUIDUtils.get32UUID());
            appAccountManage.setCreateTime(new Date());
            appAccountManages.add(appAccountManage);
        }
        this.save(appAccountManages);
    }

    private String getAppNameByAppId(List<AppSysManager> appSysManagers, Integer appId) {
        if(null == appId){
            return "";
        }
        if(null == appSysManagers || appSysManagers.size() ==0){
            return "";
        }
        for(AppSysManager app : appSysManagers){
            if(appId.equals(app.getId())){
                return app.getAppName();
            }
        }
        return "";
    }

    @Override
    protected void dataChangeSendMsg() {

    }

    /**
     * 数据导入校验   2022-09-26
     * @param file
     * @return
     */
    @Override
    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file){
        logger.info("账户管理导入数据校验开始");
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
        List<Map<String,Object>> datas = getAccountManageData(excelContent,appId,appName);
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
     * 应用系统导入关联应用账号校验
     * @param roleNames
     * @param accounts
     * @return
     */
    @Override
    public Map<String, Object> accountValidate(List<String> roleNames, List<Map<String, Object>> accounts) {
        Map<String,Object> result = new HashMap<>();
        result.put("status","success");
        if(CollectionUtils.isEmpty(accounts)){
            return result;
        }
        if(CollectionUtils.isEmpty(roleNames)){
            result.put("status","error");
            result.put("msg","当前应用下应用角色为空，应用账号有数据！");
            return result;
        }
        // 人员
        personMap=feignService.getPersonMap();
        List<String> accountNames = new ArrayList<>(); // 导入账号名重复
        for(Map<String, Object> data : accounts){
            String accountName = data.get("accountName").toString();
            if(accountNames.contains(accountName)){
                result.put("status","error");
                result.put("msg","应用账号中存在账号名称重复,"+accountName);
                return result;
            }
            accountNames.add(accountName);
            Map<String,Object> vResult = validateAccountColumn(data,roleNames);
            if("error".equals(vResult.get("status"))){
                result.put("status","error");
                result.put("msg",data.get("reason"));
                return result;
            }
        }
        return result;
    }
    private Map<String, Object> validateAccountColumn(Map<String, Object> data, List<String> roleNames) {
        Map<String,Object> result = new HashMap<>();
        result.put("status","success");
        Set<String> keys= data.keySet();
        for(String key : keys){
            if(!validateData(key,data,roleNames)){
                result.put("status","error");
                return result;
            }
        }
        return result;
    }


    public boolean validateData(String key, Map<String, Object> map,List<String> roleNames) {
        String value = map.get(key)==null?"":String.valueOf(map.get(key));
        Map<String,String> validateResult = isMust(key,value);
        // 必填校验
        if(!"success".equals(validateResult.get("status"))){
            map.put("reason",validateResult.get("message"));
            return false;
        }
        // 有效性校验及转换
        Map<String,String> validateValidity =validateValidityNew(key,value,map,roleNames);
        if(!"success".equals(validateValidity.get("status"))){
            map.put("reason",validateValidity.get("message"));
            return false;
        }
        return true;
    }



    /**
     * 数据校验：
     * "账户名称", "用户编号", "姓名","角色名称", "创建时间", "注销时间","登录IP","应用系统id"
     * "accountName","personNo","name","appRoleName","createTime","cancelTime","ip","appId"
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
     * 必填："accountName","personNo","name","appRoleName","createTime"
     * @param key
     * @param value
     * @return
     */
    private Map<String, String> isMust(String key, String value) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "accountName":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("账户名:"+value+"不能为空");
                }
                break;
            case "personNo":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("用户编:"+value+"不能为空");
                }
                break;
            case "name":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("姓名:"+value+"不能为空");
                }
                break;
            case "appRoleName":
                if(StringUtils.isBlank(value)){
                    return returnEroorResult("角色名称:"+value+"不能为空");
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
     * 有效性：
     *  accountName：账户名重复校验
     *  personNo：有效性，同时更新对应name
     *  appRoleName：有效性
     *  createTime：时间格式
     *  ip：ip格式
     *  cancelTime：格式
     * @param key
     * @param value
     * @param map
     * @return
     */
    private Map<String, String> validateValidity(String key, String value,Map<String, Object> map,String appId) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "accountName":
                if(accountNameExist(value,appId)){
                    return returnEroorResult("账户名:"+value+"重复");
                }
                break;
            case "personNo":
                if(!personMap.containsKey(value)){
                    return returnEroorResult("用户编号:"+value+"不存在");
                }
                String  userName=personMap.get(value);
                map.put("name",userName);
                break;
            case "appRoleName":
                if(!appRoleNameIsExist(value,appId)){
                    return returnEroorResult("角色名称:"+value+"不存在");
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
            case "ip":
                if(StringUtils.isNotBlank(value)&&!AssetUtil.checkIP(value)){
                    return returnEroorResult("登录IP:"+value+",格式异常");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }

    /**
     * 有效性：应用系统新增时关联的
     *  personNo：有效性，同时更新对应name
     *  appRoleName：有效性
     *  createTime：时间格式
     *  ip：ip格式
     *  cancelTime：格式
     * @param key
     * @param value
     * @param map
     * @return
     */
    private Map<String, String> validateValidityNew(String key, String value, Map<String, Object> map,List<String> roleNames) {
        Map<String, String> result = new HashMap<>();
        switch (key){
            case "personNo":
                if(!personMap.containsKey(value)){
                    return returnEroorResult("用户编号:"+value+"不存在");
                }
                String  userName=personMap.get(value);
                map.put("name",userName);
                break;
            case "appRoleName":
                if(!roleNames.contains(value)){
                    return returnEroorResult("角色名称:"+value+"不存在");
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
            case "ip":
                if(StringUtils.isNotBlank(value)&&!AssetUtil.checkIP(value)){
                    return returnEroorResult("登录IP:"+value+",格式异常");
                }
                break;
            default:
                break;
        }
        result.put("status","success");
        return result;
    }
    // 判断角色是不是在当前应用系统下
    private boolean appRoleNameIsExist(String value,String appId) {
        if(CollectionUtils.isEmpty(roles)){
            return false;
        }
        value= value.trim();
        appId = appId.trim();
        for(AppRoleManage role : roles){
            if(value.equals(role.getRoleName().trim())&&appId.equals(String.valueOf(role.getAppId()))){
                return true;
            }
        }
        return false;
    }

    // 判断账户名称是不是存在
    private boolean accountNameExist(String value,String appId) {
        if(CollectionUtils.isEmpty(appAccounts)){
            return false;
        }
        for(AppAccountManage account : appAccounts){
            if(value.equals(account.getAccountName().trim())&&appId.equals(String.valueOf(account.getAppId()))){
                return true;
            }
        }
        return false;
    }

    /**
     * 账户名称重复性校验--导入数据中
     * @param dataList
     * @return
     */
    public  List<Map<String, Object>> repeatHandle(List<Map<String, Object>> dataList) {
        List<Map<String, Object>> repeatDatas = new ArrayList<Map<String, Object>>();
        if(null == dataList || dataList.size() == 0){
            return null;
        }
        List<String> appNos = new ArrayList<String>();
        for(Map<String, Object> data: dataList){
            Object accountNameObj = data.get("accountName");
            if(org.springframework.util.StringUtils.isEmpty(accountNameObj)){
                continue;
            }
            String accountName = String.valueOf(accountNameObj);
            if(appNos.contains(accountName)){
                data.put("reason","导入数据中账户名称重复");
                repeatDatas.add(data);
            }else{
                appNos.add(accountName);
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
        // 人员
        personMap=feignService.getPersonMap();
        // 角色
        roles = appRoleManageService.findAll();
        appAccounts = this.findAll();
    }

    private List<Map<String, Object>> getAccountManageData(List<List<String>> excelContent,String appId,String appName) {
        String[] keys ={"accountName","personNo","name","appRoleName","createTime","cancelTime","ip"};
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
}
