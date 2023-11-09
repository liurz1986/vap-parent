package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.model.CascadePlatform;
import com.vrv.vap.admin.model.CascadeStrategy;
import com.vrv.vap.admin.model.CascadeStrategyReceive;
import com.vrv.vap.admin.service.CascadePlatformService;
import com.vrv.vap.admin.service.CascadeStrategyReceiveService;
import com.vrv.vap.admin.service.CascadeStrategyService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.CascadeStrategyQuery;
import com.vrv.vap.admin.vo.DeleteStrategyQuery;
import com.vrv.vap.admin.vo.ReportStrategyVO;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author lilang
 * @date 2021/3/25
 * @description 策略控制器
 */
@RequestMapping(path = "/cascade/policy")
@RestController
public class CascadeStrategyController extends ApiController {

    @Resource
    CascadeStrategyService cascadeStrategyService;

    @Resource
    CascadeStrategyReceiveService cascadeStrategyReceiveService;

    @Resource
    CascadePlatformService cascadePlatformService;

    private static final Logger log = LoggerFactory.getLogger(CascadeStrategyController.class);

    @ApiOperation("获取所有策略列表")
    @GetMapping
    public Result getStrategyList() {
        return this.vData(cascadeStrategyService.findAll());
    }

    @ApiOperation("查询策略")
    @PostMapping
    public VList queryStrategy(@RequestBody CascadeStrategyQuery strategyQuery) {
        Example example = this.pageQuery(strategyQuery, CascadeStrategy.class);
        return this.vList(cascadeStrategyService.findByExample(example));
    }

    @ApiOperation("添加策略")
    @PutMapping
    public VData addStrategy(@RequestBody CascadeStrategy cascadeStrategy) {
        cascadeStrategy.setUpdateTime(new Date());
        cascadeStrategy.setUid(Uuid.uuid());
        cascadeStrategyService.save(cascadeStrategy);
        return this.vData(cascadeStrategy);
    }

    @ApiOperation("修改策略")
    @PatchMapping
    public Result updateStrategy(@RequestBody CascadeStrategy cascadeStrategy) {
        if (cascadeStrategy == null || cascadeStrategy.getId() == null) {
            return this.result(false);
        }
        Boolean result = cascadeStrategyService.updateStrategy(cascadeStrategy);
        return this.result(result);
    }

    @ApiOperation("删除策略")
    @DeleteMapping
    public Result deleteStrategy(@RequestBody DeleteQuery deleteQuery) {
        String ids = deleteQuery.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        Boolean result = cascadeStrategyService.deleteStrategy(ids);
        return this.result(result);
    }

    @ApiOperation("接收删除策略")
    @PostMapping(path = "/delete")
    public Map deleteStrategy(@RequestBody DeleteStrategyQuery deleteStrategyQuery) {
        Map<String,Object> map = new HashMap<>();
        String sid = deleteStrategyQuery.getSid();
        String id = deleteStrategyQuery.getId();
        if (StringUtils.isEmpty(sid)) {
            map.put("status",415);
            map.put("message","请求格式错误");
            return map;
        }
        List<CascadePlatform> platformList = cascadePlatformService.findByProperty(CascadePlatform.class,"local",1);
        if (CollectionUtils.isNotEmpty(platformList)) {
            CascadePlatform cascadePlatform = platformList.get(0);
            if (!id.equals(cascadePlatform.getPlatformId())) {
                map.put("status",401);
                map.put("message","认证失败");
                return map;
            }
        }
        List<CascadeStrategyReceive> receiveList = cascadeStrategyReceiveService.findByProperty(CascadeStrategyReceive.class,"puid",sid);
        if (CollectionUtils.isNotEmpty(receiveList)) {
            for (CascadeStrategyReceive strategyReceive : receiveList) {
                cascadeStrategyReceiveService.deleteById(strategyReceive.getId());
            }
        }
        map.put("status",200);
        map.put("message","执行成功");
        return map;
    }

    @ApiOperation("接收下发策略")
    @PostMapping(path = "/report")
    public Map reportStrategy(@RequestBody ReportStrategyVO strategyVO) {
        Map<String,Object> map = new HashMap<>();
        String id = strategyVO.getId();
        String sid = strategyVO.getSid();
        List<Map<String,Object>> strategy = strategyVO.getStrategy();
        Map kafka = strategyVO.getKafka();
        if (StringUtils.isEmpty(id) || StringUtils.isEmpty(sid) || CollectionUtils.isEmpty(strategy) || kafka == null) {
            log.info(LogForgingUtil.validLog("id:"+id+",sid:"+sid+",strategy:"+strategy+",kafka"+kafka));
            map.put("status",415);
            map.put("message","请求格式错误");
            return map;
        }
        List<CascadePlatform> platformList = cascadePlatformService.findByProperty(CascadePlatform.class,"local",1);
        if (CollectionUtils.isNotEmpty(platformList)) {
            CascadePlatform cascadePlatform = platformList.get(0);
            if (!id.equals(cascadePlatform.getPlatformId())) {
                map.put("status",401);
                map.put("message","认证失败");
                return map;
            }
        }
        Boolean result = cascadeStrategyReceiveService.saveStrategy(strategyVO);
        if (result) {
            map.put("status",200);
            map.put("message","执行成功");
        } else {
            map.put("status",999);
            map.put("message","保存下发策略错误");
        }
        return map;
    }
}
