package com.vrv.vap.alarmdeal.business.appsys.service.query;

import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoNewVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppInfoVO;
import com.vrv.vap.alarmdeal.business.appsys.vo.query.AppQueryTotalVO;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import com.vrv.vap.jpa.web.NameValue;

import java.util.List;


public interface AppQueryService {
    /**
     * 应用总数
     * @return
     */

    AppQueryTotalVO appQueryTotal();

    /**
     * 应用数量按密集等级统计
     * @return
     */

    List<AssetStatisticsVO> queryAppSecretlevelTotal();

    /**
     * 应用信息统计
     * @return
     */
    List<AppInfoVO> queryAppInfoTotal();
    /**
     * 应用信息统计列表
     * 2023-07-04
     * *@return
     */
    List<AppInfoNewVO> queryAppTabulation();
    List<NameValue> countAppSecretStatistics();

    List<NameValue> countAppOgrStatistics();

    List<NameValue> countAppRoleStatistics();
}
