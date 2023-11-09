package com.vrv.vap.xc.controller;


import com.github.mustachejava.util.HtmlEscaper;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.constants.CommentConstants;
import com.vrv.vap.xc.init.SingleTableBuilder;
import com.vrv.vap.xc.model.QueryModel;
import com.vrv.vap.xc.model.SingleTableModel;
import com.vrv.vap.xc.service.SingleTableService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 单表操作
 *
 * @author xw
 * @date 2018年4月12日
 */
@RestController
public class SingleTableController {

    @Autowired
    private SingleTableService singleTableService;

    @ResponseBody
    @GetMapping("/single/all/{table}")
    @ApiOperation("单表查询,最多返回1000条数据")
    public VList<Map<String, Object>> queryAll(@PathVariable("table") String table) {
        Optional<SingleTableModel> singleTableModel = SingleTableBuilder.getSingleTableModel(table);
        return singleTableModel.isPresent()
                ? singleTableService.queryAll(singleTableModel.get()).orElse(VoBuilder.errorVlist())
                : VoBuilder.rvl(RetMsgEnum.ERROR_ILLEGAL);
    }

    @ResponseBody
    @PostMapping("/single/{table}")
    @ApiOperation("单表查询")
    @ApiImplicitParams({@ApiImplicitParam(name = "table", value = "表名", paramType = "path"),
            @ApiImplicitParam(name = "requestMap", value = CommentConstants.SINGLE_TABLE)})
    public VList<Map<String, Object>> query(@PathVariable("table") String table,
                                            @RequestBody Map<String, Object> requestMap) {

        Optional<SingleTableModel> singleTableModel = SingleTableBuilder.getSingleTableModel(table);
        QueryModel queryModel = SingleTableBuilder.buildQueryModel(table, requestMap);
        return singleTableModel.isPresent()
                ? singleTableService.query(singleTableModel.get(), queryModel).orElse(VoBuilder.errorVlist())
                : VoBuilder.rvl(RetMsgEnum.ERROR_ILLEGAL);
    }

    @ResponseBody
    @DeleteMapping("/single/{table}/{pk}")
    @ApiOperation("单表根据主键删除")
    public Result delete(@PathVariable("table") String table, @PathVariable("pk") String pk) {
        return this.batchDelete(table, pk);
    }

    @ResponseBody
    @DeleteMapping("/single/batch/{table}")
    @ApiOperation("单表根据主键批量删除")
    public Result batchDelete(@PathVariable("table") String table, @RequestBody String ids) {
        Optional<SingleTableModel> singleTableModel = SingleTableBuilder.getSingleTableModel(table);
        if (!singleTableModel.isPresent() || StringUtils.isEmpty(ids)) {
            return VoBuilder.result(RetMsgEnum.ERROR_PARAM);
        }
        return VoBuilder.result(singleTableService.delete(singleTableModel.get(), ids.split(",")));
    }

    @ResponseBody
    @PutMapping("/single/{table}")
    @ApiOperation("单表新增数据,返回data为新增数据主键值")
    @ApiImplicitParams({@ApiImplicitParam(name = "table", value = "表名", paramType = "path"),
            @ApiImplicitParam(name = "kv", value = "新增数据键值对", paramType = "body")})
    public VData<String> add(@PathVariable("table") String table, @RequestBody Map<String, Object> kv) {
        Optional<SingleTableModel> singleTableModel = SingleTableBuilder.getSingleTableModel(table);
        if (!singleTableModel.isPresent()) {
            return VoBuilder.vd(null, RetMsgEnum.ERROR_PARAM);
        }
        Optional<String> tmp = singleTableService.add(singleTableModel.get(), kv);

        return tmp.isPresent() ? VoBuilder.vd(tmp.get()) : VoBuilder.errorVdata();
    }

    @ResponseBody
    @PatchMapping("/single/{table}")
    @ApiOperation("单表根据主键修改数据")
    @ApiImplicitParams({@ApiImplicitParam(name = "table", value = "表名", paramType = "path"),
            @ApiImplicitParam(name = "pk", value = "主键值", paramType = "path"),
            @ApiImplicitParam(name = "kv", value = "更新数据键值对", paramType = "body")})
    public Result update(@PathVariable("table") String table,
                         @RequestBody Map<String, Object> kv) {
        Map<String, Object> param = new HashMap<>();
        kv.entrySet().forEach(s ->{
                    param.put(s.getKey(),s.getValue());
        }
        );
        Optional<SingleTableModel> singleTableModel = SingleTableBuilder.getSingleTableModel(table);
        if (!singleTableModel.isPresent()) {
            return VoBuilder.result(RetMsgEnum.ERROR_PARAM);
        }
        kv.put("pkv", String.valueOf(param.get("id")));
        return VoBuilder.result(singleTableService.update(singleTableModel.get(), kv));
    }

}
