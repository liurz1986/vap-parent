package com.vrv.vap.xc.interceptor;

import com.google.common.collect.Maps;
import com.vrv.vap.xc.tools.SecurityScopeTools;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.cglib.beans.BeanMap;

import java.util.List;
import java.util.Map;
import java.util.Properties;


/**
 * 安全域拦截器
 * <br/>拦截query查询, 对Example对象参数追加安全域过滤
 * <br/>拦截query查询, 对bean对象或map参数追加安全域过滤
 */
@Intercepts({
        @Signature(
                type = Executor.class, method = "query",
                args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}
        )
})
public class SecurityScopeInterceptor implements Interceptor {

    private ThreadLocal tl = new ThreadLocal();

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();

        for (int i = 0; i < args.length; i++) {
            Object arg = args[i];
            if (arg == null) {
                continue;
            }
            String className = arg.getClass().getSimpleName();

            //判断操作类型是否是select
            if (arg instanceof MappedStatement) {
                MappedStatement ms = (MappedStatement) arg;
                SqlCommandType sqlCommandType = ms.getSqlCommandType();
                if (sqlCommandType != SqlCommandType.SELECT) {
                    break;
                }
                continue;
            }

            //对参数添加安全域过滤
            if (arg instanceof Map) {
                SecurityScopeTools.setSecurityScopeM(arg);
                break;
            } else {
                if (className.endsWith("Example")) {
                    //避免重复添加安全域过滤条件
                    if (arg == tl.get()) {
                        break;
                    }

                    tl.set(arg);
                    SecurityScopeTools.setSecurityScope(arg, className);
                    break;
                } else {
                    //对象参数
                    MappedStatement ms = (MappedStatement) args[0];
                    Object parameter = args[1];
                    RowBounds rowBounds = (RowBounds) args[2];
                    ResultHandler resultHandler = (ResultHandler) args[3];
                    Executor executor = (Executor) invocation.getTarget();

                    //重写参数
                    parameter = reWriteToMap(parameter, ms);
                    SecurityScopeTools.setSecurityScopeM(parameter);
                    BoundSql boundSql = ms.getBoundSql(parameter);
                    CacheKey cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
                    return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
                }
            }

        }
        return invocation.proceed();
    }

    private Map<String, Object> reWriteToMap(Object paramObj, MappedStatement ms) throws Exception {
        Map<String, Object> paramMap = new MapperMethod.ParamMap<>();
        if (paramObj == null) {
        } else if (paramObj instanceof Map) {
            return (Map<String, Object>) paramObj;
        } else if (paramObj.getClass().isPrimitive() || paramObj instanceof Integer || paramObj instanceof Long || paramObj instanceof String || paramObj instanceof Boolean) {
            BoundSql boundSql2 = ms.getSqlSource().getBoundSql(paramObj);
            List parameterMappings = boundSql2.getParameterMappings();
            paramMap.put(((ParameterMapping) parameterMappings.get(0)).getProperty(), paramObj);
        } else {
            writeMapParam(paramObj, paramMap);
        }
        return paramMap;
    }

    private void writeMapParam(Object parameterObject, Map<String, Object> paramMap) {
        if (parameterObject instanceof Map  == false) {
            // 如果参数是bean，反射设置值
            paramMap.putAll(beanToMap(parameterObject));
        }
    }

    public static <T> Map<String, Object> beanToMap(T bean) {
        Map<String, Object> map = Maps.newHashMap();
        if (bean != null) {
            BeanMap beanMap = BeanMap.create(bean);
            for (Object key : beanMap.keySet()) {
                map.put(key + "", beanMap.get(key));
            }
        }
        return map;
    }

    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
