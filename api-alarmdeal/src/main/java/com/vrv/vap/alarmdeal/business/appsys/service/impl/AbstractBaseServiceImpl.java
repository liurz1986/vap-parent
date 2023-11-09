package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.vrv.vap.alarmdeal.business.appsys.enums.*;
import com.vrv.vap.alarmdeal.business.appsys.service.AbstractBaseService;
import com.vrv.vap.alarmdeal.business.appsys.service.AppRoleManageService;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppAccountManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppRoleManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerVo;
import com.vrv.vap.alarmdeal.business.asset.vo.ExcelValidationData;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/18
 */

public abstract class AbstractBaseServiceImpl<T, ID extends Serializable> extends BaseServiceImpl<T,ID> implements AbstractBaseService<T,ID> {


    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private FeignService feignService;

    @Autowired
    private AppRoleManageService appRoleManageService;

    public static final String PATTERN_DEFAULT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 校验字段 true-正确，false-错误
     * @param map(key-需要校验的字段，value-对应的值)
     * @return
     */
    @Override
    public Boolean checkParam(String key, String value) {
        Boolean bool = true;
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq(key, value));
        Long count = count(conditions);
        if (count > 0) {
            bool = false;
        }
        return bool;
    }

    /**
     * 应用系统关联的账号,资源，服务器需现在在单个应用
     * 校验字段 true-正确，false-错误
     * @param map(key-需要校验的字段，value-对应的值)
     * @return
     */
    @Override
    public Boolean checkParam(String key, String value, Integer appId) {
        Boolean bool = true;
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.eq(key, value));
        conditions.add(QueryCondition.eq("appId",appId));
        Long count = count(conditions);
        if (count > 0) {
            bool = false;
        }
        return bool;
    }




    @Override
    public void saveMap(Map<String,Object> map, Class<T> classOfT){
        T t=mapperUtil.map(map,classOfT);
        // 数据变更发送消息 2022-06-01
        dataChangeSendMsg();
        save(t);
    }

    /**
     * 数据变更发送消息
     */
    protected abstract void dataChangeSendMsg();

    /**
     * excel 每列格式
     * @param exportType
     * @param index
     * @param colName
     * @param sheetName
     * @return
     */
    @Override
    public ExcelValidationData  getExcelValidationData(String exportType, int index, String colName, String sheetName,String[] classifiedLevels,String[]  protectLevelValues, List<String> domainNames ) {
        ExcelValidationData excelValidationData=null;
        switch (colName){
            case "涉密等级":
                String[] levels =classifiedLevels; //改为字典获取
                excelValidationData=new ExcelValidationData(index, levels, "必填", "下拉框选择");
                break;
            case "单位名称":
                List<String> orgNameList=feignService.getOrgName();
                String[]  departments= orgNameList.toArray(new String[orgNameList.size()]);
                excelValidationData=new ExcelValidationData(index, departments, "必填", "下拉框选择");
                break;
            case "防护等级":
                excelValidationData=new ExcelValidationData(index, protectLevelValues, "必填", "下拉框选择");
                break;
            case "接入方式":
                String[]  netType= InternetTypeNum.getEnumNames();
                excelValidationData=new ExcelValidationData(index, netType, "必填", "下拉框选择");
                break;
            case "网络类型":
                String[]  internetType= NetTypeNum.getEnumNames();
                excelValidationData=new ExcelValidationData(index, internetType, "必填", "下拉框选择");
                break;
            case "资源类别":
                String[]  resourceTypes= ResourceTypeEnum.getEnumNames();
                excelValidationData=new ExcelValidationData(index, resourceTypes, "必填", "下拉框选择");
                break;
            case "安全域":
                String[]  domains= domainNames.toArray(new String[domainNames.size()]);
                excelValidationData=new ExcelValidationData(index, domains, "必填", "下拉框选择");
                break;
            case "用户编号":
                List<String>  personList= feignService.getPersonNo();
                String[]  personNos=personList.toArray(new String[personList.size()]);
                excelValidationData=new ExcelValidationData(index, personNos, "必填", "下拉框选择");
                break;
            case "角色名称":
                if(StringUtils.isNumeric(exportType)){
                    if(!sheetName.equals(AppRoleManageVo.APP_ROLE_MANAGE)){
                        List<String> roleNames= appRoleManageService.getRoleNames(Integer.valueOf(exportType));
                        String[] strArray = new String[roleNames.size()];
                        roleNames.toArray(strArray);
                        excelValidationData=new ExcelValidationData(index, strArray, "必填", "角色名称必填");
                    }else{
                        excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "角色名称不能重复");
                    }
                }else{
                    if(exportType.equals(AppRoleManageVo.APP_ROLE_MANAGE)){
                        excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "同一应用系统id下，角色名称不能重复");
                    }else if(exportType.equals(AppAccountManageVo.APP_ACCOUNT_MANAGE)){
                        excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "角色名称，必须在'应用角色中存在'");
                    }
                }
                break;
            case "域名":
            case "URL":
                excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "格式如: https://www.baidu.com");
                break;
            case "账户名称" :
                if(StringUtils.isNumeric(exportType)){
                    excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "账号名称不能重复");
                }else{
                    excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "同一应用系统id下，账号名称不能重复");
                }
                break;
            case "资源编号" :
                if(StringUtils.isNumeric(exportType)){
                    excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "同一应用系统id下，资源编号不能重复");
                }else{
                    excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "资源编号不能重复");
                }

                break;
            case "应用系统id":
                if(!exportType.equals(AppSysManagerVo.APP_SYS_MANAGER)){
                    excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "当前模板中，应用系统id均在需在'应用系统信息'中存在");
                }else{
                    excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "不能为空");
                }
                break;
            case "创建时间":
                excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "格式：yyyy-MM-dd HH:mm:ss");
                break;
            case "注销时间":
                excelValidationData=new ExcelValidationData(index, 1, 50, null, null, " ", "格式：yyyy-MM-dd HH:mm:ss");
                break;
            case "互联单位":
            case "网络名称":
            case "数据标识":
                excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填",colName+"不能重复" );
                break;
            case "文件名称":
            case "文件类型":
            case "文件大小(MB)":
            case "文件管理状态":
            case "登录IP":
                excelValidationData=new ExcelValidationData(index, 1, 500, null, null, null, null);
                break;
            default:
                excelValidationData=new ExcelValidationData(index, 1, 50, null, null, "必填", "不能为空");
                break;
        }
        return excelValidationData;
    }

    // 时间格式校验
    public Boolean isDateVail(String date) {
        //用于指定 日期/时间 模式
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(PATTERN_DEFAULT);
        boolean flag = true;
        try {
            //Java 8 新添API 用于解析日期和时间
            LocalDateTime.parse(date, dtf);
        } catch (Exception e) {
            flag = false;
        }
        return flag;
    }
    /**
     * URL格式校验：
     * http:// 或https://开头
     * @param value
     * @return
     */
    public boolean urlFormatValidate(String value) {
        // 以指定的前缀开始:以起始位置0开始
        if(value.startsWith("http://",0)||value.startsWith("https://",0)){
            return true;
        }
        return false;
    }

    public Map<String, String> returnEroorResult(String message) {
        Map<String, String> result = new HashMap<>();
        result.put("message",message);
        result.put("status","error");
        return result;
    }

}
