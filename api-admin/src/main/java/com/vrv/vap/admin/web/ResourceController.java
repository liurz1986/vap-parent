package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.condition.VapOrZhyCondition;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.ThreePowersUtil;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.model.Resource;
import com.vrv.vap.admin.model.Role;
import com.vrv.vap.admin.model.RoleResource;
import com.vrv.vap.admin.model.UserRole;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.vo.Menu;
import com.vrv.vap.admin.vo.ResourceQuery;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Options;
import org.ehcache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping(path = "/resource")
@Conditional(VapOrZhyCondition.class)
public class ResourceController extends ApiController {
    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    ResourceService resourceService;

    @Autowired
    Cache<String, List<Menu>> menuCache;

    @Autowired
    UserRoleService userRoleService;

    @Autowired
    RoleService roleService;

    @Autowired
    RoleResourceService roleResourceService;

    @javax.annotation.Resource
    private SystemConfigService systemConfigService;


    //     兼容（新）专用Build Admin菜单的方法
    private List<Menu> buildAdminMenu(List<Menu> menus, List<Resource> resources, Resource parent) {
        for (Resource resource : resources) {
            if (resource.getPuid()!= null && resource.getPuid().equals(parent.getUid())) {
                Menu menu = new Menu();
                menu.setId(String.valueOf(resource.getId()));
                menu.setPath(resource.getPath());
                menu.setIcon(resource.getIcon());
                menu.setName(resource.getTitle());
                menu.setSort(resource.getSort());
                menu.setType(resource.getType());
                menu.setSign(resource.getSign());
                menu.setParent(resource.getPuid());
                menu.setDevelopStatus(resource.getDevelopStatus()==null?0:resource.getDevelopStatus());
                if (resource.getType() == 2 && StringUtils.isNoneEmpty(resource.getPath())) {
                    String url = resource.getPath();
                    String[] ptns = url.split("#");
                    if(ptns.length==2){
                        menu.setRoot(ptns[0]);
                        menu.setRoute(ptns[1]);
                    }else{
                        menu.setRoot(parent.getPath());
                        menu.setRoute(resource.getPath());
                    }
                }else{
                    menu.setChildren(buildAdminMenu(new ArrayList<>(), resources, resource));
                }
                menus.add(menu);
            }

        }

        return menus;
    }

    //遍历所有菜单构造 MENU
    private List<Menu> getMenus(List<Menu> menus, List<Resource> resources, String rootType) {
        if ("0".equals(rootType)) {
            Resource res = new Resource();
            res.setUid("0");
            return this.buildAdminMenu(menus, resources, res);
        }

        for (Resource resource : resources) {
            if (rootType.equals(resource.getPuid()) && (resource.getType() == 1 || resource.getType() == 2)) {
                Menu menu = new Menu();
                menu.setId(String.valueOf(resource.getId()));
                menu.setPath(resource.getPath());
                menu.setIcon(resource.getIcon());
                menu.setName(resource.getTitle());
                menu.setSort(resource.getSort());
                menu.setType(resource.getType());
                menu.setSign(resource.getSign());
                menu.setParent(resource.getPuid());
                menu.setDevelopStatus(resource.getDevelopStatus());
                menu.setChildren(getMenus(new ArrayList<>(), resources, resource.getUid()));
                menus.add(menu);
            }
        }
        return menus;
    }

