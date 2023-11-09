package com.vrv.vap.xc.init;

import com.google.common.collect.ImmutableList;
import com.vrv.vap.xc.VapXcApplication;
import com.vrv.vap.xc.constants.QueryConstants;
import com.vrv.vap.xc.model.QueryModel;
import com.vrv.vap.xc.model.SingleTableModel;
import com.vrv.vap.toolkit.tools.CommonTools;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.*;

/**
 * 获取数据库表信息
 *
 * @author xw
 * @date 2018年4月10日
 */
@Component
public class SingleTableBuilder {
    private static final Log log = LogFactory.getLog(SingleTableBuilder.class);

    @Value("${spring.datasource.dynamic.datasource.core.username}")
    private String username;

    @Value("${spring.datasource.dynamic.datasource.core.password}")
    private String password;

    @Value("${spring.datasource.dynamic.datasource.core.url}")
    private String url;

    @Value("${spring.datasource.dynamic.datasource.core.driver-class-name}")
    private String driver;

    /**
     * 查询关键字,不能当查询条件
     */
    private static final String[] KEYS = new String[]{"table", "start_", "count_", "order_", "by_"};

    private static final Map<String, SingleTableModel> TABLE_MAP = new HashMap<>();

    // 允许获取的表
    private static final ImmutableList<String> ALLOW_LIST = ImmutableList
            .copyOf(new String[]{"dict_", "rpt_", "dossier_", "base_dict_", "log_"});

    public void init() {
        Connection conn = null;
        try {
            log.debug("开始初始化单表查询");
            Class.forName(driver);
            conn = DriverManager.getConnection(url, username, password);
            DatabaseMetaData dbmd = conn.getMetaData();
            // 获取表名
            ResultSet ret = dbmd.getTables(null, "%", "%", new String[]{"TABLE"});
            while (ret.next()) {
                SingleTableModel singelTableModel = new SingleTableModel();
                singelTableModel.setTable(ret.getString(3));
                if (!ALLOW_LIST.stream().anyMatch(m -> {
                    return singelTableModel.getTable().startsWith(m);
                })) {
                    continue;
                }
                // 获取主键
                ResultSet ret2 = dbmd.getPrimaryKeys(null, "%", singelTableModel.getTable());
                if (ret2.first()) {
                    singelTableModel.setPrimaryKey(ret2.getString(4));
                }
                ret2.close();

                // 获取字段名
                ResultSet ret3 = dbmd.getColumns(null, "%", singelTableModel.getTable(), "%");
                List<String> columnList = new ArrayList<>();
                List<String> columnCamelList = new ArrayList<>();
                Map<String, String> camelToDbMap = new HashMap<>();
                while (ret3.next()) {
                    String value = ret3.getString(4);
                    String key = CommonTools.underLineToCamel(value);
                    columnList.add(value);
                    columnCamelList.add(key);
                    camelToDbMap.put(key, value);
                }
                ret3.close();

                singelTableModel.setColumnList(columnList);
                singelTableModel.setColumnCamelList(columnCamelList);
                singelTableModel.setColumnString(StringUtils.join(columnList, ","));
                singelTableModel.setCamelToDbMap(camelToDbMap);

                TABLE_MAP.put(singelTableModel.getTable().toUpperCase(), singelTableModel);
                log.debug(singelTableModel);
            }
            ret.close();

            log.debug("结束初始化单表查询");
        } catch (Exception e) {
            log.error("", e);
        } finally {
            try {
                if(conn != null){
                    conn.close();
                }
            } catch (SQLException e) {
                log.error("", e);
            }
        }
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    /**
     * 获取原始的SingleTableModel,慎用!! 一般请使用getCloneSingleTableModel
     *
     * @param table
     * @return
     */
    public static Optional<SingleTableModel> getOriginSingleTableModel(String table) {
        SingleTableModel singleTableModel = TABLE_MAP.get(table.toUpperCase());
        boolean f = (singleTableModel == null);
        if (f) {
            log.error("未获取到" + table + "表缓存的singletable信息");
        }
        return f ? Optional.empty() : Optional.of(singleTableModel);
    }

    /**
     * 获取一个SingleTableModel的复制
     *
     * @param table
     * @return
     */
    public static Optional<SingleTableModel> getSingleTableModel(String table) {
        return getCloneSingleTableModel(table);
    }

    /**
     * 获取一个SingleTableModel的复制
     *
     * @param table
     * @return
     */
    public static Optional<SingleTableModel> getCloneSingleTableModel(String table) {
        SingleTableModel singleTableModel = TABLE_MAP.get(table.toUpperCase());
        if (singleTableModel == null) {
            //log.error("未获取到" + table + "表缓存的singletable信息");
            log.error("未获取到表缓存的singletable信息");
            log.error("TABLE_MAP size: " + TABLE_MAP.size());
            SingleTableBuilder singleTable = VapXcApplication.getApplicationContext().getBean(SingleTableBuilder.class);
            singleTable.init();
            log.error("TABLE_MAP size: " + TABLE_MAP.size());
            singleTableModel = TABLE_MAP.get(table.toUpperCase());
        }
        return singleTableModel == null ? Optional.empty() : Optional.of(singleTableModel.clones());
    }

    /**
     * 构造查询条件
     *
     * @param table      表名
     * @param requestMap 请求参数
     * @return
     */
    public static QueryModel buildQueryModel(String table, Map<String, Object> requestMap) {
        Object order = requestMap.get("order_");
        Object by = requestMap.get("by_");
        Object count = requestMap.get("count_");
        Object start = requestMap.get("start_");

        Map<String, Object> param = new HashMap<>();
        requestMap.entrySet().forEach(kv -> {
            if (check(kv.getKey())) {
                param.put(kv.getKey(), kv.getValue());
            }
        });

        QueryModel queryModel = new QueryModel();
        queryModel.setTable(table);
        queryModel.setWhere(param);
        if (null != by && StringUtils.isNotEmpty(by.toString())) {
            queryModel.setBy(by.toString());
        }
        if (null != order && StringUtils.isNotEmpty(order.toString())) {
            queryModel.setOrder(order.toString());
        }
        queryModel.setStart(null == start ? 0 : Integer.parseInt(start.toString()));
        queryModel.setCount(null == count ? 10 : Integer.parseInt(count.toString()));

        return queryModel;
    }

    /**
     * 添加范围查询
     *
     * @param requestMap 前台请求
     * @param column     数据库字段名
     * @param bigValue   最大值
     * @param smallValue 最小值
     */
    public static void setRange(Map<String, Object> requestMap, String column, Object bigValue, Object smallValue) {
        if (null != bigValue) {
            requestMap.put(QueryConstants.RANGE_LTE + column, bigValue);
        }
        if (null != smallValue) {
            requestMap.put(QueryConstants.RANGE_GTE + column, smallValue);
        }
    }

    /**
     * 校验关键字
     *
     * @param key
     * @return
     */
    private static boolean check(String key) {
        for (String k : KEYS) {
            if (key.equalsIgnoreCase(k)) {
                return false;
            }
        }
        return true;
    }
}
