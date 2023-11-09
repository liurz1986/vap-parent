package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.manager.TaskManager;
import com.vrv.vap.admin.model.DbBackupConfigInfo;
import com.vrv.vap.admin.model.DbBackupStrategy;
import com.vrv.vap.admin.model.JobModel;
import com.vrv.vap.admin.service.DbBackupConfigService;
import com.vrv.vap.admin.service.DbBackupStrategyService;
import com.vrv.vap.admin.vo.DbBackupStrategyVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* 数据库备份策略操作
*
*
*/
@RestController
@Api(value = "数据库备份策略操作")
@RequestMapping("/db/backup/strategy")
public class DbBackupStrategyController extends ApiController {

    private static Logger logger = LoggerFactory.getLogger(DbBackupStrategyController.class);

    @Autowired
    private DbBackupStrategyService dbBackupStrategyService;

    @Autowired
    private DbBackupConfigService dbBackupConfigService;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("strategyStatus","{\"0\":\"未启用\",\"1\":\"已启用\"}");
    }

    /**
    * 查询所有操作记录
    */
    @ApiOperation(value = "查询所有备份策略")
    @GetMapping
    public VData<List<DbBackupStrategy>> getAllDbOperationInfos() {
        return this.vData(dbBackupStrategyService.findAll());
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加备份策略")
    @PutMapping
    @SysRequestLog(description="添加备份策略", actionType = ActionType.ADD)
    public Result addDbOperationInfo(@RequestBody DbBackupStrategy dbBackupStrategy) {
        List<DbBackupStrategy> dbBackupStrategies = dbBackupStrategyService.findByProperty(DbBackupStrategy.class, "dataTypes", dbBackupStrategy.getDataTypes());
        if (CollectionUtils.isNotEmpty(dbBackupStrategies)) {
            return new Result("-1", "已存在同种数据类型备份策略");
        }

        int result = dbBackupStrategyService.save(dbBackupStrategy);
        if (result > 0 && dbBackupStrategy.getStrategyStatus() == 1) {
            DbBackupStrategy backupStrategy = dbBackupStrategyService.findOne(dbBackupStrategy);
            TaskManager.addBackupTask(backupStrategy);
            TaskManager.resumeJob("dbBackupTask_" + backupStrategy.getId());
        }
        if (result > 0) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(dbBackupStrategy,"添加备份策略",transferMap);
            return vData(dbBackupStrategyService.findOne(dbBackupStrategy));
        }
        return this.result(false);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改备份策略")
    @PatchMapping
    @SysRequestLog(description="修改备份策略", actionType = ActionType.UPDATE)
    public Result updateAlarmItem(@RequestBody DbBackupStrategy dbBackupStrategy) {
        DbBackupStrategy backupStrategySec = dbBackupStrategyService.findById(dbBackupStrategy.getId());
        JobModel jobModel = new JobModel();
        jobModel.setJobName("dbBackupTask_" + dbBackupStrategy.getId());
        TaskManager.removeJob(jobModel);
        if (dbBackupStrategy.getStrategyStatus() == 1) {
            TaskManager.addBackupTask(dbBackupStrategy);
            TaskManager.resumeJob("dbBackupTask_" + dbBackupStrategy.getId());
        }
        int result = dbBackupStrategyService.update(dbBackupStrategy);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(backupStrategySec,dbBackupStrategy,"修改备份策略",transferMap);
        }
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除备份策略")
    @DeleteMapping
    @SysRequestLog(description="删除备份策略", actionType = ActionType.DELETE)
    public Result delAlarmItem(@RequestBody DeleteQuery deleteQuery) {
        List<DbBackupStrategy> dbBackupStrategies = dbBackupStrategyService.findByids(deleteQuery.getIds());
        if (CollectionUtils.isNotEmpty(dbBackupStrategies)) {
            JobModel jobModel = new JobModel();
            jobModel.setJobName("dbBackupTask_" + dbBackupStrategies.get(0).getId());
            TaskManager.removeJob(jobModel);
        }

        int result = dbBackupStrategyService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            dbBackupStrategies.forEach(dbBackupStrategy -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(dbBackupStrategy,"删除备份策略",transferMap);
            });
        }
        return this.result(result == 1);
    }

    /**
     * 查询（分页）
     */
    @ApiOperation(value = "查询备份策略（分页）")
    @PostMapping
    @SysRequestLog(description="查询备份策略", actionType = ActionType.SELECT)
    public VList<DbBackupStrategy> queryDbOperationInfos(@RequestBody DbBackupStrategyVO query) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(query,"查询备份策略",transferMap);
        Example example = this.pageQuery(query, DbBackupStrategy.class);
        return this.vList(dbBackupStrategyService.findByExample(example));
    }

    /**
     * 查询备份数据类型
     */
    @ApiOperation(value = "查询备份数据类型")
    @GetMapping("/data/types")
    @SysRequestLog(description="查询备份数据类型", actionType = ActionType.SELECT)
    public VData<List<String>> queryDataTypes() {
        return this.vData(dbBackupConfigService.findAll().stream().map(DbBackupConfigInfo::getDataType).collect(Collectors.toList()));
    }

    /*@PostMapping("/startTask")
    @ApiOperation("启动周期任务")
    public Result addTask(DbBackupStrategy dbBackupStrategy) {
        TaskManager.addBackupTask(dbBackupStrategy);
        TaskManager.resumeJob("dbBackupTask_" + dbBackupStrategy.getId());
        dbBackupStrategy.setStrategyStatus(1);
        dbBackupStrategyService.update(dbBackupStrategy);
        return result(true);
    }

    @PostMapping("/stopTask")
    @ApiOperation("停止周期任务")
    public Result stopTask(DbBackupStrategy dbBackupStrategy) {
        JobModel jobModel = new JobModel();
        jobModel.setJobName("dbBackupTask_" + dbBackupStrategy.getId());
        TaskManager.removeJob(jobModel);
        dbBackupStrategy.setStrategyStatus(0);
        dbBackupStrategyService.update(dbBackupStrategy);
        return result(true);
    }*/
}