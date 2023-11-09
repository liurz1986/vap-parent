package com.vrv.vap.alarmdeal.business.appsys.service;


import com.vrv.vap.alarmdeal.business.appsys.model.AppResourceManage;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppResourceManageVo;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/10
 */

public interface AppResourceManageService extends AbstractBaseService<AppResourceManage,String> {

    /**
     * 应用系统资源分页查询
     * @param appResourceManageVo
     * @return
     */
    PageRes<AppResourceManage> getAppResourceManagePage(AppResourceManageVo appResourceManageVo);

    /**
     * 查询应用系统所有资源
     * @param appId
     * @return
     */
    List<AppResourceManage> getAllByAppId(Integer appId);

    /**
     * 导入应用系统，关联导入
     * @param list
     * @param appId
     */
    void getImportData(List<Map<String, Object>> list, Integer appId, List<AppResourceManage> appResourceManages,String appName);


    /**
     * 某应用资源类型分布数据
     * @param appId
     * @return
     */
     Map<String,Object>  countResourceGroupByType(Integer appId);

    Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file);

    public Map<String, Object> resourceValidate(List<Map<String, Object>> accounts);
}
