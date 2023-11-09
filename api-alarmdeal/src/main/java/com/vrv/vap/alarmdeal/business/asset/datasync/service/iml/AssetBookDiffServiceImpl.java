package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.alarmdeal.business.asset.datasync.constant.AssetDataSyncConstant;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDetail;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDiff;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetExtendVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.repository.AssetBookDiffRepository;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.*;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetBookDiffDetailVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetBookDiffSearchVO;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.service.AssetExtendService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeGroupService;
import com.vrv.vap.alarmdeal.business.asset.service.AssetTypeService;
import com.vrv.vap.alarmdeal.business.asset.util.AssetValidateUtil;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.lang.reflect.Field;
import java.util.*;

@Service
@Transactional
public class AssetBookDiffServiceImpl extends BaseServiceImpl<AssetBookDiff, String> implements AssetBookDiffService {
    @Autowired
    private AssetBookDiffRepository assetBookDiffRepository;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private AssetService assetService;
    @Autowired
    private AssetExtendService assetExtendService;
    @Autowired
    private AssetBookDetailService assetBookDetailService;
    @Autowired
    private HandStrategyService handStrategyService;
    @Autowired
    private AssetVerifyService assetVerifyService;
    @Autowired
    private AssetExtendVerifyService assetExtendVerifyService;
    @Autowired
    private AssetTypeService assetTypeService;
    @Autowired
    private AssetTypeGroupService assetTypeGroupService;
    @Override
    public BaseRepository<AssetBookDiff, String> getRepository() {
        return assetBookDiffRepository;
    }

    @Override
    public PageRes<AssetBookDiff> getPage(AssetBookDiffSearchVO assetBookDiffSearchVO) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        PageReq pager = mapper.map(assetBookDiffSearchVO, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        addSearchCondition(queryConditions,assetBookDiffSearchVO);
        Page<AssetBookDiff> page=findAll(queryConditions,pager.getPageable());
        PageRes<AssetBookDiff> res = new PageRes();
        res.setList(page.toList());
        res.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        res.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        res.setTotal(page.getTotalElements());
        return res;
    }

    private void addSearchCondition(List<QueryCondition> queryConditions, AssetBookDiffSearchVO assetVerifySearch){
        // typeGuid
        if(!StringUtils.isEmpty(assetVerifySearch.getTypeGuid())){
            queryConditions.add(QueryCondition.like("typeGuid",assetVerifySearch.getTypeGuid()));
        }
        // ip
        if(!StringUtils.isEmpty(assetVerifySearch.getIp())){
            queryConditions.add(QueryCondition.like("ip",assetVerifySearch.getIp()));
        }
        // name
        if(!StringUtils.isEmpty(assetVerifySearch.getName())){
            queryConditions.add(QueryCondition.like("name",assetVerifySearch.getName()));
        }
        // mac
        if(!StringUtils.isEmpty(assetVerifySearch.getMac())){
            queryConditions.add(QueryCondition.like("mac",assetVerifySearch.getMac()));
        }
        // deviceDesc
        if(!StringUtils.isEmpty(assetVerifySearch.getDeviceDesc())){
            queryConditions.add(QueryCondition.like("deviceDesc",assetVerifySearch.getDeviceDesc()));
        }
        // assetNum
        if(!StringUtils.isEmpty(assetVerifySearch.getAssetNum())){
            queryConditions.add(QueryCondition.like("assetNum",assetVerifySearch.getAssetNum()));
        }
        // responsibleName
        if(!StringUtils.isEmpty(assetVerifySearch.getResponsibleName())){
            queryConditions.add(QueryCondition.like("responsibleName",assetVerifySearch.getResponsibleName()));
        }
        // orgName
        if(!StringUtils.isEmpty(assetVerifySearch.getOrgName())){
            queryConditions.add(QueryCondition.like("orgName",assetVerifySearch.getOrgName()));
        }
        // serialNumber
        if(!StringUtils.isEmpty(assetVerifySearch.getSerialNumber())){
            queryConditions.add(QueryCondition.like("serialNumber",assetVerifySearch.getSerialNumber()));
        }
        // equipmentIntensive
        if(!StringUtils.isEmpty(assetVerifySearch.getEquipmentIntensive())){
            queryConditions.add(QueryCondition.like("equipmentIntensive",assetVerifySearch.getEquipmentIntensive()));
        }
        // location
        if(!StringUtils.isEmpty(assetVerifySearch.getLocation())){
            queryConditions.add(QueryCondition.like("location",assetVerifySearch.getLocation()));
        }
        // extendDiskNumber
        if(!StringUtils.isEmpty(assetVerifySearch.getExtendDiskNumber())){
            queryConditions.add(QueryCondition.like("extendDiskNumber",assetVerifySearch.getExtendDiskNumber()));
        }
        // osList
        if(!StringUtils.isEmpty(assetVerifySearch.getOsList())){
            queryConditions.add(QueryCondition.like("osList",assetVerifySearch.getOsList()));
        }
        // typeSnoGuid
        if(!StringUtils.isEmpty(assetVerifySearch.getTypeSnoGuid())){
            queryConditions.add(QueryCondition.like("typeSnoGuid",assetVerifySearch.getTypeSnoGuid()));
        }
        // remarkInfo
        if(!StringUtils.isEmpty(assetVerifySearch.getRemarkInfo())){
            queryConditions.add(QueryCondition.like("remarkInfo",assetVerifySearch.getRemarkInfo()));
        }
        // registerTime
        if(null != assetVerifySearch.getRegisterTimeStart() && null != assetVerifySearch.getRegisterTimeEnd()){
            queryConditions.add(QueryCondition.between("registerTime",assetVerifySearch.getRegisterTimeStart(),assetVerifySearch.getRegisterTimeEnd()));
        }
        // osSetupTime
        if(null != assetVerifySearch.getOsSetupTimeStart() && null != assetVerifySearch.getOsSetupTimeEnd()){
            queryConditions.add(QueryCondition.between("osSetupTime",assetVerifySearch.getOsSetupTimeStart(),assetVerifySearch.getOsSetupTimeEnd()));
        }
    }

