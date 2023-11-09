package com.vrv.vap.alarmdeal.business.asset.dao;

import java.util.List;
import java.util.Map;

public interface BaseKoalOrgDao {

    /**
     * 获取下级单位及应用
     */
    public List<Map<String,Object>> organizationByParentCode(String code);


    public List<Map<String,Object>> organizationByCode(String code);
}
