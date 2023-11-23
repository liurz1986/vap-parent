package com.vrv.vap.admin.web;

import com.github.pagehelper.Page;
import com.vrv.flume.cmd.FlumeTools;
import com.vrv.flume.cmd.model.AppState;
import com.vrv.flume.cmd.model.CollectorInfo;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.CommonUtil;
import com.vrv.vap.admin.model.CollectorDataAccess;
import com.vrv.vap.admin.model.CollectorRuleCollection;
import com.vrv.vap.admin.service.AuthorizationService;
import com.vrv.vap.admin.service.CollectorDataAccessService;
import com.vrv.vap.admin.service.CollectorRuleCollectionService;
import com.vrv.vap.admin.vo.AuthorizationVO;
import com.vrv.vap.admin.vo.CollectorDataAccessQuery;
import com.vrv.vap.admin.vo.CollectorDataAccessVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2022/1/5
 * @description
 */
@RequestMapping(path = "/collector/data/access")
@RestController
public class CollectorDataAccessController extends ApiController {

    private static final Logger logger = LoggerFactory.getLogger(CollectorDataAccessController.class);

    @Autowired
    private CollectorDataAccessService collectorDataAccessService;

    @Autowired
    private CollectorRuleCollectionService collectorRuleCollectionService;

    @Autowired
    private AuthorizationService authorizationService;

    String workingDir = CommonUtil.getBaseInfo("VAP_WORK_DIR");

