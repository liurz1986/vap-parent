package com.vrv.vap.alarmdeal.business.asset.service;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 资产涉密等级
 */
public interface AssetClassifiedLevel {

    /**
     * 获取资产的所有涉密等级
     * @return
     */
    public List<BaseDictAll> findAll();

    /**
     * 通过中文信息获取对应的code值
     * @param value
     * @return
     */

    public  String getCodeByValue(String value,List<BaseDictAll> assetManageDatas);
    /**
     * 通过code获取中文信息
     * @param code
     * @return
     */
    public  String getValueByCode(String code,List<BaseDictAll> assetManageDatas);

    public  String[] getAllCodeValue(List<BaseDictAll> assetManageDatas);

}
