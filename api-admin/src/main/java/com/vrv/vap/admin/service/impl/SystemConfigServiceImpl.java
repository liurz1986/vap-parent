package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.ResourceRunner;
import com.vrv.vap.admin.common.constant.Const;
import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.enums.RoleEnum;
import com.vrv.vap.admin.common.util.EncryptUtil;
import com.vrv.vap.admin.mapper.SystemConfigMapper;
import com.vrv.vap.admin.model.Role;
import com.vrv.vap.admin.model.SystemConfig;
import com.vrv.vap.admin.model.User;
import com.vrv.vap.admin.service.ResourceService;
import com.vrv.vap.admin.service.RoleService;
import com.vrv.vap.admin.service.SystemConfigService;
import com.vrv.vap.admin.service.UserService;
import com.vrv.vap.admin.vo.Menu;
import com.vrv.vap.admin.vo.PassTimeConf;
import com.vrv.vap.base.BaseServiceImpl;
import com.vrv.vap.common.interfaces.ResultAble;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.ehcache.Cache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @BelongsProject api-admin
 * @BelongsPackage com.vrv.vap.admin.service.impl
 * @Author tongliang@VRV
 * @CreateTime 2019/03/11 17:41
 * @Description (账号安全性配置服务实现)
 * @Version 2019/06/27 - 重写此模块，统一使用通用 map ，改名为 SYSTEMCONFIG，加入缓存机制
 */
@Service
public class SystemConfigServiceImpl extends BaseServiceImpl<SystemConfig> implements SystemConfigService {
    private static final String EXPIRETIME = "expiretime";
    private static final String ENABLE = "enable";
    private static final String THREE_POWER_ON = "THREE_POWER_ON";
    private static final String CASCADE = "cascade";
    private static final String EXCEED_STRATEGY_SWITCH = "authority_exceed_strategy_switch";
    private static final String EXCEED_STRATEGY_UNKNOWN = "authority_exceed_strategy_unknown";
    private static final String SYS_LOG_CLEAN = "sys_log_clean";
    @Autowired
    Cache<String, SystemConfig> configCache;

    @Resource
    private SystemConfigMapper systemConfigMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private StringRedisTemplate redisTpl;

    @Autowired
    private ResourceRunner resourceRunner;

    private static String LOGIN_PAGE = "_LOGIN_PAGE";

    @Autowired
    Cache<String, List<Menu>> menuCache;

    @Override
    public SystemConfig findByConfId(String confId) {
        if (configCache.containsKey(confId)) {
            return configCache.get(confId);
        }
        SystemConfig config = systemConfigMapper.selectByPrimaryKey(confId);
        if (config == null) {
            config = new SystemConfig();
            config.setConfId(confId);
            this.save(config);
        }
        configCache.put(config.getConfId(), config);
        return config;
    }

    @Override
    public Object getStaticConfig(String key) {
        SystemConfig systemConfig =  this.findByConfId(key);
        return systemConfig==null?null:systemConfig.getConfValue();
    }


    @Override
    public boolean saveConfValue(String key, String value) {
        List<SystemConfig> all = this.findAll();
        SystemConfig systemConfig = null;
        for (SystemConfig config : all) {
            if (config.getConfId().equals(key)) {
                systemConfig = config;
                break;
            }
        }
        try {
            if (systemConfig == null) {
                systemConfig = new SystemConfig();
                systemConfig.setConfId(key);
                systemConfig.setConfValue(value);
                systemConfig.setConfTime(new Date());
                systemConfig.setConfEnable((short)1);
                systemConfig.setStatusUpdate((short) 1);
                this.save(systemConfig);
            } else {
                systemConfig.setConfValue(value);
                this.updateSelective(systemConfig);
            }
        } catch (Exception e) {
            throw new RuntimeException("保存key为：" + key + "的配置失败：" + e.getMessage());
        }
        return true;
    }

    @Override
    public Integer updateSelective(SystemConfig config) {
       SystemConfig systemConfig = new SystemConfig();
       systemConfig.setConfId(config.getConfId());
        if("loginPage".equals(config.getConfId())){
            redisTpl.opsForValue().set(LOGIN_PAGE,config.getConfValue());
        }
       List<SystemConfig> list =  systemConfigMapper.select(systemConfig);
       if(CollectionUtils.isEmpty(list)){
            return this.save(config);
       }
       else {
           config.setConfTime(new Date());
           configCache.remove(config.getConfId());
           return super.updateSelective(config);
       }
    }

