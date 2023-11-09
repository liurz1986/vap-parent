package com.vrv.vap.admin.service.impl;

import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.util.CtyUtil;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.common.util.HttpRequestUtil;
import com.vrv.vap.admin.common.util.WebSocketClientUtil;
import com.vrv.vap.admin.mapper.UserMapper;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.RolePersonQuery;
import com.vrv.vap.admin.vo.UserQuery;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Options;
import org.java_websocket.WebSocket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by CodeGenerator on 2018/03/21.
 */
@Service
@Transactional
public class UserServiceImpl extends BaseServiceImpl<User> implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private static final String SIMPLE_NAME = "simpleName";
    private static final String ORGANIZATION_ID = "organizationId";
    @Resource
    private UserMapper userMapper;

    @Autowired
    private BaseKoalOrgService baseKoalOrgService;

    @Autowired
    private BaseSecurityDomainService baseSecurityDomainService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserDomainService userDomainService;

    @Autowired
    private UserOrgService userOrgService;

    @Autowired
    private RoleService roleService;


    @Value("${vap.cty.token}")
    private String ctyToken;

    @Value("${vap.cty.userUrl}")
    private  String userUrl;

    @Value("${vap.cty.organizationUrl}")
    private  String organizationUrl;

    @Value("${vap.xc.appName:xxx}")
    private String appName;

    @Value("${vap.xc.appId:xxx}")
    private String appId;

    @Value("${vap.xc.wsUri:xxx}")
    private String wsUri;

    @Value("${vap.xc.action:xxx}")
    private String action;

    @Value("${vap.xc.validateTokenUrl:127.0.0.1}")
    private String validateTokenUrl;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public List<User> queryUser(UserQuery query) {
        String[] roleids = query.getRoleId().split(",");
        String[] orgSubs = new String[0];
        if (StringUtils.isNotEmpty(query.getOrgSub())) {
            orgSubs = query.getOrgSub().split(",");
            Arrays.stream(orgSubs).forEach(p -> p = ("," + p + ","));
        }
        return userMapper.queryUser(query, Arrays.asList(roleids),Arrays.asList(orgSubs));
    }

    @Override
    public boolean updateOrg(List<Integer> userIds, BaseKoalOrg baseKoalOrg) {
        User user = new User();
        user.setOrgCode(baseKoalOrg.getCode());
        user.setOrgName(baseKoalOrg.getName());
        user.setIsLeader((byte)0);
        Example example = new Example(User.class);
        example.createCriteria().andIn("id",userIds);
        int result = userMapper.updateByExampleSelective(user,example);
        return result > 0;
    }


    @Override
    public boolean updateArea(List<Integer> userIds, BaseArea baseArea) {
        int result = userMapper.updateArea(userIds, baseArea);
        return result > 0;
    }

    @Override
    public User findOneUser(User user) {
        List<User> users = userMapper.select(user);
        if(users == null || users.size() == 0) {
            return null;
        }
        users = users.stream().filter(p->!Const.USER_STATUS_DEL.equals(p.getStatus())).collect(Collectors.toList());
        if(users == null || users.size() == 0) {
            return null;
        }
        return users.get(0);
    }

    @Override
    public Integer deleteUserByIds(String ids) {
        List<User> users = userMapper.selectByIds(ids);
        if(users == null) {
            return 0;
        }
        users.stream().forEach(p->{
            p.setStatus(Const.USER_STATUS_DEL);
            userMapper.updateByPrimaryKey(p);
        });
        return users.size();
    }

    @Override
    public List<User> getUserNotBuildIn(String monitorRoleCode) {
        return userMapper.getUserNotBuildIn(monitorRoleCode);
    }


    /**
     * 获取楚天云机构信息
     * */
    @Override
    @Transactional
    public  boolean getCtyOrganizations(String token,String url){
        try {
            String  param="page=1&limit=10000&token="+token;
            String  result= HttpRequestUtil.sendGet(url,param);
            List<Map<String,Object>> arrList= CtyUtil.stringToList(result);
            for(Map<String,Object> map : arrList){
                BaseKoalOrg baseKoalOrg=new BaseKoalOrg();
                BaseSecurityDomain baseSecurityDomain=new BaseSecurityDomain();
                int code1=0;
                if(map.containsKey("name")) {
                    baseKoalOrg.setName(map.get("name").toString());
                }
                if(map.containsKey(SIMPLE_NAME)) {
                    baseKoalOrg.setShortName(map.get(SIMPLE_NAME).toString());
                }
                baseSecurityDomain.setDomainName(map.get(SIMPLE_NAME).toString());
                if(map.containsKey(ORGANIZATION_ID)){
                    double code=Double.parseDouble(map.get(ORGANIZATION_ID).toString());
                    code1=(int) code;
                    baseKoalOrg.setCode(code1+"");
                    baseSecurityDomain.setCode(code1+"");
                }

                if(map.containsKey("parentId")){
                    double parentCode=Double.parseDouble(map.get(ORGANIZATION_ID).toString());
                    int parentCode1=(int) parentCode;
                    baseKoalOrg.setParentCode(parentCode1+"");
                    baseSecurityDomain.setParentCode(parentCode1+"");
                }else{
                    baseKoalOrg.setParentCode("0");
                    baseSecurityDomain.setParentCode("1");
                }

                BaseKoalOrg baseKoalOrg1=baseKoalOrgService.findByCode(code1+"");

                if(baseKoalOrg1!=null){
                    baseKoalOrg.setUuId(baseKoalOrg1.getUuId());
                    baseKoalOrgService.update(baseKoalOrg);
                }else{
                    baseKoalOrgService.save(baseKoalOrg);
                    baseSecurityDomainService.save(baseSecurityDomain);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return  true;
    }

    /**
     * 获取楚天云用戶信息
     * */
    @Override
    public  boolean getCtyUserInfo(String token,String url){
        try {
            String  param="page=1&limit=10000&token="+token;
            String  result= HttpRequestUtil.sendGet(url,param);
            List<Map<String,Object>> arrList= CtyUtil.stringToList(result);
            for(Map<String,Object> map : arrList){
                User user=new User();
                /// UserExtend  userExtend=new UserExtend();
                UserRole userRole=new UserRole();
                double userId=Double.parseDouble(map.get("userId").toString());
                user.setCtyId((int) userId);
                //userExtend.setOtherId((int) userId);
                user.setSalt((String)map.get("pwdSalt"));
                user.setName((String)map.get("trueName"));
                user.setAccount((String)map.get("username"));
                user.setPassword((String)map.get("password"));

                if(map.get(ORGANIZATION_ID) != null){
                    double id=Double.parseDouble(map.get(ORGANIZATION_ID).toString());
                    int idInt=(int) id;
                    BaseKoalOrg baseKoalOrg=baseKoalOrgService.findByCode(String.valueOf(idInt));
                    user.setOrgCode(baseKoalOrg.getCode());
                    user.setOrgName(baseKoalOrg.getShortName());
                    user.setDomainCode(baseKoalOrg.getCode());
                }
                if(map.get("allOrg") != null){
                    double id=Double.parseDouble(map.get("allOrg").toString());
                    int idInt=(int) id;
                    BaseKoalOrg baseKoalOrg=baseKoalOrgService.findByCode(String.valueOf(idInt));
                    user.setOrgCode(baseKoalOrg.getCode());
                    user.setOrgName(baseKoalOrg.getShortName());
                    user.setDomainCode(baseKoalOrg.getCode());
                }
                if(url.equals(userUrl)) {
                    user.setRoleId("100");
                    userRole.setRoleId(100);
                }else{
                    user.setRoleId("101");
                    userRole.setRoleId(101);
                }
                //楚天云用户均挂到系统管理员31下
                user.setCreator(31);
                byte allBitsOne = (byte)0x00;
                user.setStatus(allBitsOne);
                List<User> userList=queryByCtyId((int) userId);
                if(userList.size()==0){
                    save(user);
                    User user1=userMapper.queryByCtyId((int) userId).get(0);
                    userRole.setUserId(user1.getId());
                    userRoleService.save(userRole);
                    // userExtendService.save(userExtend);
                }else if(!userList.get(0).getRoleId().equals(user.getRoleId())){
                    save(user);
                    User user1=userMapper.queryByCtyId((int) userId).get(0);
                    userRole.setUserId(user1.getId());
                    userRoleService.save(userRole);
                }else{
                    user.setId(userList.get(0).getId());
                    update(user);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return  true;
    }

    @Override
    public Integer updateUserRole(User user) {
        // 修改之前的角色
        List<UserRole> existedRoles = userRoleService.findByProperty(UserRole.class, "userId", user.getId());
        Map<Integer, Integer> alreadys = new HashMap<>();
        for (UserRole ur : existedRoles) {
            alreadys.put(ur.getRoleId(), ur.getId());
        }
        Set<Integer> already = alreadys.keySet();
        // 修改之后的角色
        Set<Integer> newRole = new HashSet<>();
        if (StringUtils.isNotBlank(user.getRoleId())) {
            for (String roleStr : user.getRoleId().split(",")) {
                newRole.add(Integer.parseInt(roleStr));
            }
        }
        // 修改前后取交集，统计出新增的和删除的角色
        List<Integer> stroge = (List<Integer>) CollectionUtils.intersection(already, newRole);
        List<UserRole> toAdd = new ArrayList<>();
        List<Integer> toDelete = new ArrayList<>();
        for (Integer roleId : already) {
            if (!stroge.contains(roleId)) {
                toDelete.add(alreadys.get(roleId));
            }
        }
        for (Integer roleId : newRole) {
            if (!stroge.contains(roleId)) {
                UserRole ur = new UserRole();
                ur.setUserId(user.getId());
                ur.setRoleId(roleId);
                toAdd.add(ur);
            }
        }
        // 保存修改数据
        int result = userMapper.updateByPrimaryKeySelective(user);
        if (result == 1) {
            if (toAdd.size() > 0) {
                userRoleService.save(toAdd);
            }
            if (toDelete.size() > 0) {
                userRoleService.deleteByIds(StringUtils.join(toDelete.toArray(), ","));
            }
        }
        return result;
    }


    @Override
    public Integer updateUserDomain(User user) {
        // 修改前的安全域
        List<UserDomain> existedDomains = userDomainService.findByProperty(UserDomain.class, "userId", user.getId());
        Map<String, Integer> alreadys = new HashMap<>();
        for (UserDomain ud : existedDomains) {
            alreadys.put(ud.getDomainCode(), ud.getId());
        }
        Set<String> already = alreadys.keySet();
        // 修改后的安全域
        Set<String> newDomain = new HashSet<>();
        if (StringUtils.isNotBlank(user.getDomainCode())) {
            for (String domainStr : user.getDomainCode().split(",")) {
                newDomain.add(domainStr);
            }
        }
        // 修改前后取交集，统计新增和删除的安全域
        List<String> stroge = (List<String>) CollectionUtils.intersection(already, newDomain);
        List<UserDomain> toAdd = new ArrayList<>();
        List<Integer> toDelete = new ArrayList<>();
        for (String domainCode : already) {
            if (!stroge.contains(domainCode)) {
                toDelete.add(alreadys.get(domainCode));
            }
        }
        for (String domainCode : newDomain) {
            if (!stroge.contains(domainCode)) {
                UserDomain ud = new UserDomain();
                ud.setUserId(user.getId());
                ud.setDomainCode(domainCode);
                toAdd.add(ud);
            }
        }
        // 保存数据
        int result = userMapper.updateByPrimaryKeySelective(user);
        if (result == 1) {
            if (toAdd.size() > 0) {
                userDomainService.save(toAdd);
            }
            if (toDelete.size() > 0) {
                userDomainService.deleteByIds(StringUtils.join(toDelete.toArray(), ","));
            }
        }
        return result;
    }

    @Override
    public Integer updateUserOrg(User user) {
        // 修改前的组织机构
        List<UserOrg> existedOrgs = userOrgService.findByProperty(UserOrg.class,"userId",user.getId());
        Set<Integer> already = new HashSet<>();
        for (UserOrg orgUser : existedOrgs) {
            already.add(orgUser.getOrgId());
        }
        // 修改后的组织机构
        Set<Integer> newOrg = new HashSet<>();
        if (StringUtils.isNotBlank(user.getOrgId())) {
            for (String orgId : user.getOrgId().split(",")) {
                newOrg.add(Integer.valueOf(orgId));
            }
        }
        // 修改前后取交集，统计新增和删除的组织机构
        List<String> stroge = (List<String>) CollectionUtils.intersection(already, newOrg);
        List<UserOrg> toAdd = new ArrayList<>();
        List<Integer> toDelete = new ArrayList<>();
        for (UserOrg orgUser : existedOrgs) {
            if (!stroge.contains(orgUser.getOrgId())) {
                toDelete.add(orgUser.getId());
            }
        }
        for (Integer orgId : newOrg) {
            if (!stroge.contains(orgId)) {
                UserOrg orgUser = new UserOrg();
                orgUser.setUserId(user.getId());
                orgUser.setOrgId(orgId);
                toAdd.add(orgUser);
            }
        }
        // 保存数据
        int result = userMapper.updateByPrimaryKeySelective(user);
        if (result == 1) {
            if (toAdd.size() > 0) {
                userOrgService.save(toAdd);
            }
            if (toDelete.size() > 0) {
                userOrgService.deleteByIds(StringUtils.join(toDelete.toArray(), ","));
            }
        }
        return result;
    }

    @Override
    public List<User> queryByCtyId(Integer ctyId){
        return  userMapper.queryByCtyId(ctyId);
    }


    @Override
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    public int insertUserList(List<User> userList) {
        return userMapper.insertList(userList);
    }

    @Override
    public void deleteAllUser() {
        List<User> userList = userMapper.selectAll();
        if (CollectionUtils.isNotEmpty(userList)) {
            for (User user : userList) {
                userMapper.deleteByPrimaryKey(user.getId());
            }
        }
    }

    @Override
    public String getXcToken() {
        String token = "";
        try {
            log.info("获取token请求地址：" + wsUri);
            WebSocketClientUtil webSocketClientUtil = new WebSocketClientUtil(wsUri);
            Map<String,Object> map = new HashMap<>();
            map.put("appName",appName);
            map.put("appId",appId);
            map.put("action",action);
            log.info("获取token请求参数：" + JSON.toJSONString(map));
            if (StringUtils.isNotEmpty(wsUri) && wsUri.startsWith("wss")) {
                WebSocketClientUtil.trustAllHosts(webSocketClientUtil);
            }
            webSocketClientUtil.connect();
            log.info("连接状态为：" +webSocketClientUtil.getReadyState());
            for (int j = 1;j <= 3;j++) {
                if (webSocketClientUtil.getReadyState() != WebSocket.READYSTATE.OPEN) {
                    Thread.sleep(1000);
                    log.info("连接状态为：" +webSocketClientUtil.getReadyState());
                }
            }
            webSocketClientUtil.setToken("");
            webSocketClientUtil.getConnection().send(JSON.toJSONString(map));
            for (int i = 1; i <= 3;i++) {
                token = webSocketClientUtil.getToken();
                log.info("token ：" + token);
                if (StringUtils.isEmpty(token)) {
                    Thread.sleep(1000);
                } else {
                    break;
                }
            }
            log.info("token is：" + token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return token;
    }

    @Override
    public String validateXcToken(String token) {
        String result = "";
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        map.put("appName",appName);
        map.put("appId",appId);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        String requestParam = JSON.toJSONString(map);
        log.info("验证token请求参数：" + requestParam);
        try {
            log.info("验证token请求地址：" + validateTokenUrl);
            result = HTTPUtil.POST(validateTokenUrl,headers,requestParam);
            log.info("验证结果：" + LogForgingUtil.validLog(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String validationToken(String token, String appName, String appId) {
        String result = "";
        Map<String,Object> map = new HashMap<>();
        map.put("token",token);
        map.put("appName",appName);
        map.put("appId",appId);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        String requestParam = JSON.toJSONString(map);
        log.info("验证token请求参数：" + requestParam);
        try {
            log.info("验证token请求地址：" + validateTokenUrl);
//            result = "{\"userName\":\"zhangsan\",\"userRealName\":\"张三\",\"currentUserBiosName\":\"zhangsan@SOCID\",\"card\":\"xxxxxx\",\"stateCode\":\"001\",\"adminCode\":\"001\",\"message\":\" token is valid\"}";
            result = HTTPUtil.POST(validateTokenUrl,headers,requestParam);
            log.info("验证结果：" + LogForgingUtil.validLog(result));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public Page<User> queryUserByRoleOrPerson(RolePersonQuery query) {
        return userMapper.queryUserByRoleOrPerson(query);
    }

    @Override
    public List<User> getBusinessAndOperationUser(String roleId,String dealType) {
        List<User> userList = new ArrayList<>();
        String[] roleIds = roleId.split(",");
        boolean exist = false;
        for (String id : roleIds) {
            Role role = roleService.findById(Integer.valueOf(id));
            if (role != null) {
                String roleCode = role.getCode();
                if (Const.SECRET_MGR.equals(roleCode)) {
                    exist = true;
                }
            }
        }
        if (exist) {
            List<User> businessUsers = this.getUserByRoleCode(Const.BUSINESS_MGR);
            userList.addAll(businessUsers);
            List<User> operationUsers = this.getUserByRoleCode(Const.OPERATION_MGR);
            userList.addAll(operationUsers);
            //20231017调整：策略配置选择需要增加保密主管，督促不需要，用的新接口，这样避免改原来的接口出现新的bug
            if("strategy".equals(dealType)){
                List<User> secretUsers = this.getUserByRoleCode(Const.SECRET_MGR);
                userList.addAll(secretUsers);
            }
        }
        return userList;
    }


    public List<User> getUserByRoleCode(String roleCode) {
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
                result = userMapper.selectByExample(example);
                result.stream().forEach(p->p.setPassword(""));
            }
        }
        return result;
    }
}
