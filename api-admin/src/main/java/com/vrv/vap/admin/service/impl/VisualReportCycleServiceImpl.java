package com.vrv.vap.admin.service.impl;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.common.pdf.PdfData;
import com.vrv.vap.admin.common.pdf.PdfExport;
import com.vrv.vap.admin.common.pdf.PdfWriteHandler;
import com.vrv.vap.admin.mapper.VisualReportCycleMapper;
import com.vrv.vap.admin.model.VisualReportCycle;
import com.vrv.vap.admin.service.Report4JavaService;
import com.vrv.vap.admin.service.VisualReportCycleService;
import com.vrv.vap.admin.vo.VisualReportJavaVO;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.File;
import java.net.URI;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by CodeGenerator on 2020/09/10.
 */
@Service
@Transactional
public class VisualReportCycleServiceImpl extends BaseServiceImpl<VisualReportCycle> implements VisualReportCycleService {

    private static final Logger log = LoggerFactory.getLogger(VisualReportCycleServiceImpl.class);

    @Resource
    private VisualReportCycleMapper visualReportCycleMapper;

    @Autowired
    private Report4JavaService report4JavaService;

    private static Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss:SSS").create();
    /**
     * 截屏服务地址
     */
    @Value("${screen-capturer.url}")
    private String url;

    /**
     * 截屏文件生成路径
     */
    @Value("${screen-capturer.file-path}")
    private String filePath;


//    /**
//     * 本地保存路径
//     */
//    @Value("${report.file-path}")
//    private String reportFilePath;

    @Override
    public String exportReport(Map<String, Object> params) {
        // 调用nodejs服务，开始导出pdf
        String printUrl = "";
        Map<String, Object> responseBean = new HashMap<>();
        try {
            printUrl = url + "/print/" + URLEncoder.encode(gson.toJson(params), "UTF-8");
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(printUrl);
            responseBean = restTemplate.getForObject(uri, Map.class);
        } catch (Exception e) {
            log.error(e+"");
            return null;
        }
        return  (String)responseBean.get("data");
    }

    @Override
    public String getReportFilePath() {
        return filePath;
    }

    @Override
    public boolean checkExistFile(String fileId) {
        return checkExistFileWithSuffix(fileId,"pdf");
    }

    @Override
    public boolean checkExistFileWithSuffix(String fileId, String suffix) {
        String fileName = filePath + File.separator + fileId + "."+suffix;
        log.info("校验文件名称：" + fileName);
        boolean isPdfExist = new File(fileName).exists();
        return isPdfExist;
    }


    @Override
    public PdfExport.PdfProgress pdfExport(Map<String, String> record) {
        // 调用nodejs服务，开始导出pdf
        String printUrl = "";
        Map<String, Object> responseBean = new HashMap<>();
        try {
            printUrl = url + "/print/" + URLEncoder.encode(gson.toJson(record), "UTF-8");
            RestTemplate restTemplate = new RestTemplate();
            URI uri = new URI(printUrl);
            responseBean = restTemplate.getForObject(uri, Map.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        PdfData data = new PdfData();
        data.setTaskId((String) responseBean.get("data"));
        data.setPdfName(record.get("fileName"));
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        String fileName = filePath + File.separator + data.getTaskId() + ".pdf";
        if(StringUtils.isEmpty(record.get("fileName"))){
            data.setPdfName(fileName);
        }
        if(!data.getPdfName().endsWith(".pdf")){
            data.setPdfName(data.getPdfName()+".pdf");
        }
        return PdfExport.build(data).start(PdfWriteHandler.fun(f -> {
            f.generatePdf(fileName);
        }));
    }

    @Override
    public PdfExport.PdfProgress pdfJavaExport(VisualReportJavaVO visualReportJavaVO) {
        // 调用nodejs服务，开始导出pdf
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        String fileid = report4JavaService.export(visualReportJavaVO);
        PdfData data = new PdfData();
        data.setTaskId(fileid);
        data.setPdfName(visualReportJavaVO.getFileName());
        String fileName = filePath + File.separator + data.getTaskId() + ".pdf";
        if(StringUtils.isEmpty(visualReportJavaVO.getFileName())){
            data.setPdfName(fileName);
        }
        if(!data.getPdfName().endsWith(".pdf")){
            data.setPdfName(data.getPdfName()+".pdf");
        }
        return PdfExport.build(data).start(PdfWriteHandler.fun(f -> {
            f.generatePdf(fileName);
        }));
    }


    @Override
    public PdfExport.PdfProgress getProgress(String workId) {
        PdfExport.PdfProgress progress = PdfExport.getProcess(workId);
        return progress;
    }
}
