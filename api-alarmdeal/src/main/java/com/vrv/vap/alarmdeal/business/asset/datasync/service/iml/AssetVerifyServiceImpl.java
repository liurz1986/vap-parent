package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.vo.BaseDictAll;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.datasync.constant.AssetDataSyncConstant;
import com.vrv.vap.alarmdeal.business.asset.datasync.dao.AssetSyncDao;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetExtendVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.repository.AssetVerifyRepository;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetExtendVerifyService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetSyncService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetVerifyService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.MessageService;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.ExportExcelUtils;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.SyncDataUtil;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.*;
import com.vrv.vap.alarmdeal.business.asset.model.Asset;
import com.vrv.vap.alarmdeal.business.asset.model.AssetExtend;
import com.vrv.vap.alarmdeal.business.asset.model.AssetType;
import com.vrv.vap.alarmdeal.business.asset.model.AssetTypeGroup;
import com.vrv.vap.alarmdeal.business.asset.service.*;
import com.vrv.vap.alarmdeal.business.asset.service.impl.AssetAlarmServiceImpl;
import com.vrv.vap.alarmdeal.business.asset.util.AssetValidateUtil;
import com.vrv.vap.alarmdeal.business.asset.vo.AlarmEventMsgVO;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetRedisCacheVO;
import com.vrv.vap.alarmdeal.business.asset.vo.TerminalAssteInstallTimeJobVO;
import com.vrv.vap.alarmdeal.frameworks.config.FileConfiguration;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BasePersonZjg;
import com.vrv.vap.alarmdeal.frameworks.contract.user.BaseSecurityDomain;
import com.vrv.vap.exportAndImport.excel.exception.ExcelException;
import com.vrv.vap.exportAndImport.excel.util.DateUtils;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.FileUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultCodeEnum;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.PageReq;
import com.vrv.vap.jpa.web.page.PageRes;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.*;

/**
 * 审核数据处理
 *  2022-06-03
 */
@Service
@Transactional
public class AssetVerifyServiceImpl extends BaseServiceImpl<AssetVerify, String> implements AssetVerifyService {
    private static Logger logger = LoggerFactory.getLogger(AssetVerifyServiceImpl.class);
    @Autowired
    private AssetVerifyRepository assetVerifyRepository;
    @Autowired
    private MapperUtil mapper;
    @Autowired
    private AssetTypeService assetTypeService;
    @Autowired
    private AssetTypeGroupService assetTypeGroupService;
    @Autowired
    private AssetExtendVerifyService assetExtendVerifyService;
    @Autowired
    private AssetValidateServiceImpl assetValidateService;
    @Autowired
    private AssetSyncService assetSyncService;
    @Autowired
    private AssetService assetService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private AssetDao assetDao;
    @Autowired
    private BatchAssetValidateServiceImpl batchAssetValidateServiceImpl;
    @Autowired
    private AssetBaseDataService assetBaseDataCacheService;
    @Autowired
    private AssetExtendService assetExtendService;
    @Autowired
    private AssetAlarmService assetAlarmService;
    @Autowired
    private TerminalAssetInstallService terminalAssetInstallService;
    @Autowired
    private BaseDataRedisCacheService baseDataRedisCacheService;
    @Autowired
    private AssetTypeGroupService groupService;
    @Autowired
    private AssetSyncDao assetSyncDao;
    @Autowired
    private FileConfiguration fileConfiguration;
    @Autowired
    private TerminalAssteInstallTimeService terminalAssteInstallTimeService;

    // 获取所有二级资产类型
    List<AssetType> assetTypes = null;

    // 获取所有资产信息：ip、mac、序列号、二级资产类型guid、资产guid、组织机构、责任人、安全域、涉密等级、数据来源、外部数据来源
    private List<AssetQueryVO> assets = null;

    // 获取所有二级终端资产类型Unicode
    List<String> hostTypeUnicodes = null;

    @Autowired
    private AssetClassifiedLevel assetClassifiedLevel;

    @Override
    public BaseRepository<AssetVerify, String> getRepository() {
        return assetVerifyRepository;
    }

    /**
     * 待审表：编辑时校验资产类型、ip、序列号、mac唯一性(忽略状态除外),ip格式校验，mac地址格式
     *        有效性校验：安全域、责任人
     * @param asetVerify
     * @return
     */
    @Override
    public Result<String> validateData(AssetVerifyVO asetVerify) {
        String guid = asetVerify.getGuid();
        if(StringUtils.isEmpty(guid)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据guid不能为空！");
        }
        // 资产类型校验
        Result<String> assetTypeValidate = validateAssetType(asetVerify);
        if (assetTypeValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return assetTypeValidate;
        }
        String ip = asetVerify.getIp();
        // ip地址格式、唯一校验
        Result<String> ipValidate = validateIp(ip,guid);
        if (ipValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return ipValidate;
        }

        // mac地址格式、唯一校验
        Result<String> macValidate = validateMac(asetVerify.getMac(),guid);
        if (macValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return macValidate;
        }
        // 序列号格式、唯一校验
        Result<String> serialNumberValidate = validateSerialNumber(asetVerify.getSerialNumber(),guid);
        if (serialNumberValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return serialNumberValidate;
        }
        // 有效性校验：责任人、安全域、涉密等级、国产校验：code进行校验，校验ok进行名称填充
        return validityValidate(asetVerify);
    }

