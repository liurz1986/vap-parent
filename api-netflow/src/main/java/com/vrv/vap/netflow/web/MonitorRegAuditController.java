package com.vrv.vap.netflow.web;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Query;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.netflow.common.batch.BatchQueue;
import com.vrv.vap.netflow.mapper.CollectorDataAccessMapper;
import com.vrv.vap.netflow.mapper.NetworkMonitorAuditedMapper;
import com.vrv.vap.netflow.model.NetworkMonitor;
import com.vrv.vap.netflow.model.NetworkMonitorAudited;
import com.vrv.vap.netflow.model.NetworkMonitorCurrentStatus;
import com.vrv.vap.netflow.model.NetworkMonitorRegAuditLog;
import com.vrv.vap.netflow.service.*;
import com.vrv.vap.netflow.service.impl.MonitorLogServiceImpl;
import com.vrv.vap.netflow.service.kafka.KafkaSenderService;
import com.vrv.vap.netflow.utils.HttpUtil;
import com.vrv.vap.netflow.vo.NetworkMonitorCurrentStatusVO;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

/**
 * @author sj
 * @date 2023/10/07
 * @description
 */
@RestController
@RequestMapping(path = "/monitor_reg_audit")
public class MonitorRegAuditController extends ApiController {
    private final Logger logger = LoggerFactory.getLogger(MonitorRegAuditController.class);
    private static final String KAFKA_MONITOR_STATUS_TOPIC = "monitor_status_topic";
    @Autowired
    BatchQueue<Map<String, Object>> batchQueue;
    @Autowired
    MonitorLogService monitorLogService;
    @Resource
    CollectorDataAccessMapper collectorDataAccessMapper;
    @Resource
    private KafkaSenderService kafkaSenderService;
    @Resource
    private NetworkMonitorService networkMonitorService;

    @Resource
    private NetworkMonitorAuditedService networkMonitorAuditedService;

    @Resource
    private NetworkMonitorCurrentStatusService networkMonitorCurrentStatusService;
    @Resource
    private NetworkMonitorRegAuditLogService networkMonitorRegAuditLogService;


    @Resource
    private NetworkMonitorAuditedMapper monitorAuditedMapper;
    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
    private static Type diskListType = new TypeToken<List<MonitorLogServiceImpl.DiskInfo>>() {
    }.getType();
    private static Type cpuListType = new TypeToken<List<MonitorLogServiceImpl.RegStatusCpuDeatil>>() {
    }.getType();


    //
    @ApiOperation("待审核注册数量查询")
    @PostMapping(path = "/get_unaudit_count")
    @ResponseBody
    public Result getUnauditCount() {
        return this.vData(monitorAuditedMapper.getUnauditCount());
    }


    // 已审核监测器列表接口(不分页)
    @ApiOperation("已审核监测器列表接口(不分页)")
    @PostMapping(path = "/get_audited_list")
    @ResponseBody
    public Result getAuditedList() {
        List<NetworkMonitorCurrentStatus> list = networkMonitorCurrentStatusService.findAll();
        List<NetworkMonitorAudited> auditedList =  networkMonitorAuditedService.findAll();
        List<NetworkMonitorCurrentStatusVO>  result=new ArrayList<>();

        for (NetworkMonitorAudited audited : auditedList) {
            NetworkMonitorCurrentStatusVO item =new NetworkMonitorCurrentStatusVO();
            item.setDeviceId(audited.getDeviceId());
            item.setApiKey(audited.getApiKey());
            item.setDeviceStatus(0);//默认异常
            item.setIp(networkMonitorService.getValueFromMapStringByKey("ip", audited.getInterfaceInfo()));


            for (NetworkMonitorCurrentStatus status : list){
                if(status.getDeviceId().equals(audited.getDeviceId())){
                    BeanUtils.copyProperties(status,item,"apiKey");
                    break;
                }
            }
            result.add(item);
        }
        return this.vData(result);
    }


