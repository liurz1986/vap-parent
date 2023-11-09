package com.vrv.vap.toolkit.aspect;

import com.vrv.vap.flumeavrostarter.sender.FlumeDataSender;
import com.vrv.vap.toolkit.config.EnviromentConfig;
import com.vrv.vap.toolkit.tools.SessionTools;
import com.vrv.vap.toolkit.tools.LogAspectTools;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 日志记录
 *
 * @author xw
 * @date 2018年5月3日
 */
@Aspect
@Component
public class LogAspect {

    private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(1000);
    private static ThreadPoolExecutor excutor = new ThreadPoolExecutor(2, 4, 1, TimeUnit.MINUTES, workQueue);

    @Autowired
    private FlumeDataSender flumeDataSender;


    @Pointcut("execution(* com.vrv.vap.*.controller.*.*(..))")
    public void anyMethod() {
    }

    @After("anyMethod()")
    public void after(JoinPoint jp) {
        Map<String, Object> systemLog = LogAspectTools.handler4Map(jp, (ServletRequestAttributes) RequestContextHolder.getRequestAttributes(), SessionTools.getUserInfo());
        excutor.execute(() -> push(systemLog));
        EnviromentConfig.removeEnvironment();
    }

    private void push(Map<String, Object> systemLog) {
        try {
            if (systemLog != null) {
                systemLog.put("id", UUID.randomUUID().toString().replace("-", "").toLowerCase());
                flumeDataSender.send(systemLog);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
