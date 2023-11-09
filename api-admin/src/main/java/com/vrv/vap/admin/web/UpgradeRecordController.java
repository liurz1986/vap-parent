package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.model.UpgradeRecordInfo;
import com.vrv.vap.admin.service.UpgradeRecordService;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.util.FileFilterUtil;
import com.vrv.vap.admin.vo.UpgradeRecordQuery;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
* 系统升级记录
*
*
*/
@RestController
@Api(value = "系统升级记录")
@RequestMapping("/upgrade/record")
public class UpgradeRecordController extends ApiController {

    private static Logger logger = LoggerFactory.getLogger(UpgradeRecordController.class);

    @Autowired
    private UpgradeRecordService upgradeRecordService;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("result","{\"0\":\"失败\",\"1\":\"成功\"}");
    }

    @Value("${vap_work_dir:/opt/test}")
    private String workDir;

    /**
    * 查询所有升级记录
    */
    @ApiOperation(value = "查询所有升级记录")
    @GetMapping
    @SysRequestLog(description = "查询所有升级记录",actionType = ActionType.SELECT)
    public VData<List<UpgradeRecordInfo>> getAllDbOperationInfos() {
        return this.vData(upgradeRecordService.findAll());
    }

    /**
    * 添加升级记录
    **/
    @ApiOperation(value = "添加升级记录")
    @PutMapping
    @SysRequestLog(description="添加升级记录", actionType = ActionType.ADD)
    public Result addDbOperationInfo(@RequestBody UpgradeRecordInfo upgradeRecordInfo) {
        int result = upgradeRecordService.save(upgradeRecordInfo);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(upgradeRecordInfo,"添加升级记录",transferMap);
        }
        return this.result(result == 1);
    }

    /**
    * 修改升级记录
    **/
    @ApiOperation(value = "修改升级记录")
    @PatchMapping
    @SysRequestLog(description="修改升级记录", actionType = ActionType.UPDATE)
    public Result updateAlarmItem(@RequestBody UpgradeRecordInfo upgradeRecordInfo) {
        UpgradeRecordInfo infoSec = upgradeRecordService.findById(upgradeRecordInfo.getId());
        int result = upgradeRecordService.update(upgradeRecordInfo);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(infoSec,upgradeRecordInfo,"修改升级记录",transferMap);
        }
        return this.result(result == 1);
    }

    /**
    * 删除升级记录
    **/
    @ApiOperation(value = "删除升级记录")
    @DeleteMapping
    @SysRequestLog(description="删除升级记录", actionType = ActionType.DELETE)
    public Result delAlarmItem(@RequestBody DeleteQuery deleteQuery) {
        List<UpgradeRecordInfo> infoList = upgradeRecordService.findByids(deleteQuery.getIds());
        int result = upgradeRecordService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            infoList.forEach(upgradeRecordInfo -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(upgradeRecordInfo,"删除升级记录",transferMap);
            });
        }
        return this.result(result > 0);
    }

    /**
     * 查询升级记录（分页）
     */
    @ApiOperation(value = "查询升级记录（分页）")
    @PostMapping
    @SysRequestLog(description="查询升级记录", actionType = ActionType.SELECT)
    public VList<UpgradeRecordInfo> queryDbOperationInfos(@RequestBody UpgradeRecordQuery query) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(query, UpgradeRecordInfo.class);
        return this.vList(upgradeRecordService.findByExample(example));
    }

    /**
     * 上传升级文件
     */
    @ApiOperation(value = "上传升级文件")
    @PostMapping("/upload")
    @SysRequestLog(description="上传升级文件", actionType = ActionType.UPLOAD,manually = false)
    public Result queryDbOperationInfos(@ApiParam(value = "上传的文件", required = true) @RequestParam("file") MultipartFile file) {
        String fileName = CleanUtil.cleanString(file.getOriginalFilename());
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
        // 扫描备注：已做文件格式白名单校验
        if (!FileFilterUtil.validFileType(fileType)) {
            return new Result("-1","文件类型错误，支持类型:zip");
        }
        logger.info("上传开始时间：" + System.currentTimeMillis());
        String filePath = Paths.get(workDir,"/upgrade", fileName).toString();
        boolean result = FileUtils.uploadFile(file, filePath);
        logger.info("上传结束时间：" + System.currentTimeMillis());
        return this.result(result);
    }

    /**
     * 开始升级
     */
    @ApiOperation(value = "开始升级")
    @PostMapping("/start")
    @SysRequestLog(description="开始升级", actionType = ActionType.SELECT)
    public Result upgrade() {
        try {
            // 校验执行
            Process proCheck = Runtime.getRuntime().exec(CleanUtil.cleanString("sh " + workDir + "/common/check_sign.sh &"));
            int exitValue = proCheck.waitFor(1,TimeUnit.MINUTES) ? 0 : 1;
            if (exitValue == 0) {
                String line = "";
                String lastLine = "";
                BufferedReader checkInput = new BufferedReader(new InputStreamReader(proCheck.getInputStream()));
                logger.info("开始读取 check_sign");
                while ((line = checkInput.readLine()) != null) {
                    lastLine = line;
                }
                checkInput.close();
                //最后一行 会返回执行状态
                if(lastLine.equals("false")) {
                    return this.result(false);
                }

                //开始升级
                Process proUpgrade = Runtime.getRuntime().exec(CleanUtil.cleanString("sh " + workDir + "/common/upgrade.sh &"));
                proUpgrade.waitFor(10,TimeUnit.MINUTES);
                return this.result(true);
            }
        } catch (Exception e) {
            logger.error("升级失败", e);
        }
        return this.result(false);
    }
}