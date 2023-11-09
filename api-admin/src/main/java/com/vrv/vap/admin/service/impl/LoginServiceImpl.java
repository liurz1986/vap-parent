package com.vrv.vap.admin.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.LoginTypeEnum;
import com.vrv.vap.admin.common.enums.TypeEnum;
import com.vrv.vap.admin.common.properties.Licence;
import com.vrv.vap.admin.common.properties.Site;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.HttpRequestUtil;
import com.vrv.vap.admin.common.util.IPUtils;
import com.vrv.vap.admin.common.util.UisaDecodeUtil;
import com.vrv.vap.admin.mapper.ServiceApiMapper;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.vo.Menu;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.model.SystemLog;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
@Service
public class LoginServiceImpl implements LoginService {

    private static Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserRoleService userRoleService;
    @Autowired
    private RoleService roleService;
    @Autowired
    private UserDomainService userDomainService;
    @Autowired
    private BaseSecurityDomainIpSegmentService baseSecurityDomainIpSegmentService;
    @Autowired
    private BaseKoalOrgService baseKoalOrgService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RedisService redisService;
    @Autowired
    private BusinessViewService businessViewService;
    @Value("${role.signIn}")
    private String userSignInPath;

    @Autowired
    private Licence licence;

    @Autowired
    private LicenseService licenseService;

    @Autowired
    private StringRedisTemplate redisTpl;

    private static final String HTTP_LICENSE = "httpLicense";

    public static final String LICENSEAUTHENTICATION_KEY = "licenseAuthentication";

    private static final String GITGATEWAY_NAMESPACE = "http://www.jit.com.cn/cinas/ias/ns/saml/saml11/X.509";

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private UserOrgService userOrgService;

    @Value("${pki.randomFrom:1}")
    private String randomFrom;

    @Value("${pki.appId:testApp}")
    private String appId;

    @Value("${pki.accessControl:false}")
    private String accessControl;

    private static final Integer AUTH_STATUS_OK = 2;

    private static final String ROLE_API = "_ROLE_API";

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Autowired
    Cache<String, List<Menu>> menuCache;

    @javax.annotation.Resource
    private ServiceApiMapper serviceApiMapper;

    @Autowired
    private Site siteProperties;


