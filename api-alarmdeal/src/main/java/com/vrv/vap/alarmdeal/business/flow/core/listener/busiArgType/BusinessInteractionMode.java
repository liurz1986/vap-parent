package com.vrv.vap.alarmdeal.business.flow.core.listener.busiArgType;

/**
 * 业务交互方式
 * @author wudi
 * @date 2022/11/16 14:16
 */
public interface BusinessInteractionMode {

 /**
  *交互方式具体实现
  */
 public void interationModeImpl(BusinessParamVO businessParamVO);

}
