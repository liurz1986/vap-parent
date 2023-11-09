package com.vrv.vap.server.zuul.common;

public enum URITYPE {
    LOGIN_PAGE,//登录页面
    ALL,    // 任何人都能访问的资源
    AUTH,   // 登录之后不需要license的接口
    AUTH_RESOURCE, // 登录之后不需要license资源
    AUTH_LICENSE, //登录之后 需要license的接口
    AUTH_LICENSE_RESOURCE//登录之后 需要license的资源
}
