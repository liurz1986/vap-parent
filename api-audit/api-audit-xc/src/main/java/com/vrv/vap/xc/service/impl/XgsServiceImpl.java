package com.vrv.vap.xc.service.impl;

import com.vrv.vap.xc.mapper.rds2.WhiteListMapper;
import com.vrv.vap.xc.service.XgsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
@Transactional
public class XgsServiceImpl implements XgsService {

    @Autowired
    private WhiteListMapper whiteListDao;

    @Override
    public Map<String, Object> queryWhiteListStatistic() {
        Map<String, Object> result = new HashMap<>();
        int netDevice = whiteListDao.queryWhiteListStatistic("12");
        int safeDevice = whiteListDao.queryWhiteListStatistic("345");
        int safeSecurityDevice = whiteListDao.queryWhiteListStatistic("1-5");
        result.put("netDevice", netDevice);
        result.put("safeDevice", safeDevice);
        result.put("safeSecurityDevice", safeSecurityDevice);
        return result;
    }
}
