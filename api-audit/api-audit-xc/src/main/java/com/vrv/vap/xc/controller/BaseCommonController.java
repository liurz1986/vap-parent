package com.vrv.vap.xc.controller;

import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import com.vrv.vap.xc.pojo.ConfLookup;
import com.vrv.vap.xc.pojo.ObjectAnalyseConfig;
import com.vrv.vap.xc.service.BaseCommonService;
import com.vrv.vap.xc.vo.ConfLookupQuery;
import com.vrv.vap.xc.vo.ObjectAnalyseConfigQuery;
import io.swagger.annotations.ApiOperation;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

/**
 * 公共操作
 * Created by lilei on 2021/08/05.
 */
@RestController
public class BaseCommonController {
    private Log log = LogFactory.getLog(BaseCommonController.class);
    @Autowired
    private BaseCommonService baseCommonService;

    @Ignore
    @InitBinder
    private void populateCustomerRequest(WebDataBinder binder) {
        binder.setDisallowedFields(new String[]{});
    }

    @ResponseBody
    @PostMapping("/lookup")
    @ApiOperation("获取lookup配置信息")
    public VList<ConfLookup> getlookup(@RequestBody ConfLookupQuery record) {
        return baseCommonService.getConfLookup(record);
    }

    @ResponseBody
    @PatchMapping("/lookup")
    @ApiOperation("修改lookup配置信息")
    public Result updatelookup(@RequestBody ConfLookup record) {
        baseCommonService.updateConfLookup(record);
        return VoBuilder.success();
    }

    @PostMapping("/object/analyse/config")
    @ApiOperation("查询对象访问分析配置")
    public VData<List<ObjectAnalyseConfig>> queryObjectAnalyseConfig(@RequestBody ObjectAnalyseConfigQuery param) {
        return baseCommonService.queryObjectAnalyseConfig(param);
    }

    @PatchMapping("/object/analyse/config")
    @ApiOperation("修改对象访问分析配置")
    public Result updateObjectAnalyseConfig(@RequestBody ObjectAnalyseConfig param) {
        return baseCommonService.updateObjectAnalyseConfig(param);
    }


}
