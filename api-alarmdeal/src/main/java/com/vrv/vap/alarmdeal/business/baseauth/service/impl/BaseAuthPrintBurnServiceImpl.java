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
import com.vrv.vap.alarmdeal.business.baseauth.model.BaseAuthPrintBurn;
import com.vrv.vap.alarmdeal.business.baseauth.repository.BaseAuthPrintBurnRepository;
import com.vrv.vap.alarmdeal.business.baseauth.service.BaseAuthPrintBurnService;
import com.vrv.vap.alarmdeal.business.baseauth.vo.BaseAuthPrintBurnVo;
import com.vrv.vap.alarmdeal.business.baseauth.vo.query.BaseAuthPrintBurnQueryVo;
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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;


/**
 * 审批类型配置
 *
 * 2023-08-25
 * @Author liurz
 */
@Service
@Transactional
public class BaseAuthPrintBurnServiceImpl extends BaseServiceImpl<BaseAuthPrintBurn, Integer> implements BaseAuthPrintBurnService {
    private static Logger logger = LoggerFactory.getLogger(BaseAuthPrintBurnServiceImpl.class);

    @Autowired
    private BaseAuthPrintBurnRepository baseAuthPrintBurnRepository;
    @Autowired
    private MapperUtil mapper;
    @Override
    public BaseRepository<BaseAuthPrintBurn, Integer> getRepository() {
        return this.baseAuthPrintBurnRepository;
    }
    @Autowired
    private AssetService assetService;
    @Autowired
    private AssetTypeService assetTypeService;
    @Autowired
    private AssetTypeGroupService assetTypeGroupService;
    private List<String> printBrunAssetTypeCode=Arrays.asList("asset-Host","asset-service","asset-MaintenHost");
    private List<String> maintenAssetTypeCode=Arrays.asList("asset-Host","asset-service","asset-MaintenHost","asset-NetworkDevice","asset-SafeDevice");
    private final String APP_TYPE_CODE="app-server";
    @Autowired
    private AppSysManagerService appSysManagerService;

