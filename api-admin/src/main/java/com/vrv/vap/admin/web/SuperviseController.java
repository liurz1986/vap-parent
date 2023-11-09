package com.vrv.vap.admin.web;

import com.google.gson.Gson;
import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.common.util.ShellExecuteScript;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.service.SuperviseService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.vo.supervise.RegisterInfo;
import com.vrv.vap.admin.vo.supervise.ServerInfo;
import com.vrv.vap.admin.vo.supervise.ServerInfoBase;
import com.vrv.vap.admin.vo.supervise.ServerRegisterResult;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.controller.ApiController;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.common.vo.VData;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionType;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.dozer.DozerBeanMapperBuilder;
import org.dozer.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping(path = "")
public class SuperviseController extends ApiController {


    private static final Logger logger = LoggerFactory.getLogger(ApiController.class);
    @Autowired
    SuperviseService superviseService;

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    KafkaSenderService kafkaSenderService;

    //持有Dozer单例, 避免重复创建DozerMapper消耗资源.
    public static final Mapper mapper = DozerBeanMapperBuilder.buildDefault();

    @GetMapping("/supervise/getServerInfo")
    @ApiOperation(value = "获取服务器信息")
    @SysRequestLog(description = "获取服务器信息", actionType = ActionType.SELECT)
    public VData<ServerInfo> getServerInfo(HttpServletRequest request) {
        ServerInfo serverInfo = superviseService.getServerInfo(request);
        return this.vData(serverInfo);
    }

    @PostMapping("/supervise/serverRegister")
    @ApiOperation(value = "服务器信息（在线）注册认证")
    @SysRequestLog(description = "服务器信息（在线）注册认证", actionType = ActionType.UPDATE)
    public Result serverRegister(@RequestBody ServerInfoBase baseinfo) {
        ServerInfo result = mapper.map(baseinfo, ServerInfo.class);

        result.setIsRegister(false);
        result.setRegisterType(0);//未注册
        ServerRegisterResult serverRegister = superviseService.serverRegister(baseinfo);
        if ("200".equals(serverRegister.getCode())) {
            result.setIsRegister(true);
            result.setRegisterType(1);//在线注册
            result.setClientId(serverRegister.getClientId());
            result.setClientSecret(serverRegister.getClientSecret());
        }

        result.setRegisterDescript(serverRegister.getCodeDescript());

        boolean saveServerInfo = superviseService.saveServerInfo(result);
        if (saveServerInfo) {
            result.setIsRegister(true);
            result.setRegisterType(1);//在线注册
            result.setRegisterDescript("在线注册成功");
            superviseService.saveServerInfo(result);
            return this.vData(result);
        }

        return this.vData(Global.ERROR);
    }

    @PatchMapping("/supervise/updateServerInfo")
    @ApiOperation(value = "修改注册信息")
    @SysRequestLog(description = "修改注册信息", actionType = ActionType.UPDATE)
    public Result updateRegister(@RequestBody ServerInfoBase baseinfo) {
        return this.result(superviseService.updateRegister(baseinfo) > 0);
    }


    @PostMapping("/supervise/offLineServerRegisterFileCreate")
    @ApiOperation(value = "离线注册文件生成")
    @SysRequestLog(description = "离线注册文件生成", actionType = ActionType.ADD)
    public Result offLineServerRegisterFileCreate(@RequestBody ServerInfoBase baseinfo) {
        try {
            String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
            String savePath = "/opt/file/cascade/register";
            File file = new File(savePath);
            // 判断上传文件的保存目录是否存在
            if (!file.exists() || !file.isDirectory()) {
                // 创建目录
                file.mkdirs();
            }

            String filePath = Paths.get(savePath, fileName + ".txt").toString();

            File dataFile = new File(filePath);
            if (!dataFile.exists() || !dataFile.isFile()) {
                // 创建目录
                dataFile.createNewFile();
            }
            RegisterInfo registerInfo = new RegisterInfo();
            BeanUtils.copyProperties(baseinfo, registerInfo);
            if (FileUtils.writeFile(new Gson().toJson(registerInfo), filePath)) {
                //String cmd = "cd " + savePath + " && tar -czvf " + fileName + ".tar.gz " + filePath;
                String cmd = "tar -czvf " + Paths.get(savePath, fileName + ".tar.gz") + " -C " + savePath + " " + fileName + ".txt";
                logger.info("压缩命令：" + cmd);
                Process pro = Runtime.getRuntime().exec(cmd);
                if (pro.waitFor(30, TimeUnit.SECONDS) && pro.exitValue() == 0) {
                    logger.info("执行结果：" + new Gson().toJson(ShellExecuteScript.getMessage(pro.getErrorStream())));
                    FileUtils.deleteFile(filePath);
                    return this.vData(fileName);
                }
            }
        } catch (Exception e) {
            return this.vData(Global.ERROR);
        }

        return this.vData(Global.ERROR);
    }

    @GetMapping("/supervise/offLineServerRegisterFileDownload/{fileName}")
    @ApiOperation(value = "离线注册文件下载")
    @SysRequestLog(description = "离线注册文件下载", actionType = ActionType.SELECT)
    public void offLineServerRegisterFileDownload(@PathVariable String fileName, HttpServletRequest request, HttpServletResponse response) {

        String savePath = "/opt/file/cascade/register";

        String filePath = Paths.get(savePath, CleanUtil.cleanString(fileName) + ".tar.gz").toString();
        FileUtils.downloadFile(filePath, response);
    }


