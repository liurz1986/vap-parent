package com.vrv.vap.syslog.service;

import org.aspectj.lang.ProceedingJoinPoint;

/**
 * @author wh1107066
 * @date 2021/7/1 19:06
 */
public interface SyslogProcessor<C> {

    /**
     * 用户操作日志审计流程，记录日志
     * @param joinPoint 连接点
     * @param resResult 操作结果状态， 0 代表失败   1代表成功
     * @return 返回泛型对象
     */
    C processing(ProceedingJoinPoint joinPoint, String resResult);
}
