package com.vrv.vap.alarmdeal.business.asset.datasync.service.iml;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.analysis.model.TbConf;
import com.vrv.vap.alarmdeal.business.asset.datasync.constant.AssetDataSyncConstant;
import com.vrv.vap.alarmdeal.business.asset.datasync.dao.AssetSyncDao;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDetail;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetBookDiff;
import com.vrv.vap.alarmdeal.business.asset.datasync.model.AssetVerify;
import com.vrv.vap.alarmdeal.business.asset.datasync.repository.AssetBookDetailRepository;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetBookDetailService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetBookDiffService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.AssetVerifyService;
import com.vrv.vap.alarmdeal.business.asset.datasync.service.HandStrategyService;
import com.vrv.vap.alarmdeal.business.asset.datasync.util.DataCompareUtil;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.AssetVerifyCompareVO;
import com.vrv.vap.alarmdeal.business.asset.datasync.vo.DataCompareVO;
import com.vrv.vap.jpa.basedao.BaseRepository;
import com.vrv.vap.jpa.baseservice.impl.BaseServiceImpl;
import com.vrv.vap.jpa.common.DateUtil;
import com.vrv.vap.jpa.common.UUIDUtils;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.ResultUtil;
import com.vrv.vap.jpa.web.page.QueryCondition;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 台账明细操作
 * 2023-4
 * @author liurz
 */
@Service
@Transactional
public class AssetBookDetailServiceImpl extends BaseServiceImpl<AssetBookDetail, String> implements AssetBookDetailService {
    private static Logger logger = LoggerFactory.getLogger(AssetBookDetailServiceImpl.class);

    @Autowired
    private AssetBookDetailRepository assetBookDetailRepository;
    @Autowired
    private HandStrategyService handStrategyService;
    @Autowired
    private AssetSyncDao assetSyncDao;
    @Autowired
    private MapperUtil mapperUtil;
    @Autowired
    private AssetBookDiffService assetBookDiffService;
    @Autowired
    private AssetVerifyService assetVerifyService;

    @Override
    public BaseRepository<AssetBookDetail, String> getRepository() {
        return assetBookDetailRepository;
    }
    /**
     手动入库开始比对逻辑：
     1. 根据手动策略配置中选择的数据源、查询这些数据源在台账明显表中的所有数据
     2. 按照手动策略中唯一标识进行分组
     3. 分组后数据处理
     4. 所有数据都要与正式库数据比较
     5. 正式库数据存在,比较没有差异，用正式库数据作为最终入统一台账数据
     6. 正式库数据不存在,比较没有差异，除了比对规则字段外其他字段随机取
     7. 比较有差异，入统一台账差异表中
     * @return
     */
    @Override
    public void comparison() {
        logger.warn("==================异步执行开始比对==================");
       new Thread(new Runnable() {
           @Override
           public void run() {
               excComparison();
           }
       }).start();
    }


    private void excComparison(){
        try{
            logger.info("==================开始比对开始==================");
            long startTime = System.currentTimeMillis();
            // 根据手动策略：获取配置数据源、唯一标识、比对列配置
            Map<String,Object> configMessage = getStrategyConfigs();
            // 获取数据源下数据、正式库数据
            Map<String,Object> compareDatas = getCompareDatas(configMessage);
            // 数据比对处理
            Map<String,Object> compareResults =dataCompareHandle(configMessage,compareDatas);
            // 数据入库处理
            dataSaveHandle(compareResults);
            long endTime = System.currentTimeMillis();
            logger.info("==================开始比对结束,总耗时:"+(endTime - startTime)+"==================");
        }catch (Exception e){
            logger.error("开始比对异常",e);
        }
    }


