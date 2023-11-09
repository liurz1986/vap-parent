package com.vrv.vap.line.tools;

import com.vrv.vap.line.constants.LineConstants;
import com.vrv.vap.line.model.BaseLineFrequent;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.flink.types.Row;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FreSaveTools {
    private String urls;
    private String ukey;
    public static Connection conn =null;
    private static LogUtil log = new LogUtil();
    private PreparedStatement insertFrq=null;
    private PreparedStatement queryFrq=null;
    private PreparedStatement updateFrq=null;

    static {
        if(conn == null){
            try{
                Class.forName(LineConstants.MYSQL.DRIVER);
                conn = DriverManager.getConnection("jdbc:mysql://"+ LineConstants.SQ.MYSQL_HOST+":3306/ajb_vap?serverTimezone=Asia/Shanghai", LineConstants.MYSQL.USER, LineConstants.MYSQL.PASSWORD);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public FreSaveTools() {
    }

    public FreSaveTools(String urls, String ukey) {
        log.info(this + "  >>>>>>>>>>>>>>>>>>  "+ conn);
        System.out.println(this + "  >>>>>>>>>>>>>>>>>>  "+ conn);
        this.urls = urls;
        this.ukey = ukey;

    }

    public void open(){
        try{
            insertFrq = conn.prepareStatement("INSERT INTO base_line_frequent (user_id, frequents, count,is_continue,time,type) VALUES (?, ?, ?,?, ?, ?)");
            queryFrq = conn.prepareStatement("SELECT * FROM base_line_frequent WHERE user_id = ?");
            updateFrq = conn.prepareStatement("UPDATE base_line_frequent SET frequents = ? WHERE user_id = ?");
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void close() throws SQLException {
        insertFrq.close();
        queryFrq.close();
        updateFrq.close();
    }

    public void save(List<Row> rows) throws Exception {
        System.out.println("入库开始");
        open();
        queryFrq.setString(1,this.ukey);
        updateFrq.setString(2,this.ukey);
        Timestamp now = new Timestamp(System.currentTimeMillis());
        ResultSet resultSet = queryFrq.executeQuery();
        boolean next = resultSet.next();
        List<String> frees = new ArrayList<>();
        if(next){
            String frequents = resultSet.getString("frequents");
            frees.add(frequents);
        }
        rows.parallelStream().forEach((r) ->{
            //System.out.println("当前线程："+Thread.currentThread().getName());
            String item = r.getField(0).toString();
            //查找连续性
            //System.out.println(a.getAndIncrement());
            if(this.urls.indexOf(item) > -1){
                try{
                    frees.add(item);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        StringBuffer temStr = new StringBuffer();
        if(CollectionUtils.isNotEmpty(frees)){
            Collections.sort(frees, Comparator.comparingInt(i -> i == null ? 0 : i.toString().length()).reversed());
            for(String s : frees){
                if(temStr.toString().indexOf(s) == -1){
                    if(StringUtils.isNotEmpty(temStr)){
                        temStr.append(LineConstants.SQ.itemSeparator);
                    }
                    temStr.append(s);
                }
            }
        }
        if(next){
            updateFrq.setString(1,temStr.toString());
            updateFrq.executeUpdate();
        }else{
            insertFrq.setString(1,this.ukey);
            insertFrq.setString(2,temStr.toString());
            insertFrq.setInt(3,1);
            insertFrq.setString(4,"1");
            insertFrq.setTimestamp(5,now);
            insertFrq.setString(6,"1");
            insertFrq.execute();
        }
        insertFrq.clearParameters();
        queryFrq.clearParameters();
        updateFrq.clearParameters();
        //close();
    }
}
