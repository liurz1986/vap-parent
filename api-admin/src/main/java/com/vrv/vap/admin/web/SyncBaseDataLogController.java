package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.SyncBaseDataLog;
import com.vrv.vap.admin.service.SyncBaseDataLogService;
import com.vrv.vap.admin.vo.SyncBaseDataLogQuery;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author lilang
 * @date 2022/7/21
 * @description
 */
@RestController
@Api(value = "基础数据同步记录")
@RequestMapping("/sync/base/data/log")
public class SyncBaseDataLogController extends ApiController {

    private static final Logger log = LoggerFactory.getLogger(SyncBaseDataLogController.class);

    @Autowired
    SyncBaseDataLogService syncBaseDataLogService;

    @GetMapping
    @ApiOperation("获取基础数据同步记录")
    @SysRequestLog(description = "获取基础数据同步记录",actionType = ActionType.SELECT)
    public Result getSyncLogList() {
        return this.vData(syncBaseDataLogService.findAll());
    }

    @PostMapping
    @ApiOperation("查询基础数据同步记录")
    @SysRequestLog(description = "查询基础数据同步记录",actionType = ActionType.SELECT)
    public VList queryLogList(@RequestBody SyncBaseDataLogQuery query) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(query,SyncBaseDataLog.class);
        return this.vList(syncBaseDataLogService.findByExample(example));
    }

    @DeleteMapping
    @ApiOperation("删除基础数据同步记录")
    @SysRequestLog(description = "删除基础数据同步记录",actionType = ActionType.DELETE)
    public Result deleteTask(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<SyncBaseDataLog> logList = syncBaseDataLogService.findByids(ids);
        int result = syncBaseDataLogService.deleteByIds(ids);
        if (result > 0) {
            logList.forEach(syncBaseDataLog -> {
                SyslogSenderUtils.sendDeleteSyslog(syncBaseDataLog,"删除基础数据同步记录");
            });
        }
        return this.result(result > 0);
    }
}
