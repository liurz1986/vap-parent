package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.common.util.JsonUtil;
import com.vrv.vap.admin.common.util.TwoObjectTransformTools;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.Menu;
import com.vrv.vap.admin.vo.SyncGWDataVO;
import com.vrv.vap.common.constant.Global;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2020/5/19
 * @description
 */
@Service
@Transactional
public class SyncGWDataServiceImpl implements SyncGWDataService {

    private static final Logger log = LoggerFactory.getLogger(SyncGWDataServiceImpl.class);

    @Resource
    BaseKoalOrgService baseKoalOrgService;
    @Resource
    BaseSecurityDomainService baseSecurityDomainService;
    @Resource
    BaseSecurityDomainIpSegmentService baseSecurityDomainIpSegmentService;
    @Resource
    ResourceService resourceService;
    @Resource
    RoleService roleService;
    @Resource
    RoleResourceService roleResourceService;
    @Autowired
    private  UserService userService;
    @Autowired
    private  UserDomainService userDomainService;
    @Autowired
    private  UserRoleService userRoleService;
    @Autowired
    SystemConfigService systemConfigService;

    //添加
    private static final String ADD = "add";
    //删除
    private static final String DELETE = "delete";
    //导入
    private static final String IMPORT = "import";
    //编辑
    private static final String EDIT = "edit";
    //组织机构
    private static final String ORG = "org";
    //ip范围
    private static final String IPRANGE = "ipRange";
    //管理员
    private static final String MANAGER = "manager";
    //角色权限
    private static final String ROLE_PRIVILEGE = "role_privilege";
    //管理范围
    private static final String MANAGER_ORGANIZATION = "manager_organization";

    private static final String PUSH_DATA_URL = "/api-common/syncData";

    private static final String WEB_URL = "/api-common/user/pwFreeLogin";

    private static final Integer DATA_SIZE = 10;

    @Value("${vap.cems.authTokenUrl:https://ip:port/CEMS-SERVICE-SOC/comm/generalDataMethod}")
    private String authTokenUrl;

    @Value("${vap.cems.syncDataUrl:https://ip:port/CEMS-SERVICE-SOC/auth/token}")
    private String syncDataUrl;

    @Value("${vap.cems.uname:admin}")
    private String uname;

    @Value("${vap.cems.pcode:vrv@123456}")
    private String pcode;

    @Value("${auth.type:0}")
    private String authType;

    @Value("${vap.cems.home:https://192.168.119.209}")
    private String home;

    private static final String AUTHTYPE_CEMS = "3";

    private static final String APPNAME = "大数据产品线";

    private static final String tokenKey = "GW_CEMS_TOKEN";

    @Autowired
    private StringRedisTemplate redisTpl;

    @Autowired
    Cache<String, List<Menu>> menuCache;


    @Override
    public boolean syncData(SyncGWDataVO syncGWDataVO) {
        String dataType = syncGWDataVO.getDataType();
        String operateType = syncGWDataVO.getOperateType();
        log.info("数据类型dataType：" + dataType);
        log.info("操作类型operateType：" + operateType);
        log.info("数据data：" + syncGWDataVO.getData());
        try {
            if (!IMPORT.equals(operateType)) {
                //组织机构变更
                if (ORG.equals(dataType)) {
                    syncOrgData(syncGWDataVO);
                    syncSecurityDomainData(syncGWDataVO);
                }
                //ip范围
                if (IPRANGE.equals(dataType)) {
                    return syncIpRangeData(syncGWDataVO);
                }
                //管理员变更
                if (MANAGER.equals(dataType)) {
                    return syncManagerChangeData(syncGWDataVO);
                }
                //角色权限变更
                if (ROLE_PRIVILEGE.equals(dataType)) {
                    syncRoleData(syncGWDataVO);
                    syncRoleResourceData(syncGWDataVO);
                }
                //管理范围变更
                if (MANAGER_ORGANIZATION.equals(dataType)) {
                    return syncManagerOrgnizationChangeData(syncGWDataVO);
                }
            } else {
                String token = getAuthToken();
                String appCode = getAppCode(token);
                //导入组织机构
                if (ORG.equals(dataType)) {
                    syncAllOrg(token, appCode);
                }
            }
        } catch (Exception e) {
            log.error("",e);
            //手动回滚事物
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return false;
        }
        return true;
    }

