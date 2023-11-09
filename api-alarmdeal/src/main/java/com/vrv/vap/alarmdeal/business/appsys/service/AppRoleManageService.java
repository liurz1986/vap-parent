package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.business.appsys.model.AppRoleManage;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppRoleManageQueryVo;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/10
 */

public interface AppRoleManageService extends AbstractBaseService<AppRoleManage,String> {

    /**
     * 应用角色分页查询
     * @param appRoleManageQueryVo
     * @return
     */
    PageRes<AppRoleManage> getAppRoleManagePage(AppRoleManageQueryVo appRoleManageQueryVo);

    /**
     * 应用角色
     * @param appId
     * @return
     */
    public List<String> getRoleNames(Integer appId);

    /**
     * 应用角色
     * @param appId
     * @return
     */
    List<AppRoleManage> getAllByAppId(Integer appId);

    /**
     * 导入应用系统，关联导入
     * @param list
     * @param appId
     */
    public void getImportData(List<Map<String,Object>> list, Integer appId,List<AppRoleManage> appRoleManages,String oldId,String appName);

    /**
     * 通过name查询角色
     * @param appId
     * @param roleName
     * @return
     */
    public AppRoleManage getRoleByNameAndAppId(Integer appId, String roleName);


    Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file);

    public Map<String,Object> roleValidate(List<Map<String, Object>> roles);
}
