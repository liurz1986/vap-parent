package com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.impl;

import com.google.gson.Gson;
import com.vrv.vap.alarmModel.model.WarnResultLogTmpVO;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.service.AlarmNoticeService;
import com.vrv.vap.alarmdeal.business.analysis.server.command.ResponseCommandInvoker;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.AlarmInfoMergerHandler;
import com.vrv.vap.alarmdeal.business.analysis.server.core.mergeStream.RuleMergeHandler;
import com.vrv.vap.alarmdeal.business.analysis.server.core.service.AlarmLogDesc;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmquery.service.RiskEventRuleService;
import com.vrv.vap.alarmdeal.business.analysis.server.strategy.SelectRelateStrategy;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.AlarmNotice;
import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.BlockVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.RuleInfoVO;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Map;


@Service
public  class AlarmNoticeServiceImpl implements AlarmNoticeService {

    private static Logger logger =  LoggerFactory.getLogger(AlarmNoticeService.class);

    @Autowired
    private ResponseCommandInvoker responseCommandInvoker;

    @Autowired
    private AlarmInfoMergerHandler alarmInfoMergerHandler;


    /**
     * 告警短信、邮件通知
     */
     @Override
     public  boolean  sendNoticeList(Map<String,WarnResultLogTmpVO> map){
             for(Map.Entry<String,WarnResultLogTmpVO> entry : map.entrySet()) {
                 WarnResultLogTmpVO warnResultLogTmpVO=entry.getValue();
                 sendNotice(warnResultLogTmpVO);
             }
         return  true;
     }

    /**
     * 短信、邮件,联动响应响应器
     */
    @Override
    public  boolean  sendNotice(WarnResultLogTmpVO warnResultLogTmpVO){
        RuleMergeHandler ruleMergeHandler=alarmInfoMergerHandler.chooseRuleMergeHandler(warnResultLogTmpVO.getRuleCode());
        RuleInfoVO ruleInfoVO=null;
        if(ruleMergeHandler!=null){
            ruleInfoVO=ruleMergeHandler.getRuleInfoVO();
        }else{
            return  false;
        }
        String  extend2=ruleInfoVO.getExtend2();
        logger.info("告警描述替换前："+extend2);
        AlarmNotice alarmNotice = null;
        if(StringUtils.isNotBlank(extend2)){
            extend2 = alarmInfoMergerHandler.replaceHolder(warnResultLogTmpVO,extend2); //占位符替换
            logger.info("告警描述替换后："+extend2);
            alarmNotice =new Gson().fromJson(extend2, AlarmNotice.class);
        }

        if(alarmNotice==null){
            logger.info("告警响应配置异常");
            return  false;
        }
        responseCommandInvoker.executeResponseCommand(alarmNotice);
        return true;
    }

    /**
     * 构造AlarmNotice
     * @param warnResultLogTmpVO
     * @param alarmNotice
     */
	private void constructAlarmNotice(WarnResultLogTmpVO warnResultLogTmpVO, AlarmNotice alarmNotice) {
		if(alarmNotice.getCkBlockInfo().equals(AlarmNotice.OPEN)&&alarmNotice.getBlockVO()!=null){
			   BlockVO blockVO = alarmNotice.getBlockVO();
			   String blockIpKey = blockVO.getBlockIp();
			   try {
				Field blockIpField = warnResultLogTmpVO.getClass().getDeclaredField(blockIpKey);
				// blockIpField.setAccessible(true);
                ReflectionUtils.makeAccessible(blockIpField);
				Object object = blockIpField.get(warnResultLogTmpVO);
				if(object!=null) {
					blockVO.setBlockIp(object.toString());
				}
			} catch (NoSuchFieldException | SecurityException |IllegalArgumentException | IllegalAccessException e) {
				e.printStackTrace();
			}
			   alarmNotice.setBlockVO(blockVO);
		}
		if(warnResultLogTmpVO.getAssetInfo()!=null){
            alarmNotice.setAssetGuids(warnResultLogTmpVO.getAssetInfo().get("assetguids").toString()); //关联资产
        }
        alarmNotice.setName(warnResultLogTmpVO.getRuleName());
	}

}
