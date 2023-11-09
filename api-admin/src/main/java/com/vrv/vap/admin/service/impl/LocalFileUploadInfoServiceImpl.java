package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.config.FileUploadConfig;
import com.vrv.vap.admin.model.FileUpLoadInfo;
import com.vrv.vap.admin.service.FileUploadInfoService;
import com.vrv.vap.base.BaseServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;

@Transactional
public class LocalFileUploadInfoServiceImpl extends BaseServiceImpl<FileUpLoadInfo> implements FileUploadInfoService {


    private static final Logger logger = LoggerFactory.getLogger(LocalFileUploadInfoServiceImpl.class);
    @Autowired
    private FileUploadConfig fileUploadConfig;

    @Override
    public String uploadFile(FileUpLoadInfo fileUpLoadInfo, MultipartFile multipartFile) {
        String pathBase = fileUpLoadInfo.getNamespace().replaceAll("\\.", "/");
        String filePath = Paths.get(fileUploadConfig.getPath(), pathBase).toString();
        logger.info("file Path：" + filePath);
        String fileName = fileUpLoadInfo.getFileId() + "." + fileUpLoadInfo.getFileType() ;
        FileOutputStream out = null;
        try {
            File targetFile = new File(filePath);
            if (!targetFile.exists()) {
                targetFile.mkdirs();
            }
            String path = Paths.get(filePath, fileName).toString();
            out = new FileOutputStream(path);
            out.write(multipartFile.getBytes());
            out.flush();
            return path;
        } catch (Exception e) {
            logger.info(e.getMessage());
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }


}
