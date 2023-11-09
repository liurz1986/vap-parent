package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.SuperviseDataReceive;
import com.vrv.vap.base.BaseService;
import com.vrv.vap.common.vo.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SuperviseDataReceiveService extends BaseService<SuperviseDataReceive> {
    Result importAnnounce(MultipartFile file);

    Result saveAnnounce(Map info);
}
