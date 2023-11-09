package com.vrv.vap.admin.service;

import com.vrv.vap.admin.common.enums.LoginTypeEnum;
import com.vrv.vap.admin.common.enums.TypeEnum;
import com.vrv.vap.admin.model.User;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

public interface LoginService {
    boolean sessionLogin(HttpSession session, User currentUser, int loginType);

    String buildLoginForm(Map<String,String> params);

    void sendLog(User currentUser, HttpServletRequest request, TypeEnum type, LoginTypeEnum logType, int status, String methodType, String description);

    boolean validateHttpLicense();

    boolean validateXgsHttpLicense();

    boolean validateImportLicense(HttpSession session);

    Map<String, String> loginRedirect(HttpSession session, User currentUser);

    String getUIASDecodeUserInfo(Map encodeMap);

    public String getDefaultPWD();

}
