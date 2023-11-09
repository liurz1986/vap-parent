package com.vrv.vap.admin.common.config;

import com.vrv.vap.admin.model.DbBackupConfigInfo;
import com.vrv.vap.admin.service.DbBackupConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DbBackupConfig {

    private static Map<String, DbBackupConfigInfo> dbBackupConfigMap;

    private static List<String> tableNames = new ArrayList<>();

    @Autowired
    private DbBackupConfigService dbBackupConfigService;

    @PostConstruct
    public void getDbBackupConfig() {
        List<DbBackupConfigInfo> dbBackupConfigInfos = dbBackupConfigService.findAll();
        dbBackupConfigMap = dbBackupConfigInfos.stream().collect(Collectors.toMap(DbBackupConfigInfo::getDataType,p -> p,(k1,k2) -> k1));
        for (DbBackupConfigInfo dbBackupConfigInfo : dbBackupConfigInfos) {
            if (StringUtils.isNotEmpty(dbBackupConfigInfo.getTableName())) {
                for (String table : dbBackupConfigInfo.getTableName().split(",")) {
                    tableNames.add(table);
                }
            }
        }
    }

    public static Map<String, DbBackupConfigInfo> getDbBackupConfigMap() {
        return dbBackupConfigMap;
    }

    public static List<String> getTableNames() {
        return tableNames;
    }

}
