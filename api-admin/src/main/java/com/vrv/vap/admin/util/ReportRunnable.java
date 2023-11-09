package com.vrv.vap.admin.util;

import com.alibaba.fastjson.JSONArray;
import com.vrv.vap.admin.common.pdf.PdfExport;
import com.vrv.vap.admin.model.BaseReport;
import com.vrv.vap.admin.model.BaseReportModel;
import com.vrv.vap.admin.vo.ReportComUid;
import com.vrv.vap.report.ReportGenerater;
import com.vrv.vap.report.config.ComponentConfig;
import com.vrv.vap.report.config.ReportConfig;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ReportRunnable implements Runnable{

    private static final Logger log = LoggerFactory.getLogger(ReportRunnable.class);

    private static final Map<String, String> STATUS = new HashMap<>();

    private BaseReport report;

    private String wid;

    public ReportRunnable() {
    }

    public static String getStatus(String uid) {
        return STATUS.get(uid);
    }

    public static String removeStatus(String uid) {
        return STATUS.remove(uid);
    }

    public ReportRunnable(BaseReport report,String wid) {
        this.report = report;
        this.wid = wid;
    }
    @Override
    public void run() {
        log.info("#######报表预览线程执行开始wid:"+this.wid);
        List<ComponentConfig> parent = new ArrayList<ComponentConfig>();
        ReportConfig reportConfig = new ReportConfig();
        reportConfig.setTitle(report.getTitle());
        reportConfig.setMenuEnable(report.getMenuEnable());
        reportConfig.setComponentList(parent);
        reportConfig.setSubTitle(report.getSubTitle());
        ReportEngine.threadReport(report,parent);
        /*
        try{
            String s  = reportConfig.toYaml();
        }catch (Exception e){

        }
        */
        String srcHtml = ReportGenerater.renderHtml(reportConfig);
        STATUS.put(wid,srcHtml);
        log.info("#######报表预览线程执行结束wid:"+this.wid);
    }
}
