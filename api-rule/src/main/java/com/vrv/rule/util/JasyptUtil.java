package com.vrv.rule.util;

import org.jasypt.util.text.BasicTextEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 数据库关于盐的加密解密
 * @author wudi
 * @date 2022/7/6 11:04
 */
public class JasyptUtil {

    private static Logger logger  = LoggerFactory.getLogger(JasyptUtil.class);
    //16Ej3toVrEJrRW7kS62ngQ==
    public static void main(String[] args) {
        String password = "ENC(yFWoV01qLkF4r5M7FPqvyQ==)";
        String decryptPassword = getDecryptPassword(password,"salt");
        System.out.println(decryptPassword);
    }


    /**
     * 解密对应的密码
     * @return
     */
    public static String decryptPassword(String key) {
        String password = YmlUtil.getValue("application.yml", key).toString();
        logger.info("ecryptPassword:{}", password);
        String decryptPassword = JasyptUtil.getDecryptPassword(password, "salt");
        return decryptPassword;
    }

    /**
     * 获得对应解密的密码
     * @param password
     * @param salt
     * @return
     */
    public static String getDecryptPassword(String password,String salt) {
        if(password.contains("ENC")) {
            int begin = password.indexOf("(")+1;
            int end = password.indexOf(")");
            password  = password.substring(begin, end);
            BasicTextEncryptor encryptor = new BasicTextEncryptor();
            encryptor.setPassword(salt);
            String result = encryptor.decrypt(password);
            return result;
        }else {
            return password;
        }
    }


}
