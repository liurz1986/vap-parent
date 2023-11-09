package com.vrv.vap.server.zuul.filter;

import cn.hutool.core.collection.IterUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.model.User;
import com.vrv.vap.server.zuul.common.URITYPE;
import com.vrv.vap.server.zuul.common.ZuulConstant;
import com.vrv.vap.server.zuul.filter.condition.VAPConditional;
import com.vrv.vap.server.zuul.model.ServiceApiData;
import com.vrv.vap.server.zuul.utils.CleanUtil;
import com.vrv.vap.server.zuul.utils.LogForgingUtil;
import com.vrv.vap.server.zuul.utils.MD5Utils;
import org.apache.commons.lang3.StringUtils;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StreamUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 等待完成
 * 1. 登录成功后，回退到登陆前的页面
 * 2.
 */

@Component
@Conditional(VAPConditional.class)
public class ResourceFilter extends ZuulFilter {
    public static final String ERR_MESSAGE = "异常处理， " ;
    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 全局静态文件
     */
    @Value("${promission.static}")
    private String[] stat;

    /**
     * 免登录页面
     */
    @Value("${promission.anon}")
    private String[] anon;
    /**
     * 需要登录
     */
    @Value("${promission.auth}")
    private String[] auth;


    /**
     * controller独有权限控制
     */
    @Value("${promission.controller:}")
    private String[] controller;
    /**
     * auditer独有权限控制
     */
    @Value("${promission.auditer:}")
    private String[] auditer;
    /**
     * safeter独有权限控制
     */
    @Value("${promission.safeter:}")
    private String[] safeter;

    /**
     * 登录页面
     */
    @Value("${promission.login}")
    private String[] login;

    /**
     * 无权限提示页面
     */
    @Value("${promission.forbidden}")
    private String forbidden;
    /**
     * 登录后入口页
     */
    @Value("${promission.home}")
    private String home;

    /**
     * 登录后需要url
     */
    @Value("${promission.url-retain}")
    private boolean urlre;


    /**
     * 登录后需要url
     */
    @Value("${server.verify.referer:false}")
    private boolean verifyReferer;

    /**
     * 登录后需要url
     */
    @Value("${server.white.referer:}")
    private String[] whiteReferer;

    @Value("${auth.type:0}")
    private String authType;

    /**
     * 绑定host
     */
    @Value("${server.host}")
    private String[] host;

    @Value("${server.verify.param:false}")
    private boolean verifyParam;

    @Value("${server.white.param:}")
    private String[] whiteParam;

    @Value("${server.verify.token:}")
    private boolean verifyToken;

    @Value("${server.white.token:}")
    private String[] whiteToken;

    /**
     * 是否开启退出登录后回调
     */
    @Value("${returnUrl.enabled:true}")
    private boolean enableReturnUrl;

    @Value("${licence.validateUrl:/hardware/?__hide=true#/configurationWizard}")
    private String validateUrl;

    @Autowired
    private RedisTemplate<String, Set<String>> redisTemplate;

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Autowired
    Cache<String, Set<String>> roleCache;

    @Override
    public String filterType() {
        return "pre" ;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    private static String deFaultLoginPage = "/login/" ;

    private static final String[] AUTH_WHITELIST_PATTERN = {
            "/dnc-web/" ,
            "/api-dncweb/**" ,
            "/hardware/" ,
            "/login/" ,
            "/login" ,
            "/login*/" ,
            "/login*" ,
            "/api-common/system/config" ,
            "/api-common/system/config/sysInfo" ,
            "/api-common/license/**" ,
            "/api-common/user/preferenceConfig" ,
            "/api-common/dictionary" ,
            "/api-common/app" ,
            "/api-common/alarm/item" ,
            "/api-common/resource/**" ,
            "/api-common/user/info/**" ,
            "/api-common/user/logout/" ,
            "/api-common/user/redirect/*" ,
            "/api-common/user/session/unlock" ,
            "/api-common/services/**" ,
            "/api-serversys/syslog/addSysLog"
    };


    private static final String[] MODIFY_PWD_PATTERN = {
            ZuulConstant.MODIFY_PWD_URL,
            "/login/" ,
            "/login" ,
            "/login*/" ,
            "/login*" ,
            "/api-common/user/**"
    };

    // 不登录可访问缓存
    private Map<String, URITYPE> uriMap = new HashMap<>();

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        if (context.getRequest() == null) {
            return true;
        }
        String url = context.getRequest().getRequestURI();
        if (ZuulConstant.CLIENT_AUTH_API.equals(url)) {
            return true;
        }
        String token = context.getRequest().getHeader("Authorization_App_Token");
        if (StringUtils.isNotEmpty(token)) {
            return false;
        }
        return true;
    }

