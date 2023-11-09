package com.vrv.vap.alarmdeal.business.appsys.service;

import com.vrv.vap.alarmdeal.business.appsys.model.NetInfoManage;
import com.vrv.vap.alarmdeal.business.appsys.vo.NetInfoManageVo;
import com.vrv.vap.jpa.web.page.PageRes;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author lps 2021/8/10
 */

public interface NetInfoManageService extends AbstractBaseService<NetInfoManage,Integer> {

    PageRes<NetInfoManage> getNetInfoManagePage(@RequestBody NetInfoManageVo netInfoManageVo);

    /**
     * 导入数据校验
     * @param file
     * @return
     */
    public Map<String, List<Map<String, Object>>> checkImportData(MultipartFile file);
}
