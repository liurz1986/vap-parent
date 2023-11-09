package com.vrv.vap.admin.service.impl;


import com.vrv.vap.admin.common.util.FastDFSUtils;
import com.vrv.vap.admin.model.FileUpLoadInfo;
import com.vrv.vap.admin.service.FileUploadInfoService;
import com.vrv.vap.base.BaseServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Transactional
public class FastdfsFileUploadInfoServiceImpl extends BaseServiceImpl<FileUpLoadInfo> implements FileUploadInfoService {

    @Autowired
    FastDFSUtils fastDFSUtils;

    @Override
    public String uploadFile(FileUpLoadInfo fileUpLoadInfo,MultipartFile multipartFile) {
        try {
            String path = fastDFSUtils.uploadFile(multipartFile);
            return path;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
