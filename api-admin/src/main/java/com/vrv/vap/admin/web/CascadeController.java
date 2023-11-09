package com.vrv.vap.admin.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.HttpClientUtils;
import com.vrv.vap.admin.common.util.HttpRequestUtil;
import com.vrv.vap.admin.common.util.JsonUtil;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RequestMapping(path = "/")
@RestController
public class CascadeController extends ApiController {
    private static final String HTTPS = "https://";
    private static final String HTTP = "http://";
    private static final String UTF_8 = "utf-8";
    private static final String USER_ID = "userId";
    private static final String TOKEN = "token";
    private static final String MESSAGE = "message";
    private static final String STATUS = "status";
    private static Logger logger = LoggerFactory.getLogger(CascadeController.class);
    public static final String LICENSEAUTHENTICATION_KEY = "licenseAuthentication";
    /**
     * 生成级联登录使用参数，token与guid
     */
    @Autowired
   private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private MapregionService mapregionService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private RedisService redisService;
    @Autowired
    private ResourceService resourceService;
    @Autowired
    private RoleResourceService roleResourceService;
    @Autowired
    private LoginService loginService;
    @Autowired
    private UserService userService;



    //解决https接口调用，需要证书的问题
    public RestTemplate httpsRestTemplate(HttpComponentsClientHttpRequestFactory httpsFactory) {
        RestTemplate restTemplate = new RestTemplate(httpsFactory);
        restTemplate.setErrorHandler(
                new ResponseErrorHandler() {
                    @Override
                    public boolean hasError(ClientHttpResponse clientHttpResponse) {
                        return false;
                    }

                    @Override
                    public void handleError(ClientHttpResponse clientHttpResponse) {
                        // 默认处理非200的返回，会抛异常
                    }
                });
        return restTemplate;
    }

    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory()
            throws Exception {
        CloseableHttpClient httpClient = HttpClientUtils.acceptsUntrustedCertsHttpClient();
        HttpComponentsClientHttpRequestFactory httpsFactory = null;
        try{
            httpsFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            httpsFactory.setReadTimeout(40000);
            httpsFactory.setConnectTimeout(40000);
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            httpClient.close();
        }

        return httpsFactory;
    }

