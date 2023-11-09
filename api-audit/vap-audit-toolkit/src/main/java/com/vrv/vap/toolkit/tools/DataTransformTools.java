package com.vrv.vap.toolkit.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据类型转换
 *
 * @author xw
 */
public class DataTransformTools {
    private static final Logger log = LoggerFactory.getLogger(DataTransformTools.class);

    /**
     * int 转 stirng
     *
     * @int i
     * @return
     */
    public static String i2s(int i) {
        return new Integer(i).toString();
    }

    /**
     * long 转 stirng
     *
     * @long i
     * @return
     */
    public static String l2s(long i) {
        return new Long(i).toString();
    }

    /**
     * string 转 int
     *
     * @String i
     * @return
     */
    public static Integer s2i(String i) {
        if (null == i) {
            return 0;
        }
        return Integer.valueOf(i);
    }

    /**
     * byte[] 转 string
     *
     * @byte bs
     * @return
     */
    public static String b2s(byte[] bs) {
        if (null == bs) {
            return null;
        }
        String result = null;
        try {
            result = new String(bs, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("b2s", e);
        }
        return result;
    }

    /**
     * list map 转 map
     *
     * @List list
     * @return
     */
    public static <T> Map<String, T> lm2m(List<Map<String, T>> list) {
        Map<String, T> map = new HashMap<>();

        for (Map<String, T> tmpMap : list) {
            for (Map.Entry<String, T> entry : tmpMap.entrySet()) {
                map.put(entry.getKey(), entry.getValue());
            }
        }

        return map;
    }

    /**
     * list[map] 转 map
     *
     * @List list
     * @return
     */
    public static <T> Map<String, T> lm2m(List<Map<String, T>> list, String key, String key1) {
        Map<String, T> map = new HashMap<>();

        for (Map<String, T> tmpMap : list) {
            if (null == tmpMap.get(key)) {
                continue;
            }
            map.put(tmpMap.get(key).toString(), tmpMap.get(key1));
        }

        return map;
    }

    /**
     * 任意map合并
     *
     * @Map maps
     * @return
     */
    @SafeVarargs
    public static Map<String, String> ms2m(Map<String, ?>... maps) {
        Map<String, String> map = new HashMap<>();
        for (Map<String, ?> tmpMap : maps) {
            for (Map.Entry<String, ?> entry : tmpMap.entrySet()) {
                map.put(entry.getKey(), entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }
        return map;
    }

    /**
     * list string转String
     *
     * @list list
     * @return
     */
    public static String list2String(List<String> list, String sp) {
        String result = "";
        for (String tmp : list) {
            result += tmp + sp;
        }
        return result.substring(0, result.length() - sp.length());
    }

    /**
     * 将文件转换成byte数组
     */
    public static byte[] file2Byte(File tradeFile) {
        byte[] buffer = null;
        try (FileInputStream fis = new FileInputStream(tradeFile);
             ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] b = new byte[1024];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            log.error("File2Byte", e);
        } catch (IOException e) {
            log.error("File2Byte", e);
        }
        return buffer;
    }
}
