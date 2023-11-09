package com.vrv.vap.alarmdeal.business.threat.service;

import com.vrv.vap.alarmdeal.business.threat.bean.ThreatManage;
import com.vrv.vap.alarmdeal.business.threat.bean.fegin.ThreatTimeReq;
import com.vrv.vap.alarmdeal.business.threat.bean.fegin.ThreatVulRes;
import com.vrv.vap.alarmdeal.business.threat.bean.request.ThreatReq;
import com.vrv.vap.alarmdeal.business.threat.bean.response.AssetRiskInfoRes;
import com.vrv.vap.alarmdeal.business.threat.bean.response.AssetRiskRes;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;
import java.util.Map;

/**
 * @author: Administrator
 * @since: 2022/8/29 16:05
 * @description:
 */
public interface ThreatManageService{
    // 资产风险情况
    AssetRiskRes getAssetRisk(ThreatReq param);

    /**
     * 威胁资产
     * @param param
     * @return
     */
    List<AssetRiskInfoRes> getAssetThreatInfo(ThreatReq param, Integer top);

    /**
     *
     * @param param
     * @return
     */
    List<ThreatManage> getThreatData(Map<String,Object> param);

    /**
     *
     * @param param
     * @return
     */
    List<ThreatManage> getThreatDataByIp(Map<String,Object> param);

    /**
     * 通过时间和ip查询威胁与漏洞
     * @param threatTimeReq
     * @return
     */
    ThreatVulRes getThreatDataByIpTimes(ThreatTimeReq threatTimeReq);

    /**
     * 资产威胁情况
     * @param threatTimeReq
     * @return
     */
    List<String> getAssetThreatMsg(ThreatTimeReq threatTimeReq);
}
