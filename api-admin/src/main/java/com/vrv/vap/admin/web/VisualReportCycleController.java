package com.vrv.vap.admin.web;


import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.common.constant.ErrorCode;
import com.vrv.vap.admin.common.manager.TaskManager;
import com.vrv.vap.admin.common.pdf.PdfExport;
import com.vrv.vap.admin.common.task.ReportCycleTask;
import com.vrv.vap.admin.common.util.CronUtils;
import com.vrv.vap.admin.service.ReportService;
import com.vrv.vap.admin.service.VisualReportCycleFileService;
import com.vrv.vap.admin.service.VisualReportCycleService;
import com.vrv.vap.admin.service.VisualReportJavaService;
import com.vrv.vap.admin.vo.VisualReportCycleQuery;
import com.vrv.vap.admin.vo.VisualReportJavaVO;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
* @BelongsPackage com.sa.platform.bussiness.web
* @Author CodeGenerator
* @CreateTime 2020/09/10
* @Description (VisualReportCycle相关接口)
* @Version
*/
@Slf4j
@RestController
@Api(value = "VisualReportCycle")
@RequestMapping
public class VisualReportCycleController extends ApiController {

    @Autowired
    private VisualReportCycleService visualReportCycleService;
    @Autowired
    private VisualReportCycleFileService visualReportCycleFileService;
    @Autowired
    private ReportService reportService;
    @Autowired
    private VisualReportJavaService reportJavaService;
    @Value("#{${screen-capturer.code-convert}}")
    private Map<String,Object> convertMap;

    @Value("${screen-capturer.report-type:node}")
    private String reportType;

