package com.vrv.vap.alarmdeal.business.asset.service.impl.query;

import com.vrv.vap.alarmdeal.business.asset.dao.query.AssetQueryDao;
import com.vrv.vap.alarmdeal.business.asset.enums.AssetTrypeGroupEnum;
import com.vrv.vap.alarmdeal.business.asset.enums.AssetWorthEnum;
import com.vrv.vap.alarmdeal.business.asset.service.query.AssetQueryService;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetTotalStatisticsVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetTypeTotalVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.SafeDeviceListVO;
import com.vrv.vap.jpa.web.NameValue;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 报表接口：资产报表查询接口
 */
@Service
public class AssetQueryServiceImpl implements AssetQueryService {
    private static Logger logger = LoggerFactory.getLogger(AssetQueryServiceImpl.class);
    @Autowired
    private AssetQueryDao assetDao;
    @Value("${classifiedLevel.parentType.asset:f5a4ae5b-3cee-a84f-7471-8f23ezjg0400}")
    private String assetParentType;   //获取涉及等级配置的parenttype的值


    /**
     * 资产总数及分类统计
     * @return AssetTotalStatisticsVO
     */
    @Override
    public AssetTotalStatisticsVO queryAssetTotalStatistics() {
        AssetTotalStatisticsVO assetTotalStatisticsVO = assetDao.queryAssetTotalStatistics();

        List<AssetStatisticsVO> levels = assetDao.queryAssetByLevel(assetParentType);
        if(null == levels || levels.size() == 0){
            assetTotalStatisticsVO.setLevel1(0);
            assetTotalStatisticsVO.setLevel2(0);
            assetTotalStatisticsVO.setLevel3(0);
            assetTotalStatisticsVO.setLevel4(0);
            assetTotalStatisticsVO.setLevel5(0);
            return assetTotalStatisticsVO;
        }
        for(AssetStatisticsVO vo : levels){
            String name= vo.getName();
            switch (name){
                case "非密" :
                    assetTotalStatisticsVO.setLevel1(vo.getCount());
                    break;
                case "内部" :
                    assetTotalStatisticsVO.setLevel2(vo.getCount());
                    break;
                case "秘密" :
                    assetTotalStatisticsVO.setLevel3(vo.getCount());
                    break;
                case "机密" :
                    assetTotalStatisticsVO.setLevel4(vo.getCount());
                    break;
                case "绝密" :
                    assetTotalStatisticsVO.setLevel5(vo.getCount());
                    break;
            }
        }
        return assetTotalStatisticsVO;
    }
    /**
     * 资产数量按类型统计(二级资产类型)
     * @return list
     */
    @Override
    public List<AssetStatisticsVO> queryAssetByAssetType() {
        return assetDao.queryAssetByAssetType();
    }
    /**
     * 资产数量按部门
     *
     * @return list
     */
    @Override
    public List<AssetStatisticsVO> queryAssetByDepartment() {
        return assetDao.queryAssetByDepartment();
    }
    /**
     * 资产数量按密级统计
     *
     * @param type 一级资产类型，null表示所有资产
     * @return AssetLevelStatisticsVO
     */
    @Override
    public List<AssetStatisticsVO> queryAssetByLevel(String type) {
        List<AssetStatisticsVO> datas = null;
        if(StringUtils.isNotEmpty(type)){
            return assetDao.queryAssetTypeGroupByLevel(type,assetParentType);   // 饼图
        }else{
            datas = assetDao.queryAssetByLevel(assetParentType);   // 柱形图
            List<AssetStatisticsVO> initDatas = AssetStatisticsVO.getInitLevels();
            if(null == datas || datas.size() == 0){
                return initDatas;
            }
            for(AssetStatisticsVO vo : datas){
                String levelName = vo.getName();
                if(StringUtils.isEmpty(levelName)){
                    continue;
                }
                int count = vo.getCount();
                levelCountHandle(levelName,count,initDatas);
            }
            return initDatas;
        }
    }

    private void levelCountHandle(String levelName, int count ,List<AssetStatisticsVO> initDatas){
        for(AssetStatisticsVO data : initDatas){
            if(levelName.equalsIgnoreCase(data.getName())){
                data.setCount(count);
                return;
            }
        }
        return;
    }
    /**
     * 资产分类汇总统计：按资产类型统计 终端总数$，服务器总数$，网络设备总数$，安全设备总数$，其他设备数$。
     * @return
     */
    @Override
    public AssetTypeTotalVO queryAssetTypeTotal() {

        return assetDao.queryAssetTypeTotal();
    }