    private void toHome(RequestContext context) {
        sendZuulRedirectUri(context, home);
    }

    private void toLogin(RequestContext context) {
        fetchLoginUrl();
        HttpSession session = context.getRequest().getSession(false);
        if (session != null) {
            session.invalidate();
        }
        sendZuulRedirectUri(context, deFaultLoginPage);
    }

    public void sendZuulRedirectUri(RequestContext context, String uri) {
        try {
            context.setSendZuulResponse(false);
            context.getResponse().sendRedirect(CleanUtil.cleanString(uri));
        } catch (IOException e) {
            logger.error("异常处理，uri: " + LogForgingUtil.validLog(uri) + "," + e.getMessage());
        }
    }

    // 跳转到登录页面
    private void toLogin(String uri, RequestContext context) {
        fetchLoginUrl();
        // 需要判断uri是否是一个资源页面
        String rtnUrl = uri;
        for (String pos : auth) {
            if (uri.startsWith(pos)) {
                rtnUrl = home;
            }
        }
        logger.info("rtnUrl:" + LogForgingUtil.validLog(rtnUrl));
        if (rtnUrl.equals("/") || rtnUrl.equals("/_ehc.html")) {
            rtnUrl = home;
        }
        //如果是无权限的资源，直接转到根目录
        if (!rtnUrl.endsWith("/")) {
            rtnUrl = home;
        } else {
            //丰富resource缓存可做判断
        }
        //开启了回调地址，则转到回调地址
        if (enableReturnUrl) {
            context.getRequest().getSession().setAttribute(Global.SESSION.RETURN_URL, rtnUrl);
        }
        try {
            context.getResponse().sendRedirect(deFaultLoginPage);
        } catch (IOException e) {
            logger.error(ERR_MESSAGE + e.getMessage());
        }
    }

    // 跳转到限制访问页面
    private void toForbidden(RequestContext context) {
        try {
            logger.info("<<<<<<<<<<<<<forbidden>>>>>>>>>>>>>>>" + forbidden);
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(302);
            context.getResponse().sendRedirect(forbidden);
        } catch (Exception e) {
            logger.error(ERR_MESSAGE + e.getMessage());
        }
    }

    // 跳转到限制访问页面
    private void toForbiddenInterFace(RequestContext context) {
        try {
            logger.info("<<<<<<<<<<<<<forbidden>>>>>>>>>>>>>>>" + forbidden);
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(401);
//            context.getResponse().sendRedirect(forbidden);
        } catch (Exception e) {
            logger.error(ERR_MESSAGE + e.getMessage());
        }
    }


    // 跳转到授权页面
    private void toLicense(RequestContext context, String uri) {
        try {
            logger.info("<<<<<<<<<<<<<license>>>>>>>>>>>>>>>");
            context.setSendZuulResponse(false);
            context.getResponse().sendRedirect(uri);
        } catch (IOException e) {
            logger.error(ERR_MESSAGE + e.getMessage());
        }
    }

    private void toModifyPwd(RequestContext context, String uri) {
        try {
            logger.info("<<<<<<<<<<<<<modify_password>>>>>>>>>>>>>>>");
            context.setSendZuulResponse(false);
            context.getResponse().sendRedirect(uri);
        } catch (IOException e) {
            logger.error("异常处理， " + e.getMessage());
        }
    }