    private static String JOB_PRE = "reportCycle-";

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("status", "{\"0\":\"停用\",\"1\":\"启用\"}");
        transferMap.put("fileType", "{\"1\":\"pdf\",\"2\":\"doc\",\"3\":\"html\"}");
    }

    /**
    * 获取所有数据--VisualReportCycle
    */
    @ApiOperation(value = "获取所有VisualReportCycle")
    @SysRequestLog(description="获取所有周期报表", actionType = ActionType.SELECT)
    @GetMapping("/report/cycle")
    public VData< List<VisualReportCycle>> getAllVisualReportCycle() {
        List<VisualReportCycle> list = visualReportCycleService.findAll();
        return this.vData(list);
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加VisualReportCycle")
    @SysRequestLog(description="添加周期报表", actionType = ActionType.ADD)
    @PutMapping("/report/cycle")
    public Result addVisualReportCycle(@RequestBody VisualReportCycle visualReportCycle) {

//        if(StringUtils.isNotEmpty(report.getParam()) && StringUtils.isEmpty(visualReportCycle.getParam())){
//            log.info("校验是否必填参数");
//            return  this.result(ErrorCode.PARAM_NULL);
//        }
        //校验cron表达式是否合法
        if(StringUtils.isEmpty(visualReportCycle.getCron()) || !CronUtils.isValid(visualReportCycle.getCron())){
            log.info("校验cron表达式不合法");
            return  this.result(ErrorCode.CRON_NULL);
        }
        visualReportCycle.setReportType(reportType);
        visualReportCycle.setCount(0);
        int result = visualReportCycleService.save(visualReportCycle);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(visualReportCycle,"添加周期报表",transferMap);
        }
        // 动态新增周期报表定时任务
        JobModel jobModel = new JobModel();
        jobModel.setJobName(JOB_PRE + visualReportCycle.getId());
        jobModel.setCronTime(visualReportCycle.getCron());
        jobModel.setJobClazz(ReportCycleTask.class);
        Map<String, String> param = new HashMap<String, String>();
        param.put("id", visualReportCycle.getId().toString());
        TaskManager.addJob(jobModel, param);
        return this.vData(visualReportCycleService.findById(visualReportCycle.getId()));
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改VisualReportCycle", hidden = false)
    @SysRequestLog(description="修改周期报表", actionType = ActionType.UPDATE)
    @PatchMapping("/report/cycle")
    public Result updateVisualReportCycle(@RequestBody VisualReportCycle  visualReportCycle) {
        VisualReportCycle reportCycleSec = visualReportCycleService.findById(visualReportCycle.getId());
        if(visualReportCycle.getStatus() == 1) {
            //校验是否必填参数
//            Report report = reportService.findById(visualReportCycle.getReportId());
//            if (StringUtils.isNotEmpty(report.getParam()) && StringUtils.isEmpty(visualReportCycle.getParam())) {
//                return this.result(ErrorCode.PARAM_NULL);
//            }
            //校验cron表达式是否合法
            if (StringUtils.isEmpty(visualReportCycle.getCron()) || !CronUtils.isValid(visualReportCycle.getCron())) {
                return this.result(ErrorCode.CRON_NULL);
            }
        }
        JobModel jobModel = new JobModel();
        jobModel.setJobName(JOB_PRE + visualReportCycle.getId());
        jobModel.setCronTime(visualReportCycle.getCron());
        jobModel.setJobClazz(ReportCycleTask.class);
        // 修改先删除该任务
        TaskManager.removeJob(jobModel);
        // 1-启动  0-挂起
        // 如果状态是启动，则启动，否则不启动
        if (visualReportCycle.getStatus() != 0) {
            Map<String, String> param = new HashMap<String, String>();
            param.put("id", visualReportCycle.getId().toString());
            TaskManager.addJob(jobModel, param);
        }

        int result = visualReportCycleService.update(visualReportCycle);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(reportCycleSec,visualReportCycle,"修改周期报表",transferMap);
        }
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除VisualReportCycle")
    @SysRequestLog(description="删除周期报表", actionType = ActionType.DELETE)
    @DeleteMapping("/report/cycle")
    public Result delVisualReportCycle(@RequestBody DeleteQuery deleteQuery) {
        List<VisualReportCycle> reportCycleList = visualReportCycleService.findByids(deleteQuery.getIds());
        Example cycleExample = new Example(VisualReportCycle.class);
        cycleExample.createCriteria().andEqualTo("id",deleteQuery.getIds());
        List<VisualReportCycle> cycleList = visualReportCycleService.findByExample(cycleExample);// dashboardReportCycleService.findByIds(deleteQuery.getIds());
        for (VisualReportCycle visualReportCycle : cycleList) {
            JobModel jobModel = new JobModel();
            jobModel.setJobName(JOB_PRE + visualReportCycle.getId());
            jobModel.setCronTime(visualReportCycle.getCron());
            jobModel.setJobClazz(ReportCycleTask.class);
            // 删除时删除该任务
            TaskManager.removeJob(jobModel);
        }
        int result = visualReportCycleService.deleteByIds(deleteQuery.getIds());
        //关闭任务
        if (result > 0) {
            reportCycleList.forEach(visualReportCycle -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(visualReportCycle,"删除周期报表",transferMap);
            });
        }
        return this.result(result == 1);
    }
    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询VisualReportCycle（分页）")
    @SysRequestLog(description="查询周期报表", actionType = ActionType.SELECT)
    @PostMapping("/report/cycle")
    public VList<VisualReportCycle> queryVisualReportCycle(@RequestBody VisualReportCycleQuery queryVo) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(queryVo, VisualReportCycle.class);
        List<VisualReportCycle> list =  visualReportCycleService.findByExample(example);
        return this.vList(list);
    }



    @ResponseBody
    @PostMapping("/report/cycle/pdf/export")
    @SysRequestLog(description="页面pdf导出", actionType = ActionType.EXPORT)
    @ApiOperation("页面pdf导出")
    public Result pdfExport(@RequestBody Map<String, String> record) {
        SyslogSenderUtils.sendExportSyslog();
        String reportId = "";
        if (record.containsKey("__id")) {
            reportId = record.get("__id");
        } else if (record.containsKey("__code") && convertMap.containsKey(record.get("__code"))) {
            reportId = convertMap.get(record.get("__code")).toString();
            record.put("__id",reportId);
        }
        if (StringUtils.isEmpty(reportId)) {
            return this.result(ErrorCode.REPORT_NULL);
        }
        Report report = reportService.findById(Integer.valueOf(reportId));
        if(report!=null) {
            PdfExport.PdfProgress pdfProgress = visualReportCycleService.pdfExport(record);
            VisualReportCycleFile cycleLog = new VisualReportCycleFile();
            cycleLog.setCreateTime(new Date());
            cycleLog.setReportId(report.getId());
            cycleLog.setCycleId(0);
            cycleLog.setFileName(pdfProgress.getFileName());
            cycleLog.setFilePath(visualReportCycleService.getReportFilePath());
            cycleLog.setCycleTitle(report.getTitle());
            cycleLog.setReportTitle(report == null ? "" : report.getTitle());
            cycleLog.setStatus(1);
            cycleLog.setFileId(pdfProgress.getWorkId());
            cycleLog.setSourceType(1);
            visualReportCycleFileService.save(cycleLog);
            return this.vData(pdfProgress);
        }
        return this.result(ErrorCode.REPORT_NULL);
    }


    @ResponseBody
    @PostMapping("/report/cycle/pdf/java/export")
    @SysRequestLog(description="页面pdf导出", actionType = ActionType.SELECT)
    @ApiOperation("页面pdf导出")
    public Result pdfJavaExport(@RequestBody Map<String, String> record) {
        SyslogSenderUtils.sendExportSyslog();
        String reportId = "";
        if (record.containsKey("__id")) {
            reportId = record.get("__id");
        } else if (record.containsKey("__code") && convertMap.containsKey(record.get("__code"))) {
            reportId = convertMap.get(record.get("__code")).toString();
            record.put("__id",reportId);
        }
        if (StringUtils.isEmpty(reportId)) {
            return this.result(ErrorCode.REPORT_NULL);
        }
        VisualReportJava visualReportJava = reportJavaService.findById(Integer.valueOf(reportId));
        VisualReportJavaVO visualReportJavaVO = new VisualReportJavaVO();
        BeanUtils.copyProperties(visualReportJava,visualReportJavaVO);
        visualReportJavaVO.setExportType(1);
        visualReportJavaVO.setFileName(record.get("fileName"));
        if(visualReportJava!=null) {
            PdfExport.PdfProgress pdfProgress = visualReportCycleService.pdfJavaExport(visualReportJavaVO);
            VisualReportCycleFile cycleLog = new VisualReportCycleFile();
            cycleLog.setCreateTime(new Date());
            cycleLog.setReportId(visualReportJava.getId());
            cycleLog.setCycleId(0);
            cycleLog.setFileName(pdfProgress.getFileName());
            cycleLog.setFilePath(visualReportCycleService.getReportFilePath());
            cycleLog.setCycleTitle(visualReportJava.getTitle());
            cycleLog.setReportTitle(visualReportJava == null ? "" : visualReportJava.getTitle());
            cycleLog.setStatus(1);
            cycleLog.setFileId(pdfProgress.getWorkId());
            cycleLog.setSourceType(1);
            visualReportCycleFileService.save(cycleLog);
            return this.vData(pdfProgress);
        }
        return this.result(ErrorCode.REPORT_NULL);
    }

    @ResponseBody
    @GetMapping("/progress/pdf/{workId}")
    @ApiOperation("根据workid获取导出进度")
    public Result getProgress(@PathVariable("workId") String workId) {
        PdfExport.PdfProgress pdfProgress =visualReportCycleService.getProgress(workId);
        if(pdfProgress.getProcess()==1){
            List<VisualReportCycleFile> visualReportCycleFileList = visualReportCycleFileService.findByProperty(VisualReportCycleFile.class,"filePath",workId);
            if(visualReportCycleFileList!=null && visualReportCycleFileList.size()>0){
                visualReportCycleFileList.forEach(p->{
                    p.setStatus(2);
                    visualReportCycleFileService.save(p);
                });
            }

        }
        return this.vData(pdfProgress);
    }

    @GetMapping("/download/pdf/{workId}")
    @SysRequestLog(description="下载pdf文件", actionType = ActionType.DOWNLOAD)
    @ApiOperation("根据workid下载pdf文件")
    public void download(@ApiParam("workId")@PathVariable("workId") String workId, HttpServletResponse resp, HttpServletRequest req) {
        SyslogSenderUtils.sendDownLosdSyslog();
        PdfExport.PdfProgress progress = PdfExport.getProcess(workId);
        if (null == progress) {
            log.error("未查询到指定workid文件: " + workId);
            return;
        }
        String pdfFileName = progress.getWorkId()+".pdf";
        String pdfFilePath = visualReportCycleService.getReportFilePath() + File.separator + pdfFileName;
        String downloadName = progress.getFileName();
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("content-type", "application/octet-stream");
        resp.setContentType("application/octet-stream");
        String agent = req.getHeader("USER-AGENT");
        try {
            if(agent != null && agent.toLowerCase(Locale.ENGLISH).indexOf("firefox") > 0)
            {
            downloadName = "=?UTF-8?B?" + (new String(Base64Utils.encodeToString(downloadName.getBytes("UTF-8")))) + "?=";
            } else {
                downloadName =  java.net.URLEncoder.encode(downloadName, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        resp.setHeader("Content-Disposition", "attachment;filename=" + CleanUtil.cleanString(downloadName));
        try (InputStream in = FileUtils.openInputStream(new File(CleanUtil.cleanString(pdfFilePath)));
             OutputStream out = resp.getOutputStream();) {
            IOUtils.copy(in, out);
        } catch (IOException e) {
            log.error("", e);
        }
    }



    /**
     * 获取所有报表模板
     */
    @ApiOperation(value = "获取所有报表模板")
    @SysRequestLog(description="获取所有报表模板", actionType = ActionType.SELECT)
    @GetMapping("/report/templates")
    public Result getAllTemplates() {
        if(org.apache.commons.lang.StringUtils.isNotEmpty(reportType) && reportType.equals("java")) {
            return this.vData(reportJavaService.findAll());
        }else{
            return this.vData(reportService.findAll());
        }
    }
}