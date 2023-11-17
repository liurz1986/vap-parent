package com.vrv.vap.alarmdeal.business.baseauth.service.impl;


import com.vrv.vap.alarmdeal.business.alaramevent.alarmdatasave.util.QueueUtil;
import com.vrv.vap.alarmdeal.business.appsys.model.AppSysManager;
import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthOperation;
import com.vrv.vap.alarmdeal.business.baseauth.repository.BaseAuthOperationtRepository;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthOperationService;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthoOperationVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthInternetQueryVo;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Service
@Transactional
public class BaseAuthAppOperationImpl extends BaseServiceImpl<BaseAuthOperation, Integer> implements BaseAuthOperationService {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthAppOperationImpl.class);

    @Autowired
    private BaseAuthOperationtRepository baseAuthOperationtRepository;
    @Autowired
    private AssetService assetService;
    @Autowired
    private AssetTypeGroupService assetTypeGroupService;
    @Autowired
    private AssetTypeService assetTypeService;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private AppSysManagerService appSysManagerService;
    @Override
    public BaseRepository<BaseAuthOperation, Integer> getRepository() {
        return this.baseAuthOperationtRepository;
    }


    @Override
    public Result<BaseAuthoOperationVo> addAuthOperation(BaseAuthoOperationVo baseAuthOperation) {
        BaseAuthoOperationVo baseAuthoOperationVo=new BaseAuthoOperationVo();
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("ip",baseAuthOperation.getIp()));
        queryConditions.add(QueryCondition.eq("dstIp",baseAuthOperation.getDstIp()));
        long count = count(queryConditions);
        if (count>0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "相同数据已存在");
        }
        BaseAuthOperation baseAuthOperation1 = new BaseAuthOperation();
        baseAuthOperation1.setCreateTime(new Date());
        baseAuthOperation1.setIp(baseAuthOperation.getIp());
        baseAuthOperation1.setDstIp(baseAuthOperation.getDstIp());
        if (baseAuthOperation.getAssetType().equals("app-server")){
            baseAuthOperation1.setType(1);
        }else {
            baseAuthOperation1.setType(0);
        }
        BaseAuthOperation save = save(baseAuthOperation1);
        baseAuthoOperationVo.setId(save.getId());
        baseAuthoOperationVo.setIp(save.getIp());
        baseAuthoOperationVo.setDstIp(save.getDstIp());
        if (save.getType()==0){
            List<AssetTypeGroup> assetTypeGroups = assetTypeGroupService.findAll();
            List<QueryCondition> queryConditionList=new ArrayList<>();
            queryConditionList.add(QueryCondition.eq("ip",baseAuthOperation.getDstIp()));
            List<Asset> assetServiceAll = assetService.findAll(queryConditionList);
            if (assetServiceAll.size()>0){
                Asset asset = assetServiceAll.get(0);
                baseAuthoOperationVo.setResponsibleName(asset.getResponsibleName());
                baseAuthoOperationVo.setOrgName(asset.getOrgName());
                baseAuthoOperationVo.setMac(asset.getMac());
                baseAuthoOperationVo.setOperationUrl(asset.getOperationUrl());
                AssetType one = assetTypeService.getOne(asset.getAssetType());
                if (one!=null){
                    AssetTypeGroup assetOneType = getAssetOneType(one, assetTypeGroups);
                    if (assetOneType!=null&&StringUtils.isNotBlank(assetOneType.getName())){
                        baseAuthoOperationVo.setAssetType(assetOneType.getName());
                    }
                    if (assetOneType!=null&&StringUtils.isNotBlank(assetOneType.getTreeCode())){
                        baseAuthoOperationVo.setTreeCode(assetOneType.getTreeCode());
                    }
                }
            }
        }else {
            List<QueryCondition> queryConditionList=new ArrayList<>();
            queryConditionList.add(QueryCondition.eq("domainName",baseAuthOperation.getDstIp()));
            List<AppSysManager> appSysManagers = appSysManagerService.findAll(queryConditionList);
            baseAuthoOperationVo.setAssetType("应用系统");
            baseAuthoOperationVo.setTreeCode("app-server");
            if (appSysManagers.size()>0){
                baseAuthoOperationVo.setOperationUrl(appSysManagers.get(0).getOperationUrl());
                baseAuthoOperationVo.setOrgName(appSysManagers.get(0).getDepartmentName());
            }
        }
        try {
            QueueUtil.putAuth(5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success(baseAuthoOperationVo);
    }

    @Override
    public Result<BaseAuthoOperationVo> updateAuthOperation(BaseAuthoOperationVo baseAuthOperation) {
        BaseAuthoOperationVo baseAuthoOperationVo=new BaseAuthoOperationVo();
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("ip",baseAuthOperation.getIp()));
        queryConditions.add(QueryCondition.eq("dstIp",baseAuthOperation.getDstIp()));
        queryConditions.add(QueryCondition.notEq("id",baseAuthOperation.getId()));
        long count = count(queryConditions);
        if (count>0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "相同数据已存在");
        }
        BaseAuthOperation baseAuthOperation1 = getOne(baseAuthOperation.getId());
        baseAuthOperation1.setDstIp(baseAuthOperation.getDstIp());
        baseAuthOperation1.setIp(baseAuthOperation.getIp());
        if (baseAuthOperation.getAssetType().equals("app-server")){
            baseAuthOperation1.setType(1);
        }else {
            baseAuthOperation1.setType(0);
        }
        BaseAuthOperation save = save(baseAuthOperation1);
        baseAuthoOperationVo.setId(save.getId());
        baseAuthoOperationVo.setIp(save.getIp());
        baseAuthoOperationVo.setDstIp(save.getDstIp());
        if (save.getType()==0){
            List<AssetTypeGroup> assetTypeGroups = assetTypeGroupService.findAll();
            List<QueryCondition> queryConditionList=new ArrayList<>();
            queryConditionList.add(QueryCondition.eq("ip",baseAuthOperation.getDstIp()));
            List<Asset> assetServiceAll = assetService.findAll(queryConditionList);
            if (assetServiceAll.size()>0){
                Asset asset = assetServiceAll.get(0);
                baseAuthoOperationVo.setResponsibleName(asset.getResponsibleName());
                baseAuthoOperationVo.setOrgName(asset.getOrgName());
                baseAuthoOperationVo.setMac(asset.getMac());
                baseAuthoOperationVo.setOperationUrl(asset.getOperationUrl());
                AssetType one = assetTypeService.getOne(asset.getAssetType());
                if (one!=null){
                    AssetTypeGroup assetOneType = getAssetOneType(one, assetTypeGroups);
                    if (assetOneType!=null&&StringUtils.isNotBlank(assetOneType.getName())){
                        baseAuthoOperationVo.setAssetType(assetOneType.getName());
                    }
                    if (assetOneType!=null&&StringUtils.isNotBlank(assetOneType.getTreeCode())){
                        baseAuthoOperationVo.setTreeCode(assetOneType.getTreeCode());
                    }
                }
            }
        }else {
            List<QueryCondition> queryConditionList=new ArrayList<>();
            queryConditionList.add(QueryCondition.eq("domainName",baseAuthOperation.getDstIp()));
            List<AppSysManager> appSysManagers = appSysManagerService.findAll(queryConditionList);
            baseAuthoOperationVo.setAssetType("应用系统");
            baseAuthoOperationVo.setTreeCode("app-server");
            if (appSysManagers.size()>0){
                baseAuthoOperationVo.setOperationUrl(appSysManagers.get(0).getOperationUrl());
                baseAuthoOperationVo.setOrgName(appSysManagers.get(0).getDepartmentName());
            }
        }
        try {
            QueueUtil.putAuth(5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success(baseAuthoOperationVo);
    }

    @Override
    public Result<String> delAuthOperation(BaseAuthOperation baseAuthOperation) {
        delete(baseAuthOperation.getId());
        try {
            QueueUtil.putAuth(5);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success("success");
    }

    @Override
    public PageRes<BaseAuthoOperationVo> operationPage(BaseAuthInternetQueryVo baseAuthInternetQueryVo) {
        PageRes<BaseAuthoOperationVo> pageRes=new PageRes<>();
        List<BaseAuthoOperationVo> baseAuthoOperationVos=new ArrayList<>();
        List<QueryCondition> queryConditions=new ArrayList<>();
        if (StringUtils.isNotBlank(baseAuthInternetQueryVo.getIp())){
            queryConditions.add(QueryCondition.like("dstIp",baseAuthInternetQueryVo.getIp()));
        }
        Page<BaseAuthOperation> all = findAll(queryConditions, baseAuthInternetQueryVo.getPageable());
        PageRes<BaseAuthOperation> res = PageRes.toRes(all);
        pageRes.setMessage(res.getMessage());
        pageRes.setTotal(res.getTotal());
        pageRes.setCode(res.getCode());
        List<BaseAuthOperation> list = res.getList();
        List<AssetTypeGroup> assetTypeGroups = assetTypeGroupService.findAll();
        if (list.size()>0){
            for (BaseAuthOperation baseAuthOperation:list){
                BaseAuthoOperationVo baseAuthoOperationVo=new BaseAuthoOperationVo();
                baseAuthoOperationVo.setId(baseAuthOperation.getId());
                baseAuthoOperationVo.setIp(baseAuthOperation.getIp());
                baseAuthoOperationVo.setDstIp(baseAuthOperation.getDstIp());
                if (baseAuthOperation.getType()==0){
                    List<QueryCondition> queryConditionList=new ArrayList<>();
                    queryConditionList.add(QueryCondition.eq("ip",baseAuthOperation.getDstIp()));
                    List<Asset> assetServiceAll = assetService.findAll(queryConditionList);
                    if (assetServiceAll.size()>0){
                        Asset asset = assetServiceAll.get(0);
                        baseAuthoOperationVo.setResponsibleName(asset.getResponsibleName());
                        baseAuthoOperationVo.setOrgName(asset.getOrgName());
                        baseAuthoOperationVo.setMac(asset.getMac());
                        baseAuthoOperationVo.setOperationUrl(asset.getOperationUrl());
                        AssetType one = assetTypeService.getOne(asset.getAssetType());
                        if (one!=null){
                            AssetTypeGroup assetOneType = getAssetOneType(one, assetTypeGroups);
                            if (assetOneType!=null&&StringUtils.isNotBlank(assetOneType.getName())){
                                baseAuthoOperationVo.setAssetType(assetOneType.getName());
                            }
                            if (assetOneType!=null&&StringUtils.isNotBlank(assetOneType.getTreeCode())){
                                baseAuthoOperationVo.setTreeCode(assetOneType.getTreeCode());
                            }
                        }
                    }
                }else {
                    List<QueryCondition> queryConditionList=new ArrayList<>();
                    queryConditionList.add(QueryCondition.eq("domainName",baseAuthOperation.getDstIp()));
                    List<AppSysManager> appSysManagers = appSysManagerService.findAll(queryConditionList);
                    baseAuthoOperationVo.setAssetType("应用系统");
                    baseAuthoOperationVo.setTreeCode("app-server");
                    if (appSysManagers.size()>0){
                        baseAuthoOperationVo.setOperationUrl(appSysManagers.get(0).getOperationUrl());
                        baseAuthoOperationVo.setOrgName(appSysManagers.get(0).getDepartmentName());
                    }
                }
                baseAuthoOperationVos.add(baseAuthoOperationVo);
            }
        }

        pageRes.setList(baseAuthoOperationVos);
        return pageRes;
    }

    private AssetTypeGroup getAssetOneType(AssetType one, List<AssetTypeGroup> assetTypeGroups) {
        String treeCode = one.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo);
        for(AssetTypeGroup group : assetTypeGroups){
            if(treeCodeGroup.equals(group.getTreeCode())){
                return group;
            }
        }
        return null;
    }
}
