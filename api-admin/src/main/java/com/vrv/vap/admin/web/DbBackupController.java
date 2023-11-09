package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.properties.VapBackupProperties;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.*;
import com.vrv.vap.admin.vo.*;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;
import java.util.concurrent.FutureTask;


@RequestMapping(path = "/db/task")
@RestController
public class DbBackupController extends ApiController {

    private static Logger logger = LoggerFactory.getLogger(DbBackupController.class);

    @Autowired
    private VapBackupProperties vapBackupProperties;

    @Autowired
    private DbBackupService dbBackupService;

    @PostMapping(value = "/backup")
    @ApiOperation(value = "备份文件")
    public Result backup(@RequestBody TaskVO taskVO) {
        if (taskVO.getTaskId() == null || StringUtils.isEmpty(taskVO.getFileName())) {
            return new Result("201", "参数格式错误");
        }

        DbTaskInfo dbTaskInfo = new DbTaskInfo();
        dbTaskInfo.setBusinessId(taskVO.getBusinessId());
        dbTaskInfo.setTaskId(taskVO.getTaskId());
        dbTaskInfo.setTaskType(0);
        dbTaskInfo.setStatus(0);
        dbTaskInfo.setFileName(taskVO.getFileName() + ".vapbak");
        dbTaskInfo.setCreateTime(new Date());
        int res = dbBackupService.save(dbTaskInfo);
        if (res != 1) {
            return new Result("202", "任务创建失败");
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        FutureTask futureTask = new FutureTask<>(() -> {
            Map<String, Object> result = null;
            try {
                int sysTableCount = vapBackupProperties.getSys().size();
                logger.info("start backup config，vap tables："+sysTableCount);
                // 开始备份
                result = dbBackupService.backup(dbTaskInfo);
            } catch (Exception e) {
                logger.info("结果推送失败", e);
            }
            return result;
        });
        new Thread(futureTask).start();

        return result(true);
    }

    @PostMapping(value = "/recovery")
    @ApiOperation(value = "还原文件")
    public Result recovery(@RequestBody TaskVO taskVO) {
        if (taskVO.getTaskId() == null) {
            return new Result("201", "参数格式错误");
        }

        DbTaskInfo dbTask = new DbTaskInfo();
        dbTask.setBusinessId(taskVO.getBusinessId());
        DbTaskInfo dbTaskInfo = dbBackupService.findOne(dbTask);
        if (dbTaskInfo == null) {
            return new Result("204", "未找到任务记录");
        }
        dbTaskInfo.setTaskType(1);
        dbTaskInfo.setTaskId(taskVO.getTaskId());
        dbTaskInfo.setStatus(0);
        dbTaskInfo.setCreateTime(new Date());
        int result = dbBackupService.update(dbTaskInfo);
        if (result != 1) {
            return new Result("202", "任务创建失败");
        }

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        FutureTask futureTask = new FutureTask<>(() -> {
            Map<String, Object> resultMap = dbBackupService.recovery(dbTaskInfo);

            if (resultMap.get("resCode").equals("0")) {
                DbTaskInfo query = new DbTaskInfo();
                query.setTaskId(taskVO.getTaskId());
                DbTaskInfo taskInfo = dbBackupService.findOne(query);
                taskInfo.setStatus(5);
                dbBackupService.update(taskInfo);

                dbBackupService.restart();
            }
            return null;
        });
        new Thread(futureTask).start();

        return result(true);
    }

    @DeleteMapping
    @ApiOperation(value = "删除文件")
    @SysRequestLog(description = "删除文件",actionType = ActionType.DELETE)
    public Result delete(@RequestBody TaskVO taskVO) {
        return dbBackupService.deleteFile(taskVO);
    }

    @PostMapping("/import/file")
    @ApiOperation(value = "导入文件")
    @SysRequestLog(description = "导入文件",actionType = ActionType.IMPORT,manually = false)
    public Result importFile(@RequestBody TaskVO taskVO) {
        return dbBackupService.importFile(taskVO);
    }

    @PostMapping("/download")
    @ApiOperation(value = "下载文件")
    @SysRequestLog(description = "下载文件",actionType = ActionType.DOWNLOAD)
    public void download(@RequestBody TaskVO taskInfo, HttpServletResponse response) {
        SyslogSenderUtils.sendDownLosdSyslog();
        DbTaskInfo query = new DbTaskInfo();
        query.setBusinessId(taskInfo.getBusinessId());
        DbTaskInfo dbTaskInfo = dbBackupService.findOne(query);
        if (dbTaskInfo == null) {
            return;
        }
        try (InputStream fis = new BufferedInputStream(new FileInputStream(new File(dbTaskInfo.getFilePath())));
             OutputStream out = new BufferedOutputStream(response.getOutputStream())) {
            // path是指欲下载的文件的路径
            File file = new File(dbTaskInfo.getFilePath());
            // 取得文件名
            String filename = file.getName();

            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.setContentType("application/octet-stream");
            out.write(buffer);
            out.flush();
        } catch (IOException ex) {
            logger.info("下载失败");
        }
    }
}