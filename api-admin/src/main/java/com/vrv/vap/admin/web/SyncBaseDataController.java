package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.manager.TaskManager;
import com.vrv.vap.admin.common.task.SyncBaseDataTask;
import com.vrv.vap.admin.model.JobModel;
import com.vrv.vap.admin.model.SyncBaseData;
import com.vrv.vap.admin.service.BookAssetProducerService;
import com.vrv.vap.admin.service.SyncBaseDataService;
import com.vrv.vap.admin.vo.SyncBaseDataQuery;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/4/21
 * @description
 */
@RestController
@Api(value = "基础数据同步")
@RequestMapping("/sync/base/data")
public class SyncBaseDataController extends ApiController {

    private static final Logger log = LoggerFactory.getLogger(SyncBaseDataController.class);

    @Resource
    SyncBaseDataService syncBaseDataService;

    @Resource
    BookAssetProducerService bookAssetProducerService;

    private static String JOB_PRE = "syncBaseData-";

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("status", "{\"0\":\"启用\",\"1\":\"停止\"}");
        transferMap.put("type", "{\"asset\":\"资产\",\"person\":\"人员\",\"app\":\"应用\",\"org\":\"机构\",\"file\":\"文件\"}");
        transferMap.put("source", "{\"bxy-ry\":\"北信源-融一\",\"bxy-zr\":\"北信源-准入\",\"bxy-zs\":\"北信源-主审\",\"bxy-gmp\":\"北信源-GMP\"," +
                "\"bxy-mb\":\"北信源-密标\",\"bxy-yg\":\"北信源-运管\",\"bxy-fs\":\"北信源-服审\"}");
    }

    @GetMapping
    @ApiOperation("获取所有基础数据同步任务")
    @SysRequestLog(description = "获取所有数据同步任务",actionType = ActionType.SELECT)
    public Result getDataTaskList() {
        return this.vData(syncBaseDataService.findAll());
    }

    @PostMapping
    @ApiOperation("查询基础数据同步任务")
    @SysRequestLog(description = "查询基础数据同步任务",actionType = ActionType.SELECT)
    public VList queryDataTask(@RequestBody SyncBaseDataQuery query) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(query,"查询基础数据同步任务",transferMap);
        Example example = this.pageQuery(query,SyncBaseData.class);
        return this.vList(syncBaseDataService.findByExample(example));
    }

    @GetMapping(path = "/{type}")
    @ApiOperation("根据类型获取基础数据同步任务")
    @SysRequestLog(description = "根据类型获取同步任务",actionType = ActionType.SELECT)
    public Result getTaskByType(@PathVariable String type) {
        Example example = new Example(SyncBaseData.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("type",type);
        return this.vData(syncBaseDataService.findByExample(example));
    }

    @PutMapping
    @ApiOperation("添加基础数据同步任务")
    @SysRequestLog(description = "添加基础数据同步任务",actionType = ActionType.ADD)
    public Result addTask(@RequestBody SyncBaseData syncBaseData) {
        syncBaseData.setStatus(0);
        int result = syncBaseDataService.save(syncBaseData);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(syncBaseData,"添加基础数据同步任务",transferMap);
        }
        // 动态新增周期报表定时任务
        JobModel jobModel = new JobModel();
        jobModel.setJobName(JOB_PRE + syncBaseData.getId());
        jobModel.setCronTime(syncBaseData.getCron());
        jobModel.setJobClazz(SyncBaseDataTask.class);
        Map<String, String> param = new HashMap<>();
        param.put("id", syncBaseData.getId().toString());
        param.put("type",syncBaseData.getType());
        TaskManager.addJob(jobModel, param);
        return this.vData(syncBaseData);
    }

    @PatchMapping
    @ApiOperation("修改基础数据同步任务")
    @SysRequestLog(description = "修改基础数据同步任务",actionType = ActionType.UPDATE)
    public Result updateTask(@RequestBody SyncBaseData syncBaseData) {
        SyncBaseData syncBaseDataSec = syncBaseDataService.findById(syncBaseData.getId());
        Integer taskId = syncBaseData.getId();
        if (taskId == null) {
            return this.result(false);
        }
        JobModel jobModel = new JobModel();
        jobModel.setJobName(JOB_PRE + taskId);
        TaskManager.removeJob(jobModel);
        jobModel.setCronTime(syncBaseData.getCron());
        jobModel.setJobClazz(SyncBaseDataTask.class);
        Map<String, String> param = new HashMap<>();
        param.put("id", taskId.toString());
        param.put("type",syncBaseData.getType());
        TaskManager.addJob(jobModel, param);
        int result = syncBaseDataService.updateSelective(syncBaseData);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(syncBaseDataSec,syncBaseData,"修改基础数据同步任务",transferMap);
        }
        return this.result(result > 0);
    }

    @DeleteMapping
    @ApiOperation("删除基础数据同步任务")
    @SysRequestLog(description = "删除基础数据同步任务",actionType = ActionType.DELETE)
    public Result deleteTask(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<SyncBaseData> baseDataList = syncBaseDataService.findByids(ids);
        String[] idsArr = ids.split(",");
        for (String id : idsArr) {
            JobModel jobModel = new JobModel();
            jobModel.setJobName(JOB_PRE + id);
            TaskManager.removeJob(jobModel);
        }
        int result = syncBaseDataService.deleteByIds(ids);
        if (result > 0) {
            baseDataList.forEach(syncBaseData -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(syncBaseData,"删除基础数据同步任务",transferMap);
            });
        }
        return this.result(result > 0);
    }

    @GetMapping(path = "/execute/{id}")
    @ApiOperation("立即执行基础数据同步任务")
    @SysRequestLog(description = "基础数据同步",actionType = ActionType.UPDATE)
    public Result executeTask(@PathVariable @ApiParam("任务ID") Integer id) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        syslogSender.sendSysLog(ActionType.UPDATE, "基础数据同步:【任务ID:" + id + "】", null, "1");
        TaskManager.triggerJob(JOB_PRE + id);
        return this.result(true);
    }

    @GetMapping(path = "/start/{id}")
    @ApiOperation("重新开始基础数据同步任务")
    @SysRequestLog(description = "重新开始基础数据同步任务",actionType = ActionType.SELECT)
    public Result startTask(@PathVariable Integer id) {
        TaskManager.resumeJob(JOB_PRE + id);
        SyncBaseData syncBaseData =  syncBaseDataService.findById(id);
        if (syncBaseData == null) {
            return this.result(false);
        }
        syncBaseData.setStatus(0);
        syncBaseDataService.updateSelective(syncBaseData);
        return this.result(true);
    }

    @GetMapping(path = "/stop/{id}")
    @ApiOperation("停止基础数据同步任务")
    @SysRequestLog(description = "停止基础数据同步任务",actionType = ActionType.SELECT)
    public Result stopTask(@PathVariable Integer id) {
        if (id == null) {
            return this.result(false);
        }
        TaskManager.pauseJob(JOB_PRE + id);
        SyncBaseData syncBaseData =  syncBaseDataService.findById(id);
        if (syncBaseData == null) {
            return this.result(false);
        }
        syncBaseData.setStatus(1);
        syncBaseDataService.updateSelective(syncBaseData);
        return this.result(true);
    }

    @ApiOperation("离线运维资产台账数据导入")
    @PostMapping(path = "/book/import")
    @SysRequestLog(description="离线运维资产台账数据导入", actionType = ActionType.IMPORT,manually = false)
    public Result importBookFile(@ApiParam(value = "导入的文件", required = true) @RequestParam MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (!fileName.endsWith("xls")) {
            return this.result(ErrorCode.OFFLINE_TEMPLATE_TYPE_ERROR);
        }
        new Thread(() -> {
            bookAssetProducerService.importBookData(file);
        }).start();
        return this.vData(true);
    }

    @ApiOperation("下载模板")
    @GetMapping(path = "/download/{fileName}")
    @SysRequestLog(description="下载模板", actionType = ActionType.DOWNLOAD)
    public void downloadFile(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        syslogSender.sendSysLog(ActionType.DOWNLOAD, "下载模板：【文件名：" + fileName + "】", null, "1");

        ClassPathResource classPathResource = new ClassPathResource(Paths.get("/templates", fileName).toString());
        try (InputStream fis = classPathResource.getInputStream();
             ServletOutputStream out = response.getOutputStream();
             HSSFWorkbook workbook = new HSSFWorkbook(fis)) {

            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/binary;charset=utf-8");
            workbook.write(out);
            out.flush();
        } catch (Exception e) {
            log.error("下载失败",  e);
        }
    }
}
