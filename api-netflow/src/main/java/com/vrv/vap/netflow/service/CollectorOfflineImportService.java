package com.vrv.vap.netflow.service;

import com.vrv.vap.netflow.common.enums.ErrorCode;
import org.springframework.web.multipart.MultipartFile;

public interface CollectorOfflineImportService {

    public ErrorCode importData(MultipartFile file, Integer type, Integer templateId);
}
