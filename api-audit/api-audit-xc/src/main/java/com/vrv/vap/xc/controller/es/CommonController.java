package com.vrv.vap.xc.controller.es;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.xc.pojo.DataDumpLog;
import com.vrv.vap.xc.service.CommonService;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * es公共操作
 * Created by lizj on 2019/09/16.
 */
@RestController
public class CommonController {
    private Log log = LogFactory.getLog(CommonController.class);
    @Autowired
    private CommonService commonService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @PostMapping("/common/alias")
    @ApiOperation("创建别名")
    public Result createAlias() {
        return commonService.createAlias();
    }

    @PostMapping("/common/set/window_max_result")
    @ApiOperation("设置window_max_result")
    public Result setWindowMaxResult() {
        return commonService.setWindowMaxResult();
    }

    @PostMapping("/common/data/clean")
    @ApiOperation("es数据清理")
    public Result dataClean(@RequestBody Map<String, Object> paramModel) {
        return commonService.dataClean(paramModel);
    }

    @PostMapping("/common/data/backup")
    @ApiOperation("es数据备份")
    public Result dataBackup(@RequestBody Map<String, Object> paramModel) {
        return commonService.dataBackup(paramModel);
    }

    @PostMapping("/common/data/backup_clean")
    @ApiOperation("es数据备份(快照)-清理")
    public Result dataBackupAndCLean(@RequestBody Map<String, Object> paramModel) {
        return commonService.dataBackupAndCLean(paramModel);
    }

    @PostMapping("/common/data/roll_back")
    @ApiOperation("es数据还原")
    public Result datarollBack(@RequestBody DataDumpLog paramModel) {
        return commonService.datarollBack(paramModel);
    }

    @PostMapping("/common/data/transfer")
    @ApiOperation("es数据转存")
    public Result dataTransfer(@RequestBody Map<String, Object> paramModel) {
        return commonService.dataTransfer(paramModel);
    }

}
