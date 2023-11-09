package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.util.CommonTools;
import com.vrv.vap.admin.common.util.TimeTools;
import com.vrv.vap.admin.model.NetworkMonitor;
import com.vrv.vap.admin.service.NetworkMonitorReportService;
import com.vrv.vap.admin.service.NetworkMonitorService;
import com.vrv.vap.admin.service.NetworkMonitorStatusService;
import com.vrv.vap.admin.vo.NetworkMonitorQuery;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import java.util.*;
import java.util.stream.Collectors;


/**
 * @author lilang
 * @date 2021/8/10
 * @description 网络监视器控制器
 */
@RestController
@RequestMapping(path = "/networkMonitor")
public class NetworkMonitorController extends ApiController {

    @Autowired
    NetworkMonitorService networkMonitorService;

    @Autowired
    NetworkMonitorReportService networkMonitorReportService;

    @Autowired
    NetworkMonitorStatusService networkMonitorStatusService;

    //已删除
    private static final Integer STATUS_DEL = 1;
    // 手工录入
    private static final Integer REGTYPE_INPUT = 0;

    @Value("${vap.zjg.monitor.reportInterval:5}")
    private Integer interval;

    private static Map<String, Object> transferMap = new HashMap<>();
    static {
        transferMap.put("networkMonitorStatus", "{\"0\":\"异常\", \"1\":\"正常\"}");
        transferMap.put("regType", "{\"0\":\"手工录入\", \"1\":\"在线注册\"}");
    }

    @GetMapping
    @ApiOperation("获取所有监视器")
    @SysRequestLog(description = "获取所有监视器", actionType = ActionType.SELECT)
    public Result getAllMonitor() {
        List<NetworkMonitor> monitorList = networkMonitorService.findAll();
        List<NetworkMonitor> resultList = monitorList.stream().filter(networkMonitor -> networkMonitor.getStatus() == 0).collect(Collectors.toList());
        return this.vData(resultList);
    }

    @PostMapping
    @ApiOperation("查询监视器")
    @SysRequestLog(description = "查询监视器", actionType = ActionType.SELECT)
    public VList<NetworkMonitor> queryNetworkMonitor(@RequestBody NetworkMonitorQuery networkMonitorQuery) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(networkMonitorQuery, NetworkMonitor.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("status","0");
        example.and(criteria);
        return this.vList(networkMonitorService.findByExample(example));
    }

