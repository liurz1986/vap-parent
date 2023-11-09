package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.business.appsys.model.DataInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.vo.DataInfoManageVo;
import com.vrv.vap.jpa.web.Result;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/10
 */

public interface DataInfoManageService extends AbstractBaseService<DataInfoManage,Integer> {

    PageRes<DataInfoManage> getDataInfoManagePage(DataInfoManageVo dataInfoManageVo);


    public boolean validateFileSize(String value);

    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file);

    public  Result<List<Map<String, Object>>> getFilesAuth();
}
