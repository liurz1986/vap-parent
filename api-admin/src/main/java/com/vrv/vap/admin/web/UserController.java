package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.condition.VapOrZhyCondition;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.enums.LoginTypeEnum;
import com.vrv.vap.admin.common.enums.TypeEnum;
import com.vrv.vap.admin.common.properties.*;
import com.vrv.vap.admin.common.util.*;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.*;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.interfaces.ResultAble;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.crypto.hash.Sha256Hash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = "/user")
@Conditional(VapOrZhyCondition.class)
@Slf4j
public class UserController extends ApiController {

    private static final String UTF_8 = "utf-8";
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    public static final String LICENSEAUTHENTICATION_KEY = "licenseAuthentication";

    @Autowired
    private UserService userService;

    @Autowired
    private AppService appService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserPageService userPageService;

    @Autowired
    private UserPreferenceService userPreferenceService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private Site siteProperties;

    @Autowired
    private CaptchaProperties captchaProperties;

    @Autowired
    private GuoWangProperties guoWangProperties;

    @Autowired
    private BaseKoalOrgService baseKoalOrgService;

    @Autowired
    private Licence licence;

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private UserPreferenceConfigService userPreferenceConfigService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private AuthLoginFieldService authLoginFieldService;
    @Autowired
    private LoginService loginService;

    @Autowired
    private UserUkeyService userUkeyService;

    @Autowired
    private RegisterProperties registerProperties;

    @Autowired
    private UserTokenService userTokenService;

    @Autowired
    private UserOrgService userOrgService;

    @Autowired
    private BasePersonZjgService basePersonZjgService;

    @Value("pkiUrl")
    private String pkiUrl;

    @Value("${vap.cty.ctyToVap}")
    private String ctyToVap;

    @Value("${vap.cty.token}")
    private String token;

    @Value("${vap.cty.userUrl}")
    private String userUrl;

    @Value("${vap.cty.userUrl_zh}")
    private String userZhUrl;

    @Value("${vap.cty.monitor_roleCode}")
    private String  monitorRoleCode;


    @Value("${vap.cty.organizationUrl}")
    private String organizationUrl;

    @Value("${licence.httpLicenseUrl}")
    private String httpLicenseUrl;

    @Value("${role.signIn}")
    private String userSignInPath;

    @Value("${vap.xinjiang.thirdPlatformLogin:http://127.0.0.1:8080/uias/uias/isAgainAuth4Client.do}")
    private String thirdPlatformLoginUrl;

    @Value("${vap.xinjiang.thirdPlatformLogOut:http://127.0.0.1:9090/uias/uias/shotOff}")
    private String thirdPlatformLogOutUrl;

    @Value("${vap.xinjiang.tokenToUser:http://127.0.0.1:8080/uias/unity/user_info}")
    private String tokenToUserUrl;

    @Value("${vap.xinjiang.codeToToken:http://127.0.0.1:8080/uias/oauth/token}")
    private String codeToTokenUrl;

    @Value("${vap.xinjiang.client_id:aaa}")
    private String clientId;

    @Value("${vap.xinjiang.client_secret:bbb}")
    private String clientSecret;

    @Value("${vap.xinjiang.pkiLogin_enable:false}")
    private String pkiLoginEnable;

    @Value("${vap.xinjiang.pkiLogin_response_type:code}")
    private String responseType;

    @Value("${vap.xinjiang.pkiLogin_scope:read,write}")
    private String scope;

    @Value("${vap.pushUrl:https://127.0.0.1:8780/push/user}")
    private String pushUrl;

    //新疆pki登陆获取token接口回调地址
    private static final String UISA_LOGIN_CALLBACK_ADDR = "/api-common/user/uiasLogin/callback";

    private static final String AUTHTYPE_CEMS = "3";

    @Value("${auth.type:0}")
    private String authType;

    @Value("${vap.cems.checkTokenUrl:https://192.168.118.112:8443/CEMS/checkToken}")
    private String checkTokenUrl;


    private static final String HTTP_LICENSE = "httpLicense";

    private static final String SESSION_USER_LOCK_STATUS = "__lock_status";
    private static final String SESSION_USER_LOCK_PAGE = "__lock_page";

    @Autowired
    private StringRedisTemplate redisTpl;

    private final static Pattern REGEXP_IDCARD = Pattern.compile("(\\d{15}|\\d{17}(\\d|X|x)),");

    public static final Pattern REGEX_NAMW = Pattern.compile("([\\u4e00-\\u9fa5]{2,4}) ");

    //吉大正元pki登录地址
    @Value("${pki.authURL:http://10.64.28.146:6180/MessageService}")
    private String pkiAuthURL;

    private static final ScheduledExecutorService watcher = Executors.newSingleThreadScheduledExecutor();

    private static final Map<String, Integer> status = new HashMap();

    private static boolean isWatching = false;

    @Value("${licence.httpLicenseModule}")
    private String httpLicenseModule;

    private static final String CEMS_MODULE = "CEMS";

    private static final String XGS_MODULE = "XGS";

    @Value("${vap.xc.account:secaudit}")
    private String xcAccount;

    private static Map<String, Object> transferMap = new HashMap<>();
    static {
        transferMap.put("status", "{\"0\":\"启用\", \"1\":\"禁用\"}");
    }

    public void startWatcher() {
        if (isWatching) {
            return;
        }
        isWatching = true;
        log.info("启动一个监听器,每天24:00清除不存在用户登录失败状态");
        long oneDay = 24 * 60 * 60 * 1000;
        long initDelay = DateUtil.getTimeMillis("24:00:00") - System.currentTimeMillis();
        initDelay = initDelay > 0 ? initDelay : oneDay + initDelay;
        watcher.scheduleAtFixedRate(() -> {
            Map<String, Integer> tmp = new HashMap<>(status);
            tmp.forEach((k, v) ->
                status.remove(k)
            );
        }, initDelay, oneDay, TimeUnit.MILLISECONDS);
    }

