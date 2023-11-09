package com.vrv.vap.admin.web;


import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.model.WorkbenchAuthority;
import com.vrv.vap.admin.model.WorkbenchIndividuation;
import com.vrv.vap.admin.service.UserService;
import com.vrv.vap.admin.service.WorkbenchAuthorityService;
import com.vrv.vap.admin.service.WorkbenchIndividuationService;
import com.vrv.vap.admin.vo.WorkbenchVo;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RequestMapping("/workbench")
@RestController
public class WorkbenchController extends ApiController {

    private static Logger logger = LoggerFactory.getLogger(WorkbenchController.class);

    @Resource
    private WorkbenchIndividuationService workbenchIndividuationService;

    @Resource
    private WorkbenchAuthorityService workbenchAuthorityService;


    @Autowired
    private UserService userService;

    /**
     * 配置工作台权限
     */
    @PostMapping("saveAuthorization")
    @ApiOperation(value = "配置角色工作台权限", notes = "")
    @SysRequestLog(description = "配置角色工作台权限", actionType = ActionType.UPDATE)
    @Transactional
    public Result saveAuthorization(@RequestBody List<WorkbenchVo> workbenchVoList) {

        for (WorkbenchVo workbenchVo : workbenchVoList) {
            WorkbenchAuthority workbenchAuthority = new WorkbenchAuthority();
            BeanUtils.copyProperties(workbenchVo, workbenchAuthority);
            WorkbenchAuthority workbenchAuthority1 = workbenchAuthorityService.findByRoleId(workbenchVo.getRoleId());
            if (workbenchAuthority1 != null) {
                workbenchAuthority.setId(workbenchAuthority1.getId());
                workbenchAuthority.setUpdateTime(new Date());
                int result = workbenchAuthorityService.updateSelective(workbenchAuthority);
                if (result == 1) {
                    SyslogSenderUtils.sendAddSyslog(workbenchAuthority,"配置角色工作台权限");
                }
            } else {
                workbenchAuthority.setCreateTime(new Date());
                int result = workbenchAuthorityService.save(workbenchAuthority);
                if (result == 1) {
                    SyslogSenderUtils.sendAddSyslog(workbenchAuthority,"配置角色工作台权限");
                }
            }
        }
        return this.result(true);
    }


    /**
     * 查询角色工作台权限
     */
    @GetMapping("getAuthorization")
    @ApiOperation(value = "查询角色工作台权限", notes = "")
    @SysRequestLog(description = "查询角色工作台权限", actionType = ActionType.SELECT)
    public Result getAuthorization() {

        List<WorkbenchAuthority> workbenchAuthorityList = workbenchAuthorityService.findAll();
        List<WorkbenchVo> workbenchVoList = new ArrayList<>();
        for (WorkbenchAuthority workbenchAuthority : workbenchAuthorityList) {
            WorkbenchVo workbenchVo = new WorkbenchVo();
            BeanUtils.copyProperties(workbenchAuthority, workbenchVo);
            workbenchVoList.add(workbenchVo);
        }
        return this.vData(workbenchVoList);


    }


    /**
     * 用户工作台个性化配置
     */
    @PostMapping("saveIndividuation")
    @ApiOperation(value = "用户工作台个性化配置", notes = "")
    @SysRequestLog(description = "用户工作台个性化配置", actionType = ActionType.UPDATE)
    public Result saveIndividuation(HttpServletRequest request, @RequestBody WorkbenchVo workbenchVo) {
        String workBenckConfig = workbenchVo.getWorkbenchConfig();
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        Integer userId = user.getId();
        logger.info("",userId);
        WorkbenchIndividuation workbenchIndividuation = workbenchIndividuationService.findByUserId(userId);
        logger.info(JSON.toJSONString(workbenchIndividuation));
        if (workbenchIndividuation == null) {
            workbenchIndividuation = new WorkbenchIndividuation();
            workbenchIndividuation.setWorkbenchConfig(workBenckConfig);
            workbenchIndividuation.setCodes(workbenchVo.getCodes());
            workbenchIndividuation.setUserId(userId);
            int result = workbenchIndividuationService.save(workbenchIndividuation);
            if (result == 1) {
                SyslogSenderUtils.sendAddSyslog(workbenchIndividuation,"用户工作台个性化配置");
            }
        } else {
            workbenchIndividuation.setWorkbenchConfig(workBenckConfig);
            workbenchIndividuation.setCodes(workbenchVo.getCodes());
            int result = workbenchIndividuationService.updateSelective(workbenchIndividuation);
            if (result == 1) {
                SyslogSenderUtils.sendAddSyslog(workbenchIndividuation,"用户工作台个性化配置");
            }
        }

        return this.result(true);
    }


    /**
     * 用户个性化工作台配置查询
     */
    @GetMapping("getIndividuation")
    @ApiOperation(value = "用户个性工作台查询", notes = "")
    @SysRequestLog(description = "用户个性工作台查询", actionType = ActionType.SELECT,manually = false)
    public VData<WorkbenchVo> getIndividuation(HttpServletRequest request) {
        com.vrv.vap.common.model.User user = (com.vrv.vap.common.model.User) request.getSession().getAttribute(Global.SESSION.USER);
        Integer userId = user.getId();
        String codes = workbenchAuthorityService.getCodesByUserId(user.getRoleIds());
        WorkbenchVo workbenchVo = new WorkbenchVo();
        workbenchVo.setCodes(codes);
        WorkbenchIndividuation workbenchIndividuation = workbenchIndividuationService.findByUserId(userId);
        if (workbenchIndividuation != null && StringUtils.isNotEmpty(workbenchIndividuation.getWorkbenchConfig())) {
            workbenchVo.setWorkbenchConfig(workbenchIndividuation.getWorkbenchConfig());
            return this.vData(workbenchVo);
        } else {
            User user1 = userService.findById(userId);
            String[] roleIds = user1.getRoleId().split(",");
            for (String roleId : roleIds) {
                WorkbenchAuthority workbenchAuthority = workbenchAuthorityService.findByRoleId(roleId);
                if (workbenchAuthority != null && StringUtils.isNotEmpty(workbenchAuthority.getWorkbenchConfig())) {
                    workbenchVo.setWorkbenchConfig(workbenchAuthority.getWorkbenchConfig());
                    return this.vData(workbenchVo);
                }
            }
            return this.vData(workbenchVo);
        }
    }

}
