package com.vrv.vap.xc.interceptor;

import com.google.common.collect.Maps;
import com.vrv.vap.xc.tools.SecurityScopeTools;
import com.vrv.vap.toolkit.tools.CommonTools;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.mapping.StatementType;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.wrapper.DefaultObjectWrapperFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;


/**
 * kingbase拦截器
 * <br/>拦截query查询, 对字段加双引号处理
 * <br/>拦截query查询, 对参数追加安全域过滤
 */
@Intercepts({
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class})

})
public class KingQuotationInterceptor implements Interceptor {

    private ThreadLocal tl = new ThreadLocal();

    protected static final Map<String, MappedStatement> MS_CACHE = new ConcurrentHashMap<>();

    public static List<String> keywords = Arrays.asList(" level ", " status ", " case ", " check ", " column ", " create ", " cross ", " current_time ", " current_user ", " full ",
            " offset ", " package ", " source ", " day ", " year ");

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object[] args = invocation.getArgs();
        MappedStatement ms = (MappedStatement) args[0];
        Executor executor = (Executor) invocation.getTarget();
        Object parameter = args[1];

        SqlCommandType sqlCommandType = ms.getSqlCommandType();
        if (sqlCommandType == SqlCommandType.SELECT) {
            if (StatementType.CALLABLE != ms.getStatementType()) {
                return handSelect(invocation, args, ms, executor, sqlCommandType);
            }
        } else if (sqlCommandType == SqlCommandType.INSERT) {
            BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = forKingBaseInsert(ms, boundSql);
            MS_CACHE.putIfAbsent(ms.getId() + "_kb_insert", newMappedStatement(ms, boundSql, sql));
            args[0] = MS_CACHE.get(ms.getId() + "_kb_insert");
        } else if (sqlCommandType == SqlCommandType.UPDATE) {
            BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = forKingBaseUpdate(boundSql);
            MS_CACHE.putIfAbsent(ms.getId() + "_kb_update", newMappedStatement(ms, boundSql, sql));
            args[0] = MS_CACHE.get(ms.getId() + "_kb_update");
        } else if (sqlCommandType == SqlCommandType.DELETE) {
            BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = forKingBaseDelete(boundSql);

            MS_CACHE.putIfAbsent(ms.getId() + "_kb_delete", newMappedStatement(ms, boundSql, sql));
            args[0] = MS_CACHE.get(ms.getId() + "_kb_delete");
        }
        return invocation.proceed();
    }

    private MappedStatement newMappedStatement(final MappedStatement ms, BoundSql oldBound, String sqlStr) {
        MappedStatement newStatement = copyFromMappedStatement(ms, new BoundSqlSqlSource(oldBound, ms.getConfiguration(), sqlStr));
        MetaObject msObject = MetaObject.forObject(newStatement, new DefaultObjectFactory(), new DefaultObjectWrapperFactory(), new DefaultReflectorFactory());
        msObject.setValue("sqlSource.boundSql.sql", sqlStr);
        return newStatement;
    }


    private MappedStatement copyFromMappedStatement(MappedStatement ms, SqlSource newSqlSource) {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId(), newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        List<ResultMap> resultMaps = new ArrayList<>();
        String id = "-inline";
        if (ms.getResultMaps() != null && ms.getResultMaps().size() > 0) {
            id = ms.getResultMaps().get(0).getId() + "-inline";
        }
        ResultMap resultMap = new ResultMap.Builder(null, id, Long.class, new ArrayList()).build();
        resultMaps.add(resultMap);
        builder.resultMaps(resultMaps);
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        builder.databaseId(ms.getDatabaseId());
        return builder.build();
    }

    public static class BoundSqlSqlSource implements SqlSource {
        private BoundSql boundSql;
        private final Configuration configuration;
        private final String sql;

        BoundSqlSqlSource(BoundSql boundSql, Configuration configuration, String sql) {
            this.boundSql = boundSql;
            this.configuration = configuration;
            this.sql = sql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return new BoundSql(configuration, sql, boundSql.getParameterMappings(), parameterObject);
        }
    }

    protected Object handSelect(Invocation invocation, Object[] args, MappedStatement ms, Executor executor, SqlCommandType sqlCommandType) throws Exception {
        if (sqlCommandType != SqlCommandType.SELECT || StatementType.CALLABLE == ms.getStatementType()) {
            return invocation.proceed();
        }
        Object parameter = args[1];
        String className = parameter != null ? parameter.getClass().getSimpleName() : "";
        RowBounds rowBounds = (RowBounds) args[2];
        ResultHandler resultHandler = (ResultHandler) args[3];
        // 重写主键查询sql
        if (ms.getId().endsWith(SelectKeyGenerator.SELECT_KEY_SUFFIX)) {
            String idName = ms.getKeyProperties()[0];
            String seq = CommonTools.camelToUnderLine(CommonTools.lowerCaseFirstLetter(className)) + "_" + idName + "_SEQ";
            BoundSql boundSql = ms.getBoundSql(parameter);
            String sql = String.format("select NEXTVAL('\"%s\"'::REGCLASS)", seq);

            boundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), parameter);
            CacheKey cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
            return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);

        }

        //对参数添加安全域过滤
        if (parameter instanceof Map) {
            SecurityScopeTools.setSecurityScopeM(parameter);
            return invocation.proceed();
        } else {
            if (className.endsWith("Example")) {
                //避免重复添加安全域过滤条件
                if (parameter == tl.get()) {
                    SecurityScopeTools.setSecurityScope(parameter, className);
                }

                tl.set(parameter);
                BoundSql boundSql = ms.getBoundSql(parameter);
                String sql = forKingBaseSelect(boundSql);

                // sql被定义为final无法通过反射赋值, 故new 一个BoundSql
                boundSql = new BoundSql(ms.getConfiguration(), sql, boundSql.getParameterMappings(), parameter);
                CacheKey cacheKey = executor.createCacheKey(ms, parameter, rowBounds, boundSql);
                return executor.query(ms, parameter, RowBounds.DEFAULT, resultHandler, cacheKey, boundSql);
            } else {
                //对象参数
                //重写参数
                parameter = reWriteToMap(parameter, ms);
                SecurityScopeTools.setSecurityScopeM(parameter);
            }
        }
        return invocation.proceed();
    }


    protected String forKingBaseInsert(MappedStatement ms, BoundSql boundSql) throws Exception {
        StringBuilder sb = new StringBuilder(boundSql.getSql());
        if (ms.getKeyGenerator() != null && ms.getKeyGenerator() instanceof SelectKeyGenerator) {
            SelectKeyGenerator keyGenerator = (SelectKeyGenerator) ms.getKeyGenerator();
            Field keyStatement = keyGenerator.getClass().getDeclaredField("keyStatement");
            //Fortify会扫描出漏洞
            //keyStatement.setAccessible(true);
            //改为如下：
            ReflectionUtils.makeAccessible(keyStatement);

            MappedStatement keyMappedStatement = (MappedStatement) keyStatement.get(keyGenerator);
            String idName = keyMappedStatement.getKeyProperties()[0];
            int point = sb.indexOf(")");
            String idCols = ", " + CommonTools.camelToUnderLine(idName);
            sb.insert(point, idCols);
            point = sb.indexOf(")", point + idCols.length() + 1);
            sb.insert(point, ", ?");
        }
        return forKingBaseInsert(sb.toString());
    }

    /**
     * 针对金仓数据库查询语句转义关键字的列
     * select所有列统一加双引号
     * order by 的列添加双引号
     * where的列(如果是关键字)添加双引号
     *
     * @param boundSql
     * @return
     */
    protected String forKingBaseSelect(BoundSql boundSql) {
        String sql = boundSql.getSql();
        // 对select 的列添加双引号
        int selectIndex = sql.toUpperCase().indexOf("SELECT");
        int fromIndex = sql.toUpperCase().indexOf("FROM");
        String selectCols = sql.substring(selectIndex + 6, fromIndex);

        //对where条件的列(关键字)添加双引号, 这里的替换逻辑只适用于生成的代码
        sql = replaceSelectWhere(sql);

        if (selectCols.contains("count(")) {
            return sql;
        }

        String comma = ",";
        String[] cols = selectCols.split(comma);
        StringBuilder sb = new StringBuilder();
        String quote = "\"";
        for (String col : cols) {
            sb.append(" ").append(quote).append(col.trim()).append(quote).append(comma);
        }
        sql = sql.replaceFirst(selectCols, sb.substring(0, sb.length() - 1) + " ");

        // 对order by 的列添加双引号
        int orderByIndex = sql.toUpperCase().indexOf("ORDER BY");
        int limitIndex = sql.toUpperCase().indexOf("LIMIT");
        if (orderByIndex > 0) {
            limitIndex = limitIndex > 0 ? limitIndex : sql.length();
            String orderByCols = sql.substring(orderByIndex + 8, limitIndex);
            StringBuilder oSb = new StringBuilder();
            for (String col : orderByCols.split(comma)) {
                boolean asc = col.toLowerCase().contains("asc");
                boolean desc = col.toLowerCase().contains("desc");
                String order = asc ? "asc" : (desc ? "desc" : "");
                if (asc || desc) {
                    col = col.replace(order, "");
                }
                oSb.append(" ").append(quote).append(col.trim()).append(quote).append(order).append(comma);

            }
            sql = sql.replaceFirst(orderByCols, oSb.substring(0, oSb.length() - 1));
        }

        return sql;
    }

    private String replaceSelectWhere(String sql) {
        int orderByIndex;
        int limitIndex;
        int whereIndex = sql.toUpperCase().indexOf("WHERE ");
        orderByIndex = sql.toUpperCase().indexOf("ORDER BY");
        limitIndex = sql.toUpperCase().indexOf("LIMIT");
        if (whereIndex > 0) {
            int endWhereIndex = orderByIndex > 0 ? orderByIndex : (limitIndex > 0 ? limitIndex : sql.length());
            String whereCondition = sql.substring(whereIndex + 6, endWhereIndex);
            String newWhereCondition = new String(whereCondition.replaceFirst("\\(\\(","(( "));
            boolean hasKeyword = false;
            for (String k : keywords) {
                if (!whereCondition.contains(k)) {
                    continue;
                }
                hasKeyword = true;
                newWhereCondition = newWhereCondition.replaceAll(k, " \"" + k.trim() + "\" ");
            }
            if (hasKeyword) {
                sql = sql.replaceFirst(whereCondition, newWhereCondition);
            }

        }
        return sql;
    }

    /**
     * 针对金仓数据库insert语句转义关键字的列
     * 所有列统一加双引号
     * order by 的列添加双引号
     *
     * @param sql
     * @return
     */
    protected String forKingBaseInsert(String sql) {
        // 对select 的列添加双引号
        int startColIndex = sql.toUpperCase().indexOf("(");
        int endColIndex = sql.toUpperCase().indexOf(")");

        String selectCols = sql.substring(startColIndex + 1, endColIndex);

        String comma = ",";
        String[] cols = selectCols.split(comma);
        StringBuilder sb = new StringBuilder();
        String quote = "\"";
        for (String col : cols) {
            sb.append(" ").append(quote).append(col.trim()).append(quote).append(comma);
        }
        sql = sql.replaceFirst(selectCols, sb.substring(0, sb.length() - 1) + " ");

        return sql;
    }

    /**
     * 针对金仓数据库查询语句转义关键字的列
     *
     * @param boundSql
     * @return
     */
    protected String forKingBaseUpdate(BoundSql boundSql) {
        String comma = ",";
        String quote = "\"";
        String equal = "=";
        String blank = " ";
        String sql = boundSql.getSql();

        //对where条件的列(关键字)添加双引号, 这里的替换逻辑只适用于生成的代码
        int setIndex = sql.toUpperCase().indexOf("SET ");
        int whereIndex = sql.toUpperCase().indexOf("WHERE ");
        sql = replaceWhere(sql, whereIndex);
        if (setIndex > 0) {
            int endWhereIndex = whereIndex > 0 ? whereIndex : sql.length();
            String colsVal = sql.substring(setIndex + 4, endWhereIndex);
            String newColsVal = new String(colsVal);

            StringBuilder sb = new StringBuilder();
            for (String colVal : colsVal.split(comma)) {
                sb.setLength(0);
                String[] colValArray = colVal.split(equal);
                sb.append(quote).append(colValArray[0].trim()).append(quote).append(blank).append(equal).append(blank);
                newColsVal = newColsVal.replaceFirst(colVal.substring(0, colVal.length() - 1), sb.substring(0, sb.length() - 1));
            }
            sql = sql.substring(0, setIndex) + "set " + newColsVal + " " + sql.substring(whereIndex, sql.length());
        }

        return sql;
    }

    /**
     * 针对金仓数据库查询语句转义关键字的列
     *
     * @param boundSql
     * @return
     */
    protected String forKingBaseDelete(BoundSql boundSql) {
        String sql = boundSql.getSql();
        int whereIndex = sql.toUpperCase().indexOf("WHERE ");
        sql = replaceWhere(sql, whereIndex);
        return sql;
    }

    private String replaceWhere(String sql, int whereIndex) {
        if (whereIndex > 0) {
            int endWhereIndex = sql.length();
            String whereCondition = sql.substring(whereIndex + 6, endWhereIndex);
            String newWhereCondition = new String(whereCondition);
            boolean hasKeyword = false;
            for (String k : keywords) {
                if (!whereCondition.contains(k)) {
                    continue;
                }
                hasKeyword = true;
                newWhereCondition = newWhereCondition.replaceAll(k, " \"" + k.trim() + "\" ");
            }
            if (hasKeyword) {
                sql = sql.replaceFirst(whereCondition, newWhereCondition);
            }
        }
        return sql;
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
        if (parameterObject instanceof Map) {
        } else {
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
