package com.vrv.vap.monitor.server.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.monitor.server.model.DbTaskInfo;
import com.vrv.vap.monitor.server.vo.Result;
import com.vrv.vap.monitor.server.vo.TaskVO;

import java.util.Map;

public interface DbBackupService extends BaseService<DbTaskInfo> {

    Map<String, Object> backup(DbTaskInfo dbTaskInfo);

    Map<String, Object> recovery(DbTaskInfo dbTaskInfo);

    Result deleteFile(TaskVO taskVO);

    void restart();

    Result importFile(TaskVO taskVO);
}