    @PostMapping("/supervise/offLineServerRegister")
    @ApiOperation(value = "服务器信息（离线）注册认证")
    @SysRequestLog(description = "服务器信息（离线）注册认证", actionType = ActionType.UPDATE)
    public Result offLineServerRegister(@RequestBody ServerInfoBase baseinfo) {
        if (StringUtils.isEmpty(baseinfo.getClientId())) {
            return this.vData(Global.ERROR);
        }
        ServerInfo result = new ServerInfo();
        BeanUtils.copyProperties(baseinfo, result);

        result.setIsRegister(true);
        result.setRegisterType(2);//离线注册

        result.setRegisterDescript("离线注册成功");

        boolean saveServerInfo = superviseService.saveServerInfo(result);
        if (saveServerInfo) {
            return this.vData(result);
        }

        return this.vData(Global.ERROR);
    }

    @PostMapping("/coor/api/routing/register")
    public Map registerCoor(@RequestBody Map param) {
        Map result = new HashMap();
        result.put("client_id", Uuid.uuid());
        result.put("client_secret", Uuid.uuid());
        result.put("code", 200);
        result.put("msg", "成功");
        return result;
    }

    @PostMapping("/coor/api/routing/update")
    public Map updateCoor(@RequestBody Map param) {
        Map result = new HashMap();
        result.put("code", 200);
        result.put("msg", "成功");
        return result;
    }


    @PostMapping("/coor/api/routing/token")
    public Map getCoorToken(@RequestBody Map param) {
        Map result = new HashMap();
        result.put("access_token", Uuid.uuid());
        result.put("token_type", "bearer");
        result.put("expires_in", 36000);
        result.put("code", 200);
        result.put("msg", "成功");
        return result;
    }

    @PostMapping("/coor/api/routing/status")
    public Map coorStatus(@RequestBody Map param) {
        Map result = new HashMap();
        result.put("code", 200);
        result.put("msg", "成功");
        return result;
    }

    @PostMapping("/coor/api/routing/data")
    public Map coorData(@RequestBody Map param) {
        Map result = new HashMap();
        result.put("code", 200);
        result.put("msg", "成功");
        return result;
    }

    @GetMapping("/sendKafkaData")
    public void sendKafkaData() {
        // 监管事件数据
        String json = "{\"alert_info\":{\"alert\":{\"client_id\":\"f146fb98-71ba-4ab7-8734-9cf2109ac0b4\",\"update_time\":\"2021-01-01 10:10:10\",\"type\":1,\"data\":[{\"event_id\":\"234234234324\",\"event_name\":\"防火墙长期不在线\",\"event_type\":\"1\",\"event_createtime\":\"2021-10-08 10:10:10\",\"event_version\":\"1.0\",\"unit_list\":[{\"unit_geo_name\":\"北京市海淀区\",\"unit_name\":\"北信源\"}],\"staff_num\":\"1\",\"staff_list\":[{\"staff_name\":\"张三\"}],\"device_count\":1,\"device_list\":[{\"device_name\":\"ll-PC\",\"device_ip\":\"192.168.2.3\"}],\"device_app_count\":1,\"application_list\":[],\"file_list\":[],\"event_triggers\":\"\",\"event_details\":\"\",\"extention\":[]}]}}}";
        // 上报时间处置数据
        String json2 = "{\"client_id\":\"f146fb98-71ba-4ab7-8734-9cf2109ac0b4\",\"update_time\":\"2021-01-01 10:10:10\",\"type\":2,\"data\":[{\"event_id\":\"234324234\",\"event_createtime\":\"2021-10-08 10:10:10\",\"disposal_person_name\":\"李明\",\"disposal_person_role\":\"管理员\",\"disposal_time\":\"2021-10-09 10:10:10\",\"is_misreport\":\"0\",\"disposal_status\":\"2\"}]}";
        // 上报线索信息
        String json3 = "{\"client_id\":\"f146fb98-71ba-4ab7-8734-9cf2109ac0b4\",\"update_time\":\"2021-01-01 10:10:10\",\"type\":3,\"data\":[{\"event_id\":\"234324234\",\"event_createtime\":\"2021-10-08 10:10:10\",\"event_kind\":\"1\",\"event_type\":\"1\",\"person_count\":1}]}";
        // 上报协查请求信息
        String json4 = "{\"client_id\":\"f146fb98-71ba-4ab7-8734-9cf2109ac0b4\",\"update_time\":\"2021-01-01 10:10:10\",\"type\":4,\"notice_id\":\"23423423\",\"co_file\":[{\"aaa\":\"bbb\"}],\"data\":[{\"event_id\":\"234324234\",\"event_createtime\":\"2021-10-08 10:10:10\",\"event_kind\":\"1\",\"event_type\":\"1\",\"person_count\":1}]}";
        // 上报协办结果
        String json5 = "{\"client_id\":\"f146fb98-71ba-4ab7-8734-9cf2109ac0b4\",\"update_time\":\"2021-01-01 10:10:10\",\"type\":5,\"notice_id\":\"23423423\",\"co_file\":[{\"ccc\":\"ddd\"}],\"data\":[{\"assis_id\":\"234324234\",\"apply_unit\":\"2\"}]}";
        // 上报预警响应信息
        String json6 = "{\"client_id\":\"f146fb98-71ba-4ab7-8734-9cf2109ac0b4\",\"update_time\":\"2021-01-01 10:10:10\",\"type\":6,\"notice_id\":\"23423423\",\"warn_file\":[{\"file_name\":\"ddd\"}],\"data\":[{\"warnning_description\":\"22222\",\"warnning_conlusion\":\"33333\"}]}";
        kafkaSenderService.send("SuperviseDataSubmit", null, json6);
    }
}
