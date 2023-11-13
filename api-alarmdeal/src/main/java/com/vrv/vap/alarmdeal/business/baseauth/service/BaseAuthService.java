package com.vrv.vap.alarmdeal.business.baseauth.service;

import com.vrv.vap.jpa.web.Result;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 功能描述
 *
 * @author liangguolu
 * @date 2021年12月24日 15:20
 */
public interface BaseAuthService {

    Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file, Integer code);

    void saveList(Map<String, Object> map);

    Result<String> exportInfo(Map<String, Object> map);
}
