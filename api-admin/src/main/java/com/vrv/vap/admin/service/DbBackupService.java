package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.DbTaskInfo;
import com.vrv.vap.admin.vo.TaskVO;
import com.vrv.vap.base.BaseService;
import com.vrv.vap.common.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface DbBackupService extends BaseService<DbTaskInfo> {

    Map<String, Object> backup(DbTaskInfo dbTaskInfo);

    Map<String, Object> recovery(DbTaskInfo dbTaskInfo);

    Result deleteFile(TaskVO taskVO);

    void restart();

    Result importFile(TaskVO taskVO);
}