    /**
     * 获取所有资源
     */
    @ApiOperation(value = "获取所有资源", hidden = false)
    @GetMapping
    public List<Resource> getAll(HttpServletRequest request) {
        Example example = new Example(Resource.class);
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user != null) {
            List<Integer> roleIds = user.getRoleIds();
            Short confEnable = systemConfigService.getThreePowerEnable();
            if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString())) && !Const.USER_ADMIN.equals(user.getAccount())) {
                if (!CollectionUtils.isEmpty(roleIds)) {
                    Integer loginRoleId = roleIds.get(0);
                    Role role = roleService.findById(loginRoleId);
                    if (role != null) {
                        // 同一资源分配给多种三权时，threePowers为多种三权的和，角色三权组合包含资源三权即证明拥有该资源权限
                        List<Integer> threePowers = ThreePowersUtil.getRolePowers(role.getThreePowers());
                        example.createCriteria().andIn("threePowers", threePowers);
                    }
                }
            }
        }
        example.createCriteria().andEqualTo("disabled", 0);
        return resourceService.findByExample(example);
    }

    /**
     * 查询资源
     */
    @ApiOperation(value = "查询资源", hidden = false)
    @SysRequestLog(description="查询资源", actionType = ActionType.SELECT)
    @PostMapping
    public VList<Resource> query(@RequestBody ResourceQuery resourceQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(resourceQuery, Resource.class);
        List<Resource> list = resourceService.findByExample(example);
        return this.vList(list);
    }

    /**
     * 修改资源
     **/
    @ApiOperation(value = "修改资源", hidden = false)
    @PatchMapping
    @SysRequestLog(description="修改资源", actionType = ActionType.UPDATE)
    public Result update(HttpServletRequest request,@RequestBody Resource resource) {
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user != null) {
            List<Integer> roleIds = user.getRoleIds();
            // 校验三权是否勾选
            Short confEnable = systemConfigService.getThreePowerEnable();
            if (Const.THREE_POWER_ON.equals(Byte.valueOf(confEnable.toString()))) {
                Byte threePower = resource.getThreePowers();
                if (threePower == null) {
                    return this.result(ErrorCode.THREE_POWER_NOT_CHOOSE);
                }
                // 三权开启时，校验用户三权与勾选的三权是否一致
                if (!CollectionUtils.isEmpty(roleIds) && !Const.USER_ADMIN.equals(user.getAccount())) {
                    Integer loginRoleId = roleIds.get(0);
                    Role role = roleService.findById(loginRoleId);
                    if (role != null) {
                        List<Integer> threePowers = ThreePowersUtil.getRolePowers(role.getThreePowers());
                        if (threePowers.indexOf(threePower.intValue()) == -1) {
                            return this.result(ErrorCode.THREE_POWER_CHOOSE_WRONG);
                        }
                    }
                }
            }
        }
        Resource resourceSrc = resourceService.findById(resource.getId());
        boolean result = resourceService.update(resource) != 0;
        if (result) {
            SyslogSenderUtils.sendUpdateSyslog(resourceSrc,resource,"修改资源");
            menuCache.clear();
        }
        return this.result(result);
    }


    /**
     * 删除资源
     */
    @ApiOperation(value = "删除资源", hidden = false)
    @DeleteMapping
    @SysRequestLog(description="删除资源", actionType = ActionType.DELETE)
    public Result delResource(@RequestBody DeleteQuery deleteQuery) {
        // 校验是否存在角色拥有该资源，存在则不能删除
        int count = roleResourceService.queryCountByResource(deleteQuery.getIds());
        if (count > 0) {
            return this.result(ErrorCode.RESOURCE_HAS_ROLE);
        }
        List<Resource> resources = resourceService.findByids(deleteQuery.getIds());
        int result = resourceService.deleteByIds(deleteQuery.getIds());
        if (result == 1) {
            resources.forEach(resource -> {
                SyslogSenderUtils.sendDeleteSyslog(resource,"删除资源");
            });
        }
        String ids = deleteQuery.getIds();
        String[] resourceIds = ids.split(",");
        roleResourceService.deleteByResourceIds(resourceIds);
        menuCache.clear();
        return this.result(result > 0);
    }


    /**
     * 添加资源
     */
    @ApiOperation(value = "添加资源", hidden = false)
    @PutMapping
    @SysRequestLog(description="添加资源", actionType = ActionType.ADD)
    @Options(useGeneratedKeys=true, keyProperty="id", keyColumn="id")
    public Result addResource(HttpServletRequest request,@RequestBody Resource resource) {
        resource.setUid(Uuid.uuid());
        int result = resourceService.save(resource);
        Integer resourceId = resource.getId();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
         List<Integer> roleIds = user.getRoleIds();
         for(Integer roleId : roleIds){
             RoleResource roleResource = new RoleResource();
             roleResource.setResourceId(resourceId);
             roleResource.setRoleId(roleId);
             roleResourceService.save(roleResource);
         }
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(resource,"添加资源");
            menuCache.clear();
            return this.vData(resource);
        }
        return this.result(false);
    }

    /**
     * 修改资源顺序
     **/
    @ApiOperation(value = "修改资源顺序", hidden = false)
    @PatchMapping(value = "/sort")
    @SysRequestLog(description="修改资源顺序", actionType = ActionType.UPDATE)
    public Result sort(@RequestBody List<Resource> resources) {
        for (Resource resource : resources) {
            Resource resourceSrc = resourceService.findById(resource.getId());
            int result = resourceService.updateSelective(resource);
            if (result == 1) {
                SyslogSenderUtils.sendUpdateSyslog(resourceSrc,resource,"修改资源顺序");
            }
        }
        menuCache.clear();
        return Global.OK;
    }

    /**
     * 根据当前登录的用户角色查询资源
     */
    @ApiOperation(value = "根据当前登录的用户角色查询资源", hidden = false)
    @GetMapping(value = "/byRole")
    @SysRequestLog(description="根据角色查询资源", actionType = ActionType.SELECT)
    public VData<List<Resource>> queryByRoleIds(HttpServletRequest request) {
        SyslogSenderUtils.sendSelectSyslog();
        List<Resource> list = new ArrayList();
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (user == null) {
            return this.vData(false);
        }
        if (Const.USER_ADMIN.equals(user.getAccount())) {
            Example example = new Example(Resource.class);
            example.createCriteria().andEqualTo("disabled", 0);
            return this.vData(resourceService.findByExample(example));
        }
        List<UserRole> urs = userRoleService.findByProperty(UserRole.class, "userId", user.getId());
        List<Integer> roleIdList = urs.stream().map(userRole -> userRole.getRoleId()).sorted((a, b) -> b - a).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(roleIdList)) {
            list = resourceService.loadResourceByRoleIds(roleIdList);
        }
        return this.vData(list);
    }


    /**
     * 根据角色查询资源
     */
    @ApiOperation(value = "根据角色查询资源", hidden = false)
    @GetMapping(value = "/byRole/{roleId}")
    public VData<List<Resource>> queryByRoleId(@PathVariable String roleId) {
        List<Resource> list = resourceService.loadResource(Integer.parseInt(roleId));
        return this.vData(list);
    }






    /**
     * 获取管理平台菜单
     */
    @ApiOperation(value = "获取平台菜单,pid=0:管理平台，-1：展示平台,-2:大屏平台")
    @GetMapping(value = "/menus/{pid}")
    public VData<List<Menu>> getAdminMenus(HttpServletRequest request, @PathVariable("pid") Integer pid) {
        User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
        if (pid < -2 || pid > 0) {
            return this.vData(ErrorCode.RESOURCE_NULL);
        }
        // 缓存中存在则取缓存
        List<UserRole> urs = userRoleService.findByProperty(UserRole.class, "userId", user.getId());
        List<Integer> roleIds = urs.stream().map(userRole -> userRole.getRoleId()).sorted((a, b) -> b - a).collect(Collectors.toList());
        String key = pid + StringUtils.join(roleIds);
        if (menuCache.containsKey(key)) {
            return this.vData(menuCache.get(key));
        }
        List<Resource> res = resourceService.loadResourceByRoleIds(roleIds);
        res.sort((a, b) -> a.getSort() - b.getSort());
        List<Menu> menus = getMenus(new ArrayList<>(), res, String.valueOf(pid));
        menuCache.put(key, menus);
        return this.vData(menus);
    }



    /**
     * 导出所有SQL
     * */
    @ApiOperation(value = "导出所有SQL",hidden = false)
    @GetMapping("/export")
    @SysRequestLog(description="导出所有SQL", actionType = ActionType.EXPORT, manually = false)
    public void exportAll(HttpServletResponse response){
        List<Resource> resourceList = resourceService.findAll();
        List<RoleResource> roleResourceList = roleResourceService.findAll();
        List<String> resourceSql = genterateResourceSql(resourceList);
        List<String> rolwResourceSql = genterateRoleResourceSql(roleResourceList);
        export(response,resourceSql,rolwResourceSql);
    }

    /**
     * 导出出货版本SQL
     * */
    @ApiOperation(value = "导出出货版本SQL",hidden = false)
    @GetMapping("/export/products")
    @SysRequestLog(description="导出出货版本SQL", actionType = ActionType.EXPORT, manually = false)
    public void exportProducts(HttpServletResponse response){
        List<Resource> resourceList = resourceService.findAll();
        //disabled:0 不禁用 1 禁用  ; progress 3 出货
        resourceList = resourceList.stream()
                .filter(p->p.getDisabled() == 0 && p.getProgress()!=null&& p.getProgress() == 3)
                .collect(Collectors.toList());
        List<RoleResource> roleResourceList = roleResourceService.findAll();
        List<String> resourceSql = genterateResourceSql(resourceList);
        List<String> rolwResourceSql = genterateRoleResourceSql(roleResourceList);
        export(response,resourceSql,resourceSql);
    }


    private byte[] createZip(List<String> resourceSql, List<String> rolwResourceSql) {
        StringBuilder stringBuilder = new StringBuilder();
        Optional<String> rsqlStr = resourceSql.stream().reduce((a, b)->a+"\n"+b);
        Optional<String> rolersqlStr = rolwResourceSql.stream().reduce((a, b)->a+"\n"+b);
        if(rsqlStr.isPresent()){
            stringBuilder.append(rsqlStr.get());
            stringBuilder.append("\n");
        }
        if(rolersqlStr.isPresent()){
            stringBuilder.append(rolersqlStr.get());
            stringBuilder.append("\n");
        }
        logger.info("导出SQL语句");
        logger.info(stringBuilder.toString());
        ByteArrayOutputStream byteArrayOutputStream = null;
        ZipOutputStream zipOutputStream = null;
        byte[] arrayOfByte = null;
        try {
            byteArrayOutputStream = new ByteArrayOutputStream();
            zipOutputStream = new ZipOutputStream(byteArrayOutputStream);
            zipOutputStream.putNextEntry(new ZipEntry("sql.txt"));
            zipOutputStream.write(stringBuilder.toString().getBytes("UTF-8"));//这里采用gbk方式压缩，如果采用编译器默认的utf-8，这里就直接getByte();
            zipOutputStream.closeEntry();
            arrayOfByte = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            logger.error("",e);
        } finally {
            if (zipOutputStream != null) {
                try {
                    zipOutputStream.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
            if (byteArrayOutputStream != null) {
                try {
                    byteArrayOutputStream.close();
                } catch (IOException e) {
                    logger.error("",e);
                }
            }
        }
        return arrayOfByte;

    }


    private  void  export(HttpServletResponse response,List<String> resourceSql, List<String> rolwResourceSql){
        response.setCharacterEncoding("utf-8");
        // response.setContentType("application/octet-stream");
        String downloadName = "initsql.zip";
        //将文件进行打包下载
        try {
            OutputStream out = response.getOutputStream();
            byte[] data = createZip(resourceSql,rolwResourceSql);//服务器存储地址
            response.reset();
            response.setHeader("Content-Disposition","attachment;fileName="+downloadName);
            response.setContentLength(data != null ? data.length : 0);
            response.setContentType("application/octet-stream;charset=UTF-8");
            IOUtils.write(data, out);
            IOUtils.closeQuietly(out);
            out.flush();
            out.close();
        } catch (Exception e) {
            logger.error("",e);
        }
    }


    private  List<String> genterateResourceSql(List<Resource> resourceList){
        List<String> sqlList = new ArrayList<>();
        resourceList.forEach(p->{
            String sql = "INSERT INTO  `resource` (`id`, `name`, `title`, `icon`, `type`, `path`, `sort`, `service_id`, `parent`, `sign`, `disabled`, `uid`, `puid`, `progress`, `place`, `version_code`, `three_powers`,`develop_status`) SELECT '"+p.getId()+"', '"+p.getName()+"', '"+p.getTitle()+"', '"+p.getIcon()+"', '"+p.getType()+"', '"+p.getPath()+"', '"+p.getSort()+"', '"+p.getServiceId()+"', '0', '"+p.getSign()+"', '"+p.getDisabled()+"', '"+p.getUid()+"', '"+p.getPuid()+"', '"+p.getProgress()+"', '"+p.getPlace()+"', '"+p.getVersionCode()+"', '"+p.getThreePowers()+"', '" +p.getDevelopStatus() + "' FROM DUAL WHERE NOT EXISTS (SELECT * FROM resource WHERE id ='"+p.getId()+"');";
            sqlList.add(sql);
        });
        return  sqlList;
    }


    private  List<String> genterateRoleResourceSql(List<RoleResource> roleResourceList){
        List<String> sqlList = new ArrayList<>();
        roleResourceList.forEach(p->{
            String sql = "INSERT INTO `role_resource` ( `role_id`, `resource_id`) SELECT  '"+p.getRoleId()+"', '"+p.getResourceId()+"' FROM DUAL WHERE NOT EXISTS (SELECT * FROM `role_resource` WHERE role_id= '"+p.getRoleId()+"' and resource_id ='"+p.getResourceId()+"');";
            sqlList.add(sql);
        });
        return  sqlList;
    }








}
