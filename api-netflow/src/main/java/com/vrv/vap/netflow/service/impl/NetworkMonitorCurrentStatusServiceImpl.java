package com.vrv.vap.netflow.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.netflow.component.ESManager;
import com.vrv.vap.netflow.model.NetworkMonitorCurrentStatus;
import com.vrv.vap.netflow.service.NetworkMonitorCurrentStatusService;
import org.apache.http.util.EntityUtils;
import org.elasticsearch.client.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * @author sj
 * @date 2023/10/07
 * @description 网络监视器实现类
 */
@Service
@Transactional
public class NetworkMonitorCurrentStatusServiceImpl extends BaseServiceImpl<NetworkMonitorCurrentStatus> implements NetworkMonitorCurrentStatusService {
    private final Logger logger = LoggerFactory.getLogger(NetworkMonitorCurrentStatusServiceImpl.class);
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    @Override
    public List<Map> getMonitorStatuses(String device_id, int pageSize) {
        String json = "{\"from\":0,\"size\":" + pageSize + ",\"query\":{\"match\":{\"device_id\":\"" + device_id + "\"}},\"sort\":[{\"time\":\"desc\"}]}";
        try {
            // _index/_type
            Response response = ESManager.search("network-monitor-status/_search", json);
            if (response != null) {
                String result=  EntityUtils.toString(response.getEntity(), "utf-8");
                Map resultMap=gson.fromJson(result,Map.class);
                if(resultMap!=null&&resultMap.containsKey("hits")){


                    if(resultMap.get("hits")!=null&&((Map)resultMap.get("hits")).containsKey("hits")){

                        List<Map> list=(List<Map>)(((Map)resultMap.get("hits")).get("hits"));

                        List<Map> listSource=new LinkedList<>();
                        list.forEach(item->{

                            if(item.containsKey("_source")){

                                listSource.add(0,(Map)(item.get("_source")));
                            }
                        });
                        return listSource;
                    }
                }
            }
        } catch (Exception e) {
            logger.error("es查询失败", e);
        }
        return null;
    }

    @Override
    public NetworkMonitorCurrentStatus getCurrentStatus(String device_id) {
        List<NetworkMonitorCurrentStatus> logList = this.findByProperty(NetworkMonitorCurrentStatus.class, "deviceId", device_id);
        if (logList != null && !logList.isEmpty()) {
            return logList.get(0);
        }
        return null;
    }

    //   @Override
    public Map getLastStatus(String device_id) {
        List<Map> list = this.getMonitorStatuses(device_id, 1);
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
