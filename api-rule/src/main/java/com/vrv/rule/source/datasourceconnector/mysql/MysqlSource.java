package com.vrv.rule.source.datasourceconnector.mysql;

import com.vrv.rule.source.datasourceparam.impl.MysqlDatasourceParam;
import com.vrv.rule.vo.FieldInfoVO;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.functions.source.RichSourceFunction;
import org.apache.flink.types.Row;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class MysqlSource extends RichSourceFunction<Row> {

    private JdbcTemplate jdbcTemplate;

    private MysqlDatasourceParam mysqlDatasourceParam;

    public MysqlSource(MysqlDatasourceParam mysqlDatasourceParam) {
        this.mysqlDatasourceParam = mysqlDatasourceParam;
    }


    @Override
    public void open(Configuration parameters) {
        DataSource dataSource = createDataSource();
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    private DataSource createDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setDriverClassName(mysqlDatasourceParam.getDriverClassName());
        dataSource.setJdbcUrl(mysqlDatasourceParam.getJdbcUrl());
        dataSource.setUsername(mysqlDatasourceParam.getUserName());
        dataSource.setPassword(mysqlDatasourceParam.getPassword());
        dataSource.setMinimumIdle(mysqlDatasourceParam.getMinimumIdel());
        dataSource.setMaximumPoolSize(mysqlDatasourceParam.getMaximumPoolSize());
        dataSource.setIdleTimeout(mysqlDatasourceParam.getIdleTimeout());
        return dataSource;
    }


    @Override
    public void run(SourceContext<Row> sourceContext) throws Exception {
        String sql = mysqlDatasourceParam.getSql();
        List<FieldInfoVO> fieldInfoVOs = mysqlDatasourceParam.getFieldInfoVOs();
        jdbcTemplate.query(sql, rs -> {
            Spliterator<Row> rowSpliterator = getRowSpliterator(rs, fieldInfoVOs);
            Stream<Row> rowStream = StreamSupport.stream(rowSpliterator, false);
            rowStream.forEach(row -> sourceContext.collect(row));
        });

    }


    /**
     * 获得对应的row数据
     *
     * @param rs
     * @param fieldInfoVOs
     * @return
     */
    private Row generateRowByFieldInfoVOs(ResultSet rs, List<FieldInfoVO> fieldInfoVOs) {
        Row row = new Row(fieldInfoVOs.size());
        for (FieldInfoVO fieldInfoVO : fieldInfoVOs) {
            try {
                Object rowResult = getRowResult(rs, fieldInfoVO);
                row.setField(fieldInfoVO.getOrder(), rowResult);
            } catch (SQLException e) {
                throw new RuntimeException("获取字段" + fieldInfoVO.getFieldName() + "失败,字段类型：" + fieldInfoVO.getFieldType());
            }
        }
        return row;
    }


    /**
     * 获得查询的值数据
     *
     * @param rs
     * @param fieldInfoVO
     * @return
     * @throws SQLException
     */
    private Object getRowResult(ResultSet rs, FieldInfoVO fieldInfoVO) throws SQLException {
        Object result = null;
        String fieldType = fieldInfoVO.getFieldType();
        String fieldName = fieldInfoVO.getFieldName();
        switch (fieldType) {
            case "varchar":
                result = rs.getString(fieldName);
                break;
            case "int":
                result = rs.getInt(fieldName);
                break;
            case "bigint":
                result = rs.getLong(fieldName);
                break;
            case "double":
                result = rs.getDouble(fieldName);
                break;
            case "float":
                result = rs.getFloat(fieldName);
                break;
            case "boolean":
                result = rs.getBoolean(fieldName);
                break;
            case "datetime":
                result = rs.getDate(fieldName);
                break;
            default:
                throw new RuntimeException("不支持的类型:" + fieldType);
        }
        return result;
    }


    /**
     * 通过流的方式对数据进行分析，不再一次性加载到内存当中
     *
     * @param rs
     * @param fieldInfoVOs
     * @return
     * @throws RuntimeException
     */
    private Spliterator<Row> getRowSpliterator(ResultSet rs, List<FieldInfoVO> fieldInfoVOs) throws RuntimeException {
        Spliterator<Row> spliterator = new Spliterator<Row>() {
            @Override
            public boolean tryAdvance(Consumer<? super Row> action) {
                try {
                    if (rs.next()) {
                        Row row = generateRowByFieldInfoVOs(rs, fieldInfoVOs);// 创建Row对象
                        action.accept(row);
                        return true;
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                return false;
            }

            @Override
            public Spliterator<Row> trySplit() {
                return null;
            }

            @Override
            public long estimateSize() {
                return 0;
            }

            @Override
            public int characteristics() {
                return 0;
            }
            // 其他未实现的方法
        };
        return spliterator;
    }

    @Override
    public void cancel() {
    }
}
