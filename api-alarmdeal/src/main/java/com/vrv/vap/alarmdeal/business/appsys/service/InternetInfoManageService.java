package com.vrv.vap.alarmdeal.business.appsys.service;


import com.vrv.vap.alarmdeal.business.appsys.model.InternetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.vo.InternetInfoManageVo;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/10
 */

public interface InternetInfoManageService extends AbstractBaseService<InternetInfoManage,Integer> {

    public PageRes<InternetInfoManage> getInternetInfoManagePage(InternetInfoManageVo internetInfoManageVo);

    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file);
}
