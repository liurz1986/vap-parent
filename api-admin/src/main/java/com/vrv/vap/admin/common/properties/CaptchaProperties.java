package com.vrv.vap.admin.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "site.captcha")
public class CaptchaProperties {
    private  boolean enable;
    private int width;
    private int height;
    private int length;
    private String fontFamily; // 1-10
    private float fontSize; // 1-10
    private String type;//  Spec  Gif     Chinese  ChineseGif  arithmetic
    private String mode; // TYPE_DEFAULT	数字和字母混 TYPE_ONLY_NUMBER	纯数字 TYPE_ONLY_CHAR	纯字母 TYPE_ONLY_UPPER	纯大写字母 TYPE_ONLY_LOWER	纯小写字母 TYPE_NUM_AND_UPPER

}