    // 自定义
    private static final Integer BUILD_TYPE_ADD = 0;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("updateNew","{\"true\":\"是\",\"false\":\"否\"}");
        transferMap.put("type","{\"1\":\"UDP\",\"2\":\"TCP\",\"3\":\"HTTP\"}");
        transferMap.put("sourceType","{\"1\":\"常规\",\"2\":\"离线导入\",\"3\":\"监测器转发\"}");
    }

    @ApiOperation("获取数据接入列表")
    @GetMapping
    @SysRequestLog(description = "获取数据接入列表",actionType = ActionType.SELECT)
    public Result getDataAccessList() {
        List<CollectorDataAccess> accessList = collectorDataAccessService.findAll();
//        accessList = accessList.stream().filter(item -> item.getSourceType() != null && !item.getSourceType().equals(2)).collect(Collectors.toList());
        List<CollectorDataAccessVO> accessVOList = collectorDataAccessService.transformDataAccess(accessList);
        return this.vData(accessVOList);
    }

    @ApiOperation("根据ID获取数据接入")
    @GetMapping(path = "/{id}")
    public VData<CollectorDataAccess> getDataAccess(@PathVariable Integer id) {
        CollectorDataAccess collectorDataAccess = collectorDataAccessService.findById(id);
        return this.vData(collectorDataAccess);
    }

    @ApiOperation("查询数据接入列表")
    @PostMapping
    @SysRequestLog(description = "查询数据接入列表",actionType = ActionType.SELECT)
    public VList queryDataAccess(@RequestBody CollectorDataAccessQuery query) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("查询数据接入列表打印日志[%s]",
                    ReflectionToStringBuilder.toString(query, ToStringStyle.MULTI_LINE_STYLE)));
        }
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(query,CollectorDataAccess.class);
//        Example.Criteria criteria = example.createCriteria();
//        criteria.andNotEqualTo("sourceType",2);
//        example.and(criteria);
        List<CollectorDataAccess> dataAccessList = collectorDataAccessService.findByExample(example);
        long total = ((Page)dataAccessList).getTotal();
        List<CollectorDataAccessVO> accessVOList = collectorDataAccessService.transformDataAccess(dataAccessList);
        return this.vList(accessVOList,Integer.valueOf(total + ""));
    }

    @ApiOperation("获取离线数据接入列表")
    @GetMapping(path = "/offline/{templateType}")
    @SysRequestLog(description = "获取离线数据接入列表",actionType = ActionType.SELECT)
    public Result getOfflineAccessList(@PathVariable @ApiParam(value = "模板类型", required = true) Integer templateType) {
        if (templateType == null) {
            return this.vData(false);
        }
        List<CollectorDataAccess> accessList = collectorDataAccessService.findAll();
        List<CollectorDataAccess> offlineList = accessList.stream().filter(p -> templateType.equals(p.getTemplateType())).collect(Collectors.toList());
        return this.vData(offlineList);
    }

    @ApiOperation("添加数据接入")
    @PutMapping
    @SysRequestLog(description = "添加数据接入任务",actionType = ActionType.ADD)
    public VData addDataAccess(@RequestBody CollectorDataAccess collectorDataAccess) {
        Integer collectionId = collectorDataAccess.getCollectionId();
        if (collectionId == null) {
            return this.vData(false);
        }

        // 监测器转发接收配置重复性检查
        if (collectorDataAccess.getSourceType() == 3) {
            List<CollectorDataAccess> listTrans = collectorDataAccessService.findByProperty(CollectorDataAccess.class,"sourceType", 3);
            if (CollectionUtils.isNotEmpty(listTrans)) {
                return this.vData(ErrorCode.FLUME_MONITOR_TRANS_ERROR);
            }
        }

        // 端口重复性检查
        Example example = new Example(CollectorDataAccess.class);
        example.createCriteria().andEqualTo("port", collectorDataAccess.getPort());
        List<CollectorDataAccess> list = collectorDataAccessService.findByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            return this.vData(ErrorCode.FLUME_DUPLICATE_PORT);
        }

        CollectorRuleCollection ruleCollection = collectorRuleCollectionService.findById(collectionId);
        if (ruleCollection == null) {
            return this.vData(false);
        }
        String version = ruleCollection.getVersion();
        collectorDataAccess.setVersion(version);
        collectorDataAccess.setBuildType(BUILD_TYPE_ADD);
        int result = collectorDataAccessService.save(collectorDataAccess);
        if (result == 1) {
            collectorDataAccess.setCollectionName(ruleCollection.getName());
            SyslogSenderUtils.sendAddSyslogAndTransferredField(collectorDataAccess, "添加数据接入",transferMap);
        }
        // 组装规则内容
        collectorDataAccess.setUpdateNew(true);
        collectorDataAccess.setType(ruleCollection.getAccessType());
        collectorDataAccess.setEncoding(ruleCollection.getEncoding());
        String content = collectorDataAccessService.getRuleContent(collectorDataAccess);
        // 创建采集器配置文件
        FlumeTools flumeTools = collectorDataAccessService.getFlumeTool();
        CollectorInfo collectorInfo = flumeTools.createConfig(content);
        String cid = collectorInfo.getId();
        collectorDataAccess.setCid(cid);
        collectorDataAccessService.updateSelective(collectorDataAccess);
        List<CollectorDataAccess> dataAccessList = new ArrayList<>();
        dataAccessList.add(collectorDataAccess);
        List<CollectorDataAccessVO> accessVOList = collectorDataAccessService.transformDataAccess(dataAccessList);
        return this.vData(accessVOList.get(0));
    }

    @ApiOperation("修改数据接入")
    @PatchMapping
    @SysRequestLog(description = "修改数据接入任务",actionType = ActionType.UPDATE)
    public Result updateDataAccess(@RequestBody CollectorDataAccess collectorDataAccess) {
        CollectorDataAccess dataAccessSec = collectorDataAccessService.findById(collectorDataAccess.getId());
        this.transerCollectionName(dataAccessSec);
        Integer collectionId = collectorDataAccess.getCollectionId();
        CollectorRuleCollection ruleCollection = collectorRuleCollectionService.findById(collectionId);
        if (ruleCollection == null) {
            return this.vData(false);
        }

        // 监测器转发接收配置重复性检查
        if (collectorDataAccess.getSourceType() == 3) {
            List<CollectorDataAccess> listTrans = collectorDataAccessService.findByProperty(CollectorDataAccess.class,"sourceType", 3);
            if (CollectionUtils.isNotEmpty(listTrans)) {
                return this.vData(ErrorCode.FLUME_MONITOR_TRANS_ERROR);
            }
        }

        // 端口重复性检查
        Example example = new Example(CollectorDataAccess.class);
        example.createCriteria().andEqualTo("port", collectorDataAccess.getPort());
        List<CollectorDataAccess> list = collectorDataAccessService.findByExample(example);
        if (CollectionUtils.isNotEmpty(list)) {
            CollectorDataAccess dataAccess = list.get(0);
            if (!collectorDataAccess.getId().equals(dataAccess.getId())) {
                return this.vData(ErrorCode.FLUME_DUPLICATE_PORT);
            }
        }

        String version = ruleCollection.getVersion();
        Boolean updateNew = collectorDataAccess.getUpdateNew();
        if (updateNew) {
            collectorDataAccess.setVersion(version);
        }
        // 组装规则内容
        collectorDataAccess.setType(ruleCollection.getAccessType());
        collectorDataAccess.setEncoding(ruleCollection.getEncoding());
        String content = collectorDataAccessService.getRuleContent(collectorDataAccess);
        // 修改采集器配置文件
        String cid = collectorDataAccess.getCid();
        FlumeTools flumeTools = collectorDataAccessService.getFlumeTool();
        flumeTools.updateConfig(cid,content);
        int result = collectorDataAccessService.updateSelective(collectorDataAccess);
        if (result == 1) {
            collectorDataAccess.setCollectionName(ruleCollection.getName());
            SyslogSenderUtils.sendUpdateAndTransferredField(dataAccessSec, collectorDataAccess,"修改数据接入",transferMap);
        }
        // 更新规则集重启采集器
        if (collectorDataAccess.getUpdateNew()) {
            collectorDataAccessService.restartFlume();
        }

        List<CollectorDataAccess> dataAccessList = new ArrayList<>();
        dataAccessList.add(collectorDataAccess);
        List<CollectorDataAccessVO> accessVOList = collectorDataAccessService.transformDataAccess(dataAccessList);
        return this.vData(accessVOList.get(0));
    }

    private void transerCollectionName(CollectorDataAccess collectorDataAccess) {
        CollectorRuleCollection ruleCollectionSec = collectorRuleCollectionService.findById(collectorDataAccess.getCollectionId());
        collectorDataAccess.setCollectionName(ruleCollectionSec.getName());
    }

    @ApiOperation("删除采集规则")
    @DeleteMapping
    @SysRequestLog(description = "删除数据接入任务",actionType = ActionType.DELETE)
    public Result deleteDataAccess(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<CollectorDataAccess> accessList = collectorDataAccessService.findByids(ids);
        // 删除采集器配置文件
        FlumeTools flumeTools = collectorDataAccessService.getFlumeTool();
        String[]  idsArr = ids.split(",");
        for (String id : idsArr) {
            CollectorDataAccess collectorDataAccess = collectorDataAccessService.findById(Integer.valueOf(id));
            String cid = collectorDataAccess.getCid();
            flumeTools.deleteConfig(cid);
        }
        int result = collectorDataAccessService.deleteByIds(ids);
        if (result > 0) {
            accessList.forEach(collectorDataAccess -> {
                this.transerCollectionName(collectorDataAccess);
                SyslogSenderUtils.sendDeleteAndTransferredField(collectorDataAccess,"删除数据接入",transferMap);
            });
        }
        return this.result(result >= 1);
    }

    @ApiOperation("开启采集器")
    @GetMapping(path = "/start/{cid}")
    @SysRequestLog(description = "开启采集器",actionType = ActionType.AUTO)
    public Result startFlume(@PathVariable String cid) {
        FlumeTools flumeTools = collectorDataAccessService.getFlumeTool();
        // 授权点数校验
        Long runCount = collectorDataAccessService.getRunningCount();
        AuthorizationVO authorizationVO = authorizationService.getAuthorizationInfo();
        Integer terminalCount = authorizationVO.getTerminalCount();
        logger.info("运行任务数：" + runCount + ",授权点数：" +terminalCount);
        if (runCount >= terminalCount) {
            ErrorCode.FLUME_ACCESS_TOO_MANY_ERROR.getResult().setMessage("产品授权使用数量:" + terminalCount + "个,当前运行接入任务数已超过限制");
            return this.result(ErrorCode.FLUME_ACCESS_TOO_MANY_ERROR);
        }
        // 端口已使用校验
        List<CollectorDataAccess> dataAccessList = collectorDataAccessService.findByProperty(CollectorDataAccess.class,"cid",cid);
        Integer initMemory = 512;
        if (dataAccessList.size() > 0) {
            CollectorDataAccess dataAccess = dataAccessList.get(0);
            String port = dataAccess.getPort();
            initMemory = dataAccess.getInitMemory();
            Example example = new Example(CollectorDataAccess.class);
            Example.Criteria criteria = example.createCriteria();
            criteria.andEqualTo("port",port);
            example.and(criteria);
            List<CollectorDataAccess> accesses = collectorDataAccessService.findByExample(example);
            if (CollectionUtils.isNotEmpty(accesses)) {
                for (CollectorDataAccess access : accesses) {
                    AppState appState = flumeTools.status(access.getCid());
                    if (appState != null && appState.isRunning()) {
                        return this.result(ErrorCode.FLUME_ACCESS_PORT_IN_USE);
                    }
                }
            }
        }
        String jvmOption = "-Djava.security.auth.login.config=" + workingDir + "/flume/file/00/kafka_client_jaas.conf";
        logger.info(jvmOption);
        AppState appState = flumeTools.start(cid,initMemory,jvmOption,60000);
        collectorDataAccessService.changeStatus(cid,appState.isRunning());
        return this.result(appState.isRunning());
    }

    @ApiOperation("关闭采集器")
    @GetMapping(path = "/stop/{cid}")
    @SysRequestLog(description = "关闭采集器",actionType = ActionType.AUTO)
    public Result stopFlume(@PathVariable String cid) {
        FlumeTools flumeTools = collectorDataAccessService.getFlumeTool();
        AppState appState = flumeTools.stop(cid,30000);
        logger.info(cid + "已停止，开始发送通知");
        synchronized (this) {
            collectorDataAccessService.changeStatus(cid,appState.isRunning());
        }
        return this.result(!appState.isRunning());
    }

    @ApiOperation("获取监控数据")
    @GetMapping(path = "/metric/{cid}")
    @SysRequestLog(description = "获取采集器数据监控",actionType = ActionType.SELECT)
    public VData getMetricDetail(@PathVariable String cid) {
        FlumeTools flumeTools = collectorDataAccessService.getFlumeTool();
        String content = flumeTools.getMetricDetail(cid);
        return this.vData(content);
    }

    @ApiOperation("获取授权点数信息")
    @GetMapping(path = "/auth/count")
    public VData getAuthCount() {
        Map result = new HashedMap();
        Long runCount = collectorDataAccessService.getRunningCount();
        AuthorizationVO authorizationVO = authorizationService.getAuthorizationInfo();
        Integer terminalCount = authorizationVO.getTerminalCount();
        long leftCount = terminalCount - runCount;
        result.put("terminalCount",terminalCount);
        result.put("leftCount",leftCount);
        result.put("runCount",runCount);
        return this.vData(result);
    }

    @GetMapping(value = "/download/{cid}")
    @ApiOperation(value = "下载日志")
    @SysRequestLog(description = "下载任务运行日志",actionType = ActionType.DOWNLOAD)
    public Result downLoadLog(@PathVariable @ApiParam("采集器ID") String cid, HttpServletResponse response) {
        SyslogSenderUtils.sendDownLosdSyslog();
        return collectorDataAccessService.downloadLog(response,cid);
    }

    @ApiOperation("已接产品上报数据时间")
    @GetMapping("/report")
    public Result queryAccessReportInfo() {
        return this.vData(collectorDataAccessService.getAccessReport());
    }
}
