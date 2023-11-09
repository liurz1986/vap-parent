package com.vrv.vap.admin.service.impl;

import com.google.json.JsonSanitizer;
import com.vrv.vap.admin.common.config.DbBackupConfig;
import com.vrv.vap.admin.common.util.BackupUtil;
import com.vrv.vap.admin.common.util.FileUtils;
import com.vrv.vap.admin.common.util.JsonUtil;
import com.vrv.vap.admin.common.util.Uuid;
import com.vrv.vap.admin.model.DbBackupConfigInfo;
import com.vrv.vap.admin.model.DbOperationInfo;
import com.vrv.vap.admin.service.DbOperationService;
import com.vrv.vap.admin.service.VapBackupService;
import com.vrv.vap.admin.util.CleanUtil;
import com.vrv.vap.admin.vo.BackupTableVO;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.vo.Result;
import com.vrv.vap.syslog.common.utils.SyslogSenderUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.FutureTask;
import java.util.stream.Collectors;

@Service
public class DbOperationServiceImpl extends BaseServiceImpl<DbOperationInfo> implements DbOperationService {

    private static Logger logger = LoggerFactory.getLogger(DbOperationServiceImpl.class);

    @Autowired
    private VapBackupService vapBackupService;

    @Value("${vap.zjg.backup.filePath:/opt/file/backup}")
    private String filePath;

    @Value("${cmd:/opt/SecAudit/vrv/vap/cloud/restartService.sh}")
    private String cmd;

    private static final String FILE_SUFFIX = ".vapbak";

    @Override
    public Result backup(DbOperationInfo dbOperationInfo) {
        DbOperationInfo dbOperation = new DbOperationInfo();
        dbOperation.setUuid(Uuid.uuid());
        dbOperation.setDataTypes(dbOperationInfo.getDataTypes());
        dbOperation.setOperationType(1);
        dbOperation.setOperationStatus(3);
        Date date = new Date();
        dbOperation.setStartTime(date);
        dbOperation.setFileStorage("本地");
        this.save(dbOperation);

        try {
            String[] dataTypes = dbOperationInfo.getDataTypes().split(",");
            Map<String, DbBackupConfigInfo> dbBackupConfigMap = DbBackupConfig.getDbBackupConfigMap();
            List<String> tables = new ArrayList<>();
            for (String dataType : dataTypes) {
                tables.addAll(Arrays.stream(dbBackupConfigMap.get(dataType).getTableName().split(",")).collect(Collectors.toList()));
            }
            logger.info("start backup config");
            // 备份系统表信息
            Map<String,Object> infoMap = new HashMap<>();
            Map<String, List<Map>> sysInfoMap = new HashMap<>();
            for (String tableName : tables) {
                logger.info("start backup vap table：" + tableName);
                List<Map> list = vapBackupService.getInfo(tableName);
                sysInfoMap.put(tableName, list);
            }
            String foldName = dbOperationInfo.getDataTypes().replace(",", "&") + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(date);
            File fs = new File(foldName);
            if (fs.exists()) {
                fs.delete();
            }
            infoMap.put("tables",sysInfoMap);
            infoMap.put("dataTypes",dbOperationInfo.getDataTypes());
            BackupUtil.createBakFile(filePath, foldName, "sys", JsonUtil.objToJson(infoMap));
            logger.info("start compress backup sql");
            String zipPath = BackupUtil.createBackZip(filePath, foldName, "qazwsx");
            BackupUtil.deleteDirectory(Paths.get(filePath, foldName).toString());

            try (InputStream in = new FileInputStream(zipPath)) {
                String md5 = DigestUtils.md5Hex(in);
                dbOperation.setFileMd5(md5);
            } catch (Exception e) {
                logger.info(e.getMessage());
            }
            dbOperation.setFileName(foldName + FILE_SUFFIX);
            dbOperation.setOperationStatus(1);
            dbOperation.setEndTime(new Date());
            this.update(dbOperation);
            return Global.OK;
        } catch (Exception e) {
            logger.info("备份失败", e);
            dbOperation.setMessage("系统异常：" + e.getMessage());
        }
        dbOperation.setOperationStatus(2);
        dbOperation.setEndTime(new Date());
        this.update(dbOperation);
        return new Result("-1", "备份失败");
    }

