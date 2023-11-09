package com.vrv.vap.monitor.server.service;

import com.vrv.vap.base.BaseService;
import com.vrv.vap.monitor.server.model.DbOperationInfo;
import com.vrv.vap.monitor.server.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface DbOperationService extends BaseService<DbOperationInfo> {

    Result backup(DbOperationInfo dbOperationInfo);

    Result recovery(DbOperationInfo dbOperationInfo);

    Result restart(DbOperationInfo dbOperationInfo);

    DbOperationInfo uploadFile(MultipartFile file);

    Result downloadFile(String uuid, HttpServletResponse response);
}