    @PutMapping
    @ApiOperation("添加监视器")
    @SysRequestLog(description = "添加监视器", actionType = ActionType.ADD)
    public VData<NetworkMonitor> addNetworkMonitor(@RequestBody NetworkMonitor networkMonitor) {
        networkMonitor.setDeviceId(CommonTools.generateId());
        if (networkMonitor.getStatus() == null) {
            networkMonitor.setStatus(0);
        }
        networkMonitor.setNetworkMonitorStatus(0);
        networkMonitor.setRegType(REGTYPE_INPUT);
        int result = networkMonitorService.save(networkMonitor);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(networkMonitor,"添加监视器",transferMap);
        }
        return this.vData(networkMonitor);
    }

    @PatchMapping
    @ApiOperation("修改监视器")
    @SysRequestLog(description = "修改监视器", actionType = ActionType.UPDATE)
    public Result updateNetworkMonitor(@RequestBody NetworkMonitor networkMonitor) {
        Integer id = networkMonitor.getId();
        if (id == null) {
            return this.result(false);
        }
        NetworkMonitor networkMonitorSec = networkMonitorService.findById(id);
        int result = networkMonitorService.updateSelective(networkMonitor);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(networkMonitorSec,networkMonitor,"修改监视器",transferMap);
        }
        return this.result(result == 1);
    }

    @DeleteMapping
    @ApiOperation("删除监视器")
    @SysRequestLog(description = "删除监视器", actionType = ActionType.DELETE)
    public Result deleteNetworkMonitor(@RequestBody DeleteQuery param) {
        int result = 0;
        String ids = param.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<NetworkMonitor> monitorList = networkMonitorService.findByids(ids);
        String[] idsArr = ids.split(",");
        for (String id : idsArr) {
            NetworkMonitor networkMonitor = networkMonitorService.findById(Integer.valueOf(id));
            networkMonitor.setStatus(STATUS_DEL);
            networkMonitorService.updateSelective(networkMonitor);
            result++;
        }
        if (result > 0) {
            monitorList.forEach(networkMonitor -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(networkMonitor,"删除监视器",transferMap);
            });
        }
        return this.result(result >= 1);
    }

    @PostMapping(path = "/onlineStatus")
    @ApiOperation("查询监视器在线状态")
    @SysRequestLog(description = "查询监视器在线状态", actionType = ActionType.SELECT)
    public Result queryOnlineStatus(@RequestBody NetworkMonitorQuery networkMonitorQuery) {
        String deviceId = networkMonitorQuery.getDeviceId();
        if (StringUtils.isEmpty(deviceId)) {
            return new Result("-1","设备ID为空");
        }
        List<NetworkMonitor> networkMonitorList = networkMonitorService.findByProperty(NetworkMonitor.class,"deviceId",deviceId);
        if (CollectionUtils.isNotEmpty(networkMonitorList)) {
            NetworkMonitor monitorReport = networkMonitorList.get(0);
            Map<String, Object> statusMap = new HashMap<>();
            Integer netStatus = 0;
            if(monitorReport.getReportTime() != null) {
                Date reportTime = monitorReport.getReportTime();
                Date onlineTime = TimeTools.getNowBeforeByMinute(interval);
                if (onlineTime.getTime() < reportTime.getTime()) {
                    netStatus = 1;
                }
            }
            statusMap.put("status", netStatus);
            if (netStatus != monitorReport.getNetworkMonitorStatus()) {
                monitorReport.setNetworkMonitorStatus(netStatus);
                networkMonitorService.update(monitorReport);
            }
            return this.vData(statusMap);
        }
        return new Result("-1","未找到对应设备");


    }

    @GetMapping(path = "/count")
    @ApiOperation("获取监视器数量")
    @SysRequestLog(description = "获取监视器数量", actionType = ActionType.SELECT)
    public VData getMonitorCount() {
        Map map = new HashMap();
        int count = 0;
        List<NetworkMonitor> monitorList = networkMonitorService.findAll();
        List<NetworkMonitor> resultList = monitorList.stream().filter(networkMonitor -> networkMonitor.getStatus() == 0).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(resultList)) {
            count = resultList.size();
        }
        map.put("monitorCount",count);
        return this.vData(map);
    }

    @GetMapping(path = "/countByStatus")
    @ApiOperation("获取在线离线数量")
    @SysRequestLog(description = "获取在线离线数量", actionType = ActionType.SELECT)
    public VData getMonitorCountByStatus() {
        Map map = new HashMap();
        List<NetworkMonitor> monitorList = networkMonitorService.findAll();
        List<NetworkMonitor> resultList = monitorList.stream().filter(networkMonitor -> networkMonitor.getStatus() == 0).collect(Collectors.toList());
        List<NetworkMonitor> onlineList;
        List<NetworkMonitor> offlineList;
        if (CollectionUtils.isNotEmpty(resultList)) {
            onlineList = resultList.stream().filter(p -> p.getNetworkMonitorStatus() == 1).collect(Collectors.toList());
            offlineList = resultList.stream().filter(p -> p.getNetworkMonitorStatus() == 0).collect(Collectors.toList());
            map.put("onlineCount",onlineList.size());
            map.put("offlineCount",offlineList.size());
            map.put("totalCount",resultList.size());
        } else {
            map.put("onlineCount",0);
            map.put("offlineCount",0);
            map.put("totalCount",0);
        }
        return this.vData(map);
    }
}
