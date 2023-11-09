package com.vrv.vap.admin.web;

import cn.hutool.core.collection.CollectionUtil;
import com.vrv.vap.admin.model.UserModule;
import com.vrv.vap.admin.service.UserModuleService;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.vo.Query;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;


@RequestMapping(path = "/preference")
@Api(value = "用户模块管理")
@RestController
@ApiOperation(value = "用户模块管理")
public class UserModuleController extends ApiController {

    @Autowired
    private UserModuleService userModuleService;

    /**
     * 获取用户模块
     */
    @ApiOperation(value = "获取用户模块")
    @GetMapping("/{module}")
    @SysRequestLog(description = "获取用户模块", actionType = ActionType.SELECT)
    public VData<List<UserModule>> getUserModule(HttpServletRequest request, @PathVariable("module") String module) {
        User user = (User)request.getSession().getAttribute(Global.SESSION.USER);
        Example example = new Example(UserModule.class);
        example.createCriteria().andEqualTo("userId",user.getId()).andEqualTo("module", module);
        return this.vData(userModuleService.findByExample(example));
    }

    /**
     * 获取用户模块key
     */
    @ApiOperation(value = "获取用户模块key")
    @GetMapping("/{module}/{key}")
    @SysRequestLog(description = "获取用户模块key", actionType = ActionType.SELECT)
    public VData<UserModule> getUserModuleKey(HttpServletRequest request, @PathVariable("module") String module, @PathVariable("key") String key) {
        User user = (User)request.getSession().getAttribute(Global.SESSION.USER);
        Example example = new Example(UserModule.class);
        example.createCriteria().andEqualTo("userId",user.getId()).andEqualTo("module",module).andEqualTo("key",key);
        List<UserModule> userModules = userModuleService.findByExample(example);
        if (CollectionUtil.isNotEmpty(userModules)) {
            return this.vData(userModules.get(0));
        }
        return this.vData(true);
    }

     /**
     * 修改或添加用户模块
     */
    @PatchMapping
    @ApiOperation(value = "修改或添加用户模块")
    @SysRequestLog(description = "修改或添加用户模块", actionType = ActionType.UPDATE)
    public Result updateOrSaveInfo(HttpServletRequest request,@RequestBody @Valid UserModule userModule) {
        User user = (User)request.getSession().getAttribute(Global.SESSION.USER);
        Example example = new Example(UserModule.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",user.getId()).andEqualTo("module", userModule.getModule())
                .andEqualTo("key", userModule.getKey());

        userModule.setUserId(user.getId());
        if (CollectionUtil.isNotEmpty(userModuleService.findByExample(example))) {
            userModuleService.updateSelective(userModule);
        } else {
            userModuleService.save(userModule);
        }
        return this.vData(userModule);
    }

    /**
     * 删除用户模块key
     */
    @ApiOperation(value = "删除用户模块key")
    @DeleteMapping
    @SysRequestLog(description = "删除用户模块key", actionType = ActionType.DELETE)
    public Result getUserModuleKey(HttpServletRequest request, @RequestBody @Valid UserModule userModule) {
        User user = (User)request.getSession().getAttribute(Global.SESSION.USER);
        UserModule module = new UserModule();
        module.setUserId(user.getId());
        module.setModule(userModule.getModule());
        module.setKey(userModule.getKey());
        UserModule moduleInfo = userModuleService.findOne(module);
        if (moduleInfo == null) {
            return new Result("-1", "未找到对应模块信息");
        }
        return this.result(userModuleService.deleteById(moduleInfo.getId()) > 0);
    }
}
