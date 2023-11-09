package com.vrv.vap.server.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.vrv.vap.server.zuul.common.ZuulConstant;
import com.vrv.vap.server.zuul.filter.condition.VAPConditional;
import com.vrv.vap.server.zuul.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;


/**
 * 等待完成 ： 用于通过API修改资源等信息后，刷新缓存
 */
@Component
@Conditional(VAPConditional.class)
public class AppAuthFilter extends ZuulFilter {

    static final Pattern PTN = Pattern.compile("\"roleId\":\"?(\\d+)\"?");

    private Logger logger = LogManager.getLogger(this.getClass());

    @Autowired
    private RedisTemplate<String, String> stringRedisTemplate;

    @Override
    public String filterType() {
        return "pre";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    private AntPathMatcher antPathMatcher = new AntPathMatcher();

    /**
     * 管理员更新权限后，刷zuul的权限池
     */
    @Override
    public boolean shouldFilter() {
        RequestContext context = RequestContext.getCurrentContext();
        if(context.getRequest()==null){
            return false;
        }
        String url = context.getRequest().getRequestURI();
        if(ZuulConstant.CLIENT_AUTH_API.equals(url)){
            return false;
        }
        String token = context.getRequest().getHeader(ZuulConstant.CLIENT_HEADER_AUTH);
        if(StringUtils.isNotEmpty(token)){
            return true;
        }
        return false;
    }
    private void toForbiddenInterFace(RequestContext context) {
        try {
            logger.info("<<<<<<<<<<<<<forbidden>>>>>>>>>>>>>>>" );
            context.setSendZuulResponse(false);
            context.setResponseStatusCode(401);
//            context.getResponse().sendRedirect(forbidden);
        } catch (Exception e) {
            logger.error( e.getMessage());
        }
    }

    @Override
    public Object run() {
        RequestContext context = RequestContext.getCurrentContext();
        try {

            String token = context.getRequest().getHeader("Authorization_App_Token");
            String uri = context.getRequest().getRequestURI();
            logger.info("uri: {}", uri);
            String prefix = "vap:client:token:";
            String redisKey = prefix+token;
            String params = stringRedisTemplate.opsForValue().get(redisKey);
            Map<String,Object> contentMap = JsonUtils.toMap(params);
            if(!contentMap.containsKey("appId")){
                logger.error("appId不存在");
                toForbiddenInterFace(context);
            }
            if(!contentMap.containsKey("apis")){
                logger.error("无任何权限apis");
                toForbiddenInterFace(context);
            }
            List<String> apis = (List<String>)contentMap.get("apis");
            Boolean auth = false;
            for(String api:apis){
                if(antPathMatcher.match(api,uri)){
                    auth = true;
                    break;
                }
            }
            logger.info("appId:{},访问URI:{},权限校验：{}",contentMap.get("appId"),uri,auth?"OK":"FAIL");
            if(!auth){
                toForbiddenInterFace(context);
            }

        } catch (Exception ex) {
            logger.error("ReflushFilter中捕获异常信息:{}", ex.getMessage());
            toForbiddenInterFace(context);
        }
        return null;
    }
}
