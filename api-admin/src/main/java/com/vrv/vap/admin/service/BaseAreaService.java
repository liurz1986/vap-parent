package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.BaseArea;
import com.vrv.vap.base.BaseService;

import java.util.List;

/**
 * Created by Main on 2019/07/24.
 */
public interface BaseAreaService extends BaseService<BaseArea> {
    BaseArea findByCode(String areaCode);

    List<BaseArea>  findSubAreaByCode(String areaCode);
}