    /**
     * 根据手动策略：获取配置数据源、唯一标识、比对列配置
     * @return
     */
    private Map<String, Object> getStrategyConfigs() {
        Map<String, Object> configs = new HashMap<>();
        // 获取策略配置信息
        List<TbConf> tbConfs=  handStrategyService.queyConfigAssets();
        if(CollectionUtils.isEmpty(tbConfs)){
            logger.error("策略配置信息不存在,不执行比对");
            throw new RuntimeException("策略配置信息不存在,不执行比对");
        }
        // 获取策略配置中数据源
        List<String> syscnDataSources = getSyscnDataSources(tbConfs);
        // 获取策略中配置唯一标识配置
        String flag = getUniqueFlag(tbConfs);
        logger.info("当前比对的唯一标识："+flag);
        // 获取比对列配置
        Map<String,Object> diffJons = getDiffJson(tbConfs);
        configs.put("synchDataSource",syscnDataSources);
        configs.put("flag",flag);
        configs.put("diffJons",diffJons);
        return configs;
    }
    /**
     * 获取比对数据：获取数据源下数据、正式库数据
     * @param configMessage
     * @return
     */
    private Map<String, Object> getCompareDatas(Map<String, Object> configMessage) {
        Map<String, Object> result = new HashMap<>();
        List<String> dataSources = (List<String>)configMessage.get("synchDataSource");
        // 获取数据源下的数据
        List<AssetBookDetail> datas = getDataSource(dataSources);
        if(CollectionUtils.isEmpty(datas)){
            logger.error("当前数据源没有明细数据,不执行比对，当前数据数据源为："+JSON.toJSONString(dataSources));
            throw new RuntimeException("当前数据源没有明细数据,不执行比对");
        }
        logger.info("当前比对明细数据数据，"+ datas.size());
        // 获取正式库数据asset
        List<AssetBookDetail> assets = assetSyncDao.getAllAssetComparison();
        result.put("detailDatas",datas);
        result.put("assets",assets);
        return result;
    }

    /**
     * 数据比对处理
     * 1. 按唯一标识分组处理
     * 2. 数据比对处理
     * @param configMessage
     * @param compareDatas
     * @return
     */
    private Map<String, Object> dataCompareHandle(Map<String, Object> configMessage, Map<String, Object> compareDatas) throws ParseException, NoSuchFieldException, IllegalAccessException {
        Map<String, Object> results =new HashMap<>();
        // 获取处理数据
        String flag = (String)configMessage.get("flag");
        Map<String,Object> diffJons = (Map<String,Object>)configMessage.get("diffJons");
        List<AssetBookDetail> details = (List<AssetBookDetail>)compareDatas.get("detailDatas");
        List<AssetBookDetail> assets = compareDatas.get("assets")==null?null:(List<AssetBookDetail>)compareDatas.get("assets");
        List<AssetBookDetail> fliterDatas = getFliterDataByType(flag,details);
        logger.info("去掉唯一标识为空的数据后，比对的数据量是，"+ fliterDatas.size());

        // 按照唯一标识分组
        Map<String,List<AssetBookDetail>> groupResult = fliterDatas.stream().collect(Collectors.groupingBy(item ->getGroupItem(flag,item)));

        // 分组后的数据比对处理
        List<AssetVerifyCompareVO> assetVerifys = new ArrayList<>() ;  // 统一台账数据
        List<AssetBookDiff> diffs = new ArrayList<>() ; // 差异数据
        groupDataHandle(groupResult,flag,assets,assetVerifys,diffJons,diffs);

        results.put("assetVerifys",assetVerifys);
        results.put("diffs",diffs);
        return results;
    }

