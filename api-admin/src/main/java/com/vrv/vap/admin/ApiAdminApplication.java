package com.vrv.vap.admin;

import com.github.tobato.fastdfs.FdfsClientConfig;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import com.vrv.vap.admin.common.excel.out.Export;
import com.vrv.vap.admin.common.manager.TaskManager;
import com.vrv.vap.admin.service.AuthorizationService;
import com.vrv.vap.admin.service.BaseKoalOrgService;
import com.vrv.vap.admin.service.BaseSecurityDomainService;
import com.vrv.vap.admin.service.LicenseService;
import com.vrv.vap.admin.vo.AuthorizationVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.session.web.http.DefaultCookieSerializer;

@EnableDiscoveryClient
@EnableHystrix
@EnableFeignClients
@Import(FdfsClientConfig.class)
@EnableScheduling
@SpringBootApplication(scanBasePackages = {"com.vrv.vap"})
@EnableEncryptableProperties
public class ApiAdminApplication {


    private static boolean importLicense;

    @Value("${vap.common.session-base64:false}")
    private Boolean sessionBase64;

    public static void main(String[] args) {
        ApplicationContext appCtx = SpringApplication.run(ApiAdminApplication.class, args);
        run(appCtx);
    }


    public static void run(ApplicationContext appCtx){
        try {
            TaskManager.loadTask();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LicenseService licenseService = appCtx.getBean(LicenseService.class);
        AuthorizationService authorizationService= appCtx.getBean(AuthorizationService.class);
        if (importLicense) {
            AuthorizationVO authorizationVO = authorizationService.getAuthorizationInfo();
            if(!authorizationVO.getStatus()){
                authorizationService.resetAuthorizationStatus("fail");
            }else{
                authorizationService.resetAuthorizationStatus("success");
            }
            licenseService.updateResourceByLicense();
        }
        //组织机构初始化添加subcode
        BaseKoalOrgService baseKoalOrgService = appCtx.getBean(BaseKoalOrgService.class);
        baseKoalOrgService.initSubCode();
        //安全域添加初始化添加subcode
        BaseSecurityDomainService baseSecurityDomainService = appCtx.getBean(BaseSecurityDomainService.class);
        baseSecurityDomainService.initSubCode();

        Export.startWatcher();

    }

    @Bean
    public DefaultCookieSerializer getDefaultCookieSerializer(){
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setUseBase64Encoding(sessionBase64);
        return cookieSerializer;
    }

    @Value("${licence.imported:true}")
    public void setImportLicense(boolean importLicense) {
        ApiAdminApplication.importLicense = importLicense;
    }
}

