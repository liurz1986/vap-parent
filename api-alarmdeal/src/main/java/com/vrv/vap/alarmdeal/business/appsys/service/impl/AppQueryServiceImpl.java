package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.vrv.vap.alarmdeal.business.appsys.dao.AppSysManagerDao;
import com.vrv.vap.alarmdeal.business.appsys.service.query.AppQueryService;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoNewVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppQueryTotalVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.jpa.web.NameValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 应用系统报表接口
 */
@Service
public class AppQueryServiceImpl implements AppQueryService {
    @Autowired
    private AppSysManagerDao appSysManagerDao;

    @Value("${classifiedLevel.parentType.app:f5a4ae5b-3cee-a84f-7471-8f23ezjg1100}")
    private String appParentType;   //获取涉及等级配置的parenttype的值

    /**
     * 应用总数
     *
     * @return
     */
    @Override
    public AppQueryTotalVO appQueryTotal() {
        return appSysManagerDao.appQueryTotal(appParentType);
    }

    /**
     * 应用数量按密集等级统计
     *
     * @return
     */
    @Override
    public  List<AssetStatisticsVO> queryAppSecretlevelTotal() {
        List<AssetStatisticsVO> datas = appSysManagerDao.queryAppSecretlevelTotal(appParentType);
        return datas;

    }


    /**
     * 应用信息统计
     *
     * @return
     */
    @Override
    public List<AppInfoVO> queryAppInfoTotal() {
        return appSysManagerDao.queryAppInfoTotal(appParentType);
    }
    /**
     * 应用信息统计列表
     * 2023-07-04
     * *@return
     */
    @Override
    public List<AppInfoNewVO> queryAppTabulation() {
        return appSysManagerDao.queryAppTabulation(appParentType);
    }

    @Override
    public List<NameValue> countAppSecretStatistics() {
        List<NameValue> datas = appSysManagerDao.countAppSecretStatistics(appParentType);
        return datas;
    }

    @Override
    public List<NameValue> countAppOgrStatistics() {
        List<NameValue> datas = appSysManagerDao.countAppOgrStatistics();
        return datas;
    }

    @Override
    public List<NameValue> countAppRoleStatistics() {
        List<NameValue> datas = appSysManagerDao.countAppRoleStatistics();
        return datas;
    }
}
