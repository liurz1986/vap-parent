package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.ResourceApi;
import com.vrv.vap.admin.service.ResourceApiService;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/10/9
 * @description
 */
@RestController
@RequestMapping(path = "/resourceApi")
public class ResourceApiController extends ApiController {

    @Resource
    ResourceApiService resourceApiService;

    @ApiOperation("获取资源菜单接口权限")
    @GetMapping("/{resourceId}")
    @SysRequestLog(description = "获取资源菜单接口权限", actionType = ActionType.SELECT)
    public VData getResourceApiList(@PathVariable Integer resourceId) {
        Example example = new Example(ResourceApi.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("resourceId",resourceId);
        return this.vData(resourceApiService.findByExample(example));
    }

    @ApiOperation("修改资源菜单接口权限")
    @PutMapping("/{resourceId}")
    @SysRequestLog(description = "修改资源菜单接口权限", actionType = ActionType.UPDATE,manually = false)
    public Result manageResourceApi(@RequestBody Map<String, String> param,@PathVariable Integer resourceId) {
        String added = param.get("added");
        String deled = param.get("deled");
        // 统计新增、删除的接口权限
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
        boolean result = resourceApiService.manageResourceApi(resourceId,addList,delList);
        return this.result(result);
    }
}
