package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.appsys.service.ClassifiedLevelService;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 涉密等级
 * 2022-07-08
 *
 * 其中：将应用系统涉密等级数据存在redis缓存中，主要是用于数据同步用
 */
@Service
public class ClassifiedLevelServiceImpl implements ClassifiedLevelService {

    @Autowired
    private AdminFeign adminFeign;
    //网络信息
    @Value("${classifiedLevel.parentType.network:f5a4ae5b-3cee-a84f-7471-8f23ezjg1100}")
    private String networkParentType;
    //应用系统信息
    @Value("${classifiedLevel.parentType.app:f5a4ae5b-3cee-a84f-7471-8f23ezjg1300}")
    private String appParentType;
    //互联信息
    @Value("${classifiedLevel.parentType.internetInfo:f5a4ae5b-3cee-a84f-7471-8f23ezjg1100}")
    private String internetInfoParentType;
    //数据属性信息
    @Value("${classifiedLevel.parentType.dataInfo:f5a4ae5b-3cee-a84f-7471-8f23ezjg0500}")
    private String dataInfoParentType;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;

    public static final String appClassifiedLevelCacheRedisKey="appClassifiedLevelCacheRedisKey";

    /**
     * 互联信息
     * @return
     */
    @Override
    public List<BaseDictAll> getInternetAll() {
        List<BaseDictAll> queryDatas = getListData(internetInfoParentType);
        return queryDatas;
    }

    /**
     * 网络信息
     * @return
     */
    @Override
    public List<BaseDictAll> getNetWorkAll() {
        List<BaseDictAll> queryDatas = getListData(networkParentType);
        return queryDatas;
    }

    /**
     * 应用系统
     * @return
     */
    @Override
    public List<BaseDictAll> getAppAll() {
        List<BaseDictAll> queryDatas = getListData(appParentType);
        return queryDatas;
    }

    /**
     * 数据属性信息
     * @return
     */
    @Override
    public List<BaseDictAll> getDataInfoAll() {
        List<BaseDictAll> queryDatas = getListData(dataInfoParentType);
        return queryDatas;
    }

    @Override
    public String[] getAppSecretLevelAllValues() {
        List<BaseDictAll> queryDatas = getListData(appParentType);
        return getValues(queryDatas);

    }

    @Override
    public String[] getDataInfoSecretLevelAllValues() {
        List<BaseDictAll> queryDatas = getListData(dataInfoParentType);
        return getValues(queryDatas);
    }

    @Override
    public List<String> getDataInfoSecretLevelAllCodes() {
        List<BaseDictAll> queryDatas = getListData(dataInfoParentType);
        return getCodes(queryDatas);
    }


    @Override
    public String[] getInternetSecretLevelAllValues() {
        List<BaseDictAll> queryDatas = getListData(internetInfoParentType);
        return getValues(queryDatas);
    }

    @Override
    public String[] getNetWorkSecretLevelAllValues() {
        List<BaseDictAll> queryDatas = getListData(networkParentType);
        return getValues(queryDatas);
    }

    private  String[] getValues( List<BaseDictAll> queryDatas ){
        List<String> codeValues = new ArrayList<>();
        for(BaseDictAll data : queryDatas){
            if(!codeValues.contains(data.getCodeValue())){
                codeValues.add(data.getCodeValue());
            }
        }
        return codeValues.toArray(new String[codeValues.size()]);
    }

    private List<String> getCodes(List<BaseDictAll> queryDatas) {
        List<String> codes = new ArrayList<>();
        for(BaseDictAll data : queryDatas){
            if(!codes.contains(data.getCode())){
                codes.add(data.getCode());
            }
        }
        return codes;
    }


    private List<BaseDictAll> getListData(String parentTypeAssetManage) {
        Map<String,Object> param = new HashMap<>();
        param.put("parentType",parentTypeAssetManage);
        VList<BaseDictAll> result = adminFeign.getPageDict(param);
        if (result != null && result.getCode().equals("0")) {
            List<BaseDictAll> data = result.getList();
            return data;
        }
        return null;
    }


}
