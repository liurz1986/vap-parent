package com.vrv.vap.netflow.web;

import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.netflow.model.CollectorOfflineTemplate;
import com.vrv.vap.netflow.service.CollectorOfflineTemplateService;
import com.vrv.vap.netflow.vo.CollectorOfflineTemplateQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;

/**
 * @author lilang
 * @date 2022/3/28
 * @description 离线模板管理
 */
@RequestMapping(path = "/collector/offline/template")
@RestController
public class CollectorOfflineTemplateController extends ApiController {

    private static final Logger log = LoggerFactory.getLogger(CollectorOfflineTemplateController.class);

    @Autowired
    CollectorOfflineTemplateService collectorOfflineTemplateService;

    // 自定义类型
    private static final Integer OPER_TYPE_ADD = 0;

    @ApiOperation("获取离线模板列表")
    @GetMapping
    public Result getTemplateList() {
        return this.vData(collectorOfflineTemplateService.findAll());
    }

    @ApiOperation("根据模板类型获取模板列表")
    @GetMapping(path = "/{type}")
    public Result getTemplates(@PathVariable Integer type) {
        List<CollectorOfflineTemplate> templateList = collectorOfflineTemplateService.findByProperty(CollectorOfflineTemplate.class,"type",type);
        return this.vData(templateList);
    }

    @ApiOperation("查询离线模板")
    @PostMapping
    public VList queryTemplate(@RequestBody CollectorOfflineTemplateQuery query) {
        Example example = this.pageQuery(query,CollectorOfflineTemplate.class);
        return this.vList(collectorOfflineTemplateService.findByExample(example));
    }

    @ApiOperation("添加离线模板")
    @PutMapping
    public VData addTemplate(@RequestBody CollectorOfflineTemplate collectorOfflineTemplate) {
        collectorOfflineTemplate.setCreateTime(new Date());
        collectorOfflineTemplate.setLastUpdateTime(new Date());
        collectorOfflineTemplate.setOperType(OPER_TYPE_ADD);
        int result = collectorOfflineTemplateService.save(collectorOfflineTemplate);
        return this.vData(result > 0);
    }

    @ApiOperation("修改离线模板")
    @PatchMapping
    public Result updateTemplate(@RequestBody CollectorOfflineTemplate collectorOfflineTemplate) {
        collectorOfflineTemplate.setLastUpdateTime(new Date());
        int result = collectorOfflineTemplateService.updateSelective(collectorOfflineTemplate);
        return this.vData(result > 0);
    }

    @ApiOperation("删除离线模板")
    @DeleteMapping
    public Result deleteTemplate(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        int result = collectorOfflineTemplateService.deleteByIds(ids);
        return this.result(result >= 1);
    }

    @ApiOperation("下载离线模板")
    @GetMapping("/download")
    @SysRequestLog(description="下载离线模板", actionType = ActionType.DOWNLOAD)
    public Result downloadTemplate(@RequestParam(value = "path",required = false) String path,@RequestParam("name") String name,HttpServletResponse response) {
        InputStream st = null;
        try {
            if (StringUtils.isEmpty(path) || "null".equals(path)) {
                st = this.getClass().getResourceAsStream("/template/" + name);
            } else {
                st = new FileInputStream(path);
            }
            if (st == null) {
                return new Result("-1", "下载文件流缺失");
            }
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-disposition", "attachment; filename=" + URLEncoder.encode(name, "UTF-8"));
            IOUtils.copy(st, response.getOutputStream());
            return null;
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                if (st != null) {
                    st.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return this.result(true);
    }
}