    /**
     * @return
     * @Description (获取持久化安全性配置)
     * @Param
     */
    @Override
    public PassTimeConf getSafetyConfig() {
        PassTimeConf passTimeConf = new PassTimeConf();
        passTimeConf.setMaxFailNumber(Integer.valueOf(this.findByConfId("maxfailnumber").getConfValue()));
        passTimeConf.setCheckTimeSet(Integer.valueOf(this.findByConfId("checktimeset").getConfValue()));
        passTimeConf.setUppercase(this.findByConfId("uppercase").getConfEnable());
        passTimeConf.setLowercase(this.findByConfId("lowercase").getConfEnable());
        passTimeConf.setSpecialChart(this.findByConfId("specialchart").getConfEnable());
        passTimeConf.setNumbers(this.findByConfId("numbers").getConfEnable());
        passTimeConf.setMinlength(Integer.valueOf(this.findByConfId("minlength").getConfValue()));
        passTimeConf.setMaxlength(Integer.valueOf(this.findByConfId("maxlength").getConfValue()));
        return passTimeConf;
    }


    /**
     * @return 返回改变记录的条数
     * @Description (持久化安全性配置)
     * @Param
     */
    @Override
    public Integer safetyConfig(PassTimeConf passTimeConf) {

        SystemConfig config = this.findByConfId("maxfailnumber");
        config.setConfValue(String.valueOf(passTimeConf.getMaxFailNumber()));
        this.updateSelective(config);

        config = this.findByConfId("checktimeset");
        config.setConfValue(String.valueOf(passTimeConf.getCheckTimeSet()));
        this.updateSelective(config);

        config = this.findByConfId("uppercase");
        config.setConfEnable(passTimeConf.getUppercase());
        this.updateSelective(config);

        config = this.findByConfId("lowercase");
        config.setConfEnable(passTimeConf.getLowercase());
        this.updateSelective(config);

        config = this.findByConfId("specialchart");
        config.setConfEnable(passTimeConf.getSpecialChart());
        this.updateSelective(config);

        config = this.findByConfId("numbers");
        config.setConfEnable(passTimeConf.getNumbers());
        this.updateSelective(config);

        config = this.findByConfId("minlength");
        config.setConfValue(String.valueOf(passTimeConf.getMinlength()));
        this.updateSelective(config);

        config = this.findByConfId("maxlength");
        config.setConfValue(String.valueOf(passTimeConf.getMaxlength()));
        this.updateSelective(config);

        return 1;
    }


    /**
     * @Description (设置密码时效性的配置信息)
     */
    @Override
    public Integer setExpiretime(Map<String, Short> param) {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(EXPIRETIME);
        systemConfig.setConfValue(String.valueOf(param.get(EXPIRETIME)));
        systemConfig.setConfEnable(param.get(ENABLE));
        systemConfig.setStatusUpdate((short) 1);
        return this.updateSelective(systemConfig);
    }


    /**
     * @Description (获取用户时效性的配置信息)
     */
    @Override
    public Map<String, Short> getExpiretime() {
        SystemConfig systemConfig = this.findByConfId(EXPIRETIME);
        Map<String, Short> expiretimeConfig = new HashMap<>();
        expiretimeConfig.put(EXPIRETIME, Short.valueOf(systemConfig.getConfValue()));
        expiretimeConfig.put(ENABLE, systemConfig.getConfEnable());
        return expiretimeConfig;
    }


    /**
     * @return 用户未被锁定 或者 用户已经到达自动解锁时间返回 0
     * @Description (检查用户是否被锁定)
     * @Param
     */
    @Override
    public long checkLockStatus(User currentUser) {
        PassTimeConf safetyConfig = getSafetyConfig();
        Integer lockStatus = currentUser.getLoginTimes();
        Integer maxFailNumber = safetyConfig.getMaxFailNumber();
        //判断是否为null 为null默认关闭
        if (lockStatus == null) {
            lockStatus = 0;
        }
        if (maxFailNumber == null) {
            maxFailNumber = 3;
        }
        if (lockStatus >= maxFailNumber) {
            //用户锁定
            long currentTime = System.currentTimeMillis(); //当前时间
            Date lastLoginTime = currentUser.getLastLoginTime();
            Integer checkTimeSet = safetyConfig.getCheckTimeSet();
            if (lastLoginTime == null) {
                lastLoginTime = new Date(currentTime - 60 * 60 * 24 * 1000);
                currentUser.setLastLoginTime(lastLoginTime);
            }
            if (checkTimeSet == null) {
                checkTimeSet = 30;
            }
            long lockTime = lastLoginTime.getTime(); //最近登录时间 被锁定时间
            long unlockTime = (long) checkTimeSet * 60 * 1000; //自动解锁时间 ms
            if (currentTime - lockTime < unlockTime) {
                long leftTime = currentTime - lockTime;
                long restTime = unlockTime - leftTime; //自动解锁剩余时间 (毫秒)
                //未到自动解锁时间
                return restTime;
            } else {
                //自动解锁 将尝试次数清0
                currentUser.setLoginTimes(0);
                userService.update(currentUser);
            }
        }

        return 0;
    }


