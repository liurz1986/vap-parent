package com.vrv.vap.admin.util;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SM4Util {

    private static final Logger log = LoggerFactory.getLogger(SM4Util.class);

    private String key;

    public SM4Util(String key) {
        this.key = key;
    }

    public String encrypt(String content) {
        if(StringUtils.isEmpty(content)) {
            log.info("SM4Util->key must be not null");
        }
        SymmetricCrypto sm4 = SmUtil.sm4(key.getBytes());
        return sm4.encryptHex(content);
    }

    public String decrypt(String content) {
        if(StringUtils.isEmpty(content)) {
            log.info("SM4Util->key must be not null");
        }
        SymmetricCrypto sm4 = SmUtil.sm4(key.getBytes());
        return sm4.decryptStr(content, CharsetUtil.CHARSET_UTF_8);
    }

    public static void main(String[] args )
    {
        String content = "fisco bcos";
        String key="1234567890123456";
        SymmetricCrypto sm4 = SmUtil.sm4(key.getBytes());
        String encryptHex = sm4.encryptHex(content);
        String decryptStr = sm4.decryptStr(encryptHex, CharsetUtil.CHARSET_UTF_8);
        System.out.println(encryptHex+"\r\n"+decryptStr);
    }
}
