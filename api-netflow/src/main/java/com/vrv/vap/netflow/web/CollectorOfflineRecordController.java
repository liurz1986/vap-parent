package com.vrv.vap.netflow.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.netflow.model.CollectorOfflineRecord;
import com.vrv.vap.netflow.service.CollectorOfflineRecordService;
import com.vrv.vap.netflow.vo.CollectorOfflineRecordQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.util.List;

/**
 * @author lilang
 * @date 2022/3/28
 * @description 离线导入记录管理
 */
@RequestMapping(path = "/collector/offline/record")
@RestController
public class CollectorOfflineRecordController extends ApiController {

    @Autowired
    CollectorOfflineRecordService collectorOfflineRecordService;

    @ApiOperation("获取导入记录列表")
    @GetMapping
    @SysRequestLog(description="获取所有导入记录", actionType = ActionType.SELECT)
    public Result getRecordList() {
        return this.vData(collectorOfflineRecordService.findAll());
    }

    @ApiOperation("查询导入记录")
    @PostMapping
    @SysRequestLog(description="查询导入记录", actionType = ActionType.SELECT)
    public VList queryRecord(@RequestBody CollectorOfflineRecordQuery query) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(query,CollectorOfflineRecord.class);
        return this.vList(collectorOfflineRecordService.findByExample(example));
    }

    @ApiOperation("删除导入记录")
    @DeleteMapping
    @SysRequestLog(description="删除导入记录", actionType = ActionType.DELETE)
    public Result deleteTemplate(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        // 删除记录时，删除失败日志文件
        List<CollectorOfflineRecord> recordList = collectorOfflineRecordService.findByids(ids);
        for (CollectorOfflineRecord record : recordList) {
            String path = record.getErrorFile();
            if (StringUtils.isNotEmpty(path)) {
                File file = new File(path);
                if (file.isFile() && file.exists()) {
                    file.delete();
                }
            }
        }
        int result = collectorOfflineRecordService.deleteByIds(ids);
        if (result > 0) {
            recordList.forEach(collectorOfflineRecord -> {
                SyslogSenderUtils.sendDeleteSyslog(collectorOfflineRecord,"删除导入记录");
            });
        }
        return this.result(result >= 1);
    }
}
