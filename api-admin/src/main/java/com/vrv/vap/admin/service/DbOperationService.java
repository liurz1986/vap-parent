package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.DbOperationInfo;
import com.vrv.vap.base.BaseService;
import com.vrv.vap.common.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

public interface DbOperationService extends BaseService<DbOperationInfo> {

    Result backup(DbOperationInfo dbOperationInfo);

    Result recovery(DbOperationInfo dbOperationInfo);

    Result restart(DbOperationInfo dbOperationInfo);

    DbOperationInfo uploadFile(MultipartFile file);

    Result downloadFile(String uuid, HttpServletResponse response);
}
