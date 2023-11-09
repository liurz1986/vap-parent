package com.vrv.vap.admin.service;

import com.vrv.vap.admin.vo.AuthorizationVO;

public interface AuthorizationService {
    void resetAuthorizationStatus(String status);
    AuthorizationVO getAuthorizationInfo();
    AuthorizationVO resetAuthorizationfInfo();

    String getHardwareSerialNo();
}
