package com.vrv.vap.xc.interceptor;

import com.baomidou.mybatisplus.core.MybatisParameterHandler;
import com.baomidou.mybatisplus.core.toolkit.ExceptionUtils;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


/**
 * kingbase拦截器
 * <br/>拦截insert查询,  处理主键
 */
@Intercepts({
//        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class})

})
public class KingQuotationUpdateInterceptor implements Interceptor {

    private List<String> keywords = KingQuotationInterceptor.keywords;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {

        Method method = invocation.getMethod();
        switch (method.getName()) {
            case "prepare":
                return handlePrepare(invocation);
            case "update":
                return handleUpdate(invocation);
            default:
                break;
        }
        return invocation.proceed();

    }

    protected Object handleUpdate(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        MappedStatement ms = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.UPDATE == ms.getSqlCommandType() || SqlCommandType.DELETE == ms.getSqlCommandType()) {
            return invocation.proceed();
        }

        try {
            BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
            Object paramObj = boundSql.getParameterObject();
            if (ms.getKeyGenerator() != null && ms.getKeyGenerator() instanceof SelectKeyGenerator) {
                SelectKeyGenerator keyGenerator = (SelectKeyGenerator) ms.getKeyGenerator();
                Field keyStatement = keyGenerator.getClass().getDeclaredField("keyStatement");
                //keyStatement.setAccessible(true);
                ReflectionUtils.makeAccessible(keyStatement);

                MappedStatement keyMappedStatement = (MappedStatement) keyStatement.get(keyGenerator);
                String idName = keyMappedStatement.getKeyProperties()[0];
                Field idField = paramObj.getClass().getDeclaredField(idName);
                ReflectionUtils.makeAccessible(idField);
                //idField.setAccessible(true);
                String id = String.valueOf(idField.get(paramObj));
                PreparedStatement statement = (PreparedStatement) invocation.getArgs()[0];
                int parameterCount = boundSql.getSql().length() - boundSql.getSql().replaceAll("\\?", "").length();
                //设置主键参数
                statement.setString(parameterCount, id);
            }

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invocation.proceed();
    }

    protected Object handlePrepare(Invocation invocation) throws InvocationTargetException, IllegalAccessException {
        StatementHandler statementHandler = PluginUtils.realTarget(invocation.getTarget());
        MetaObject metaObject = SystemMetaObject.forObject(statementHandler);

        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        if (SqlCommandType.UPDATE != mappedStatement.getSqlCommandType() && SqlCommandType.DELETE != mappedStatement.getSqlCommandType()) {
            return invocation.proceed();
        }

        BoundSql boundSql = (BoundSql) metaObject.getValue("delegate.boundSql");
        Object paramObj = boundSql.getParameterObject();
        String className = paramObj != null ? paramObj.getClass().getSimpleName() : "";

//        String sql = forKingBaseUpdate(boundSql);
        String sql = "";
        Connection connection = (Connection) invocation.getArgs()[0];

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            MybatisParameterHandler parameterHandler = new MybatisParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ErrorContext.instance().sql(sql);
            List<ParameterMapping> mappings = new ArrayList<>(boundSql.getParameterMappings());
            metaObject.setValue("delegate.boundSql.parameterMappings", mappings);

            return statement;
        } catch (Exception e) {
            throw ExceptionUtils.mpe("Error: execution error of sql : \n %s \n", e, sql);
        }
    }


    @Override
    public Object plugin(Object target) {
        return Plugin.wrap(target, this);
    }

    @Override
    public void setProperties(Properties properties) {

    }
}