    @Override
    public PageRes<BaseAuthPrintBurnVo> getPager(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo) {
        PageRes<BaseAuthPrintBurnVo> printBurnPageRes=new PageRes<>();
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("type",baseAuthPrintBurnQueryVo.getType()));
        if (StringUtils.isNotBlank(baseAuthPrintBurnQueryVo.getIp()))
        queryConditions.add(QueryCondition.eq("ip",baseAuthPrintBurnQueryVo.getIp()));
        if (StringUtils.isNotBlank(baseAuthPrintBurnQueryVo.getResponsibleName())){
            List<QueryCondition> assetQueryConditions=new ArrayList<>();
            assetQueryConditions.add(QueryCondition.like("responsibleName",baseAuthPrintBurnQueryVo.getResponsibleName()));
            List<Asset> assetServiceAll = assetService.findAll(assetQueryConditions);
            if (assetServiceAll.size()>0){
                List<String> strings = assetServiceAll.stream().map(a -> a.getIp()).collect(Collectors.toList());
                queryConditions.add(QueryCondition.in("ip",strings));
            }else {
                queryConditions.add(QueryCondition.isNull("ip"));
            }
        }
        Page<BaseAuthPrintBurn> all = this.findAll(queryConditions,baseAuthPrintBurnQueryVo.getPageable());
        PageRes<BaseAuthPrintBurn> res = PageRes.toRes(all);
        List<BaseAuthPrintBurn> list = res.getList();
        printBurnPageRes.setCode(res.getCode());
        printBurnPageRes.setMessage(res.getMessage());
        printBurnPageRes.setTotal(res.getTotal());
        if (list.size()>0){
            List<BaseAuthPrintBurnVo> baseAuthPrintBurnVos=new ArrayList<>();
            List<AssetTypeGroup> assetTypeGroups = assetTypeGroupService.findAll();
            for (BaseAuthPrintBurn baseAuthPrintBurn:list){
                BaseAuthPrintBurnVo baseAuthPrintBurnVo=new BaseAuthPrintBurnVo();
                BeanUtils.copyProperties(baseAuthPrintBurn,baseAuthPrintBurnVo);
                List<QueryCondition> queryConditionList=new ArrayList<>();
                queryConditionList.add(QueryCondition.eq("ip",baseAuthPrintBurn.getIp()));
                List<Asset> assetServiceAll = assetService.findAll(queryConditionList);
                if (assetServiceAll.size()>0){
                    Asset asset = assetServiceAll.get(0);
                    baseAuthPrintBurnVo.setResponsibleName(asset.getResponsibleName());
                    baseAuthPrintBurnVo.setOrgName(asset.getOrgName());
                    AssetType one = assetTypeService.getOne(asset.getAssetType());
                    if (one!=null){
                        AssetTypeGroup assetOneType = getAssetOneType(one, assetTypeGroups);
                        if (assetOneType!=null&&StringUtils.isNotBlank(assetOneType.getName())){
                            baseAuthPrintBurnVo.setAssetType(assetOneType.getName());
                        }
                        if (assetOneType!=null&&StringUtils.isNotBlank(assetOneType.getTreeCode())){
                            baseAuthPrintBurnVo.setTreeCode(assetOneType.getTreeCode());
                        }
                    }
                }
                baseAuthPrintBurnVos.add(baseAuthPrintBurnVo);
            }
            printBurnPageRes.setList(baseAuthPrintBurnVos);
        }
        return printBurnPageRes;
    }

    @Override
    public Result<Map<String,List<String>>> saveData(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo) {
        Result<Map<String,List<String>>> isMust = isMustValidate(baseAuthPrintBurnQueryVo);
        if(ResultCodeEnum.UNKNOW_FAILED.getCode().equals(isMust.getCode())){
            return isMust;
        }
        List<String> ips = baseAuthPrintBurnQueryVo.getIps();
        List<String> dataTrue=new ArrayList<>();
        List<String> dataFlase=new ArrayList<>();
        for (String s:ips){
            long count=getAssetCount(s,baseAuthPrintBurnQueryVo);
            if (count==0){
                BaseAuthPrintBurn baseAuthPrintBurn=new BaseAuthPrintBurn();
                baseAuthPrintBurn.setDecide(baseAuthPrintBurnQueryVo.getDecide());
                baseAuthPrintBurn.setType(baseAuthPrintBurnQueryVo.getType());
                baseAuthPrintBurn.setCreateTime(new Date());
                baseAuthPrintBurn.setIp(s);
                this.save(baseAuthPrintBurn);
                dataTrue.add(s);
            }else {
                dataFlase.add(s);
            }
        }
        try {
            QueueUtil.putAuth(baseAuthPrintBurnQueryVo.getType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Map<String,List<String>> map=new HashMap<>();
        map.put("dataTrue",dataTrue);
        map.put("dataFlase",dataFlase);
        Result result=new Result();
        result.setData(map);
        result.setMsg("成功添加"+dataTrue.size()+"个审批信息，失败添加"+dataFlase.size()+"个审批信息。");
        result.setCode(0);
        return result;
    }

    @Override
    public Result<BaseAuthPrintBurn> updatePrintBurn(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo) {
        long count=cheakUpdate(baseAuthPrintBurnQueryVo);
        if (count>0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "该设备ip审批信息已存在");
        }
        BaseAuthPrintBurn baseAuthPrintBurn = getOne(baseAuthPrintBurnQueryVo.getId());
        baseAuthPrintBurn.setId(baseAuthPrintBurnQueryVo.getId());
        baseAuthPrintBurn.setIp(baseAuthPrintBurnQueryVo.getIp());
        baseAuthPrintBurn.setDecide(baseAuthPrintBurnQueryVo.getDecide());
        baseAuthPrintBurn.setType(baseAuthPrintBurnQueryVo.getType());
        save(baseAuthPrintBurn);
        try {
            QueueUtil.putAuth(baseAuthPrintBurnQueryVo.getType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success(baseAuthPrintBurn);
    }

    @Override
    public Result<String> delPrintBurn(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo) {
        BaseAuthPrintBurn one = getOne(baseAuthPrintBurnQueryVo.getIds().get(0));
        for (Integer id:baseAuthPrintBurnQueryVo.getIds()){
            delete(id);
        }
        try {
            QueueUtil.putAuth(one.getType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return ResultUtil.success("success");
    }

    @Override
    public Result<List<String>> getIpsByAssetType(AssetTypeGroup assetTypeGroup) {
        String treeCode = assetTypeGroup.getTreeCode();
        if (treeCode.equals(APP_TYPE_CODE)){
            List<AppSysManager> appSysManagers = appSysManagerService.findAll();
            List<String> strings = appSysManagers.stream().map(a -> a.getDomainName()).collect(Collectors.toList());
            return ResultUtil.successList(strings);
        }else {
            List<QueryCondition> conditions = new ArrayList<>();
            List<QueryCondition> con = new ArrayList<>();
            con.add(QueryCondition.likeBegin("treeCode", treeCode + "-"));
            List<String> guids = new ArrayList<>();
            List<AssetType> find = assetTypeService.findAll(con);
            if (find != null && !find.isEmpty()) {
                for (AssetType assetType1 : find) {
                    guids.add(assetType1.getGuid());
                }
            }
            if (!guids.isEmpty()) {
                conditions.add(QueryCondition.in("assetType", guids));
            }else{
                conditions.add(QueryCondition.isNull("assetType"));
            }
            List<Asset> assetServiceAll = assetService.findAll(conditions);
            List<String> strings = assetServiceAll.stream().map(a -> a.getIp()).collect(Collectors.toList());
            return ResultUtil.successList(strings);
        }
    }

    @Override
    public Result<List<AssetTypeGroup>> getPrintBrunAssetType() {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.in("treeCode",printBrunAssetTypeCode));
        List<AssetTypeGroup> all = assetTypeGroupService.findAll(queryConditions);
        return ResultUtil.successList(all);
    }

    @Override
    public Result<List<AssetTypeGroup>> getMaintenAssetType() {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.in("treeCode",maintenAssetTypeCode));
        List<AssetTypeGroup> all = assetTypeGroupService.findAll(queryConditions);
        AssetTypeGroup assetTypeGroup =new AssetTypeGroup();
        assetTypeGroup.setTreeCode(APP_TYPE_CODE);
        assetTypeGroup.setName("应用系统");
        all.add(assetTypeGroup);
        return ResultUtil.successList(all);
    }

    private long cheakUpdate(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo) {
        BaseAuthPrintBurn one = this.getOne(baseAuthPrintBurnQueryVo.getId());
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("ip",baseAuthPrintBurnQueryVo.getIp()));
        queryConditions.add(QueryCondition.eq("type",baseAuthPrintBurnQueryVo.getType()));
        queryConditions.add(QueryCondition.notEq("ip",one.getIp()));
        long count = this.count(queryConditions);
        return count;
    }

    private long getAssetCount(String s,BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("ip",s));
        queryConditions.add(QueryCondition.eq("type",baseAuthPrintBurnQueryVo.getType()));
        long count = this.count(queryConditions);
        return count;
    }

    private  Result<Map<String,List<String>>> isMustValidate(BaseAuthPrintBurnQueryVo baseAuthPrintBurnQueryVo) {
        if(baseAuthPrintBurnQueryVo.getIps().size()==0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "设备ip不能为空");
        }
        if(baseAuthPrintBurnQueryVo.getType()==null){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "打印刻录类型需要确定");
        }
        if(baseAuthPrintBurnQueryVo.getDecide()==null){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(), "权限选择不能为空");
        }
        return ResultUtil.success(null);
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
