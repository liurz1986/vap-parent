package com.vrv.vap.admin.common.task;

import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.VisualReportCycleFile;
import com.vrv.vap.admin.service.VisualReportCycleFileService;
import com.vrv.vap.admin.service.VisualReportCycleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author wn
 * @date 2020/9/17
 * @description
 */
@Component
public class CheckReportCycleTask {

    private static final Logger log = LoggerFactory.getLogger(CheckReportCycleTask.class);

    @Autowired
    private VisualReportCycleFileService visualReportCycleFileService;
    @Autowired
    private VisualReportCycleService visualReportCycleService;

    @Scheduled(cron = "0 0/5 * * * ?")
    public void checkCycleFieStatus() {
        //获取所有的
        log.info("start checking reportfile status...");
        List<VisualReportCycleFile> visualReportCycleFileList = visualReportCycleFileService.findByProperty(VisualReportCycleFile.class,"status","1");
            visualReportCycleFileList.stream().forEach(visualReportCycleFile -> {
                String suffix =visualReportCycleFile.getFileName().substring(visualReportCycleFile.getFileName().lastIndexOf(".") + 1);
            boolean pdfExist = visualReportCycleService.checkExistFileWithSuffix(visualReportCycleFile.getFileId(),suffix);
           if(pdfExist){
               visualReportCycleFile.setStatus(2);
               visualReportCycleFileService.update(visualReportCycleFile);
           }else{

               //如果和创建时间相比，超过1一个小时，则视为失败
                if(TimeTools.getMillisecond(visualReportCycleFile.getCreateTime(),new Date())>60*60*1000){
                    log.info("ID:"+visualReportCycleFile.getId()+"--文件ID："+visualReportCycleFile.getFileId()+"--超过1小时未生成，标记失败");
                    visualReportCycleFile.setStatus(0);
                    visualReportCycleFileService.update(visualReportCycleFile);
                }
           }
        });
        log.info("finish refresh elasticsearch index alias ...");


    }


}
