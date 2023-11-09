package com.vrv.vap.alarmdeal.frameworks.controller;

import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author 梁国露
 * @date 2021年11月11日 10:53
 */
public class BaseController {
    final String[] DISALLOWED_FIELDS = new String[]{"", "",
            ""};

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.setDisallowedFields(DISALLOWED_FIELDS);
    }
}
