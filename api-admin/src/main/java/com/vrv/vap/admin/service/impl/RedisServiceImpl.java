package com.vrv.vap.admin.service.impl;

import com.vrv.vap.admin.common.enums.ErrorCode;
import com.vrv.vap.admin.common.util.Base64Util;
import com.vrv.vap.admin.common.util.RC4;
import com.vrv.vap.admin.model.*;
import com.vrv.vap.admin.service.RedisService;
import com.vrv.vap.common.constant.Global;
import com.vrv.vap.common.model.User;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
public class RedisServiceImpl implements RedisService {
    private static final String DOMAIN_CACHE_NEW = "securityDomainCacheNew";
    private static final String SEGMENT_CACHE_NEW = "securityDomainIpSegmentCacheNew";
    private static final String SORT_CACHE_NEW = "securityDomainIpSegmentSortCacheNew";
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private RedisTemplate<String, Set<String>> roleCache;

    @Autowired
    private ValueOperations<String, Set<String>> roleCacheValues;

    @Autowired
    private RedisTemplate<String, User> ssoCache;


    @Autowired
    private RedisTemplate<String, Map<String,BaseSecurityDomain>>  securityDomainTemplate;

    @Autowired
    private RedisTemplate<String, Map<String,BaseSecurityDomainIpSegment>>  securityDomainIpSegmentTemplate;

    @Autowired
    private RedisTemplate<String, String> securityDomainIpSegmentSortTemplate;
    @Value("${sso.timeout}")
    private int timeout;

    @Override
    public void updateOrAddSecurityDomainResource(BaseSecurityDomain baseSecurityDomain) {
        BoundHashOperations<String,String,BaseSecurityDomain> boundHashOperations = securityDomainTemplate.boundHashOps(DOMAIN_CACHE_NEW);
        boundHashOperations.put(baseSecurityDomain.getCode(),baseSecurityDomain);
    }

    @Override
    public void deleteSecurityDomainResource(String guid) {
        BoundHashOperations<String,String,BaseSecurityDomain> boundHashOperations = securityDomainTemplate.boundHashOps(DOMAIN_CACHE_NEW);
        boundHashOperations.delete(guid);
    }

    @Override
    public BaseSecurityDomain getSecurityDomainResource(String guid) {
        BoundHashOperations<String,String,BaseSecurityDomain> boundHashOperations = securityDomainTemplate.boundHashOps(DOMAIN_CACHE_NEW);
        return  boundHashOperations.get(guid);
    }

    @Override
    public Set<String> getRoleResource(String roleId) {
        // 如果没有Redis缓存，则将角色权限设置进去
        String key = Global.SESSION.ROLE_RESOURCE + roleId;
        if(roleCache.hasKey(key)){
            return roleCacheValues.get(key);
        }
        return null;
    }



    @Override
    public void updateOrAddSecurityDomainIpSegmentResource(BaseSecurityDomainIpSegment baseSecurityDomainIpSegment) {
        BoundHashOperations<String,String,BaseSecurityDomainIpSegment> boundHashOperations = securityDomainIpSegmentTemplate.boundHashOps(SEGMENT_CACHE_NEW);
        boundHashOperations.put(baseSecurityDomainIpSegment.getId().toString(),baseSecurityDomainIpSegment);
        securityDomainIpSegmentSortTemplate.opsForZSet().add(SORT_CACHE_NEW,baseSecurityDomainIpSegment.getId().toString(),baseSecurityDomainIpSegment.getEndIpNum());

    }

    @Override
    public void deleteSecurityDomainIpSegmentResource(String id) {
        BoundHashOperations<String,String,BaseSecurityDomainIpSegment> boundHashOperations = securityDomainIpSegmentTemplate.boundHashOps(SEGMENT_CACHE_NEW);
        boundHashOperations.delete(id);
        securityDomainIpSegmentSortTemplate.opsForZSet().remove(SORT_CACHE_NEW,id);
    }

    @Override
    public BaseSecurityDomainIpSegment getSecurityDomainSegmentIpResource(Long ipNumber) {
        Set<String> idResult = securityDomainIpSegmentSortTemplate.opsForZSet().rangeByScore(SORT_CACHE_NEW,ipNumber,Long.MAX_VALUE, 0, 1);
        if(idResult.isEmpty()){
            return  null;
        }
        String id = idResult.iterator().next();
        BoundHashOperations<String,String,BaseSecurityDomainIpSegment> boundHashOperations = securityDomainIpSegmentTemplate.boundHashOps(SEGMENT_CACHE_NEW);
        BaseSecurityDomainIpSegment baseSecurityDomainIpSegment = boundHashOperations.get(id);
        if(ipNumber>=baseSecurityDomainIpSegment.getStartIpNum() && ipNumber<=baseSecurityDomainIpSegment.getEndIpNum()){
          return  baseSecurityDomainIpSegment;
        }
        return  null;
    }

    @Override
    public boolean hasRoleResource(String roleId) {
        String key = Global.SESSION.ROLE_RESOURCE + roleId;
        return roleCache.hasKey(key);
    }
    @Override
    public void setRoleResource(String roleId, Set<String> resourcesSet) {
        // 如果没有Redis缓存，则将角色权限设置进去
        String key = Global.SESSION.ROLE_RESOURCE + roleId;
        roleCache.opsForValue().set(key, resourcesSet);
    }




    @Override
    public void clearRoleResource(int roleId) {
        roleCache.delete(Global.SESSION.ROLE_RESOURCE + roleId);
    }


    // 生成一个token, 以当前 时间 + 用户 ID + 应用 ID 生成TOKEN
    // 以 token-user的形式保存到redis里面，并设置超时时间 （单位：秒）
    @Override
    public String genToken(User user, Integer appId) {
        String token = RC4.encrypt(System.currentTimeMillis() + "|" + user.getId() + "|" + appId);
        String base64Toekn = Base64Util.encodeBase64(token);
        ssoCache.opsForValue().set(base64Toekn, user);
        ssoCache.expire(base64Toekn, timeout, TimeUnit.MILLISECONDS);
        return  base64Toekn;
    }

    /**
     * 这里返回Object ，在Controller 里面进行判断：
     * 验证成功时，返回 User 验证失败时，返回 ErrorCode
     * */
    @Override
    public Object validateToken(String token) {
    	logger.debug("before token is:" + token);
        token = token.replaceAll(" ", "+");
        logger.debug("after replace space:" + token);
        String base64Toekn = Base64Util.decoderByBase64(token,"utf-8");
        String message = RC4.decryption(base64Toekn);
        logger.debug("decryption token is:" + message);
        if(StringUtils.isEmpty(message)){
            return ErrorCode.TOKEN_INVALIDATE;
        }
        String[] ptns = message.split("\\|");
        if(ptns.length!=3){
            return  ErrorCode.TOKEN_INVALIDATE;
        }
        long now = System.currentTimeMillis();
        if((now - Long.parseLong(ptns[0]))> timeout){
            return  ErrorCode.TOKEN_OUT_DATE;
        }
        boolean has = ssoCache.hasKey(token);
        if (has) {
            User user = ssoCache.opsForValue().get(token);
            if(user.getId() != Integer.parseInt(ptns[1])){
                return ErrorCode.TOKEN_USER_ERROR;
            }
            //ssoCache.delete(base64Toekn);
            return user;
        }
        return  ErrorCode.TOKEN_USED;
    }
}
