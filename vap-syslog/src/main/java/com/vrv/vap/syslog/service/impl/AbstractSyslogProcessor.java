package com.vrv.vap.syslog.service.impl;

import com.alibaba.fastjson.JSON;
import com.vrv.vap.common.utils.DateUtils;
import com.vrv.vap.common.utils.ip.IpUtils;
import com.vrv.vap.syslog.common.annotation.LogField;
import com.vrv.vap.syslog.common.annotation.SysRequestLog;
import com.vrv.vap.syslog.common.enums.ActionTypeEnums;
import com.vrv.vap.syslog.common.utils.SessionUtils;
import com.vrv.vap.syslog.common.utils.UserUtil;
import com.vrv.vap.syslog.model.ExtendFiledDTO;
import com.vrv.vap.syslog.model.SystemLog;
import com.vrv.vap.syslog.model.UserdDTO;
import com.vrv.vap.syslog.service.SyslogProcessor;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Base64Utils;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wh1107066
 * @date 2021/7/1 19:09
 */
public abstract class AbstractSyslogProcessor<C extends SystemLog> implements SyslogProcessor {

    private static final Logger logger = LoggerFactory.getLogger(AbstractSyslogProcessor.class);

    /**
     * 模版模式
     * 1. 组装syslog
     * 2. 发送 ，  可以实现可扩展的发送发送， 可发送rabbitmq， 发送flume， 将来还有更高级的发送组件
     *
     * @param joinPoint ProceedingJoinPoint
     */
    @Override
    public C processing(ProceedingJoinPoint joinPoint, String resResult) {
        // TODO 获取AOP的属性并生成Syslog对象
        if (logger.isDebugEnabled()) {
            logger.debug("processing调整打印日志...");
        }
        C c = generate(joinPoint, resResult);
        if (logger.isDebugEnabled()) {
            logger.debug("processing调整打印日志生成, {}...", c);
        }
        // TODO 是否发送，由注解决定：手动是人工发送，那么就不需要自动的通过AOP切面在发送一次。
        //  c.getManually()== true 代表自动发送。 自动发送到flume，并做日志保存。 由自定义注解@SysRequestLog 的 manually= true决定。默认是true
        if (c != null && Boolean.FALSE.equals(c.getManually())) {
            sending(c);
        }
        return c;
    }


    /**
     * @param joinPoint 连接点
     * @param resResult 操作成功或者失败
     * @return 返回操作日志对象
     */
    private C generate(ProceedingJoinPoint joinPoint, String resResult) {
        if (logger.isDebugEnabled()) {
            logger.debug("generate调整打印日志,构建syslog对象开始...");
        }
        C syslog = null;
        try {
            String methodName = joinPoint.getSignature().getName();
            String beanName = joinPoint.getSignature().getDeclaringTypeName();
            String operationObject = getOperationObject(joinPoint);
            // 获取切面上的【方法】
            Method targetMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
            ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
            // 获取方法上的括号中的【参数名称】
            String[] parameterNames = parameterNameDiscoverer.getParameterNames(targetMethod);
            // 获取方法上的括号中的【对象的值】
            Object[] args = joinPoint.getArgs();
            Parameter[] parameters = targetMethod.getParameters();
            // TODO 反射方法参数中对应的对象，对象中的所有属性为查询字段。 保存的是额外的名称、描述、值
            List<ExtendFiledDTO> extendFiledList = parameterFiledList(parameters, parameterNames, args);
            String description = getMethodDescription(joinPoint);
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            Object user = UserUtil.getUser();
            //  TODO 构建审计日志对象， 并赋值操作
            SysRequestLog sysRequestLog = targetMethod.getDeclaredAnnotation(SysRequestLog.class);
            int type = ActionTypeEnums.actionTypeEnumsEscape(sysRequestLog.actionType().getName());
            // 获取自定义注解中的自动发送还是手动发送的boolean类型标识
            boolean manually = sysRequestLog.manually();
            syslog = createSyslog(request, request.getRequestURI(), methodName, beanName, user, extendFiledList, description, type,
                    manually, operationObject, resResult);
        } catch (Exception e) {
            logger.error("syslog参数构析异常!!!", e);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("generate调整打印日志,构建syslog对象结束...");
        }
        return syslog;
    }