    /**
     * 获取token
     *
     * @return
     */
    public String getAuthToken() {
        String token = "";
        try {
            //获取appCode及token
            Map<String, String> headers = new HashMap<>();
            Map<String, String> param = new HashMap<>();
            param.put("username", uname);
            param.put("password", pcode);
            String requestParam = JSON.toJSONString(param);
            String response = HTTPUtil.POST(authTokenUrl, headers, requestParam);
            log.info("认证信息返回结果：" + LogForgingUtil.validLog(response));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> map = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (map != null && Global.OK.getCode().equals(map.get("code"))) {
                token = (String) map.get("token");
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return token;
    }


    /**
     * 获取appCode
     *
     * @return
     */
    public String getAppCode(String token) {
        String appCode = "";
        try {
            //获取appCode及token
            Map<String, String> param = new HashMap<>();
            param.put("pushDataUrl", home + PUSH_DATA_URL);
            param.put("webUrl", home + WEB_URL);
            param.put("appName", APPNAME);
            param.put("dataType", "pushconfig");
            String requestParam = JSON.toJSONString(param);
            Map<String, String> headers = generateHeaders(token);
            String response = HTTPUtil.POST(syncDataUrl, headers, requestParam);
            log.info("配置信息返回结果：" + LogForgingUtil.validLog(response));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> map = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (map != null && Global.OK.getCode().equals(map.get("code"))) {
                appCode = (String) map.get("appCode");
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return appCode;
    }

    /**
     * 全量同步组织机构数据
     *
     * @param token
     * @param appCode
     */
    public void syncAllOrg(String token, String appCode) {
        //先删除
        baseKoalOrgService.deleteAllOrg();
        baseSecurityDomainService.deleteAllDomain();
        baseSecurityDomainIpSegmentService.deleteAllDomainIp();
        //获取数据总量
        token = this.validateToken(token);
        Map<String, Object> param = new HashMap<>();
        param.put("dataSize", "1");
        param.put("pageNum", "1");
        param.put("dataType", "org");
        param.put("appCode", appCode);
        String requestParam = JSON.toJSONString(param);
        Map<String, String> headers = generateHeaders(token);
        try {
            String response = HTTPUtil.POST(syncDataUrl, headers, requestParam);
            ObjectMapper objectMapper = new ObjectMapper();
            //根据总量获取分页的组织机构
            Map<String, Object> result = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (result != null && Global.OK.getCode().equals(result.get("code"))) {
                Integer total = (Integer) result.get("total");
                if (total > 0) {
                    Integer pageCount = total / DATA_SIZE;
                    if (total % DATA_SIZE != 0) {
                        pageCount ++;
                    }
                    for (int i = 0; i < pageCount; i++) {
                        param.put("dataSize", DATA_SIZE);
                        param.put("pageNum", i + 1);
                        param.put("dataType", "org");
                        String resp = HTTPUtil.POST(syncDataUrl, headers, JSON.toJSONString(param));
                        Map<String, Object> pageResult = objectMapper.readValue(JsonSanitizer.sanitize(resp), Map.class);
                        if (pageResult != null && Global.OK.getCode().equals(result.get("code"))) {
                            List<Map<String, Object>> dataList = (List<Map<String, Object>>) pageResult.get("data");
                            if (CollectionUtils.isNotEmpty(dataList)) {
                                for (Map<String, Object> data : dataList) {
                                    List<Map<String,Object>> datas = new ArrayList<>();
                                    datas.add(data);
                                    String dataStr = objectMapper.writeValueAsString(datas);
                                    SyncGWDataVO syncGWDataVO = new SyncGWDataVO();
                                    syncGWDataVO.setData(dataStr);
                                    syncGWDataVO.setOperateType(ADD);
                                    syncOrgData(syncGWDataVO);
                                    //取消同步11.27
//                                    syncSecurityDomainData(syncGWDataVO);
//                                    syncGWDataVO.setData(objectMapper.writeValueAsString(data.get("ipRange")));
//                                    syncIpRangeData(syncGWDataVO);
                                }
                            }
                        }
                    }
                } else {
                    log.info("组织机构总数为0");
                }
            }
        } catch (Exception e) {
            log.error("",e);
            throw new RuntimeException();
        }
    }

    /**
     * 全量获取角色和权限
     *
     * @param token
     * @param appCode
     */
    public void syncAllRole(String token, String appCode) {
        menuCache.clear();
        //先删除
        roleService.deleteAllRole();
        roleResourceService.deleteAllRoleResource();
        //获取同步数据
        token = this.validateToken(token);
        Map<String, Object> param = new HashMap<>();
        param.put("dataType", "role_privilege");
        param.put("appCode", appCode);
        String requestParam = JSON.toJSONString(param);
        Map<String, String> headers = generateHeaders(token);
        try {
            String response = HTTPUtil.POST(syncDataUrl, headers, requestParam);
            log.info("角色资源返回结果:" + LogForgingUtil.validLog(response));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> result = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (result != null && Global.OK.getCode().equals(result.get("code"))) {
                List<Map<String, Object>> roles = (List<Map<String, Object>>) result.get("data");
                if (CollectionUtils.isNotEmpty(roles)) {
                    for (Map<String, Object> role : roles) {
                        SyncGWDataVO syncGWDataVO = new SyncGWDataVO();
                        List<Map<String,Object>> roleDataList = new ArrayList<>();
                        roleDataList.add(role);
                        String roleData = objectMapper.writeValueAsString(roleDataList);
                        syncGWDataVO.setData(roleData);
                        syncGWDataVO.setOperateType(ADD);
                        syncRoleData(syncGWDataVO);
                        syncRoleResourceData(syncGWDataVO);
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException();
        }
    }

    /**
     * 全量获取用户及关联数据
     * @param token
     * @param appCode
     */
    public void syncAllUser(String token, String appCode) {
        //先删除
        userService.deleteAllUser();
        userRoleService.deleteAllUserRole();
        //获取同步数据
        token = this.validateToken(token);
        Map<String, Object> param = new HashMap<>();
        param.put("dataType", "mgr_role");
        param.put("appCode", appCode);
        String requestParam = JSON.toJSONString(param);
        Map<String, String> headers = generateHeaders(token);
        try {
            String response = HTTPUtil.POST(syncDataUrl, headers, requestParam);
            log.info("用户返回结果:" + LogForgingUtil.validLog(response));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> result = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (result != null && Global.OK.getCode().equals(result.get("code"))) {
                List<Map<String, Object>> users = (List<Map<String, Object>>) result.get("data");
                if (CollectionUtils.isNotEmpty(users)) {
                    for (Map<String, Object> user : users) {
                        SyncGWDataVO syncGWDataVO = new SyncGWDataVO();
                        List<Map<String,Object>> userList = new ArrayList<>();
                        userList.add(user);
                        String userData = objectMapper.writeValueAsString(userList);
                        syncGWDataVO.setData(userData);
                        syncGWDataVO.setOperateType(ADD);
                        syncManagerChangeData(syncGWDataVO);
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException();
        }
    }

    /**
     * 全量获取用户管理范围
     * @param token
     * @param appCode
     */
    public void syncAllUserDomain(String token, String appCode) {
        //先删除
        userDomainService.deleteAllUserDomain();
        //获取同步数据
        token = this.validateToken(token);
        Map<String, Object> param = new HashMap<>();
        param.put("dataType", "manager_organization");
        param.put("appCode", appCode);
        String requestParam = JSON.toJSONString(param);
        Map<String, String> headers = generateHeaders(token);
        try {
            String response = HTTPUtil.POST(syncDataUrl, headers, requestParam);
            log.info("用户返回结果:" + LogForgingUtil.validLog(response));
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> result = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (result != null && Global.OK.getCode().equals(result.get("code"))) {
                List<Map<String, Object>> userDomains = (List<Map<String, Object>>) result.get("data");
                if (CollectionUtils.isNotEmpty(userDomains)) {
                    for (Map<String, Object> userDomain : userDomains) {
                        SyncGWDataVO syncGWDataVO = new SyncGWDataVO();
                        List<Map<String,Object>> userDomainList = new ArrayList<>();
                        userDomainList.add(userDomain);
                        String userData = objectMapper.writeValueAsString(userDomainList);
                        syncGWDataVO.setData(userData);
                        syncGWDataVO.setOperateType(ADD);
                        syncManagerOrgnizationChangeData(syncGWDataVO);
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
            throw new RuntimeException();
        }
    }

    /**
     * 上报资源数据
     *
     * @param token
     * @param appCode
     */
    public void reportResourceInfo(String token, String appCode) {
        //资源数据上报
        token = this.validateToken(token);
        List<Map> resultList = new ArrayList<>();
        List<com.vrv.vap.admin.model.Resource> resourceList = resourceService.findAll();
        if (CollectionUtils.isNotEmpty(resourceList)) {
            for (com.vrv.vap.admin.model.Resource resource : resourceList) {
                Map<String, Object> map = new HashMap<>();
                map.put("resourceId", resource.getUid());
                map.put("resourceName", resource.getName());
                map.put("title", resource.getTitle());
                map.put("type", "system");
                Byte type = resource.getType();
                map.put("mark", type.equals("3") ? "button" : "menu");
                map.put("url", resource.getPath());
                map.put("sort", resource.getSort());
                map.put("tier", type.equals("1") ? "1" : "2");
                map.put("parentId", resource.getPuid());
                resultList.add(map);
            }
            Map<String, Object> param = new HashMap<>();
            param.put("dataType", "resource");
            param.put("data", resultList);
            param.put("appCode", appCode);
            String requestParam = JSON.toJSONString(param);
            Map<String, String> headers = generateHeaders(token);
            try {
                String response = HTTPUtil.POST(syncDataUrl, headers, requestParam);
                log.info("上报资源数据返回结果：" + LogForgingUtil.validLog(response));
            } catch (Exception e) {
                log.error("", e);
                throw new RuntimeException();
            }
        }
    }

    /**
     * 缓存中token为空则重新获取
     * @param token
     * @return
     */
    private String validateToken(String token) {
        if (StringUtils.isEmpty(redisTpl.opsForValue().get(tokenKey))) {
            token = getAuthToken();
            redisTpl.opsForValue().set(tokenKey,token,25, TimeUnit.MINUTES);
        }
        return token;
    }

    /**
     * 构造请求头
     * @param token
     * @return
     */
    private Map<String,String> generateHeaders(String token) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Authorization","Bearer " + token);
        headers.put("Content-Type", "application/json");
        return headers;
    }

    /**
     * 增量同步组织机构数据
     *
     * @param syncGWDataVO
     * @return
     */
    private boolean syncOrgData(SyncGWDataVO syncGWDataVO) {
        String operateType = syncGWDataVO.getOperateType();
        String orgData = syncGWDataVO.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        List<BaseKoalOrg> baseKoalOrgs = new ArrayList<>();
        try {
            List<Map<String, Object>> orgList = objectMapper.readValue(JsonSanitizer.sanitize(orgData), List.class);
            if (CollectionUtils.isNotEmpty(orgList)) {
                for (Map<String, Object> org : orgList) {
                    BaseKoalOrg baseKoalOrg = new BaseKoalOrg();
                    baseKoalOrg.setCode((String) org.get("did"));
                    baseKoalOrg.setParentCode((String) org.get("pdid"));
                    baseKoalOrg.setName((String) org.get("orgName"));
                    baseKoalOrgs.add(baseKoalOrg);
                }
                //新增
                if (ADD.equals(operateType)) {
                    baseKoalOrgService.save(baseKoalOrgs);
                }
                //修改、删除
                if (EDIT.equals(operateType) || DELETE.equals(operateType)) {
                    for (BaseKoalOrg baseKoalOrg : baseKoalOrgs) {
                        List<BaseKoalOrg> koalOrgList = baseKoalOrgService.findByProperty(BaseKoalOrg.class, "code", baseKoalOrg.getCode());
                        if (CollectionUtils.isNotEmpty(koalOrgList)) {
                            BaseKoalOrg koalOrg = koalOrgList.get(0);
                            koalOrg.setParentCode(baseKoalOrg.getParentCode());
                            koalOrg.setName(baseKoalOrg.getName());
                            if (EDIT.equals(operateType)) {
                                baseKoalOrgService.updateSelective(koalOrg);
                            } else {
                                baseKoalOrgService.deleteById(koalOrg.getUuId());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException();
        }
        return true;
    }

    /**
     * 增量同步安全域数据
     *
     * @param syncGWDataVO
     * @return
     */
    private boolean syncSecurityDomainData(SyncGWDataVO syncGWDataVO) {
        String operateType = syncGWDataVO.getOperateType();
        String orgData = syncGWDataVO.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        List<BaseSecurityDomain> baseSecurityDomains = new ArrayList<>();
        try {
            List<Map<String, Object>> orgList = objectMapper.readValue(orgData, List.class);
            if (CollectionUtils.isNotEmpty(orgList)) {
                for (Map<String, Object> org : orgList) {
                    BaseSecurityDomain baseSecurityDomain = new BaseSecurityDomain();
                    baseSecurityDomain.setCode((String) org.get("did"));
                    baseSecurityDomain.setParentCode((String) org.get("pdid"));
                    baseSecurityDomain.setDomainName((String) org.get("orgName"));
                    baseSecurityDomains.add(baseSecurityDomain);
                }
                //新增
                if (ADD.equals(operateType)) {
                    baseSecurityDomainService.save(baseSecurityDomains);
                }
                //修改、删除
                if (EDIT.equals(operateType) || DELETE.equals(operateType)) {
                    for (BaseSecurityDomain securityDomain : baseSecurityDomains) {
                        List<BaseSecurityDomain> securityDomainList = baseSecurityDomainService.findByProperty(BaseSecurityDomain.class, "code", securityDomain.getCode());
                        if (CollectionUtils.isNotEmpty(securityDomainList)) {
                            BaseSecurityDomain baseSecurityDomain = securityDomainList.get(0);
                            baseSecurityDomain.setParentCode(securityDomain.getParentCode());
                            baseSecurityDomain.setDomainName(securityDomain.getDomainName());
                            if (EDIT.equals(operateType)) {
                                baseSecurityDomainService.updateSelective(baseSecurityDomain);
                            } else {
                                baseSecurityDomainService.deleteById(baseSecurityDomain.getId());
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException();
        }
        return true;
    }

    /**
     * 同步ip范围数据
     *
     * @param syncGWDataVO
     * @return
     */
    private boolean syncIpRangeData(SyncGWDataVO syncGWDataVO) {
        String operateType = syncGWDataVO.getOperateType();
        String ipRangeData = syncGWDataVO.getData();
        String orgId = syncGWDataVO.getOrgId();
        ObjectMapper objectMapper = new ObjectMapper();
        List<BaseSecurityDomainIpSegment> ipSegments = new ArrayList<>();
        try {
            List<Map<String, Object>> ipRangeList = objectMapper.readValue(ipRangeData, List.class);
            if (CollectionUtils.isNotEmpty(ipRangeList)) {
                for (Map<String, Object> ipRange : ipRangeList) {
                    BaseSecurityDomainIpSegment ipSegment = new BaseSecurityDomainIpSegment();
                    ipSegment.setCode(orgId);
                    ipSegment.setStartIp((String) ipRange.get("beginIp"));
                    ipSegment.setEndIp((String) ipRange.get("endIp"));
                    ipSegment.setStartIpNum(IPUtils.ip2int((String) ipRange.get("beginIp")));
                    ipSegment.setEndIpNum(IPUtils.ip2int((String) ipRange.get("endIp")));
                    ipSegments.add(ipSegment);
                }
            }
            if (ADD.equals(operateType)) {
                if (CollectionUtils.isNotEmpty(ipSegments)) {
                    baseSecurityDomainIpSegmentService.save(ipSegments);
                }
            }
            if (EDIT.equals(operateType)) {
                List<BaseSecurityDomainIpSegment> ipSegmentList = baseSecurityDomainIpSegmentService.findByProperty(BaseSecurityDomainIpSegment.class, "code", orgId);
                if (CollectionUtils.isNotEmpty(ipSegmentList)) {
                    for (BaseSecurityDomainIpSegment domainIpSegment : ipSegmentList) {
                        baseSecurityDomainIpSegmentService.deleteById(domainIpSegment.getId());
                    }
                }
                if (CollectionUtils.isNotEmpty(ipSegments)) {
                    baseSecurityDomainIpSegmentService.save(ipSegments);
                }
            }
        } catch (IOException e) {
            log.error("",e);
            return false;
        }
        return true;
    }

    /**
     * 增量同步角色数据
     *
     * @param syncGWDataVO
     * @return
     */
    private boolean syncRoleData(SyncGWDataVO syncGWDataVO) {
        menuCache.clear();
        String operateType = syncGWDataVO.getOperateType();
        String roleData = syncGWDataVO.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<Role> roleArrayList = new ArrayList<>();
            List<Map<String, Object>> roleList = objectMapper.readValue(JsonSanitizer.sanitize(roleData), List.class);
            if (CollectionUtils.isNotEmpty(roleList)) {
                for (Map<String, Object> map : roleList) {
                    Role role = new Role();
                    role.setGuid((String) map.get("roleId"));
                    role.setName((String) map.get("roleName"));
                    role.setThreePowers(1);
                    roleArrayList.add(role);
                }
                for (Role role : roleArrayList) {
                    String roleGuid = role.getGuid();
                    List<Role> allRole = roleService.findByProperty(Role.class, "guid", roleGuid);
                    if (CollectionUtils.isNotEmpty(allRole)) {
                        role.setId(allRole.get(0).getId());
                    }
                }
                if (ADD.equals(operateType)) {
                    if (CollectionUtils.isNotEmpty(roleArrayList)) {
                        roleService.save(roleArrayList);
                    }
                }
                if (EDIT.equals(operateType)) {
                    if (CollectionUtils.isNotEmpty(roleArrayList)) {
                        for (Role role : roleArrayList) {
                            roleService.updateSelective(role);
                        }
                    }
                }
                if (DELETE.equals(operateType)) {
                    if (CollectionUtils.isNotEmpty(roleArrayList)) {
                        for (Role role : roleArrayList) {
                            Integer roleId = role.getId();
                            List<RoleResource> resources = roleResourceService.findByProperty(RoleResource.class, "roleId", roleId);
                            if (CollectionUtils.isNotEmpty(resources)) {
                                for (RoleResource roleResource : resources) {
                                    roleResourceService.deleteById(roleResource.getId());
                                }
                            }
                            roleService.deleteById(role.getId());
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
            throw new RuntimeException();
        }
        return true;
    }

    /**
     * 增量同步角色权限数据
     *
     * @param syncGWDataVO
     * @return
     */
    private boolean syncRoleResourceData(SyncGWDataVO syncGWDataVO) {
        String operateType = syncGWDataVO.getOperateType();
        String roleData = syncGWDataVO.getData();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            List<RoleResource> roleResources = new ArrayList<>();
            List<Map<String, Object>> roleList = objectMapper.readValue(JsonSanitizer.sanitize(roleData), List.class);
            List<Integer> roleIdList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(roleList)) {
                for (Map<String, Object> map : roleList) {
                    Integer roleId = 0;
                    String roleGuid = (String) map.get("roleId");
                    List<Role> allRole = roleService.findByProperty(Role.class, "guid", roleGuid);
                    if (CollectionUtils.isNotEmpty(allRole)) {
                        Role ro = allRole.get(0);
                        roleId = ro.getId();
                        roleIdList.add(roleId);
                    }
                    List<Map<String, Object>> privileges = (List<Map<String, Object>>) map.get("privilege");
                    if (CollectionUtils.isNotEmpty(privileges)) {
                        for (Map<String, Object> privilege : privileges) {
                            RoleResource roleResource = new RoleResource();
                            String resourceId = (String) privilege.get("resourceId");
                            List<com.vrv.vap.admin.model.Resource> resources = resourceService.findByProperty(com.vrv.vap.admin.model.Resource.class, "uid", resourceId);
                            if (CollectionUtils.isNotEmpty(resources)) {
                                com.vrv.vap.admin.model.Resource resource = resources.get(0);
                                roleResource.setResourceId(resource.getId());
                                roleResource.setRoleId(roleId);
                                roleResources.add(roleResource);
                            }
                        }
                    }
                }
                if (ADD.equals(operateType)) {
                    if (CollectionUtils.isNotEmpty(roleResources)) {
                        roleResourceService.save(roleResources);
                    }
                }
                if (EDIT.equals(operateType)) {
                    if (CollectionUtils.isNotEmpty(roleIdList)) {
                        Integer roleId = roleIdList.get(0);
                        List<RoleResource> resources = roleResourceService.findByProperty(RoleResource.class, "roleId", roleId);
                        if (CollectionUtils.isNotEmpty(resources)) {
                            for (RoleResource resource : resources) {
                                roleResourceService.deleteById(resource.getId());
                            }
                        }
                        if (CollectionUtils.isNotEmpty(roleResources)) {
                            for (RoleResource roleResource : roleResources) {
                                roleResourceService.save(roleResource);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
            throw new RuntimeException();
        }
        return true;
    }

    /**
     *   增量更新管理员数据
     *
     */
    private boolean syncManagerChangeData(SyncGWDataVO syncGWDataVO) {
        // 获取增量数据，以List形式封装到bean中
        String data = syncGWDataVO.getData();
        String operatorType = syncGWDataVO.getOperateType();
        List<User> userList = new ArrayList<>();
        List<UserRole> userRoleList = new ArrayList<>();
        if (StringUtils.isNotEmpty(data)) {
            List<Map> dataList = JsonUtil.jsonToList(data);
            if (CollectionUtils.isNotEmpty(dataList)) {
                for (Map map : dataList) {
                    User user = new User();
                    String guid = (String) map.get("mgrId");
                    String name = (String) map.get("niceName");
                    String account = (String) map.get("mgrAccount");
                    String password = (String) map.get("pwd");
                    String orgCode = (String) map.get("organizationId");
                    String phone = (String) map.get("phone");
                    String email = (String) map.get("email");
                    Byte state = map.get("state") != null ? Byte.valueOf(map.get("state").toString()) : 0;
                    List<Map> roleMapList = (List<Map>) map.get("role");
                    List<String> roleStringList = new ArrayList<>();
                    if (CollectionUtils.isNotEmpty(roleMapList)) {
                        roleStringList = roleMapList.stream().map(p -> p.get("roleId").toString()).collect(Collectors.toList());
                    }
                    List<Integer> roleIntegerList = new ArrayList<>();
                    String roleId = "";
                    if (CollectionUtils.isNotEmpty(roleStringList)) {
                        for(String roleString : roleStringList){
                            Role role = roleService.findRoleByGuid(roleString);
                            if(role!=null){
                                roleIntegerList.add(role.getId());
                            }
                        }
                        roleId = StringUtils.join(roleIntegerList.toArray(), ",");
                    }
                    //   人员信息
                    user.setGuid(guid);
                    user.setName(name);
                    user.setAccount(account);
                    user.setPassword(password);
                    user.setOrgCode(orgCode);
                    user.setPhone(phone);
                    user.setEmail(email);
                    user.setStatus(state);
                    user.setRoleId(roleId);
                    userList.add(user);
                }
                if (ADD.equals(operatorType)) {
                    userService.insertUserList(userList);
                    //   人员角色关系信息
                    for (User userTemp : userList) {
                        if(StringUtils.isNotEmpty(userTemp.getRoleId())){
                            for (String roleIdTemp : userTemp.getRoleId().split(",")) {
                                UserRole userRoleTemp = new UserRole();
                                userRoleTemp.setRoleId(Integer.valueOf(roleIdTemp));
                                userRoleTemp.setUserId(userTemp.getId());
                                userRoleList.add(userRoleTemp);
                            }
                        }
                    }
                    if (CollectionUtils.isNotEmpty(userRoleList)) {
                        userRoleService.save(userRoleList);
                    }
                }
                if (EDIT.equals(operatorType)) {
                    for (User user : userList) {
                        List<User> list = userService.findByProperty(User.class, "guid", user.getGuid());
                        if(CollectionUtils.isNotEmpty(list)){
                            User userTemp = list.get(0);
                            if(userTemp!=null){
                                TwoObjectTransformTools.copyProperties(user, userTemp, User.class, true, false);
                                userService.updateSelective(userTemp);
                                userService.updateUserRole(userTemp);
                            }
                        }
                    }
                }
                if (DELETE.equals(operatorType)) {
                    List<Integer> userIdDel = new ArrayList<>();
                    for (User user : userList) {
                        List<User> list = userService.findByProperty(User.class, "guid", user.getGuid());
                        if(CollectionUtils.isNotEmpty(list)){
                            User userTemp = list.get(0);
                            if(userTemp!=null) {
                                userIdDel.add(userTemp.getId());
                            }
                        }
                    }
                    String ids = StringUtils.join(userIdDel.toArray(), ",");
                    userService.deleteByIds(ids);
                    userRoleService.deleteByUserIds(ids.split(","));
                    userDomainService.deleteByUserIds(ids.split(","));
                }
            }
        }
        return true;
    }

    /**
     *   增量更新管理范围（安全域）数据
     *
     */
    private boolean syncManagerOrgnizationChangeData(SyncGWDataVO syncGWDataVO) {
        // 获取增量数据，以List形式封装到bean中
        String data = syncGWDataVO.getData();
        String operatorType = syncGWDataVO.getOperateType();
        List<UserDomain> userDomainList = new ArrayList<>();
        if(StringUtils.isNotEmpty(data)){
            List<Map> dataList = JsonUtil.jsonToList(data);
            if(CollectionUtils.isNotEmpty(dataList)){
                for (Map map : dataList){
                    UserDomain userDomain = new UserDomain();
                    String userGuid = (String) map.get("managerId");
                    String domainCode = (String)map.get("organizationId");
                    List<User> list = userService.findByProperty(User.class,"guid",userGuid);
                    if(CollectionUtils.isNotEmpty(list)){
                        User userTemp = list.get(0);
                        Integer userId = userTemp.getId();
                        userDomain.setUserId(userId);
                        userDomain.setDomainCode(domainCode);
                        userDomainList.add(userDomain);
                    }
                }
                if (CollectionUtils.isNotEmpty(userDomainList)) {
                    if(ADD.equals(operatorType)){
                        userDomainService.save(userDomainList);
                    }
                    if(EDIT.equals(operatorType)){
                        Integer userId =  userDomainList.get(0).getUserId();
                        String[] userids = new String[]{ userId +""};
                        userDomainService.deleteByUserIds(userids);
                        userDomainService.save(userDomainList);
                    }
                    if(DELETE.equals(operatorType)){
                        Integer userId =  userDomainList.get(0).getUserId();
                        String[] userids = new String[]{ userId +""};
                        userDomainService.deleteByUserIds(userids);
                    }
                }
            }
        }
        return true;
    }

    @Override
    public void syncAllData() {
        SystemConfig systeminitStatus = systemConfigService.findByConfId("systeminit_status");
        if (AUTHTYPE_CEMS.equals(authType) && !"TRUE".equals(StringUtils.upperCase(systeminitStatus.getConfValue()))) {
            String token = getAuthToken();
            redisTpl.opsForValue().set(tokenKey,token,25, TimeUnit.MINUTES);
            if (StringUtils.isNotEmpty(token)) {
                String appCode = getAppCode(token);
                if (StringUtils.isNotEmpty(appCode)) {
                    //上报资源数据
                    reportResourceInfo(token,appCode);
                    //全量获取角色及权限
                    syncAllRole(token,appCode);
                    //全量同步组织机构
                    syncAllOrg(token,appCode);
                    //全量同步用户
                    syncAllUser(token,appCode);
                    //全量同步用户管理范围
                    syncAllUserDomain(token,appCode);

                } else {
                    log.info("appCode获取失败，请检查");
                }
            } else {
                log.info("token获取失败，请检查");
            }
        }
    }

}
