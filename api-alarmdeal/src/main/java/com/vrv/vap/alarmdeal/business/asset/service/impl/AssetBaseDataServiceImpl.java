package com.vrv.vap.alarmdeal.business.asset.service.impl;
import com.alibaba.fastjson.JSONArray;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.model.AssetSystemAttributeSettings;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.service.AssetBaseDataService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetSystemAttributeSettingsService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.util.RedisCacheUtil;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.jpa.web.page.QueryCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 资产用到的基础数据
 * 改为直接查询--解决数据不一致性 2022-08-05
 *
 * 1.获取所有用户信息
 * 2.获取所有安全域
 * 3.获取所有资产类型
 * 4.终端下所有二级资产类型的uniqueCode
 * 5.获取所有偏好配置
 */
@Service
public class AssetBaseDataServiceImpl implements AssetBaseDataService {
    Logger logger = LoggerFactory.getLogger(AssetBaseDataServiceImpl.class);
    @Autowired
    private AssetDao assetDao;
    @Autowired
    private AdminFeign adminFeign;
    @Autowired
    private AssetTypeGroupService assetTypeGroupService;
    @Autowired
    private AssetTypeService assetTypeService;
    @Autowired
    private AssetSystemAttributeSettingsService assetSystemAttributeSettingsService;

    @Autowired
    private RedisCacheUtil redisCacheUtil;

    @Value("${classifiedLevel.parentType.asset:f5a4ae5b-3cee-a84f-7471-8f23ezjg0400}")
    private String assetParentType;

    /***
     * 获取所有人信息
     * @return
     */
    public List<BasePersonZjg> queryAllPerson(){
        try{
            VData<List<BasePersonZjg>> vData = adminFeign.getAllPerson();
            List<BasePersonZjg> basePersonZjgList = vData.getData();
            return basePersonZjgList;
        }catch(Exception e){
            logger.error("获取所有用户信息接口异常", e);
            throw new RuntimeException("获取所有用户信息接口异常");
        }
    }
    /**
     * 获取所有人信息（缓存）
     * @return
     */
    public List<BasePersonZjg> queryAllPersonCache(){
        try{
            Object data = redisCacheUtil.get(RedisCacheUtil.person_all_redis_key);
            if(null == data){
                return queryAllPerson();  //缓存不存在查接口
            }
            List<BasePersonZjg> persons = JSONArray.parseArray(data.toString(),BasePersonZjg.class);
            return persons;
        }catch(Exception e){
            logger.error("获取所有用户信息缓存异常", e);
            throw new RuntimeException("获取所有用户信息缓存异常");
        }
    }

    /**
     * 获取所有安全域信息
     */
    public List<BaseSecurityDomain> queryAllDomain(){
        try {
            VData<List<BaseSecurityDomain>> allDomainf = adminFeign.getAllDomain();
            if (allDomainf != null && "0".equals(allDomainf.getCode())) {
                // 过滤掉安全域中的一级，只展示二级，parentCode为null为一级
                List<BaseSecurityDomain> domains = new ArrayList<>();
                for(BaseSecurityDomain domain : allDomainf.getData()){
                    if(StringUtils.isEmpty(domain.getParentCode())){
                        continue;
                    }
                    domains.add(domain);
                }
                return domains;
            }
        } catch (Exception e) {
            logger.error("获取所有安全域接口异常", e);
            throw new RuntimeException("获取所有安全域接口异常");
        }
        return null;
    }

    /**
     * 获取所有安全域信息(缓存)
     */
    public List<BaseSecurityDomain> queryAllDomainCache(){
        try{
            Object data = redisCacheUtil.get(RedisCacheUtil.domain_all_redis_key);
            if(null == data){
                return queryAllDomain();  //缓存不存在查接口
            }
            List<BaseSecurityDomain> allDomains = JSONArray.parseArray(data.toString(),BaseSecurityDomain.class);
            // 过滤掉安全域中的一级，只展示二级，parentCode为null为一级
            List<BaseSecurityDomain> domains = new ArrayList<>();
            for(BaseSecurityDomain domain : allDomains){
                if(StringUtils.isEmpty(domain.getParentCode())){
                    continue;
                }
                domains.add(domain);
            }
            return domains;
        }catch(Exception e){
            logger.error("获取所有用户信息缓存异常", e);
            throw new RuntimeException("获取所有用户信息缓存异常");
        }
    }

    /**
     * 获取所有一级资产类型
     * @return
     */
    public List<AssetTypeGroup> queyAllAssetTypeGroup(){
        try{
            List<AssetTypeGroup> assetTypeGroups = assetTypeGroupService.findAll();
            return assetTypeGroups;
        }catch(Exception e){
            logger.error("获取所有一级资产类型异常", e);
            throw new RuntimeException("获取所有一级资产类型异常");
        }
    }

    /**
     * 获取所有二级资产类型
     * @return
     */
    public List<AssetType> queryAllAssetType(){
        try{
            List<AssetType> assetTypes = assetTypeService.findAll();
            return assetTypes;
        }catch(Exception e){
            logger.error("获取所有二级资产类型异常", e);
            throw new RuntimeException("获取所有二级资产类型异常");
        }
    }


    /**
     * 获取所有终端资产类型unicode
     * @return
     */
    public List<String> queryAllAssetHostTypeUnicode(){
        try{
            List<String> unicodes = assetDao.getTypeUnicodesIsAssetHost();
            return unicodes;
        }catch(Exception e){
            logger.error("获取所有终端资产类型unicode异常", e);
            throw new RuntimeException("获取所有终端资产类型unicode异常");
        }
    }


    /**
     * 获取所有偏好配置信息
     *
     */
    public List<AssetSystemAttributeSettings> queryAllAssetSystemAttributeSetting(){
        try{
            List<QueryCondition> querys = new ArrayList<>();
            querys.add(QueryCondition.eq("visible", true));
            List<AssetSystemAttributeSettings> assetSystemAttributeSettings= assetSystemAttributeSettingsService.findAll(querys);
            return  assetSystemAttributeSettings;
        }catch(Exception e){
            logger.error("获取所有偏好配置异常", e);
            throw new RuntimeException("获取所有偏好配置异常");
        }
    }


    /**
     * 资产涉密等级
     * @return
     */
    public  List<BaseDictAll> queryAssetSecretLevels() {
        try {
            Map<String, Object> param = new HashMap<>();
            param.put("parentType", assetParentType);
            VList<BaseDictAll> result = adminFeign.getPageDict(param);
            if (result != null && result.getCode().equals("0")) {
                List<BaseDictAll> data = result.getList();

                return data;
            }
            return null;
        } catch (Exception e) {
            logger.error("获取资产涉密等级异常", e);
            throw new RuntimeException("获取资产涉密等级异常");
        }
    }
}
