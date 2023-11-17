package com.vrv.vap.alarmdeal.business.appsys.service;


import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.vo.AppSysManagerCacheVo;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppImportResultVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerQueryVo;
import com.vrv.vap.alarmdeal.business.appsys.vo.AppSysManagerVo;
import com.vrv.vap.jpa.web.NameValue;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/9
 */

public interface AppSysManagerService extends  AbstractBaseService<AppSysManager,Integer> {

    public List<AppSysManagerCacheVo> getAppSysManagerList();

    /**
     * 应用系统分页查询
     * @param appSysManagerQueryVo
     * @return
     */
    PageRes<AppSysManagerVo> getAppSysManagerPage(AppSysManagerQueryVo appSysManagerQueryVo);

    /**
     * 批量存储
     * @param list
     */
    public void saveList(List<Map<String, Object>> list);

    /**
     *查询应用系统信息
     * @param id
     * @return
     */
    AppSysManagerVo queryOne(Integer id);

    /**
     * 某应用服务器厂商分布数据
     * @param id
     * @return
     */
    public Map<String,Object>  countServerGroupByType(Integer id);

    /**
     * 服务器列表数据
     * @param id
     * @return
     */
    List<Map<String,Object>> getServerList(Integer id);

    /**
     * * 资产删除时，删除对应服务器数据
     * @param guid 资产id
     * @return
     */

    public Boolean deleteAppServers(String guid);

    /**
     * * 资产批量删除时，删除对应服务器数据
     * @param guids 资产id
     * @return
     */

    public Boolean batchDeleteAppServers(List<String> guids);

    /**
     * 获取应用系统当前最大的id
     * @return
     */
    public int getCurrentMaxId();


    AppImportResultVO checkImportData(MultipartFile file);

    public  void deleteData(String id);

    PageRes<AppSysManagerVo> getAppSysManagerImgPage(AppSysManagerQueryVo appSysManagerQueryVo);

    List<NameValue> getAppAlarmEventTop10();

    Result<String> exportNewAssetInfo(AppSysManagerQueryVo appSysManagerQueryVo);

    public void deleteRefByAppIds(List<Integer> appIds);

    public Result<List<Map<String, Object>>> getAppsAuth();

    AppSysManager getAppByIp(String dstIp);
}
