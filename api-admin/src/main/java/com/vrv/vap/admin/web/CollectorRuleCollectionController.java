package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.Tools;
import com.vrv.vap.admin.model.CollectorDataAccess;
import com.vrv.vap.admin.model.CollectorRule;
import com.vrv.vap.admin.model.CollectorRuleCollection;
import com.vrv.vap.admin.service.CollectorDataAccessService;
import com.vrv.vap.admin.service.CollectorRuleCollectionService;
import com.vrv.vap.admin.service.CollectorRuleService;
import com.vrv.vap.admin.vo.CollectorRuleCollectionQuery;
import com.vrv.vap.admin.vo.CollectorRuleCollectionVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.utils.ApplicationContextUtil;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import com.vrv.vap.syslog.service.SyslogSender;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author lilang
 * @date 2022/1/4
 * @description 采集器规则集控制器
 */
@RequestMapping(path = "/collector/rule/collection")
@RestController
public class CollectorRuleCollectionController extends ApiController {

    private static final Logger logger = LoggerFactory.getLogger(CollectorRuleCollectionController.class);

    @Autowired
    private CollectorRuleCollectionService collectorRuleCollectionService;

    @Autowired
    private CollectorRuleService collectorRuleService;

    @Autowired
    private CollectorDataAccessService collectorDataAccessService;
    // 自定义
    private static final int TYPE_ADD = 0;
    // 预定义
    private static final int TYPE_PRE = 1;

    @ApiOperation("获取采集器规则集列表")
    @GetMapping
    @SysRequestLog(description = "获取采集器规则集列表",actionType = ActionType.SELECT)
    public Result getCollectionList() {
        List<CollectorRuleCollection> ruleCollectionList = collectorRuleCollectionService.findAll();
        List<CollectorRuleCollectionVO> collectionVOList = collectorRuleCollectionService.transformRuleCollection(ruleCollectionList);
        return this.vData(collectionVOList);
    }

