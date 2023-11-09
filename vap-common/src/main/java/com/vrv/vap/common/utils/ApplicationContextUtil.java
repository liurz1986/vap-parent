package com.vrv.vap.common.utils;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取IOC容器中的ApplicationContext
 *
 * @author wh1107066
 */
@Component
public class ApplicationContextUtil implements ApplicationContextAware {

    private static Logger logger = LoggerFactory.getLogger(ApplicationContextUtil.class);
    /**
     * 声明一个静态变量保存
     **/
    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        logger.info("applicationContext正在初始化,application:" + applicationContext);
        ApplicationContextUtil.applicationContext = applicationContext;
    }

    /**
     * 通过实体类获取Bean
     *
     * @param clazz clazz
     * @param <T>   类类型
     * @return 返回实例
     */
    public static <T> T getBean(Class<T> clazz) throws BeansException {
        if (applicationContext == null) {
            logger.info("applicationContext是空的");
            throw new RuntimeException("applicationContext是空");
        }
        return applicationContext.getBean(clazz);
    }


    public static Object getBean(String name) throws BeansException {
        return applicationContext.getBean(name);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static Object getBean(String name, Class requiredType) throws BeansException {
        return applicationContext.getBean(name, requiredType);
    }


    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }


    public static boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.isSingleton(name);
    }

    @SuppressWarnings("rawtypes")
    public static Class getType(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getType(name);
    }

    public static String[] getAliases(String name) throws NoSuchBeanDefinitionException {
        return applicationContext.getAliases(name);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
