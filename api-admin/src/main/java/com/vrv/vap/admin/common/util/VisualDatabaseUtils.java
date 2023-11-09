package com.vrv.vap.admin.common.util;

import com.vrv.vap.admin.model.VisualDatabaseConnection;
import com.vrv.vap.admin.service.VisualDatabaseService;
import com.vrv.vap.admin.util.SqlCheckUtil;
import com.vrv.vap.admin.vo.DatabaseConnectionVo;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

@Component
public class VisualDatabaseUtils {

    private final static Logger LOGGER = LoggerFactory.getLogger(VisualDatabaseUtils.class);

    private static final String DRIVER = "com.mysql.jdbc.Driver";
    @Value("${spring.datasource.url}")
    private String URL;
    @Value("${spring.datasource.username}")
    private String USERNAME;
    @Value("${spring.datasource.password}")
    private String PASSWORD;
    // 内部数据库（默认）
    private static final Integer TYPE_INNER = 0;

    private static final String SQL = "SELECT * FROM ";// 数据库操作
    @Autowired
    private VisualDatabaseService visualDatabaseService;

    static {
        try {
            Class.forName(DRIVER);
        } catch (ClassNotFoundException e) {
            LOGGER.error("can not load jdbc driver", e);
        }
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public Connection getConnection(DatabaseConnectionVo databaseConnectionVo) {
        Connection conn = null;
        if (databaseConnectionVo != null) {
            String url = "";
            String userName = "";
            String password;
            Integer type = databaseConnectionVo.getType();
            if (TYPE_INNER.equals(type)) {
                url = URL;
                userName = USERNAME;
                password = PASSWORD;
            } else {
                url = "jdbc:mysql://"+databaseConnectionVo.getAddress()
                        +":"+databaseConnectionVo.getPort()
                        +"/"+databaseConnectionVo.getDatabaseName()
                        +"?useUnicode=true&useSSL=false&characterEncoding=UTF-8";
                userName = databaseConnectionVo.getUser();
                password = databaseConnectionVo.getPassword();
                password = CommonTools.decodeBase64(password);
            }
            try {
                conn = DriverManager.getConnection(url, userName, password);
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
            }
        }
        return conn;
    }

    /**
     * 获取数据库连接
     *
     * @return
     */
    public Connection getConnection(Integer connectionId) {
        Connection conn = null;
        VisualDatabaseConnection visualDatabaseConnection = visualDatabaseService.findById(connectionId);
        if (visualDatabaseConnection != null) {
            String url = "";
            String userName = "";
            String password;
            Integer type = visualDatabaseConnection.getType();
            if (TYPE_INNER.equals(type)) {
                url = URL;
                userName = USERNAME;
                password = PASSWORD;
            } else {
                url = "jdbc:mysql://"+visualDatabaseConnection.getAddress()
                        +":"+visualDatabaseConnection.getPort()
                        +"/"+visualDatabaseConnection.getDatabaseName()
                        +"?useUnicode=true&useSSL=false&characterEncoding=UTF-8";
                userName = visualDatabaseConnection.getUser();
                password = visualDatabaseConnection.getPassword();
                password = CommonTools.decodeBase64(password);
            }
            try {
                conn = DriverManager.getConnection(url, userName, password);
            } catch (SQLException e) {
                LOGGER.error("get connection failure", e);
            }
        }
        return conn;
    }

    /**
     * 关闭数据库连接
     * @param conn
     */
    public static void closeConnection(Connection conn) {
        if(conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                LOGGER.error("close connection failure", e);
            }
        }
    }

