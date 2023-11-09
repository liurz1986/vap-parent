package com.vrv.vap.admin.web;


import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.ExcelUtil;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.common.util.YmlUtils;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.service.feign.AuditXcFeign;
import com.vrv.vap.admin.vo.*;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.annotations.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;


@RequestMapping(path = "/secruity/domain")
@RestController
public class SecruityDomainController extends ApiController {
    private static final String START_IP_NUM = "startIpNum";
    private static final String END_IP_NUM = "endIpNum";
    Logger logger = LoggerFactory.getLogger(SecruityDomainController.class);

    @Autowired
    private BaseSecurityDomainService baseSecurityDomainService;

    @Autowired
    private BaseSecurityDomainIpSegmentService baseSecurityDomainIpSegmentService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private  UserDomainService userDomainService;

    @Autowired
    private  UserService userService;

    @Autowired
    private  RoleService roleService;
    @Autowired
    private AuditXcFeign auditXcFeign;
    @Autowired
    private SelfConcernAssetService selfConcernAssetService;
    @Autowired
    StringRedisTemplate redisTemplate;

    @Value("${collector.configPath}")
    private String collectorConfigPath;

    @Value("${vap.cty.tenant_roleCode}")
    private String tenantRoleCode;

    private static final String CACHE_SEC_KEY = "_BASEINFO:BASE_SECURITY_DOMAIN:ALL";

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("secretLevel", "{\"0\":\"绝密\",\"1\":\"机密\",\"2\":\"秘密\",\"3\":\"内部\",\"4\":\"非密\"}");
    }
    private static Log log = LogFactory.getLog(SecruityDomainController.class);
    /**
     * 获取安全域树形结构
     *
     */
    @ApiOperation(value = "获取安全域树形结构")
    @GetMapping(value ="/tree")
    @SysRequestLog(description="获取安全域树形结构", actionType = ActionType.SELECT)
    public BaseSecurityDomainTreeVO getDomainTree(){
        List<BaseSecurityDomain> allDomain = baseSecurityDomainService.findAll();
        List<BaseSecurityDomainTreeVO> treeList = allDomain.stream().map(p->toBaseSecurityDomainTreeVO(p)).collect(Collectors.toList());
        treeList.forEach(p->p.setIsAuthorized(1));
        BaseSecurityDomainTreeVO topNode = new BaseSecurityDomainTreeVO();
        for(BaseSecurityDomainTreeVO bt : treeList){
            if(bt.getParentCode() == null){
                topNode = bt;
                break;
            }
        }
        BaseSecurityDomainTreeVO result = findChildren(topNode,treeList);
        return  result;
    }


    /**
     * 获取所有安全域
     */
    @ApiOperation(value = "获取所有安全域")
    @GetMapping(value ="/all")
    @SysRequestLog(description="获取所有安全域", actionType = ActionType.SELECT)
    public VData<List<BaseSecurityDomainVO>> getAllDomain() {
        String domainStr = redisTemplate.opsForValue().get(CACHE_SEC_KEY);
        List<BaseSecurityDomainVO> securityDomainVoList = JSON.parseArray(domainStr,BaseSecurityDomainVO.class);
        if (CollectionUtils.isEmpty(securityDomainVoList)) {
            List<BaseSecurityDomain> securityDomainList = baseSecurityDomainService.findAll();
            securityDomainVoList = baseSecurityDomainService.fillChildren(securityDomainList);
            redisTemplate.opsForValue().set(CACHE_SEC_KEY, JSON.toJSONString(securityDomainVoList));
        }
        return this.vData(securityDomainVoList);
    }


    /**
     * 根据code获取单个安全域
     * @param guid
     * @return
     */
    @ApiOperation(value = "根据code获取单个安全域")
    @GetMapping(value = "/single/{code}")
    @SysRequestLog(description="根据code获取单个安全域", actionType = ActionType.SELECT)
    public VData getSingleDomain(@PathVariable("code") String guid) {
        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().andEqualTo("code",guid);
        List<BaseSecurityDomain> securityDomainList = baseSecurityDomainService.findByExample(example);
        if (CollectionUtils.isNotEmpty(securityDomainList)) {
            return this.vData(securityDomainList.get(0));
        }
        return this.vData(false);
    }

    /**
     * 查询网络边界画像
     * @param domainQuery
     * @return
     */
    @ApiOperation(value = "查询网络边界画像")
    @PostMapping(value = "/getDomainPage")
    @SysRequestLog(description="查询安全域", actionType = ActionType.SELECT)
    public VList<List<BaseSecurityDomainInfo>> getDomainPage(@RequestBody DomainQuery domainQuery,HttpServletRequest httpServletRequest) {
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) httpServletRequest.getSession().getAttribute(Global.SESSION.USER);
        domainQuery.setUserId(user.getId());
        List<PktResultVo> data = null;
        try {
            List<Map<String, Object>> allDomainIps = baseSecurityDomainService.getAllDomainIps();
            Map<String, List<Map<String, Object>>> map=new HashMap<>();
            map.put("ipRangeList",allDomainIps);
            VData<List<PktResultVo>> listVData = auditXcFeign.communicationTotalPkt(map);
            data = listVData.getData();
        } catch (Exception e) {
            log.error("",e);
        }
        addQuery(domainQuery);
        List<BaseSecurityDomainInfo> domainPage = baseSecurityDomainService.getDomainPage(domainQuery);
        if (data!=null&&data.size()>0){
            for (BaseSecurityDomainInfo baseSecurityDomainInfo:domainPage){
                List<PktResultVo> collect = data.stream().filter(p -> StringUtils.isNotBlank(p.getName())&&p.getName().equals(baseSecurityDomainInfo.getDomainName())).collect(Collectors.toList());
                if (collect.size()>0){
                    baseSecurityDomainInfo.setPackages(collect.get(0).getTotalPkt());
                }
            }
        }
        Integer domainPageCount = baseSecurityDomainService.getDomainPageCount(domainQuery);
        return this.vList(domainPage,domainPageCount);
    }

    private void addQuery(DomainQuery domainQuery) {
        if (StringUtils.isNotBlank(domainQuery.getDomainInfo())){
            boolean validIPAddress = IPUtils.isValidIPAddress(domainQuery.getDomainInfo());
            if (validIPAddress){
                domainQuery.setIp(domainQuery.getDomainInfo());
                setCodes(domainQuery);
            }else {
                domainQuery.setDomainName(domainQuery.getDomainInfo());
            }
        }
        if (domainQuery.getIsJustAssetOfConcern()!=null&&domainQuery.getIsJustAssetOfConcern()){
            List<SelfConcernAsset> selfConcernAssets=selfConcernAssetService.getByUseId(domainQuery.getUserId());
            if (selfConcernAssets.size()>0){
                List<String> strings = selfConcernAssets.stream().map(p -> p.getIp()).collect(Collectors.toList());
                domainQuery.setIds(strings);
            }else {
                List<String> strings=new ArrayList<>();
                strings.add("0");
                domainQuery.setIds(strings);
            }
        }
    }
    private void setCodes(DomainQuery domainQuery) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(domainQuery.getIp())){
            Long aLong = IPUtils.ip2int(domainQuery.getIp());
            Example example=new Example(BaseSecurityDomainIpSegment.class);
            example.createCriteria().andLessThanOrEqualTo("startIpNum",aLong).andGreaterThanOrEqualTo("endIpNum",aLong);
            List<BaseSecurityDomainIpSegment> baseSecurityDomainIpSegments = baseSecurityDomainIpSegmentService.findByExample(example);
            if (baseSecurityDomainIpSegments.size()>0){
                List<String> strings = baseSecurityDomainIpSegments.stream().map(p -> p.getCode()).collect(Collectors.toList());
                domainQuery.setCodes(strings);
            }else {
                List<String> strings=new ArrayList<>();
                strings.add("0");
                domainQuery.setCodes(strings);
            }
        }
    }
    /**
     * 查询网络边界画像
     * @param
     * @return
     */
    @ApiOperation(value = "获取全部边界及ip范围")
    @GetMapping(value = "/getAllDomainIps")
    @SysRequestLog(description="查询安全域", actionType = ActionType.SELECT)
    public VData<List<Map<String,Object>>> getAllDomainIps() {
        List<Map<String, Object>> allDomainIps = baseSecurityDomainService.getAllDomainIps();
        return this.vData(allDomainIps);
    }
    /**
     * 查询网络边界画像密级统计
     * @return
     */
    @ApiOperation(value = "查询网络边界画像密级统计")
    @GetMapping (value = "/getDomainBySecretLevel")
    @SysRequestLog(description="查询安全域", actionType = ActionType.SELECT)
    public VList getDomainBySecretLevel() {
        return this.vList(baseSecurityDomainService.getDomainBySecretLevel());
    }
    /**
     * 查询网络边界画像密级统计
     * @return
     */
    @ApiOperation(value = "查询网络边界事件数量top10")
    @GetMapping (value = "/getDomainCountTop10")
    @SysRequestLog(description="查询网络边界事件数量top10", actionType = ActionType.SELECT)
    public VData<List<Map<String,Object>>> getDomainCountTop10() {
        return this.vData(baseSecurityDomainService.getDomainCountTop10());
    }
    /**
     * 导出网络边界信息
     * @return
     */
    @ApiOperation(value = "导出网络边界信息")
    @GetMapping (value = "/export")
    @SysRequestLog(description="导出网络边界信息", actionType = ActionType.EXPORT)
    public Result export(DomainQuery domainQuery,HttpServletResponse httpServletResponse,HttpServletRequest request) throws Exception{
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        domainQuery.setUserId(user.getId());
        addQuery(domainQuery);
        domainQuery.setStart_(0);
        domainQuery.setCount_(10000);
        List<BaseSecurityDomainInfo> domainPage = baseSecurityDomainService.getDomainPage(domainQuery);
        for (BaseSecurityDomainInfo baseSecurityDomainInfo:domainPage){
            if (baseSecurityDomainInfo != null && baseSecurityDomainInfo.getSecretLevel() != null) {
                switch (baseSecurityDomainInfo.getSecretLevel()) {
                    case "1":
                        baseSecurityDomainInfo.setSecretName("非密");
                        break;
                    case "2":
                        baseSecurityDomainInfo.setSecretName("内部");
                        break;
                    case "3":
                        baseSecurityDomainInfo.setSecretName("秘密");
                        break;
                    case "4":
                        baseSecurityDomainInfo.setSecretName("机密");
                        break;
                    case "5":
                        baseSecurityDomainInfo.setSecretName("绝密");
                        break;
                    default:
                        break;

                }
            }
        }
        ExcelUtil.exportExcel(domainPage, null, "网络边界信息", BaseSecurityDomainInfo.class, "网络边界信息.xls",true, httpServletResponse);
        return null;
    }
    /**
     * 查询安全域（分页)
     * @param domainQuery
     * @return
     */
    @ApiOperation(value = "查询安全域（不分页，不用传start_和count_，排序参数可用）")
    @PostMapping
    @SysRequestLog(description="查询安全域", actionType = ActionType.SELECT)
    public VData<List<BaseSecurityDomain>> getDomainList(@RequestBody DomainQuery domainQuery) {
        Example example = this.query(domainQuery,BaseSecurityDomain.class);
        return this.vData(baseSecurityDomainService.findByExample(example));
    }
    /**
     * 互联互通,外部信息code
     * @param
     * @return
     */
    @ApiOperation(value = "查询联互通,外部信息code")
    @PostMapping(value = "/getNetExtCode")
    @SysRequestLog(description="互联互通,外部信息code", actionType = ActionType.SELECT)
    public VData<BaseSecurityDomain> getNetExtCode(@RequestBody Map<String,String> map) {
        BaseSecurityDomain s=baseSecurityDomainService.getNetExtCode(map);
        return this.vData(s);
    }

    /**
     * 查询安全域（不分页)
     * @param domainQuery
     * @return
     */
    @ApiOperation(value = "查询安全域（使用名称）")
    @PostMapping(value = "/byName")
    @SysRequestLog(description="查询安全域", actionType = ActionType.SELECT)
    public VData<List<BaseSecurityDomain>> getDomainByName(@RequestBody DomainQuery domainQuery) {
        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().andEqualTo("domainName",domainQuery.getDomainName());
        return this.vData(baseSecurityDomainService.findByExample(example));
    }

    /**
     * 获取第一层级区域
     */
    @ApiOperation(value = "获取第一层级区域")
    @GetMapping
    @SysRequestLog(description="获取第一层级区域", actionType = ActionType.SELECT)
    public VData<List<BaseSecurityDomainVO>> getFirstDomain() {
        return this.vData( baseSecurityDomainService.findSubDomainByCode(null));
    }


    /**
     * 获取区域下级
     */
    @ApiOperation(value = "获取区域下级")
    @GetMapping(value ="/{code}")
    @SysRequestLog(description="获取区域下级", actionType = ActionType.SELECT)
    public VData<List<BaseSecurityDomainVO>> getSubDomain(@PathVariable("code") String guid) {
        return this.vData(baseSecurityDomainService.findSubDomainByCode(guid));
    }

    /**
     * 获取区域下级
     */
    @ApiOperation(value = "获取区域下级所有安全域")
    @GetMapping(value ="/sub/{code}")
    @SysRequestLog(description="获取区域下级所有安全域", actionType = ActionType.SELECT)
    public VData<List<BaseSecurityDomain>> getSubAllDomain(@PathVariable("code") String guid) {
        return this.vData(baseSecurityDomainService.findSubAllDomainByCode(guid));
    }


    /**
     * 安全域添加地区
     */
    @ApiOperation(value = "添加安全域地区,注意传入parentCode，为当前节点的code")
    @PutMapping
    @SysRequestLog(description="添加安全域", actionType = ActionType.ADD)
    public Result add(@RequestBody BaseSecurityDomain baseSecurityDomain){
        String parentCode = baseSecurityDomain.getParentCode();
        List<BaseSecurityDomainVO> baseSecurityDomains = baseSecurityDomainService.findSubDomainByCode(parentCode);
        String domainName = baseSecurityDomain.getDomainName();
        long count = baseSecurityDomains.stream().filter(p->domainName.equals(p.getDomainName())).count();
        if(count >0 ){
            return this.result(ErrorCode.DOMAIN_HAS_NAME);
        }
        String guid = UUID.randomUUID().toString();
        baseSecurityDomain.setCode(guid);
        // 生成安全域的层级维护代码
        baseSecurityDomain =baseSecurityDomainService.generateSubCode(baseSecurityDomain);
        int result = baseSecurityDomainService.save(baseSecurityDomain);
//        if(result == 1){
//            // 给用户赋予---添加安全域的使用权限
//            HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
//            HttpSession session =  request.getSession();
//            User user = (User)session.getAttribute(Global.SESSION.USER);
//            Integer userId = user.getId();
//            com.vrv.vap.admin.model.User loginUser = userService.findById(userId);
//            String newDomainCode =  new StringBuffer().append(loginUser.getDomainCode()).append(",").append(baseSecurityDomain.getCode()).toString();
//            String newDomainName =  new StringBuffer().append(loginUser.getDomainName()).append(",").append(baseSecurityDomain.getDomainName()).toString();
//            loginUser.setDomainCode(newDomainCode);
//            loginUser.setDomainName(newDomainName);
//            redisService.updateOrAddSecurityDomainResource(baseSecurityDomain);
//            int result1 = userService.updateSelective(loginUser);
//            if(result1 == 1){
//                UserDomain ud = new UserDomain();
//                ud.setDomainCode(baseSecurityDomain.getCode());
//                ud.setUserId(userId);
//                int result2 = userDomainService.save(ud);
//                if(result2 == 1){
//                    Map<String,String> domainMap = (Map<String,String>) session.getAttribute(Global.SESSION.DOMAIN);
//                    domainMap.put(baseSecurityDomain.getCode(),"");
//                    //重置session
//                    session.setAttribute(Global.SESSION.DOMAIN,domainMap);
//                   return this.vData(baseSecurityDomain);
//               }
//            }
//        }
//        return this.result( false);
        if(result == 1){
            BaseSecurityDomain securityDomain = new BaseSecurityDomain();
            BeanUtils.copyProperties(baseSecurityDomain,securityDomain);
            this.transferParentCode(securityDomain);
            SyslogSenderUtils.sendAddSyslogAndTransferredField(securityDomain,"添加安全域",transferMap);
            redisService.updateOrAddSecurityDomainResource(baseSecurityDomain);
            baseSecurityDomainService.cacheDomain();
            baseSecurityDomainService.sendChangeMessage();
            return this.vData(baseSecurityDomain);
        }
        return this.result( false);
    }

    /**
     * 修改安全域地区
     */
    @ApiOperation(value = "修改安全域地区")
    @PatchMapping
    @SysRequestLog(description="修改安全域", actionType = ActionType.UPDATE)
    public Result edit(@RequestBody  BaseSecurityDomain baseSecurityDomain){
        BaseSecurityDomain baseSecurityDomainSrc = baseSecurityDomainService.findById(baseSecurityDomain.getId());
        int result = baseSecurityDomainService.updateSelective(baseSecurityDomain);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(baseSecurityDomainSrc,baseSecurityDomain,"修改安全域",transferMap);
            redisService.updateOrAddSecurityDomainResource(baseSecurityDomain);
            baseSecurityDomainService.cacheDomain();
            baseSecurityDomainService.sendChangeMessage();
            return this.vData(true);
        }
        return this.vData(false);
    }

    /**
     * 查询区域下的IP段 （分页）
     */
    @PostMapping(value = "/ip")
    @ApiOperation(value = "查询区域下的IP段 （分页）")
    @SysRequestLog(description="查询区域下的IP段", actionType = ActionType.SELECT)
    public VList<BaseSecurityDomainIpSegment> queryOrgIp(@RequestBody BaseSecurityDomainIpSegmentVO baseSecurityDomainIpSegmentVO) {
        if (StringUtils.isNotEmpty(baseSecurityDomainIpSegmentVO.getCode())) {
            List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class,"code",baseSecurityDomainIpSegmentVO.getCode());
            if (CollectionUtils.isNotEmpty(baseSecurityDomains)) {
                baseSecurityDomainIpSegmentVO.setName(baseSecurityDomains.get(0).getDomainName());
            }
        }
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(baseSecurityDomainIpSegmentVO,"查询区域下的IP段",transferMap);
        Example example = this.pagination(baseSecurityDomainIpSegmentVO, BaseSecurityDomainIpSegment.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("code", baseSecurityDomainIpSegmentVO.getCode());
        if(StringUtils.isNotEmpty(baseSecurityDomainIpSegmentVO.getStartIp())){
            criteria.andLike("startIp",baseSecurityDomainIpSegmentVO.getStartIp());
        }
        if(StringUtils.isNotEmpty(baseSecurityDomainIpSegmentVO.getEndIp())){
            criteria.andLike("endIp",baseSecurityDomainIpSegmentVO.getEndIp());
        }
        return this.vList(baseSecurityDomainIpSegmentService.findByExample(example));
    }

    /**
     * 删除安全域
     */
    @DeleteMapping
    @ApiOperation(value = "删除安全域，ids传入code")
    @SysRequestLog(description="删除安全域", actionType = ActionType.DELETE)
    public Result delDomain(@RequestBody DeleteQuery deleteQuery) {
        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().andEqualTo("parentCode", deleteQuery.getIds());
        List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainService.findByExample(example);
        if (CollectionUtils.isNotEmpty(baseSecurityDomains)) {
            return this.result(ErrorCode.DOMAIN_HAS_SUB);
        }
        Example exampleIp = new Example(BaseSecurityDomainIpSegment.class);
        exampleIp.createCriteria().andEqualTo("code", deleteQuery.getIds());
        List<BaseSecurityDomainIpSegment> baseOrgIpSegments = baseSecurityDomainIpSegmentService.findByExample(exampleIp);
        if (baseOrgIpSegments != null && baseOrgIpSegments.size() > 0) {
            return this.result(ErrorCode.DOMAIN_HAS_IP);
        }
        Example exampleUserDomain = new Example(UserDomain.class);
        exampleUserDomain.createCriteria().andEqualTo("domainCode", deleteQuery.getIds());
        List<UserDomain> userDomains = userDomainService.findByExample(exampleUserDomain);
        if (CollectionUtils.isNotEmpty(userDomains)) {
            return this.result(ErrorCode.DOMAIN_HAS_USER);
        }

        List<BaseSecurityDomain> baseDomains = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class,"code",deleteQuery.getIds());
        int count = baseSecurityDomainService.deleteDomainByCode(deleteQuery.getIds());
        if (count > 0) {
            baseDomains.forEach(baseSecurityDomain -> {
                this.transferParentCode(baseSecurityDomain);
                SyslogSenderUtils.sendDeleteAndTransferredField(baseSecurityDomain,"删除安全域",transferMap);
            });
            redisService.deleteSecurityDomainResource(deleteQuery.getIds());
            baseSecurityDomainService.cacheDomain();
            baseSecurityDomainService.sendChangeMessage();
            //重置session
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            HttpSession session = request.getSession();
            Map<String, String> domainMap = (Map<String, String>) session.getAttribute(Global.SESSION.DOMAIN);
            for (String code : deleteQuery.getIds().split(",")) {
                List<BaseSecurityDomain> list = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class, "code", code);
                if (CollectionUtils.isNotEmpty(list)) {
                    if (list.get(0) != null) {
                        domainMap.remove(list.get(0).getCode());
                    }
                }
            }
            session.setAttribute(Global.SESSION.DOMAIN, domainMap);
            return this.vData(true);
        }
        return this.vData(false);
    }


    /**
     * 删除区域下的IP段
     */
    @DeleteMapping(value = "/ip")
    @ApiOperation(value = "删除区域下的IP段")
    @SysRequestLog(description="删除区域下的IP段", actionType = ActionType.DELETE)
    public Result delOrgIp(@RequestBody DeleteQuery deleteQuery) {
        List<BaseSecurityDomainIpSegment> list = baseSecurityDomainIpSegmentService.findByids(deleteQuery.getIds());
        int result = baseSecurityDomainIpSegmentService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            list.forEach(p -> {
                this.transferDomainName(p);
                SyslogSenderUtils.sendDeleteSyslog(p,"删除区域下的IP段");
            });
            for(String id : deleteQuery.getIds().split(",")){
                redisService.deleteSecurityDomainIpSegmentResource(id);
            }
            baseSecurityDomainService.cacheDomain();
            baseSecurityDomainService.sendChangeMessage();
            return this.vData(true);
        }
        return this.vData(false);
    }


    /**
     * 根据code获取用户列表
     */
    @ApiOperation(value = "根据code获取用户列表(POST)")
    @PostMapping(value = "user/byCode")
    @SysRequestLog(description="获取用户列表", actionType = ActionType.SELECT)
    public VData<List<com.vrv.vap.admin.model.User>> userByCode(@RequestBody Map<String,Object> map){
        String code = null;
        Object codeObj = map.get("code");
        if(codeObj!=null){
            code = codeObj.toString();
        }
        List<com.vrv.vap.admin.model.User> result = new ArrayList<>();
        List<UserDomain> userDomainList =  userDomainService.findByProperty(UserDomain.class,"domainCode",code);
        List<Integer> userIdList = userDomainList.stream().map(p->p.getUserId()).collect(Collectors.toList());
        if(CollectionUtils.isNotEmpty(userDomainList)){
            Example example = new Example(com.vrv.vap.admin.model.User.class);
            example.createCriteria().andIn("id",userIdList);
            result = userService.findByExample(example);
            result.stream().forEach(p->p.setPassword(""));
        }
        return  this.vData(result);
    }



    /**
     *     根据code获取租户列表
     */
    @ApiOperation(value = "根据code获取租户列表(POST)")
    @PostMapping(value = "tenant/byCode")
    @SysRequestLog(description="获取租户列表", actionType = ActionType.SELECT)
    public VData  tenantByCode(@RequestBody Map<String,Object> mapOrg){
        Map<String,Object> map = new HashedMap();
        mapOrg.keySet().stream().forEach(p->{
            map.put(p,mapOrg.get(p));
        });
        List<Role> roleList = roleService.findByProperty(Role.class,"code",tenantRoleCode);
        if(CollectionUtils.isEmpty(roleList)){
            return this.vData(new Result("-1","租户roleCode配置错误"));
        }
        Integer roleId = roleList.get(0).getId();
        String code = null;
        Object codeObj = map.get("code");
        if(codeObj!=null){
            code = codeObj.toString();
        }
        List<com.vrv.vap.admin.model.User> result =  baseSecurityDomainService.findTenantByCode(roleId,code);
        result.stream().forEach(p->p.setPassword(""));
        return  this.vData(result);
    }


    /**
     * 楚天云，获取租户拥有的安全域
     */
    @ApiOperation(value = "租户拥有的安全域")
    @GetMapping(value = "/ByTenant")
    public VData domainByTenant(){
        List<Role> roleList = roleService.findByProperty(Role.class,"code",tenantRoleCode);
        if(CollectionUtils.isEmpty(roleList)){
            return this.vData(new Result("-1","租户roleCode配置错误"));
        }
        Integer roleId = roleList.get(0).getId();
       List<BaseSecurityDomain> result =  baseSecurityDomainService.domainByTenant(roleId);
        return  this.vData(result);
    }


    /**
     * 获取用户安全域
     * @return
     */
    @ApiOperation(value = "获取用户安全域")
    @GetMapping(value = "/byUser")
    @SysRequestLog(description="获取用户安全域", actionType = ActionType.SELECT)
    public VData userDomain(HttpServletRequest request) {
        return this.vData(queryUserDomian(request));
    }

    /**
     * 获取用户安全域中最小的单位级别
     * @return
     */
    @ApiOperation(value = "获取用户安全域中最小的单位级别")
    @GetMapping(value = "/byUser/minOrghierarchy")
    public VData minUserDomainOrghierarchy (HttpServletRequest request) {
        List<BaseSecurityDomainVO> domainResult = queryUserDomian(request);
        if (CollectionUtils.isNotEmpty(domainResult)) {
            List<BaseSecurityDomainVO> domainList = domainResult.stream().filter(p -> p.getOrghierarchy() != null).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(domainList)) {
                BaseSecurityDomainVO baseSecurityDomainVO = domainList.stream().min(Comparator.comparing(p -> p.getOrghierarchy())).get();
                return this.vData(baseSecurityDomainVO.getOrghierarchy());
            }
        }
        return this.vData(true);
    }

    /**
     * 查询当前用户指定级别的安全域
     * @return
     */
    @ApiOperation(value = "查询当前用户指定级别的安全域")
    @GetMapping(value = "/hierarchy/{orgHierarchy}")
    public VData secruityDomainByOrgHierarchy(HttpServletRequest request,@PathVariable("orgHierarchy") Byte orgHierarchy) {
        List<BaseSecurityDomainVO> domainResult = queryUserDomian(request);
        domainResult =  domainResult.stream().filter(p->orgHierarchy.equals(p.getOrghierarchy())).collect(Collectors.toList());
        return this.vData(domainResult);
    }

    /**
     *  查询指定用户指定级别的安全域
     * @return
     */
    @ApiOperation(value = "查询指定用户指定级别的安全域")
    @PostMapping(value = "/hierarchy")
    public VData secruityDomainByOrgHierarchy(@RequestBody BaseSecurityDomainQuery baseSecurityDomainQuery) {
        Integer userId = baseSecurityDomainQuery.getUserId();
        Byte orgHierarchy = baseSecurityDomainQuery.getOrgHierarchy();
        List<UserDomain> userDomains = userDomainService.findByProperty(UserDomain.class, "userId", userId);
        List<String> domainCodeList = new ArrayList<>();
        List<BaseSecurityDomainVO> domainResult = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(userDomains)) {
            domainCodeList = userDomains.stream().filter(p -> StringUtils.isNotEmpty(p.getDomainCode())).map(p -> p.getDomainCode()).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(domainCodeList)){
           return this.vData(domainResult);
        }
        Example example = new Example(BaseSecurityDomain.class);
        example.createCriteria().andIn("code", domainCodeList);
        List<BaseSecurityDomain> list = baseSecurityDomainService.findByExample(example);
        String ips = "";
        for(BaseSecurityDomain baseSecurityDomain : list){
            BaseSecurityDomainVO baseSecurityDomainVO = new BaseSecurityDomainVO();
            if(baseSecurityDomain!=null){
                BeanUtils.copyProperties(baseSecurityDomain, baseSecurityDomainVO);
//                List<BaseSecurityDomainIpSegment> resultSegment = baseSecurityDomainIpSegmentService.findByProperty(BaseSecurityDomainIpSegment.class, "code", baseSecurityDomain.getCode());
//                if (CollectionUtils.isNotEmpty(resultSegment)) {
//                    Optional<String> ipOption = resultSegment.stream().map(p -> p.getStartIp() + "-" + p.getEndIp()).reduce((a, b) -> a + "," + b);
//                    if (ipOption.isPresent()) {
//                        ips = ipOption.get();
//                    }
//                }
//                baseSecurityDomainVO.setIpRange(ips);
                domainResult.add(baseSecurityDomainVO);
            }
        }
        if(orgHierarchy != null){
            domainResult =  domainResult.stream().filter(p->orgHierarchy.equals(p.getOrghierarchy())).collect(Collectors.toList());
        }
        return  this.vData(domainResult);
    }


    private List<BaseSecurityDomainVO> queryUserDomian(HttpServletRequest request){
        HttpSession session = request.getSession(false);
        List<BaseSecurityDomainVO> domainResult= new ArrayList<>();
        if (session != null) {
            Map<String, String> domain = (Map<String, String>) session.getAttribute(Global.SESSION.DOMAIN);
            if(domain!=null) {
                List<String> codeList = new ArrayList<>();
                for (String key : domain.keySet()) {
                    codeList.add(key);
                }
                if (CollectionUtils.isEmpty(codeList)) {
                    return domainResult;
                }
                Example example = new Example(BaseSecurityDomain.class);
                example.createCriteria().andIn("code", codeList);
                List<BaseSecurityDomain> list = baseSecurityDomainService.findByExample(example);
                for (BaseSecurityDomain baseSecurityDomain : list) {
                    BaseSecurityDomainVO baseSecurityDomainVO = new BaseSecurityDomainVO();
                    if (baseSecurityDomain != null) {
                        BeanUtils.copyProperties(baseSecurityDomain, baseSecurityDomainVO);
                        baseSecurityDomainVO.setIpRange(domain.get(baseSecurityDomain.getCode()));
                        domainResult.add(baseSecurityDomainVO);
                    }
                }
            }
        }
        return domainResult;
    }

    /**
     * 根据IP获取安全域
     */
    @ApiOperation(value = "根据IP获取区域名称(POST)")
    @PostMapping(value = "/byIp")
    public VData<BaseSecurityDomain> areaNameByIp(@RequestBody Map<String,Object> map) {
        String ip = null;
        Object ipObj = map.get("ip");
        if(ipObj!=null){
            ip = ipObj.toString();
        }
        BaseSecurityDomainIpSegment baseSecurityDomainIpSegment =  baseSecurityDomainIpSegmentService.findByIp(ip);
        if (baseSecurityDomainIpSegment == null) {
            return this.vData(ErrorCode.DOMAIN_NAME_NOT_FIND);
        }
        List<BaseSecurityDomain> areas = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class, "code", baseSecurityDomainIpSegment.getCode());
        if (areas != null && areas.size() > 0) {
            return this.vData(areas.get(0));
        }
        return this.vData(ErrorCode.DOMAIN_NAME_NOT_FIND);
    }

    /**
     * 根据安全域code获取ip范围
     */
    @ApiOperation(value = "根据安全域code获取ip范围")
    @PostMapping(path = "ip/byCode")
    public  VData<List<IpRangeVO>> queryAreaIps(@RequestBody IpRangeVO ipRangeVO) {
        Example example = new Example(BaseSecurityDomainIpSegment.class);
        example.createCriteria().andEqualTo("code",ipRangeVO.getCode());
        List<BaseSecurityDomainIpSegment> baseSecurityDomainIpSegments = baseSecurityDomainIpSegmentService.findByExample(example);
        List<IpRangeVO> resultList = new ArrayList<>();
        for(BaseSecurityDomainIpSegment temp :baseSecurityDomainIpSegments){
            IpRangeVO result = new IpRangeVO();
            result.setCode(ipRangeVO.getCode());
            result.setStartIpValue(temp.getStartIpNum());
            result.setEndIpValue(temp.getEndIpNum());
            resultList.add(result);
        }
        return this.vData(resultList);
    }



    /**
     * 增加安全域下的IP段
     */
    @PutMapping(value = "/ip")
    @ApiOperation(value = "增加安全域下的IP段")
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    @SysRequestLog(description="增加安全域下的IP段", actionType = ActionType.ADD)
    public Result addOrgIp(@RequestBody BaseSecurityDomainIpSegment baseSecurityDomainIpSegment) {
        Long start = IPUtils.ip2int(baseSecurityDomainIpSegment.getStartIp());
        Long end = IPUtils.ip2int(baseSecurityDomainIpSegment.getEndIp());
        if (start == 0L || end == 0L) {
            return this.result(ErrorCode.ORG_IP_INVALIDATE);
        }
        if (end < start) {
            return this.result(ErrorCode.ORG_IP_RANGE_VALIDATE);
        }
        List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class,"code",baseSecurityDomainIpSegment.getCode());
        BaseSecurityDomain baseSecurityDomain = null;
        if(baseSecurityDomains.size()>0){
            baseSecurityDomain = baseSecurityDomains.get(0);
        }

        boolean conflict = false;
        if (StringUtils.isNotEmpty(baseSecurityDomain.getParentCode())) {
            Example example = new Example(BaseSecurityDomainIpSegment.class);
            example.createCriteria().andBetween(START_IP_NUM,start,end);
            Example.Criteria criteria2 = example.createCriteria().andBetween(END_IP_NUM,start,end);
            Example.Criteria criteria3 = example.createCriteria().andGreaterThanOrEqualTo(START_IP_NUM,start).andLessThanOrEqualTo(END_IP_NUM,end);
            Example.Criteria criteria4 = example.createCriteria().andLessThanOrEqualTo(START_IP_NUM,start).andGreaterThanOrEqualTo(END_IP_NUM,end);
            example.or(criteria2);
            example.or(criteria3);
            example.or(criteria4);
            List<BaseSecurityDomainIpSegment> lists = baseSecurityDomainIpSegmentService.findByExample(example);
            if(lists.size() > 0){
                conflict = true;
            }
        }

        if (conflict) {
            return this.result(ErrorCode.ORG_IP_IN_SELF);
        }
        baseSecurityDomainIpSegment.setCode(baseSecurityDomain != null ? baseSecurityDomain.getCode() : null);
        baseSecurityDomainIpSegment.setStartIpNum(start);
        baseSecurityDomainIpSegment.setEndIpNum(end);
        int result = baseSecurityDomainIpSegmentService.save(baseSecurityDomainIpSegment);
        if(result>0) {
            baseSecurityDomainIpSegment.setName(baseSecurityDomain != null ? baseSecurityDomain.getDomainName() : "");
            SyslogSenderUtils.sendAddSyslog(baseSecurityDomainIpSegment,"增加安全域下的IP段");
            redisService.updateOrAddSecurityDomainIpSegmentResource(baseSecurityDomainIpSegment);
            baseSecurityDomainService.cacheDomain();
            baseSecurityDomainService.sendChangeMessage();
            return this.vData(baseSecurityDomainIpSegment);
        }
        return this.vData(false);
    }


    @ApiOperation(value = "查询安全域数量")
    @GetMapping(value = "/count")
    public Result calcCount(){
        Integer count =   baseSecurityDomainService.count(new BaseSecurityDomain());
        return this.vData(count);
    }

    /**
     *  省厅:
     *     area-code: 510001
     *     ip-ranges: 10.64.0.0-10.64.255.255,10.71.0.0-10.71.255.255
     */
    @ApiOperation(value = "同步至采集器")
    @GetMapping(value = "/sync")
    public Result synCollector(){
        List<BaseSecurityDomain> resultArea = baseSecurityDomainService.findAll();
        List<BaseSecurityDomainIpSegment> resultSegment = baseSecurityDomainIpSegmentService.findAll();
        if(resultArea == null||resultArea.size() == 0) {
            return this.vData(ErrorCode.AREA_IS_NULL);
        }
        if(resultSegment == null||resultSegment.size() == 0) {
            return this.vData(ErrorCode.SEGMENT_IP_IS_NULL);
        }
        Map<String,Object> configMap = new LinkedHashMap<>();
        for(BaseSecurityDomain baseSecurityDomain : resultArea){
            String guid = baseSecurityDomain.getCode();
            String name = baseSecurityDomain.getDomainName();
            Optional<String> ipOption = resultSegment.stream().filter(p->guid.equals(p.getCode())).map(p->p.getStartIp()+"-"+p.getEndIp()).reduce((a, b) -> a+"," +b);
            String ips = "";
            if(ipOption.isPresent()){
                ips = ipOption.get();
            }
            if(StringUtils.isEmpty(ips)) {
                continue;
            }

            Map<String,Object> ipMap = new LinkedHashMap<>();
            ipMap.put("area-code",guid);
            ipMap.put("ip-ranges",ips);
            configMap.put(name,ipMap);
        }

        try {
            logger.info("====同步至采集器路径地址==="+collectorConfigPath);
            File myPath = new File(collectorConfigPath);
            if ( !myPath.exists()){//
                myPath.mkdirs();
                System.out.println("创建文件夹路径为："+collectorConfigPath);
            }
            String collectorConfigFile = collectorConfigPath+ "/area.yml";
            YmlUtils.addMapIntoYml(new File(collectorConfigFile),configMap);
        } catch (IOException e) {
            logger.error("",e);
        }
        return this.result(true);
    }


    /**
     * 构建树
     *
     */
    private  BaseSecurityDomainTreeVO findChildren(BaseSecurityDomainTreeVO tree, List<BaseSecurityDomainTreeVO> list) {
        if(tree==null || CollectionUtils.isEmpty(list)){
            return  null;
        }
        for (BaseSecurityDomainTreeVO node : list) {
            if (StringUtils.isNotEmpty(node.getParentCode()) && node.getParentCode().equals(tree.getCode())) {
                if (tree.getChildren() == null) {
                    tree.setChildren(new ArrayList<BaseSecurityDomainTreeVO>());
                }
                tree.getChildren().add(findChildren(node, list));
            }
        }
        return tree;
    }

    /**
     * 获取组成安全域树形结构的节点元素
     *
     */
    private List<BaseSecurityDomainTreeVO> getTreeList(Set<String> domainCodeSet){
        List<BaseSecurityDomainTreeVO> result =  new ArrayList<>();
        if(CollectionUtils.isEmpty(domainCodeSet)){
            return  result;
        }
        // 获取组成安全域树形结构的节点集
        for(String domainCode : domainCodeSet) {
            BaseSecurityDomain baseSecurityDomain = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class, "code", domainCode).get(0);
            if (baseSecurityDomain == null) {
                continue;
            }
            //将节点自身加入节点集
            String code = baseSecurityDomain.getCode();
            if(!result.stream().anyMatch(p->code.equals(p.getCode()))){
                result.add(toBaseSecurityDomainTreeVO(baseSecurityDomain));
            }
            if (baseSecurityDomain.getParentCode() != null){
                this.generateTopTree(baseSecurityDomain,result);
            } else {
                List<BaseSecurityDomain> securityDomainList = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class, "parentCode", domainCode);
                if (CollectionUtils.isNotEmpty(securityDomainList)) {
                    for (BaseSecurityDomain securityDomain : securityDomainList) {
                        String upperCode = securityDomain.getCode();
                        boolean isInclude = result.stream().anyMatch(p->upperCode.equals(p.getCode()));
                        if(!isInclude){
                            //将向下搜索的节点加入节点集
                            BaseSecurityDomainTreeVO treeVO = toBaseSecurityDomainTreeVO(securityDomain);
                            treeVO.setIsAuthorized(1);
                            result.add(treeVO);
                        }
                    }
                }
            }
        }
        //为节点打上权限标识
        for(BaseSecurityDomainTreeVO bdTreeVO : result){
            if(domainCodeSet.contains(bdTreeVO.getCode())){
                bdTreeVO.setIsAuthorized(1);
            } else {
                bdTreeVO.setIsAuthorized(0);
            }
        }
        result = result.stream().sorted(Comparator.comparing(BaseSecurityDomainTreeVO::getSubCode)).collect(Collectors.toList());
        return  result;
    }


    private void generateTopTree(BaseSecurityDomain baseSecurityDomain,List<BaseSecurityDomainTreeVO> result) {
        baseSecurityDomain = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class,"code",baseSecurityDomain.getParentCode()).get(0);
        String upperCode = baseSecurityDomain.getCode();
        boolean isInclude = result.stream().anyMatch(p->upperCode.equals(p.getCode()));
        if(!isInclude){
            //将向上搜索的节点加入节点集
            result.add(toBaseSecurityDomainTreeVO(baseSecurityDomain));
        }
        if (baseSecurityDomain.getParentCode() != null) {
            this.generateTopTree(baseSecurityDomain,result);
        }
    }

    private  BaseSecurityDomainTreeVO  toBaseSecurityDomainTreeVO(BaseSecurityDomain baseSecurityDomain){
        BaseSecurityDomainTreeVO baseSecurityDomainTreeVO = new BaseSecurityDomainTreeVO();
        baseSecurityDomainTreeVO.setCode(baseSecurityDomain.getCode());
        baseSecurityDomainTreeVO.setDomainName(baseSecurityDomain.getDomainName());
        baseSecurityDomainTreeVO.setParentCode(baseSecurityDomain.getParentCode());
        baseSecurityDomainTreeVO.setDomainId(baseSecurityDomain.getId());
        baseSecurityDomain.setSort(baseSecurityDomain.getSort());
        baseSecurityDomainTreeVO.setUserList(getUseList(baseSecurityDomain.getCode()));
        baseSecurityDomainTreeVO.setOrghierarchy(baseSecurityDomain.getOrghierarchy());
        baseSecurityDomainTreeVO.setSubCode(baseSecurityDomain.getSubCode());
        return  baseSecurityDomainTreeVO;
    }


    private List<com.vrv.vap.admin.model.User> getUseList(String domainCode){
        List<com.vrv.vap.admin.model.User> result = new ArrayList<>();
        List<Integer> userIdList = new ArrayList<>();
        if(StringUtils.isNotEmpty(domainCode)){
            List<UserDomain> userDomainList =  userDomainService.findByProperty(UserDomain.class,"domainCode",domainCode);
            userIdList = userDomainList.stream().map(p->p.getUserId()).collect(Collectors.toList());
        }
        if(!CollectionUtils.isEmpty(userIdList)){
            Example example = new Example(com.vrv.vap.admin.model.User.class);
            example.createCriteria().andIn("id",userIdList);
            result = userService.findByExample(example);
        }
        return result;
    }

    private void transferParentCode(BaseSecurityDomain baseSecurityDomain) {
        List<BaseSecurityDomain> securityDomainList = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class,"code",baseSecurityDomain.getParentCode());
        if (CollectionUtils.isNotEmpty(securityDomainList)) {
            BaseSecurityDomain securityDomain = securityDomainList.get(0);
            baseSecurityDomain.setParentCode(securityDomain.getDomainName());
        }
    }

    private void transferDomainName(BaseSecurityDomainIpSegment baseSecurityDomainIpSegment) {
        List<BaseSecurityDomain> baseSecurityDomains = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class,"code",baseSecurityDomainIpSegment.getCode());
        if(baseSecurityDomains.size()>0){
            BaseSecurityDomain baseSecurityDomain = baseSecurityDomains.get(0);
            baseSecurityDomainIpSegment.setName(baseSecurityDomain.getDomainName());
        }
    }
}