    // 已审核监测器详情查询

    @ApiOperation("已审核监测器详情查询")
    @PostMapping(path = "/reg_audited_info")
    @ResponseBody
    public Result getRegAuditedInfo(@RequestBody Map map) {

        String device_id = map.get("device_id").toString();

        List<NetworkMonitorAudited> monitorList = networkMonitorAuditedService.findByProperty(NetworkMonitorAudited.class, "deviceId", device_id);
        if (monitorList.isEmpty()) {
            return this.vData(false);
        } else {
            return this.vData(monitorList.get(0));
        }
    }

    @Data
    private class RegCpuDetail {
        public Integer physical_id;
        public Integer core;
        public Float clock;
    }


    @ApiOperation("已审核监测器CPU详情查询")
    @PostMapping(path = "/reg_audited_cpu_info")
    @ResponseBody
    public Result getRegAuditedCpuInfo(@RequestBody Map map) {

        String device_id = map.get("device_id").toString();

        List<NetworkMonitorAudited> monitorList = networkMonitorAuditedService.findByProperty(NetworkMonitorAudited.class, "deviceId", device_id);
        if (monitorList.isEmpty()) {
            return this.vData(false);
        } else {
            NetworkMonitorAudited networkMonitorAudited = monitorList.get(0);
            Map<String, Object> result = new HashMap<>();

            Type cpuListType = new TypeToken<List<RegCpuDetail>>() {
            }.getType();

            List<RegCpuDetail> cpus = gson.fromJson(networkMonitorAudited.getCpuInfo(), cpuListType);

            result.put("coreTotal", cpus.stream().mapToInt(RegCpuDetail::getCore).sum());
            result.put("physicalTotal", cpus.size());
            result.put("detail", cpus);


            return this.vData(result);
        }
    }


    @ApiOperation("已审核监测器当前运行状态")
    @PostMapping(path = "/reg_audited_current_status")
    @ResponseBody
    public Result getRegCurrentStatus(@RequestBody Map map) {

        String device_id = map.get("device_id").toString();

        NetworkMonitorCurrentStatus lastStatus = networkMonitorCurrentStatusService.getCurrentStatus(device_id);
        if (lastStatus == null) {
            return this.vData(false);
        } else {
            return this.vData(lastStatus);
        }
    }

    @Data
    public class NameValueItem {

        private String name;

        private Object value;
    }


    @ApiOperation("注册器cpu使用率折线图")
    @PostMapping(path = "/reg_cpu_usage_lines")
    @ResponseBody
    public Result getRegCpuUsageLines(@RequestBody Map map) {
        String device_id = map.get("device_id").toString();

        List<Map> list = networkMonitorCurrentStatusService.getMonitorStatuses(device_id, 12);
        logger.error(gson.toJson(list));
        List<Map> resultlist = new ArrayList<>();


        if (list != null) {

            for (Map item : list) {
                List<MonitorLogServiceImpl.RegStatusCpuDeatil> cpus = gson.fromJson(item.get("cpu").toString(), cpuListType);

                for (MonitorLogServiceImpl.RegStatusCpuDeatil cpuInfo : cpus) {
                    Map<String, Object> result = null;
                    List<NameValueItem> data = null;
                    for (Map resultitem : resultlist) {
                        if (resultitem.get("name").toString().equals("physical_id:" + cpuInfo.getPhysical_id())) {
                            result = resultitem;
                            break;
                        }
                    }

                    if (result == null) {
                        result = new HashMap<>();
                        resultlist.add(result);
                    }

                    if (result.containsKey("data")) {
                        data = (List<NameValueItem>) result.get("data");
                    } else {
                        data = new ArrayList<>();
                        result.put("name", "physical_id:" + cpuInfo.getPhysical_id());
                        result.put("data", data);
                    }

                    NameValueItem nameValueItem = new NameValueItem();
                    nameValueItem.name = item.get("time").toString();
                    nameValueItem.value = cpuInfo.getCpu_usage();
                    data.add(nameValueItem);
                }
            }
        }

        return this.vData(resultlist);
    }

