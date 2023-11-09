package com.vrv.vap.admin.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.admin.model.VisualScreenTemplate;
import com.vrv.vap.admin.service.ScreenTemplateService;
import com.vrv.vap.admin.vo.ScreenTemplateQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * @author lilang
 * @date 2020/1/13
 * @description 大屏模板控制器
 */
@RestController
@RequestMapping(path = "/screenTemplate")
public class ScreenTemplateController extends ApiController{


    @Autowired
    public ScreenTemplateService screenTemplateService;


    @GetMapping
    @ApiOperation("获取所有模板")
    public Result getAllTemplate() {
        return this.vData(screenTemplateService.findAll());
    }

    @PostMapping
    @ApiOperation("查询大屏模板")
    @SysRequestLog(description="查询大屏模板", actionType = ActionType.SELECT)
    public VList queryScreenTemplate(@RequestBody ScreenTemplateQuery templateQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(templateQuery,VisualScreenTemplate.class);
        return this.vList(screenTemplateService.findByExample(example));
    }

    @PutMapping
    @ApiOperation("添加大屏模板")
    @SysRequestLog(description="添加大屏模板", actionType = ActionType.ADD)
    public VData addScreenTemplate(@RequestBody VisualScreenTemplate visualScreenTemplate) {
        int result = screenTemplateService.save(visualScreenTemplate);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(visualScreenTemplate,"添加大屏模板");
        }
        return this.vData(visualScreenTemplate);
    }

    @PatchMapping
    @ApiOperation("修改大屏模板")
    @SysRequestLog(description="修改大屏模板", actionType = ActionType.UPDATE)
    public Result updateScreenTemplate(@RequestBody VisualScreenTemplate visualScreenTemplate) {
        VisualScreenTemplate templateSec = screenTemplateService.findById(visualScreenTemplate.getId());
        Integer id = visualScreenTemplate.getId();
        if (id == null) {
            return this.result(false);
        }
        int result = screenTemplateService.updateSelective(visualScreenTemplate);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(templateSec,visualScreenTemplate,"修改大屏模板");
        }
        return this.result(result == 1);
    }

    @DeleteMapping
    @ApiOperation("删除大屏模板")
    @SysRequestLog(description="删除大屏模板", actionType = ActionType.DELETE)
    public Result deleteScreenTemplate(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<VisualScreenTemplate> templateList = screenTemplateService.findByids(ids);
        int result = screenTemplateService.deleteByIds(ids);
        if (result > 0) {
            templateList.forEach(visualScreenTemplate -> {
                SyslogSenderUtils.sendDeleteSyslog(visualScreenTemplate,"删除大屏模板");
            });
        }
        return this.result(result >= 1);
    }
}
