package com.vrv.vap.admin.common.task;

import com.vrv.vap.admin.service.AuthorizationService;
import com.vrv.vap.admin.service.LicenseService;
import com.vrv.vap.admin.vo.AuthorizationVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class LiceseStatusCheckTask {

       @Autowired
       private AuthorizationService authorizationService;

       @Autowired
       private LicenseService licenseService;

       @Value("${licence.imported:true}")
       private boolean importLicense;


        @Scheduled(cron = "0 0 0/1 * * ?")
        public void licenseStatusCheck() {
            if (importLicense) {
                AuthorizationVO authorizationVO = authorizationService.getAuthorizationInfo();
                if(!authorizationVO.getStatus()){
                    authorizationService.resetAuthorizationStatus("fail");
                }else{
                    authorizationService.resetAuthorizationStatus("success");
                }
                licenseService.updateResourceByLicense();
            }
        }
    }

