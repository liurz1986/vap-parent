package com.vrv.vap.admin.web;

import com.vrv.vap.admin.model.CollectorDataAccess;
import com.vrv.vap.admin.model.CollectorIndex;
import com.vrv.vap.admin.model.CollectorRule;
import com.vrv.vap.admin.model.CollectorRuleCollection;
import com.vrv.vap.admin.service.CollectorDataAccessService;
import com.vrv.vap.admin.service.CollectorIndexService;
import com.vrv.vap.admin.service.CollectorRuleCollectionService;
import com.vrv.vap.admin.service.CollectorRuleService;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author lilang
 * @date 2022/1/18
 * @description
 */
@RequestMapping(path = "/collector/index")
@RestController
public class CollectorIndexController extends ApiController {

    @Resource
    CollectorIndexService collectorIndexService;

    @Resource
    CollectorDataAccessService collectorDataAccessService;

    @Resource
    CollectorRuleCollectionService collectorRuleCollectionService;

    @Resource
    CollectorRuleService collectorRuleService;

    @GetMapping
    @ApiOperation("获取采集器关联日志")
    public Result getIndexList() {
        return this.vData(collectorIndexService.findAll());
    }

    @GetMapping(path = "/{accessId}")
    @ApiOperation("根据离线流程ID获取关联日志")
    public VData getOfflineIndex(@PathVariable Integer accessId) {
        CollectorDataAccess dataAccess = collectorDataAccessService.findById(accessId);
        if (dataAccess != null) {
            Integer collectionId = dataAccess.getCollectionId();
            CollectorRuleCollection ruleCollection = collectorRuleCollectionService.findById(collectionId);
            if (ruleCollection != null) {
                List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",ruleCollection.getId());
                if (CollectionUtils.isNotEmpty(ruleList)) {
                    CollectorRule rule = ruleList.get(0);
                    String relateIndex = rule.getRelateIndex();
                    if (StringUtils.isNotEmpty(relateIndex)) {
                        List<CollectorIndex> indexList = collectorIndexService.findByProperty(CollectorIndex.class,"type",relateIndex);
                        if (CollectionUtils.isNotEmpty(indexList)) {
                            return this.vData(indexList.get(0));
                        }
                    }
                }
            }
        }
        return this.vData(false);
    }
}
