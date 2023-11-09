package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.appsys.service.OrgService;
import com.vrv.vap.alarmdeal.business.asset.util.RedisCacheUtil;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.exception.AlarmDealException;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 获取组织机构数据
 *
 * 将数据存在redis中，主要是用于数据同步中
 * 2022-07-13
 */
@Service
public class OrgServiceImpl implements OrgService {

    @Autowired
    private AdminFeign adminFegin;

    @Autowired
    private RedisCacheUtil redisCacheUtil;


    /**
     * 通过缓存获取单位名称，缓存没有查询接口
     * @return
     */
    public  List<BaseKoalOrg> getOrgsCache(){
       Object data = getData(RedisCacheUtil.org_all_redis_key);
       if(null != data){
           return JSONObject.parseArray(data.toString(), BaseKoalOrg.class);
       }
       return getOrgs();
    }

    /**
     * 通过接口查询单位名称
     * @return
     */
    public List<BaseKoalOrg> getOrgs(){
        VData<BaseKoalOrg> rootInfo = adminFegin.getRoot();  // 获取组织机构根节点
        BaseKoalOrg baseKoalOrg = rootInfo.getData();
        if(null == baseKoalOrg){
            throw new AlarmDealException(-1,"fegin接口获取组织机构根节点数据为空："+rootInfo.getCode());
        }
        String rootCode = baseKoalOrg.getCode();
        VData<List<BaseKoalOrg>> vdata = adminFegin.getOrgChildren(rootCode); // 获取根据节点下面的子节点
        List<BaseKoalOrg> orgs = vdata.getData();
        orgs.add(baseKoalOrg);  // 加上根组织机构
        return orgs;
    }

    private Object getData(String key){
        return redisCacheUtil.get(key);
    }
}
