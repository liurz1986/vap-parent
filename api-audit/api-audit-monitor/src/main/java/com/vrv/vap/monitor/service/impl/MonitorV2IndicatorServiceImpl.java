package com.vrv.vap.monitor.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.vrv.vap.monitor.command.MonitorRunnerV2;
import com.vrv.vap.monitor.entity.Monitor2Indicator;
import com.vrv.vap.monitor.mapper.Monitor2IndicatorMapper;
import com.vrv.vap.monitor.model.es.MonitorData;
import com.vrv.vap.monitor.service.MonitorV2IndicatorService;
import com.vrv.vap.monitor.vo.Monitor2DataQuery;
import com.vrv.vap.monitor.vo.Monitor2IndicatorQuery;
import com.vrv.vap.toolkit.plugin.util.QueryWrapperUtil;
import com.vrv.vap.toolkit.tools.TimeTools;
import com.vrv.vap.toolkit.vo.Query;
import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.toolkit.vo.VList;
import com.vrv.vap.toolkit.vo.VoBuilder;
import lombok.Synchronized;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

@Service
public class MonitorV2IndicatorServiceImpl implements MonitorV2IndicatorService {

    private static final Log log = LogFactory.getLog(MonitorV2IndicatorServiceImpl.class);

    @Autowired
    private Monitor2IndicatorMapper monitor2IndicatorMapper;

    private final List<Monitor2Indicator> monitor2Indicators = new ArrayList<>();

    @Transactional(rollbackFor = Exception.class)
    @Synchronized
    @Override
    public int addItem(Monitor2Indicator monitor2Indicator) {
        setSupportView(monitor2Indicator);
        monitor2Indicator.setUnit(monitor2Indicator.getUnit() == null ? "" : monitor2Indicator.getUnit());
        int res = monitor2IndicatorMapper.insert(monitor2Indicator);
        if (res == 1 && monitor2Indicator.getAvailable() == 1) {
            syncEsTemplate(monitor2Indicator);
        }
        return res;
    }

    private void syncEsTemplate(Monitor2Indicator monitor2Indicator) {
        //同步es模板修改
        MonitorRunnerV2.syncEsTemplate(Collections.singletonList(monitor2Indicator));
        refreshMonitor2IndicatorsCache();
    }

    private void setSupportView(Monitor2Indicator monitor2Indicator) {
        switch (monitor2Indicator.getDataType()) {
            case "num":
                monitor2Indicator.setSupportView("val_p,trend,val_t");
                break;
            case "text":
                monitor2Indicator.setSupportView("val_p,val_t");
                break;
            case "list":
                monitor2Indicator.setSupportView("list,table");
                break;
            case "disk":
                monitor2Indicator.setSupportView("disk");
                break;
            case "other":
                monitor2Indicator.setSupportView("val_p,val_t");
                break;
            default:
                break;
        }
    }

    @Override
    public int updateItem(Monitor2Indicator monitor2Indicator) {
        setSupportView(monitor2Indicator);
        monitor2Indicator.setUnit(monitor2Indicator.getUnit() == null ? "" : monitor2Indicator.getUnit());
        int res = monitor2IndicatorMapper.updateById(monitor2Indicator);
        if (res == 1) {
            //refreshMonitor2IndicatorsCache();
            syncEsTemplate(monitor2Indicator);
        }
        return res;
    }

    @Override
    public int deleteItem(Monitor2Indicator monitor2Indicator) {
        int res = monitor2IndicatorMapper.deleteById(monitor2Indicator.getId());
        if (res == 1) {
            refreshMonitor2IndicatorsCache();
        }
        return res;
    }

    @Override
    public Monitor2Indicator querySingle(Monitor2Indicator monitor2Indicator) {
        return monitor2IndicatorMapper.selectById(monitor2Indicator.getId());
    }

