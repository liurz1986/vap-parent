package com.vrv.rule.ruleInfo.udf;

import java.lang.reflect.Field;
import java.util.regex.Pattern;

/**
 * * 
 *
 * @author wudi   E‐mail:wudi@vrvmail.com.cn
 *  @version 创建时间：2019年10月15日 下午5:46:32  类说明 用户自定义工具
 */
public class UdfFunctionUtil {

    /**
     * 对比字段的类型，满足各种不同的数值类型，最后都转换成Long类型
     *
     * @param type
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     */
    public static Long compareRelateType(Class<?> type, String fieldName, Field field, Object obj)
            throws IllegalArgumentException, IllegalAccessException {
        Long longValue = 0L;
        if (type == Long.class) {
            longValue = (Long) field.get(obj);
        } else if (type == Integer.class) {
            Integer fieldValue = (Integer) field.get(obj);
            longValue = new Integer(fieldValue).longValue();
        } else if (type == Double.class) {
            Double fieldValue = (Double) field.get(obj);
            longValue = new Double(fieldValue).longValue();
        } else if (type == Float.class) {
            Float fieldValue = (Float) field.get(obj);
            longValue = new Float(fieldValue).longValue();
        } else if (type == String.class) {
            String fieldValue = (String) field.get(obj);
            try {
                Double doubleNumber = Double.parseDouble(fieldValue);
                longValue = new Double(doubleNumber).longValue();
            } catch (Exception e) {
                throw new RuntimeException(fieldName + "字段不是数值型数据，请检查！");
            }
        } else {
            throw new RuntimeException(fieldName + "字段不是数值型数据，请检查！");
        }
        return longValue;
    }


    /**
     * 是否是整型
     *
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }

    /**
     * 是否是double型
     *
     * @param str
     * @return
     */
    public static boolean isDouble(String str) {
        Pattern pattern = Pattern.compile("^[-//+]?//d+(//.//d*)?|//.//d+$");
        return pattern.matcher(str).matches();
    }

    /**
     * 是否是float型
     *
     * @param str
     * @return
     */
    public static boolean isFloat(String str) {
        Pattern pattern = Pattern.compile("^[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)$");
        return pattern.matcher(str).matches();
    }

    /**
     * 获得double数值类型
     *
     * @param value
     * @return
     */
    public static Double getDoubleValue(Object value) {
        if (value != null) {
			Double doubleNumber = 0.00d;
			String fieldValue  =String.valueOf(value);
			try{
				doubleNumber = Double.parseDouble(fieldValue);
			}catch (Exception e){
				throw new RuntimeException(value + "不是数值型数据，请检查！");
			}
			return doubleNumber;
        } else {
            return 0.00d;
        }
    }

    public static Long getRelateValue(Object value) {
        if (value != null) {
            Class<? extends Object> type = value.getClass();
            Long longValue = 0L;
            if (type == Long.class) {
                longValue = (Long) value;
            } else if (type == Integer.class) {
                Integer fieldValue = (Integer) value;
                longValue = new Integer(fieldValue).longValue();
            } else if (type == Double.class) {
                Double fieldValue = (Double) value;
                longValue = new Double(fieldValue).longValue();
            } else if (type == Float.class) {
                Float fieldValue = (Float) value;
                longValue = new Float(fieldValue).longValue();
            } else if (type == String.class) {
                String fieldValue = (String) value;
                try {
                    Double doubleNumber = Double.parseDouble(fieldValue);
                    longValue = new Double(doubleNumber).longValue();
                } catch (Exception e) {
                    throw new RuntimeException(value + "不是数值型数据，请检查！");
                }
            } else {
                throw new RuntimeException(value + "不是数值型数据，请检查！");
            }
            return longValue;
        } else {
            return 0L;
        }
    }

    public static void main(String[] args) {
		Object s ="6.0";
		Double doubleValue = getDoubleValue(s);
		System.out.println(doubleValue);
	}