    @Override
    public Result<List<AssetBookDiffDetailVO>> getDiffDetails(String guid) {
        AssetBookDiff assetBookDiff = this.getOne(guid);
        if(null == assetBookDiff){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前guid对应数据不存在，"+guid);
        }
        String detailGuid = assetBookDiff.getRefDetailGuid();
        List<AssetBookDetail> assetBookDetails = getDetailByGuid(detailGuid);
        if(null == assetBookDetails ||assetBookDetails.size() == 0 ){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前guid对应明细数据不存在，"+guid);
        }
        String assetGuid = assetBookDiff.getRefAsetGuid();
        // 组装详细数据
        return structureData(assetBookDetails,assetGuid);
    }




    private List<AssetBookDetail> getDetailByGuid(String detailGuid) {
        if(StringUtils.isEmpty(detailGuid)){
            return null;
        }
        String[] guids = detailGuid.split(",");
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.in("guid",guids));
        return assetBookDetailService.findAll(queryConditions);
    }


    /**
     * 构造详情数据数据
     * @param assetBookDetails
     * @param assetGuid
     * @return
     */
    private Result<List<AssetBookDiffDetailVO>> structureData(List<AssetBookDetail> assetBookDetails, String assetGuid) {
        List<AssetBookDiffDetailVO> details =new ArrayList<>();
        for(AssetBookDetail detail : assetBookDetails){
            AssetBookDiffDetailVO data = mapper.map(detail,AssetBookDiffDetailVO.class);
            data.setOsSetupTime(detail.getOsSetuptime());
            details.add(data);
        }
        AssetBookDiffDetailVO asset = getStructureDataByAsset(assetGuid);
        if(null != asset){
            details.add(asset);
        }
        return ResultUtil.successList(details);
    }


    /**
     * 正式表数据构造
     * @param assetGuid
     * @return
     */
    private AssetBookDiffDetailVO getStructureDataByAsset(String assetGuid) {
        if(StringUtils.isEmpty(assetGuid)){
            return null;
        }
       Asset asset=  assetService.getOne(assetGuid);
        if(null == asset){
            return null;
        }
        AssetExtend assetExtend = assetExtendService.getOne(assetGuid);

        AssetBookDiffDetailVO data = mapper.map(asset,AssetBookDiffDetailVO.class);
        data.setTypeGuid(asset.getAssetType());
        data.setTypeSnoGuid(asset.getAssetTypeSnoGuid());
        data.setOsSetupTime(asset.getOsSetuptime());
        data.setSyncSource("asset_source");
        if(null == assetExtend){        // 磁盘序列号处理extendDiskNumber
          return data;
        }
        String jsons = assetExtend.getExtendInfos();
        if(StringUtils.isEmpty(jsons)){
            return data;
        }
        Map<String,Object> params = JSONObject.parseObject(jsons,Map.class);
        String extendDiskNumber = params.get("extendDiskNumber")==null?"":String.valueOf(params.get("extendDiskNumber"));
        data.setExtendDiskNumber(extendDiskNumber);
        return data;
    }

    /**
     * 差异数据编辑详情确认处理
     * @param assetBookDiffDetailVO
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    @Override
    public Result<String> handle(AssetBookDiffDetailVO assetBookDiffDetailVO) throws NoSuchFieldException, IllegalAccessException {
        AssetBookDiff diffVO = this.getOne(assetBookDiffDetailVO.getGuid());
        if(null == diffVO){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前差异数据不存在，差异数据guid："+assetBookDiffDetailVO.getGuid());
        }
        // 关联详情信息
        List<AssetBookDetail> details  = getAssetDetail(diffVO.getRefDetailGuid());
        if(null == details|| details.size() == 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"没有对应明细数据");
        }
        // 策略中配置比对列
        Map<String,Object> configs = handStrategyService.queyConfigAssets("sync_asset_data_diff_json").getData();
        if(configs.isEmpty()){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"策略配置中比对列没有配置");
        }
        // 构造AssetVerify数据
        Map<String,Object> result = structureAssetVerify(diffVO,details,configs,assetBookDiffDetailVO);
        // 数据处理
        dataSaveHandle(result,diffVO);
        return ResultUtil.success("success");
    }

    /**
     * assetVerrify数据入库
     * 差异数据状态更新
     *
     * @param result
     * @param diffVO
     */
    private void dataSaveHandle(Map<String, Object> result, AssetBookDiff diffVO) {
        // assetVerrify数据入库
        saveAssetVerify(result);
        // 差异数据状态更新
        updateDiffStatus(diffVO);
    }

    private void updateDiffStatus(AssetBookDiff diffVO) {
        diffVO.setHandleStatus("1");
        this.save(diffVO);
    }


    private List<AssetBookDetail> getAssetDetail(String refDetailGuid) {
        String[] guids = refDetailGuid.split(",");
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.in("guid",guids));
        List<AssetBookDetail> details = assetBookDetailService.findAll(queryConditions);
        if(CollectionUtils.isEmpty(details)){
            return null;
        }
        return details;
    }
    // 构造AssetVerify数据
    private Map<String,Object> structureAssetVerify(AssetBookDiff diffVO, List<AssetBookDetail> details, Map<String, Object> configs, AssetBookDiffDetailVO assetBookDiffDetailVO) throws NoSuchFieldException, IllegalAccessException {
        Map<String,Object> result =new HashMap<>();
        AssetBookDetail assetBookDetail = details.get(0);
        AssetVerify assetVerify =  mapper.map(assetBookDetail, AssetVerify.class); // 默认选择第一明细作为统一台账数据，后面根据策略配置修改值
        Set<String> keys = configs.keySet();
        String extendVerify = null;
        for(String key : keys){
            Object mapObject = configs.get(key);
            String configValue =  mapObject==null?"":String.valueOf(mapObject);
            // extendDiskNumber处理
            if(key.equals("extendDiskNumber")){
                extendVerify = extendDiskNumberHandle(configValue,assetBookDiffDetailVO,diffVO,assetBookDetail);
                continue;
            }
            Field fieldVerify = getFieldVerify(assetVerify,key);
            // 比对
            if("-1".equals(configValue)){
                assetVerifyAdd(key,assetBookDiffDetailVO,fieldVerify,assetVerify);
                // 责任人和单位处理
                otherHandle(key,assetVerify,assetBookDiffDetailVO);
            }else{ // 数据源
                addDataSoucreHandle(details,configValue,assetVerify,fieldVerify,key);
                // 责任人和单位处理
                otherDataSourceHandle(key,assetVerify,configValue,details);
            }
        }
        AssetExtendVerify assetExtendVerify =getAssetExtendVerigy(assetBookDetail,extendVerify);
        result.put("assetVerify",assetVerify);
        result.put("assetExtendVerify",assetExtendVerify);
        return result;
    }

    /**
     * 策略配置为数据时：责任人和单位处理
     * @param key
     * @param assetVerify
     * @param configValue
     * @param details
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void otherDataSourceHandle(String key, AssetVerify assetVerify, String configValue, List<AssetBookDetail> details) throws NoSuchFieldException, IllegalAccessException {
        if("responsibleName".equals(key)){
            Field fieldResponsibleCode =assetVerify.getClass().getDeclaredField("responsibleCode");
            fieldResponsibleCode.setAccessible(true);
            addDataSoucreHandle(details,configValue,assetVerify,fieldResponsibleCode,"responsibleCode");
        }
        if("orgName".equals(key)){
            Field fieldOrgCode =assetVerify.getClass().getDeclaredField("orgCode");
            fieldOrgCode.setAccessible(true);
            addDataSoucreHandle(details,configValue,assetVerify,fieldOrgCode,"orgCode");
        }
    }

    /**
     * 策略为比对时，责任人和单位处理
     * @param key
     * @param assetVerify
     * @param assetBookDiffDetailVO
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void otherHandle(String key, AssetVerify assetVerify, AssetBookDiffDetailVO assetBookDiffDetailVO) throws NoSuchFieldException, IllegalAccessException {
        if("responsibleName".equals(key)){
            Field fieldResponsibleCode =assetVerify.getClass().getDeclaredField("responsibleCode");
            fieldResponsibleCode.setAccessible(true);
            assetVerifyAdd("responsibleCode",assetBookDiffDetailVO,fieldResponsibleCode,assetVerify);
        }
        if("orgName".equals(key)){
            Field fieldOrgCode =assetVerify.getClass().getDeclaredField("orgCode");
            fieldOrgCode.setAccessible(true);
            assetVerifyAdd("orgCode",assetBookDiffDetailVO,fieldOrgCode,assetVerify);
        }
    }

    private Field getFieldVerify(AssetVerify assetVerify, String key) throws NoSuchFieldException {
        String assetVerityKey= key;
        if(key.equals("typeGuid")){
            assetVerityKey= "assetType";
        }
        if(key.equals("typeSnoGuid")){
            assetVerityKey= "assetTypeSnoGuid";
        }
        if(key.equals("osSetupTime")){
            assetVerityKey= "osSetuptime";
        }
        Field fieldVerify =assetVerify.getClass().getDeclaredField(assetVerityKey);
        fieldVerify.setAccessible(true);
        return fieldVerify;
    }

    private void addDataSoucreHandle(List<AssetBookDetail> details, String configValue, AssetVerify assetVerify, Field fieldVerify,String key) throws NoSuchFieldException, IllegalAccessException {
        AssetBookDetail assetBookDetail = getDetailByDataSource(configValue,details);
        Object value = null;
        if("osSetupTime".equals(key)){
            key="osSetuptime";
        }
        if(null != assetBookDetail){
            Field fieldDetail = assetBookDetail.getClass().getDeclaredField(key);
            fieldDetail.setAccessible(true);
            value= fieldDetail.get(assetBookDetail);
        }
        setValue(value,assetVerify,fieldVerify);
    }

    private AssetBookDetail getDetailByDataSource(String configValue, List<AssetBookDetail> details) {
        if(StringUtils.isEmpty(configValue)){
            return null;
        }
        for(AssetBookDetail detail : details){
           String syncSource= detail.getSyncSource();
           if(configValue.equals(syncSource)){
               return detail;
           }
        }
        return null;
    }

    private AssetExtendVerify getAssetExtendVerigy(AssetBookDetail assetBookDetail, String extendDiskNumber) {
        AssetExtendVerify assetExtendVerify = new AssetExtendVerify();
        String extendInfos = assetBookDetail.getExtendInfos();
        Map<String,Object> infos =new HashMap<>();
        if(!StringUtils.isEmpty(extendInfos)){
            infos = JSON.parseObject(extendInfos,Map.class);
        }
        infos.put("extendDiskNumber",extendDiskNumber);
        assetExtendVerify.setExtendInfos(JSON.toJSONString(infos));
        return assetExtendVerify;

    }

    private String extendDiskNumberHandle(String configValue, AssetBookDiffDetailVO assetBookDiffDetailVO, AssetBookDiff diffVO,AssetBookDetail assetBookDetail) throws NoSuchFieldException, IllegalAccessException {
        String extendDiskNumber="";
        if("-1".equals(configValue)){
            Field extendDiskNumberField = assetBookDiffDetailVO.getClass().getSuperclass().getDeclaredField("extendDiskNumber");
            extendDiskNumberField.setAccessible(true);
            Object value =extendDiskNumberField.get(assetBookDiffDetailVO);
            extendDiskNumber = value==null?"":String.valueOf(value);
        }else{
            Field extendDiskNumberField = diffVO.getClass().getDeclaredField("extendDiskNumber");
            extendDiskNumberField.setAccessible(true);
            Object value =extendDiskNumberField.get(diffVO);
            extendDiskNumber = value==null?"":String.valueOf(value);
        }
        return extendDiskNumber;
    }


    private void assetVerifyAdd(String key,AssetBookDiffDetailVO assetBookDiffDetailVO,Field fieldVerify,AssetVerify assetVerify) throws NoSuchFieldException, IllegalAccessException {
        Field fieldSear = getDiffDetailField(key,assetBookDiffDetailVO);
        fieldSear.setAccessible(true);
        Object value= fieldSear.get(assetBookDiffDetailVO);
        setValue(value,assetVerify,fieldVerify);
    }

    // 赋值
    private void setValue(Object value, AssetVerify assetVerify, Field fieldVerify) throws IllegalAccessException {
        if(null == value){
            fieldVerify.set(assetVerify,null);
            return ;
        }
        if(value instanceof Date){
            fieldVerify.set(assetVerify,(Date)value);
        }else{
            fieldVerify.set(assetVerify,String.valueOf(value));
        }
    }
    private Field getDiffDetailField(String key, AssetBookDiffDetailVO assetBookDiffDetailVO) throws NoSuchFieldException {
        Field fieldSear = null;
        try{
            //获取父类
            fieldSear = assetBookDiffDetailVO.getClass().getSuperclass().getDeclaredField(key);
        }catch (Exception e){
            fieldSear =  assetBookDiffDetailVO.getClass().getDeclaredField(key);
        }
       return fieldSear;
    }

    private void saveAssetVerify(Map<String,Object> result) {
        AssetVerify assetVerify = (AssetVerify)result.get("assetVerify");
        AssetExtendVerify assetExtendVerify = (AssetExtendVerify)result.get("assetExtendVerify");
        boolean isUsb = isUsb(assetVerify.getAssetType(),assetVerify);
        List<QueryCondition> queryConditions= getConditions(isUsb,assetVerify);
        List<AssetVerify> list = assetVerifyService.findAll(queryConditions);
        if(null != list && list.size() > 0){
            AssetVerify  oldDat = list.get(0);
            assetVerify.setGuid(oldDat.getGuid());
            assetVerify.setUpdateTime(new Date());
            assetVerify.setCreateTime(oldDat.getCreateTime());
        }else{
            String guid= UUIDUtils.get32UUID();
            assetVerify.setGuid(guid);
            assetVerify.setCreateTime(new Date());
        }
        assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSEDIT); //待编辑状态
        assetVerifyService.save(assetVerify);
        assetExtendVerify.setAssetGuid(assetVerify.getGuid());
        assetExtendVerifyService.save(assetExtendVerify);
    }

    private List<QueryCondition> getConditions(boolean isUsb, AssetVerify assetVerify) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        if(isUsb){
            queryConditions.add(QueryCondition.eq("serialNumber",assetVerify.getSerialNumber()));
        }else{
            queryConditions.add(QueryCondition.eq("ip",assetVerify.getIp()));
        }
        return queryConditions;
    }

    private boolean isUsb(String typeGuid, AssetVerify data) {
        if(StringUtils.isEmpty(typeGuid)){
            data.setType("未知");
            return false;
        }
        AssetType assetType =assetTypeService.getOne(typeGuid);
        if(null == assetType){
            data.setType("未知");
            return false;
        }
        String type = getAssetVerifyType(assetType);
        data.setType(type);
        return  AssetValidateUtil.isUsb(assetType.getTreeCode());
    }

    /**
     * 获取待申表type的值：一级资产类型名称-二级资产类型名称
     * @param assetType
     * @return
     */
    public String getAssetVerifyType(AssetType assetType) {
        String assetTypeName= assetType.getName();
        String treeCode = assetType.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("treeCode",treeCodeGroup));
        List<AssetTypeGroup> assetTypeGroups = assetTypeGroupService.findAll(queryConditions);
        String assetTypeGroupName ="未知";
        if(null!=assetTypeGroups && assetTypeGroups.size() >0){
            assetTypeGroupName = assetTypeGroups.get(0).getName();
        }
        String type = assetTypeGroupName+"-"+assetTypeName;
        return type;
    }


}