    @ApiOperation("注册器mem使用率折线图")
    @PostMapping(path = "/reg_mem_usage_lines")
    @ResponseBody
    public Result getRegMemUsageLines(@RequestBody Map map) {
        String device_id = map.get("device_id").toString();
        List<Map> resultlist = getMonitorStatusLines(device_id, "内存", (Map item) -> {
            NameValueItem nameValueItem = new NameValueItem();
            nameValueItem.name = item.get("time").toString();
            nameValueItem.value = item.get("mem").toString();
            return nameValueItem;
        });
        return this.vData(resultlist);
    }


    @ApiOperation("注册器disk使用率折线图")
    @PostMapping(path = "/reg_disk_usage_lines")
    @ResponseBody
    public Result getRegDiskUsageLines(@RequestBody Map map) {
        String device_id = map.get("device_id").toString();

        NetworkMonitorAudited networkMonitorAudited = networkMonitorAuditedService.getItem(device_id);

        List<MonitorLogServiceImpl.DiskInfo> disks = gson.fromJson(networkMonitorAudited.getDiskInfo(), diskListType);
        double total = 0;
        for (MonitorLogServiceImpl.DiskInfo disk : disks
        ) {
            total += disk.getSize();
        }
        Double finalTotal = total;
        DecimalFormat df = new DecimalFormat("#.00");

        List<Map> resultlist = getMonitorStatusLines(device_id, "磁盘", (Map item) -> {
            NameValueItem nameValueItem = new NameValueItem();
            nameValueItem.name = item.get("time").toString();
            nameValueItem.value = df.format(Double.parseDouble(item.get("disk").toString()) * 100 / (finalTotal));
            return nameValueItem;
        });
        return this.vData(resultlist);
    }


    private List<Map> getMonitorStatusLines(String device_id, String indexName, Function<Map, NameValueItem> fun) {
        List<Map> resultlist = new ArrayList<>();
        Map<String, Object> result = new HashMap<>();

        result.put("name", indexName);
        List<NameValueItem> data = new ArrayList<>();
        List<Map> list = networkMonitorCurrentStatusService.getMonitorStatuses(device_id, 12);
        logger.error(gson.toJson(list));
        if (list != null) {
            for (Map item : list) {
                logger.error(gson.toJson(item));
                NameValueItem nameValueItem = fun.apply(item);
                data.add(nameValueItem);
            }
        }
        if (data.isEmpty()) {

            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            for (int i = 11; i >= 0; i--) {
                NameValueItem nameValueItem = new NameValueItem();

                Date time = DateUtils.addMinutes(new Date(), -i * 10);
                nameValueItem.setName(sdf.format(time));
                nameValueItem.setValue(0d);
                data.add(nameValueItem);
            }
        }
        result.put("data", data);
        resultlist.add(result);
        return resultlist;
    }


    // 已审核监测器列表接口(不分页)
    @ApiOperation("未注册（审批）监测器列表接口(不分页)")
    @PostMapping(path = "/get_unaudit_list")
    @ResponseBody
    public Result getUnAuditList() {

        return this.vData(monitorAuditedMapper.getUnAuditList());
    }

    @ApiOperation("未注册（审批）监测器列表接口(分页)")
    @PostMapping(path = "/get_unaudit_page")
    @ResponseBody
    public Result getUnAuditPage(Query query) {
        List<NetworkMonitor> list = monitorAuditedMapper.getUnAuditList();
        int total = list.size();
        list.stream().skip(query.getStart_()).limit(query.getCount_());

        return this.vList(list, total);
    }


    // 已审核监测器详情查询