    /**
     * 跳转到应用（SSO）
     */
    @ApiOperation(value = "跳转到应用（SSO）")
    @GetMapping(value = "/redirect/{appid}")
    public void redirect(HttpServletRequest request, HttpServletResponse response, @PathVariable("appid") Integer appid)
            throws Exception {
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) request.getSession()
                .getAttribute(Global.SESSION.USER);
        App app = appService.findById(appid);
        if (app.getType() == 1) {
            throw new Exception("父级应用不能直接被访问");
        }
        try {
            String redirect = app.getUrl();
            if (redirect.startsWith("/")) {
                response.sendRedirect(app.getUrl());
            } else if (redirect.startsWith("http://") || app.getUrl().startsWith("https://")) {
                String token = redisService.genToken(user, appid);
                if (redirect.indexOf("?") <= 0) {
                    redirect = redirect + "?token=" + URLEncoder.encode(token, UTF_8);
                } else {
                    redirect = redirect + "&token=" + URLEncoder.encode(token, UTF_8);
                }
                response.sendRedirect(redirect);
            }
        } catch (IOException e) {
            logger.error("", e);
        }
    }

    @Ignore
    @ApiOperation(value = "用户退出登录")
    @GetMapping(value = "/logout")
    public Result logout(HttpServletRequest request) {
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        User currentUser = new User();
        try {
            BeanUtils.copyProperties(user, currentUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String sessionId = (String) request.getSession().getAttribute("UIASSessionId");
        request.getSession().invalidate();
        if (StringUtils.isNotEmpty(sessionId)) {
            String paramFormat = "sessionId=%s&jsoncallback=?";
            paramFormat = String.format(paramFormat, sessionId);
            String result = HttpRequestUtil.sendGet(thirdPlatformLogOutUrl, paramFormat);
            logger.info("退出参数：" + paramFormat + "退出结果:" + result);
            Map map = StringUtils.isEmpty(result) ? null : jsonToMap(result);
            if (StringUtils.isEmpty(result) || (map != null && !(boolean) map.get("success"))) {
                loginService.sendLog(currentUser, request, TypeEnum.SIGN_OUT, LoginTypeEnum.CERTIFICATE, 1, "POST", "退出登录");
                return this.result(ErrorCode.UIAS_LOGOUT_FAIL);
            }
        }
        //发送退出登录日志
        loginService.sendLog(currentUser, request, TypeEnum.SIGN_OUT, LoginTypeEnum.lOGIN, 1, "POST", "退出登录");
        return Global.OK;
    }

    @PutMapping(value = "/theme")
    public Result theme(@RequestBody Map<String, String> param, HttpServletResponse res) {
        if (param.containsKey("theme")) {
            Cookie cookie = new Cookie("__theme", CleanUtil.cleanString(param.get("theme")));
//            cookie.setHttpOnly(false);
            cookie.setSecure(false);
            //cookie.setPath("/");
            cookie.setPath("/MyForum");
            res.addCookie(cookie);
        }
        return Global.OK;
    }

    /**
     * SSO验证接口
     */
    @ApiOperation(value = "SSO验证接口")
    @GetMapping(value = "/sso")
    @ApiImplicitParam(name = "token", value = "票据", required = true, paramType = "form")
    public Result ssoLogin(HttpServletRequest request) {
        String token = request.getParameter("token");
        // try {
        // token = URLDecoder.decode(token, "utf-8");
        // } catch (UnsupportedEncodingException e) {
        // token = URLDecoder.decode(token);
        // }
        Object result = redisService.validateToken(token);
        if (result instanceof com.vrv.vap.common.model.User) {
            return this.vData(result);
        }
        return this.result((ErrorCode) result);
    }




    /**
     * 锁定会话
     */
    @ApiOperation(value = "锁定会话接口")
    @PostMapping(value = "/session/unlock")
    public Result unlockSession(HttpServletRequest request, HttpServletResponse response,@RequestBody  UserLoginVo userLoginVo) throws Exception {
        if (StringUtils.isEmpty(userLoginVo.getPpp())) {
            return this.result(ErrorCode.USER_PASS_EMPTY);
        }
        com.vrv.vap.common.model.User sessinUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        if (sessinUser == null) {
            return new Result("-1", "未登录");
        }
        User user = new User();
        user.setAccount(sessinUser.getAccount()); //当前只通过用户名查找
        User currentUser = userService.findOneUser(user);
        if (currentUser == null) {
            return new Result("-1", "用户信息不存在");
        }
        if (!userLoginVo.getPpp().equals(currentUser.getPassword())) {
            String pwd = null;
            try {
                pwd = EncryptUtil.decodeBase65(userLoginVo.getPpp());
            } catch (Exception e) {
                logger.error("",e);
                if ("TimeOut".equals(e.getMessage())) {
                    return this.result(ErrorCode.USER_PASS_TIME_DIFF);
                }
                e.printStackTrace();
            }
            boolean pwdFlag = false;
            if (StringUtils.isNotEmpty(pwd) && StringUtils.isNotEmpty(currentUser.getSalt())) {
                String shaPwd = new Sha256Hash(pwd, currentUser.getSalt()).toString();
                if (currentUser.getPassword().equals(shaPwd)) {
                    pwdFlag = true;
                }
            }
            if (!pwdFlag) {
                return new Result("-1", "用户密码错误");
            }
        }
        return Global.OK;
    }



    /**
     * 根据用户ID获取用户信息
     */
    @ApiOperation(value = "根据用户ID获取用户信息")
    @GetMapping(value = "{userId:[\\d]+}")
    public Result getUser(@PathVariable int userId) {
        User user = userService.findById(userId);
        if(user == null){
            return this.result(ErrorCode.USER_NOT_EXIST);
        }
        user.setPassword("");
        user.setIdcard("");
        return this.vData(user);
    }

    /**
     *
     *  楚天云,查询非内置用户，非监管单位用户。
     * @param
     * @return
     */
    @ApiOperation(value = "查询非内置用户，非监管单位用户")
    @GetMapping(value = "/notBuildIn")
    public Result getUserNotBuildIn() {
        List<User> users = userService.getUserNotBuildIn(monitorRoleCode);
        users.stream().forEach(p->p.setPassword(""));
        return this.vData(users);
    }


    /**
     * 根据角色ID获取用户信息
     *
     * @param roleCode
     * @return
     */
    @ApiOperation(value = "根据角色编码获取用户信息")
    @RequestMapping(method = RequestMethod.GET,value = "/roleCode/{roleCode}")
    public Result getUserByRoleCode(@PathVariable String roleCode) {
        Role param = new Role();
        param.setCode(roleCode);
        Role role = roleService.findOne(param);
        List<User> result = new ArrayList<>();
        if (role != null) {
            List<UserRole> userRoleList = userRoleService.findByProperty(UserRole.class,"roleId",role.getId());
            List<Integer> userIdList = userRoleList.stream().map(p->p.getUserId()).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(userRoleList)){
                Example example = new Example(User.class);
                example.createCriteria().andIn("id",userIdList);
                result = userService.findByExample(example);
                result.stream().forEach(p->p.setPassword(""));
            }
        }
        return this.vData(result);
    }


    /**
     * 根据角色code和安全域code，获取用户信息
     * @param
     * @return
     */
    @ApiOperation(value = "根据roleCode和domainCode，获取用户信息")
    @PostMapping(value = "/byRoleAndDomain")
    public Result queryUser(@RequestBody  UserQuery userQuery){
        String domainCode = userQuery.getDomainCode();
        String roleCode = userQuery.getRoleCode();
        Role role = null;
        if(StringUtils.isNotEmpty(roleCode)){
            Role param = new Role();
            param.setCode(roleCode);
            role = roleService.findOne(param);
        }
        List<User> result = new ArrayList<>();
        List<Integer> userIdListForRole = new ArrayList<>();
        List<Integer> userIdListForDomain = new ArrayList<>();
        List<Integer> userIdList = new ArrayList<>();
        if (role != null) {
            List<UserRole> userRoleList = userRoleService.findByProperty(UserRole.class,"roleId",role.getId());
            userIdListForRole = userRoleList.stream().map(p->p.getUserId()).collect(Collectors.toList());
        }
        if(StringUtils.isNotEmpty(domainCode)){
            List<UserDomain> userDomainList =  userDomainService.findByProperty(UserDomain.class,"domainCode",domainCode);
            userIdListForDomain = userDomainList.stream().map(p->p.getUserId()).collect(Collectors.toList());
        }
        if(CollectionUtils.isEmpty(userIdListForRole)){
            userIdList = userIdListForDomain;
        }
        if(CollectionUtils.isEmpty(userIdListForDomain)){
            userIdList = userIdListForRole;
        }
        if(CollectionUtils.isNotEmpty(userIdListForDomain) && CollectionUtils.isNotEmpty(userIdListForRole)){
            //  两者取交集
            final  List<Integer> userIdListForDomains = userIdListForDomain;
            userIdList = userIdListForRole.stream().filter(p -> userIdListForDomains.contains(p)).collect(Collectors.toList());
        }
        if(!CollectionUtils.isEmpty(userIdList)){
            Example example = new Example(User.class);
            example.createCriteria().andIn("id",userIdList);
            result = userService.findByExample(example);
        }
        else {
            result = userService.findAll();
        }
        result.stream().forEach(p->p.setPassword(""));
        return this.vData(result);
    }

    @ApiOperation(value = "根据角色或用户名，获取用户信息")
    @PostMapping(value = "/byRoleOrPerson")
    public VList<User> queryUserByRoleOrPerson(@RequestBody RolePersonQuery query) {
        return this.vList(userService.queryUserByRoleOrPerson(query));
    }


    /**
     * 获取当前登录用户信息
     */
    @ApiOperation(value = "获取当前登录用户信息(session信息)")
    @GetMapping(value = "/info")
    public VData<com.vrv.vap.common.model.User> userInfo(HttpServletRequest request) {
        return this.vData((com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER));
    }


    /**
     * 获取当前登录用户信息
     */
    @ApiOperation(value = "获取当前登录用户信息（原始属性值)")
    @GetMapping(value = "/info/db")
    public VData<User> userDBInfo(HttpServletRequest request) {
        com.vrv.vap.common.model.User sessionUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        User user=  userService.findById(sessionUser.getId());
        return this.vData(user);
    }

    /**
     * 校验用户是否存在
     */
    @ApiOperation(value = "校验用户是否存在")
    @PostMapping(value = "/check")
    public Result checkName(@RequestBody User user) {
        if (StringUtils.isNotEmpty(user.getAccount())) {
            User param = new User();
            param.setAccount(user.getAccount());

            User result = userService.findOneUser(param);
            if (result != null) {
                return this.result(ErrorCode.HAS_SAME_ACCOUNT);
            }
        } else if (StringUtils.isNotEmpty(user.getName())) {
            User param = new User();
            param.setName(user.getName());
            User result = userService.findOneUser(param);
            if (result != null) {
                return this.result(ErrorCode.HAS_SAME_NAME);
            }
        }
        return Global.OK;
    }


    /**
     * 重置用户密码
     */
    @ApiOperation(value = "重置用户密码", hidden = false)
    @PostMapping(value = "/reset/pwd")
    public Result resetPwd(@RequestBody User user) {
        if (user.getId()>0) {
            User result = userService.findById(user.getId());
            if(result!=null) {
                result.setPassword(loginService.getDefaultPWD());
                result.setPwdStatus(Const.PWD_STATUS_DEFAULT);
                userService.update(result);
            }
        }
        return Global.OK;
    }

    /**
     * 获取所有用户
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取所有用户")
    @GetMapping
    public VData<List<User>> getAllUser(HttpServletRequest request) {
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("status", Const.USER_STATUS_DEL);
        example.and(criteria);
        return this.vData(userService.findByExample(example));
    }

    @ApiOperation(value = "获取所有用户(保密员权限)")
    @GetMapping(value = "/permission")
    @SysRequestLog(description = "获取所有用户（保密员权限）", actionType = ActionType.SELECT)
    public VData<List<User>> getAllPermissionUser(HttpServletRequest request) {
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        User currentUser = userService.findById(loginUser.getId());
        // 保密主管获取业务主管和运维主管下的用户
        List<User> userList = userService.getBusinessAndOperationUser(currentUser.getRoleId(),null);
        if (CollectionUtils.isNotEmpty(userList)) {
            return this.vData(userList);
        }
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("status", Const.USER_STATUS_DEL);
        Short confEnable = systemConfigService.getThreePowerEnable();
        if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(loginUser.getAccount())) {
            criteria.andEqualTo("creator",currentUser.getId());
        }
        example.and(criteria);
        return this.vData(userService.findByExample(example));
    }

    @ApiOperation(value = "获取处理有关的权限，策略里面和督促里面不一样，策略里面需要显示保密主管，事件处置的发起人改成了保密住惯了，但是督促里面是不需要的显示的")
    @GetMapping(value = "/dealPermission")
    @SysRequestLog(description = "获取所有用户（保密员权限）", actionType = ActionType.SELECT)
    public VData<List<User>> getAllNewPermissionUser(HttpServletRequest request,@RequestParam String dealType) {
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        User currentUser = userService.findById(loginUser.getId());
        // 保密主管获取业务主管和运维主管下的用户
        List<User> userList = userService.getBusinessAndOperationUser(currentUser.getRoleId(),dealType);
        if (CollectionUtils.isNotEmpty(userList)) {
            return this.vData(userList);
        }
        Example example = new Example(User.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andNotEqualTo("status", Const.USER_STATUS_DEL);
        Short confEnable = systemConfigService.getThreePowerEnable();
        if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(loginUser.getAccount())) {
            criteria.andEqualTo("creator",currentUser.getId());
        }
        example.and(criteria);
        return this.vData(userService.findByExample(example));
    }



    /**
     * 获取需要签到的用户
     *
     * @param
     * @return
     */
    @ApiOperation(value = "获取需要签到的用户,传入path值")
    @GetMapping(value = "/byRoleRemark/{path}")
    public VData<List<User>> getAllUser( @PathVariable String path ){
        List<Resource> resList = resourceService.findByProperty(Resource.class,"path",path);
        if(CollectionUtils.isEmpty(resList)){
            return this.vData(new ArrayList<>());
        }
        Resource res = resList.get(0);
        Integer resourceId = res.getId();
        List<RoleResource> roleResourceList = roleResourceService.findByProperty(RoleResource.class, "resourceId", resourceId);
        List<Integer> roleIdList = roleResourceList.stream().map(p -> p.getRoleId()).distinct().collect(Collectors.toList());
        Example exampleUserRole = new Example(UserRole.class);
        exampleUserRole.createCriteria().andIn("roleId", roleIdList);
        List<UserRole> userRoleList = userRoleService.findByExample(exampleUserRole);
        List<Integer> userIdList = userRoleList.stream().map(p -> p.getUserId()).distinct().collect(Collectors.toList());
        Example exampleUser = new Example(User.class);
        exampleUser.createCriteria().andIn("id", userIdList);
        List<User> userList = userService.findByExample(exampleUser);
        userList.stream().forEach(p->p.setPassword(""));
        return this.vData(userList);
    }

    /**
     * 查询用户列表
     */
    @ApiOperation(value = "查询用户列表")
    @SysRequestLog(description = "查询用户列表", actionType = ActionType.SELECT)
    @PostMapping
    public VList<User> queryUser(HttpServletRequest request, @RequestBody UserQuery userQueryOrg) {
        SyslogSenderUtils.sendSelectSyslog();
        UserQuery userQuery = new UserQuery();
        BeanUtils.copyProperties(userQueryOrg,userQuery);
        // 支持 CODE查询
        if (!StringUtils.isBlank(userQuery.getRoleCode())) {
            Role param = new Role();
            param.setCode(userQuery.getRoleCode());
            Role result = roleService.findOne(param);
            if (result == null) {
                return this.vList(new ArrayList<>(), 0);
            }
            userQuery.setRoleId(String.valueOf(result.getId()));
            userQuery.setRoleCode(null);
        }
        // 外部接口调用时，默认排序
        List<User> users;
        // 组织机构名称与编码排序时，均按照编码排序
        if (StringUtils.isNotEmpty(userQuery.getOrder_())){
            String order_ = CommonUtil.camelToUnderLine(userQuery.getOrder_());
            if ("orgName".equals(userQuery.getOrder_())) {
                userQuery.setOrder_("org_code");
            }
            userQuery.setOrder_(order_);
        }
        else if (StringUtils.isBlank(userQuery.getOrder_()) || StringUtils.isBlank(userQuery.getBy_())) {
            userQuery.setOrder_("id");
            userQuery.setBy_("desc");
        }
        // 三权开启时，用户只能看见自己创建的用户（admin除外）
        Object obj = request.getSession().getAttribute(Global.SESSION.USER);
        if (obj != null) {
            com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) obj;
            Short confEnable = systemConfigService.getThreePowerEnable();
            if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(loginUser.getAccount())) {
                userQuery.setCreator(loginUser.getId());
            }
        }
        if (StringUtils.isNotEmpty(userQuery.getOrgCode())) {
            String orgCode = userQuery.getOrgCode();
            logger.info("获取机构的层级维护代码");
           List<BaseKoalOrg> baseKoalOrgList = baseKoalOrgService.findByProperty(BaseKoalOrg.class,"code",orgCode);
           if(CollectionUtils.isEmpty(baseKoalOrgList)){
               return this.vList(new ArrayList<>(), 0);
           }
           String subcode = baseKoalOrgList.get(0).getSubCode();
           if(StringUtils.isEmpty(subcode)){
               logger.info("该机构的层级维护代码为空");
               return this.vList(new ArrayList<>(), 0);
           }
           userQuery.setSubCode(subcode);
        }
        if (StringUtils.isNotEmpty(userQuery.getRoleId())) {
            List roleIds = Arrays.asList(userQuery.getRoleId().split(","));
            userQuery.setRoleIds(roleIds);
        }
        users = baseKoalOrgService.queryUsers(userQuery);
        long count = baseKoalOrgService.queryUsersCount(userQuery);
        List<BasePersonZjg> personZjgList = basePersonZjgService.findAll();
        Map<Integer, String> personMap = personZjgList.stream().collect(Collectors.toMap(BasePersonZjg::getId, BasePersonZjg::getUserName));
        // 不返回密码（安全性）
        for (User user : users) {
            user.setPassword("");
            user.setPersonName(personMap.get(user.getPersonId()));
        }
        return this.vList(users,(int)count);
    }


    /**
     * 查询用户列表
     */
    @ApiOperation(value = "查询用户列表,绑定UKEY")
    @SysRequestLog(description = "查询用户列表，绑定UKEY", actionType = ActionType.SELECT)
    @PostMapping(value="/keyList")
    public VList<UserKeyVo> queryUserKeyList(HttpServletRequest request, @RequestBody UserQuery userQueryOrg) {
        UserQuery userQuery = new UserQuery();
        BeanUtils.copyProperties(userQueryOrg,userQuery);
        // 外部接口调用时，默认排序
        List<UserKeyVo> users;
        // 组织机构名称与编码排序时，均按照编码排序
        if (StringUtils.isNotEmpty(userQuery.getOrder_())){
            String order_ = CommonUtil.camelToUnderLine(userQuery.getOrder_());
            if ("orgName".equals(userQuery.getOrder_())) {
                userQuery.setOrder_("org_code");
            }
            userQuery.setOrder_(order_);
        }
        else if (StringUtils.isBlank(userQuery.getOrder_()) || StringUtils.isBlank(userQuery.getBy_())) {
            userQuery.setOrder_("id");
            userQuery.setBy_("desc");
        }

        users = baseKoalOrgService.queryUsersKey(userQuery);
        long count = baseKoalOrgService.queryUsersKeyCount(userQuery);
        // 不返回密码（安全性）
        for (User user : users) {
            user.setPassword("");
        }
        return this.vList(users,(int)count);
    }


    /**
     * 添加用户
     */
    @ApiOperation(value = "添加用户")
    @SysRequestLog(description = "添加用户", actionType = ActionType.ADD)
    @PutMapping
    public Result add(@RequestBody User user, HttpServletRequest request) {
        //  禁止超级管理员账户，创建了拥有超级管理员权限的用户
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        Short confEnable = systemConfigService.getThreePowerEnable();
        if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && Const.USER_ADMIN.equals(loginUser.getAccount())){
            String roleId = user.getRoleId();
           if(hasAdminRole(roleId)){
               return this.result(ErrorCode.THREE_POWER_USER_CREATE_WRONG);
           }
        }
        // 密码复杂度判断
        ResultAble complexResult = systemConfigService.judgePassComplex((String) request.getAttribute("bpp"));
        // 未开启或者通过校验
        if (complexResult != null) {
            return this.result(complexResult);
        }
        // 注册时间
        user.setLastUpdateTime(new Date());
        user.setLastLoginTime(new Date());
        // 创建人
        com.vrv.vap.common.model.User creator = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        if (creator != null) {
            user.setCreator(creator.getId());
        }
        // 默认组织机构、省、市
        if (StringUtils.isEmpty(user.getOrgCode())) {
//            user.setOrgCode(siteProperties.getOrgRoot());
            user.setCity("");
            user.setProvince("");
        }
        // 默认密码及密码状态
        user.setPassword(loginService.getDefaultPWD());
        user.setSalt(Const.DEFAULT_SALT);
        user.setPwdStatus(Const.PWD_STATUS_DEFAULT);
        if (user.getIsLeader() == null) {
            user.setIsLeader(Const.IS_LEADER_STATUS);
        }
        user.setLoginTimes(Const.LOGIN_ERROR_TIMES);
        // 自监管人员
        if (user.getPersonId() != null) {
            BasePersonZjg basePersonZjg = basePersonZjgService.findById(user.getPersonId());
            if (basePersonZjg != null) {
                user.setOrgCode(basePersonZjg.getOrgCode());
                user.setOrgName(basePersonZjg.getOrgName());
                user.setPersonName(basePersonZjg.getUserName());
            }
        }
        List<String> orgList = userOrgService.getDefaultOrg(user);
        user.setOrgId(StringUtils.strip(orgList.toString().replace(" ",""),"[]"));
        int result = userService.save(user);
        if (result == 1) {
            user.setPassword("");
            // 用户角色、用户组织机构
            userRoleService.saveUserRole(user.getRoleId(), user.getId());
            userOrgService.saveOrgUsers(user.getOrgId(), user.getId());
            User userSec = new User();
            BeanUtils.copyProperties(user,userSec);
            this.tranferRoleName(userSec);
            SyslogSenderUtils.sendAddSyslogAndTransferredField(userSec, "添加用户", transferMap);
            return this.vData(user);
        }
        return this.result(false);
    }

    private boolean hasAdminRole(String roleId) {
        if (StringUtils.isNotEmpty(roleId)) {
            String[] roleIds = roleId.split(",");
            List<String> roleIdList = Arrays.asList(roleIds);
            Example example = new Example(Role.class);
            example.createCriteria().andIn("id",roleIdList);
            List<Role> roleList =  roleService.findByExample(example);
            if(CollectionUtils.isNotEmpty(roleList)){
               return roleList.stream().anyMatch(p-> Const.ADMIN_THREE_POWER.equals(p.getThreePowers()));
            }
        }
        return false;
    }


    /**
     * 修改用户
     */
    @ApiOperation(value = "修改用户")
    @SysRequestLog(description = "修改用户", actionType = ActionType.UPDATE)
    @PatchMapping
    public Result edit(@RequestBody User user) {
        //  禁止超级管理员账户，创建了拥有超级管理员权限的用户
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        Short confEnable = systemConfigService.getThreePowerEnable();
        if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && Const.USER_ADMIN.equals(loginUser.getAccount())){
            String roleId = user.getRoleId();
            if(hasAdminRole(roleId)){
                return this.result(ErrorCode.THREE_POWER_USER_CREATE_WRONG);
            }
        }
        if (user.getPersonId() != null) {
            BasePersonZjg basePersonZjg = basePersonZjgService.findById(user.getPersonId());
            if (basePersonZjg != null) {
                user.setOrgCode(basePersonZjg.getOrgCode());
                user.setOrgName(basePersonZjg.getOrgName());
            }
        }

        User userSrc = userService.findById(user.getId());
        transferRoleNameAndPerson(userSrc);
        User userTrans = new User();
        BeanUtils.copyProperties(user, userTrans);
        transferRoleNameAndPerson(userTrans);

        if (StringUtils.isEmpty(user.getRoleId()) && StringUtils.isEmpty(user.getOrgId())) {
            int result = userService.updateSelective(user);
            if (result == 1) {
                this.sendLog(userSrc,userTrans);
            }
            return this.result(result == 1);
        }
        // 更新用户角色、用户组织机构
        /*else if (StringUtils.isEmpty(user.getOrgId())) {
            return this.result(userService.updateUserRole(user) == 1);
        }*/
        else if (StringUtils.isEmpty(user.getRoleId())) {
            int result = userService.updateUserOrg(user);
            if (result == 1) {
                this.sendLog(userSrc,userTrans);
            }
            return this.result(result == 1);
        }
        int result1 = userService.updateUserRole(user);
        int result2 = userService.updateUserOrg(user);
        if (result1 == 1 && result2 == 1) {
            this.sendLog(userSrc,userTrans);
        }
        return this.result(result1 == 1 && result2 == 1);
    }

    private void sendLog(User userSrc,User userTrans) {
        this.transferOrg(userSrc);
        this.transferOrg(userTrans);
        SyslogSenderUtils.sendUpdateAndTransferredField(userSrc, userTrans, "修改用户", transferMap);
    }

    private void transferRoleNameAndPerson(User user) {
        this.tranferRoleName(user);
        this.transferPersonName(user);
    }

    private void tranferRoleName(User user) {
        List<Role> rolesSrc = roleService.findByids(user.getRoleId());
        Optional<String> roleNames = rolesSrc.stream().map(Role::getName).reduce((a, b) -> a + "," + b);
        user.setRoleId(roleNames.isPresent() ? roleNames.get() : user.getRoleId());
    }

    private void transferPersonName(User user) {
        if (user.getPersonId() != null) {
            BasePersonZjg basePersonZjg = basePersonZjgService.findById(user.getPersonId());
            user.setPersonName(basePersonZjg.getUserName());
        }
    }

    private void transferOrg(User user) {
        if (StringUtils.isNotEmpty(user.getOrgId())) {
            List<BaseKoalOrg> orgs = baseKoalOrgService.findByids(user.getOrgId());
            Optional<String> orgNames = orgs.stream().map(BaseKoalOrg::getName).reduce((a, b) -> a + "," + b);
            user.setOrgId(orgNames.isPresent() ? orgNames.get() : user.getOrgId());
        }
    }

    /**
     * @Description (未登录在登录界面进行修改用户密码接口)
     */
    @ApiOperation(value = "修改用户密码（未登录）")
    @PostMapping(value = "/updatepwd")
    @SysRequestLog(description = "修改用户密码", actionType = ActionType.UPDATE)
    public Result updatepwd(HttpServletRequest request, @RequestBody Map<String, String> param) {
        User user = new User();
        user.setAccount(param.get("uuu"));
        User currentUser = userService.findOneUser(user);
        // 空判断
        if (currentUser == null) {
            return this.result(false);
        }
        // 校验用户老密码是否正确
        if (!currentUser.getPassword().equals(param.get("ppp"))) {
            return this.result(ErrorCode.USER_OLD_INVALIDATE);
        }
        // 校验密码复杂度
        ResultAble complexResult = systemConfigService.judgePassComplex(param.get("bpp"));
        // 未开启或者通过校验
        if (complexResult != null) {
            return this.result(complexResult);
        }
        // 修改密码及密码状态
        currentUser.setPassword(param.get("nppd"));
        currentUser.setPwdStatus(Const.PWD_STATUS_CHANGED);
        // 更新修改时间
        currentUser.setLastUpdateTime(new Date());
        int result = userService.update(currentUser);

        return this.result(result == 1);
    }

    /**
     * @Description (在登录成功之后的修改用户密码接口)
     */
    @ApiOperation(value = "修改用户密码（已登录）")
    @PatchMapping(value = "/upd")
    @SysRequestLog(description = "修改用户密码", actionType = ActionType.UPDATE)
    public Result upd(HttpServletRequest request, @RequestBody Map<String, String> param) {
        HttpSession session = request.getSession();
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        User currentUser = userService.findById(loginUser.getId());
        // 空判断
        if (currentUser == null) {
            return this.result(false);
        }
        // 新密码与老密码相同
        if (!currentUser.getPassword().equals(param.get("old"))) {
            return this.result(ErrorCode.USER_OLD_INVALIDATE);
        }
        // 密码复杂度判断
        ResultAble complexResult = systemConfigService.judgePassComplex(param.get("bnew"));
        // 未开启或者通过校验
        if (complexResult != null) {
            return this.result(complexResult);
        }
        // 修改为新密码
        currentUser.setPassword(param.get("new"));
        currentUser.setPwdStatus(Const.PWD_STATUS_CHANGED);
        Map<String,Integer> userExtends = (Map) session.getAttribute(Global.SESSION.USER_EXTENDS);
        if (userExtends != null) {
            userExtends.put("pwdStatus", Integer.valueOf(Const.PWD_STATUS_CHANGED));
        }
        session.setAttribute(Global.SESSION.USER_EXTENDS,userExtends);
        // 更新密码修改时间
        currentUser.setLastUpdateTime(new Date());
        int result = userService.update(currentUser);

        return this.result(result == 1);
    }

    /**
     * @Description (修改密码，采用新的加密方式)
     */
    @ApiOperation(value = "修改密码，采用新的加密方式")
    @PatchMapping(value = "/updpw/new")
    @SysRequestLog(description = "修改用户密码", actionType = ActionType.UPDATE)
    public Result updpw(HttpServletRequest request, @RequestBody PwUpdateVO pwUpdateVO) {
        HttpSession session = request.getSession();
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        User currentUser = null;
        if(loginUser != null){
            currentUser = userService.findById(loginUser.getId());
        }
        if(StringUtils.isNotEmpty(pwUpdateVO.getUuu())){
            if(currentUser!=null && !currentUser.getAccount().equals(pwUpdateVO.getUuu())){
                return this.result(ErrorCode.OTHER_SAME_NAME);
            }
            if(currentUser == null) {
                User user = new User();
                user.setAccount(pwUpdateVO.getUuu());
                currentUser = userService.findOneUser(user);
            }
        }
        // 空判断
        if (currentUser == null) {
            return this.result(ErrorCode.USER_NOT_EXIST);
        }
        // 新密码与老密码相同
        String opp = "";
        try {
            opp = new Sha256Hash(EncryptUtil.decodeBase65(pwUpdateVO.getOpp()), currentUser.getSalt()).toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!currentUser.getPassword().equals(opp)) {
            return this.result(ErrorCode.USER_OLD_INVALIDATE);
        }

        String password;
        try {
            password = EncryptUtil.decodeBase65(pwUpdateVO.getNpp());
        } catch (Exception e) {
            logger.error("",e);
            if ("TimeOut".equals(e.getMessage())) {
                return this.result(ErrorCode.USER_PASS_TIME_DIFF);
            }
            return this.result(ErrorCode.USER_PASS_ERROR); // 密码解析有误
        }
        if(StringUtils.isEmpty(password)){
            return this.result(ErrorCode.USER_PASS_EMPTY);
        }

        ResultAble complexResult = systemConfigService.judgePassComplexNew(password);
        // 未开启或者通过校验
        if (complexResult != null) {
            return this.result(complexResult);
        }
        // 修改为新密码
        currentUser.setPassword(new Sha256Hash(password, currentUser.getSalt()).toString());
        currentUser.setPwdStatus(Const.PWD_STATUS_CHANGED);
        Map<String,Integer> userExtends = (Map) session.getAttribute(Global.SESSION.USER_EXTENDS);
        if (userExtends != null) {
            userExtends.put("pwdStatus", Integer.valueOf(Const.PWD_STATUS_CHANGED));
        }
        session.setAttribute(Global.SESSION.USER_EXTENDS,userExtends);
        // 更新密码修改时间
        currentUser.setLastUpdateTime(new Date());
        int result = userService.update(currentUser);

        return this.result(result == 1);
    }

    /**
     * 删除用户（支持批量）
     */
    @ApiOperation(value = "删除用户（支持批量）")
    @SysRequestLog(description = "删除用户（支持批量）", actionType = ActionType.DELETE)
    @DeleteMapping
    public Result del(@RequestBody DeleteQuery deleteQuery) {
        List<User> users = userService.findByids(deleteQuery.getIds());
        int result = userService.deleteUserByIds(deleteQuery.getIds());
        if (result > 0) {
            users.forEach(user -> {
                transferRoleNameAndPerson(user);
                SyslogSenderUtils.sendDeleteAndTransferredField(user,"删除用户", transferMap);
            });
            userRoleService.deleteByUserIds(deleteQuery.getIds().split(","));
            userOrgService.deleteByUserIds(deleteQuery.getIds().split(","));
        }
        return this.result(result > 0);
    }


    /**
     * pki登录按钮，跳转到第三方系统
     */
    @Ignore
    @ApiOperation(value = "新疆pki登录")
    @GetMapping(value = "/uisaLogin")
    public Result UIASLogin(HttpServletRequest request,HttpServletResponse response){
//        String referer = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
        String referer = "https" + "://" + request.getServerName() + ":" + "443";
        String redirectURL = referer + UISA_LOGIN_CALLBACK_ADDR;
        String goURL = request.getRequestURI();
        Map<String, Object> params = new HashMap<>();
        params.put("response_type", responseType);
        params.put("scope", scope);
        params.put("client_id", clientId);
        params.put("state", request.getSession().getId());
        params.put("referer", referer);
        params.put("redirect_uri", redirectURL);
        params.put("gotoUrl", goURL);
        String strParams = getUrlParamsByMapEncode(params);
        String loginURL = thirdPlatformLoginUrl + "?" + strParams;
        try {
            response.sendRedirect(CleanUtil.cleanString(loginURL));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Global.OK;
    }


    public static String getUrlParamsByMapEncode(Map<String, Object> map) {
        if (map == null) {
            return "";
        }
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            try {
                sb.append(entry.getKey() + "=" +  URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                sb.append("&");
            }catch (Exception e){
                logger.error("参数转议错误");
            }

        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = org.apache.commons.lang3.StringUtils.substringBeforeLast(s, "&");
        }
        return s;
    }


    /**
     * pki登陆回调函数
     */
    @ApiOperation(value = "pki登陆回调函数", hidden = false)
    @GetMapping(value = "/uiasLogin/callback")
    public Result codeToToken(HttpServletRequest request,HttpServletResponse response) throws  Exception{
        String code = request.getParameter("code");
        if(StringUtils.isNotEmpty(code)) {
            String referer = "https://" + request.getServerName() + ":443";
            String redirectUrl = referer + "/api-common/user/uiasLogin/callback";
            String getTokenformat = "client_id=%s&client_secret=%s&grant_type=%s&redirect_uri=%s&code=%s";
            getTokenformat = String.format(getTokenformat, this.clientId, this.clientSecret, "authorization_code", URLEncoder.encode(redirectUrl, "UTF-8"), URLEncoder.encode(code, "UTF-8"));
            logger.info("codeToToke请求回调地址:" + redirectUrl);
            logger.info("codeToToke请求回调地址参数:" + LogForgingUtil.validLog(getTokenformat));
            String result = HttpRequestUtil.sendPostRedircet(codeToTokenUrl, getTokenformat);
            logger.info("获取token信息： " + LogForgingUtil.validLog(result));
            Map tokenInfoMap = StringUtils.isEmpty(result) ? null : jsonToMap(result);
            if(tokenInfoMap == null || (tokenInfoMap!= null && !tokenInfoMap.containsKey("access_token"))){
                return this.result(ErrorCode.UIAS_UIASLOGIN_NO_RESULT);
            }
            String accessToken = (String) tokenInfoMap.get("access_token");
            logger.info("access_token:" + LogForgingUtil.validLog(accessToken));
            if (StringUtils.isNotEmpty(accessToken)) {
                //根据acess_token，获取人员信息登陆
                String getUserformat = "access_token=" + accessToken;
                String encodeUserInfoString = HttpRequestUtil.sendGet(tokenToUserUrl, getUserformat);
                logger.info("生成加密人员信息：" + encodeUserInfoString);
                Map encodeMap = jsonToMap(encodeUserInfoString);
                if (encodeMap == null) {
                    return this.result(ErrorCode.UIAS_UIASLOGIN_NO_RESULT);
                }
                // 获取解密的人员信息
                String decodeUserInfoString = loginService.getUIASDecodeUserInfo(encodeMap);
                logger.info("生成解密人员信息：" + decodeUserInfoString);
                // 校验解密的人员信息
                Result UIASCheckresult = verifyUIASDecodeUserInfo(decodeUserInfoString);
                if (UIASCheckresult != null) {
                    return UIASCheckresult;
                }
                // 根据解密的人员信息获取系统用户
                Map<String, Object> map = jsonToMap(decodeUserInfoString);
                HttpSession session = request.getSession();
                String idCard = (String) map.get("guid");
                String sessionId = (String) map.get("sessionId");
                User user = new User();
                user.setIdcard(idCard);
                User currentUser = userService.findOneUser(user);
                // 校验登陆用户
                ErrorCode errorCode = validateUIASLoginUser(currentUser,session);
                // 根据用户校验结果进行登陆相关操作
                Result afterResult = loginAfterUserValidate(errorCode,request,currentUser,session,LoginTypeEnum.CERTIFICATE,sessionId);
                String redirectURI = "/";
                if (afterResult != null) {
                    // 证书没有通过验证,跳转证书上传页面
                    if (afterResult instanceof VData) {
                        Object resp = ((VData) afterResult).getData();
                        if (resp instanceof Map) {
                            Map resultMap = (Map) resp;
                            if (resultMap.containsKey("homepage")) {
                                redirectURI = (String) resultMap.get("homepage");
                            }
                        }
                    }
                    else {
                        return afterResult;
                    }
                }
                String reUrl = "https://" + request.getServerName() +redirectURI;
                response.sendRedirect(reUrl);
                logger.info("最终重定向的redirectURI为:" + reUrl);
                return Global.OK;
            }
        }
        return this.result(ErrorCode.UIAS_UIASLOGIN_CALLBACK_FAIL);
    }


    /**
     * 校验解密的UIAS人员信息
     * @param decodeUserInfoString
     * @return
     */
    private Result verifyUIASDecodeUserInfo(String decodeUserInfoString) {
        if (StringUtils.isEmpty(decodeUserInfoString)) {
            return this.result(ErrorCode.UIAS_UIASLOGIN_DECODE_FAIL);
        }
        Map<String, Object> map = jsonToMap(decodeUserInfoString);
        if (map == null || !map.containsKey("guid") || !map.containsKey("sessionId")) {
            return this.result(ErrorCode.UIAS_UIASLOGIN_DECODE_FAIL);
        }
        return null;
    }

    private ErrorCode validateUIASLoginUser(User currentUser,HttpSession session) {
        // 用户不存在
        if (currentUser == null) {
            return ErrorCode.USER_NOT_EXIST;
        }
        ErrorCode threePowerResult = (ErrorCode) systemConfigService.checkThreePower(currentUser);
        if (threePowerResult != null) {
            return threePowerResult;
        }
        return null;
    }

    private Map jsonToMap(String source){
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            Map<String, Object> map = objectMapper.readValue(JsonSanitizer.sanitize(source), Map.class);
            return map;
        }catch (IOException e){
            return null;
        }
    }


    /**
     * 登录
     */
    @Ignore
    @ApiOperation(value = "登录")
    @PostMapping(value = "/login")
    public Result login(HttpServletRequest request, @RequestBody UserLoginVo userLoginVo) {
        HttpSession session = request.getSession(false);
        String code = "";
        if (session != null) {
            if (captchaProperties.isEnable()) {
                code = (String) session.getAttribute("captcha");
            }
            session.invalidate();
        } else {
            if (captchaProperties.isEnable()) {
                return this.result(ErrorCode.USER_MESSAGE_CODE_EXPIRE);
            }
        }
        session = request.getSession(true);
        if (StringUtils.isNotEmpty(code)) {
            session.setAttribute("captcha",code);
        }
        String userAccount = userLoginVo.getUuu(); //用户输入的账号
        // 获取登录用户
        User user = new User();
        user.setAccount(userAccount); //当前只通过用户名查找
        User currentUser = userService.findOneUser(user);
        // 校验登录参数
        ErrorCode errorCode = this.validateLoginUser(session, currentUser, userLoginVo);
        // 根据用户校验结果进行登陆相关操作
        Result afterResult = loginAfterUserValidate(errorCode,request,currentUser,session,LoginTypeEnum.lOGIN,null);
        if (afterResult != null) {
            return afterResult;
        }
        return Global.OK;
    }

    /**
     * 根据用户校验结果进行登陆相关操作
     * @param errorCode
     * @param request
     * @param currentUser
     * @param session
     * @param loginType
     * @param uiasSessionId
     * @return
     */
    private Result loginAfterUserValidate(ErrorCode errorCode,HttpServletRequest request,User currentUser,HttpSession session,LoginTypeEnum loginType,String uiasSessionId) {
        if (errorCode != null) {
            if (ErrorCode.USER_LOCK.getResult().getCode().equals(errorCode.getResult().getCode())) {
                PassTimeConf safetyConfig = systemConfigService.getSafetyConfig();
                // 发送登录失败日志
                //String message = String.format(errorCode.getResult().getMessage(), safetyConfig.getMaxFailNumber(), safetyConfig.getCheckTimeSet());
                String message = "连续" + safetyConfig.getMaxFailNumber() + "次登录错误，账号将锁定" + safetyConfig.getCheckTimeSet() + "分钟";
                loginService.sendLog(currentUser, request, TypeEnum.LOGIN, loginType, 0, "POST", message);
                return new Result(errorCode.getResult().getCode(), message);
            }
            if (ErrorCode.SIGN_VALID_ERROR.getResult().getCode().equals(errorCode.getResult().getCode())) {
                // 发送验签失败日志
                loginService.sendLog(currentUser, request, TypeEnum.LOGIN, loginType, 0, "POST", errorCode.getResult().getMessage());
                return this.result(errorCode);
            }
            // 发送登录失败日志
            loginService.sendLog(currentUser, request, TypeEnum.LOGIN, loginType, 0, "POST", "用户登录");
            return this.result(errorCode);
        }
        // 发送登录成功日志
        loginService.sendLog(currentUser, request, TypeEnum.LOGIN, loginType, 1, "POST", "用户登录");
        logger.debug("【登录的sessionID】is:" + request.getSession().getId());
        // 校验登录证书
        Result licenseResult = this.validateLoginLicense(session);
        if (licenseResult != null) {
            return licenseResult;
        }
        // 新疆pki登录session保存UIASSessionId
        if (StringUtils.isNotEmpty(uiasSessionId)) {
            session.setAttribute("UIASSessionId", uiasSessionId);
        }
        // 通过检验,将用户信息保存到session中
        if (!loginService.sessionLogin(session, currentUser, loginType.getCode())) {
            return this.result(ErrorCode.USER_NO_ROLE);
        }
        if (currentUser.getLoginTimes() !=null && currentUser.getLoginTimes() != 0) {
            // 用户密码输入成功 将尝试次数清0
            currentUser.setLoginTimes(0);
            userService.update(currentUser);
        }
        // 登录后页面跳转
        Map<String,String> redirectResult = loginService.loginRedirect(session, currentUser);
        if (redirectResult != null) {
            return this.vData(redirectResult);
        }
        return null;
    }

    @ApiOperation(value = "授权字段登录")
    @GetMapping(value = "/authFieldLogin")
    public void authFieldLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 参数非空校验
        String authField = "";
        String authFieldValue = "";
        SystemConfig authLoginConfig = systemConfigService.findByConfId("authLoginField");
        if (authLoginConfig != null && authLoginConfig.getConfEnable() == 1) {
            authField = authLoginConfig.getConfValue();
            authFieldValue = request.getParameter(authField);
            if (authFieldValue == null) {
                logger.info("传递的参数字段与配置字段不一致！");
                return;
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put("authField", authFieldValue);
        // 拼装返回结果html
        String hmtlForm = loginService.buildLoginForm(params);
        response.getWriter().write(hmtlForm);
        return;
    }

    /**
     * 授权字段登录
     */
    @Ignore
    @ApiOperation(value = "授权字段登录")
    @PostMapping(value = "/authFieldLogin")
    public Result authFieldLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SystemConfig authLoginConfig = systemConfigService.findByConfId("authLoginField");
        if (authLoginConfig != null && authLoginConfig.getConfEnable() == 1) {
            String authField = authLoginConfig.getConfValue();
            String authFieldValue = request.getParameter(authField);
            if (authFieldValue == null) {
                logger.info("传递的参数字段与配置字段不一致！");
                return Global.ERROR;
            }
            List<AuthLoginField> authLoginFieldList = authLoginFieldService.findByProperty(AuthLoginField.class,"authFieldValue",authFieldValue);
            if (CollectionUtils.isNotEmpty(authLoginFieldList)) {
                AuthLoginField authLoginField = authLoginFieldList.get(0);
                Integer userId = authLoginField.getUserId();
                User currentUser = userService.findById(userId);
                UserLoginVo userLoginVo = new UserLoginVo();
                userLoginVo.setUuu(currentUser.getAccount());
                userLoginVo.setPpp(currentUser.getPassword());
                // 校验登录参数
                ErrorCode errorCode = this.validateLoginUser(session, currentUser, userLoginVo);
                // 根据用户校验结果进行登陆相关操作
                Result afterResult = loginAfterUserValidate(errorCode,request,currentUser,session,LoginTypeEnum.lOGIN,null);
                if (afterResult != null) {
                    return afterResult;
                }
                return Global.OK;
            } else {
                logger.info("未配置对应的授权用户！");
            }
        } else {
            logger.info("授权字段登陆未开启或未配置！");
        }
        return Global.ERROR;
    }

    /**
     * ip授权登录
     */
    @Ignore
    @ApiOperation(value = "ip授权登录")
    @GetMapping(value = "/authFieldLogin/ip")
    public Result authFieldLoginIp(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SystemConfig authLoginConfig = systemConfigService.findByConfId("authLoginField");
        if (authLoginConfig != null && authLoginConfig.getConfEnable() == 1) {
            String ip = IPUtils.getIpAddress(request);
            logger.info("客户端ip地址为：" + LogForgingUtil.validLog(ip));
            List<AuthLoginField> authLoginFieldList = authLoginFieldService.findByProperty(AuthLoginField.class,"authFieldValue",ip);
            if (CollectionUtils.isNotEmpty(authLoginFieldList)) {
                AuthLoginField authLoginField = authLoginFieldList.get(0);
                Integer userId = authLoginField.getUserId();
                User currentUser = userService.findById(userId);
                UserLoginVo userLoginVo = new UserLoginVo();
                userLoginVo.setUuu(currentUser.getAccount());
                userLoginVo.setPpp(currentUser.getPassword());
                // 校验登录参数
                ErrorCode errorCode = this.validateLoginUser(session, currentUser, userLoginVo);
                // 根据用户校验结果进行登陆相关操作
                Result afterResult = loginAfterUserValidate(errorCode,request,currentUser,session,LoginTypeEnum.lOGIN,null);
                if (afterResult != null) {
                    return afterResult;
                }
                return Global.OK;
            } else {
                logger.info("未配置对应的授权用户！");
            }
        } else {
            logger.info("授权字段登陆未开启或未配置！");
        }
        return Global.ERROR;
    }

    @ApiOperation(value = "授权MAC登录")
    @GetMapping(value = "/authMacLogin")
    public void authMacLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 参数非空校验
        String authField = "";
        String authFieldValue = "";
        SystemConfig authLoginConfig = systemConfigService.findByConfId("authMacLogin");
        if (authLoginConfig != null && authLoginConfig.getConfEnable() == 1) {
            authField = authLoginConfig.getConfValue();
            authFieldValue = request.getParameter(authField);
            if (authFieldValue == null) {
                logger.info("传递的参数字段与配置字段不一致！");
                return;
            }
        }
        Map<String, String> params = new HashMap<>();
        params.put("authField", authFieldValue);
        // 拼装返回结果html
        String hmtlForm = loginService.buildLoginForm(params);
        response.getWriter().write(hmtlForm);
        return;
    }

    /**
     * 授权Mac字段登录
     */
    @Ignore
    @ApiOperation(value = "授权Mac字段登录")
    @PostMapping(value = "/authMacLogin")
    public Result authMacLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        SystemConfig authLoginConfig = systemConfigService.findByConfId("authMacLogin");
        if (authLoginConfig != null && authLoginConfig.getConfEnable() == 1) {
            String authField = authLoginConfig.getConfValue();
            String authFieldValue = request.getParameter(authField);
            if (authFieldValue == null) {
                logger.info("传递的参数字段与配置字段不一致！");
                return Global.ERROR;
            }
            List<AuthLoginField> authLoginFieldList = authLoginFieldService.findByProperty(AuthLoginField.class,"authFieldValue",authFieldValue);
            if (CollectionUtils.isNotEmpty(authLoginFieldList)) {
                AuthLoginField authLoginField = authLoginFieldList.get(0);
                Integer userId = authLoginField.getUserId();
                User currentUser = userService.findById(userId);
                UserLoginVo userLoginVo = new UserLoginVo();
                userLoginVo.setUuu(currentUser.getAccount());
                userLoginVo.setPpp(currentUser.getPassword());
                // 校验登录参数
                ErrorCode errorCode = this.validateLoginUser(session, currentUser, userLoginVo);
                // 根据用户校验结果进行登陆相关操作
                Result afterResult = loginAfterUserValidate(errorCode,request,currentUser,session,LoginTypeEnum.lOGIN,null);
                if (afterResult != null) {
                    return afterResult;
                }
                return Global.OK;
            } else {
                logger.info("未配置对应的授权用户！");
            }
        } else {
            logger.info("授权Mac字段登陆未开启或未配置！");
        }
        return Global.ERROR;
    }

    private ErrorCode validateLoginUser(HttpSession session, User currentUser, UserLoginVo userLoginVo) {
        String ppp = userLoginVo.getPpp(); //用户输入的密码
        String userAccount = userLoginVo.getUuu(); //用户输入的账号
        String userMac = userLoginVo.getMmm();//客户端mac
        PassTimeConf safetyConfig = systemConfigService.getSafetyConfig();
        Integer checkTimeSet = safetyConfig.getCheckTimeSet();
        // 用户名称不为空
        if (StringUtils.isEmpty(userAccount)) {
            return ErrorCode.USER_NAME_EMPTY;
        }
        // 判断用户是否已经登录
        if (session.getAttribute(Global.SESSION.USER) != null) {
            return ErrorCode.USER_IS_LOGIN;
        }
        // 用户名或者密码错误
        if (currentUser == null) {
            startWatcher();
            Integer count = safetyConfig.getMaxFailNumber() - 1;
            if (status.containsKey(userAccount)) {
                count = status.get(userAccount);
                if (count >= 1) {
                    count = count - 1;
                    status.put(userAccount,count);
                } else {
                    String message = "连续" + safetyConfig.getMaxFailNumber() + "次登录错误，账户已锁定！";
                    ErrorCode.LOGIN_LOCK.getResult().setMessage(message);
                    return ErrorCode.LOGIN_LOCK;
                }
            } else {
                status.put(userAccount,count);
            }
            ErrorCode.INVALIDATE.getResult().setMessage("用户名或者密码错误，剩余次数" + count + "次");
            return ErrorCode.INVALIDATE;
        }
        // 用户密码不为空
        if (StringUtils.isEmpty(ppp)) {
            return ErrorCode.USER_PASS_EMPTY;
        }
        if (captchaProperties.isEnable()) {
            String code = (String) session.getAttribute("captcha");
            logger.info("captcha:" + code);
            if (code == null) {
                return ErrorCode.USER_CODE_INVALIDATE;
            }
            if (userLoginVo.getVerCode() == null || !code.trim().equalsIgnoreCase(userLoginVo.getVerCode().trim())) {
                return ErrorCode.USER_MESSAGE_CODE_INVALIDATE;
            }
        }
        // 检查三权是否开启,开启或者非三权用户,返回为null
        ErrorCode threePowerResult = (ErrorCode) systemConfigService.checkThreePower(currentUser);
        if (threePowerResult != null) {
            return threePowerResult;
        }
        // 判断当前用户是否被锁定,达到解锁时间自动解锁用户
        long isLock = systemConfigService.checkLockStatus(currentUser);
        if (isLock > 0) {
            //剩余解锁时间 不为0
            Date lastLoginTime = currentUser.getLastLoginTime();
            Date unlockTime = DateUtil.addMinutes(lastLoginTime,checkTimeSet);
            String message = "连续" + safetyConfig.getMaxFailNumber() + "次登录错误，请于" + DateUtil.format(unlockTime,DateUtil.DEFAULT_DATE_PATTERN) + "之后再登录";
            ErrorCode.LOGIN_LOCK.getResult().setMessage(message);
            return ErrorCode.LOGIN_LOCK;
        }
        // 密码输入错误,允许重新输入次数判断,重试次数超过设定值就锁定用户
        if (!ppp.equals(currentUser.getPassword())) {
            String pStr = "";
            try {
                pStr = EncryptUtil.decodeBase65(ppp);
            } catch (Exception e) {
                logger.error("",e);
                if ("TimeOut".equals(e.getMessage())) {
                    return ErrorCode.USER_PASS_TIME_DIFF;
                }
                e.printStackTrace();
            }
            boolean pwdFlag = false;
            if (StringUtils.isNotEmpty(pStr) && StringUtils.isNotEmpty(currentUser.getSalt())) {
                String shaPwd = new Sha256Hash(pStr, currentUser.getSalt()).toString();
                if (currentUser.getPassword().equals(shaPwd)) {
                    pwdFlag = true;
                }
            }
            if (!pwdFlag) {
                // 剩余重试次数为0,自动锁定用户
                int restNumber = systemConfigService.checkRetryNumber(currentUser);
                if (restNumber == 0) {
                    // 推送信息
                    //pushInfo(userAccount);
                }
                // 密码错误,返回用户名或者密码错误
                ErrorCode.INVALIDATE.getResult().setMessage("用户名或者密码错误，剩余次数" + restNumber + "次");
                return ErrorCode.INVALIDATE;
            }
        }
        // 判断该用户是否被启用
        if (currentUser.getStatus() == 1) {
            return ErrorCode.USER_FREEZE;
        }
        // 判断当前用户密码时效性
        long outtimeJudge = systemConfigService.passOuttimeJudge(currentUser);
        if (outtimeJudge != 0) {
            // 不为0表示密码时效性过期
            return ErrorCode.USER_PASS_OUTTIME;
        }
        // ip登录限制
        Short ipLoginConf = systemConfigService.getIpLoginEnabled();
        if (ipLoginConf != null && Const.CONF_ENABLED.equals(Byte.valueOf(ipLoginConf.toString())) && !Const.USER_ADMIN.equals(currentUser.getAccount())) {
            boolean ipLoginFlag = authLoginFieldService.validateLoginIp(currentUser.getId());
            if (!ipLoginFlag) {
                return ErrorCode.USER_IP_NOT_CONFIG;
            }
        }
        // mac登录限制
        Short macLoginConf = systemConfigService.getMacLoginEnabled();
        if (macLoginConf != null && Const.CONF_ENABLED.equals(currentUser.getIpLogin()) && !Const.USER_ADMIN.equals(currentUser.getAccount())) {
            boolean macLoginFlag = authLoginFieldService.validateLoginMac(currentUser.getId(),userMac);
            if (!macLoginFlag) {
                return ErrorCode.USER_MAC_NOT_CONFIG;
            }
        }
        return null;
    }

    private Result validateLoginLicense(HttpSession session) {
        //授权证书通过接口获取
        if (!licence.isImported()) {
            if (CEMS_MODULE.equals(httpLicenseModule) && !loginService.validateHttpLicense()) {
                return this.result(ErrorCode.LICENSE_INVALID);
            }
            if (XGS_MODULE.equals(httpLicenseModule) && !loginService.validateXgsHttpLicense()) {
                session.setAttribute(LICENSEAUTHENTICATION_KEY, "false");
                return null;
            }
        } else {
            //授权证书通过导入的方式
            if (!loginService.validateImportLicense(session)) {
                return null;
            }
        }
        session.setAttribute(LICENSEAUTHENTICATION_KEY, "true");
        return null;
    }

    /**
     * GAB的匹配： 获取证书相关信息，并返回给登录首页
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/getLoginCA", method = RequestMethod.POST)
    public @ResponseBody
    String getLoginCA(HttpServletRequest request) throws RuntimeException {
        String caInfo = "";
        logger.debug("url: /getLoginCA,  enter login_CA!!!!!");
        Object client_version = request.getHeader("X-CLIENT-VERIFY");
        Object ssl_client = CleanUtil.cleanString(request.getHeader("X-SSL-Client-S-DN"));
        logger.info(LogForgingUtil.validLog("client_version:" + client_version + ", ssl_client:" + ssl_client));
        if (client_version == null) {
            logger.error("X-CLIENT-VERIFY未获取到值！");
            return caInfo;
        }
        if (ssl_client == null) {
            logger.error("获取证书失败，证书内容为空！！");
            return caInfo;
        }
        if (!StringUtils.isEmpty(ssl_client.toString()) && "SUCCESS".equalsIgnoreCase(client_version.toString())) {
            String ssl = ssl_client.toString();
            logger.info("ssl_client.toString()" + LogForgingUtil.validLog(ssl));
            // String info="CN=李四 , OU=00, OU=01,O=10, L=00, L=00, ST=01, C=CN";
            logger.info(LogForgingUtil.validLog(ssl));
            if (StringUtils.isNotBlank(ssl)) {
                String infoHead = ssl.split(",")[0];
                logger.info("infoHead:" + LogForgingUtil.validLog(infoHead));
                String infoNameAndID = infoHead.split("=")[1];
                String[] nameAndCardId = infoNameAndID.split(" ");
                if (nameAndCardId.length == 2) {
                    String realName = nameAndCardId[0];// 姓名
                    realName = realName.replace("\\\\", "");
                    realName = Base64Util.hexStringToString(realName);
                    String userCardId = nameAndCardId[1];// 身份证号
                    caInfo = realName + "|" + userCardId;
                    logger.info("realName" + LogForgingUtil.validLog(realName));
                    logger.info("userCardId" + LogForgingUtil.validLog(userCardId));
                } else {
                    logger.info("未获取到证书信息(infoNameAndID):" + LogForgingUtil.validLog(infoNameAndID));
                    caInfo = "";
                }
            } else {
                caInfo = "";
            }
        } else {
            throw new RuntimeException("未获取到证书信息，请检查！！！");
        }
        logger.info("caInfo:" + LogForgingUtil.validLog(caInfo));
        return caInfo;
    }

    /**
     * 使用PKI登录
     */
    @ApiOperation(value = "使用PKI登录")
    // @GetMapping(value = "/pkiLogin")
    @RequestMapping(value = "/loginCA", method = RequestMethod.POST)
    public Result pkiLogin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        logger.error("pki login!!!!" + new Date());
