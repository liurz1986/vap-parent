package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.condition.VapOrZhyCondition;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.model.App;
import com.vrv.vap.admin.model.Resource;
import com.vrv.vap.admin.model.Role;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.vo.Menu;
import com.vrv.vap.admin.vo.RoleQuery;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Options;
import org.ehcache.Cache;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping(path = "/role")
@Conditional(VapOrZhyCondition.class)
public class RoleController extends ApiController {
    private static final String ROLE_ID = "roleId";
    @Autowired
    private RoleService roleService;

    @Autowired
    Cache<String, List<Menu>> menuCache;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private SystemConfigService systemConfigService;

    @Autowired
    private RoleOrgService roleOrgService;

    @Autowired
    private UserService userService;

    /**
     * 根据角色id获取角色信息
     */
    @ApiOperation(value = "根据角色id获取角色信息")
    @GetMapping("/{roleId}")
    @SysRequestLog(description = "根据角色id获取角色信息", actionType = ActionType.SELECT)
    public VData<Role> getUsersByRoleId( @PathVariable(ROLE_ID) Integer roleId){
       List<Role> roleList =  roleService.findByProperty(Role.class,"id",roleId);
       Role role = new Role();
       if(CollectionUtils.isNotEmpty(roleList))
       {
           role = roleList.get(0);
       }
        return  this.vData(role);
    }

