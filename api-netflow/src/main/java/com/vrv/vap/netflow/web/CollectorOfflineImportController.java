package com.vrv.vap.netflow.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.netflow.common.enums.ErrorCode;
import com.vrv.vap.netflow.common.enums.TemplateTypeEnum;
import com.vrv.vap.netflow.service.CollectorOfflineImportService;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author lilang
 * @date 2022/3/29
 * @description 离线数据导入
 */
@RequestMapping(path = "/collector/offline/import")
@RestController
public class CollectorOfflineImportController extends ApiController {

    @Resource
    CollectorOfflineImportService collectorOfflineImportService;

    @ApiOperation("离线数据导入")
    @PostMapping
    @SysRequestLog(description="离线数据导入", actionType = ActionType.IMPORT,manually = false)
    public Result importFile(@ApiParam(value = "导入的文件", required = true) @RequestParam MultipartFile file,@ApiParam(value = "类型", required = true) @RequestParam("type") Integer type,
                             @ApiParam(value = "模板ID", required = true) @RequestParam("templateId") Integer templateId) {
        String fileName = file.getOriginalFilename();
        // 文件格式不对
        if (TemplateTypeEnum.TYPE_XLS.getCode().equals(type) && !(fileName.endsWith("xls"))) {
            return this.result(ErrorCode.OFFLINE_TEMPLATE_TYPE_ERROR);
        }
        if (TemplateTypeEnum.TYPE_XML.getCode().equals(type) && !fileName.endsWith("xml")) {
            return this.result(ErrorCode.OFFLINE_TEMPLATE_TYPE_ERROR);
        }
        ErrorCode errorCode = collectorOfflineImportService.importData(file,type,templateId);
        if (errorCode != null) {
            return this.result(errorCode);
        }
        return this.result(true);
    }
}
