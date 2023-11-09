package com.vrv.vap.admin.web;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.util.HTTPUtil;
import com.vrv.vap.admin.model.CascadePlatform;
import com.vrv.vap.admin.model.PlatformStatusInfo;
import com.vrv.vap.admin.service.CascadePlatformService;
import com.vrv.vap.admin.service.HardwareService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.*;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.DeleteQuery;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.common.vo.VList;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
* @BelongsPackage com.vrv.vap.admin.web
* @Author CodeGenerator
* @CreateTime 2021/03/26
* @Description (CascadePlatform相关接口)
* @Version
*/
@RestController
@Api(value = "级联平台操作")
@RequestMapping("/cascade")
public class CascadePlatformController extends ApiController {
    private static Logger logger = LoggerFactory.getLogger(CascadePlatformController.class);

    @Autowired
    private CascadePlatformService cascadePlatformService;

    @Autowired
    HardwareService hardwareService;

    private static   ObjectMapper objectMapper = new ObjectMapper();

    private static Map<String, Object> transferMap = new HashMap<>();

    static {
        transferMap.put("local", "{\"0\":\"下级平台\",\"1\":\"本机平台\"}");
        transferMap.put("productType","{\"0\":\"是\",\"1\":\"否\"}");
        transferMap.put("regStatus","{\"0\":\"失败\",\"1\":\"成功\"}");
    }

    /**
    * 获取所有数据
    */
    @ApiOperation(value = "获取所有级联平台信息")
    @GetMapping
    public VData< List<CascadePlatform>> getAllCascadePlatform() {
        List<CascadePlatform> list = cascadePlatformService.findAll();
        list = list.stream().filter(p->p.getLocal()==0).collect(Collectors.toList());
        return this.vData(list);
    }

    /**
    * 添加下级平台
    **/
    @ApiOperation(value = "添加下级平台")
    @PutMapping
    @SysRequestLog(description = "添加下级平台", actionType = ActionType.ADD)
    public VData addCascadePlatform(@RequestBody CascadePlatform cascadePlatform) {
        cascadePlatform.setToken(UUID.randomUUID().toString());
        cascadePlatform.setLocal(0);
//        cascadePlatform.setProductType(0);
        int result = cascadePlatformService.save(cascadePlatform);
        if (result == 1) {
            SyslogSenderUtils.sendAddSyslogAndTransferredField(cascadePlatform, "添加下级平台",transferMap);
        }
        return this.vData(cascadePlatform);
    }

    /**
    * 修改
    **/
    @ApiOperation(value = "修改级联平台信息", hidden = false)
    @PatchMapping
    @SysRequestLog(description = "修改级联平台信息", actionType = ActionType.UPDATE)
    public Result updateCascadePlatform(@RequestBody CascadePlatform  cascadePlatform) {
        CascadePlatform  cascadePlatformSec = cascadePlatformService.findById(cascadePlatform.getId());
        int result = cascadePlatformService.update(cascadePlatform);
        if (result == 1) {
            SyslogSenderUtils.sendUpdateAndTransferredField(cascadePlatformSec, cascadePlatform,"修改级联平台信息",transferMap);
        }
        return this.result(result == 1);
    }

    /**
    * 查询（分页）
    */
    @ApiOperation(value = "查询级联平台信息")
    @PostMapping
    @SysRequestLog(description = "查询级联平台信息", actionType = ActionType.SELECT)
    public VList<CascadePlatform> queryCascadePlatform(@RequestBody PlatformRegisterVo queryVo) {
        SyslogSenderUtils.sendSelectSyslog();
        Example example = this.pageQuery(queryVo, CascadePlatform.class);
        Example.Criteria criteria = null;
        if( example.getOredCriteria().size()>0){
            criteria = example.getOredCriteria().get(0);
        }else {
            criteria = example.createCriteria();
        }
        criteria.andEqualTo("local", 0);
        List<CascadePlatform> list =  cascadePlatformService.findByExample(example);
        return this.vList(list);
    }

