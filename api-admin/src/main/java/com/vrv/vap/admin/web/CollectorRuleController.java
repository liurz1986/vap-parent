package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.model.CollectorRule;
import com.vrv.vap.admin.model.CollectorRuleCollection;
import com.vrv.vap.admin.service.CollectorRuleCollectionService;
import com.vrv.vap.admin.service.CollectorRuleService;
import com.vrv.vap.admin.vo.CollectorRuleQuery;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2022/1/5
 * @description 规则过滤器
 */
@RequestMapping(path = "/collector/rule")
@RestController
public class CollectorRuleController extends ApiController {

    @Autowired
    private CollectorRuleService collectorRuleService;

    @Autowired
    private CollectorRuleCollectionService ruleCollectionService;

    // 预定义
    private static final int TYPE_PRE = 1;

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("charaterType", "{\"1\":\"字符串\",\"2\":\"正则\"}");
        transferMap.put("handler", "{\"TEST\":\"自动提取\",\"JSON\":\"JSON\",\"SPLIT\":\"字符串分隔\",\"REGEX\":\"正则表达式\"}");
    }

    @ApiOperation("获取规则列表")
    @GetMapping()
    @SysRequestLog(description = "获取规则列表",actionType = ActionType.SELECT)
    public Result getRuleList() {
        return this.vData(collectorRuleService.findAll());
    }

    @ApiOperation("查询规则")
    @PostMapping
    @SysRequestLog(description = "查询采集规则",actionType = ActionType.SELECT)
    public VList queryRuleList(@RequestBody CollectorRuleQuery query) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(query,CollectorRule.class);
        return this.vList(collectorRuleService.findByExample(example));
    }

    @ApiOperation("添加采集规则")
    @PutMapping
    @SysRequestLog(description = "添加采集规则",actionType = ActionType.ADD)
    public Result addRule(@RequestBody CollectorRule collectorRule) {
        Integer collectionId = collectorRule.getCollectionId();
        // 预定义规则集不可修改
        CollectorRuleCollection ruleCollection = ruleCollectionService.findById(collectionId);
        Integer type = ruleCollection.getType();
        if (TYPE_PRE == type) {
            return this.result(ErrorCode.FLUME_COLLECTION_UPDATE_ERROR);
        }
        // 生成过滤规则
        Map<String,Object> configData = collectorRuleService.getFlumeRule(collectorRule,"2");
        String ruleJson = collectorRuleService.buildConfigData(configData,collectorRule.getRenames());
        collectorRule.setRuleJson(ruleJson);
        // 保存
        int result = collectorRuleService.save(collectorRule);
        if (result == 1) {
            collectorRule.setCollectionName(ruleCollection.getName());
            SyslogSenderUtils.sendAddSyslog(collectorRule, "添加采集规则");
        }
        // 同步规则集下的二次解析js
        collectorRuleService.syncJsContent(collectionId,collectorRule.getJsContent());
        // 修改规则集版本号
        ruleCollectionService.updateVersion(collectionId);
        return this.vData(result > 0);
    }

    @ApiOperation("修改采集规则")
    @PatchMapping
    @SysRequestLog(description = "修改采集规则",actionType = ActionType.UPDATE)
    public Result updateRule(@RequestBody CollectorRule collectorRule) {
        CollectorRule ruleSec = collectorRuleService.findById(collectorRule.getId());
        Integer collectionId = collectorRule.getCollectionId();
        // 预定义规则集不可修改
        CollectorRuleCollection ruleCollection = ruleCollectionService.findById(collectionId);
        Integer type = ruleCollection.getType();
        if (TYPE_PRE == type) {
            return this.result(ErrorCode.FLUME_COLLECTION_UPDATE_ERROR);
        }
        if (collectorRule.getId() == null) {
            return this.result(false);
        }
        // 生成过滤规则
        Map<String,Object> configData = collectorRuleService.getFlumeRule(collectorRule,"2");
        String ruleJson = collectorRuleService.buildConfigData(configData,collectorRule.getRenames());
        collectorRule.setRuleJson(ruleJson);
        // 修改
        int result = collectorRuleService.updateSelective(collectorRule);
        if (result == 1) {
            ruleSec.setCollectionName(ruleCollection.getName());
            collectorRule.setCollectionName(ruleCollection.getName());
            SyslogSenderUtils.sendUpdateAndTransferredField(ruleSec, collectorRule,"修改采集规则",transferMap);
        }
        // 同步规则集下的二次解析js
        collectorRuleService.syncJsContent(collectionId,collectorRule.getJsContent());
        // 修改规则集版本号
        ruleCollectionService.updateVersion(collectionId);
        return this.result(result > 0);
    }

    @ApiOperation("删除采集规则")
    @DeleteMapping
    @SysRequestLog(description = "删除采集规则",actionType = ActionType.DELETE)
    public Result deleteRule(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<CollectorRule> ruleList = collectorRuleService.findByids(ids);
        List<Integer> collectionIds = new ArrayList<>();
        String[] idsArr = ids.split(",");
        for (String id : idsArr) {
            CollectorRule collectorRule = collectorRuleService.findById(Integer.valueOf(id));
            Integer collectionId = collectorRule.getCollectionId();
            // 预定义规则集不可修改
            CollectorRuleCollection ruleCollection = ruleCollectionService.findById(collectionId);
            Integer type = ruleCollection.getType();
            if (TYPE_PRE == type) {
                return this.result(ErrorCode.FLUME_COLLECTION_UPDATE_ERROR);
            }
            collectionIds.add(collectionId);
        }
        int result = collectorRuleService.deleteByIds(ids);
        if (result > 0) {
            ruleList.forEach(collectorRule -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(collectorRule,"删除采集规则",transferMap);
            });
        }
        // 修改规则集版本号
        if (CollectionUtils.isNotEmpty(collectionIds)) {
            for (Integer cId : collectionIds) {
                ruleCollectionService.updateVersion(cId);
            }
        }
        return this.result(result >= 1);
    }

    @ApiOperation("生成提取规则")
    @PostMapping(path = "/generateCollect")
    public VData generateCollectRule(@RequestBody CollectorRule collectorRule) {
        if (StringUtils.isEmpty(collectorRule.getSource())) {
            return this.vData(ErrorCode.FLUME_RULE_SOURCE_ERROR);
        }
        Map<String,Object> result = collectorRuleService.getFlumeRule(collectorRule,"1");
        if (result == null) {
            return this.vData(ErrorCode.FLUME_DATA_ANALYSIS_ERROR);
        }
        return this.vData(result);
    }
}
