package com.vrv.vap.admin.web;

import com.vrv.vap.admin.common.properties.CaptchaProperties;
import com.wf.captcha.ArithmeticCaptcha;
import com.wf.captcha.GifCaptcha;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.base.Captcha;
import com.wf.captcha.utils.CaptchaUtil;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


@Controller
@RequestMapping(path = "/captcha")
@Slf4j
public class CaptchaController {

    private static Logger logger = LoggerFactory.getLogger(CaptchaController.class);

    @Autowired
    private CaptchaProperties captchaProperties;

    private Integer[] fonts = null;

    private static final Map<String, Integer> SUPPORT_MODES = new HashMap<>();
    private static final Map<String, Integer> SUPPORT_FONT = new HashMap<>();


    static {
        SUPPORT_MODES.put("default", Captcha.TYPE_DEFAULT);
        SUPPORT_MODES.put("number", Captcha.TYPE_ONLY_NUMBER);
        SUPPORT_MODES.put("char", Captcha.TYPE_ONLY_CHAR);
        SUPPORT_MODES.put("upper", Captcha.TYPE_ONLY_UPPER);
        SUPPORT_MODES.put("lower", Captcha.TYPE_ONLY_LOWER);
        SUPPORT_MODES.put("number-upper", Captcha.TYPE_NUM_AND_UPPER);
        SUPPORT_FONT.put("1", Captcha.FONT_1);
        SUPPORT_FONT.put("2", Captcha.FONT_2);
        SUPPORT_FONT.put("3", Captcha.FONT_3);
        SUPPORT_FONT.put("4", Captcha.FONT_4);
        SUPPORT_FONT.put("5", Captcha.FONT_5);
        SUPPORT_FONT.put("6", Captcha.FONT_6);
        SUPPORT_FONT.put("7", Captcha.FONT_7);
        SUPPORT_FONT.put("8", Captcha.FONT_8);
        SUPPORT_FONT.put("9", Captcha.FONT_9);
        SUPPORT_FONT.put("10", Captcha.FONT_10);
    }


    private Captcha getCaptcha() {
        Captcha captcha = null;
        switch (captchaProperties.getType().toLowerCase()) {
            case "gif":
                captcha = new GifCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(), captchaProperties.getLength());
                break;
//                缺少字体，不开放中文验证码
//            case "chinese":
//                captcha = new ChineseCaptcha(this.width,this.height,this.length);
//                break;
//            case "chinese-gif":
//                captcha = new ChineseGifCaptcha(this.width,this.height,this.length);
//                break;
            case "arithmetic":
                captcha = new ArithmeticCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(), 2);
                break;
            default:
                captcha = new SpecCaptcha(captchaProperties.getWidth(), captchaProperties.getHeight(), captchaProperties.getLength());
                if (SUPPORT_MODES.containsKey(captchaProperties.getMode())) {
                    captcha.setCharType(SUPPORT_FONT.get(captchaProperties.getMode()));
                } else {
                    captcha.setCharType(Captcha.TYPE_DEFAULT);
                }
                break;
        }
        if (this.fonts == null) {
            String[] fonts = captchaProperties.getFontFamily().split(",");
            List<Integer> fontArr = new LinkedList<>();
            for (String font : fonts) {
                if (SUPPORT_FONT.containsKey(font)) {
                    fontArr.add(SUPPORT_FONT.get(font));
                }
            }
            if (fontArr.size() == 0) {
                fontArr.add(Captcha.FONT_1);
            }
            this.fonts = fontArr.stream().toArray(Integer[]::new);
        }
        try {
            captcha.setFont(this.fonts[RandomUtils.nextInt() % this.fonts.length].intValue(), captchaProperties.getFontSize());
        } catch (IOException e) {
//            e.printStackTrace();
        } catch (FontFormatException e) {
//            e.printStackTrace();
        }
        ;
        return captcha;

    }

    @GetMapping
    @ApiOperation(value = "获取验证码，以图片返回")
    public void captcha(HttpServletRequest request, HttpServletResponse response) throws Exception {
        Captcha captcha = this.getCaptcha();
        CaptchaUtil.out(captcha, request, response);
        HttpSession session = request.getSession();
        session.setAttribute("captcha", captcha.text());
    }


}
