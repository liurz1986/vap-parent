package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.vrv.vap.alarmdeal.business.analysis.server.command.DealCommandService;
import com.vrv.vap.alarmdeal.business.analysis.vo.SoarDataVO;
import com.vrv.vap.alarmdeal.business.analysis.vo.SoarVO;
import com.vrv.vap.alarmdeal.frameworks.contract.soar.SoarScript;
import com.vrv.vap.alarmdeal.frameworks.contract.soar.SoarScriptTask;
import com.vrv.vap.alarmdeal.frameworks.feign.SoarFegin;
import com.vrv.vap.jpa.json.JsonMapper;
import com.vrv.vap.jpa.web.ResultObjVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 远程调用实现方式
 * @author wd-pc
 *
 */
@Service("soarRelateCommandService")
public class SoarRelateCommandServiceImpl implements DealCommandService<SoarVO> {


    private static Logger logger = LoggerFactory.getLogger(SoarRelateCommandServiceImpl.class);

    @Autowired
    private SoarFegin soarFegin;


    @Override
    public void executeResponseCommond(List<SoarVO> soarVOListList){
    	if(soarVOListList.size()==1) {
    		SoarVO soarVO = soarVOListList.get(0);
    		sendSoar(soarVO);
    	}
    }

    /**
     * soar
     * 通知
     */
    private void sendSoar(SoarVO soarVO){
    	Map<String, Object> map = soarVO.getScriptParams();
    	String scriptCode = soarVO.getScriptCode();
    	ResultObjVO<SoarScript> soarScriptByCode = soarFegin.getSoarScriptByCode(scriptCode);
    	SoarScript soarScript = soarScriptByCode.getData();
    	if(soarScript!=null) {
    		executeSoarCommand(map, soarScript);
    	}else  {
    		ResultObjVO<SoarScript> curSoarScriptByCode = soarFegin.getCurSoarScriptByCode(scriptCode);
    		SoarScript data = curSoarScriptByCode.getData();
    		if(data!=null) {
    			executeSoarCommand(map, data);
    		}else {
    			logger.info("不存在该剧本，剧本code为：{}",scriptCode);    			
    		}
    		
    	}
    	
    }

	private void executeSoarCommand(Map<String, Object> map, SoarScript soarScript) {
		String guid = soarScript.getGuid();
		String soarVOStr = JsonMapper.toJsonString(map);
		logger.info("阻断入参数据：{}",soarVOStr);
		SoarDataVO soarDataVO = new SoarDataVO();
		soarDataVO.setScriptGuid(guid);
		soarDataVO.setForms(map);
		try {
			ResultObjVO<SoarScriptTask> resultObjVO = soarFegin.start(soarDataVO);
			SoarScriptTask soarScriptTask = resultObjVO.getData();
			String jsonString = JsonMapper.toJsonString(soarScriptTask);
			logger.info("调用soar返回发送返回结果：{}",jsonString);
		}catch(Exception e) {
			logger.error("soar的执行接口出现问题，报错原因：{}",e.getMessage());
		}
	}
    
    
   
    
    
}