    @ApiOperation(value = "跳转级联设备登录，前端传入级联系统的ip和端口port")
    @GetMapping(value = "/cascade/redirect/{ip}/{ishttps}")
    public void redirect(HttpServletRequest request, HttpServletResponse response, @PathVariable("ip") String ip, @PathVariable("ishttps") int ishttps)  {
        // 将生成的token存储入redis
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User)request.getSession().getAttribute(Global.SESSION.USER);
        String userId = String.valueOf(user.getId());
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(token,userId,2,TimeUnit.MINUTES);
        List<Mapregion> mapregions = mapregionService.getMapregions();
        String guid =  mapregions.get(0).getGuid();
    //        String  userId = "31";
        try {
            if(ishttps==0){
                String  redirect = HTTP + ip + "/api-common/cascade/toLogin"
                        + "?token=" + URLEncoder.encode(token, UTF_8) + "&guid=" + URLEncoder.encode(guid, UTF_8);
                response.sendRedirect(CleanUtil.cleanString(redirect));
            }
            if (ishttps==1){
                String  redirect = HTTPS + ip +  "/api-common/cascade/toLogin"
                        + "?token=" + URLEncoder.encode(token, UTF_8) + "&guid=" + URLEncoder.encode(guid, UTF_8);
                response.sendRedirect(CleanUtil.cleanString(redirect));
            }

        } catch (Exception e) {
            logger.error("",e);
        }
    }

    @ApiOperation(value = "用于验证级联登录，是否通过")
    @PostMapping(value = "/cascade/checkLogin")
    public Result checkLogin( @RequestBody Map<String,String> param) {
       String token =  param.get(TOKEN);
       String guid =   param.get("guid");
       int userId =  Integer.parseInt(param.get(USER_ID));
      Map map = new HashMap<String,Object>();
      if(StringUtils.isEmpty(token) || StringUtils.isEmpty(guid)){
          map.put("rtn",-1);
          map.put(MESSAGE,"级联登录缺失请求参数");
          return this.vData(map);
      }
      List<Mapregion> mapregions = mapregionService.getMapregions();
      String checkGuid =  mapregions.get(0).getGuid();
      if(stringRedisTemplate.hasKey(token) && guid.equals(checkGuid)){
          User user = userService.findById(userId);
          map.put("rtn",0);
          map.put(MESSAGE,"级联登录验证成功");
          map.put("user",user);
      }
      else{
          map.put("rtn",-1);
          map.put(MESSAGE,"级联登录验证失败");
      }
        return this.vData(map);
    }

    @ApiOperation(value = "级联登录")
    @GetMapping(value = "/cascade/toLogin")
    public Result toLogin(HttpServletRequest request){
        //调用其他平台的接口
        String token =  request.getParameter(TOKEN);
        String guid =  request.getParameter("guid");
        String userId = stringRedisTemplate.opsForValue().get(token);
        List<Mapregion> mapregions = mapregionService.getMapregions();
        String upIp = mapregions.get(0).getUpIp();
        String url = request.getRequestURL().toString();
        String checkUrl = "";
        if(StringUtils.isNotEmpty(url) && url.startsWith(HTTP) ){
            checkUrl = HTTP + upIp + "/api-common/cascade/checkLogin";
        }
        if(StringUtils.isNotEmpty(url) && url.startsWith(HTTPS) ){
            checkUrl = HTTPS + upIp + "/api-common/cascade/checkLogin";
        }
        if(StringUtils.isEmpty(checkUrl)){
            return  this.result(ErrorCode.CASCADE_CALL_INTERFACE_FAILE);
        }
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put(TOKEN, token);
        paramMap.put("guid", guid);
        paramMap.put(USER_ID,userId);
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String checkJson = objectMapper.writeValueAsString(paramMap);
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("application/json;charset=UTF-8");
            headers.setContentType(type);
            headers.add("SESSION", request.getSession().getId());
            RestTemplate restTemplate =  this.httpsRestTemplate(this.httpComponentsClientHttpRequestFactory());
            HttpEntity<String> checkRequest = new HttpEntity<>(checkJson, headers);
            ResponseEntity<String> responseEntity =restTemplate.postForEntity(checkUrl, checkRequest, String.class);
            HttpStatus statusCode = responseEntity.getStatusCode();
            if (statusCode.value() == 200) {
                String bodyStr = responseEntity.getBody();
                Map<String, Object> body = objectMapper.readValue(JsonSanitizer.sanitize(bodyStr), Map.class);
                Map<String, Object> result = (Map<String, Object>) body.get("data");
                int rtn = (int) result.get("rtn");
                Map currentUserMap = (Map) result.get("user");
                logger.info("currentUserMap:" + LogForgingUtil.validLog(currentUserMap.toString()));
                if (rtn == 0) {
                    //登录成功，用户信息写入session
                    HttpSession session = request.getSession();
                    if (currentUserMap == null) {
                        return this.result(ErrorCode.USER_NOT_EXIST);
                    }
                    if ((Integer) currentUserMap.get(STATUS) == 1) {
                        return this.result(ErrorCode.USER_FREEZE);
                    }
                    if (!this.sessionLogin(session, currentUserMap)) {
                        return this.result(ErrorCode.USER_NO_ROLE);
                    }
                    Map<String, String> resp = new HashMap<>();
                    resp.put("homepage", "/");
                    return this.vData(resp);
                } else {
                    return this.result(ErrorCode.CASCADE_LOGIN_CHECK_FAILE);
                }
            }
            else{
                return  this.result(ErrorCode.CASCADE_CALL_INTERFACE_FAILE) ;
            }
        } catch (Exception e) {
            logger.error("",e);
        }
        return null;
    }

    // 将用户信息存到SESSION，并更新权限缓存
    private boolean sessionLogin(HttpSession session, Map currentUserMap) {
        com.vrv.vap.common.model.User loginUser = new com.vrv.vap.common.model.User();
        List<UserRole> userRoles = userRoleService.findByProperty(UserRole.class, USER_ID, currentUserMap.get("id"));
        if (userRoles == null || userRoles.size() <= 0) {
            return false;
        }
        loginUser.setAccount((String)currentUserMap.get("account"));
        loginUser.setId(currentUserMap.get("id")!= null? (int)currentUserMap.get("id"):0);
        loginUser.setIdcard((String)currentUserMap.get("idcard"));
        loginUser.setName((String)currentUserMap.get("name"));
        loginUser.setStatus(currentUserMap.get(STATUS)!= null? (int)currentUserMap.get(STATUS):0);
        loginUser.setLoginType(0);
        loginUser.setOrgCode((String)currentUserMap.get("orgCode"));
        loginUser.setOrgName((String)currentUserMap.get("orgName"));
        loginUser.setProvince((String)currentUserMap.get("province"));
        loginUser.setCity((String)currentUserMap.get("city"));
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
        session.setAttribute(Global.SESSION.USER, loginUser);
        this.cacheRoleResource(loginUser.getRoleIds());
        return true;
    }

    private void cacheRoleResource(List<Integer> roleIds) {
        for (Integer roleId : roleIds) {
            if (!redisService.hasRoleResource(roleId + "")) {
                List<Resource> resources = resourceService.loadResource(roleId);
                Set<String> resourcesSet = roleResourceService.buildRole(resources);
                redisService.setRoleResource(roleId + "", resourcesSet);
            }
        }
    }

    @ApiOperation(value = "跳转级联设备登录，传入下级的guid")
    @GetMapping(value = "/cascade/redirectNew/{guid}")
    public Result redirectNew(HttpServletRequest request, HttpServletResponse response, @PathVariable("guid") String guid)  {
        if(StringUtils.isEmpty(guid)){
            return  this.result(ErrorCode.CASCADE_HASNOT_GUID);
        }
        Example example = new Example(Mapregion.class);
        example.createCriteria().andEqualTo("guid",guid);
        // 将生成的token存储入redis
        List<Mapregion> mapregions =  mapregionService.findByExample(example);
        if(mapregions.size() == 0){
            //未找到下级IP配置
            return  this.result(ErrorCode.CASCADE_MAPREGION_NOT);
        }
        Mapregion mapregion = mapregions.get(0);
        if(StringUtils.isEmpty(mapregion.getLoginName())){
            return  this.result(ErrorCode.CASCADE_HASNOT_USER);
        }
        // 获取登录用户
        User queryUser = new User();
        queryUser.setAccount(mapregion.getLoginName()); //当前只通过用户名查找
        User user = userService.findOneUser(queryUser);
        if(user ==  null){
            return  this.result(ErrorCode.USER_HASNOT_SALT);
        }
        String userId = String.valueOf(user.getId());
        String token = UUID.randomUUID().toString();
        stringRedisTemplate.opsForValue().set(token,userId,2,TimeUnit.MINUTES);
        //        String  userId = "31";
        String homePage = "/";
        if(StringUtils.isNotEmpty(mapregion.getUrl())) {
            homePage = mapregion.getUrl();
        }
        if(StringUtils.isNotEmpty(request.getParameter("url"))){
            homePage = request.getParameter("url");
        }
        int port = request.getServerPort();
        try {

                String  redirect = request.getScheme()+"://" + mapregion.getIp() +":"+ port + "/api-common/cascade/toLoginNew"
                        + "?token=" + URLEncoder.encode(token, UTF_8) + "&guid=" + URLEncoder.encode(guid, UTF_8)
                        + "&url=" + URLEncoder.encode(homePage, UTF_8)+ "&upip=" + URLEncoder.encode(mapregion.getUpIp(), UTF_8);
                logger.info("级联登录转向连接：" + LogForgingUtil.validLog(redirect));
                response.sendRedirect(CleanUtil.cleanString(redirect));
                return null;

        } catch (Exception e) {
            logger.error("",e);
        }
        return null;
    }

    @ApiOperation(value = "级联登录")
    @GetMapping(value = "/cascade/toLoginNew")
    public void toLoginNew(HttpServletRequest request, HttpServletResponse response) throws Exception  {
        // 参数非空校验
        String token = CleanUtil.cleanString(request.getParameter(TOKEN));
        String guid = request.getParameter("guid");
        String homePage = CleanUtil.cleanString(request.getParameter("url"));
        String upip = CleanUtil.cleanString(request.getParameter("upip"));
        // 拼装返回结果html
        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html>");
        builder.append("<html>");
        builder.append("<body>");
        builder.append("<form method=\"post\" id=\"login\">");
        builder.append("\t<input type=\"hidden\" token=\""+token+"\" upip=\""+upip+"\" url=\"" + homePage + "\">");
        builder.append("</form>");
        builder.append("</body>");
        builder.append("<script>");
        builder.append("document.getElementById('login').submit();");
        builder.append("</script>");
        builder.append("</html>");
        response.getWriter().write(builder.toString());
        return;
    }


    @ApiOperation(value = "级联登录")
    @PostMapping(value = "/cascade/toLoginNew")
    public Result toLoginNewPOST(HttpServletRequest request, HttpServletResponse response) {
        //调用其他平台的接口
        String token = request.getParameter(TOKEN);
        String guid = request.getParameter("guid");
        String homePage = request.getParameter("url");
        String upip = request.getParameter("upip");

        List<Mapregion> mapregions = mapregionService.getMapregions();
       // String upIp = mapregions.get(0).getUpIp();
        String url = request.getRequestURL().toString();
        String paramStr  = String.format("token=%s&guid=%s&",token,guid);
        String checkUrl = null;
        String responseStr = "";
        logger.info("参数信息："+ LogForgingUtil.validLog(paramStr) +";原始获取头信息URL：" + LogForgingUtil.validLog(url));
        if(StringUtils.isNotEmpty(url) && request.getScheme().toLowerCase(Locale.ENGLISH).startsWith("http") ){
            checkUrl = request.getScheme()+"://" + upip + ":"+request.getServerPort()+"/api-common/cascade/checkLoginNew";
            logger.info("转向HTTP验证：" + LogForgingUtil.validLog(checkUrl));
            responseStr = HttpRequestUtil.sendGet(checkUrl,paramStr);
        }
        if(StringUtils.isNotEmpty(url) && request.getScheme().toLowerCase(Locale.ENGLISH).startsWith("https") ){
            checkUrl = request.getScheme()+"://" + upip + ":"+request.getServerPort()+"/api-common/cascade/checkLoginNew?"+paramStr;
            logger.info("转向HTTPS验证：" + LogForgingUtil.validLog(checkUrl));
            responseStr = HttpRequestUtil.sendHttpsSslGet(checkUrl);
        }
        if(StringUtils.isEmpty(responseStr)){
            return  this.result(ErrorCode.CASCADE_LOGIN_TIMEOUT);
        }
        logger.info("返回结果：" + LogForgingUtil.validLog(responseStr));
        Map<String,Object> responseMap = JsonUtil.jsonToMap(responseStr);
        if(responseMap.containsKey("data")&&responseMap.get("data")!=null){
            Map<String,Object> map = (Map)responseMap.get("data");
            if(!map.containsKey("rtn") || (int)map.get("rtn") != 0){
                return this.result(ErrorCode.CASCADE_CALL_INTERFACE_FAILE);
            }
            int userId = (int)map.get(USER_ID);
            HttpSession session = request.getSession();
            // 证书校验通过,不进入授权导入页面
            session.setAttribute(LICENSEAUTHENTICATION_KEY, "true");
            User currentUser = userService.findById(Integer.valueOf(userId));

            // 通过检验,将用户信息保存到session中
            if (!loginService.sessionLogin(session, currentUser, 0)) {
                return this.result(ErrorCode.USER_NO_ROLE);
            }
            logger.info("===============跳转到配置页面");
            try {
                logger.info(LogForgingUtil.validLog(homePage));
                response.sendRedirect(CleanUtil.cleanString(homePage));
            } catch (IOException e) {
                logger.error(e.toString());
            }
        }

//        Map<String, String> resp = new HashMap<>();
//        session.removeAttribute(Global.SESSION.RETURN_URL);
//        resp.put("homepage", homePage);

        return null;
    }


    @ApiOperation(value = "用于验证级联登录，是否通过")
    @GetMapping(value = "/cascade/checkLoginNew")
    public Result checkLoginNew( HttpServletRequest httpServletRequest) {
        String token =  httpServletRequest.getParameter(TOKEN);
        String guid =    httpServletRequest.getParameter("guid");
        Map map = new HashMap<String,Object>();
        if(StringUtils.isEmpty(token) || StringUtils.isEmpty(guid)){
            map.put("rtn",-1);
            map.put(MESSAGE,"级联登录缺失请求参数");
            return this.vData(map);
        }

        String userIdStr = stringRedisTemplate.opsForValue().get(token);
        if(StringUtils.isEmpty(userIdStr)){
            return  this.result(ErrorCode.CASCADE_LOGIN_TIMEOUT);
        }
        int  userId = Integer.parseInt(userIdStr);
        List<Mapregion> mapregions = mapregionService.getMapregions();
      //  String checkGuid =  mapregions.get(0).getGuid();
        if(mapregions!=null ){
            User user = userService.findById(userId);
            map.put("rtn",0);
            map.put(MESSAGE,"级联登录验证成功");
            map.put(USER_ID,user.getId());
        }
        else{
            map.put("rtn",-1);
            map.put(MESSAGE,"级联登录验证失败");
        }
        return this.vData(map);
    }
}



