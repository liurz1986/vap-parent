package com.vrv.vap.admin.service.impl;

import com.google.gson.Gson;
import com.vrv.vap.admin.common.enums.SuperviseDataSubmitStatusEnum;
import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.model.SuperviseDataSubmit;
import com.vrv.vap.admin.service.SuperviseDataSubmitService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.util.LogForgingUtil;
import com.vrv.vap.admin.vo.supervise.ServerInfo;
import com.vrv.vap.base.BaseServiceImpl;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Service
@Transactional
public class SuperviseDataSubmitServiceImpl extends BaseServiceImpl<SuperviseDataSubmit> implements SuperviseDataSubmitService {

    private static final Logger log = LoggerFactory.getLogger(SuperviseDataSubmitServiceImpl.class);

    @Autowired
    private SystemConfigService systemConfigService;

    @Override
    public void offLineExport(String ids, HttpServletResponse response) {
        Example exam = new Example(SuperviseDataSubmit.class);
        exam.createCriteria().andIn("guid", Arrays.stream(ids.split(",")).collect(Collectors.toList()));
        List<SuperviseDataSubmit> superviseDataSubmitList = this.findByExample(exam);
        if (CollectionUtils.isEmpty(superviseDataSubmitList)) {
            return;
        }

        boolean result = false;
        String path = "/opt/file/cascade/data";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        String fileName = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()) + ".tar.gz";
        String tarPath = Paths.get(path, fileName).toString();
        String dataPath = Paths.get(path, "data.txt").toString();
        StringBuilder stringBuilder = new StringBuilder();
        for (SuperviseDataSubmit superviseDataSubmit : superviseDataSubmitList) {
            stringBuilder.append(superviseDataSubmit.getData()).append("\r\n");
        }

        try {
            FileUtils.writeFile(stringBuilder.toString(), dataPath);
            //OAuth2ClientKey oAuth2ClientKey = superviseService.getClientKey();
            ServerInfo serverInfo = new Gson().fromJson(systemConfigService.findByConfId("ServerInfo").getConfValue(),ServerInfo.class);
            String cmd = "tar -czvf - -C " +  path + " data.txt | openssl des -salt -k "
                    + serverInfo.getClientSecret() + " -out " + tarPath;
            log.info("压缩命令：" + LogForgingUtil.validLog(cmd));
            String[] cmdArr = new String[]{"/bin/sh","-c", cmd};
            Process pro = Runtime.getRuntime().exec(cmdArr);
            if (pro.waitFor(30,TimeUnit.SECONDS) && pro.exitValue() == 0) {
                result = FileUtils.downloadFile(tarPath,response);
            }
        } catch (Exception e) {
            log.error("数据压缩失败",e);
        }
        FileUtils.deleteFile(dataPath);
        //FileUtils.deleteFile(tarPath);

        if (result) {
            Example example = new Example(SuperviseDataSubmit.class);
            example.createCriteria().andIn("guid", Arrays.stream(ids.split(",")).collect(Collectors.toList()));
            SuperviseDataSubmit superviseDataSubmit = new SuperviseDataSubmit();
            superviseDataSubmit.setSubmitStatus(SuperviseDataSubmitStatusEnum.OFFLINE_SUBMIT_SUCCESS.getCode());
            superviseDataSubmit.setSubmitTime(new Date());
            this.updateSelectiveByExample(superviseDataSubmit, example);
        }
    }
}
