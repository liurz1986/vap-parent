package com.vrv.vap.common.constant;

import com.vrv.vap.common.vo.Result;

public final class Global {

    public static final Result OK = new Result("0","Success");
    public static final Result ERROR = new Result("999","系统繁忙，请稍后再试！");

    public final static class SESSION{


        public static final String USER = "_USER";
        
        public static final String USER_APP = "_USER_APP";

        public static final String ROLE_RESOURCE = "_ROLE_RESOURCE";

        public static final String RETURN_URL = "_RETURN_URL";

        public static final String DOMAIN = "_DOMAIN";

        public static final String USER_EXTENDS = "_USER_EXTENDS";
    }

}