    /**
     * 获取所有角色
     */
    @ApiOperation(value = "获取所有角色")
    @GetMapping
    @SysRequestLog(description = "获取所有角色", actionType = ActionType.SELECT)
    public VData<List<Role>> getAllRole(HttpServletRequest request) {
        Example example = new Example(Role.class);
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user != null) {
            List<Integer> roleIds = user.getRoleIds();
            // 三权开启时，非管理员用户只能看到同种三权且是自己创建的角色
            Short confEnable = systemConfigService.getThreePowerEnable();
            if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(user.getAccount())) {
                if (!CollectionUtils.isEmpty(roleIds)) {
                    Integer loginRoleId = roleIds.get(0);
                    Role role = roleService.findById(loginRoleId);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("threePowers",role.getThreePowers());
                    criteria.andIn("id",roleIds);
                    criteria.orEqualTo("creator",user.getId());
                }
            }
        }
        return this.vData(roleService.findByExample(example));
    }

    /**
     * 查询所有角色
     */
    @ApiOperation(value = "查询所有角色")
    @GetMapping("/all")
    @SysRequestLog(description = "查询所有角色", actionType = ActionType.SELECT)
    public VData<List<Role>> getRoles() {
        return this.vData(roleService.findAll());
    }

    @ApiOperation(value = "获取所有角色（保密员权限）")
    @GetMapping(value = "/permission")
    @SysRequestLog(description = "获取所有角色（保密员权限）", actionType = ActionType.SELECT)
    public VData<List<Role>> getAllPermissionRole(HttpServletRequest request) {
        Example example = new Example(Role.class);
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user != null) {
            List<Integer> roleIds = user.getRoleIds();
            // 三权开启时，非管理员用户只能看到同种三权且是自己创建的角色
            Short confEnable = systemConfigService.getThreePowerEnable();
            if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(user.getAccount())) {
                // 保密主管获取业务主管和运维主管下的角色
                com.vrv.vap.admin.model.User currentUser = userService.findById(user.getId());
                List<Role> roleList = roleService.getBusinessAndOperationRole(currentUser.getRoleId(),null);
                if (CollectionUtils.isNotEmpty(roleList)) {
                    return this.vData(roleList);
                }
                if (!CollectionUtils.isEmpty(roleIds)) {
                    Integer loginRoleId = roleIds.get(0);
                    Role role = roleService.findById(loginRoleId);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("threePowers",role.getThreePowers());
                    criteria.andIn("id",roleIds);
                    criteria.orEqualTo("creator",user.getId());
                }
            }
        }
        return this.vData(roleService.findByExample(example));
    }
    @ApiOperation(value = "获取所有角色（保密员权限）")
    @GetMapping(value = "/dealPermission")
    @SysRequestLog(description = "获取所有角色（保密员权限）", actionType = ActionType.SELECT)
    public VData<List<Role>> getAllPermissionRole(HttpServletRequest request,@RequestParam String dealType) {
        Example example = new Example(Role.class);
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user != null) {
            List<Integer> roleIds = user.getRoleIds();
            // 三权开启时，非管理员用户只能看到同种三权且是自己创建的角色
            Short confEnable = systemConfigService.getThreePowerEnable();
            if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(user.getAccount())) {
                // 保密主管获取业务主管和运维主管下的角色
                com.vrv.vap.admin.model.User currentUser = userService.findById(user.getId());
                List<Role> roleList = roleService.getBusinessAndOperationRole(currentUser.getRoleId(),dealType);
                if (CollectionUtils.isNotEmpty(roleList)) {
                    return this.vData(roleList);
                }
                if (!CollectionUtils.isEmpty(roleIds)) {
                    Integer loginRoleId = roleIds.get(0);
                    Role role = roleService.findById(loginRoleId);
                    Example.Criteria criteria = example.createCriteria();
                    criteria.andEqualTo("threePowers",role.getThreePowers());
                    criteria.andIn("id",roleIds);
                    criteria.orEqualTo("creator",user.getId());
                }
            }
        }
        return this.vData(roleService.findByExample(example));
    }

    /**
     * 获取当前用户可控制的角色
     */
    @ApiOperation(value = "获取可控制的角色")
    @GetMapping(value = "/control")
    @SysRequestLog(description = "获取可控制的角色", actionType = ActionType.SELECT)
    public VData<List<String>> getControlRole(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        List<Integer> roleIds = user.getRoleIds();
        List<String> controls = new ArrayList<>();
        for (Integer roleId : roleIds) {
            Role role = roleService.findById(roleId);
            if (role != null && StringUtils.isNotBlank(role.getControl())) {
                controls.add(role.getControl());
            }
        }
        return this.vData(controls);
    }

    /**
     * 新增角色
     */
    @ApiOperation(value = "新增角色")
    @SysRequestLog(description="新增角色", actionType = ActionType.ADD)
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    @PutMapping
    public Result addRole(@RequestBody Role role,HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user != null) {
            Short confEnable = systemConfigService.getThreePowerEnable();
            if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && Const.USER_ADMIN.equals(user.getAccount())){
                 return this.result(ErrorCode.THREE_POWER_ROLE_CREATE_WRONG);
            }
            List<Integer> roleIds = user.getRoleIds();
            if (!CollectionUtils.isEmpty(roleIds)) {
                Integer roleId = roleIds.get(0);
                Role loginRole = roleService.findById(roleId);
                // 保存角色三权及创建用户
                if (loginRole != null) {
                    role.setThreePowers(loginRole.getThreePowers());
                    role.setCreator(user.getId());
                }
            }
        }

        int result = roleService.save(role);
        if(StringUtils.isEmpty(role.getCode())){
            role.setCode(role.getId()+"");
            roleService.update(role);
        }
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(role,"添加角色");
            return this.vData(role);
        }
        return this.result(false);
    }

    /**
     * 修改角色
     */
    @ApiOperation(value = "修改角色")
    @SysRequestLog(description="修改角色", actionType = ActionType.UPDATE)
    @PatchMapping
    public Result editRole(@RequestBody Role roleOrg) {
        Role role = new Role();
        BeanUtils.copyProperties(roleOrg,role);
        role.setCode(StringUtils.isEmpty(role.getCode())?role.getId()+"":role.getCode());
        Role roleSrc = roleService.findById(role.getId());
        if (role.getStatus() != null && role.getStatus() == 1) {
            // 校验角色下是否存在用户，存在则无法禁用
            int count = userRoleService.queryCountByRole(String.valueOf(role.getId()));
            if (count > 0) {
                return this.result(ErrorCode.ROLE_USER_IN_USE);
            }
        }
        if (StringUtils.isEmpty(role.getOrgId())) {
            int result = roleService.updateSelective(role);
            if (result == 1) {
                SyslogSenderUtils.sendUpdateSyslog(roleSrc, role,"修改角色");
            }
            return this.result(result == 1);
        } else {
            int result1 = roleService.updateRoleOrg(role);
            if (result1 == 1) {
                SyslogSenderUtils.sendUpdateSyslog(roleSrc, role,"修改角色");
            }
            return this.result(result1 == 1);
        }
    }

    /**
     * 删除角色（支持批量）
     */
    @ApiOperation(value = "删除角色（支持批量）")
    @SysRequestLog(description="删除角色（支持批量）", actionType = ActionType.DELETE)
    @DeleteMapping
    public Result delRole(@RequestBody DeleteQuery deleteQuery) {
        com.vrv.vap.admin.model.User u = new com.vrv.vap.admin.model.User();
        // 校验角色下是否存在用户，存在则无法删除
        int count = userRoleService.queryCountByRole(deleteQuery.getIds());
        if (count > 0) {
            return this.result(ErrorCode.ROLE_HAS_USER);
        }
        List<Role> roles = roleService.findByids(deleteQuery.getIds());
        int result = roleService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            roles.forEach(role -> {
                SyslogSenderUtils.sendDeleteSyslog(role,"删除角色");
            });
            roleOrgService.deleteByRoleIds(deleteQuery.getIds().split(","));
        }
        return this.result(result > 0);
    }

    /**
     * 根据条件查询角色
     * 支持分页查询、条件查询 、任意字段排序
     */
    @ApiOperation(value = "根据条件查询角色")
    @SysRequestLog(description="根据条件查询角色", actionType = ActionType.SELECT)
    @PostMapping
    public Result queryRoles(@RequestBody RoleQuery roleQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user != null) {
            List<Integer> roleIds = user.getRoleIds();
            if (!CollectionUtils.isEmpty(roleIds)) {
                Integer roleId = roleIds.get(0);
                Role role = roleService.findById(roleId);
                if (role != null) {
                    // 三权开启时，非管理员用户只能看到同种三权且是自己创建的角色
                    Short confEnable = systemConfigService.getThreePowerEnable();
                    if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(user.getAccount())) {
                        roleQuery.setThreePowers(role.getThreePowers());
                        roleQuery.setCreator(user.getId());
                    }
                }
            }
        }
        Example example = this.pageQuery(roleQuery, Role.class);
        return this.vList(roleService.findByExample(example));
    }

    /**
     * 根据code获取角色信息
     */
    @ApiOperation(value = "根据code获取角色信息")
    @GetMapping("/getRoleInfo/{code}")
    public VData<Role> getRoleByCode(@PathVariable("code") String code) {
        Role role = new Role();
        Example example = new Example(Role.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("code", code);
        criteria.andNotEqualTo("status",2);
        List<Role> roleList = roleService.findByExample(example);
        if(CollectionUtils.isNotEmpty(roleList)) {
            role = roleList.get(0);
        }
        return  this.vData(role);
    }

    /**
     * 校验角色是否存在
     */
    @ApiOperation(value = "校验角色是否存在")
    @PostMapping(value = "/check")
    public Result checkCode(@RequestBody RoleQuery roleQuery) {
        Example example = this.pageQuery(roleQuery, Role.class);
        return this.vList(roleService.findByExample(example));
    }

    /**
     * 查询角色下的应用权限信息
     */
    @ApiOperation(value = "查询角色下的应用权限信息")
    @GetMapping(value = "/apps")
    @SysRequestLog(description = "查询角色下的应用权限信息", actionType = ActionType.SELECT)
    public Result app(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        return this.vData(roleResourceService.getRoleApps(user.getRoleIds()));
    }

    /**
     * 查询角色下的页面权限信息（type=3）
     */
    @ApiOperation(value = "查询角色下的页面权限信息")
    @GetMapping(value = "/rules")
    @SysRequestLog(description = "查询角色下的页面权限信息", actionType = ActionType.SELECT)
    public Result rule(HttpServletRequest request) {
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        return this.vData(roleResourceService.getRoleRules(user.getRoleIds()));
    }

    /**
     * 查询角色下的操作权限信息（type=4）
     */
    @ApiOperation(value = "查询角色下的操作权限信息")
    @GetMapping(value = "/widgets")
    @SysRequestLog(description = "查询角色下的操作权限信息", actionType = ActionType.SELECT)
    public Result widgets(HttpServletRequest request) {
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        List<Resource> list = roleResourceService.getRoleWidgets(user.getRoleIds());
        return this.vData(list);
    }

    /**
     * 修改角色下的权限信息
     */
    @ApiOperation(value = "修改角色下的权限信息")
    @SysRequestLog(description="修改角色下的权限信息", actionType = ActionType.UPDATE)
    @PutMapping(value = "/resource/{roleId}")
    public Result manageResource(@RequestBody Map<String, String> param, @ApiParam("角色ID") @PathVariable(ROLE_ID) Integer roleId) {
        String added = param.get("added");
        String deled = param.get("deled");
        // 统计新增、删除的角色权限
        String[] addList = new String[0];
        String[] delList = new String[0];
        if (!StringUtils.isEmpty(added)) {
            addList = added.split(",");
        }
        if (!StringUtils.isEmpty(deled)) {
            delList = deled.split(",");
        }
        if (addList.length == 0 && delList.length == 0) {
            return Global.OK;
        }
        // 保存修改信息
        boolean result = roleResourceService.managerRoleResource(roleId, addList, delList);
        if (result) {
            // 修改角色权限缓存
            redisService.clearRoleResource(Integer.parseInt(param.get(ROLE_ID)));
            List<Resource> resources = resourceService.loadResource(Integer.parseInt(param.get(ROLE_ID)));
            Set<String> resourcesSet = roleResourceService.buildRole(resources);
            redisService.setRoleResource(param.get(ROLE_ID), resourcesSet);
            menuCache.clear();
        }
        return this.result(result);
    }
}