    private void fetchLoginUrl() {
        if (stringRedisTemplate.hasKey(ZuulConstant.LOGIN_PAGE) && StringUtils.isNotEmpty(stringRedisTemplate.opsForValue().get(ZuulConstant.LOGIN_PAGE))) {
            String loginPage = stringRedisTemplate.opsForValue().get(ZuulConstant.LOGIN_PAGE);
            logger.info("=========配置登录页面" + loginPage);
            deFaultLoginPage = loginPage;
            return;
        }
        //兼容老版本
        if (login.length > 0 && !login[0].contains("*")) {
            deFaultLoginPage = login[0];
            return;
        }
    }

    public boolean checkResource(String uri, User user) {
        List<Integer> roleIds = user.getRoleIds();
        for (int i = 0, j = roleIds.size(); i < j; i++) {
            String key = Global.SESSION.ROLE_RESOURCE + roleIds.get(i);
            logger.info(String.format("验证权限URI%s" , LogForgingUtil.validLog(uri)));
            if ("/".equals(uri)) {
                return true;
            }
            //判断redis中是否含有此key
            if (redisTemplate.hasKey(key)) {
                Set<String> resources = redisTemplate.opsForValue().get(key);
                roleCache.put(key, resources);
            } else {
                logger.info("<<<<<<<<<<<<<无缓存角色,使用缓存资源");
            }

            if (roleCache.containsKey(key) && roleCache.get(key).contains(uri)) {
                return true;
            }
        }
        return false;
    }


    public void checkUriMap(String uri, String licenseAuthentication) {
        //匹配登录页面
        for (String pattern : login) {
            if (antPathMatcher.match(pattern, uri)) {
                uriMap.put(uri, URITYPE.LOGIN_PAGE);
                return;
            }
        }

        if (uriMap.containsKey(uri)) {
            return;
        }
        //静态资源
        for (String pattern : stat) {
            if (uri.contains(pattern)) {
                uriMap.put(uri, URITYPE.ALL);
                return;
            }
        }
        //无需登录
        for (String pattern : anon) {
            if (antPathMatcher.match(pattern, uri)) {
                uriMap.put(uri, URITYPE.ALL);
                return;
            }
        }
        //需要权限 不需要license认证
        for (String pattern : AUTH_WHITELIST_PATTERN) {
            //如果已经授权，那么走后续的过滤器控制
//            if("true".equalsIgnoreCase(licenseAuthentication)){
//                continue;
//            }
            boolean licenseWithListFlag = antPathMatcher.match(pattern, uri);
            if (!licenseWithListFlag) {
                continue;
            }
            for (String apiPattern : auth) {
                if (uri.startsWith(apiPattern)) {
                    uriMap.put(uri, URITYPE.AUTH);
                    return;
                }
            }
            uriMap.put(uri, URITYPE.AUTH_RESOURCE);
            return;
        }
        //不在白名单的，需要有license
        for (String apiPattern : auth) {
            if (uri.startsWith(apiPattern)) {
                uriMap.put(uri, URITYPE.AUTH_LICENSE);
                return;
            }
        }
        uriMap.put(uri, URITYPE.AUTH_LICENSE_RESOURCE);
    }

    public boolean verifyModifyPwd(String uri, RequestContext context) {
        HttpSession session = context.getRequest().getSession();
        Map userExtends = (Map) session.getAttribute(Global.SESSION.USER_EXTENDS);
        if (userExtends == null || !ZuulConstant.PWD_STATUS_DEFAULT.equals(userExtends.get("pwdStatus"))) {
            return true;
        }
        for (String pattern : MODIFY_PWD_PATTERN) {
            if (antPathMatcher.match(pattern, uri)) {
                return true;
            }
        }
        return false;
    }