    @ApiOperation("未注册（审批）监测器详情查询")
    @PostMapping(path = "/reg_unaudit_info")
    @ResponseBody
    public Result getRegUnAuditInfo(@RequestBody Map map) {

        String device_id = map.get("device_id").toString();

        List<NetworkMonitor> monitorList = networkMonitorService.findByProperty(NetworkMonitor.class, "deviceId", device_id);
        if (monitorList != null && !monitorList.isEmpty()) {
            monitorList.sort(Comparator.comparing(NetworkMonitor::getReportTime).reversed());
            return this.vData(monitorList.get(0));
        } else {
            return this.vData(false);
        }
    }

    @ApiOperation("监测器审核")
    @PostMapping(path = "/reg_audit")
    @ResponseBody
    public Result RegAudit(@RequestBody Map map) {

        String device_ids = map.get("device_ids").toString();
        String result = map.get("result").toString();
        String view = map.get("view").toString();

        if (StringUtils.isEmpty(view)) {
            if ("1".equals(result)) {
                view = "审核通过";
            } else {

                // 报错
                return this.vData(new Result("-1", "审核不通过时，原因必填"));
            }
        }

        for (String device_id : device_ids.split(",")) {
            // 获取最新的注册信息

            NetworkMonitor networkMonitor = networkMonitorService.getLastItem(device_id);
            if (networkMonitor == null) {
                return this.vData(new Result("-1", "未找到注册申请信息"));
            }

            NetworkMonitorRegAuditLog regAuditLog = new NetworkMonitorRegAuditLog();
            regAuditLog.setAuditAccount("审计账户");
            regAuditLog.setAuditAccountName("审核人");
            regAuditLog.setAuditResult(Integer.parseInt(result));
            regAuditLog.setAuditTime(new Date());
            regAuditLog.setDeviceId(networkMonitor.getDeviceId());
            regAuditLog.setRegId(networkMonitor.getId());
            regAuditLog.setMemo(view);

            networkMonitorRegAuditLogService.saveSelective(regAuditLog);

            // 更新  或者  新增审核注册信息

            NetworkMonitorAudited audited = networkMonitorAuditedService.getItem(device_id);
            if (audited == null) {
                audited = new NetworkMonitorAudited();
            }
            // 更新值audited
            BeanUtils.copyProperties(networkMonitor, audited, "id","apiKey");
            if (audited.getId() == null) {
                networkMonitorAuditedService.saveSelective(audited);
            } else {
                networkMonitorAuditedService.update(audited);
            }

            // 更新状态信息
            List<NetworkMonitorCurrentStatus> statuslist = networkMonitorCurrentStatusService.findByids(device_id);
            if (statuslist != null && !statuslist.isEmpty()) {
                NetworkMonitorCurrentStatus regStatus = statuslist.get(0);
                regStatus.setDeviceBelong(audited.getDeviceBelong());
                regStatus.setDeviceId(audited.getDeviceId());
                regStatus.setDeviceLocation(audited.getDeviceLocation());
                regStatus.setDeviceSoftVersion(audited.getDeviceSoftVersion());
                regStatus.setIp(networkMonitorService.getValueFromMapStringByKey("ip", audited.getInterfaceInfo()));
                networkMonitorCurrentStatusService.update(regStatus);
            } else {
                NetworkMonitorCurrentStatus regStatus = new NetworkMonitorCurrentStatus();
                regStatus.setDeviceBelong(audited.getDeviceBelong());
                regStatus.setDeviceId(audited.getDeviceId());
                regStatus.setDeviceLocation(audited.getDeviceLocation());
                regStatus.setDeviceSoftVersion(audited.getDeviceSoftVersion());
                regStatus.setIp(networkMonitorService.getValueFromMapStringByKey("ip", audited.getInterfaceInfo()));

                regStatus.setDeviceCpuUsage(0d);
                regStatus.setDeviceMemUsage(0d);
                regStatus.setDeviceDiskUsage(0d);

                networkMonitorCurrentStatusService.saveSelective(regStatus);
            }
        }

        return this.vData(true);
    }


