package com.vrv.vap.alarmdeal.business.appsys.service.impl;


import com.vrv.vap.alarmdeal.business.appsys.service.OrgService;
import com.vrv.vap.alarmdeal.frameworks.contract.audit.BaseKoalOrg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.feign.AdminFeign;
import com.vrv.vap.common.vo.VData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lps 2021/8/19
 */

@Service
public class FeignService {

    @Autowired
    private AdminFeign adminFeign;

    @Autowired
    private OrgService orgService;

    /**
     * 获取人员编号
     * @return
     */
    public List<String> getPersonNo(){
        List<String> result=new ArrayList<>();
        VData<List<BasePersonZjg>> vData=adminFeign.getAllPerson();
        List<BasePersonZjg> basePersonZjgList=vData.getData();
        result=basePersonZjgList.stream().map(item->item.getUserNo()).collect(Collectors.toList());
        return result;
    }

    /**
     * 获取部门名称
     * @return
     */
    public List<String> getOrgName(){
        List<BaseKoalOrg> orgs = orgService.getOrgs();
        List<String> result=new ArrayList<>();
        result=orgs.stream().map(item->item.getName()).distinct().collect(Collectors.toList());
        return result;
    }

    /**
     * 构造 部门和人员map
     * @return
     */
    public  Map<String,String> getDeptMap(){
        Map<String,String> deptMap=new HashMap<>();
        List<BaseKoalOrg> orgs = orgService.getOrgs();
        for(BaseKoalOrg basePersonZjg : orgs){
            if(!deptMap.containsKey(basePersonZjg.getName())){
                deptMap.put(basePersonZjg.getName(),basePersonZjg.getCode());
            }
        }
        return deptMap;

    }

    public  Map<String,String> getPersonMap(){
        Map<String,String> personMap=new HashMap<>();
        VData<List<BasePersonZjg>> vData=adminFeign.getAllPerson();
        List<BasePersonZjg> basePersonZjgList=vData.getData();
        for(BasePersonZjg basePersonZjg : basePersonZjgList){
            if(!personMap.containsKey(basePersonZjg.getOrgName())){
                personMap.put(basePersonZjg.getUserNo(),basePersonZjg.getUserName());
            }
        }
        return personMap;

    }

}
