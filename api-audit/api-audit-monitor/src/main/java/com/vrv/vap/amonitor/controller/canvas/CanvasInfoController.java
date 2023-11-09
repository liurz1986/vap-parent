package com.vrv.vap.amonitor.controller.canvas;


import com.vrv.vap.amonitor.entity.AssetCanvasInfo;
import com.vrv.vap.amonitor.service.canvas.CanvasInfoService;
import com.vrv.vap.amonitor.vo.AssetCanvasInfoQuery;
import com.vrv.vap.amonitor.vo.DeleteModel;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@RestController
public class CanvasInfoController {

    @Autowired
    private CanvasInfoService service;

    private static Log log = LogFactory.getLog(CanvasInfoController.class);

    @GetMapping("canvas_info/{id}")
    @ApiOperation("获取画布信息")
    public VData<AssetCanvasInfo> querySingleRecode(@PathVariable Integer id) {
        AssetCanvasInfo record = null;
        try {
            AssetCanvasInfo param = new AssetCanvasInfo();
            param.setId(id);
            record = service.querySingle(param);

        } catch (Exception e) {
            log.error("", e);
            return VoBuilder.errorVdata();
        }
        return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
    }

    @PostMapping("canvas_info")
    @ApiOperation("查询画布列表")
    public VList<AssetCanvasInfo> queryByPage(@RequestBody AssetCanvasInfoQuery record) {
        VList<AssetCanvasInfo> recordList = null;
        try {
            recordList = service.queryByPage(record);
        } catch (Exception e) {
            log.error("", e);
            recordList = new VList<AssetCanvasInfo>(0, Collections.emptyList());
        }
        return recordList;
    }

    @GetMapping("canvas_info")
    @ApiOperation("查询所有画布列表")
    public VData<List<AssetCanvasInfo>> queryAll() {
        VData<List<AssetCanvasInfo>> recordList = null;
        try {
            recordList = service.queryAll(new AssetCanvasInfoQuery());
        } catch (Exception e) {
            log.error("", e);
            recordList = new VData<>(Collections.emptyList());
        }
        return recordList;
    }

    @DeleteMapping("canvas_info")
    @ApiOperation("删除画布及相关信息")
    public Result deleteItem(@RequestBody DeleteModel record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            AssetCanvasInfo param = new AssetCanvasInfo();
            param.setId(record.getIntegerId());
            service.deleteItem(param);
        } catch (NumberFormatException e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }


    @PutMapping("canvas_info")
    @ApiOperation("新增画布")
    public Result add(@RequestBody AssetCanvasInfo record) {
        Result res = null;
        try {
            record.setCreateTime(LocalDateTime.now());
            record.setUpdateTime(LocalDateTime.now());
            service.addItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    @PatchMapping("canvas_info")
    @ApiOperation("修改画布")
    public Result update(@RequestBody AssetCanvasInfo record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            record.setUpdateTime(LocalDateTime.now());
            service.updateItem(record);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @PatchMapping("canvas_info/top")
    @ApiOperation("设置默认(置顶)")
    public Result setDefaultItem(@RequestBody AssetCanvasInfo record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            service.setDefaultItem(record);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }


}
