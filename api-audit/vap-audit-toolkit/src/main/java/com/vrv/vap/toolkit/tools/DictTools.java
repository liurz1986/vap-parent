package com.vrv.vap.toolkit.tools;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DictTools {
    private static Log log = LogFactory.getLog(ConvertTools.class);
    private static String dictSql = "SELECT code,code_value,parent_type,type from base_dict_all";
    private static Map<String, Map<String,String>> dictMap = new HashMap<>();

    static {
        Connection conn = null;
        Statement statement = null;
        try{
            conn = SpringUtil.getAppContext().getBean(DataSource.class).getConnection();
            statement = conn.createStatement();
            ResultSet resultSet = statement.executeQuery(dictSql);
            while (resultSet.next()){
                String parent_type = resultSet.getString("parent_type");
                String code_value = resultSet.getString("code_value");
                String type = resultSet.getString("type");
                String code = resultSet.getString("code");
                if(!"0".equals(parent_type)){
                    if(dictMap.containsKey(parent_type)){
                        dictMap.get(parent_type).put(code,code_value);
                    }else{
                        Map<String,String> map = new HashMap<>();
                        map.put(code,code_value);
                        dictMap.put(parent_type,map);
                    }
                }
            }
        }catch (Exception e){
            log.error(e.getMessage(),e);
        }finally {
            try{
                if(statement != null){
                    statement.close();
                }
                if(conn != null){
                    conn.close();
                }
            }catch (Exception e){
                log.error(e.getMessage(),e);
            }
        }
    }



    public static String translate(String parent,String code){
        if(StringUtils.isEmpty(parent) || StringUtils.isEmpty(code)){
            return code;
        }
        if(!dictMap.containsKey(parent)){
            return code;
        }
        if(!dictMap.get(parent).containsKey(code)){
            return code;
        }
        return dictMap.get(parent).get(code);
    }

}