    /**
     * 一级资产类型下 ，按照二级资产类型、国产非国产进行分类统计
     *
     * @return Result
     */
    @Override
    public List<AssetStatisticsVO> queryAssetTypeTotalByTermType(String assetTypeGroupTreeCode) {
        String treeCode = "";
        switch(assetTypeGroupTreeCode){
            case "assetHost":
                treeCode = AssetTrypeGroupEnum.ASSETHOSt.getTreeCode();
                break;
            case "assetService" :
                treeCode = AssetTrypeGroupEnum.ASSETSERVICE.getTreeCode();
                break;
            case "assetNetworkDevice" :
                treeCode = AssetTrypeGroupEnum.ASSETNET.getTreeCode();
                break;
            case "assetSafeDevice" :
                treeCode = AssetTrypeGroupEnum.ASSETSAFE.getTreeCode();
                break;
            default:
                break;
        }
        if(StringUtils.isEmpty(treeCode)){
            return new ArrayList<>();
        }
        return assetDao.queryAssetTypeTotalByTermType(treeCode);
    }
    /**
     * 其他设备数量按类型统计
     * 其他设备  ：刻录机、打印机、涉密专用介质
     * @return
     */
    @Override
    public List<AssetStatisticsVO> queryOtherAssetNumber() {
        return assetDao.queryOtherAssetNumber();
    }
    /**
     * 安全设备信息列表
     *
     * @return
     */
    @Override
    public List<SafeDeviceListVO> querySafeDeviceAssetList() {
        return assetDao.querySafeDeviceAssetList();
    }

    @Override
    public List<AssetStatisticsVO> queryAssetByWorth() {
        List<AssetStatisticsVO> assetStatisticsVOS=new ArrayList<>();
        for (AssetWorthEnum assetWorthEnum:AssetWorthEnum.values()) {
            AssetStatisticsVO assetStatisticsVO=new AssetStatisticsVO();
            assetStatisticsVO.setName(assetWorthEnum.getName());
            //TODO worth值0和1 暂时都统计在很低
            if (assetWorthEnum.getCode()==1){
               Integer count= assetDao.countByWorth(assetWorthEnum.getCode());
                Integer count1= assetDao.countByWorth(0);
                assetStatisticsVO.setCount(count+count1);
            }else {
                Integer count= assetDao.countByWorth(assetWorthEnum.getCode());
                assetStatisticsVO.setCount(count);
            }
            assetStatisticsVOS.add(assetStatisticsVO);
        }
        return assetStatisticsVOS;
    }

    /**
     * 资产数量按安全域统计
     *  2023-07-04
     * @return
     */
    @Override
    public List<AssetStatisticsVO> queryAssetByDomain() {
        return assetDao.queryAssetByDomain();
    }
    /**
     * 其他设备类型统计（除终端、服务器、网络设备、安全设备一级资产类型外的设备）
     *  2023-07-04
     * @return
     */
    @Override
    public List<AssetStatisticsVO> queryAssetByOther() {
        return assetDao.queryAssetByOther();
    }

    @Override
    public List<NameValue> queryAssetByArea() {
        return assetDao.queryAssetByArea();
    }

    @Override
    public List<AssetStatisticsVO> queryAssetByLevelType(String type) {
        String assetType = getAssetType(type);
        List<AssetStatisticsVO> assetStatisticsVOS = assetDao.queryAssetTypeGroupByLevel(assetType, assetParentType);
        List<String> strings = assetStatisticsVOS.stream().map(a -> a.getName()).collect(Collectors.toList());
        if (strings.size()<5){
            List<String> list = Arrays.asList("内部", "机密", "秘密", "绝密", "非密");
            for (String s:list){
                if (!strings.contains(s)){
                    AssetStatisticsVO assetStatisticsVO = new AssetStatisticsVO();
                    assetStatisticsVO.setCount(0);
                    assetStatisticsVO.setName(s);
                    assetStatisticsVOS.add(assetStatisticsVO);
                }
            }
        }
        return assetStatisticsVOS;   // 饼图
    }

    @Override
    public List<AssetStatisticsVO> queryAssetByDepartmentType(String type) {
        String assetType = getAssetType(type);
        return assetDao.queryAssetByDepartmentType(assetType);
    }

    @Override
    public List<AssetStatisticsVO> queryAssetNumByAssetType(String type) {
        String assetType = getAssetType(type);
        return assetDao.queryAssetNumByAssetType(assetType);
    }

    public String getAssetType(String type){
        String treeCode = "";
        switch(type){
            case "assetHost":
                treeCode = AssetTrypeGroupEnum.ASSETHOSt.getTreeCode();
                break;
            case "assetService" :
                treeCode = AssetTrypeGroupEnum.ASSETSERVICE.getTreeCode();
                break;
            case "assetNetworkDevice" :
                treeCode = AssetTrypeGroupEnum.ASSETNET.getTreeCode();
                break;
            case "assetSafeDevice" :
                treeCode = AssetTrypeGroupEnum.ASSETSAFE.getTreeCode();
                break;
            case "assetMaintenHost" :
                treeCode = AssetTrypeGroupEnum.ASSETMAINTEN.getTreeCode();
                break;
            default:
                break;
        }
        return treeCode;
    }
}
