package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.AppRoleService;
import com.vrv.vap.admin.service.AppService;
import com.vrv.vap.admin.service.SysAppPrivilegeService;
import com.vrv.vap.admin.service.SysPrivilegeService;
import com.vrv.vap.admin.vo.AppIconMenu;
import com.vrv.vap.admin.vo.AppQuery;
import com.vrv.vap.admin.vo.AppRoleVO;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


@RequestMapping(path = "/app")
@RestController
public class AppController extends ApiController {


    @Autowired
    private AppService appService;

    @Autowired
    private AppRoleService appRoleService;

    @Autowired
    private SysPrivilegeService sysPrivilegeService;

    @Autowired
    private SysAppPrivilegeService sysAppPrivilegeService;



    /**
     * 添加应用
     */
    @ApiOperation(value = "添加应用`")
    @PutMapping
    public VData<App> addApp(@RequestBody App appOrg) {
        App app = new App();
        BeanUtils.copyProperties(appOrg,app);
        app.setCreatetime(new Date());
        app.setThird((byte) 0);
        if (StringUtils.isNotBlank(app.getUrl()) && (app.getUrl().startsWith("http://") || app.getUrl().startsWith("https://"))) {
            app.setThird((byte) 1);
            app.setClientid(Uuid.uuid());
            app.setClientsecret(Uuid.uuid());
            app.setScope("select");
            app.setAuthorizedgranttypes("client_credentials,refresh_token");
        }
        int result = appService.save(app);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(app, "添加应用");
            if(app.getThird()==1){
                Integer id = app.getId();
                sysAppPrivilegeService.insertBuiltIn(id);
            }
            return this.vData(app);
        }
        return this.vData(false);
    }

    /**
     * 修改应用密钥
     */
    @PatchMapping(value = "/secert")
    @ApiOperation(value = "修改密钥`")
    public VData<App> editAppSecert(@RequestBody App app) {
        App tmpApp = appService.findById(app.getId());
        String newAppSecert = Uuid.uuid();
        tmpApp.setClientsecret(newAppSecert);
        tmpApp.setUpdatetime(new Date());
        int result = appService.updateSelective(tmpApp);
        if (result == 1) {
            return this.vData(tmpApp);
        }
        return this.vData(Global.ERROR);
    }


    /**
     * 修改应用
     */
    @PatchMapping
    @ApiOperation(value = "修改应用`")
    public Result editApp(@RequestBody App app) {
        App tmpApp = appService.findById(app.getId());
        App appSrc = new App();
        BeanUtils.copyProperties(tmpApp,appSrc);

        tmpApp.setIcon(app.getIcon());
        tmpApp.setName(app.getName());
        tmpApp.setStatus(app.getStatus());
        tmpApp.setUrl(app.getUrl());
        tmpApp.setUpdatetime(new Date());
        tmpApp.setParentId(app.getParentId());
        tmpApp.setFolder(app.getFolder());
        int result = appService.updateSelective(tmpApp);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(appSrc, tmpApp, "修改应用");
        }
        return this.result(result == 1);
    }

    /**
     * 删除应用，支持批量删除
     */
    @ApiOperation(value = "删除应用，支持批量删除`")
    @DeleteMapping
    public Result delApp(@RequestBody DeleteQuery deleteQuery) {
        List<App> apps = appService.findByids(deleteQuery.getIds());
        String[] ids = deleteQuery.getIds().split(",");
        List<Integer> appIds = new ArrayList<>();
        for(String id:ids){
            appIds.add(Integer.parseInt(id));
        }
        sysAppPrivilegeService.deleteByAppIds(appIds);
        int result = appService.deleteByIds(deleteQuery.getIds());
        if (result == 1) {
            apps.forEach(app -> {
                SyslogSenderUtils.sendDeleteSyslog(app, "删除应用");
            });
        }
        return this.result(result > 0);
    }

    /**
     * 查询应用（分页）
     */
    @ApiOperation(value = "查询应用（分页）")
    @PostMapping
    public VList<App> queryApp(@RequestBody AppQuery appvo) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(appvo, App.class);
        List<App> list = appService.findByExample(example);
        return this.vList(list);
    }

    /**
     * 用户获取个人应用信息
     */
    @ApiOperation(value = "用户获取个人应用信息")
    @GetMapping(produces = {"application/json"})
    public String getCurrUserApps() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        List<AppIconMenu> appIconMenuList = null;
        Object appMenuJson = request.getSession().getAttribute(Global.SESSION.USER_APP);
        if (appMenuJson != null && !("".equals(appMenuJson))) {
            return appMenuJson.toString();
        } else {
            User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
            appIconMenuList = appService.buildAppIconMenu(user.getId(), user.getRoleIds());
            String jsonStr = JSON.toJSONString(appIconMenuList);
            request.getSession().setAttribute(Global.SESSION.USER_APP, jsonStr);
            
            return jsonStr;
        }
    }

    /**
     * 获取所有应用
     */
    @ApiOperation(value = "获取所有应用")
    @GetMapping(value = "/all")
    public VData<List<AppIconMenu>> getAllAppIconMenu() {
        List<AppIconMenu> appIconMenuList = appService.getAllAppIconMenu();
        return this.vData(appIconMenuList);
    }


    /**
     * 根据角色根据应用
     */
    @ApiOperation(value = "根据角色根据应用")
    @PostMapping(value = "/byRole")
    public Result getAppsByRoleId(@RequestBody AppRole appRole) {
        AppRole alarmVo = new AppRole();
        BeanUtils.copyProperties(appRole,alarmVo);
        List<AppSort> appRoleList = appService.getAppsByRoleId(alarmVo);
        return this.vData(appRoleList);
    }

    /**
     * 维护 角色 - 应用 关系
     */
    @ApiOperation(value = "维护 角色 - 应用 关系")
    @PostMapping(value = "/permission")
    public Result setAppsToRole(@RequestBody AppRoleVO appRoleVO) {
        //前端数据格式：  {"appRoles":[{"appId":35,"roleId":17,"sort":1},{"appId":36,"roleId":17,"sort":2}],"roleId":17}
        int delRes = appRoleService.deleteByRoleId(appRoleVO.getRoleId());
        if (appRoleVO.getAppRoles().size() == 0) {
            return Global.OK;
        }
        if (delRes >= 0) {
            int result = appRoleService.save(appRoleVO.getAppRoles());
            return this.result(result > 0);
        } else {
            return this.result(delRes > 0);
        }
    }



    @ApiOperation(value = "查看所有权限")
    @GetMapping(value = "/privilege")
    public VData<List<SysPrivilege>> getAllPrivilege() {
        return this.vData(sysPrivilegeService.findAll());
    }


    @ApiOperation(value = "获取应用权限")
    @PostMapping(value = "/privilege")
    public VData<List<SysAppPrivilege>> getAppPrivilege(@RequestBody App app) {
        return this.vData(sysAppPrivilegeService.findByProperty(SysAppPrivilege.class, "appId", app.getId()));
    }


    @ApiOperation(value = "修改应用权限")
    @PatchMapping(value = "/privilege/{appId}")
    public Result updateAppPrivilege(@RequestBody Map<String, String> param, @PathVariable("appId") int appId) {
        String added = param.get("added");
        String deled = param.get("deled");
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
        return this.result(sysAppPrivilegeService.managerAppPrivilege(appId, addList, delList));
    }


}