    /**
     * 删除平台信息
     */
    @ApiOperation(value = "删除平台信息")
    @DeleteMapping
    @SysRequestLog(description = "删除平台信息", actionType = ActionType.DELETE)
    public Result queryCascadePlatform(@RequestBody DeleteQuery deleteQuery) {
        String ids = deleteQuery.getIds();
        if (StringUtils.isEmpty(ids)) {
            return this.result(false);
        }
        List<CascadePlatform> platformList = cascadePlatformService.findByids(ids);
        int result = cascadePlatformService.deleteByIds(ids);
        if (result > 0) {
            platformList.forEach(cascadePlatform -> {
                SyslogSenderUtils.sendDeleteAndTransferredField(cascadePlatform,"删除平台信息",transferMap);
            });
        }
        return this.result(result == 1);
    }

    /**
     * 平台注册
     *
     * @return result
     */
    @ApiOperation(value = "平台注册信息查询")
    @GetMapping(value = "/register/info")
    public Result registerInfo() {
        PlatformRegisterVo platformRegisterVo = new PlatformRegisterVo();
        List<CascadePlatform> list = cascadePlatformService.findByProperty(CascadePlatform.class,"local",1);
        if(list!=null && list.size()>0){
            platformRegisterVo.setLocalHost(list.get(0).getIp());
            platformRegisterVo.setLocalPort(list.get(0).getPort());
            platformRegisterVo.setPlatformName(list.get(0).getPlatformName());
            platformRegisterVo.setPlatformId(list.get(0).getPlatformId());
            platformRegisterVo.setStatus(list.get(0).getStatus());
            platformRegisterVo.setRegStatus(list.get(0).getRegStatus());
            platformRegisterVo.setRegMsg(list.get(0).getRegMsg());
            platformRegisterVo.setSecurityClassification(list.get(0).getSecurityClassification());
            platformRegisterVo.setToken(list.get(0).getToken());
        }
        list = cascadePlatformService.findByProperty(CascadePlatform.class,"local",2);
        if(list!=null && list.size()>0){
            platformRegisterVo.setUpHost(list.get(0).getIp());
            platformRegisterVo.setUpPort(list.get(0).getPort());
        }
        return this.vData(platformRegisterVo);

    }

    /**
     * 平台注册
     *
     * @return result
     */
    @ApiOperation(value = "平台注册")
    @PutMapping(value = "/register/local")
    public Result register(HttpServletRequest request, @RequestBody PlatformRegisterVo platformRegisterVo) {
        String requestUrl = "https://" + platformRegisterVo.getUpHost() + ":" + platformRegisterVo.getUpPort() + "/cascade/register";
        logger.info("注册到："+requestUrl);
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        platformRegisterVo.setLocalPort(request.getServerPort());
        String localHost = cascadePlatformService.getLocalHost();
        if (StringUtils.isEmpty(localHost)) {
            localHost = request.getLocalAddr();
        }
        platformRegisterVo.setLocalHost(localHost);
        CascadeRegistVo cascadeRegistVo = new CascadeRegistVo();
        cascadeRegistVo.setHost(localHost);
        cascadeRegistVo.setPort(request.getServerPort()+"");
        cascadeRegistVo.setToken(platformRegisterVo.getToken()+"");
        cascadeRegistVo.setName(platformRegisterVo.getPlatformName()+"");
        cascadeRegistVo.setSecurityClassification(platformRegisterVo.getSecurityClassification()+"");
        String reqParam = JSON.toJSONString(cascadeRegistVo);

        try {
            logger.info("注册请求参数：" + reqParam);
            String response = HTTPUtil.POST(requestUrl, headers, reqParam);
            logger.info("注册返回信息：" + LogForgingUtil.validLog(response));

            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (MapUtils.isEmpty(resMap)) {
                setFailRegist("未知错误");
                return new Result("-1", "未知错误");
            }
            initPlatForm(platformRegisterVo);
            List<CascadePlatform> list = cascadePlatformService.findByProperty(CascadePlatform.class,"local",1);
            CascadePlatform cascadePlatformInfo =list.get(0);
            int status = (Integer)resMap.get("status");
            if (status == 200) {
//                cascadePlatformInfo.setPlatformName(resMap.get("platformName").toString());
                cascadePlatformInfo.setPlatformId(resMap.get("id").toString());
                cascadePlatformInfo.setStatus(1);
                cascadePlatformInfo.setRegStatus(1);
                cascadePlatformInfo.setRegMsg("");
            }else{
                cascadePlatformInfo.setStatus(0);
                cascadePlatformInfo.setRegStatus(0);
                cascadePlatformInfo.setRegMsg(resMap.get("message").toString());
            }
            cascadePlatformService.update(cascadePlatformInfo);

            if (status == 200) {
                return result(true);
            }
            return new Result(resMap.get("status").toString(), resMap.get("message").toString());
        } catch (Exception e) {
            logger.error("请求发送失败：" + requestUrl, e);
            setFailRegist("未找到指定url");
            return new Result("404", "未找到指定url");
        }
    }


