package com.vrv.vap.amonitor.command;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.vrv.vap.amonitor.service.AssetMonitorInfoV2Service;
import com.vrv.vap.amonitor.service.AssetMonitorOidV2Service;
import com.vrv.vap.amonitor.service.MonitorV2IndicatorService;
import com.vrv.vap.amonitor.VapMonitorApplication;
import com.vrv.vap.amonitor.entity.Monitor2AssetInfo;
import com.vrv.vap.amonitor.entity.Monitor2AssetOidAlg;
import com.vrv.vap.amonitor.entity.Monitor2Indicator;
import com.vrv.vap.amonitor.model.Monitor2Setting;
import com.vrv.vap.amonitor.model.MonitorDataInfo;
import com.vrv.vap.amonitor.model.OidAlgEx;
import com.vrv.vap.amonitor.model.es.IndexTemplate;
import com.vrv.vap.amonitor.snmp.ISnmpMonitor;
import com.vrv.vap.amonitor.snmp.SnmpMonitorV2;
import com.vrv.vap.amonitor.tools.EsCurdTools;
import com.vrv.vap.amonitor.tools.QueryTools;
import com.vrv.vap.amonitor.vo.AssetMonitorInfoQuery;
import com.vrv.vap.amonitor.vo.Monitor2AssetOidAlgQuery;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.elasticsearch.action.support.WriteRequest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class MonitorRunnerV2 implements ApplicationRunner {

    private static final Log log = LogFactory.getLog(MonitorRunnerV2.class);

    //private String master;

    //private String nodes;

    private String application = VapMonitorApplication.getApplicationContext().getEnvironment().getProperty("spring.application.name", "application");

    @Autowired
    private NacosDiscoveryProperties nacosDiscoveryProperties;

    @Autowired
    private AssetMonitorInfoV2Service monitorInfoService;

    @Autowired
    private MonitorV2IndicatorService monitorV2IndicatorService;

    @Autowired
    private AssetMonitorOidV2Service monitorOidV2Service;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private KafkaTemplate<String, Map<String, Object>> kafkaTemplate;

    @Value("${monitor.dynamic:false}")
    private boolean dynamicOpen;

    @Value("${monitor.write-kafka:false}")
    private boolean writeKafka = true;

    @Value("${monitor.refresh-time:30}")
    private int refreshTime = 30;

    private ExecutorService ec = new ThreadPoolExecutor(3, 10, 60L, TimeUnit.SECONDS, new LinkedBlockingQueue(100));

    private static final String KEY_PREFIX = "monitor:devstatus:";

    private final static String MONITOR_TEMPLATE_PREFIX = "monitor-asset-v2";

    private final static String TEXT = "text";
    private final static String KEYWORD = "keyword";

    private static ScheduledExecutorService excutor = new ScheduledThreadPoolExecutor(2,
            new BasicThreadFactory.Builder().namingPattern("es-schedule-pool-%d").daemon(true).build());

    /**
     * 存放资产的监控数据
     */
    private static ConcurrentLinkedDeque<MonitorDataInfo> queue = new ConcurrentLinkedDeque();

    /**
     * 存放资产的连通数据
     */
    private static ConcurrentLinkedDeque<MonitorDataInfo> queue2 = new ConcurrentLinkedDeque();

    private Map<String, SnmpMonitorV2> runningMap = new ConcurrentHashMap<>();

    private Map<String, SnmpMonitorV2> snmpMonitorMap = new HashMap<>();

    public static void pushMonitorData(MonitorDataInfo data) {
        queue.addLast(data);
    }

    public static void pushConnectedData(MonitorDataInfo data) {
        queue2.addLast(data);
    }

    /**
     * Callback used to run the bean.
     *
     * @param args incoming application arguments
     * @throws Exception on error
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (dynamicOpen) {
            execute();
            //低于0.11版本不支持事务及幂等
            boolean enableIdempotence = Boolean.valueOf(kafkaTemplate.getProducerFactory().getConfigurationProperties().getOrDefault(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, "true").toString());
            kafkaTemplate.setAllowNonTransactional(!enableIdempotence);
        }
    }

    private void execute() {
        //NamingService namingService = nacosDiscoveryProperties.namingServiceInstance();
        //List<Instance> allInstances = namingService.getAllInstances(application, true);

        //检查监控数据入库模板
        List<Monitor2Indicator> monitor2Indicators = monitorV2IndicatorService.getMonitor2Indicators();
        syncEsTemplate(monitor2Indicators);
        initMonitors();

        int second = Calendar.getInstance().get(Calendar.SECOND);
        int delay = 10 - second % 10;
        //开启监控线程
        excutor.scheduleAtFixedRate(this::startMonitor, delay, refreshTime, TimeUnit.SECONDS);

        //开启写任务线程
        excutor.scheduleAtFixedRate(this::writeData, 10, refreshTime + 30, TimeUnit.SECONDS);
    }

    public synchronized static void syncEsTemplate(List<Monitor2Indicator> monitor2Indicators) {
        String templateJson = EsCurdTools.searchTemplate(MONITOR_TEMPLATE_PREFIX);
        try {
            Map<String, IndexTemplate> indexTemplateMap = QueryTools.objectMapper.readValue(templateJson, new TypeReference<Map<String, IndexTemplate>>() {
            });
            indexTemplateMap.forEach((k, v) -> {
                Map<String, IndexTemplate.Field> fields = v.getMappings().getCompatibleProperties();
                Map<String, IndexTemplate.Field> changedFields = new HashMap<>();
                final boolean[] changed = {false};
                monitor2Indicators.forEach(monitor2Indicator -> {
                    if (monitor2Indicator.getAvailable() != 1) {
                        return;
                    }
                    String indicatorField = monitor2Indicator.getIndicatorField();
                    String esType = StringUtils.isBlank(monitor2Indicator.getEsType()) ? KEYWORD : monitor2Indicator.getEsType();

                    if (StringUtils.isBlank(indicatorField)) {
                        return;
                    }

                    IndexTemplate.Field newField = new IndexTemplate.Field(esType);

                    if (TEXT.equals(esType)) {
                        newField.setIndex("false");
                    }
                    if (!fields.containsKey(indicatorField)) {
                        fields.put(indicatorField, newField);
                        changedFields.put(indicatorField, newField);
                        changed[0] = true;
                        return;
                    }

                    if (fields.containsKey(indicatorField) && !esType.equals(fields.get(indicatorField).getType())) {
                        fields.put(indicatorField, newField);
                        changedFields.put(indicatorField, newField);
                        changed[0] = true;
                    }
                });
                try {
                    if (changed[0]) {
                        //同步模板
                        EsCurdTools.saveTemplate(k, QueryTools.objectMapper.writeValueAsString(v));
                        //同步索引mappings
                        IndexTemplate.Mappings mappings = new IndexTemplate.Mappings();
                        mappings.setProperties(changedFields);
                        EsCurdTools.saveMappings(MONITOR_TEMPLATE_PREFIX + "*", QueryTools.objectMapper.writeValueAsString(mappings));
                    }
                } catch (JsonProcessingException e) {
                    log.error("", e);
                }
            });
        } catch (JsonProcessingException e) {
            log.error("", e);
        }
    }

    /**
     * 标记配置变化
     *
     * @param snoUnicode
     */
    public void settingChanged(String snoUnicode) {
        if (StringUtils.isEmpty(snoUnicode)) {
            return;
        }
        for (SnmpMonitorV2 monitorV2 : snmpMonitorMap.values()) {
            if (!snoUnicode.equals(monitorV2.getSetting().getAssetInfo().getSnoUnicode())) {
                continue;
            }
            monitorV2.setSettingChanged(true);
        }
    }

    private void markConnectedStateInRedis(Monitor2AssetInfo monitorAssetInfo) {
        redisTemplate.boundValueOps(KEY_PREFIX + monitorAssetInfo.getDevId()).set(1, 30, TimeUnit.SECONDS);
    }

    public Integer queryConnectStateFromRedis(Monitor2AssetInfo monitorAssetInfo) {
        Object state = redisTemplate.boundValueOps(KEY_PREFIX + monitorAssetInfo.getDevId()).get();
        if (state != null) {
            try {
                monitorAssetInfo.setConnectState(Integer.parseInt(state.toString()));
            } catch (NumberFormatException e) {
                monitorAssetInfo.setConnectState(0);
            }
        } else {
            monitorAssetInfo.setConnectState(0);
        }
        return monitorAssetInfo.getConnectState();
    }

    public List<Integer> queryConnectStateFromRedis(List<Monitor2AssetInfo> monitorAssetInfo) {
        int size = monitorAssetInfo.size();
        int batch = 100;
        int start = 0;
        List<Integer> allKeysState = new ArrayList<>(monitorAssetInfo.size());
        while (start < size) {
            List<Monitor2AssetInfo> tmpList = monitorAssetInfo.subList(start, start + batch > size ? size : batch);
            allKeysState.addAll(queryConnectStateFromRedisLimit(tmpList));
            start += batch;
        }
        return allKeysState;
    }

    private List<Integer> queryConnectStateFromRedisLimit(List<Monitor2AssetInfo> monitorAssetInfo) {
        List<String> keys = monitorAssetInfo.stream().map(r -> KEY_PREFIX + r.getDevId()).collect(Collectors.toList());
        List<Integer> state = redisTemplate.opsForValue().multiGet(keys);
        if (state == null) {
            return state;
        }
        for (int i = 0; i < state.size(); i++) {
            monitorAssetInfo.get(i).setConnectState(state.get(i));
        }
        return state;
    }

    /**
     * 添加或修改资产监视
     *
     * @param monitorAssetInfo
     * @return
     */
    public SnmpMonitorV2 addOrUpdateMonitor(Monitor2AssetInfo monitorAssetInfo) {
        // 获取 oid配置
        Monitor2AssetOidAlgQuery assetOidQuery = new Monitor2AssetOidAlgQuery();
        assetOidQuery.setSnoUnicode(monitorAssetInfo.getSnoUnicode());
        assetOidQuery.setAvailable(1);
        //assetOidQuery.setRealQuery(0);
        Map<String, List<OidAlgEx>> assetOidVoMap = getStringMonitorAssetOidVoMap(assetOidQuery);
        try {
            List<OidAlgEx> oidAlgos = assetOidVoMap.get(monitorAssetInfo.getSnoUnicode());
            /*if (oidAlgos == null) {
                return null;
            }*/
            Monitor2Setting setting = new Monitor2Setting();
            setting.setAssetInfo(monitorAssetInfo);
            setting.setOidAlgs(oidAlgos);
            SnmpMonitorV2 snmpMonitorV2 = SnmpMonitorV2.createMonitor(setting);
            snmpMonitorMap.put(monitorAssetInfo.getDevId(), snmpMonitorV2);
            runningMap.remove(monitorAssetInfo.getDevId());
            return snmpMonitorV2;
        } catch (IOException e) {
            log.error("", e);
        }
        return null;
    }

    /**
     * 删除监视
     *
     * @param monitorAssetInfo
     */
    public void deleteMonitor(Monitor2AssetInfo monitorAssetInfo) {
        snmpMonitorMap.remove(monitorAssetInfo.getDevId());
        runningMap.remove(monitorAssetInfo.getDevId());
    }

    /**
     * 监视连通测试
     *
     * @param monitorAssetInfo
     * @return
     */
    public boolean test(Monitor2AssetInfo monitorAssetInfo) {
        try {
            ISnmpMonitor monitor = SnmpMonitorV2.createTestMonitor(monitorAssetInfo);
            return monitor.testSnmp();
        } catch (IOException e) {
            log.error("", e);
        }
        return false;
    }

    public Object test(Monitor2AssetInfo monitorAssetInfo, String oids, String... algo) {
        try {
            SnmpMonitorV2 monitor = SnmpMonitorV2.createTestMonitor(monitorAssetInfo);
            return algo != null ? monitor.testSnmp(algo[0], oids) : monitor.testSnmp(oids.split(","));
        } catch (IOException e) {
            log.error("", e);
            return e;
        }
    }

    /**
     * 获取在线时长
     *
     * @param monitorAssetInfo
     * @return
     */
    public String upTime(Monitor2AssetInfo monitorAssetInfo) throws Throwable {
        return upTime(monitorAssetInfo.getDevId());
    }

    /**
     * 获取在线时长
     *
     * @param devId
     * @return
     */
    public String upTime(String devId) throws Throwable {
        try {
            ISnmpMonitor snmpMonitor = snmpMonitorMap.get(devId);
            if (snmpMonitor == null) {
                throw new Throwable();
            }

            return snmpMonitor != null ? snmpMonitor.uptime() : "";
        } catch (Exception e) {
            log.error("", e);
        }
        return "";
    }

    /**
     * 获取实时cpu利用率,内存利用率
     *
     * @param devId
     * @return
     */
    public Map<String, Object> cpuAndMemoryPercent(String devId) {
        Map<String, Object> res = new HashMap<>(2);
        try {
            SnmpMonitorV2 snmpMonitor = snmpMonitorMap.get(devId);
            if (snmpMonitor == null || !snmpMonitor.testSnmp()) {
                return res;
            }
            OidAlgEx cpuUseAlg = snmpMonitor.getSetting().getOidAlgMap().get("cpu_use");
            OidAlgEx memoryPercentAlg = snmpMonitor.getSetting().getOidAlgMap().get("memory_percent");
            if (snmpMonitor != null) {
                res.put("cpu_use", snmpMonitor.execute(cpuUseAlg));
                Object memoryInfo = snmpMonitor.execute(memoryPercentAlg);
                res.put("memory_percent", memoryInfo instanceof Map ? ((Map) memoryInfo).get("memory_percent") : memoryInfo);
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return res;
    }

    /**
     * 获取实时监控数据
     *
     * @param devId
     * @return
     */
    public Map<String, String> realInfo(String devId, String... indicators) {
        return realInfo(devId, Arrays.asList(indicators));
    }

    /**
     * 获取实时监控数据
     *
     * @param devId
     * @return
     */
    public Map<String, String> realInfo(String devId, List<String> indicators) {
        Map<String, String> res = new HashMap<>(indicators.size());
        try {
            SnmpMonitorV2 snmpMonitor = snmpMonitorMap.get(devId);
            if (snmpMonitor == null || !snmpMonitor.testSnmp()) {
                return res;
            }

            for (String indicator : indicators) {
                OidAlgEx cpuUseAlg = snmpMonitor.getSetting().getOidAlgMap().get(indicator);
                if (snmpMonitor != null) {
                    Object executeRes = snmpMonitor.execute(cpuUseAlg);
                    res.put(indicator, (executeRes instanceof List || executeRes instanceof Map) ? QueryTools.objectMapper.writeValueAsString(executeRes) : String.valueOf(executeRes));
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return res;
    }

    public Map<String, OidAlgEx> snmpMonitorAlg(String devId) {
        SnmpMonitorV2 snmpMonitor = snmpMonitorMap.get(devId);
        return snmpMonitor.getSetting().getOidAlgMap();
    }

    private void initMonitors() {
        AssetMonitorInfoQuery query = new AssetMonitorInfoQuery();
        query.setStartupState(1);
        List<Monitor2AssetInfo> monitorAssetList = monitorInfoService.queryAll(query).getData();

        // 获取 oid配置
        Monitor2AssetOidAlgQuery algQuery = new Monitor2AssetOidAlgQuery();
        algQuery.setAvailable(1);
        //algQuery.setRealQuery(0);
        Map<String, List<OidAlgEx>> assetOidVoMap = getStringMonitorAssetOidVoMap(algQuery);
        // Monitor2Setting
        for (Monitor2AssetInfo monitorAssetInfo : monitorAssetList) {
            try {
                List<OidAlgEx> oidAlgos = assetOidVoMap.getOrDefault(monitorAssetInfo.getSnoUnicode(), assetOidVoMap.get(monitorAssetInfo.getAssetType()));
                if (oidAlgos == null) {
                    log.warn(monitorAssetInfo.getSnoUnicode() + "类型未匹配到监控oid参数");
                }
                Monitor2Setting setting = new Monitor2Setting();
                setting.setAssetInfo(monitorAssetInfo);
                setting.setOidAlgs(oidAlgos);
                snmpMonitorMap.put(monitorAssetInfo.getDevId(), SnmpMonitorV2.createMonitor(setting));
            } catch (Exception e) {
                log.error("", e);
            }
        }

    }

    private Map<String, List<OidAlgEx>> getStringMonitorAssetOidVoMap(Monitor2AssetOidAlgQuery query) {
        List<Monitor2AssetOidAlg> monitorAssetOidVos = monitorOidV2Service.queryAll(query).getData();
        Map<String, String> indicatorTypes = monitorV2IndicatorService.getMonitor2Indicators().stream().collect(Collectors.toMap(Monitor2Indicator::getIndicatorField, Monitor2Indicator::getIndicatorType));
        Map<String, List<OidAlgEx>> listMap = monitorAssetOidVos.stream().map(r -> {
            OidAlgEx algEx = new OidAlgEx();
            BeanUtils.copyProperties(r, algEx);
            algEx.setIndicatorType(indicatorTypes.get(r.getIndicatorField()));
            return algEx;
        }).collect(Collectors.groupingBy(Monitor2AssetOidAlg::getSnoUnicode));
        return listMap;
    }

    private void startMonitor() {
        ForkJoinPool forkJoinPool = new ForkJoinPool(100);
        forkJoinPool.execute(() -> {
            List<SnmpMonitorV2> monitors = new ArrayList(snmpMonitorMap.values());
            monitors.parallelStream().forEach(s -> {
                SnmpMonitorV2 s1 = s;
                Monitor2AssetInfo assetInfo = s1.getSetting().getAssetInfo();

                //更新配置
                if (s1.isSettingChanged()) {
                    Monitor2AssetInfo newMonitor2AssetInfo = monitorInfoService.querySingle(assetInfo);
                    s1 = addOrUpdateMonitor(newMonitor2AssetInfo);
                    if (s1 == null) {
                        return;
                    }
                    assetInfo = s1.getSetting().getAssetInfo();
                }

                if (runningMap.containsKey(assetInfo.getDevId())) {
                    log.debug(assetInfo.getDevId() + "上一个监控周期正在执行~~~~~~~~~~~" + TimeTools.format2(new Date()));
                    return;
                }

                runningMap.put(assetInfo.getDevId(), s1);
                try {

                        log.debug(assetInfo.getDevId() + "监控周期正在执行,时间 " + TimeTools.format2(new Date()));
                        s1.monitorAll();
                } catch (Exception e) {
                    log.error("", e);
                } finally {
                    runningMap.remove(assetInfo.getDevId());
                }
            });
        });
    }

    private void writeData() {
        writeDataToEsAndKafka();
    }

    private void writeDataToEsAndKafka() {
        int maxBatchCount = 2000;
        MonitorDataInfo record = null;
        Map<String, Object> data = null;

        List<Map<String, Object>> connectList = new ArrayList<>();
        List<MonitorDataInfo> connectData = new ArrayList<>();
        List<Map<String, Object>> monitorDatalist = new ArrayList<>();
        List<MonitorDataInfo> monitorData = new ArrayList<>();

        while (connectList.size() < maxBatchCount && (record = queue2.poll()) != null) {
            connectData.add(record);
            connectList.add(record.getData());
        }

        while (monitorDatalist.size() < maxBatchCount && (record = queue.poll()) != null) {
            monitorData.add(record);
            monitorDatalist.add(record.getData());
        }

        try {
            QueryTools.writeData2(connectList, QueryTools.TYPE, QueryTools.build(), WriteRequest.RefreshPolicy.NONE);
            QueryTools.writeData2(monitorDatalist, QueryTools.TYPE, QueryTools.build(), WriteRequest.RefreshPolicy.NONE);
            //刷新资产连通状态 redis , mysql
            ec.execute(() -> {
                connectList.forEach(r -> {
                    Monitor2AssetInfo param = new Monitor2AssetInfo();
                    param.setDevId((String) r.get("dev_id"));
                    param.setConnectState((Integer) r.get("reachable"));
                    monitorInfoService.updateConnectStatus(param);
                    markConnectedStateInRedis(param);
                });
            });
        } catch (Exception e) {
            log.error("writing monitor data to es failed!", e);
        }

        if (!writeKafka) {
            return;
        }

        try {
            ec.execute(() -> {
                sendKafka(connectData);
                sendKafka(monitorData);
            });
        } catch (Exception e) {
            log.error("writing monitor data to kafka failed!", e);
        }

    }

    private void sendKafka(List<MonitorDataInfo> recordList) {
        try {
            for (MonitorDataInfo record : recordList) {
                kafkaTemplate.send(record.getTopic(), record.getData());
            }
        } catch (Exception e) {
            log.error("writing monitor data to kafka failed!", e);
        }
    }

}
