package com.vrv.vap.line.tools;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;
import com.vrv.vap.line.constants.LineConstants;

public class BlackListTools {
    private static Set<String> blacklist = new HashSet<>();

    private BlackListTools() {
    }

    public static Set<String> getBlacklist() {
        return blacklist;
    }

    public static void setBlacklist(Set<String> blacklist) {
        BlackListTools.blacklist = blacklist;
    }

    static {
        Connection conn =null;
        Statement query=null;
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            if(conn == null){
                conn = DriverManager.getConnection("jdbc:mysql://"+ LineConstants.SQ.MYSQL_HOST+":3306/ajb_vap?serverTimezone=Asia/Shanghai", LineConstants.MYSQL.USER, LineConstants.MYSQL.PASSWORD);
            }
            query = conn.createStatement();
            ResultSet resultSet = query.executeQuery("select url from base_line_filter where `type` = '1'");
            if(resultSet != null){
                while (resultSet.next()){
                    blacklist.add(resultSet.getString("url"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                if(conn != null){
                    conn.close();
                }
                if(query != null){
                    query.close();
                }
            }catch (Exception var){
                var.printStackTrace();
            }
        }
    }
}