    private void setFailRegist(String message){
        List<CascadePlatform> list = cascadePlatformService.findByProperty(CascadePlatform.class,"local",1);
        if(list.size()>0) {
            CascadePlatform cascadePlatformInfo = list.get(0);
            cascadePlatformInfo.setStatus(0);
            cascadePlatformInfo.setRegStatus(0);
            cascadePlatformInfo.setRegMsg(message);
            cascadePlatformService.update(cascadePlatformInfo);
        }
    }

    private void initPlatForm(PlatformRegisterVo platformRegisterVo){
        List<CascadePlatform> list = cascadePlatformService.findByProperty(CascadePlatform.class,"local",1);
        CascadePlatform cascadePlatformInfo =list.size()>0? list.get(0):new CascadePlatform();
        cascadePlatformInfo.setPlatformName(platformRegisterVo.getPlatformName());
        cascadePlatformInfo.setIp(platformRegisterVo.getLocalHost());
        cascadePlatformInfo.setPort(platformRegisterVo.getLocalPort());
        cascadePlatformInfo.setToken(platformRegisterVo.getToken());
        cascadePlatformInfo.setSecurityClassification(platformRegisterVo.getSecurityClassification());
        cascadePlatformInfo.setLocal(1);
        if(list.size()==0){
            // 注册成功保存本机信息
            cascadePlatformService.save(cascadePlatformInfo);
        }else {
            cascadePlatformService.update(cascadePlatformInfo);
        }

        list = cascadePlatformService.findByProperty(CascadePlatform.class,"local",2);
        cascadePlatformInfo =list.size()>0? list.get(0):new CascadePlatform();
        cascadePlatformInfo.setPlatformName("上级平台");
        cascadePlatformInfo.setIp(platformRegisterVo.getUpHost());
        cascadePlatformInfo.setPort(platformRegisterVo.getUpPort());
        cascadePlatformInfo.setLocal(2);
        if(list.size()==0){
            // 注册成功保存本机信息
            cascadePlatformService.save(cascadePlatformInfo);
        }else {
            cascadePlatformService.update(cascadePlatformInfo);
        }
    }
    /**
     * 接收平台注册
     *
     * @return result
     */
    @ApiOperation(value = "接收平台注册")
    @PostMapping(value = "/register")
    public Map checkRegister(@RequestBody CascadeRegistVo cascadeRegistVo ) {
        logger.info("接收注册参数："+JSON.toJSONString(cascadeRegistVo));
        Map<String, Object> resultMap = new HashMap<>();
        if (cascadeRegistVo == null
                ||StringUtils.isEmpty(cascadeRegistVo.getName())
                || StringUtils.isEmpty(cascadeRegistVo.getHost())
                || cascadeRegistVo.getPort() == null
                || StringUtils.isEmpty(cascadeRegistVo.getSecurityClassification())) {
            resultMap.put("id", "");
            resultMap.put("status", 415);
            resultMap.put("message", "请求格式错误");
            return resultMap;
        }

        CascadePlatform queryInfo = new CascadePlatform();
        queryInfo.setToken(cascadeRegistVo.getToken());
        queryInfo.setLocal(0);
        CascadePlatform cascadePlatform = cascadePlatformService.findOne(queryInfo);
        if (cascadePlatform == null) {
            resultMap.put("id", "");
            resultMap.put("status", 401);
            resultMap.put("message", "token校验失败");
            return resultMap;
        }

        String platformId = UUID.randomUUID().toString();
        cascadePlatform.setPlatformId(platformId);
        cascadePlatform.setIp(cascadeRegistVo.getHost());
        cascadePlatform.setPort(Integer.parseInt(cascadeRegistVo.getPort()));
        cascadePlatform.setSecurityClassification(cascadeRegistVo.getSecurityClassification());
        cascadePlatform.setStatus(1);
        cascadePlatform.setLocal(0);
        int result = cascadePlatformService.update(cascadePlatform);
        if (result == 1) {
            resultMap.put("platformName", cascadePlatform.getPlatformName());
            resultMap.put("id", platformId);
            resultMap.put("status", 200);
            resultMap.put("message", "成功");
            return resultMap;
        }

        resultMap.put("id", "");
        resultMap.put("status", 400);
        resultMap.put("message", "系统错误");
        return resultMap;
    }

