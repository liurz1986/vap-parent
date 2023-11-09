package com.vrv.vap.monitor.server.common.config;

import com.vrv.vap.monitor.server.common.util.SpringContextUtil;
import com.vrv.vap.monitor.server.model.DbBackupConfigInfo;
import com.vrv.vap.monitor.server.service.DbBackupConfigService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class DbBackupConfig {

    private static Map<String, DbBackupConfigInfo> dbBackupConfigMap;

    private static List<String> tableNames = new ArrayList<>();

    //@Autowired
    //private DbBackupConfigService dbBackupConfigService;

    //@PostConstruct
    public static void getDbBackupConfig() {
        DbBackupConfigService dbBackupConfigService = SpringContextUtil.getBean(DbBackupConfigService.class);
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