    @Override
    public Result recovery(DbOperationInfo dbOperationInfo) {
        DbOperationInfo dbOperation = new DbOperationInfo();
        BeanUtils.copyProperties(dbOperationInfo, dbOperation);
        dbOperation.setUuid(Uuid.uuid());
        dbOperation.setOperationType(2);
        dbOperation.setOperationStatus(3);
        dbOperation.setStartTime(new Date());
        //dbOperation.setEndTime(null);
        this.save(dbOperation);

        String fileFullPath = Paths.get(filePath, dbOperationInfo.getFileName()).toString();
        try (InputStream in = new FileInputStream(fileFullPath)) {
            File fs = new File(fileFullPath);
            if (!fs.exists()) {
                logger.error("未找到文件");
                dbOperation.setOperationStatus(2);
                dbOperation.setEndTime(new Date());
                dbOperation.setMessage("未找到文件");
                this.update(dbOperation);
                return new Result("-1", "未找到文件");
            }
            String md5 = DigestUtils.md5Hex(in);
            if (!StringUtils.equals(md5, dbOperationInfo.getFileMd5())) {
                dbOperation.setOperationStatus(2);
                dbOperation.setEndTime(new Date());
                dbOperation.setMessage("文件MD5不匹配");
                this.update(dbOperation);
                return new Result("-1", "文件MD5不匹配");
            }

            logger.info("start decompress backup sql");
            Map<String,Object> infoMap;
            final Map<String, List<Map<String, Object>>> sysInfoMap;

            String folderPath = fileFullPath.replace(FILE_SUFFIX, "");
            BackupUtil.unZipFile(fileFullPath, folderPath, "qazwsx");
            String sysJson = BackupUtil.decompressFile(fileFullPath, filePath, "sys", "qazwsx");
            infoMap = JsonUtil.getObjectMapper().readValue(JsonSanitizer.sanitize(sysJson), Map.class);
            sysInfoMap = (Map<String, List<Map<String, Object>>>) infoMap.get("tables");

            String dataTypeStr = (String) infoMap.get("dataTypes");
            String[] dataTypes = dataTypeStr.split(",");
            Map<String, DbBackupConfigInfo> dbBackupConfigMap = DbBackupConfig.getDbBackupConfigMap();
            StringBuilder stringBuilder = new StringBuilder();
            for (String dataType : dataTypes) {
                if (StringUtils.isNotEmpty(dbBackupConfigMap.get(dataType).getTimeField())) {
                    stringBuilder.append(dbBackupConfigMap.get(dataType).getTimeField()).append(",");
                }
            }

            String[] timeFields = null;
            if (stringBuilder.length() > 0) {
                timeFields = stringBuilder.deleteCharAt(stringBuilder.length() - 1).toString().split(",");
            }

            logger.info("start recovery config，vap-sys tables：" + sysInfoMap.keySet().size() + "；vap tables：" + sysInfoMap.keySet().size());
            for (String tableName : sysInfoMap.keySet()) {
                BackupTableVO backupTableVO = new BackupTableVO();
                backupTableVO.setName(tableName);
                logger.info("start backup vap-sys table：" + backupTableVO);
                vapBackupService.clearRows(backupTableVO.getName());
                backupTableVO.setDateFields(timeFields);
                vapBackupService.insertInfo(backupTableVO, sysInfoMap.get(tableName));
            }

            BackupUtil.deleteDirectory(folderPath);
            logger.info("数据还原完成");
            dbOperation.setDataTypes(dataTypeStr);
            dbOperation.setOperationStatus(1);
            dbOperation.setEndTime(new Date());
            this.update(dbOperation);
            logger.info("服务重启中");
            restartService(dbOperation);
            return Global.OK;
        } catch (Exception e) {
            logger.error("数据还原失败",e);
            dbOperation.setOperationStatus(2);
            dbOperation.setMessage("系统异常：文件内容格式异常！");
            dbOperation.setEndTime(new Date());
        }
        this.update(dbOperation);
        return new Result("-1", "文件内容格式异常！");
    }

    @Override
    public Result restart(DbOperationInfo dbOperationInfo) {
        try {
            Map<String, DbBackupConfigInfo> dbBackupConfigMap = DbBackupConfig.getDbBackupConfigMap();
            Set<String> services = new HashSet<>();
            for (String dataType : dbOperationInfo.getDataTypes().split(",")) {
                services.addAll(Arrays.stream(dbBackupConfigMap.get(dataType).getRelateService().split(",")).collect(Collectors.toSet()));
            }
            for (String serviceName : services) {
                Runtime.getRuntime().exec("systemctl restart " + serviceName);
            }
            return Global.OK;
        } catch (Exception e) {
            logger.error("服务重启失败", e);
        }

        return Global.ERROR;
    }

