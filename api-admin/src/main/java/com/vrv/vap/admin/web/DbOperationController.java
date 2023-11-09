package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.DbOperationInfo;
import com.vrv.vap.admin.service.DbOperationService;
import com.vrv.vap.admin.util.FileFilterUtil;
import com.vrv.vap.admin.vo.DbOperationInfoVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.*;
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

import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
* 数据库备份还原操作
*
*
*/
@RestController
@Api(value = "数据库备份还原操作")
@RequestMapping("/db/operation")
public class DbOperationController extends ApiController {

    private static Logger logger = LoggerFactory.getLogger(DbOperationController.class);

    @Autowired
    private DbOperationService dbOperationService;

    @Value("${filePath:opt/file/backup}")
    private String filePath;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("operationType", "{\"1\":\"备份\",\"2\":\"还原\",\"3\":\"上传\",\"4\":\"下载\"}");
        transferMap.put("operationStatus", "{\"1\":\"成功\",\"2\":\"失败\",\"3\":\"运行中\",\"4\":\"已过期\"}");
    }

    /**
    * 查询所有操作记录
    */
    @ApiOperation(value = "查询所有操作记录")
    @GetMapping
    public VData<List<DbOperationInfo>> getAllDbOperationInfos() {
        return this.vData(dbOperationService.findAll());
    }

    /**
    * 添加
    **/
    @ApiOperation(value = "添加操作记录")
    @PutMapping
    @SysRequestLog(description="添加操作记录", actionType = ActionType.ADD)
    public Result addDbOperationInfo(@RequestBody DbOperationInfo dbOperationInfo) {
        int result = dbOperationService.save(dbOperationInfo);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(dbOperationInfo,"添加操作记录",transferMap);
        }
        return this.result(result == 1);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改操作记录")
    @PatchMapping
    @SysRequestLog(description="修改操作记录", actionType = ActionType.UPDATE)
    public Result updateAlarmItem(@RequestBody DbOperationInfo dbOperationInfo) {
        DbOperationInfo operationInfoQuery = new DbOperationInfo();
        operationInfoQuery.setUuid(dbOperationInfo.getUuid());
        DbOperationInfo operationInfoSec = dbOperationService.findOne(operationInfoQuery);
        int result = dbOperationService.update(dbOperationInfo);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(operationInfoSec,dbOperationInfo,"修改操作记录",transferMap);
        }
        return this.result(result == 1);
    }

    /**
    * 删除
    **/
    @ApiOperation(value = "删除操作记录")
    @DeleteMapping
    @SysRequestLog(description="删除操作记录", actionType = ActionType.DELETE)
    public Result delAlarmItem(@RequestBody DeleteQuery deleteQuery) {
        List<DbOperationInfo> operationInfoList = dbOperationService.findByids(deleteQuery.getIds());
        int result = dbOperationService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            operationInfoList.forEach(dbOperationInfo -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(dbOperationInfo,"删除操作记录",transferMap);
            });
        }
        return this.result(result == 1);
    }

    /**
     * 查询（分页）
     */
    @ApiOperation(value = "查询操作记录（分页）")
    @PostMapping
    @SysRequestLog(description="查询操作记录（分页）", actionType = ActionType.SELECT)
    public VList<DbOperationInfo> queryDbOperationInfos(@RequestBody DbOperationInfoVO dbOperationInfoVO) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(dbOperationInfoVO, DbOperationInfo.class);
        return this.vList(dbOperationService.findByExample(example));
    }

    @ApiOperation(value = "查询可用备份记录")
    @PostMapping("/backup/available")
    @SysRequestLog(description="上传文件", actionType = ActionType.SELECT)
    public VList<DbOperationInfo> getAvailableFile(@RequestBody Query query) {
        Example example = this.pageQuery(query, DbOperationInfo.class);
        example.getOredCriteria().get(0).andIn("operationType", Arrays.asList(1,3)).andEqualTo("operationStatus", 1);
        return this.vList(dbOperationService.findByExample(example));
    }

    /**
     * 备份
     **/
    @ApiOperation(value = "备份操作")
    @PostMapping("/backup")
    @SysRequestLog(description="备份操作", actionType = ActionType.AUTO)
    public Result backup(@RequestBody DbOperationInfo dbOperationInfo) {
        return dbOperationService.backup(dbOperationInfo);
    }

    /**
     * 还原
     **/
    @ApiOperation(value = "还原操作")
    @PostMapping("/recovery")
    @SysRequestLog(description="还原操作", actionType = ActionType.AUTO)
    public Result recovery(@RequestBody DbOperationInfo dbOperationInfo) {
        return dbOperationService.recovery(dbOperationInfo);
    }

    @ApiOperation(value = "下载文件")
    @GetMapping("/download")
    @SysRequestLog(description="下载数据库备份文件", actionType = ActionType.DOWNLOAD)
    public Result download(@RequestParam("uuid") @ApiParam(value = "唯一ID", required = true) String uuid, HttpServletResponse response) {
        SyslogSenderUtils.sendDownLosdSyslog();
        return dbOperationService.downloadFile(uuid, response);
    }

    @ApiOperation(value = "上传文件")
    @PostMapping("/upload")
    @SysRequestLog(description="上传数据库备份文件", actionType = ActionType.UPLOAD,manually = false)
    public VData<DbOperationInfo> upload(@RequestBody MultipartFile file) {
        String fileName = file.getOriginalFilename();
        String fileType = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase(Locale.ENGLISH);
        // 扫描备注：已做文件格式白名单校验
        if (!FileFilterUtil.validFileType(fileType)) {
            return this.vData(false);
        }
        return this.vData(dbOperationService.uploadFile(file));
    }

    @ApiOperation(value = "重启服务")
    @PostMapping("/restart")
    @SysRequestLog(description="重启服务", actionType = ActionType.SELECT)
    public Result restart(@RequestBody DbOperationInfo dbOperationInfo) {
        return dbOperationService.restart(dbOperationInfo);
    }

}