    @Override
    public Object run() {
        try {
            RequestContext context = RequestContext.getCurrentContext();
            String uri = context.getRequest().getRequestURI();
            String method = context.getRequest().getMethod();
            HttpSession session = context.getRequest().getSession(false);
            logger.info(String.format("uri is %s" , LogForgingUtil.validLog(uri)));
            logger.info(String.format(">>>>>>>>>>>>>>>session: %s" , session == null ? "unkown" : LogForgingUtil.validLog(session.getId())));
            String licenseAuthentication = session == null ? "" : (String) session.getAttribute(ZuulConstant.LICENSEAUTHENTICATION_KEY);
            boolean headerFlag = checkHeaderReferAndHost(context, uri);
            if (!headerFlag) {
                this.toForbiddenInterFace(context);
                return null;
            }
            this.checkUriMap(uri, licenseAuthentication);
            if (!uriMap.containsKey(uri)) {
                //0.理论上不应该有次类型
                logger.info(String.format("未知类型%s" , LogForgingUtil.validLog(uri)));
                this.toHome(context);
                return null;
            }
            URITYPE type = uriMap.get(uri);

            //0.根据配置的登录页面
            Object u = session == null ? null : session.getAttribute(Global.SESSION.USER);
            if (type == URITYPE.LOGIN_PAGE) {
                if (u != null) {
                    session.invalidate();
                }
                fetchLoginUrl();
                if (deFaultLoginPage.equals(uri)) {
                    return null;
                }
                this.toLogin(context);
                return null;
            }

            //1.免登陆白名单放过，不需要任何权限的页面,所有匿名的请求
            if (type == URITYPE.ALL) {
                return null;
            }


            //2.需要登录验证
            if (type == URITYPE.AUTH || type == URITYPE.AUTH_RESOURCE || type == URITYPE.AUTH_LICENSE || type == URITYPE.AUTH_LICENSE_RESOURCE) {
                if (u == null) {
                    logger.info(">>>>>>>>>>>>>>>未登录");
                    if (type == URITYPE.AUTH_LICENSE_RESOURCE || type == URITYPE.AUTH_RESOURCE) {
                        //2.1 需要登录验证,登录后跳转
                        if (urlre) {
                            this.toLogin(uri, context);
                            return null;
                        }
                    }
                    //2.2 需要登录验证,不需要跳转
                    this.toLogin(context);
                    return null;
                }
                logger.info(">>>>>>>>>>>>>>>登录OK");
                if (type == URITYPE.AUTH || type == URITYPE.AUTH_RESOURCE) {
                    if (type == URITYPE.AUTH) {

                        //校验 防参数修改
                        boolean verifyFlag = checkVerify(context, uri);
                        if (!verifyFlag) {
                            this.toForbiddenInterFace(context);
                            return null;
                        }
                        //校验 防止重复提交
                        boolean postTokenFlag = checkPostToken(context, uri);
                        if (!postTokenFlag) {
                            this.toForbiddenInterFace(context);
                            return null;
                        }
                    }
                    return null;
                }

            }

            //3。需要授权
            if (type == URITYPE.AUTH_LICENSE || type == URITYPE.AUTH_LICENSE_RESOURCE) {
                if (StringUtils.isEmpty(licenseAuthentication) || ZuulConstant.LICENSEAUTHENTICATION_DEFALUT_VALUE.equalsIgnoreCase(licenseAuthentication)) {
                    logger.info(String.format("证书未授权，重定向导入证书页面!!, uri %s" , LogForgingUtil.validLog(uri)));
                    this.toLicense(context, validateUrl);
                    return null;
                }

                //判断需要修改密码的情况
                if (!verifyModifyPwd(uri, context)) {
                    logger.info(">>>密码未修改，需要修改密码");
                    this.toModifyPwd(context, ZuulConstant.MODIFY_PWD_URL);
                    return null;
                }


                if (type == URITYPE.AUTH_LICENSE) {
                    logger.info(">>>>>>>>>>>>>>>授权OK");
                    //需要根据用户账户控制
                    boolean result = checkApiAuth(uri, method, (User) u);
                    if (!result) {
                        logger.info(">>>>>>>>>>>>>>>接口权限校验不通过");
                        this.toForbiddenInterFace(context);
                        return null;
                    }


                    //校验 防参数修改
                    boolean verifyFlag = checkVerify(context, uri);
                    if (!verifyFlag) {
                        this.toForbiddenInterFace(context);
                        return null;
                    }
                    //校验 防止重复提交
                    boolean postTokenFlag = checkPostToken(context, uri);
                    if (!postTokenFlag) {
                        this.toForbiddenInterFace(context);
                        return null;
                    }

                    return null;
                }
            }

            //4.需要角色配置权限验证
            boolean roleHasResource = checkResource(uri, (User) u);
            if (type == URITYPE.AUTH_LICENSE_RESOURCE) {
                if (roleHasResource) {
                    logger.info(">>>>>>>>>>>>>>>权限OK");
                    return null;
                } else {
                    logger.info(">>>>>>>>>>>>>>>权限认证失败");
                }
            }
            //4.1 考虑因为斜杠导致无权限问题
            if (type == URITYPE.AUTH_LICENSE_RESOURCE) {
                if (!uri.endsWith("/")) {
                    logger.info(">>>>>>>>>>>>>>>考虑斜杠兼容");
                    boolean roleHascompatible = checkResource(uri + "/" , (User) u);
                    if (roleHascompatible) {
                        sendZuulRedirectUri(context, uri + "/");
                        return null;
                    }
                }

            }
            //5. 其它情况，跳转至 forbidden
            this.toForbidden(context);
        } catch (Exception ex) {
            logger.error("ResouceFilter中捕获异常信息:{}" , ex);
        }
        return null;
    }

