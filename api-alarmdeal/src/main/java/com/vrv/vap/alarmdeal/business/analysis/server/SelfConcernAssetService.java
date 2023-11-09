package com.vrv.vap.alarmdeal.business.analysis.server;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.bean.SelfConcernAsset;
import com.vrv.vap.jpa.baseservice.BaseService;

import java.util.List;

public interface SelfConcernAssetService extends BaseService<SelfConcernAsset, String> {
    List<SelfConcernAsset> getSelfConcernAssets(SelfConcernAsset selfConcernAsset);
    Long getCountByUserIP(String userId,Integer type,String Ip);
}
