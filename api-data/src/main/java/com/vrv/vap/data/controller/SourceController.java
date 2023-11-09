package com.vrv.vap.data.controller;


import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.common.model.User;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.*;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.constant.SYSTEM;
import com.vrv.vap.data.model.Source;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.model.SourceMonitor;
import com.vrv.vap.data.service.SourceFieldService;
import com.vrv.vap.data.service.SourceMonitorService;
import com.vrv.vap.data.service.SourceService;
import com.vrv.vap.data.service.crontab.MonitorService;
import com.vrv.vap.data.vo.SourceFieldQuery;
import com.vrv.vap.data.vo.SourceQuery;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(path = SYSTEM.PREFIX_API + "/source")
@Api(value = "【基础数据】数据源 & 字段管理", tags = "【基础数据】数据源 & 字段管理")
public class SourceController extends ApiController {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Value("${elk.permission}")
    private String permission;

    @Autowired
    SourceService sourceService;

    @Autowired
    SourceFieldService fieldService;

    @Autowired
    SourceMonitorService monitorService;

    @Autowired
    MonitorService monitor;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("type", "{\"1\":\"ES\",\"2\":\"Mysql\",\"3\":\"远程ES\",\"4\":\"远程Mysql\"}");
        transferMap.put("dataType","{\"1\":\"原始日志\",\"2\":\"基线数据\"}");
        transferMap.put("show","{\"0\":\"否\",\"1\":\"是\",\"true\":\"是\",\"false\":\"否\"}");
        transferMap.put("sorter","{\"0\":\"否\",\"1\":\"是\",\"true\":\"是\",\"false\":\"否\"}");
        transferMap.put("filter","{\"0\":\"否\",\"1\":\"是\",\"true\":\"是\",\"false\":\"否\"}");
        transferMap.put("tag","{\"0\":\"否\",\"1\":\"是\",\"true\":\"是\",\"false\":\"否\"}");
        transferMap.put("analysisShow","{\"0\":\"否\",\"1\":\"是\"}");
    }


    @ApiOperation(value = "获取全部可用数据源")
    @GetMapping
    @SysRequestLog(description = "获取全部可用数据源", actionType = ActionType.SELECT)
    public VData<List<Source>> all(HttpServletRequest request) {
        switch (this.permission) {
            case SYSTEM.PERMISSION_FIELD:
            case SYSTEM.PERMISSION_SOURCE:
                User user = (User) request.getSession().getAttribute(Global.SESSION.USER);
                return this.vData(sourceService.findAllByRoleIds(user.getRoleIds()));
            default:
                return this.vData(sourceService.findAll());
        }
    }

    @ApiOperation(value = "根据ID获取数据源")
    @GetMapping(path = "/{sourceId}")
    public VData<Source> getSource(@PathVariable("sourceId") Integer sourceId) {
        return this.vData(sourceService.findById(sourceId));
    }

    @ApiOperation(value = "查询数据源")
    @PostMapping
    @SysRequestLog(description = "查询数据源", actionType = ActionType.SELECT)
    public VList querySource(@RequestBody SourceQuery query) {
        SyslogSenderUtils.sendSelectSyslogAndTransferredField(query,"查询数据源",transferMap);
        Example example = this.pageQuery(query,Source.class);
        return this.vList(sourceService.findByExample(example));
    }


    @ApiOperation(value = "新增数据源")
    @PutMapping
    @SysRequestLog(description = "新增数据源", actionType = ActionType.ADD)
    public VData<Source> add(@RequestBody Source source) {
        String topicName = source.getTopicName();
        if (StringUtils.isNotEmpty(topicName)) {
            source.setTopicAlias(topicName.replaceAll("-","_"));
        }
        int result = sourceService.save(source);
        if (result != 1) {
            return this.vData(false);
        } else {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(source,"新增数据源",transferMap);
        }
        try {
            logger.info("Data Source Add Success, Start Sync Fields");
            this.syncField(source.getId());
            monitor.monitor(source);
        } catch (ApiException e) {
            logger.error(e.getMessage(), e);
        }
        return this.vData(source);
    }

    @ApiOperation(value = "修改数据源")
    @PatchMapping
    @SysRequestLog(description = "修改数据源", actionType = ActionType.UPDATE)
    public Result update(@RequestBody Source source) {
        Source sourceSec = sourceService.findById(source.getId());
        String topicName = source.getTopicName();
        if (StringUtils.isNotEmpty(topicName)) {
            source.setTopicAlias(topicName.replaceAll("-","_"));
        }
        int result = sourceService.updateSelective(source);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(sourceSec,source,"修改数据源",transferMap);
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除数据源")
    @DeleteMapping
    @SysRequestLog(description = "删除数据源", actionType = ActionType.DELETE)
    public Result delete(@RequestBody DeleteQuery delete) {
        List<Source> sourceList = sourceService.findByids(delete.getIds());
        Example example = new Example(SourceField.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andIn("sourceId", Arrays.asList(delete.getIds().split(",")));
        List<SourceField> fields = fieldService.findByExample(example);
        if (fields.size() > 0) {
            // 删除数据源下的字段
            List<String> ids = new ArrayList<>();
            fields.forEach(field -> ids.add(String.valueOf(field.getId())));
            fieldService.deleteByIds(ids.stream().collect(Collectors.joining(",")));
        }
        int count = sourceService.deleteByIds(delete.getIds());
        if (count > 0) {
            sourceList.forEach(source -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(source,"删除数据源",transferMap);
            });
        }
        return this.result(count > 0);
    }


    @ApiOperation(value = "根据指定数据源下的全部字段")
    @GetMapping(value = "/field/{sourceId}")
    @SysRequestLog(description = "查询指定数据源下的全部字段", actionType = ActionType.SELECT)
    public VData<List<SourceField>> getFields(HttpServletRequest request, @PathVariable("sourceId") Integer sourceId) {
//        SourceFieldQuery query = new SourceFieldQuery();
//        query.setSourceId(sourceId);
//        Example example = this.query(query, SourceField.class);
        switch (this.permission) {
            case SYSTEM.PERMISSION_FIELD:
                // TODO 字段粒度级别控制（暂未实现）权限一期只做到索引级别， 实现时需要加缓存
//                LoginUserInfo user = (LoginUserInfo) request.getSession().getAttribute(Global.SESSION_USER);
            default:
                return this.vData(fieldService.findAllBySourceId(sourceId));
        }
    }

    @ApiOperation(value = "查询字段")
    @PostMapping(value = "/field/{sourceId}")
    @SysRequestLog(description = "查询数据源字段", actionType = ActionType.SELECT)
    public VList<SourceField> queryFields(@PathVariable("sourceId") Integer sourceId, @RequestBody SourceFieldQuery sourceFieldQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        sourceFieldQuery.setSourceId(sourceId);
        Example example = this.pageQuery(sourceFieldQuery, SourceField.class);
        return this.vList(fieldService.findByExample(example));
    }


    @ApiOperation(value = "新增字段")
    @PutMapping(value = "/field")
    @SysRequestLog(description = "新增数据源字段", actionType = ActionType.ADD)
    public VData<SourceField> addField(@RequestBody SourceField sourceField) {
        int result = fieldService.save(sourceField);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(sourceField,"新增数据源字段");
            return this.vData(sourceField);
        }
        return this.vData(false);
    }

    @ApiOperation(value = "修改字段")
    @PatchMapping(value = "/field")
    @SysRequestLog(description = "修改数据源字段", actionType = ActionType.UPDATE)
    public Result updateField(@RequestBody SourceField sourceField) {
        SourceField sourceFieldSec = fieldService.findById(sourceField.getId());
        int result = fieldService.updateSelective(sourceField);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(sourceFieldSec,sourceField,"修改数据源字段",transferMap);
        }
        return this.result(result == 1);
    }

    @ApiOperation(value = "删除字段")
    @DeleteMapping(value = "/field")
    @SysRequestLog(description = "删除数据源字段", actionType = ActionType.DELETE)
    public Result deleteField(@RequestBody DeleteQuery delete) {
        List<SourceField> fieldList = fieldService.findByids(delete.getIds());
        int count = fieldService.deleteByIds(delete.getIds());
        if (count > 0) {
            fieldList.forEach(sourceField -> {
                SyslogSenderUtils.sendDeleteSyslog(sourceField,"删除数据源字段");
            });
        }
        return this.result(count > 0);
    }


    @ApiOperation(value = "同步字段")
    @GetMapping(value = "/field/sync/{sourceId}")
    @SysRequestLog(description = "同步数据源字段", actionType = ActionType.UPDATE)
    public VData<Integer> syncField(@PathVariable Integer sourceId) throws ApiException {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        syslogSender.sendSysLog(ActionType.UPDATE, "同步数据源字段:【数据源ID:" + sourceId + "】", null, "1");
        Source source = sourceService.findById(sourceId);
        if (source == null) {
            throw new ApiException(ErrorCode.NOT_FOUND_INDEX.getResult().getCode(),ErrorCode.NOT_FOUND_INDEX.getResult().getMessage());
        }
        List<SourceField> fields = sourceService.fetchTypes(source);
        if (fields.size() == 0) {
            return this.vData(0);
        }
        List<SourceField> used = fieldService.findAllBySourceId(source.getId());
        Set<String> exists = new HashSet<>();
        used.forEach(field -> exists.add(field.getField()));
        List<SourceField> add = new ArrayList<>();
        for (SourceField sourceField : fields) {
            if (!exists.contains(sourceField.getField())) {
                add.add(sourceField);
                exists.add(sourceField.getField());
            }
        }
        if (add.size() == 0) {
            return this.vData(0);
        }
        return this.vData(fieldService.save(add));
    }


    @ApiOperation(value = "获取字段, 说明： 仅传 type 和 name")
    @PostMapping(value = "/field/fetch")
    @SysRequestLog(description = "获取字段", actionType = ActionType.SELECT)
    public VData<List<SourceField>> fetchField(@RequestBody Source source) throws ApiException {
        Source param = new Source();
        param.setId(-1);
        param.setType(source.getType());
        param.setName(source.getName());
        return this.vData(sourceService.fetchTypes(param));
    }


    @ApiOperation(value = "获取字段, 说明： 仅传 type 和 name")
    @GetMapping(value = "/monitor/{sourceId}")
    @SysRequestLog(description = "获取字段", actionType = ActionType.SELECT)
    public VData<SourceMonitor> getMonitor(@PathVariable @ApiParam(value = "数据源ID") Integer sourceId) {
        return this.vData(monitorService.findLastBySourceId(sourceId));

    }

}
