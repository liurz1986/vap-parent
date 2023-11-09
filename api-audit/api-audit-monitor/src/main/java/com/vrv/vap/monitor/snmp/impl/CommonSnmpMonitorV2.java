package com.vrv.vap.monitor.snmp.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.vrv.vap.monitor.command.MonitorRunnerV2;
import com.vrv.vap.monitor.model.Monitor2Setting;
import com.vrv.vap.monitor.model.MonitorDataInfo;
import com.vrv.vap.monitor.model.OidAlgEx;
import com.vrv.vap.monitor.snmp.SnmpAlgo;
import com.vrv.vap.monitor.snmp.SnmpMonitorV2;
import com.vrv.vap.toolkit.tools.TimeTools;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 通用监视器V2
 * <p>根据配置表达式来计算监控指标值</p>
 */
public class CommonSnmpMonitorV2 extends SnmpMonitorV2 {

    private static final Log log = LogFactory.getLog(CommonSnmpMonitorV2.class);

    private List<String> notPartion = Arrays.asList("physical memory", "virtual memory", "memory buffers", "cached memory", "swap space", "shared memory");

    public CommonSnmpMonitorV2(Monitor2Setting setting) throws IOException {
        super(setting);
        if (setting.getOidAlgs() == null) {
            return;
        }
        for (OidAlgEx oidAlgEx : setting.getOidAlgs()) {
            if (StringUtils.isBlank(oidAlgEx.getAlgo())) {
                continue;
            }
            oidAlgEx.setEx(jexlEngine.createExpression(oidAlgEx.getAlgo()));
        }
    }

    @Override
    public void monitorAll() {

        log.debug(setting.getAssetInfo().getDevId() + "获取监控信息,IP " + setting.getAssetInfo().getDevIp() + ", 时间:" + TimeTools.format2(new Date()));
        if (!getConnectedStatus()) {
            return;
        }

        if (!snmpOpen) {
            return;
        }

        // try catch 保证上面的连通监控正常运行
        try {
            if (!testSnmp()) {
                return;
            }

            Map<String, List<OidAlgEx>> algGroup = setting.getOidAlgs().stream().collect(Collectors.groupingBy(OidAlgEx::getIndicatorType));
            algGroup.forEach(this::executeAlg);
        } catch (Exception e) {
            log.error("", e);
        }

    }

    private void executeAlg(String indicatorType, List<OidAlgEx> oidAlgs) {
        //数据
        Map<String, Object> data = new LinkedHashMap<>();
        //单位
        Map<String, Object> dataUnit = new LinkedHashMap<>();
        Map<String, Object> dataTitle = new LinkedHashMap<>();
        Map<String, Object> memoryFromDiff = new HashMap<>();

        for (OidAlgEx oidAlgEx : oidAlgs) {
            if (StringUtils.isBlank(oidAlgEx.getAlgo()) || oidAlgEx.getRealQuery() == 1) {
                continue;
            }

            //是否在线有特殊的处理
            if ("reachable".equals(oidAlgEx.getIndicatorField())) {
                continue;
            }

            dataUnit.put(oidAlgEx.getIndicatorField(), oidAlgEx.getUnit());
            dataTitle.put(oidAlgEx.getIndicatorField(), oidAlgEx.getIndicatorName());

            if (oidAlgEx.getAlgo().startsWith("Algo.")) {
                boolean hasMemoryCache = (oidAlgEx.getAlgo().contains("memoryFromDisk") || oidAlgEx.getAlgo().contains("memoryUSG400"));
                if (hasMemoryCache && memoryFromDiff.containsKey(oidAlgEx.getIndicatorField())) {
                    data.put(oidAlgEx.getIndicatorField(), memoryFromDiff.get(oidAlgEx.getIndicatorField()));
                    continue;
                }

                //算法示例, 内存算法: Algo.memoryFromDisk(a), 进程列表算法: Algo.list(a), 常规算法: Algo.cal(a,'(v1+v2)/1024')
                Object val = execute(oidAlgEx);
                if (oidAlgEx.getAlgo().contains("memoryFromDisk") || oidAlgEx.getAlgo().contains("memoryUSG400")) {
                    //1.从磁盘信息中获取内存信息只需要获取一次,其他内存指标一并获取了
                    //2.USG400类型资产获取内存信息只需要获取一次,其他内存指标一并获取了
                    memoryFromDiff.putAll((Map) val);
                    data.put(oidAlgEx.getIndicatorField(), memoryFromDiff.get(oidAlgEx.getIndicatorField()));
                } else {
                    if (oidAlgEx.getAlgo().startsWith("Algo.disk")) {
                        //计算总量
                        Optional<DiskPartionInfo> partionTotalInfo = ((List<DiskPartionInfo>) val).stream().filter(f -> !notPartion.contains(f.getName().toLowerCase())).reduce((a, b) -> {
                            a.setTotal(a.getTotal() + b.getTotal());
                            a.setUsed(a.getUsed() + b.getUsed());
                            return a;
                        });
                        if (partionTotalInfo.isPresent()) {
                            data.put("disk_total", partionTotalInfo.get().getTotal());
                            data.put("disk_used", partionTotalInfo.get().getUsed());
                        }
                    }
                    else if("memory".equals(oidAlgEx.getIndicatorType())){
                        val=val.toString().replace("M","");
                    }

                    try {
                        data.put(oidAlgEx.getIndicatorField(), val instanceof List ? OBJECT_MAPPER.writeValueAsString(val) : val);
                    } catch (JsonProcessingException e) {
                        log.error("", e);
                    }
                }
            } else {
                //通用get逻辑
                Object valueFromOid = common(oidAlgEx.getOid());
                data.put(oidAlgEx.getIndicatorField(), valueFromOid);
            }

        }

        MonitorDataInfo monitorDataInfo = new MonitorDataInfo(MONITOR_DATA_INDEX + indicatorType, MONITOR_DATA_TOPIC + indicatorType, new HashMap<>(4));
        monitorDataInfo.kv("dev_id", setting.getAssetInfo().getDevId()).kv("dev_ip", setting.getAssetInfo().getDevIp()).kv("index", MONITOR_DATA_INDEX + indicatorType).kv("event_time", TimeTools.format2(new Date()));
        monitorDataInfo.kv("dev_type", setting.getAssetInfo().getAssetType());
        monitorDataInfo.kv("sno", setting.getAssetInfo().getSnoUnicode());
        try {
            //monitorDataInfo.kv("monitor_data", OBJECT_MAPPER.writeValueAsString(data));
            monitorDataInfo.kvAll(data);
            monitorDataInfo.kv("units", OBJECT_MAPPER.writeValueAsString(dataUnit));
            monitorDataInfo.kv("tips", OBJECT_MAPPER.writeValueAsString(dataTitle));
            MonitorRunnerV2.pushMonitorData(monitorDataInfo);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }
    }

}