    private static String getOperationObject(ProceedingJoinPoint joinPoint) {
        Annotation annotation = joinPoint.getSignature().getDeclaringType().getAnnotation(Api.class);
        if (annotation != null) {
            String value = ((Api) (annotation)).value();
            String[] tags = ((Api) (annotation)).tags();
            return StringUtils.isNotEmpty(value) ? value : StringUtils.join(tags, ",");
        }
        return "";
    }

    /**
     * 获取连接点的方法的参数。  args[]的参数获取
     * 返回方法中的参数的类型和值，用于查询字段的json保存，保存在表中的sys_log.params_value中
     *
     * @param parameters     参数数组
     * @param parameterNames 参数名字数组
     * @param args           参数数组
     * @return 返回List<ExtendFiledDTO>
     */
    private List<ExtendFiledDTO> parameterFiledList(Parameter[] parameters, String[] parameterNames, Object[] args) {
        List<ExtendFiledDTO> extendFiledList = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> paramClazz = parameter.getType();
            if (logger.isDebugEnabled()) {
                logger.debug("Parameter参数， paramClazz类型: -->{} ", paramClazz);
            }
            Object arg = args[i];
            // TODO ，controller中参数列表中，如果是java简单类型需要加入@ApiParam注解方式
            if (isPrimitive(parameter.getType())) {
                LogField logField = parameter.getAnnotation(LogField.class);
                ApiParam apiParam = parameter.getAnnotation(ApiParam.class);
                String parameterName = parameterNames[i];
                // 获取方法的参数列表中获取对象的值
                List<Map> mapList = getParamkeyValue(parameterName, arg);
                if (!mapList.isEmpty()) {
                    for (Map<String, Object> map : mapList) {
                        for (Map.Entry<String, Object> entity : map.entrySet()) {
                            ExtendFiledDTO extendFiledDTO = new ExtendFiledDTO();
                            if (logField != null) {
                                buildExtendFiledDTOFromLogFieldAnnotation(logField, extendFiledDTO);
                            } else if (apiParam != null) {
                                buildExtendFiledDTOFromApiParamAnnotation(extendFiledDTO, entity.getKey(), apiParam.value());
                            }
                            extendFiledDTO.setFieldValue(entity.getValue());
                            extendFiledList.add(extendFiledDTO);
                        }
                    }
                }
                continue;
            }

            /**
             * fix bugs. 如果参数不带RequestBody 直接是一个对象接收get请求数据，直接continue
             */
            if (parameter.getType().isAssignableFrom(HttpServletRequest.class) || parameter.getType().isAssignableFrom(HttpSession.class)
                    || parameter.getType().isAssignableFrom(HttpServletResponse.class) || parameter.getType().isAssignableFrom(BindingResult.class)
                /*|| parameter.getAnnotation(RequestBody.class) == null*/) {
                continue;
            }
            // TODO  类型为Map，参数为复杂对象类型，这个参数也存在问题
            if (parameter.getType().isAssignableFrom(Map.class)) {
                logger.warn("暂不支持Map类型操作的对象分解!!");
                continue;
            }

            // TODO  参数类型为List<Object>，参数为复杂对象类型，这个参数也存在问题, fixed
            if (parameter.getType().isAssignableFrom(List.class)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("参数类型:{}, 参数名称:{},  参数值:{}", parameter.getType(), parameterNames[i], args[i]);
                }
                for (Object o : (List) arg) {
                    // 循环List中的所有对象，然后进行build额外的对象
                    buildExtendFiledDTOWithComplexParameter(extendFiledList, o.getClass(), o);
                }
            }

