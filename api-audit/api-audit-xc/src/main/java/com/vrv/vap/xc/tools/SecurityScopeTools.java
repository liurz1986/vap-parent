package com.vrv.vap.xc.tools;

import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.config.PermissionMappingConfig;
import com.vrv.vap.toolkit.tools.CommonTools;
import com.vrv.vap.toolkit.tools.SessionTools;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 安全域权限工具类
 */
public class SecurityScopeTools {

    private static final Log log = LogFactory.getLog(SecurityScopeTools.class);

    private static final String SECURITY_CODE_FORMAT = "inet_aton(%s)>=%s and inet_aton(%s)<=%s";

    private static final String SECURITY_CODE_FORMAT2 = "inet_aton(%s) between %s and %s";

    private static PermissionMappingConfig permissionMappingConfig = VapXcApplication.getApplicationContext().getBean(PermissionMappingConfig.class);

    private static Map<String, String[]> mysqlMapping = permissionMappingConfig.getMysqlMapping();

    private static Map<String, String[]> mysqlIpMapping = permissionMappingConfig.getMysqlIpMapping();

    /**
     * 设置安全域 (example类)
     *
     * @param example
     */
    public static void setSecurityScope(Object example, String exampleName) {

        if (!SessionTools.isPermissionCheck()) {
//            log.info("不需要安全域数据权限过滤~~~~~~~~");
            return;
        }

        //获取表名(默认生成的Example类都可以这样获取)
        String tableName = CommonTools.camelToUnderLine(exampleName.substring(0, exampleName.indexOf("Example")));
        tableName = tableName.substring(1, tableName.length());
        String[] securityField = null;
        if ((securityField = mysqlMapping.get(tableName)) != null) {
//             log.info(String.format("%s表安全域过滤", tableName));
            setSecurityScope(example, securityField);
        } else if ((securityField = mysqlIpMapping.get(tableName)) != null) {
//             log.info(String.format("%s表安全域过滤", tableName));
            setSecurityScopeIP(example, securityField);
        }

    }


    /**
     * 设置安全域 (手写类)
     *
     * @param arg
     */
    public static void setSecurityScopeM(Object arg) {

        if (!SessionTools.isPermissionCheck()) {
            if (arg instanceof Map) {
                //不设置空参数则可能生成sql时报错
                ((Map) arg).put("securityScope", null);
                ((Map) arg).put("securityScopeIP", null);
            }
//            log.info("不需要安全域数据权限过滤~~~~~~~~");
            return;
        }

        Set<String> securityDomains = SessionTools.getSecurityDomains();
//        securityDomains.add("420000000000");
        if (securityDomains == null || securityDomains.isEmpty()) {
            ((Map) arg).put("securityScope", null);
            ((Map) arg).put("securityScopeIP", null);
            return;
        }
        //安全域code
        List<String> securityCode = new ArrayList<>(securityDomains);
        if (arg instanceof Map) {
//            ((Map) arg).put("areaCode","420000");
            String securityScope = securityDomains.stream().map(r -> "'" + r + "'").collect(Collectors.joining(",", " in (", ")"));
            ((Map) arg).put("securityScope", securityScope);
            Set<String> securityDomainAndIpRange = SessionTools.getIpRange();

            if (securityDomainAndIpRange == null || securityDomainAndIpRange.isEmpty()) {
//                ((Map) arg).put("securityScope", null);
                ((Map) arg).put("securityScopeIP", null);
                return;
            }
            List<String> ipList = securityDomainAndIpRange.stream().map(r -> String.format(" between %s and %s ", r.split(",")[0], r.split(",")[1])).collect(Collectors.toList());
            ((Map) arg).put("securityScopeIP", ipList);
        }
    }

    /**
     * 设置安全域
     *
     * @param example
     */
    private static void setSecurityScope(Object example, String[] securityField) {

        Set<String> securityDomains = SessionTools.getSecurityDomains();
        if (securityDomains == null || securityDomains.isEmpty()) {
            return;
        }

        //安全域code
        List<String> securityCode = new ArrayList<>(securityDomains);

        Class<?> clazz = example.getClass();
        try {
            Object criterias = clazz.getDeclaredMethod("getOredCriteria").invoke(example);
            List criteriasList = (List) criterias;

            Object criteria = criteriasList.isEmpty() ? clazz.getDeclaredMethod("createCriteria").invoke(example) : criteriasList.get(0);

            criteriasList.forEach(c -> eachCriteriaSecurityScope(securityField, securityCode, c));

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            log.error("", e);
        }
    }

    private static void eachCriteriaSecurityScope(String[] securityField, List<String> securityCode, Object criteria) {
        try {
            Class<?> subClazz = criteria.getClass().getSuperclass();

            Method addCriterion = subClazz.getDeclaredMethod("addCriterion", String.class);
            ReflectionUtils.makeAccessible(addCriterion);
            //addCriterion.setAccessible(true);
            String condition = Arrays.stream(securityField).map(field ->
                    field + " in (" + securityCode.stream().map(r -> "'" + r + "'").collect(Collectors.joining(",")) + ")"
            ).collect(Collectors.joining(" or "));
            addCriterion.invoke(criteria, condition);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("", e);
        }
    }

    /**
     * 设置安全域(IP段)
     *
     * @param example
     * @param securityField 安全域字段
     */
    private static void setSecurityScopeIP(Object example, String[] securityField) {

        Set<String> securityDomainAndIpRange = SessionTools.getIpRange();
        if (securityDomainAndIpRange == null || securityDomainAndIpRange.isEmpty()) {
            return;
        }

        Class<?> clazz = example.getClass();
        try {
            Object criterias = clazz.getDeclaredMethod("getOredCriteria").invoke(example);
            List criteriasList = (List) criterias;
            Object criteria = criteriasList.isEmpty() ? clazz.getDeclaredMethod("createCriteria").invoke(example) : criteriasList.get(0);
            criteriasList.forEach(c -> eachCriteriaSecurityScopeIp(securityField, securityDomainAndIpRange, c));

        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            log.error("", e);
        }
    }

    private static void eachCriteriaSecurityScopeIp(String[] securityField, Set<String> securityDomainAndIpRange, Object criteria) {
        try {
            Class<?> subClazz = criteria.getClass().getSuperclass();

            Method addCriterion = subClazz.getDeclaredMethod("addCriterion", String.class);
            ReflectionUtils.makeAccessible(addCriterion);
            //addCriterion.setAccessible(true);
            String condition = Arrays.stream(securityField).map(field ->
                    "(" + securityDomainAndIpRange.stream().map(r -> {
                        return String.format(SECURITY_CODE_FORMAT2, field, r.split(",")[0], r.split(",")[1]);
                    }).collect(Collectors.joining(" or ")) + ")"
            ).collect(Collectors.joining(" or "));
            addCriterion.invoke(criteria, condition);
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