    @Data
    public class TokenResult {

      private   Integer errno;
        private  String errmsg;
        private  TokenResultData data;

    }

    @Data
    public class TokenResultData {
        private String token;
        private String expire;
    }


    @ApiOperation("监测器免密登录apikey配置")
    @PostMapping(path = "/reg_apikey")
    @ResponseBody
    public Result RegApiKey(@RequestBody Map map) {
        String device_id = map.get("device_id").toString();
        String api_key = map.get("api_key").toString();
        NetworkMonitorAudited audited = networkMonitorAuditedService.getItem(device_id);

        if (audited == null) {
            return this.vData(new Result("-1", "该监测器未审批！"));
        }
        audited.setApiKey(api_key);

        networkMonitorAuditedService.update(audited);


        NetworkMonitorCurrentStatus status = networkMonitorCurrentStatusService.getCurrentStatus(device_id);

        NetworkMonitorCurrentStatusVO item = new NetworkMonitorCurrentStatusVO();
        item.setDeviceId(audited.getDeviceId());
        item.setApiKey(audited.getApiKey());
        item.setDeviceStatus(0);//默认异常
        item.setIp(networkMonitorService.getValueFromMapStringByKey("ip", audited.getInterfaceInfo()));

        if (status != null) {
            BeanUtils.copyProperties(status, item, "apiKey");
        }
        return this.vData(item);
    }


    @ApiOperation("监测器免密登录")
    @PostMapping(path = "/reg_auto_login")
    @ResponseBody
    public Result RegAutoLogin(@RequestBody Map map) {
        String device_id = map.get("device_id").toString();
        String device_ip = map.get("device_ip").toString();

        NetworkMonitorAudited audited = networkMonitorAuditedService.getItem(device_id);

        if (audited == null) {
            return this.vData(new Result("-1", "该监测器未审批！"));
        }

        String apiKey = audited.getApiKey();

        // if("220409011401".equals(device_id)) {
        //     apiKey = "uyXqZxuqeQ1Wsmdy";
        // }

        if (StringUtils.isEmpty(apiKey)) {
            return this.vData(new Result("-1", "该监测器未配置apiKey！"));
        }

        logger.error("apiKey:" + apiKey);

        // 获取token
        String url = "https://" + device_ip + "/api/authorize";
        try {

            Map<String, Object> params = new HashMap<>();


            String json = JSONObject.toJSONString(params);
            logger.info("json:{}", json);

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json;charset=UTF-8");
            headers.put("apiKey", apiKey);

            String post = HttpUtil.POST(url, headers, json);
            if (StringUtils.isEmpty(post)) {
                return this.vData(new Result("-1", "调用post请求异常：网络连接失败！"));
            } else {
                logger.error(post);
            }

            //{"errno":0,"errmsg":"","data":{"token":"8fa6811050b5ade352f476cf407de3ee","expire":30}}


            TokenResult tokenResult = gson.fromJson(post, TokenResult.class);
            if (tokenResult.getErrno() == 0) {
                String address = System.getenv("LOCAL_SERVER_IP");

                // 组装url
                String token = tokenResult.getData().token;
                String autourl = "https://{ip}/login.php?action=autologin&token={token}&ip={server_ip}";

                autourl = autourl.replace("{ip}", device_ip);
                autourl = autourl.replace("{token}", token);
                autourl = autourl.replace("{server_ip}", address);
                // 返回结果

                return this.vData(autourl);
            } else {
                return this.vData(new Result("-1", tokenResult.getErrmsg()));
            }


        } catch (Exception e) {
            //  throw new RuntimeException("调用post请求异常！", e);
            logger.error("调用post请求异常！", e);
            return this.vData(new Result("-1", "调用post请求异常！"));
            // return this.vData("https://www.baidu.com/");
        }

    }


}