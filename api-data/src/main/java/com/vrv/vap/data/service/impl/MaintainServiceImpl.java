package com.vrv.vap.data.service.impl;

import com.vrv.vap.common.exception.ApiException;
import com.vrv.vap.data.constant.ErrorCode;
import com.vrv.vap.data.mapper.MaintainMapper;
import com.vrv.vap.data.model.Maintain;
import com.vrv.vap.data.model.SourceField;
import com.vrv.vap.data.service.BaseCacheServiceImpl;
import com.vrv.vap.data.service.MaintainService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
@Transactional
public class MaintainServiceImpl extends BaseCacheServiceImpl<Maintain> implements MaintainService {

    private Logger logger = LoggerFactory.getLogger(getClass());

    @Resource
    private MaintainMapper maintainMapper;

    @Autowired
    DataSource dataSource;

    @Override
    public Integer execInsert(String tableName, List<SourceField> fields, Map data) throws ApiException {
        Connection conn = this.getConn();

        PreparedStatement ps = null;
        try {
            List params = new ArrayList();
            StringBuffer SQL = new StringBuffer();
            SQL.append("INSERT INTO `");
            SQL.append(tableName);
            SQL.append("` (");
            for (SourceField field : fields) {
                if (data.containsKey(field.getField()) && StringUtils.isNotBlank(data.get(field.getField()).toString())) {
                    SQL.append('`').append(field.getField()).append('`');
                    SQL.append(',');
                    if ("long".equals(field.getType())) {
                        params.add(Long.parseLong(data.get(field.getField()).toString()));
                    } else if ("double".equals(field.getType())) {
                        params.add(Double.parseDouble(data.get(field.getField()).toString()));
                    } else {
                        params.add(data.get(field.getField()).toString());
                    }
                }
            }
            if (params.size() == 0) {
                throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
            }
            SQL.deleteCharAt(SQL.length() - 1);
            SQL.append(") VALUES (");
            SQL.append(params.stream().map(i -> "?").collect(Collectors.joining(",")));
            SQL.append(")");
            String sql = SQL.toString();
            logger.info("================表格插入" + sql);
            ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            for (int i = 0, _i = params.size(); i < _i; i++) {
                Object param = params.get(i);
                if (param instanceof Long) {
                    ps.setLong(i + 1, (Long) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                } else {
                    ps.setString(i + 1, param.toString());
                }
            }
            int affected = ps.executeUpdate();
            if (affected == 1) {
                ResultSet keys = ps.getGeneratedKeys();
                keys.next();
                int newKey = keys.getInt(1);
                return newKey;
            } else {
                throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
//            throw  new ApiException();
        } finally {
            try {
                if (ps != null) ps.close();
                this.closeConn(conn);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    @Override
    public Integer execUpdate(String tableName, String pk, List<SourceField> fields, Map data) throws ApiException {
        Connection conn = this.getConn();
        List params = new ArrayList();

        StringBuffer SQL = new StringBuffer();
        SQL.append("UPDATE `");
        SQL.append(tableName);
        SQL.append("` SET ");

        Object primary = null;
        boolean isString = false;
        for (SourceField field : fields) {
            if (pk.equalsIgnoreCase(field.getField())) {
                primary = data.get(pk);
                if (!"long".equals(field.getType())) {
                    isString = true;
                }
                continue;
            }

            if (data.containsKey(field.getField()) && StringUtils.isNotBlank(data.get(field.getField()).toString())) {
                SQL.append('`').append(field.getField()).append("`=?,");
                if ("long".equals(field.getType())) {
                    params.add(Long.parseLong(data.get(field.getField()).toString()));
                } else if ("double".equals(field.getType())) {
                    params.add(Double.parseDouble(data.get(field.getField()).toString()));
                } else {
                    params.add(data.get(field.getField()).toString());
                }
            }
        }
        if (params.size() == 0) {
            throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
        }
        SQL.deleteCharAt(SQL.length() - 1);
        SQL.append("WHERE `").append(pk).append("` = ?");
        String sql = SQL.toString();
        logger.info("================表格插入" + sql);
        PreparedStatement ps = null;
        try {
            ps = conn.prepareStatement(sql);
            int i = 0;
            for (int _i = params.size(); i < _i; i++) {
                Object param = params.get(i);
                if (param instanceof Long) {
                    ps.setLong(i + 1, (Long) param);
                } else if (param instanceof Double) {
                    ps.setDouble(i + 1, (Double) param);
                } else {
                    ps.setString(i + 1, param.toString());
                }
            }
            if (isString) {
                ps.setString(i + 1, primary.toString());
            } else {
                ps.setInt(i + 1, Integer.valueOf(primary.toString()));
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            this.closeConn(conn);
        }

    }

    @Override
    public Integer execDelete(String tableName, String pk, String[] ids) throws ApiException {
        Connection conn = this.getConn();
        PreparedStatement ps = null;
        try {
            conn = dataSource.getConnection();
            StringBuffer SQL = new StringBuffer();
            SQL.append("DELETE FROM `").append((tableName)).append("` WHERE ").append(pk).append(" IN (");
            SQL.append(StringUtils.repeat("?,", ids.length));
            SQL.deleteCharAt(SQL.length() - 1);
            SQL.append(')');

            String sql = SQL.toString();
            logger.info("================表格删除" + sql);
            ps = conn.prepareStatement(SQL.toString());
            for (int i = 0; i < ids.length; i++) {
                ps.setString(i + 1, ids[i]);
            }
            return ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                }
            }
            this.closeConn(conn);

        }
    }


    // 后续可以优化为 Connection Pool
    private Connection getConn() throws ApiException {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
            throw new ApiException(ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getCode(),ErrorCode.SQL_TABLE_NOT_EXISTS.getResult().getMessage());
//            e.printStackTrace();
        }
    }

    private void closeConn(Connection conn) {
        if (conn == null) {
            return;
        }
        try {
            if (conn.isClosed()) {
                return;
            }

            conn.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
//            e.printStackTrace();
        }
    }


}