//        String realName = request.getParameter("www");
        String userCardId = request.getParameter("ppp");
//        logger.info("userCardId:" + userCardId);
        User user = new User();
        String idCard = Base64Util.decoderByBase64(userCardId, UTF_8);
        user.setIdcard(idCard);
        User currentUser = null;
        try {
            currentUser = userService.findOneUser(user);
            //logger.info("currentUser:" + currentUser);
        } catch (Exception e) {
            return this.result(ErrorCode.USER_TOOMANY_CODE);
        }
        if (currentUser == null) {
            return this.result(ErrorCode.USER_NOT_EXIST);
        }
        if (currentUser.getStatus() == 1) {
            return this.result(ErrorCode.USER_FREEZE);
        }
        if (!this.loginService.sessionLogin(session, currentUser, 1)) {
            return this.result(ErrorCode.USER_NO_ROLE);
        }

        Object url = request.getSession().getAttribute(Global.SESSION.RETURN_URL);
        Map<String, String> resp = setHomePage(url, request);
        if (url != null) {
            return this.vData(resp);
        } else {
            return Global.OK;
        }
    }

    private Map<String, String> setHomePage(Object url, HttpServletRequest request) {
        Map<String, String> resp = new HashMap<>();
        String urlstr = String.valueOf(url);
        request.getSession().removeAttribute(Global.SESSION.RETURN_URL);
        resp.put("homepage", urlstr);
        return resp;
    }

    // 获取用户portal页配置
    @ApiOperation(value = "获取用户portal页配置")
    @GetMapping(value = "/page")
    public VData<UserPage> getUserPages(HttpServletRequest request) {
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession()
                .getAttribute(Global.SESSION.USER);
        User user = userService.findById(loginUser.getId());
        UserPage userPage = new UserPage();
        userPage.setUserid(user.getId());
        return this.vData(userPageService.findOne(userPage));
    }

    // 设置用户portal页面配置
    @ApiOperation(value = "设置用户portal页面配置")
    @PatchMapping(value = "/page")
    public Result setUserPages(HttpServletRequest request, @RequestBody UserPage param) {
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession()
                .getAttribute(Global.SESSION.USER);
        User user = userService.findById(loginUser.getId());
        UserPage userPage = new UserPage();
        userPage.setUserid(user.getId());
        userPage = userPageService.findOne(userPage);
        if (userPage == null) {
            userPage = new UserPage();
            userPage.setUserid(user.getId());
            userPage.setPages(param.getPages());
            userPage.setReside(param.getReside());
            userPageService.save(userPage);
        } else {
            userPage.setPages(param.getPages());
            userPageService.update(userPage);
        }
        return this.vData(userPage);
    }

    /**
     *    获取用户皮肤喜好配置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "获取用户喜好配置")
    @GetMapping(value = "/preferenceConfig")
    public VData<String> getUserPreferenceConfig(HttpServletRequest request){
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession()
                .getAttribute(Global.SESSION.USER);
        Integer userId =  loginUser.getId();
        UserPreferenceConfig userPreference = new UserPreferenceConfig();
        userPreference.setUserId(userId);
        UserPreferenceConfig userPreferenceConfig = userPreferenceConfigService.findOne(userPreference);
        String result = "";
        if(userPreferenceConfig!= null){
            result = userPreferenceConfig.getPreferenceConfig();
        }
        return this.vData(result);
    }

    /**
     *   设置用户皮肤配置
     *
     * @param request
     * @return
     */
    @ApiOperation(value = "设置用户喜好配置")
    @PatchMapping(value = "/preferenceConfig")
    public Result setUserSkinPreference(HttpServletRequest request, @RequestBody UserPreferenceConfigVO userPreferenceConfigVO){
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession()
                .getAttribute(Global.SESSION.USER);
        Integer userId =  loginUser.getId();
        UserPreferenceConfig userPreferenceConfig = new UserPreferenceConfig();
        userPreferenceConfig.setUserId(userId);
        userPreferenceConfig.setPreferenceConfig(userPreferenceConfigVO.getPreferenceConfig());
        List<UserPreferenceConfig>  list = userPreferenceConfigService.findByProperty(UserPreferenceConfig.class,"userId",userId);
        if(CollectionUtils.isNotEmpty(list)){
            userPreferenceConfig.setId(list.get(0).getId());
           int count =  userPreferenceConfigService.updateSelective(userPreferenceConfig);
           return  this.vData(count == 1);
        }
        int count = userPreferenceConfigService.save(userPreferenceConfig);
        return this.vData(count ==1 );
    }



    // 获取用户喜好配置
    @ApiOperation(value = "获取用户喜好配置")
    @PostMapping(value = "/preference")
    public VData<UserPreference> getUserPreference(HttpServletRequest request, @RequestBody UserPreference param) {
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession()
                .getAttribute(Global.SESSION.USER);
        User user = userService.findById(loginUser.getId());
        UserPreference userPreference = new UserPreference();
        userPreference.setUserid(user.getId());
        userPreference.setResolution(param.getResolution());
        return this.vData(userPreferenceService.findOne(userPreference));
    }

    // 设置用户喜好
    @ApiOperation(value = "设置用户喜好")
    @PatchMapping(value = "/preference")
    public Result setUserPreference(HttpServletRequest request, @RequestBody UserPreference param) {
        com.vrv.vap.common.model.User loginUser = (com.vrv.vap.common.model.User) request.getSession()
                .getAttribute(Global.SESSION.USER);
        // 1、选择当前用户下的所有数据，修改skinclass和module
        Example query = new Example(UserPreference.class);
        query.createCriteria().andEqualTo("userid", loginUser.getId());
        List<UserPreference> userPreferences = userPreferenceService.findByExample(query);
        UserPreference userPreference = null;
        for (UserPreference item : userPreferences) {
            item.setSkinClass(param.getSkinClass());
            item.setModules(param.getModules());
            // 2、选择当前用户当前分辨率下的数据，修改pages、folders
            if (item.getResolution().equals(param.getResolution())) {
                userPreference = item;
            } else {
                userPreferenceService.update(item);
            }
        }
        // 保存配置数据
        if (userPreference == null) {
            userPreference = new UserPreference();
            userPreference.setUserid(loginUser.getId());
            userPreference.setPages(param.getPages());
            userPreference.setSkinClass(param.getSkinClass());
            userPreference.setResolution(param.getResolution());
            userPreference.setFolders(param.getFolders());
            userPreference.setModules(param.getModules());
            userPreferenceService.save(userPreference);
        } else {
            userPreference.setPages(param.getPages());
            userPreference.setFolders(param.getFolders());
            userPreferenceService.update(userPreference);
        }
        return this.vData(true);
    }

    /**
     * 国网一体化免密登录功能
     */
    @ApiOperation(value = "免密登录")
    @GetMapping(value = "/pwFreeLogin")
    public void redirectLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 单点登录校验接口获取证书
        if (!licence.isImported()) {
            if (!loginService.validateHttpLicense()) {
                logger.info("单点登录证书校验失败！");
                response.sendRedirect(guoWangProperties.getCemsLoginPage());
                return;
            }
        }
        // 参数非空校验
        String ticket = request.getParameter("ticket");
        ticket = StringUtils.isEmpty(ticket) ? request.getParameter("token") : ticket;
        String type = request.getParameter("type");
        if (StringUtils.isEmpty(ticket) || StringUtils.isEmpty(type)) {
            response.sendRedirect(guoWangProperties.getCemsLoginPage());
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("ticket", ticket);
        params.put("type", type);
        // 拼装返回结果html
        String hmtlForm = loginService.buildLoginForm(params);
        response.getWriter().write(hmtlForm);
        return;
    }


    /**
     * 国网到homePage
     */
    @ApiOperation(value = "跳转首页")
    @GetMapping(value = "/toCemsPage")
    public void toHomePage(HttpServletResponse response) throws Exception {
        logger.info("即将转向:" + guoWangProperties.getCemsHomePage());
        response.sendRedirect(guoWangProperties.getCemsHomePage());
        return;
    }

    /**
     * 国网一体化免密登录功能
     */
    @ApiOperation(value = "免密登录")
    @PostMapping(value = "/pwFreeLogin")
    public void pwFreeCEMSLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 请求参数非空校验
        String enToken = request.getParameter("token");
        String ticket = request.getParameter("ticket");
        if (enToken == null && ticket == null) {
            logger.info("未获取到token:" + LogForgingUtil.validLog(enToken));
            response.sendRedirect(guoWangProperties.getCemsLoginPage());
            return;
        }
        enToken = enToken == null ? ticket : enToken;
        String type = request.getParameter("type");
        // 默认配置用户校验
        String defaultUserAccount = guoWangProperties.getDefaultUser();
        if (StringUtils.isNotEmpty(authType) && AUTHTYPE_CEMS.equals(authType)) {
            String checkResponse = HTTPUtil.GET(checkTokenUrl + "?token=" + enToken,new HashMap<>());
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> checkMap = objectMapper.readValue(JsonSanitizer.sanitize(checkResponse), Map.class);
            if (checkMap != null && checkMap.containsKey("username") && checkMap.get("username") != null) {
                defaultUserAccount = (String) checkMap.get("username");
                HttpSession session = request.getSession();
                Result licenseResult = this.validateLoginLicense(session);
                if (licenseResult != null) {
                    logger.info("授权校验失败");
                    response.sendRedirect(guoWangProperties.getCemsLoginPage());
                    return;
                }
            } else {
                logger.info("验证失败:" + LogForgingUtil.validLog(checkResponse));
                response.sendRedirect(guoWangProperties.getCemsLoginPage());
                return;
            }
        } else {
            // cems Token认证
            String turl = this.guoWangProperties.getCmesAuthenticationToken() + enToken;
            logger.info("rqurl:" + LogForgingUtil.validLog(turl));
            String res = turl.startsWith("https") ? HttpRequestUtil.sendHttpsSslGet(turl) : HttpRequestUtil.sendHttpsRestGet(turl);
            logger.info("rpes:" + res);
            // 认证结果校验
            Gson gson = new Gson();
            Map<String, Object> map = new HashMap<>();
            map = gson.fromJson(JsonSanitizer.sanitize(res), map.getClass());
            if (!map.containsKey("code") || !"1".equals(map.get("code"))) {
                logger.info("验证失败:" + res);
                response.sendRedirect(guoWangProperties.getCemsLoginPage());
                return;
            }
        }
        User user = new User();
        user.setAccount(defaultUserAccount); //当前只通过用户名查找
        User currentUser = userService.findOneUser(user);
        if (currentUser == null) {
            // 用户名错误 返回用户名或者密码错误
            logger.info("用户名错误:" + LogForgingUtil.validLog(defaultUserAccount));
            response.sendRedirect(guoWangProperties.getCemsLoginPage());
            return;
        }
        // 登录成功，根据类型进行页面跳转
        HttpSession session = request.getSession();
        boolean b = this.loginService.sessionLogin(session, currentUser, 0);
        if (b) {
            logger.info("登录成功:" + LogForgingUtil.validLog(defaultUserAccount));
            session.setAttribute(LICENSEAUTHENTICATION_KEY, "true");
            if ("1".equals(type)) {
                logger.info("即将转向:" + guoWangProperties.getVapGwDefaultScreen());
                response.sendRedirect(this.guoWangProperties.getVapGwDefaultScreen());
            }
            if ("2".equals(type)) {
                logger.info("即将转向:" + guoWangProperties.getVapGwDefaultAudit());
                response.sendRedirect(this.guoWangProperties.getVapGwDefaultAudit());
            }
            return;
        }
        response.sendRedirect(guoWangProperties.getCemsLoginPage());
        return;
    }


    /**
     * 获取登录token
     */
    @ApiOperation(value = "获取登录token")
    @PostMapping(value = "/token")
    public Result getToken(HttpServletRequest request, HttpServletResponse response,@RequestBody Map<String,Object> clientInfo)
            throws Exception {
        if(clientInfo==null || !clientInfo.containsKey("clientId") || StringUtils.isEmpty(clientInfo.get("clientId").toString())){
            return ErrorCode.ORG_CODE_NULL.getResult();
        }

        if(clientInfo==null || !clientInfo.containsKey("clientSecret") || StringUtils.isEmpty(clientInfo.get("clientSecret").toString())){
            return ErrorCode.ORG_CODE_NULL.getResult();
        }

        String clientId = (String)clientInfo.get("clientId");
        String clientSecret = (String)clientInfo.get("clientSecret");
        Example example = new Example(App.class);

        List<App> apps = appService.findByProperty(App.class,"clientid",clientId);
        List<App> exzist =  apps.stream().filter(p->clientSecret.equals(p.getClientsecret())).collect(Collectors.toList());
        if(exzist.size() == 0){
            return ErrorCode.APP_NOT.getResult();
        }
        App app = exzist.get(0);
        String account = app.getUrl();
        List<User> userList = userService.findByProperty(User.class,"account",account);
        if(userList == null || userList.size() == 0){
            return ErrorCode.USER_NOT_EXIST.getResult();
        }
        com.vrv.vap.common.model.User sUser = new com.vrv.vap.common.model.User();
        sUser.setId(userList.get(0).getId());
        sUser.setAccount(userList.get(0).getAccount());
        String token = redisService.genToken(sUser, app.getId());
        return this.vData(token);
    }

    /**
     * 获取登录token
     */
    @ApiOperation(value = "第三方服务请求token")
    @PostMapping(value = "/getLoginToken")
    public String getLoginToken(@RequestBody String requestParam) {
        if (StringUtils.isEmpty(requestParam)) {
            logger.error("parameter error");
            return null;
        }

        Map<String, Object> paramMap = JSON.parseObject(requestParam);
        String user = (String) paramMap.get("username");
        if (StringUtils.isEmpty(user) || !user.equals(registerProperties.getUsername())) {
            logger.error("parameter error");
            return null;
        }

        Long timestamp = (Long) paramMap.get("timestamp");
        if (timestamp == null) {
            logger.error("parameter error");
            return null;
        }

        String validate = (String) paramMap.get("validate");
        if (StringUtils.isEmpty(validate)) {
            logger.error("parameter error");
            return null;
        }

        String md5Value = Md5Util.string2Md5(new StringBuilder(user).append(timestamp).append(registerProperties.getSecurity_key()).toString());
        if (!StringUtils.equals(md5Value, validate)) {
            logger.error("parameter error");
            return null;
        }

        Map<String, Object> result = new HashMap<>();
        List<App> apps = appService.findByProperty(App.class,"clientid", registerProperties.getClient_id());
        List<App> exzist =  apps.stream().filter(p->registerProperties.getClient_secret().equals(p.getClientsecret())).collect(Collectors.toList());
        if(exzist.size() == 0){
            return null;
        }
        App app = exzist.get(0);
        String account = app.getUrl();
        List<User> userList = userService.findByProperty(User.class,"account",account);
        if(userList == null || userList.size() == 0){
            return null;
        }
        com.vrv.vap.common.model.User loginUser = new com.vrv.vap.common.model.User();
        loginUser.setId(userList.get(0).getId());
        loginUser.setAccount(userList.get(0).getAccount());
        logger.info("userinfo:" + loginUser.getId() + "--" + loginUser.getAccount() + "--" + app.getId());
        result.put("token", redisService.genToken(loginUser, app.getId()));
        logger.info("json:" + JSON.toJSONString(result));
        return JSON.toJSONString(result);
    }

    @GetMapping(value = "/tokenLogin")
    public void tokenLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 参数非空校验
        String ticket = request.getParameter("ticket");
        ticket = StringUtils.isEmpty(ticket) ? request.getParameter("token") : ticket;
        Map<String, String> params = new HashMap<>();
        params.put("token", ticket);
        // 拼装返回结果html
        String hmtlForm = loginService.buildLoginForm(params);
        response.getWriter().write(hmtlForm);
        return;
    }


    @ApiOperation(value = "免密登录")
    @PostMapping(value = "/tokenLogin")
    public void pwTokenLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String referer = "https://" + request.getServerName() + "/";
        // 请求参数非空校验
        String enToken = request.getParameter("token");
        String ticket = request.getParameter("ticket");
        if (enToken == null && ticket == null) {
            logger.info("未获取到token:" + LogForgingUtil.validLog(enToken));
            response.sendRedirect(referer);
            return;
        }
        enToken = enToken == null ? ticket : enToken;
        String token = request.getParameter("token");

        Object result = redisService.validateToken(token);
        if (!(result instanceof com.vrv.vap.common.model.User)) {
            logger.info("校验token失败:");
            logger.info(((ErrorCode) result).getResult().getCode() + ":" + ((ErrorCode) result).getResult().getMessage());
            response.sendRedirect(referer);
            return;
        }

        User user = new User();
        user.setAccount(((com.vrv.vap.common.model.User)result).getAccount()); //当前只通过用户名查找
        User currentUser = userService.findOneUser(user);
        if (currentUser == null) {
            // 用户名错误 返回用户名或者密码错误
            logger.info("用户名错误:" + user.getAccount());
            response.sendRedirect(referer);
            return;
        }
        // 登录成功，根据类型进行页面跳转
        HttpSession session = request.getSession();
        boolean b = this.loginService.sessionLogin(session, currentUser, 0);
        if (b) {
            logger.info("登录成功:" + user.getAccount());
            session.setAttribute(LICENSEAUTHENTICATION_KEY, "true");
        }
        logger.info("跳转到："+referer);
        response.sendRedirect(referer);
        return;
    }

    @PostMapping("/token/confirmation")
    @ApiOperation("采集器token验证")
    public Map<String,Object> confirmToken(@RequestBody Map<String,String> param) {
        Map<String,Object> map = new HashMap<>();
        String token = param.get("token");
         try {
         token = URLDecoder.decode(token, "utf-8");
         } catch (UnsupportedEncodingException e) {
         token = URLDecoder.decode(token);
         }
        Object result = redisService.validateToken(token);
        if (!(result instanceof com.vrv.vap.common.model.User)) {
            map.put("res",false);
            map.put("msg","验证不通过");
        } else {
            map.put("res",true);
            map.put("msg","验证通过");
        }
        return map;
    }

    @ApiOperation("查找账户绑定ukey的序列号")
    @RequestMapping(value = "/ukeySerial", method = RequestMethod.GET)
    public VData queryUkeySerial(@RequestParam(value = "account",required = false) String account) {
        logger.info("query ukey serial, account={}", LogForgingUtil.validLog(account));
        return this.vData(userUkeyService.queryUkeySerial(account));
    }

    @ApiOperation("绑定ukey")
    @RequestMapping(value = "/ukey/bind/{userId}", method = RequestMethod.PUT)
    public Result bindUkey(@PathVariable Integer userId, @RequestBody Map map) {
        String ukeySerial = (String) map.get("ukeySerial");
        String ukeyPublicKey = (String) map.get("ukeyPublicKey");
        String ukeyCertificate = (String) map.get("ukeyCertificate");
        logger.info("bind ukey, userId={}, ukeySerial={}, ukeyPublicKey={}, ukeyCertificate={}", userId, ukeySerial, ukeyPublicKey, ukeyCertificate);
        return this.vData(userUkeyService.bindUkey(userId, ukeySerial, ukeyPublicKey, ukeyCertificate));
    }

    @ApiOperation("解绑ukey")
    @RequestMapping(value = "/ukey/unbind/{userId}", method = RequestMethod.POST)
    public Result unbindUkey(@PathVariable Integer userId) {
        logger.info("unbind ukey, id={}", userId);
        return this.vData(userUkeyService.unbindUkey(userId));
    }

    @ApiOperation("验签ukey签名")
    @RequestMapping(value = "/ukey/{account}", method = RequestMethod.POST)
    public Result verifyUkeySign(@PathVariable String account, @RequestBody Map map,HttpServletRequest request) {
        String sign = (String) map.get("sign");
        String pwd = (String) map.get("ppp");
        String mac = (String) map.get("mmm");
        UserLoginVo userLoginVo = new UserLoginVo();
        userLoginVo.setUuu(account);
        userLoginVo.setPpp(pwd);
        userLoginVo.setMmm(mac);
        logger.info("verify ukey sign, account={}, sign={}", LogForgingUtil.validLog(account), LogForgingUtil.validLog(sign));
        HttpSession session = request.getSession();
        Boolean result = userUkeyService.verifyUkeySign(account,sign);
        logger.info("验证结果：" + result);
        if (result) {
            User user = new User();
            user.setAccount(account);
            User currentUser = userService.findOneUser(user);
            // 校验登陆用户
            ErrorCode errorCode = this.validateLoginUser(session, currentUser, userLoginVo);
            // 根据用户校验结果进行登陆相关操作
            String sessionId = request.getSession().getId();
            Result afterResult = loginAfterUserValidate(errorCode,request,currentUser,session,LoginTypeEnum.CERTIFICATE,sessionId);
            if (afterResult != null) {
                return afterResult;
            }
            return this.vData(true);
        }
        return this.vData(false);
    }

    @ApiOperation("检测ukey是否已绑定")
    @RequestMapping(value = "/ukey/available/{ukeySerial}", method = RequestMethod.GET)
    public Result checkUkeyAvailable(@PathVariable String ukeySerial) {
        logger.info("check ukey available, ukeySerial={}", LogForgingUtil.validLog(ukeySerial));
        return this.vData(userUkeyService.checkUkeyAvailable(ukeySerial));
    }

    @ApiOperation("信创单点登录")
    @RequestMapping(value = "/xcTokenLogin", method = RequestMethod.GET)
    public void xcTokenLogin(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String referer = "https://" + request.getServerName() + "/";
        HttpSession session = request.getSession();
        String token = userService.getXcToken();
        if (StringUtils.isNotEmpty(token)) {
            String validateResult = userService.validateXcToken(token);
            if (StringUtils.isNotEmpty(validateResult)) {
                JSONObject result = (JSONObject) JSON.parse(validateResult);
                if (result != null) {
                    String stateCode = result.getString("stateCode");
                    if (StringUtils.isNotEmpty(stateCode) && "001".equals(stateCode)) {
                        String adminCode = result.getString("adminCode");
                        String account;
                        //暂时屏蔽，后续权限梳理清楚后再调整
//                        if ("001".equals(adminCode)) {
//                            account = Const.USER_SYSCONTROLLER;
//                        } else if ("010".equals(adminCode)) {
//                            account = Const.USER_SAFETER;
//                        } else if ("011".equals(adminCode)) {
//                            account = Const.USER_AUDITER;
//                        } else {
                            account = Const.USER_ADMIN;
//                        }
                        User user = new User();
                        user.setAccount(account);
                        User currentUser = userService.findOneUser(user);
                        ErrorCode errorCode = validateUIASLoginUser(currentUser,session);
                        // 根据用户校验结果进行登陆相关操作
                        String sessionId = request.getSession().getId();
                        Result afterResult = loginAfterUserValidate(errorCode,request,currentUser,session,LoginTypeEnum.CERTIFICATE,sessionId);
                        if (afterResult != null) {
                            logger.info("校验结果：" + afterResult.getCode() + "  " + afterResult.getMessage());
                            if (afterResult instanceof VData) {
                                Object resp = ((VData) afterResult).getData();
                                if (resp instanceof Map) {
                                    Map resultMap = (Map) resp;
                                    if (resultMap.containsKey("homepage")) {
                                        referer = (String) resultMap.get("homepage");
                                        response.sendRedirect(referer);
                                        return;
                                    }
                                }
                            }
                            return;
                        }
                        logger.info("即将跳转：" + referer);
                        response.sendRedirect(referer);
                        return;
                    }
                }
            }
        }
        return;
    }

    /**
     * 南京运维单点登录
     */
    @ApiOperation(value = "南京运维单点登录")
    @GetMapping(value = "/validationToken")
    public void validationTokenParam(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 参数非空校验
        String appId = request.getParameter("appId");
        String appName = request.getParameter("appName");
        String token = request.getParameter("token");
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(appName) || StringUtils.isEmpty(token)) {
            logger.info("参数不能为空！");
            return;
        }

        Map<String, String> params = new HashMap<>();
        params.put("appId", appId);
        params.put("appName", appName);
        params.put("token", token);
        // 拼装返回结果html
        String hmtlForm = loginService.buildLoginForm(params);
        response.setHeader("Content-Type"," text/html");
        response.setContentType("text/html");
        response.getWriter().write(hmtlForm);
        return;
    }

    @ApiOperation("南京运维单点登录")
    @PostMapping(value = "/validationToken")
    public void validationToken(HttpServletRequest request,HttpServletResponse response) throws Exception {
        String referer = "https://" + request.getServerName() + "/";
        HttpSession session = request.getSession();
        String token = request.getParameter("token");
        String appName = request.getParameter("appName");
        String appId = request.getParameter("appId");
        String validateResult = userService.validationToken(token,appName,appId);
        if (StringUtils.isNotEmpty(validateResult)) {
            JSONObject result = (JSONObject) JSON.parse(validateResult);
            if (result != null) {
                String stateCode = result.getString("stateCode");
                if (StringUtils.isNotEmpty(stateCode) && Const.TOKEN_VALIDATE_SUCCESS.equals(stateCode)) {
                    String card = result.getString("card");
                    String currentUserBiosName = result.getString("currentUserBiosName");
                    List<UserToken> users = userTokenService.findAll();
                    for (UserToken userToken : users) {
                        String userCard = userToken.getUserCard();
                        String biosUserName = userToken.getBiosUserName();
                        if(userCard.equals(card) || biosUserName.equals(currentUserBiosName)) { //TODO 说明符合要求
                            logger.info("userCard:"+userCard+",biosUserName:"+biosUserName);
                            User user = new User();
                            user.setAccount(xcAccount);
                            User currentUser = userService.findOneUser(user);
                            ErrorCode errorCode = validateUIASLoginUser(currentUser,session);
                            // 根据用户校验结果进行登陆相关操作
                            String sessionId = request.getSession().getId();
                            Result afterResult = loginAfterUserValidate(errorCode,request,currentUser,session,LoginTypeEnum.CERTIFICATE,sessionId);
                            if (afterResult != null) {
                                logger.info("校验结果：" + afterResult.getCode() + "  " + afterResult.getMessage());
                                if (afterResult instanceof VData) {
                                    Object resp = ((VData) afterResult).getData();
                                    if (resp instanceof Map) {
                                        Map resultMap = (Map) resp;
                                        if (resultMap.containsKey("homepage")) {
                                            referer = (String) resultMap.get("homepage");
                                            response.sendRedirect(referer);
                                            return;
                                        }
                                    }
                                }
                                return;
                            }
                            logger.info("即将跳转：" + referer);
                            response.sendRedirect(referer);
                            return;
                        }
                    }
                }
            }
        }
        return;
    }

}
