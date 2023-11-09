package com.vrv.vap.monitor.server.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.json.JsonSanitizer;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.monitor.server.common.config.VapBackupProperties;
import com.vrv.vap.monitor.server.common.util.BackupUtil;
import com.vrv.vap.monitor.server.common.util.CleanUtil;
import com.vrv.vap.monitor.server.common.util.JsonUtil;
import com.vrv.vap.monitor.server.model.DbTaskInfo;
import com.vrv.vap.monitor.server.service.DbBackupService;
import com.vrv.vap.monitor.server.service.VapBackupService;
import com.vrv.vap.monitor.server.vo.BackupTableVO;
import com.vrv.vap.monitor.server.vo.Result;
import com.vrv.vap.monitor.server.vo.TaskVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.nio.file.Paths;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DbBackupServiceImpl extends BaseServiceImpl<DbTaskInfo> implements DbBackupService {

    private static Logger logger = LoggerFactory.getLogger(DbBackupServiceImpl.class);

    @Autowired
    private VapBackupService vapBackupService;

    @Autowired
    private VapBackupProperties vapBackupProperties;

    @Autowired
    private DbBackupService dbBackupService;

    @Value("${NAMESPACE}")
    private String namespace;

    @Value("${SERVER_ADDR}")
    private String nacosAddr;

    @Value("${vap.backup.url:}")
    private String pushUrl;

    private static final String FILE_SUFFIX = ".vapbak";

    @Override
    public Map<String, Object> backup(DbTaskInfo dbTaskInfo) {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("taskId", dbTaskInfo.getTaskId());
        resMap.put("status", 2);
        dbTaskInfo.setStatus(2);
        try {
            logger.info("start backup config");
            // 备份系统表信息
            Map<String, List<Map>> sysInfoMap = new HashMap<>();
            int sysTableCount = vapBackupProperties.getSys().size();
            for (int i = 0; i < sysTableCount; i++) {
                String tableName = vapBackupProperties.getSys().get(i).getName();
                logger.info("start backup vap-sys table：" + vapBackupProperties.getSys().get(i));
                List<Map> list = vapBackupService.getInfo(tableName);
                sysInfoMap.put(tableName, list);
            }
            String foldName = dbTaskInfo.getFileName().replace(FILE_SUFFIX, "");
            File fs = new File(foldName);
            if (fs.exists()) {
                fs.delete();
            }

            if (!backupNacos(Paths.get(vapBackupProperties.getExchangePath(), foldName).toString())) {
                resMap.put("resCode", "202");
                resMap.put("resMsg", "nacos配置备份失败");
                dbBackupService.update(dbTaskInfo);
                return resMap;
            }

            BackupUtil.createBakFile(vapBackupProperties.getExchangePath(), foldName, "sys", JsonUtil.objToJson(sysInfoMap));
            logger.info("start compress backup sql");
            String zipPath = BackupUtil.createBackZip(vapBackupProperties.getExchangePath(), foldName, "qazwsx");
            BackupUtil.deleteDirectory(Paths.get(vapBackupProperties.getExchangePath(), foldName).toString());

            try (InputStream in = new FileInputStream(zipPath)) {
                String md5 = DigestUtils.md5Hex(in);
                dbTaskInfo.setFilePath(zipPath);
                dbTaskInfo.setFileMd5(md5);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }

            resMap.put("status", 1);
            resMap.put("resCode", "0");
            resMap.put("resMsg", "成功");
        } catch (Exception e) {
            logger.info("备份失败", e);
            resMap.put("resCode", "203");
            resMap.put("resMsg", "备份失败");
        }

        dbTaskInfo.setStatus(1);
        dbBackupService.update(dbTaskInfo);
        return resMap;
    }

    @Override
    public Map<String, Object> recovery(DbTaskInfo dbTaskInfo) {
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("status", 4);
        resMap.put("taskId", dbTaskInfo.getTaskId());
        dbTaskInfo.setStatus(4);
        String fileFullPath = dbTaskInfo.getFilePath();
        File fs = new File(fileFullPath);
        if (!fs.exists()) {
            logger.error("未找到文件");
            resMap.put("resCode", "206");
            resMap.put("resMsg", "未找到文件");
            dbBackupService.update(dbTaskInfo);
            return resMap;
        }
        try (InputStream in = new FileInputStream(fileFullPath)) {
            String md5 = DigestUtils.md5Hex(in);
            if (!StringUtils.equals(md5, dbTaskInfo.getFileMd5())) {
                return resMap;
            }

            logger.info("start decompress backup sql");
            final Map<String, List<Map<String, Object>>> sysInfoMap;

            String folderPath = fileFullPath.replace(FILE_SUFFIX, "");
            BackupUtil.unZipFile(fileFullPath, folderPath, "qazwsx");
            if (!recoveryNacos(Paths.get(folderPath, "nacos.zip").toString())) {
                resMap.put("resCode", "207");
                resMap.put("resMsg", "nacos配置还原失败");
                dbBackupService.update(dbTaskInfo);
                return resMap;
            }
            String sysJson = BackupUtil.decompressFile(fileFullPath, vapBackupProperties.getExchangePath(), "sys", "qazwsx");
            sysInfoMap = JsonUtil.getObjectMapper().readValue(JsonSanitizer.sanitize(sysJson), Map.class);

            logger.info("start recovery config，vap-sys tables：" + sysInfoMap.keySet().size() + "；vap tables：" + sysInfoMap.keySet().size());
            sysInfoMap.keySet().stream().forEach(tableName -> {
                BackupTableVO backupTableVO = vapBackupProperties.getSys().stream().filter(p -> tableName.equals(p.getName())).findFirst().get();
                logger.info("start backup vap-sys table：" + backupTableVO);
                vapBackupService.clearRows(backupTableVO.getName());
                vapBackupService.insertInfo(backupTableVO, sysInfoMap.get(tableName));
            });

            BackupUtil.deleteDirectory(folderPath);
            logger.info("数据还原完成");
            resMap.put("resCode", "0");
            resMap.put("resMsg", "成功");
            resMap.put("status", 3);
            dbTaskInfo.setStatus(3);
        } catch (Exception e) {
            logger.error("配置还原失败",e);
            resMap.put("resCode", "208");
            resMap.put("resMsg", "配置还原失败");
        }

        dbBackupService.update(dbTaskInfo);
        return resMap;
    }

    @Override
    public Result deleteFile(TaskVO taskInfo) {
        String resCode = "0";
        String resMsg = "成功";
        DbTaskInfo query = new DbTaskInfo();
        query.setBusinessId(taskInfo.getBusinessId());
        DbTaskInfo dbTaskInfo = dbBackupService.findOne(query);
        if (dbTaskInfo == null) {
            return new Result("205", "未找到任务记录");
        }
        String filePath = dbTaskInfo.getFilePath();
        File file = new File(filePath);
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                if (dbBackupService.deleteById(dbTaskInfo.getId()) != 1) {
                    logger.info("数据库信息删除失败");
                    resCode = "203";
                    resMsg = "数据库信息删除失败";
                }
            } else {
                logger.info("文件删除失败");
                resCode = "202";
                resMsg = "文件删除失败";
            }
        } else {
            logger.info("文件不存在");
            resCode = "201";
            resMsg = "文件不存在";
        }

        return new Result(resCode, resMsg);
    }

    @Override
    public void restart() {
        try {
            Runtime.getRuntime().exec(CleanUtil.cleanString("nohup sh  " + vapBackupProperties.getRestartCmd() + "  &"));
        } catch (Exception e){
            logger.error("服务重启失败", e);
        }
    }

    @Override
    public Result importFile(TaskVO taskVO) {
        if (taskVO.getBusinessId() == null || taskVO.getTaskId() == null || StringUtils.isEmpty(taskVO.getFilePath())) {
            return new Result("201", "参数格式错误");
        }

        try (InputStream in = new FileInputStream(CleanUtil.cleanString(taskVO.getFilePath()))) {
            DbTaskInfo dbTaskInfo = new DbTaskInfo();
            dbTaskInfo.setBusinessId(taskVO.getBusinessId());
            dbTaskInfo.setTaskId(taskVO.getTaskId());
            dbTaskInfo.setFilePath(taskVO.getFilePath());
            dbTaskInfo.setStatus(0);
            dbTaskInfo.setTaskType(2);
            File file = new File(CleanUtil.cleanString(taskVO.getFilePath()));
            dbTaskInfo.setFileName(file.getName());
            dbTaskInfo.setFileMd5(DigestUtils.md5Hex(in));
            dbTaskInfo.setCreateTime(new Date());
            dbBackupService.save(dbTaskInfo);
            return new Result("0","success");
        } catch (IOException e) {
            logger.error("文件MD5值获取异常", e);
        }
        return new Result("202", "文件MD5值获取异常");
    }

    private boolean backupNacos(String filePath) {
        boolean result = false;
        File file = new File(filePath);
        if (!file.exists()) {
            file.mkdir();
        }
        CloseableHttpClient httpClient = null;
        try (OutputStream out = new FileOutputStream(new File(filePath, "nacos.zip"))) {
            httpClient = HttpClientBuilder.create().build();
            String url = "http://" + nacosAddr + "/nacos/v1/cs/configs" +
                    "?export=true&group=&tenant=" + namespace + "&appName=&ids=&dataId=";
            HttpGet get = new HttpGet(url);
            CloseableHttpResponse response = httpClient.execute(get);
            org.apache.http.HttpEntity entity = response.getEntity();
            entity.writeTo(out);
            result = true;
        } catch (Exception e) {
            logger.error("nacos配置备份失败", e);
        } finally {
            if (httpClient != null) {
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private boolean recoveryNacos(String filePath) {
        boolean result = false;
        try {
            String url = "http://" + nacosAddr + "/nacos/v1/cs/configs?import=true&namespace=" + namespace;
            RestTemplate restTemplate = new RestTemplate();
            //设置请求头
            HttpHeaders headers = new HttpHeaders();
            MediaType type = MediaType.parseMediaType("multipart/form-data");
            headers.setContentType(type);

            //设置请求体
            FileSystemResource fileSystemResource = new FileSystemResource(filePath);
            if (!fileSystemResource.exists()) {
                logger.info("文件不存在");
                return result;
            }

            MultiValueMap<String, Object> form = new LinkedMultiValueMap<>();
            form.add("policy", "OVERWRITE");
            form.add("file", fileSystemResource);

            //用HttpEntity封装整个请求报文
            HttpEntity<MultiValueMap<String, Object>> files = new HttpEntity<>(form, headers);

            String res = restTemplate.postForObject(url, files, String.class);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String,Object> resMap = objectMapper.readValue(JsonSanitizer.sanitize(res), Map.class);
            if (MapUtils.isEmpty(resMap) || (Integer)resMap.get("code") != 200) {
                logger.info("nacos配置还原失败");
            } else {
                result = true;
            }
        } catch (Exception e) {
            logger.error("nacos配置还原失败", e);
        }
        return result;
    }
}
