package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.vrv.vap.alarmdeal.business.appsys.dao.InternetInfoManageQueryDao;
import com.vrv.vap.alarmdeal.business.appsys.service.query.InternetInfoManageQueryService;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.InternetInfoManageQueryVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 互联信息报表接口
 */
@Service
public class InternetInfoManageQueryServiceImpl implements InternetInfoManageQueryService {

    @Autowired
    private InternetInfoManageQueryDao InternetInfoManageQueryDao;

    // 获取防护等级配置的parenttype的值
    @Value("${protectionLevel.parentType.internetInfo:f5a4ae5b-3cee-a84f-7471-8f23ezjg1200}")
    private String protectLevelParentType;

    //获取涉密等级配置的parenttype的值
    @Value("${classifiedLevel.parentType.internetInfo:f5a4ae5b-3cee-a84f-7471-8f23ezjg1100}")
    private String secretlevelParentType;

    @Override
    public List<InternetInfoManageQueryVO> tabulation() {
        return InternetInfoManageQueryDao.tabulation(protectLevelParentType,secretlevelParentType);
    }
}
