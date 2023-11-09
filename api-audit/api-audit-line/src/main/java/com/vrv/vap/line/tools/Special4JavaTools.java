package com.vrv.vap.line.tools;

import com.alibaba.fastjson.JSONObject;
import com.vrv.vap.line.model.BaseLine;
import com.vrv.vap.line.model.BaseLineSpecial;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class Special4JavaTools {

    public void execute(Map<String,Object> params,String classes) throws Exception {
        String method = "execute";
        Object target = Class.forName(classes).newInstance();
        Method md = target.getClass().getMethod(method, Map.class);
        md.invoke(target,params);
    }

}
