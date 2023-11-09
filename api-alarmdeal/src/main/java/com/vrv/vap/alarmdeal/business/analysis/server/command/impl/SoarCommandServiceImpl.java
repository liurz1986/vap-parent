package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.vrv.vap.alarmdeal.business.alaramevent.alarmeventdeal.vo.BlockVO;
import com.vrv.vap.alarmdeal.business.analysis.server.command.DealCommandService;
import com.vrv.vap.alarmdeal.business.analysis.vo.CallLinkageVO;
import com.vrv.vap.alarmdeal.frameworks.feign.SoarFegin;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.ResultObjVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 远程调用实现方式
 * @author wd-pc
 *
 */
@Service
public class SoarCommandServiceImpl implements DealCommandService<BlockVO> {


    private static Logger logger = LoggerFactory.getLogger(SoarCommandServiceImpl.class);

    @Autowired
    private SoarFegin soarFegin;


    @Override
    public void executeResponseCommond(List<BlockVO> blockVOListList){
    	if(blockVOListList.size()==1) {
    		BlockVO blockVO = blockVOListList.get(0);
    		sendSoar(blockVO);
    	}
    }

    /**
     * 短信通知
     */
    private void sendSoar(BlockVO blockVO){
    	String blockVOStr = JsonMapper.toJsonString(blockVO);
    	logger.info("阻断入参数据：{}",blockVOStr);
    	try {
    		CallLinkageVO callLinkageVO = new CallLinkageVO();
    		callLinkageVO.setGuid(blockVO.getGuid());
    		callLinkageVO.setParams(JsonMapper.toJsonString(blockVO.getParam()));
    		ResultObjVO<String> result = soarFegin.callLinkageRule(callLinkageVO);
    		String data = result.getData();
    		logger.info("设备联动发送返回结果：{}",data);
    	}catch(Exception e) {
    		logger.error("callLinkageRule接口出现问题，报错原因：{}",e.getMessage());
    	}
    }
}
