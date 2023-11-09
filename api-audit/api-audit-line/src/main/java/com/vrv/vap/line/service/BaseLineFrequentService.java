package com.vrv.vap.line.service;

import com.vrv.vap.line.model.BaseLineFrequent;

import java.util.List;

public interface BaseLineFrequentService {

    List<BaseLineFrequent> findUserFrequent(String key);

    BaseLineFrequent findByUser(String userKey);

    BaseLineFrequent findByUserAndSysid(String user,String sysId);

    void updateFrequent(BaseLineFrequent frequent);

    List<BaseLineFrequent> queryByCondition(String column ,String org);

}
