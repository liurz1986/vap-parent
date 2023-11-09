package com.vrv.vap.admin.common.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.admin.common.util.SpringContextUtil;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.BaseReport;
import com.vrv.vap.admin.model.VisualReportCycle;
import com.vrv.vap.admin.model.VisualReportCycleFile;
import com.vrv.vap.admin.service.BaseReportService;
import com.vrv.vap.admin.service.VisualReportCycleFileService;
import com.vrv.vap.admin.service.VisualReportCycleService;
import com.vrv.vap.admin.util.CronUtils;
import com.vrv.vap.admin.util.ReportEngine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.quartz.JobDataMap;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ReportCycleTask extends BaseTask {

    private static Logger logger = LoggerFactory.getLogger(ReportCycleTask.class);

    VisualReportCycleFileService visualReportCycleFileService = SpringContextUtil.getApplicationContext().getBean(VisualReportCycleFileService.class);
    VisualReportCycleService visualReportCycleService = SpringContextUtil.getApplicationContext().getBean(VisualReportCycleService.class);
    BaseReportService baseReportService = SpringContextUtil.getApplicationContext().getBean(BaseReportService.class);

    @Override
    void run(String jobName, JobDataMap jobDataMap) {
        if (null == jobDataMap) {
            logger.error("参数异常");
            return;
        }
        Date now = new Date();
        // 周期报表编号
        Integer cycleId = jobDataMap.getInt("id");
        logger.info("周期报表生成任务开始执行cycleId:"+cycleId);
        // 报表存放路径
        String filePath = visualReportCycleService.getReportFilePath();
        // 根据编号获取该周期报表详情
        VisualReportCycle visualReportCycle = visualReportCycleService.findById(cycleId);
        // 周期报表生成日志
        VisualReportCycleFile cycleLog = new VisualReportCycleFile();
        cycleLog.setCreateTime(new Date());
        cycleLog.setReportId(visualReportCycle.getReportId());
        cycleLog.setCycleId(visualReportCycle.getId());
        String _suffix = "pdf";
        if(StringUtils.isNotEmpty(visualReportCycle.getFileType()) ){
            if("3".equals(visualReportCycle.getFileType())){
                _suffix = "html";
            }
            if("2".equals(visualReportCycle.getFileType())){
                _suffix = "doc";
            }
            if("4".equals(visualReportCycle.getFileType())){
                _suffix = "wps";
            }
        }
        //visualReportCycle.setFileType(_suffix);
        cycleLog.setFileName(visualReportCycle.getTitle()+"-"+ TimeTools.format3(new Date()) + "."+_suffix);

        cycleLog.setFilePath(filePath);
        cycleLog.setCycleTitle(visualReportCycle.getTitle());

        cycleLog.setStatus(1);
        cycleLog.setSourceType(0);
        BaseReport report = baseReportService.findById(visualReportCycle.getReportId());
        if(StringUtils.isNotEmpty(visualReportCycle.getParam())){
            Map<String,Object> params = (Map) JSON.parse(visualReportCycle.getParam());
            report.setBindParam(params);
        }
        cycleLog.setReportTitle(report==null?"":report.getTitle());
        new Thread(new ConnectJavaRunable(visualReportCycle, cycleLog,report,_suffix,now)).start();

    }

    public class ConnectJavaRunable implements Runnable {
        private VisualReportCycle reportCycle;
        private VisualReportCycleFile log;
        private BaseReport report;
        private String fileType;
        private Date date;

        public ConnectJavaRunable(VisualReportCycle reportCycle,
                                  VisualReportCycleFile log,
                                  BaseReport report,String fileType,Date date
                            ) {
            this.reportCycle = reportCycle;
            this.log = log;
            this.report = report;
            this.fileType = fileType;
            this.date = date;
        }

        @Override
        public void run() {
            Map<String,Object> params = new HashMap<>();
            String startTime = CronUtils.getLastTriggerTime(reportCycle.getCron(),2);
            String endTime = TimeTools.format2(date);
            params.put("startTime",startTime);
            params.put("endTime",endTime);
            logger.info("周期报表:"+reportCycle.getId()+" params->"+ JSONObject.toJSONString(params));
            report.setBindParam(params);
            String fileid = ReportEngine.cycleReport(report,fileType);
            //更新执行次数
            reportCycle.setLastTime(new Date());
            reportCycle.setCount(reportCycle.getCount()+1);
            visualReportCycleService.update(reportCycle);
            log.setStatus(2);
            if (StringUtils.isEmpty(fileid)) {
                //失败直接设置为0
                log.setStatus(0);
            }
            log.setFileId(fileid);
            visualReportCycleFileService.save(log);
            logger.info("周期报表生成任务执行结束cycleId:"+reportCycle.getId());
        }
    }

    @Override
    void run(String jobName) {
        this.run(jobName, null);
    }
}
