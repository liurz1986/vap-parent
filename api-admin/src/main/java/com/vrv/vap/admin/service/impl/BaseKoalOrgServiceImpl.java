package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.Page;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.properties.Site;
import com.vrv.vap.admin.common.util.ExcelUtil;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.dao.BaseKoalOrgDao;
import com.vrv.vap.admin.mapper.BaseKoalOrgMapper;
import com.vrv.vap.admin.mapper.BaseOrgIpSegmentMapper;
import com.vrv.vap.admin.mapper.UserMapper;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.BaseDictAllService;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.FileUploadInfoService;
import com.vrv.vap.admin.service.UserOrgService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.vo.*;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


/**
 * Created by CodeGenerator on 2018/09/13.
 */
@Service
@Transactional
@Slf4j
public class BaseKoalOrgServiceImpl extends BaseServiceImpl<BaseKoalOrg> implements BaseKoalOrgService {
    @Resource
    private BaseKoalOrgMapper baseKoalOrgMapper;

    @Resource
    private BaseOrgIpSegmentMapper baseOrgIpSegmentMapper;

    @Resource
    private UserMapper userMapper;

    @Autowired
    private BaseKoalOrgDao baseKoalOrgDao;

    @Autowired
    Cache<String, List<BaseKoalOrgVO>> koalOrgCache;

    private static final String CHCHE_KOAL_ORG = "_KOAL_ORG";
    @Autowired
    private Site siteProperties;

    @Autowired
    private BaseDictAllService baseDictAllService;

    @Autowired
    private FileUploadInfoService fileUploadInfoService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    private KafkaSenderService kafkaSenderService;

    private static final String CACHE_ORG_KEY = "_BASEINFO:BASE_KOAL_ORG:ALL";

    private static final String CACHE_ORG_VO_KEY = "_BASEINFO:BASE_KOAL_ORG_VO:ALL";

    @Autowired
    private UserOrgService userOrgService;

    private static final String ORG_TOP = "1";

    private static final String ORG_TOP_CODE = "1";

    @Override
    public List<User> queryUsers(UserQuery userQuery) {
        return baseKoalOrgMapper.queryUsers(userQuery);
    }

    @Override
    public long queryUsersCount(UserQuery userQuery) {
        return baseKoalOrgMapper.queryUsersCount(userQuery);
    }

    @Override
    public List<UserKeyVo> queryUsersKey(UserQuery userQuery) {
        return baseKoalOrgMapper.queryUsersKey(userQuery);
    }

    @Override
    public long queryUsersKeyCount(UserQuery userQuery) {
        return baseKoalOrgMapper.queryUsersKeyCount(userQuery);
    }

    @Value("${collector.configPath}")
    private String collectorConfigPath;

    @Override
    public BaseKoalOrg findByCode(String code) {
        BaseKoalOrg query = new BaseKoalOrg();
        query.setCode(code);
        BaseKoalOrg baseKoalOrg = this.findOne(query);
        if (baseKoalOrg != null) {
            BaseKoalOrgVO baseKoalOrgVO = new BaseKoalOrgVO();
            BeanUtils.copyProperties(baseKoalOrg, baseKoalOrgVO);
            //是否有下级节点
            Example example = new Example(BaseKoalOrg.class);
            example.createCriteria().andEqualTo("parentCode", code);
            List<BaseKoalOrg> koalOrgList = this.findByExample(example);
            if (CollectionUtils.isNotEmpty(koalOrgList)) {
                baseKoalOrgVO.setHasChildren(true);
            } else {
                baseKoalOrgVO.setHasChildren(false);
            }
            return baseKoalOrgVO;
        }
        return null;
    }

    @Override
    public List<BaseKoalOrg> findByParentCode(String parentCode) {
        Example example = new Example(BaseKoalOrg.class);
        example.setOrderByClause("sort asc");
        example.createCriteria().andEqualTo("parentCode", parentCode);
        return this.findByExample(example);
    }

    @Override
    public List<BaseKoalOrgVO> findHasChildren(List<BaseKoalOrg> koalOrgList) {
        List<BaseKoalOrgVO> koalOrgVOList = new ArrayList<>();

        if (CollectionUtils.isNotEmpty(koalOrgList)) {
            for (BaseKoalOrg baseKoalOrg : koalOrgList) {
                BaseKoalOrgVO baseKoalOrgVO = new BaseKoalOrgVO();
                BeanUtils.copyProperties(baseKoalOrg, baseKoalOrgVO);
                String orgCode = baseKoalOrg.getCode();
                List<BaseKoalOrg> orgList = this.findByParentCode(orgCode);
                if (CollectionUtils.isNotEmpty(orgList)) {
                    baseKoalOrgVO.setHasChildren(true);
                } else {
                    baseKoalOrgVO.setHasChildren(false);
                }
                koalOrgVOList.add(baseKoalOrgVO);
            }
        }
        return koalOrgVOList;
    }