    /**
     * 数据入库处理
     * 1. 入库前清除差异数据、统一台账数据
     * 2. 差异数据入库
     * 3. 统一台账数据入库
     * @param compareResults
     */
    private void dataSaveHandle(Map<String, Object> compareResults) {
        // 删除历史差异数据
        assetBookDiffService.deleteAllInBatch();
        // 删除统一台账数据
        assetVerifyService.deleteAllInBatch();
        List<AssetBookDiff> diffs = (List<AssetBookDiff>)compareResults.get("diffs");
        List<AssetVerifyCompareVO> assetVerifys = (List<AssetVerifyCompareVO>)compareResults.get("assetVerifys");
        // 历史差异数据保存
        if(!CollectionUtils.isEmpty(diffs)){
            logger.info("开始比对时入差异表数量："+diffs.size());
            assetBookDiffService.save(diffs);
        }
        // 统一台账数据保存
        if(!CollectionUtils.isEmpty(assetVerifys)){
            logger.info("开始比对时入统一台账库数量："+assetVerifys.size());
            assetVerifyService.saveAssetVerifys(assetVerifys);
        }
    }

    private List<AssetBookDetail> getDataSource(List<String> syscnDataSources) {
        List<QueryCondition> conditions = new ArrayList<>();
        logger.info("当前比对的数据源："+JSON.toJSONString(syscnDataSources));
        conditions.add(QueryCondition.in("syncSource",syscnDataSources));
        List<AssetBookDetail> datas = this.findAll(conditions);
        return datas;
    }

    /**
     * 分组后的数据比对处理
     *
     * @param groupResult
     * @param flag
     * @param assets
     * @param assetVerifys
     * @param diffJons
     * @return
     * @throws ParseException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private void groupDataHandle(Map<String, List<AssetBookDetail>> groupResult, String flag, List<AssetBookDetail> assets, List<AssetVerifyCompareVO> assetVerifys, Map<String, Object> diffJons,List<AssetBookDiff> diffs) throws ParseException, NoSuchFieldException, IllegalAccessException {
        for(String key :groupResult.keySet()){
            List<AssetBookDetail> list =  groupResult.get(key);
            if(CollectionUtils.isEmpty(list)){
                continue;
            }
            AssetBookDiff diff = getCompareDiffDatas(list,flag,assets,assetVerifys,diffJons);
            if(null != diff){
                diffs.add(diff);
            }
        }
    }


    /**
     * 根据配置的唯一标识，过滤出唯一标识不为空的数据
     * @param flag
     * @return
     */
    private List<AssetBookDetail> getFliterDataByType(String flag,List<AssetBookDetail> datas) {
        // ip地址
        if("ip".equals(flag)){
            return datas.stream().filter(item -> StringUtils.isNotEmpty(item.getIp())).collect(Collectors.toList());
        }
        // MAC地址
        if("mac".equals(flag)){
            return datas.stream().filter(item -> StringUtils.isNotEmpty(item.getMac())).collect(Collectors.toList());
        }
        // 序列号
        if("serialNumber".equals(flag)){
            return datas.stream().filter(item -> StringUtils.isNotEmpty(item.getSerialNumber())).collect(Collectors.toList());
        }
        return null;
    }

    /**
     * 分组后数据处理
     * 1. 所有数据都要与正式库数据比较
     * 2. 正式库数据存在,比较没有差异，用正式库数据作为最终入统一台账数据
     * 3. 正式库数据不存在,比较没有差异，除了比对规则字段外其他字段随机取
     * 4. 比较有差异，入统一台账差异表中
     * @param list
     * @param flag
     * @param assets
     * @return
     */
    private AssetBookDiff getCompareDiffDatas(List<AssetBookDetail> list, String flag, List<AssetBookDetail> assets,List<AssetVerifyCompareVO> assetVerifys,Map<String,Object> diffJons) throws NoSuchFieldException, IllegalAccessException, ParseException {
        AssetBookDiff diff = new AssetBookDiff();
        AssetBookDetail detail =  list.get(0);
        // 获取唯一标识对应的正式库数据
        AssetBookDetail asset =  getAssetData(flag,assets,detail);
        // 单条数据
        if(list.size()==1){
            return  singleDataHandle(list,asset,diffJons,assetVerifys,diff);
        }
        // 多条数据
        if(list.size()>1){
            return  multipleDataHandle(list,asset,diffJons,assetVerifys,diff);
        }
        return null;
    }