    /**
     * 校验访问接口权限
     *
     * @param uri  接口地址
     * @param user 访问用户
     * @return 是否有权限
     */
    private boolean checkApiAuth(String uri, String method, User user) {
        // 获取垂直越权开关及白名单列表
        String switchJson = stringRedisTemplate.opsForValue().get(ZuulConstant.AUTH_STRATEGY_KEY);
        if (StringUtils.isEmpty(switchJson)) {
            return true;
        }
        Map<String, Object> map = gson.fromJson(switchJson, new TypeToken<Map<String, Object>>() {
        }.getType());
        // 是否开启垂直越权
        boolean strategy_switch = map != null && map.containsKey(ZuulConstant.AUTHORITY_EXCEED_STRATEGY_SWITCH) ? (boolean) map.get(ZuulConstant.AUTHORITY_EXCEED_STRATEGY_SWITCH) : false;
        // 白名单列表
        List<String> whiteList = map != null && map.containsKey(ZuulConstant.AUTHORITY_EXCEED_STRATEGY_WHITELIST) ? (List<String>) map.get(ZuulConstant.AUTHORITY_EXCEED_STRATEGY_WHITELIST) : new ArrayList<>();
        // 是否放行未维护接口
        boolean strategy_unknown = map != null && map.containsKey(ZuulConstant.AUTHORITY_EXCEED_STRATEGY_UNKNOWN) ? (boolean) map.get(ZuulConstant.AUTHORITY_EXCEED_STRATEGY_UNKNOWN) : false;
        // 是否开启策略控制
        if (!strategy_switch) {
            return true;
        }
        // 是否存在白名单中
        boolean contains = whiteList.stream().anyMatch(w -> antPathMatcher.match(w, uri));
        if (contains) {
            return true;
        }
        // 是否存在于接口列表中
        String apiJson = stringRedisTemplate.opsForValue().get(ZuulConstant.API_ALL_KEY);
        List<ServiceApiData> apiAllList = gson.fromJson(apiJson, new TypeToken<List<ServiceApiData>>() {
        }.getType());
        if (IterUtil.isEmpty(apiAllList)) {
            return true;
        }
        Integer apiId = null;
        boolean existUri = false;
        String[] split = uri.split("/");
        String prefix = "" ;
        StringBuilder builder = new StringBuilder();
        if (split.length > 0) {
            for (String s : split) {
                if (StringUtils.isNotEmpty(s)) {
                    if (s.startsWith("api-")) {
                        prefix = s;
                    } else {
                        builder.append("/");
                        builder.append(s);
                    }
                }
            }
        }
        String suffix = builder.toString();
        for (ServiceApiData entry : apiAllList) {
            String pre = entry.getPrefix();
            String path = entry.getPath();
            String apiMethod = entry.getMethod();
            if (pre.contains(prefix) && antPathMatcher.match(path, suffix) && method.equals(apiMethod)) {
                apiId = entry.getId();
                existUri = true;
                break;
            }
        }
        // 如果不是维护中的接口
        if (!existUri) {
            return strategy_unknown;
        }
        List<Integer> roleIds = user.getRoleIds();
        for (Integer roleId : roleIds) {
            String key = ZuulConstant.ROLE_API + roleId + "" ;
            String apiListJson = stringRedisTemplate.opsForValue().get(key);
            List<Integer> roleApiList = gson.fromJson(apiListJson, new TypeToken<List<Integer>>() {
            }.getType());
            if (IterUtil.isNotEmpty(roleApiList)) {
                Integer finalApiId = apiId;
                return roleApiList.stream().anyMatch(r -> r.equals(finalApiId));
            }
        }
        return true;
    }

