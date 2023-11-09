package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.AuthLoginField;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.service.AuthLoginFieldService;
import com.vrv.vap.admin.service.UserService;
import com.vrv.vap.admin.vo.AuthLoginFieldQuery;
import com.vrv.vap.admin.vo.AuthLoginFieldVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@RestController
@RequestMapping(path = "/authField")
public class AuthLoginFieldController extends ApiController {

    @Autowired
    private AuthLoginFieldService authLoginFieldService;

    @Autowired
    private UserService userService;

    @ApiOperation("查询授权登陆字段")
    @PostMapping
    @SysRequestLog(description = "查询授权登陆", actionType = ActionType.SELECT)
    public VList queryAuthField(@RequestBody AuthLoginFieldQuery authLoginFieldQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(authLoginFieldQuery, AuthLoginField.class);
        return this.vList(authLoginFieldService.findByExample(example));
    }


    @ApiOperation("添加授权登陆字段")
    @PutMapping
    @SysRequestLog(description = "添加授权登陆", actionType = ActionType.ADD)
    public VData addAuthField(@RequestBody AuthLoginFieldVO authLoginFieldVO) {
        List<AuthLoginField> loginFieldList = authLoginFieldVO.getLoginFieldList();
        User user = userService.findById(authLoginFieldVO.getUserId());
        if (user != null) {
            user.setIpLogin(authLoginFieldVO.getIpLogin());
            userService.updateSelective(user);
        }
        List<AuthLoginField> fieldList = authLoginFieldService.findByProperty(AuthLoginField.class,"userId",authLoginFieldVO.getUserId());
        if (CollectionUtils.isNotEmpty(fieldList)) {
            for (AuthLoginField field : fieldList) {
                authLoginFieldService.deleteById(field.getId());
            }
        }
        if (CollectionUtils.isNotEmpty(loginFieldList)) {
            int result = authLoginFieldService.save(loginFieldList);
            if (result == 1) {
                if (CollectionUtils.isNotEmpty(loginFieldList)) {
                    for (AuthLoginField authLoginField : loginFieldList) {
                        SyslogSenderUtils.sendAddSyslog(authLoginField, "添加授权登陆");
                    }
                }
            }
        }
        return this.vData(true);
    }

    @ApiOperation("修改授权登陆字段")
    @PatchMapping
    @SysRequestLog(description = "修改授权登陆", actionType = ActionType.UPDATE,manually = false)
    public Result updateAuthField(@RequestBody AuthLoginFieldVO authLoginFieldVO) {
        User user = userService.findById(authLoginFieldVO.getUserId());
        if (user != null) {
            user.setIpLogin(authLoginFieldVO.getIpLogin());
            userService.updateSelective(user);
        }
        List<AuthLoginField> fieldList = authLoginFieldService.findByProperty(AuthLoginField.class,"userId",authLoginFieldVO.getUserId());
        if (CollectionUtils.isNotEmpty(fieldList)) {
            for (AuthLoginField field : fieldList) {
                authLoginFieldService.deleteById(field.getId());
            }
        }
        List<AuthLoginField> loginFieldList = authLoginFieldVO.getLoginFieldList();
        if (CollectionUtils.isNotEmpty(loginFieldList)) {
            authLoginFieldService.save(loginFieldList);
        }
        return this.result(true);
    }

    @ApiOperation("删除授权登陆字段")
    @DeleteMapping
    @SysRequestLog(description = "删除授权登陆", actionType = ActionType.DELETE)
    public Result deleteAuthField(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<AuthLoginField> loginFieldList = authLoginFieldService.findByids(param.getIds());
        int result = authLoginFieldService.deleteByIds(ids);
        if (result > 0) {
            loginFieldList.forEach(authLoginField -> {
                SyslogSenderUtils.sendDeleteSyslog(authLoginField,"删除授权登陆");
            });
        }
        return this.result(result >= 1);
    }
}
