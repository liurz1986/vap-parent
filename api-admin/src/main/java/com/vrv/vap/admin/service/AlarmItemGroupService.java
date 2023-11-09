package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.AlarmItemGroup;
import com.vrv.vap.base.BaseService;

/**
 * @author lilang
 * @date 2022/10/14
 * @description
 */
public interface AlarmItemGroupService extends BaseService<AlarmItemGroup> {

    void initGroupData();
}
