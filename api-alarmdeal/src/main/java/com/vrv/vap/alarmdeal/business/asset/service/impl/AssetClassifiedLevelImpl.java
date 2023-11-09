package com.vrv.vap.alarmdeal.business.asset.service.impl;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.asset.service.AssetClassifiedLevel;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VList;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AssetClassifiedLevelImpl implements AssetClassifiedLevel {
    private static Logger logger = LoggerFactory.getLogger(AssetClassifiedLevelImpl.class);
    @Autowired
    private AdminFeign adminFeign;
    //资产管理
    @Value("${classifiedLevel.parentType.asset:f5a4ae5b-3cee-a84f-7471-8f23ezjg0400}")
    private String assetParentType;

    @Override
    public List<BaseDictAll> findAll() {
        Map<String,Object> param = new HashMap<>();
        param.put("parentType",assetParentType);
        VList<BaseDictAll> result = adminFeign.getPageDict(param);
        if (result != null && result.getCode().equals("0")) {
            List<BaseDictAll> data = result.getList();
            return data;
        }
        return null;
    }

    /**
     * 通过中文信息获取对应的code值
     * @param value
     * @return
     */

    public  String getCodeByValue(String value,List<BaseDictAll> assetManageDatas) {
        for(BaseDictAll data : assetManageDatas){
            if(value.equalsIgnoreCase(data.getCodeValue())){
                return data.getCode();
            }
        }
        return "";
    }
    /**
     * 通过code获取中文信息
     * @param code
     * @return
     */
    public  String getValueByCode(String code,List<BaseDictAll> assetManageDatas) {
        for(BaseDictAll data : assetManageDatas){
            if(code.equalsIgnoreCase(data.getCode())){
                return data.getCodeValue();
            }
        }
        return "";
    }
    public  String[] getAllCodeValue(List<BaseDictAll> assetManageDatas){
        List<String> codeValues = new ArrayList<>();
        for(BaseDictAll data : assetManageDatas){
            if(!codeValues.contains(data.getCodeValue())){
                codeValues.add(data.getCodeValue());
            }
        }
        return codeValues.toArray(new String[codeValues.size()]);
    }
}
