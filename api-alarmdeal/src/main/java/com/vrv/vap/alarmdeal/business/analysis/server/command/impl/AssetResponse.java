package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.analysis.model.AlarmResponseLog;
import com.vrv.vap.alarmdeal.business.analysis.vo.ResponseTypeVO;
import com.vrv.vap.alarmdeal.business.asset.service.AssetService;
import com.vrv.vap.alarmdeal.business.asset.vo.AssetDetailVO;
import com.vrv.vap.alarmdeal.frameworks.contract.user.User;
import com.vrv.vap.alarmdeal.frameworks.feign.FeignCache;
import com.vrv.vap.alarmdeal.business.analysis.server.impl.AlarmReponseLogService;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Service
public class AssetResponse {

    private static Logger logger=LoggerFactory.getLogger(AssetResponse.class);

    @Autowired
    private AlarmReponseLogService alarmReponseLogService;

    @Autowired
    private MapperUtil mapperUtil;

    @Autowired
    private FeignCache feignCache;

    @Autowired
    private AssetService assetService;


    /**
     *资产责任人
     */
    public Set<ResponseTypeVO> getAssetResponsibility(String assetGuids, String alarmName, String type){
        AlarmResponseLog alarmResponseLog = contructAlarmResponseLog(alarmName, type);
        Set<ResponseTypeVO> set=new HashSet<>();
        if(StringUtils.isEmpty(assetGuids)){
            alarmResponseLog.setGuid(UUID.randomUUID().toString());
            alarmResponseLog.setResponseReason("未关联上资产");
            alarmReponseLogService.save(alarmResponseLog);
        }else{
            String[] assetGuidArray=assetGuids.split(",");
            for (String assetGuid : assetGuidArray){
                AssetDetailVO assetDetailVO = assetService.getAssetDetail(assetGuid);
                logger.info("AssetDetailVO："+ JSON.toJSONString(assetDetailVO));
                String userIds = assetDetailVO.getAsset().getEmployeeGuid();
                if (StringUtils.isEmpty(userIds)) {
                    alarmResponseLog.setGuid(UUID.randomUUID().toString());
                    alarmResponseLog.setResponseReason("未匹配的资产责任人");
                    alarmReponseLogService.save(alarmResponseLog);
                    continue;
                }
                String[] userIdArray=userIds.split(",");
                Set<ResponseTypeVO> assetUser=new HashSet<>();
                for(String userId : userIdArray){
                    User user = feignCache.getUserById(userId);
                    if(user!=null) {
	                    logger.info("User："+ JSON.toJSONString(user));
	                    ResponseTypeVO responseTypeVO=mapperUtil.map(user,ResponseTypeVO.class);
	                    assetUser.add(responseTypeVO);
                    }
                }
                set.addAll(assetUser);
            }
        }

        return  set;
    }

    public AlarmResponseLog contructAlarmResponseLog(String alarmName, String type) {
        AlarmResponseLog alarmResponseLog=new AlarmResponseLog();
        alarmResponseLog.setAlarmName(alarmName);
        alarmResponseLog.setResponseResult("发送失败");
        alarmResponseLog.setResponseType(type);
        return alarmResponseLog;
    }



}