    /**
     * 单条数据数据
     * 1.正式库不存在，直接入统一台账库
     * 2.正式库存在，当前数据与正式库比较，没有差异的话，用正式库数据作为最终入统一台账数据
     * 3.正式库存在，当前数据与正式库比较，有差异的话，入统一台账差异表
     * @param lists
     * @param asset
     * @param diffJons
     * @param assetVerifys
     * @param diff
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private AssetBookDiff singleDataHandle(List<AssetBookDetail> lists, AssetBookDetail asset, Map<String, Object> diffJons, List<AssetVerifyCompareVO> assetVerifys,AssetBookDiff diff) throws NoSuchFieldException, IllegalAccessException {
        AssetBookDetail detail =  lists.get(0);
        // 如果正式库不存在,当前单条数据入统一台账库
        if(null == asset){
            structureAssetVerify(detail,assetVerifys);
            return null;
        }
        // 如果正式库存在,当前单条数据与正式库进行比较
        lists.add(asset);
        DataCompareVO result = DataCompareUtil.excCompare(lists,diffJons);
        // 比对没有差异的:用正式库数据作为最终入统一台账数据
        if(result.isResult()){
            isSameAndAssetExist(assetVerifys,asset);
            return null;
        }else{ // 有差异的构造差异数据
            return structureDiff(detail,diff,result,asset);
        }
    }

    private AssetBookDiff structureDiff(AssetBookDetail detail, AssetBookDiff diff, DataCompareVO result,AssetBookDetail asset) {
        diff.setRefDetailGuid(detail.getGuid());
        diffDataAdd(diff,result);
        diff.setRefAsetGuid(asset.getGuid());  //关联正式表guid
        diff.setGuid(UUIDUtils.get32UUID());
        diff.setCreateTime(new Date());
        return diff;
    }

    private void structureAssetVerify(AssetBookDetail detail, List<AssetVerifyCompareVO> assetVerifys) {
        AssetVerify assetVerify = getAssetVerifys(detail);
        AssetVerifyCompareVO vo = new AssetVerifyCompareVO();
        vo.setAssetVerify(assetVerify);
        vo.setExtendInfos(detail.getExtendInfos());
        vo.setAsset(false);
        assetVerifys.add(vo);
    }


    // 将磁盘序列号放入扩展内容中
    private String addExtendInfos(String extendInfos, List<String> extendDiskNumbers) {
        if(StringUtils.isEmpty(extendInfos)){
            return extendInfos;
        }
        if(CollectionUtils.isEmpty(extendDiskNumbers)){
            return extendInfos;
        }
        Map<String,Object> extendInfoMaps= JSON.parseObject(extendInfos,Map.class);
        extendInfoMaps.put("extendDiskNumber",extendDiskNumbers.get(0));
        return JSON.toJSONString(extendInfoMaps);
    }


    /**
     * 多条数据处理
     * @param lists
     * @param asset
     * @param diffJons
     * @param assetVerifys
     * @param diff
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private AssetBookDiff multipleDataHandle(List<AssetBookDetail> lists, AssetBookDetail asset, Map<String, Object> diffJons, List<AssetVerifyCompareVO> assetVerifys, AssetBookDiff diff) throws NoSuchFieldException, IllegalAccessException {
        // 正式库数据存在，加入进行一起比较
        if(null != asset){
            lists.add(asset);
        }
        // 执行比对
        DataCompareVO result = DataCompareUtil.excCompare(lists,diffJons);
        // 多条数据比对结果处理
        return multipleDataCompareResultHandle(assetVerifys,diff,lists,result,asset);
    }

    /**
     * 多条数据比对结果处理
     * @param assetVerifys
     * @param diff
     * @param lists
     * @param result
     * @param asset
     * @return
     */
    private AssetBookDiff multipleDataCompareResultHandle(List<AssetVerifyCompareVO> assetVerifys, AssetBookDiff diff, List<AssetBookDetail> lists, DataCompareVO result, AssetBookDetail asset) {
        // 比对没有差异的
        if(result.isResult()){
            AssetVerifyCompareVO vo = new AssetVerifyCompareVO();
            if(null != asset){ // 正式库数据存在的情况
                isSameAndAssetExist(assetVerifys,asset);
            }else{ // 正式库数据不存在的情况
                isSameAndNoAssetExist(result,lists,assetVerifys,vo);
            }
            return null;
        }else{ // 有差异
            diff.setRefDetailGuid(handleRefDetailGuid(result.getRefGuid()));
            diffDataAdd(diff,result);
            if(null != asset){
                diff.setRefAsetGuid(asset.getGuid());  //关联正式表guid
            }
            diff.setGuid(UUIDUtils.get32UUID());
            return diff;
        }
    }

