package com.vrv.vap.toolkit.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 工具类
 *
 * @author xw
 * @date 2015年11月12日
 */
public final class CommonTools {
    private static Log log = LogFactory.getLog(CommonTools.class);

    private static Pattern upPattern = Pattern.compile("[A-Z]");

    /**
     * 获取主键id
     *
     * @return
     */
    public static String generateId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * MD5加码 生成32位md5码
     */
    public static String string2MD5(String inStr) {
        if (StringUtils.isEmpty(inStr)) {
            return inStr;
        }
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            log.error("", e);
            return inStr;
        }
        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }

    /**
     * base64转码
     *
     * @String param
     * @return
     */
    public static String encodeBase64(String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }
        try {
            return encodeBase64(param.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            log.error("", e);
        }
        return "";
    }

    /**
     * base64转码
     *
     * @String param
     * @return
     */
    public static String encodeBase64(byte[] param) {
        if (null == param) {
            return "";
        }
        return Base64.getEncoder().encodeToString(param);
    }

    /**
     * base64解码
     *
     * @String param
     * @return
     */
    public static String decodeBase64(String param) {
        if (StringUtils.isEmpty(param)) {
            return "";
        }
        String result = "";
        try {
            result = new String(Base64.getDecoder().decode(param), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            log.error("decodeBase64", e);
        }
        return result;
    }

    /**
     * base64解码
     *
     * @String params
     * @return
     */
    public static String[] decodeBase64(String[] params) {
        List<String> result = new ArrayList<String>();
        try {
            for (String param : params) {
                result.add(decodeBase64(param));
            }
        } catch (Exception e) {
            log.error("decodeBase64", e);
        }
        return result.toArray(new String[0]);
    }
    /**
     * desEnc
     * */
    public static String desEnc(String key, String val) {
        Cipher c = getDesCipher(Cipher.ENCRYPT_MODE, key);
        try {
            if (c != null) {
                return encodeBase64(c.doFinal(val.getBytes("UTF-8")));
            }
        } catch (IllegalBlockSizeException e) {
            log.error("", e);
        } catch (BadPaddingException e) {
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        }
        return val;
    }
    /**desDec*/
    public static String desDec(String key, String val) {
        Cipher c = getDesCipher(Cipher.DECRYPT_MODE, key);
        try {
            if (c != null) {
                return new String(c.doFinal(Base64.getDecoder().decode(val)), "UTF-8");
            }
        } catch (IllegalBlockSizeException e) {
            log.error("", e);
        } catch (BadPaddingException e) {
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        }
        return val;
    }
    /**getDesCipher*/
    public static Cipher getDesCipher(int mode, String desKey) {
        Cipher c = null;
        try {
            c = Cipher.getInstance("DES");
            KeyGenerator key = KeyGenerator.getInstance("DES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(desKey.getBytes("UTF-8"));
            key.init(random);
            c.init(mode, key.generateKey());
        } catch (NoSuchAlgorithmException e) {
            log.error("", e);
        } catch (NoSuchPaddingException e) {
            log.error("", e);
        } catch (InvalidKeyException e) {
            log.error("", e);
        } catch (Exception e) {
            log.error("", e);
        }
        return c;
    }

    /**
     * 为字符串添加时间 格式 ${name}_yyyyMMdd.SSS
     *
     * @String name
     * @return
     */
    public static String appendTime(String name) {
        return new StringBuilder().append(name).append("_").append(TimeTools.format(new Date(), "yyyyMMdd.SSS"))
                .toString();
    }

    /**
     * 首字符转大写
     *
     * @String tmp
     * @return
     */
    public static String upperCaseFirstLetter(String tmp) {
        if ((int) tmp.charAt(0) > 96 && (int) tmp.charAt(0) < 123) {
            char[] cs = tmp.toCharArray();
            cs[0] = (char) (cs[0] - 32);
            return new String(cs);
        }
        return tmp;
    }

    /**
     * 首字符转小写
     *
     * @String tmp
     * @return
     */
    public static String lowerCaseFirstLetter(String tmp) {
        if ((int) tmp.charAt(0) < 91 && (int) tmp.charAt(0) > 64) {
            char[] cs = tmp.toCharArray();
            cs[0] = (char) (cs[0] + 32);
            return new String(cs);
        }
        return tmp;
    }

    /**
     * 下划线转驼峰
     *
     * @String tmp
     * @return
     */
    public static String underLineToCamel(String tmp) {
        Optional<String> worldOpt = Arrays.stream(tmp.split("_")).map(CommonTools::upperCaseFirstLetter).reduce((a, b) -> a + b);
        String world = tmp;
        if (worldOpt.isPresent()) {
            world = worldOpt.get();
        }
        return lowerCaseFirstLetter(world);
    }

    /**
     * 驼峰转下划线
     *
     * @String tmp
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


    private static final char[] HEX_CODE = "0123456789ABCDEF".toCharArray();

    /**
     * 计算文件 MD5
     *
     * @String file
     * @return 返回文件的md5字符串，如果计算过程中任务的状态变为取消或暂停，返回null， 如果有其他异常，返回空字符串
     */
    public static String calcMD5(File file) {
        try (InputStream stream = Files.newInputStream(file.toPath(), StandardOpenOption.READ)) {
            return calcMD5(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**输入流取MD5*/
    public static String calcMD5(InputStream stream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return toHexString(digest.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**toHexString*/
    public static String toHexString(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(HEX_CODE[(b >> 4) & 0xF]);
            r.append(HEX_CODE[(b & 0xF)]);
        }
        return r.toString();
    }
}
