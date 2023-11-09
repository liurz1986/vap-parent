package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.admin.model.VisualReportJava;
import com.vrv.vap.admin.service.Report4JavaService;
import com.vrv.vap.admin.service.VisualReportJavaService;
import com.vrv.vap.admin.vo.VisualReportJavaVO;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * java报表（freemarker + jfreechart）
 * Created by lizj on 2020/12/11
 */
@RestController
@RequestMapping(path = "/report4Java")
@Slf4j
public class Report4JavaController extends ApiController {

    @Autowired
    private Report4JavaService report4JavaService;
    @Autowired
    private VisualReportJavaService visualReportJavaService;

    @GetMapping("/preview/{reportId}")
    @SysRequestLog(description="报表预览", actionType = ActionType.SELECT)
    @ApiOperation("报表预览")
    public Object previewGet(@PathVariable("reportId") String reportId, HttpServletResponse resp, HttpServletRequest req) {
        if(StringUtils.isEmpty(reportId)){
             return   new Result("-1","报表ID未传入");
        }
        List<VisualReportJava> list= visualReportJavaService.findByids(reportId);
        if(list.size()==0){
            return   new Result("-1","无对应报表");
        }
        resp.addHeader("Access-Control-Allow-Origin", "*");
        resp.setHeader("Content-Type", "text/html;charset=UTF-8");
        return report4JavaService.preview(list.get(0));
    }




    @ApiOperation("报表预览")
    @SysRequestLog(description="报表预览")
    @PostMapping(value = "/preview")
    public String preview(HttpServletResponse response, @RequestBody VisualReportJava param) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        return report4JavaService.preview(param);
    }

    @ApiOperation("导出")
    @SysRequestLog(description="导出",actionType = ActionType.EXPORT)
    @PostMapping(value = "/export")
    public Result export(@RequestBody VisualReportJavaVO param) {
        SyslogSenderUtils.sendExportSyslog();
        String fileId = report4JavaService.export(param);
        return this.vData(fileId);
    }

}