    /**
     * 比对没有差异，正式库村在处理
     * @param assetVerifys
     * @param asset
     */
    private void isSameAndAssetExist(List<AssetVerifyCompareVO> assetVerifys, AssetBookDetail asset) {
        AssetVerifyCompareVO vo = new AssetVerifyCompareVO();
        vo.setAsset(true);
        vo.setAssetGuid(asset.getGuid());
        assetVerifys.add(vo);
    }

    /**
     *  比对没有差异，正式库数据不存在的情况
     * @param result
     * @param lists
     * @param assetVerifys
     * @param vo
     */
    private void isSameAndNoAssetExist(DataCompareVO result, List<AssetBookDetail> lists, List<AssetVerifyCompareVO> assetVerifys, AssetVerifyCompareVO vo) {
        // 正式库数据不存在的情况
        AssetVerify assetVerify = getAssetVerifyByDataCompare(result,lists);
        vo.setAssetVerify(assetVerify);
        vo.setExtendInfos(addExtendInfos(lists.get(0).getExtendInfos(),result.getExtendDiskNumber()));
        vo.setAsset(false);
        assetVerifys.add(vo);
    }

    private String handleRefDetailGuid(List<String> refGuids) {
        StringBuffer buffer = new StringBuffer();
        for(String data :refGuids){
            buffer.append(data).append(",");
        }
        String guids= buffer.toString();
        guids = guids.substring(0,guids.lastIndexOf(","));
        return guids;
    }

    private void diffDataAdd(AssetBookDiff data, DataCompareVO result) {
        data.setTypeGuid(listToString(result.getTypeGuid()));
        data.setTypeSnoGuid(listToString(result.getTypeSnoGuid()));
        data.setName(listToString(result.getName()));
        data.setAssetNum(listToString(result.getAssetNum()));
        data.setIp(listToString(result.getIp()));
        data.setMac(listToString(result.getMac()));
        data.setSerialNumber(listToString(result.getSerialNumber()));
        data.setEquipmentIntensive(listToString(result.getEquipmentIntensive()));
        data.setOsSetupTime(listToString(result.getOsSetuptime()));
        data.setRegisterTime(listToString(result.getRegisterTime()));
        data.setOsList(listToString(result.getOsList()));
        data.setRemarkInfo(listToString(result.getRemarkInfo()));
        data.setLocation(listToString(result.getLocation()));
        data.setDeviceDesc(listToString(result.getDeviceDesc()));
        data.setOrgName(listToString(result.getOrgName()));
        data.setResponsibleName(listToString(result.getResponsibleName()));
        data.setExtendDiskNumber(listToString(result.getExtendDiskNumber()));
        data.setDeviceArch(listToString(result.getDeviceArch()));
    }
    private String listToString(List<String> datas){
        if(null == datas || datas.size() == 0){
            return "";
        }
        if(datas.size() == 1){
            return datas.get(0);
        }
        StringBuffer buffer = new StringBuffer();
        for(String data : datas){
            buffer.append(data).append("|");
        }
        String strDatas = buffer.toString();
        strDatas= strDatas.substring(0,strDatas.lastIndexOf("|"));
        return strDatas;
    }
    private AssetVerify getAssetVerifyByDataCompare(DataCompareVO result, List<AssetBookDetail> lists)  {
        AssetBookDetail detail =  lists.get(0);
        AssetVerify assetVerify = getAssetVerifys(detail); // 除了比对字段，其他字段随机取其中一个
        // 没有差异的：比对数据处理
        compareDataAdd(assetVerify,result,detail);
        return assetVerify;
    }

