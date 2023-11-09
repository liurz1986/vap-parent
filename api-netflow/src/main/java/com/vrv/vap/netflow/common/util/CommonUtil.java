package com.vrv.vap.netflow.common.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommonUtil {


    private static final Logger log = LoggerFactory.getLogger(CommonUtil.class);
    private static Pattern upPattern = Pattern.compile("[A-Z]");
    //环境变量
    public static final String ENVIROMENT_PATH = "/etc/profile";

    /**
     * 驼峰转下划线
     *
     * @param tmp
     * @return
     */
    public static String camelToUnderLine(String tmp) {
        Matcher matcher = upPattern.matcher(tmp);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }


    /**
     * MapToBean
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

    public static String upperCaseFirstLetter(String tmp) {
        if (tmp.charAt(0) > '`' && tmp.charAt(0) < '{') {
            char[] cs = tmp.toCharArray();
            cs[0] = (char) (cs[0] - 32);
            return new String(cs);
        } else {
            return tmp;
        }
    }

    private static <T> void convertType(Object value, T bean, Field field) {
        String method = "set" + upperCaseFirstLetter(field.getName());
        Method met = null;
        Object val = null;
        try {
            if (field.getType().equals(String.class)) {
                met = bean.getClass().getDeclaredMethod(method, String.class);
                val = value.toString();
            } else if (field.getType().equals(Integer.class)) {
                met = bean.getClass().getDeclaredMethod(method, Integer.class);
                if (StringUtils.isNumericSpace(value.toString())) {
                    val = Integer.valueOf(value.toString());
                }
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

    // 从类unix机器上获取mac地址
    public static String getMac(String ip) throws IOException {
        String mac = "";
        if (ip != null) {
            try {
                Process process = Runtime.getRuntime().exec(CleanUtil.cleanString("arp " + ip));
                InputStreamReader ir = new InputStreamReader(process.getInputStream());
                LineNumberReader input = new LineNumberReader(ir);
                String line;
                StringBuffer s = new StringBuffer();
                while ((line = input.readLine()) != null) {
                    s.append(line);
                }
                mac = s.toString();
                if (StringUtils.isNotBlank(mac)) {
                    mac = mac.substring(mac.indexOf(":") - 2, mac.lastIndexOf(":") + 3);
                }
                return mac;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mac;
    }

    // 从windows机器上获取mac地址
    public static String getMacInWindows(final String ip) {
        String result = "";
        String[] cmd = {"cmd", "/c", "ping " + ip};
        String[] another = {"cmd", "/c", "ipconfig -all"};
        // 获取执行命令后的result
        String cmdResult = callCmd(cmd, another);
        // 从上一步的结果中获取mac地址
        result = filterMacAddress(ip, cmdResult, "-");
        return result;
    }

    // 命令执行
    public static String callCmd(String[] cmd, String[] another) {
        String result = "";
        String line = "";
        try {
            Runtime rt = Runtime.getRuntime();
            // 执行第一个命令
            Process proc = rt.exec(CleanUtil.cleanStrArray(cmd));
            proc.waitFor();
            // 执行第二个命令
            proc = rt.exec(CleanUtil.cleanStrArray(another));
            InputStreamReader is = new InputStreamReader(proc.getInputStream());
            BufferedReader br = new BufferedReader(is);
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取mac地址
    public static String filterMacAddress(final String ip, final String sourceString, final String macSeparator) {
        String result = "";
        String regExp = "((([0-9,A-F,a-f]{1,2}" + macSeparator + "){1,5})[0-9,A-F,a-f]{1,2})";
        Pattern pattern = Pattern.compile(regExp);
        Matcher matcher = pattern.matcher(sourceString);
        while (matcher.find()) {
            result = matcher.group(1);
            // 因计算机多网卡问题，截取紧靠IP后的第一个mac地址
            int num = sourceString.indexOf(ip) - sourceString.indexOf(": " + result + " ");
            if (num > 0 && num < 300) {
                break;
            }
        }
        return result;
    }

    public static String getBaseInfo(String key) {
        String result = null;
        try {
            List<String> readFile = FileUtils.readFile(ENVIROMENT_PATH);
            for (String content : readFile) {
                if (content.contains(key)) {
                    String[] split = content.split("=");
                    if (split.length > 1) {
                        result = split[1];
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.error("",e);
        }

        return result;
    }
}