    @Override
    public VList<Monitor2Indicator> queryByPage(Query record) {
        if (record.getOrder() == null) {
            record.setOrder("indicator_type,indicator_name");
            record.setBy("asc");
        }
        Page<Monitor2Indicator> page = new Page<>(record.getCurrentPage(), record.getMyCount());
        QueryWrapper<Monitor2Indicator> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, record);
        return VoBuilder.vl(monitor2IndicatorMapper.selectPage(page, queryWrapper));
    }

    @Override
    public VData<List<Monitor2Indicator>> queryAll(Query query) {
        if (query.getOrder() == null) {
            query.setOrder("indicator_name");
            query.setBy("asc");
        }
        QueryWrapper<Monitor2Indicator> queryWrapper = new QueryWrapper<>();
        QueryWrapperUtil.convertQuery(queryWrapper, query);
        return VoBuilder.vd(monitor2IndicatorMapper.selectList(queryWrapper));
    }

    @Override
    public List<Monitor2Indicator> getMonitor2Indicators() {
        if (monitor2Indicators.isEmpty()) {
            monitor2Indicators.addAll(queryAll(new Monitor2IndicatorQuery()).getData());
        }
        return monitor2Indicators;
    }

    @Override
    public Map<String, Object> monitorExample(Monitor2DataQuery monitor2DataQuery) {
        Map<String, Object> res = new HashMap<>();
        res.put("remark", "请按实际指标调整: monitor_data为采集的数据, units为显示的单位(例如G,%,个等,没有则填空串)");

        Boolean trend = false;
        Boolean list = false;
        boolean disk = false;
        boolean table = false;
        String viewType = monitor2DataQuery.getViewType().split("_")[0];

        switch (viewType) {
            case "trend":
                trend = true;
                break;
            case "list":
                list = true;
                break;
            case "disk":
                disk = true;
                break;
            case "table":
                table = true;
                break;
            default:
                break;
        }

        List<String> indicators = Arrays.asList(monitor2DataQuery.getIndicators().split(","));
        List<Monitor2Indicator> monitor2Indicators = getMonitor2Indicators().stream().filter(f -> indicators.contains(f.getIndicatorField())).collect(Collectors.toList());
        List<MonitorData> tempData = new ArrayList<>();
        Random random = new Random();
        if (!trend) {
            MonitorData single = new MonitorData();
            single.setMonitorData(new HashMap<>());
            //single.setDevIp("192.168.119.1");
            //single.setDevId("test");
            //single.setDevType("test");
            //single.setSno("test");
            single.setEventTime(TimeTools.format2(TimeTools.getNow()));

            for (Monitor2Indicator r : monitor2Indicators) {
                single.getTip().put(r.getIndicatorField(), r.getIndicatorName());
                single.getUnit().put(r.getIndicatorField(), guessUnit(r));
                if (list || table) {
                    String listJson = String.format("[\"%s%s\",\"%s%s\",\"%s%s\"]", r.getIndicatorName(), 1, r.getIndicatorName(), 2, r.getIndicatorName(), 3);
                    single.getMonitorData().put(r.getIndicatorField(), listJson);
                } else if (disk) {
                    String diskJson = "[{\"name\":\"/home\",\"total\":79.97,\"used\":0.23,\"unit\":\"G\"},{\"name\":\"/run\",\"total\":23.75,\"used\":0.2,\"unit\":\"G\"},{\"name\":\"/\",\"total\":203.10,\"used\":35.6,\"unit\":\"G\"}]";
                    single.getMonitorData().put(r.getIndicatorField(), diskJson);
                } else {
                    String val;
                    if ("double".equals(r.getEsType())) {
                        val = String.valueOf(random.nextInt(100) + (double) random.nextInt(100) / 100);
                    } else if ("long".equals(r.getEsType())) {
                        val = String.valueOf(random.nextInt(100));
                    } else {
                        val = "测试数据";
                    }
                    single.getMonitorData().put(r.getIndicatorField(), val);
                }
            }

            tempData.add(single);
        } else {
            Date[] range = {TimeTools.getNow(), TimeTools.getNowBeforeByMinute(1), TimeTools.getNowBeforeByMinute(2)};
            for (Date time : range) {
                MonitorData single = new MonitorData();
                single.setMonitorData(new HashMap<>());
                //single.setDevIp("192.168.119.1");
                //single.setDevId("test");
                //single.setDevType("test");
                //single.setSno("test");
                single.setEventTime(TimeTools.format2(time));
                for (Monitor2Indicator r : monitor2Indicators) {
                    single.getTip().put(r.getIndicatorField(), r.getIndicatorName());
                    single.getUnit().put(r.getIndicatorField(), guessUnit(r));
                    single.getMonitorData().put(r.getIndicatorField(), String.valueOf(random.nextInt(100) + (double) random.nextInt(100) / 100));
                }
                tempData.add(single);
            }
        }

        res.put("list", tempData);
        return res;
    }

    private void refreshMonitor2IndicatorsCache() {
        synchronized (monitor2Indicators) {
            monitor2Indicators.clear();
            monitor2Indicators.addAll(queryAll(new Monitor2IndicatorQuery()).getData());
        }
    }

    private String guessUnit(Monitor2Indicator r) {
        if (r.getUnit() != null) {
            return r.getUnit();
        }
        String indicatorField = r.getIndicatorField();
        if (indicatorField.contains("memory") || indicatorField.contains("disk")) {
            return "G";
        } else if (indicatorField.contains("percent")) {
            return "%";
        } else {
            return "";
        }
    }
}