    /**
     * 平台级联注销
     *
     * @return result
     */
    @ApiOperation(value = "上级注销平台级联")
    @PostMapping(value = "/logout/local")
    public Result logoutLocal(@RequestBody CascadePlatform cascadePlatform) {
        CascadePlatform cascadePlatformNew = cascadePlatformService.findById(cascadePlatform.getId());
        String ip = cascadePlatformNew.getIp();
        int port = cascadePlatformNew.getPort();
        CascadeLogoutVo cascadeLogoutVo = new CascadeLogoutVo();
        cascadeLogoutVo.setId(cascadePlatformNew.getPlatformId());
        cascadeLogoutVo.setToken(cascadePlatformNew.getToken());
        String reqParam = JSON.toJSONString(cascadeLogoutVo);
        String requestUrl = "https://" + ip + ":" + port + "/cascade/logout";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        try {
            logger.info("注销请求参数："+reqParam);
            String response = HTTPUtil.POST(requestUrl, headers, reqParam);
            logger.info("注销返回参数："+ LogForgingUtil.validLog(response));
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (MapUtils.isEmpty(resMap) && !resMap.containsKey("status")) {
                return new Result("402", "系统错误");
            }
            if ((Integer) resMap.get("status") == 200) {

                cascadePlatformNew.setPlatformId(null);
                cascadePlatformNew.setStatus(0);
                cascadePlatformNew.setRegStatus(0);
                cascadePlatformNew.setIp(null);
                cascadePlatformNew.setPort(null);
                cascadePlatformNew.setSecurityClassification("");
                int result = cascadePlatformService.update(cascadePlatformNew);
                if (result != 1) {
                    return new Result("-1", "注销失败");
                }

                return result(true);
            }
            return new Result(resMap.get("status").toString(), resMap.get("message").toString());
        } catch (Exception e) {
            logger.error("请求发送失败:" + requestUrl, e);
            return new Result("404", "未找到指定url");
        }
    }

    /**
     * 下级接收平台级联注销
     *
     * @return result
     */
    @ApiOperation(value = "下级接收平台级联注销")
    @PostMapping(value = "/logout")
    public Map logout(@RequestBody CascadeLogoutVo cascadeLogoutVo) {
        logger.info("注销接收参数："+JSON.toJSONString(cascadeLogoutVo));
        Map<String, Object> resMap = new HashMap<>();
        List<CascadePlatform> upInfos = cascadePlatformService.findByProperty(CascadePlatform.class,"local",2);
        if (CollectionUtils.isEmpty(upInfos)) {
            resMap.put("status", 401);
            resMap.put("message", "未找到上级平台信息");
            return resMap;
        }


        String upIp = upInfos.get(0).getIp();
        int result1 = cascadePlatformService.deleteById(upInfos.get(0).getId());

        List<CascadePlatform> list = cascadePlatformService.findByProperty(CascadePlatform.class,"local",1);
        if(list.size()>0) {
            CascadePlatform localInfo = list.get(0);
            localInfo.setStatus(0);
            localInfo.setSecurityClassification("");
            int ressult2 = cascadePlatformService.update(localInfo);

            if (result1 == 1 && ressult2 == 1) {
                resMap.put("status", 200);
                resMap.put("message", "下级注销成功");
                return resMap;
            }
        }
        resMap.put("status", 402);
        resMap.put("message", "系统错误");
        return resMap;
    }

    /**
     * 服务注册
     *
     * @return result
     */
    @ApiOperation(value = "查询下级平台状态")
    @PostMapping("/status/local")
    public Result status() {
        Map<String, Object> resultMap = new HashMap<>();
        List<PlatformStatusInfo> statusInfos = new ArrayList<>();
        List<PlatformStatusInfo> platformStatusInfos = Collections.synchronizedList(statusInfos);
        Example example = new Example(CascadePlatform.class);
        example.createCriteria().andEqualTo("status", 1).andEqualTo("local", 0);
        List<CascadePlatform> list = cascadePlatformService.findByExample(example);

        list.forEach(p -> {
            /*Runnable runnable = () -> {
                getPlatformStatus(p, platformStatusInfos);
            };
            new Thread(runnable).start();*/
            getPlatformStatus(p, platformStatusInfos);
        });

        //将多线程查询结果汇总
        int total = list.size();
        int online = platformStatusInfos.stream().filter(p -> p.getStatus() == 1).collect(Collectors.toList()).size();
        String onlineRate = new BigDecimal(online/total * 100).setScale(1, RoundingMode.HALF_UP) + "%";
        resultMap.put("total", total);
        resultMap.put("onlineRate", onlineRate);
        resultMap.put("platformStatusInfos", platformStatusInfos);
        return vData(resultMap);
    }