    private boolean checkHeaderReferAndHost(RequestContext context, String uri) {
        if (StringUtils.isNotEmpty(uri) && uri.startsWith("/api-")) {
            HttpServletRequest httpServletRequest = context.getRequest();
            if (httpServletRequest == null) {
                logger.info("request信息为空");
                return false;
            }
            String hostIp = httpServletRequest.getServerName();
            if (host != null && host.length > 0 && hostIp != null) {
                long count = Arrays.stream(host).filter(p -> hostIp.equals(p)).count();
                if (count == 0) {
                    logger.error(String.format("host检验失败,没有匹配的host互相比对，有防盗链风险(host) %s: hostip:%s" ,  host, hostIp));
                    return false;
                }
            }

            if (verifyReferer) {
                // 从 HTTP 头中取得 Referer 值
                String referer = httpServletRequest.getHeader("Referer");
                String basePathWithPort = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName() + ":" + httpServletRequest.getServerPort();
                String basePath = httpServletRequest.getScheme() + "://" + httpServletRequest.getServerName();
                logger.info("basePath:" + LogForgingUtil.validLog(basePath));
                //防盗链
                if (!checkedWhiteReferer(referer) && referer != null && referer.lastIndexOf(basePath) != 0) {
                    logger.info("referer 检验失败，有防盗链风险(referer):" + LogForgingUtil.validLog(referer));
                    return false;
                }
            }

            String ip = httpServletRequest.getRemoteAddr();
            context.addZuulRequestHeader("client-ip" , ip);
        }
        return true;
    }


    private boolean checkedWhiteReferer(String referer) {

        if (whiteReferer != null && referer != null) {
            long count = Arrays.stream(whiteReferer).filter(p -> referer.startsWith(p)).count();
            if (count > 0) {
                logger.info("在白名单中");
                return true;
            }
        }
        return false;
    }

    private boolean checkPostToken(RequestContext context, String uri) {
        if (checkedWhiteToken(uri)) {
            return true;
        }
        String method = context.getRequest().getMethod();
        if (uri.startsWith("/api-") && verifyToken && !method.toUpperCase(Locale.ENGLISH).equals("GET")) {
            HttpServletRequest httpServletRequest = context.getRequest();
            String postToken = httpServletRequest.getHeader("p-token");
            if (org.apache.commons.lang.StringUtils.isEmpty(postToken)) {
                return true;
            }
            logger.info("p-token：" + LogForgingUtil.validLog(postToken));
            Date d = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String dateNowStr = sdf.format(d);
            String key = "token:" + dateNowStr;
            if (!redisTemplate.hasKey(key)) {
                logger.info("没有key:" + key);
                Set<String> tokenSet = new HashSet<>();
                tokenSet.add(postToken);
                redisTemplate.opsForValue().set(key, tokenSet, 10, TimeUnit.SECONDS);
            } else {
                logger.info("存在key:" + key);
                Set<String> tokenSet = redisTemplate.opsForValue().get(key);
                if (tokenSet.contains(postToken)) {
                    logger.info("重复提交");
                    return false;
                } else {
                    logger.info("设置key:" + key);
                    tokenSet.add(postToken);
                    redisTemplate.opsForValue().set(key, tokenSet, 10, TimeUnit.SECONDS);
                }
            }

        }
        return true;
    }