    @Override
    public BaseKoalOrg findByIpNum(long ipNum, String orgHierarchy, String code) {
        if (koalOrgCache.get(CHCHE_KOAL_ORG) == null) {
            List<BaseKoalOrgVO> koalOrgVoList = baseKoalOrgMapper.findAll();
            if (CollectionUtils.isNotEmpty(koalOrgVoList)) {
                koalOrgCache.put(CHCHE_KOAL_ORG, koalOrgVoList);
            }
        }
        List<BaseKoalOrgVO> orgVoList = koalOrgCache.get(CHCHE_KOAL_ORG);
        orgVoList = orgVoList.stream().filter(p->p.getStartIpNum()!=null).collect(Collectors.toList());
        for (BaseKoalOrgVO baseKoalOrgVO : orgVoList) {
            if ((orgHierarchy != null ? orgHierarchy.equals(baseKoalOrgVO.getOrghierarchy() + "") : true)
                    && (code != null ? code.equals(baseKoalOrgVO.getCode() + "") : true)
                    && ipNum >= baseKoalOrgVO.getStartIpNum()
                    && ipNum <= baseKoalOrgVO.getEndIpNum()) {
                BaseKoalOrg baseKoalOrg = new BaseKoalOrg();
                BeanUtils.copyProperties(baseKoalOrgVO, baseKoalOrg);
                return baseKoalOrg;
            }
        }
        return null;

    }

    @Override
    public Integer deleteByOrgIds(String ids) {
        Example example = new Example(BaseKoalOrg.class);
        example.createCriteria().andEqualTo("code", ids);
        List<BaseKoalOrg> baseKoalOrgs = this.findByExample(example);
        if (baseKoalOrgs == null || baseKoalOrgs.size() == 0) {
            return 0;
        }
        List<BaseKoalOrg> list = new ArrayList<>();
        list.addAll(baseKoalOrgs);

        for (BaseKoalOrg org : list) {

            this.deleteById(org.getUuId());
        }
        return list.size();
    }


//    @Override
//    public List<OrgMember> queryOrgUsers(OrgUserQuery query) {
//        return baseKoalOrgMapper.queryOrgUsers(query);
//    }
//
//    @Override
//    public List<OrgMember> queryMembers(OrgLeaderQuery param) {
//        return baseKoalOrgMapper.queryMembers(param);
//    }
//
//
//    @Override
//    public List<OrgMember> queryUpMembers(OrgLeaderQuery param) {
//        return baseKoalOrgMapper.queryUpMembers(param);
//    }

    @Override
    public List<BaseKoalOrg> findAllHasUser() {
        return baseKoalOrgMapper.findAllHasUser();
    }

    @Override
    public Page<BaseKoalOrg> getOrgPageByIpRange(IpRangeQuery iprange) {
        return baseKoalOrgDao.getOrgPageByIpRange(iprange);
    }


    @Override
    public List<BaseOrgIpSegment> getAllProvinceIp() {
        return baseKoalOrgMapper.getAllProvinceIp();
    }