    @Override
    public DbOperationInfo uploadFile(MultipartFile file) {
        DbOperationInfo dbOperationInfo = new DbOperationInfo();
        Date date = new Date();
        dbOperationInfo.setUuid(Uuid.uuid());
        String fileType = file.getOriginalFilename().split("_")[0];
        String fileName = fileType + "_" + new SimpleDateFormat("yyyyMMddHHmmss").format(date) + FILE_SUFFIX;
        dbOperationInfo.setFileName(fileName);
        dbOperationInfo.setDataTypes(fileType.replace("%26", ","));
        dbOperationInfo.setOperationType(3);
        dbOperationInfo.setOperationStatus(3);
        dbOperationInfo.setStartTime(date);
        dbOperationInfo.setFileStorage("本地");
        this.save(dbOperationInfo);

        String fileFullPath = Paths.get(filePath, CleanUtil.cleanString(fileName)).toString();
        InputStream in = null;
        try {
            in = new ByteArrayInputStream(file.getBytes());
            if (FileUtils.uploadFile(file, fileFullPath)) {
                dbOperationInfo.setFileMd5(DigestUtils.md5Hex(in));
                dbOperationInfo.setOperationStatus(1);
                dbOperationInfo.setEndTime(new Date());
                this.update(dbOperationInfo);
                return dbOperationInfo;
            } else {
                dbOperationInfo.setMessage("找不到文件");
            }
        } catch (IOException e) {
            logger.error("上传失败");
            dbOperationInfo.setMessage("系统异常：" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        dbOperationInfo.setOperationStatus(2);
        dbOperationInfo.setEndTime(new Date());
        this.update(dbOperationInfo);
        return dbOperationInfo;
    }

    @Override
    public Result downloadFile(String uuid, HttpServletResponse response) {
        List<DbOperationInfo> infoList = this.findByProperty(DbOperationInfo.class,"uuid",uuid);
        if (CollectionUtils.isEmpty(infoList)) {
            return new Result("-1", "无此备份记录");
        }
        DbOperationInfo dbOperationInfo = infoList.get(0);
        File file = new File(Paths.get(filePath, dbOperationInfo.getFileName()).toString());
        if (!file.exists()) {
            return new Result("-1", "文件已删除");
        }

        DbOperationInfo operationInfo = new DbOperationInfo();
        BeanUtils.copyProperties(dbOperationInfo, operationInfo);
        operationInfo.setUuid(Uuid.uuid());
        operationInfo.setOperationType(4);
        operationInfo.setOperationStatus(3);
        operationInfo.setStartTime(new Date());
        //operationInfo.setEndTime(null);
        this.save(operationInfo);

        if (FileUtils.downloadFile(Paths.get(filePath, dbOperationInfo.getFileName()).toString(), response)) {
            operationInfo.setOperationStatus(1);
            operationInfo.setEndTime(new Date());
            this.update(operationInfo);
            return Global.OK;
        }
        operationInfo.setOperationStatus(2);
        operationInfo.setEndTime(new Date());
        operationInfo.setMessage("文件异常");
        this.update(operationInfo);
        return new Result("-1", "文件下载失败");
    }

    public void restartService(DbOperationInfo dbOperationInfo) {
        FutureTask futureTask = new FutureTask<>(() -> {
            try {
                Map<String, DbBackupConfigInfo> dbBackupConfigMap = DbBackupConfig.getDbBackupConfigMap();
                Set<String> services = new HashSet<>();
                for (String dataType : dbOperationInfo.getDataTypes().split(",")) {
                    String serviceStr = dbBackupConfigMap.get(dataType).getRelateService();
                    if (StringUtils.isNotEmpty(serviceStr)) {
                        services.addAll(Arrays.stream(serviceStr.split(",")).collect(Collectors.toSet()));
                    }
                }

                boolean isAdminService = false;
                for (String serviceName : services) {
                    if (serviceName.equals("api-admin")) {
                        isAdminService = true;
                        continue;
                    }
                    Runtime.getRuntime().exec(CleanUtil.cleanString("systemctl restart " + serviceName));
                }
                if (isAdminService) {
                    Runtime.getRuntime().exec(CleanUtil.cleanString("systemctl restart api-admin"));
                }
            } catch (Exception e) {
                logger.error("服务重启失败", e);
            }
            return null;
        });
        Thread thread = new Thread(futureTask);
        thread.start();
    }
}
