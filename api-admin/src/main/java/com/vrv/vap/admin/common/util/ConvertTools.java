package com.vrv.vap.admin.common.util;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 类型转换
 * 
 * @author xw
 *
 * @date 2018年4月3日
 */
public final class ConvertTools {
	private static Log log = LogFactory.getLog(ConvertTools.class);

	/**
	 * 将实体bean转换为map
	 * 
	 * @param obj
	 * @return Map
	 */
	public static Map<String, Object> bean2Map(Object obj) {
		Field[] fields = obj.getClass().getDeclaredFields();
		Map<String, Object> result = new HashMap<String, Object>(fields.length);
		try {
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				StringBuffer sb = new StringBuffer(field.getName().length() + 3);
				sb.append(field.getName());
				sb.setCharAt(0, (char) (sb.charAt(0) - 32));
				if (field.getType().equals(boolean.class)) {
					sb.insert(0, "is");
				} else {
					sb.insert(0, "get");
				}
				result.put(field.getName(), obj.getClass().getDeclaredMethod(sb.toString()).invoke(obj));
			}
		} catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			log.error("", e);
		}
		return result;
	}

	/**
	 * 将map类型转为对象实体类
	 * 
	 * @param data
	 * @parambean
	 */
	public static <T> T wrapBean(Map<String, Object> data, T bean) {
		Object value = null;
		for (Field field : bean.getClass().getDeclaredFields()) {
			value = data.get(field.getName());
			if (null == value) {
				continue;
			}
			convertType(value, bean, field);
		}
		return bean;
	}

	/**
	 * 数据转换
	 * 
	 * @param value
	 * @param bean
	 * @param field
	 */
	private static <T> void convertType(Object value, T bean, Field field) {
		String method = "set" + CommonTools.upperCaseFirstLetter(field.getName());
		Method met = null;
		Object val = null;
		try {
			if (field.getType().equals(String.class)) {
				met = bean.getClass().getDeclaredMethod(method, String.class);
				val = value.toString();
			} else if (field.getType().equals(int.class)) {
				met = bean.getClass().getDeclaredMethod(method, int.class);
				if (StringUtils.isNumericSpace(value.toString())) {
					val = Integer.valueOf(value.toString());
				}
			} else if (field.getType().equals(long.class)) {
				met = bean.getClass().getDeclaredMethod(method, long.class);
				if (StringUtils.isNumericSpace(value.toString())) {
					val = Long.valueOf(value.toString());
				}
			} else if (field.getType().equals(float.class)) {
				met = bean.getClass().getDeclaredMethod(method, float.class);
				if (StringUtils.isNumericSpace(value.toString())) {
					val = Float.valueOf(value.toString());
				}
			} else if (field.getType().equals(double.class)) {
				met = bean.getClass().getDeclaredMethod(method, double.class);
				if (StringUtils.isNumericSpace(value.toString())) {
					val = Double.valueOf(value.toString());
				}
			} else if (field.getType().equals(Date.class)) {
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				try {
					met = bean.getClass().getDeclaredMethod(method, Date.class);
					if (value instanceof Date) {
						val = (Date) value;
					} else {
						val = sdf.parse(value.toString());
					}
				} catch (ParseException e) {
					log.error("", e);
				}
			} else if (field.getType().equals(boolean.class)) {
				met = bean.getClass().getDeclaredMethod(method, boolean.class);
				String tmp = value.toString();
				if ("0".equals(tmp)) {
					val = false;
				} else if ("1".equals(tmp)) {
					val = true;
				} else {
					val = Boolean.parseBoolean(tmp);
				}
			}
		} catch (NoSuchMethodException | SecurityException | IllegalArgumentException e) {
			log.error("", e);
		}
		if (null != met && null != val) {
			try {
				met.invoke(bean, val);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				log.error("", e);
			}
		}
	}
}
