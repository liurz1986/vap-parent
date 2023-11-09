package com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.impl;

import com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.BusinessInteractionMode;
import com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.BusinessParamVO;
import com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.impl.httpRequest.BusinessHttpResponseVO;
import com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType.impl.httpRequest.BusinessInfoRequest;
import com.vrv.vap.jpa.req.HttpSyncRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * http交互方式的实现
 * @author wudi
 * @date 2022/11/16 14:19
 */
@Service
public class HttpInteractionModeImpl implements BusinessInteractionMode {

     private static Logger logger = LoggerFactory.getLogger(HttpInteractionModeImpl.class);

     @Autowired
     private HttpSyncRequest httpSyncRequest;

     @Override
     public void interationModeImpl(BusinessParamVO businessParamVO) {
          String url = businessParamVO.getExtraParamURL();
          String extraParamType = businessParamVO.getExtraParamType();
          String requestParam = businessParamVO.getRequestParam();
          logger.info("url:{},extraParamType:{},requestParam:{}",url,extraParamType,requestParam);
          BusinessInfoRequest businessInfoRequest = new BusinessInfoRequest(url,extraParamType,requestParam);
          BusinessHttpResponseVO result = httpSyncRequest.getResult(businessInfoRequest);
          logger.info("http request result is {}",result.getData());
     }
}
