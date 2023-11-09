package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.SuperviseDataReceive;
import com.vrv.vap.admin.service.SuperviseDataReceiveService;
import com.vrv.vap.admin.util.FileFilterUtil;
import com.vrv.vap.admin.vo.supervise.SuperviseDataReceiveQuery;
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
import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@RestController
@Api(value = "级联数据接收")
@RequestMapping(path = "")
public class SuperviseDataReceiveController extends ApiController {

    @Autowired
    private SuperviseDataReceiveService superviseDataReceiveService;

    public static final Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @ApiOperation(value = "查询所有级联接收数据")
    @GetMapping
    public VData< List<SuperviseDataReceive>> getAllSuperviseDataReceive() {
        List<SuperviseDataReceive> list = superviseDataReceiveService.findAll();
        return this.vData(list);
    }

    @ApiOperation(value = "新增级联接收数据")
    @PutMapping("/superviseDataReceive")
    @SysRequestLog(description="新增级联接收数据", actionType = ActionType.ADD)
    public Result addSuperviseDataSubmit(@RequestBody SuperviseDataReceive superviseDataReceive) {
        int result = superviseDataReceiveService.save(superviseDataReceive);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(superviseDataReceive,"新增级联接收数据");
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "修改级联接收数据")
    @PatchMapping("/superviseDataReceive")
    @SysRequestLog(description = "修改级联接收数据", actionType = ActionType.UPDATE)
    public Result updateSuperviseDataSubmit(@RequestBody SuperviseDataReceive superviseDataReceive) {
        SuperviseDataReceive dataReceiveSec = superviseDataReceiveService.findById(superviseDataReceive.getId());
        int result = superviseDataReceiveService.update(superviseDataReceive);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(dataReceiveSec,superviseDataReceive,"修改级联接收数据");
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除级联接收数据")
    @DeleteMapping("/superviseDataReceive")
    @SysRequestLog(description = "删除级联接收数据", actionType = ActionType.DELETE)
    public Result delSuperviseDataSubmit(@RequestBody DeleteQuery deleteQuery) {
        List<SuperviseDataReceive> dataReceiveList = superviseDataReceiveService.findByids(deleteQuery.getIds());
        int result = superviseDataReceiveService.deleteByIds(deleteQuery.getIds());
        if (result > 0) {
            dataReceiveList.forEach(superviseDataReceive -> {
                SyslogSenderUtils.sendDeleteSyslog(superviseDataReceive,"删除级联接收数据");
            });
        }
        return this.result(result == 1);
    }

    @PostMapping("/superviseDataReceive")
    @ApiOperation("查询级联接收数据（分页）")
    @SysRequestLog(description = "查询级联接收数据", actionType = ActionType.SELECT)
    public VList<SuperviseDataReceive> querySuperviseDataReceive(@RequestBody SuperviseDataReceiveQuery query) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(query, SuperviseDataReceive.class);
        return this.vList(superviseDataReceiveService.findByExample(example));
    }

    @PostMapping("/superviseDataReceive/supervise/data/import")
    @ApiOperation(value = "导入上级监管平台数据接口")
    @SysRequestLog(description="导入上级监管平台数据接口", actionType = ActionType.IMPORT,manually = false)
    public Result importAannounce(@RequestParam("file") MultipartFile file) {
        String oriName = file.getOriginalFilename();
        String fileType = oriName.substring(oriName.lastIndexOf(".") + 1, oriName.length()).toLowerCase();
        // 扫描备注：已做文件格式白名单校验
        if (!FileFilterUtil.validFileType(fileType)) {
            return this.result(false);
        }
        return superviseDataReceiveService.importAnnounce(file);
    }

//    @PutMapping("/coor/api/routing/announce")
//    @ApiOperation(value = "在线接收上级监管平台数据")
//    @SysRequestLog(description="在线接收上级监管平台数据", actionType = ActionType.ADD)
//    public Result receiveAnnounce(@RequestBody Map info) {
//        return superviseDataReceiveService.saveAnnounce(info);
//    }
}