    @Override
    public boolean sessionLogin(HttpSession session, User currentUser, int loginType) {
        com.vrv.vap.common.model.User loginUser = new com.vrv.vap.common.model.User();
        List<UserRole> userRoles = userRoleService.findByProperty(UserRole.class, "userId", currentUser.getId());
        if (userRoles == null || userRoles.size() <= 0) {
            return false;
        }
        loginUser.setAccount(currentUser.getAccount());
        loginUser.setId(currentUser.getId());
        loginUser.setIdcard(currentUser.getIdcard());
        loginUser.setName(currentUser.getName());
        loginUser.setStatus(currentUser.getStatus());
        loginUser.setLoginType(loginType);
        loginUser.setOrgCode(currentUser.getOrgCode());
        loginUser.setOrgName(currentUser.getOrgName());
        loginUser.setProvince(currentUser.getProvince());
        loginUser.setCity(currentUser.getCity());
        List<Integer> roleIds = new ArrayList<>();
        List<String> roleCode = new ArrayList<>();
        List<String> roleName = new ArrayList<>();
        userRoles.stream().forEach(role -> roleIds.add(role.getRoleId()));
        List<Role> roles = roleService.findByids(StringUtils.join(roleIds.toArray(), ","));
        roles.stream().forEach(r -> {
            roleName.add(r.getName());
            if (StringUtils.isNotBlank(r.getCode())) {
                roleCode.add(r.getCode());
            }
        });
        loginUser.setRoleIds(roleIds);
        loginUser.setRoleCode(roleCode);
        loginUser.setRoleName(roleName);
        //用户组织机构
        Set<String> orgCodeSet = new HashSet();
        if (Const.USER_ADMIN.equals(currentUser.getAccount())) {
            List<BaseKoalOrg> allOrg = baseKoalOrgService.findAll();
            allOrg.stream().forEach(p -> orgCodeSet.add(p.getCode()));
        } else {
            Example example = new Example(UserOrg.class);
            example.createCriteria().andEqualTo("userId",currentUser.getId());
            List<UserOrg> orgUserList = userOrgService.findByExample(example);
            if (CollectionUtils.isNotEmpty(orgUserList)) {
                for (UserOrg userOrg : orgUserList) {
                    BaseKoalOrg baseKoalOrg = baseKoalOrgService.findById(userOrg.getOrgId());
                    if (baseKoalOrg != null) {
                        orgCodeSet.add(baseKoalOrg.getCode());
                    }
                }
            }
        }

        List<BaseKoalOrg> baseKoalOrgList = baseKoalOrgService.findByProperty(BaseKoalOrg.class,"code",currentUser.getOrgCode());
        if(baseKoalOrgList!=null && baseKoalOrgList.size()>0){
            session.setAttribute("_SUB_CODE", baseKoalOrgList.get(0).getSubCode());
        }
        Map<String, Integer> userExtends = new HashMap<>();
        userExtends.put("authorityType", currentUser.getAuthorityType());
        userExtends.put("pwdStatus", currentUser.getPwdStatus() == null ? Const.PWD_STATUS_CHANGED : Integer.valueOf(currentUser.getPwdStatus()));
        session.setAttribute(Global.SESSION.USER, loginUser);
        session.setAttribute("_ORG", orgCodeSet);
        Integer userForSignIn = this.cacheRoleResource(loginUser.getRoleIds());
        this.cacheRoleApiList(loginUser.getRoleIds());
        userExtends.put("userForSignIn",userForSignIn);
        session.setAttribute(Global.SESSION.USER_EXTENDS, userExtends);
        return true;
    }

    @Override
    public void sendLog(User currentUser, HttpServletRequest request, TypeEnum type, LoginTypeEnum logType, int status, String methodType, String description) {
        if (currentUser != null) {
            String requestIp = IPUtils.getIpAddress(request);
            SystemLog systemLog = this.createSysLog(type.getCode(), logType.getCode(), status, currentUser, requestIp, methodType, description);
            SyslogSenderUtils.sendSyslogManually(systemLog);
        }
    }

    @Override
    public boolean validateHttpLicense() {
        String licenseKey = HTTP_LICENSE + DateUtil.format(new Date(), DateUtil.DEFAULT_YYYYMMDD);
        String httpLicense = redisTpl.opsForValue().get(licenseKey);
        if (StringUtils.isEmpty(httpLicense)) {
            String httpLicenseUrl = licence.getHttpLicenseUrl();
            httpLicense = httpLicenseUrl.startsWith("https") ? HttpRequestUtil.sendHttpsSslGet(httpLicenseUrl) : HttpRequestUtil.sendHttpsRestGet(httpLicenseUrl);
            if (StringUtils.isEmpty(httpLicense)) {
                logger.info("接口获取的license为空！");
                return false;
            }
        }
        String license = licenseService.getHttpEncriptLicense();
        if (license.equals(httpLicense)) {
            if (StringUtils.isEmpty(redisTpl.opsForValue().get(licenseKey))) {
                licenseService.saveAuthenticationConfig();
            }
            redisTpl.opsForValue().set(licenseKey, license, 1, TimeUnit.DAYS);
        } else {
            logger.info("接口获取的license不匹配，校验未通过！");
            return false;
        }
        return true;
    }

