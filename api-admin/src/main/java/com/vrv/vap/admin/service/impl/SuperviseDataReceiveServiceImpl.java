package com.vrv.vap.admin.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vrv.vap.admin.common.util.DateUtil;
import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.common.util.ShellExecuteScript;
import com.vrv.vap.admin.model.SuperviseDataReceive;
import com.vrv.vap.admin.service.SuperviseDataReceiveService;
import com.vrv.vap.admin.service.SuperviseService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.service.kafka.KafkaSenderService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.supervise.ServerInfo;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.math.BigDecimal;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Transactional
public class SuperviseDataReceiveServiceImpl extends BaseServiceImpl<SuperviseDataReceive> implements SuperviseDataReceiveService {

    private static final Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();

    private static final Logger log = LoggerFactory.getLogger(SuperviseDataReceiveServiceImpl.class);

    @Autowired
    SystemConfigService systemConfigService;

    @Autowired
    SuperviseService superviseService;

    @Autowired
    KafkaSenderService kafkaSenderService;

    @Override
    public Result importAnnounce(MultipartFile file) {
        String originalFilename = "receive_" + new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".tar.gz";
        String uploadPath = "/opt/file/cascade/receive";
        String filePath = Paths.get(uploadPath,originalFilename).toString();
        String dataPath = Paths.get(uploadPath,"data").toString();

        boolean result = FileUtils.uploadFile(file,filePath);

        File fileData = new File(dataPath);
        if (!fileData.exists()) {
            fileData.mkdirs();
        }

        //OAuth2ClientKey oAuth2ClientKey = superviseService.getClientKey();
        String serverJson = systemConfigService.findByConfId("ServerInfo").getConfValue();
        if (StringUtils.isEmpty(serverJson)) {
            return new Result("-1", "系统未注册");
        }
        ServerInfo serverInfo = gson.fromJson(serverJson,ServerInfo.class);
        if (!serverInfo.getIsRegister()) {
            return new Result("-1", "系统注册失败");
        }

        try {
            String cmd = "openssl des -d -k " + serverInfo.getClientSecret()
                    + " -salt -in  " + filePath  + " | tar xzf - -C " + dataPath;
            log.info("解压命令：" + LogForgingUtil.validLog(cmd));
            String[] cmdArr = new String[]{"/bin/sh","-c", cmd};
            Process pro = Runtime.getRuntime().exec(cmdArr);
            List<String> errInfo = ShellExecuteScript.getMessage(pro.getErrorStream());
            log.info(new Gson().toJson("错误信息：" + errInfo));
            for (String info : errInfo) {
                if (info.contains("Error ")) {
                    return new Result("-1", "数据包解压失败，请检查文件加密方式");
                }
            }
            if (pro.waitFor(30,TimeUnit.SECONDS) && pro.exitValue() == 0) {
                File files = new File(dataPath);
                for (File dataFile : files.listFiles()) {
                    List<String> dataList = FileUtils.readFile(dataFile.getPath());
                    for (String data : dataList) {
                        log.info("data:" + data);
                        kafkaSenderService.send("SuperviseAnnounce", null, data);
                        SuperviseDataReceive superviseDataReceive = new SuperviseDataReceive();
                        try {
                            Map<String, Object> dataMap = gson.fromJson(data, Map.class);
                            BigDecimal bigDecimal = new BigDecimal(dataMap.get("notice_type").toString());
                            superviseDataReceive.setDataType(bigDecimal.intValue());
                            superviseDataReceive.setCreateTime(DateUtil.parseDate(dataMap.get("create_time").toString(),"yyyy-MM-dd HH:mm:ss"));
                            superviseDataReceive.setReceiveType(2);
                            superviseDataReceive.setReceiveTime(new Date());
                            superviseDataReceive.setData(data);
                        } catch (Exception e) {
                            log.error("文件内容解析失败",e);
                            return new Result("-1", "文件内容解析失败，请检查数据格式");
                        } finally {
                            FileUtils.deleteFile(dataFile.getAbsolutePath());
                        }
                        int res = this.save(superviseDataReceive);
                        if (res == 1) {
                            SyslogSenderUtils.sendAddSyslog(superviseDataReceive,"导入上级监管平台数据");
                        }
                    }
                }

                return Global.OK;
            }
        } catch (Exception e) {
            log.error("数据解压失败", e);
        } finally {
            if (result) {
                FileUtils.deleteFile(filePath);
            }
        }

        return new Result("-1", "未知错误，请联系管理员");
    }

    @Override
    public Result saveAnnounce(Map info) {
        kafkaSenderService.send("SuperviseAnnounce", null, gson.toJson(info));
        SuperviseDataReceive superviseDataReceive = new SuperviseDataReceive();
        try {
            superviseDataReceive.setDataType(Integer.parseInt(info.get("notice_type").toString()));
            superviseDataReceive.setCreateTime(DateUtil.parseDate((String) info.get("create_time"),DateUtil.DEFAULT_DATE_PATTERN));
        } catch (ParseException e) {
            log.error("拉取监管业务系统数据失败！",e);
            return new Result("-1", "数据解析失败，请检查数据格式");
        }
        superviseDataReceive.setReceiveType(1);
        superviseDataReceive.setReceiveTime(new Date());
        superviseDataReceive.setData(gson.toJson(info));
        int res = this.save(superviseDataReceive);
        if (res == 1) {
            SyslogSenderUtils.sendAddSyslog(superviseDataReceive,"接收上级监管平台数据");
        }
        return Global.OK;
    }
}