    @Override
    public BaseKoalOrg generateSubCode(BaseKoalOrg baseKoalOrg) {
        BaseKoalOrg baseKoalquery= new BaseKoalOrg();
        if(siteProperties.getOrgRoot().equals(baseKoalOrg.getCode())){
            // 为最顶层节点
            baseKoalOrg.setSubCode("001");
            return baseKoalOrg;
        }
        baseKoalquery.setCode(baseKoalOrg.getParentCode());
        List<BaseKoalOrg>  list =   baseKoalOrgMapper.select(baseKoalquery);
        if(CollectionUtils.isNotEmpty(list)){
            String subCode = list.get(0).getSubCode();
            if(StringUtils.isNotEmpty(subCode)){
                // 确定下一子机构的最大编码
                BaseKoalOrg baseKoalquery2= new BaseKoalOrg();
                baseKoalquery2.setParentCode(baseKoalOrg.getParentCode());
                List<BaseKoalOrg>  list2 = baseKoalOrgMapper.select(baseKoalquery2);
                String newSubCode =  subCode + getMaxNum(list2);
                baseKoalOrg.setSubCode(newSubCode);
            }
            return baseKoalOrg;
        }
        return  baseKoalOrg;
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public void initSubCode() {
        Example example = new Example(BaseKoalOrg.class);
        example.createCriteria().orEqualTo("subCode","").orIsNull("subCode");
        int count = baseKoalOrgMapper.selectCountByExample(example);
        if(count > 100){
            //如果超过100个节点需要更新，那么直接查询所有在内存计算code
            List<BaseKoalOrg> baseKoalOrgList =  this.findAll();
            List<BaseKoalOrg> updateOrgList =   getUpdateOrgs(baseKoalOrgList);
            updateOrgList.forEach(p->{
                        this.update(p);
                    }
            );
            return;
        }
        List<BaseKoalOrg> list = baseKoalOrgMapper.selectByExample(example);
       if(CollectionUtils.isNotEmpty(list)){
            for(BaseKoalOrg baseKoalOrg : list){
                updateBaseKoalOrg(baseKoalOrg);
            }
        }
    }


    @Override
    public List<BaseKoalOrg> findSubOrgByCode(String code) {
        List<BaseKoalOrg> BaseKoalOrgs = new ArrayList<>();
        //加入自身节点
        getSubOrg(BaseKoalOrgs,code);
        return BaseKoalOrgs;
    }



    private void  getSubOrg(List<BaseKoalOrg> baseKoalOrgs, String code){
        Example example = new Example(BaseKoalOrg.class);
        example.createCriteria().andEqualTo("parentCode",code);
        List<BaseKoalOrg> subOrgs = this.findByExample(example);
        if(subOrgs == null|| subOrgs.size() == 0) {
            return;
        }
        baseKoalOrgs.addAll(subOrgs);
        for(BaseKoalOrg org :subOrgs){
            getSubOrg(baseKoalOrgs,org.getCode());
        }
    }




    private  List<BaseKoalOrg> getUpdateOrgs(List<BaseKoalOrg> list){
        Optional<BaseKoalOrg> rootOrgs = list.stream().filter(p->siteProperties.getOrgRoot().equals(p.getCode())).findFirst();
        List<BaseKoalOrg> baseKoalOrgList = new ArrayList<>();
        if(rootOrgs.isPresent()){
            BaseKoalOrg rootOrg = rootOrgs.get();
            if(StringUtils.isEmpty(rootOrg.getSubCode())){
                rootOrg.setSubCode("001");
                baseKoalOrgList.add(rootOrg);
            }
            List<BaseKoalOrg> childList = list.stream().filter(p->rootOrg.getCode().equals(p.getParentCode())).collect(Collectors.toList());
            getUpdateOrgByTree(rootOrg,childList,list,baseKoalOrgList);
        }
        return  baseKoalOrgList;
    }

    private  void getUpdateOrgByTree(BaseKoalOrg parent,List<BaseKoalOrg> childList,List<BaseKoalOrg> allList,List<BaseKoalOrg> updateList){
        if (childList.size() == 0) {
            return;
        }
        Optional<BaseKoalOrg> maxOrgPresent = childList.stream().filter(p->StringUtils.isNotEmpty(p.getSubCode())).max((s1,s2)->s1.getSubCode().compareTo(s2.getSubCode()));
        String parentSubCode = parent.getSubCode();
        Integer maxNum = 0;
        if(maxOrgPresent.isPresent()){
            BaseKoalOrg maxOrg = maxOrgPresent.get();
            String maxStr = maxOrg.getSubCode().substring(maxOrg.getSubCode().length()-3,maxOrg.getSubCode().length());
            maxNum = Integer.parseInt(maxStr);
        }
        List<BaseKoalOrg> needUpdateChildList = childList.stream().filter(p->StringUtils.isEmpty(p.getSubCode())).collect(Collectors.toList());
        for(BaseKoalOrg p:needUpdateChildList) {
            maxNum++;
            p.setSubCode(parentSubCode + String.format("%03d", maxNum));
            updateList.add(p);

        }
        childList.forEach(p->{
            List<BaseKoalOrg> subList = allList.stream().filter(q->p.getCode().equals(q.getParentCode())).collect(Collectors.toList());
            getUpdateOrgByTree(p,subList,allList,updateList);
        });

    }

    private  void updateBaseKoalOrg(BaseKoalOrg baseKoalOrg){
        List<BaseKoalOrg> list = getBaseKoalOrgList(baseKoalOrg);
        if(CollectionUtils.isNotEmpty(list)){
            for(int i= 0;i<list.size();i++){
                BaseKoalOrg baseKoalOrg1 = generateSubCode(list.get(list.size()-i-1));
                baseKoalOrgMapper.updateByPrimaryKeySelective(baseKoalOrg1);
            }
        }
    }


    // 获取该节点，及其以上需要维护subCode的节点，构成节点集合
    private  List<BaseKoalOrg>  getBaseKoalOrgList(BaseKoalOrg baseKoalOrg){
        baseKoalOrg = baseKoalOrgMapper.selectByPrimaryKey(baseKoalOrg.getUuId());
        List<BaseKoalOrg> list = new ArrayList<>();
        if(StringUtils.isNotEmpty(baseKoalOrg.getSubCode())){
            return list;
        }
        if(siteProperties.getOrgRoot().equals(baseKoalOrg.getCode())){
           list.add(baseKoalOrg);
           return  list;
        }
        boolean flag = true;
        list.add(baseKoalOrg);
        while (flag){
            BaseKoalOrg  parentBaseKoalOrg = getParentBaseKoalOrg(baseKoalOrg);
            if(parentBaseKoalOrg == null){
                return new ArrayList<>();
            }
            if(StringUtils.isEmpty(parentBaseKoalOrg.getSubCode())){
                list.add(parentBaseKoalOrg);
                baseKoalOrg = parentBaseKoalOrg;
            }
            else {
                flag = false;
            }
            if(siteProperties.getOrgRoot().equals(baseKoalOrg.getCode())){
                flag = false;
            }
        }
        return  list;
    }





    private BaseKoalOrg getParentBaseKoalOrg(BaseKoalOrg baseKoalOrg){
        Example example = new Example(BaseKoalOrg.class);
        example.createCriteria().andEqualTo("code",baseKoalOrg.getParentCode());
        List<BaseKoalOrg> list = baseKoalOrgMapper.selectByExample(example);
        if(CollectionUtils.isNotEmpty(list)){
            return list.get(0);
        }
        return null;
    }

    public void deleteAllOrg() {
        List<BaseKoalOrg> baseKoalOrgList = baseKoalOrgMapper.selectAll();
        if (CollectionUtils.isNotEmpty(baseKoalOrgList)) {
            for (BaseKoalOrg baseKoalOrg : baseKoalOrgList) {
                baseKoalOrgMapper.deleteByPrimaryKey(baseKoalOrg.getUuId());
            }
        }
    }

    @Override
    public BaseKoalOrg findRootBaseKoal() {
        List<BaseKoalOrg> baseKoalOrgList = this.findByProperty(BaseKoalOrg.class,"code",siteProperties.getOrgRoot());
        if (CollectionUtils.isEmpty(baseKoalOrgList)) {
            List<BaseKoalOrg> orgList = this.findByProperty(BaseKoalOrg.class,"type","1");
            if (CollectionUtils.isNotEmpty(orgList)) {
                return orgList.get(0);
            }
        }
        // 兼容基础数据同步
        if (CollectionUtils.isEmpty(baseKoalOrgList)) {
            baseKoalOrgList = this.findByProperty(BaseKoalOrg.class,"code",ORG_TOP_CODE);
        }
        if(baseKoalOrgList.size()>0){
            Example example = new Example(BaseKoalOrg.class);
            BaseKoalOrg rootOrg = baseKoalOrgList.get(0);
            example.createCriteria().andEqualTo("parentCode",rootOrg.getCode());
            Integer count = this.count(example);
            BaseKoalOrgVO baseKoalOrgVO = new BaseKoalOrgVO();
            BeanUtils.copyProperties(rootOrg, baseKoalOrgVO);
            baseKoalOrgVO.setHasChildren(count>0);

            return baseKoalOrgVO;
        }
        return null;
    }


    private  String getMaxNum ( List<BaseKoalOrg> list2){
        if(CollectionUtils.isEmpty(list2)){
           return  "001";
        }
        else {
            List<BaseKoalOrg>  hasSubCodeList = list2.stream().filter(p->StringUtils.isNotEmpty(p.getSubCode())).collect(Collectors.toList());
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

    @Override
    @Transactional
    public void importOrg(List<BaseKoalOrgExcel> baseKoalOrgExcelList,Integer importType) {
        baseDictAllService.generateDicMap();
        Map<String, Map<String,String>> dicMap = baseDictAllService.getDicValueToCodeMap();

        if(baseKoalOrgExcelList.size()>0){
            if (importType == 0) {
                Example example = new Example(BaseKoalOrg.class);
                example.createCriteria().andGreaterThan("uuId",0);
                baseKoalOrgMapper.deleteByExample(example);
                example = new Example(BaseOrgIpSegment.class);
                example.createCriteria().andGreaterThan("id",0);
                baseOrgIpSegmentMapper.deleteByExample(example);
            }
            final List<BaseKoalOrg> rootOrgs = new ArrayList<>();
            List<BaseKoalOrg> orgList = new ArrayList<>();
            List<BaseOrgIpSegment> segmentList = new ArrayList<>();
            baseKoalOrgExcelList.forEach(b->{
                BaseKoalOrg baseKoalOrgImport = new BaseKoalOrg();
                BeanUtils.copyProperties(b,baseKoalOrgImport);
                String secretLevel = dicMap.get("zjg-保密等级").get(b.getSecretLevel());
                String type = dicMap.get("zjg-机构类型").get(b.getType());
                String protectionLevel = dicMap.get("网络信息-防护等级").get(b.getProtectionLevel());
                String secretQualifications = dicMap.get("zjg-保密资格").get(b.getSecretQualifications());
                String orgType = dicMap.get("zjg-单位类别").get(b.getOrgType());
                if (StringUtils.isNotEmpty(secretLevel)) {
                    baseKoalOrgImport.setSecretLevel(Integer.parseInt(secretLevel));
                }
                if (StringUtils.isNotEmpty(type)) {
                    baseKoalOrgImport.setType(type);
                }
                if (StringUtils.isNotEmpty(protectionLevel)) {
                    baseKoalOrgImport.setProtectionLevel(Integer.parseInt(protectionLevel));
                }
                if (StringUtils.isNotEmpty(secretQualifications)) {
                    baseKoalOrgImport.setSecretQualifications(Integer.parseInt(secretQualifications));
                }
                if (StringUtils.isNotEmpty(orgType)) {
                    baseKoalOrgImport.setOrgType(Integer.parseInt(orgType));
                }

                baseKoalOrgImport.setOrghierarchy(Byte.valueOf("0"));
                baseKoalOrgImport.setStatus("0");
                String code = b.getCode();
                if("1".equals(baseKoalOrgImport.getType()) && importType == 0) {
                    rootOrgs.add(baseKoalOrgImport);
                    //siteProperties.setOrgRoot(baseKoalOrgImport.getCode());
                    siteProperties.orgRoot = baseKoalOrgImport.getCode();
                }
                BaseKoalOrg baseKoalOrg = this.generateSubCode(baseKoalOrgImport);
                orgList.add(baseKoalOrg);

                if (StringUtils.isNotEmpty(b.getIpRanges())) {
                    Example exampleip = new Example(BaseOrgIpSegment.class);
                    exampleip.createCriteria().andEqualTo("areaCode", code);
                    List<BaseOrgIpSegment> baseOrgIpSegments = baseOrgIpSegmentMapper.selectByExample(exampleip);
                    Optional<String> idsOp =  baseOrgIpSegments.stream().map(orgip->orgip.getId().toString()).reduce((p1,p2) -> p1+","+p2);
                    if(idsOp.isPresent() && importType == 0){
                        baseOrgIpSegmentMapper.deleteByIds(idsOp.get());
                    }

                    for (String ipRange : b.getIpRanges().split(",")) {
                        String[] ip = ipRange.split("-");
                        Long start = IPUtils.ip2int(ip[0]);
                        Long end = IPUtils.ip2int(ip[1]);
                        BaseOrgIpSegment baseOrgIpSegment = new BaseOrgIpSegment();
                        baseOrgIpSegment.setAreaCode(code);
                        baseOrgIpSegment.setStartIpSegment(ip[0]);
                        baseOrgIpSegment.setEndIpSegment(ip[1]);
                        baseOrgIpSegment.setDepartmentCode("");
                        baseOrgIpSegment.setAreaName(b.getName());
                        baseOrgIpSegment.setStartIpNum(start);
                        baseOrgIpSegment.setEndIpNum(end);
                        segmentList.add(baseOrgIpSegment);
                    }
                }
            });
            if (CollectionUtils.isNotEmpty(orgList)) {
                this.save(orgList);
            }
            if (CollectionUtils.isNotEmpty(segmentList)) {
                baseOrgIpSegmentMapper.insertList(segmentList);
            }
            if(rootOrgs.size()>0 && importType == 0){
                List<User> users = userMapper.selectAll();
                users.stream().forEach(user -> {
                    user.setOrgCode(rootOrgs.get(0).getCode());
                    user.setOrgName(rootOrgs.get(0).getName());
                    userMapper.updateByPrimaryKey(user);
                });
            }

        }
    }

    @Override
    public List<BaseKoalOrgVO> generateKoalOrgVO(List<BaseKoalOrg> koalOrgList) {
        List<BaseKoalOrgVO> orgVOList = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(koalOrgList)) {
            for (BaseKoalOrg org : koalOrgList) {
                BaseKoalOrgVO baseKoalOrgVO = new BaseKoalOrgVO();
                BeanUtils.copyProperties(org,baseKoalOrgVO);
                orgVOList.add(baseKoalOrgVO);
            }
        }
        return orgVOList;
    }

    public BaseKoalOrgVO toBaseKoalOrgVO(BaseKoalOrg org) {
        BaseKoalOrgVO baseKoalOrgVO = new BaseKoalOrgVO();
        BeanUtils.copyProperties(org,baseKoalOrgVO);
        return baseKoalOrgVO;
    }

    /**
     * 构建树
     *
     */
    public BaseKoalOrgVO findChildren(BaseKoalOrgVO tree, List<BaseKoalOrgVO> list) {
        if(tree == null || CollectionUtils.isEmpty(list)){
            return  null;
        }
        for (BaseKoalOrgVO node : list) {
            if (!ORG_TOP.equals(node.getType()) && node.getParentCode().equals(tree.getCode())) {
                if (tree.getChildren() == null) {
                    tree.setChildren(new ArrayList<>());
                    tree.setHasChildren(false);
                }
                tree.getChildren().add(findChildren(node, list));
                if (findChildren(node, list) != null) {
                    tree.setHasChildren(true);
                } else {
                    tree.setHasChildren(false);
                }
            }
        }
        return tree;
    }

    @Override
    public List<BaseKoalOrgVO> getTreeList(Set<String> orgCodeSet) {
        List<BaseKoalOrgVO> result =  new ArrayList<>();
        Set<String> authorizedSet = new HashSet<>(orgCodeSet);
        List<BaseKoalOrgVO> baseKoalOrgList = baseKoalOrgMapper.findAll();
        Map<String, List<BaseKoalOrgVO>> baseKoalOrgMap = baseKoalOrgList.stream().filter(p -> !ORG_TOP.equals(p.getType())).collect(Collectors.groupingBy(BaseKoalOrgVO::getParentCode));
        if(CollectionUtils.isEmpty(orgCodeSet)){
            return  result;
        }
        // 获取组成安全域树形结构的节点集
        for(String orgCode : orgCodeSet) {
            Example example = new Example(BaseKoalOrg.class);
            example.createCriteria().andEqualTo("code",orgCode);
            List<BaseKoalOrg> orgList = baseKoalOrgMapper.selectByExample(example);
            if (CollectionUtils.isEmpty(orgList)) {
                continue;
            }
            BaseKoalOrg baseKoalOrg = orgList.get(0);
            //将节点自身加入节点集
            String code = baseKoalOrg.getCode();
            if(!result.stream().anyMatch(p->code.equals(p.getCode()))){
                BaseKoalOrgVO baseKoalOrgVO = new BaseKoalOrgVO();
                BeanUtils.copyProperties(baseKoalOrg,baseKoalOrgVO);
                result.add(baseKoalOrgVO);
            }
            if (!ORG_TOP.equals(baseKoalOrg.getType())){
                // 向上搜索节点
                this.generateTopTree(baseKoalOrg,result);
                // 向下搜索节点
                this.generateDownTree(baseKoalOrg,result,baseKoalOrgMap, authorizedSet);

            } else {
                Example exam = new Example(BaseKoalOrg.class);
                exam.createCriteria().andEqualTo("parentCode",orgCode);
                List<BaseKoalOrg> koalOrgList = baseKoalOrgMapper.selectByExample(exam);
                if (CollectionUtils.isNotEmpty(koalOrgList)) {
                    for (BaseKoalOrg koalOrg : koalOrgList) {
                        String upperCode = koalOrg.getCode();
                        boolean isInclude = result.stream().anyMatch(p->upperCode.equals(p.getCode()));
                        if(!isInclude){
                            //将向下搜索的节点加入节点集
                            BaseKoalOrgVO treeVO = toBaseKoalOrgVO(koalOrg);
                            treeVO.setIsAuthorized(1);
                            result.add(treeVO);
                        }
                    }
                }
            }
        }
        //为节点打上权限标识
        for(BaseKoalOrgVO treeVO : result){
            if(authorizedSet.contains(treeVO.getCode())){
                treeVO.setIsAuthorized(1);
            } else {
                treeVO.setIsAuthorized(0);
            }
        }
        result = result.stream().sorted(Comparator.comparing(BaseKoalOrgVO::getCode)).collect(Collectors.toList());
        return  result;
    }

    private void generateTopTree(BaseKoalOrg baseKoalOrg,List<BaseKoalOrgVO> result) {
        Example example = new Example(BaseKoalOrg.class);
        example.createCriteria().andEqualTo("code",baseKoalOrg.getParentCode());
        List<BaseKoalOrg> orgList = baseKoalOrgMapper.selectByExample(example);
        if (CollectionUtils.isNotEmpty(orgList)) {
            BaseKoalOrg koalOrg = orgList.get(0);
            String upperCode = koalOrg.getCode();
            boolean isInclude = result.stream().anyMatch(p->upperCode.equals(p.getCode()));
            if(!isInclude){
                //将向上搜索的节点加入节点集
                result.add(toBaseKoalOrgVO(koalOrg));
            }
            if (!ORG_TOP.equals(koalOrg.getType())) {
                this.generateTopTree(koalOrg,result);
            }
        }
    }

    private void generateDownTree(BaseKoalOrg baseKoalOrg,List<BaseKoalOrgVO> result, Map<String,
            List<BaseKoalOrgVO>> baseKoalOrgMap, Set<String> authorizedSet) {
        List<BaseKoalOrgVO> baseKoalOrgList = baseKoalOrgMap.get(baseKoalOrg.getCode());
        if (CollectionUtils.isEmpty(baseKoalOrgList)) {
            return;
        }

        baseKoalOrgList.forEach(k -> {
            authorizedSet.add(k.getCode());
            boolean isInclude = result.stream().anyMatch(p -> k.getCode().equals(p.getCode()));
            if (!isInclude) {
                result.add(toBaseKoalOrgVO(k));
            }
            generateDownTree(k, result, baseKoalOrgMap, authorizedSet);
        });
    }


    @Override
    public String sync() {
        List<BaseKoalOrg> baseKoalOrgs = findAll();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\"table\":\"baseinfo_org\",\"join\":\"std_org_code\",\"add\":[\"std_sys_port\",\"std_org_name\",\"std_org_type\",\"std_secret_qualifications\",\"std_secret_level\",\"std_protection_level\"],sep:\",\"}");
        stringBuilder.append("\n");
        stringBuilder.append("std_org_code,std_org_name,std_org_type,std_secret_qualifications,std_secret_level,std_protection_level");
        stringBuilder.append("\n");
        AtomicInteger count = new AtomicInteger(0);
        baseKoalOrgs.stream().forEach(baseKoalOrg -> {
            stringBuilder.append(baseKoalOrg.getCode());
            stringBuilder.append(",");
            stringBuilder.append(StringUtils.isNotEmpty(baseKoalOrg.getName())?baseKoalOrg.getName():"-1");
            stringBuilder.append(",");
            stringBuilder.append(baseKoalOrg.getOrgType()!=null?baseKoalOrg.getOrgType():"-1");
            stringBuilder.append(",");
            stringBuilder.append(baseKoalOrg.getSecretQualifications()!=null?baseKoalOrg.getSecretQualifications():"-1");
            stringBuilder.append(",");
            stringBuilder.append(baseKoalOrg.getSecretLevel()!=null?baseKoalOrg.getSecretLevel():"-1");
            stringBuilder.append(",");
            stringBuilder.append(baseKoalOrg.getProtectionLevel()!=null?baseKoalOrg.getProtectionLevel():"-1");
            stringBuilder.append("\n");
        });

        return stringBuilder.toString();
    }

    @Override
    public List<BaseKoalOrg> findByUser(User user) {
        List<BaseKoalOrg> orgList = new ArrayList<>();
        if (Const.USER_ADMIN.equals(user.getAccount())) {
            List<BaseKoalOrg> allOrg = baseKoalOrgMapper.selectAll();
            return allOrg;
        }
        Example example = new Example(UserOrg.class);
        example.createCriteria().andEqualTo("userId",user.getId());
        List<UserOrg> orgUserList = userOrgService.findByExample(example);
        if (CollectionUtils.isNotEmpty(orgUserList)) {
            for (UserOrg userOrg : orgUserList) {
                BaseKoalOrg baseKoalOrg = baseKoalOrgMapper.selectByPrimaryKey(userOrg.getOrgId());
                if (baseKoalOrg != null) {
                    orgList.add(baseKoalOrg);
                }
            }
        }
        return orgList;
    }

    @Override
    public void cacheOrg() {
        List<BaseKoalOrg> orgList = baseKoalOrgMapper.selectAll();
        List<BaseKoalOrgVO> orgVOList = baseKoalOrgMapper.findAll();
        redisTemplate.opsForValue().set(CACHE_ORG_KEY,JSON.toJSONString(orgList));
        redisTemplate.opsForValue().set(CACHE_ORG_VO_KEY,JSON.toJSONString(orgVOList));
    }

    @Override
    public Map<String, Object> validateImportOrg(String id,Integer importType) {
        Map<String, Object> result = new HashMap<>();
        List<BaseKoalOrgExcel> successList = new ArrayList<>();
        List<BaseKoalOrgExcel> errorList = new ArrayList<>();
        result.put("false", errorList);
        result.put("true", successList);
        List<FileUpLoadInfo> fileUpLoadInfoList = fileUploadInfoService.findByProperty(FileUpLoadInfo.class, "fileId", id);
        if (CollectionUtils.isNotEmpty(fileUpLoadInfoList)) {
            FileUpLoadInfo fileUpLoadInfo = fileUpLoadInfoList.get(0);
            if (fileUpLoadInfo.getUploadType() == 0) {
                String filePath = fileUpLoadInfo.getFilePath();
                List<BaseKoalOrgExcel> baseKoalOrgExcelList = null;
                try (InputStream st = new FileInputStream(filePath)) {
                    baseKoalOrgExcelList = ExcelUtil.importExcel(st, 1, 1, BaseKoalOrgExcel.class);
                } catch (Exception e) {
                    log.error("",e);
                }
                if (CollectionUtils.isEmpty(baseKoalOrgExcelList)) {
                    return result;
                }
                List<BaseKoalOrg> orgList = this.findAll();
                Set<String> set = new HashSet<>();
                Integer unitIndex = 0;
                // 增量时统计单位数量
                if (importType == 1) {
                    if (CollectionUtils.isNotEmpty(orgList)) {
                        for (BaseKoalOrg org : orgList) {
                            set.add(org.getCode());
                            if ("1".equals(org.getType())) {
                                unitIndex++;
                            }
                        }
                    }
                }
                long rootCount = baseKoalOrgExcelList.stream().filter(item -> "单位".equals(item.getType())).count();
                for (BaseKoalOrgExcel baseKoalOrgExcel : baseKoalOrgExcelList) {
                    String reason = "";
                    // 全量时校验根节点不存在
                    if (rootCount == 0 && importType == 0) {
                        reason += "机构根节点不存在;";
                    }
                    // 组织机构编码不可重复
                    String code = baseKoalOrgExcel.getCode();
                    if (!set.contains(code)) {
                        set.add(code);
                    } else {
                        reason += "组织机构编码重复;";
                    }
                    // 单位有且只能有一个
                    if ("单位".equals(baseKoalOrgExcel.getType())) {
                        unitIndex++;
                    }
                    if (importType == 1) {
                        if ("单位".equals(baseKoalOrgExcel.getType()) && unitIndex > 1) {
                            reason += ErrorCode.ORGANZATION_TYPE_ORG.getResult().getMessage() + ";";
                        }
                    } else {
                        if (unitIndex > 1) {
                            reason += ErrorCode.ORGANZATION_TYPE_ORG.getResult().getMessage() + ";";
                        }
                    }
                    // 空字段校验
                    if (StringUtils.isEmpty(baseKoalOrgExcel.getCode())) {
                        reason += "机构编码为空;";
                    }
                    if (StringUtils.isEmpty(baseKoalOrgExcel.getParentCode())) {
                        reason += "上级机构编码为空;";
                    }
                    if (StringUtils.isEmpty(baseKoalOrgExcel.getName())) {
                        reason += "机构名称为空;";
                    }
                    if (StringUtils.isEmpty(baseKoalOrgExcel.getType())) {
                        reason += "机构类型为空;";
                    } else {
                        // 上级机构编码非空
                        if ("部门".equals(baseKoalOrgExcel.getType()) && StringUtils.isNotEmpty(baseKoalOrgExcel.getParentCode())) {
                            String parentCode = baseKoalOrgExcel.getParentCode();
                            long parentCount = baseKoalOrgExcelList.stream().filter(item -> (item.getCode() != null && parentCode.equals(item.getCode()))).count();
                            // 增量时继续校验原有数据
                            if (importType == 1) {
                                parentCount += orgList.stream().filter(item -> (item.getCode() != null && parentCode.equals(item.getCode()))).count();
                            }
                            if (parentCount == 0) {
                                reason += "上级机构编码不存在;";
                            }
                        }
                    }
                    if (StringUtils.isEmpty(baseKoalOrgExcel.getSecretLevel())) {
                        reason += "保密等级为空;";
                    }
                    if (StringUtils.isEmpty(baseKoalOrgExcel.getProtectionLevel())) {
                        reason += "防护等级为空;";
                    }
                    if (StringUtils.isEmpty(baseKoalOrgExcel.getSecretQualifications())) {
                        reason += "保密资格为空;";
                    }
                    if (StringUtils.isEmpty(baseKoalOrgExcel.getOrgType())) {
                        reason += "单位类别为空;";
                    }
                    Long start = 0L;
                    Long end = 0L;
                    if (StringUtils.isNotEmpty(baseKoalOrgExcel.getIpRanges())) {
                        String ipRanges = baseKoalOrgExcel.getIpRanges().replaceAll(" ", "")
                                .replaceAll("\n", "");
                        for (String ipRange : ipRanges.split(",")) {
                            String[] ip = ipRange.split("-");
                            if (ip == null || ip.length < 2) {
                                reason += ipRanges + "IP范围无效;";
                            } else {
                                if (StringUtils.isNotEmpty(ip[0])) {
                                    start = IPUtils.ip2int(ip[0]);
                                }
                                if (ip.length > 1 && StringUtils.isNotEmpty(ip[1])) {
                                    end = IPUtils.ip2int(ip[1]);
                                }
                                if (start == 0L || end == 0L || !IPUtils.isValidIPAddress(ip[0]) || (ip.length > 1 && !IPUtils.isValidIPAddress(ip[1]))) {
                                    reason += ipRanges + "IP范围无效;";
                                }
                            }
                        }
                    }
                    if (StringUtils.isNotEmpty(reason)) {
                        baseKoalOrgExcel.setReason(reason);
                        errorList.add(baseKoalOrgExcel);
                    } else {
                        successList.add(baseKoalOrgExcel);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public void sendChangeMessage() {
        Map<String,Object> result = new HashMap<>();
        result.put("item","org");
        result.put("time", System.currentTimeMillis());
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String content = objectMapper.writeValueAsString(result);
            kafkaSenderService.send("vap_base_data_change_message",null,content);
        } catch (Exception e) {
            log.error("",e);
        }
    }
}