    /**
     * 获取数据库下的所有表名
     */
    public List<Map<String,Object>> getTableNames(DatabaseConnectionVo databaseConnectionVo) {
        List<Map<String,Object>> tableNames = new ArrayList<>();
        ResultSet rs = null;
        try (Connection conn = this.getConnection(databaseConnectionVo)) {
            //获取数据库的元数据
            DatabaseMetaData db = conn.getMetaData();
            //从元数据中获取到所有的表名
            rs = db.getTables(null, null, null, new String[] { "TABLE" });
            while(rs.next()) {
                Map<String,Object> map = new HashMap<>();
                map.put("tableName",rs.getString(3));
                tableNames.add(map);
            }
        } catch (SQLException e) {
            LOGGER.error("getTableNames failure", e);
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                LOGGER.error("close ResultSet failure", e);
            }
        }
        return tableNames;
    }

    /**
     * 获取表中字段的所有注释
     * @param databaseConnectionVo
     * @return
     */
    public List<Map<String,String>> getColumnComments(DatabaseConnectionVo databaseConnectionVo) {
        String tableName = databaseConnectionVo.getTableName();
        List<String> columnTypes = new ArrayList<>();
        //PreparedStatement pStemt = null;
        // 检查是否有注入风险
        if (SqlCheckUtil.checkSqlTableName(tableName)) {
            return new ArrayList<>();
        }
        String tableSql = SQL + tableName;
        //用于存储字段与注释
        List<Map<String,String>> columns = new ArrayList<>();
        try (Connection conn = this.getConnection(databaseConnectionVo);
             PreparedStatement pStemt = conn.prepareStatement(tableSql);
             ResultSet rs = pStemt.executeQuery("show full columns from " + tableName);) {
            while (rs.next()) {
                Map<String,String> columnMap = new HashMap<>();
                columnMap.put("uuid",CommonTools.generateId());
                columnMap.put("field",rs.getString("Field"));
                columnMap.put("comment",rs.getString("Comment"));
                columnMap.put("type",translateFieldType(rs.getString("Type")));
                columnMap.put("linkType","");
                columnMap.put("displayed","");
                columnMap.put("filtered","");
                columnMap.put("tag","");
                columnMap.put("unit","");
                columnMap.put("format","");
                columnMap.put("sort","");
                columns.add(columnMap);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }

    /**
     * 数据类型转换
     * @return
     */
    private static String translateFieldType(String fieldType) {
        String type = "keyword";
        if (StringUtils.isNotEmpty(fieldType)) {
            String[] fieldArr = fieldType.split("\\(");
            String field = fieldArr[0].toUpperCase(Locale.ENGLISH);
            // 日期类型
            String[] dateArr = {"DATE","TIME","YEAR","DATETIME","TIMESTAMP"};
            // 长整型
            String[] longArr = {"TINYINT","SMALLINT","MEDIUMINT","INT","INTEGER","BIGINT"};
            // 浮点型
            String[] doubleArr = {"FLOAT","DOUBLE"};
            for (String dateStr : dateArr) {
                if (field.equals(dateStr)) {
                    type = "date";
                    break;
                }
            }
            for (String longStr : longArr) {
                if (field.equals(longStr)) {
                    type = "long";
                }
            }
            for (String doubleStr : doubleArr) {
                if (field.equals(doubleStr)) {
                    type = "double";
                }
            }
        }
        return type;
    }

    public List<Map> queryData(String sql,Integer connectionId) {
        List<Map> list = new ArrayList();
        if (StringUtils.isEmpty(sql)) {
            return list;
        }
        try (Connection conn = this.getConnection(connectionId);
             PreparedStatement pStemt = conn.prepareStatement(sql);
             ResultSet rs = pStemt.executeQuery();) {
            ResultSetMetaData md = rs.getMetaData();//获取键名
            int columnCount = md.getColumnCount();//获取行的数量
            while (rs.next()) {
                Map rowData = new HashMap();//声明Map
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = md.getColumnLabel(i) != null ? md.getColumnLabel(i) : md.getColumnName(i);
                    rowData.put(columnName, rs.getObject(i));//获取键名及值
                }
                list.add(rowData);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean testConnection(DatabaseConnectionVo databaseConnectionVo) {
        Connection conn = null;
        String url = "jdbc:mysql://"+databaseConnectionVo.getAddress()
                +":"+databaseConnectionVo.getPort()
                +"/"+databaseConnectionVo.getDatabaseName()
                +"?useUnicode=true&useSSL=false&characterEncoding=UTF-8";
        String userName = databaseConnectionVo.getUser();
        String password = databaseConnectionVo.getPassword();
        try {
            conn = DriverManager.getConnection(url, userName, CommonTools.decodeBase64(password));
        } catch (SQLException e) {
            LOGGER.error("get connection failure", e);
            return false;
        } finally {
            try {
                closeConnection(conn);
            } catch (Exception e) {
                LOGGER.error("close connection failure", e);
            }
        }
        return true;
    }
}