    public boolean validateXgsHttpLicense() {
        String httpLicenseUrl = licence.getHttpLicenseUrl();
        String httpLicense = httpLicenseUrl.startsWith("https") ? HttpRequestUtil.sendHttpsSslGet(httpLicenseUrl) : HttpRequestUtil.sendHttpsRestGet(httpLicenseUrl);
        logger.info("授权信息：" + httpLicense);
        ObjectMapper objectMapper = new ObjectMapper();
        if (StringUtils.isNotEmpty(httpLicense)) {
            try {
                Map licenseMap = objectMapper.readValue(JsonSanitizer.sanitize(httpLicense),Map.class);
                if (licenseMap != null && licenseMap.containsKey("data")) {
                    String dataStr = (String) licenseMap.get("data");
                    Map data = objectMapper.readValue(JsonSanitizer.sanitize(dataStr),Map.class);
                    if (data != null && data.containsKey("authStatus")) {
                        Integer authStatus = (Integer) data.get("authStatus");
                        if (AUTH_STATUS_OK.equals(authStatus)) {
                            licenseService.saveAuthenticationConfig();
                            menuCache.clear();
                            return true;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                logger.error("",e);
            }
        }
        licenseService.forbiddenResource();
        menuCache.clear();
        return false;
    }

    @Override
    public boolean validateImportLicense(HttpSession session) {
        //授权证书通过导入的方式
        if (licence.isEnable() && !systemConfigService.hasLicence()) {
            logger.info("系统并未通过 Licence 认证");
            session.setAttribute(LICENSEAUTHENTICATION_KEY, "false");
            return false;
        }
        return true;
    }

    @Override
    public Map<String, String> loginRedirect(HttpSession session, User currentUser) {
        // 跳转到配置证书页面
        String licenseAuthenticationKey = (String) session.getAttribute(LICENSEAUTHENTICATION_KEY);
        if ("false".equalsIgnoreCase(licenseAuthenticationKey)) {
            Map<String, String> resp = new HashMap<>();
            session.removeAttribute(Global.SESSION.RETURN_URL);
            resp.put("homepage", licence.getValidateUrl());
            return resp;
        }
        // 初始密码需要修改
        if (Const.PWD_STATUS_DEFAULT.equals(currentUser.getPwdStatus())) {
            logger.info("===============第一次登录未修改默认密码");
            Map<String, String> resp = new HashMap<>();
            session.removeAttribute(Global.SESSION.RETURN_URL);
            resp.put("homepage", "/modify_password/");
            return resp;
        }
        // 跳转到业务视图
        Example example = new Example(BusinessView.class);
        example.createCriteria().andEqualTo("userId", currentUser.getId());
        List<BusinessView> businessViewList = businessViewService.findByExample(example);
        if (CollectionUtils.isNotEmpty(businessViewList)) {
            logger.info("===============有业务大屏设置，跳转到业务大屏");
            Map<String, String> resp = new HashMap<>();
            session.removeAttribute(Global.SESSION.RETURN_URL);
            resp.put("homepage", "/navigation/");
            return resp;
        }
        // 跳转到return_url
        Object url = session.getAttribute(Global.SESSION.RETURN_URL);
        if (url != null) {
            Map<String, String> resp = new HashMap<>();
            String urlstr = String.valueOf(url);
            session.removeAttribute(Global.SESSION.RETURN_URL);
            resp.put("homepage", urlstr);
            return resp;
        }
        return null;
    }

    @Override
    public String buildLoginForm(Map<String, String> params) {
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        builder.append("<body>");
        builder.append("<form method=\"post\" id=\"login\">");
        params.keySet().forEach(p->{
            builder.append("\t<input type=\"hidden\" name=\""+p+"\" value=\"" + params.get(p) + "\">");
        });
        builder.append("</form>");
        builder.append("</body>");
        builder.append("<script>");
        builder.append("document.getElementById('login').submit();");
        builder.append("</script>");
        builder.append("</html>");
        return builder.toString();
    }

    private SystemLog createSysLog(int type, int logType, int status, User currentUser, String requestIp, String methodType, String description) {
        // 构建系统日志
        String idCard = currentUser.getIdcard();
        String userName = currentUser.getName();
        String orgName = currentUser.getOrgName();
        List<UserRole> userRoles = userRoleService.findByProperty(UserRole.class, "userId", currentUser.getId());
        String roleName = "";
        if (CollectionUtils.isNotEmpty(userRoles)) {
            Role role = roleService.findById(userRoles.get(0).getRoleId());
            if (role != null) {
                roleName = role.getName();
            }
        }
        SystemLog systemLog = new SystemLog();
        systemLog.setDescription(description);
        systemLog.setRequestTime(new Date());
        systemLog.setLoginType(logType);
        systemLog.setUserId(idCard);
        systemLog.setUserName(userName);
        systemLog.setOrganizationName(orgName);
        systemLog.setRoleName(roleName);
        systemLog.setRequestIp(requestIp);
        systemLog.setResponseResult(status);
        systemLog.setType(type);
        systemLog.setRequestMethod(methodType);
        systemLog.setId(UUID.randomUUID().toString());
        return systemLog;
    }

    private Integer cacheRoleResource(List<Integer> roleIds) {
        Integer userForSignIn = 0;
        for (Integer roleId : roleIds) {
            List<Resource> resources = resourceService.loadResource(roleId);
            if (CollectionUtils.isNotEmpty(resources)) {
                Set<String> resourcesSet = resources.stream().filter(p -> p.getPath() != null && p.getDisabled() != 1).map(p -> p.getPath().split("#")[0]).sorted().collect(Collectors.toSet());
                if (resources.stream().filter(p -> p.getPath() != null && p.getDisabled() != 1).anyMatch(p->userSignInPath.equals(p.getPath()))){
                    userForSignIn = 1;
                }
                if (!redisService.hasRoleResource(roleId + "")) {
                    logger.info("保存权限：" + roleId);
                    redisService.setRoleResource(roleId + "", resourcesSet);
                } else {
                    String resourceString = resourcesSet.stream().collect(Collectors.joining(","));
                    Set<String> cacheSet = redisService.getRoleResource(roleId + "");

                    String cacheResourceString = cacheSet.stream().collect(Collectors.joining(","));
                    if (!resourceString.equals(cacheResourceString)) {
                        logger.info("更新权限：" + roleId);
                        redisService.setRoleResource(roleId + "", resourcesSet);
                    }
                }
            }
        }
        return  userForSignIn;
    }

    /**
     * 缓存角色对应的API列表
     *
     * @param roleIds 角色列表
     */
    private void cacheRoleApiList(List<Integer> roleIds) {
        for (Integer roleId : roleIds) {
            List<Integer> apiList = serviceApiMapper.getApiListByRoleId(roleId);
            if (CollectionUtils.isNotEmpty(apiList)) {
                String key = ROLE_API + roleId + "";
                logger.info("更新角色：" + roleId + "对应API接口列表：" + apiList);
                redisTpl.opsForValue().set(key, gson.toJson(apiList));
            }
        }
    }

    @Override
    public String getUIASDecodeUserInfo(Map encodeMap) {
        String decodeUserInfoString = "";
        if (encodeMap != null && encodeMap.containsKey("appCode") && encodeMap.containsKey("result")) {
            String fileName = "/UIASKey_" + encodeMap.get("appCode") + ".dat";
            InputStream is = this.getClass().getResourceAsStream("/uisaKey" + fileName);
            decodeUserInfoString = UisaDecodeUtil.parseUIAS((String) encodeMap.get("result"), is, "pkcs1");
        }
        return decodeUserInfoString;
    }



    public String generateRandomNumByApplication() throws Exception{
        String num = "1234567890abcdefghijklmnopqrstopqrstuvwxyz";
        int size = 6;
        char[] charArray = num.toCharArray();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < size; i++) {
            sb.append(charArray[((int) (new SecureRandom().nextInt() * 10000.0D) % charArray.length)]);
        }
        return sb.toString();
    }


    public boolean isNotNull(String str) {
        if ((str == null) || (str.trim().equals(""))) {
            return false;
        }
        return true;
    }

    public String getDefaultPWD(){
        Object pwdObj = systemConfigService.getStaticConfig(Const.PWD_DEFAULT_FIELD);
        if(pwdObj != null && StringUtils.isNotEmpty(pwdObj.toString())){
            return pwdObj.toString();
        }
        return siteProperties.getPwdDefault();
    }

}
