package com.vrv.vap.alarmdeal.business.appsys.service;


import com.vrv.vap.alarmdeal.business.appsys.model.AppAccountManage;
import com.vrv.vap.alarmdeal.business.appsys.model.AppRoleManage;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppAccountManageVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerQueryVo;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/10
 */

public interface AppAccountManageService extends  AbstractBaseService<AppAccountManage,String> {


   /**
    * 应用账号分页查询
    * @param appAccountManageVo
    * @return
    */
   PageRes<AppAccountManage> getAppAccountManagePage(AppAccountManageVo appAccountManageVo);

   /**
    * 应用资产分页查询
    * @param appSysManagerQueryVo
    * @return
    */
   PageRes<Map<String,Object>> getAppAccountAssetPage(AppSysManagerQueryVo appSysManagerQueryVo);

   /**
    * 查询应用下账户
    * @param appId
    * @return
    */
   public List<AppAccountManage> getAllByAppId(Integer appId);

   /**
    * 导入应用系统，关联导入
    * @param list
    * @param appId
    */
   public void getImportData(List<Map<String, Object>> list, Integer appId,List<AppAccountManage> appAccountManages,List<AppRoleManage> appRoleManages,String oldId,String appName);

   /**
    * 导入数据保存    2022-04-25
    * @param list
    */
   public void saveList(List<Map<String, Object>> list);

   /**
    * 数据导入校验   2022-09-26
    * @param file
    * @return
    */
   public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file);


   public Map<String, Object> accountValidate(List<String> roleNames, List<Map<String, Object>> accounts);
}
