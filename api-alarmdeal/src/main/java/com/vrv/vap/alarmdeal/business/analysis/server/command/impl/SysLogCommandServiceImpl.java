package com.vrv.vap.alarmdeal.business.analysis.server.command.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.alarmdeal.business.analysis.server.command.DealCommandService;
import com.vrv.vap.alarmdeal.frameworks.contract.syslog.ResultBody;
import com.vrv.vap.alarmdeal.frameworks.contract.syslog.SysLogBO;
import com.vrv.vap.alarmdeal.frameworks.contract.syslog.SysLogVO;
import com.vrv.vap.alarmdeal.frameworks.feign.ServerSystemFegin;
import com.vrv.vap.utils.dozer.MapperUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysLogCommandServiceImpl implements DealCommandService<SysLogVO> {


    private static Logger logger = LoggerFactory.getLogger(SysLogCommandServiceImpl.class);

    @Autowired
    private ServerSystemFegin serverSysFeign;

    @Autowired
    private MapperUtil mapperUtil;

    @Override
    public void executeResponseCommond(List<SysLogVO> smsVOListList){
        sendSyslog(smsVOListList);
    }

    /**
     * 短信通知
     */
    private void sendSyslog(List<SysLogVO> smsVOListList){
        for(SysLogVO sysLogVO :smsVOListList){
            try {
                SysLogBO sysLogBO=mapperUtil.map(sysLogVO,SysLogBO.class);
                ResultBody result= serverSysFeign.addSysLog(sysLogBO);
                logger.info("sysLog发送结果: "+ JSON.toJSONString(result)+","+JSON.toJSONString(sysLogBO));

            } catch (Exception e) {
                logger.info("sysLog发送失败: "+e.getMessage());
            }
        }
    }
}