    /**
     *  有效性校验：责任人、安全域、涉密等级、是否国产：code进行校验，校验ok进行名称填充
     * @param asetVerify
     * @return
     */
    private Result<String> validityValidate(AssetVerifyVO asetVerify) {
        // 责任人处理
        Result<String>  responsibleCodeValidateResult = responsibleCodeValidate(asetVerify);
        if (responsibleCodeValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return responsibleCodeValidateResult;
        }
        // 安全域处理
        Result<String>  domainCcdeValidateResult =domainCcdeValidate(asetVerify);
        if (domainCcdeValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return domainCcdeValidateResult;
        }
        // 涉密等级校验
        Result<String>  classifiedLevelValidateResult =classifiedLevelValidate(asetVerify);
        if (classifiedLevelValidateResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return classifiedLevelValidateResult;
        }
        // 是否国产：1：表示国产 2：非国产
        String termType = asetVerify.getTermType();
        if(org.apache.commons.lang3.StringUtils.isNotEmpty(termType) && !AssetValidateUtil.termTypeCodeValidate(termType)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"是否国产的值不合法,"+termType);
        }
        return ResultUtil.success("success");
    }




    private Result<String> responsibleCodeValidate(AssetVerifyVO asetVerify) {
        String responsibleCode = asetVerify.getResponsibleCode();
        if(StringUtils.isEmpty(responsibleCode)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前责任人不存在于平台人员信息库中，请确认！");
        }
        // 获取所有用户
        List<BasePersonZjg> persons= assetBaseDataCacheService.queryAllPerson();
        if(null == persons){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"Feign接口获取所有人信息为空");
        }
        BasePersonZjg person = getPersonByCode(responsibleCode, persons);
        if (null == person) {
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"责任人code不存在，"+responsibleCode);
        }
        // code存在进行name，组织机构信息覆盖处理
        asetVerify.setResponsibleName(person.getUserName());
        asetVerify.setOrgCode(person.getOrgCode());
        asetVerify.setOrgName(person.getOrgName());
        return ResultUtil.success("success");
    }

    private Result<String> domainCcdeValidate(AssetVerifyVO asetVerify) {
        String  domainCcde = asetVerify.getSecurityGuid();
        if(StringUtils.isEmpty( domainCcde)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"安全域code不能为空，"+domainCcde);
        }
        // 获取所有安全域
        List<BaseSecurityDomain> allDomains = assetBaseDataCacheService.queryAllDomain();
        if(null == allDomains){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"Feign接口获取所有安全域信息为空");
        }
        BaseSecurityDomain domain = getDomainByCode(domainCcde,allDomains);
        if(null == domain){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"安全域code不存在，"+domainCcde);
        }
        // 自动填充名称，subcode
        asetVerify.setDomainName(domain.getDomainName());
        asetVerify.setDomainSubCode(domain.getSubCode());
        return ResultUtil.success("success");
    }

    private Result<String> classifiedLevelValidate(AssetVerifyVO asetVerify) {
        String equipmentIntensive = asetVerify.getEquipmentIntensive();
        if(StringUtils.isEmpty( equipmentIntensive)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"涉密等级不能为空");
        }
        // 资产涉密等级
        List<BaseDictAll> secretLevels = assetBaseDataCacheService.queryAssetSecretLevels();
        if(null == secretLevels){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"Feign接口获取 资产涉密等级为空");
        }
        List<String> codes =getSercretLevelCodes(secretLevels);
        if(!codes.contains(equipmentIntensive.trim())){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"涉密等级的值不合法,"+equipmentIntensive);
        }
        return ResultUtil.success("success");
    }
    private List<String> getSercretLevelCodes(List<BaseDictAll>  secretLevels) {
        List<String> codes = new ArrayList<>();
        for(BaseDictAll data :secretLevels){
            codes.add(data.getCode());
        }
        return codes;
    }
    private BaseSecurityDomain getDomainByCode(String domainCcde, List<BaseSecurityDomain> allDomain) {
        for (BaseSecurityDomain domain : allDomain) {
            if (domainCcde.equals(domain.getCode())) {
                return domain;
            }
        }
        return null;
    }

    private BasePersonZjg getPersonByCode(String userNo, List<BasePersonZjg> basePersonZjgList) {
        for (BasePersonZjg zig : basePersonZjgList) {
            if (userNo.equalsIgnoreCase(zig.getUserNo())) {
                return zig;
            }
        }
        return null;
    }

    private Result<String> validateAssetType(AssetVerifyVO asetVerify) {
        String assetTypeGuid = asetVerify.getAssetType();
        if (StringUtils.isEmpty(assetTypeGuid)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"资产类型不能为空");
        }
        // 根据二级资产类型guid获取类型：一级资产类型名称-二级资产类型名称
        AssetType assetType = assetTypeService.getOne(assetTypeGuid);
        if (null == assetType){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"资产类型不存在");
        }
        // 资产类型对应的一级资产类型是否存在
        String treeCode = assetType.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("treeCode",treeCodeGroup));
        List<AssetTypeGroup> groups = assetTypeGroupService.findAll(queryConditions);
        if(CollectionUtils.isEmpty(groups)){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"资产类型对应的一级资产类型不存在");
        }
        return ResultUtil.success("true");
    }

    /**
     * ip地址格式、唯一性校验
     * @param ip
     * @param guid :guid主要是针对编辑时，新增时guid为null
     * @return
     */
    public Result<String> validateIp(String ip, String guid ) {
        if(StringUtils.isEmpty(ip)){
            return ResultUtil.success("true");
        }
        // ip格式
        Result<String> ipValidate = AssetValidateUtil.ipFormat(ip);
        if (ipValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return ipValidate;
        }
        // ip唯一性校验
        List<QueryCondition> queryConditions=new ArrayList<>();
        if(!StringUtils.isEmpty(guid)){
            queryConditions.add(QueryCondition.notEq("guid",guid));// 不等于当前数据
        }
        queryConditions.add(QueryCondition.eq("ip",ip));
        long countIp = this.count(queryConditions);
        if(countIp > 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前ip已经存在");
        }
        return ResultUtil.success("true");
    }

    /**
     * mac地址格式、唯一性校验
     * @param mac
     * @param guid:guid主要是针对编辑时，新增时guid为null
     * @return
     */
    public Result<String> validateMac(String mac, String guid) {
        if(StringUtils.isEmpty(mac)){
            return ResultUtil.success("true");
        }
        // mac格式
        Result<String> macValidate = AssetValidateUtil.macFormat(mac);
        if (macValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return macValidate;
        }
        // mac唯一性校验
        List<QueryCondition> queryConditions=new ArrayList<>();
        if(!StringUtils.isEmpty(guid)){
            queryConditions.add(QueryCondition.notEq("guid",guid));// 不等于当前数据
        }
        queryConditions.add(QueryCondition.eq("mac",mac));
        long countIp = this.count(queryConditions);
        if(countIp > 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前mac已经存在");
        }
        return ResultUtil.success("true");
    }

    /**
     * 序列号唯一性校验
     * @param serialNumber
     * @param guid:guid主要是针对编辑时，新增时guid为null
     * @return
     */
    public Result<String> validateSerialNumber(String serialNumber, String guid) {
        if(StringUtils.isEmpty(serialNumber)){
            return ResultUtil.success("true");
        }
        Result<String> serialNumberValidate = AssetValidateUtil.serialNumberFormat(serialNumber);
        if (serialNumberValidate.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            return serialNumberValidate;
        }
        // 唯一性校验
        List<QueryCondition> queryConditions=new ArrayList<>();
        if(!StringUtils.isEmpty(guid)){
            queryConditions.add(QueryCondition.notEq("guid",guid));// 不等于当前数据
        }
        queryConditions.add(QueryCondition.eq("serialNumber",serialNumber));
        long countIp = this.count(queryConditions);
        if(countIp > 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"当前序列号已经存在，");
        }
        return ResultUtil.success("true");
    }



    /**
     * 编辑保存数据
     * @param assetVerifyVO
     * @return
     */
    @Override
    public Result<String> saveEditdData(AssetVerifyVO assetVerifyVO) {
        AssetVerify verify = this.getOne(assetVerifyVO.getGuid());
        if(null == verify){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据不存在");
        }
        String assetTypeGuid = assetVerifyVO.getAssetType();
        // 根据二级资产类型guid获取类型：一级资产类型名称-二级资产类型名称
        AssetType assetType = assetTypeService.getOne(assetTypeGuid);
        AssetVerify assetVerify = mapper.map(assetVerifyVO, AssetVerify.class);
        // 一级资产类型名称-二级资产类型名称
        assetVerify.setType(getAssetVerifyType(assetType));
        assetVerify.setTypeUnicode(assetType.getUniqueCode());
        assetVerify.setUpdateTime(new Date());
        // 更新为待入库状态
        assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSWAIT);
        // 编辑后将历史关联的资产id，入库信息清空
        assetVerify.setAssetId(null);
        assetVerify.setSyncMessage(null);
        // 保留同步时一些数据
        assetVerify.setDataSourceType(verify.getDataSourceType());
        assetVerify.setSyncUid(verify.getSyncUid());
        assetVerify.setSyncSource(verify.getSyncSource());
        assetVerify.setSyncTime(verify.getSyncTime());
        String treeCode = assetType.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        verify.setTags(treeCodeGroup);
        // 责任人补全单位处理 2023-4-23
        responsibleCodeToOrgName(verify);
        this.save(assetVerify);
        // 保存扩展数据
        saveExtendAssetVerify(assetVerifyVO);
        return ResultUtil.success("success");
    }
    // 责任人补全单位处理
    private void responsibleCodeToOrgName(AssetVerify verify) {
        try{
            // 获取所有用户
            List<BasePersonZjg> persons= assetBaseDataCacheService.queryAllPerson();
            if(null == persons){
                return;
            }
            BasePersonZjg person = getPersonByCode(verify.getResponsibleCode(), persons);
            if (null == person) {
              return;
            }
            // code存在进行name，组织机构信息覆盖处理
            verify.setResponsibleName(person.getUserName());
            verify.setOrgCode(person.getOrgCode());
            verify.setOrgName(person.getOrgName());
        }catch (Exception e){
            logger.error("责任人补全单位异常",e);
        }
    }

    /**
     * 保存待审表扩展信息数据
     * @param assetVerifyVO
     */
    private void saveExtendAssetVerify(AssetVerifyVO assetVerifyVO) {
        if(StringUtils.isEmpty(assetVerifyVO.getExtendInfos())){
            return;
        }
        AssetExtendVerify extend = new AssetExtendVerify();
        extend.setAssetGuid(assetVerifyVO.getGuid());
        extend.setExtendInfos(assetVerifyVO.getExtendInfos());
        assetExtendVerifyService.save(extend);
    }

    /**
     * 根据二级资产类型获取类型：一级资产类型名称-二级资产类型名称
     * @param assetType
     * @return
     */
    @Override
    public String getAssetVerifyType(AssetType assetType) {
        String assetTypeName= assetType.getName();
        String treeCode = assetType.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("treeCode",treeCodeGroup));
        List<AssetTypeGroup> groups = assetTypeGroupService.findAll(queryConditions);
        String assetTypeGroupName ="未知";
        if(groups.size() > 0){
            assetTypeGroupName = groups.get(0).getName();
        }
        String type = assetTypeGroupName+"-"+assetTypeName;
        return type;
    }


    @Override
    public PageRes<AssetVerifyVO> query(AssetVerifySearchVO assetVerifySearch) {
        List<QueryCondition> queryConditions=new ArrayList<>();
        PageReq pager = mapper.map(assetVerifySearch, PageReq.class);
        pager.setOrder("createTime");
        pager.setBy("desc");
        addSearchCondition(queryConditions,assetVerifySearch);
        Page<AssetVerify> page=findAll(queryConditions,pager.getPageable());
        // 将实体进行替换处理
        PageRes<AssetVerify> data = PageRes.toRes(page);
        List<AssetVerify> lists = data.getList();
        List<AssetVerifyVO> datas = new ArrayList<>();
        for(AssetVerify assetver : lists){
            AssetVerifyVO assetVerifyVO = mapper.map(assetver, AssetVerifyVO.class);
            addExtendInfos(assetVerifyVO);
            datas.add(assetVerifyVO);
        }
        PageRes<AssetVerifyVO> res = new PageRes();
        res.setList(datas);
        res.setMessage(ResultCodeEnum.SUCCESS.getMsg());
        res.setCode(ResultCodeEnum.SUCCESS.getCode().toString());
        res.setTotal(page.getTotalElements());
        return res;
    }

    /**
     * 增加扩展信息
     * @param assetVerifyVO
     */
    private void addExtendInfos(AssetVerifyVO assetVerifyVO) {
       AssetExtendVerify extendVerify = assetExtendVerifyService.getOne(assetVerifyVO.getGuid());
       if(null != extendVerify){
           assetVerifyVO.setExtendInfos(extendVerify.getExtendInfos());
       }
    }

    /**
     * 查询条件
     * @param queryConditions
     * @param assetVerifySearch
     */
    private void addSearchCondition(List<QueryCondition> queryConditions, AssetVerifySearchVO assetVerifySearch) {
        // type
        if(!StringUtils.isEmpty(assetVerifySearch.getType())){
            queryConditions.add(QueryCondition.like("type",assetVerifySearch.getType()));
        }
        // ip
        if(!StringUtils.isEmpty(assetVerifySearch.getIp())){
            queryConditions.add(QueryCondition.like("ip",assetVerifySearch.getIp()));
        }
        // name
        if(!StringUtils.isEmpty(assetVerifySearch.getName())){
            queryConditions.add(QueryCondition.like("name",assetVerifySearch.getName()));
        }
        // SyncStatus
        if(assetVerifySearch.getSyncStatus() > 0){
            queryConditions.add(QueryCondition.eq("syncStatus",assetVerifySearch.getSyncStatus()));
        }
    }

    /**
     * 忽略
     * @param guid
     * @return
     */
    @Override
    public Result<String> neglect(String guid) {
        AssetVerify assetVerify =this.getOne(guid);
        assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSNEG); // 更新为忽略状态
        // 忽略后将历史关联的资产id，入库信息清空
        assetVerify.setAssetId(null);
        assetVerify.setSyncMessage(null);
        this.save(assetVerify);
        return ResultUtil.success("success");
    }

    /**
     *  入库
     *   1. 判断数据是不是存在
     *   2. 必填子段校验
     *   3. 格式校验：IP、mac、序列号
     *   4. 重复性校验：IP、mac、序列号
     *   5. 有效性校验：责任人、责任单位校验：code进行校验，校验ok进行名称填充
     *   6. 保存数据
     *   7. 资产类型为终端：操作系统安装时间记录到asset_terminal_install_time表中
     *   8. 数据写入csv文件中：baseinfo_dev
     *   9. 数据变化发kafka消消息
     * @param guid
     * @return
     */
    @Override
    public Result<String> saveAsset(String  guid) {
        AssetVerify assetVerify =this.getOne(guid);
        if(null == assetVerify){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"数据不能存在");
        }
        // 是不是处于入库状态
        if(AssetDataSyncConstant.SYNCSTATUSWAIT != assetVerify.getSyncStatus()){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"该数据处于入库状态");
        }
        // 数据校验
        Map<String,Object> handleResult = assetVerifyToAssetDataHandle(assetVerify);
        if("error".equals(handleResult.get("status"))){ // 校验失败，记录失败原因，状态改为入库失败
            assetVerify.setSyncMessage(handleResult.get("msg").toString());
            assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSFAIL); // 入库失败
            this.save(assetVerify);
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),handleResult.get("msg").toString());
        }
        Asset asset = (Asset)handleResult.get("asset");
        AssetType assetType = (AssetType)handleResult.get("assetType");
        String exist = (String)handleResult.get("exist");
        String oldIp = (String)handleResult.get("oldIp");
        // 获取对应的一级,放在tags字段中，策略维表用到改字段 2023-09-19
        String treeCode = assetType.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        asset.setTags(treeCodeGroup);
        // 保存数据
        assetService.save(asset);
        // 保存扩展信息 2022-07-12
        saveAssetExtendInfo(asset,guid);
        // 更新数据状态
        assetVerify.setSyncMessage("入库成功");
        assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSSUCCESS); // 入库成功
        assetVerify.setAssetId(asset.getGuid());
        this.save(assetVerify);
        // 资产类型为终端：操作系统安装时间记录到asset_terminal_install_time表中
        assetTerminalInstallTime(handleResult,asset);

        // 资产告警事件处理 2022-07-11
        assetChangeSendAlarmEvent(handleResult,assetType,asset);
        // 终端设置统计审计客户端安装情况 2022-07-13
        terminalAssetInstallService.sendCountKafkaMsg();
        // 更新redis缓存  2022-08-09
        changeRedisCache(asset,assetType,exist,oldIp);
        // 发消息
        messageService.sendKafkaMsg("asset");
        return ResultUtil.success("success");
    }

    // 更新redis缓存 2022-08-09
    private void changeRedisCache(Asset asset, AssetType assetType, String exist ,String oldIp) {
        AssetRedisCacheVO assetCache= mapper.map(asset,AssetRedisCacheVO.class);
        assetCache.setTypeName(assetType.getName());
        String treeCode = assetType.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        List<QueryCondition> conditionList=new ArrayList<>();
        conditionList.add(QueryCondition.like("treeCode",treeCodeGroup));
        List<AssetTypeGroup> groups = groupService.findAll(conditionList);
        if(!CollectionUtils.isEmpty(groups)){
            assetCache.setGroupName(groups.get(0).getName());
        }
        if("success".equals(exist)){
            // 编辑处理
            baseDataRedisCacheService.editAsset(assetCache,assetType.getTreeCode(),oldIp);
        }else{
            // 新增处理
            baseDataRedisCacheService.addAsset(assetCache);
        }

    }

    private void assetChangeSendAlarmEvent(Map<String,Object> handleResult,AssetType assetType, Asset asset) {
        AlarmEventMsgVO event = new AlarmEventMsgVO();
        String exist =(String)handleResult.get("exist");
        if("success".equals(exist)){
            event.setOsType(AssetAlarmServiceImpl.OSTYPEEDIT);
        }else{
            event.setOsType(AssetAlarmServiceImpl.OSTYPESAVE);
        }
        Object osSetupTimeObj = handleResult.get("osSetupTime");
        Date osSetupTimeOld = null;
        if(null != osSetupTimeObj){
            osSetupTimeOld =(Date)osSetupTimeObj;
        }

        Object osListbj = handleResult.get("osList");
        String osListOld = "";
        if(!StringUtils.isEmpty(osListbj)){
            osListOld = String.valueOf(osListbj);
        }
        event.setSyncSource(asset.getSyncSource());
        event.setTypeTreeCode(assetType.getTreeCode());
        event.setIp(asset.getIp());
        event.setOsSetuptime(asset.getOsSetuptime());
        event.setOsList(asset.getOsList());
        event.setOsSetuptimeOld(osSetupTimeOld);
        event.setOsListOld(osListOld);
        assetAlarmService.assetChangeSendAlarmEvnet(event);
    }
    /**
     * 保存扩展信息数据
     * @param asset
     * @param verifyGuid
     */
    private void saveAssetExtendInfo(Asset asset,String  verifyGuid) {
        AssetExtendVerify extendVerify = assetExtendVerifyService.getOne(verifyGuid);
        AssetExtend extend = new AssetExtend();
        extend.setAssetGuid(asset.getGuid());
        if(null != extendVerify){
            extend.setExtendInfos(extendVerify.getExtendInfos());
        }else{ //为空处理,资产扩展内容不能为空
            Map<String,String> param = new HashMap<>();
            param.put("guid",asset.getGuid());
            extend.setExtendInfos(JSON.toJSONString(param));
        }
        assetExtendService.save(extend);
    }

    /**
     * 操作系统安装时间记录：异步操作
     * @param handleResult
     * @param asset
     */
    private void assetTerminalInstallTime( Map<String,Object> handleResult, Asset asset) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String exist =(String)handleResult.get("exist");
                if("success".equalsIgnoreCase(exist)){
                    //存在更新处理
                    Object osSetupTimeObj = handleResult.get("osSetupTime");
                    Date osSetupTimeOld = null;
                    if(null != osSetupTimeObj){
                        osSetupTimeOld =(Date)osSetupTimeObj;
                    }
                    terminalAddQue(osSetupTimeOld,asset,"2");
                }else{
                    // 不存在新增处理
                    terminalAddQue(null,asset,"1");
                }
            }
        }).start();
    }
    private void terminalAddQue(Date oldOsSetupTime, Asset assetNew,String type)  {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    TerminalAssteInstallTimeJobVO terminalAssteInstallTimeJobVO = new TerminalAssteInstallTimeJobVO();
                    terminalAssteInstallTimeJobVO.setAsset(assetNew);
                    terminalAssteInstallTimeJobVO.setOldOsSetupTime(oldOsSetupTime);
                    terminalAssteInstallTimeJobVO.setType(type);
                    terminalAssteInstallTimeService.excTerminalAssteInstallTime(terminalAssteInstallTimeJobVO);
                }catch (Exception e){
                    logger.error("系统安装时间数据存放队列异常",e);
                }
            }
        }).start();

    }

    /**
     * 待申表入主表校验处理
     * @param assetVerify
     * @return
     */
    private Map<String,Object> assetVerifyToAssetDataHandle(AssetVerify assetVerify){
        Map<String,Object> result = new HashMap<String,Object>();
        result.put("status","error");
        String guid = assetVerify.getGuid();
        if(StringUtils.isEmpty(guid)){
            result.put("msg","当前数据guid不能为空");
            return result;
        }
        AssetVerify assetVerifyData =this.getOne(guid);
        if(null == assetVerifyData){
            result.put("msg","当前数据不存在");
            return result;
        }
        AssetType assetType = assetTypeValidate(assetVerify,result); // 资产类型校验
        if(null == assetType){
            return  result;
        }
        Asset asset = mapper.map(assetVerify, Asset.class);
        asset.setTypeUnicode(assetType.getUniqueCode());
        // 判断是不是usb资产及数据是不是存在
        boolean isUsb = AssetValidateUtil.isUsb(assetType.getTreeCode());
        Asset assetQuey = dataExistAsset(assetVerify.getIp(),assetVerify.getSerialNumber(),isUsb);
        String assetGuid = null;
        String exist="error";
        Date osSetupTimeOlld = null;
        String osListOld =null;
        String oldIp = null;
        Date date = new Date();
        if(null != assetQuey){ // 存在获取资产guid
            assetGuid = assetQuey.getGuid();
            exist="success";
            osSetupTimeOlld =assetQuey.getOsSetuptime();
            osListOld = assetQuey.getOsList();
            oldIp = assetQuey.getIp();
        }
        // 数据校验
        Result<String> validateAssetResult = assetValidateService.validateAsset(asset,assetGuid);
        if (validateAssetResult.getCode().equals(ResultCodeEnum.UNKNOW_FAILED.getCode())){
            result.put("msg",validateAssetResult.getMsg());
            return result;
        }
        if(StringUtils.isEmpty(assetGuid)){
            assetGuid = UUIDUtils.get32UUID();
        }
        asset.setGuid(assetGuid);
        asset.setCreateTime(date);
        SyncDataUtil.initAsset(asset);
        result.put("status","success");
        result.put("asset",asset);
        result.put("assetType",assetType);
        result.put("exist",exist);
        result.put("osSetupTime",osSetupTimeOlld);
        result.put("osList",osListOld);
        result.put("oldIp",oldIp);
        return result;
    }

    // 资产类型校验
    private AssetType assetTypeValidate(AssetVerify assetVerify, Map<String, Object> result) {
        String assetTypeGuid = assetVerify.getAssetType();
        if(StringUtils.isEmpty(assetTypeGuid)){
            result.put("msg","资产类型不能为空");
            return null;
        }
        AssetType assetType = assetTypeService.getOne(assetTypeGuid);
        if (null == assetType){
            result.put("msg","资产类型不存在");
            return null;
        }
        return assetType;
    }

    /**
     * 批量数据入库：
     * 1. 待申表中所有待入库的数据
     * 2. 相关校验处理：
     *        判断数据是不是存在
     *        必填子段校验
     *        格式校验：IP、mac、序列号
     *        重复性校验：IP、mac、序列号
     *        有效性校验：责任人、责任单位校验：code进行校验，校验ok进行名称填充
     *  3. 校验成功的执行入库，校验失败的记录失败原因
     *  4. 入库的数据，资产类型为终端：操作系统安装时间记录到asset_terminal_install_time表中
     *  5. 入库的数据，数据写入csv文件中：baseinfo_dev
     *  6. 存在入库的数据，数据变化发kafka消消息
     * @return
     */
    @Override
    public Result<String> batchSaveAsset() {
        List<QueryCondition> queryConditions=new ArrayList<>();
        queryConditions.add(QueryCondition.eq("syncStatus",2));
        List<AssetVerify> assetVerifys= this.findAll(queryConditions);
        if(assetVerifys.size() == 0){
            return ResultUtil.error(ResultCodeEnum.UNKNOW_FAILED.getCode(),"没有待入库的数据");
        }
        // 获取扩展信息 2022-07-12
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.in("assetGuid",getGuids(assetVerifys)));
        List<AssetExtendVerify> extendVerifies = assetExtendVerifyService.findAll(conditions);
        // 异步处理数据
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    excBatchSaveAsset(assetVerifys,extendVerifies);
                }catch (Exception e){
                    logger.error("异步批量入库异常",e);
                }

            }
        }).start();
        return ResultUtil.success("批量数据入库处理中");
    }

    private List<String> getGuids(List<AssetVerify> assetVerifys) {
        List<String> guids = new ArrayList<>();
        for(AssetVerify asset : assetVerifys){
            guids.add(asset.getGuid());
        }
        return guids;
    }

    /**
     * 批量数据入库处理
     * @param assetVerifys
     */
    private  void  excBatchSaveAsset(List<AssetVerify> assetVerifys,List<AssetExtendVerify> extendVerifies){
        logger.debug("异步批量入库开始");
        // 初始化数据
        initData();
        // 数据处理
        Map<String,Object> handleResult = assetVeriftyHandle(assetVerifys,extendVerifies);
        List<AssetValidateVO> validateSuccess =(List<AssetValidateVO>)handleResult.get("validateSuccess");  // 成功的数据
        List<AssetVerify> validateFail =(List<AssetVerify>)handleResult.get("validateFail");  // 失败的数据
        List<AssetValidateVO> terAssetList =(List<AssetValidateVO>)handleResult.get("terAssetList");  // 用于存在数据系统安装时间更新
        // 数据校验及数据组装
        List<Asset> assets = new ArrayList<Asset>();  //校验成功的资产
        List<AssetExtend> assetExtends = new ArrayList<>(); //资产扩展数据 2022-07-12
        List<AssetVerify> assetVerifyList =new ArrayList<>();
        List<String> assetGuids = new ArrayList<>(); // 存在的资产guid，主要是为了记录系统安装时间用
        if(validateSuccess.size() > 0){
            batchAssetValidateServiceImpl.batchValidateAsset(validateSuccess,assets,assetGuids,assetVerifyList,assetExtends);
        }
        // 校验成功的数据，入库处理;校验失败的记录失败原因
        saveData(assetVerifyList,assets,assetGuids,validateFail,terAssetList,validateSuccess,assetExtends);
        logger.debug("异步批量入库结束");
    }

    /**
     * 初始化数据
     */
    private void initData() {
        // 获取所有二级资产类型
        assetTypes = assetBaseDataCacheService.queryAllAssetType();
        // 获取所有资产信息：ip、mac、序列号、二级资产类型guid、资产guid、组织机构、责任人、安全域、涉密等级、系统安装时间、数据来源、外部数据来源
         assets  = assetDao.getAllAssetSync();
        // 获取所有二级终端资产类型Unicode
        hostTypeUnicodes = assetBaseDataCacheService.queryAllAssetHostTypeUnicode();
    }


    /**
     *  数据处理
     *
     *  1. 资产类型校验
     *  2. usb判断
     *  3. 数据是不是存在
     * @param assetVerifys
     */
    private Map<String,Object> assetVeriftyHandle(List<AssetVerify> assetVerifys,List<AssetExtendVerify> extendVerifies){
        // 资产类型校验、不存在数据统计
        List<AssetVerify> validateFail = new ArrayList<>(); // 校验失败的数据
        List<AssetValidateVO> validateSuccess = new ArrayList<>();  // 校验成功的数据
        List<AssetValidateVO> assetList = new ArrayList<>();  // 用于存在数据系统安装时间更新
        Map<String,Object> result = new HashMap<>();
        AssetValidateVO data =null;
        for(AssetVerify assetVerify : assetVerifys) {
            String assetTypeGuid = assetVerify.getAssetType();
            if (StringUtils.isEmpty(assetTypeGuid)) {
                assetVerify.setSyncMessage("资产类型不能为空");
                assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSFAIL); // 入库失败
                validateFail.add(assetVerify);
                continue;
            }
            AssetType assetType = getAssetTypeByGuid(assetTypeGuid,assetTypes);
            if (null == assetType) {
                assetVerify.setSyncMessage("资产类型不存在");
                assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSFAIL); // 入库失败
                validateFail.add(assetVerify);
                continue;
            }
            // 判断是不是usb资产及数据是不是存在
            boolean isUsb = AssetValidateUtil.isUsb(assetType.getTreeCode());
            AssetQueryVO assetQuey =assetSyncService.batchDataExistAsset(assetVerify.getIp(), assetVerify.getSerialNumber(), isUsb,assets);
            data = new AssetValidateVO();
            if (null != assetQuey) { // 存在获取资产guid
                String assetGuid = assetQuey.getGuid();
                assetVerify.setAssetId(assetGuid);
                data.setOsSetuptime(assetQuey.getOsSetuptime());
                data.setCurOsSetuptime(assetVerify.getOsSetuptime());
                data.setAssetGuid(assetGuid);
                assetList.add(data);
                data.setExistOld(true);
                data.setOsList(assetQuey.getOsList());
                data.setAssetTypeTreeCode(assetType.getTreeCode());
                data.setAssetVerify(assetVerify);
            }else{
                assetVerify.setAssetId(null);
                data.setAssetTypeTreeCode(assetType.getTreeCode());
                data.setExistOld(false);
                data.setAssetVerify(assetVerify);
            }

            // 获取对应的扩展信息 2022-07-12
            addAssetExtendVerify(data,extendVerifies,assetVerify.getGuid());
            validateSuccess.add(data);
        }
        result.put("validateFail",validateFail);
        result.put("validateSuccess",validateSuccess);
        result.put("terAssetList",assetList);
        return result;
    }

    /**
     * 添加对应的扩展信息数据
     * @param data
     * @param extendVerifies
     * @param guid
     */
    private void addAssetExtendVerify(AssetValidateVO data, List<AssetExtendVerify> extendVerifies, String guid) {
        if(null == extendVerifies || extendVerifies.size() == 0){
            return;
        }
        AssetExtendVerify extendVerify = getAssetExtendVerify(extendVerifies,guid);
        if(null != extendVerify){
            data.setAssetExtendVerify(extendVerify);
        }
    }

    private AssetExtendVerify getAssetExtendVerify(List<AssetExtendVerify> extendVerifies, String guid) {
        for(AssetExtendVerify extendVerify : extendVerifies){
            if(guid.equals(extendVerify.getAssetGuid())){
                return extendVerify;
            }
        }
        return  null;
    }


    /**
     * 获取资产类型
     * @param assetGuid
     * @return
     */
    private AssetType getAssetTypeByGuid(String assetGuid,List<AssetType> assetTypes) {
        for(AssetType assetType : assetTypes){
            if(assetGuid.equals(assetType.getGuid())){
                return assetType;
            }
        }
        return null;
    }
    /**
     * 校验成功的数据，入库处理;校验失败的记录失败原因
     *
     * @param assetVerifys
     * @param assets  校验成功的数据
     * @param assetGuids 数据存在的资产guid
     * @param validateFail 校验失败的部分数据
     */
    private void saveData(List<AssetVerify> assetVerifys, List<Asset> assets, List<String> assetGuids,List<AssetVerify> validateFail,List<AssetValidateVO> terAssetList,List<AssetValidateVO> validates, List<AssetExtend> assetExtends){
        // 校验成功的数据入库处理
        if(assets.size() > 0){
            assetService.save(assets); // 入库处理
            otherHandle(validates);

        }
        // 扩展信息入库 2022-07-12
        if(assetExtends.size() > 0){
            assetExtendService.save(assetExtends);
        }
        // 更新数据状态
        if(validateFail.size() > 0){
            assetVerifys.addAll(validateFail);   // 加上资产类型校验失败的
        }
        this.save(assetVerifys);
    }

    private void otherHandle(List<AssetValidateVO> validates) {
        terminalAssteInstallTimeAddQue();// 更新操作系统安装时间
        // 资产变化发告警事件 2022-07-11
        assetAlarmEvent(validates);
        // 终端设置统计审计客户端安装情况 2022-07-13
        terminalAssetInstallService.sendCountKafkaMsg();
        // 全量更新资产相关缓存 2022-08-09
        baseDataRedisCacheService.updateAllAssetCache();
    }

    /**
     *  资产变动触发告警事件 2022-07-11
     * @param alarmAssetList
     */
    private void assetAlarmEvent(List<AssetValidateVO> alarmAssetList) {
        List<AlarmEventMsgVO> eventMsgs = new ArrayList<>();
        for(AssetValidateVO data : alarmAssetList){
            if(!data.isCheckSucess()){  // 校验失败不处理
                continue;
            }
            eventMsgs.add(getEventMsg(data));
        }
        if(eventMsgs.size() == 0){
            return;
        }
        assetAlarmService.assetChangeSendAlarmEvnets(eventMsgs);
    }
    private AlarmEventMsgVO getEventMsg(AssetValidateVO data) {
        AlarmEventMsgVO event = new AlarmEventMsgVO();
        if(data.isExistOld()){
            event.setOsType(AssetAlarmServiceImpl.OSTYPEEDIT);
        }else{
            event.setOsType(AssetAlarmServiceImpl.OSTYPESAVE);
        }
        event.setSyncSource(data.getAssetVerify().getSyncSource());
        event.setTypeTreeCode(data.getAssetTypeTreeCode());
        event.setIp(data.getAssetVerify().getIp());
        event.setOsSetuptime(data.getAssetVerify().getOsSetuptime());
        event.setOsList(data.getAssetVerify().getOsList());
        event.setOsSetuptimeOld(data.getOsSetuptime());
        event.setOsListOld(data.getOsList());
        return event;
    }

    private void terminalAssteInstallTimeAddQue() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    TerminalAssteInstallTimeJobVO terminalAssteInstallTimeJobVO = new TerminalAssteInstallTimeJobVO();
                    terminalAssteInstallTimeJobVO.setType("4");   // 待审库批量入库
                    terminalAssteInstallTimeService.excTerminalAssteInstallTime(terminalAssteInstallTimeJobVO);
                }catch(Exception e){
                    logger.error("操作系统安装时间处理异常",e);
                }
            }
        }).start();

    }


    /**
     * 判断数据是不是存在(asset表中)
     * 非usb进行ip判断
     * usb进行序列号判断
     * @param ip
     * @param serialNumber
     * @param isUsb
     * @return
     */
    public Asset dataExistAsset(String ip,String serialNumber, boolean isUsb) {
        if (!isUsb){ // 非usb
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("ip",ip));
            List<Asset> assets = assetService.findAll(conditions);
            if(null != assets && assets.size() > 0){
                return assets.get(0);
            }
            return null;
        }else{ //usb(USB存储介质、USB外设 )
            List<QueryCondition> conditions = new ArrayList<>();
            conditions.add(QueryCondition.eq("serialNumber",serialNumber));
            List<Asset> assets = assetService.findAll(conditions);
            if(null != assets && assets.size() > 0){
                return assets.get(0);
            }
            return null;
        }
    }





    /**
     * 比对入统一台账库处理
     * @param assetVerifys
     */
    @Override
    public void saveAssetVerifys(List<AssetVerifyCompareVO> assetVerifys) {
        //统一台账历史数据
        List<AssetType> assetTypes = assetTypeService.findAll();
        List<AssetTypeGroup> assetTypeGroups = assetBaseDataCacheService.queyAllAssetTypeGroup();
        // 对于正式库数据入统一库处理
        List<AssetVerifyCompareVO> datas = assetDataHandle(assetVerifys,assetTypes,assetTypeGroups);
        // 数据保存
        excsaveData(datas);
    }

    /**
     * 对于正式库数据入统一库处理
     * @param assetVerifys
     * @return
     */
    private List<AssetVerifyCompareVO> assetDataHandle(List<AssetVerifyCompareVO> assetVerifys, List<AssetType> assetTypes,List<AssetTypeGroup> assetTypeGroups) {
        List<String> guids = new ArrayList<>();
        List<AssetVerifyCompareVO> results = new ArrayList<>();
        for(AssetVerifyCompareVO compare : assetVerifys){
            if(compare.isAsset()){  // 采用正式库覆盖的场景
                guids.add(compare.getAssetGuid());
            }else{
                // 类型数据构造
                addType(assetTypes,assetTypeGroups,compare.getAssetVerify());
                results.add(compare);
            }
        }
        if(CollectionUtils.isEmpty(guids)){
            return results;
        }
        //存在采用正式库覆盖入统一台账情况处理
        List<QueryCondition> conditions = new ArrayList<>();
        conditions.add(QueryCondition.in("guid",guids));
        List<Asset> assets = assetService.findAll(conditions);
        conditions = new ArrayList<>();
        conditions.add(QueryCondition.in("assetGuid",guids));
        List<AssetExtend> assetExtends =assetExtendService.findAll(conditions);
        // 构造数据
        assetVerifyStructure(results,assets,assetExtends,assetTypes,assetTypeGroups);
        return results;
    }

    /**
     * 处理type的值
     * @param assetTypes
     * @param assetTypeGroups
     * @param assetVerify
     */
    private void addType(List<AssetType> assetTypes, List<AssetTypeGroup> assetTypeGroups, AssetVerify assetVerify) {
        String typeGuid = assetVerify.getAssetType();
        if(StringUtils.isEmpty(typeGuid)){
            assetVerify.setType("未知");
            return;
        }
        AssetType assetype = getAssetTypeByGuid(typeGuid,assetTypes);
        if(null == assetype){
            assetVerify.setType("未知");
            return;
        }
        String treeCode = assetype.getTreeCode();
        if(StringUtils.isEmpty(treeCode)){
            assetVerify.setType("未知");
            return;
        }
        String groupName = getGroupNameByTreeCode(treeCode,assetTypeGroups);
        String type = groupName+"-"+assetype.getName();
        assetVerify.setType(type);
    }
    public String getGroupNameByTreeCode(String treeCode,List<AssetTypeGroup> assetTypeGroups) {
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        AssetTypeGroup assetTypeGroup = getAssetTypeGroupByTreeCode(treeCodeGroup,assetTypeGroups);
        String assetTypeGroupName ="未知";
        if(null != assetTypeGroup){
            assetTypeGroupName = assetTypeGroup.getName();
        }
        return assetTypeGroupName;
    }

    private void assetVerifyStructure(List<AssetVerifyCompareVO> results, List<Asset> assets, List<AssetExtend> assetExtends,List<AssetType> assetTypes, List<AssetTypeGroup> assetTypeGroups) {
        for(Asset asset : assets){
            AssetVerifyCompareVO assetVerifyCompareVO = new AssetVerifyCompareVO();
            AssetVerify assetVerify = mapper.map(asset, AssetVerify.class);
            assetVerify.setGuid(UUIDUtils.get32UUID());
            assetVerify.setCreateTime(new Date());
            assetVerify.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSEDIT); //待编辑状态
            addType(assetTypes,assetTypeGroups,assetVerify);
            assetVerifyCompareVO.setAssetVerify(assetVerify);
            AssetExtendVerify assetExtendVerify = getAssetExendVerify(asset,assetExtends);
            assetVerifyCompareVO.setExtendInfos(assetExtendVerify.getExtendInfos());
            results.add(assetVerifyCompareVO);
        }
    }

    private AssetExtendVerify getAssetExendVerify(Asset asset, List<AssetExtend> assetExtends) {
        if(CollectionUtils.isEmpty(assetExtends)){
            return new AssetExtendVerify();
        }
        AssetExtend assetExtend =  getAssetExtend(assetExtends,asset.getGuid());
        if(null == assetExtend){
            return new AssetExtendVerify();
        }
        AssetExtendVerify assetVerifyExtend = mapper.map(assetExtend, AssetExtendVerify.class);
        return assetVerifyExtend;
    }

    private AssetExtend getAssetExtend(List<AssetExtend> assetExtends, String guid) {
        for(AssetExtend extend : assetExtends){
            if(guid.equals(extend.getAssetGuid())){
                return extend;
            }
        }
        return null;
    }


    private void excsaveData(List<AssetVerifyCompareVO> newDatas) {
        List<AssetVerify> assetVerifys= new ArrayList<>();
        List<AssetExtendVerify> assetExtendVerifys= new ArrayList<>();
        for(AssetVerifyCompareVO data : newDatas){
            AssetVerify assetVerify = data.getAssetVerify();
            assetVerifys.add(assetVerify);
            assetExtendVerifys.add(getAssetExtendVerifys(data.getExtendInfos(),assetVerify.getGuid()));
        }
        if(!CollectionUtils.isEmpty(assetVerifys)){
            this.save(assetVerifys);
        }
        if(!CollectionUtils.isEmpty(assetExtendVerifys)){
           this.assetExtendVerifyService.save(assetExtendVerifys);
        }
    }

    private AssetExtendVerify getAssetExtendVerifys(String extendInfo , String guid) {
        AssetExtendVerify assetExtendVerify = new AssetExtendVerify();
        assetExtendVerify.setExtendInfos(extendInfo);
        assetExtendVerify.setAssetGuid(guid);
        return assetExtendVerify;
    }


    /**
     * 获取待申表type的值：一级资产类型名称-二级资产类型名称
     * @param assetType
     * @return
     */
    public String getAssetVerifyType(AssetType assetType,List<AssetTypeGroup> assetTypeGroups) {
        String assetTypeName= assetType.getName();
        String treeCode = assetType.getTreeCode();
        int indexTwo = treeCode.lastIndexOf('-');
        String treeCodeGroup =  treeCode.substring(0, indexTwo); // 获取一级类型
        AssetTypeGroup assetTypeGroup = getAssetTypeGroupByTreeCode(treeCodeGroup,assetTypeGroups);
        String assetTypeGroupName ="未知";
        if(null != assetTypeGroup){
            assetTypeGroupName = assetTypeGroup.getName();
        }
        String type = assetTypeGroupName+"-"+assetTypeName;
        return type;
    }
    private AssetTypeGroup getAssetTypeGroupByTreeCode(String treeCodeGroup, List<AssetTypeGroup> assetTypeGroups) {
        for(AssetTypeGroup group : assetTypeGroups){
            if(treeCodeGroup.equals(group.getTreeCode())){
                return group;
            }
        }
        return  null;
    }


    @Override
    public Result<String> exportAssetInfo(AssetVerifySearchVO assetVerifySearchVO) {
        String fileName = "统一台账" + DateUtils.date2Str(new Date(), "yyyyMMddHHmmss");
        String rootPath = fileConfiguration.getAssetOnLinePath();
        File targetFile = new File(rootPath);
        if (!targetFile.exists()) {
            targetFile.mkdirs();
        }
        String filePath = Paths.get(rootPath, fileName).toString();
        try {
            // 获取导出数据
            List<AssetVerifyExportVO> datas = getAssetVerifyExportDatas(assetVerifySearchVO);
            ExportExcelUtils.getInstance().createExcel(datas, AssetVerifyExportVO.class, filePath);
            return ResultUtil.success(fileName);
        } catch (ExcelException | IOException | NoSuchFieldException | IllegalAccessException e) {
            logger.error("导出excel异常", e);
            return ResultUtil.error(-1,"导出excel异常");
        }
    }

    private List<AssetVerifyExportVO> getAssetVerifyExportDatas(AssetVerifySearchVO assetVerifySearchVO) {
        List<AssetVerifyExportVO> exportDatas = assetSyncDao.queryExportData(assetVerifySearchVO);
        List<BaseDictAll> baseDictAlls =assetClassifiedLevel.findAll();
        for(AssetVerifyExportVO data : exportDatas){
            assetVerifyVOHandle(data,baseDictAlls);
        }
        return exportDatas;
    }

    private void assetVerifyVOHandle(AssetVerifyExportVO assetVerifyVO,List<BaseDictAll> baseDictAlls) {
        String equipmentIntensive = assetVerifyVO.getEquipmentIntensive();
        // 涉密等级
        if(!StringUtils.isEmpty(equipmentIntensive)){
            String value = assetClassifiedLevel.getValueByCode(equipmentIntensive, baseDictAlls);
            assetVerifyVO.setEquipmentIntensive(value);
        }
    }

    @Override
    public void exportAssetFile(String fileName, HttpServletResponse response) {
        String realPath = fileConfiguration.getAssetOnLinePath();; // 文件路径
        FileUtil.downLoadFile(fileName + ".xls", realPath, response);
    }
}
