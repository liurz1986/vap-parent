package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.common.util.JsonUtil;
import com.vrv.vap.admin.mapper.*;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.NetflowBaseDataService;
import com.vrv.vap.admin.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.ehcache.Cache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class NetflowBaseDataServiceImpl implements NetflowBaseDataService {


    @Autowired
    Cache<String, List<Menu>> menuCache;


    Map<String, BaseKoalOrg> ipKoalOrgCache = new HashMap<>();


    Map<String, AssetVo> ipAssetCache  = new HashMap<>() ;


    Map<String, BasePersonZjg> ipPersonCache  = new HashMap<>();


    Map<String, AppSysManager> ipAppCache  = new HashMap<>();


    Map<String, AppSysManager> urlAppCache  = new HashMap<>();


    Map<String, AssetVo> appAssetCache  = new HashMap<>();


    Map<String, BaseSecurityDomain> ipSecCache  = new HashMap<>();

    Map<String, BaseSecurityDomain> idSecCache  = new HashMap<>();


    @Resource
    private AppSysManagerMapper appSysManagerMapper;

    @Resource
    private BaseKoalOrgMapper baseKoalOrgMapper;

    @Resource
    private BasePersonZjgMapper basePersonZjgMapper;

    @Resource
    private AssetMapper assetMapper;

    @Resource
    private BaseSecurityDomainMapper baseSecurityDomainMapper;

    @Resource
    private BaseSecurityDomainIpSegmentMapper baseSecurityDomainIpSegmentMapper;



    private List<BaseKoalOrgVO> baseKoalOrgVOList;

    private List<AssetVo> assetVoList;

    private List<BasePersonZjg> basePersonZjgList;

    private List<AppSysManager> appSysManagerList;

    private List<BaseSecurityDomainRangeVO> baseSecurityDomainRangeVOList;



    private BaseSecurityDomain defaultNullSec = new BaseSecurityDomain();

    private BaseKoalOrg defaultNullObj = new BaseKoalOrg();

    private AppSysManager defaultNullApp = new AppSysManager();

    @Override
    public void initBaseData() {
        assetVoList = assetMapper.findAllAsset();
        basePersonZjgList = basePersonZjgMapper.selectAll();
        appSysManagerList = appSysManagerMapper.selectAll();
        baseKoalOrgVOList = baseKoalOrgMapper.findAll().stream().filter(baseKoalOrgVO -> baseKoalOrgVO.getStartIpNum()!=null && baseKoalOrgVO.getEndIpNum()!=null).collect(Collectors.toList());
        ipKoalOrgCache.clear();
        ipPersonCache.clear();
        ipPersonCache.clear();
        ipAppCache.clear();
        urlAppCache.clear();
        appAssetCache.clear();
        updateipAssetData();
        updateipPersonData();
        updateipAppData();
        updateSecData();
    }



    @Override
    public void updatePersonData() {
        basePersonZjgList = basePersonZjgMapper.selectAll();
        ipPersonCache.clear();
        updateipPersonData();
    }

    @Override
    public void updateAssetData() {
        assetVoList = assetMapper.findAllAsset();
        ipPersonCache.clear();
        updateipAssetData();
        updateipPersonData();
        updateipAppData();
    }

    @Override
    public void updateAppData() {
        appSysManagerList = appSysManagerMapper.selectAll();
        ipAppCache.clear();
        urlAppCache.clear();
        appAssetCache.clear();
        updateipAppData();
    }

    @Override
    public void updateOrgData() {
        baseKoalOrgVOList = baseKoalOrgMapper.findAll().stream().filter(baseKoalOrgVO -> baseKoalOrgVO.getStartIpNum()!=null && baseKoalOrgVO.getEndIpNum()!=null).sorted(Comparator.comparing(BaseKoalOrgVO::getSubCode).reversed()).collect(Collectors.toList());
        ipKoalOrgCache.clear();
    }

    @Override
    public void updateSecData() {
       List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainMapper.selectAll();
       List<BaseSecurityDomainIpSegment> baseSecurityDomainIpSegments = baseSecurityDomainIpSegmentMapper.selectAll();
        baseSecurityDomainRangeVOList = new ArrayList<>();
        baseSecurityDomainIpSegments.stream().filter(p->p.getStartIpNum()!=null&&p.getEndIpNum()!=null).forEach(p->{
           Optional<BaseSecurityDomain> optionalBaseSecurityDomain = baseSecurityDomains.stream().filter(baseSecurityDomain -> p.getCode().equals(baseSecurityDomain.getCode())).findFirst();
           if(optionalBaseSecurityDomain.isPresent()){
               BaseSecurityDomainRangeVO baseSecurityDomainRangeVO = new BaseSecurityDomainRangeVO();
               BeanUtils.copyProperties(optionalBaseSecurityDomain.get(),baseSecurityDomainRangeVO);
               baseSecurityDomainRangeVO.setStartIpNum(p.getStartIpNum());
               baseSecurityDomainRangeVO.setEndIpNum(p.getEndIpNum());
               baseSecurityDomainRangeVOList.add(baseSecurityDomainRangeVO);
           }
        });
        ipSecCache.clear();
        idSecCache.clear();
    }


    private void  updateipAssetData(){
        if(assetVoList == null){
            return;
        }
        log.debug("=========资产缓存信息==");
        assetVoList.stream().filter(assetVo -> StringUtils.isNotEmpty(assetVo.getIp())).forEach(p->{
            if(StringUtils.isEmpty(p.getTerminalType()) && "asset-service".equals(p.getTreeCode())){
                p.setTerminalType("3");
            }
            ipAssetCache.put(p.getIp(),p);

            log.debug("========="+p.getIp()+ JsonUtil.objToJson(p));
        });
        log.debug("=========资产缓存信息结束==");

    }

    private void  updateipPersonData(){
        if(assetVoList == null || basePersonZjgList == null){
            return;
        }
        log.debug("=========人员缓存信息==");
        assetVoList.stream().filter(assetVo -> StringUtils.isNotEmpty(assetVo.getResponsibleCode()) && StringUtils.isNotEmpty(assetVo.getIp())).forEach(p->{
            Optional<BasePersonZjg> basePersonZjgOptional = basePersonZjgList.stream().filter(basePersonZjg -> p.getResponsibleCode().equals(basePersonZjg.getUserNo())).findFirst();
            if(basePersonZjgOptional.isPresent()) {
                ipPersonCache.put(p.getIp(),basePersonZjgOptional.get());
                log.debug("========="+p.getIp()+ JsonUtil.objToJson(basePersonZjgOptional.get()));
            }
        });
        log.debug("=========人员缓存信息结束==");
    }

    private void  updateipAppData(){
        if(assetVoList == null || appSysManagerList == null){
            return;
        }
        log.debug("=========应用缓存信息==");
        assetVoList.stream().filter(assetVo -> StringUtils.isNotEmpty(assetVo.getIp())).forEach(p->{
           Optional<AppSysManager> optionalAppSysManager =  appSysManagerList.stream().filter(appSysManager->StringUtils.isNotEmpty(appSysManager.getServiceId())&&appSysManager.getServiceId().contains(p.getGuid())).findFirst();

           if(optionalAppSysManager.isPresent()){
               ipAppCache.put(p.getIp(),optionalAppSysManager.get());
               appAssetCache.put(optionalAppSysManager.get().getAppNo(),p);
               log.debug("========="+p.getIp()+ JsonUtil.objToJson(optionalAppSysManager.get()));
           }
        });
        log.debug("=========应用缓存信息结束==");

    }

    @Override
    public BasePersonZjg fixPersonIpCache(String ip) {
        if(ipPersonCache.containsKey(ip)){
            return ipPersonCache.get(ip);
        }
        return null;
    }

    @Override
    public AssetVo fixAssetIpCache(String ip) {
        if(ipAssetCache.containsKey(ip)){
            return ipAssetCache.get(ip);
        }
        return null;
    }

    @Override
    public AppSysManager fixAppIpCache(String ip) {
        if(ipAppCache.containsKey(ip)){
            return ipAppCache.get(ip);
        }
        return null;
    }

    @Override
    public AppSysManager fixAppUrlCache(String url) {
        if(!urlAppCache.containsKey(url)){
            Optional<AppSysManager> optionalAppSysManager = appSysManagerList.stream().filter(appSysManager ->StringUtils.isNotEmpty( appSysManager.getDomainName()) && url.contains(appSysManager.getDomainName())).findFirst();
            urlAppCache.put(url, optionalAppSysManager.isPresent()? optionalAppSysManager.get():defaultNullApp);
        }
        AppSysManager appSysManager = urlAppCache.get(url);
        if(appSysManager!=null && appSysManager.getAppNo() != null){
            return appSysManager;
        }
        return null;

    }

    @Override
    public AssetVo fixAppAssetCache(String appNo) {
        if(appAssetCache.containsKey(appNo)){
            return appAssetCache.get(appNo);
        }
        return null;
    }

    @Override
    public BaseKoalOrg fixOrgIpCache(String ip) {
        if(!ipKoalOrgCache.containsKey(ip)){
            Long num = IPUtils.ip2int(ip);
            if(num==0){
                ipKoalOrgCache.put(ip,defaultNullObj);

            }else {

                Optional<BaseKoalOrgVO> optionalBaseKoalOrgVO = baseKoalOrgVOList.stream().filter( baseKoalOrgVO-> baseKoalOrgVO.getStartIpNum() <= num && baseKoalOrgVO.getEndIpNum() >= num).findFirst();
                ipKoalOrgCache.put(ip, optionalBaseKoalOrgVO.isPresent() ? optionalBaseKoalOrgVO.get() : defaultNullObj);
            }
        }

        BaseKoalOrg baseKoalOrg = ipKoalOrgCache.get(ip);
        if(baseKoalOrg!=null && baseKoalOrg.getUuId() != null){
            return baseKoalOrg;
        }
        return null;
    }

    @Override
    public BaseSecurityDomain fixSecIpCache(String ip) {
        if(!ipSecCache.containsKey(ip)){
            Long num = IPUtils.ip2int(ip);
            if(num==0){
                ipSecCache.put(ip,defaultNullSec);

            }else {

                Optional<BaseSecurityDomainRangeVO> optionalBaseSecurityDomainRangeVO = baseSecurityDomainRangeVOList.stream().filter( baseSecurityDomainRangeVO-> baseSecurityDomainRangeVO.getStartIpNum() <= num && baseSecurityDomainRangeVO.getEndIpNum() >= num).findFirst();
                ipSecCache.put(ip, optionalBaseSecurityDomainRangeVO.isPresent() ? optionalBaseSecurityDomainRangeVO.get() : defaultNullSec);
            }
        }

        BaseSecurityDomain baseSecurityDomain = ipSecCache.get(ip);
        if(baseSecurityDomain!=null && baseSecurityDomain.getCode() != null){
            return baseSecurityDomain;
        }
        return null;
    }

    @Override
    public BaseSecurityDomain fixSecCodeCache(String id) {
        if(StringUtils.isEmpty(id)){
            return null;
        }
        if(idSecCache.containsKey(id)){
            return idSecCache.get(id);
        }

        Optional<BaseSecurityDomainRangeVO> optionalBaseSecurityDomainRangeVO = baseSecurityDomainRangeVOList.stream().filter( baseSecurityDomainRangeVO-> baseSecurityDomainRangeVO.getCode()!=null && baseSecurityDomainRangeVO.getCode().equals(id)).findFirst();
       if(optionalBaseSecurityDomainRangeVO.isPresent()){
           ipSecCache.put(id, optionalBaseSecurityDomainRangeVO.get() );
           return  optionalBaseSecurityDomainRangeVO.get();

       }

        return null;
    }


}
