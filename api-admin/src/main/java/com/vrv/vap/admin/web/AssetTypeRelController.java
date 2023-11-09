package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.AssetTypeRel;
import com.vrv.vap.admin.service.AssetTypeRelService;
import com.vrv.vap.admin.vo.AssetTypeRelQuery;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2022/6/6
 * @description
 */
@RestController
@RequestMapping(path = "/assetTypeRel")
public class AssetTypeRelController extends ApiController{

    @Autowired
    AssetTypeRelService assetTypeRelService;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("type", "{\"1\":\"主审\",\"2\":\"准入\",\"3\":\"运管\"}");
    }

    @ApiOperation("获取所有主审设备类型关系")
    @GetMapping
    public Result getTypeRelList() {
        return this.vData(assetTypeRelService.findAll());
    }

    @ApiOperation("根据类别获取主审设备类型关系")
    @GetMapping(path = "/{type}")
    public Result getListByType(@PathVariable Integer type) {
        if (type == null) {
            return this.vData(false);
        }
        return this.vData(assetTypeRelService.findAll().stream().filter(item -> type.equals(item.getType())).collect(Collectors.toList()));
    }

    @ApiOperation("查询主审设备类型关系")
    @PostMapping
    @SysRequestLog(description = "查询设备类型关系", actionType = ActionType.SELECT)
    public VList queryTypeRelList(@RequestBody AssetTypeRelQuery relQuery) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(relQuery,"查询设备类型关系",transferMap);
        Example example = this.pageQuery(relQuery, AssetTypeRel.class);
        return this.vList(assetTypeRelService.findByExample(example));
    }

    @ApiOperation("添加主审设备类型关系")
    @PutMapping
    @SysRequestLog(description = "添加设备类型关系", actionType = ActionType.ADD)
    public Result addTypeRel(@RequestBody AssetTypeRel auditTypeRel) {
        int result = assetTypeRelService.save(auditTypeRel);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(auditTypeRel, "添加设备类型关系",transferMap);
        }
        return this.vData(auditTypeRel);
    }

    @ApiOperation("修改主审设备类型关系")
    @PatchMapping
    @SysRequestLog(description = "修改设备类型关系", actionType = ActionType.UPDATE)
    public Result updateTypeRel(@RequestBody AssetTypeRel typeRel) {
        AssetTypeRel typeRelSec = assetTypeRelService.findById(typeRel.getId());
        if (typeRel.getId() == null) {
            return this.result(false);
        }
        int result = assetTypeRelService.updateSelective(typeRel);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(typeRelSec, typeRel,"修改设备类型关系",transferMap);
        }
        return this.result(result == 1);
    }

    @ApiOperation("删除主审设备类型关系")
    @DeleteMapping
    @SysRequestLog(description = "删除设备类型关系", actionType = ActionType.DELETE)
    public Result deleteTypeRel(@RequestBody DeleteQuery param) {
        List<AssetTypeRel> assetTypeRelList = assetTypeRelService.findByids(param.getIds());
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        int result = assetTypeRelService.deleteByIds(ids);
        if (result > 0) {
            assetTypeRelList.forEach(assetTypeRel -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(assetTypeRel,"删除设备类型关系",transferMap);
            });
        }
        return this.result(result >= 1);
    }
}
