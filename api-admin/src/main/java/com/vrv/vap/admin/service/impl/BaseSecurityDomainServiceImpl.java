package com.vrv.vap.admin.service.impl;


import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.mapper.BaseSecurityDomainIpSegmentMapper;
import com.vrv.vap.admin.mapper.BaseSecurityDomainMapper;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.BaseSecurityDomainService;
import com.vrv.vap.admin.service.SelfConcernAssetService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.service.feign.AlarmDealFeign;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.util.Result;
import com.vrv.vap.admin.vo.BaseSecurityDomainInfo;
import com.vrv.vap.admin.vo.BaseSecurityDomainRangeVO;
import com.vrv.vap.admin.vo.BaseSecurityDomainVO;
import com.vrv.vap.admin.vo.DomainQuery;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Transactional
public class BaseSecurityDomainServiceImpl extends BaseServiceImpl<BaseSecurityDomain> implements BaseSecurityDomainService {

    private static final Logger log = LoggerFactory.getLogger(BaseSecurityDomainServiceImpl.class);

    @Resource
    BaseSecurityDomainMapper baseSecurityDomainMapper;

    @Resource
    BaseSecurityDomainIpSegmentMapper baseSecurityDomainIpSegmentMapper;

    @Autowired
    private KafkaSenderService kafkaSenderService;
    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    StringRedisTemplate redisTemplate;
    @Autowired
    AlarmDealFeign alarmDealFeign;
    @Autowired
    private SelfConcernAssetService selfConcernAssetService;
    private static final String CACHE_SEC_KEY = "_BASEINFO:BASE_SECURITY_DOMAIN:ALL";

    private static final String CACHE_SEC_RANGE_VO_KEY = "_BASEINFO:BASE_SECURITY_DOMAIN_RANGE_VO:ALL";

    private static final String CACHE_SEC_ID_KEY = "_BASEINFO:BASE_SECURITY_DOMAIN:ID";
    private static final String CONNECT = "connect_code";
    private static final String EXTERNAL = "external_code";
    private static final String IPEVEVT = "ip_events";
    @Override
    public List<BaseSecurityDomainVO> findSubDomainByCode(String code) {
        Example exampleParent = new Example(BaseSecurityDomain.class);
        if(StringUtils.isEmpty(code)){
            Example.Criteria criteria = exampleParent.createCriteria();
            criteria.orIsNull("parentCode");
            criteria.orEqualTo("parentCode","");
        }else{
            Example.Criteria criteria = exampleParent.createCriteria();
            criteria.orEqualTo("parentCode",code);
        }
        List<BaseSecurityDomain> firstDomains = this.findByExample(exampleParent);

        Example example = new Example(BaseSecurityDomain.class);
        List<BaseSecurityDomain> securityDomainList = this.findByExample(example);

        List<BaseSecurityDomainVO> securityDomainVoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(firstDomains)) {
            for (BaseSecurityDomain baseSecurityDomain : firstDomains) {
                BaseSecurityDomainVO baseSecurityDomainVO = new BaseSecurityDomainVO();
                BeanUtils.copyProperties(baseSecurityDomain, baseSecurityDomainVO);
                String orgCode = baseSecurityDomain.getCode();
                long matchCount = securityDomainList.stream().filter(p -> orgCode.equals(p.getParentCode())).count();
                if (matchCount > 0) {
                    baseSecurityDomainVO.setHasChildren(true);
                } else {
                    baseSecurityDomainVO.setHasChildren(false);
                }
                securityDomainVoList.add(baseSecurityDomainVO);
            }
        }

