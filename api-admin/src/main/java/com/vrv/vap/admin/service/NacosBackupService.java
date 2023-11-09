package com.vrv.vap.admin.service;

import com.vrv.vap.admin.vo.BackupTableVO;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface NacosBackupService {

    List<Map> getInfo(String tableName);

    void insertInfo(BackupTableVO backupTableVO, List<Map<String,Object>> rows);

    void clearRows(String tableName);
}