    /**
     * 没有差异的：比对数据处理，获取其中一个数据即可
     * @param data
     * @param result
     * @param detail
     */
    private void compareDataAdd(AssetVerify data, DataCompareVO result,AssetBookDetail detail)  {
        data.setAssetType(listHandle(result.getTypeGuid()));
        data.setAssetTypeSnoGuid(listHandle(result.getTypeSnoGuid()));
        data.setName(listHandle(result.getName()));
        data.setAssetNum(listHandle(result.getAssetNum()));
        data.setIp(listHandle(result.getIp()));
        data.setMac(listHandle(result.getMac()));
        data.setSerialNumber(listHandle(result.getSerialNumber()));
        data.setEquipmentIntensive(listHandle(result.getEquipmentIntensive()));
        data.setOsSetuptime(toDate(listHandle(result.getOsSetuptime())));
        data.setRegisterTime(toDate(listHandle(result.getRegisterTime())));
        data.setOsList(listHandle(result.getOsList()));
        data.setRemarkInfo(listHandle(result.getRemarkInfo()));
        data.setLocation(listHandle(result.getLocation()));
        data.setDeviceDesc(listHandle(result.getDeviceDesc()));
        data.setOrgName(listHandle(result.getOrgName()));
        data.setOrgCode(detail.getOrgCode());
        data.setResponsibleName(listHandle(result.getResponsibleName()));
        data.setResponsibleCode(detail.getResponsibleCode());
    }

    private String listHandle(List<String> lists) {
        if(null == lists || lists.size() == 0){
            return "";
        }
        return lists.get(0);
    }

    private Date toDate(String s)  {
        if(StringUtils.isEmpty(s)){
            return null;
        }
        try{
            return DateUtil.parseDate(s,DateUtil.DEFAULT_DATE_PATTERN);
        }catch (Exception e){
            logger.error("日期解析异常:"+s,e);
            return null;
        }
    }

    private AssetVerify getAssetVerifys(AssetBookDetail deail) {
        AssetVerify data = mapperUtil.map(deail,AssetVerify.class);
        data.setAssetType(deail.getTypeGuid());
        data.setAssetTypeSnoGuid(deail.getTypeSnoGuid());
        data.setGuid(UUIDUtils.get32UUID());
        data.setCreateTime(new Date());
        data.setSyncStatus(AssetDataSyncConstant.SYNCSTATUSEDIT); //待编辑状态
        return data;
    }

    /**
     * 单条数据，获取正式库数据
     * @param flag
     * @param assets
     * @param deail
     * @return
     */
    private AssetBookDetail getAssetData(String flag,List<AssetBookDetail> assets, AssetBookDetail deail) {
        switch(flag){
            case "ip":  // ip分组
                return getAssetByIp(assets,deail.getIp());
            case "mac":  // mac地址分组
                return getAssetByMac(assets,deail.getMac());
            case "serialNumber": // 设置序列号
                return getAssetBySerialNumber(assets,deail.getSerialNumber());
        }
        return null;
    }


    private AssetBookDetail getAssetBySerialNumber(List<AssetBookDetail> assets, String serialNumber) {
        if(CollectionUtils.isEmpty(assets)){
            return null;
        }
        for(AssetBookDetail asset : assets){
            if(serialNumber.equals(asset.getSerialNumber())){
                return asset;
            }
        }
        return null;
    }

    private AssetBookDetail getAssetByMac(List<AssetBookDetail> assets, String mac) {
        if(CollectionUtils.isEmpty(assets)){
            return null;
        }
        for(AssetBookDetail asset : assets){
            if(mac.equals(asset.getMac())){
                return asset;
            }
        }
        return null;
    }