    @ApiOperation("查询采集器规则集")
    @PostMapping
    @SysRequestLog(description = "查询采集器规则集",actionType = ActionType.SELECT)
    public VList queryRuleCollection(@RequestBody CollectorRuleCollectionQuery query) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(query,CollectorRuleCollection.class);
        VList vList = this.vList(collectorRuleCollectionService.findByExample(example));
        List<CollectorRuleCollection> ruleCollectionList = vList.getList();
        List<CollectorRuleCollectionVO> collectionVOList = collectorRuleCollectionService.transformRuleCollection(ruleCollectionList);
        return this.vList(collectionVOList,vList.getTotal());
    }

    @ApiOperation("添加采集器规则集")
    @PutMapping
    @SysRequestLog(description = "添加采集器规则集",actionType = ActionType.ADD)
    public VData addRuleCollection(@RequestBody CollectorRuleCollection collectorRuleCollection) {
        int result = collectorRuleCollectionService.save(collectorRuleCollection);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(collectorRuleCollection, "添加采集器规则集");
        }
        return this.vData(collectorRuleCollection);
    }

    @ApiOperation("修改采集器规则集")
    @PatchMapping
    @SysRequestLog(description = "修改采集器规则集",actionType = ActionType.UPDATE)
    public Result updateRuleCollection(@RequestBody CollectorRuleCollection collectorRuleCollection) {
        // 预定义不可修改
        Integer id = collectorRuleCollection.getId();
        if (id == null) {
            return  this.result(false);
        }
        CollectorRuleCollection ruleCollection = collectorRuleCollectionService.findById(id);
        Integer type = ruleCollection.getType();
        if (TYPE_PRE == type) {
            return this.result(ErrorCode.FLUME_COLLECTION_UPDATE_ERROR);
        }
        int result = collectorRuleCollectionService.updateSelective(collectorRuleCollection);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateSyslog(ruleCollection, collectorRuleCollection,"修改采集器规则集");
        }
        return this.result(result > 0);
    }

    @ApiOperation("删除采集器规则集")
    @DeleteMapping
    @SysRequestLog(description = "删除采集器规则集",actionType = ActionType.DELETE)
    public Result deleteRuleCollection(@RequestBody DeleteQuery param) {
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<CollectorRuleCollection> collectionList = collectorRuleCollectionService.findByids(ids);
        String[] idsArr = ids.split(",");
        for (String id : idsArr) {
            // 规则集使用中，不可删除
            List<CollectorDataAccess> accessList = collectorDataAccessService.findByProperty(CollectorDataAccess.class,"collectionId",id);
            if (CollectionUtils.isNotEmpty(accessList)) {
                return this.result(ErrorCode.FLUME_COLLECTION_IN_USE);
            }
            CollectorRuleCollection ruleCollection = collectorRuleCollectionService.findById(Integer.valueOf(id));
            // 预定义不可删除
            Integer type = ruleCollection.getType();
            if (TYPE_PRE == type) {
                return this.result(ErrorCode.FLUME_COLLECTION_DELETE_ERROR);
            }
            List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",ruleCollection.getId());
            if (CollectionUtils.isNotEmpty(ruleList)) {
                List<String> idList = ruleList.stream().map(p -> String.valueOf(p.getId())).collect(Collectors.toList());
                Optional<String> ruleIds = idList.stream().reduce((a, b) -> a + "," + b);
                if (ruleIds.isPresent()) {
                    collectorRuleService.deleteByIds(ruleIds.get());
                }
            }
        }
        int result = collectorRuleCollectionService.deleteByIds(ids);
        if (result > 0) {
            collectionList.forEach(collectorRuleCollection -> {
                SyslogSenderUtils.sendDeleteSyslog(collectorRuleCollection,"删除采集器规则集");
            });
        }
        return this.result(result >= 1);
    }

    @ApiOperation("复制采集器规则集")
    @GetMapping(path = "/copy{id}")
    @SysRequestLog(description = "复制规则集",actionType = ActionType.ADD)
    public Result copyRuleCollection(@PathVariable Integer id) {
        CollectorRuleCollection collectorRuleCollection = collectorRuleCollectionService.findById(id);
        collectorRuleCollection.setId(null);
        collectorRuleCollection.setType(TYPE_ADD);
        collectorRuleCollection.setName(collectorRuleCollection.getName() + "-副本");
        int result = collectorRuleCollectionService.save(collectorRuleCollection);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslog(collectorRuleCollection,"复制采集器规则集");
        }
        List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",id);
        if (CollectionUtils.isNotEmpty(ruleList)) {
            ruleList.stream().forEach(item -> {
                item.setId(null);
                item.setCollectionId(collectorRuleCollection.getId());
            });
            collectorRuleService.save(ruleList);
            String ruleListJson = JSON.toJSONString(ruleList);
            String md5Value = Tools.string2MD5(ruleListJson);
            collectorRuleCollection.setVersion(md5Value);
            collectorRuleCollection.setType(TYPE_ADD);
            collectorRuleCollectionService.updateSelective(collectorRuleCollection);
        }
        return this.result(result > 0);
    }

    @ApiOperation("导出规则集")
    @GetMapping(path = "/export/{id}")
    @SysRequestLog(description = "导出规则集",actionType = ActionType.EXPORT,manually = false)
    public void exportRuleCollection(HttpServletResponse response,@PathVariable @ApiParam(value = "规则集ID") Integer id) {
        CollectorRuleCollection collectorRuleCollection = collectorRuleCollectionService.findById(id);
        CollectorRuleCollectionVO collectionVO = new CollectorRuleCollectionVO();
        BeanUtils.copyProperties(collectorRuleCollection,collectionVO);
        List<CollectorRule> ruleList = collectorRuleService.findByProperty(CollectorRule.class,"collectionId",id);
        if (CollectionUtils.isNotEmpty(ruleList)) {
            collectionVO.setRuleList(ruleList);
        }
        String content = JSON.toJSONString(collectionVO);
        collectorRuleCollectionService.export(response,content,id);
    }

    @ApiOperation("导入规则集")
    @PostMapping(path = "/import")
    @SysRequestLog(description = "导入规则集",actionType = ActionType.IMPORT)
    public Result importRuleCollection(@ApiParam(value = "导入的文件", required = true) MultipartFile file) {
        SyslogSender syslogSender = ApplicationContextUtil.getBean(SyslogSender.class);
        syslogSender.sendSysLog(ActionType.IMPORT, null, null, "1");
        int result = collectorRuleCollectionService.importRuleCollection(file);
        return this.result(result > 0);
    }

}
