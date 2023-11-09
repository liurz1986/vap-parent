package com.vrv.vap.alarmdeal.business.asset.service.impl.query;

import com.vrv.vap.alarmdeal.business.appsys.service.AppSysManagerService;
import com.vrv.vap.alarmdeal.business.asset.dao.AssetDao;
import com.vrv.vap.alarmdeal.business.asset.service.query.ZkLargeScreenService;
import com.vrv.vap.alarmdeal.business.asset.vo.query.ZkLargeSearchVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * cas大屏
 * @author liurz
 */
@Service
public class ZkLargeScreenServiceImpl implements ZkLargeScreenService {
    @Autowired
    private AssetDao assetDao;
    @Autowired
    private AppSysManagerService appSysManagerService;

    /**
     * 服务器：资产中一级资产类型为服务器的资产总数
     * 终端总数：资产中一级资产类型为终端的资产总数
     * 应用系统数：应用系统表总数
     * @return
     */
    @Override
    public ZkLargeSearchVO queryOverview() {
        ZkLargeSearchVO data = new ZkLargeSearchVO();
        // 终端总数
        int hostTotal = assetDao.getAssetTotalByGroupType("asset-Host");
        data.setTerminalTotal(hostTotal);
        // 服务器总数
        int serverTotal = assetDao.getAssetTotalByGroupType("asset-service");
        data.setServerCount(serverTotal);
        // 应用系统数量
        long appcount= appSysManagerService.count();
        data.setAppCount(appcount);
        return data;
    }
}