    private boolean checkVerify(RequestContext context, String uri) {
        String method = context.getRequest().getMethod();
        if (checkedWhiteParam(uri)) {
            return true;
        }

        HttpServletRequest httpServletRequest = context.getRequest();
        String md5Verify = httpServletRequest.getHeader("p-verify");
        if (StringUtils.isEmpty(md5Verify)) {
            return true;
        }
        logger.info("p-verify：" + LogForgingUtil.validLog(md5Verify));
        if (httpServletRequest.getHeader(HttpHeaders.CONTENT_TYPE).contains(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            //文件上传类型直接跳过
            return true;
        }

        if (uri.startsWith("/api-") && verifyParam && org.apache.commons.lang.StringUtils.isNotEmpty(method) && !method.toUpperCase(Locale.ENGLISH).equals("GET")) {
            String body = getParamJson(context, uri);
            String md5Param = MD5Utils.stringToMD5(body);
            logger.info("计算MD5：" + md5Param);
            if (org.apache.commons.lang.StringUtils.isEmpty(body) || org.apache.commons.lang.StringUtils.isEmpty(md5Verify)) {
                logger.info("body信息为空");
                return true;
            }
            if (!md5Param.equals(md5Verify)) {
                return false;
            }
        }
        return true;
    }

    private boolean checkedWhiteParam(String uri) {

        if (whiteParam != null && uri != null) {
            long count = Arrays.stream(whiteParam).filter(p -> uri.startsWith(p)).count();
            if (count > 0) {
                logger.info("param在api白名单中");
                return true;
            }
        }
        return false;
    }

    private boolean checkedWhiteToken(String uri) {

        if (whiteToken != null && uri != null) {
            long count = Arrays.stream(whiteToken).filter(p -> uri.startsWith(p)).count();
            if (count > 0) {
                logger.info("token在api白名单中");
                return true;
            }
        }
        return false;
    }

    private String getParamJson(RequestContext context, String uri) {
        try {
            String method = context.getRequest().getMethod();
            if (uri.startsWith("/api-") && org.apache.commons.lang.StringUtils.isNotEmpty(method) && !method.toUpperCase(Locale.ENGLISH).equals("GET")) {
                InputStream in = context.getRequest().getInputStream();
                byte[] reqBodyBytes = StreamUtils.copyToByteArray(in);
                String body = new String(reqBodyBytes, Charset.forName("UTF-8"));
                context.getRequest().getInputStream().mark(0);
                // 重写上下文的HttpServletRequestWrapper
                context.setRequest(new HttpServletRequestWrapper(context.getRequest()) {
                    @Override
                    public ServletInputStream getInputStream() throws IOException {
                        return new ServletInputStreamWrapper(reqBodyBytes);
                    }

                    @Override
                    public int getContentLength() {
                        return reqBodyBytes.length;
                    }

                    @Override
                    public long getContentLengthLong() {
                        return reqBodyBytes.length;
                    }
                });
                logger.info("请求body中内容:" + body);
                return body;
            }
        } catch (IOException e) {
            logger.error("" , e);
        }
        return "" ;
    }

    class MyServletInputStream extends ServletInputStream {

        private ByteArrayInputStream bis;

        public MyServletInputStream(ByteArrayInputStream bis) {
            this.bis = bis;
        }

        @Override
        public boolean isFinished() {
            return true;
        }

        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setReadListener(ReadListener listener) {
        }

        @Override
        public int read() throws IOException {
            return bis.read();
        }
    }
}
