package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.ResourceApi;
import com.vrv.vap.base.BaseService;

/**
 *@author lilang
 *@date 2022/10/9
 *@description
 */
public interface ResourceApiService extends BaseService<ResourceApi> {

    boolean manageResourceApi(Integer resourceId,String[] addList,String[] delList);
}
