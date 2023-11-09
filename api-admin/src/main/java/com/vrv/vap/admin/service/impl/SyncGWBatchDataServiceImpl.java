package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.service.LoginService;
import com.vrv.vap.admin.service.SyncGWDataService;
import com.vrv.vap.admin.service.SystemConfigService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author lilang
 * @date 2020/5/20
 * @description 国网系统初始化时同步全量数据接口
 */
@Component
@Order(value = 1)
public class SyncGWBatchDataServiceImpl implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SyncGWBatchDataServiceImpl.class);
    @Autowired
    SyncGWDataService syncGWDataService;
    @Autowired
    SystemConfigService systemConfigService;

    private static final String AUTHTYPE_CEMS = "3";

    @Value("${auth.type:0}")
    private String authType;

    private static final String tokenKey = "GW_CEMS_TOKEN";
    @Autowired
    private StringRedisTemplate redisTpl;
    @Autowired
    private LoginService loginService;

    @Override
    public void run(ApplicationArguments applicationArguments) {
        SystemConfig systeminitStatus = systemConfigService.findByConfId("systeminit_status");
        try {
            if (AUTHTYPE_CEMS.equals(authType) && !"TRUE".equals(StringUtils.upperCase(systeminitStatus.getConfValue()))) {
                String token = syncGWDataService.getAuthToken();
                redisTpl.opsForValue().set(tokenKey, token, 25, TimeUnit.MINUTES);
                if (StringUtils.isNotEmpty(token)) {
                    String appCode = syncGWDataService.getAppCode(token);
                    if (StringUtils.isNotEmpty(appCode)) {
                        //全量获取角色及权限
                        syncGWDataService.syncAllRole(token, appCode);
                        //全量同步组织机构
                        syncGWDataService.syncAllOrg(token, appCode);
                        //全量同步用户
                        syncGWDataService.syncAllUser(token, appCode);
                        //全量同步用户管理范围
                        syncGWDataService.syncAllUserDomain(token, appCode);
                        //上报资源数据
                        syncGWDataService.reportResourceInfo(token, appCode);
                        //授权
                        loginService.validateHttpLicense();
                    } else {
                        log.info("appCode获取失败，请检查");
                    }
                } else {
                    log.info("token获取失败，请检查");
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }

    }

}
