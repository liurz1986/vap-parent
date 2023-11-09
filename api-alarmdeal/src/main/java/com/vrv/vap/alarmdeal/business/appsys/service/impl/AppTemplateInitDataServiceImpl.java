package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.vrv.vap.alarmdeal.business.appsys.service.AppTemplateInitDataService;
import com.vrv.vap.alarmdeal.business.appsys.vo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用系统相关导入模板中预置数据
 * 1.互联网信息 2.网络信息 3.数据属性 4.应用角色 5. 应用账号 6.应用资源 7. 应用系统
 * 2022-09-23
 */
@Service
public class AppTemplateInitDataServiceImpl implements AppTemplateInitDataService {
    private Logger logger = LoggerFactory.getLogger(AppTemplateInitDataServiceImpl.class);

    @Override
    public Map<String,List<List<String>>> getInitDataByType(String type) {
        switch (type){
            case InternetInfoManageVo.INTERNET_INFO_MANAGE:  // 互联网信息  <互联单位", "接入方式", "涉密等级", "防护等级>
                return getInternet();
            case NetInfoManageVo.NET_INFO_MANAGE:  // 网络信息
                return getNetinfo();
            case DataInfoManageVo.DATA_INFO_MANAGE:  // 数据属性 "数据标识", "业务类型", "涉密等级","文件名称","文件类型","文件大小(MB)","文件管理状态"
                return getDataInfo();
            case AppSysManagerVo.APP_SYS_MANAGER:  // 应用系统；联动应用账号、应用资源、应用角色
                return getAppSysManager();
            case AppAccountManageVo.APP_ACCOUNT_MANAGE:  // 应用账号  ("账户名称", "用户编号", "姓名","角色名称", "创建时间", "注销时间","登录IP","应用系统id")
                return getAppAccount();
            case AppResourceManageVo.APP_RESOURCE_MANAGE:  // 应用资源 "资源编号", "URL", "资源类别","应用系统id"
                return getAppResource();
            case AppRoleManageVo.APP_ROLE_MANAGE:  // 应用角色 "角色名称", "角色描述", "创建时间", "注销时间","应用系统id"
                return getAppRole();
        }
        return null;
    }



    // 互联网信息  <互联单位", "接入方式", "涉密等级", "防护等级>
    private Map<String,List<List<String>>> getInternet() {
        Map<String,List<List<String>>> param = new HashMap<>();
        List<String> data = new ArrayList<>();
        List<List<String>> reslut = new ArrayList<>();
        data.add("xxx保密单位");
        data.add("网络接入");
        data.add("非密");
        data.add("机密一般");
        reslut.add(data);
        param.put("main",reslut);
        return param;
    }
   //  网络信息 ("网络名称", "网络类型", "涉密等级","安全域", "防护等级")
    private Map<String,List<List<String>>> getNetinfo() {
        Map<String,List<List<String>>> param = new HashMap<>();
        List<String> data = new ArrayList<>();
        List<List<String>> reslut = new ArrayList<>();
        data.add("xxx网络名称");
        data.add("局域网");
        data.add("非密");
        data.add("远程管理域");
        data.add("机密一般");
        reslut.add(data);
        param.put("main",reslut);
        return param;
    }
    // 数据属性 "数据标识", "业务类型", "涉密等级","文件名称","文件类型","文件大小(MB)","文件管理状态"
    private  Map<String,List<List<String>>> getDataInfo() {
        Map<String,List<List<String>>> param = new HashMap<>();
        List<String> data = new ArrayList<>();
        List<List<String>> reslut = new ArrayList<>();
        data.add("b26e6eeb5ab64b91b673463da67a974f");
        data.add("未知");
        data.add("非密");
        data.add("xx.txt");
        data.add("txt");
        data.add("0.01");
        data.add("xxx");
        reslut.add(data);
        param.put("main",reslut);
        return param;
    }
    // 应用账号  ("账户名称", "用户编号", "姓名","角色名称", "创建时间", "注销时间","登录IP","应用系统id")
    private  Map<String,List<List<String>>> getAppAccount() {
        Map<String,List<List<String>>> param = new HashMap<>();
        List<String> data = new ArrayList<>();
        List<List<String>> reslut = new ArrayList<>();
        data.add("testxx");
        data.add("001");
        data.add("张三");
        data.add("testxx");
        data.add("2022-01-19 18:04:25");
        data.add("2022-05-19 18:04:25");
        data.add("1.0.0.0");
        reslut.add(data);
        param.put("main",reslut);
        return param;
    }

    //  应用资源 "资源编号", "URL", "资源类别","应用系统id"
    private Map<String,List<List<String>>> getAppResource() {
        Map<String,List<List<String>>> param = new HashMap<>();
        List<String> data = new ArrayList<>();
        List<List<String>> reslut = new ArrayList<>();
        data.add("001");
        data.add("http://xxx.com");
        data.add("管理");
        reslut.add(data);
        param.put("main",reslut);
        return param;
    }

    // 应用角色 "角色名称", "角色描述", "创建时间", "注销时间","应用系统id"
    private Map<String,List<List<String>>> getAppRole() {
        Map<String,List<List<String>>> param = new HashMap<>();
        List<String> data = new ArrayList<>();
        List<List<String>> reslut = new ArrayList<>();
        data.add("testxx");
        data.add("test描述");
        data.add("2022-01-19 18:04:25");
        data.add("2022-05-19 18:04:25");
        reslut.add(data);
        param.put("main",reslut);
        return param;
    }


    // 应用系统；联动应用账号、应用资源、应用角色
    private Map<String, List<List<String>>> getAppSysManager() {
        Map<String,List<List<String>>> param = new HashMap<>();
        List<String> data = new ArrayList<>();
        // 应用账号
        List<List<String>> account = new ArrayList<>();
        data.add("testxx");
        data.add("001");
        data.add("张三");
        data.add("testxx");
        data.add("2022-01-19 18:04:25");
        data.add("2022-05-19 18:04:25");
        data.add("1.0.0.0");
        data.add("1");
        account.add(data);
        param.put("account",account);
        // 应用资源
        data = new ArrayList<>();
        List<List<String>> resouces = new ArrayList<>();
        data.add("001");
        data.add("http://xxx.com");
        data.add("管理");
        data.add("1");
        resouces.add(data);
        param.put("resouces",resouces);

        // 应用角色
        data = new ArrayList<>();
        List<List<String>> role = new ArrayList<>();
        data.add("testxx");
        data.add("test描述");
        data.add("2022-01-19 18:04:25");
        data.add("2022-05-19 18:04:25");
        data.add("1");
        role.add(data);
        param.put("role",role);

        // 应用系统
        data = new ArrayList<>();
        List<List<String>> appsys = new ArrayList<>();
        data.add("1");
        data.add("111");
        data.add("xxx应用名称");
        data.add("武汉市保密办");
        data.add("非密");
        data.add("xx厂商");
        data.add("https://xx.com");
        data.add("xx");
        data.add("xx");
        appsys.add(data);
        param.put("main",appsys);
        return param;
    }


}
