package com.vrv.vap.admin.service;

import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.vo.PassTimeConf;
import com.vrv.vap.base.BaseService;
import com.vrv.vap.common.interfaces.ResultAble;

import java.util.Map;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.service
 * @Author tongliang@VRV
 * @CreateTime 2019/03/11 17:27
 * @Description (账号安全性配置服务接口)
 * @Version
 */
public interface SystemConfigService extends BaseService<SystemConfig> {

    PassTimeConf getSafetyConfig();

    Integer safetyConfig(PassTimeConf passTimeConf);

    Integer setExpiretime(Map<String, Short> param);

    Map<String, Short> getExpiretime();

    ResultAble judgePassComplex(String bpp);

    ResultAble judgePassComplexNew(String bpp);

    long checkLockStatus(User currentUser);

    int checkRetryNumber(User currentUser);

    long passOuttimeJudge(User currentUser);

    Map<String, Short> getThreePowerConfig();

    Short getThreePowerEnable();

    Integer threePowerConfig(Map<String, Short> param);

    ResultAble checkThreePower(User currentUser);
    /**
     * 是否已经验证了 licence
     * */
    boolean hasLicence();

    public boolean saveConfValue(String key, String value);

    public SystemConfig findByConfId(String confId);

    Object getStaticConfig(String key);

    public Short getIpLoginEnabled();

    public Short getMacLoginEnabled();

    Integer cascadeConfig(Map<String, Byte> param);

    Map<String, Byte> getCascadeConfig();

    Map<String, Short> getExceedSwitchConfig();

    Integer exceedSwitchConfig(Map<String, Short> param);

    Map<String, Short> getExceedUnknownConfig();

    Integer exceedUnknownConfig(Map<String, Short> param);

    Map<String,Object> getSyslogCleanConfig();

    Integer syslogCleanConfig(Map<String, Short> param);
}
