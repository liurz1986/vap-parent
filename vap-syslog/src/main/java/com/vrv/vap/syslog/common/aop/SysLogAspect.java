package com.vrv.vap.syslog.common.aop;

import com.vrv.vap.syslog.common.constant.SyslogConstant;
import com.vrv.vap.syslog.common.utils.SyslogContextHolder;
import com.vrv.vap.syslog.exception.VapSyslogException;
import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.service.SyslogProcessor;
import jdk.nashorn.internal.ir.annotations.Ignore;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * @author wh1107066
 * 类说明 : 记录日志
 */
@Aspect
@Component
@Order(-5)
public class SysLogAspect implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(SysLogAspect.class);

    @Autowired
    private SyslogProcessor syslogProcessor;

    @Override
    public void afterPropertiesSet() {
        Assert.notNull(this.syslogProcessor, "syslogProcessor must be set. maybe not set properties: vap.flume.enable=true");
    }

    @Pointcut("@annotation( com.vrv.vap.syslog.common.annotation.SysRequestLog)")
    public void controllerAspect() {
    }
    /**
     * AOP增加在方法上标有SysRequestLog的注解
     *
     * @param joinPoint 切面
     * @return 返回Controller中的返回的实体
     * @throws Throwable 抛出异常
     */
    @Around("controllerAspect()")
    public Object doAround(ProceedingJoinPoint joinPoint) throws Throwable {
        // ignore的注解不记录日志
        if (needIgnoreAnnotation(joinPoint) || needIgnoreNoUser(joinPoint)) {
            return joinPoint.proceed();
        }
        // 记录操作日志并发送
        try {
            SystemLog systemLog = (SystemLog)syslogProcessor.processing(joinPoint, SyslogConstant.SUCCESS);
            SyslogContextHolder.setSyslog(systemLog);
            return joinPoint.proceed();
        } catch (Throwable throwable) {
            logger.error("后台接口调用异常，发送syslog的查询结果为查询失败!!", throwable);
            syslogProcessor.processing(joinPoint, SyslogConstant.FAILED);
            throw new VapSyslogException("服务器内部异常，请联系管理员!!", throwable);
        } finally {
            SyslogContextHolder.clearSyslog();
        }
    }

    private Boolean needIgnoreNoUser(ProceedingJoinPoint joinPoint) {
        // 获取不到user信息，不记录日志
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(attributes == null) {
            logger.warn("非Servlet容器ServletRequestAttributes为空，不记录日志");
            return  true;
        }
        HttpServletRequest request = attributes.getRequest();
        if(request == null) {
            logger.warn("非Servlet容器,request为空，不记录日志");
            return  true;
        }
        // 后台使用feign接口调用带有注解的SysRequestLog的方法，则后台有可能获取不到user对象。 或者使用restTemplate的方式。 需要在header中携带user
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SyslogConstant.USER) == null) {
            logger.warn("获取的用户信息为空， 查明原因，是否是在非web容器内调用 或者 未登录进行访问接口？？");
            logger.warn(String.format("session为空，接口放过log日志记录，name=%s, uri=%s", joinPoint.getSignature().getName(), request.getRequestURI()));
            return true;
        }
        return false;
    }


    /**
     * 方法标记Ignore 或者  类标记 Ignore 可以忽略日志审计操作
     */
    private static Boolean needIgnoreAnnotation(ProceedingJoinPoint joinPoint) {
        Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Ignore ignore = targetMethod.getDeclaredAnnotation(Ignore.class);
        Ignore ignoreClass = joinPoint.getTarget().getClass().getAnnotation(Ignore.class);
        if (ignoreClass != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("类：" + joinPoint.getTarget().getClass().getName() + ":标记为忽略,不记录日志");
            }
            return true;
        } else if (ignore != null) {
            if (logger.isDebugEnabled()) {
                logger.debug("方法名" + joinPoint.getSignature().getName() + ":标记为忽略,不记录日志");
            }
            return true;
        }
        return false;
    }


}