        return securityDomainVoList;
    }

    private List<String> getConExtCodes() {
        List<String> strings=new ArrayList<>();
        SystemConfig connectCode = systemConfigService.findByConfId(CONNECT);
        if (connectCode!=null){
            strings.add(connectCode.getConfValue());
        }
        SystemConfig externalCode = systemConfigService.findByConfId(EXTERNAL);
        if (externalCode!=null){
            strings.add(externalCode.getConfValue());
        }
        return strings;
    }

    @Override
    public List<BaseSecurityDomain> findSubAllDomainByCode(String code ) {
        List<BaseSecurityDomain> baseSecurityDomains = new ArrayList<>();
        getSubDomain(baseSecurityDomains,code);
        return baseSecurityDomains;
    }

    @Override
    public Integer deleteDomainByCode(String code) {
        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().andEqualTo("code", code);
        List<BaseSecurityDomain> baseDomains = this.findByExample(example);
        if (baseDomains == null || baseDomains.size() == 0) {
            return 0;
        }
        List<BaseSecurityDomain> list = new ArrayList<>();
        list.addAll(baseDomains);

        for (BaseSecurityDomain org : list) {

            baseSecurityDomainMapper.delete(org);
        }
        return list.size();
    }

    @Override
    public List<BaseSecurityDomain> domainByTenant(Integer roleId) {
        return baseSecurityDomainMapper.domainByTenant( roleId);
    }

    @Override
    public List<User> findTenantByCode(Integer roleId, String code) {
        return baseSecurityDomainMapper.findTenantByCode(roleId ,code);
    }


    @Override
    public BaseSecurityDomain generateSubCode(BaseSecurityDomain baseSecurityDomain) {
        BaseSecurityDomain baseSecurityDomainquery= new BaseSecurityDomain();
        if(baseSecurityDomain.getParentCode()== null){
            // 为最顶层节点
            baseSecurityDomain.setSubCode("001");
            return baseSecurityDomain;
        }
        baseSecurityDomainquery.setCode(baseSecurityDomain.getParentCode());
        List<BaseSecurityDomain>  list =   baseSecurityDomainMapper.select(baseSecurityDomainquery);
        if(CollectionUtils.isNotEmpty(list)){
            String subCode = list.get(0).getSubCode();
            if(StringUtils.isNotEmpty(subCode)){
                // 确定下一子机构的最大编码
                BaseSecurityDomain baseSecurityDomain2= new BaseSecurityDomain();
                baseSecurityDomain2.setParentCode(baseSecurityDomain.getParentCode());
                List<BaseSecurityDomain>  list2 = baseSecurityDomainMapper.select(baseSecurityDomain2);
                String newSubCode =  subCode + getMaxNum(list2);
                baseSecurityDomain.setSubCode(newSubCode);
            }
            return baseSecurityDomain;
        }
        return  baseSecurityDomain;
    }



    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void initSubCode() {
        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().orEqualTo("subCode","").orIsNull("subCode");
        int count = baseSecurityDomainMapper.selectCountByExample(example);
        if(count > 100){
            //如果超过100个节点需要更新，那么直接查询所有在内存计算code
            List<BaseSecurityDomain> baseSecurityDomainList =  this.findAll();
            List<BaseSecurityDomain> updateOrgList =  getUpdateOrgs(baseSecurityDomainList);
            updateOrgList.forEach(p->{
                        this.update(p);
                    }
            );
            return;
        }
        List<BaseSecurityDomain> list = baseSecurityDomainMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            for(BaseSecurityDomain baseSecurityDomain : list){
                updateBaseKoalOrg(baseSecurityDomain);
            }
        }
    }


    private  List<BaseSecurityDomain> getUpdateOrgs(List<BaseSecurityDomain> list){
        Optional<BaseSecurityDomain> rootOrgs = list.stream().filter(p->p.getParentCode()==null).findFirst();
        List<BaseSecurityDomain> baseSecurityDomainList = new ArrayList<>();
        if(rootOrgs.isPresent()){
            BaseSecurityDomain rootOrg = rootOrgs.get();
            if(StringUtils.isEmpty(rootOrg.getSubCode())){
                rootOrg.setSubCode("001");
                baseSecurityDomainList.add(rootOrg);
            }
            List<BaseSecurityDomain> childList = list.stream().filter(p->rootOrg.getCode().equals(p.getParentCode())).collect(Collectors.toList());
            getUpdateOrgByTree(rootOrg,childList,list,baseSecurityDomainList);
        }
        return  baseSecurityDomainList;
    }




    private  void getUpdateOrgByTree(BaseSecurityDomain parent,List<BaseSecurityDomain> childList,List<BaseSecurityDomain> allList,List<BaseSecurityDomain> updateList){
        if(childList.size() == 0)
        { return ;}
        Optional<BaseSecurityDomain> maxOrgPresent = childList.stream().filter(p->StringUtils.isNotEmpty(p.getSubCode())).max((s1,s2)->s1.getSubCode().compareTo(s2.getSubCode()));
        String parentSubCode = parent.getSubCode();
        Integer maxNum = 0;
        if(maxOrgPresent.isPresent()){
            BaseSecurityDomain maxOrg = maxOrgPresent.get();
            String maxStr = maxOrg.getSubCode().substring(maxOrg.getSubCode().length()-3,maxOrg.getSubCode().length());
            maxNum = Integer.parseInt(maxStr);
        }
        List<BaseSecurityDomain> needUpdateChildList = childList.stream().filter(p->StringUtils.isEmpty(p.getSubCode())).collect(Collectors.toList());
        for(BaseSecurityDomain p:needUpdateChildList) {
            maxNum++;
            p.setSubCode(parentSubCode + String.format("%03d", maxNum));
            updateList.add(p);

        }
        childList.forEach(p->{
            List<BaseSecurityDomain> subList = allList.stream().filter(q->p.getCode().equals(q.getParentCode())).collect(Collectors.toList());
            getUpdateOrgByTree(p,subList,allList,updateList);
        });
    }



    private  void updateBaseKoalOrg(BaseSecurityDomain baseSecurityDomain){
        List<BaseSecurityDomain> list = getBaseKoalOrgList(baseSecurityDomain);
        if(CollectionUtils.isNotEmpty(list)){
            for(int i= 0;i<list.size();i++){
                BaseSecurityDomain baseSecurityDomain1 = generateSubCode(list.get(list.size()-i-1));
                baseSecurityDomainMapper.updateByPrimaryKeySelective(baseSecurityDomain1);
            }
        }
    }


    // 获取该节点，及其以上需要维护subCode的节点，构成节点集合
    private  List<BaseSecurityDomain>  getBaseKoalOrgList(BaseSecurityDomain baseSecurityDomain){
        baseSecurityDomain = baseSecurityDomainMapper.selectByPrimaryKey(baseSecurityDomain.getId());
        List<BaseSecurityDomain> list = new ArrayList<>();
        if(StringUtils.isNotEmpty(baseSecurityDomain.getSubCode())){
            return list;
        }
        if(baseSecurityDomain.getParentCode() == null){
            list.add(baseSecurityDomain);
            return  list;
        }
        boolean flag = true;
        list.add(baseSecurityDomain);
        while (flag){
            BaseSecurityDomain  parentBaseSecurityDomain = getParentBaseKoalOrg(baseSecurityDomain);
            if(parentBaseSecurityDomain == null){
                return new ArrayList<>();
            }
            if(StringUtils.isEmpty(parentBaseSecurityDomain.getSubCode())){
                list.add(parentBaseSecurityDomain);
                baseSecurityDomain = parentBaseSecurityDomain;
            }
            else {
                flag = false;
            }
            if(baseSecurityDomain.getParentCode()==null){
                flag = false;
            }
        }
        return  list;
    }



    private BaseSecurityDomain getParentBaseKoalOrg(BaseSecurityDomain baseSecurityDomain){
        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().andEqualTo("code",baseSecurityDomain.getParentCode());
        List<BaseSecurityDomain> list = baseSecurityDomainMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }


    private  String getMaxNum ( List<BaseSecurityDomain> list2){
        if(CollectionUtils.isEmpty(list2)){
            return  "001";
        }
        else {
            List<BaseSecurityDomain>  hasSubCodeList = list2.stream().filter(p->StringUtils.isNotEmpty(p.getSubCode())).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(hasSubCodeList)){
                return  "001";
            }
            else {
                Integer maxNum = hasSubCodeList .stream().
                        map(p->Integer.valueOf(p.getSubCode().substring(p.getSubCode().length()-3,p.getSubCode().length()))).
                        max(Integer::compareTo).get();
                Integer newNum = maxNum + 1;
                if(newNum >= 100){
                    return  ""+ newNum;
                }
                else if (newNum >= 10){
                    return  "0" + newNum;
                }
                else {
                    return  "00" + newNum;
                }
            }
        }
    }

    /**
     * 删除所有安全域
     */
    public void deleteAllDomain() {
        List<BaseSecurityDomain> baseSecurityDomainList = baseSecurityDomainMapper.selectAll();
        if (CollectionUtils.isNotEmpty(baseSecurityDomainList)) {
            for (BaseSecurityDomain baseSecurityDomain : baseSecurityDomainList) {
                baseSecurityDomainMapper.deleteByPrimaryKey(baseSecurityDomain.getId());
            }
        }
    }


    private void  getSubDomain(List<BaseSecurityDomain> baseKoalOrgs, String code){

        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().andEqualTo("parentCode",code);
        List<BaseSecurityDomain> subOrgs = this.findByExample(example);
        if(subOrgs == null|| subOrgs.size() == 0) {
            return;
        }
        baseKoalOrgs.addAll(subOrgs);
        for(BaseSecurityDomain org :subOrgs){
            getSubDomain(baseKoalOrgs,org.getCode());
        }
    }

    @Override
    public List<BaseSecurityDomainVO> fillChildren(List<BaseSecurityDomain> securityDomainList) {
        List<BaseSecurityDomainVO> securityDomainVoList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(securityDomainList)) {
            for (BaseSecurityDomain baseSecurityDomain : securityDomainList) {
                BaseSecurityDomainVO baseSecurityDomainVO = new BaseSecurityDomainVO();
                BeanUtils.copyProperties(baseSecurityDomain, baseSecurityDomainVO);
                String orgCode = baseSecurityDomain.getCode();
                long matchCount = securityDomainList.stream().filter(p -> orgCode.equals(p.getParentCode())).count();
                if (matchCount > 0) {
                    baseSecurityDomainVO.setHasChildren(true);
                } else {
                    baseSecurityDomainVO.setHasChildren(false);
                }
                securityDomainVoList.add(baseSecurityDomainVO);
            }
        }
        return securityDomainVoList;
    }

    @Override
    public void cacheDomain() {
        Map<String,Object> SecIdMap = new HashMap<>();
        List<BaseSecurityDomainRangeVO> baseSecurityDomainRangeVOList = new ArrayList<>();
        List<BaseSecurityDomain> baseSecurityDomains = this.findAll();
        if (CollectionUtils.isNotEmpty(baseSecurityDomains)) {
            for (BaseSecurityDomain domain : baseSecurityDomains) {
                Integer id = domain.getId();
                SecIdMap.put(id + "",JSON.toJSONString(domain));
            }
        }
        redisTemplate.delete(CACHE_SEC_ID_KEY);
        redisTemplate.opsForHash().putAll(CACHE_SEC_ID_KEY,SecIdMap);
        List<BaseSecurityDomainVO> domainVOList = this.fillChildren(baseSecurityDomains);
        redisTemplate.opsForValue().set(CACHE_SEC_KEY, JSON.toJSONString(domainVOList));
        List<BaseSecurityDomainIpSegment> baseSecurityDomainIpSegments = baseSecurityDomainIpSegmentMapper.selectAll();
        baseSecurityDomainIpSegments.stream().filter(p -> p.getStartIpNum() != null && p.getEndIpNum() != null).forEach(p -> {
            Optional<BaseSecurityDomain> optionalBaseSecurityDomain = baseSecurityDomains.stream().filter(baseSecurityDomain -> p.getCode().equals(baseSecurityDomain.getCode())).findFirst();
            if (optionalBaseSecurityDomain.isPresent()) {
                BaseSecurityDomainRangeVO baseSecurityDomainRangeVO = new BaseSecurityDomainRangeVO();
                BeanUtils.copyProperties(optionalBaseSecurityDomain.get(), baseSecurityDomainRangeVO);
                baseSecurityDomainRangeVO.setStartIpNum(p.getStartIpNum());
                baseSecurityDomainRangeVO.setEndIpNum(p.getEndIpNum());
                baseSecurityDomainRangeVOList.add(baseSecurityDomainRangeVO);
            }
        });
        redisTemplate.opsForValue().set(CACHE_SEC_RANGE_VO_KEY, JSON.toJSONString(baseSecurityDomainRangeVOList));
    }

    @Override
    public void sendChangeMessage() {
        Map<String,Object> result = new HashMap<>();
        result.put("item","domain");
        result.put("time", System.currentTimeMillis());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String content = objectMapper.writeValueAsString(result);
            kafkaSenderService.send("vap_base_data_change_message",null,content);
        } catch (Exception e) {
            log.error("",e);
        }
    }

    @Override
    public BaseSecurityDomain getNetExtCode(Map<String, String> map) {
        String type = map.get("type");
        String code="";
        try {
            if (type.equals("1")){
                SystemConfig byConfId = systemConfigService.findByConfId(CONNECT);
                code= byConfId.getConfValue();
            }else {
                SystemConfig byConfId = systemConfigService.findByConfId(EXTERNAL);
                code= byConfId.getConfValue();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (StringUtils.isNotBlank(code)){
            Example example=new Example(BaseSecurityDomain.class);
            example.createCriteria().andEqualTo("code",code);
            List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainMapper.selectByExample(example);
            if (baseSecurityDomains.size()>0){
                return baseSecurityDomains.get(0);
            }

        }
        return null;
    }

    @Override
    public List<BaseSecurityDomainInfo> getDomainPage(DomainQuery domainQuery) {
        List<BaseSecurityDomainInfo> baseSecurityDomains= baseSecurityDomainMapper.getDomainPage(domainQuery);
        //补充ip段
        supplementIPs(baseSecurityDomains);
        //补充异常事件数
        try {
            supplementEventCount(baseSecurityDomains);
        } catch (Exception e) {
          log.info(""+e);
        }
        //是否关注
        isJustAssetOfConcern(baseSecurityDomains,domainQuery.getUserId());
        return baseSecurityDomains;
    }
    @Override
    public Integer getDomainPageCount(DomainQuery domainQuery) {
        return baseSecurityDomainMapper.getDomainPageCount(domainQuery);
    }
    private void isJustAssetOfConcern(List<BaseSecurityDomainInfo> baseSecurityDomains,Integer id) {
        if (baseSecurityDomains.size()>0){
            for (BaseSecurityDomainInfo baseSecurityDomain:baseSecurityDomains){
                Example example=new Example(SelfConcernAsset.class);
                example.createCriteria().andEqualTo("type",2)
                        .andEqualTo("ip",baseSecurityDomain.getId()).andEqualTo("userId",id);
                Integer count = selfConcernAssetService.count(example);
                if (count>0){
                    baseSecurityDomain.setIsJustAssetOfConcern(true);
                }
            }}
    }

    private void supplementIPs(List<BaseSecurityDomainInfo> baseSecurityDomains) {
        if (baseSecurityDomains.size()>0){
            for (BaseSecurityDomainInfo baseSecurityDomain:baseSecurityDomains){
                Example example=new Example(BaseSecurityDomainIpSegment.class);
                example.createCriteria().andEqualTo("code",baseSecurityDomain.getCode());
                List<BaseSecurityDomainIpSegment> baseSecurityDomainIpSegments = baseSecurityDomainIpSegmentMapper.selectByExample(example);
                if (baseSecurityDomainIpSegments.size()>0){
                 List<String> strings=new ArrayList<>();
                 for (BaseSecurityDomainIpSegment baseSecurityDomainIpSegment:baseSecurityDomainIpSegments){
                     String ips=baseSecurityDomainIpSegment.getStartIp()+"-"+baseSecurityDomainIpSegment.getEndIp();
                     strings.add(ips);
                 }
                    baseSecurityDomain.setRangIp(StringUtils.join(strings,","));
                }
            }
        }
    }
    @Override
    public List<Map<String,Object>> getDomainCountTop10() {
        List<Map<String,Object>> maps=new ArrayList<>();
        Example example=new Example(BaseSecurityDomain.class);
        example.createCriteria().andIsNotNull("parentCode");
        List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainMapper.selectByExample(example);
        List<BaseSecurityDomainInfo> baseSecurityDomainInfos=new ArrayList<>();
        for (BaseSecurityDomain baseSecurityDomain:baseSecurityDomains){
            BaseSecurityDomainInfo baseSecurityDomainInfo=new BaseSecurityDomainInfo();
            BeanUtils.copyProperties(baseSecurityDomain,baseSecurityDomainInfo);
            baseSecurityDomainInfos.add(baseSecurityDomainInfo);
        }
        supplementEventCount(baseSecurityDomainInfos);
        List<BaseSecurityDomainInfo> collect = baseSecurityDomainInfos.stream().sorted(Comparator.comparing(BaseSecurityDomainInfo::getCountEvent).reversed()).collect(Collectors.toList());
        for (int i = 0; i <10 ; i++) {
            if (collect.size()>=i+1){
                Map<String,Object> map=new HashMap<>();
                map.put("name",collect.get(i).getDomainName());
                map.put("value",collect.get(i).getCountEvent());
                maps.add(map);
            }
        }
        return maps;
    }

    @Override
    public List<Map<String, Object>> getAllDomainIps() {
        List<Map<String,Object>> maps=new ArrayList<>();
        Example example=new Example(BaseSecurityDomain.class);
        example.createCriteria().andIsNotNull("parentCode");
        List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainMapper.selectByExample(example);
        List<BaseSecurityDomainInfo> baseSecurityDomainInfos=new ArrayList<>();
        for (BaseSecurityDomain baseSecurityDomain:baseSecurityDomains){
            BaseSecurityDomainInfo baseSecurityDomainInfo=new BaseSecurityDomainInfo();
            BeanUtils.copyProperties(baseSecurityDomain,baseSecurityDomainInfo);
            baseSecurityDomainInfos.add(baseSecurityDomainInfo);
        }
        supplementIPs(baseSecurityDomainInfos);
        for (BaseSecurityDomainInfo baseSecurityDomain:baseSecurityDomainInfos){
            Map<String,Object> map=new HashMap<>();
            map.put("name",baseSecurityDomain.getDomainName());
            map.put("rangeIps",baseSecurityDomain.getRangIp());
            maps.add(map);
        }
        return maps;
    }


    private void supplementEventCount(List<BaseSecurityDomainInfo> baseSecurityDomains) {
        Map<String, Long> data =new HashMap<>();
        String s = redisTemplate.opsForValue().get(IPEVEVT);
        if (StringUtils.isNotBlank(s)){
            Map<String, Integer> datas = JSON.parseObject(s, Map.class);
            for (Map.Entry<String, Integer> m:datas.entrySet()){
                data.put(m.getKey(),m.getValue().longValue());
            }
        }else {
            Result<Map<String, Long>> ipGroup = alarmDealFeign.getIpGroup();
            data = ipGroup.getData();
            redisTemplate.opsForValue().set(IPEVEVT, JSON.toJSONString(data),10, TimeUnit.SECONDS);
        }
        if (data.size()>0){
            //转换ip为num
            Map<Long, Integer> datas=updateIpGroup(data);
            for (BaseSecurityDomainInfo baseSecurityDomainInfo:baseSecurityDomains){
                Example example=new Example(BaseSecurityDomainIpSegment.class);
                example.createCriteria().andEqualTo("code",baseSecurityDomainInfo.getCode());
                List<BaseSecurityDomainIpSegment> baseSecurityDomainIpSegments = baseSecurityDomainIpSegmentMapper.selectByExample(example);
                if (baseSecurityDomainIpSegments.size()>0){
                    Integer sum= 0;
                    for (Map.Entry<Long, Integer> m:datas.entrySet()){
                        for (BaseSecurityDomainIpSegment baseSecurityDomainIpSegment:baseSecurityDomainIpSegments){
                            if (baseSecurityDomainIpSegment.getStartIpNum()<=m.getKey()
                                    &&baseSecurityDomainIpSegment.getEndIpNum()>=m.getKey()){
                                sum+=m.getValue();
                                break;
                            }
                        }
                    }
                    baseSecurityDomainInfo.setCountEvent(sum);
                }
            }
        }


    }

    private  Map<Long, Integer> updateIpGroup(Map<String, Long> data) {
        Map<Long, Integer> datas=new HashMap<>();
        for (Map.Entry<String, Long> m:data.entrySet()){
            Long aLong = IPUtils.ip2int(m.getKey());
            datas.put(aLong, Math.toIntExact(m.getValue()));
        }
        return datas;
    }



    @Override
    public Page<Map<String, Object>> getDomainBySecretLevel() {
        Page<Map<String, Object>> domainBySecretLevel = baseSecurityDomainMapper.getDomainBySecretLevel();
        List<Map<String, Object>> result = domainBySecretLevel.getResult();
        if (result.size()<5){
            List<String> name = result.stream().map(p -> p.get("name").toString()).collect(Collectors.toList());
            List<String> list = Arrays.asList("内部", "机密", "秘密", "绝密", "非密");
            for (String s:list){
                if (!name.contains(s)){
                    Map<String, Object> map=new HashMap<>();
                    map.put("name",s);
                    map.put("count",0);
                    result.add(map);
                }
            }
        }
        return domainBySecretLevel;
    }


}
