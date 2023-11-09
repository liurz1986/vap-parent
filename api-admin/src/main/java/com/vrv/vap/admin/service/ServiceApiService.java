package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.ServiceApi;
import com.vrv.vap.admin.model.ServiceModule;
import com.vrv.vap.base.BaseService;

/**
 *@author lilang
 *@date 2022/10/9
 *@description
 */
public interface ServiceApiService extends BaseService<ServiceApi> {

    /**
     * 同步所有服务接口
     */
    String syncServiceApi();

    /**
     * 同步服务接口
     * @param serviceModule
     */
    Boolean syncServieApi(ServiceModule serviceModule);
}
