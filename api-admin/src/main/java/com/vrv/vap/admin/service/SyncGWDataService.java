package com.vrv.vap.admin.service;

import com.vrv.vap.admin.vo.SyncGWDataVO;

public interface SyncGWDataService {
    /**
     * @author lilang
     * @date 2020/5/19
     * @description 同步国网数据接口
     */

    boolean syncData(SyncGWDataVO syncGWDataVO);

    /**
     * 全量同步国网所有数据
     */
    void syncAllData();

    /**
     * 全量同步组织机构
     * @param token
     * @param appCode
     */
    void syncAllOrg(String token, String appCode);

    /**
     * 全量同步角色权限数据
     * @param token
     * @param appCode
     */
    void syncAllRole(String token, String appCode);

    /**
     * 全量同步用户及关联数据
     * @param token
     * @param appCode
     */
    void syncAllUser(String token, String appCode);

    /**
     * 全量同步用户管理范围
     * @param token
     * @param appCode
     */
    void syncAllUserDomain(String token, String appCode);

    /**
     * 资源数据上报
     * @param token
     * @param appCode
     */
    void reportResourceInfo(String token, String appCode);

    /**
     * 获取认证token
     * @return
     */
    String getAuthToken();

    /**
     * 获取认证appCode
     * @param token
     * @return
     */
    String getAppCode(String token);
}