    /**
     * 获取平台状态
     *
     * @return result
     */
    @ApiOperation(value = "获取平台状态")
    @GetMapping("/status")
    public Map getStatus() {
        Map<String,Object> result = new HashMap<>();
        try {
            LocalHostInfoVO vo = hardwareService.getHostInfoDetail();
            long diskTotal = 0L, diskFree = 0L;
            String diskUsedRate = "0";
            List<DiskInfoVO> diskInfos = vo.getDiskInfoVo();
            if (CollectionUtils.isNotEmpty(diskInfos)) {
                for (DiskInfoVO disk : diskInfos) {
                    diskTotal += disk.getDiskTotal();
                    diskFree += disk.getDiskFree();
                }
                diskUsedRate = new BigDecimal((double) (diskTotal - diskFree)/diskTotal * 100).setScale(2, RoundingMode.HALF_UP).toString();
            }
            result.put("cpu", vo.getCpuInfoVo().getUsedRate() + "%");
            result.put("mem", vo.getRamInfoVo().getPercentAge() + "%");
            result.put("disk", diskUsedRate + "%");
            result.put("status", 1);
            List<CascadePlatform> platformList = cascadePlatformService.findByProperty(CascadePlatform.class,"local",1);
            if (CollectionUtils.isNotEmpty(platformList)) {
                CascadePlatform cascadePlatform = platformList.get(0);
                result.put("id", cascadePlatform.getPlatformId());
            } else {
                result.put("id","");
            }
            return result;
        } catch (Exception e) {
            logger.error("获取系统状态信息异常",e);
        }
        return result;
    }

    private void getPlatformStatus(CascadePlatform cascadePlatformInfo, List<PlatformStatusInfo> platformStatusInfos) {
        String requestUrl = "https://" + cascadePlatformInfo.getIp() + ":" + cascadePlatformInfo.getPort() + "/cascade/status";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");
        PlatformStatusInfo platformStatusInfo = new PlatformStatusInfo();
        platformStatusInfo.setPlatformName(cascadePlatformInfo.getPlatformName());
        platformStatusInfo.setPlatformId(cascadePlatformInfo.getPlatformId());
        try {
            String response = HTTPUtil.GET(requestUrl, headers);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            if (MapUtils.isNotEmpty(resMap)) {
                platformStatusInfo.setStatus((Integer) resMap.get("status"));
                platformStatusInfo.setCpu(resMap.get("cpu").toString());
                platformStatusInfo.setMem(resMap.get("mem").toString());
                platformStatusInfo.setDisk(resMap.get("disk").toString());
            }
        } catch (Exception e) {
            logger.error("请求发送失败：" + requestUrl, e);
        }
        platformStatusInfos.add(platformStatusInfo);
    }


    @ApiOperation(value = "查询下级平台统计")
    @PostMapping("/summary/local")
    public Result summary(@RequestBody CascadePlatform cascadePlatformInfo) {
        List<CascadePlatform> list = cascadePlatformService.findByProperty(CascadePlatform.class,"platformId",cascadePlatformInfo.getPlatformId());
        if(list.size() == 0 || list.get(0).getLocal()!=0){
            return new Result("-1","无该下级信息");
        }
        CascadePlatform cascadePlatform = list.get(0);
        String requestUrl = "https://" + cascadePlatform.getIp() + ":" + cascadePlatform.getPort() + "/cascade/summary";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Accept", "application/json");

        try {
            String response = HTTPUtil.GET(requestUrl, headers);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(response), Map.class);
            return this.vData(resMap);
        } catch (Exception e) {
            logger.error("请求发送失败：" + requestUrl, e);

        }

        return new Result("-1","获取下级信息失败");
    }
}