    /**
     * 两者之间的比较
     *
     * @param value
     * @param betweenValue
     * @return
     */
    public static boolean compareFieldByBetween(Object value, String betweenValue) {
        String[] numberArray = betweenValue.split(",");
        Long min = getRelateValue(numberArray[0]);
        Long max = getRelateValue(numberArray[1]);
        Long relateValue = getRelateValue(value);
        if (min < max) {
            if (relateValue <= max && relateValue >= min) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new RuntimeException(max + "<" + min + ",请检查！");
        }
    }


    /**
     * 两者的比较
     *
     * @param value
     * @param betweenValue
     * @return
     */
    public static boolean compareFieldByNotBetween(Object value, String betweenValue) {
        String[] numberArray = betweenValue.split(",");
        Long min = getRelateValue(numberArray[0]);
        Long max = getRelateValue(numberArray[1]);
        Long relateValue = getRelateValue(value);
        if (min < max) {
            if (relateValue > max || relateValue < min) {
                return true;
            } else {
                return false;
            }
        } else {
            throw new RuntimeException(max + "<" + min + ",请检查！");
        }
    }


    /**
     * 数字值与阈值比较的结果
     *
     * @param value
     * @param sign
     * @return
     */
    public static boolean compareFieldBySign(Object value, String sign, Long threadHold) {
        Long relateValue = getRelateValue(value);
        switch (sign) {
            case ">":
                if (relateValue > threadHold) {
                    return true;
                }
                break;
            case "<":
                if (relateValue < threadHold) {
                    return true;
                }
                break;
            case "=":
                if (relateValue == threadHold) {
                    return true;
                }
                break;
            case "<>":
                if (relateValue != threadHold) {
                    return true;
                }
                break;
            case ">=":
                if (relateValue >= threadHold) {
                    return true;
                }
                break;
            case "<=":
                if (relateValue <= threadHold) {
                    return true;
                }
            default:
                break;
        }
        return false;
    }

    /**
     * 字符串的比较
     *
     * @param value
     * @param sign
     * @param threadHold
     * @return
     */
    public static boolean compareFieldBySignStr(String value, String sign, String threadHold) {
        switch (sign) {
            case "=":
                if (value.equals(threadHold)) {
                    return true;
                }
                break;
            case "<>":
                if (!value.equals(threadHold)) {
                    return true;
                }
                break;
            case "like":
            case "in":
                if (value.contains(threadHold)) {
                    return true;
                }
                break;
            case "likeBegin":
                if (value.indexOf(threadHold) == 0) {
                    return true;
                }
                break;
            case "likeEnd":
                if (value.endsWith(threadHold)) {
                    return true;
                }
                break;
            case "not like":
            case "not in":
                if (!value.contains(threadHold)) {
                    return true;
                }
                break;
            case "not likeBegin":
                if (value.indexOf(threadHold) != 0) {
                    return true;
                }
                break;
            case "not likeEnd":
                if (!value.endsWith(threadHold)) {
                    return true;
                }
                break;
            default:
                break;
        }
        return false;
    }

    /**
     * Boolean类型的比较
     *
     * @param value
     * @param result
     * @return
     */
    public static boolean compareFieldBySignBoolean(Boolean value, Boolean result) {
        if (value == result) {
            return true;
        }
        return false;
    }


    public static String getOperatoror(String operator, boolean nonAttr) {
        if (nonAttr) { //说明为非
            switch (operator) {
                case ">":
                    operator = "<=";
                    break;
                case "<":
                    operator = ">=";
                    break;
                case "=":
                    operator = "<>";
                    break;
                case ">=":
                    operator = "<";
                    break;
                case "<=":
                    operator = ">";
                    break;
                case "like":
                    operator = "not like";
                    break;
                case "likeBegin":
                    operator = "not likeBegin";
                    break;
                case "likeEnd":
                    operator = "not likeEnd";
                    break;
                case "between":
                    operator = "not between";
                    break;
                case "in":
                    operator = "not in";
                    break;
                default:
                    break;

            }
            return operator;
        } else {
            return operator;
        }
    }

}
