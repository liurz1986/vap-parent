package com.vrv.vap.monitor.controller.canvas;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.vrv.vap.monitor.command.MonitorRunnerV2;
import com.vrv.vap.monitor.entity.Monitor2AssetIndicatorView;
import com.vrv.vap.monitor.entity.Monitor2AssetIndicatorViewHistory;
import com.vrv.vap.monitor.entity.Monitor2AssetInfo;
import com.vrv.vap.monitor.entity.Monitor2AssetOidAlg;
import com.vrv.vap.monitor.entity.Monitor2Indicator;
import com.vrv.vap.monitor.entity.Monitor2IndicatorView;
import com.vrv.vap.monitor.model.AssetType;
import com.vrv.vap.monitor.model.OidAlgEx;
import com.vrv.vap.monitor.model.PageModel;
import com.vrv.vap.monitor.model.es.MonitorData;
import com.vrv.vap.monitor.model.es.QueryModel;
import com.vrv.vap.monitor.service.AssetMonitorInfoV2Service;
import com.vrv.vap.monitor.service.AssetMonitorOidV2Service;
import com.vrv.vap.monitor.service.MonitorV2AssetIndicatorViewHistoryService;
import com.vrv.vap.monitor.service.MonitorV2IndicatorService;
import com.vrv.vap.monitor.tools.QueryTools;
import com.vrv.vap.monitor.vo.AssetMonitorInfoQuery;
import com.vrv.vap.monitor.vo.DeleteModel;
import com.vrv.vap.monitor.vo.Monitor2AssetIndicatorViewHistoryQuery;
import com.vrv.vap.monitor.vo.Monitor2AssetIndicatorViewQuery;
import com.vrv.vap.monitor.vo.Monitor2AssetInfoQuery;
import com.vrv.vap.monitor.vo.Monitor2AssetOidAlgQuery;
import com.vrv.vap.monitor.vo.Monitor2DataQuery;
import com.vrv.vap.monitor.vo.Monitor2IndicatorQuery;
import com.vrv.vap.monitor.vo.Monitor2IndicatorViewQuery;
import com.vrv.vap.monitor.vo.MonitorAssetGeneral;
import com.vrv.vap.monitor.vo.OidAlgTest;
import com.vrv.vap.toolkit.constant.RetMsgEnum;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.tools.ValidateTools;
import com.vrv.vap.toolkit.vo.Result;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RestController
public class AssetMonitorInfoV2Controller {

    @Autowired
    private AssetMonitorInfoV2Service service;

    @Autowired
    private AssetMonitorOidV2Service monitorOidV2Service;

    @Autowired
    private MonitorV2IndicatorService monitorV2IndicatorService;

    @Autowired
    private MonitorV2AssetIndicatorViewHistoryService assetIndicatorViewHistoryService;

    @Lazy
    @Autowired
    private MonitorRunnerV2 monitorRunner;

    private static Log log = LogFactory.getLog(AssetMonitorInfoV2Controller.class);

    private static ObjectMapper objectMapper;

    private final static String MONITOR_INDEX_PREFIX = "monitor-asset-v2-";

    private ExecutorService ec = new ThreadPoolExecutor(10, 100, 0L,
            TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(1000), new ThreadFactoryBuilder().setNameFormat("monitor-data-pool-%d").build(),
            new ThreadPoolExecutor.CallerRunsPolicy());

