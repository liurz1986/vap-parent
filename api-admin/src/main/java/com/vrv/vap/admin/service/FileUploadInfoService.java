package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.FileUpLoadInfo;
import com.vrv.vap.base.BaseService;
import org.springframework.web.multipart.MultipartFile;



public interface FileUploadInfoService extends BaseService<FileUpLoadInfo> {
     String  uploadFile(FileUpLoadInfo fileUpLoadInfo ,MultipartFile multipartFile) ;
}
