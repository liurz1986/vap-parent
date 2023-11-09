package com.vrv.vap.line.tools;

import com.vrv.vap.line.VapLineApplication;
import com.vrv.vap.line.constants.LineConstants;
import org.apache.commons.lang3.StringUtils;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FrequentRoleAll {
    private static DataSource dataSource = VapLineApplication.getApplicationContext().getBean(DataSource.class);
    private Connection conn =null;
    PreparedStatement replaceInsert=null;
    Statement queryFrq=null;
    String sql = "SELECT role,sys_id,GROUP_CONCAT(frequents separator '#') AS frequents FROM base_line_frequent WHERE `role` <> '' GROUP BY role,sys_id";
    public FrequentRoleAll() {
    }

    public void open(){
        try{
            if(conn == null || conn.isClosed()){
                conn = dataSource.getConnection();
            }
            replaceInsert = conn.prepareStatement("REPLACE INTO base_line_frequent_role (`role`,sys_id, frequents, update_time) VALUES (?, ?, ?,?)");
            queryFrq = conn.createStatement();
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void updateFrequent(){
        try{
            open();
            List<String> items = new ArrayList<>();
            ResultSet resultSet = queryFrq.executeQuery(sql);
            while (resultSet.next()){
                String fs = resultSet.getString("frequents");
                String role = resultSet.getString("role");
                String sys_id = resultSet.getString("sys_id");
                if(StringUtils.isNotEmpty(fs)){
                    System.out.println("更新机构频繁序列");
                    items.addAll(Arrays.asList(fs.split(LineConstants.SQ.itemSeparator)));
                    String quents = AlinkTools.apriori(items, "2");
                    replaceInsert.setString(1,role);
                    replaceInsert.setString(2,sys_id);
                    replaceInsert.setString(3,quents);
                    replaceInsert.setTimestamp(4,new Timestamp(System.currentTimeMillis()));
                    replaceInsert.execute();
                    replaceInsert.clearParameters();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            cloes();
        }
    }

    public void cloes(){
        try{
            if(replaceInsert != null){
                replaceInsert.close();
            }
            if(queryFrq != null){
                queryFrq.close();
            }
            if(conn != null){
                conn.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