    static {
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @ApiOperation("获取资产类型")
    @RequestMapping(method = RequestMethod.GET, value = "/assettype/getAssetTypeComboboxTree")
    public VData<List<AssetType>> getAssetType() {
        return service.getAssetType();
    }

    @ApiOperation("获取资产类型")
    @RequestMapping(method = RequestMethod.GET, value = "/assettype/getMonitorAssetTypeTree")
    public VData<List<AssetType>> getMonitorAssetTypeTree() {
        return service.getMonitorAssetTypeTree();
    }



    @GetMapping("/v2/asset_monitor/{id}")
    @ApiOperation("获取资产监控v2")
    public VData<Monitor2AssetInfo> querySingleRecode(@PathVariable String id) {
        Monitor2AssetInfo record = new Monitor2AssetInfo();
        try {
            record.setId(Integer.parseInt(id));
            record = service.querySingle(record);
        } catch (Exception e) {
            Monitor2AssetInfoQuery query = new Monitor2AssetInfoQuery();
            query.setDevId(id);
            List<Monitor2AssetInfo> listVData = service.queryAll(query).getData();
            record = listVData.isEmpty() ? record : listVData.get(0);
        }
        return VoBuilder.vd(record, RetMsgEnum.SUCCESS);
    }

    @PostMapping("/v2/asset_monitor")
    @ApiOperation("分页查询资产监控列表v2")
    public VList<Monitor2AssetInfo> queryByPage(@RequestBody Monitor2AssetInfoQuery record) {
        VList<Monitor2AssetInfo> recordList = null;
        try {
            recordList = service.queryByPage(record);
            monitorRunner.queryConnectStateFromRedis(recordList.getList());
        } catch (Exception e) {
            log.error("", e);
            recordList = new VList<>(0, Collections.emptyList());
        }
        return recordList;
    }

    @PostMapping("/v2/asset_monitor/by_type")
    @ApiOperation(value = "查询资产监控列表,按类型分类并统计v2", notes = "snoUnicode不为空时会获取设备的cpu和内存使用率信息")
    public VData<List<MonitorAssetGeneral<Monitor2AssetInfo>>> queryDataGroupByType(@RequestBody Monitor2AssetInfoQuery record) {
        List<MonitorAssetGeneral<Monitor2AssetInfo>> resList = new ArrayList<>();
        try {
            record.setMyStart(0);
            record.setMyCount(9999);
            List<Monitor2AssetInfo> recordList = service.queryByPage(record).getList();
            boolean littleQuery = record.getSnoUnicode() != null || record.getAssetType() != null;

            Map<String, List<Monitor2AssetInfo>> listMap = recordList.stream().collect(Collectors.groupingBy(Monitor2AssetInfo::getAssetType));
            listMap.forEach((k, v) -> {
                MonitorAssetGeneral monitorAssetGeneral = new MonitorAssetGeneral();
                monitorAssetGeneral.setConnectCount(v.stream().filter(f -> f.getStartupState() == 1 && f.getConnectState() != null && f.getConnectState() == 1).count());
                monitorAssetGeneral.setStartUpCount(v.stream().filter(f -> f.getStartupState() != null && f.getStartupState() == 1).count());
                monitorAssetGeneral.setType(k);
                if (littleQuery) {
                    monitorAssetGeneral.setData(v);
                }
                monitorAssetGeneral.setTotalCount(v.size());
                resList.add(monitorAssetGeneral);
            });
        } catch (Exception e) {
            log.error("", e);
        }
        return VoBuilder.vd(resList);
    }

    @PostMapping("/v2/asset_monitor_base/by_type")
    @ApiOperation(value = "查询资产监控列表,按类型分类并统计v2", notes = "snoUnicode不为空时会获取设备的cpu和内存使用率信息")
    public VData<List<MonitorAssetGeneral<Monitor2AssetInfo>>> queryBaseUsedGroupByType(@RequestBody Monitor2AssetInfoQuery record) {
        List<MonitorAssetGeneral<Monitor2AssetInfo>> resList = new ArrayList<>();
        try {
            record.setMyStart(0);
            record.setMyCount(9999);
            List<Monitor2AssetInfo> recordList = service.queryByPage(record).getList();
            boolean littleQuery = record.getSnoUnicode() != null || record.getAssetType() != null;
            if (littleQuery) {
                //获取实时cpu及内存信息
                CountDownLatch doneSignal = new CountDownLatch(recordList.size());
                recordList.forEach(r -> {
                    ec.execute(() -> {
                        r.setRealInfo(monitorRunner.cpuAndMemoryPercent(r.getDevId()));
                        doneSignal.countDown();
                    });
                });
                doneSignal.await(5, TimeUnit.SECONDS);
                //recordList.parallelStream().forEach(r -> r.setRealInfo(monitorRunner.cpuAndMemoryPercent(r.getDevId())));
            }

            Map<String, List<Monitor2AssetInfo>> listMap = recordList.stream().collect(Collectors.groupingBy(Monitor2AssetInfo::getAssetType));
            listMap.forEach((k, v) -> {
                MonitorAssetGeneral monitorAssetGeneral = new MonitorAssetGeneral();
                monitorAssetGeneral.setConnectCount(v.stream().filter(f -> f.getStartupState() == 1 && f.getConnectState() != null && f.getConnectState() == 1).count());
                monitorAssetGeneral.setStartUpCount(v.stream().filter(f -> f.getStartupState() != null && f.getStartupState() == 1).count());
                monitorAssetGeneral.setType(k);
                if (littleQuery) {
                    monitorAssetGeneral.setData(v);
                }
                monitorAssetGeneral.setTotalCount(v.size());
                resList.add(monitorAssetGeneral);
            });
        } catch (Exception e) {
            log.error("", e);
        }
        return VoBuilder.vd(resList);
    }

    @DeleteMapping("/v2/asset_monitor")
    @ApiOperation("删除资产监控及相关信息v2")
    public Result deleteItem(@RequestBody DeleteModel record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            Monitor2AssetInfo param = new Monitor2AssetInfo();
            param.setId(record.getIntegerId());

            Monitor2AssetInfo monitorAssetInfo = service.querySingle(param);
            if (monitorAssetInfo != null) {
                monitorRunner.deleteMonitor(monitorAssetInfo);
            }
            service.deleteItem(param);

        } catch (NumberFormatException e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }


    @PutMapping("/v2/asset_monitor")
    @ApiOperation("新增资产监控v2")
    public Result add(@RequestBody Monitor2AssetInfo record) {
        Result res = null;
        try {
            LocalDateTime createTime = LocalDateTime.now();
            record.setCreateTime(createTime);
            record.setUpdateTime(createTime);
            AssetMonitorInfoQuery query = new AssetMonitorInfoQuery();
            query.setDevId(record.getDevId());
            if (service.queryAll(query).getData().size() > 0) {
                res = new Result("", "监控设备已存在");
                return VoBuilder.result(res);
            }
            service.addItem(record);
            if (record.getStartupState() == 1) {
                if (monitorRunner.addOrUpdateMonitor(record) == null) {
                    res = VoBuilder.vd(record, new ValidateTools.RetMsg("添加成功, 但未匹配到正确的监控配置", "0"));
                    return res;
                }
            }
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    @GetMapping("/v2/asset_monitor/all_devid")
    @ApiOperation("获取所有已开启资产监控的devid及连通状态")
    public VData<List<Map<String, Object>>> selectAssetConnectStatus() {
        List<Map<String, Object>> maps = service.selectAssetConnectStatus();
        return VoBuilder.vd(maps);
    }

    @PostMapping("/v2/asset_monitor/test")
    @ApiOperation("资产监控连通测试v2")
    public Result test(@RequestBody Monitor2AssetInfo record) {
        boolean test = monitorRunner.test(record);
        return test ? VoBuilder.success() : VoBuilder.fail();
    }

    @PostMapping("/v2/asset_monitor/test/oid")
    @ApiOperation("资产监控连通测试")
    public Result testOid(@RequestBody OidAlgTest record) {
        String oids = record.getOtherInfo();
        return VoBuilder.vd(monitorRunner.test(record, oids));
    }

    @PostMapping("/v2/asset_monitor/test/algo")
    @ApiOperation("资产监控算法测试")
    public Result testOidWithAlgo(@RequestBody OidAlgTest record) {
        String oids = record.getOids();
        Object test = monitorRunner.test(record, oids, record.getAlgo());
        if (test == null) {
            return VoBuilder.errorVdata();
        }
        Monitor2AssetOidAlg param = new Monitor2AssetOidAlg();
        param.setId(record.getAlgId());
        param.setTestRes(test.toString());
        monitorOidV2Service.updateItem(param);
        return VoBuilder.vd(test, RetMsgEnum.SUCCESS);
    }

    @PatchMapping("/v2/asset_monitor")
    @ApiOperation("修改资产监控v2")
    public Result update(@RequestBody Monitor2AssetInfo record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            service.updateItem(record);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @PostMapping("/v2/asset_monitor/all_indicator")
    @ApiOperation("获取所有监控指标")
    public VData<List<Monitor2Indicator>> queryIndicatorAll(@RequestBody Monitor2IndicatorQuery query) {
        List<Monitor2Indicator> maps = service.queryIndicatorAll(query);
        return VoBuilder.vd(maps);
    }

    @GetMapping("/v2/asset_monitor/all_indicator_view/{snoUnicode}")
    @ApiOperation("获取某资产类型可展示面板")
    public VData<List<Monitor2IndicatorView>> queryIndicatorViewAll(@PathVariable String snoUnicode) {
        Monitor2IndicatorViewQuery query = new Monitor2IndicatorViewQuery();
        List<Monitor2IndicatorView> list = service.queryIndicatorViewAll(query);

        Monitor2AssetOidAlgQuery record = new Monitor2AssetOidAlgQuery();
        record.setSnoUnicode(snoUnicode);
        record.setAvailable(1);
        record.setMyCount(999);
        record.setMyStart(0);
        List<Monitor2AssetOidAlg> assetOidAlgs = monitorOidV2Service.queryByPage(record).getList();
        if (assetOidAlgs.isEmpty()) {
            list.clear();
        } else {
            List<String> indicators = assetOidAlgs.stream().map(Monitor2AssetOidAlg::getIndicatorField).collect(Collectors.toList());
            list = list.stream().filter(f ->
                    indicators.containsAll(Arrays.asList(f.getIndicators().split(",")))
            ).collect(Collectors.toList());
        }
        return VoBuilder.vd(list);
    }

    @PutMapping("/v2/asset_type/indicator_view_setting")
    @ApiOperation("新增资产类型展示配置")
    public Result addAssetIndicatorViewAll(@RequestBody Monitor2AssetIndicatorView record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            Monitor2AssetIndicatorViewQuery query = new Monitor2AssetIndicatorViewQuery();
            query.setAssetType(record.getAssetType());
            query.setSnoUnicode(record.getSnoUnicode());
            if (service.queryAssetIndicatorViewAll(query).size() > 0) {
                return VoBuilder.result(new Result("9999", "数据已存在"));
            }

            service.saveAssetIndicatorViewAll(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @PatchMapping("/v2/asset_type/indicator_view_setting")
    @ApiOperation("修改资产类型展示配置")
    public Result queryAssetIndicatorViewAll(@RequestBody Monitor2AssetIndicatorView record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            service.saveAssetIndicatorViewAll(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @PatchMapping("/v2/asset_type/indicator_view_setting/backup")
    @ApiOperation("修改资产类型展示配置并备份历史版本")
    public Result queryAssetIndicatorViewAllThenBackup(@RequestBody Monitor2AssetIndicatorView record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            service.saveAssetIndicatorViewAll(record);
            Monitor2AssetIndicatorViewHistory history = new Monitor2AssetIndicatorViewHistory();
            history.setVersion(TimeTools.formatTimeStamp(TimeTools.getNow()));
            BeanUtils.copyProperties(record, history, "id", "createTime", "updateTime");
            assetIndicatorViewHistoryService.addItem(history);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @DeleteMapping("/v2/asset_type/indicator_view_setting/backup")
    @ApiOperation("删除历史版本")
    public Result queryAssetIndicatorViewAllThenBackup(@RequestBody DeleteModel record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            Monitor2AssetIndicatorViewHistory history = new Monitor2AssetIndicatorViewHistory();
            history.setId(record.getIntegerId());
            assetIndicatorViewHistoryService.deleteItem(history);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @PostMapping("/v2/asset_type/indicator_view_setting")
    @ApiOperation("获取所有资产类型展示配置")
    public VData<List<Monitor2AssetIndicatorView>> queryAssetIndicatorViewAll(@RequestBody Monitor2AssetIndicatorViewQuery query) {
        List<Monitor2AssetIndicatorView> maps = service.queryAssetIndicatorViewAll(query);
        return VoBuilder.vd(maps);
    }

    @PostMapping("/v2/asset_type/indicator_view_setting/history")
    @ApiOperation(value = "获取该资产类型展示配置的历史版本", notes = "参数 snoUnicode")
    public VList<Monitor2AssetIndicatorViewHistory> queryAssetIndicatorViewHistory(@RequestBody Monitor2AssetIndicatorViewHistoryQuery query) {
        query.setOrder("createTime");
        query.setBy("desc");
        return assetIndicatorViewHistoryService.queryByPage(query);
    }

    @GetMapping("/v2/asset_monitor/algo/{snoUnicode}")
    @ApiOperation("查询某资产类型的所有指标算法")
    public VData<List<Monitor2AssetOidAlg>> queryAlgoBySno(@PathVariable String snoUnicode) {
        Monitor2AssetOidAlgQuery record = new Monitor2AssetOidAlgQuery();
        record.setSnoUnicode(snoUnicode);
        record.setMyCount(999);
        return VoBuilder.vd(monitorOidV2Service.queryByPage(record).getList());
    }

    @PostMapping("/v2/asset_monitor/algo")
    @ApiOperation("分页查询指标算法")
    public VList<Monitor2AssetOidAlg> queryAlgoByPage(@RequestBody Monitor2AssetOidAlgQuery record) {
        VList<Monitor2AssetOidAlg> recordList = null;
        try {
            recordList = monitorOidV2Service.queryByPage(record);
        } catch (Exception e) {
            log.error("", e);
            recordList = new VList<>(0, Collections.emptyList());
        }
        return recordList;
    }

    @PutMapping("/v2/asset_monitor/algo")
    @ApiOperation("新增指标算法")
    public Result addAlgo(@RequestBody Monitor2AssetOidAlg record) {
        Result res = null;
        try {
            if (checkAlgoContainEmpty(record)) {
                return VoBuilder.vd(record, new ValidateTools.RetMsg("算法参数不能为空", RetMsgEnum.EMPTY_PARAM.getCode()));
            }

            monitorOidV2Service.addItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res = VoBuilder.result(RetMsgEnum.FAIL);
        }
        return res;
    }

    private boolean checkAlgoContainEmpty(@RequestBody Monitor2AssetOidAlg record) {
        record.setUnit(record.getUnit() == null ? "" : record.getUnit());
        String algoPrefix = "Algo.";
        String emptyArg = "''";
        if (record.getAlgo().startsWith(algoPrefix) && record.getAlgo().contains(emptyArg)) {
            //含有空参数
            return true;
        }
        return false;
    }

    @PatchMapping("/v2/asset_monitor/algo")
    @ApiOperation("修改指标算法v2")
    public Result updateAlgo(@RequestBody Monitor2AssetOidAlg record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            if (checkAlgoContainEmpty(record)) {
                return VoBuilder.vd(record, new ValidateTools.RetMsg("算法参数不能为空", RetMsgEnum.EMPTY_PARAM.getCode()));
            }
            monitorOidV2Service.updateItem(record);
            res = VoBuilder.vd(record, RetMsgEnum.SUCCESS);
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @DeleteMapping("/v2/asset_monitor/algo")
    @ApiOperation("删除指标算法")
    public Result deleteAlgoItem(@RequestBody DeleteModel record) {
        Result res = VoBuilder.result(RetMsgEnum.SUCCESS);
        try {
            Monitor2AssetOidAlg param = new Monitor2AssetOidAlg();
            record.getIntegerIdList().forEach(r -> {
                param.setId(r);
                monitorOidV2Service.deleteItem(param);
            });
        } catch (Exception e) {
            log.error("", e);
            res.setCode(RetMsgEnum.FAIL.getCode());
        }
        return res;
    }

    @PostMapping("/v2/monitor/lastest")
    @ApiOperation(value = "监控数据统一查询接口", notes = "必传条件assetId,viewType,indicators;时间范围可不传, 默认最近10分钟范围")
    public VData<Map<String, Object>> search(@RequestBody Monitor2DataQuery monitor2DataQuery) {
        if (StringUtils.isAnyBlank(monitor2DataQuery.getAssetId(), monitor2DataQuery.getViewType())) {
            return VoBuilder.errorVdata();
        }
        Map<String, Object> res = new HashMap<>();
        QueryTools.QueryWrapper wrapper = QueryTools.build();

        if (monitor2DataQuery.getMyStartTime() == null || monitor2DataQuery.getMyEndTime() == null) {
            monitor2DataQuery.setMyStartTime(TimeTools.getNowBeforeByMinute(10));
            monitor2DataQuery.setMyEndTime(TimeTools.getNow());
        }

        PageModel model = new PageModel();
        model.setMyStartTime(monitor2DataQuery.getMyStartTime());
        model.setMyEndTime(monitor2DataQuery.getMyEndTime());

        boolean trend = false;
        String viewType = monitor2DataQuery.getViewType().split("_")[0];
        if ("trend".equals(viewType)) {
            trend = true;
        }
        model.setMyCount(trend ? 100 : 1);

        List<MonitorData> assembledData = new ArrayList<>();
        List<String> indicators = Arrays.asList(monitor2DataQuery.getIndicators().split(","));
        List<Monitor2Indicator> monitor2Indicators = monitorV2IndicatorService.getMonitor2Indicators();
        Map<String, List<MonitorData>> resAll = searchUnrealData(monitor2DataQuery, wrapper, model, indicators, monitor2Indicators);

        MonitorData realData = searchRealMonitorData(monitor2DataQuery, indicators, monitor2Indicators);
        if (realData != null) {
            resAll.put("real", Collections.singletonList(realData));
            //assembledData.add(realData);
        }

        if (!trend) {
            //多个类型的数据合成一条
            MonitorData single = resAll.values().stream().flatMap(Collection::stream).reduce((a, b) -> {
                a.getMonitorData().putAll(b.getMonitorData());
                a.getTip().putAll(b.getTip());
                a.getUnit().putAll(b.getUnit());
                return a;
            }).orElseGet(MonitorData::new);
            assembledData.add(single);
        } else {
            resAll.forEach((k, v) -> assembledData.addAll(v));
        }

        //assembledData.forEach(d -> d.setMonitorData(d.getSource()));
        res.put("list", assembledData);
        return VoBuilder.vd(res);
    }

    private MonitorData searchRealMonitorData(@RequestBody Monitor2DataQuery monitor2DataQuery, List<String> indicators, List<Monitor2Indicator> monitor2Indicators) {
        Map<String, String> realIndicatorTypes = monitor2Indicators.stream().filter(f -> f.getRealQuery() == 1).collect(Collectors.toMap(Monitor2Indicator::getIndicatorField, Monitor2Indicator::getIndicatorType));
        List<String> realIndicators = indicators.stream().filter(realIndicatorTypes::containsKey).collect(Collectors.toList());

        if (realIndicators.isEmpty()) {
            return null;
        }

        Map<String, String> data = monitorRunner.realInfo(monitor2DataQuery.getAssetId(), realIndicators);
        MonitorData realData = new MonitorData();
        realData.setMonitorData(data);

        realData.setDevId(monitor2DataQuery.getAssetId());

        realData.setEventTime(TimeTools.format2(TimeTools.getNow()));
        Map unitMap = new HashMap(realIndicators.size());
        Map tipMap = new HashMap(realIndicators.size());

        Map<String, OidAlgEx> oidAlgExMap = monitorRunner.snmpMonitorAlg(monitor2DataQuery.getAssetId());
        realIndicators.forEach(r -> {
            OidAlgEx algEx = oidAlgExMap.get(r);
            unitMap.put(r, algEx.getUnit());
            tipMap.put(r, algEx.getIndicatorName());
        });

        realData.setTip(tipMap);
        realData.setUnit(unitMap);
        return realData;
    }

    private Map<String, List<MonitorData>> searchUnrealData(@RequestBody Monitor2DataQuery monitor2DataQuery, QueryTools.QueryWrapper wrapper, PageModel model, List<String> indicators, List<Monitor2Indicator> monitor2Indicators) {
        Map<String, List<MonitorData>> resAll = new HashMap<>(indicators.size());
        Map<String, String> indicatorTypes = monitor2Indicators.stream().filter(f -> f.getRealQuery() == 0).collect(Collectors.toMap(Monitor2Indicator::getIndicatorField, Monitor2Indicator::getIndicatorType));
        if (indicatorTypes.isEmpty()) {
            return resAll;
        }
        //按指标类型分组
        Map<String, List<String>> indicatorsGroupByType = indicators.stream().filter(indicatorTypes::containsKey).collect(Collectors.groupingBy(r -> indicatorTypes.get(r)));
        //分索引去查询( key为指标类型, 一个类型对应一个索引)


        indicatorsGroupByType.forEach((indicatorType, v) -> {
            List<MonitorData> data = searchLastestObj(model, monitor2DataQuery.getAssetId(), wrapper, MONITOR_INDEX_PREFIX + indicatorType, indicators);
            if (data != null && !data.isEmpty()) {
                resAll.put(indicatorType, data);
            }
        });
        return resAll;
    }

    @PostMapping("/v2/monitor/eg")
    @ApiOperation(value = "生成面板样例数据模板")
    public VData<Map<String, Object>> eg(@RequestBody Monitor2DataQuery monitor2DataQuery) {
        Map<String, Object> res = monitorV2IndicatorService.monitorExample(monitor2DataQuery);
        return VoBuilder.vd(res);
    }

    /**
     * @return
     * @throws IOException
     */
    @GetMapping("/v2/monitor/upTime/{assetId}")
    @ApiOperation("查询设备运行时长")
    public VData<Map<String, Object>> upTime(@PathVariable String assetId) {
        Map<String, Object> res = new HashMap<>();
        try {
            String upTime = monitorRunner.upTime(assetId);
            upTime = upTime.replaceFirst(" days, ", "天").replaceFirst("days", "天").replaceFirst(" day,", "天")
                    .replaceFirst("day", "天").replaceFirst(":", "时").replaceFirst(":", "分");
            res.put("rt", "".equals(upTime) ? upTime : upTime + "秒");
        } catch (Throwable throwable) {
            return VoBuilder.rvd(new ValidateTools.RetMsg("未匹配到正确的监控配置信息", "1999"));
        }
        return VoBuilder.vd(res);
    }

    private List<MonitorData> searchLastestObj(@RequestBody PageModel model, @PathVariable String assetId, QueryTools.QueryWrapper wrapper, String index, List<String> indicators) {
        QueryModel queryModelCpu = QueryTools.buildQueryModel(wrapper, model, index, "event_time");
        BoolQueryBuilder query = new BoolQueryBuilder();
        if (StringUtils.isNotEmpty(assetId)) {
            query.filter(QueryBuilders.termQuery("dev_id", assetId));
        }
        queryModelCpu.setQueryBuilder(query);
        queryModelCpu.setSort(true);
        queryModelCpu.setSortFields(new String[]{"event_time"});
        queryModelCpu.setSortOrder(SortOrder.DESC);
        SearchResponse searchResponse = wrapper.getSearchResponse(queryModelCpu);
        if (searchResponse != null) {
            List<Map<String, String>> list = wrapper.wrapResponse(searchResponse.getHits(), "event_time");
            List<MonitorData> data = list.stream().map(r -> {
                MonitorData monitorData = new MonitorData();
                monitorData.setDevId(r.get("dev_id"));
                monitorData.setDevIp(r.get("dev_ip"));
                monitorData.setDevType(r.get("dev_type"));
                monitorData.setEventTime(r.get("event_time"));
                monitorData.setSno(r.get("sno"));
                String units = r.get("units");
                String tips = r.get("tips");
                try {
                    Map unitMap = objectMapper.readValue(units, Map.class);
                    Map tipMap = objectMapper.readValue(tips, Map.class);
                    unitMap.keySet().removeIf(k -> !indicators.contains(k));
                    tipMap.keySet().removeIf(k -> !indicators.contains(k));
                    monitorData.setTip(tipMap);
                    monitorData.setUnit(unitMap);
                } catch (JsonProcessingException e) {
                    log.error("", e);
                }
                r.keySet().removeIf(k -> !indicators.contains(k));
                monitorData.setMonitorData(r);
                return monitorData;
            }).collect(Collectors.toList());
            return data;
        }
        return Collections.emptyList();
    }
}