            // TODO  类型为复杂类型， ，构建查询对象条件
            buildExtendFiledDTOWithComplexParameter(extendFiledList, paramClazz, arg);
        }
        List<ExtendFiledDTO> list = excludeEmptyFieldValueFromExtendFiled(extendFiledList);
        return list;
    }

    /**
     * 排除原有originalList中fieldValue数据为空的数据
     *
     * @param originalList 原始list值
     * @return 返回排除后的filedValue为空的列表
     */
    private static List<ExtendFiledDTO> excludeEmptyFieldValueFromExtendFiled(List<ExtendFiledDTO> originalList) {
        if (Optional.of(originalList).isPresent()) {
            List<ExtendFiledDTO> collect = originalList.stream()
                    .filter(temp -> StringUtils.isNotEmpty(String.valueOf(temp.getFieldValue())))
                    .collect(Collectors.toList());
            return collect;
        }
        return originalList;
    }

    /**
     * 使用传入参数为复杂类型，来构建查询的条件字段
     *
     * @param extendFiledList 查询集合
     * @param paramClazz      复杂类型clazz
     * @param arg             参数
     */
    private void buildExtendFiledDTOWithComplexParameter(List<ExtendFiledDTO> extendFiledList, Class<?> paramClazz, Object arg) {
        //  TODO 复杂对象类型, 循环各个字段，获取字段及字段的值，构建ExtendFiledDTO。 即查询条件，为空的都会被排除
        Field[] declaredFields = paramClazz.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
            LogField logField = field.getAnnotation(LogField.class);
            try {
                Object fieldValue = field.get(arg);
                if (fieldValue != null && !StringUtils.equalsIgnoreCase("serialVersionUID", field.getName())) {
                    ExtendFiledDTO extendFiledDTO = new ExtendFiledDTO();
                    if (apiModelProperty != null) {
                        buildExtendFiledDTOFromApiParamAnnotation(extendFiledDTO, field.getName(), apiModelProperty.value());
                    }
                    // 覆盖ApiModelProperty注解的值，以字段含有LogField的值为准
                    if (logField != null) {
                        buildExtendFiledDTOFromLogFieldAnnotation(logField, extendFiledDTO);
                    }
                    extendFiledDTO.setFieldValue(fieldValue);
                    extendFiledList.add(extendFiledDTO);
                }
            } catch (IllegalAccessException e) {
                logger.error("syslog参数构析异常!!!", e);
            }
        }
    }

    private void buildExtendFiledDTOFromApiParamAnnotation(ExtendFiledDTO extendFiledDTO, String key, String value) {
        extendFiledDTO.setFieldName(key);
        extendFiledDTO.setFieldDescription(value);
    }

    private void buildExtendFiledDTOFromLogFieldAnnotation(LogField logField, ExtendFiledDTO extendFiledDTO) {
        if (logField.name() != null) {
            extendFiledDTO.setFieldName(logField.name());
        }
        if (logField.description() != null) {
            extendFiledDTO.setFieldDescription(logField.description());
        }
    }

    /**
     * clazz.isPrimitive() 判断Class是否为原始类型（boolean、char、byte、short、int、long、float、double）
     *
     * @param clazz 类类型
     * @return 返回是否是简单类型的boolean值
     */
    private boolean isPrimitive(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class;
    }


    private List<Map> getParamkeyValue(String paramNames, Object arg) {
        Map<String, Object> map = null;
        List list = new ArrayList();
        if (arg != null) {
            if (arg instanceof Double
                    || arg instanceof Float
                    || arg instanceof Long
                    || arg instanceof Short
                    || arg instanceof Byte
                    || arg instanceof Boolean
                    || arg instanceof String
                    || arg instanceof Integer) {
                map = new HashMap<>();
                map.put(paramNames, arg);
                list.add(map);
            } else if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)) {

                String json = "";
                try {
                    json = JSON.toJSONString(arg);
                } catch (Exception e) {
                    logger.error("json转化异常", e);
                }
                map = JSON.parseObject(json, Map.class);
                if (map != null && !map.isEmpty()) {
                    map.put(paramNames, JSON.toJSONString(map));
                    list.add(map);
                }
            } else {
                map = new HashMap<>();
                map.put(paramNames, arg);
            }
        }
        return list;
    }

    /**
     * 连接点方法中如果定义了SysRequestLog 和 ApiOperation 的注解， 以SysRequestLog的注解为准，
     * SysRequestLog的description的值（优先） ，  返回ApiOperation的value的值，
     *
     * @param point 连接点
     * @return 返回方法的描述信息。  例如增加
     */
    private static String getMethodDescription(JoinPoint point) {
        String targetName = point.getTarget().getClass().getName();
        String methodName = point.getSignature().getName();
        Object[] args = point.getArgs();
        Class targetClass = null;
        try {
            targetClass = Class.forName(targetName);
        } catch (ClassNotFoundException e) {
            logger.error("未找到类: {}", targetName, e);
        }
        Method[] methods = targetClass.getMethods();
        String description = "";
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                Class[] clazzs = method.getParameterTypes();
                if (method.isAnnotationPresent(SysRequestLog.class)) {
                    if (clazzs.length == args.length) {
                        description = method.getAnnotation(SysRequestLog.class).description();
                        break;
                    }
                }
                if (method.isAnnotationPresent(ApiOperation.class)) {
                    if (clazzs.length == args.length) {
                        description = method.getAnnotation(ApiOperation.class).value();
                        break;

                    }
                }
            }
        }
        return description;
    }


    /**
     * 构建操作日志对象
     *
     * @param request         请求
     * @param uri             如： /list
     * @param methodName      如： checkItem方法名称
     * @param beanName        如：com.vrv.vap.sys.hardware.controller.SystemSelfCheckController
     * @param user            当前用户
     * @param extendFiledList 查询列表
     * @param description     操作描述信息
     * @param type            操作类型
     * @param resResult       操作结果
     * @return 返回syslog对象
     */
    private C createSyslog(HttpServletRequest request, String uri, String methodName, String beanName, Object user,
                           List<ExtendFiledDTO> extendFiledList, String description, int type, boolean manually, String operationObject,
                           String resResult) {
        SystemLog systemLog = new SystemLog();
        if (user != null) {
            UserdDTO userdDTO = new UserdDTO();
            BeanUtils.copyProperties(user, userdDTO);
            if (StringUtils.isNotEmpty(String.valueOf(userdDTO.getId()))) {
                systemLog.setUserId(String.valueOf(userdDTO.getIdcard()));
            }
            if (StringUtils.isNotBlank(userdDTO.getName())) {
                systemLog.setUserName(userdDTO.getName());
            }
            if (StringUtils.isNotBlank(userdDTO.getOrgName())) {
                systemLog.setOrganizationName(userdDTO.getOrgName());
            }
            if (userdDTO.getRoleName() != null && !userdDTO.getRoleName().isEmpty()) {
                systemLog.setRoleName(userdDTO.getRoleName().get(0));
            }
            systemLog.setLoginType(userdDTO.getLoginType());
        }

        HttpSession session = SessionUtils.getSession();
        if (session != null) {
            Object token = session.getAttribute("token");
            Object taskCode = session.getAttribute("taskCode");
            systemLog.setToken(token != null ? String.valueOf(token) : "");
            systemLog.setTaskCode(token != null ? String.valueOf(taskCode) : "");
        } else {
            logger.error("session 为空，获取token为空！");
        }

        if (Optional.ofNullable(extendFiledList).isPresent()) {
            systemLog.setExtendFields(JSON.toJSON(extendFiledList).toString());
            systemLog.setParamsValue(JSON.toJSON(extendFiledList).toString());
        }
        systemLog.setId(UUID.randomUUID().toString());
        systemLog.setBeanName(beanName);
        systemLog.setMethodName(methodName);
        systemLog.setRequestUrl(uri);
        systemLog.setRequestIp(IpUtils.getIpAddr(request));
        systemLog.setRequestMethod(request.getMethod());
        systemLog.setDescription(description);
        systemLog.setType(type);
        systemLog.setManually(manually);
        systemLog.setResponseResult(Integer.valueOf(resResult));
        String time = DateUtils.getTime();
        systemLog.setRequestTime(DateUtils.dateTime(DateUtils.YYYY_MM_DD_HH_MM_SS, time));
        systemLog.setOperationObject(operationObject);
        String referer = request.getHeader("Referer");
        systemLog.setReferer(referer);
        String requestPageUri = request.getHeader("request-page-uri");
        systemLog.setRequestPageUri(requestPageUri == null ? "" : requestPageUri);
        String requestPageTitle = request.getHeader("request-page-title");
        String title = "";
        if (StringUtils.isEmpty(requestPageTitle)) {
            logger.debug("requestPageTitle is null!");
        } else {
            try {
                logger.debug(String.format("requestPageTitle is --> %s", requestPageTitle));
                byte[] xxx = Base64Utils.decodeFromString(requestPageTitle);
                title = java.net.URLDecoder.decode(new String(xxx, "UTF-8"));
                logger.debug(String.format("title is --> %s", title));
            } catch (Exception e) {
                logger.error("获取requestPageTitle error:", e);
            }
        }
        systemLog.setRequestPageTitle(title);
        return (C) systemLog;
    }


    /**
     * 在配置了@SysRequestLog 注解，同时注解中的manually = false的情况下，会自动发送操作日志
     *
     * @param syslog SystemLog对象
     */
    abstract void sending(C syslog);

}
