package com.vrv.vap.common.controller;

import org.springframework.ui.Model;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;


public class AppController {

    private static final String PAGE = "__page";

    private static final String TITLE = "__title";

    private static final String MENU = "__menu";

    protected static final String THEME = "__theme";




    public String display(String tmpl, HttpServletRequest request) {

        String page = request.getRequestURI().substring(1);
        if(page.length()<=0){
            page = "index";
        }
        request.setAttribute(PAGE,page);

        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for (Cookie cookie : cookies) {
                if (THEME.equals(cookie.getName())) {
                    request.setAttribute(THEME, cookie.getValue());
                    break;
                }
            }
        }
        return tmpl;
    }


    public String display(String tmpl, HttpServletRequest request, Model model) {
        Cookie[] cookies = request.getCookies();
        for( Cookie cookie:cookies){
            if(THEME.equals(cookie.getName())){
                model.addAttribute(THEME,cookie.getValue());
                break;
            }
        }
        return tmpl;
    }


    public String admin(String tmpl){
        return  tmpl;
    }



}
