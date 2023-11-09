package com.vrv.vap.syslog.service;

import com.vrv.vap.syslog.common.enums.ActionType;

/**
 * 发送接口，可以有多个发送方式  1. rabbitmq   2. flume  3. 其他
 *
 * @author wh1107066
 * @date 2021/7/2 10:42
 */
public interface SyslogSender<C> {
    /**
     * 通过切面记录日志，么对比前后值的变化
     *
     * @param systemLog
     */
    void sendSysLog(C systemLog);

    /**
     * 比较之前和之后的对象的数据，进行数据的对比，  resResult必须要传（手动处理）,  用法如下
     * String message = CompareObjectUtil.objectDescription(user,添加用户);
     * logMessageSender.sendSysLog(ActionType.ADD, message);
     *
     * @param actionType      操作类型，枚举类
     * @param context         比较之后的数据内容
     * @param operationObject 操作对象
     * @param resResult       手动给予操作成功或失败
     */
    void sendSysLog(ActionType actionType, String context, String operationObject, String resResult);
}
