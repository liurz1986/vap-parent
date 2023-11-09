package com.vrv.vap.toolkit.tools;

import com.alibaba.fastjson.JSON;
import com.github.xtool.collect.Lists;
import com.github.xtool.collect.Maps;
import com.github.xtool.util.StringUtil;
import com.vrv.vap.toolkit.annotations.Ignore;
import com.vrv.vap.toolkit.annotations.LogDict;
import com.vrv.vap.toolkit.constant.ActionTypeEnum;
import com.vrv.vap.toolkit.model.ParamModel;
import com.vrv.vap.toolkit.model.SystemLog;
import com.vrv.vap.toolkit.model.UserInfoModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.util.Base64Utils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 切面日志处理工具
 * Created by lizj on 2019/10/14.
 */
public class LogAspectTools {

    private static Log log = LogFactory.getLog(LogAspectTools.class);

    /*private static final String FILTER = "filter";
    private static final String FEIGN_INTERFACE = "feignInterface";*/
    /**handler*/
    public static SystemLog handler(JoinPoint jp, ServletRequestAttributes attributes, Optional<UserInfoModel> userInfoModelOpt) {
        Ignore ignore = jp.getTarget().getClass().getDeclaredAnnotation(Ignore.class);
        if (null != ignore) {
            if (log.isDebugEnabled()) {
                log.debug(jp.getTarget().getClass().getSimpleName() + ":标记为忽略,不记录日志");
            }
            return null;
        }

        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        ignore = targetMethod.getDeclaredAnnotation(Ignore.class);
        if (null != ignore) {
            if (log.isDebugEnabled()) {
                log.debug(targetMethod.getName() + ":标记为忽略,不记录日志");
            }
            return null;
        }


        SystemLog systemLog = new SystemLog();
        systemLog.setType(ActionTypeEnum.typeName(targetMethod.getName()));
        systemLog.setBeanName(jp.getSignature().getDeclaringTypeName());

        HttpServletRequest request = attributes.getRequest();

        // 过滤feign请求
        /*String filterFeignInterface = request.getHeader(FILTER);
        if (StringUtils.isNotBlank(filterFeignInterface)) {
            if (FEIGN_INTERFACE.equals(filterFeignInterface)) {
                if (log.isDebugEnabled()) {
                    log.debug(targetMethod.getName() + ":为feign请求,不记录日志");
                }
                return null;
            }
        }*/

        // 根据要求只记录用户行为，新增、修改、删除和查询，查询由前端调用接口记日志，后台只需要记录新增、修改和删除
        if (!"PUT".equals(request.getMethod()) && !"PATCH".equals(request.getMethod()) && !"DELETE".equals(request.getMethod())) {
            return null;
        }
        HttpServletResponse response = attributes.getResponse();
        systemLog.setResponseResult(response.getStatus() == 200 ? 1 : 0);

        systemLog.setMethodName(jp.getSignature().getName());
        systemLog.setRequestUrl(request.getRequestURI());
        systemLog.setRequestIp(IpTools.getIpAddress(request));
        systemLog.setRequestMethod(request.getMethod());

        // 请求参数
        Object[] args = jp.getArgs();
        Parameter[] parameters = targetMethod.getParameters();
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(targetMethod);
        List paramList = parameterFiledList(parameters, parameterNames, args);
        if (Lists.isNotEmpty(paramList)) {
            systemLog.setParamsValue(JSON.toJSON(paramList).toString());
        }
        /*String params = "";
        if ("POST".equals(request.getMethod())) {
            Object[] paramsArray = jp.getArgs();
            params = argsArrayToString(paramsArray);
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            params = paramsMap.toString();
        }
        systemLog.setParamsValue(params);*/
        // systemLog.setDescription(null != api ? api.value() : "");
        ApiOperation api = targetMethod.getDeclaredAnnotation(ApiOperation.class);
        systemLog.setDescription(null != api ? api.value() : "");
        systemLog.setRequestTime(new Date());

        //判断是否有补充日志信息(目前只有应用系统变化审计) add 20201231
        String extendDesc = getExtendDesc(args);
        if (extendDesc != null) {
            systemLog.setDescription(systemLog.getDescription() + ", " + extendDesc);
        }

        if (userInfoModelOpt.isPresent()) {
            UserInfoModel userInfoModel = userInfoModelOpt.get();
            if (StringUtils.isNotEmpty(userInfoModel.getIdcard())) {
                systemLog.setUserId(userInfoModel.getIdcard());
            }
            if (StringUtils.isNotBlank(userInfoModel.getName())) {
                systemLog.setUserName(userInfoModel.getName());
            }
            if (StringUtils.isNotBlank(userInfoModel.getOrgName())) {
                systemLog.setOrganizationName(userInfoModel.getOrgName());
            }
            if (userInfoModel.getRoleName() != null && userInfoModel.getRoleName().size() > 0) {
                systemLog.setRoleName(userInfoModel.getRoleName().get(0));
            }
            systemLog.setLoginType(userInfoModel.getLoginType());
        } else {
            log.warn("未获取到登录用户信息");
            systemLog.setDescription(systemLog.getDescription() + ";未获取到登录用户信息");
        }
        return systemLog;
    }
    /**handler4Map*/
    public static Map<String, Object> handler4Map(JoinPoint jp, ServletRequestAttributes attributes,
                                                  Optional<UserInfoModel> userInfoModelOpt) {
        Ignore ignore = jp.getTarget().getClass().getDeclaredAnnotation(Ignore.class);
        if (null != ignore) {
            if (log.isDebugEnabled()) {
                log.debug(jp.getTarget().getClass().getSimpleName() + ":标记为忽略,不记录日志");
            }
            return null;
        }

        Signature signature = jp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();

        ignore = targetMethod.getDeclaredAnnotation(Ignore.class);
        if (null != ignore) {
            if (log.isDebugEnabled()) {
                log.debug(targetMethod.getName() + ":标记为忽略,不记录日志");
            }
            return null;
        }

        ApiOperation api = targetMethod.getDeclaredAnnotation(ApiOperation.class);
        Map<String, Object> systemLog = new HashMap<>();
        systemLog.put("type", ActionTypeEnum.typeName(targetMethod.getName()));
        systemLog.put("bean_name", jp.getSignature().getDeclaringTypeName());

        HttpServletRequest request = attributes.getRequest();

        // 过滤feign请求
        /*String filterFeignInterface = request.getHeader(FILTER);
        if (StringUtils.isNotBlank(filterFeignInterface)) {
            if (FEIGN_INTERFACE.equals(filterFeignInterface)) {
                if (log.isDebugEnabled()) {
                    log.debug(targetMethod.getName() + ":为feign请求,不记录日志");
                }
                return null;
            }
        }*/

        // 根据要求只记录用户行为，新增、修改、删除和查询，查询由前端调用接口记日志，后台只需要记录新增、修改和删除
        if (!"POST".equals(request.getMethod()) && !"PUT".equals(request.getMethod()) && !"PATCH".equals(request.getMethod()) && !"DELETE".equals(request.getMethod())) {
            return null;
        }
        HttpServletResponse response = attributes.getResponse();
        systemLog.put("response_result", response.getStatus() == 200 ? 1 : 0);

        systemLog.put("method_name", jp.getSignature().getName());
        systemLog.put("request_url", request.getRequestURI());
        systemLog.put("request_ip", IpTools.getIpAddress(request));

        // 获取请求头中

        // 请求参数页面url和页面标题标题字段
        String url = request.getHeader("request-page-uri");
        String title = request.getHeader("request-page-title");
        systemLog.put("request_page_uri", url);
        systemLog.put("request_page_title", title);

        Object[] args = jp.getArgs();
        Parameter[] parameters = targetMethod.getParameters();
        ParameterNameDiscoverer parameterNameDiscoverer = new LocalVariableTableParameterNameDiscoverer();
        String[] parameterNames = parameterNameDiscoverer.getParameterNames(targetMethod);
        List<ParamModel> paramList = parameterFiledList(parameters, parameterNames, args);
        if (Lists.isNotEmpty(paramList)) {
            systemLog.put("params_value", JSON.toJSON(paramList).toString());
        }
        // 新增、删除时的描述
        String desc = buildDesc(paramList);
        // 方面描述
        String methodDesc = "PUT".equals(request.getMethod()) ? "新增" : "PATCH".equals(request.getMethod()) ? "修改" : "删除";
        /*String params = "";
        if ("POST".equals(request.getMethod())) {
            Object[] paramsArray = jp.getArgs();
            params = argsArrayToString(paramsArray);
        } else {
            Map<?, ?> paramsMap = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
            params = paramsMap.toString();
        }
        systemLog.setParamsValue(params);*/
        // systemLog.setDescription(null != api ? api.value() : "");
        //判断是否有补充日志信息(目前只有应用系统变化审计) add 20201231
        String extendDesc = getExtendDesc(args);
        // 修改
        if ("PATCH".equals(request.getMethod()) && extendDesc != null) {
            systemLog.put("description", null != api ? api.value() + ":" + extendDesc : methodDesc + ":" + extendDesc);
        } else {
            // 新增、删除
            systemLog.put("description", null != api ? api.value() + ":" + desc : methodDesc + ":" + desc);
        }

        systemLog.put("request_time", TimeTools.format(new Date(), "yyyy-MM-dd HH:mm:ss"));

        if (userInfoModelOpt.isPresent()) {
            UserInfoModel userInfoModel = userInfoModelOpt.get();
            if (StringUtils.isNotEmpty(userInfoModel.getIdcard())) {
                systemLog.put("user_id", userInfoModel.getIdcard());
            }
            if (StringUtils.isNotBlank(userInfoModel.getName())) {
                systemLog.put("user_name", userInfoModel.getName());
            }
            if (StringUtils.isNotBlank(userInfoModel.getOrgName())) {
                systemLog.put("organization_name", userInfoModel.getOrgName());
            }
            if (userInfoModel.getRoleName() != null && userInfoModel.getRoleName().size() > 0) {
                systemLog.put("role_name", userInfoModel.getRoleName().get(0));
            }
            systemLog.put("login_type", userInfoModel.getLoginType());
        } else {
            log.warn("未获取到登录用户信息");
            systemLog.put("description", systemLog.get("description") + ";未获取到登录用户信息");
        }

        systemLog.put("requestPageUri",request.getHeader("request-page-uri"));
        String pageTitle = request.getHeader("request-page-title");
        if(StringUtils.isNotEmpty(pageTitle)){
            byte[] bytes = Base64Utils.decodeFromString(pageTitle);
            try{
                pageTitle = URLDecoder.decode(new String(bytes,"UTF-8"));
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
            systemLog.put("request_page_title",pageTitle);
        }
        return systemLog;
    }

    /**
     * 根据参数列表拼装描述信息
     * @List paramList
     * @return
     */
    private static String buildDesc(List<ParamModel> paramList) {
        String result = "";
        StringBuffer sb = new StringBuffer();
        if (paramList.size() > 0) {
            sb.append("【");
            for (ParamModel model : paramList) {
                if (StringUtils.isNotEmpty(model.getFieldDescription())) {
                    sb.append(model.getFieldDescription());
                    sb.append(":");
                    sb.append(model.getFieldValue());
                    sb.append(";");
                }
            }
        }
        String tmpStr = sb.toString();
        if (tmpStr.endsWith(";")) {
            result = tmpStr.substring(0, tmpStr.length() - 1);
        }
        return result + "】";
    }

    /**
     * 判断是否有补充日志信息(目前只有应用系统变化审计)
     *
     * @Object args
     */
    protected static String getExtendDesc(Object[] args) {
        List paramList = Arrays.stream(args).collect(Collectors.toList());
        String extendDesc = null;
        try {
            //判断是否有补充日志信息(目前只有应用系统变化审计)
            Optional extendDescParam = paramList.stream().filter(f -> {
                try {
                    f.getClass().getMethod("getExtendDesc");
                    return true;
                } catch (NoSuchMethodException e) {
                    return false;
                }
            }).findFirst();
            if (extendDescParam.isPresent()) {
                Object param = extendDescParam.get();
                Method extendDescMethod = param.getClass().getMethod("getExtendDesc");
                extendDescMethod.setAccessible(true);
                Object extendDescO = extendDescMethod.invoke(param);
                if (extendDescO != null) {
                    extendDesc = String.valueOf(extendDescO);
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return extendDesc;
    }

    private static List<ParamModel> parameterFiledList(Parameter[] parameters, String[] parameterNames, Object[] args) {
        List<ParamModel> paramList = Lists.newArrayList();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Class<?> paramClazz = parameter.getType();
            Object arg = Arrays.stream(args).filter(ar -> paramClazz.isAssignableFrom(ar.getClass())).findFirst().get();
            // 是否原始类型
            if (isPrimite(parameter.getType())) {
                ApiParam apiParam = parameter.getAnnotation(ApiParam.class);
                LogDict logDict = parameter.getAnnotation(LogDict.class);
                String parameterName = parameterNames[i];
                List<Map> mapList = getParamkeyValue(parameterName, arg);
                if (Lists.isNotEmpty(mapList)) {
                    for (Map<String, Object> map : mapList) {
                        for (Map.Entry<String, Object> entity : map.entrySet()) {
                            if (Objects.isNull(entity.getValue()) && !"".equals(entity.getValue().toString())) {
                                ParamModel paramModel = new ParamModel();
                                paramModel.setFieldName(StringUtil.toUnderScoreCase((entity.getKey())));
                                paramModel.setFieldDescription(apiParam.value());
                                if(logDict != null && StringUtils.isNotEmpty(logDict.value())){
                                    paramModel.setFieldValue(DictTools.translate(logDict.value(),entity.getValue().toString()));
                                }else{
                                    paramModel.setFieldValue(entity.getValue());
                                }
                                paramList.add(paramModel);
                            }
                        }
                    }
                }
                continue;
            }
            // 过滤非业务参数
            if (parameter.getType().isAssignableFrom(HttpServletRequest.class)
                    || parameter.getType().isAssignableFrom(HttpSession.class)
                    || parameter.getType().isAssignableFrom(HttpServletResponse.class)
                    || parameter.getAnnotation(RequestBody.class) == null) {
                continue;
            }
            Field[] declaredFields = paramClazz.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                ApiModelProperty apiModelProperty = field.getAnnotation(ApiModelProperty.class);
                LogDict logDict = field.getAnnotation(LogDict.class);
                try {
                    Object fieldValue = field.get(arg);
                    if (fieldValue != null && !"".equals(fieldValue.toString()) && null != apiModelProperty) {
                        ParamModel paramModel = new ParamModel();
                        paramModel.setFieldName(StringUtil.toUnderScoreCase(field.getName()));
                        paramModel.setFieldDescription(apiModelProperty.value());
                        if(logDict != null && StringUtils.isNotEmpty(logDict.value())){
                            paramModel.setFieldValue(DictTools.translate(logDict.value(),fieldValue.toString()));
                        }else{
                            paramModel.setFieldValue(fieldValue);
                        }
                        paramList.add(paramModel);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return paramList;
    }

    private static boolean isPrimite(Class<?> clazz) {
        return clazz.isPrimitive() || clazz == String.class;
    }

    private static List<Map> getParamkeyValue(String paramNames, Object arg) {
        Map<String, Object> map = null;
        List list = new ArrayList();

        if (arg != null) {
            if (arg instanceof Integer
                    || arg instanceof Double
                    || arg instanceof Float
                    || arg instanceof Long
                    || arg instanceof Short
                    || arg instanceof Byte
                    || arg instanceof Boolean
                    || arg instanceof String) {
                map = Maps.newConcurrentHashMap();
                map.put(paramNames, arg);
                list.add(map);
            } else if (!(arg instanceof HttpServletRequest) && !(arg instanceof HttpServletResponse)) {

                String json = "";
                try {
                    json = JSON.toJSONString(arg);
                } catch (Exception e) {
                    log.error("", e);
                }
                map = JSON.parseObject(json, Map.class);
                if (map != null && !map.isEmpty()) {
                    map.put(paramNames, JSON.toJSONString(map));
                    list.add(map);
                }
            } else {
                map = Maps.newConcurrentHashMap();
                map.put(paramNames, arg);
            }
        }
        return list;
    }
    /**getIpAddress*/
    public static String getIpAddress(HttpServletRequest request) {
        String xip = request.getHeader("X-Real-IP");
        String forward = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(forward) && !"unKnown".equalsIgnoreCase(forward)) {
            //多次反向代理后会有多个ip值，第一个ip才是真实ip
            int index = forward.indexOf(",");
            if (index != -1) {
                return forward.substring(0, index);
            } else {
                return forward;
            }
        }
        forward = xip;
        if (StringUtils.isNotEmpty(forward) && !"unKnown".equalsIgnoreCase(forward)) {
            return forward;
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getHeader("HTTP_CLIENT_IP");
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (StringUtils.isBlank(forward) || "unknown".equalsIgnoreCase(forward)) {
            forward = request.getRemoteAddr();
        }
        return forward;
    }
}
