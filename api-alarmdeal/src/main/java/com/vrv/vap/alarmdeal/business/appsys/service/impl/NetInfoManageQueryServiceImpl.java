package com.vrv.vap.alarmdeal.business.appsys.service.impl;

import com.vrv.vap.alarmdeal.business.appsys.dao.NetInfoManageQueryDao;
import com.vrv.vap.alarmdeal.business.appsys.service.NetInfoManageService;
import com.vrv.vap.alarmdeal.business.appsys.service.query.NetInfoManageQueryService;
import com.vrv.vap.alarmdeal.business.asset.vo.query.AssetStatisticsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 网路信息报表接口
 */
@Service
public class NetInfoManageQueryServiceImpl implements NetInfoManageQueryService {
    // 获取防护等级配置的parenttype的值
    @Value("${protectionLevel.parentType.network:f5a4ae5b-3cee-a84f-7471-8f23ezjg1200}")
    private String protectLevelParentType;

    //获取涉密等级配置的parenttype的值
    @Value("${classifiedLevel.parentType.network:f5a4ae5b-3cee-a84f-7471-8f23ezjg1100}")
    private String secretlevelParentType;
    @Autowired
    private NetInfoManageQueryDao netInfoManageQueryDao;
    @Autowired
    private NetInfoManageService netInfoManageService;
    /**
     * 网络基本情况汇总
     * 部门共计5个网络(实际就是多少条记录)
     * *@return
     */
    @Override
    public Map<String, Object> baseInfo() {
        Map<String, Object> result = new HashMap<>();
        long total=  netInfoManageService.count();
        result.put("totalNum",total);
        return result;
    }
    /**
     * 网络类型划分(局域网、广域网)
     * 2023-07-04
     * *@return
     */
    @Override
    public List<AssetStatisticsVO> netInfoType() {
        return netInfoManageQueryDao.netInfoType();
    }
    /**
     * 网络密级统计
     * 2023-07-04
     * *@return
     */
    @Override
    public List<AssetStatisticsVO> secretlevelTotal() {
        return netInfoManageQueryDao.secretlevelTotal(secretlevelParentType);
    }
    /**
     * 网络防护等级统计
     * 2023-07-04
     * *@return
     */
    @Override
    public List<AssetStatisticsVO> protectLevelTotal() {
        return netInfoManageQueryDao.protectLevelTotal(protectLevelParentType);
    }

    /**
     * 网络按安全域统计
     * @return
     */
    @Override
    public List<AssetStatisticsVO> domainTotal() {
        return netInfoManageQueryDao.domainTotal();
    }
}
