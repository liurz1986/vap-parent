package com.vrv.vap.xc.interceptor;

import com.vrv.vap.toolkit.vo.VData;
import com.vrv.vap.xc.exception.BusinessBasicException;
import com.vrv.vap.xc.tools.JsonQueryTools;
import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;

@Aspect
@Component
public class ControllerAspect {

    public static final Logger logger = Logger.getLogger(ControllerAspect.class);
    private static final String head = "##########|\t";
    private static final String tail = "====================================================";

    @Pointcut("bean(*Controller)")
    public void controllerPointCut() {
    }

    @Before("controllerPointCut()")
    public void doBefore(JoinPoint joinPoint) {
        // Receives the request and get request content
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        assert attributes != null;
        HttpServletRequest request = attributes.getRequest();
        logger.info(head + "URL : " + request.getRequestURL().toString());
        logger.info(head + "HTTP_METHOD : " + request.getMethod());
        logger.info(head + "IP : " + request.getRemoteAddr());
        logger.info(head + "CLASS_METHOD : " + joinPoint.getSignature().getDeclaringTypeName() + "." + joinPoint.getSignature().getName());
        logger.info(head + "ARGS : " + Arrays.toString(joinPoint.getArgs()));
    }

    @AfterReturning(returning = "ret", pointcut = "controllerPointCut()")
    public void doAfterReturning(VData ret) {
        // Processes the request and returns the content
        // logger.info(head + "RESPONSE : " + JsonQueryTools.toJson(ret));
        logger.info(tail);
    }

    @AfterThrowing(throwing = "ex", pointcut = "controllerPointCut()")
    public void doAfterThrowing(BusinessBasicException ex) {
        final String msg = ex.getMessage();
        // Processes the request and returns the content
        logger.info(head + "RESPONSE : " +  ex.addSuffix(msg).replace("()", ""));
        logger.info(tail);
    }

    @Around("controllerPointCut()")
    public Object interceptor(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        logger.info(tail);
        return proceedingJoinPoint.proceed();
    }
}
