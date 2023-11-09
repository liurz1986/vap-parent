package com.vrv.vap.netflow.service.impl;


import com.alibaba.fastjson.JSONArray;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.netflow.model.NetworkMonitor;
import com.vrv.vap.netflow.service.NetworkMonitorService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author lilang
 * @date 2021/8/10
 * @description 网络监视器实现类
 */
@Service
@Transactional
public class NetworkMonitorServiceImpl extends BaseServiceImpl<NetworkMonitor> implements NetworkMonitorService {

    @Override
    public void deleteByDeviceId(String deviceId) {
        List<NetworkMonitor> monitorList = this.findByProperty(NetworkMonitor.class, "deviceId", deviceId);
        if (CollectionUtils.isNotEmpty(monitorList)) {
            NetworkMonitor networkMonitor = monitorList.get(0);
            this.deleteById(networkMonitor.getId());
        }
    }

    @Override
    public String getValueFromMapStringByKey(String key, String original) {
        if (StringUtils.isEmpty(original)) {
            return "";
        }
        String val = "";
        JSONArray jsonArray = JSONArray.parseArray(original);
        List<Map> javaList = jsonArray.toJavaList(Map.class);
        Iterator<Map> iterator = javaList.iterator();
        while (iterator.hasNext()) {
            Map next = iterator.next();
            val = (String) next.get(key);
            break;
        }
        return val;
    }

    @Override
    public NetworkMonitor getLastItem(String deviceId) {
        List<NetworkMonitor> logList = this.findByProperty(NetworkMonitor.class, "deviceId", deviceId);
        if(logList!=null&&!logList.isEmpty()){
            logList.sort(Comparator.comparing(NetworkMonitor::getReportTime).reversed());
            return logList.get(0);
        }

        return null;
    }
}
