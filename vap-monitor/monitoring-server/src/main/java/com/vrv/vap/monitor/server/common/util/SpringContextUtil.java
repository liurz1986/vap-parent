package com.vrv.vap.monitor.server.common.util;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取spring上下文
 * 
 * @author xw
 *
 *         2017年6月19日
 */
@Component
public class SpringContextUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		setValue(arg0);
	}

	public static void setValue(ApplicationContext arg0) throws BeansException {
		applicationContext = arg0;
	}

	public static ApplicationContext getApplicationContext() {
		return applicationContext;
	}

	public static <T> T getBean(Class<T> clazz) {
		return (T) applicationContext.getBean(clazz);
	}
}