    /**
     * @return 0 ：表示用户已经被锁定 或者 达到锁定用户的条件
     * @Description (检查用户的剩余重试次数)
     * @Param
     */
    @Override
    public int checkRetryNumber(User currentUser) {
        PassTimeConf safetyConfig = getSafetyConfig();
        Integer lockStatus = currentUser.getLoginTimes();
        Integer maxFailNumber = safetyConfig.getMaxFailNumber();
        if (lockStatus == null) {
            lockStatus = 0;
        }
        if (maxFailNumber == null) {
            maxFailNumber = 3;
        }
        lockStatus += 1; //密码错误尝试次数加1
        int restRetryNumber = maxFailNumber - lockStatus;
        if (lockStatus >= maxFailNumber) {
            currentUser.setLastLoginTime(new Date());
            currentUser.setLoginTimes(maxFailNumber);
        } else {
            currentUser.setLoginTimes(lockStatus);
        }
        userService.update(currentUser);
        if (restRetryNumber < 0) {
            return 0;
        }
        return restRetryNumber;
    }


    /**
     * @return 为null时表示未开启密码复杂度的判断或者通过密码复杂度的校验
     * @Description (密码复杂度判断方法)
     * @Param
     */
    @Override
    public ResultAble judgePassComplex(String bpp) {
        PassTimeConf safetyConfig = getSafetyConfig();
        //密码复杂度判断 判断是否开启
        if (bpp != null) {
            ResultAble complexResult = null;
            try {
                complexResult = checkPassComplex(safetyConfig,
                        EncryptUtil.decodeBase65(bpp));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (complexResult != null) {
                //符合或者未开启密码复杂度的规则返回 null
                return complexResult;
            }
        }
        return null;
    }

    @Override
    public ResultAble judgePassComplexNew(String bpp) {
        PassTimeConf safetyConfig = getSafetyConfig();
        //密码复杂度判断 判断是否开启
        if (bpp != null) {
            ResultAble complexResult = checkPassComplex(safetyConfig,
                    bpp );
            if (complexResult != null) {
                //符合或者未开启密码复杂度的规则返回 null
                return complexResult;
            }
        }
        return null;
    }


    /**
     * @Description (内部调用方法 被JudgePassComplex ()方法调用 通过正则表达式检查字符串复杂度)
     */
    private ResultAble checkPassComplex(PassTimeConf safetyConfig, String userPassword) {
        //判断是否为null 为null默认关闭
        Short numbers = safetyConfig.getNumbers();
        if (numbers == null) {
            numbers = 0;
        }

        Short uppercase = safetyConfig.getUppercase();
        if (uppercase == null) {
            uppercase = 0;
        }

        Short lowercase = safetyConfig.getLowercase();
        if (lowercase == null) {
            lowercase = 0;
        }

        //判断是否为null 为null默认关闭
        Short specialChart = safetyConfig.getSpecialChart();
        if (specialChart == null) {
            specialChart = 0;
        }

        if (numbers == 1 || uppercase == 1 || lowercase == 1 || specialChart == 1) {
            // 判断是否开启这个限定
            Integer minlength = safetyConfig.getMinlength(); //密码最小长度
            if (minlength > 0 && userPassword.length() < minlength) {
                return ErrorCode.PWD_MIN;
            }
            Integer maxlength = safetyConfig.getMaxlength(); //密码最大长度
            if (maxlength > 0 && userPassword.length() > maxlength) {
                return ErrorCode.PWD_MAX;
            }
            if (uppercase == 1) {
                //开启大写字母限制
                if (!userPassword.matches(".*[A-Z].*")) {
                    //不包含大写字母
                    return ErrorCode.PWD_UPPER;
                }
            }
            if (lowercase == 1) {
                //开启小写字母限制
                if (!userPassword.matches(".*[a-z].*")) {
                    //不包含小写字母
                    return ErrorCode.PWD_LOWER;
                }
            }
            if (specialChart == 1) {
                //开启特殊字符和下划线限制
                if (userPassword.matches("[a-zA-Z0-9]+")) {
                    //不包特殊字符和下划线
                    return ErrorCode.PWD_SPECIAL;
                }
            }
            if (numbers == 1) {
                //开启数字限制
                if (!userPassword.matches(".*[0-9].*")) {
                    //不包数字
                    return ErrorCode.PWD_NUMBER;
                }
            }
        }
        return null;
    }

    /**
     * 信工所密码复杂度，至少包含数字、字母、特殊字符两种及以上
     * @param safetyConfig
     * @param userPassword
     * @return
     */
    /*private ResultAble checkXgsPassComplex(PassTimeConf safetyConfig,String userPassword) {
        // 判断是否开启这个限定
        Integer minlength = safetyConfig.getMinlength(); //密码最小长度
        if (minlength > 0 && userPassword.length() < minlength) {
            return ErrorCode.PWD_MIN;
        }
        Integer maxlength = safetyConfig.getMaxlength(); //密码最大长度
        if (maxlength > 0 && userPassword.length() > maxlength) {
            return ErrorCode.PWD_MAX;
        }
        if (!userPassword.matches("^(?![A-Z]+$)(?![a-z]+$)(?!\\d+$)(?![\\W_]+$)\\S{"+minlength+","+maxlength+"}$")) {
            return ErrorCode.PWD_TWO_OR_MORE;
        }
        return null;
    }*/


    /**
     * @param
     * @return 返回密码失效天数  0：表示 未失效 或者 未开启
     * @Description (密码是否过时失效的判断)
     */
    @Override
    public long passOuttimeJudge(User currentUser) {
        Map<String, Short> expiretime = getExpiretime();
        if (expiretime != null && expiretime.get(ENABLE) != null && expiretime.get(ENABLE) == 1) {
            //管理员密码时效性功能开启
            Date lastUpdateTime = currentUser.getLastUpdateTime();
            if (lastUpdateTime == null) {
                lastUpdateTime = new Date();
            }
            Short expiretime0 = expiretime.get(EXPIRETIME);
            if (expiretime0 == null) {
                expiretime0 = 7;
            }
            long currentTime = System.currentTimeMillis();
            long modifyTime = lastUpdateTime.getTime();
            long expireTime = (long) expiretime0 * 24 * 60 * 60 * 1000; //天 转为 毫秒
            if (currentTime - modifyTime >= expireTime) {
                //管理员密码时效性到期
                return expiretime0;
            }
        }
        return 0;
    }


    /**
     * @Description (获取三权是否开启的配置)
     */
    @Override
    public Map<String, Short> getThreePowerConfig() {
        SystemConfig systemConfig = this.findByConfId(THREE_POWER_ON);
        Map<String, Short> threePowerConfig = new HashMap<>();
        threePowerConfig.put(THREE_POWER_ON, systemConfig.getConfEnable());
        return threePowerConfig;
    }

    @Override
    public Short getThreePowerEnable() {
        SystemConfig systemConfig = this.findByConfId(THREE_POWER_ON);
        if (systemConfig != null) {
            return systemConfig.getConfEnable();
        }
        return null;
    }

    @Override
    public Map<String, Short> getExceedSwitchConfig() {
        SystemConfig systemConfig = this.findByConfId(EXCEED_STRATEGY_SWITCH);
        Map<String, Short> exceedSwitchConfig = new HashMap<>();
        exceedSwitchConfig.put(EXCEED_STRATEGY_SWITCH,systemConfig.getConfEnable());
        return exceedSwitchConfig;
    }

    @Override
    public Map<String, Short> getExceedUnknownConfig() {
        SystemConfig systemConfig = this.findByConfId(EXCEED_STRATEGY_UNKNOWN);
        Map<String, Short> exceedSwitchConfig = new HashMap<>();
        exceedSwitchConfig.put(EXCEED_STRATEGY_UNKNOWN,systemConfig.getConfEnable());
        return exceedSwitchConfig;
    }

    @Override
    public Map<String, Object> getSyslogCleanConfig() {
        SystemConfig systemConfig = this.findByConfId(SYS_LOG_CLEAN);
        Map<String, Object> SyslogCleanConfig = new HashMap<>();
        SyslogCleanConfig.put(SYS_LOG_CLEAN,systemConfig.getConfValue());
        return SyslogCleanConfig;
    }

    /**
     * ip登录是否开启
     * @return
     */
    @Override
    public Short getIpLoginEnabled() {
        SystemConfig systemConfig = this.findByConfId("authLoginField");
        if (systemConfig != null) {
            return systemConfig.getConfEnable();
        }
        return null;
    }


    /**
     * mac登录是否开启
     * @return
     */
    @Override
    public Short getMacLoginEnabled() {
        SystemConfig systemConfig = this.findByConfId("authMacLogin");
        if (systemConfig != null) {
            return systemConfig.getConfEnable();
        }
        return null;
    }

    /**
     * @Description (持久化三权是否开启的配置)
     */
    @Override
    public Integer threePowerConfig(Map<String, Short> param) {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(THREE_POWER_ON);
        systemConfig.setConfEnable(param.get(THREE_POWER_ON));
        systemConfig.setStatusUpdate((short) 1);
        return this.updateSelective(systemConfig);
    }

    @Override
    public Integer exceedSwitchConfig(Map<String, Short> param) {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(EXCEED_STRATEGY_SWITCH);
        systemConfig.setConfEnable(param.get(EXCEED_STRATEGY_SWITCH));
        systemConfig.setConfValue(String.valueOf(param.get(EXCEED_STRATEGY_SWITCH)));
        systemConfig.setStatusUpdate((short) 1);
        Integer result =  this.updateSelective(systemConfig);
        resourceRunner.initStrategyConfig();
        return result;
    }

    @Override
    public Integer exceedUnknownConfig(Map<String, Short> param) {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(EXCEED_STRATEGY_UNKNOWN);
        systemConfig.setConfEnable(param.get(EXCEED_STRATEGY_UNKNOWN));
        systemConfig.setConfValue(String.valueOf(param.get(EXCEED_STRATEGY_UNKNOWN)));
        systemConfig.setStatusUpdate((short) 1);
        Integer result = this.updateSelective(systemConfig);
        resourceRunner.initStrategyConfig();
        return result;
    }

    @Override
    public Integer syslogCleanConfig(Map<String, Short> param) {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(SYS_LOG_CLEAN);
        systemConfig.setConfEnable(param.get(SYS_LOG_CLEAN) > 0 ?  Short.valueOf("1") : Short.valueOf("0"));
        systemConfig.setConfValue(String.valueOf(param.get(SYS_LOG_CLEAN)));
        systemConfig.setStatusUpdate((short) 1);
        Integer result = this.updateSelective(systemConfig);
        return result;
    }

    @Override
    public Integer cascadeConfig(Map<String, Byte> param) {
        SystemConfig systemConfig = new SystemConfig();
        systemConfig.setConfId(CASCADE);
        systemConfig.setConfEnable(Short.valueOf(param.get(CASCADE)));
        systemConfig.setStatusUpdate(Short.valueOf("1"));
        if (Const.CONF_ENABLED.equals(param.get(CASCADE))) {
            resourceService.enableResource("61222");
        } else {
            resourceService.disableResource("61222");
        }
        menuCache.clear();
        return this.updateSelective(systemConfig);
    }

    @Override
    public Map<String, Byte> getCascadeConfig() {
        SystemConfig systemConfig = this.findByConfId(CASCADE);
        Map<String, Byte> threePowerConfig = new HashMap<>();
        threePowerConfig.put(CASCADE, Byte.valueOf(systemConfig.getConfEnable().toString()));
        return threePowerConfig;
    }


    /**
     * @Description (检查三权是否开启 开启或者非三权用户 返回为 null)
     */
    @Override
    public ResultAble checkThreePower(User currentUser) {
        //判断是否开启三权
        Short threePowerEnable = getThreePowerEnable();
        if (threePowerEnable == 0) {
            //表示三权未开启 三权用户禁止登陆
            //获取当前用户的所有角色
            String[] roleIds = currentUser.getRoleId().split(",");
            for (String roleId : roleIds) {
                Role role = roleService.findById(Integer.valueOf(roleId));
                if (role != null) {
                    String roleCode = role.getCode();
                    if (RoleEnum.SYSCONTROLLER.getRoleCode().equals(roleCode)
                            || RoleEnum.AUDIT.getRoleCode().equals(roleCode)
                            || RoleEnum.SAFETER.getRoleCode().equals(roleCode)) {

                        //返回 三权未开启，当前用户无法登陆
                        return ErrorCode.THREE_POWER_OFF;

                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean hasLicence() {
        SystemConfig systeminitStatus = this.findByConfId("systeminit_status");
        SystemConfig authorizationStatus = this.findByConfId("authorization_Status");
        if ("TRUE".equals(StringUtils.upperCase(systeminitStatus.getConfValue()))
            && "SUCCESS".equals(StringUtils.upperCase(authorizationStatus.getConfValue()))) {
            return true;
        }
        return false;
    }
}
