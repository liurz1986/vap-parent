package com.vrv.vap.xc.tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据库表格相关
 *
 * @author xw
 * @date 2016年5月3日
 */
public class TableTools {
    /**
     * 构造表格校验查询参数
     *
     * @param type
     * @param tableNames
     * @return
     */
    public static Map<String, Object> buildCheckTableParam(String type, List<String> tableNames) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("type", type);
        param.put("tables", tableNames);
        return param;
    }
}
