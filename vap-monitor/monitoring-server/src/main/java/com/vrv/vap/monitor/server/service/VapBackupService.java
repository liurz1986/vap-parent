package com.vrv.vap.monitor.server.service;

import com.vrv.vap.monitor.server.vo.BackupTableVO;

import java.util.List;
import java.util.Map;

public interface VapBackupService {

    List<Map> getInfo(String tableName);

    void insertInfo(BackupTableVO backupTableVO, List<Map<String, Object>> rows);

    void clearRows(String tableName);
}