    private AssetBookDetail getAssetByIp(List<AssetBookDetail> assets, String ip) {
        if(CollectionUtils.isEmpty(assets)){
            return null;
        }
        for(AssetBookDetail asset : assets){
            if(ip.equals(asset.getIp())){
                return asset;
            }
        }
        return null;
    }


    private List<String> getSyscnDataSources(List<TbConf> tbConfs) {
        TbConf tbConf= getTbconfByConfId("sync_asset_data_source_type",tbConfs);
        if(null == tbConf){
            logger.error("策略中没有数据源配置,不执行比对");
            throw new RuntimeException("策略中没有数据源配置,不执行比对");
        }
        String value = tbConf.getValue();
        if(StringUtils.isEmpty(value)){
            logger.error("策略中没有数据源配置,不执行比对");
            throw new RuntimeException("策略中没有数据源配置,不执行比对");
        }
        String[] dataSources = value.split(",");
        List<String> datas= new ArrayList<>();
        for(int i= 0;i < dataSources.length ;i++){
            String data = dataSources[i].trim();
            if(StringUtils.isEmpty(data)){
                continue;
            }
            datas.add(data);
        }
        if(CollectionUtils.isEmpty(datas)){
            logger.error("策略中数据源配置数据为空,不执行比对");
            throw new RuntimeException("策略中数据源配置数据为空,不执行比对");
        }
        return datas;
    }

    private String getUniqueFlag(List<TbConf> tbConfs) {
        TbConf tbConf= getTbconfByConfId("sync_asset_data_key_type",tbConfs);
        if(null == tbConf){
            throw new RuntimeException("策略中没有唯一标识配置,不执行比对");
        }
        String value = tbConf.getValue();
        if(StringUtils.isEmpty(value)){
            throw new RuntimeException("策略中没有唯一标识配置,不执行比对");
        }
        String[] flag={"ip","mac","serialNumber"};
        List<String> flags = Arrays.asList(flag);
        if(!flags.contains(value)){
            logger.error("策略中唯一标识为ip,mac,serialNumber三个其中一个，当前值为："+value);
            throw new RuntimeException("策略中唯一标识不符合要求,不执行比对");
        }
        return value;
    }
    private Map<String,Object> getDiffJson(List<TbConf> tbConfs) {
        TbConf tbConf= getTbconfByConfId("sync_asset_data_diff_json",tbConfs);
        if(null == tbConf){
            throw new RuntimeException("策略中字段策略配置不存在,不执行比对");
        }
        String value = tbConf.getValue();
        if(StringUtils.isEmpty(value)){
            throw new RuntimeException("策略中字段策略配置不存在,不执行比对");
        }
        Map<String,Object> jsons = JSON.parseObject(value,Map.class);
        return jsons;
    }

    private String getGroupItem(String flag,AssetBookDetail item) {
        switch(flag){
            case "ip":  // ip分组
                return item.getIp();
            case "mac":  // mac地址分组
                return item.getMac();
            case "serialNumber": // 设置序列号
                return item.getSerialNumber();
        }
        return null;
    }

    public TbConf getTbconfByConfId(String confId,List<TbConf> tbConfs){
        for(TbConf tbConf: tbConfs){
            if(confId.equals(tbConf.getKey())){
                return tbConf;
            }
        }
        return null;
    }

    @Override
    public Result<List<String>> queryDataSources() {
        return ResultUtil.successList(assetSyncDao.queryDataSources());
    }

    /**
     * 清除非当前批次数据
     * 2023 -4 -20
     * @param curBatchNo
     */
    @Override
    public void deleteByBatchNo(Map<String,String> curBatchNo) {
       for(String key :curBatchNo.keySet()){
           String batchNo = curBatchNo.get(key);
           assetSyncDao.deleteBookDetailBatchNo(batchNo,key);
       }
    }

}
